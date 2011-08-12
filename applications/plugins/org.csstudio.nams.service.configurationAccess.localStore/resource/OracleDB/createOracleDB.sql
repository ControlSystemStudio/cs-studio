
/* Alte Tabellennamen - NICHT MEHR VERWENDEN
drop table AMSFilterNegationCond4Filter;
--create table AMSFilterNegationCond4Filter (
--    iFilterConditionRef			NUMBER(11) NOT NULL,
--    iNegatedFCRef               NUMBER(11) NOT NULL
--);

--drop table AMSFilterCondConj4FilterCommon;
--create table AMSFilterCondConj4FilterCommon (
--   iFilterConditionRef			NUMBER(11) NOT NULL,
--   Operator                     VARCHAR2(3) NOT NULL, 
--   CONSTRAINT AMSFilterCondConj4FilterCommon CHECK (Operator IN ('AND', 'OR'))
--);

drop table AMSFilterCondConj4FilterFCJoin;
create table AMSFilterCondConj4FilterFCJoin (
   iFilterConditionID           NUMBER(11) NOT NULL,
   iFilterConditionRef			NUMBER(11) NOT NULL
 );
Alte Tabellennamen - NICHT MEHR VERWENDEN -- ENDE*/

--

-- NAMS Oracle --

DROP TABLE AMS_FilterCond_Junction;
CREATE TABLE AMS_FilterCond_Junction
(
   iFilterConditionRef NUMBER(11) NOT NULL,
   Operator VARCHAR2(3) NOT NULL
);

DROP TABLE AMS_FilterCond_Junction_Syn;
CREATE TABLE AMS_FilterCond_Junction_Syn
(
   iFilterConditionRef NUMBER(11) NOT NULL,
   Operator VARCHAR2(3) NOT NULL
);

DROP TABLE AMS_FilterCond_FilterCond;
CREATE TABLE AMS_FilterCond_FilterCond
(
   iFilterConditionId NUMBER(11) NOT NULL,
   iFilterConditionRef NUMBER(11) NOT NULL
);

DROP TABLE AMS_FilterCond_FilterCond_Syn;
CREATE TABLE AMS_FilterCond_FilterCond_Syn
(
   iFilterConditionId NUMBER(11) NOT NULL,
   iFilterConditionRef NUMBER(11) NOT NULL
);

DROP TABLE AMS_FilterCond_Negation;
CREATE TABLE AMS_FilterCond_Negation
(
   iFilterConditionRef NUMBER(11) NOT NULL,
   iNegatedFCRef NUMBER(11) NOT NULL
);

DROP TABLE AMS_FilterCond_Negation_Syn;
CREATE TABLE AMS_FilterCond_Negation_Syn
(
   iFilterConditionRef NUMBER(11) NOT NULL,
   iNegateDfcRef NUMBER(11) NOT NULL
);

-- Create Oracle AMS --

drop table AMS_User;
create table AMS_User
(
	iUserId 		NUMBER(11) NOT NULL,
	iGroupRef		NUMBER(11) default -1 NOT NULL, /* FK AMS_Groups.iGroupId */
	cUserName 		VARCHAR2(128),
	cEmail 			VARCHAR2(128),           /* fuer MAIL */
	cMobilePhone	VARCHAR2(64),            /* fuer SMS 						*/
	cPhone			VARCHAR2(64),			 /* fuer VM 						*/
	cStatusCode		VARCHAR2(32),			 /* Identifz. fuer Remote An- und Abmelden 		*/
	cConfirmCode	VARCHAR2(32),            /* Bestaetigungscode der Antwort 			*/
	sActive			NUMBER(6),               /* 0 - Inactive, 1 - Active				*/
	sPreferredAlarmingTypeRR	NUMBER(6),   /* ReplyRequired: 1 - SMS, 2 - VM, 3 - MAIL 		*/
	/* sPreferredAlarmingType	NUMBER(6),		Without Reply: 1 - SMS, 2 - VM, 3 - MAIL 		*/
	PRIMARY KEY (iUserId)						
);

drop table AMS_UserGroup;
create table AMS_UserGroup 
(
	iUserGroupId		NUMBER(11) NOT NULL,		
	iGroupRef		NUMBER(11) default -1 NOT NULL,	/* FK AMS_Groups.iGroupId				*/
	cUserGroupName		VARCHAR2(128),
	sMinGroupMember		NUMBER(6),			/* Anzahl minimale aktive Benutzer fuer die Alarmbearbeitung */
	iTimeOutSec		NUMBER(11),			/* Timeout pro Benachrichtigungsversuch 		*/
	sActive			NUMBER(6),			/* 0 - Inactive, 1 - Active				*/
	PRIMARY KEY (iUserGroupId)						
);

