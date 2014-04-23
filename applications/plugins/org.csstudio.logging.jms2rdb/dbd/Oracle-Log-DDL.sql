# Oracle DDL for the CSS Message Log Tables.
#
# Translated from AMS-Log-DDL.sql (Markus Moeller, DESY),
# then adapted:
#
# Removed tables: msg_type, msg_type_property_type
# Removed columns: message.msg_type_id
# Added columns: message type, name, severity
#
# kasemirk@ornl.gov

-- MESSAGE Table: ID, date, 'essential' content (type, name, severity)
DROP TABLE message

CREATE TABLE message
(
 id                             NUMBER NOT NULL,
 datum                          TIMESTAMP (6) NOT NULL,
 type                           VARCHAR2(10) NOT NULL,
 name                           VARCHAR2(80),
 severity                       VARCHAR2(20),
 CONSTRAINT MESSAGE_PK PRIMARY KEY (id) USING INDEX
);

ALTER TABLE message
ADD CHECK ("ID" IS NOT NULL)
DISABLE NOVALIDATE;


-- MESSAGE_CONTENT Table: Additional message content, arbitrary type/value pairs
-- ID column isn't really used...
DROP TABLE message_content;

CREATE TABLE message_content
(
  id                             NUMBER NOT NULL,
  message_id                     NUMBER,
  msg_property_type_id           NUMBER,
  value                          VARCHAR2(300),
  CONSTRAINT MESSAGE_CONTENT_PK PRIMARY KEY (id) USING INDEX
);

CREATE INDEX message_content_val_indx ON message_content
  (
    value                           ASC
  );

CREATE INDEX message_content_msgid_indx ON message_content
  (
    message_id                      ASC
  );

ALTER TABLE message_content
ADD CONSTRAINT message_content_msg_prope_fk1 FOREIGN KEY (msg_property_type_id)
REFERENCES msg_property_type (id);

ALTER TABLE message_content
ADD CONSTRAINT message_content_message_fk1 FOREIGN KEY (message_id)
REFERENCES message (id) ON DELETE CASCADE;


-- MSG_PROPERTY_TYPE Table: Message content type IDs
DROP TABLE msg_property_type;

CREATE TABLE msg_property_type
(
  id                             NUMBER NOT NULL,
  name                           VARCHAR2(20),
  CONSTRAINT MSG_PROPERTY_TYPE_PK PRIMARY KEY (id) USING INDEX
);


-- Sequences for message.id, message_content.id
CREATE SEQUENCE message_id_seq
 START WITH 10001
 INCREMENT BY 1
 NOMAXVALUE
 CACHE 20;

CREATE SEQUENCE message_content_id_seq
 START WITH 10001
 INCREMENT BY 1
 NOMAXVALUE
 CACHE 20;

-- Trigger to auto-update the message_content.id from sequence
CREATE OR REPLACE TRIGGER set_message_content_tr
   BEFORE INSERT
   ON message_content
   FOR EACH ROW
DECLARE
BEGIN
   IF (:new.id IS NULL) THEN
      SELECT message_content_id_seq.NEXTVAL INTO :new.id FROM DUAL;
   END IF;
EXCEPTION
   WHEN OTHERS THEN
      opr$oracle.global_utils.v_errornum := SQLCODE;
      opr$oracle.global_utils.v_errortxt := SUBSTR (SQLERRM, 1, 200);
      opr$oracle.global_utils.log_error ('set_message_content_tr',
 
opr$oracle.global_utils.v_errornum,
 
opr$oracle.global_utils.v_errortxt);
      RAISE;
END set_message_content_tr;

-- SELECT MSG_LOG.message_id_seq.NEXTVAL FROM DUAL;
-- ALTER SEQUENCE MSG_LOG.message_id_seq INCREMENT BY -20;
