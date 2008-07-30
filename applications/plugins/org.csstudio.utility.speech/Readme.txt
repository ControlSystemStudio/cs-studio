Speech Support
==============

See AnnunciatorTest.java for usage.


FreeTTS Detail
--------------

#
# Run the original tests
#
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
 
 
#
# Jar file explosion
# 
Tests look like they only need these jars:
  freetts.jar and maybe jsapi.jar
  
But to function, FreeTTS looks for voices.txt
in the same directory that contained freetts.jar,
and then it opens all the other jar files
to locate the voices listed in voices.txt,
adds them to the classloader, and tries to
create them.

When using JSAPI, speech.properties is also required
in various places.

This runtime lookup of files, jars, and especially
the manipulation of the class loader clashes with
the Eclipse/equinox handling of the classpath.

Setting the System Property "freetts.voices"
and including the FreeTTS jars beforehand
in the classpath (or the Eclipse MANIFEST.MF)
seems to avoid the use of the voices.txt
as well as the runtime classpath gymnastics.