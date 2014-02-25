#+======================================================================
# $HeadURL$
# $Id$
#
# Project       : CODAC Core System
#
# Description   : Test utilities
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
# Obtain the value of the specified PV by using
# "caget" and check if the value is equal to the
# specified value.
#
# ARGUMENT
#  $1: PV name
#  $2: Expected value.
#
# RETURN
#   If the obtained values is equal to the specified
#   value, this function returns 1, otherwise it
#   returns 0.
#######################################################
function epics_pv_eq(){
    res=`caget $1 | sed "s/$1\\s*//"`
    if [ "$res" = "$2" ]; then
	echo "$1 = $2"
	return 0
    else
	echo "ERROR: $1 != $2"
	return 1
    fi
}

#######################################################
# Obtain the value of the specified PV by using
# "caget" and check if the value is NOT equal to the
# specified value.
#
# ARGUMENT
#  $1: PV name
#  $2: Nonexpected value.
#
# RETURN
#   If the obtained values is NOT equal to the
#   specified value, this function returns 1,
#   otherwise it returns 0.
#######################################################
function epics_pv_not_eq(){
    res=`caget $1 | sed "s/$1\\s*//"`
    if [ "$res" = "$2" ]; then
	echo "ERROR: $1 = $2"
	return 1
    else
	echo "$1 != $2"
	return 0
    fi
}

#######################################################
# Obtain the value of the specified PV by using
# "caget" and check if the value is between two
# specified values.
#
# ARGUMENT
#  $1: PV name
#  $2: Lower bound
#  $3: Upper bound
#
# RETURN
#   If the obtained values is between two specified
#   values, this function returns 1, otherwise it
#   returns 0.
#######################################################
function epics_pv_between(){
    res=`caget $1 | sed "s/$1\\s*//"`
    ret_exp=`echo "$2 < $res && $res < $3" | bc`
    if [ "$ret_exp" = "1" ]; then
	echo "$1 is between $2 and $3."
	return 0
    else
	echo "ERROR: $1 is not between $2 and $3."
	echo "ERROR: $1 is currently $res."
	return 1
    fi
}
