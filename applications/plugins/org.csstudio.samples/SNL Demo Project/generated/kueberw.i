# 1 "/scratch/workspace/kstr12/source/kueberw.st"
# 1 "<eingebaut>"
# 1 "<Kommandozeile>"
# 1 "/scratch/workspace/kstr12/source/kueberw.st"
# 11 "/scratch/workspace/kstr12/source/kueberw.st"
program kueberw
# 27 "/scratch/workspace/kstr12/source/kueberw.st"
%%#include <stdio.h>
%%#include <time.h>
%%time_t t_abs_secs;
%%#include <string.h>
%%#include <snlMsgLog.h>

char text[40];


double tlimit;
assign tlimit to "KS2:SNL:KUE:TLIMIT_ai";
monitor tlimit;
double thyst;
assign thyst to "KS2:SNL:KUE:THYST_ai";
monitor thyst;
double dtime;
assign dtime to "KS2:SNL:KUE:DTIME_ai";

unsigned short kompBits;


assign kompBits to "KS2:SNL:KUE:KUEBERW_mbbiD";
# 59 "/scratch/workspace/kstr12/source/kueberw.st"
char kstrOK;
assign kstrOK to "KS2:SNL:KUE:KSTROK_bi";
char SoftStop;
assign SoftStop to "KS2:SNL:KUE:SOFTSTOP_bi";
monitor SoftStop;
string SoftStopString;
assign SoftStopString to "KS2:SNL:KUE:SOFTSTOP_si";
char ndoel;
assign ndoel to "KS2:SNL:KUE:KUEBERW_mbbiD.B6";
monitor ndoel;
char hdoel;
assign hdoel to "KS2:SNL:KUE:KUEBERW_mbbiD.B7";
monitor hdoel;

double HDoelTemp;
assign HDoelTemp to "22TI560_ai";
monitor HDoelTemp;
double NDoelTemp;
assign NDoelTemp to "12TI459_ai";
monitor NDoelTemp;




short T1;
assign T1 to "12Y135_bi";
monitor T1;
short T2;
assign T2 to "12Y235_bi";
monitor T2;
short T3;
assign T3 to "12Y335_bi";
monitor T3;



short T5;
assign T5 to "22Y535_bi";
monitor T5;

double T6;
assign T6 to "12TI402_ai";
monitor T6;
double T6hihi;
assign T6hihi to "12TI402_ai.HIHI";
monitor T6hihi;

short freigabeHD;
assign freigabeHD to "22Y545_bo";
short freigabeND;
assign freigabeND to "12KTRIP_dlog.I13";
double hdSchieber;
assign hdSchieber to "22GI519_ai";
monitor hdSchieber;
short CV520OMSL;
assign CV520OMSL to "22CV520_ao.OMSL";
short CV520Endschalter_AUF;
assign CV520Endschalter_AUF to "22CV520_dlog.SB2";
monitor CV520Endschalter_AUF;
short enddruckReglerAM;
assign enddruckReglerAM to "32PC106_pid.AM";
double enddruckReglerSOUT;
assign enddruckReglerSOUT to "32PC106_pid.SOUT";
short CV107OMSL;
assign CV107OMSL to "32CV107_ao.OMSL";
double CV107;
assign CV107 to "32CV107_ao";
short NC157AM;
assign NC157AM to "12NC157_pid.AM";
double NC157SOUT;
assign NC157SOUT to "12NC157_pid.SOUT";
short CV108OMSL;
assign CV108OMSL to "32CV108_ao.OMSL";
double CV108;
assign CV108 to "32CV108_ao";
short CV109OMSL;
assign CV109OMSL to "32CV109_ao.OMSL";
double CV109;
assign CV109 to "32CV109_ao";
short PC503AM;
assign PC503AM to "22PC503_pid.AM";
double PC503SOUT;
assign PC503SOUT to "22PC503_pid.SOUT";
short fumf12sy157OMSL;
assign fumf12sy157OMSL to "12SY157_ao.OMSL";
double fumf12sy157;
assign fumf12sy157 to "12SY157_ao";
short fumf12sy257OMSL;
assign fumf12sy257OMSL to "12SY257_ao.OMSL";
double fumf12sy257;
assign fumf12sy257 to "12SY257_ao";
short fumf12sy357OMSL;
assign fumf12sy357OMSL to "12SY357_ao.OMSL";
double fumf12sy357;
assign fumf12sy357 to "12SY357_ao";


short byp12sy120;
assign byp12sy120 to "12SY120_dlog.CMD";


