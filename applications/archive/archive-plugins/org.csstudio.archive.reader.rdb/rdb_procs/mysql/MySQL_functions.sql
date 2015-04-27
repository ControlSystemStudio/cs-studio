-- Stored functions and procedures
-- for server-side determination of "optimized"
-- samples, similar to Oracle implementation.
--
-- Requires MySQL version 5.0 or higher
--
-- Unclear if DATETIME or TIMESTAMP is better.
-- Since the UNIXTIME-related computations are limited
-- to the TIMESTAMP range anyway, we use TIMESTAMP.
--
-- @author Laurent Philippe (Ganil, original code)
-- @author Kay Kasemir (get_actual_start_time, comments)


-- #####################################################################
-- READ THESE NOTES:
-- #####################################################################

# This file should not contain any 'tab' characters!
# When pasting the content of this file into an MySQL command-line shell,
# the tabs can otherwise trigger command-completion at the wrong moment.

# MySQL assigns a "definer" to stored functions and procedures.
# MySQL will execute a procedure in a thread that belongs to the definer.
# Only the definer can cancel() a call to the procedure.
# This implies that you should create the get_browser_data procedure
# under the MySQL user who will later read data.
# That may be a read-only account like "reports" with a commonly
# known password, different from an "archive" user that for example
# archive engines use to write data.

-- #####################################################################
-- #####################################################################

-- Ideally, the rest can be 'pasted' into a MySQL shell,
-- but you may still prefer to adjust for example permissions
-- for your needs.


USE archive;

DROP PROCEDURE IF EXISTS log;
DROP PROCEDURE IF EXISTS dump;
DROP FUNCTION IF EXISTS get_count_by_date_range;
DROP FUNCTION IF EXISTS get_actual_start_time;
DROP FUNCTION IF EXISTS get_sample_datatype;
DROP PROCEDURE IF EXISTS get_browser_data;
DROP PROCEDURE IF EXISTS get_browser_data_on_row;


# The default delimiter ';' will cause errors with the
# semicolons inside the function.
# The following will switch to '|' while creating the func/proc,
# then revert to ';'

-- *************************************************************
-- Debug stuff
-- You can skip this if all the "call log ..." calls below are commented out
-- *************************************************************
/*
DROP TABLE IF EXISTS log;
CREATE TABLE log
(
   message TEXT
);

DELIMITER |
CREATE PROCEDURE log(IN message TEXT)
BEGIN
  INSERT INTO log (message) VALUES (message);
END
|

CREATE PROCEDURE dump()
BEGIN
  SELECT * FROM log;
END
|
DELIMITER ;
*/

-- *************************************************************
-- FUNCTION get_count_by_date_range
-- *************************************************************
DELIMITER |
CREATE FUNCTION get_count_by_date_range(p_chan_id INT, p_start_time TIMESTAMP, p_end_time TIMESTAMP)
RETURNS INT
READS SQL DATA
BEGIN
    DECLARE v_count INT;
    SELECT count(channel_id) INTO v_count FROM sample
        WHERE channel_id = p_chan_id AND smpl_time BETWEEN p_start_time AND p_end_time;
    RETURN v_count;
END
|
DELIMITER ;


-- *************************************************************
-- FUNCTION get_actual_start_time
--
-- p_chan_id      : Channel ID
-- p_start_time   : Start time
--
-- Determines time stamp of actual last sample at-or-before
-- provided start time.
--
-- Ignores the nanoseconds, should be 'good enough' for
-- start time of averaged data computation
-- *************************************************************
DELIMITER |
CREATE FUNCTION get_actual_start_time(p_chan_id INT, p_start_time TIMESTAMP)
RETURNS TIMESTAMP
READS SQL DATA
BEGIN
    DECLARE l_time TIMESTAMP;
    SELECT smpl_time INTO l_time FROM sample
        WHERE channel_id = p_chan_id AND smpl_time <= p_start_time
        ORDER BY smpl_time DESC LIMIT 1;
    RETURN l_time;
END
|
DELIMITER ;


-- *************************************************************
-- FUNCTION get_sample_datatype
-- *************************************************************
DELIMITER |
CREATE FUNCTION get_sample_datatype(p_chan_id INT, p_start_time TIMESTAMP, p_end_time TIMESTAMP)
RETURNS VARCHAR(10)
READS SQL DATA
BEGIN
    DECLARE l_float_val double;     -- must have the same type as sample.float_val
    DECLARE l_num_val int;          -- must have the same type as sample.num_val
    DECLARE l_datatype VARCHAR(10);

    SELECT float_val, num_val into l_float_val, l_num_val FROM sample 
        WHERE channel_id = p_chan_id 
        AND smpl_time BETWEEN p_start_time AND p_end_time 
        AND ((float_val is not null) OR (num_val is not null))
        LIMIT 1;

    IF l_float_val is not null THEN
        SET l_datatype = 'float_val';        
    ELSEIF l_num_val is not null THEN
        SET l_datatype = 'num_val';
    ELSE
        SET l_datatype = 'str_val';
    END IF;

    RETURN l_datatype;
