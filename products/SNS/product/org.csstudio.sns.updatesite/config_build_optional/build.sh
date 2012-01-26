# Headless build of SNS's optional CSS features
#
# Based on info from Jan Hatje and
# http://www.vogella.de/articles/EclipsePDEBuild/article.html
#
# Kay Kasemir

source ../settings.sh

# Build _feature_ using build.xml, not productBuild.xml
# Add  -verbose option after antRunner to get truckload of meaningless info
java -jar $ECLIPSE/plugins/org.eclipse.equinox.launcher_*.jar \
  -application org.eclipse.ant.core.antRunner \
  -buildfile $ECLIPSE/plugins/org.eclipse.pde.build_$PDE_VER*/scripts/build.xml \
  -Dbuilder=`pwd` \
  -DbuildDirectory=$BUILDDIR \
  -Dversion=$VERSION \
  -Dbase=$ECLIPSE_BASE \
  -Ddeltapack=$DELTAPACK \
  -Dqualifier=$QUALIFIER \
   > $BUILDDIR/$PROD.log 2>&1
   
tail $BUILDDIR/$PROD.log

# Cute way to signal we're done
# play /usr/share/system-config-soundcard/sound-sample.wav
say "I think I'm done building optional features"

