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
cd $BUILD_DIR/build/BuildDirectory/I.CSS-NSLSII/

unzip CSS-NSLSII-linux.gtk.x86.zip
if [[ ! -d jdk_linux_x86 ]]
  then
    wget http://sourceforge.net/projects/cs-studio/files/jre/jdk_linux_x86.tar.gz
    tar -xvf jdk_linux_x86.tar.gz
  fi
cp -r jdk_linux_x86/jre css-nsls2/
zip -r CSS-NSLSII-linux32-$VERSION.zip css-nsls2
rm -rf css-nsls2
rm -rf CSS-NSLSII-linux.gtk.x86.zip

unzip CSS-NSLSII-linux.gtk.x86_64.zip
if [[ ! -d jdk_linux_x64 ]]
  then
    wget http://sourceforge.net/projects/cs-studio/files/jre/jdk_linux_x64.tar.gz  
    tar -xvf jdk_linux_x64.tar.gz
  fi
cp -r jdk_linux_x64/jre css-nsls2/
zip -r CSS-NSLSII-linux64-$VERSION.zip css-nsls2
rm -rf css-nsls2
rm -rf CSS-NSLSII-linux.gtk.x86_64.zip

mv CSS-NSLSII-macosx.cocoa.x86.zip CSS-NSLSII-macosx-$VERSION.zip
mv CSS-NSLSII-win32.win32.x86.zip CSS-NSLSII-win32-$VERSION.zip
mv CSS-NSLSII-win32.win32.x86_64.zip CSS-NSLSII-win64-$VERSION.zip
scp *.zip bnl-jenkins,cs-studio@frs.sourceforge.net:/home/frs/project/cs-studio/nsls2-release/
