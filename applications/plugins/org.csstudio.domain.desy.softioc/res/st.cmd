# Time zone
epicsEnvSet("EPICS_TIMEZONE","MET::-60:033001:102601")

# iocLogClient
# interconnection server for demo, IP: 127.0.0.1 (local host)
epicsEnvSet("EPICS_IOC_LOG_INET_LIST","127.0.0.1")
epicsEnvSet("EPICS_IOC_NAME","TrainIoc")
epicsEnvSet("EPICS_FACILITY","TEST")

epicsEnvSet(IOCSH_PS1,"TrainIoc> ")

## Register all support components
dbLoadDatabase "dbd/demo.dbd"
demo_registerRecordDeviceDriver pdbbase

## Load record instances
#dbLoadRecords("D:\development\repo\cs-studio\applications\plugins\org.csstudio.domain.desy.softioc\res\db\iocCommon.db", "APPL=TrainIoc")
#dbLoadRecords("D:/development/repo/cs-studio/applications/plugins/org.csstudio.domain.desy.softioc/res/db/iocCommon.db", "APPL=TrainIoc")
#dbLoadRecords("res/db/iocCommon.db", "APPL=TrainIoc")
dbLoadRecords("db/iocCommon.db", "APPL=TrainIoc")

# CA security
asSetFilename("asFile")

iocInit

dbpf "TrainIoc:valid","Enabled"   # from demo
