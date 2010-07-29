#!/bin/sh
#
# Unzip a plugin jar, replace it with its content
#
# kasemirk@ornl.gov

if [ $# -ne 1 ]
then
    echo "Usage: unzip_plugin.sh the_plugin.jar"
    exit 1
fi

JAR=$1
DIR=`echo $JAR | sed -e s/_.*//`

echo "Unpack $JAR"
echo "into   $DIR ?"
echo -n " (Ctrl-c to stop)"
read x

mkdir $DIR
cd $DIR
unzip ../$JAR
cd ..
rm $JAR
