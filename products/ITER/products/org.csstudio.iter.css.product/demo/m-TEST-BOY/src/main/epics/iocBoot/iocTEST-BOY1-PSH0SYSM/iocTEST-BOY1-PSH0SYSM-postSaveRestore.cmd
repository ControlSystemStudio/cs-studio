#############################################
## Autosave monitor post setup             ##
#############################################

cd "${TOP}/iocBoot/$(IOC)"
create_monitor_set("iocTEST-BOY1-PSH0SYSM.req",30,"P=$(AUTOSAVE_SYSM_PV_PREFIX)")

