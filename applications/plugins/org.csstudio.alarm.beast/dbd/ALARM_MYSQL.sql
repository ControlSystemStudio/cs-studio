# Original version, 2009-03-19: chenx1@ornl.gov
# Updates: Kay Kasemir

# Before using this file to create config tables, you must change hostname, 
# username, password to the real name.
# Under the directory containing this file, use this command to create the database:
# mysql -h hostname -u username -p alarm<ALARM_MySQL.sql

# Take snapshot, restore from snapshot:
#
#  mysqldump -u username -p -l alarm >alarm_snapshot.sql
#  mysql -u username -p alarm <alarm_snapshot.sql


# ----------------------

DROP DATABASE IF EXISTS ALARM;

CREATE DATABASE ALARM;

USE ALARM;


--
-- TABLE: ALARM.ALARM_TREE 
--

CREATE TABLE ALARM.ALARM_TREE(
    COMPONENT_ID       INT            NOT NULL COMMENT 'Component Identifier: The id for identification of each component.',
    PARENT_CMPNT_ID    INT                     COMMENT 'Parent Component Identifier:The parent id of the component in the configuration hierarchy, null for root of hierarchy.',
    NAME               VARCHAR(80)    NOT NULL COMMENT 'Name: Component name.',
    CONFIG_TIME        TIMESTAMP               COMMENT 'Configuration Time: Time of last configuration update.',
    PRIMARY KEY (COMPONENT_ID)
)ENGINE=INNODB
COMMENT=''
;

-- 
-- TABLE: ALARM.COMMAND 
--

CREATE TABLE ALARM.COMMAND(
    COMPONENT_ID     INT              NOT NULL COMMENT 'Component Identifier: The id for identification of each component.',
    TITLE            VARCHAR(100)     NOT NULL COMMENT 'Title: Brief description of the command, which will be displayed in the context menu.',
    COMMAND_ORDER    INT              NOT NULL COMMENT 'Order: The order by which the commands are arranged.',
    DETAIL           VARCHAR(4000)    NOT NULL COMMENT 'Detail: The related command which will be executed when you click on its title.',
    PRIMARY KEY (COMPONENT_ID, TITLE)
)ENGINE=INNODB
COMMENT='commands for the component.'
;

-- 
-- TABLE: ALARM.DISPLAY 
--

CREATE TABLE ALARM.DISPLAY(
    COMPONENT_ID     INT              NOT NULL COMMENT 'Component Identifier: The id for identification of each component.',
    TITLE            VARCHAR(100)     NOT NULL COMMENT 'Title: Brief description of the display, which will be displayed in the context menu.',
    DISPLAY_ORDER    INT              NOT NULL COMMENT 'Order: The order by which the displays are arranged.',
    DETAIL           VARCHAR(4000)    NOT NULL COMMENT 'Detail: The related display which will be launched when you click on its title.',
    PRIMARY KEY (COMPONENT_ID, TITLE)
)ENGINE=INNODB
COMMENT='Displays for the component.'
;

-- 
-- TABLE: ALARM.GUIDANCE 
--

CREATE TABLE ALARM.GUIDANCE(
    COMPONENT_ID      INT              NOT NULL COMMENT 'Component Identifier: The id for identification of each component.',
    TITLE             VARCHAR(100)     NOT NULL COMMENT 'Title: Brief description of the guidance, which will be displayed in the context menu.',
    GUIDANCE_ORDER    INT              NOT NULL COMMENT 'Order: The order by which the guidance are arranged.',
    DETAIL            VARCHAR(4000)    NOT NULL COMMENT 'Detail: Guidance information which is displayed in the guidance dialog.',
    PRIMARY KEY (COMPONENT_ID, TITLE)
)ENGINE=INNODB
COMMENT='Guidance information for the component.'
;

-- 
-- TABLE: ALARM.AUTOMATED_ACTION 
--

