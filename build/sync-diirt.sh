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

echo Synching pvmanager-exec
sync_dir pvmanager $HGDIR/pvmanager-exec/src/main/java/org/epics ../core/plugins/org.csstudio.utility.pvmanager.exec/src/org/epics/
sync_dir . $HGDIR/pvmanager-exec/src/main/javadoc/org/epics/pvmanager/exec/doc-files ../core/plugins/org.csstudio.utility.pvmanager.exec/html
git commit --author="Gabriele Carcassi <gabriele.carcassi@gmail.com>" -m "o.c.u.pvmanage.exec: update to current SNAPSHOT"
echo Done pvmanager-exec

echo Synching pvmanager-file
sync_dir pvmanager $HGDIR/pvmanager-file/src/main/java/org/epics ../core/plugins/org.csstudio.utility.pvmanager.file/src/org/epics/
sync_dir . $HGDIR/pvmanager-file/src/main/javadoc/org/epics/pvmanager/file/doc-files ../core/plugins/org.csstudio.utility.pvmanager.file/html
git commit --author="Gabriele Carcassi <gabriele.carcassi@gmail.com>" -m "o.c.u.pvmanage.file: update to current SNAPSHOT"
echo Done pvmanager-file

echo Synching pvmanager-extra
# NB: We are not generating and copying the generated parser
# That will still be a manual step
sync_dir pvmanager $HGDIR/pvmanager-extra/src/main/java/org/epics ../core/plugins/org.csstudio.utility.pvmanager.extra/src/org/epics/
sync_dir . $HGDIR/pvmanager-extra/src/main/javadoc/org/epics/pvmanager/formula/doc-files ../core/plugins/org.csstudio.utility.pvmanager.extra/html
git commit --author="Gabriele Carcassi <gabriele.carcassi@gmail.com>" -m "o.c.u.pvmanage.extra: update to current SNAPSHOT"
echo Done pvmanager-extra

echo Synching pvmanager-graphene
sync_dir pvmanager $HGDIR/pvmanager-graphene/src/main/java/org/epics ../applications/plugins/org.csstudio.utility.graphene/src/org/epics/
git commit --author="Gabriele Carcassi <gabriele.carcassi@gmail.com>" -m "o.c.u.pvmanager.graphene: update to current SNAPSHOT"
echo Done pvmanager-graphene

echo Synching pvmanager-jca
sync_dir pvmanager $HGDIR/pvmanager-jca/src/main/java/org/epics ../core/plugins/org.csstudio.utility.pvmanager.epics/src/org/epics/
sync_dir . $HGDIR/pvmanager-jca/src/main/javadoc/org/epics/pvmanager/jca/doc-files ../core/plugins/org.csstudio.utility.pvmanager.epics/html
git commit --author="Gabriele Carcassi <gabriele.carcassi@gmail.com>" -m "o.c.u.pvmanage.jca: update to current SNAPSHOT"
echo Done pvmanager-jca

