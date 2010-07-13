#!/bin/sh
#
# Import XML config, restart alarm server and GUIs
# kasemirk@ornl.gov

if [ $# -ne 1 ]
then
    echo "Usage: import /path/to/config.xml"
    echo "Need full path!"
    exit -1
fi

../JMSSender/talk.sh "Importing new alarm system configuration"
WS=/tmp/import.$$
../AlarmConfigTool/AlarmConfigTool -data $WS -import -root Annunciator -file $1
rm -rf $WS
../JMSSender/reload.sh

