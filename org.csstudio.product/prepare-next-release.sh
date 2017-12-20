#!/bin/bash

# Check parameters
VERSION=$1
PUSH=$2
BUILD_DIR="build"

VERSIONP=$(echo $VERSION | sed -r 's/(.*)\.(.*)\.(.*)/echo \1\.\2\.$((\3+1))/ge');
VERSION="${VERSIONP}-SNAPSHOT"

if [! $# == 1 ]
then 
  echo You must provide the product version, compat link, milestone, notes \(e.g. \"prepare_release.sh 3.3.0 \"https://github\" \"https://github\" \"Some notes\"\"\)
exit -1
fi

echo ::: Prepare splash :::
java -jar $BUILD_DIR/ImageLabeler-2.0.jar $VERSION 462 53 plugins/org.csstudio.product/splash-template.bmp plugins/org.csstudio.product/splash.bmp "Community Edition" 19 151
echo ::: Change about dialog version :::
echo 0=$VERSION > plugins/org.csstudio.product/about.mappings

echo ::: Updating plugin versions ::
mvn -Dtycho.mode=maven org.eclipse.tycho:tycho-versions-plugin:1.0.0:set-version -DnewVersion=$VERSION -Dartifacts=product,products-csstudio-plugins,org.csstudio.product,org.csstudio.startup.intro,products-csstudio-features,org.csstudio.product.feature,org.csstudio.product.configuration.feature,repository
# update product because set-version doesn't
sed -i 's/\(\<product[^>]\+\? version=\"\)[^"]*\("[^>]\+\?>\)/\1'${VERSIONP}'\2/g'  repository/cs-studio.product

echo ::: Committing and tagging version $VERSION :::
git commit -a -m "Updating changelog, splash, manifests to version $VERSION"
if [ "$PUSH" = "true" ]
then
  echo ::: Pushing changes :::
  git push origin
fi
