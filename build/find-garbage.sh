#!/bin/bash
TEMPFILE="/tmp/css-find-garbage"
rm -f $TEMPFILE
find ../core -maxdepth 2 | xargs -I {} bash -c "echo {}; ls {} -1a | wc -l" | grep -x "3" -B1 | grep "/" > $TEMPFILE
find ../applications -maxdepth 2 | xargs -I {} bash -c "echo {}; ls {} -1a | wc -l" | grep -x "3" -B1 | grep "/" >> $TEMPFILE

BROKENPLUGINS=`wc -l < $TEMPFILE`
if [[ $BROKENPLUGINS == "0" ]]
then
rm -f $TEMPFILE
exit 0
else
echo Found plug-ins with just one file/directory:
cat $TEMPFILE
rm -f $TEMPFILE
exit -1
fi

