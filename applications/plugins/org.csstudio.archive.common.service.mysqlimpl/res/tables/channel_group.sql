--
-- Table structure for table `channel_group`
--

DROP TABLE IF EXISTS `channel_group`;
CREATE TABLE `channel_group` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `engine_id` int(10) unsigned NOT NULL,
  `description` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`),
  KEY `engine_id` (`engine_id`),
  CONSTRAINT `channel_group_ibfk_1` FOREIGN KEY (`engine_id`) REFERENCES `engine` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;
