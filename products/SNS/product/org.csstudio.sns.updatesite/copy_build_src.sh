# Headless build of SNS CSS
#
# Based on info from Jan Hatje
#
# Kay Kasemir

source settings.sh

# Clean previous build
mkdir -p $BUILDDIR/features
mkdir -p $BUILDDIR/plugins

# How to copy
#CP="cp -r"
#CP="rsync -avz --delete"
CP="rsync -az --delete"

# Get the sources
# For now using local copy
cat plugins.list | while read p
do
    comment=`echo $p | fgrep -c "#"`
    if [ 0 -eq $comment  -a  "x$p" != "x" ]
    then
        $CP $WORKSPACE/$p $BUILDDIR/plugins
    fi
done

cat features.list | while read p
do
    comment=`echo $p | fgrep -c "#"`
    if [ 0 -eq $comment  -a  "x$p" != "x" ]
    then
        $CP $WORKSPACE/$p $BUILDDIR/features
    fi
done

