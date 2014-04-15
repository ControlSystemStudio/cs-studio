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

PROD_IOC += CWS-TCPH

# CWS-TCPH.dbd will be created and installed
DBD += CWS-TCPH.dbd

# CWS-TCPH.dbd will be made up from base.dbd and module dbds listed below:
CWS-TCPH_DBD += base.dbd

# CWS-TCPH_registerRecordDeviceDriver.cpp derives from CWS-TCPH.dbd
CWS-TCPH_SRCS += CWS-TCPH_registerRecordDeviceDriver.cpp

# Build the main IOC entry point on workstation OSs.
CWS-TCPH_SRCS_DEFAULT += CWS-TCPHMain.cpp
CWS-TCPH_SRCS_vxWorks += -nil-

#=============================
# Add support for the following uncommented modules:

# # AUTOSAVE
CWS-TCPH_DBD += asSupport.dbd
CWS-TCPH_LIBS += autosave

# # IOC Monitor
CWS-TCPH_DBD += iocmon.dbd
CWS-TCPH_LIBS += iocmon

# # System Monitor
CWS-TCPH_DBD += sysmon.dbd
CWS-TCPH_LIBS += sysmon

# # STD
CWS-TCPH_DBD += std.dbd
CWS-TCPH_LIBS += std

#=============================

# Finally link to the EPICS Base libraries
CWS-TCPH_LIBS += $(EPICS_BASE_IOC_LIBS)

#=============================

include $(TOP)/configure/RULES
#----------------------------------------
#  ADD RULES AFTER THIS LINE

