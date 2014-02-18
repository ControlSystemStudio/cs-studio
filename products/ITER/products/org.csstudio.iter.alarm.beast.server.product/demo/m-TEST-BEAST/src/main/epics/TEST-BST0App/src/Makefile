#+======================================================================
# $HeadURL: https://svnpub.iter.org/codac/iter/codac/dev/units/m-epics-iter-templates/tags/CODAC-CORE-4.0B5/templates/sddconfApp/src/Makefile $
# $Id: Makefile 33482 2013-01-18 17:09:46Z zagara $
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

PROD_IOC += TEST-BST0

# TEST-BST0.dbd will be created and installed
DBD += TEST-BST0.dbd

# TEST-BST0.dbd will be made up from base.dbd and module dbds listed below:
TEST-BST0_DBD += base.dbd

# TEST-BST0_registerRecordDeviceDriver.cpp derives from TEST-BST0.dbd
TEST-BST0_SRCS += TEST-BST0_registerRecordDeviceDriver.cpp

# Build the main IOC entry point on workstation OSs.
TEST-BST0_SRCS_DEFAULT += TEST-BST0Main.cpp
TEST-BST0_SRCS_vxWorks += -nil-

#=============================
# Add support for the following uncommented modules:

# # AUTOSAVE
TEST-BST0_DBD += asSupport.dbd
TEST-BST0_LIBS += autosave

# # IOC Monitor
TEST-BST0_DBD += iocmon.dbd
TEST-BST0_LIBS += iocmon

# # System Monitor
TEST-BST0_DBD += sysmon.dbd
TEST-BST0_LIBS += sysmon

# # STD
TEST-BST0_DBD += std.dbd
TEST-BST0_LIBS += std

#=============================

# Finally link to the EPICS Base libraries
TEST-BST0_LIBS += $(EPICS_BASE_IOC_LIBS)

#=============================

include $(TOP)/configure/RULES
#----------------------------------------
#  ADD RULES AFTER THIS LINE

