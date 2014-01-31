#!/bin/bash

# sync_dir dir src_location dest_location
# e.g. sync_dir util $HGDIR/src/main/java/org/epics ../core/plugins/org.epics.util/src/org/epics/
function sync_dir {
    find $2/$1 -type f -exec grep -qI '' {} ';' -exec perl -pi -e 's/\r\n/\n/g' {} '+'
    rsync -r --delete $2/$1 $3
    git add $3/$1
}

# Go into build directory
BASEDIR=$(dirname $0)
cd $BASEDIR

HGDIR=diirt_tmp
rm -rf $HGDIR
hg clone http://hg.code.sf.net/p/epics-util/code $HGDIR
echo Synching epics-util
sync_dir util $HGDIR/src/main/java/org/epics ../core/plugins/org.epics.util/src/org/epics/
git commit --author="Gabriele Carcassi <gabriele.carcassi@gmail.com>" -m "org.epics.util: update to current SNAPSHOT"
echo Done epics-util

rm -rf $HGDIR
echo pvmanager repo
hg clone http://hg.code.sf.net/p/pvmanager/pvmanager $HGDIR

echo Synching epics-vtype
sync_dir vtype $HGDIR/epics-vtype/src/main/java/org/epics ../core/plugins/org.epics.vtype/src/org/epics/
git commit --author="Gabriele Carcassi <gabriele.carcassi@gmail.com>" -m "org.epics.vtype: update to current SNAPSHOT"
echo Done epics-vtype

echo Synching pvmanager-core
sync_dir pvmanager $HGDIR/pvmanager-core/src/main/java/org/epics ../core/plugins/org.csstudio.utility.pvmanager/src/org/epics/
git commit --author="Gabriele Carcassi <gabriele.carcassi@gmail.com>" -m "o.c.u.pvmanager: update to current SNAPSHOT"
echo Done pvmanager-core





