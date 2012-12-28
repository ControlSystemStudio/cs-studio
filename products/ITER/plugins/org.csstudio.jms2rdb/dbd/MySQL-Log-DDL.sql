# MySQL DDL for the CSS Message Log Tables.
#
# Translated from AMS-Log-DDL.sql (Markus Moeller, DESY),
# then adapted to the Message Tables.pdf from July 2008:
#
# Removed tables: msg_type, msg_type_property_type
# Removed columns: message.msg_type_id
# Added columns: message type, name, severity
#
# NOTE:
# The msghist tool uses subselect queries as shown below to do a full search
# for properties that are not optimized as MESSAGE columns.
# Those subselect statements only work in MySQL 6.x.
# MySQL 5.x will just hang forever in them as soon as there
# are a few rows in the MESSAGE* tables!
#
# SELECT m.id, m.datum, m.TYPE, m.NAME, m.SEVERITY, c.msg_property_type_id p, c.value
#   FROM message m, message_content c
#  WHERE m.datum BETWEEN ? AND ? AND m.id=c.message_id
#    AND m.id IN
#      ( SELECT message_id FROM message_content WHERE msg_property_type_id=4 AND value LIKE ?)

#
# kasemirk@ornl.gov

# Create a 'log' user who can access the 'log' tables
GRANT ALL ON log.* TO log IDENTIFIED BY '$log';
# Check
SELECT User, Host FROM user;
SELECT User, Host, Db FROM db;

#SET PASSWORD FOR root@localhost=PASSWORD('new-password');
#SET PASSWORD FOR root@'titan-terrier'=PASSWORD('new-password');

# Create database
DROP DATABASE IF EXISTS log;
CREATE DATABASE log;

# Create tables
USE log;

-- Available Properties
DROP TABLE IF EXISTS msg_property_type;
CREATE TABLE IF NOT EXISTS msg_property_type
(
  id  INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
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
INSERT INTO msg_property_type(name) VALUES ('FILENAME');
SELECT * FROM msg_property_type;

-- Message
DROP TABLE IF EXISTS message;
CREATE TABLE IF NOT EXISTS message
(
   id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
   datum TIMESTAMP NOT NULL,
   type VARCHAR(10) NOT NULL,
   name VARCHAR(80) NULL,
   severity VARCHAR(20) NULL  
);

-- Elements of a Message
-- ID column isn't really used...
DROP TABLE IF EXISTS message_content;
CREATE TABLE IF NOT EXISTS message_content
(
  id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  message_id INT UNSIGNED NOT NULL,
  msg_property_type_id INT UNSIGNED NOT NULL,
  value VARCHAR(100)
);


# NOTE:
# MyISAM ignores forgeign keys, and the software will work fine
# without them, but for the sake of completeness there should
# be these foreign keys:
#
# Message content must point to a valid message entry:
# message_content.message_id -> message.id
#
# Property ID must point to a defined message property
# message_content.msg_property_type_id -> msg_property_type.id
#
# For performance reasons, you also want indices on
# message.ID, message.datum, maybe more


# Example Message with some elements
-- NOTE:
-- When you manually insert data as shown below,
-- you need to also update the sequences to cover
-- the message and message_content IDs that you
-- used for the data!
INSERT INTO message VALUES(1, NOW(), 'log', '', 'INFO');
INSERT INTO message_content VALUES(3, 1, 3, NOW());
INSERT INTO message_content VALUES(4, 1, 4, 'Message Text');
INSERT INTO message_content VALUES(5, 1, 5, 'User Fred');
INSERT INTO message_content VALUES(6, 1, 6, 'My Host');

# Dump messages with all their properties
SELECT m.*, p.name Property, c.value Value
  FROM message m, msg_property_type p, message_content c
  WHERE m.id = c.message_id
    AND p.id = c.msg_property_type_id;

# Dump only 'TEXT' of 'log' messages
SELECT m.datum Date, c.value Text
  FROM message m, msg_property_type p, message_content c
  WHERE m.id = c.message_id
    AND p.id = c.msg_property_type_id
    AND p.name = 'TEXT'
  ORDER BY m.datum;

# From org.csstudio.alarm.dbaccess:
SELECT m.id ID, m.datum Date, mpt.name as Property, mct.value Value 
  FROM  message m, message_content mct, msg_property_type mpt 
  WHERE mpt.id = mct.msg_property_type_id 
    AND m.id = mct.MESSAGE_ID
    AND m.datum >= '2008-06-23'
  ORDER BY mct.MESSAGE_ID, Property DESC;
  
  