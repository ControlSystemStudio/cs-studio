--
-- Table structure for table `channel`
--

DROP TABLE IF EXISTS `channel`;
CREATE TABLE `channel` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `description` varchar(100) DEFAULT NULL,
  `datatype` varchar(100) DEFAULT NULL,
  `group_id` int(10) unsigned DEFAULT NULL,
  `control_system_id` int(10) NOT NULL,
  `enabled` boolean NOT NULL,
  `display_high` varchar(100) DEFAULT NULL,
  `display_low` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`),
  KEY `group_id` (`group_id`),
  KEY `symbol` (`control_system_id`),
  CONSTRAINT `channel_ibfk_1` FOREIGN KEY (`group_id`) REFERENCES `channel_group` (`id`),
  CONSTRAINT `symbol` FOREIGN KEY (`control_system_id`) REFERENCES `control_system` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;