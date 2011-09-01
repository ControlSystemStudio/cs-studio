-- Dear CSS people
-- I (bastian.knerr@desy.de) had to adjust some things on the tables to use them with MySQL:
-- Summarized: partitioning on sample, trigger on sample to write ltst_smpl_time to channel and
-- the routines for the stored procedures (which work with the MySQLStoredProcedureValueIterator 
-- but we do not intend to use, as the performance is too poor.)
-- look out for this string "(bknerr)" where I indicated my modifications 

-- BEWARE!!! application of this file to an existing database will drop anything in there!!!  


-- MySQL dump 10.13  Distrib 5.5.6-rc, for Win32 (x86)
--
-- Host: localhost    Database: archive
-- ------------------------------------------------------
-- Server version   5.5.6-rc

--
-- Log in as root
--
DROP DATABASE IF EXISTS archive_test;
CREATE DATABASE archive_test;
USE archive_test;

--
-- Table structure for table `engine`
--
DROP TABLE IF EXISTS `engine`;
CREATE TABLE `engine` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `description` varchar(100) NOT NULL,
  `url` varchar(100) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
-- BK: statement tested

--
-- Table structure for table `channel_group`
--
DROP TABLE IF EXISTS `channel_group`;
CREATE TABLE `channel_group` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `engine_id` int(10) unsigned NOT NULL,
  `description` varchar(100) DEFAULT NULL,
  `enabling_channel_id` int(10) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`),
  FOREIGN KEY (engine_id) REFERENCES engine(id)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
-- BK: statement tested

--
-- Table structure for table `sample_mode`
--
DROP TABLE IF EXISTS `sample_mode`;
CREATE TABLE `sample_mode` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `description` varchar(100) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
INSERT INTO sample_mode VALUES (1, 'MONITOR', 'Store every received update');
INSERT INTO sample_mode VALUES (2, 'SCAN', 'Periodic scan');
-- BK: statement tested

--
-- Table structure for table `severity` (EPICS related!) 
--
DROP TABLE IF EXISTS `severity`;
CREATE TABLE `severity` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
-- FOR EPICS:
--INSERT INTO severity (name) VALUES ('UNKNOWN'), ('NO_ALARM'), ('MINOR'), ('MAJOR'), ('INVALID');
-- BK: statement tested

--
-- Table structure for table `status` (EPICS related!)
--
DROP TABLE IF EXISTS `status`;
CREATE TABLE `status` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
-- FOR EPICS:
-- INSERT INTO status (name) VALUES ('NO_ALARM'), ('READ'), ('WRITE'), ('HIHI'), ('HIGH'), ('LOLO'), ('LOW'), ('STATE'), ('COS'), ('COMM'), ('TIMEOUT'), ('HWLIMIT'), ('CALC'), ('SCAN'), ('LINK'), ('SOFT'), ('BADSUB'), ('UDF'), ('DISABLE'), ('SIMM'), ('READACCESS'), ('WRITEACCESS'), ('UNKNOWN');
-- BK: statement tested

--
-- Table structure for table `channel`
--
DROP TABLE IF EXISTS `channel`;
CREATE TABLE `channel` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `description` varchar(100) DEFAULT NULL,
  `datatype` enum('Byte','Double','Float','Integer','Long','Short','Enum','String'),
  `metatype` enum('Array','Set','Scalar'),
  `group_id` int(10) unsigned DEFAULT NULL,
  `sample_mode_id` int(10) unsigned DEFAULT NULL,
  `sample_period` double DEFAULT NULL,
  `last_sample_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`),
  FOREIGN KEY (group_id) REFERENCES channel_group(id),
  FOREIGN KEY (sample_mode_id) REFERENCES sample_mode(id)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
-- BK: statement tested

--
-- Table structure for table `archiver_mgmt`
--
DROP TABLE IF EXISTS `archiver_mgmt`;
CREATE TABLE `archiver_mgmt` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `channel_id` int(10) unsigned NOT NULL,
  `monitor_mode` enum('ON', 'OFF') ,
  `engine_id` int(10) unsigned NOT NULL,
  `time` datetime NOT NULL,
  `info` varchar(1000) DEFAULT NULL,
  PRIMARY KEY (`id`, `time`, `channel_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1
