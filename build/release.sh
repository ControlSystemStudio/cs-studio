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
  cd ../products/$PRODUCT
  ./prepare-release.sh $VERSION
  git commit -a -m "$PRODUCT: preparing for release $VERSION"
done < release.products

cd $BUILD_DIR

echo ::: Tagging version $VERSION :::
git tag CSS-$VERSION
git push origin CSS-$VERSION

echo ::: Build all products :::
while read PRODUCT
do
  ./build.sh $PRODUCT
done < release.products

echo ::: Release Done :::
