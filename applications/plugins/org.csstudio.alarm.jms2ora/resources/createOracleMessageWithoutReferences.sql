
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
    CONSTRAINT MESSAGE_PK PRIMARY KEY (id) USING INDEX
);

CREATE TABLE msg_type_property_type
(
    id                             NUMBER NOT NULL,
    msg_type_id                    NUMBER,
    msg_property_type_id           NUMBER,
    CONSTRAINT MSG_TYPE_PROPERTY_TYPE_PK PRIMARY KEY (id) USING INDEX
);

CREATE TABLE message_content
(
    id                             NUMBER NOT NULL,
    message_id                     NUMBER,
    msg_property_type_id           NUMBER,
    value                          VARCHAR2(300),
    CONSTRAINT MESSAGE_CONTENT_PK PRIMARY KEY (id) USING INDEX
);
