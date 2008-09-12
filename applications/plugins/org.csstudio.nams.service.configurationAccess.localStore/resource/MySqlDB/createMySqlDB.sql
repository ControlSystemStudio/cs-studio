
DROP TABLE AMS_FilterCond_Junction;
CREATE TABLE AMS_FilterCond_Junction
(
   iFilterConditionRef INT NOT NULL,
   Operator VARCHAR(3) NOT NULL
);

DROP TABLE AMS_FilterCond_Junction_Syn;
CREATE TABLE AMS_FilterCond_Junction_Syn
(
   iFilterConditionRef INT NOT NULL,
   Operator VARCHAR(3) NOT NULL
);

DROP TABLE AMS_FilterCond_FilterCond;
CREATE TABLE AMS_FilterCond_FilterCond
(
   iFilterConditionId INT NOT NULL,
   iFilterConditionRef INT NOT NULL
);

DROP TABLE AMS_FilterCond_FilterCond_Syn;
CREATE TABLE AMS_FilterCond_FilterCond_Syn
(
   iFilterConditionId INT NOT NULL,
   iFilterConditionRef INT NOT NULL
);

DROP TABLE AMS_FilterCond_Negation;
CREATE TABLE AMS_FilterCond_Negation
(
   iFilterConditionRef INT NOT NULL,
   iNegatedFCRef INT NOT NULL
);

DROP TABLE AMS_FilterCond_Negation_Syn;
CREATE TABLE AMS_FilterCond_Negation_Syn
(
   iFilterConditionRef INT NOT NULL,
   iNegateDfcRef INT NOT NULL
);

drop table AMS_User;
create table AMS_User
(
	iUserId 		INT NOT NULL,
	iGroupRef		INT default -1 NOT NULL, /* FK AMS_Groups.iGroupId				*/
	cUserName 		VARCHAR(128),
	cEmail 			VARCHAR(128),			/* f�r MAIL 						*/
	cMobilePhone		VARCHAR(64),			/* f�r SMS 						*/
	cPhone			VARCHAR(64),			/* f�r VM 						*/
	cStatusCode		VARCHAR(32),			/* Identifz. f�r Remote An- und Abmelden 		*/
	cConfirmCode		VARCHAR(32),			/* Best�tigungscode der Antwort 			*/
	sActive			SMALLINT,			/* 0 - Inactive, 1 - Active				*/
	sPreferredAlarmingTypeRR	SMALLINT,		/* ReplyRequired: 1 - SMS, 2 - VM, 3 - MAIL 		*/
	/*sPreferredAlarmingType	SMALLINT,		Without Reply: 1 - SMS, 2 - VM, 3 - MAIL 		*/
	PRIMARY KEY (iUserId)						
);

drop table AMS_UserGroup;
create table AMS_UserGroup 
(
	iUserGroupId		INT NOT NULL,		
	iGroupRef		INT default -1 NOT NULL,	/* FK AMS_Groups.iGroupId				*/
	cUserGroupName		VARCHAR(128),
	sMinGroupMember		SMALLINT,			/* Anzahl minimale aktive Benutzer f�r die Alarmbearbeitung */
	iTimeOutSec		INT,			/* Timeout pro Benachrichtigungsversuch 		*/
	sActive			SMALLINT,			/* 0 - Inactive, 1 - Active				*/
	PRIMARY KEY (iUserGroupId)						
);

drop table AMS_UserGroup_User;
create table AMS_UserGroup_User
(
	iUserGroupRef		INT NOT NULL,
	iUserRef		INT NOT NULL,
	iPos			INT NOT NULL,		/* Benchrichtigungsreihenfolge 				*/
	sActive			SMALLINT,			/* Gruppenzugeh�rigkeit aktiv?(0 - Inactive, 1 - Active) */
	cActiveReason		VARCHAR(128),			/* Grund/Ursache der An/Abmeldung			*/
	tTimeChange		BIGINT,			/* Zeitstempel der letzten �nderung des Datensatzes	*/
	PRIMARY KEY(iUserGroupRef,iUserRef)					
);


drop table AMS_FilterConditionType;
create table AMS_FilterConditionType				/* Stringbed., Zeitbed., Array, System 	*/
(
	iFilterConditionTypeID	INT,
	cName			VARCHAR(128),
	cClass			VARCHAR(256),			/* Filterklasse 					*/
	cClassUI		VARCHAR(256),
	PRIMARY KEY(iFilterConditionTypeID) 
);

