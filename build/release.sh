#!/bin/bash

# Check parameters
VERSION=$1
BUILD_DIR=`pwd`
if [ -z "$VERSION" ]
then 
  echo You must provide the version \(e.g. \"release.sh 3.2.4\"\)
exit -1
fi

echo ::: Prepare all products :::
while read PRODUCT
do
  ./prepare-release.sh $PRODUCT $VERSION
  git commit -a -m "$PRODUCT: preparing for release $VERSION"
done < release.products

echo ::: Tagging version $VERSION :::
git tag CSS-$VERSION
git push

echo ::: Build all products :::
while read PRODUCT
do
  ./build.sh $PRODUCT
done < release.products

echo ::: Release Done :::
