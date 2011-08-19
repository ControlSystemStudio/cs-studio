
/*
Alte Tabellennamen - NICHT MEHR VERWENDEN
DROP TABLE IF EXISTS AMSFilterNegationCond4Filter;
--CREATE TABLE AMSFilterNegationCond4Filter (
--    iFilterConditionRef			BIGINT NOT NULL,
--    iNegatedFCRef               BIGINT NOT NULL
--);

--DROP TABLE IF EXISTS AMSFilterCondConj4FilterCommon;
--CREATE TABLE AMSFilterCondConj4FilterCommon (
--   iFilterConditionRef			BIGINT NOT NULL,
--   Operator                     VARCHAR(3) NOT NULL, 
--   CONSTRAINT AMSFilterCondConj4FilterCommon CHECK (Operator IN ('AND', 'OR'))
--);

DROP TABLE IF EXISTS AMSFilterCondConj4FilterFCJoin;
CREATE TABLE AMSFilterCondConj4FilterFCJoin (
   iFilterConditionID           BIGINT NOT NULL,
   iFilterConditionRef			BIGINT NOT NULL
 );
Alte Tabellennamen - NICHT MEHR VERWENDEN -- ENDE
*/

/* NAMS HSqlDb */

DROP TABLE IF EXISTS AMS_FilterCond_Junction;
CREATE TABLE AMS_FilterCond_Junction
(
   iFilterConditionRef BIGINT NOT NULL,
   Operator VARCHAR(3) NOT NULL
);

DROP TABLE IF EXISTS AMS_FilterCond_Junction_Syn;
CREATE TABLE AMS_FilterCond_Junction_Syn
(
   iFilterConditionRef BIGINT NOT NULL,
   Operator VARCHAR(3) NOT NULL
);

DROP TABLE IF EXISTS AMS_FilterCond_FilterCond;
CREATE TABLE AMS_FilterCond_FilterCond
(
   iFilterConditionId BIGINT NOT NULL,
   iFilterConditionRef BIGINT NOT NULL
);

DROP TABLE IF EXISTS AMS_FilterCond_FilterCond_Syn;
CREATE TABLE AMS_FilterCond_FilterCond_Syn
(
   iFilterConditionId BIGINT NOT NULL,
   iFilterConditionRef BIGINT NOT NULL
);

DROP TABLE IF EXISTS AMS_FilterCond_Negation;
CREATE TABLE AMS_FilterCond_Negation
(
   iFilterConditionRef BIGINT NOT NULL,
   iNegatedFCRef BIGINT NOT NULL
);

DROP TABLE IF EXISTS AMS_FilterCond_Negation_Syn;
CREATE TABLE AMS_FilterCond_Negation_Syn
(
   iFilterConditionRef BIGINT NOT NULL,
   iNegateDfcRef BIGINT NOT NULL
);

/* Create HSqlDb AMS */

DROP TABLE IF EXISTS AMS_User;
CREATE TABLE AMS_User
(
	iUserId 		BIGINT NOT NULL,
	iGroupRef		BIGINT default -1 NOT NULL, /* FK AMS_Groups.iGroupId				*/
	cUserName 		VARCHAR(128),
	cEmail 			VARCHAR(128),			/* f?r MAIL 						*/
	cMobilePhone		VARCHAR(64),			/* f?r SMS 						*/
	cPhone			VARCHAR(64),			/* f?r VM 						*/
	cStatusCode		VARCHAR(32),			/* Identifz. f?r Remote An- und Abmelden 		*/
	cConfirmCode		VARCHAR(32),			/* Best?tigungscode der Antwort 			*/
	sActive			INTEGER,			/* 0 - Inactive, 1 - Active				*/
	sPreferredAlarmingTypeRR	INTEGER,		/* ReplyRequired: 1 - SMS, 2 - VM, 3 - MAIL 		*/
	/*sPreferredAlarmingType	INTEGER,		Without Reply: 1 - SMS, 2 - VM, 3 - MAIL 		*/
	PRIMARY KEY (iUserId)						
);

