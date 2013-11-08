#!/bin/sh
ORIGIN_DIR=`pwd`

# Absolute path to this script.
SCRIPT=$(readlink -f $0)
# Absolute path this script is in.
SCRIPTPATH=`dirname "$SCRIPT"`

REPO_DIR=`cd "$SCRIPTPATH/../..";pwd`
DIST="$REPO_DIR/dist"

rm -rf "$DIST"
mkdir "$DIST"

cd "$REPO_DIR/build"

function buildProduct {
  echo ==== Start building $2
  ./build.sh "$1" -c -p "$2"
  if [ "$?" -ne "0" ]; then
    echo Build failed: $2. >&2
    exit 1;
  fi
  mkdir "$DIST/$2"
  cp build/BuildDirectory/I.*/*.zip "$DIST/$2/"
  cp -r build/*Repository "$DIST/$2/"
}
function buildWebProduct {
  warproduct=`basename "$2"`
  echo ==== Start building $warproduct
  ./build_web.sh "$1" "$2"
  if [ "$?" -ne "0" ]; then
    echo Build failed: $2.
    exit 1;
  fi
  mkdir "$DIST/$warproduct"
  cp build/BuildDirectory/*.war "$DIST/$warproduct/"
}

buildProduct ITER org.csstudio.iter.css.product
buildProduct ITER org.csstudio.iter.scan.server.product
buildProduct ITER org.csstudio.iter.alarm.beast.annunciator.product
buildProduct ITER org.csstudio.iter.alarm.beast.configtool.product
buildProduct ITER org.csstudio.iter.alarm.beast.notifier.product
buildProduct ITER org.csstudio.iter.alarm.beast.server.product
buildProduct ITER org.csstudio.iter.archive.config.rdb.product
buildProduct ITER org.csstudio.iter.archive.engine.product
buildProduct ITER org.csstudio.iter.jms2rdb.product
buildProduct ITER org.csstudio.iter.utility.jmssendcmd.product

buildWebProduct ITER ../products/ITER/products/org.csstudio.iter.webopi/webopi.warproduct.path
buildWebProduct ITER ../products/ITER/products/org.csstudio.iter.webdatabrowser/webdatabrowser.warproduct.path
buildWebProduct ITER ../products/ITER/products/org.csstudio.iter.webalarm/webalarm.warproduct.path

cd "$ORIGIN_DIR"
