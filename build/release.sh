#!/bin/bash

# Check parameters
VERSION=$1
BUILD_DIR=`pwd`
if [ -z "$VERSION" ]
then 
  echo You must provide the version \(e.g. \"release.sh 3.2.4\"\)
exit -1
fi

# Prepare all products
while read PRODUCT
do
  ./prepare-release.sh $PRODUCT $VERSION
  git commit -a -m "Preparing product $PRODUCT for release $VERSION"
done < release.products