DROP TABLE IF EXISTS AMS_UserGroup;
CREATE TABLE AMS_UserGroup 
(
	iUserGroupId		BIGINT NOT NULL,		
	iGroupRef		BIGINT default -1 NOT NULL,	/* FK AMS_Groups.iGroupId				*/
	cUserGroupName		VARCHAR(128),
	sMinGroupMember		INTEGER,			/* Anzahl minimale aktive Benutzer f?r die Alarmbearbeitung */
	iTimeOutSec		BIGINT,			/* Timeout pro Benachrichtigungsversuch 		*/
	sActive			INTEGER,			/* 0 - Inactive, 1 - Active				*/
	PRIMARY KEY (iUserGroupId)						
);

DROP TABLE IF EXISTS AMS_UserGroup_User;
CREATE TABLE AMS_UserGroup_User
(
	iUserGroupRef		BIGINT NOT NULL,
	iUserRef		BIGINT NOT NULL,
	iPos			BIGINT NOT NULL,		/* Benchrichtigungsreihenfolge 				*/
	sActive			INTEGER,			/* Gruppenzugeh?rigkeit aktiv?(0 - Inactive, 1 - Active) */
	cActiveReason		VARCHAR(128),			/* Grund/Ursache der An/Abmeldung			*/
	tTimeChange		BIGINT,			/* Zeitstempel der letzten ?nderung des Datensatzes	*/
	PRIMARY KEY(iUserGroupRef,iUserRef)					
);


DROP TABLE IF EXISTS AMS_FilterConditionType;
CREATE TABLE AMS_FilterConditionType				/* Stringbed., Zeitbed., Array, System 	*/
(
	iFilterConditionTypeID	BIGINT,
	cName			VARCHAR(128),
	cClass			VARCHAR(256),			/* Filterklasse 					*/
	cClassUI		VARCHAR(256),
	PRIMARY KEY(iFilterConditionTypeID) 
);

DROP TABLE IF EXISTS AMS_FilterCondition;
CREATE TABLE AMS_FilterCondition
(
	iFilterConditionID	BIGINT NOT NULL,
	iGroupRef		BIGINT default -1 NOT NULL,	/*FK AMS_Groups.iGroupId				*/
	cName			VARCHAR(128),
	cDesc			VARCHAR(256),
	iFilterConditionTypeRef BIGINT,			/*FK AMS_FilterConditionType.iFilterConditionTypeID 	*/
	PRIMARY KEY(iFilterConditionID)
);

DROP TABLE IF EXISTS AMS_FilterCondition_String;
CREATE TABLE AMS_FilterCondition_String
(
	iFilterConditionRef	BIGINT NOT NULL,
	cKeyValue		VARCHAR(16),
	sOperator		INTEGER,
	cCompValue		VARCHAR(128)
);

DROP TABLE IF EXISTS AMS_FilterCond_ArrStr;
CREATE TABLE AMS_FilterCond_ArrStr
(
	iFilterConditionRef	BIGINT NOT NULL,
	cKeyValue		VARCHAR(16),
	sOperator		INTEGER
);

DROP TABLE IF EXISTS AMS_FilterCond_ArrStrVal;
CREATE TABLE AMS_FilterCond_ArrStrVal
(
	iFilterConditionRef	BIGINT NOT NULL,
	cCompValue		VARCHAR(128)
);


DROP TABLE IF EXISTS AMS_FilterCond_TimeBased;
CREATE TABLE AMS_FilterCond_TimeBased
(
	iFilterConditionRef	BIGINT NOT NULL,
	cStartKeyValue		VARCHAR(16),
	sStartOperator		INTEGER,
	cStartCompValue		VARCHAR(128),
	cConfirmKeyValue	VARCHAR(16),
	sConfirmOperator	INTEGER,
	cConfirmCompValue	VARCHAR(128),
	sTimePeriod			INTEGER,
	sTimeBehavior		INTEGER
);

DROP TABLE IF EXISTS AMS_FilterCondition_PV;
CREATE TABLE AMS_FilterCondition_PV
(
	iFilterConditionRef	BIGINT NOT NULL,
	cPvChannelName		VARCHAR(128),
	sSuggestedPvTypeId	INTEGER,
	sOperatorId			INTEGER,
	cCompValue			VARCHAR(128)
);

