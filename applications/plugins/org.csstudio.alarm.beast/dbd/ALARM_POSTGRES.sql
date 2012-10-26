/*
 * PostGres SQL DDL for the BEAST Alarm Server Tables.
 *
 * Lana Abadie, based on original by chenx1@ornl.gov.
 *
 * Before using this file to create config tables, you must change hostname, 
 * username, password to the real name.
 * Under the directory containing this file, use this command to create the database:
 * su postgres -c "psql -q -f ALARM_POSTGRES.sql"
 */

/*
-- Alarm user who can write to the tables
CREATE USER alarm;
ALTER USER alarm WITH PASSWORD '$alarm';

-- Read-only user for reports
CREATE USER report WITH PASSWORD '$report';

SELECT * FROM pg_user;

-- The following would have to be executed _after_ creating the tables:
GRANT SELECT, INSERT, UPDATE, DELETE
  ON alarm_tree, command, display, guidance, pv, severity, status TO alarm;

GRANT SELECT
  ON alarm_tree, command, display, guidance, pv, severity, status  TO report;

*/

set client_min_messages='warning';

-- 
-- TABLE: ALARM.ALARM_TREE 
--
DROP DATABASE IF EXISTS ALARM;

CREATE DATABASE ALARM;

\connect alarm;

set client_min_messages='warning';


CREATE TABLE ALARM_TREE(
    COMPONENT_ID       INT           NOT NULL,
    PARENT_CMPNT_ID    INT                  ,
    NAME               VARCHAR(80)    NOT NULL,
    CONFIG_TIME        TIMESTAMP,
    PRIMARY KEY (COMPONENT_ID)
);

COMMENT ON COLUMN ALARM_TREE.COMPONENT_ID IS 'Component Identifier: The id for identification of each component.'
;
COMMENT ON COLUMN ALARM_TREE.PARENT_CMPNT_ID IS 'Parent Component Identifier:The parent id of the component in the configuration hierarchy, null for root of hierarchy.'
;
COMMENT ON COLUMN ALARM_TREE.NAME IS 'Name: Component name.'
;
COMMENT ON COLUMN ALARM_TREE.CONFIG_TIME IS 'Configuration Time: Time of last configuration update. '
;
-- 
-- TABLE: ALARM.COMMAND 
--

CREATE TABLE COMMAND(
    COMPONENT_ID     INT             NOT NULL,
    TITLE            VARCHAR(100)     NOT NULL ,
    COMMAND_ORDER    INT              NOT NULL ,
    DETAIL           VARCHAR(4000)    NOT NULL ,
    PRIMARY KEY (COMPONENT_ID, TITLE)
);


COMMENT on TABLE COMMAND IS  'command for the component.'
;

COMMENT ON COLUMN COMMAND.COMPONENT_ID IS 'Component Identifier: The id for identification of each component.'
;

COMMENT ON COLUMN COMMAND.TITLE IS 'Title: Brief description of the command, which will be displayed in the context menu.'
;

COMMENT ON COLUMN COMMAND.COMMAND_ORDER IS 'Order: The order by which the commands are arranged.'
;

COMMENT ON COLUMN COMMAND.DETAIL IS 'Detail: The related command which will be executed when you click on its title.'
;






-- 
-- TABLE: ALARM.DISPLAY 
--

CREATE TABLE DISPLAY(
    COMPONENT_ID     INT              NOT NULL ,
    TITLE            VARCHAR(100)     NOT NULL ,
    DISPLAY_ORDER    INT              NOT NULL ,
    DETAIL           VARCHAR(4000)    NOT NULL ,
    PRIMARY KEY (COMPONENT_ID, TITLE)
);

COMMENT ON TABLE DISPLAY IS  'Displays for the component.'
; 

COMMENT ON COLUMN DISPLAY.COMPONENT_ID  IS 'Component Identifier: The id for identification of each component.'
;

COMMENT ON COLUMN DISPLAY.TITLE  IS 'Title: Brief description of the display, which will be displayed in the context menu.'
;

COMMENT ON COLUMN DISPLAY.DISPLAY_ORDER  IS 'Order: The order by which the displays are arranged.'
;

COMMENT ON COLUMN DISPLAY.DETAIL   IS 'Detail: The related display which will be launched when you click on its title.'
;

-- 
-- TABLE: ALARM.GUIDANCE 
--

CREATE TABLE GUIDANCE(
    COMPONENT_ID      INT              NOT NULL ,
    TITLE             VARCHAR(100)     NOT NULL ,
    GUIDANCE_ORDER    INT              NOT NULL ,
    DETAIL            VARCHAR(4000)    NOT NULL ,
    PRIMARY KEY (COMPONENT_ID, TITLE)
);

COMMENT ON TABLE GUIDANCE IS  'Guidance information for the component.'
; 

COMMENT ON COLUMN GUIDANCE.COMPONENT_ID  IS 'Component Identifier: The id for identification of each component.'
;

COMMENT ON COLUMN GUIDANCE.TITLE  IS 'Title: Brief description of the guidance, which will be displayed in the context menu.'
;

