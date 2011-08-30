DROP TABLE IF EXISTS message_content;
DROP TABLE IF EXISTS message;
DROP TABLE IF EXISTS msg_type_property_type;
DROP TABLE IF EXISTS msg_property_type;
DROP TABLE IF EXISTS msg_type;

CREATE TABLE msg_type
(
    id INT PRIMARY KEY NOT NULL,
    name VARCHAR(20)
);

CREATE TABLE msg_property_type
(
    id INT PRIMARY KEY NOT NULL,
    name VARCHAR(20)
);

CREATE TABLE msg_type_property_type
(
    id INT PRIMARY KEY NOT NULL,
    msg_type_id INT NOT NULL,
    msg_property_type_id INT NOT NULL
);

CREATE TABLE message_content
(
    id INT PRIMARY KEY NOT NULL,
    message_id INT NOT NULL,
    msg_property_type_id INT NOT NULL,
    value VARCHAR(300)
);

CREATE TABLE message
(
    id INT PRIMARY KEY NOT NULL,
    msg_type_id INT NOT NULL,
    datum TIMESTAMP,
    name VARCHAR(300),
    type VARCHAR(300),
    severity VARCHAR(300)
);

CREATE USER 'krykams'@'localhost' IDENTIFIED BY 'krykams';
GRANT ALL PRIVILEGES ON messagearchive.* TO 'krykams'@'localhost' IDENTIFIED BY 'krykams';
