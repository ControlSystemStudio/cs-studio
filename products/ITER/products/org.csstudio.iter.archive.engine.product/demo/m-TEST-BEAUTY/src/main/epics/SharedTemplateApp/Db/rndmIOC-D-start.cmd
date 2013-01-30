cd src/main/epics/SharedTemplateApp/Db/
epicsEnvSet("ARCH","linux-x86_64")
epicsEnvSet("IOC","rndmIOC-D")
dbLoadRecords("rndm.db","IOC=D")
iocInit
