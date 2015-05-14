/* 
 * PostGres SQL DDL for the CSS Message Log Tables.
 *
 * Translated from AMS-Log-DDL.sql (Markus Moeller, DESY),
 * then adapted to the Message Tables.pdf from July 2008:
 *
 * Removed tables: msg_type, msg_type_property_type
 * Removed columns: message.msg_type_id
 * Added columns: message type, name, severity
 *
 * kasemirk@ornl.gov, Lana Abadie
 */

/*
CREATE USER log;
ALTER USER log WITH PASSWORD '$log';

CREATE USER report WITH PASSWORD '$report';

SELECT * FROM pg_user;

-- The following would have to be executed _after_ creating the tables:
GRANT SELECT, INSERT, UPDATE, DELETE
  ON msg_property_type, message, message_content TO log;

GRANT SELECT
  ON msg_property_type, message, message_content TO report;

GRANT USAGE, UPDATE ON SEQUENCE message_id_seq, message_content_id_seq  TO log;

*/


set client_min_messages='warning';

-- Create database
DROP DATABASE IF EXISTS log;
CREATE DATABASE log;

-- Create tables
\connect log;

set client_min_messages='warning';

-- Available Properties
DROP TABLE IF EXISTS msg_property_type;
CREATE TABLE  msg_property_type
(
  id  BIGINT NOT NULL PRIMARY KEY,
  name VARCHAR(20) NOT NULL
);
INSERT INTO msg_property_type VALUES (1, 'TYPE');
INSERT INTO msg_property_type VALUES (2, 'EVENTTIME');
INSERT INTO msg_property_type VALUES (3, 'CREATETIME');
INSERT INTO msg_property_type VALUES (4, 'TEXT');
INSERT INTO msg_property_type VALUES (5, 'USER');
INSERT INTO msg_property_type VALUES (6, 'HOST');
INSERT INTO msg_property_type VALUES (7, 'NAME');
INSERT INTO msg_property_type VALUES (8, 'APPLICATION-ID');
INSERT INTO msg_property_type VALUES (9, 'CLASS');
INSERT INTO msg_property_type VALUES (10,'FILENAME');
-- add these new properties to avoid error in css while using mess history and filtering
INSERT INTO msg_property_type VALUES (11,'SEQ');
INSERT INTO msg_property_type VALUES (12,'SEVERITY');
INSERT INTO msg_property_type VALUES (13,'ID');
INSERT INTO msg_property_type VALUES (14,'DELTA');
INSERT INTO msg_property_type VALUES (15,'TIME');
/*SELECT * FROM msg_property_type;*/

-- Message
DROP TABLE IF EXISTS message;
CREATE TABLE  message
(
   id SERIAL PRIMARY KEY,
   datum TIMESTAMP NOT NULL,
   type VARCHAR(10) NOT NULL,
   name VARCHAR(80) NULL,
   severity VARCHAR(20) NULL  
);

-- Elements of a Message
-- ID column isn't really used...
DROP TABLE IF EXISTS message_content;
CREATE TABLE  message_content
(
  id SERIAL PRIMARY KEY,
  message_id BIGINT NOT NULL,
  msg_property_type_id BIGINT NOT NULL,
  value VARCHAR(100)
);


--adding foreign key
alter table message_content add constraint message_id_fk foreign key (message_id) references message(id);
alter table message_content add constraint msg_pptype_id_fk foreign key (msg_property_type_id) references msg_property_type(id);

--add indexes

create index msg_id_idx on message_content (message_id);
create index msg_pp_type_id_idx on message_content (msg_property_type_id);

-- Example Message with some elements
-- NOTE:
-- When you manually insert data as shown below,
-- you need to also update the sequences to cover
-- the message and message_content IDs that you
-- used for the data!
/*
INSERT INTO message VALUES(1, NOW(), 'log', '', 'INFO');
INSERT INTO message_content VALUES(3, 1, 3, NOW());
INSERT INTO message_content VALUES(4, 1, 4, 'Message Text');
INSERT INTO message_content VALUES(5, 1, 5, 'User Fred');
INSERT INTO message_content VALUES(6, 1, 6, 'My Host');
*/

/*
-- Dump messages with all their properties
SELECT m.*, p.name as Property, c.value as Value
  FROM message m, msg_property_type p, message_content c
  WHERE m.id = c.message_id
    AND p.id = c.msg_property_type_id;

-- Dump only 'TEXT' of 'log' messages
SELECT m.datum as Date, c.value as Text
  FROM message m, msg_property_type p, message_content c
  WHERE m.id = c.message_id
    AND p.id = c.msg_property_type_id
    AND p.name = 'TEXT'
  ORDER BY m.datum;

-- From org.csstudio.alarm.dbaccess:
SELECT m.id ID, m.datum Date, mpt.name as Property, mct.value as Value 
  FROM  message m, message_content mct, msg_property_type mpt 
  WHERE mpt.id = mct.msg_property_type_id 
    AND m.id = mct.MESSAGE_ID
    AND m.datum >= '2008-06-23'
  ORDER BY mct.MESSAGE_ID, Property DESC;
*/  