drop table AMS_UserGroup_User;
create table AMS_UserGroup_User
(
	iUserGroupRef		NUMBER(11) NOT NULL,
	iUserRef		NUMBER(11) NOT NULL,
	iPos			NUMBER(11) NOT NULL,		/* Benchrichtigungsreihenfolge 				*/
	sActive			NUMBER(6),			/* Gruppenzugehoerigkeit aktiv?(0 - Inactive, 1 - Active) */
	cActiveReason		VARCHAR2(128),			/* Grund/Ursache der An/Abmeldung			*/
	tTimeChange		NUMBER(14),			/* Zeitstempel der letzten Aenderung des Datensatzes	*/
	PRIMARY KEY(iUserGroupRef,iUserRef)					
);


drop table AMS_FilterConditionType;
create table AMS_FilterConditionType				/* Stringbed., Zeitbed., Array, System 	*/
(
	iFilterConditionTypeID	NUMBER(11),
	cName			VARCHAR2(128),
	cClass			VARCHAR2(256),			/* Filterklasse 					*/
	cClassUI		VARCHAR2(256),
	PRIMARY KEY(iFilterConditionTypeID) 
);

drop table AMS_FilterCondition;
create table AMS_FilterCondition
(
	iFilterConditionID	NUMBER(11) NOT NULL,
	iGroupRef		NUMBER(11) default -1 NOT NULL,	/*FK AMS_Groups.iGroupId				*/
	cName			VARCHAR2(128),
	cDesc			VARCHAR2(256),
	iFilterConditionTypeRef NUMBER(11),			/*FK AMS_FilterConditionType.iFilterConditionTypeID 	*/
	PRIMARY KEY(iFilterConditionID)
);

drop table AMS_FilterCondition_String;
create table AMS_FilterCondition_String
(
	iFilterConditionRef	NUMBER(11) NOT NULL,
	cKeyValue		VARCHAR2(16),
	sOperator		NUMBER(6),
	cCompValue		VARCHAR2(128)
);

drop table AMS_FilterCond_ArrStr;
create table AMS_FilterCond_ArrStr
(
	iFilterConditionRef	NUMBER(11) NOT NULL,
	cKeyValue		VARCHAR2(16),
	sOperator		NUMBER(6)
);

drop table AMS_FilterCond_ArrStrVal;
create table AMS_FilterCond_ArrStrVal
(
	iFilterConditionRef	NUMBER(11) NOT NULL,
	cCompValue		VARCHAR2(128)
);


drop table AMS_FilterCond_TimeBased;
create table AMS_FilterCond_TimeBased
(
	iFilterConditionRef	NUMBER(11) NOT NULL,
	cStartKeyValue		VARCHAR2(16),
	sStartOperator		NUMBER(6),
	cStartCompValue		VARCHAR2(128),
	cConfirmKeyValue	VARCHAR2(16),
	sConfirmOperator	NUMBER(6),
	cConfirmCompValue	VARCHAR2(128),
	sTimePeriod			NUMBER(6),
	sTimeBehavior		NUMBER(6)
);

drop table AMS_FilterCondition_PV;
create table AMS_FilterCondition_PV
(
	iFilterConditionRef	NUMBER(11) NOT NULL,
	cPvChannelName		VARCHAR2(128),
	sSuggestedPvTypeId	NUMBER(6),
	sOperatorId			NUMBER(6),
	cCompValue			VARCHAR2(128)
);

drop table AMS_FilterCond_Conj_Common;
create table AMS_FilterCond_Conj_Common
(
	iFilterConditionRef			NUMBER(11) NOT NULL,
	iFirstFilterConditionRef	NUMBER(11) NOT NULL,
	iSecondFilterConditionRef   NUMBER(11) NOT NULL,
	iOperand                    NUMBER(6,0)
);

drop table AMS_Filter;
create table AMS_Filter
(
	iFilterID		NUMBER(11),
	iGroupRef		NUMBER(11) default -1 NOT NULL, /*FK AMS_Groups.iGroupId				*/
	cName			VARCHAR2(128),
	cDefaultMessage		VARCHAR2(1024),			/* Default Msg mit Platzhalter, wenn in Aktion keine Msg */
	PRIMARY KEY (iFilterID)
);

drop table AMS_Filter_FilterCondition;
create table AMS_Filter_FilterCondition
(
	iFilterRef			NUMBER(11),
	iFilterConditionRef	NUMBER(11),
	iPos				NUMBER(11),
	PRIMARY KEY (iFilterRef,iFilterConditionRef)
);

