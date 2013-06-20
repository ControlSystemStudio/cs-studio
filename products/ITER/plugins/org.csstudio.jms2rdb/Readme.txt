JMS2RDB

Tool that listens to CSS messages for log, alarm, ... on JMS
and sends them to the RDB.

"dbd" subdir has MySQL, Oracle and Postgres DBD.

JMS2RDB.product is Eclipse product definition for building
the application.

Meant to run as headless app with -pluginCustomization
option to override defaults in plug_customization.ini.

Headless app Issues on Linux:
Need -vmargs  -D java.awt.headless=true ?
See also https://bugs.eclipse.org/bugs/show_bug.cgi?id=201414

VERSIONS
Defined in plugin.xml and JMS2RDB.product.
See webroot/versions.html for brief notes.

V2.0.0
IDs for message entries now depend on Oracle sequence or MySQL auto-increment columns.
Before, this program would determine the next available message ID, but that can fail
when multiple programs try to add to the message tables.
