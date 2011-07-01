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

# Fetch new copy of sources
$ANT clean

echo Fetching sources
$ANT get_sources

for prod in css archiveengine
do
	echo Building $prod Product
	java -jar $ECLIPSE/plugins/org.eclipse.equinox.launcher_*.jar \
	  -application org.eclipse.ant.core.antRunner \
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
	java -jar $ECLIPSE/plugins/org.eclipse.equinox.launcher_*.jar \
	  -application org.eclipse.ant.core.antRunner \
	  -buildfile $ECLIPSE/plugins/org.eclipse.pde.build_$PDE_VER/scripts/build.xml \
	  -Dbuilder=$TOP/products/KEK/plugins/org.csstudio.kek.build/$feature \
	  -DbuildDirectory=$BUILDDIR \
	  -Dbase=$ECLIPSE_BASE \
	  -Dversion=$VERSION \
	  -Dqualifier=$QUALIFIER \
	  -Ddeltapack=$DELTAPACK 2>&1 | tee $feature/build.log
done
 