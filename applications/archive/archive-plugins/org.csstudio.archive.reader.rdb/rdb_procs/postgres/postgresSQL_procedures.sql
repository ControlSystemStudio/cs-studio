-- ******************************************************************************
--      NAME:       Stored functions and procedures for server-side determination 
-- 		    of "optimized"samples, similar to Oracle/MySQL implementation.
--      PURPOSE:    Transform the sample data for a given set of PV's.
-- ******************************************************************************
--      NAME:       get_browser_data
--      PURPOSE:    Return a weak reference cursor with the computed min, max and  
--                  average for a given channel by time range reduced into the 
--                  variable set of buckets determined by the reduction number.
--                  The buckets are from the originally requested start_time to
--                  end_time time range split into reduction number sections. 
-- 		    If there is less data than requested the raw data are returned.
-- 		    This includes the case of no data at all.                 
-- ******************************************************************************

CREATE OR REPLACE FUNCTION public.get_browser_data (
	p_chan_id 	IN bigint,
	p_start_time    varchar(20),
        p_end_time      varchar(20),
        p_reduction_nbr	IN bigint) 
RETURNS REFCURSOR AS $body$
DECLARE
	c_browser_data          REFCURSOR;      		
      	t_start_time		TIMESTAMP;
     	t_end_time		TIMESTAMP;
	actual_start_time       TIMESTAMP;
	actual_start_time_nbr 	double precision;
    	v_delta_time_nbr 	double precision;
	v_for_bucket 		double precision;
      	v_records               bigint;
      	v_datatype              varchar(9);
      	l_return_raw_data       bigint := 0;
      	raw_data_query     	varchar(400) := 'SELECT smpl_time, severity_id, status_id, num_val, float_val, str_val, nanosecs
        					FROM public.sample
       						WHERE channel_id = $1
        					AND smpl_time BETWEEN $2 AND $3
        					ORDER BY smpl_time, nanosecs';
      	width_bucket_data_query varchar(2000)	:= 'SELECT -1 wb,smpl_time,severity_id,status_id,NULL min_val,NULL max_val,NULL avg_val,str_val,1 cnt
						FROM public.sample
						WHERE channel_id = $1 AND (str_val IS NOT NULL) AND smpl_time BETWEEN $2 AND $3
						UNION ALL
						SELECT wb, min_smpl smpl_time, NULL severity_id, NULL status_id, min_val, max_val, avg_val, NULL str_val, cnt
						FROM( 
							SELECT FLOOR((EXTRACT(EPOCH FROM smpl_time) - $4)/$5) wb,
							MIN(smpl_time) min_smpl, MIN(<datatype>) min_val, MAX(<datatype>) max_val, AVG(<datatype>) avg_val, COUNT(*) cnt
							FROM public.sample
							WHERE channel_id = $1 AND smpl_time BETWEEN $2 AND $3
							GROUP BY wb
						) bucket
						ORDER BY smpl_time';
BEGIN
      	t_start_time := to_timestamp(p_start_time, 'YYYY/MM/DD HH24:MI:SS');
      	t_end_time := to_timestamp(p_end_time, 'YYYY/MM/DD HH24:MI:SS');
      	-- Find the closest timestamp that is less than or equal to the starting time.
      	actual_start_time := get_actual_start_time (p_chan_id, t_start_time);
        actual_start_time_nbr := EXTRACT(EPOCH FROM actual_start_time);
	-- Find the delta time number in the time range.
	v_delta_time_nbr := EXTRACT(EPOCH FROM t_end_time) - actual_start_time_nbr;
	-- Split delta time in reduction number sections
	v_for_bucket := v_delta_time_nbr / (p_reduction_nbr - 1);
     	-- Determine how many records are in the time range.
      	v_records := get_count_by_date_range (p_chan_id, actual_start_time, t_end_time);
      	IF v_records < p_reduction_nbr THEN
        	l_return_raw_data := 1;
      	ELSE
        	-- Find out what datatype the channel value is
		v_datatype := get_sample_datatype (p_chan_id, actual_start_time, t_end_time);
		IF (v_datatype IS NOT NULL) THEN
			-- Replace the tag by the data type in width_bucket_data_query .
		    	width_bucket_data_query := REPLACE (width_bucket_data_query, '<datatype>', v_datatype);
		ELSE
		    	-- Data cannot be reduced numerically. Return the raw data
		    	l_return_raw_data := 1;
		END IF;
      	END IF;                
      	IF l_return_raw_data = 1 THEN
        	-- Return data from the base query, i.e. raw data, but(!) sort it by time            
            	OPEN c_browser_data FOR EXECUTE raw_data_query
                USING p_chan_id, actual_start_time, t_end_time;
      	ELSE
            	-- Get the min, max and average for the calculated window of records
            	OPEN c_browser_data FOR EXECUTE width_bucket_data_query
               	USING p_chan_id, actual_start_time, t_end_time, actual_start_time_nbr, v_for_bucket;
      	END IF;
      	RETURN c_browser_data;   
