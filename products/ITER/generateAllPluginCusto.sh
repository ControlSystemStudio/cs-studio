#!/bin/sh
ORIGIN_DIR=`pwd`

# Absolute path to this script.
SCRIPT=$(readlink -f $0)
# Absolute path this script is in.
SCRIPTPATH=`dirname "$SCRIPT"`

REPO_DIR=`cd "$SCRIPTPATH/../../";pwd`

BUILD_DIR="$REPO_DIR/build"

cd "$SCRIPTPATH"

function generatePluginCusto {
  echo ==== Start generation $2
  echo ====== Compute plugins list of $2
  cd "$BUILD_DIR"
  python scan_dependencies.py -p "$REPO_DIR/products/ITER/products/$2" -b "$BUILD_DIR" -r "$REPO_DIR" --confBuildDir "$REPO_DIR/products/ITER/products/$2/build"
  if [ "$?" -ne "0" ]; then
    echo Scan dependencies failed. >&2
    exit 1;
  fi
  echo ====== Compute plugin_cutomization.ini.complete of $2
  cd "$SCRIPTPATH"
  ./generate-plugin_custo.pl -r "$REPO_DIR" -o $1 -l "$REPO_DIR/products/ITER/products/$2/build/plugins.list" -p "$REPO_DIR/products/ITER/products/$2/plugin_customization.ini" -f "$REPO_DIR/products/ITER/products/$2/plugin_customization.ini.full"
  if [ "$?" -ne "0" ]; then
    echo Generation failed: $2. >&2
    exit 1;
  fi
}
function generateCssRapIni {
  orga=$1;
  shift;
  cssrap=$1;
  shift;
  warproductargs='';
  for warproductpath in $@
  do
    warproductargs="$warproductargs -w \"$REPO_DIR/$warproductpath\""
  done
  echo ==== Start generating css_rap.ini
  eval ./generate-plugin_custo.pl -r \"$REPO_DIR\" -o $orga -p \"$cssrap\" $warproductargs -f "$cssrap.ini.full"
  if [ "$?" -ne "0" ]; then
    echo Generation failed: css_rap.ini.
    exit 1;
  fi
}

generatePluginCusto ITER org.csstudio.iter.css.product
generatePluginCusto ITER org.csstudio.iter.scan.server.product
generatePluginCusto ITER org.csstudio.iter.alarm.beast.annunciator.product
generatePluginCusto ITER org.csstudio.iter.alarm.beast.configtool.product
generatePluginCusto ITER org.csstudio.iter.alarm.beast.notifier.product
generatePluginCusto ITER org.csstudio.iter.alarm.beast.server.product
generatePluginCusto ITER org.csstudio.iter.archive.config.rdb.product
generatePluginCusto ITER org.csstudio.iter.archive.engine.product
generatePluginCusto ITER org.csstudio.iter.jms2rdb.product
generatePluginCusto ITER org.csstudio.iter.utility.jmssendcmd.product

generateCssRapIni ITER ~/svn/trunk/m-tomcat-iter/src/main/conf/css_rap.ini \
  products/ITER/products/org.csstudio.iter.webopi/webopi.warproduct.path \
  products/ITER/products/org.csstudio.iter.webdatabrowser/webdatabrowser.warproduct.path \
  products/ITER/products/org.csstudio.iter.webalarm/webalarm.warproduct.path

cd "$ORIGIN_DIR"

