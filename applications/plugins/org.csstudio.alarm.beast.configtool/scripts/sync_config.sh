#!/bin/sh
#
# Pull alarm configuration from ICS,
# update office demo config to match.
#
# Added to crontab like this:
#
# Run 5 minutes after 19:00, every day
#   5 19 * * *    /bin/sh /usr/local/css/alarm_configs/sync_config.sh 2>&1
#
# kasemirk@ornl.gov

export PATH=/usr/local/java/jdk1.5.0_13/bin:$PATH

CSS=/usr/local/css

cd $CSS/alarm_configs

$CSS/JMSSender/JMSSender -url tcp://ics-srv02.sns.ornl.gov:61616 -topic TALK -type alarm -text "Synchronizing alarm configuration with I C S setup"

# Get ICS config
# wget would create ....1 copy, not overwrite
rm Annunciator_latest.xml
wget http://ics-srv-web2.sns.ornl.gov/ade/epics/Data/Alarms/Annunciator_latest.xml

# Update office config
$CSS/AlarmConfigTool/AlarmConfigTool -import -root Annunciator -file `pwd`/Annunciator_latest.xml
# Notify alarm server and GUIs
$CSS/JMSSender/JMSSender -url tcp://ics-srv02.sns.ornl.gov:61616 -topic ALARM_CLIENT -type alarm -text CONFIG
