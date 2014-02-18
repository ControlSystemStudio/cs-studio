#!/bin/bash

#+======================================================================
# $HeadURL$
# $Id$
#
# Project       : CODAC Core System
#
# Description   : Wrapper script for launching Alarm Notifier
#
# Author(s)     : Aurelien MARIAGE, Sopra
#                 Anze Zagar, Cosylab
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

CODAC_ROOT=$(readlink -f "$0" | sed -n -e 's|^\(/[^/]*/[^/]*\)/.*|\1|p')
. ${CODAC_ROOT}/bin/codacenv
. ${CODAC_ROOT}/bin/css-wrapper-script

