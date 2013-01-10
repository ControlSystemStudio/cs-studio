connect 'jdbc:derby://localhost/amsdb;create=true';

drop table AMS_Topic;
create table AMS_Topic
(
	iTopicId 		INT NOT NULL,
	iGroupRef		INT default -1 NOT NULL,
	cTopicName 		VARCHAR(128),
	cName	 		VARCHAR(128),
	cDescription	VARCHAR(256),
	PRIMARY KEY (iTopicId)						
);

disconnect;