drop table AMS_Topic;
create table AMS_Topic
(
	iTopicId 		NUMBER(11) NOT NULL,
	iGroupRef		NUMBER(11) default -1 NOT NULL, -- FK AMS_Groups.iGroupId
	cTopicName 		VARCHAR2(128),
	cName	 		VARCHAR2(128),
	cDescription	VARCHAR2(256),
	PRIMARY KEY (iTopicId)						
);

/*
drop table AMS_Topic;
create table AMS_Topic
(			
	iTopicID		NUMBER(11) NOT NULL,
	cName			VARCHAR2(128),
	cUrl			VARCHAR2(256),
	cPort			VARCHAR2(16),			-- 0 - Standard Port
	cProtocol		VARCHAR2(16),			-- tcp, rmi
	PRIMARY KEY(iTopicID)
);
*/

drop table AMS_FilterActionType;			
create table AMS_FilterActionType				/* 1-9 definiert, 100 - freie Topics 			*/
(			
	iFilterActionTypeID	NUMBER(11) NOT NULL,    	/* 0, 1 - SMS, 2 - SMS G, 3 - SMS G R, 4 - VM, 5 - VM G, 6 - VM G R, 7 - MAIL, 8 - MAIL G, 9 - MAIL G R */
	cName			VARCHAR2(128),
	iTopicRef		NUMBER(11),
	PRIMARY KEY(iFilterActionTypeID)
);

drop table AMS_FilterAction;
create table AMS_FilterAction
(			
	iFilterActionID		NUMBER(11) NOT NULL,
	iFilterActionTypeRef	NUMBER(11) NOT NULL,		/*FK AMS_FilterActionType.iFilterActionTypeID 		*/
	iReceiverRef		NUMBER(11),			/* abhaengig von iFilterActionTypeID User oder UserGroup */
	cMessage		VARCHAR2(1024),			/* Aktionsmessage mit Platzhalter der 17 Messagewerte, z.B. %HOST% */
	PRIMARY KEY(iFilterActionID)
);

drop table AMS_Filter_FilterAction;
create table AMS_Filter_FilterAction
(
	iFilterRef		NUMBER(11) NOT NULL,
	iFilterActionRef	NUMBER(11) NOT NULL,
	iPos			NUMBER(11) NOT NULL		/* Reihenfolge fuer die GUI, werden parallel ausgefuehrt */
);


/* nur fuer die Oberflaeche => wird nicht repliziert */

drop table AMS_Groups;
create table AMS_Groups						/* logische GUI Baumstruktur 				*/
(
	iGroupId		NUMBER(11) NOT NULL,		
	cGroupName		VARCHAR2(128),
	sType			NUMBER(6),			/* 1 - User, 2 - UserGroup, 3 - FilterCond, 4 - Filter, 5 - Topic */
	PRIMARY KEY (iGroupId)
);

drop table AMS_DefMessageText;
create table AMS_DefMessageText
(
	iDefMessageTextID	NUMBER(11)	NOT NULL,
	cName			VARCHAR2(128) 	NOT NULL,
	cText			VARCHAR2(1024)	NOT NULL,
	PRIMARY KEY(iDefMessageTextID)
);


drop table AMS_Flag;
create table AMS_Flag
(
	cFlagName		VARCHAR2(32)	NOT NULL,
	sFlagValue		NUMBER(6)	NOT NULL,
	PRIMARY KEY(cFlagName)
);

-- Create Oracle AMS Sync --


/*
drop table AMS_User_Syn;
drop table AMS_UserGroup_User_Syn;
drop table AMS_FilterConditionType_Syn;
drop table AMS_FilterCondition_Syn;
drop table AMS_FilterCondition_String_Syn;
drop table AMS_FilterCondition_PV_Syn;

drop table AMS_FilterCond_ArrStr_Syn;
drop table AMS_FilterCond_ArrStrVal_Syn;
drop table AMS_FilterCond_TimeBased_Syn;
drop table AMS_Filter_Syn;
drop table AMS_Filter_FilterCondition_Syn;

drop table AMS_Topic_Syn;
drop table AMS_FilterActionType_Syn;
drop table AMS_FilterAction_Syn;
drop table AMS_Filter_FilterAction_Syn;
drop table AMS_UserGroup_Syn;
*/


