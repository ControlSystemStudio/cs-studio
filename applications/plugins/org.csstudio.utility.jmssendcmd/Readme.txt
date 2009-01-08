JMS Send Command
================

Headless (command-line) application that sends lines of input
from stdin or command-line to JMS.

Run with "-h" to see available command-line options
to configure JMS URL, topic, ...


Send messages from command-line to annunciator
----------------------------------------------
JMSSender -url tcp://ics-srv02.sns.ornl.gov:61616 -topic TALK -type alarm


Trigger alarm server and GUI to re-load the configuration
---------------------------------------------------------
JMSSender -url tcp://ics-srv02.sns.ornl.gov:61616 -topic ALARM_CLIENT -type alarm -text CONFIG

