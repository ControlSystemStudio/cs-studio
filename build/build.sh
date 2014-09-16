#!/bin/bash

function show_usage {
cat <<EOF
Usage:
    build.sh <ORGANIZATION> [options]

Options:
    -p, --product PRODUCT
         Product id (path in ../products/<ORGANIZATION>/products/) to build.
         The product path must contains a 'build' folder with build.properties
         plugins.list and features.list inside.
         If this option is not set, the above files must be in
         ../products/<ORGANIZATION>/ directory.
         
    -c, --compute-deps
         Compute dependencies before build and save it
         in plugins.list and features.list
EOF
}

# parse command line
computedeps=0

LONGOPTS="help \
product: \
compute-deps"

ORGANIZATION=$1
shift
if [ -z "$ORGANIZATION" ]
then
  show_usage
  exit 1
fi

ARGS=`getopt -o hcp: -l "${LONGOPTS}" -- "$@"`
eval set -- "$ARGS"

while [ $# -gt 0 ]
do
  case "$1" in
      -p|--product)
          PRODUCT_ID=$2
          shift
          ;;
      -c|--compute-deps)
          computedeps=1
          ;;
      -h|--help)
          show_usage
          exit 2
          ;;
      --)
          ;;
      *)
          echo "Unrecognized option: $1"
          echo ""
          show_usage
          exit 1
          ;;
  esac
  shift
done

if [ -z "$PRODUCT_ID" ]; then
  productPath=`readlink -f "../products/$ORGANIZATION"`
  confBuildDir="$productPath"
else
  productPath=`readlink -f "../products/$ORGANIZATION/products/$PRODUCT_ID"`
  confBuildDir="$productPath/build"
fi
if [[ ! -d "$productPath" ]]
then
  echo "Directory $productPath not found" >&2
  exit 1
fi
if [[ ! -d "$confBuildDir" ]]
then
  echo "Directory $confBuildDir not found" >&2
  exit 1
fi

BUILD="build"

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
  # Install BATIK in plugins directory
  unzip -o ../batik-1.7.zip -d eclipse/
  cd ..
fi

if [ "$ORGANIZATION" = "ITER" ]
then
  cd ext
  if [[ ! -d eclipse/dropins/subclipse ]]
  then
    # Download and install subclipse in dropins directory
    if [[ ! -f subclipse-site-1.10.5.zip ]]
    then
      wget -O subclipse-site-1.10.5.zip http://subclipse.tigris.org/files/documents/906/49382/site-1.10.5.zip
    fi
    unzip -o subclipse-site-1.10.5.zip -d eclipse/dropins/subclipse
  fi
  if [[ ! -d eclipse/dropins/pydev ]]
  then
    # Download and install pydev in dropins directory
    if [[ ! -f PyDev_3.7.0.zip ]]
    then
      wget -O PyDev_3.7.0.zip http://sourceforge.net/projects/pydev/files/pydev/PyDev%203.7.0/PyDev%203.7.0.zip/download
    fi
    unzip -o PyDev_3.7.0.zip -d eclipse/dropins/pydev
    # Remove signature from pydev jars
    find eclipse/dropins/pydev -name '*.DSA' -exec rm -f {} \;
    find eclipse/dropins/pydev -name '*.SF' -exec rm -f {} \;
  fi
  if [[ ! -d eclipse/dropins/mylyn ]]
  then
    # Download and install mylyn in dropins directory (https://bugs.eclipse.org/bugs/show_bug.cgi?id=336129)
    if [[ ! -f mylyn-3.12.0.zip ]]
    then
      wget -O mylyn-3.12.0.zip http://download.eclipse.org/mylyn/drops/3.12.0/v20140609-1648/mylyn-3.12.0.v20140609-1648.zip
    fi
    unzip -o mylyn-3.12.0.zip -d eclipse/dropins/mylyn
  fi
  cd ..
fi

