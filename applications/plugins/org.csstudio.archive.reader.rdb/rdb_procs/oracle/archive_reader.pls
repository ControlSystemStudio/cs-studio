CREATE OR REPLACE PACKAGE               archive_reader_pkg
AS
   /******************************************************************************
      NAME:       archive_reader
      PURPOSE:

      REVISIONS:
      Ver        Date        Author        Description
      ---------  ----------  ------------  ------------------------------------
      1.0        11/18/2009  Jeff Patton   Created sample_aggregation_pkg.
      1.0        02/02/2010  Kay Kasemir   Copied into archive_reader_pkg for experiments.
                                           See more comments in package body.
   ******************************************************************************/
   TYPE refcursor IS REF CURSOR;

   /* Get data for a 'browser', a tool that shows data to a user,
      in a way that's somehow optimized over fetching each raw sample
      by returning a reduced number of min/max/average samples.

      Will _not_ return the exact number of requested samples.
      Uses that number to determine how many average samples to compute,
      but there may be less samples in the raw data, or additional samples
      without numeric values.
      If the data cannot be reduced because the data type is String,
      return raw data.

      Parameters:
      p_chan_id        Channel ID
      p_start_time     Start time
      p_end_time       End time
      p_reduction_nbr  Requested number of samples

      Result:
      Depends on wether it's raw or averaged data.
   */

   user_cancel   EXCEPTION;
   PRAGMA EXCEPTION_INIT (user_cancel, -1013);

   FUNCTION get_browser_data (p_chan_id         IN NUMBER,
                              p_start_time      IN TIMESTAMP_UNCONSTRAINED,
                              p_end_time        IN TIMESTAMP_UNCONSTRAINED,
                              p_reduction_nbr   IN NUMBER)
      RETURN SYS_REFCURSOR;

   FUNCTION get_browser_data_by_array (
      p_chan_id         IN NUMBER,
      p_start_time      IN TIMESTAMP_UNCONSTRAINED,
      p_end_time        IN TIMESTAMP_UNCONSTRAINED,
      p_reduction_nbr   IN NUMBER)
      RETURN SYS_REFCURSOR;

   FUNCTION get_count_by_date_range (
      p_chan_id      IN NUMBER,
      p_start_time   IN TIMESTAMP_UNCONSTRAINED,
      p_end_time     IN TIMESTAMP_UNCONSTRAINED)
      RETURN NUMBER;

   FUNCTION get_parallel_degree (p_start_time   IN TIMESTAMP_UNCONSTRAINED,
                                 p_end_time     IN TIMESTAMP_UNCONSTRAINED)
      RETURN VARCHAR2;

   FUNCTION get_actual_start_time (p_chan_id      IN NUMBER,
                                   p_start_time   IN TIMESTAMP_UNCONSTRAINED)
      RETURN TIMESTAMP_UNCONSTRAINED;

   FUNCTION get_sample_datatype (p_chan_id      IN NUMBER,
                                 p_start_time   IN TIMESTAMP_UNCONSTRAINED,
                                 p_end_time     IN TIMESTAMP_UNCONSTRAINED)
      RETURN VARCHAR2;

   FUNCTION is_sample_array (p_chan_id      IN NUMBER,
                             p_start_time   IN TIMESTAMP_UNCONSTRAINED,
                             p_end_time     IN TIMESTAMP_UNCONSTRAINED)
      RETURN BOOLEAN;
END archive_reader_pkg;
/