END
|
DELIMITER ;


-- *************************************************************
-- PROCEDURE get_browser_data
--
-- p_chan_id      : Channel ID
-- p_start_time   : Start time
-- p_end_time     : End time
-- p_reduction_nbr: Number of desired values. Must be > 1!
--
-- If there are less than p_reduction_nbr actual values,
-- the raw data is returned.
--
-- Otherwise about p_reduction_nbr min/max/average samples
-- are computed.
--
-- Any non-numeric samples are returned as is with 'wb -1'.
-- *************************************************************
DELIMITER |
CREATE PROCEDURE get_browser_data(IN p_chan_id INT, IN p_start_time TIMESTAMP,  IN p_end_time TIMESTAMP,  IN p_reduction_nbr INT)
SQL SECURITY INVOKER
BEGIN
    DECLARE v_act_start_time TIMESTAMP;
    DECLARE v_count INT;
    DECLARE v_start_time INT;
    DECLARE v_delta_time INT;
    DECLARE v_for_bucket DOUBLE;
    DECLARE v_datatype VARCHAR(9);
    DECLARE v_return_raw BOOL DEFAULT FALSE;

    -- Query for raw data
    SET @l_cur_base_query =
       'SELECT smpl_time, severity_id, status_id, num_val, float_val, str_val, nanosecs
        FROM sample
        WHERE channel_id = p_chan_id
        AND smpl_time BETWEEN p_start_time AND p_end_time
        ORDER BY smpl_time, nanosecs';
        
    -- Query for averaged, 'optimized' data
    -- Ignores the nanosecs, but that's more than "good enough"
    -- as soon as we average over more than a second.
    --
    -- Determines the returned smpl_time as the min, i.e. 'left'
    -- edge of the bucket.
    -- That way the 'average' sample will always be displayed just
    -- before special values like "Archive off" or "Channel Disconnected",
    -- avoiding problems where the plot appears to continue after such events.
    SET @l_cur_with_bucket = 
       'SELECT -1 wb, smpl_time, severity_id, status_id, NULL min_val, NULL max_val, NULL avg_val, str_val, 1 cnt
        FROM sample 
        WHERE channel_id = p_chan_id 
        AND smpl_time BETWEEN p_start_time AND p_end_time
        AND str_val is not null
        
        UNION ALL
        
        SELECT wb, min_smpl smpl_time, NULL severity_id, NULL status_id, min_val, max_val, avg_val, NULL str_val, cnt
        FROM( SELECT FLOOR((UNIX_TIMESTAMP(smpl_time) - v_start_time)/ v_for_bucket) wb,
                      MIN(smpl_time) min_smpl,
                      MIN(<tag>) min_val, MAX(<tag>) max_val, AVG(<tag>) avg_val,
                      COUNT(*) cnt
              FROM sample
              WHERE channel_id = p_chan_id
              AND smpl_time BETWEEN p_start_time AND p_end_time
              GROUP BY wb
            ) bucket
        
        ORDER BY smpl_time';

    -- Get actual start time, because there may not be a sample at p_start_time
    SET v_act_start_time = get_actual_start_time(p_chan_id, p_start_time);
    -- call log(CONCAT('Requested start time:', p_start_time));
    -- call log(CONCAT('Actual start time   :', v_act_start_time));
    
    SET v_count = get_count_by_date_range(p_chan_id, v_act_start_time, p_end_time);
    -- call log(CONCAT('Raw value count:', v_count));
    
    SET v_start_time = UNIX_TIMESTAMP(v_act_start_time);
    SET v_delta_time = UNIX_TIMESTAMP(p_end_time) - v_start_time;
    SET v_for_bucket = v_delta_time / (p_reduction_nbr - 1);
    
    IF v_count < p_reduction_nbr THEN
        -- Only few samples, return raw data
        SET v_return_raw = TRUE;
    ELSE 
        -- Determine what data to return
        SET v_datatype = get_sample_datatype(p_chan_id, v_act_start_time, p_end_time);
        
        IF v_datatype is not null THEN
            SELECT REPLACE(@l_cur_with_bucket, '<tag>', v_datatype) into @l_cur_with_bucket;
        ELSE
            -- Fall back to raw data if averaging is not possible
            SET v_return_raw = TRUE;
        END IF;
    END IF;
    
    IF v_return_raw THEN
        SELECT REPLACE(@l_cur_base_query, 'p_start_time',CONCAT('\'', v_act_start_time, '\'')) into @l_cur_base_query;
        SELECT REPLACE(@l_cur_base_query, 'p_end_time', CONCAT('\'', p_end_time, '\'')) into @l_cur_base_query;
        SELECT REPLACE(@l_cur_base_query, 'p_chan_id', p_chan_id) into @l_cur_base_query;
        
        -- call log(@l_cur_base_query);
        
        PREPARE stmt FROM @l_cur_base_query;
        EXECUTE stmt;
    ELSE
        SELECT REPLACE(@l_cur_with_bucket, 'p_start_time',CONCAT('\'', v_act_start_time, '\'')) into @l_cur_with_bucket;
        SELECT REPLACE(@l_cur_with_bucket, 'v_start_time',  v_start_time) into @l_cur_with_bucket;
        SELECT REPLACE(@l_cur_with_bucket, 'p_end_time', CONCAT('\'', p_end_time, '\'')) into @l_cur_with_bucket;
        SELECT REPLACE(@l_cur_with_bucket, 'p_chan_id', p_chan_id) into @l_cur_with_bucket;
        SELECT REPLACE(@l_cur_with_bucket, 'v_for_bucket', v_for_bucket) into @l_cur_with_bucket;

        -- call log(@l_cur_with_bucket);
       
        PREPARE stmt FROM @l_cur_with_bucket;
        EXECUTE stmt;
    END IF;
