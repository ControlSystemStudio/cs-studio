# 1 "/scratch/workspace/kstr12/source/cueberw.st"
# 1 "<eingebaut>"
# 1 "<Kommandozeile>"
# 1 "/scratch/workspace/kstr12/source/cueberw.st"
# 11 "/scratch/workspace/kstr12/source/cueberw.st"
program cueberw
# 20 "/scratch/workspace/kstr12/source/cueberw.st"
%%#include <string.h>
%%#include <snlMsgLog.h>

char text[40];

char cboxOK; assign cboxOK to "KS2:SNL:CUE:CBOXOK_bi";
char cboxState; assign cboxState to "KS2:SNL:CUE:CUEBERW_mbbi";
char kstrOK; assign kstrOK to "KS2:SNL:KUE:KSTROK_bi";
      monitor kstrOK;
char TK1run; assign TK1run to "KS2:SNL:TK1:RUN_bi";
char SoftStop; assign SoftStop to "KS2:SNL:CUE:SOFTSTOP_bi";
      monitor SoftStop;
string SoftStopString; assign SoftStopString to "KS2:SNL:CUE:SOFTSTOP_si";
evflag softstop;
char pas; assign pas to "42PAS700_bi";
      monitor pas;
double QI108;
double QI109;
double QI208;
double QI209;




short CV340OMSL; assign CV340OMSL to "42CV340_ao.OMSL";
double CV340; assign CV340 to "42CV340_ao";
short CV400OMSL; assign CV400OMSL to "42CV400_ao.OMSL";
double CV400; assign CV400 to "42CV400_ao";
short CV104OMSL; assign CV104OMSL to "42CV104_ao.OMSL";
double CV104; assign CV104 to "42CV104_ao";
short TC528SMSL; assign TC528SMSL to "42TC528_pid.SMSL";

double TC528; assign TC528 to "42TC528_pid.SOUT";
short TC026SMSL; assign TC026SMSL to "42TC026_pid.SMSL";
double TC026; assign TC026 to "42TC026_pid.SOUT";
char UZV122; assign UZV122 to "32UZV122_dlog.CMD";
char UZV122ZU; assign UZV122ZU to "32G122L_bi";
      monitor UZV122ZU;
char CV106ZU; assign CV106ZU to "42G106L_bi";
      monitor CV106ZU;

