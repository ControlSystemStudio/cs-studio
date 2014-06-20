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

PROD_IOC += SharedTemplate

# SharedTemplate.dbd will be created and installed
DBD += SharedTemplate.dbd

# SharedTemplate.dbd will be made up from base.dbd and module dbds listed below:
SharedTemplate_DBD += base.dbd

# SharedTemplate_registerRecordDeviceDriver.cpp derives from SharedTemplate.dbd
SharedTemplate_SRCS += SharedTemplate_registerRecordDeviceDriver.cpp

# Build the main IOC entry point on workstation OSs.
SharedTemplate_SRCS_DEFAULT += SharedTemplateMain.cpp
SharedTemplate_SRCS_vxWorks += -nil-

#=============================
# Add support for the following uncommented modules:

# # AUTOSAVE
SharedTemplate_DBD += asSupport.dbd
SharedTemplate_LIBS += autosave

# # IOC Monitor
SharedTemplate_DBD += iocmon.dbd
SharedTemplate_LIBS += iocmon

# # System Monitor
SharedTemplate_DBD += sysmon.dbd
SharedTemplate_LIBS += sysmon

# # STD
SharedTemplate_DBD += std.dbd
SharedTemplate_LIBS += std

#=============================

# Finally link to the EPICS Base libraries
SharedTemplate_LIBS += $(EPICS_BASE_IOC_LIBS)

#=============================

include $(TOP)/configure/RULES
#----------------------------------------
#  ADD RULES AFTER THIS LINE