drop table AMS_User_Syn;
create table AMS_User_Syn
(
	iUserId 		NUMBER(11) NOT NULL,
	iGroupRef		NUMBER(11) default -1 NOT NULL, /* FK AMS_Groups.iGroupId				*/
	cUserName 		VARCHAR2(128),
	cEmail 			VARCHAR2(128),			/* fuer MAIL 						*/
	cMobilePhone		VARCHAR2(64),			/* fuer SMS 						*/
	cPhone			VARCHAR2(64),			/* fuer VM 						*/
	cStatusCode		VARCHAR2(32),			/* Identifz. fuer Remote An- und Abmelden 		*/
	cConfirmCode		VARCHAR2(32),			/* Bestaetigungscode der Antwort 			*/
	sActive			NUMBER(6),			/* 0 - Inactive, 1 - Active				*/
	sPreferredAlarmingTypeRR	NUMBER(6),		/* ReplyRequired: 1 - SMS, 2 - VM, 3 - MAIL 		*/
	/*sPreferredAlarmingType	NUMBER(6),		Without Reply: 1 - SMS, 2 - VM, 3 - MAIL 		*/
	PRIMARY KEY (iUserId)						
);

drop table AMS_UserGroup_User_Syn;
create table AMS_UserGroup_User_Syn
(
	iUserGroupRef		NUMBER(11) NOT NULL,
	iUserRef		NUMBER(11) NOT NULL,
	iPos			NUMBER(11) NOT NULL,		/* Benchrichtigungsreihenfolge 				*/
	sActive			NUMBER(6),			/* Gruppenzugehörigkeit aktiv?(0 - Inactive, 1 - Active) */
	cActiveReason		VARCHAR2(128),			/* Grund/Ursache der An/Abmeldung			*/
	tTimeChange		NUMBER(14),			/* Zeitstempel der letzten Aenderung des Datensatzes	*/
	PRIMARY KEY(iUserGroupRef,iUserRef)					
);

drop table AMS_FilterConditionType_Syn;
create table AMS_FilterConditionType_Syn			/* Stringbed., Zeitbed., Array, System 	*/
(
	iFilterConditionTypeID	NUMBER(11),
	cName			VARCHAR2(128),
	cClass			VARCHAR2(256),			/* Filterklasse 					*/
	cClassUI		VARCHAR2(256),
	PRIMARY KEY(iFilterConditionTypeID) 
);

drop table AMS_FilterCondition_Syn;
create table AMS_FilterCondition_Syn
(
	iFilterConditionID	NUMBER(11) NOT NULL,
	iGroupRef		NUMBER(11) default -1 NOT NULL,	/*FK AMS_Groups.iGroupId				*/
	cName			VARCHAR2(128),
	cDesc			VARCHAR2(256),
	iFilterConditionTypeRef NUMBER(11),			/*FK AMS_FilterConditionType.iFilterConditionTypeID 	*/
	PRIMARY KEY(iFilterConditionID)
);

drop table AMS_FilterCondition_String_Syn;
create table AMS_FilterCondition_String_Syn
(
	iFilterConditionRef	NUMBER(11) NOT NULL,
	cKeyValue		VARCHAR2(16),
	sOperator		NUMBER(6),
	cCompValue		VARCHAR2(128)
);

drop table AMS_FilterCond_ArrStr_Syn;
create table AMS_FilterCond_ArrStr_Syn
(
	iFilterConditionRef	NUMBER(11) NOT NULL,
	cKeyValue		VARCHAR2(16),
	sOperator		NUMBER(6)
);

drop table AMS_FilterCond_ArrStrVal_Syn;
create table AMS_FilterCond_ArrStrVal_Syn
(
	iFilterConditionRef	NUMBER(11) NOT NULL,
	cCompValue		VARCHAR2(128)
);


drop table AMS_FilterCond_TimeBased_Syn;
create table AMS_FilterCond_TimeBased_Syn
(
	iFilterConditionRef	NUMBER(11) NOT NULL,
	cStartKeyValue		VARCHAR2(16),
	sStartOperator		NUMBER(6),
	cStartCompValue		VARCHAR2(128),
	cConfirmKeyValue	VARCHAR2(16),
	sConfirmOperator	NUMBER(6),
	cConfirmCompValue	VARCHAR2(128),
	sTimePeriod		NUMBER(6),
	sTimeBehavior		NUMBER(6)
);

drop table AMS_FilterCondition_PV_Syn;
create table AMS_FilterCondition_PV_Syn
(
	iFilterConditionRef	NUMBER(11) NOT NULL,
	cPvChannelName		VARCHAR2(128),
	sSuggestedPvTypeId	NUMBER(6),
	sOperatorId			NUMBER(6),
	cCompValue			VARCHAR2(128)
);

drop table AMS_FilterCond_Conj_Common_Syn;
create table AMS_FilterCond_Conj_Common_Syn
(
	iFilterConditionRef			NUMBER(11) NOT NULL,
	iFirstFilterConditionRef	NUMBER(11) NOT NULL,
	iSecondFilterConditionRef   NUMBER(11) NOT NULL,
	iOperand					NUMBER(6,0) default 0
);

