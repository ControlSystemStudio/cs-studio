Speech Support
==============

See AnnunciatorTest.java for usage.

* FreeTTS Detail *

To run the original tests:
 cd lib
 unzip freetts-1.2.1-src.zip
 rm -rf META-INF

# ^^^ these steps will also allow you to source-leveldebug
# ^^^ the Annunciator code down into FreeTTS

 cd freetts-1.2.1
 cd lib
 sh jsapi.sh
 cd ..
 ant
 java -jar bin/HelloWorld.jar
  
Tests look like they only need these jars:
  freetts.jar and maybe jsapi.jar
  
 But to function, FreeTTS looks for voices.txt
 and most of the other jar files at runtime
 to locate the voices. Per default, they must
 be in the same directory as freetts.jar!
 
 When using JSAPI, speech.properties is also required
 in various places.
 