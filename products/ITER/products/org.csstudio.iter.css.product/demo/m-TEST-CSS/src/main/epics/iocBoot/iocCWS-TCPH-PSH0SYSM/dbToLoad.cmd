#======================================================================
# SYS Monitor
#======================================================================
cd $(EPICS_MODULES)/sysmon/db
dbLoadRecords("sysmon.db","CBS1=CWS, CBS2=TCPH, CTRLTYPE=H, IDX=0"))


#======================================================================
# IOC Monitor
#======================================================================
cd $(EPICS_MODULES)/iocmon/db
dbLoadRecords("iocmon.db","CBS1=CWS, CBS2=TCPH, CTRLTYPE=H, IDX=0, IOCTYPE=SYSM")
