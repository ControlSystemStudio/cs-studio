#!/bin/bash

# Check parameters
VERSION=$1
PUSH=$2
BUILD_DIR="build"

increment_version ()
{
  declare -a part=( ${1//\./ } )
  declare    new
  declare -i carry=1

  for (( CNTR=${#part[@]}-1; CNTR>=0; CNTR-=1 )); do
    len=${#part[CNTR]}
    new=$((part[CNTR]+carry))
    [ ${#new} -gt $len ] && carry=1 || carry=0
    [ $CNTR -gt 0 ] && part[CNTR]=${new: -len} || part[CNTR]=${new}
  done
  new="${part[*]}"
  echo -e "${new// /.}"
} 

VERSIONP=$(increment_version ${VERSION})
VERSION="${VERSIONP}-SNAPSHOT"

if [! $# == 1 ]
then 
  echo You must provide the product version, compat link, milestone, notes \(e.g. \"prepare_release.sh 3.3.0 \"https://github\" \"https://github\" \"Some notes\"\"\)
exit -1
fi

echo ::: Prepare splash :::
java -jar $BUILD_DIR/ImageLabeler-1.0.jar $VERSION 462 53 plugins/org.csstudio.product/splash-template.bmp plugins/org.csstudio.product/splash.bmp
echo ::: Change about dialog version :::
echo 0=$VERSION > plugins/org.csstudio.product/about.mappings

echo ::: Updating plugin versions ::
mvn -Dtycho.mode=maven org.eclipse.tycho:tycho-versions-plugin:set-version -DnewVersion=$VERSION -Dartifacts=product,products-csstudio-plugins,org.csstudio.product,org.csstudio.startup.intro,products-csstudio-features,org.csstudio.product.feature,org.csstudio.product.configuration.feature,repository
# update product because set-version doesn't
sed -i 's/\(\<product[^>]\+\? version=\"\)[^"]*\("[^>]\+\?>\)/\1'${VERSIONP}'\2/g'  repository/cs-studio.product

echo ::: Committing and tagging version $VERSION :::
git commit -a -m "Updating changelog, splash, manifests to version $VERSION"
if [ "$PUSH" = "true" ]
then
  echo ::: Pushing changes :::
  git push origin
fi
