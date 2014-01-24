#!/bin/bash


#*******************************************************************************
# * Copyright (c) 2010-2014 ITER Organization.
# * All rights reserved. This program and the accompanying materials
# * are made available under the terms of the Eclipse Public License v1.0
# * which accompanies this distribution, and is available at
# * http://www.eclipse.org/legal/epl-v10.html
# ******************************************************************************/


# Check parameters
VERSION=$1
BUILD_DIR="../../build"
if [ -z "$VERSION" ]
then 
  echo You must provide the product version \(e.g. \"prepare_release.sh 3.2.4\"\)
exit -1
fi

echo ::: Prepare splash :::
java -jar $BUILD_DIR/ImageLabeler-1.0.jar $VERSION 462 53 ./products/org.csstudio.iter.css.product/splash-template.bmp ./products/org.csstudio.iter.css.product/splash.bmp
echo ::: Change about dialog version :::
echo 0=$VERSION > about.mappings