DROP TABLE IF EXISTS AMS_FilterCond_Conj_Common;
CREATE TABLE AMS_FilterCond_Conj_Common
(
	iFilterConditionRef			BIGINT NOT NULL,
	iFirstFilterConditionRef	BIGINT NOT NULL,
	iSecondFilterConditionRef   BIGINT NOT NULL,
	iOperand                    INTEGER
);

DROP TABLE IF EXISTS AMS_Filter;
CREATE TABLE AMS_Filter
(
	iFilterID		BIGINT,
	iGroupRef		BIGINT default -1 NOT NULL, /*FK AMS_Groups.iGroupId				*/
	cName			VARCHAR(128),
	cDefaultMessage		VARCHAR(1024),			/* Default Msg mit Platzhalter, wenn in Aktion keine Msg */
	PRIMARY KEY (iFilterID)
);

DROP TABLE IF EXISTS AMS_Filter_FilterCondition;
CREATE TABLE AMS_Filter_FilterCondition
(
	iFilterRef			BIGINT,
	iFilterConditionRef	BIGINT,
	iPos				BIGINT,
	PRIMARY KEY (iFilterRef,iFilterConditionRef)
);

DROP TABLE IF EXISTS AMS_Topic;
CREATE TABLE AMS_Topic
(
	iTopicId 		BIGINT NOT NULL,
	iGroupRef		BIGINT default -1 NOT NULL, -- FK AMS_Groups.iGroupId
	cTopicName 		VARCHAR(128),
	cName	 		VARCHAR(128),
	cDescription	VARCHAR(256),
	PRIMARY KEY (iTopicId)						
);

DROP TABLE IF EXISTS AMS_FilterActionType;			
CREATE TABLE AMS_FilterActionType				/* 1-9 definiert, 100 - freie Topics 			*/
(			
	iFilterActionTypeID	BIGINT NOT NULL,    	/* 0, 1 - SMS, 2 - SMS G, 3 - SMS G R, 4 - VM, 5 - VM G, 6 - VM G R, 7 - MAIL, 8 - MAIL G, 9 - MAIL G R */
	cName			VARCHAR(128),
	iTopicRef		BIGINT,
	PRIMARY KEY(iFilterActionTypeID)
);

DROP TABLE IF EXISTS AMS_FilterAction;
CREATE TABLE AMS_FilterAction
(			
	iFilterActionID		BIGINT NOT NULL,
	iFilterActionTypeRef	BIGINT NOT NULL,		/*FK AMS_FilterActionType.iFilterActionTypeID 		*/
	iReceiverRef		BIGINT,			/* abh?ngig von iFilterActionTypeID User oder UserGroup */
	cMessage		VARCHAR(1024),			/* Aktionsmessage mit Platzhalter der 17 Messagewerte, z.B. %HOST% */
	PRIMARY KEY(iFilterActionID)
);

DROP TABLE IF EXISTS AMS_Filter_FilterAction;
CREATE TABLE AMS_Filter_FilterAction
(
	iFilterRef		BIGINT NOT NULL,
	iFilterActionRef	BIGINT NOT NULL,
	iPos			BIGINT NOT NULL		/* Reihenfolge f?r die GUI, werden parallel ausgef?hrt */
);


/* nur f?r die Oberfl?che => wird nicht repliziert */

DROP TABLE IF EXISTS AMS_Groups;
CREATE TABLE AMS_Groups						/* logische GUI Baumstruktur 				*/
(
	iGroupId		BIGINT NOT NULL,		
	cGroupName		VARCHAR(128),
	sType			INTEGER,			/* 1 - User, 2 - UserGroup, 3 - FilterCond, 4 - Filter, 5 - Topic */
	PRIMARY KEY (iGroupId)
);

DROP TABLE IF EXISTS AMS_DefMessageText;
CREATE TABLE AMS_DefMessageText
(
	iDefMessageTextID	BIGINT	NOT NULL,
	cName			VARCHAR(128) 	NOT NULL,
	cText			VARCHAR(1024)	NOT NULL,
	PRIMARY KEY(iDefMessageTextID)
);


