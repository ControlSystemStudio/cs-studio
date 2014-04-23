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

REQUIRE_DISPLAY=true
CODAC_ROOT=$(readlink -f "$0" | sed -n -e 's|^\(/[^/]*/[^/]*\)/.*|\1|p')

# If user args do not contain any argument starting with -
# add --launcher.openFile before to interpret them as files to open
OPEN_FILE_ARG="--launcher.openFile"
USER_ARGS=""
while [ -n "$1" ]; do
  case $1 in
    *)
      USER_ARGS="${USER_ARGS:+${USER_ARGS} }\"$1\"";
      if [[ "${1:0:1}" == "-"  ]]; then
        OPEN_FILE_ARG="";
      fi
     shift 1;;
  esac
done
if [[ USER_ARGS != "" ]]; then
  USER_ARGS="${OPEN_FILE_ARG} ${USER_ARGS}"
fi

set -- ${USER_ARGS} -share_link /opt/codac/opi=CSS/opi,/opt/codac/examples=CSS/examples,/opt/codac/opi/boy/SymbolLibrary=CSS/SymbolLibrary

. ${CODAC_ROOT}/bin/codacenv
. ${CODAC_ROOT}/bin/css-wrapper-script