short byp12sy220;
assign byp12sy220 to "12SY220_dlog.CMD";


short byp12sy320;
assign byp12sy320 to "12SY320_dlog.CMD";



short K1BETR;
assign K1BETR to "12K1BETR_dlog";
short K2BETR;
assign K2BETR to "12K2BETR_dlog";
short K3BETR;
assign K3BETR to "12K3BETR_dlog";


short hdK1BETR;
assign hdK1BETR to "22K1BETR_dlog";
short K1BETRcmd;
assign K1BETRcmd to "12K1BETR_dlog.CMD";
short K2BETRcmd;
assign K2BETRcmd to "12K2BETR_dlog.CMD";
short K3BETRcmd;
assign K3BETRcmd to "12K3BETR_dlog.CMD";
short hdK1BETRcmd;
assign hdK1BETRcmd to "22K1BETR_dlog.CMD";

ss HDTemp {
 state ok {
  entry {
   kompBits |= (1<<7);
   pvPut( kompBits);
  }
  when (HDoelTemp>tlimit) {
   snlMsgLog( "HDoelTemp=%f > %f", HDoelTemp, tlimit);
  } state not_ok
 }
 state not_ok {
  entry {
   kompBits &= ~(1<<7);
   pvPut( kompBits);
  }
  when (HDoelTemp<tlimit-thyst) {
   snlMsgLog( "HDoelTemp=%f < %f", HDoelTemp, tlimit);
  } state ok
 }
}
ss NDTemp {
 state ok {
  entry {
   kompBits |= (1<<6);
   pvPut( kompBits);
  }
  when (NDoelTemp>tlimit) {
   snlMsgLog( "NDoelTemp=%f > %f", NDoelTemp, tlimit);
  } state not_ok
 }
 state not_ok {
  entry {
   kompBits &= ~(1<<6);
   pvPut( kompBits);
  }
  when (NDoelTemp<tlimit-thyst) {
   snlMsgLog( "NDoelTemp=%f < %f", NDoelTemp, tlimit);
  } state ok
 }
}

