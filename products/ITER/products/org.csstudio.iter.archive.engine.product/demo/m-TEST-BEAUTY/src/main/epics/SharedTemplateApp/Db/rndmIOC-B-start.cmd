cd src/main/epics/SharedTemplateApp/Db/
epicsEnvSet("ARCH","linux-x86_64")
epicsEnvSet("IOC","rndmIOC-B")
dbLoadRecords("rndm.db","IOC=B")
iocInit
