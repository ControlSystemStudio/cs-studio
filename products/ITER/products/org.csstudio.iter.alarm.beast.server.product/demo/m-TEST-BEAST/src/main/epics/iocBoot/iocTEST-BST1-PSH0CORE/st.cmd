#!../../bin/linux-x86_64/TEST-BST1
#+======================================================================
# $HeadURL: https://svnpub.iter.org/codac/iter/codac/dev/units/m-epics-iter-templates/branches/codac-core-4.0/templates/genericBoot/ioc/st.cmd $
# $Id: st.cmd 33491 2013-01-20 18:21:08Z zagara $
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

dbLoadDatabase "dbd/TEST-BST1.dbd"
TEST_BST1_registerRecordDeviceDriver pdbbase

< "${TOP}/iocBoot/iocTEST-BST1-PSH0CORE/sddPreDriverConf.cmd"
< "${TOP}/iocBoot/iocTEST-BST1-PSH0CORE/userPreDriverConf.cmd"
< "${TOP}/iocBoot/iocTEST-BST1-PSH0CORE/threadSchedulingConf.cmd"
< "${TOP}/iocBoot/iocTEST-BST1-PSH0CORE/dbToLoad.cmd"
< "${TOP}/iocBoot/iocTEST-BST1-PSH0CORE/iocTEST-BST1-PSH0CORE-preSaveRestore.cmd"

#############################################
## IOC Logging                             ##
#############################################
iocLogInit

#############################################
## IOC initialization                      ##
#############################################
cd "${TOP}/db"
iocInit

< "${TOP}/iocBoot/iocTEST-BST1-PSH0CORE/iocTEST-BST1-PSH0CORE-postSaveRestore.cmd"
< "${TOP}/iocBoot/iocTEST-BST1-PSH0CORE/sddSeqToLoad.cmd"
< "${TOP}/iocBoot/iocTEST-BST1-PSH0CORE/seqToLoad.cmd"
< "${TOP}/iocBoot/iocTEST-BST1-PSH0CORE/sddPostDriverConf.cmd"
< "${TOP}/iocBoot/iocTEST-BST1-PSH0CORE/userPostDriverConf.cmd"


