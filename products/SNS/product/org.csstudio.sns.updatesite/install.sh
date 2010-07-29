# Install from P2 repository (or just list content)
#
# Kay Kasemir

source settings.sh

if [ $# -lt 1  -o  $# -gt 2 ]
then
    echo Usage: install.sh {repository}
    echo Usage: install.sh {repository} {what}
    echo
    echo Example: install.sh http://ics-web.sns.ornl.gov/css/updates
    echo Example: install.sh http://ics-web.sns.ornl.gov/css/updates org.csstudio.sns.product.product
    exit -1
fi

REPO="$1"

if [ $# -eq 1 ]
then
    java -jar $ECLIPSE/plugins/org.eclipse.equinox.launcher_*.jar \
      -application org.eclipse.equinox.p2.director \
      -repository $REPO \
      -list
fi

WHAT="$2"
DEST=/tmp/install

ARCH="-p2.os macosx -p2.ws carbon -p2.arch x86"
#ARCH="-p2.os linux  -p2.ws gtk    -p2.arch x86"
#ARCH="-p2.os win32  -p2.ws win32  -p2.arch x86"

java -jar $ECLIPSE/plugins/org.eclipse.equinox.launcher_*.jar \
  -application org.eclipse.equinox.p2.director \
  -repository $REPO \
  -installIU $WHAT \
  -destination $DEST \
  $ARCH \
  -profile profile \
  -profileProperties org.eclipse.update.install.features=true \
  -bundlepool $DEST \
  -roaming \
  -consoleLog
    