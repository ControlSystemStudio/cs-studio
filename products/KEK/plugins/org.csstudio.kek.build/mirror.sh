#!/bin/sh
#
# Script to merge one P2 repository to another.
# This script is based on the script showen in this document:
#  http://cs-studio.sourceforge.net/docbook/ch17.html
#
# Author:
#  - Takashi Nakamoto (Cosylab)
#

SOURCE=$1
DEST=$2

SCRIPTDIR=$(cd $(dirname $0) && pwd)

source settings.sh

# 1. Merge the metadata (content.xml)
java -jar $ECLIPSE/plugins/org.eclipse.equinox.launcher_*.jar \
 -application org.eclipse.equinox.p2.metadata.repository.mirrorApplication \
 -source $SOURCE -destination $DEST

# 2. Merge the artifcats (plugins, features, binary and artifacts.xml)
java -jar $ECLIPSE/plugins/org.eclipse.equinox.launcher_*.jar \
 -application org.eclipse.equinox.p2.artifact.repository.mirrorApplication \
 -verbose \
 -compare \
 -source $SOURCE -destination $DEST

