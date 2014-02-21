This plugin provides EPICS ChannelAccess libraries,
initializing their settings from Eclipse preferences.
For GUI Applications, consider adding org.csstudio.platform.libs.epics.ui.

* JCA
.. is the basic Java Channel Access API, including an implementation
that uses the original EPICS libraries via JNI.
Binaries for JCA are in OS-specific fragments
org.csstudio.platform.libs.epics.macosx,
org.csstudio.platform.libs.epics.linux,
..


* CAJ
.. is a pure Java implementation of the JCA API.



Examples for building JCA JNI Binaries
--------------------------------------

Get sources for JCA from http://epics-jca.sourceforge.net/jca:
> hg clone http://epics-jca.hg.sourceforge.net:8000/hgroot/epics-jca/epics-jca 
> cd epics-jca
> hg update JCA_20120614_2_3_6

Set EPICS_BASE_RELEASE, HOST_ARCH, EPICS_HOST_ARCH as required to build EPICS base.

> ant
  
This creates two items of interest:
1) Java library O.core/jca.jar, like the one in this plugin's lib subdir
2) OS-specific JNI lib like O.darwin-x86/libjca.jnilib, which typically
   should be re-linked as described below and then placed into the corresponding
   lib/<OS>/<CPU> subdir of the fragment for that OS.

- Mac OS X
Created on Intel OS X 10.6.8 with EPICS base R3.14.12.2 java 1.6.0_31.

The 'ant' build ends like this:

  g++ -L/Kram/epics/R3.14.12.2/base/lib/darwin-x86 -lca -lCom -framework JavaVM \
   -arch i386 -dynamiclib -Wl,-single_module \
   -o /Kram/MerurialRepos/jca/O.darwin-x86/libjca.jnilib \
   /Kram/MerurialRepos/jca/O.darwin-x86/JNI.o

 
   
... which resulted in a shared library with further shared lib dependencies:
otool -L O.darwin-x86/libjca.jnilib 
O.darwin-x86/libjca.jnilib:
    ...
    /Kram/epics/R3.14.12.2/base/lib/darwin-x86/libca.3.14.12.dylib (compatibility version 3.14.0, current version 3.14.12)
    /Kram/epics/R3.14.12.2/base/lib/darwin-x86/libCom.3.14.12.dylib (compatibility version 3.14.0, current version 3.14.12)
    ...

Manually issuing the following command creates a JCA JNI lib with only
"system" dependencies, no remaining EPICS shared lib dependecies:

LIB=$EPICS_BASE_RELEASE/lib/$EPICS_HOST_ARCH
g++ -framework JavaVM -arch i386 -dynamiclib -Wl,-single_module -o O.darwin-x86/libjca.jnilib \
    O.darwin-x86/JNI.o $LIB/libCom.a $LIB/libca.a 
        
cp O.darwin-x86/libjca.jnilib ....../org.csstudio.platform.libs.epics.macosx/lib/macosx/x86/libjca.jnilib 



- Linux X86
Created on Red Hat Enterprise Linux AS release 4 (Nahant Update 8)
with EPICS base R3.14.11, JDK 1.6.0_21

Similar to the OS X binary, the JNI lib was linked without
further dependencies on shared EPICS base libraries:

cd O.linux-x86
LIB=$EPICS_BASE_RELEASE/lib/$EPICS_HOST_ARCH
g++ -shared -lpthread -lreadline  -lncurses -lm -lrt -Wl,-rpath,. -o libjca.so JNI.o $LIB/libca.a $LIB/libCom.a
# Check:
ldd -d libjca.so 
cp libjca.so ....../org.csstudio.platform.libs.epics.linux/lib/linux/x86/libjca.so 

  

- Linux X86_64
Red Hat Enterprise Linux Workstation release 6.2 (Santiago)
with EPICS base R3.14.12.2, JDK 1.6.0_31

cd O.linux-x86_64
ldd libjca.so
# Showed dependencies on libca.so, ...

LIB=$EPICS_BASE_RELEASE/lib/$EPICS_HOST_ARCH
g++ -shared -fPIC -lpthread -lreadline -lncurses -lm -lrt -Wl,-rpath,.  -o libjca.so JNI.o $LIB/libca.a $LIB/libCom.a

cp libjca.so ...../org.csstudio.platform.libs.epics.linux/lib/linux/x86_64 






Older Notes
-----------

caj-1.1.7.jar
--------------
Built from 2010/11/10 source snapshot from http://epics-jca.sourceforge.net with the following adjustments:

build.xml was changed to point to the previously generated JCA jar,
and debug="true" to include debug information:

   <path id="build.classpath">
        <pathelement location="../org.csstudio.platform.libs.epics/lib/jca-2.3.2.jar"/>
   </path>
 
	<target name="compile" depends="init">
 		<property name="build.compiler" value="modern"/>
 		<javac srcdir="${dir.src}" destdir="${dir.temp}" classpathref="build.classpath" source="1.4" debug="true">
 			<classpath refid="build.classpath"/>
 		</javac>
 	</target>

src/com/cosylab/epics/caj/util/nif/InetAddressUtilV6.java was renamed to *.java6 to
allow compilation with both Java 1.5 and 6.

Created on Intel OS X 10.5.8, java 1.5.0_24 by running

ant
cp target/caj.jar ....../org.csstudio.platform.libs.epics/lib/caj-1.1.7.jar



jca-2.3.2.jar was built from jca-2.3.2 sources with the following changes:
src/core/gov/aps/jca/jni/JNI.cpp, line 1112
  // KUK, Mar 31 2009: Leaked memory from ....addMonitor()
  delete ((MonitorID*)monitorID);

jca-2.3.1.jar was built from the jca-2.3.1 sources with the following changes:
- Updated ThreadSafeContext.java. Based on findings by Tom Pelaia and
  Kay Kasemir, the pause() call in the run() loop was replaced by a blocking
  queue. The original implementation would be quite slow on fast CPUs(!)
  because the code would sit in pause() instead of handling requests.
  Now it wakes immediately when new requests arrive.
- JNITargetArch.java checks for Mac OS X handled osarch="ppc" and "x86",
  but latest Intel Macs seem to report "i386", which is now handled as well.
  
