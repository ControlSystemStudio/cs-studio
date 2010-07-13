Alarm Server
============

To check or update settings, refer to 
plugin_customization.ini for examples,
then create something like SNS_CCR_ANC.ini
for your application and provide that via
  -pluginCustomization MySettings.ini
command line.


Versions
========

Version number of org.csstudio.alarm.server/plugin.xml is
considered overall Alarm Server version,
also printed on startup.

2.0.3 - 2010/07/01
Error in auto-reconnect to RDB

2.0.2 - 2010/06/18
Handle 'current' status of PVs, jump version # to match CSS client

1.1.3 - 2010/04/13
Handle more NULLs in RDB configuration

1.1.2 - 2010/03/12
pv_start_delay option

1.1.1 - 2009/11/19
Include 'sim' PVs

1.1.0 - 2009/08/27
Send 'CONFIG' in messages to allow separate configurations at the same time.

1.0.2 - 2009/08/10
Support for 'maintenance mode'

1.0.1
First SNS Production Version, no more details


Memory
======
[main] csstudio.alarm.WorkQueue (WorkQueue.java:60)
 - java.lang.OutOfMemoryError: Java heap space

There's memory for heap space and permanent mem used for code.
Default heap max seems to be 64MB.

Set min and max to the same value to avoid resizing?
On command-line when starting product:
 -vmargs -Xms256m -Xmx256m -XX:PermSize=64m -XX:MaxPermSize=64m 
 
Also possible in css.ini or eclipse.ini, found next to css, eclipse executable.
On OS X that's under *.app/Content/MacOS.
In there, put each argument on a separate line:

-Xms128m
-Xmx256m
-XX:MaxPermSize=256m
-XX:+UseParallelGC

Eclipse IDE uses
-Xms40m
-Xmx512m
-XX:MaxPermSize=256m

This *.ini is automatically created from the *.product's
Launching/Launching Argumants/VM Arguments flags