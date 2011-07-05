#!/bin/sh
#
# Script that triggers a build of "everything"
#
# Runs on Windows with cygwin installed
#
# Kay Kasemir
CYGDRIVE=/cygdrive/c

source settings.sh

# Hack the windows file permissions for cygwin
chmod 664 $CYGDRIVE/$BUILDDIR/I.css_kek_$VERSION/*.zip $CYGDRIVE/$BUILDDIR/css_kek_$VERSION-src.zip
chmod -R 777 $CYGDRIVE/$BUILDDIR/buildRepo

scp $CYGDRIVE/$BUILDDIR/I.css_kek_$VERSION/*.zip $CYGDRIVE/$BUILDDIR/css_kek_$VERSION-src.zip kasemir@abco4.kek.jp:/mnt/linac-misc/httpd/home/cont/css/apps
scp -r $CYGDRIVE/$BUILDDIR/buildRepo/* kasemir@abco4.kek.jp:/mnt/linac-misc/httpd/home/cont/css/updates

