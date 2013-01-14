# 2010-11-15, DESY - jp
#
# Initialization
dbLoadDatabase("dbd/iocShell.dbd",0,0)
iocShell_registerRecordDeviceDriver(pdbbase)
epicsEnvSet(IOCSH_PS1,"SoftIoc> ")

# Loading the demo databases
dbLoadRecords("demo/AlarmDemo.db")

# start
iocInit()
