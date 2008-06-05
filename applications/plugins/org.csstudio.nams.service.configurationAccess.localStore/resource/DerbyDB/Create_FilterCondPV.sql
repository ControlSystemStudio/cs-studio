connect 'jdbc:derby://localhost/amsdb;create=true';

drop table AMS_FilterCondition_PV;
create table AMS_FilterCondition_PV
(
	iFilterConditionRef	INT NOT NULL,
	cPvChannelName		VARCHAR(128),
	sSuggestedPvTypeId	INT,
	sOperatorId		INT,
	cCompValue		VARCHAR(128)
);

disconnect;