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
  echo Eclipse alredy installed
else
  mkdir -p ext
  cd ext
  if [[ ! -f eclipse-rcp-galileo-SR2-linux-gtk.tar.gz ]]
    then
    wget http://carroll.aset.psu.edu/pub/eclipse/technology/epp/downloads/release/galileo/SR2/eclipse-rcp-galileo-SR2-linux-gtk.tar.gz
    fi
  if [[ ! -f eclipse-3.5.2-delta-pack.zip ]]
  then
    wget http://archive.eclipse.org/eclipse/downloads/drops/R-3.5.2-201002111343/eclipse-3.5.2-delta-pack.zip
  fi
  tar -xzvf eclipse-rcp-galileo-SR2-linux-gtk.tar.gz
  unzip -o eclipse-3.5.2-delta-pack.zip
  cd ..
fi

# Copy product sources
cp -R ../products/$PRODUCT $BUILD
cat $BUILD/plugins.list | xargs -i cp -R ../core/plugins/{} $BUILD/plugins
cat $BUILD/plugins.list | xargs -i cp -R ../applications/plugins/{} $BUILD/plugins
cat $BUILD/features.list | xargs -i cp -R ../applications/features/{} $BUILD/features
mkdir $BUILD/BuildDirectory
cd $BUILD
mv features BuildDirectory
mv plugins BuildDirectory
cd ..

# Run the build
# XXX Doing it in the plugin directory: it was breaking otherwise
ABSOLUTE_DIR=$PWD
echo $ABSOLUTE_DIR
java -jar "$ABSOLUTE_DIR"/ext/eclipse/plugins/org.eclipse.equinox.launcher_1.0.201.R35x_v20090715.jar -application org.eclipse.ant.core.antRunner -buildfile "$ABSOLUTE_DIR"/ext/eclipse/plugins/org.eclipse.pde.build_3.5.2.R35x_20100114/scripts/productBuild/productBuild.xml -Dbuilder="$ABSOLUTE_DIR"/build -Dbuild.dir="$ABSOLUTE_DIR"

#cd ext/eclipse/plugins/org.eclipse.pde.build_3.5.2.R35x_20100114/scripts/productBuild
#java -jar ../../../../../../ext/eclipse/plugins/org.eclipse.equinox.launcher_1.0.201.R35x_v20090715.jar -application org.eclipse.ant.core.antRunner -buildfile productBuild.xml -Dbuilder="$ABSOLUTE_DIR/$BUILD" -Dbuild.dir="$ABSOLUTE_DIR"