CREATE TABLE ALARM.AUTOMATED_ACTION(
    COMPONENT_ID      INT              NOT NULL COMMENT 'Component Identifier: The id for identification of each component.',
    TITLE             VARCHAR(100)     NOT NULL COMMENT 'Title: The action title.',
    AUTO_ACTION_ORDER INT              NOT NULL COMMENT 'Order: The order by which the actions are arranged.',
    DETAIL            VARCHAR(4000)    NOT NULL COMMENT 'Detail: The action value (send email, phone, etc.).',
    DELAY             INT              NOT NULL COMMENT 'Delay: The action delay in seconds.',
    PRIMARY KEY (COMPONENT_ID, TITLE)
)ENGINE=INNODB
COMMENT='Automated actions for the component.'
;

-- 
-- TABLE: ALARM.PV 
--

CREATE TABLE ALARM.PV(
    COMPONENT_ID       INT              NOT NULL           COMMENT 'Component Identifier: The id for identification of each component.',
    DESCR              VARCHAR(100)                        COMMENT 'Description: Description that might be more meaningful than PV name.',
    ENABLED_IND        BOOL         DEFAULT FALSE NOT NULL COMMENT 'Enabled Indicator: Indicates if alarms are enabled for a given PV.',
    ANNUNCIATE_IND     BOOL         DEFAULT FALSE NOT NULL COMMENT 'Annunciate Indicator:  Indicates if alarm should be annunciated.',
    LATCH_IND          BOOL         DEFAULT FALSE NOT NULL COMMENT 'Latch Indicator: Indicates that alarm should be latched for acknowledgement, even if PV recovers.',
    DELAY              INT                                 COMMENT 'Delay: Minimum time in seconds before raising the alarm.',
    FILTER             VARCHAR(4000)                       COMMENT 'Filter: Filter expression, may be used to compute \'enabled\' from expression.',
    DELAY_COUNT        INT                                 COMMENT 'Delay Count: Alarm when PV != OK more often than this count within delay.',
    STATUS_ID          INT                                 COMMENT 'Status Identifier: Alarm system state for the severity identifier.',
    SEVERITY_ID        INT                                 COMMENT 'Severity Identifier: Alarm system severity.',
    CUR_STATUS_ID      INT                                 COMMENT 'Current Status Identifier: Current status of PV.',
    CUR_SEVERITY_ID    INT                                 COMMENT 'Current Severity Identifier: Current severity of PV.',
    PV_VALUE           VARCHAR(100)                        COMMENT 'Process Variable Value: PV value that caused severity/status.',
    ALARM_TIME         TIMESTAMP                           COMMENT 'Alarm Time: The time of the most recent alarm.',
    ACT_GLOBAL_ALARM_IND BOOL DEFAULT FALSE NOT NULL COMMENT 'Indicates if PV has an active global alarm.',
    PRIMARY KEY (COMPONENT_ID)
)ENGINE=INNODB
COMMENT='Process Variable: '
;
-- Add CUR_STATUS_ID, ACT_GLOBAL_ALARM_IND to older setups:
-- ALTER TABLE ALARM.PV  ADD  CUR_STATUS_ID INT  AFTER  SEVERITY_ID;  
-- ALTER TABLE ALARM.PV  ADD  ACT_GLOBAL_ALARM_IND BOOL DEFAULT FALSE NOT NULL AFTER  ALARM_TIME;  

-- 
-- TABLE: ALARM.SEVERITY 
--

CREATE TABLE ALARM.SEVERITY(
    SEVERITY_ID    INT             NOT NULL COMMENT 'Severity Identifier: Unique identifier for the alarm severity.',
    NAME           VARCHAR(100)    NOT NULL COMMENT 'Severity Name: ',
    PRIMARY KEY (SEVERITY_ID)
)ENGINE=INNODB
COMMENT='Severity of an alarm like "invalid", "major alarm" etc.'
;

