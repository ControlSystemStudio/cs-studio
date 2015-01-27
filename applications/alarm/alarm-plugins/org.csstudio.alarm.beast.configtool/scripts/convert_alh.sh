#!/bin/sh
# Convert ALH config into XML format
# kasemirk@ornl.gov

if [ $# -ne 2 ]
then
        echo Usage: convert ALH_file New_file
        echo Need full path names for both!
        exit -1
fi

WS=/tmp/cvt.$$

../AlarmConfigTool/AlarmConfigTool -data $WS -alh -file $1 >$2
rm -rf $WS

