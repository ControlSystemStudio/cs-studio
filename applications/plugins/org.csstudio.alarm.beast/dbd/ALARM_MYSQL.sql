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

--
-- TABLE: ALARM.ALARM_TREE 
--

CREATE TABLE ALARM.ALARM_TREE(
    COMPONENT_ID       INT            NOT NULL COMMENT 'Component Identifier: The id for identification of each component.',
    PARENT_CMPNT_ID    INT                     COMMENT 'Parent Component Identifier:The parent id of the component in the configuration hierarchy, null for root of hierarchy.',
    NAME               VARCHAR(80)    NOT NULL COMMENT 'Name: Component name.',
    CONFIG_TIME        TIMESTAMP               COMMENT 'Configuration Time: Time of last configuration update.',
    PRIMARY KEY (COMPONENT_ID)
)ENGINE=MYISAM
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
)ENGINE=MYISAM
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
)ENGINE=MYISAM
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
)ENGINE=MYISAM
COMMENT='Guidance information for the component.'
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
)ENGINE=MYISAM
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
)ENGINE=MYISAM
COMMENT='Severity of an alarm like "invalid", "major alarm" etc.'
;

-- 
-- TABLE: ALARM.STATUS 
--

CREATE TABLE ALARM.STATUS(
    STATUS_ID    INT             NOT NULL COMMENT 'Status Identifier: Unique identifier for the alarm status.',
    NAME         VARCHAR(100)    NOT NULL COMMENT 'Status Name: such as "read error", "disconnected", ...',
    PRIMARY KEY (STATUS_ID)
)ENGINE=MYISAM
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