drop table AMS_FilterCondition;
create table AMS_FilterCondition
(
	iFilterConditionID	INT NOT NULL,
	iGroupRef		INT default -1 NOT NULL,	/*FK AMS_Groups.iGroupId				*/
	cName			VARCHAR(128),
	cDesc			VARCHAR(256),
	iFilterConditionTypeRef INT,			/*FK AMS_FilterConditionType.iFilterConditionTypeID 	*/
	PRIMARY KEY(iFilterConditionID)
);

drop table AMS_FilterCondition_String;
create table AMS_FilterCondition_String
(
	iFilterConditionRef	INT NOT NULL,
	cKeyValue		VARCHAR(16),
	sOperator		SMALLINT,
	cCompValue		VARCHAR(128)
);

drop table AMS_FilterCond_ArrStr;
create table AMS_FilterCond_ArrStr
(
	iFilterConditionRef	INT NOT NULL,
	cKeyValue		VARCHAR(16),
	sOperator		SMALLINT
);

drop table AMS_FilterCond_ArrStrVal;
create table AMS_FilterCond_ArrStrVal
(
	iFilterConditionRef	INT NOT NULL,
	cCompValue		VARCHAR(128)
);


drop table AMS_FilterCond_TimeBased;
create table AMS_FilterCond_TimeBased
(
	iFilterConditionRef	INT NOT NULL,
	cStartKeyValue		VARCHAR(16),
	sStartOperator		SMALLINT,
	cStartCompValue		VARCHAR(128),
	cConfirmKeyValue	VARCHAR(16),
	sConfirmOperator	SMALLINT,
	cConfirmCompValue	VARCHAR(128),
	sTimePeriod			SMALLINT,
	sTimeBehavior		SMALLINT
);

drop table AMS_FilterCondition_PV;
create table AMS_FilterCondition_PV
(
	iFilterConditionRef	INT NOT NULL,
	cPvChannelName		VARCHAR(128),
	sSuggestedPvTypeId	SMALLINT,
	sOperatorId			SMALLINT,
	cCompValue			VARCHAR(128)
);

drop table AMS_FilterCond_Conj_Common;
create table AMS_FilterCond_Conj_Common
(
	iFilterConditionRef			INT NOT NULL,
	iFirstFilterConditionRef	INT NOT NULL,
	iSecondFilterConditionRef   INT NOT NULL,
	iOperand                    SMALLINT
);

drop table AMS_Filter;
create table AMS_Filter
(
	iFilterID		INT,
	iGroupRef		INT default -1 NOT NULL, /*FK AMS_Groups.iGroupId				*/
	cName			VARCHAR(128),
	cDefaultMessage		VARCHAR(1024),			/* Default Msg mit Platzhalter, wenn in Aktion keine Msg */
	PRIMARY KEY (iFilterID)
);

drop table AMS_Filter_FilterCondition;
create table AMS_Filter_FilterCondition
(
	iFilterRef			INT,
	iFilterConditionRef	INT,
	iPos				INT,
	PRIMARY KEY (iFilterRef,iFilterConditionRef)
);

drop table AMS_Topic;
create table AMS_Topic
(
	iTopicId 		INT NOT NULL,
	iGroupRef		INT default -1 NOT NULL, -- FK AMS_Groups.iGroupId
	cTopicName 		VARCHAR(128),
	cName	 		VARCHAR(128),
	cDescription	VARCHAR(256),
	PRIMARY KEY (iTopicId)						
);

drop table AMS_FilterActionType;			
create table AMS_FilterActionType				/* 1-9 definiert, 100 - freie Topics 			*/
(			
	iFilterActionTypeID	INT NOT NULL,    	/* 0, 1 - SMS, 2 - SMS G, 3 - SMS G R, 4 - VM, 5 - VM G, 6 - VM G R, 7 - MAIL, 8 - MAIL G, 9 - MAIL G R */
	cName			VARCHAR(128),
	iTopicRef		INT,
	PRIMARY KEY(iFilterActionTypeID)
);

drop table AMS_FilterAction;
create table AMS_FilterAction
(			
	iFilterActionID		INT NOT NULL,
	iFilterActionTypeRef	INT NOT NULL,		/*FK AMS_FilterActionType.iFilterActionTypeID 		*/
	iReceiverRef		INT,			/* abh�ngig von iFilterActionTypeID User oder UserGroup */
	cMessage		VARCHAR(1024),			/* Aktionsmessage mit Platzhalter der 17 Messagewerte, z.B. %HOST% */
	PRIMARY KEY(iFilterActionID)
);

