source settings.sh

cd $BUILDDIR

NIGHTLY=/var/www/html/css/nightly

rm -rf $NIGHTLY
mkdir -p $NIGHTLY

mv buildRepo/ $NIGHTLY/repo
mv apps/*$VERSION* $NIGHTLY




