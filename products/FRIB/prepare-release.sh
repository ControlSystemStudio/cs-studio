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
java -jar $BUILD_DIR/ImageLabeler-1.0.jar $VERSION 462 53 plugins/org.csstudio.frib.product/splash-template.bmp plugins/org.csstudio.frib.product/splash.bmp
echo ::: Change about dialog version :::
echo 0=$VERSION > plugins/org.csstudio.frib.product/about.mappings
