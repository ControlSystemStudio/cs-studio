#############################################
## Save and restore                        ##
#############################################

### save_restore setup
# status-PV prefix
save_restoreSet_status_prefix("$(AUTOSAVE_SYSM_PV_PREFIX)")

# Use status-PV 
save_restoreSet_UseStatusPVs(1)

# Debug-output level
save_restoreSet_Debug(0)

# Ok to save/restore save sets with missing values (no CA connection to PV)?
save_restoreSet_IncompleteSetsOk(1)
# Save dated backup files?
save_restoreSet_DatedBackupFiles(1)

# Number of sequenced backup files to write
save_restoreSet_NumSeqFiles(3)
# Time interval between sequenced backups
save_restoreSet_SeqPeriodInSeconds(300)

# specify where save files should be
set_savefile_path("$(EPICS_AUTOSAVE_VAR)/$(UNIT_NAME)")

# specify what save files should be restored.  Note these files must be
# in the directory specified in set_savefile_path(), or, if that function
# has not been called, from the directory current when iocInit is invoked

# Save files associated with the request files 'auto-output.req' and
# 'auto-input.req'.  These files are the standard way to use autosave 
 
set_pass1_restoreFile("iocTEST-BST0-PSH0SYSM.sav")

# specify directories in which to to search for included request files
set_requestfile_path("./")

dbLoadRecords("$(EPICS_MODULES)/autosave/db/save_restoreStatus.db"), "P=$(AUTOSAVE_SYSM_PV_PREFIX)")
