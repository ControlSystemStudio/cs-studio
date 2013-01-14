--
-- Table structure for table `control_system`
--

DROP TABLE IF EXISTS `control_system`;
CREATE TABLE `control_system` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `type` enum('EPICS_V3','EPICS_V4','TANGO','DOOCS') NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;
