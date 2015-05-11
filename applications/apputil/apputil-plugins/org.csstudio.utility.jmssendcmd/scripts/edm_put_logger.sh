#!/bin/sh
# Use JMSSender as EDM 'put' logger

/usr/local/css/JMSSender/JMSSender -url tcp://ics-srv-epics1.ics.sns.gov:61616 -topic PUT -type put -edm_mode