COMMENT ON COLUMN GUIDANCE.GUIDANCE_ORDER  IS 'Order: The order by which the guidance are arranged.'
;

COMMENT ON COLUMN GUIDANCE.DETAIL  IS 'Detail: Guidance information which is displayed in the guidance dialog.'
;

-- 
-- TABLE: ALARM.AUTOMATED_ACTION
--

CREATE TABLE AUTOMATED_ACTION (
    COMPONENT_ID      INT              NOT NULL ,
    TITLE             VARCHAR(100)     NOT NULL ,
    AUTO_ACTION_ORDER INT              NOT NULL ,
    DETAIL            VARCHAR(4000)    NOT NULL ,
    DELAY             INT              NOT NULL ,
    PRIMARY KEY (COMPONENT_ID, TITLE)
);


COMMENT ON TABLE AUTOMATED_ACTION IS 'Automated actions for the component.'
;

COMMENT ON COLUMN AUTOMATED_ACTION.COMPONENT_ID IS 'Component Identifier: The id for identification of each component.'
;

COMMENT ON COLUMN AUTOMATED_ACTION.TITLE IS 'Title: The action title.'
;

COMMENT ON COLUMN AUTOMATED_ACTION.AUTO_ACTION_ORDER IS 'Order: The order by which the actions are arranged.'
;

COMMENT ON COLUMN AUTOMATED_ACTION.DETAIL IS 'Detail: The action value (send email, phone, etc.).'
;

COMMENT ON COLUMN AUTOMATED_ACTION.DELAY IS 'Delay: The action delay in seconds.'
;

-- 
-- TABLE: ALARM.PV 
--

CREATE TABLE PV(
    COMPONENT_ID       INT              NOT NULL           ,
    DESCR              VARCHAR(100)     ,
    ENABLED_IND        boolean    DEFAULT false NOT NULL ,
    ANNUNCIATE_IND     boolean    DEFAULT false NOT NULL ,
    LATCH_IND          boolean    DEFAULT false NOT NULL,
    DELAY              INT    ,
    FILTER             VARCHAR(4000)                       ,
    DELAY_COUNT        INT                                 ,
    STATUS_ID          INT                                 ,
    SEVERITY_ID        INT                                 ,
    CUR_STATUS_ID      INT                                 ,
    CUR_SEVERITY_ID    INT                                 ,
    PV_VALUE           VARCHAR(100)                        ,
    ALARM_TIME         TIMESTAMP                           ,
    ACT_GLOBAL_ALARM_IND  boolean    DEFAULT false NOT NULL,
    PRIMARY KEY (COMPONENT_ID)
);


COMMENT ON TABLE PV IS  'Process Variable:'
; 

COMMENT ON COLUMN PV.COMPONENT_ID  IS 'Component Identifier: The id for identification of each component.'
;

COMMENT ON COLUMN PV.DESCR IS 'Description: Description that might be more meaningful than PV name.'
;

COMMENT ON COLUMN PV.ENABLED_IND  IS 'Enabled Indicator: Indicates if alarms are enabled for a given PV.'
;

COMMENT ON COLUMN PV.ANNUNCIATE_IND  IS 'Annunciate Indicator:  Indicates if alarm should be annunciated.'
;

COMMENT ON COLUMN PV.LATCH_IND  IS 'Latch Indicator: Indicates that alarm should be latched for acknowledgement, even if PV recovers.'
;

COMMENT ON COLUMN PV.DELAY  IS 'Delay: Minimum time in seconds before raising the alarm.'
;

COMMENT ON COLUMN PV.FILTER  IS 'Filter: Filter expression, may be used to compute ''enabled'' from expression.'
;

COMMENT ON COLUMN PV.DELAY_COUNT  IS  'Delay Count: Alarm when PV != OK more often than this count within delay.'
;

COMMENT ON COLUMN PV.STATUS_ID   IS  'Status Identifier: Alarm system state for the severity identifier.'
;

COMMENT ON COLUMN PV.SEVERITY_ID  IS  'Severity Identifier: Alarm system severity.'
;

COMMENT ON COLUMN PV.CUR_SEVERITY_ID  IS 'Current Severity Identifier: Current severity of PV.'
;

COMMENT ON COLUMN PV.PV_VALUE  IS  'Process Variable Value: PV value that caused severity/status.'
;

COMMENT ON COLUMN PV.ALARM_TIME  IS  'Alarm Time: The time of the most recent alarm.'
;

COMMENT ON COLUMN PV.ACT_GLOBAL_ALARM_IND  IS  'Indicates if PV has an active global alarm.'
;

-- 
-- TABLE: ALARM.SEVERITY 
--

CREATE TABLE SEVERITY(
    SEVERITY_ID    INT             NOT NULL,
    NAME           VARCHAR(100)    NOT NULL,
    PRIMARY KEY (SEVERITY_ID)
);

COMMENT ON TABLE SEVERITY IS 'Severity of an alarm like "invalid", "major alarm" etc.'
;

