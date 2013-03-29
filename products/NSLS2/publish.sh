#!/bin/bash

# Check parameters
VERSION=$1
BUILD_DIR="../../build"
if [ -z "$VERSION" ]
then 
  echo You must provide the product version \(e.g. \"prepare_release.sh 3.2.4\"\)
exit -1
fi

echo ::: Publish product to sourceforge :::
cd $BUILD_DIR/release/BuildDirectory/I.CSS-NSLSII/
mv CSS-NSLSII-linux.gtk.x86.zip CSS-NSLSII-linux-$VERSION.zip
mv CSS-NSLSII-linux.gtk.x86_64.zip CSS-NSLSII-linux64-$VERSION.zip
mv CSS-NSLSII-macosx.cocoa.x86.zip CSS-NSLSII-macosx-$VERSION.zip
mv CSS-NSLSII-win32.win32.x86.zip CSS-NSLSII-win32-$VERSION.zip
mv CSS-NSLSII-win32.win32.x86_64.zip CSS-NSLSII-win64-$VERSION.zip
scp *.zip bnl-jenkins,cs-studio@frs.sourceforge.net:/home/frs/project/cs-studio/nsls2-release/