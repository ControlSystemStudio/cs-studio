# MySQL DDL for the CSS Message Log Tables.
#
# Translated from AMS-Log-DDL.sql (Markus Moeller, DESY)
# by kasemirk@ornl.gov

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

-- Available Message Types
DROP TABLE IF EXISTS msg_type;
CREATE TABLE IF NOT EXISTS msg_type
(
  id  INT UNSIGNED NOT NULL PRIMARY KEY,
  name VARCHAR(20) NOT NULL
);

INSERT INTO msg_type VALUES (1, 'log');
INSERT INTO msg_type VALUES (99, 'test');
SELECT * FROM msg_type;


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


-- Which Properties are used by a certain Message Type? 
DROP TABLE IF EXISTS msg_type_property_type;
CREATE TABLE IF NOT EXISTS msg_type_property_type
(
  id  INT UNSIGNED NOT NULL PRIMARY KEY,
  msg_type_id INT UNSIGNED NOT NULL ,
  msg_property_type_id INT UNSIGNED NOT NULL
);

INSERT INTO msg_type_property_type VALUES(1, 1, 1);
INSERT INTO msg_type_property_type VALUES(2, 1, 2);
INSERT INTO msg_type_property_type VALUES(3, 1, 3);
INSERT INTO msg_type_property_type VALUES(4, 1, 4);
INSERT INTO msg_type_property_type VALUES(5, 1, 5);
INSERT INTO msg_type_property_type VALUES(6, 1, 6);
INSERT INTO msg_type_property_type VALUES(7, 1, 7);
INSERT INTO msg_type_property_type VALUES(8, 1, 8);
INSERT INTO msg_type_property_type VALUES(9, 1, 9);
INSERT INTO msg_type_property_type VALUES(10, 1, 10);

INSERT INTO msg_type_property_type VALUES(99, 99, 1);
INSERT INTO msg_type_property_type VALUES(100, 99, 4);

# List message types and their properties
SELECT m.name Message, p.name Property
 FROM msg_type m, msg_property_type p, msg_type_property_type l
 WHERE m.id = l.msg_type_id AND l.msg_property_type_id = p.id
 ORDER BY m.name, p.id;


-- Message
DROP TABLE IF EXISTS message;
CREATE TABLE IF NOT EXISTS message
(
   id INT UNSIGNED NOT NULL PRIMARY KEY,
   msg_type_id INT UNSIGNED NOT NULL,
   datum TIMESTAMP NOT NULL
);

-- Elements of a Message
DROP TABLE IF EXISTS message_content;
CREATE TABLE IF NOT EXISTS message_content
(
  id INT UNSIGNED NOT NULL PRIMARY KEY,
  message_id INT UNSIGNED NOT NULL,
  msg_property_type_id INT UNSIGNED NOT NULL,
  value VARCHAR(300)
);

# Example Message with some elements
INSERT INTO message VALUES(1, 1, NOW());
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
select  mct.message_id, mt.name as MsgType, m.datum, mpt.name as Property,  mct.value 
  from  message m, message_content mct,msg_type mt, msg_property_type mpt 
  where  mpt.id = mct.msg_property_type_id 
  and  m.id = mct.MESSAGE_ID 
  and  m.msg_type_id = mt.id 
  ORDER BY mct.MESSAGE_ID DESC 

SELECT m.id ID, m.datum Date, mpt.name as Property, mct.value Value 
  FROM  msg_type mt, message m, message_content mct, msg_property_type mpt 
  WHERE mt.name="log"
    AND m.msg_type_id = mt.id 
    AND mpt.id = mct.msg_property_type_id 
    AND m.id = mct.MESSAGE_ID
    AND m.datum >= '2008-06-23'
  ORDER BY mct.MESSAGE_ID, Property DESC;
  
  