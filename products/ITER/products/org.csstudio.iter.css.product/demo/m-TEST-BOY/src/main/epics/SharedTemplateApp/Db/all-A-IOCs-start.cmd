cd src/main/epics/SharedTemplateApp/Db/
epicsEnvSet("ARCH","linux-x86_64")
epicsEnvSet("IOC","rndmIOC-A")
dbLoadTemplate("switch-state.substitutions")
dbLoadTemplate("rndm.substitutions")
dbLoadTemplate("ramp.substitutions")
iocInit
