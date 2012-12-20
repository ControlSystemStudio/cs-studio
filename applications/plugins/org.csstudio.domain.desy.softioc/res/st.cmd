# Time zone
epicsEnvSet("EPICS_TIMEZONE","MET::-60:033001:102601")

# iocLogClient
# interconnection server for demo, IP: 127.0.0.1 (local host)
epicsEnvSet("EPICS_IOC_LOG_INET_LIST","127.0.0.1")
epicsEnvSet("EPICS_IOC_NAME","UnitTestIoc")
epicsEnvSet("EPICS_FACILITY","TEST")

epicsEnvSet(IOCSH_PS1,"UnitTestIoc> ")

## Register all support components
dbLoadDatabase "dbd/demo.dbd"
demo_registerRecordDeviceDriver pdbbase

# CA security
#asSetFilename("asFile")

## Load record instances
dbLoadRecords("db/iocCommon.db", "APPL=UnitTestIoc")

# here the soft ioc code will append the given db files
# and thereafter the following two lines
#iocInit 
#dbpf "TrainIoc:valid","Enabled"   # from demo
