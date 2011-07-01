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

echo Building Products
java -jar $ECLIPSE/plugins/org.eclipse.equinox.launcher_*.jar \
  -application org.eclipse.ant.core.antRunner \
  -buildfile $ECLIPSE/plugins/org.eclipse.pde.build_$PDE_VER/scripts/productBuild/productBuild.xml \
  -Dbuilder=$TOP/products/KEK/plugins/org.csstudio.kek.build/css \
  -DbuildDirectory=$BUILDDIR \
  -Dbase=$ECLIPSE_BASE \
  -Dversion=$VERSION \
  -Dqualifier=$QUALIFIER \
  -Ddeltapack=$DELTAPACK 2>&1 | tee css/build.log

echo Building Features
# Features depend on the CSS product, so they will only compile
# after the product compiled OK
java -jar $ECLIPSE/plugins/org.eclipse.equinox.launcher_*.jar \
  -application org.eclipse.ant.core.antRunner \
  -buildfile $ECLIPSE/plugins/org.eclipse.pde.build_$PDE_VER/scripts/build.xml \
  -Dbuilder=$TOP/products/KEK/plugins/org.csstudio.kek.build/optional \
  -DbuildDirectory=$BUILDDIR \
  -Dbase=$ECLIPSE_BASE \
  -Dversion=$VERSION \
  -Dqualifier=$QUALIFIER \
  -Ddeltapack=$DELTAPACK 2>&1 | tee optional/build.log

 