drop table AMS_Filter_FilterAction;
create table AMS_Filter_FilterAction
(
	iFilterRef		INT NOT NULL,
	iFilterActionRef	INT NOT NULL,
	iPos			INT NOT NULL		/* Reihenfolge f�r die GUI, werden parallel ausgef�hrt */
);

drop table AMS_Groups;
create table AMS_Groups						/* logische GUI Baumstruktur 				*/
(
	iGroupId		INT NOT NULL,		
	cGroupName		VARCHAR(128),
	sType			SMALLINT,			/* 1 - User, 2 - UserGroup, 3 - FilterCond, 4 - Filter, 5 - Topic */
	PRIMARY KEY (iGroupId)
);

drop table AMS_DefMessageText;
create table AMS_DefMessageText
(
	iDefMessageTextID	INT	NOT NULL,
	cName			VARCHAR(128) 	NOT NULL,
	cText			VARCHAR(1024)	NOT NULL,
	PRIMARY KEY(iDefMessageTextID)
);

drop table AMS_Flag;
create table AMS_Flag
(
	cFlagName		VARCHAR(32)	NOT NULL,
	sFlagValue		SMALLINT	NOT NULL,
	PRIMARY KEY(cFlagName)
);

drop table AMS_User_Syn;
create table AMS_User_Syn
(
	iUserId 		INT NOT NULL,
	iGroupRef		INT default -1 NOT NULL, /* FK AMS_Groups.iGroupId				*/
	cUserName 		VARCHAR(128),
	cEmail 			VARCHAR(128),			/* f�r MAIL 						*/
	cMobilePhone		VARCHAR(64),			/* f�r SMS 						*/
	cPhone			VARCHAR(64),			/* f�r VM 						*/
	cStatusCode		VARCHAR(32),			/* Identifz. f�r Remote An- und Abmelden 		*/
	cConfirmCode		VARCHAR(32),			/* Best�tigungscode der Antwort 			*/
	sActive			SMALLINT,			/* 0 - Inactive, 1 - Active				*/
	sPreferredAlarmingTypeRR	SMALLINT,		/* ReplyRequired: 1 - SMS, 2 - VM, 3 - MAIL 		*/
	/*sPreferredAlarmingType	SMALLINT,		Without Reply: 1 - SMS, 2 - VM, 3 - MAIL 		*/
	PRIMARY KEY (iUserId)						
);

drop table AMS_UserGroup_User_Syn;
create table AMS_UserGroup_User_Syn
(
	iUserGroupRef		INT NOT NULL,
	iUserRef		INT NOT NULL,
	iPos			INT NOT NULL,		/* Benchrichtigungsreihenfolge 				*/
	sActive			SMALLINT,			/* Gruppenzugeh�rigkeit aktiv?(0 - Inactive, 1 - Active) */
	cActiveReason		VARCHAR(128),			/* Grund/Ursache der An/Abmeldung			*/
	tTimeChange		BIGINT,			/* Zeitstempel der letzten �nderung des Datensatzes	*/
	PRIMARY KEY(iUserGroupRef,iUserRef)					
);

drop table AMS_FilterConditionType_Syn;
create table AMS_FilterConditionType_Syn			/* Stringbed., Zeitbed., Array, System 	*/
(
	iFilterConditionTypeID	INT,
	cName			VARCHAR(128),
	cClass			VARCHAR(256),			/* Filterklasse 					*/
	cClassUI		VARCHAR(256),
	PRIMARY KEY(iFilterConditionTypeID) 
);

drop table AMS_FilterCondition_Syn;
create table AMS_FilterCondition_Syn
(
	iFilterConditionID	INT NOT NULL,
	iGroupRef		INT default -1 NOT NULL,	/*FK AMS_Groups.iGroupId				*/
	cName			VARCHAR(128),
	cDesc			VARCHAR(256),
	iFilterConditionTypeRef INT,			/*FK AMS_FilterConditionType.iFilterConditionTypeID 	*/
	PRIMARY KEY(iFilterConditionID)
);

drop table AMS_FilterCondition_String_Syn;
create table AMS_FilterCondition_String_Syn
(
	iFilterConditionRef	INT NOT NULL,
	cKeyValue		VARCHAR(16),
	sOperator		SMALLINT,
	cCompValue		VARCHAR(128)
);