END
|
DELIMITER ;


-- *************************************************************
-- PROCEDURE get_browser_data_on_row
--
-- p_chan_id      : Channel ID
-- p_start_time   : Start time
-- p_end_time     : End time
-- p_reduction_nbr: Number of desired values. Must be > 1!
--
-- If there are less than p_reduction_nbr actual values,
-- the raw data is returned.
--
-- Otherwise about p_reduction_nbr min/max/average samples
-- are computed based on equal number of sample fragment.
--
-- Any non-numeric samples are returned as is with 'wb -1'.
-- *************************************************************
DELIMITER |
CREATE PROCEDURE get_browser_data_on_row(IN p_chan_id INT, IN p_start_time TIMESTAMP,  IN p_end_time TIMESTAMP,  IN p_reduction_nbr INT)
SQL SECURITY INVOKER
BEGIN
    DECLARE v_act_start_time TIMESTAMP;
    DECLARE v_count INT;
    DECLARE v_start_time INT;
    DECLARE v_delta_time INT;
    DECLARE v_for_bucket DOUBLE;
    DECLARE v_datatype VARCHAR(9);
    DECLARE v_return_raw BOOL DEFAULT FALSE;
    SET @row = 0;

    -- Query for raw data
    SET @l_cur_base_query =
       'SELECT smpl_time, severity_id, status_id, num_val, float_val, str_val, nanosecs
        FROM sample
        WHERE channel_id = p_chan_id
        AND smpl_time BETWEEN p_start_time AND p_end_time
        ORDER BY smpl_time, nanosecs';
        
    -- Query for averaged, 'optimized' data
    -- Ignores the nanosecs, but that's more than "good enough"
    -- as soon as we average over more than a second.
    --
    -- Determines the returned smpl_time as the median of the
    -- samples in a bucket. Could also just return the 'center'
    -- of the bucket, saving a few min() & max() calls.
    SET @l_cur_with_bucket =
       'SELECT -1 wb, smpl_time, severity_id, status_id, NULL min_val, NULL max_val, NULL avg_val, str_val, 1 cnt
        FROM sample 
        WHERE channel_id = p_chan_id 
        AND smpl_time BETWEEN p_start_time AND p_end_time
        AND str_val is not null
        
        UNION ALL
        
        SELECT wb, min_smpl smpl_time, NULL severity_id, NULL status_id, min_val, max_val, avg_val, NULL str_val, cnt
        FROM (
            SELECT FLOOR(r / v_for_bucket) wb,
                   MIN(smpl_time) min_smpl,
                   MIN(float_val) min_val, MAX(float_val) max_val, AVG(float_val) avg_val,
                   COUNT(*) cnt
            FROM (
               SELECT @row := @row + 1 r, smpl_time, float_val
               FROM sample
               WHERE channel_id = p_chan_id
               AND smpl_time BETWEEN p_start_time AND p_end_time
             ) bucket
         GROUP BY wb) bucket_median
            
         ORDER BY smpl_time';

    -- Get actual start time, because there may not be a sample at p_start_time
    SET v_act_start_time = get_actual_start_time(p_chan_id, p_start_time);
    -- call log(CONCAT('Requested start time:', p_start_time));
    -- call log(CONCAT('Actual start time   :', v_act_start_time));
    
    SET v_count = get_count_by_date_range(p_chan_id, v_act_start_time, p_end_time);
    -- call log(CONCAT('Raw value count:', v_count));
    
    SET v_start_time = UNIX_TIMESTAMP(v_act_start_time);
    SET v_delta_time = UNIX_TIMESTAMP(p_end_time) - v_start_time;
    SET v_for_bucket = v_count / (p_reduction_nbr - 1);
    
    IF v_count < p_reduction_nbr THEN
        -- Only few samples, return raw data
        SET v_return_raw = TRUE;
    ELSE 
        -- Determine what data to return
        SET v_datatype = get_sample_datatype(p_chan_id, v_act_start_time, p_end_time);
        
        IF v_datatype is not null THEN
            SELECT REPLACE(@l_cur_with_bucket, '<tag>', v_datatype) into @l_cur_with_bucket;
        ELSE
            -- Fall back to raw data if averaging is not possible
            SET v_return_raw = TRUE;
        END IF;
    END IF;
    
    IF v_return_raw THEN
        SELECT REPLACE(@l_cur_base_query, 'p_start_time',CONCAT('\'', v_act_start_time, '\'')) into @l_cur_base_query;
        SELECT REPLACE(@l_cur_base_query, 'p_end_time', CONCAT('\'', p_end_time, '\'')) into @l_cur_base_query;
        SELECT REPLACE(@l_cur_base_query, 'p_chan_id', p_chan_id) into @l_cur_base_query;
        
        -- call log(@l_cur_base_query);
        
        PREPARE stmt FROM @l_cur_base_query;
        EXECUTE stmt;
    ELSE
        SELECT REPLACE(@l_cur_with_bucket, 'p_start_time',CONCAT('\'', v_act_start_time, '\'')) into @l_cur_with_bucket;
        SELECT REPLACE(@l_cur_with_bucket, 'v_start_time',  v_start_time) into @l_cur_with_bucket;
        SELECT REPLACE(@l_cur_with_bucket, 'p_end_time', CONCAT('\'', p_end_time, '\'')) into @l_cur_with_bucket;
        SELECT REPLACE(@l_cur_with_bucket, 'p_chan_id', p_chan_id) into @l_cur_with_bucket;
        SELECT REPLACE(@l_cur_with_bucket, 'v_for_bucket', v_for_bucket) into @l_cur_with_bucket;

        -- call log(@l_cur_with_bucket);
       
        PREPARE stmt FROM @l_cur_with_bucket;
        EXECUTE stmt;
    END IF;
