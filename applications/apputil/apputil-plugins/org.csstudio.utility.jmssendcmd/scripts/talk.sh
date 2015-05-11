#!/bin/sh
# Send messages to TALK topic
# Sends command line text or reads from stdin

SENDER="/usr/local/css/JMSSender/JMSSender -url tcp://ics-srv-epics1.ics.sns.gov:61616 -topic TALK -type talk"

if [ $# -ge 1 ]
then
    $SENDER -text "$*"
else
    $SENDER
fi
