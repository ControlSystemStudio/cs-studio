
#======================================================================
# Loading Substitution Files 
#======================================================================
cd $(TOP)/iocBoot/$(IOC)
dbLoadTemplate("PSH0-rndm_v1.substitution")




#======================================================================
# IOC Monitor
#======================================================================
cd $(EPICS_MODULES)/iocmon/db
dbLoadRecords("iocmon.db","CBS1=TEST, CBS2=BTY1, CTRLTYPE=H, IDX=0, IOCTYPE=CORE")
