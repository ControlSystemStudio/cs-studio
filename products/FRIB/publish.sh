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
cd $BUILD_DIR/build/BuildDirectory/I.CSS-FRIB/

unzip CSS-FRIB-linux.gtk.x86.zip
if [[ ! -d jdk_linux_x86 ]]
  then
    wget http://sourceforge.net/projects/cs-studio/files/jre/jdk_linux_x86.tar.gz
    tar -xvf jdk_linux_x86.tar.gz
  fi
cp -r jdk_linux_x86/jre css-frib/
zip -r CSS-FRIB-linux32-$VERSION.zip css-frib
rm -rf css-frib
rm -rf CSS-FRIB-linux.gtk.x86.zip

unzip CSS-FRIB-linux.gtk.x86_64.zip
if [[ ! -d jdk_linux_x64 ]]
  then
    wget http://sourceforge.net/projects/cs-studio/files/jre/jdk_linux_x64.tar.gz  
    tar -xvf jdk_linux_x64.tar.gz
  fi
cp -r jdk_linux_x64/jre css-frib/
zip -r CSS-FRIB-linux64-$VERSION.zip css-frib
rm -rf css-frib
rm -rf CSS-FRIB-linux.gtk.x86_64.zip

mv CSS-FRIB-macosx.cocoa.x86.zip CSS-FRIB-macosx-$VERSION.zip
mv CSS-FRIB-win32.win32.x86.zip CSS-FRIB-win32-$VERSION.zip
mv CSS-FRIB-win32.win32.x86_64.zip CSS-FRIB-win64-$VERSION.zip
scp *.zip bnl-jenkins,cs-studio@frs.sourceforge.net:/home/frs/project/cs-studio/frib-release/