COMMENT ON COLUMN SEVERITY.SEVERITY_ID IS 'Severity Identifier: Unique identifier for the alarm severity.'
;

COMMENT ON COLUMN SEVERITY.NAME IS 'Severity Name: '
;
-- 
-- TABLE: ALARM.STATUS 
--

CREATE TABLE STATUS(
    STATUS_ID    INT             NOT NULL ,
    NAME         VARCHAR(100)    NOT NULL,
    PRIMARY KEY (STATUS_ID)
);

COMMENT ON TABLE STATUS IS 'status of an alarm to provide more detail'
;

COMMENT ON COLUMN STATUS.STATUS_ID IS 'Status Identifier: Unique identifier for the alarm status.'
;
COMMENT ON COLUMN STATUS.NAME IS 'Status Name: such as "read error", "disconnected", ...'
;
-- 
-- INDEX: FK_ALARM_TREE_TO_ALARM_TREE 
--

CREATE INDEX FK_ALARM_TREE_TO_ALARM_TREE ON ALARM_TREE(PARENT_CMPNT_ID)
;
-- 
-- INDEX: FK_COMMAND_TO_ALARM_TREE 
--

CREATE INDEX FK_COMMAND_TO_ALARM_TREE ON COMMAND(COMPONENT_ID)
;
-- 
-- INDEX: FK_DISPLAY_TO_ALARM_TREE 
--

CREATE INDEX FK_DISPLAY_TO_ALARM_TREE ON DISPLAY(COMPONENT_ID)
;
-- 
-- INDEX: FK_GUIDANCE_TO_ALARM_TREE 
--

CREATE INDEX FK_GUIDANCE_TO_ALARM_TREE ON GUIDANCE(COMPONENT_ID)
;
-- 
-- INDEX: FK_AUTO_ACTION_TO_ALARM_TREE 
--

CREATE INDEX FK_AUTO_ACTION_TO_ALARM_TREE ON AUTOMATED_ACTION(COMPONENT_ID)
;
-- 
-- INDEX: FK_PV_TO_STATUS 
--

CREATE INDEX FK_PV_TO_STATUS ON PV(STATUS_ID)
;
-- 
-- INDEX: FK_PV_TO_SEVERITY 
--

CREATE INDEX FK_PV_TO_SEVERITY ON PV(SEVERITY_ID)
;
-- 
-- INDEX: FK_PV_TO_ALARM_TREE 
--

CREATE INDEX FK_PV_TO_ALARM_TREE ON PV(COMPONENT_ID)
;
-- 
-- TABLE: ALARM.ALARM_TREE 
--

ALTER TABLE ALARM_TREE ADD CONSTRAINT FK_ALARM_TREE_TO_ALARM_TREE 
    FOREIGN KEY (PARENT_CMPNT_ID)
    REFERENCES ALARM_TREE(COMPONENT_ID)
;


-- 
-- TABLE: ALARM.COMMAND 
--

ALTER TABLE COMMAND ADD CONSTRAINT FK_COMMAND_TO_ALARM_TREE 
    FOREIGN KEY (COMPONENT_ID)
    REFERENCES ALARM_TREE(COMPONENT_ID)
;


-- 
-- TABLE: ALARM.DISPLAY 
--

ALTER TABLE DISPLAY ADD CONSTRAINT FK_DISPLAY_TO_ALARM_TREE 
    FOREIGN KEY (COMPONENT_ID)
    REFERENCES ALARM_TREE(COMPONENT_ID)
;


-- 
-- TABLE: ALARM.GUIDANCE 
--

ALTER TABLE GUIDANCE ADD CONSTRAINT FK_GUIDANCE_TO_ALARM_TREE 
    FOREIGN KEY (COMPONENT_ID)
    REFERENCES ALARM_TREE(COMPONENT_ID)
;


-- 
-- TABLE: ALARM.AUTOMATED_ACTION
--

ALTER TABLE AUTOMATED_ACTION ADD CONSTRAINT FK_AUTO_ACTION_TO_ALARM_TREE 
    FOREIGN KEY (COMPONENT_ID)
    REFERENCES ALARM_TREE(COMPONENT_ID)
;


-- 
-- TABLE: ALARM.PV 
--

ALTER TABLE PV ADD CONSTRAINT FK_CUR_SVRTY_TO_SEVERITY 
    FOREIGN KEY (CUR_SEVERITY_ID)
    REFERENCES SEVERITY(SEVERITY_ID)
;

ALTER TABLE PV ADD CONSTRAINT FK_PV_TO_ALARM_TREE 
    FOREIGN KEY (COMPONENT_ID)
    REFERENCES ALARM_TREE(COMPONENT_ID)
;

ALTER TABLE PV ADD CONSTRAINT FK_PV_TO_SEVERITY 
    FOREIGN KEY (SEVERITY_ID)
    REFERENCES SEVERITY(SEVERITY_ID)
;

ALTER TABLE PV ADD CONSTRAINT FK_PV_TO_STATUS 
    FOREIGN KEY (STATUS_ID)
    REFERENCES STATUS(STATUS_ID)
;
