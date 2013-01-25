cd src/main/epics/SharedTemplateApp/Db/
epicsEnvSet("ARCH","linux-x86_64")
epicsEnvSet("IOC","rndmIOC-C")
dbLoadRecords("rndm.db","IOC=C")
iocInit
