y
y#!/bin/bash

# Check parameters
VERSION=$1
BUILD_DIR="../../build"
if [ -z "$VERSION" ]
then 
  echo You must provide the product version \(e.g. \"prepare_release.sh 3.2.4\"\)
exit -1
fi

echo ::: Publish product to sourceforge :::
cd $BUILD_DIR/build/BuildDirectory/I.CS-STUDIO/

unzip CS-STUDIO-linux.gtk.x86.zip
if [[ ! -d jdk_linux_x86 ]]
  then
    wget http://sourceforge.net/projects/cs-studio/files/jre/jdk_linux_x86.tar.gz
    tar -xvf jdk_linux_x86.tar.gz
  fi
cp -r jdk_linux_x86/jre cs-studio/
zip -r CS-STUDIO-linux32-$VERSION.zip cs-studio
rm -rf cs-studio
rm -rf CS-STUDIO-linux.gtk.x86.zip

unzip CS-STUDIO-linux.gtk.x86_64.zip
if [[ ! -d jdk_linux_x64 ]]
  then
    wget http://sourceforge.net/projects/cs-studio/files/jre/jdk_linux_x64.tar.gz  
    tar -xvf jdk_linux_x64.tar.gz
  fi
cp -r jdk_linux_x64/jre cs-studio/
zip -r CS-STUDIO-linux64-$VERSION.zip cs-studio
rm -rf cs-studio
rm -rf CS-STUDIO-linux.gtk.x86_64.zip

mv CS-STUDIO-macosx.cocoa.x86.zip CS-STUDIO-macosx-$VERSION.zip
mv CS-STUDIO-win32.win32.x86.zip CS-STUDIO-win32-$VERSION.zip
mv CS-STUDIO-win32.win32.x86_64.zip CS-STUDIO-win64-$VERSION.zip
scp *.zip bnl-jenkins,cs-studio@frs.sourceforge.net:/home/frs/project/cs-studio/cs-studio-release/