END
|
DELIMITER ;



-- Allow anybody to execute the stored procedure
GRANT EXECUTE ON PROCEDURE get_browser_data TO '%'@'%';
GRANT EXECUTE ON PROCEDURE get_browser_data TO '%'@'localhost';

-- With the above GRANTs it will be possible to execute get_browser_data()
-- from a mysql shell connected as user 'archive',
-- but from JDBC there will be a strange error
-- java.sql.SQLException:
-- User does not have access to metadata required to determine stored procedure parameter types.
-- If rights can not be granted, configure connection with "noAccessToProcedureBodies=true"...
--
-- The following GRANTs allow access from JDBC:
GRANT SELECT ON mysql.proc TO archive@localhost;
GRANT SELECT ON mysql.proc TO archive@'%';

SELECT db, name, type, security_type, definer FROM mysql.proc;


-- Demo/test, may not work for you unless you have data for that channel and time range
SELECT get_count_by_date_range(1, '2012-02-01 17:00:00', '2012-02-01 17:15:00');

SELECT get_sample_datatype(1, '2012-02-01 17:00:00', '2012-02-01 17:15:00');

SELECT get_actual_start_time(1, '2012-02-01 17:00:00');

call get_browser_data(1, '2012-02-01 17:00:00', '2012-02-01 17:15:00', 50);


call get_browser_data(1, '2012-02-05 17:00:00', '2012-02-06 17:15:00', 50);
-- call dump;

call get_browser_data_on_row(1, '2012-02-01 17:00:00', '2012-02-01 17:15:00', 50);

call get_browser_data(1, '2012-02-01 17:00:00', '2012-02-02 11:15:00', 50);

