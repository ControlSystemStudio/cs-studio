MBROLA
------

The MBROLA libraries add more voices to FreeTTS as used in
org.csstudio.utility.speech.

This runs on both Linux and OS X.
On Linux, the female voice might be a little better than the 'kevin16' default.
On OS X, the output is just noise.
So overall, this is not ready for production use.



See also the mbrola/README.html included in freetts-1.2.2-src.zip

Example for Mac OS X or Linux

 # OS X
 MBROLA=/Applications/mbrola
 
 # Linux
 MBROLA=~/mbrola


 mkdir $MBROLA
 cd mbrola_bin

 # Linux
 unzip mbr301h.zip mbrola-linux-i386
 mv mbrola-linux-i386 $MBROLA/mbrola

 unzip us1-980512.zip -d $MBROLA
 unzip us2-980812.zip -d $MBROLA
 unzip us3-990208.zip -d $MBROLA

 # OS Similar, using mbrola-darwin-ppc

Test of the mbrola binary:
 cd $MBROLA
 ./mbrola  us1/us1 us1/TEST/alice.pho /tmp/xx.wav
 open /tmp/xx.wav 

There's also an MbroliX.app OS X GUI app for playing alice.pho.
Use the preferences to add the us1/us2/us3 voices.

Using with FreeTTS:
 cd freetts-1.2.2
 cp mbrola/mbrola.jar lib
 # List all voices
 java -Dmbrola.base=$MBROLA -jar lib/mbrola.jar 
 
 # Run the FreeTTS demo with MBROLA voice:
 java -Dmbrola.base=$MBROLA -jar bin/FreeTTSHelloWorld.jar mbrola_us1

