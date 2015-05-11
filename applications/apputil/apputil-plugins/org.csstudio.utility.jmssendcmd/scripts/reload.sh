#!/bin/sh
# Send config-reload trigger

/usr/local/css/JMSSender/JMSSender -url tcp://ics-srv-epics1.ics.sns.gov:61616 -topic ALARM_CLIENT -type alarm -text CONFIG
