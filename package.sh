#!/bin/bash

# Check parameters
VERSION=$1
WIN_BUILD_DIR="build/windows"
if [ -z "$VERSION" ]
then 
  echo You must provide the product version \(e.g. \"package.sh 3.2.4\"\)
exit -1
fi

echo ::: Package Windows :::
rm -rf ${WIN_BUILD_DIR}/cs-studio
unzip repository/target/products/*64.zip -d ${WIN_BUILD_DIR}
cd ${WIN_BUILD_DIR}
cp -R ../applicationIcons cs-studio/

unset DISPLAY
wine InnoSetup5/ISCC.exe css-windows-build/install_script_general.iss "/dCSSVersion=${VERSION}"
echo ::: DONE :::
