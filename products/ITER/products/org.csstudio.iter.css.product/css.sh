#!/bin/bash

#+======================================================================
# $HeadURL: https://svnpub.iter.org/codac/iter/codac/dev/units/m-css-iter/trunk/org.csstudio.iter.css.product/css.sh $
# $Id: css.sh 31100 2012-10-23 20:17:15Z zagara $
#
# Project       : CODAC Core System
#
# Description   : Wrapper script for launching CSS
#
# Author(s)     : Takashi Nakamoto, Cosylab
#                 Anze Zagar, Cosylab
#
# Copyright (c) : 2010-2012 ITER Organization,
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

REQUIRE_DISPLAY=true
CODAC_ROOT=$(readlink -f "$0" | sed -n -e 's|^\(/[^/]*/[^/]*\)/.*|\1|p')
set -- "${@}" -share_link /opt/codac/opi=CSS/opi,/opt/codac/examples=CSS/examples,/opt/codac/opi/boy/SymbolLibrary=CSS/SymbolLibrary
. ${CODAC_ROOT}/bin/codacenv
. ${CODAC_ROOT}/bin/css-wrapper-script

