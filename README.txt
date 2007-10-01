This plugin provides EPICS ChannelAccess libraries,
and initializes their settings from Eclipse preferences.

For GUI Applications, consider adding
org.csstudio.platform.libs.epics.ui.

* JCA 2.3.1

jca-2.3.1.jar was built from the jca-2.3.1 sources "out of the box"

* Mac OS X binary
The 'ant' build ends like this:
  g++ -L/Kram/epics/R3.14.8.2/base/lib/darwin-ppc -lca -lCom -framework JavaVM -arch ppc -dynamiclib -Wl,-single_module -o /Kram/Eclipse/Workbench/jca-2.3.1/O.darwin-ppc/libjca.jnilib /Kram/Eclipse/Workbench/jca-2.3.1/O.darwin-ppc/JNI.o

... which resulted in a shared library with further shared lib dependencies:
otool -L O.darwin-ppc/libjca.jnilib 
O.darwin-ppc/libjca.jnilib:
        /Kram/Eclipse/Workbench/jca-2.3.1/O.darwin-ppc/libjca.jnilib (compatibility version 0.0.0, current version 0.0.0)
        /Kram/epics/R3.14.8.2/base/lib/darwin-ppc/libca.3.14.8.dylib (compatibility version 3.14.0, current version 3.14.8)
        /Kram/epics/R3.14.8.2/base/lib/darwin-ppc/libCom.3.14.8.dylib (compatibility version 3.14.0, current version 3.14.8)
        /System/Library/Frameworks/JavaVM.framework/Versions/A/JavaVM (compatibility version 1.0.0, current version 96.0.0)
        /usr/lib/libstdc++.6.dylib (compatibility version 7.0.0, current version 7.4.0)
        /usr/lib/libgcc_s.1.dylib (compatibility version 1.0.0, current version 1.0.0)
        /usr/lib/libSystem.B.dylib (compatibility version 1.0.0, current version 88.1.10)

Manually issuing the following command creates a JCA JNI lib with only
"system" dependencies, no remaining EPICS shared lib dependecies:

g++ -framework JavaVM -arch ppc -dynamiclib -Wl,-single_module -o /Kram/Eclipse/Workbench/jca-2.3.1/O.darwin-ppc/libjca.jnilib /Kram/Eclipse/Workbench/jca-2.3.1/O.darwin-ppc/JNI.o /Kram/epics/R3.14.8.2/base/lib/darwin-ppc/libCom.a /Kram/epics/R3.14.8.2/base/lib/darwin-ppc/libca.a 

otool -L O.darwin-ppc/libjca.jnilib
O.darwin-ppc/libjca.jnilib:
        /Kram/Eclipse/Workbench/jca-2.3.1/O.darwin-ppc/libjca.jnilib (compatibility version 0.0.0, current version 0.0.0)
        /System/Library/Frameworks/JavaVM.framework/Versions/A/JavaVM (compatibility version 1.0.0, current version 96.0.0)
        /usr/lib/libstdc++.6.dylib (compatibility version 7.0.0, current version 7.4.0)
        /usr/lib/libgcc_s.1.dylib (compatibility version 1.0.0, current version 1.0.0)
        /usr/lib/libSystem.B.dylib (compatibility version 1.0.0, current version 88.1.10)


* Linux X86 binary
Similar to the OS X binary, the JNI lib was created like this,
so that no more dependencies on a shared EPICS base library remain:

cd O.linux-x86
LIB=$EPICS_BASE/lib/$EPICS_HOST_ARCH
g++ -shared -lpthread -lreadline  -lncurses -lm -lrt -Wl,-rpath,. -o libjca.so JNI.o $LIB/libca.a $LIB/libCom.a
readelf -d libjca.so 
