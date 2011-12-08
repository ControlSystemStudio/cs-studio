--
-- Table structure for table `last_sample_time`
--

DROP TABLE IF EXISTS `last_sample_time`;
CREATE TABLE `last_sample_time` (
  `channel_id` int(10) unsigned NOT NULL,
  `time` bigint(20) DEFAULT NULL COMMENT 'Time of last written sample in nanoseconds since epoch 1970-01-01 00:00:00',
  CONSTRAINT `uk_1` UNIQUE (`channel_id`),
  CONSTRAINT `fk_1` FOREIGN KEY (`channel_id`) REFERENCES `channel` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;