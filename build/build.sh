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
  echo Build Target consisting of Eclipse with Deltapack already installed.....
else
  mkdir -p ext
  cd ext

  if [[ ! -f eclipse-rcp-indigo-SR2-linux-gtk.tar.gz ]]
    then
      wget http://download.eclipse.org/technology/epp/downloads/release/indigo/SR2/eclipse-rcp-indigo-SR2-linux-gtk.tar.gz
    fi
  if [[ ! -f eclipse-3.7.2-delta-pack.zip ]]
  then
#    wget http://download.eclipse.org/eclipse/downloads/drops/S-3.7RC4-201106030909/eclipse-3.7RC4-delta-pack.zip
#    wget http://archive.eclipse.org/eclipse/downloads/drops/S-3.7RC3-201105261708/eclipse-3.7RC3-delta-pack.zip
#    wget http://archive.eclipse.org/eclipse/downloads/drops/R-3.7-201106131736/eclipse-3.7-delta-pack.zip
     wget http://archive.eclipse.org/eclipse/downloads/drops/R-3.7.2-201202080800/eclipse-3.7.2-delta-pack.zip
  fi
  tar -xzvf eclipse-rcp-indigo-SR2-linux-gtk.tar.gz
  unzip -o eclipse-3.7.2-delta-pack.zip

  if [ "$PRODUCT" = "ITER" ]
  then 
    if [[ ! -f org.tigris.subclipse-site-1.6.18.zip ]]
    then
      wget -O subclipse-site-1.6.18.zip http://subclipse.tigris.org/files/documents/906/49028/site-1.6.18.zip
    fi
    unzip -o subclipse-site-1.6.18.zip -d eclipse/dropins/subclipse
  fi

  cd ..
fi

function copyIfNotExists {
  listfile=$1;
  source=$2;
  target=$3;
  for dir in `cat $listfile`
  do
    if [[ ! -d "$target/$dir" && -d "$source/$dir" ]]
    then
      cp -R $source/$dir $target
    fi
  done
}

# Copy product sources
cp -R ../products/$PRODUCT $BUILD
if [[ "$PRODUCT" = "ITER" ]]
then
  copyIfNotExists $BUILD/plugins.list ../products/$PRODUCT/products $BUILD/plugins
fi

copyIfNotExists $BUILD/plugins.list ../core/plugins $BUILD/plugins
copyIfNotExists $BUILD/plugins.list ../applications/plugins $BUILD/plugins

if [[ "$PRODUCT" = "ITER" ]]
then
  copyIfNotExists $BUILD/plugins.list ../products/DESY/plugins $BUILD/plugins
fi
copyIfNotExists $BUILD/features.list ../core/features $BUILD/features
copyIfNotExists $BUILD/features.list ../applications/features $BUILD/features

# Check if all required features and plugins was found
for dir in `cat $BUILD/plugins.list`
do
  if [[ ! -d "$BUILD/plugins/$dir" ]]
  then
    echo Plugin $dir not found.
  fi
done
for dir in `cat $BUILD/features.list`
do
  if [[ ! -d "$BUILD/features/$dir" ]]
  then
    echo Feature $dir not found.
  fi
done

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