END;

$body$
LANGUAGE PLPGSQL;

-- ******************************************************************************
--      NAME:       get_count_by_date_range
--      PURPOSE:    Count the number of sample records with in an time range.
-- ******************************************************************************
CREATE OR REPLACE FUNCTION public.get_count_by_date_range (
	p_chan_id      IN bigint,
      	p_start_time   IN TIMESTAMP,
      	p_end_time     IN TIMESTAMP)
RETURNS bigint AS $body$
DECLARE
      	v_count        bigint;
	v_sql_stmt     varchar(1000) := 'SELECT COUNT(channel_id) 
				FROM public.sample 
				WHERE channel_id = $1 AND smpl_time BETWEEN $2 AND $3';
BEGIN
   	EXECUTE v_sql_stmt
       		INTO v_count
       		USING p_chan_id, p_start_time, p_end_time;
   	RETURN v_count;
END;

$body$
LANGUAGE PLPGSQL;

-- ******************************************************************************
--      NAME:       get_actual_start_time
--      PURPOSE:    Find the actual timestamp preceding the start time if existing
-- 		    		otherwise return the start time chosen by the user.
-- ******************************************************************************
CREATE OR REPLACE FUNCTION public.get_actual_start_time (
	p_chan_id  	IN bigint,
        p_start_time   	IN TIMESTAMP)
RETURNS TIMESTAMP AS $body$
DECLARE
      	l_time       	TIMESTAMP;
      	l_sql_stmt   	varchar(1000) := 'SELECT smpl_time 
				FROM public.sample 
				WHERE channel_id = $1 AND smpl_time <= $2 
				ORDER BY smpl_time DESC LIMIT 1';
BEGIN     
      	EXECUTE l_sql_stmt
            	INTO l_time
           	USING p_chan_id, p_start_time;
      	IF(l_time IS NOT NULL) THEN
			RETURN l_time;
		ELSE
			RETURN p_start_time;
		END IF;
END;

$body$
LANGUAGE PLPGSQL;

-- ******************************************************************************
--      NAME:       get_sample_datatype
--      PURPOSE:    Guess the channel's prevalent datatype within the time range.
--                  Checks for the first float or num in the time range.
--   ****************************************************************************
CREATE OR REPLACE FUNCTION public.get_sample_datatype (
	p_chan_id      	IN bigint,
       	p_start_time   	IN TIMESTAMP,
        p_end_time     	IN TIMESTAMP)
RETURNS varchar AS $body$
DECLARE
      	l_float_val   	double precision;
      	l_num_val     	integer;
      	l_datatype    	varchar(10);
      	l_sql_stmt    	varchar(1000) := 'SELECT float_val, num_val 
				FROM public.sample
                		WHERE channel_id = $1 AND smpl_time BETWEEN $2 AND $3
				AND ((float_val is not null) OR (num_val is not null))
        			LIMIT 1';   
BEGIN      
      	EXECUTE l_sql_stmt
      		INTO l_float_val, l_num_val
            	USING p_chan_id, p_start_time, p_end_time;
      	IF (l_float_val IS NOT NULL AND l_float_val::text <> '') THEN
       		l_datatype := 'float_val';
      	ELSIF (l_num_val IS NOT NULL AND l_num_val::text <> '') THEN
         	l_datatype := 'num_val';
      	ELSE
         	l_datatype := 'str_val';
      	END IF;
      	RETURN l_datatype;
END;

$body$
LANGUAGE PLPGSQL;

