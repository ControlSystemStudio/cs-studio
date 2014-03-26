#!../../bin/linux-x86_64/CWS-TCPH
#+======================================================================
# $HeadURL$
# $Id$
#
# Project       : CODAC Core System
#
# Description   : ITER ioc template EPICS start up file
#
# Author(s)     : Cosylab
#
# Copyright (c) : 2010-2014 ITER Organization,
#                 CS 90 046
#                 13067 St. Paul-lez-Durance Cedex
#                 France
#
# This file is part of ITER CODAC software.
# For the terms and conditions of redistribution or use of this software
# refer to the file ITER-LICENSE.TXT located in the top level directory
# of the distribution package.
#
#-======================================================================

< envPaths
< envSystem
< envUser

cd "${TOP}"

#############################################
## Register all support components         ##
#############################################

dbLoadDatabase "dbd/CWS-TCPH.dbd"
CWS_TCPH_registerRecordDeviceDriver pdbbase

< "${TOP}/iocBoot/iocCWS-TCPH-PSH0SYSM/sddPreDriverConf.cmd"
< "${TOP}/iocBoot/iocCWS-TCPH-PSH0SYSM/userPreDriverConf.cmd"
< "${TOP}/iocBoot/iocCWS-TCPH-PSH0SYSM/threadSchedulingConf.cmd"
< "${TOP}/iocBoot/iocCWS-TCPH-PSH0SYSM/dbToLoad.cmd"
< "${TOP}/iocBoot/iocCWS-TCPH-PSH0SYSM/iocCWS-TCPH-PSH0SYSM-preSaveRestore.cmd"

#############################################
## IOC Logging                             ##
#############################################
iocLogInit

#############################################
## IOC initialization                      ##
#############################################
cd "${TOP}/db"
iocInit

< "${TOP}/iocBoot/iocCWS-TCPH-PSH0SYSM/iocCWS-TCPH-PSH0SYSM-postSaveRestore.cmd"
< "${TOP}/iocBoot/iocCWS-TCPH-PSH0SYSM/seqToLoad.cmd"
< "${TOP}/iocBoot/iocCWS-TCPH-PSH0SYSM/sddPostDriverConf.cmd"
< "${TOP}/iocBoot/iocCWS-TCPH-PSH0SYSM/userPostDriverConf.cmd"


