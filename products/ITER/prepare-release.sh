#!/bin/bash

# Check parameters
VERSION=$1
BUILD_DIR="../../build"
if [ -z "$VERSION" ]
then 
  echo You must provide the product version \(e.g. \"prepare_release.sh 3.2.4\"\)
exit -1
fi

echo ::: Prepare splash :::
java -jar $BUILD_DIR/ImageLabeler-2.0.jar $VERSION 400 170 ./products/org.csstudio.iter.css.product/splash-css-template.bmp ./products/org.csstudio.iter.css.product/splash.bmp ORANGE
echo ::: Change about dialog version :::
echo 0=$VERSION > about.mappings