drop table AMS_FilterCond_ArrStr_Syn;
create table AMS_FilterCond_ArrStr_Syn
(
	iFilterConditionRef	INT NOT NULL,
	cKeyValue		VARCHAR(16),
	sOperator		SMALLINT
);

drop table AMS_FilterCond_ArrStrVal_Syn;
create table AMS_FilterCond_ArrStrVal_Syn
(
	iFilterConditionRef	INT NOT NULL,
	cCompValue		VARCHAR(128)
);


drop table AMS_FilterCond_TimeBased_Syn;
create table AMS_FilterCond_TimeBased_Syn
(
	iFilterConditionRef	INT NOT NULL,
	cStartKeyValue		VARCHAR(16),
	sStartOperator		SMALLINT,
	cStartCompValue		VARCHAR(128),
	cConfirmKeyValue	VARCHAR(16),
	sConfirmOperator	SMALLINT,
	cConfirmCompValue	VARCHAR(128),
	sTimePeriod		SMALLINT,
	sTimeBehavior		SMALLINT
);

drop table AMS_FilterCondition_PV_Syn;
create table AMS_FilterCondition_PV_Syn
(
	iFilterConditionRef	INT NOT NULL,
	cPvChannelName		VARCHAR(128),
	sSuggestedPvTypeId	SMALLINT,
	sOperatorId			SMALLINT,
	cCompValue			VARCHAR(128)
);

drop table AMS_FilterCond_Conj_Common_Syn;
create table AMS_FilterCond_Conj_Common_Syn
(
	iFilterConditionRef			INT NOT NULL,
	iFirstFilterConditionRef	INT NOT NULL,
	iSecondFilterConditionRef   INT NOT NULL,
	iOperand					SMALLINT default 0
);

drop table AMS_Filter_Syn;
create table AMS_Filter_Syn
(
	iFilterID		INT,
	iGroupRef		INT default -1 NOT NULL, /*FK AMS_Groups.iGroupId				*/
	cName			VARCHAR(128),
	cDefaultMessage		VARCHAR(1024),			/* Default Msg mit Platzhalter, wenn in Aktion keine Msg */
	PRIMARY KEY (iFilterID)
);

drop table AMS_Filter_FilterCondition_Syn;
create table AMS_Filter_FilterCondition_Syn
(
	iFilterRef		INT,
	iFilterConditionRef	INT,
	iPos			INT,
	PRIMARY KEY (iFilterRef,iFilterConditionRef)
);

drop table AMS_Topic_Syn;
create table AMS_Topic_Syn
(
	iTopicId 		INT NOT NULL,
	iGroupRef		INT default -1 NOT NULL, -- FK AMS_Groups.iGroupId
	cTopicName 		VARCHAR(128),
	cName	 		VARCHAR(128),
	cDescription	VARCHAR(256),
	PRIMARY KEY (iTopicId)						
);

drop table AMS_FilterActionType_Syn;
create table AMS_FilterActionType_Syn				/* 1-9 definiert, 100 - freie Topics 			*/
(			
	iFilterActionTypeID	INT NOT NULL,    	/* 0, 1 - SMS, 2 - SMS G, 3 - SMS G R, 4 - VM, 5 - VM G, 6 - VM G R, 7 - MAIL, 8 - MAIL G, 9 - MAIL G R */
	cName			VARCHAR(128),
	iTopicRef		INT,
	PRIMARY KEY(iFilterActionTypeID)
);

drop table AMS_FilterAction_Syn;
create table AMS_FilterAction_Syn
(			
	iFilterActionID		INT NOT NULL,
	iFilterActionTypeRef	INT NOT NULL,		/*FK AMS_FilterActionType.iFilterActionTypeID 		*/
	iReceiverRef		INT,			/* abh�ngig von iFilterActionTypeID User oder UserGroup */
	cMessage		VARCHAR(1024),			/* Aktionsmessage mit Platzhalter der 17 Messagewerte, z.B. %HOST% */
	PRIMARY KEY(iFilterActionID)
);

drop table AMS_Filter_FilterAction_Syn;
create table AMS_Filter_FilterAction_Syn
(
	iFilterRef		INT NOT NULL,
	iFilterActionRef	INT NOT NULL,
	iPos			INT NOT NULL		/* Reihenfolge f�r die GUI, werden parallel ausgef�hrt */
);

