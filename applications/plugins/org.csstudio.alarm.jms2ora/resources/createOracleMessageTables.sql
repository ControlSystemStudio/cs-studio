DROP TABLE message_content;
DROP TABLE message;
DROP TABLE msg_type_property_type;
DROP TABLE msg_property_type;
DROP TABLE msg_type;

CREATE TABLE msg_type
(
    id                             NUMBER NOT NULL,
    name                           VARCHAR2(20),
    CONSTRAINT MSG_TYPE_PK PRIMARY KEY (id) USING INDEX
);


CREATE TABLE msg_property_type
(
    id                             NUMBER NOT NULL,
    name                           VARCHAR2(20),
    CONSTRAINT MSG_PROPERTY_TYPE_PK PRIMARY KEY (id) USING INDEX
);

CREATE TABLE message
(
    id                             NUMBER NOT NULL,
    msg_type_id                    NUMBER,
    datum                          TIMESTAMP(6),
    name                           VARCHAR2(300),
    type                           VARCHAR2(300),
    severity                       VARCHAR2(300),
    CHECK ("ID" IS NOT NULL) DISABLE NOVALIDATE,
    CONSTRAINT MESSAGE_PK PRIMARY KEY (id) USING INDEX,
    CONSTRAINT message_msg_type_fk1 FOREIGN KEY (msg_type_id) REFERENCES msg_type(id)
);

CREATE TABLE msg_type_property_type
(
    id                             NUMBER NOT NULL,
    msg_type_id                    NUMBER,
    msg_property_type_id           NUMBER,
    CONSTRAINT MSG_TYPE_PROPERTY_TYPE_PK PRIMARY KEY (id) USING INDEX,
    CONSTRAINT msg_type_property_type_ms_fk2 FOREIGN KEY (msg_property_type_id) REFERENCES msg_property_type (id),
    CONSTRAINT msg_type_property_type_ms_fk1 FOREIGN KEY (msg_type_id) REFERENCES msg_type (id)
);

CREATE TABLE message_content
(
    id                             NUMBER NOT NULL,
    message_id                     NUMBER,
    msg_property_type_id           NUMBER,
    value                          VARCHAR2(300),
    CONSTRAINT MESSAGE_CONTENT_PK PRIMARY KEY (id) USING INDEX,
    CONSTRAINT message_content_msg_prope_fk1 FOREIGN KEY (msg_property_type_id) REFERENCES msg_property_type (id),
    CONSTRAINT message_content_message_fk1 FOREIGN KEY (message_id) REFERENCES message (id) ON DELETE CASCADE
);

