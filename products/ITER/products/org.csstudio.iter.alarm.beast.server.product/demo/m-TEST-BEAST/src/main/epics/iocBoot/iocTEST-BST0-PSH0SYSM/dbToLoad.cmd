#======================================================================
# SYS Monitor
#======================================================================
cd $(EPICS_MODULES)/sysmon/db
dbLoadRecords("sysmon.db","CBS1=TEST, CBS2=BST0, CTRLTYPE=H, IDX=0"))



#======================================================================
# IOC Monitor
#======================================================================
cd $(EPICS_MODULES)/iocmon/db
dbLoadRecords("iocmon.db","CBS1=TEST, CBS2=BST0, CTRLTYPE=H, IDX=0, IOCTYPE=SYSM")
