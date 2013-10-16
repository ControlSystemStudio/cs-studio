#############################################
## Autosave monitor post setup             ##
#############################################

cd "${TOP}/iocBoot/$(IOC)"
create_monitor_set("iocCWS-TCPH-PSH0CORE.req",30,"P=$(AUTOSAVE_SYSM_PV_PREFIX)")