CREATE OR REPLACE PACKAGE BODY archive_reader_pkg
AS
   /******************************************************************************
      NAME:       archive_reader_pkg
      PURPOSE:    Transform the sample data for a given set of PV's.

      REVISIONS:
      Ver        Date        Author       Description
      ---------  ----------  -----------  ------------------------------------
      1.0        12/11/2009  Jeff Patton  Initial sample_aggregation_pkg
      1.0        02/02/2010  Kay Kasemir  Copied into archive_reader_pkg for experiments.
                                          Use TIMESTAMP data parameters, not strings.
                                          Return severity/status IDs, not names.
                                          Kept order of columns in 'base' query
                                          similar to original SAMPLE table.
                                          WIDTH_BUCKET on time as per Dave Purcell.
                                          Moved all 'global' variables into
                                          functions.
                                          Fall back to returning raw data.
                                          Determine data type by looking for first
                                          float or num_val, not just by looking
                                          at first sample.
                                          Result ordered by time, not bucket number
                                          so that the 'string' samples are in the
                                          time line.
      1.1        03/05/2010  Kay Kasemir  When there is no sample before the requested
                                          start time, use the start time as given.
                                          
      1.2        08/10/2010  Jeff Patton  Decrease the degree of parallelism
                                          in the get_paralle_degree function.                                          

   ******************************************************************************/

   /******************************************************************************
      NAME:       get_browser_data
      PURPOSE:    Return a weak reference cursor with the min, max and average
                  for a given channel by time range reduced into the variable
                  set of buckets determined by the reduction number.

                  The query listed below does the following: (Note the hint
                  syntax is altered as it's delimiter mirrors PL/SQL comment
                  delimiter.

                  Get the raw set of data for the given channel id within the
                  specified data range. We use the WITH clause so the query
                  will not have to rerun as inner queries.

                  Get a list of the string values in the SAMPLE data.

                  Get the min, max, average values for the data buckets using
                  the SQL BUCKET_WIDTH function.
                  The buckets are from the originally requested p_start_time to
                  p_end_time time range split into p_reduction_nbr sections.
                  The base_resultset request, however, starts at a time that
                  might be before p_start_time to include the last sample
                  just before the requested start time.

                  UNION ALL the two together so they can be returned as a
                  single reference cursor.

WITH base_resultset AS (
  SELECT   ** PARALLEL_INDEX(sample, pk_sample, 16) **
       smpl_time,
       severity_id,
       status_id,
       num_val,
       float_val,
       str_val
  FROM chan_arch.sample
  WHERE channel_id = :1
       AND smpl_time BETWEEN :2 AND :3
)
  SELECT -1 wb,
       smpl_time,
       severity_id,
       status_id,
       NULL min_val,
       NULL max_val,
       NULL avg_val,
       str_val,
       1 cnt
  FROM base_result
  WHERE str_val IS NOT NULL
UNION ALL
  SELECT wb,
       smpl_time,
       NULL severity_id,
       NULL status_id,
       min_val,
       max_val,
       avg_val,
       NULL str_val,
       cnt
  FROM (  SELECT wb,
                 MIN (<tag>) min_val,
                 MAX (<tag>) max_val,
                 AVG (<tag>) avg_val,
                 MEDIAN (smpl_time) smpl_time,
                 COUNT (*) cnt
            FROM (  SELECT WIDTH_BUCKET (smpl_time, :4, :5, :6) wb,
                           smpl_time,
                           <tag>
                      FROM base_result
                  ORDER BY smpl_time) a
        GROUP BY wb)
ORDER BY smpl_time

   ******************************************************************************/
   FUNCTION get_browser_data (p_chan_id         IN NUMBER,
                              p_start_time      IN TIMESTAMP_UNCONSTRAINED,
                              p_end_time        IN TIMESTAMP_UNCONSTRAINED,
                              p_reduction_nbr   IN NUMBER)
      RETURN SYS_REFCURSOR
   IS
      v_start_time              TIMESTAMP_UNCONSTRAINED;
      v_count                   NUMBER;
      v_datatype                VARCHAR2 (9);
      c_browser_data            SYS_REFCURSOR;
      l_return_raw_data         NUMBER := 0;
      l_cursor_stmt             VARCHAR2 (32767);
      l_cursor_width_bucket_1   VARCHAR2 (400) := 'WITH base_result
        AS (';

      l_cursor_base_query       VARCHAR2 (400)
                                   := '
       smpl_time,
       severity_id,
       status_id,
       num_val,
       float_val,
       str_val
  FROM chan_arch.sample
  WHERE channel_id = :1
    AND smpl_time BETWEEN :2 AND :3';

      l_cursor_width_bucket_2   VARCHAR2 (2000)
         := '
)
  SELECT -1 wb,
       smpl_time,
       severity_id,
       status_id,
       NULL min_val,
       NULL max_val,
       NULL avg_val,
       str_val,
       1 cnt
  FROM base_result
  WHERE str_val IS NOT NULL