DROP TABLE IF EXISTS AMS_Flag;
CREATE TABLE AMS_Flag
(
	cFlagName		VARCHAR(32)	NOT NULL,
	sFlagValue		INTEGER	NOT NULL,
	PRIMARY KEY(cFlagName)
);

/* Create HSqlDb AMS Sync */

/*
DROP TABLE IF EXISTS AMS_User_Syn;
DROP TABLE IF EXISTS AMS_UserGroup_User_Syn;
DROP TABLE IF EXISTS AMS_FilterConditionType_Syn;
DROP TABLE IF EXISTS AMS_FilterCondition_Syn;
DROP TABLE IF EXISTS AMS_FilterCondition_String_Syn;
DROP TABLE IF EXISTS AMS_FilterCondition_PV_Syn;

DROP TABLE IF EXISTS AMS_FilterCond_ArrStr_Syn;
DROP TABLE IF EXISTS AMS_FilterCond_ArrStrVal_Syn;
DROP TABLE IF EXISTS AMS_FilterCond_TimeBased_Syn;
DROP TABLE IF EXISTS AMS_Filter_Syn;
DROP TABLE IF EXISTS AMS_Filter_FilterCondition_Syn;

DROP TABLE IF EXISTS AMS_Topic_Syn;
DROP TABLE IF EXISTS AMS_FilterActionType_Syn;
DROP TABLE IF EXISTS AMS_FilterAction_Syn;
DROP TABLE IF EXISTS AMS_Filter_FilterAction_Syn;
DROP TABLE IF EXISTS AMS_UserGroup_Syn;
*/


DROP TABLE IF EXISTS AMS_User_Syn;
CREATE TABLE AMS_User_Syn
(
	iUserId 		BIGINT NOT NULL,
	iGroupRef		BIGINT default -1 NOT NULL, /* FK AMS_Groups.iGroupId				*/
	cUserName 		VARCHAR(128),
	cEmail 			VARCHAR(128),			/* f?r MAIL 						*/
	cMobilePhone		VARCHAR(64),			/* f?r SMS 						*/
	cPhone			VARCHAR(64),			/* f?r VM 						*/
	cStatusCode		VARCHAR(32),			/* Identifz. f?r Remote An- und Abmelden 		*/
	cConfirmCode		VARCHAR(32),			/* Best?tigungscode der Antwort 			*/
	sActive			INTEGER,			/* 0 - Inactive, 1 - Active				*/
	sPreferredAlarmingTypeRR	INTEGER,		/* ReplyRequired: 1 - SMS, 2 - VM, 3 - MAIL 		*/
	/*sPreferredAlarmingType	INTEGER,		Without Reply: 1 - SMS, 2 - VM, 3 - MAIL 		*/
	PRIMARY KEY (iUserId)						
);

DROP TABLE IF EXISTS AMS_UserGroup_User_Syn;
CREATE TABLE AMS_UserGroup_User_Syn
(
	iUserGroupRef		BIGINT NOT NULL,
	iUserRef		BIGINT NOT NULL,
	iPos			BIGINT NOT NULL,		/* Benchrichtigungsreihenfolge 				*/
	sActive			INTEGER,			/* Gruppenzugeh?rigkeit aktiv?(0 - Inactive, 1 - Active) */
	cActiveReason		VARCHAR(128),			/* Grund/Ursache der An/Abmeldung			*/
	tTimeChange		BIGINT,			/* Zeitstempel der letzten ?nderung des Datensatzes	*/
	PRIMARY KEY(iUserGroupRef,iUserRef)					
);

DROP TABLE IF EXISTS AMS_FilterConditionType_Syn;
CREATE TABLE AMS_FilterConditionType_Syn			/* Stringbed., Zeitbed., Array, System 	*/
(
	iFilterConditionTypeID	BIGINT,
	cName			VARCHAR(128),
	cClass			VARCHAR(256),			/* Filterklasse 					*/
	cClassUI		VARCHAR(256),
	PRIMARY KEY(iFilterConditionTypeID) 
);

