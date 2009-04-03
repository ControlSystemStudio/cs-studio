This plugin provides EPICS ChannelAccess libraries,
initializing their settings from Eclipse preferences.
For GUI Applications, consider adding org.csstudio.platform.libs.epics.ui.

* JCA
.. is the basic Java Channel Access API, including an implementation
that uses the original EPICS libraries via JNI.

* CAJ
.. is a pure Java implementation of the JCA API.

Both CAJ and JCA seem to work on:
Mac OS X 10.5 Intel
RedHat Enterprise Linux client 5.2 (32-bit x86)
Microsoft Windows XP Professional SP 2

* Specific build instructions

jca-2.3.2.jar
-------------
Built from jca-2.3.2 sources with the following changes:
src/core/gov/aps/jca/jni/JNI.cpp, line 1112
  // KUK, Mar 31 2009: Leaked memory from ....addMonitor()
  delete ((MonitorID*)monitorID);

Set EPICS_BASE_RELEASE, HOST_ARCH, EPICS_HOST_ARCH as required to build
EPICS base.

  cd where-org.csstudio.platform.libs.epics-is-located
  tar vzxf org.csstudio.platform.libs.epics/lib/jca-2.3.2.tgz
  cd jca-2.3.2
  ant
  
This creates two items of interest:
1) Java library O.core/jca.jar, copy to jca-2.3.2.jar in plugin's lib subdir
2) OS-specific JNI lib like O.linux-x86/libjca.so, which typically
   should be re-linked as described below and then placed into the corresponding
   lib/<OS>/<CPU> subdir.

- Mac OS X binary -
Created on Intel OS X 10.5.6 with EPICS base R3.14.9

The 'ant' build ends like this:
  g++ -L/Kram/epics/R3.14.9/base/lib/darwin-x86 -lca -lCom -framework JavaVM \
   -arch i386 -dynamiclib -Wl,-single_module \
   -o /Kram/Eclipse/Workspace/jca-2.3.2/O.darwin-x86/libjca.jnilib \
   /Kram/Eclipse/Workspace/jca-2.3.2/O.darwin-x86/JNI.o

... which resulted in a shared library with further shared lib dependencies:
otool -L O.darwin-x86/libjca.jnilib 
O.darwin-x86/libjca.jnilib:
    ...
    /Kram/epics/R3.14.9/base/lib/darwin-x86/libca.3.14.9.dylib ..
    /Kram/epics/R3.14.9/base/lib/darwin-x86/libCom.3.14.9.dylib ...
    ...

Manually issuing the following command creates a JCA JNI lib with only
"system" dependencies, no remaining EPICS shared lib dependecies:

LIB=/Kram/epics/R3.14.9/base/lib/darwin-x86
g++ -framework JavaVM -arch i386 -dynamiclib -Wl,-single_module -o O.darwin-x86/libjca.jnilib \
    O.darwin-x86/JNI.o $LIB/libCom.a $LIB/libca.a 


- Linux X86 binary -
Created on Red Hat Enterprise Linux AS release 4 (Nahant Update 6)
2.6.9-67.0.7.ELsmp 
g++ (GCC) 3.4.6 20060404 (Red Hat 3.4.6-9)
with EPICS base R3.14.9 (with epicsExit.c fix),
JDK 1.5.0_09.

Similar to the OS X binary, the JNI lib was linked without
further dependencies on shared EPICS base libraries:

cd O.linux-x86
LIB=$EPICS_BASE_RELEASE/lib/$EPICS_HOST_ARCH
g++ -shared -lpthread -lreadline  -lncurses -lm -lrt -Wl,-rpath,. -o libjca.so JNI.o $LIB/libca.a $LIB/libCom.a
# Check:
readelf -d libjca.so 
cp libjca.so ../../org.csstudio.platform.libs.epics/lib/linux/x86
  
caj-1.1.5b.jar
--------------
Built from included sources, where
build.xml was changed to turn debug="on".

cd to directory that contains jca-2.3.2
tar vzxf org.csstudio.platform.libs.epics/lib/caj-1.1.5b-src.tar.gz 
cd caj-1.1.5b
# Maybe edit build.xml, adjust this section:
   <path id="build.classpath">
        <pathelement location="../org.csstudio.platform.libs.epics/lib/jca-2.3.2.jar"/>
   </path>
ant
cp target/caj.jar ../org.csstudio.platform.libs.epics/lib/caj-1.1.5b.jar




Older stuff:
jca-2.3.1.jar was built from the jca-2.3.1 sources with the following changes:
- Updated ThreadSafeContext.java. Based on findings by Tom Pelaia and
  Kay Kasemir, the pause() call in the run() loop was replaced by a blocking
  queue. The original implementation would be quite slow on fast CPUs(!)
  because the code would sit in pause() instead of handling requests.
  Now it wakes immediately when new requests arrive.
- JNITargetArch.java checks for Mac OS X handled osarch="ppc" and "x86",
  but latest Intel Macs seem to report "i386", which is now handled as well.
  
