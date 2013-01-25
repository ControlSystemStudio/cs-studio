cd src/main/epics/SharedTemplateApp/Db/
epicsEnvSet("ARCH","linux-x86_64")
epicsEnvSet("IOC","rndmIOC-all")
dbLoadRecords("rndm.db","IOC=A")
dbLoadRecords("rndm.db","IOC=B")
dbLoadRecords("rndm.db","IOC=C")
dbLoadRecords("rndm.db","IOC=D")
iocInit
