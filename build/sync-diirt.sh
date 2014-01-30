#!/bin/bash

# Go into build directory
BASEDIR=$(dirname $0)
cd $BASEDIR

HGDIR=diirt_tmp
echo Synching epics-util
rm -rf $HGDIR
hg clone http://hg.code.sf.net/p/epics-util/code $HGDIR
rsync -r --delete $HGDIR/src/main/java/org/epics/util ../core/plugins/org.epics.util/src/org/epics/
git add ../core/plugins/org.epics.util/src/org/epics/util
rm -rf $HGDIR
git commit --author="Gabriele Carcassi <gabriele.carcassi@gmail.com>" -m "org.epics.util: update to current SNAPSHOT"
echo Done epics-util
