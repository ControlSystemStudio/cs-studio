Speech Support
==============

See AnnunciatorTest.java for usage.


FreeTTS Detail
--------------

Sources:
freetts-1.2.2-src.zip

Extracted from that into /lib and added to classpath:
cmu_time_awb.jar
cmu_us_kal.jar
cmudict04.jar
cmulex.jar
cmutimelex.jar
en_us.jar
freetts.jar
jsapi.jar


#
# Run the original tests
#
 cd lib
 unzip freetts-1.2.2-src.zip
 rm -rf META-INF

# ^^^ these steps will also allow you to source-level debug
# ^^^ the Annunciator code down into FreeTTS

 cd freetts-1.2.2/lib
 sh jsapi.sh
 cd ..
# Build.xml is broken. Change the "src" path to ".":
#     <property name="src_dir" value="." />
  vi build.xml
 ant
 java -jar bin/HelloWorld.jar
 java -jar bin/FreeTTSHelloWorld.jar
# Will complain about missing speech.properties. Copy to home dir...
 cp speech.properties ~
# .. then try the *Hello*jar again
 
 
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