ss cueberw {

 state ini {
  entry {
   cboxState = 0;
   pvPut( cboxState);
   snlMsgLog( "cueberw started");
   SoftStop = FALSE;
   pvPut( SoftStop);
  }
  when( delay(2)) {
  } state not_ok
 }

 state not_ok {
  entry {
   sprintf( SoftStopString, "Box stopped");
   pvPut( SoftStopString);
   cboxState = 1;
   pvPut( cboxState);
   snlMsgLog( "cueberw not_ok");
  }
  when( (SoftStop == FALSE) &&
    (pas == 1) &&
    (kstrOK == 1)
   ) {
  } state ok
 }

 state ok {
  entry {
   cboxState = 2;
   pvPut( cboxState);
   snlMsgLog( "cueberw ok");
   cboxOK = 1;
   pvPut( cboxOK);
   sprintf( SoftStopString, "SoftStop");
   pvPut( SoftStopString);
   efClear( softstop);
  }
  when( SoftStop && CV106ZU) {
   SoftStop = FALSE;
   pvPut( SoftStop);
   snlMsgLog( "Coldbox SoftStop?");
   sprintf( SoftStopString, "Coldbox Stop?\nJa: druecke nochmal!");
   pvPut( SoftStopString);
  } state softstop
  when( SoftStop && !CV106ZU) {
   SoftStop = FALSE;
   pvPut( SoftStop);
   snlMsgLog( "Turbinenkreise laufen noch: %d", CV106ZU);
   sprintf( SoftStopString, "Tu-Kreise laufen\nTrotzdem Coldbox Stop?");
   pvPut( SoftStopString);
  } state check_turbinenkreise
  when( pas != 1) {
   snlMsgLog( "Steuerluft ausgefallen");
  } state coldbox_ausschalten
  when( kstrOK != 1) {
   snlMsgLog( "Kompressorstrasse ausgefallen");
  } state coldbox_ausschalten
 }

 state softstop {
  entry {
   cboxState = 3;
   pvPut( cboxState);
  }
  when( SoftStop) {
   SoftStop = FALSE;
   pvPut( SoftStop);
   sprintf( SoftStopString, "SoftStop bestaetigt");
   pvPut( SoftStopString);
   snlMsgLog( "Softstop bestaetigt");
   efSet( softstop);
  } state coldbox_ausschalten
  when (delay( 30) && SoftStop==FALSE) {

   sprintf( SoftStopString, "Kein SoftStop!");
   pvPut( SoftStopString);
   snlMsgLog( "Softstop nicht bestaetigt");
  } state ok
 }

 state check_turbinenkreise {
  entry {
   cboxState = 4;
   pvPut( cboxState);
  }
  when( SoftStop) {
   SoftStop = FALSE;
   pvPut( SoftStop);
   sprintf( SoftStopString, "Stop Tu-Kreise\nbestaetigt");
   pvPut( SoftStopString);
   snlMsgLog( "Stop Tu-Kreise bestaetigt");
   efSet( softstop);
  } state stop_turbinenkreise
  when (delay( 30) && SoftStop==FALSE) {

   sprintf( SoftStopString, "Kein Tu_Kreise-Stop\nKein SoftStop!");
   pvPut( SoftStopString);
   snlMsgLog( "Stop Tu-Kreise nicht bestaetigt");
  } state ok
 }
 state stop_turbinenkreise {
  entry {
   cboxState = 5;
   pvPut( cboxState);
   TK1run = 0;
   pvPut( TK1run);
  }
  when( CV106ZU) {
   snlMsgLog( "Turbinenkreise sind geschlossen!");
  } state coldbox_ausschalten
  when( delay( 60) && !CV106ZU) {
   snlMsgLog( "Turbinenkreise immer noch nicht geschlossen!");
  } state stop_turbinenkreise
 }

 state coldbox_ausschalten {
  entry {
   cboxState = 6;
   pvPut( cboxState);
   cboxOK = 0;
   pvPut( cboxOK);



   UZV122 = 0;
   pvPut( UZV122);

     CV340OMSL= 0;
      CV340 = 0.0;
      pvPut( CV340OMSL);
      pvPut( CV340);
    CV400OMSL= 0;
    CV400 = 0.0;
    pvPut( CV400OMSL);
    pvPut( CV400);
   CV104OMSL= 0;
    CV104 = 0.0;
    pvPut( CV104OMSL);
    pvPut( CV104);

   TC528SMSL=0;
   TC528 = 0.0;
    pvPut( TC528SMSL);
    pvPut( TC528);
   TC026SMSL=0;
   TC026 = 0.0;
    pvPut( TC026SMSL);
    pvPut( TC026);
  }
  when( delay( 5) && !efTest( softstop)) {

  } state stopit
  when( efTest( softstop) && UZV122ZU) {
   cboxState = 7;
   pvPut( cboxState);

   snlMsgLog( "Coldbox wird abgesaugt");
  } state stopit
 }

 state stopit {
  entry {
   cboxState = 8;
   pvPut( cboxState);
  }
  when( TRUE) {
  } state not_ok
 }
}
ss ads_ueberw {

 state cbox_off {
  entry {
   snlMsgLog( "cbox off -> no adsorber");
  }
  when( cboxOK == 1) {
  } state cbox_on
 }

 state cbox_on {
  entry {
   snlMsgLog( "cbox off -> check adsorbers");
  }
  when( cboxOK != 1) {
  } state cbox_off
  when( QI108 >20. ) {
   snlMsgLog( " * * * Analyse AD100 > 20 ppm * * *");

   } state cbox_on
  when( QI109 >20. ) {
   snlMsgLog( " * * * Analyse AD110 > 20 ppm * * *");

   } state cbox_on
  when( QI208 >20. ) {
   snlMsgLog( " * * * Analyse AD200 > 20 ppm * * *");

   } state cbox_on
  when( QI209 >20. ) {
   snlMsgLog( " * * * Analyse AD210 > 20 ppm * * *");

   } state cbox_on
 }
}