drop table AMS_UserGroup_Syn;
create table AMS_UserGroup_Syn
(
	iUserGroupId		INT NOT NULL,		
	iGroupRef		INT default -1 NOT NULL,	/* FK AMS_Groups.iGroupId				*/
	cUserGroupName		VARCHAR(128),
	sMinGroupMember		SMALLINT,			/* Anzahl minimale aktive Benutzer f�r die Alarmbearbeitung */
	iTimeOutSec		INT,			/* Timeout pro Benachrichtigungsversuch 		*/
	sActive			SMALLINT,			/* 0 - Inactive, 1 - Active				*/
	PRIMARY KEY (iUserGroupId)						
);

-- Init Oracle AMS --

delete from AMS_User;
delete from AMS_UserGroup;
delete from AMS_UserGroup_User;

delete from AMS_FilterConditionType;
delete from AMS_FilterCondition;
delete from AMS_FilterCondition_String;
delete from AMS_FilterCondition_PV;
delete from AMS_FilterCond_ArrStr;
delete from AMS_FilterCond_ArrStrVal;
delete from AMS_FilterCond_TimeBased;
delete from AMS_Filter;
delete from AMS_Filter_FilterCondition;

delete from AMS_Topic;
delete from AMS_FilterActionType;
delete from AMS_FilterAction;
delete from AMS_Filter_FilterAction;

delete from AMS_Groups;
delete from AMS_DefMessageText;
delete from AMS_Flag;


insert into AMS_FilterConditionType (iFilterConditionTypeID,cName,cClass,cClassUI) values (1,'Stringbasiert','org.csstudio.ams.filter.FilterConditionString','org.csstudio.ams.filter.ui.FilterConditionStringUI');
insert into AMS_FilterConditionType (iFilterConditionTypeID,cName,cClass,cClassUI) values (2,'Zeitbasiert','org.csstudio.ams.filter.FilterConditionTimeBased','org.csstudio.ams.filter.ui.FilterConditionTimeBasedUI');
insert into AMS_FilterConditionType (iFilterConditionTypeID,cName,cClass,cClassUI) values (3,'Stringbasiert (Array)','org.csstudio.ams.filter.FilterConditionArrayString','org.csstudio.ams.filter.ui.FilterConditionArrayStringUI');
insert into AMS_FilterConditionType (iFilterConditionTypeID,cName,cClass,cClassUI) values (4,'PV-basiert','org.csstudio.ams.filter.FilterConditionProcessVariable','org.csstudio.ams.filter.ui.FilterConditionProcessVariableUI');
insert into AMS_FilterConditionType (iFilterConditionTypeID,cName,cClass,cClassUI) values (5,'Oder-Verknuepft','org.csstudio.ams.filter.FilterConditionOrConjunction','org.csstudio.ams.filter.ui.FilterConditionOrConjunctionUI');

insert into AMS_FilterActionType (iFilterActionTypeID,cName,iTopicRef) values (1,'SMS an Person',NULL);
insert into AMS_FilterActionType (iFilterActionTypeID,cName,iTopicRef) values (2,'SMS an Gruppe',NULL);
insert into AMS_FilterActionType (iFilterActionTypeID,cName,iTopicRef) values (3,'SMS an Gruppe Best.',NULL);
insert into AMS_FilterActionType (iFilterActionTypeID,cName,iTopicRef) values (4,'VMail an Person',NULL);
insert into AMS_FilterActionType (iFilterActionTypeID,cName,iTopicRef) values (5,'VMail an Gruppe',NULL);
insert into AMS_FilterActionType (iFilterActionTypeID,cName,iTopicRef) values (6,'VMail an Gruppe Best.',NULL);
insert into AMS_FilterActionType (iFilterActionTypeID,cName,iTopicRef) values (7,'EMail an Person',NULL);
insert into AMS_FilterActionType (iFilterActionTypeID,cName,iTopicRef) values (8,'EMail an Gruppe',NULL);
insert into AMS_FilterActionType (iFilterActionTypeID,cName,iTopicRef) values (9,'EMail an Gruppe Best.',NULL);
insert into AMS_FilterActionType (iFilterActionTypeID,cName,iTopicRef) values (10,'Message an Topic',NULL);

insert into AMS_FilterActionType (iFilterActionTypeID,cName,iTopicRef) values (100,'Topic Special Log',1);
insert into AMS_FilterActionType (iFilterActionTypeID,cName,iTopicRef) values (101,'Topic 101',2);

insert into AMS_Flag (cFlagName, sFlagValue) values ('BupState', 0);

