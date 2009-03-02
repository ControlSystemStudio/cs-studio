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


Use for EDM 'write' logging
-------------------------
EDM can log PV writes to a 'write' log, see EDM manual for details:
  http://ics-web.sns.ornl.gov/edm/edmUserManual/index.html#pv-put-logging
 
To test, first try to run
 
 JMSSender -url tcp://ics-srv02.sns.ornl.gov:61616 -topic WRITE -type write -edm_mode

and paste lines like this one to its std-input:

user="fred" host="ics-srv02" ssh="::ffff:160.91.233.3 54425 ::ffff:160.91.230.38 22" dsp="localhost:16.0" name="RFQ_Vac:Pump1:Pressure" old="1.000000" new="2.000000"

If that turns into a corresponding JMS message, wrap the JMSSender... command
into a shell script 'edm_write_logger.sh' and use that as the EDM put logger:

 export EDMPUTLOGGER=edm_write_logger.sh
 
 
 


