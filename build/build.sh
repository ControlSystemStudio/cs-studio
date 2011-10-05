#!/bin/bash

# Check parameters
PRODUCT=$1
BUILD="build"
if [ -z "$PRODUCT" ]
then 
  echo You must provide the product
exit -1
fi

# Clean up
rm -rf $BUILD

# Install Eclipse if not there
if [[ -d ext/eclipse ]]
then
  echo Build Target consisting of Eclipse with Deltapack alredy installed.....
else
  mkdir -p ext
  cd ext

  if [[ ! -f eclipse-rcp-indigo-linux-gtk.tar.gz ]]
    then
      wget http://ftp.osuosl.org/pub/eclipse//technology/epp/downloads/release/indigo/R/eclipse-rcp-indigo-linux-gtk.tar.gz
    fi
  if [[ ! -f eclipse-3.7RC4-delta-pack.zip ]]
  then
#    wget http://download.eclipse.org/eclipse/downloads/drops/S-3.7RC4-201106030909/eclipse-3.7RC4-delta-pack.zip
#    wget http://archive.eclipse.org/eclipse/downloads/drops/S-3.7RC3-201105261708/eclipse-3.7RC3-delta-pack.zip
     wget http://archive.eclipse.org/eclipse/downloads/drops/S-3.7RC4-201106030909/eclipse-3.7RC4-delta-pack.zip
  fi
  tar -xzvf eclipse-rcp-indigo-linux-gtk.tar.gz
  unzip -o eclipse-3.7RC4-delta-pack.zip
  cd ..
fi


# Copy product sources
cp -R ../products/$PRODUCT $BUILD
cat $BUILD/plugins.list | xargs -I {} cp -R ../core/plugins/{} $BUILD/plugins
cat $BUILD/plugins.list | xargs -I {} cp -R ../applications/plugins/{} $BUILD/plugins
cat $BUILD/features.list | xargs -I {} cp -R ../core/features/{} $BUILD/features
cat $BUILD/features.list | xargs -I {} cp -R ../applications/features/{} $BUILD/features
mkdir $BUILD/BuildDirectory
cd $BUILD
mv features BuildDirectory
mv plugins BuildDirectory
cd ..

# Run the build
# XXX Doing it in the plugin directory: it was breaking otherwise
ABSOLUTE_DIR=$PWD
echo $ABSOLUTE_DIR
java -jar "$ABSOLUTE_DIR"/ext/eclipse/plugins/org.eclipse.equinox.launcher_1.2.*.jar -application org.eclipse.ant.core.antRunner -buildfile "$ABSOLUTE_DIR"/ext/eclipse/plugins/org.eclipse.pde.build_3.7.*/scripts/productBuild/productBuild.xml -Dbuilder="$ABSOLUTE_DIR"/build -Dbuild.dir="$ABSOLUTE_DIR"

# read properties from the build.properties and set up variable for each of them
#TEMPFILE=$(mktemp)
#cat build/build.properties |grep -v "#" |  grep 'buildId=\|archivePrefix=\|launchName=' |sed -re 's/"/"/'g| sed -re 's/=(.*)/="\1"/g'>$TEMPFILE
#source $TEMPFILE
#rm $TEMPFILE
echo $buildId $archivePrefix $launchName