#!../../bin/linux-x86/first

## You may have to change first to something else
## everywhere it appears in this file

#< envPaths

#cd ${TOP}

scanOnceSetQueueSize(5000)

## Register all support components
##dbLoadDatabase "dbd/first.dbd"
##dbLoadDatabase "dbd/xxxRecord.dbd"
##dbLoadDatabase "dbd/xxxSupport.dbd"

##first_registerRecordDeviceDriver pdbbase

## Load record instances
##dbLoadTemplate "db/userHost.substitutions"
##dbLoadRecords "db/dbSubExample.db", "user=kunalHost"

## Load Scalability Test IOC
dbLoadTemplate "output.substitutions"

## Set this to see messages from mySub
#var mySubDebug 1

## Run this to trace the stages of iocInit
#traceIocInit

#cd ${TOP}/iocBoot/${IOC}
iocInit

## Start any sequence programs
#seq sncExample, "user=kunalHost"
