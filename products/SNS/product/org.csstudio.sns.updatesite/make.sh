#!/bin/sh
#
# Script that triggers a build of "everything"
#
# In principle, this should be an ANT build.xml,
# but the ant copy tasks were really slow compared to rsync.
# Maybe use a 'system' ant task to call rsync?
# In any case, this one is good enough for now.
#
# Kay Kasemir

source settings.sh

type javac
java -version

# Fetch new copy of sources
$ANT clean

echo Fetching sources
$ANT get_sources

PRODS="config_build_Basic_CSS config_build_SNS_CSS config_build_AlarmServer config_build_AlarmConfigTool config_build_ArchiveEngine config_build_ArchiveConfigTool config_build_JMS2RDB config_build_ScanServer"
FEATS="config_build_optional config_build_scan"

# Build products and features
for PROD in $PRODS
do
    echo $PROD

	$ECLIPSE_ANT \
	  -buildfile $ECLIPSE/plugins/org.eclipse.pde.build_$PDE_VER*/scripts/productBuild/productBuild.xml \
	  -Dbuilder=`pwd`/$PROD \
	  -DbuildDirectory=$BUILDDIR \
	  -Dversion=$VERSION \
	  -Dbase=$ECLIPSE_BASE \
	  -Ddeltapack=$DELTAPACK \
	  -Dqualifier=$QUALIFIER \
	   > $BUILDDIR/$PROD.log 2>&1
	   
	tail $BUILDDIR/$PROD.log
done

for FEAT in $FEATS
do
    echo $FEAT

	$ECLIPSE_ANT \
	  -buildfile $ECLIPSE/plugins/org.eclipse.pde.build_$PDE_VER*/scripts/build.xml \
	  -Dbuilder=`pwd`/$FEAT \
	  -DbuildDirectory=$BUILDDIR \
	  -Dversion=$VERSION \
	  -Dbase=$ECLIPSE_BASE \
	  -Ddeltapack=$DELTAPACK \
	  -Dqualifier=$QUALIFIER \
	   > $BUILDDIR/$FEAT.log 2>&1
	   
	tail $BUILDDIR/$FEAT.log
done


OK=1
# Each build log contains 2(!) "BUILD SUCCESSFUL" lines
for out in $PRODS $FEATS
do
    if [ `cat $BUILDDIR/$out.log | grep -c "BUILD SUCCESSFUL"` -eq 2 ]
    then
        echo OK: $out
    else
        echo Build failed: $out
        OK=0
    fi
done

if [ $OK = 1 ]
then
    echo Collecting ZIP files
    mkdir -p $BUILDDIR/apps
    
    ## Basic EPICS
    sh patch_product.sh I.epics_css_$VERSION/epics_css_$VERSION-macosx.cocoa.x86.zip    CSS_EPICS_$VERSION apps/epics_css_$VERSION-macosx.cocoa.x86.zip
    sh patch_product.sh I.epics_css_$VERSION/epics_css_$VERSION-linux.gtk.x86.zip       CSS_EPICS_$VERSION apps/epics_css_$VERSION-linux.gtk.x86.zip
    sh patch_product.sh I.epics_css_$VERSION/epics_css_$VERSION-linux.gtk.x86_64.zip    CSS_EPICS_$VERSION apps/epics_css_$VERSION-linux.gtk.x86_64.zip
    sh patch_product.sh I.epics_css_$VERSION/epics_css_$VERSION-win32.win32.x86.zip     CSS_EPICS_$VERSION apps/epics_css_$VERSION-win32.win32.x86.zip
    sh patch_product.sh I.epics_css_$VERSION/epics_css_$VERSION-win32.win32.x86_64.zip  CSS_EPICS_$VERSION apps/epics_css_$VERSION-win32.win32.x86_64.zip  $JRE_Win64

    ## SNS CSS
    sh patch_product.sh I.sns_css_$VERSION/sns_css_$VERSION-macosx.cocoa.x86.zip        CSS_$VERSION       apps/sns_css_$VERSION-macosx.cocoa.x86.zip
	sh patch_product.sh I.sns_css_$VERSION/sns_css_$VERSION-linux.gtk.x86.zip           CSS_$VERSION       apps/sns_css_$VERSION-linux.gtk.x86.zip
	sh patch_product.sh I.sns_css_$VERSION/sns_css_$VERSION-linux.gtk.x86_64.zip        CSS_$VERSION       apps/sns_css_$VERSION-linux.gtk.x86_64.zip
	sh patch_product.sh I.sns_css_$VERSION/sns_css_$VERSION-win32.win32.x86.zip         CSS_$VERSION       apps/sns_css_$VERSION-win32.win32.x86.zip
	sh patch_product.sh I.sns_css_$VERSION/sns_css_$VERSION-win32.win32.x86_64.zip      CSS_$VERSION       apps/sns_css_$VERSION-win32.win32.x86_64.zip    $JRE_Win64

    ## 'Simple' products (headless) that are not patched
    mv $BUILDDIR/I.alarm_server_$VERSION/alarm_server_$VERSION* $BUILDDIR/apps
    mv $BUILDDIR/I.alarm_config_$VERSION/alarm_config_$VERSION* $BUILDDIR/apps
    mv $BUILDDIR/I.archive_engine_$VERSION/archive_engine_$VERSION* $BUILDDIR/apps
    mv $BUILDDIR/I.archive_config_$VERSION/archive_config_$VERSION* $BUILDDIR/apps
    mv $BUILDDIR/I.jms2rdb_$VERSION/jms2rdb_$VERSION* $BUILDDIR/apps
    mv $BUILDDIR/I.scan_server_$VERSION/scan_server_$VERSION* $BUILDDIR/apps

    ## Optional features are already in buildRepo

    ## Source code
    $ANT zip_sources
fi

