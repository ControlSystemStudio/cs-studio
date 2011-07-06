--
-- Table structure for table `engine`
--

DROP TABLE IF EXISTS `engine`;
CREATE TABLE `engine` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `description` varchar(100) NOT NULL,
  `url` varchar(100) NOT NULL,
  `alive` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=latin1;

