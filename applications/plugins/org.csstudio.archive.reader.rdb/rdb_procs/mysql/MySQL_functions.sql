-- Stored functions and procedures 
-- for server-side determination of "optimized"
-- samples, similar to Oracle implementation.
--
-- Requires MySQL version 5.0 or higher
--
-- Laurent Philippe (Ganil)

# This file should not contain any 'tab' characters!
# When pasting the content of this file into an MySQL command-line shell,
# the tabs can otherwise trigger command-completion at the wrong moment.

# Note that MySQL assigns a "definer" to stored functions and procedures.
# You may want to execute this while connected as the "archive" database
# owner, not root.

USE archive;

DROP PROCEDURE IF EXISTS log;
DROP PROCEDURE IF EXISTS dump;
DROP FUNCTION IF EXISTS get_count_by_date_range;
DROP FUNCTION IF EXISTS get_sample_datatype;
DROP PROCEDURE IF EXISTS get_browser_data;


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
-- FUNCTION GET_COUNT_BY_DATE_RANGE
-- *************************************************************
DELIMITER |
CREATE FUNCTION get_count_by_date_range( p_chan_id INT,  p_start_time  TIMESTAMP, p_end_time DATETIME)
RETURNS INT
READS SQL DATA
BEGIN
     DECLARE v_count INT;
    select count(channel_id) into v_count from sample where channel_id = p_chan_id and smpl_time between p_start_time and p_end_time;
    return v_count;
END
|
DELIMITER ;


-- *************************************************************
-- FUNCTION GET_SAMPLE_DATATYPE
-- *************************************************************
DELIMITER |
CREATE FUNCTION get_sample_datatype(p_chan_id INT, p_start_time DATETIME, p_end_time DATETIME)
RETURNS VARCHAR(10)
READS SQL DATA
BEGIN
    DECLARE l_float_val double; -- must have the same type as sample.float_val
    DECLARE l_num_val int; -- must have the same type as sample.num_val
    DECLARE l_datatype VARCHAR(10);

    select float_val, num_val into l_float_val, l_num_val from sample 
    where channel_id = p_chan_id 
    and smpl_time between p_start_time and p_end_time 
    and ((float_val is not null) or (num_val is not null))
    LIMIT 1;

    IF l_float_val is not null THEN
        SET l_datatype = 'float_val';        
    ELSEIF l_num_val is not null THEN
        SET l_datatype = 'num_val';
    ELSE
        SET l_datatype = 'str_val';
    END IF;

    return l_datatype;
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
-- Any non-numeric samples are returned _before_ the averaged data.
-- *************************************************************
DELIMITER |
CREATE PROCEDURE get_browser_data(IN p_chan_id INT, IN p_start_time DATETIME,  IN p_end_time DATETIME,  IN p_reduction_nbr INT)
SQL SECURITY INVOKER
BEGIN
    DECLARE v_count INT;
    DECLARE v_datatype VARCHAR(9);
    DECLARE v_return INT;
    DECLARE _output TEXT DEFAULT '';
    DECLARE v_delta_time INT;
    DECLARE v_start_time INT;
    DECLARE v_for_bucket DOUBLE;

    -- TODO Get actual start time

    set v_start_time = UNIX_TIMESTAMP(p_start_time);
    set v_delta_time = UNIX_TIMESTAMP(p_end_time) - v_start_time;
    set v_for_bucket = v_delta_time / (p_reduction_nbr - 1);
    
    set @l_cur_base_query ='
        select smpl_time, severity_id, status_id, num_val, float_val, str_val, nanosecs 
        from sample 
        where channel_id = p_chan_id 
        and smpl_time between p_start_time and p_end_time
        order by smpl_time, nanosecs
        ';
        
    SET @l_cur_with_bucket = 
        'select -1 wb, smpl_time, severity_id, status_id,
        NULL min_val, NULL max_val, NULL avg_val, str_val, 1 cnt
        from sample 
        where channel_id = p_chan_id 
        and smpl_time between p_start_time and p_end_time
        and str_val is not null
        UNION ALL
            select wb,  FROM_UNIXTIME(min_smpl + (max_smpl - min_smpl)/2) smpl_time, NULL severity_id, NULL status_id, min_val, max_val, avg_val,   NULL str_val, cnt
            from(
                select floor((UNIX_TIMESTAMP(smpl_time) - v_start_time)/ v_for_bucket) wb, UNIX_TIMESTAMP(min(smpl_time)) min_smpl, UNIX_TIMESTAMP(max(smpl_time)) max_smpl ,  count(*) cnt, min(<tag>) min_val, max(<tag>) max_val, avg(<tag>) avg_val
                from sample
                where channel_id = p_chan_id
                and smpl_time between p_start_time and p_end_time
                group by wb) bucket
            ORDER by smpl_time';

    SET v_count = get_count_by_date_range(p_chan_id, p_start_time, p_end_time);
    
    -- call log('Actual value count:');
    -- call log(v_count);
    
    IF v_count < p_reduction_nbr THEN
        SET v_return = 1;else 
        SET v_datatype = get_sample_datatype(p_chan_id, p_start_time, p_end_time);
        
        IF v_datatype is not null THEN
            select REPLACE(@l_cur_with_bucket, '<tag>', v_datatype) into @l_cur_with_bucket;
        ELSE
            SET v_return = 1;
        END IF;
    END IF;
    
    IF v_return = 1 THEN
        
        select REPLACE(@l_cur_base_query, 'p_start_time',CONCAT('\'', p_start_time, '\'')) into @l_cur_base_query;
        select REPLACE(@l_cur_base_query, 'p_end_time', CONCAT('\'', p_end_time, '\'')) into @l_cur_base_query;
        select REPLACE(@l_cur_base_query, 'p_chan_id', p_chan_id) into @l_cur_base_query;
        
        -- call log(@l_cur_base_query);
        
        PREPARE stmt FROM @l_cur_base_query;
        EXECUTE stmt;
    ELSE
        select REPLACE(@l_cur_with_bucket, 'p_start_time',CONCAT('\'', p_start_time, '\'')) into @l_cur_with_bucket;
        select REPLACE(@l_cur_with_bucket, 'v_start_time',  v_start_time) into @l_cur_with_bucket;
        select REPLACE(@l_cur_with_bucket, 'p_end_time', CONCAT('\'', p_end_time, '\'')) into @l_cur_with_bucket;
        select REPLACE(@l_cur_with_bucket, 'p_chan_id', p_chan_id) into @l_cur_with_bucket;
        select REPLACE(@l_cur_with_bucket, 'v_for_bucket', v_for_bucket) into @l_cur_with_bucket;

        -- call log(@l_cur_with_bucket);
       
        PREPARE stmt FROM @l_cur_with_bucket;
        EXECUTE stmt;
    END IF;
    
END
|
DELIMITER ;


GRANT EXECUTE ON PROCEDURE get_browser_data TO '%'@'%';
GRANT EXECUTE ON PROCEDURE get_browser_data TO '%'@'localhost';

SELECT db, name, type, security_type, definer FROM mysql.proc;


-- Demo/test, may not work for you unless you have data for that channel and time range
select get_count_by_date_range(1, '2012-02-01 17:00:00', '2012-02-01 17:15:00');

select get_sample_datatype(1, '2012-02-01 17:00:00', '2012-02-01 17:15:00');

call get_browser_data(1, '2012-02-01 17:00:00', '2012-02-01 17:15:00', 50);

