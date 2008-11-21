# Update to latest schema:
# Removed tables: msg_type, msg_type_property_type
# Removed columns: message.msg_type_id
# Added columns: message type, name, severity
#
#  NOTE: This has been updated from information from the DESY CSS web page.
#        Unclear if it will still work, it has not been tested!

-- Tabelle MESSAGE
DROP TABLE message

CREATE TABLE message
(
 id                             NUMBER NOT NULL,
 datum                          TIMESTAMP (6),
 type                           VARCHAR2(10) NOT NULL,
 name                           VARCHAR2(80),
 severity                       VARCHAR2(20),
 CONSTRAINT MESSAGE_PK PRIMARY KEY (id) USING INDEX
)
/

ALTER TABLE message
ADD CHECK ("ID" IS NOT NULL)
DISABLE NOVALIDATE
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