ss Ueberw {
 state top {
  entry {
   kompBits |= (1<<1)+ (1<<2)+ (1<<3);
   kompBits += (1<<5);
   pvPut( kompBits);
   kstrOK=0;
   pvPut( kstrOK);
   SoftStop=FALSE;
   pvPut( SoftStop);
   snlMsgLog( "selected Kompressors: %d.%d.%d.%d.%d.%d",
    kompBits&1, kompBits&(1<<1), kompBits&(1<<2), kompBits&(1<<3),
    kompBits&(1<<4), kompBits&(1<<5));
   freigabeND = 1;
   pvPut( freigabeND);
  }
  when (1) {
  } state Loop_NOTOK
 }

 state Loop_NOTOK {
  entry{
   kstrOK=0;
   pvPut( kstrOK);
   snlMsgLog( "oeln/hd=%d.%d, T1235=%d.%d.%d.%d, T(He)=%f",
    ndoel, hdoel, T1, T2, T3, T5, T6);
   kompBits &= ~(1<<15);
   pvPut( kompBits);
  }
  when ((ndoel!=0) && (hdoel!=0) &&
     (T1==1) && (T2==1) && (T3==1) &&
     (T5==1) &&
     (T6<(T6hihi-thyst))) {
  } state Loop_OK
 }

 state Loop_OK {
  entry {
   kstrOK=1;
   pvPut( kstrOK);
    freigabeHD = 1;
    pvPut( freigabeHD);
   snlMsgLog( "Kompressoren OK");
   pvGet( dtime);
   pvMonitor( dtime);
   sprintf( SoftStopString, "SoftStop");
   pvPut( SoftStopString);
   kompBits &= ~(1<<15);
   pvPut( kompBits);
  }

  when( (ndoel==0) || (hdoel==0)) {
   snlMsgLog( "Oel-Temp-Bits ND:%d, HD:%d", ndoel, hdoel);
  } state EmergencyStop

  when( (T6>T6hihi)) {
   snlMsgLog( "He-Temp %f>%f", T6, T6hihi);
  } state EmergencyStop

  when((T1==0) || (T2==0) || (T3==0)) {
   snlMsgLog( "ND-Trip K1.K2.K3 = %d.%d.%d", T1, T2, T3);
  } state EmergencyStop

  when( T5==0) {
   snlMsgLog( "HD-Trip");
  } state EmergencyStop

  when (SoftStop) {
   SoftStop = FALSE;
   pvPut( SoftStop);
   sprintf( SoftStopString, "Komp Stop?\nJa: dr�cke nochmal!");
   pvPut( SoftStopString);
   snlMsgLog( "Kompressoren SoftStop");
  } state SoftStop

  exit {
  pvStopMonitor( dtime);
  }
 }
 state EmergencyStop {
  entry {
   kstrOK=0;
   pvPut( kstrOK);

     freigabeHD = 0;
    pvPut( freigabeHD);




  }

   when (TRUE) {
   snlMsgLog( "NotStop");
  } state Stopit1
 }

 state SoftStop {
  entry {
   kompBits |= (1<<15);
   pvPut( kompBits);
   }

  when( SoftStop) {
   sprintf( SoftStopString, "SoftStop bestaetigt");
   pvPut( SoftStopString);
   snlMsgLog( "Softstop bestaetigt");
  } state Stopit1
  when (delay( dtime) && SoftStop==FALSE) {

   sprintf( SoftStopString, "Kein SoftStop!");
   pvPut( SoftStopString);
   kompBits &= ~(1<<15);
   pvPut( kompBits);
   snlMsgLog( "Softstop nicht bestaetigt");
  } state Loop_OK
 }

 state Stopit1 {
  entry {

    CV107OMSL= 0;
    CV107= 0.0;
    pvPut( CV107OMSL);
    pvPut( CV107);

    fumf12sy157OMSL = 0;
    fumf12sy157 = 0.0;
    fumf12sy257OMSL = 0;
    fumf12sy257 = 0.0;
    fumf12sy357OMSL = 0;
    fumf12sy357 = 0.0;
   pvPut( fumf12sy157OMSL);
   pvPut( fumf12sy157);
   pvPut( fumf12sy257OMSL);
   pvPut( fumf12sy257);
   pvPut( fumf12sy357OMSL);
   pvPut( fumf12sy357);


   byp12sy120 = 1;
   pvPut( byp12sy120);
  }
  when (delay( 15)) {

  } state Stopit2
 }
 state Stopit2 {
  entry {

   byp12sy220 = 1;
   pvPut( byp12sy220);
  }
  when (delay( 15)) {

  } state Stopit3
 }
 state Stopit3 {
  entry {

   byp12sy320 = 1;
   pvPut( byp12sy320);
  }
  when (delay( 15) && hdSchieber < 10) {


  } state Stopit4
  when (delay( 30)) {
   snlMsgLog( "HD-Schieber schliesst nicht");
  } state Stopit4
 }
 state Stopit4 {
  entry {


   CV108OMSL = 0;
    CV108 = 0.0;
   pvPut( CV108OMSL);
   pvPut( CV108);

    CV109OMSL = 0;
    CV109 = 0.0;
   pvPut( CV109OMSL);
   pvPut( CV109);

    NC157AM = 1;
    NC157SOUT = 0.0;
    pvPut( NC157AM);
    pvPut( NC157SOUT);

    CV520OMSL = 1;
   pvPut( CV520OMSL);

    PC503AM = 1;
    PC503SOUT = 0.0;
    pvPut( PC503AM);
    pvPut( PC503SOUT);
  }
  when( CV520Endschalter_AUF) {
  } state kStop
  when (delay( 30)) {
   snlMsgLog( "HD-Regler oeffnet nicht");
  } state kStop
 }
 state kStop {
  entry {
# 444 "/scratch/workspace/kstr12/source/kueberw.st"
   K1BETRcmd = 0;
   K2BETRcmd = 0;
   K3BETRcmd = 0;
   hdK1BETRcmd = 0;
   pvPut( K1BETRcmd);
   pvPut( K2BETRcmd);
   pvPut( K3BETRcmd);
   pvPut( hdK1BETRcmd);
   }
   when( K1BETR!=8 &&
      K2BETR!=8 &&
      K3BETR!=8 &&
      hdK1BETR!=8) {
   } state Stopit5
   when( delay(2)) {
   snlMsgLog( "Kompressoren stoppen nicht");
   } state kStop
  }
 state Stopit5 {
  entry {

    enddruckReglerAM = 1;
    enddruckReglerSOUT = 50.0;
    pvPut( enddruckReglerAM);
    pvPut( enddruckReglerSOUT);


   snlMsgLog( "Stopit fertig");
   SoftStop = FALSE;
   pvPut( SoftStop);
  }
  when (delay( 1)) {
  } state Loop_NOTOK
 }
}
