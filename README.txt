This plugin provides EPICS ChannelAccess libraries,
and initializes their settings from Eclipse preferences.

For GUI Applications, consider adding
org.csstudio.platform.libs.epics.ui.

jca-2.3.1.jar was built from the jca-2.3.1 sources with the following changes:
- Updated ThreadSafeContext.java. Based on findings by Tom Pelaia and
  Kay Kasemir, the pause() call in the run() loop was replaced by a blocking
  queue. The original implementation would be quite slow on fast CPUs(!)
  because the code would sit in pause() instead of handling requests.
  Now it wakes immediately when new requests arrive.
  
caj-1.1.3 sources were changed to turn debug="on".


A caj-1.1.5.jar is included, but without sources because
the CAS support in there is currently not fully funded.


* Basic Build Instructions
Set EPICS_BASE_RELEASE,  env.EPICS_EXTENSIONS, then:
  cd some_place
  tar vzxf org.csstudio.platform.libs.epics/lib/jca-2.3.1.tgz
  cd jca-2.3.1
  ant
  
This creates two items of interest:
1) Java library O.core/jca.jar
2) OS-specific JNI lib like O.linux-x86/libjca.so, which typically
   should be re-linked as described below.

* Mac OS X binary
The 'ant' build ends like this:
  g++ -L/Kram/epics/R3.14.8.2/base/lib/darwin-ppc -lca -lCom -framework JavaVM \
   -arch ppc -dynamiclib -Wl,-single_module \
    -o /Kram/Eclipse/Workbench/jca-2.3.1/O.darwin-ppc/libjca.jnilib \
    /Kram/Eclipse/Workbench/jca-2.3.1/O.darwin-ppc/JNI.o

... which resulted in a shared library with further shared lib dependencies:
otool -L O.darwin-ppc/libjca.jnilib 
O.darwin-ppc/libjca.jnilib:
        /Kram/epics/R3.14.8.2/base/lib/darwin-ppc/libca.3.14.8.dylib ...
        /Kram/epics/R3.14.8.2/base/lib/darwin-ppc/libCom.3.14.8.dylib ...

Manually issuing the following command creates a JCA JNI lib with only
"system" dependencies, no remaining EPICS shared lib dependecies:

g++ -framework JavaVM -arch ppc -dynamiclib -Wl,-single_module \
    -o /Kram/Eclipse/Workbench/jca-2.3.1/O.darwin-ppc/libjca.jnilib \
     /Kram/Eclipse/Workbench/jca-2.3.1/O.darwin-ppc/JNI.o \
     /Kram/epics/R3.14.8.2/base/lib/darwin-ppc/libCom.a \
     /Kram/epics/R3.14.8.2/base/lib/darwin-ppc/libca.a 
     
Similar for Intel x86 macs where ppc turns into x86.

* Linux X86 binary
Created on Red Hat Enterprise Linux AS release 4 (Nahant Update 4)
Linux 2.6.9-42.EL,
g++ (GCC) 3.4.6 20060404 (Red Hat 3.4.6-3)

Similar to the OS X binary, the JNI lib was created without
further dependencies on a shared EPICS base libraries:

cd O.linux-x86
LIB=$EPICS_BASE/lib/$EPICS_HOST_ARCH
g++ -shared -lpthread -lreadline  -lncurses -lm -lrt -Wl,-rpath,. -o libjca.so JNI.o $LIB/libca.a $LIB/libCom.a
# Check:
readelf -d libjca.so 
