#+======================================================================
# $HeadURL: https://svnpub.iter.org/codac/iter/codac/dev/units/m-epics-iter-templates/tags/CODAC-CORE-3.1.0/templates/sddconfApp/src/Makefile $
# $Id: Makefile 28651 2012-06-20 14:09:27Z zagara $
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

PROD_IOC += TEST-BTY0

# TEST-BTY0.dbd will be created and installed
DBD += TEST-BTY0.dbd

# TEST-BTY0.dbd will be made up from base.dbd and module dbds listed below:
TEST-BTY0_DBD += base.dbd

# TEST-BTY0_registerRecordDeviceDriver.cpp derives from TEST-BTY0.dbd
TEST-BTY0_SRCS += TEST-BTY0_registerRecordDeviceDriver.cpp

# Build the main IOC entry point on workstation OSs.
TEST-BTY0_SRCS_DEFAULT += TEST-BTY0Main.cpp
TEST-BTY0_SRCS_vxWorks += -nil-

#=============================
# Add support for the following uncommented modules:

# # AUTOSAVE
TEST-BTY0_DBD += asSupport.dbd
TEST-BTY0_LIBS += autosave

# # IOC Monitor
TEST-BTY0_DBD += iocmon.dbd
TEST-BTY0_LIBS += iocmon

# # System Monitor
TEST-BTY0_DBD += sysmon.dbd
TEST-BTY0_LIBS += sysmon

# # STD
TEST-BTY0_DBD += std.dbd
TEST-BTY0_LIBS += std

#=============================

# Finally link to the EPICS Base libraries
TEST-BTY0_LIBS += $(EPICS_BASE_IOC_LIBS)

#=============================

include $(TOP)/configure/RULES
#----------------------------------------
#  ADD RULES AFTER THIS LINE

