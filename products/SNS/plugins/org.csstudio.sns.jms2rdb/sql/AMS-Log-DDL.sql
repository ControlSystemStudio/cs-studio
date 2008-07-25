-- Tabelle MESSAGE
DROP TABLE message

CREATE TABLE message
(
 id                             NUMBER NOT NULL,
 msg_type_id                    NUMBER,
 datum                          TIMESTAMP (6),
 CONSTRAINT MESSAGE_PK PRIMARY KEY (id) USING INDEX
)
/

ALTER TABLE message
ADD CHECK ("ID" IS NOT NULL)
DISABLE NOVALIDATE
/

ALTER TABLE message
ADD CONSTRAINT message_msg_type_fk1 FOREIGN KEY (msg_type_id)
REFERENCES msg_type (id)
/


-- Tabelle MESSAGE_CONTENT
DROP TABLE message_content
/

CREATE TABLE message_content
(
  id                             NUMBER NOT NULL,
  message_id                     NUMBER,
  msg_property_type_id           NUMBER,
  value                          VARCHAR2(300),
  CONSTRAINT MESSAGE_CONTENT_PK PRIMARY KEY (id) USING INDEX
)
/

CREATE INDEX message_content_val_indx ON message_content
  (
    value                           ASC
  )
/

CREATE INDEX message_content_msgid_indx ON message_content
  (
    message_id                      ASC
  )
/

ALTER TABLE message_content
ADD CONSTRAINT message_content_msg_prope_fk1 FOREIGN KEY (msg_property_type_id)
REFERENCES msg_property_type (id)
/

ALTER TABLE message_content
ADD CONSTRAINT message_content_message_fk1 FOREIGN KEY (message_id)
REFERENCES message (id) ON DELETE CASCADE
/


-- Tabelle MSG_TYPE
DROP TABLE msg_type
/

CREATE TABLE msg_type
(
  id                             NUMBER NOT NULL,
  name                           VARCHAR2(20),
  CONSTRAINT MSG_TYPE_PK PRIMARY KEY (id) USING INDEX
)
/


-- Tabelle MSG_PROPERTY_TYPE
DROP TABLE msg_property_type
/

CREATE TABLE msg_property_type
(
  id                             NUMBER NOT NULL,
  name                           VARCHAR2(20),
  CONSTRAINT MSG_PROPERTY_TYPE_PK PRIMARY KEY (id) USING INDEX
)
/


-- Tabelle MSG_TYPE_PROPERTY_TYPE
DROP TABLE msg_type_property_type
/

CREATE TABLE msg_type_property_type
(
  id                             NUMBER NOT NULL,
  msg_type_id                    NUMBER,
  msg_property_type_id           NUMBER,
  CONSTRAINT MSG_TYPE_PROPERTY_TYPE_PK PRIMARY KEY (id) USING INDEX
)
/

ALTER TABLE msg_type_property_type
ADD CONSTRAINT msg_type_property_type_ms_fk2 FOREIGN KEY (msg_property_type_id)
REFERENCES msg_property_type (id)
/

ALTER TABLE msg_type_property_type
ADD CONSTRAINT msg_type_property_type_ms_fk1 FOREIGN KEY (msg_type_id)
REFERENCES msg_type (id)
/
