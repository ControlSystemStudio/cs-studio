cd src/main/epics/SharedTemplateApp/Db/
epicsEnvSet("ARCH","linux-x86_64")
epicsEnvSet("IOC","rndmIOC-A")
dbLoadRecords("rndm.db","IOC=A")
iocInit
