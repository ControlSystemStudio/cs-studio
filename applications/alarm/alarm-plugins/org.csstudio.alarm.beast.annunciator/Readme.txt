See doc/annunciator.html for purpose, usage, ...

- Versions ---------------------------------------------------------------

1.1.0 2010/02/25
- Turned into CSS GUI plugin
- Changed from org.csstudio.sns.jms2speech to org.csstudio.alarm.annunciator

1.0.4 2009/08/03
- Dependency update for Eclipse 3.5
- On OS X, there have always been small issues with the speech library
  using AWT threading (detail unclear) while Eclipse prefers SWT.
  In the Eclipse 3.5 Mac OS Cocoa version, the app hangs on startup,
  while the Carbon verion runs OK.

1.0.3 2009/06/22
- Version number of org.csstudio.sns.jms2speech Plug-in is displayed on
  application startup.
- Texts starting with "!" are _not_ suppressed in the "There are N more
  messages" handling of flurries.


- Issues -----------------------------------------------------------------

On OS X, it stops after several messages (10? 50?) as shown below.
Temporarily added code to dump the Java memory info on each annunciation.
It's not the JVM that's running out of mem, it must be the OS X audio
system.
Have not seen this problem under Linux, so don't worry for now.

JMS2SPEECH(18851,0xb0d94000) malloc: *** mmap(size=1073745920) failed (error code=12)
*** error: can't allocate region
*** set a breakpoint in malloc_error_break to debug

Program received signal SIGTRAP, Trace/breakpoint trap.
[Switching to process 18851 thread 0x35f43]
0x112c4a04 in JVM_RaiseSignal ()
(gdb) bt
#0  0x112c4a04 in JVM_RaiseSignal ()
#1  0x112fbe31 in JVM_RaiseSignal ()
#2  0x110fc51b in dyld_stub_strlen ()
#3  0x111f66f4 in JNI_CreateJavaVM_Impl ()
#4  0x0006a60d in JNFObtainEnv ()
#5  0x20696183 in Java_com_sun_media_sound_Platform_nGetLibraryForFeature ()
#6  0x7000eff0 in dyld_stub_sprintf ()
#7  0x7000e7f4 in dyld_stub_sprintf ()
#8  0x7000db4b in dyld_stub_sprintf ()
#9  0x952a7001 in AudioConverterChain::CallInputProc ()
#10 0x952a6c44 in AudioConverterChain::FillBufferFromInputProc ()
#11 0x952a6be4 in BufferedAudioConverter::GetInputBytes ()
#12 0x952a6a6d in CBRConverter::RenderOutput ()
#13 0x952a67dc in BufferedAudioConverter::FillBuffer ()
#14 0x952a6964 in AudioConverterChain::RenderOutput ()
#15 0x952a67dc in BufferedAudioConverter::FillBuffer ()
#16 0x952bdb73 in AudioConverterFillComplexBuffer ()
#17 0x7000d658 in dyld_stub_sprintf ()
#18 0x70006662 in dyld_stub_sprintf ()
#19 0x70012307 in AUGenericOutputEntry ()
#20 0x910b815b in HP_IOProc::Call ()
#21 0x910b7e4c in IOA_Device::CallIOProcs ()
#22 0x910b7d28 in HP_IOThread::PerformIO ()
#23 0x910b6103 in HP_IOThread::WorkLoop ()
#24 0x910b5c27 in HP_IOThread::ThreadEntry ()
#25 0x910a6464 in CAPThread::Entry ()
#26 0x955eb095 in _pthread_start ()
#27 0x955eaf52 in thread_start ()
