#!/bin/sh
#
# Save snapshot of alarm config to file
#
# Added to crontab like this:
#
#   # run five minutes after 18:00, every day
#   5 18 * * *    /bin/sh /usr/local/css/alarm_configs/snapshot.sh 2>&1
#
# kasemirk@ornl.gov

export PATH=/usr/local/java/jdk1.5.0_09/bin:$PATH
FILE=/usr/local/css/alarm_configs/Annunciator_`date +'%a'`.xml
/usr/local/css/AlarmConfigTool/AlarmConfigTool -root Annunciator -export -file $FILE
cp $FILE /ade/epics/Data/Alarms

# Add diff to previous config
if [ -r /ade/epics/Data/Alarms/Annunciator_latest.xml ]
then
    diff -C 5 $FILE /ade/epics/Data/Alarms/Annunciator_latest.xml >$FILE.diff
fi
cp $FILE /ade/epics/Data/Alarms/Annunciator_latest.xml

