#!/bin/bash
#+======================================================================
# $HeadURL: https://svnpub.iter.org/codac/iter/codac/dev/units/m-maven-iter-plugin/tags/CODAC-CORE-3.1.0/src/main/resources/test_template.sh $
# $Id: test_template.sh 25208 2012-02-07 10:17:51Z zagara $
#
# Project       : CODAC Core System
#
# Description   : Test script
#
# Author        : Cosylab
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

#######################################################
# 1.
# Rename this file to an appropriate name.
# The file name must end with ".sh".
# 
#######################################################

TEST_SCRIPT_DIR=`dirname $0`
. ${TEST_SCRIPT_DIR}/util.sh

echo "***** Starting (test name) *****"

error=0

#######################################################
# 2.
# The main test logic must be written here.
#
# Anytime an unrecoverable error occurs, this shell
# script can give up by exiting with non-zero value.
# 
# When a sustainable error occurs, "error" variable
# should be set to non-zero value and this script can
# continue executing test procedure.
# 
# The example is shown below.
#######################################################
#caput ABC:XXX 10 || exit 1
#
#sleep 1
#
#epics_pv_eq ABC:XXX 10 || error=1
#epics_pv_not_eq ABC:XXX 11 || error=1

echo "***** End of (test name) *****"

#######################################################
# 3.
# Each test script must exit with 0 if there is no
# error, otherwise this must exit with non-zero value.
#######################################################
exit ${error}