PARTITION BY RANGE (TO_DAYS(time))
SUBPARTITION BY HASH (channel_id)
SUBPARTITIONS 40
(PARTITION p0 VALUES LESS THAN (734684) ENGINE = InnoDB,
 PARTITION p1 VALUES LESS THAN (734715) ENGINE = InnoDB,
 PARTITION p2 VALUES LESS THAN (734746) ENGINE = InnoDB,
 PARTITION p3 VALUES LESS THAN (734776) ENGINE = InnoDB,
 PARTITION p4 VALUES LESS THAN (734807) ENGINE = InnoDB,
 PARTITION p5 VALUES LESS THAN (734837) ENGINE = InnoDB,
 PARTITION p6 VALUES LESS THAN (734868) ENGINE = InnoDB,
 PARTITION p7 VALUES LESS THAN (734899) ENGINE = InnoDB,
 PARTITION p8 VALUES LESS THAN (734928) ENGINE = InnoDB,
 PARTITION p9 VALUES LESS THAN (734959) ENGINE = InnoDB,
 PARTITION p10 VALUES LESS THAN (734989) ENGINE = InnoDB,
 PARTITION p11 VALUES LESS THAN (735020) ENGINE = InnoDB,
 PARTITION p12 VALUES LESS THAN (735050) ENGINE = InnoDB,
 PARTITION p13 VALUES LESS THAN (735081) ENGINE = InnoDB,
 PARTITION p14 VALUES LESS THAN (735112) ENGINE = InnoDB,
 PARTITION p16 VALUES LESS THAN (735142) ENGINE = InnoDB,
 PARTITION p17 VALUES LESS THAN (735173) ENGINE = InnoDB,
 PARTITION p18 VALUES LESS THAN (735203) ENGINE = InnoDB,
 PARTITION p19 VALUES LESS THAN (735234) ENGINE = InnoDB,
 PARTITION p20 VALUES LESS THAN (735265) ENGINE = InnoDB,
 PARTITION p21 VALUES LESS THAN (735293) ENGINE = InnoDB,
 PARTITION p22 VALUES LESS THAN (735324) ENGINE = InnoDB,
 PARTITION p23 VALUES LESS THAN (735354) ENGINE = InnoDB,
 PARTITION pRest VALUES LESS THAN MAXVALUE ENGINE = InnoDB);
-- BK: statement tested