function copyIfNotExists {
  listfile=$1;
  source=$2;
  target=$3;
  if [[ -d "$source" ]]
  then
    for dir in `cat "$listfile"`
    do
      if [[ ! -d "$target/$dir" && -d "$source/$dir" ]]
      then
        cp -R "$source/$dir" "$target"
      fi
    done
  fi
}

if [ $computedeps -ne 0 ]; then
  ## Generate plugins.list and features.list
  echo "Compute plugins and features list"
  buildPath=`pwd`
  repoPath=`cd "..";pwd`
  python scan_dependencies.py -p "$productPath" -b "$buildPath" -r "$repoPath" --confBuildDir "$confBuildDir"
  if [ "$?" -ne "0" ]; then
    echo Scan dependencies failed. >&2
    exit 1;
  fi
fi

# Copy product sources
echo "Prepare sources"

cp -R "../products/$ORGANIZATION" "$BUILD"

cp "$confBuildDir/build.properties" "$BUILD/"
cp "$confBuildDir/plugins.list" "$BUILD/"
cp "$confBuildDir/features.list" "$BUILD/"

if [[ "$ORGANIZATION" = "ITER" ]]
then
  copyIfNotExists "$confBuildDir/plugins.list" "../products/$ORGANIZATION/products" "$BUILD/plugins"
fi

copyIfNotExists "$confBuildDir/plugins.list" "../core/plugins" "$BUILD/plugins"
copyIfNotExists "$confBuildDir/plugins.list" "../applications/plugins" "$BUILD/plugins"

if [[ "$ORGANIZATION" = "ITER" ]]
then
  # get SNL plugins from DESY
  copyIfNotExists "$confBuildDir/plugins.list" "../products/DESY/plugins" "$BUILD/plugins"
  # get SCAN plugins from SNS
  copyIfNotExists "$confBuildDir/plugins.list" "../products/SNS/plugins" "$BUILD/plugins"
fi

copyIfNotExists "$confBuildDir/features.list" "../core/features" "$BUILD/features"
copyIfNotExists "$confBuildDir/features.list" "../applications/features" "$BUILD/features"


# Check if all required features and plugins was found
for dir in `cat "$confBuildDir/plugins.list"`
do
  if [[ ! -d "$BUILD/plugins/$dir" ]]
  then
    echo Plugin $dir not found.
  fi
done
for dir in `cat "$confBuildDir/features.list"`
do
  if [[ ! -d "$BUILD/features/$dir" ]]
  then
    echo Feature $dir not found.
  fi
done

mkdir "$BUILD/BuildDirectory"
cd "$BUILD"
mv features BuildDirectory
mv plugins BuildDirectory
cd ..

# Run the build
# XXX Doing it in the plugin directory: it was breaking otherwise
ABSOLUTE_DIR=$PWD
echo "Start build"
echo $ABSOLUTE_DIR
java -jar "$ABSOLUTE_DIR"/ext/eclipse/plugins/org.eclipse.equinox.launcher_1.*.jar \
	-application org.eclipse.ant.core.antRunner \
	-buildfile "$ABSOLUTE_DIR"/ext/eclipse/plugins/org.eclipse.pde.build_3.*/scripts/productBuild/productBuild.xml \
	-Dbuilder="$ABSOLUTE_DIR"/build \
	-Dbuild.dir="$ABSOLUTE_DIR"

if [ "$?" -ne "0" ]; then
  echo Build failed. >&2
  exit 1;
fi

# read properties from the build.properties and set up variable for each of them
#TEMPFILE=$(mktemp)
#cat build/build.properties |grep -v "#" |  grep 'buildId=\|archivePrefix=\|launchName=' |sed -re 's/"/"/'g| sed -re 's/=(.*)/="\1"/g'>$TEMPFILE
#source $TEMPFILE
#rm $TEMPFILE
echo $buildId $archivePrefix $launchName
