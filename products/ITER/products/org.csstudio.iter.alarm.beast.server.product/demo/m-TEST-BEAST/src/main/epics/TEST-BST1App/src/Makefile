#+======================================================================
# $HeadURL: https://svnpub.iter.org/codac/iter/codac/dev/units/m-epics-iter-templates/branches/codac-core-4.0/templates/sddconfApp/src/Makefile $
# $Id: Makefile 33491 2013-01-20 18:21:08Z zagara $
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

PROD_IOC += TEST-BST1

# TEST-BST1.dbd will be created and installed
DBD += TEST-BST1.dbd

# TEST-BST1.dbd will be made up from base.dbd and module dbds listed below:
TEST-BST1_DBD += base.dbd

# TEST-BST1_registerRecordDeviceDriver.cpp derives from TEST-BST1.dbd
TEST-BST1_SRCS += TEST-BST1_registerRecordDeviceDriver.cpp

# Build the main IOC entry point on workstation OSs.
TEST-BST1_SRCS_DEFAULT += TEST-BST1Main.cpp
TEST-BST1_SRCS_vxWorks += -nil-

#=============================
# Add support for the following uncommented modules:

# # AUTOSAVE
TEST-BST1_DBD += asSupport.dbd
TEST-BST1_LIBS += autosave

# # IOC Monitor
TEST-BST1_DBD += iocmon.dbd
TEST-BST1_LIBS += iocmon

# # System Monitor
TEST-BST1_DBD += sysmon.dbd
TEST-BST1_LIBS += sysmon

# # STD
TEST-BST1_DBD += std.dbd
TEST-BST1_LIBS += std

#=============================

# Finally link to the EPICS Base libraries
TEST-BST1_LIBS += $(EPICS_BASE_IOC_LIBS)

#=============================

include $(TOP)/configure/RULES
#----------------------------------------
#  ADD RULES AFTER THIS LINE

