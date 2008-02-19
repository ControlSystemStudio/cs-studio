The interconnectionServer receives alarms in form of ASCII messages from the IOC

recGbl.c 
--------
provides the alarm hook (default in EPICS > 3.14.?)

logAlarms.c 
-----------
is the core routine preparing and sending alarm messages

startup_common_V2
-----------------
is setting EPICS_ALARM_SERVER_INET (the machines where the nterconnectionServer is supposed to run)
@ DESY: Two instances running on a SUN cluster, two instances on test machines

startup
-------
Initializes logAlarms by calling: logAlarmsInit