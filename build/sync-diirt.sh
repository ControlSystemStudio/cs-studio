#!/bin/bash

# Go into build directory
BASEDIR=$(dirname $0)
cd $BASEDIR

HGDIR=diirt_tmp
rm -rf $HGDIR
hg clone http://hg.code.sf.net/p/epics-util/code $HGDIR
echo Synching epics-util
rsync -r --delete $HGDIR/src/main/java/org/epics/util ../core/plugins/org.epics.util/src/org/epics/
git add ../core/plugins/org.epics.util/src/org/epics/util
git commit --author="Gabriele Carcassi <gabriele.carcassi@gmail.com>" -m "org.epics.util: update to current SNAPSHOT"
echo Done epics-util

rm -rf $HGDIR
hg clone http://hg.code.sf.net/p/pvmanager/pvmanager $HGDIR
echo Synching pvmanager-core
rsync -r --delete $HGDIR/pvmanager-core/src/main/java/org/epics/pvmanager ../core/plugins/org.csstudio.utility.pvmanager/src/org/epics/
git add ../core/plugins/org.csstudio.utility.pvmanager/src/org/epics/pvmanager
git commit --author="Gabriele Carcassi <gabriele.carcassi@gmail.com>" -m "o.c.u.pvmanager: update to current SNAPSHOT"
echo Done pvmanager-core
