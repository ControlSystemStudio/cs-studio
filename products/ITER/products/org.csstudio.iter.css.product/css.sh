#!/bin/bash

#+======================================================================
# $HeadURL: https://svnpub.iter.org/codac/iter/codac/dev/units/m-css/trunk/products/ITER/products/org.csstudio.iter.css.product/css.sh $
# $Id: css.sh 50265 2014-09-30 11:32:47Z zagara $
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
OPEN_FILES=()
USER_ARGS=()
FILELIST=true
while [ -n "$1" ]; do
  case $1 in
    --launcher.openFile)
      FILELIST=true
      ;;
    -*)
      USER_ARGS=("${USER_ARGS[@]}" "$1")
      FILELIST=false
      ;;
    *)
      if $FILELIST; then
        OPEN_FILES=("${OPEN_FILES[@]}" "$(readlink -fm "$1")")
      else 
        USER_ARGS=("${USER_ARGS[@]}" "$1")
      fi
      ;;
  esac
  shift 1
done
if [ ${#OPEN_FILES[@]} -gt 0 ]; then
  USER_ARGS=("${USER_ARGS[@]}" "--launcher.openFile" "${OPEN_FILES[@]}")
fi
set -- -share_link /opt/codac/opi=CSS/opi,/opt/codac/examples=CSS/examples,/opt/codac/opi/boy/SymbolLibrary=CSS/SymbolLibrary "${USER_ARGS[@]}"

. ${CODAC_ROOT}/bin/codacenv
. ${CODAC_ROOT}/bin/css-wrapper-script