DROP TABLE IF EXISTS AMS_FilterCondition_Syn;
CREATE TABLE AMS_FilterCondition_Syn
(
	iFilterConditionID	BIGINT NOT NULL,
	iGroupRef		BIGINT default -1 NOT NULL,	/*FK AMS_Groups.iGroupId				*/
	cName			VARCHAR(128),
	cDesc			VARCHAR(256),
	iFilterConditionTypeRef BIGINT,			/*FK AMS_FilterConditionType.iFilterConditionTypeID 	*/
	PRIMARY KEY(iFilterConditionID)
);

DROP TABLE IF EXISTS AMS_FilterCondition_String_Syn;
CREATE TABLE AMS_FilterCondition_String_Syn
(
	iFilterConditionRef	BIGINT NOT NULL,
	cKeyValue		VARCHAR(16),
	sOperator		INTEGER,
	cCompValue		VARCHAR(128)
);

DROP TABLE IF EXISTS AMS_FilterCond_ArrStr_Syn;
CREATE TABLE AMS_FilterCond_ArrStr_Syn
(
	iFilterConditionRef	BIGINT NOT NULL,
	cKeyValue		VARCHAR(16),
	sOperator		INTEGER
);

DROP TABLE IF EXISTS AMS_FilterCond_ArrStrVal_Syn;
CREATE TABLE AMS_FilterCond_ArrStrVal_Syn
(
	iFilterConditionRef	BIGINT NOT NULL,
	cCompValue		VARCHAR(128)
);


DROP TABLE IF EXISTS AMS_FilterCond_TimeBased_Syn;
CREATE TABLE AMS_FilterCond_TimeBased_Syn
(
	iFilterConditionRef	BIGINT NOT NULL,
	cStartKeyValue		VARCHAR(16),
	sStartOperator		INTEGER,
	cStartCompValue		VARCHAR(128),
	cConfirmKeyValue	VARCHAR(16),
	sConfirmOperator	INTEGER,
	cConfirmCompValue	VARCHAR(128),
	sTimePeriod		INTEGER,
	sTimeBehavior		INTEGER
);

DROP TABLE IF EXISTS AMS_FilterCondition_PV_Syn;
CREATE TABLE AMS_FilterCondition_PV_Syn
(
	iFilterConditionRef	BIGINT NOT NULL,
	cPvChannelName		VARCHAR(128),
	sSuggestedPvTypeId	INTEGER,
	sOperatorId			INTEGER,
	cCompValue			VARCHAR(128)
);

DROP TABLE IF EXISTS AMS_FilterCond_Conj_Common_Syn;
CREATE TABLE AMS_FilterCond_Conj_Common_Syn
(
	iFilterConditionRef			BIGINT NOT NULL,
	iFirstFilterConditionRef	BIGINT NOT NULL,
	iSecondFilterConditionRef   BIGINT NOT NULL,
	iOperand					INTEGER default 0
);

DROP TABLE IF EXISTS AMS_Filter_Syn;
CREATE TABLE AMS_Filter_Syn
(
	iFilterID		BIGINT,
	iGroupRef		BIGINT default -1 NOT NULL, /*FK AMS_Groups.iGroupId				*/
	cName			VARCHAR(128),
	cDefaultMessage		VARCHAR(1024),			/* Default Msg mit Platzhalter, wenn in Aktion keine Msg */
	PRIMARY KEY (iFilterID)
);

DROP TABLE IF EXISTS AMS_Filter_FilterCondition_Syn;
CREATE TABLE AMS_Filter_FilterCondition_Syn
(
	iFilterRef		BIGINT,
	iFilterConditionRef	BIGINT,
	iPos			BIGINT,
	PRIMARY KEY (iFilterRef,iFilterConditionRef)
);

DROP TABLE IF EXISTS AMS_Topic_Syn;
CREATE TABLE AMS_Topic_Syn
(
	iTopicId 		BIGINT NOT NULL,
	iGroupRef		BIGINT default -1 NOT NULL, -- FK AMS_Groups.iGroupId
	cTopicName 		VARCHAR(128),
	cName	 		VARCHAR(128),
	cDescription	VARCHAR(256),
	PRIMARY KEY (iTopicId)						
);

