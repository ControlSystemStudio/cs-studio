
# Time zone
epicsEnvSet("EPICS_TIMEZONE","MET::-60:033001:102601")

# iocLogClient
# interconnection server for demo, IP: 127.0.0.1 (local host)
epicsEnvSet("EPICS_IOC_LOG_INET_LIST","127.0.0.1")
epicsEnvSet("EPICS_IOC_NAME","DALTestIoc")
epicsEnvSet("EPICS_FACILITY","DAL_TEST")

epicsEnvSet(IOCSH_PS1,"TrainIoc> ")

## Register all support components
dbLoadDatabase "../../dbd/demo.dbd"
demo_registerRecordDeviceDriver pdbbase

## Load record instances
dbLoadRecords "../../db/DALPrecisionTest.db", "user=demoHost"
dbLoadRecords("../../db/ioc_common.db", "APPL=TrainIoc")

# CA security
asSetFilename("asFile")

## Set this to see messages from mySub
#var mySubDebug 1

## Run this to trace the stages of iocInit
#traceIocInit

iocInit

dbpf "TrainIoc:valid","Enabled"   # from demo

# ioc logging client start
#var alarmLogDisable 1
#var sysMsgLogDisable 1
#var caPutLogDisable 1
iocLogClientInit

## Start any sequence programs
seq sncExample, "user=demoHost"
