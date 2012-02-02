# Headless build of SNS CSS
#
# Based on info from Jan Hatje and
# http://www.vogella.de/articles/EclipsePDEBuild/article.html
#
# Kay Kasemir

source ../settings.sh

java -jar $ECLIPSE/plugins/org.eclipse.equinox.launcher_*.jar \
  -application org.eclipse.ant.core.antRunner \
  -buildfile $ECLIPSE/plugins/org.eclipse.pde.build_$PDE_VER*/scripts/productBuild/productBuild.xml \
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
say "I think I'm done building CSS for the S N S"