--
-- Table structure for table `sample`
--
-- (bknerr) : - partitioned table for 24 month and rest, subpartitioned (40 each) by hash on 
--              channel_id, that is then (24+1)x40=1000 partitions < max no of partitions for MySQL 
--              table
--            - added an insert trigger that updates table channel, column last_smpl_time to speed 
--              up archive engine start time (otherwise for any channel there is a 
--              "select max(smpl_time) from sample for channel_id=?" 
--              which can take forever on huge tables.
--            - no foreign keys used, as mysql partitions cannot cope with them
DROP TABLE IF EXISTS `sample`;
CREATE TABLE `sample` (
  `channel_id` int(10) unsigned NOT NULL,
  `sample_time` datetime NOT NULL,
  `ms_ns` double unsigned NOT NULL,
  `severity_id` int(10) unsigned NOT NULL,
  `status_id` int(10) unsigned NOT NULL,
  `value` varchar(1000) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1
PARTITION BY RANGE (TO_DAYS(sample_time))
SUBPARTITION BY HASH (channel_id)
SUBPARTITIONS 40
(PARTITION p0 VALUES LESS THAN (734684) ENGINE = InnoDB,
 PARTITION p1 VALUES LESS THAN (734715) ENGINE = InnoDB,
 PARTITION p2 VALUES LESS THAN (734746) ENGINE = InnoDB,
 PARTITION p3 VALUES LESS THAN (734776) ENGINE = InnoDB,
 PARTITION p4 VALUES LESS THAN (734807) ENGINE = InnoDB,
 PARTITION p5 VALUES LESS THAN (734837) ENGINE = InnoDB,
 PARTITION p6 VALUES LESS THAN (734868) ENGINE = InnoDB,
 PARTITION p7 VALUES LESS THAN (734899) ENGINE = InnoDB,
 PARTITION p8 VALUES LESS THAN (734928) ENGINE = InnoDB,
 PARTITION p9 VALUES LESS THAN (734959) ENGINE = InnoDB,
 PARTITION p10 VALUES LESS THAN (734989) ENGINE = InnoDB,
 PARTITION p11 VALUES LESS THAN (735020) ENGINE = InnoDB,
 PARTITION p12 VALUES LESS THAN (735050) ENGINE = InnoDB,
 PARTITION p13 VALUES LESS THAN (735081) ENGINE = InnoDB,
 PARTITION p14 VALUES LESS THAN (735112) ENGINE = InnoDB,
 PARTITION p16 VALUES LESS THAN (735142) ENGINE = InnoDB,
 PARTITION p17 VALUES LESS THAN (735173) ENGINE = InnoDB,
 PARTITION p18 VALUES LESS THAN (735203) ENGINE = InnoDB,
 PARTITION p19 VALUES LESS THAN (735234) ENGINE = InnoDB,
 PARTITION p20 VALUES LESS THAN (735265) ENGINE = InnoDB,
 PARTITION p21 VALUES LESS THAN (735293) ENGINE = InnoDB,
 PARTITION p22 VALUES LESS THAN (735324) ENGINE = InnoDB,
 PARTITION p23 VALUES LESS THAN (735354) ENGINE = InnoDB,
 PARTITION pRest VALUES LESS THAN MAXVALUE ENGINE = InnoDB);
DELIMITER ;;
CREATE trigger updateLastSampleTime before insert on sample for each row begin update channel set last_sample_time=NEW.sample_time where id=NEW.channel_id;end;;
DELIMITER ;
-- BK: statement tested

--
-- Table structure for table `sample_h`
-- Is filled any 3600 s = 1h with an avg, max, min value for any archived sample.
--
-- (bknerr) : - partitioned table for 24 month and rest, subpartitioned (40 each) by hash on 
--              channel_id, that is then (24+1)x40=1000 partitions < max no of partitions for MySQL 
--              table
--            - no foreign keys used, as mysql partitions cannot cope with them
CREATE TABLE `sample_h` (
  `channel_id` int(10) unsigned NOT NULL,
  `sample_time` datetime NOT NULL,
  `highest_severity_id` int(10) unsigned NOT NULL,
  `avg_val` double DEFAULT NULL,
  `min_val` double DEFAULT NULL,
  `max_val` double DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1
PARTITION BY RANGE (TO_DAYS(sample_time))
SUBPARTITION BY HASH (channel_id)
SUBPARTITIONS 40
(PARTITION p0 VALUES LESS THAN (734684) ENGINE = InnoDB,
 PARTITION p1 VALUES LESS THAN (734715) ENGINE = InnoDB,
 PARTITION p2 VALUES LESS THAN (734746) ENGINE = InnoDB,
 PARTITION p3 VALUES LESS THAN (734776) ENGINE = InnoDB,
 PARTITION p4 VALUES LESS THAN (734807) ENGINE = InnoDB,
 PARTITION p5 VALUES LESS THAN (734837) ENGINE = InnoDB,
 PARTITION p6 VALUES LESS THAN (734868) ENGINE = InnoDB,
 PARTITION p7 VALUES LESS THAN (734899) ENGINE = InnoDB,
 PARTITION p8 VALUES LESS THAN (734928) ENGINE = InnoDB,
 PARTITION p9 VALUES LESS THAN (734959) ENGINE = InnoDB,
 PARTITION p10 VALUES LESS THAN (734989) ENGINE = InnoDB,
 PARTITION p11 VALUES LESS THAN (735020) ENGINE = InnoDB,
 PARTITION p12 VALUES LESS THAN (735050) ENGINE = InnoDB,
 PARTITION p13 VALUES LESS THAN (735081) ENGINE = InnoDB,
 PARTITION p14 VALUES LESS THAN (735112) ENGINE = InnoDB,
 PARTITION p16 VALUES LESS THAN (735142) ENGINE = InnoDB,
 PARTITION p17 VALUES LESS THAN (735173) ENGINE = InnoDB,
 PARTITION p18 VALUES LESS THAN (735203) ENGINE = InnoDB,
 PARTITION p19 VALUES LESS THAN (735234) ENGINE = InnoDB,
 PARTITION p20 VALUES LESS THAN (735265) ENGINE = InnoDB,
 PARTITION p21 VALUES LESS THAN (735293) ENGINE = InnoDB,
 PARTITION p22 VALUES LESS THAN (735324) ENGINE = InnoDB,
 PARTITION p23 VALUES LESS THAN (735354) ENGINE = InnoDB,
 PARTITION pRest VALUES LESS THAN MAXVALUE ENGINE = InnoDB);
-- BK: statement tested

--
-- Table structure for table `sample_m`
-- Is filled any 60 s = 1h with an avg, max, min value for any archived sample.
--
-- (bknerr) : - partitioned table for 24 month and rest, subpartitioned (40 each) by hash on 
--              channel_id, that is then (24+1)x40=1000 partitions < max no of partitions for MySQL 
--              table
--            - added an insert trigger that updates table channel, column ltst_smpl_time to speed 
--              up archive engine start time (otherwise for any channel there is a 
--              "select max(smpl_time) from sample for channel_id=?" 
--              which can take forever on huge tables. 
CREATE TABLE `sample_m` (
  `channel_id` int(10) unsigned NOT NULL,
  `sample_time` datetime NOT NULL,
  `highest_severity_id` int(10) unsigned NOT NULL,
  `avg_val` double DEFAULT NULL,
  `min_val` double DEFAULT NULL,
  `max_val` double DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1
PARTITION BY RANGE (TO_DAYS(sample_time))
SUBPARTITION BY HASH (channel_id)
SUBPARTITIONS 40
(PARTITION p0 VALUES LESS THAN (734684) ENGINE = InnoDB,
 PARTITION p1 VALUES LESS THAN (734715) ENGINE = InnoDB,
 PARTITION p2 VALUES LESS THAN (734746) ENGINE = InnoDB,
 PARTITION p3 VALUES LESS THAN (734776) ENGINE = InnoDB,
 PARTITION p4 VALUES LESS THAN (734807) ENGINE = InnoDB,
 PARTITION p5 VALUES LESS THAN (734837) ENGINE = InnoDB,
 PARTITION p6 VALUES LESS THAN (734868) ENGINE = InnoDB,
 PARTITION p7 VALUES LESS THAN (734899) ENGINE = InnoDB,
 PARTITION p8 VALUES LESS THAN (734928) ENGINE = InnoDB,
 PARTITION p9 VALUES LESS THAN (734959) ENGINE = InnoDB,
 PARTITION p10 VALUES LESS THAN (734989) ENGINE = InnoDB,
 PARTITION p11 VALUES LESS THAN (735020) ENGINE = InnoDB,
 PARTITION p12 VALUES LESS THAN (735050) ENGINE = InnoDB,
 PARTITION p13 VALUES LESS THAN (735081) ENGINE = InnoDB,
 PARTITION p14 VALUES LESS THAN (735112) ENGINE = InnoDB,
 PARTITION p16 VALUES LESS THAN (735142) ENGINE = InnoDB,
 PARTITION p17 VALUES LESS THAN (735173) ENGINE = InnoDB,
 PARTITION p18 VALUES LESS THAN (735203) ENGINE = InnoDB,
 PARTITION p19 VALUES LESS THAN (735234) ENGINE = InnoDB,
 PARTITION p20 VALUES LESS THAN (735265) ENGINE = InnoDB,
 PARTITION p21 VALUES LESS THAN (735293) ENGINE = InnoDB,
 PARTITION p22 VALUES LESS THAN (735324) ENGINE = InnoDB,
 PARTITION p23 VALUES LESS THAN (735354) ENGINE = InnoDB,
 PARTITION pRest VALUES LESS THAN MAXVALUE ENGINE = InnoDB);
-- BK: statement tested



--
-- Dumping routines for database 'archive'
--
/*!50003 DROP FUNCTION IF EXISTS `FUNC_ARCHIVE_get_actual_start_time` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50020 DEFINER=`root`@`localhost`*/ /*!50003 FUNCTION `FUNC_ARCHIVE_get_actual_start_time`(p_chan_id INT,
                                                                                p_start_time TIMESTAMP) RETURNS timestamp
BEGIN
    DECLARE ret_time TIMESTAMP DEFAULT '1980-01-01 00:00:01';
    
    BEGIN
      -- FIXME (bknerr)
      -- CALL DBMS_APPLICATION_INFO.SET_MODULE('archive.get_actual_start_time','Find the actual start time closest to the request start time');
        DECLARE EXIT HANDLER FOR NOT FOUND
          begin
         -- Use original start time if no sample found before then
           SET ret_time = p_start_time;
          end;
--      DECLARE EXIT HANDLER FOR USER_CANCEL FIXME (bknerr)
        DECLARE EXIT HANDLER FOR 1317 /* 70100 : Query execution was interrupted */
          begin -- FIXME (bknerr) Not Supported RAISE;
           SET ret_time = '1980-01-01 00:00:01';
          end;
        DECLARE EXIT HANDLER FOR SQLEXCEPTION
          begin
           SET @v_program = 'ARCHIVE_READER_PKG_GET_ACTUAL_START_TIME';
           SET @v_errornum = -1 /*NOT SUPPORTED SQLCODE*/;
           SET @v_errortxt = SUBSTRING('Internal error',1,200);
           -- FIXME (bknerr) find error logging possibility (with an error log table perhaps?
--         CALL LOG_ERROR(@v_program,@v_errornum,@v_errortxt);
         end;
    

      SELECT smpl_time FROM sample 
                       WHERE channel_id = p_chan_id AND 
                             smpl_time <= p_start_time 
                       ORDER BY smpl_time DESC LIMIT 1 INTO ret_time;
    END;
    
    RETURN ret_time;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP FUNCTION IF EXISTS `FUNC_ARCHIVE_get_count_by_date_range` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50020 DEFINER=`root`@`localhost`*/ /*!50003 FUNCTION `FUNC_ARCHIVE_get_count_by_date_range`(p_chan_id INT,
                                                     p_start_time TIMESTAMP,
                                                     p_end_time TIMESTAMP) RETURNS int(11)
    DETERMINISTIC
BEGIN
   -- ---------------------------------------------------------
   -- Count the number of sample records within a time range.
   -- ---------------------------------------------------------
   -- DECLARE v_sql_stmt VARCHAR(4000);
   DECLARE ret_v_count INT DEFAULT 0;
   -- FIXME (bknerr)
   -- CALL DBMS_aPPLICATION_INFO.SET_MODULE('FUNC_ARCHIVE_get_count_by_date_range','Count the number of sample records within a time range.');

   -- FIXME (bknerr) : find out about parallelization
   -- 
   -- If possible parallelize the query
   -- 
   -- SET v_sql_stmt = CONCAT(ARCHIVE_READER_PKG_GET_PARALLEL_DEGREE(p_start_time,p_end_time), l_count_text);


   BEGIN
      -- DECLARE EXIT HANDLER FOR USER_CANCEL
      DECLARE EXIT HANDLER FOR 1317 /* 70100 : Query execution was interrupted */
      begin-- Not Supported RAISE;
         SET ret_v_count = 0;
      end;
      DECLARE EXIT HANDLER FOR NOT FOUND,SQLEXCEPTION
      begin
         SET @v_program = 'FUNC_ARCHIVE_get_count_by_date_range';
         SET @v_errornum = -1 /*NOT SUPPORTED SQLCODE*/;
         SET @v_errortxt = SUBSTRING('Internal error',1,200);
         -- FIXME (bknerr) find error logging possibility (with an error log table perhaps?
         -- CALL LOG_ERROR(opr$oracle.global_utils.v_program,opr$oracle.global_utils.v_errornum,opr$oracle.global_utils.v_errortxt);
-- Not Supported RAISE;
      end;
      
      SELECT COUNT(channel_id) FROM sample 
                               WHERE channel_id = p_chan_id AND 
                               smpl_time BETWEEN p_start_time AND p_end_time INTO ret_v_count;
      

   END;

   RETURN ret_v_count;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP FUNCTION IF EXISTS `FUNC_ARCHIVE_get_sample_datatype` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50020 DEFINER=`root`@`localhost`*/ /*!50003 FUNCTION `FUNC_ARCHIVE_get_sample_datatype`(
                                                p_chan_id INT,
                                                p_start_time TIMESTAMP,
                                                p_end_time TIMESTAMP) RETURNS varchar(20) CHARSET latin1
    DETERMINISTIC
BEGIN
   DECLARE l_float_val DOUBLE;
   DECLARE l_num_val INT(11);
   DECLARE SWP_Ret_Value VARCHAR(20);
-- Original only  used first sample, possibly missing numeric data that followed:
-- SELECT num_val, float_val, str_val FROM chan_arch.sample WHERE channel_id = :1
--   CALL SET_MODULE('SQLWAYS_EVAL# _pkg.get_sample_datatype','SQLWAYS_EVAL# dataype');

   BEGIN
      DECLARE EXIT HANDLER FOR 1317 /* 70100 : Query execution was interrupted */
      begin-- Not Supported RAISE;
         SET SWP_Ret_Value = 'error';
      end;
      DECLARE EXIT HANDLER FOR NOT FOUND, SQLEXCEPTION
      begin
         SET @v_program = 'ARCHIVE_get_sample_datatype';
         SET @v_errornum = -1 /*NOT SUPPORTED SQLCODE*/;
         SET @v_errortxt = SUBSTRING('Internal error',1,200);
         SET SWP_Ret_Value = 'str_val';
         -- CALL LOG_ERROR(opr$oracle.global_utils.v_program,opr$oracle.global_utils.v_errornum,opr$oracle.global_utils.v_errortxt);
         -- Not Supported RAISE;
      end;
      SELECT float_val, num_val FROM archive.sample 
                                WHERE channel_id = p_chan_id 
                                AND smpl_time BETWEEN p_start_time AND p_end_time 
                                AND ((float_val IS NOT NULL) OR (num_val IS NOT NULL)) LIMIT 1 INTO l_float_val, l_num_val;
   END;
   
   IF l_float_val IS NOT NULL THEN
      SET SWP_Ret_Value = 'float_val';
   ELSEIF l_num_val IS NOT NULL THEN
      SET SWP_Ret_Value = 'num_val';
   ELSE
      SET SWP_Ret_Value = 'str_val';
   END IF;
   RETURN SWP_Ret_Value;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP FUNCTION IF EXISTS `WIDTH_BUCKET` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50020 DEFINER=`root`@`localhost`*/ /*!50003 FUNCTION `WIDTH_BUCKET`(smpl_time DATETIME, 
                                                          min_time DATETIME, 
                                                          max_time DATETIME, 
                                                          po2 INT) RETURNS int(11)
BEGIN

  DECLARE s INT;
  DECLARE min INT;
  DECLARE max INT;
  SET s = UNIX_TIMESTAMP(smpl_time);
  SET min = UNIX_TIMESTAMP(min_time);
  SET max = UNIX_TIMESTAMP(max_time);
  
  -- CHECK VALID PARAMS
  IF min > max OR po2 < 0 THEN
    RETURN -1;
  END IF;

  -- CHECK BUCKET BORDERS
  IF s < min THEN 
    RETURN 0;
  ELSE
    IF s > max THEN 
      RETURN (1<<po2) + 1;
    END IF;
  END IF;
  
  -- ITERATIVE BINARY SEARCH (RECURSION ONLY FOR PROCEDURES)
  BEGIN
    DECLARE bucket INT;
    DECLARE depth INT;
    DECLARE mid INT;
    SET depth = po2;
    SET bucket = 1;
    SET mid = min;

    WHILE depth > 0 AND s != mid AND min != max DO
    
      SET mid = min + ((max - min)>>1);
      SET depth = depth - 1;
    
      IF s < mid THEN
        SET max = mid;
      ELSE
        SET min = mid;
        SET bucket = bucket + (1<<depth);
      END IF;
      
      
    END WHILE;
    
    RETURN bucket;
  END;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `PROC_ARCHIVE_get_browser_data` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50020 DEFINER=`root`@`localhost`*/ /*!50003 PROCEDURE `PROC_ARCHIVE_get_browser_data`(IN p_chan_id INT,
                                                IN p_start_time TIMESTAMP,
                                                IN p_end_time   TIMESTAMP,
                                                IN p_reduction_nbr INT)
BEGIN

  DECLARE v_start_time TIMESTAMP;
  DECLARE v_count INT;
  DECLARE l_return_raw_data INT DEFAULT 0;
  DECLARE v_datatype VARCHAR(20);
  DECLARE l_statement VARCHAR(2000); 
  
  DROP TABLE IF EXISTS raw_result;
  CREATE TEMPORARY TABLE raw_result 
      SELECT smpl_time,
             severity_id,
             status_id,
             num_val,
             float_val,
             str_val,
             nanosecs 
          FROM sample 
          WHERE channel_id = p_chan_id AND 
                      smpl_time BETWEEN p_start_time AND p_end_time;
                      
-- FIXME : as the reuse of a temp table within one query is not possible in mysql,
--         and to copy the temp into another is quite time consuming I disabled the first select and UNION ALL
--         statement

--  CREATE TEMPORARY TABLE raw_result_2
--       SELECT * FROM raw_result;
 
--   SET l_statement = 'CREATE TEMPORARY TABLE result
--   SELECT -1 wb,
--        smpl_time,
--        severity_id,
--        status_id,
--        NULL min_val,
--        NULL max_val,
--        NULL avg_val,
--        str_val,
--        1 cnt
--   FROM raw_result_2
--   WHERE str_val IS NOT NULL
--   UNION ALL
   SET l_statement = 'CREATE TEMPORARY TABLE opt_result
     SELECT wb,
         smpl_time,
         NULL severity_id,
         NULL status_id,
         min_val,
         max_val,
         avg_val,
         NULL str_val,
         cnt
      FROM (SELECT wb,
                   MIN(<tag>) min_val,
                   MAX(<tag>) max_val,
                   AVG(<tag>) avg_val,
                   FROM_UNIXTIME(AVG(UNIX_TIMESTAMP(smpl_time))) smpl_time,
                   COUNT(*) cnt
                FROM (SELECT WIDTH_BUCKET(smpl_time, ?, ?, ?) AS wb,
                             smpl_time,
                             <tag>
                          FROM raw_result
                          ORDER BY smpl_time) t0
                GROUP BY wb) t1
      ORDER BY smpl_time;';
      
      
      --
      -- Find the closest timestamp that is less than or equal to the starting
      -- time.
      --
      SET v_start_time = FUNC_ARCHIVE_get_actual_start_time(p_chan_id, p_start_time);
      --
      -- Determine how many records are in the time range.
      --
      SET v_count := FUNC_ARCHIVE_get_count_by_date_range(p_chan_id, p_start_time, p_end_time);
      
      -- If there is less data than requested, return raw data.
      -- This includes the case of no data at all.
      IF v_count < (1<<p_reduction_nbr) THEN
         SET l_return_raw_data = 1;
      ELSE
         --
         -- Find out what datatype the channel value is.
         --
         SET v_datatype = FUNC_ARCHIVE_get_sample_datatype(p_chan_id, v_start_time, p_end_time);

         IF v_datatype IS NOT NULL THEN
            SET l_statement = REPLACE(l_statement, '<tag>', v_datatype);
         ELSE
            -- Data cannot be reduced numerically. Return the raw data
            SET l_return_raw_data = 1;
         END IF;
      END IF; 
      
      -- The execution of the statement
      BEGIN
         IF l_return_raw_data != 1 THEN
             DROP TABLE IF EXISTS opt_result;
         
             SET @SWV_Stmt = l_statement;
             PREPARE SWT_Stmt FROM @SWV_Stmt;
             SET @1 = v_start_time;
             SET @2 = p_end_time;
             SET @3 = p_reduction_nbr;
             EXECUTE SWT_Stmt USING @1, @2, @3;
             DEALLOCATE PREPARE SWT_Stmt; 
             
             SELECT * from opt_result;
          ELSE
             SELECT * from raw_result;
          END IF;
      END;

END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2010-11-03 16:27:35
