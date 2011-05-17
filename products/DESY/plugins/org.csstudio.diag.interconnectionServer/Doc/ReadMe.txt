The interconnectionServer receives alarms in form of ASCII messages from the IOC

recGbl.c (.h) 
-------------
provides the alarm hook (default in EPICS > 3.14.?)

logAlarms.c 
-----------
is the routine queuing, preparing and sending alarm messages.

startup.common_V2
-----------------
is setting EPICS_ALARM_SERVER_INET (the machines where the interconnectionServer is supposed to run)
@ DESY: Two instances running on a SUN cluster, two instances on test machines
example:
putenv "EPICS_ALARM_SERVER_INET=krynfsa krynfsb krykPCI krykPCR"

startup
-------
other environment variables are defined here:
example:
putenv "EPICS_IOC_NAME=ttfKryoCB"
putenv "EPICS_FACILITY=FLASH"

Initializes logAlarms by calling: logAlarmsInit