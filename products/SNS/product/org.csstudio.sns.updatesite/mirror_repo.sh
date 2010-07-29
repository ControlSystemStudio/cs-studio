# Mirror from one P2 repository to another
#
# This includes the cases of fetching from an existing repository into
# an empty directory, i.e. get a fresh copy of the original,
# or adding new stuff from a small 'latest feature' repo into
# a bigger 'main' repo.
#
# Kay Kasemir

source settings.sh

if [ $# -ne 2 ]
then
    echo Usage: mirror_repo.sh {source} {destination}
    echo
    echo Example: mirror_repo.sh http://ics-web.sns.ornl.gov/css/updates file:/repo
    exit -1
fi

SOURCE="$1"
DEST="$2"

echo Mirroring $SOURCE to $DEST

java -jar $ECLIPSE/plugins/org.eclipse.equinox.launcher_*.jar \
  -application org.eclipse.equinox.p2.metadata.repository.mirrorApplication \
  -source  $SOURCE -destination $DEST

java -jar $ECLIPSE/plugins/org.eclipse.equinox.launcher_*.jar \
  -application org.eclipse.equinox.p2.artifact.repository.mirrorApplication \
  -verbose \
  -compare \
  -source  $SOURCE -destination $DEST

  