-- 
-- TABLE: ALARM.STATUS 
--

CREATE TABLE ALARM.STATUS(
    STATUS_ID    INT             NOT NULL COMMENT 'Status Identifier: Unique identifier for the alarm status.',
    NAME         VARCHAR(100)    NOT NULL COMMENT 'Status Name: such as "read error", "disconnected", ...',
    PRIMARY KEY (STATUS_ID)
)ENGINE=INNODB
COMMENT='status of an alarm to provide more detail'
;

-- 
-- INDEX: FK_ALARM_TREE_TO_ALARM_TREE 
--

CREATE INDEX FK_ALARM_TREE_TO_ALARM_TREE ON ALARM.ALARM_TREE(PARENT_CMPNT_ID)
;
-- 
-- INDEX: FK_COMMAND_TO_ALARM_TREE 
--

CREATE INDEX FK_COMMAND_TO_ALARM_TREE ON ALARM.COMMAND(COMPONENT_ID)
;
-- 
-- INDEX: FK_DISPLAY_TO_ALARM_TREE 
--

CREATE INDEX FK_DISPLAY_TO_ALARM_TREE ON ALARM.DISPLAY(COMPONENT_ID)
;
-- 
-- INDEX: FK_GUIDANCE_TO_ALARM_TREE 
--

CREATE INDEX FK_GUIDANCE_TO_ALARM_TREE ON ALARM.GUIDANCE(COMPONENT_ID)
;
-- 
-- INDEX: FK_AUTO_ACTION_TO_ALARM_TREE 
--

CREATE INDEX FK_AUTO_ACTION_TO_ALARM_TREE ON ALARM.AUTOMATED_ACTION(COMPONENT_ID)
;
-- 
-- INDEX: FK_PV_TO_STATUS 
--

CREATE INDEX FK_PV_TO_STATUS ON ALARM.PV(STATUS_ID)
;
-- 
-- INDEX: FK_PV_TO_SEVERITY 
--

CREATE INDEX FK_PV_TO_SEVERITY ON ALARM.PV(SEVERITY_ID)
;
-- 
-- INDEX: FK_PV_TO_ALARM_TREE 
--

CREATE INDEX FK_PV_TO_ALARM_TREE ON ALARM.PV(COMPONENT_ID)
;
-- 
-- TABLE: ALARM.ALARM_TREE 
--

ALTER TABLE ALARM.ALARM_TREE ADD CONSTRAINT FK_ALARM_TREE_TO_ALARM_TREE 
    FOREIGN KEY (PARENT_CMPNT_ID)
    REFERENCES ALARM.ALARM_TREE(COMPONENT_ID)
;


-- 
-- TABLE: ALARM.COMMAND 
--

ALTER TABLE ALARM.COMMAND ADD CONSTRAINT FK_COMMAND_TO_ALARM_TREE 
    FOREIGN KEY (COMPONENT_ID)
    REFERENCES ALARM.ALARM_TREE(COMPONENT_ID)
;


-- 
-- TABLE: ALARM.DISPLAY 
--

ALTER TABLE ALARM.DISPLAY ADD CONSTRAINT FK_DISPLAY_TO_ALARM_TREE 
    FOREIGN KEY (COMPONENT_ID)
    REFERENCES ALARM.ALARM_TREE(COMPONENT_ID)
;


-- 
-- TABLE: ALARM.GUIDANCE 
--

ALTER TABLE ALARM.GUIDANCE ADD CONSTRAINT FK_GUIDANCE_TO_ALARM_TREE 
    FOREIGN KEY (COMPONENT_ID)
    REFERENCES ALARM.ALARM_TREE(COMPONENT_ID)
;


-- 
-- TABLE: ALARM.AUTOMATED_ACTION 
--