DROP TABLE IF EXISTS AMS_FilterActionType_Syn;
CREATE TABLE AMS_FilterActionType_Syn				/* 1-9 definiert, 100 - freie Topics 			*/
(			
	iFilterActionTypeID	BIGINT NOT NULL,    	/* 0, 1 - SMS, 2 - SMS G, 3 - SMS G R, 4 - VM, 5 - VM G, 6 - VM G R, 7 - MAIL, 8 - MAIL G, 9 - MAIL G R */
	cName			VARCHAR(128),
	iTopicRef		BIGINT,
	PRIMARY KEY(iFilterActionTypeID)
);

DROP TABLE IF EXISTS AMS_FilterAction_Syn;
CREATE TABLE AMS_FilterAction_Syn
(			
	iFilterActionID		BIGINT NOT NULL,
	iFilterActionTypeRef	BIGINT NOT NULL,		/*FK AMS_FilterActionType.iFilterActionTypeID 		*/
	iReceiverRef		BIGINT,			/* abh?ngig von iFilterActionTypeID User oder UserGroup */
	cMessage		VARCHAR(1024),			/* Aktionsmessage mit Platzhalter der 17 Messagewerte, z.B. %HOST% */
	PRIMARY KEY(iFilterActionID)
);

DROP TABLE IF EXISTS AMS_Filter_FilterAction_Syn;
CREATE TABLE AMS_Filter_FilterAction_Syn
(
	iFilterRef		BIGINT NOT NULL,
	iFilterActionRef	BIGINT NOT NULL,
	iPos			BIGINT NOT NULL		/* Reihenfolge f?r die GUI, werden parallel ausgef?hrt */
);

DROP TABLE IF EXISTS AMS_UserGroup_Syn;
CREATE TABLE AMS_UserGroup_Syn
(
	iUserGroupId		BIGINT NOT NULL,		
	iGroupRef		BIGINT default -1 NOT NULL,	/* FK AMS_Groups.iGroupId				*/
	cUserGroupName		VARCHAR(128),
	sMinGroupMember		INTEGER,			/* Anzahl minimale aktive Benutzer f?r die Alarmbearbeitung */
	iTimeOutSec		BIGINT,			/* Timeout pro Benachrichtigungsversuch 		*/
	sActive			INTEGER,			/* 0 - Inactive, 1 - Active				*/
	PRIMARY KEY (iUserGroupId)						
);

/* Init Oracle AMS */

DELETE FROM AMS_User;
DELETE FROM AMS_UserGroup;
DELETE FROM AMS_UserGroup_User;
DELETE FROM AMS_FilterConditionType;
DELETE FROM AMS_FilterCondition;
DELETE FROM AMS_FilterCondition_String;
DELETE FROM AMS_FilterCondition_PV;
DELETE FROM AMS_FilterCond_ArrStr;
DELETE FROM AMS_FilterCond_ArrStrVal;
DELETE FROM AMS_FilterCond_Conj_Common;
DELETE FROM AMS_FilterCond_FilterCond;
DELETE FROM AMS_FilterCond_Junction;
DELETE FROM AMS_FilterCond_Negation;
DELETE FROM AMS_FilterCond_TimeBased;
DELETE FROM AMS_Filter;
DELETE FROM AMS_Filter_FilterCondition;
DELETE FROM AMS_Topic;
DELETE FROM AMS_FilterActionType;
DELETE FROM AMS_FilterAction;
DELETE FROM AMS_Filter_FilterAction;
DELETE FROM AMS_Groups;
DELETE FROM AMS_DefMessageText;
DELETE FROM AMS_Flag;


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

/* insert into AMS_Topic (iTopicID,cName,cUrl,cPort,cProtocol) values (1,'T_AMS_FREE_100','localhost','1099','rmi'); */
/* insert into AMS_Topic (iTopicID,cName,cUrl,cPort,cProtocol) values (2,'T_AMS_FREE_101','localhost','1099','rmi'); */

insert into AMS_Flag (cFlagName, sFlagValue) values ('BupState', 0);

