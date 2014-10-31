#!/bin/bash

# Check parameters
ORGANIZATION="$1"
WARPRODUCT="$2"

BUILD="build"
if [ -z "$ORGANIZATION" ]
then 
  echo You must provide the ORGANIZATION
  exit -1
fi
if [ -z "$WARPRODUCT" ]
then 
  echo "You must provide the WARPRODUCT file path"
  exit -1
fi

# If WARPRODUCT end with .path, the file only contains the path to the real war product file
if [ `echo "$WARPRODUCT" | grep "\.path$"` ]; then
  pathToWarProduct=`cat "$WARPRODUCT"`
  warProductDir=`dirname "$WARPRODUCT"`
  WARPRODUCT=`readlink -f "$warProductDir/$pathToWarProduct"`
fi
if [ ! -f "$WARPRODUCT" ]
then 
  echo "File '$WARPRODUCT' not found."
  exit -1
fi

# Clean up
rm -rf "$BUILD"

# Install Eclipse if not there
if [[ -d ext/eclipse ]]
then
  echo Build Target consisting of Eclipse with Deltapack already installed.....
else
  mkdir -p ext
  cd ext

  if [ `uname` == 'Linux' ]
  then
    if [ `uname -m` == 'x86_64' ]
    then
      if [[ ! -f eclipse-rcp-luna-R-linux-gtk-x86_64.tar.gz ]]
      then
        wget http://download.eclipse.org/technology/epp/downloads/release/luna/R/eclipse-rcp-luna-R-linux-gtk-x86_64.tar.gz
      fi
    else
      if [[ ! -f eclipse-rcp-luna-R-linux-gtk.tar.gz ]]
      then
        wget http://download.eclipse.org/technology/epp/downloads/release/luna/R/eclipse-rcp-luna-R-linux-gtk.tar.gz
      fi
    fi
  else
    echo "Missing Eclipse, only know locations for Linux"
    exit 1
  fi
  if [[ ! -f eclipse-4.4-delta-pack.zip ]]
  then
     wget http://download.eclipse.org/eclipse/downloads/drops4/R-4.4-201406061215/eclipse-4.4-delta-pack.zip
  fi
  tar -xzvf eclipse-rcp-luna*.tar.gz
  unzip -o eclipse-*-delta-pack.zip
  #https://bugs.eclipse.org/bugs/show_bug.cgi?id=438652
  cp ../org.eclipse.osgi.compatibility.state_1.0.0.v20140403-1907.jar eclipse/plugins/
  cd ..
fi

# Install Eclipse rap runtime if not there
if [[ -d ext/eclipse-rap ]]
then
  echo Build Target consisting of Eclipse RAP Runtime
else
  mkdir -p ext/eclipse-rap
  cd ext

  if [[ ! -f rap-2.3.0-R-20140610-0925.zip ]]
  then
     wget http://download.eclipse.org/rt/rap/2.3/rap-2.3.0-R-20140610-0925.zip
  fi
  unzip -o rap-2.3.0-R-20140610-0925.zip -d eclipse-rap
  find eclipse-rap -name 'org.eclipse.rap.rwt.testfixture*.jar' -exec rm -f {} \;
  cp -r ../../core/plugins/org.csstudio.rap.core/third_party_plugins/RAP_2.3_GEF/* eclipse-rap/plugins/
  cp -r ../../core/plugins/org.csstudio.rap.core/third_party_plugins/JUnit/* eclipse-rap/plugins/
  # Install BATIK in plugins directory
  unzip -o ../batik-1.7.zip -d eclipse-rap/
  cd ..
fi

function copyIfNotExists {
  pluginlist="$1";
  source="$2";
  target="$3";
  for dir in $pluginlist
  do
    if [[ ! -d "$target/$dir" && -d "$source/$dir" ]]
    then
      cp -R "$source/$dir" "$target"
    fi
  done
}

mkdir $BUILD

ORGANIZATION_tolower=`echo $ORGANIZATION | awk '{print tolower($0)}'`
warproductname=`grep '<product' "$WARPRODUCT" | sed -e 's/.*name="\([^"]*\)".*/\1/' | awk '{print tolower($0)}'`
featureid=org.csstudio.${ORGANIZATION_tolower}.${warproductname}.feature

pluginlist=`grep '<plugin id=".*"/>' "$WARPRODUCT" | sed -e 's,.*<plugin id="\([^"]*\)".*,\1,g'`
pluginlist=`echo $pluginlist | sed -e 's,org.csstudio.platform.libs.epics.macosx ,,g'`
pluginlist=`echo $pluginlist | sed -e 's,org.csstudio.platform.libs.epics.win32 ,,g'`

pluginlistwithunpack=`echo $pluginlist | sed -e 's, ,;unpack=false\,,g'`
pluginlistwithunpack=$pluginlistwithunpack";unpack=false"
#echo $pluginlistwithunpack

osgibundles=`echo $pluginlist | sed -e 's, ,@start\,,g'`
osgibundles=`echo $osgibundles | sed -e 's,org.eclipse.osgi[^@]*@start\,,,g'`
osgibundles=$osgibundles"@start,org.eclipse.osgi.services@start,org.eclipse.equinox.servletbridge.extensionbundle@start"
#echo $osgibundles

cp -r rap-build-template/* "$BUILD/"
cd rap-build-template
for file in `find . -name '*.xml'` `find . -name '*.ini'`
do
  mkdir -p "../$BUILD/"`dirname "$file"`
  sed -e "s/@PLUGINLIST@/${pluginlistwithunpack}/g" "$file" | \
     sed -e "s/@FEATUREID@/${featureid}/g" | \
     sed -e "s/@OSGIBUNDLES@/${osgibundles}/g" > "../$BUILD/$file"
done
cd ..

# Copy product sources
echo "Prepare sources"
#echo $pluginlist

mkdir $BUILD/plugins
copyIfNotExists "$pluginlist" "../products/$ORGANIZATION/plugins" "$BUILD/plugins"
copyIfNotExists "$pluginlist" "../core/plugins" "$BUILD/plugins"
copyIfNotExists "$pluginlist" "../applications/plugins" "$BUILD/plugins"

mkdir "$BUILD/BuildDirectory"
cd "$BUILD"
mv plugins BuildDirectory
cd ..

# Run the build
# XXX Doing it in the plugin directory: it was breaking otherwise
ABSOLUTE_DIR="$PWD"
echo "Start build"
echo $ABSOLUTE_DIR
java -jar "$ABSOLUTE_DIR"/ext/eclipse/plugins/org.eclipse.equinox.launcher_1.3.*.jar \
	-application org.eclipse.ant.core.antRunner \
	-buildfile "$ABSOLUTE_DIR"/ext/eclipse/plugins/org.eclipse.pde.build_3.*/scripts/build.xml \
	-Dbuild.dir="$ABSOLUTE_DIR" \
    -DbuildDirectory="$ABSOLUTE_DIR"/build/BuildDirectory \
	-Dbuilder="$ABSOLUTE_DIR"/build \
	-DbaseLocation="$ABSOLUTE_DIR"/ext/eclipse-rap \
	-DtopLevelElementId=${featureid} \
	-DpkgId=${warproductname}


