#!/bin/sh
#
# Script that triggers a build of "everything"
#
# Runs on Windows with cygwin installed:
#
#  time source make.sh
#
# Kay Kasemir
source settings.sh

# Fetch new copy of sources
$ANT clean

echo Fetching sources
$ANT get_sources

#for prod in alarmconfig alarmserver archiveengine css engineconfig jms2rdb
for prod in css
do
	echo Building $prod Product
	$ECLIPSE_ANT \
	  -buildfile $ECLIPSE/plugins/org.eclipse.pde.build_$PDE_VER/scripts/productBuild/productBuild.xml \
	  -Dbuilder=$TOP/products/KEK/plugins/org.csstudio.kek.build/$prod \
	  -DbuildDirectory=$BUILDDIR \
	  -Dbase=$ECLIPSE_BASE \
	  -Dversion=$VERSION \
	  -Dqualifier=$QUALIFIER \
	  -Ddeltapack=$DELTAPACK 2>&1 | tee $prod/build.log
done

for feature in optional 
do
	echo Building $feature Features
	# Features depend on the CSS product, so they will only compile
	# after the product compiled OK
	$ECLIPSE_ANT \
	  -buildfile $ECLIPSE/plugins/org.eclipse.pde.build_$PDE_VER/scripts/build.xml \
	  -Dbuilder=$TOP/products/KEK/plugins/org.csstudio.kek.build/$feature \
	  -DbuildDirectory=$BUILDDIR \
	  -Dbase=$ECLIPSE_BASE \
	  -Dversion=$VERSION \
	  -Dqualifier=$QUALIFIER \
	  -Ddeltapack=$DELTAPACK 2>&1 | tee $feature/build.log
done

if [ $CYGWIN ]
then
# Patch headless launchers for windows
unzip  $CYGDRIVE/$BUILDDIR/I.AlarmServer_kek_$VERSION/AlarmServer_kek_$VERSION-win32.win32.x86.zip
cp $CYGDRIVE/$ECLIPSE_BASE/eclipse/eclipsec.exe  AlarmServer$VERSION/AlarmServer.exe
zip -rm $CYGDRIVE/$BUILDDIR/I.AlarmServer_kek_$VERSION/AlarmServer_kek_$VERSION-win32.win32.x86.zip AlarmServer$VERSION 

unzip  $CYGDRIVE/$BUILDDIR/I.AlarmConfigTool_kek_$VERSION/AlarmConfigTool_kek_$VERSION-win32.win32.x86.zip
cp $CYGDRIVE/$ECLIPSE_BASE/eclipse/eclipsec.exe  AlarmConfigTool$VERSION/AlarmConfigTool.exe
zip -rm $CYGDRIVE/$BUILDDIR/I.AlarmConfigTool_kek_$VERSION/AlarmConfigTool_kek_$VERSION-win32.win32.x86.zip AlarmConfigTool$VERSION 
fi

$ANT zip_sources