ALTER TABLE ALARM.AUTOMATED_ACTION ADD CONSTRAINT FK_AUTO_ACTION_TO_ALARM_TREE 
    FOREIGN KEY (COMPONENT_ID)
    REFERENCES ALARM.ALARM_TREE(COMPONENT_ID)
;


-- 
-- TABLE: ALARM.PV 
--

ALTER TABLE ALARM.PV ADD CONSTRAINT FK_PV_TO_ALARM_TREE 
    FOREIGN KEY (COMPONENT_ID)
    REFERENCES ALARM.ALARM_TREE(COMPONENT_ID)
;

ALTER TABLE ALARM.PV ADD CONSTRAINT FK_CUR_SVRTY_TO_SEVERITY 
    FOREIGN KEY (CUR_SEVERITY_ID)
    REFERENCES ALARM.SEVERITY(SEVERITY_ID)
;

ALTER TABLE ALARM.PV ADD CONSTRAINT FK_PV_TO_SEVERITY 
    FOREIGN KEY (SEVERITY_ID)
    REFERENCES ALARM.SEVERITY(SEVERITY_ID)
;

ALTER TABLE ALARM.PV ADD CONSTRAINT FK_CUR_STS_TO_STATUS 
    FOREIGN KEY (CUR_STATUS_ID)
    REFERENCES ALARM.STATUS(STATUS_ID)
;

ALTER TABLE ALARM.PV ADD CONSTRAINT FK_PV_TO_STATUS 
    FOREIGN KEY (STATUS_ID)
    REFERENCES ALARM.STATUS(STATUS_ID)
;


--
-- Example data.
-- Skip if there is no need for an example
--

-- This entry with PARENT_CMPNT_ID = NULL
-- defines an alarm tree 'root'
INSERT INTO ALARM.ALARM_TREE VALUES (1, NULL, 'Annunciator', now());

-- Following entries are below that root
INSERT INTO ALARM.ALARM_TREE VALUES (2, 1, 'Area', now());
INSERT INTO ALARM.ALARM_TREE VALUES (3, 2, 'System', now());
INSERT INTO ALARM.ALARM_TREE VALUES (4, 3, 'PV1', now());
INSERT INTO ALARM.ALARM_TREE VALUES (5, 3, 'PV2', now());

-- ALARM_TREE entries 'PV1', 'PV2' become PVs because of associated data in PV table:
INSERT INTO ALARM.PV(COMPONENT_ID, DESCR, ENABLED_IND, ANNUNCIATE_IND, LATCH_IND, ACT_GLOBAL_ALARM_IND) VALUES (4, 'Demo 1', true, true, true, false);
INSERT INTO ALARM.PV(COMPONENT_ID, DESCR, ENABLED_IND, ANNUNCIATE_IND, LATCH_IND, ACT_GLOBAL_ALARM_IND) VALUES (5, 'Demo 2', true, true, true, false);

-- Guidance, commands, .. can be associated with Areas, systems, PVs
INSERT INTO ALARM.GUIDANCE(COMPONENT_ID, GUIDANCE_ORDER, TITLE, DETAIL) VALUES (3, 1, 'System Info', 'This is info for the system and PVs below it'); 
    
INSERT INTO ALARM.GUIDANCE(COMPONENT_ID, GUIDANCE_ORDER, TITLE, DETAIL) VALUES (4, 1, 'Info 1', 'Do something'); 
INSERT INTO ALARM.GUIDANCE(COMPONENT_ID, GUIDANCE_ORDER, TITLE, DETAIL) VALUES (4, 2, 'Info 2', 'Do something else'); 

INSERT INTO ALARM.GUIDANCE(COMPONENT_ID, GUIDANCE_ORDER, TITLE, DETAIL) VALUES (5, 1, 'Info 1', 'Do something'); 
INSERT INTO ALARM.GUIDANCE(COMPONENT_ID, GUIDANCE_ORDER, TITLE, DETAIL) VALUES (5, 2, 'Info 2', 'Do something else'); 
