# MySQL DDL for the CSS Message Log Tables.
#
# Translated from AMS-Log-DDL.sql (Markus Moeller, DESY),
# then adapted to the Message Tables.pdf from July 2008:
#
# Removed tables: msg_type, msg_type_property_type
# Removed columns: message.msg_type_id
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
  id  INT UNSIGNED NOT NULL PRIMARY KEY,
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
SELECT * FROM msg_property_type;

-- Message
DROP TABLE IF EXISTS message;
CREATE TABLE IF NOT EXISTS message
(
   id INT UNSIGNED NOT NULL PRIMARY KEY,
   datum TIMESTAMP NOT NULL
);

-- Elements of a Message
DROP TABLE IF EXISTS message_content;
CREATE TABLE IF NOT EXISTS message_content
(
  id INT UNSIGNED NOT NULL PRIMARY KEY,
  message_id INT UNSIGNED NOT NULL,
  msg_property_type_id INT UNSIGNED NOT NULL,
  value VARCHAR(100)
);

# Example Message with some elements
INSERT INTO message VALUES(1, NOW());
INSERT INTO message_content VALUES(1, 1, 1, NOW());
INSERT INTO message_content VALUES(2, 1, 2, NOW());
INSERT INTO message_content VALUES(3, 1, 3, NOW());
INSERT INTO message_content VALUES(4, 1, 4, 'Message Text');
INSERT INTO message_content VALUES(5, 1, 5, 'User Fred');
INSERT INTO message_content VALUES(6, 1, 6, 'My Host');
INSERT INTO message_content VALUES(7, 1, 7, 'MyClass');

# Dump messages with all their properties
SELECT m.id Msg, m.datum Date, p.name Property, c.value Value
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
  
  