drop table AMS_Filter_Syn;
create table AMS_Filter_Syn
(
	iFilterID		NUMBER(11),
	iGroupRef		NUMBER(11) default -1 NOT NULL, /*FK AMS_Groups.iGroupId				*/
	cName			VARCHAR2(128),
	cDefaultMessage		VARCHAR2(1024),			/* Default Msg mit Platzhalter, wenn in Aktion keine Msg */
	PRIMARY KEY (iFilterID)
);

drop table AMS_Filter_FilterCondition_Syn;
create table AMS_Filter_FilterCondition_Syn
(
	iFilterRef		NUMBER(11),
	iFilterConditionRef	NUMBER(11),
	iPos			NUMBER(11),
	PRIMARY KEY (iFilterRef,iFilterConditionRef)
);

drop table AMS_Topic_Syn;
create table AMS_Topic_Syn
(
	iTopicId 		NUMBER(11) NOT NULL,
	iGroupRef		NUMBER(11) default -1 NOT NULL, -- FK AMS_Groups.iGroupId
	cTopicName 		VARCHAR2(128),
	cName	 		VARCHAR2(128),
	cDescription	VARCHAR2(256),
	PRIMARY KEY (iTopicId)						
);

/*
drop table AMS_Topic_Syn;
create table AMS_Topic_Syn
(			
	iTopicID		NUMBER(11) NOT NULL,
	cName			VARCHAR2(128),
	cUrl			VARCHAR2(256),
	cPort			VARCHAR2(16),			-- 0 - Standard Port
	cProtocol		VARCHAR2(16),			-- tcp, rmi
	PRIMARY KEY(iTopicID)
);
*/

drop table AMS_FilterActionType_Syn;
create table AMS_FilterActionType_Syn				/* 1-9 definiert, 100 - freie Topics 			*/
(			
	iFilterActionTypeID	NUMBER(11) NOT NULL,    	/* 0, 1 - SMS, 2 - SMS G, 3 - SMS G R, 4 - VM, 5 - VM G, 6 - VM G R, 7 - MAIL, 8 - MAIL G, 9 - MAIL G R */
	cName			VARCHAR2(128),
	iTopicRef		NUMBER(11),
	PRIMARY KEY(iFilterActionTypeID)
);

drop table AMS_FilterAction_Syn;
create table AMS_FilterAction_Syn
(			
	iFilterActionID		NUMBER(11) NOT NULL,
	iFilterActionTypeRef	NUMBER(11) NOT NULL,		/*FK AMS_FilterActionType.iFilterActionTypeID 		*/
	iReceiverRef		NUMBER(11),			/* abhaengig von iFilterActionTypeID User oder UserGroup */
	cMessage		VARCHAR2(1024),			/* Aktionsmessage mit Platzhalter der 17 Messagewerte, z.B. %HOST% */
	PRIMARY KEY(iFilterActionID)
);

drop table AMS_Filter_FilterAction_Syn;
create table AMS_Filter_FilterAction_Syn
(
	iFilterRef		NUMBER(11) NOT NULL,
	iFilterActionRef	NUMBER(11) NOT NULL,
	iPos			NUMBER(11) NOT NULL		/* Reihenfolge fuer die GUI, werden parallel ausgefuehrt */
);

drop table AMS_UserGroup_Syn;
create table AMS_UserGroup_Syn
(
	iUserGroupId		NUMBER(11) NOT NULL,		
	iGroupRef		NUMBER(11) default -1 NOT NULL,	/* FK AMS_Groups.iGroupId				*/
	cUserGroupName		VARCHAR2(128),
	sMinGroupMember		NUMBER(6),			/* Anzahl minimale aktive Benutzer fuer die Alarmbearbeitung */
	iTimeOutSec		NUMBER(11),			/* Timeout pro Benachrichtigungsversuch 		*/
	sActive			NUMBER(6),			/* 0 - Inactive, 1 - Active				*/
	PRIMARY KEY (iUserGroupId)						
);

-- Init Oracle AMS --

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

-- insert into AMS_Topic (iTopicID,cName,cUrl,cPort,cProtocol) values (1,'T_AMS_FREE_100','localhost','1099','rmi');
-- insert into AMS_Topic (iTopicID,cName,cUrl,cPort,cProtocol) values (2,'T_AMS_FREE_101','localhost','1099','rmi');

insert into AMS_Flag (cFlagName, sFlagValue) values ('BupState', 0);

commit;
