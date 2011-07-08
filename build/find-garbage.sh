#!/bin/bash
TEMPFILE="/tmp/css-find-garbage"
rm -f $TEMPFILE
 
find ../core -maxdepth 2 | xargs -I {} bash -c "echo {}; ls {} -1a | wc -l" | grep -x "3" -B1 | grep "/" > $TEMPFILE
find ../applications -maxdepth 2 | xargs -I {} bash -c "echo {}; ls {} -1a | wc -l" | grep -x "3" -B1 | grep "/" >> $TEMPFILE
find ../products -maxdepth 3 | xargs -I {} bash -c "echo {}; ls {} -1a | wc -l" | grep -x "3" -B1 | grep "/" >> $TEMPFILE

BROKENPLUGINS=`wc -l < $TEMPFILE`
error=0
if [[ $BROKENPLUGINS != "0" ]]
  then
    echo Found plug-ins with just one file/directory:
	cat $TEMPFILE

	echo "Now scan these dirs for .project artifacts."
    while read line
    do	
      name=$(find $line -maxdepth 1 -type f -name ".project")
	  if [[ "$name" != "" ]]
         then 
		   error=-1
		   echo ".project artifact found in $line"
      fi
    done <$TEMPFILE
fi

rm -f $TEMPFILE
exit $error
