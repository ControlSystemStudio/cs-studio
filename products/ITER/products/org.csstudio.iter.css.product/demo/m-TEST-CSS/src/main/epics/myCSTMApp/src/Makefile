#+======================================================================
# $HeadURL$
# $Id$
#
# Project       : CODAC Core System
#
# Description   : Makefile
#
# Author(s)     : Anze Zagar
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
TOP=../..


include $(TOP)/configure/CONFIG
#----------------------------------------
#  ADD MACRO DEFINITIONS AFTER THIS LINE

#=============================
# Build the IOC support library

PROD_IOC += myCSTM

# myCSTM.dbd will be created and installed
DBD += myCSTM.dbd

# myCSTM.dbd will be made up from base.dbd and module dbds listed below:
myCSTM_DBD += base.dbd

# myCSTM_registerRecordDeviceDriver.cpp derives from myCSTM.dbd
myCSTM_SRCS += myCSTM_registerRecordDeviceDriver.cpp

# Build the main IOC entry point on workstation OSs.
myCSTM_SRCS_DEFAULT += myCSTMMain.cpp
myCSTM_SRCS_vxWorks += -nil-


# Add support from base/src/vxWorks if needed
#RS-CTRL_OBJS_vxWorks += $(EPICS_BASE_BIN)/vxComLibrary

#=============================
# Add support for the following uncommented modules:

# # AUTOSAVE
myCSTM_DBD += asSupport.dbd
myCSTM_LIBS += autosave

# # IOC Monitor
myCSTM_DBD += iocmon.dbd
myCSTM_LIBS += iocmon

# # System Monitor
myCSTM_DBD += sysmon.dbd
myCSTM_LIBS += sysmon

# # ASYN
#myCSTM_DBD += asyn.dbd
#myCSTM_LIBS += asyn

# # NI SYNC
#myCSTM_DBD += nisync.dbd
#myCSTM_LIBS += nisync-epics

# # NI SYNC General Time
#myCSTM_DBD += nisync-generalTime.dbd
#myCSTM_LIBS += nisync-generalTime

# # NI PXI-6259
#myCSTM_DBD += ni6259.dbd
#myCSTM_LIBS += ni6259

# # S7PLC
#myCSTM_DBD += s7plc.dbd
#myCSTM_LIBS += s7plc

# # SNCSEQ
#sncProgram_SNCFLAGS += +r -c +d
#myCSTM_DBD += sncProgram.dbd
#myCSTM_SRCS += sncProgram.stt
#myCSTM_LIBS += seq pv

myControlStateMachine_SNCFLAGS += +r -c +d
myCSTM_DBD += myControlStateMachine.dbd
myCSTM_SRCS += myControlStateMachine.st
myCSTM_LIBS += seq pv

#=============================

# Finally link to the EPICS Base libraries
myCSTM_LIBS += $(EPICS_BASE_IOC_LIBS)

#=============================

include $(TOP)/configure/RULES
#----------------------------------------
#  ADD RULES AFTER THIS LINE

