#!/bin/bash

# Check parameters
PRODUCT=$1
VERSION=$2
BUILD="build"
BUILD_DIR="../../build"
if [ -z "$PRODUCT" ]
then 
  echo You must provide the product \(e.g. \"prepare_release.sh NSLS2 3.2.4\"\)
exit -1
fi
if [ -z "$VERSION" ]
then 
  echo You must provide the product version \(e.g. \"prepare_release.sh NSLS2 3.2.4\"\)
exit -1
fi

echo Build directory: $BUILD_DIR
echo Product: $PRODUCT

cd ../products/$PRODUCT
java -jar $BUILD_DIR/ImageLabeler-1.0.jar $VERSION `cat splash-parameters.txt`