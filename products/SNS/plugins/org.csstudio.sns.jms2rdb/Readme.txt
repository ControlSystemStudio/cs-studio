JMS2RDB

Tool that listens to CSS messages for log, alarm, ... on JMS
and sends them to the RDB.

sql subdir has MySQL and Oracle DBD.

JMS2RDB.product is Eclipse prodict definition.
Meant to run as headless app with -pluginCustomization
option to override defaults in plug_customization.ini.

Headless app Issues on Linux:
Need -vmargs  -D java.awt.headless=true ?
See also https://bugs.eclipse.org/bugs/show_bug.cgi?id=201414