UNION ALL
  SELECT wb,
       smpl_time,
       NULL severity_id,
       NULL status_id,
       min_val,
       max_val,
       avg_val,
       NULL str_val,
       cnt
  FROM (  SELECT wb,
                 MIN (<tag>) min_val,
                 MAX (<tag>) max_val,
                 AVG (<tag>) avg_val,
                 MEDIAN (smpl_time) smpl_time,
                 COUNT (*) cnt
            FROM (  SELECT WIDTH_BUCKET (smpl_time, :4, :5, :6) wb,
                           smpl_time,
                           <tag>
                      FROM base_result
                  ORDER BY smpl_time) a
        GROUP BY wb)
ORDER BY smpl_time';
   BEGIN
      --
      -- Verify the parameters
      --
      IF opr$oracle.global_utils.is_numeric (p_chan_id) = FALSE THEN
         raise_application_error (
            -20010,
            'The channel id must be numeric and not null');
      END IF;

      IF p_start_time IS NULL OR p_end_time IS NULL THEN
         raise_application_error (-20010,
                                  'The start and end time must be specified');
      END IF;

      IF opr$oracle.global_utils.is_numeric (p_reduction_nbr) = FALSE THEN
         raise_application_error (
            -20010,
            'The reduction number be numeric and not null');
      END IF;

      --
      -- Find the closest timestamp that is less than or equal to the starting
      -- time.
      --
      v_start_time := get_actual_start_time (p_chan_id, p_start_time);

      --
      -- Determine how many records are in the time range.
      --
      v_count := get_count_by_date_range (p_chan_id, p_start_time, p_end_time);

      --- If there is less data than requested, return raw data.
      --- This includes the case of no data at all.
      IF v_count < p_reduction_nbr THEN
         l_return_raw_data := 1;
      ELSE
         --
         -- Find out what datatype the channel value is.
         --
         v_datatype :=
            get_sample_datatype (p_chan_id, v_start_time, p_end_time);

         IF v_datatype IS NOT NULL THEN
            l_cursor_width_bucket_2 :=
               REPLACE (l_cursor_width_bucket_2, '<tag>', v_datatype);
         ELSE
            -- Data cannot be reduced numerically. Return the raw data
            l_return_raw_data := 1;
         END IF;
      END IF;

      -- Construct the basic query for raw data
      l_cursor_base_query :=
         get_parallel_degree (p_start_time, p_end_time)
         || l_cursor_base_query;

      -- Combine into the complex query that creates 'buckets'
      l_cursor_stmt :=
            l_cursor_width_bucket_1
         || l_cursor_base_query
         || l_cursor_width_bucket_2;

      DBMS_APPLICATION_INFO.
       SET_MODULE (module_name   => 'archive_reader_pkg.get_browser_data',
                   action_name   => 'Run main query');

      BEGIN
         IF l_return_raw_data = 1 THEN
            -- Return data from the base query, i.e. raw data, but(!) sort it by time
            l_cursor_stmt := l_cursor_base_query || ' ORDER BY smpl_time';

            OPEN c_browser_data FOR l_cursor_stmt
               USING p_chan_id, v_start_time, p_end_time;
         ELSE
            -- Get the min, max and average for the calculated window of records
            OPEN c_browser_data FOR l_cursor_stmt
               USING p_chan_id,
                     v_start_time,
                     p_end_time,
                     p_start_time,
                     p_end_time,
                     p_reduction_nbr;
         END IF;
      EXCEPTION
         WHEN NO_DATA_FOUND THEN
            raise_application_error (
               -20010,
               'No records found which match the critieria');
         WHEN USER_CANCEL THEN
            RAISE;
         WHEN OTHERS THEN
            opr$oracle.global_utils.v_program :=
               'CHAN_ARCH_SNS.archive_reader_pkg.get_browser_data-Browser data cursor';
            opr$oracle.global_utils.v_errornum := SQLCODE;
            opr$oracle.global_utils.v_errortxt := SUBSTR (SQLERRM, 1, 200);
            opr$oracle.global_utils.
             log_error (opr$oracle.global_utils.v_program,
                        opr$oracle.global_utils.v_errornum,
                        opr$oracle.global_utils.v_errortxt);
            RAISE;
      END;

      RETURN c_browser_data;
   EXCEPTION
      WHEN USER_CANCEL THEN
         RAISE;
      WHEN OTHERS THEN
         opr$oracle.global_utils.v_program :=
            'CHAN_ARCH_SNS.archive_reader_pkg.get_browser_data';
         opr$oracle.global_utils.v_errornum := SQLCODE;
         opr$oracle.global_utils.v_errortxt := SUBSTR (SQLERRM, 1, 200);
         opr$oracle.global_utils.
          log_error (opr$oracle.global_utils.v_program,
                     opr$oracle.global_utils.v_errornum,
                     opr$oracle.global_utils.v_errortxt);
         RAISE;
   END get_browser_data;

   /******************************************************************************
      NAME:       get_browser_data_by_array
      PURPOSE:    Return a weak reference cursor with the min, max and average
                  for a given channel by time range reduced into the variable
                  set of buckets determined by the reduction number.

                  The is an alternative way to get the browser data using
                  an array to achieve the same thing as we did using SQL
                  in the get_browser_data function.

                  This is left here for comparison, it's not used by CSS
   ******************************************************************************/

   FUNCTION get_browser_data_by_array (
      p_chan_id         IN NUMBER,
      p_start_time      IN TIMESTAMP_UNCONSTRAINED,
      p_end_time        IN TIMESTAMP_UNCONSTRAINED,
      p_reduction_nbr   IN NUMBER)
      RETURN SYS_REFCURSOR
   IS
      a_aggr              proj_types.sample_aggr_tab := proj_types.sample_aggr_tab ();

      v_start_time        TIMESTAMP_UNCONSTRAINED;

      c_sample            SYS_REFCURSOR;
      l_sample            chan_arch.sample%ROWTYPE;

      c_sr_browser_data   SYS_REFCURSOR;

      l_cursor_stmt       VARCHAR2 (32767);

      x                   NUMBER;
      l_bucket_width      NUMBER := 0;
      l_bucket_count      NUMBER := 0;
      l_record_count      NUMBER := 0;
      l_max_val           FLOAT := 0;
      l_min_val           FLOAT := 0;
      l_avg_val           FLOAT := 0;
      l_float_val         FLOAT := 0;
      l_max_svrty         NUMBER := 0;
      l_max_status        NUMBER := 0;

      l_start_time        TIMESTAMP_UNCONSTRAINED;
      l_end_time          TIMESTAMP_UNCONSTRAINED;

      l_count             NUMBER := 0;

      l_cursor_text       VARCHAR2 (2000)
         := ' channel_id, smpl_time, severity_id, 
                 status_id, num_val, float_val, str_val
              FROM chan_arch.sample
             WHERE channel_id = :1
                   AND smpl_time >
                         :2
                   AND smpl_time <
                         :3
             ORDER BY smpl_time';
   BEGIN
      --
      -- Verify the parameters
      --
      IF opr$oracle.global_utils.is_numeric (p_chan_id) = FALSE THEN
         raise_application_error (
            -20010,
            'The channel id must be numeric or not null');
      END IF;

      IF p_start_time IS NULL OR p_end_time IS NULL THEN
         raise_application_error (-20010,
                                  'The start and end time must be specified');
      END IF;

      IF opr$oracle.global_utils.is_numeric (p_reduction_nbr) = FALSE THEN
         raise_application_error (
            -20010,
            'The reduction number be numeric or not null');
      END IF;

      --
      -- Determine how many records are in the time range.
      --
      l_count := get_count_by_date_range (p_chan_id, p_start_time, p_end_time);

      --
      -- Determine the width of the reduction time range
      --
      l_bucket_width := ROUND (l_count / p_reduction_nbr);

      --
      -- Find the closest timestamp that is less than or equal to the starting
      -- time.
      --
      v_start_time := get_actual_start_time (p_chan_id, p_start_time);

      l_cursor_stmt :=
         get_parallel_degree (p_start_time, p_end_time) || l_cursor_text;

      DBMS_APPLICATION_INFO.
       SET_MODULE (
         module_name   => 'archive_reader_pkg.get_browser_data_by_array',
         action_name   => 'Aggregate browser data');

      --
      -- If there is a record with a string value then flag it with a -1 bucket number.
      --   If there are enough records to aggregate the values do so, otherwise just
      -- pass the records back.
      --
      BEGIN
         OPEN c_sample FOR l_cursor_stmt
            USING p_chan_id, v_start_time, p_end_time;

         x := 0;

         FETCH c_sample INTO l_sample;

         IF l_count > p_reduction_nbr THEN
            WHILE c_sample%FOUND
            LOOP
               IF l_sample.str_val IS NOT NULL THEN
                  a_aggr.EXTEND;
                  a_aggr (x) :=
                     proj_types.sample_aggr_typ (NULL,
                                                 NULL,
                                                 NULL,
                                                 NULL,
                                                 NULL,
                                                 NULL,
                                                 NULL,
                                                 NULL);
                  a_aggr (x).bucket_val := -1;
                  a_aggr (x).smpl_time := l_sample.smpl_time;
                  a_aggr (x).str_val := l_sample.str_val;
               ELSE
                  l_min_val := l_sample.float_val;

                  IF l_sample.float_val > l_max_val THEN
                     l_max_val := l_sample.float_val;
                  END IF;

                  IF l_sample.float_val < l_min_val THEN
                     l_min_val := l_sample.float_val;
                  END IF;

                  IF l_sample.severity_id > l_max_svrty THEN
                     l_max_svrty := l_sample.severity_id;
                  END IF;

                  IF l_sample.status_id > l_max_status THEN
                     l_max_status := l_sample.status_id;
                  END IF;

                  l_record_count := l_record_count + 1;
                  l_float_val := l_float_val + l_sample.float_val;

                  IF l_record_count = l_bucket_width THEN
                     l_bucket_count := l_bucket_count + 1;
                     x := x + 1;
                     a_aggr.EXTEND;
                     a_aggr (x) :=
                        proj_types.sample_aggr_typ (NULL,
                                                    NULL,
                                                    NULL,
                                                    NULL,
                                                    NULL,
                                                    NULL,
                                                    NULL,
                                                    NULL);
                     l_avg_val := l_float_val / l_record_count;

                     a_aggr (x).bucket_val := l_bucket_count;
                     a_aggr (x).min_val := l_min_val;
                     a_aggr (x).max_val := l_min_val;
                     a_aggr (x).avg_val := l_avg_val;
                     a_aggr (x).max_svrty := l_max_svrty;
                     a_aggr (x).max_stat := l_max_status;

                     l_min_val := 0;
                     l_max_val := 0;
                     l_avg_val := 0;
                     l_max_svrty := 0;
                     l_max_status := 0;
                     l_float_val := 0;
                     l_record_count := 0;
                  END IF;
               END IF;

               FETCH c_sample INTO l_sample;
            END LOOP;
         ELSE
            WHILE c_sample%FOUND
            LOOP
               l_bucket_count := l_bucket_count + 1;
               a_aggr.EXTEND;
               x := x + 1;
               a_aggr (x) :=
                  proj_types.sample_aggr_typ (NULL,
                                              NULL,
                                              NULL,
                                              NULL,
                                              NULL,
                                              NULL,
                                              NULL,
                                              NULL);
               a_aggr (x).min_val := l_sample.float_val;
               a_aggr (x).max_val := l_sample.float_val;
               a_aggr (x).avg_val := l_sample.float_val;
               a_aggr (x).smpl_time := l_sample.smpl_time;
               a_aggr (x).max_svrty := l_max_svrty;
               a_aggr (x).max_stat := l_max_status;

               l_min_val := 0;
               l_max_val := 0;
               l_avg_val := 0;
               l_max_svrty := 0;
               l_max_status := 0;
               l_float_val := 0;
               l_record_count := 0;

               FETCH c_sample INTO l_sample;
            END LOOP;
         END IF;

         CLOSE c_sample;
      EXCEPTION
         WHEN NO_DATA_FOUND THEN
            raise_application_error (
               -20010,
               'No records found which match the critieria');
         WHEN USER_CANCEL THEN
            RAISE;
         WHEN OTHERS THEN
            opr$oracle.global_utils.v_program :=
               'CHAN_ARCH_SNS.archive_reader_pkg.get_browser_data_by_array-Browser data cursor';
            opr$oracle.global_utils.v_errornum := SQLCODE;
            opr$oracle.global_utils.v_errortxt :=
               SUBSTR (SQLERRM, 1, 200) || ' ' || l_cursor_stmt;
            opr$oracle.global_utils.
             log_error (opr$oracle.global_utils.v_program,
                        opr$oracle.global_utils.v_errornum,
                        opr$oracle.global_utils.v_errortxt);
            RAISE;
      END;

      --
      -- Convert the array to a weak reference cursor before we pass it back.
      --
      OPEN c_sr_browser_data FOR
         SELECT * FROM TABLE (CAST (a_aggr AS proj_types.sample_aggr_tab));

      RETURN c_sr_browser_data;
   EXCEPTION
      WHEN USER_CANCEL THEN
         RAISE;
      WHEN OTHERS THEN
         opr$oracle.global_utils.v_program :=
            'CHAN_ARCH_SNS.archive_reader_pkg.get_browser_data_by_array';
         opr$oracle.global_utils.v_errornum := SQLCODE;
         opr$oracle.global_utils.v_errortxt := SUBSTR (SQLERRM, 1, 200);
         opr$oracle.global_utils.
          log_error (opr$oracle.global_utils.v_program,
                     opr$oracle.global_utils.v_errornum,
                     opr$oracle.global_utils.v_errortxt);
         RAISE;
   END get_browser_data_by_array;


   FUNCTION get_parallel_degree (p_start_time   IN TIMESTAMP_UNCONSTRAINED,
                                 p_end_time     IN TIMESTAMP_UNCONSTRAINED)
      RETURN VARCHAR2
   IS
      v_number          NUMBER;
      v_parallel_text   VARCHAR2 (100);
   BEGIN
      DBMS_APPLICATION_INFO.
       SET_MODULE (module_name   => 'archive_reader_pkg.get_parallel_degree',
                   action_name   => 'Determine degree of parallelism');

      --
      -- How many days are in our time range ?
      --
      v_number := CAST (p_end_time AS DATE) - CAST (p_start_time AS DATE);

      -- Original days vs. parallel: >12  16 , >8  12,  >4  8, >=4 4
      -- Now >30 = 4, >15 = 2
      CASE
         WHEN v_number >= 30 THEN
            v_parallel_text :=
               'SELECT /*+ PARALLEL_INDEX(sample, pk_sample, 4) */';
         WHEN v_number >= 15 THEN
            v_parallel_text :=
               'SELECT /*+ PARALLEL_INDEX(sample, pk_sample, 2) */';
         ELSE
            v_parallel_text := 'SELECT ';
      END CASE;

      RETURN v_parallel_text;
   EXCEPTION
      WHEN USER_CANCEL THEN
         RAISE;
      WHEN OTHERS THEN
         opr$oracle.global_utils.v_program :=
            'CHAN_ARCH_SNS.archive_reader_pkg.get_parallel_degree';
         opr$oracle.global_utils.v_errornum := SQLCODE;
         opr$oracle.global_utils.v_errortxt := SUBSTR (SQLERRM, 1, 200);
         opr$oracle.global_utils.
          log_error (opr$oracle.global_utils.v_program,
                     opr$oracle.global_utils.v_errornum,
                     opr$oracle.global_utils.v_errortxt);
         RAISE;
   END get_parallel_degree;

   --
   -- Count the number of sample records with in an time range.
   --
   FUNCTION get_count_by_date_range (
      p_chan_id      IN NUMBER,
      p_start_time   IN TIMESTAMP_UNCONSTRAINED,
      p_end_time     IN TIMESTAMP_UNCONSTRAINED)
      RETURN NUMBER
   IS
      v_sql_stmt     VARCHAR2 (4000);
      v_count        NUMBER;
      l_count_text   VARCHAR2 (1000)
         := ' COUNT(channel_id) from chan_arch.sample where channel_id = :1
       AND smpl_time BETWEEN :2 AND :3';
   BEGIN
      DBMS_APPLICATION_INFO.
       SET_MODULE (
         module_name   => 'archive_reader_pkg.get_count_by_date_range',
         action_name   => 'Count records in time range');

      --
      -- If possible parallelize the query
      --
      v_sql_stmt :=
         get_parallel_degree (p_start_time, p_end_time) || l_count_text;

      BEGIN
         EXECUTE IMMEDIATE v_sql_stmt
            INTO v_count
            USING p_chan_id, p_start_time, p_end_time;
      EXCEPTION
         WHEN USER_CANCEL THEN
            RAISE;
         WHEN OTHERS THEN
            opr$oracle.global_utils.v_program :=
               'CHAN_ARCH_SNS.archive_reader_pkg.get_count_by_date_range';
            opr$oracle.global_utils.v_errornum := SQLCODE;
            opr$oracle.global_utils.v_errortxt := SUBSTR (SQLERRM, 1, 200);
            opr$oracle.global_utils.
             log_error (opr$oracle.global_utils.v_program,
                        opr$oracle.global_utils.v_errornum,
                        opr$oracle.global_utils.v_errortxt);
            RAISE;
      END;

      RETURN v_count;
   END get_count_by_date_range;

   --
   -- Since the start time chosen by the user most likely does not exist find
   -- the actual timestamp preceding the start time.
   --
   FUNCTION get_actual_start_time (p_chan_id      IN NUMBER,
                                   p_start_time   IN TIMESTAMP_UNCONSTRAINED)
      RETURN TIMESTAMP_UNCONSTRAINED
   IS
      l_time       TIMESTAMP_UNCONSTRAINED;
      l_sql_stmt   VARCHAR2 (1000)
         := 'SELECT smpl_time FROM (SELECT smpl_time FROM chan_arch.sample WHERE channel_id = :1
       AND smpl_time <= :2 ORDER BY smpl_time desc ) WHERE ROWNUM = 1';
   BEGIN
      DBMS_APPLICATION_INFO.
       SET_MODULE (
         module_name   => 'archive_reader_pkg.get_actual_start_time',
         action_name   => 'Find the actual start time closet to the request start time');

      BEGIN
         EXECUTE IMMEDIATE l_sql_stmt
            INTO l_time
            USING p_chan_id, p_start_time;
      EXCEPTION
         WHEN NO_DATA_FOUND THEN
            -- Use original start time if no sample found before then
            l_time := p_start_time;
         WHEN USER_CANCEL THEN
            RAISE;
         WHEN OTHERS THEN
            opr$oracle.global_utils.v_program :=
               'CHAN_ARCH_SNS.archive_reader_pkg.get_actual_start_time';
            opr$oracle.global_utils.v_errornum := SQLCODE;
            opr$oracle.global_utils.v_errortxt := SUBSTR (SQLERRM, 1, 200);
            opr$oracle.global_utils.
             log_error (opr$oracle.global_utils.v_program,
                        opr$oracle.global_utils.v_errornum,
                        opr$oracle.global_utils.v_errortxt);
            RAISE;
      END;

      RETURN l_time;
   END get_actual_start_time;

   --
   -- Guess the channel's prevalent datatype within the time range.
   --
   -- Checks for the first float or num in the time range.
   -- An even more exhaustive way would be to count the first couple of
   -- floats and nums and see what's more:
   --  SELECT COUNT(float_val), COUNT(num_val) FROM
   --  (SELECT smpl_time, float_val, num_val FROM chan_arch.sample
   --     WHERE channel_id = 58418
   --     AND smpl_time BETWEEN TIMESTAMP '2010-01-21 00:00:00' AND TIMESTAMP '2010-02-04 00:00:00'
   --     AND ((float_val IS NOT NULL) OR (num_val IS NOT NULL))
   -- ) WHERE ROWNUM<10 ;
   FUNCTION get_sample_datatype (p_chan_id      IN NUMBER,
                                 p_start_time   IN TIMESTAMP_UNCONSTRAINED,
                                 p_end_time     IN TIMESTAMP_UNCONSTRAINED)
      RETURN VARCHAR2
   IS
      l_float_val   chan_arch.sample.float_val%TYPE;
      l_num_val     chan_arch.sample.num_val%TYPE;
      l_datatype    VARCHAR2 (10);
      l_sql_stmt    VARCHAR2 (1000)
         := 'SELECT float_val, num_val FROM
             (SELECT float_val, num_val FROM chan_arch.sample
                WHERE channel_id = :1
                AND smpl_time BETWEEN :2 AND :3
                AND ((float_val IS NOT NULL) OR (num_val IS NOT NULL))
             ) WHERE ROWNUM=1';
   -- Original only used first sample, possibly missing numeric data that followed:
   -- SELECT num_val, float_val, str_val FROM chan_arch.sample WHERE channel_id = :1
   BEGIN
      DBMS_APPLICATION_INFO.
       SET_MODULE (module_name   => 'archive_reader_pkg.get_sample_datatype',
                   action_name   => 'Determine the sample dataype');

      BEGIN
         EXECUTE IMMEDIATE l_sql_stmt
            INTO l_float_val, l_num_val
            USING p_chan_id, p_start_time, p_end_time;
      EXCEPTION
         WHEN USER_CANCEL THEN
            RAISE;
         WHEN OTHERS THEN
            opr$oracle.global_utils.v_program :=
               'CHAN_ARCH_SNS.archive_reader_pkg.get_sample_datatype';
            opr$oracle.global_utils.v_errornum := SQLCODE;
            opr$oracle.global_utils.v_errortxt := SUBSTR (SQLERRM, 1, 200);
            opr$oracle.global_utils.
             log_error (opr$oracle.global_utils.v_program,
                        opr$oracle.global_utils.v_errornum,
                        opr$oracle.global_utils.v_errortxt);
            RAISE;
      END;

      IF l_float_val IS NOT NULL THEN
         l_datatype := 'float_val';
      ELSIF l_num_val IS NOT NULL THEN
         l_datatype := 'num_val';
      ELSE
         l_datatype := 'str_val';
      END IF;

      RETURN l_datatype;
   END get_sample_datatype;

   --
   -- Determine if the sample is a waveform.
   --
   FUNCTION is_sample_array (p_chan_id      IN NUMBER,
                             p_start_time   IN TIMESTAMP_UNCONSTRAINED,
                             p_end_time     IN TIMESTAMP_UNCONSTRAINED)
      RETURN BOOLEAN
   IS
      v_sql_stmt     VARCHAR2 (4000);
      l_count        NUMBER (1);
      l_count_text   VARCHAR2 (1000)
         := ' COUNT(channel_id) from chan_arch.array_val where channel_id = :1
       AND smpl_time > :2
       AND smpl_time < :3 AND ROWNUM=1';
   BEGIN
      DBMS_APPLICATION_INFO.
       SET_MODULE (module_name   => 'archive_reader_pkg.is_sample_array',
                   action_name   => 'Determine if sample is a waveform');

      v_sql_stmt :=
         get_parallel_degree (p_start_time, p_end_time) || l_count_text;

      BEGIN
         EXECUTE IMMEDIATE v_sql_stmt
            INTO l_count
            USING p_chan_id, p_start_time, p_end_time;
      EXCEPTION
         WHEN USER_CANCEL THEN
            RAISE;
         WHEN OTHERS THEN
            opr$oracle.global_utils.v_program :=
               'CHAN_ARCH_SNS.archive_reader_pkg.is_sample_array';
            opr$oracle.global_utils.v_errornum := SQLCODE;
            opr$oracle.global_utils.v_errortxt := SUBSTR (SQLERRM, 1, 200);
            opr$oracle.global_utils.
             log_error (opr$oracle.global_utils.v_program,
                        opr$oracle.global_utils.v_errornum,
                        opr$oracle.global_utils.v_errortxt);
            RAISE;
      END;

      IF l_count > 0 THEN
         RETURN TRUE;
      ELSE
         RETURN FALSE;
      END IF;
   END is_sample_array;
END archive_reader_pkg;
/
