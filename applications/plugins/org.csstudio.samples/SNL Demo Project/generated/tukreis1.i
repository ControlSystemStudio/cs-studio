# 1 "/scratch/workspace/kstr12/source/tukreis1.st"
# 1 "<eingebaut>"
# 1 "<Kommandozeile>"
# 1 "/scratch/workspace/kstr12/source/tukreis1.st"
# 100 "/scratch/workspace/kstr12/source/tukreis1.st"
program tukreis1
# 111 "/scratch/workspace/kstr12/source/tukreis1.st"
%%#include <string.h>
%%#include <math.h>
%%#include <snlMsgLog.h>



evflag tk1Freigabe;

char TK1state; assign TK1state to "KS2:SNL:TK1:STATE_mbbi";
char kstrOK; assign kstrOK to "KS2:SNL:KUE:KSTROK_bi";
      monitor kstrOK;
char cboxOK; assign cboxOK to "KS2:SNL:CUE:CBOXOK_bi";
      monitor cboxOK;
char TK1run; assign TK1run to "KS2:SNL:TK1:RUN_bi";
      monitor TK1run;
string buttonString; assign buttonString to "KS2:SNL:TK1:RUN_si";
char UZV120offen; assign UZV120offen to "32UZV120_dlog.SB2";
      monitor UZV120offen;
char UZV121offen; assign UZV121offen to "32UZV121_dlog.SB2";
      monitor UZV121offen;
char UZV122offen; assign UZV122offen to "32UZV122_dlog.SB2";
      monitor UZV122offen;
char adsorberZu; assign adsorberZu to "42SV106_dlog.SB4";
      monitor adsorberZu;
char freigabeCMD; assign freigabeCMD to "42SV106_dlog.CMD";
char freigabe; assign freigabe to "42SV106_dlog.SB0";
      monitor freigabe;
char startFreigabe; assign startFreigabe to "42SV106_dlog.I13";
      monitor startFreigabe;
char programRunnig; assign programRunnig to "42SV106_dlog.I14";
char CV106zu; assign CV106zu to "42G106L_bi";
      monitor CV106zu;
double CV106; assign CV106 to "42CV106_ao.VAL";
      monitor CV106;
char CV127offen; assign CV127offen to "42CV127_dlog.SB2";
      monitor CV127offen;
char summenAlarm; assign summenAlarm to "42XA106_bi.VAL";
      monitor summenAlarm;
char UZV618offen; assign UZV618offen to "42UZV618_dlog.SB1";
      monitor UZV618offen;
char UZV628offen; assign UZV628offen to "42UZV628_dlog.SB1";
      monitor UZV628offen;
char UZV638offen; assign UZV638offen to "42UZV638_dlog.SB1";
      monitor UZV638offen;
char lagerg1BitSIklein; assign lagerg1BitSIklein to "42UZV618_dlog.I13";
char lagerg2BitSIklein; assign lagerg2BitSIklein to "42UZV628_dlog.I13";
char lagerg3BitSIklein; assign lagerg3BitSIklein to "42UZV638_dlog.I13";
char lagerg1BitSIgross; assign lagerg1BitSIgross to "42UZV618_dlog.I14";
char lagerg2BitSIgross; assign lagerg2BitSIgross to "42UZV628_dlog.I14";
char lagerg3BitSIgross; assign lagerg3BitSIgross to "42UZV638_dlog.I14";
char PDA619; assign PDA619 to "42PDA619_bi.VAL";
      monitor PDA619;
char PDA629; assign PDA629 to "42PDA629_bi.VAL";
      monitor PDA629;
char PDA639; assign PDA639 to "42PDA639_bi.VAL";
      monitor PDA639;
char FA613; assign FA613 to "42FA613_bi.VAL";
      monitor FA613;
double SI1; assign SI1 to "42SI610_ai.VAL";
      monitor SI1;
double SI2; assign SI2 to "42SI620_ai.VAL";
      monitor SI2;
double SI3; assign SI3 to "42SI630_ai.VAL";
      monitor SI3;
double PI012roc; assign PI012roc to "42PI012_calc.HIHI";
      monitor PI012roc;
double PI127roc; assign PI127roc to "42PI127_calc.HIHI";
      monitor PI127roc;
double TI126LOW; assign TI126LOW to "42TI126_temp.LOW";
      monitor TI126LOW;
double TI126; assign TI126 to "42TI126_temp.VAL";
      monitor TI126;

double TC126ERR; assign TC126ERR to "42TC126_pid.ERR";
      monitor TC126ERR;
double TC126OUT; assign TC126OUT to "42TC126_pid.OUT";
      monitor TC126OUT;
double TC126OROC; assign TC126OROC to "42CV106_ao.OROC";
double CV106OVAL; assign CV106OVAL to "42CV106_ao.OVAL";
      monitor CV106OVAL;
double TC126SOUT; assign TC126SOUT to "42TC126_pid.SOUT";
int TC126AM; assign TC126AM to "42TC126_pid.AM";
double SC610OUT; assign SC610OUT to "42SC610_pid.OUT";
      monitor SC610OUT;
double SC620OUT; assign SC620OUT to "42SC620_pid.OUT";
      monitor SC620OUT;
double SC630OUT; assign SC630OUT to "42SC630_pid.OUT";
      monitor SC630OUT;
double PI105; assign PI105 to "42PI105_ai.VAL";
      monitor PI105;
double PI012; assign PI012 to "42PI012_ai.VAL";
      monitor PI012;

ss tukreis1 {

 state gestoppt {
  entry {
   TK1run = 0;
   sprintf( buttonString, "Start\nTuKr 1");
   pvPut( TK1run);
   pvPut( buttonString);
   snlMsgLog( "TuKr 1 gestoppt");
   TK1state = 1;
   pvPut( TK1state);
  }
  when( efTest( tk1Freigabe)) {
  } state bereit
 }
 state bereit {
  entry {
   snlMsgLog( "TuKr 1 freigegeben");
   TK1state = 2;
   pvPut( TK1state);
  }
  when( !efTest( tk1Freigabe)) {
  } state gestoppt
  when( startFreigabe) {
  } state startBereit
  when( delay( 60.0)) {
  } state bereit
 }
 state startBereit {
  entry {
   snlMsgLog( "TuKr 1 startbereit");
   TK1state = 3;
   pvPut( TK1state);
  }
  when( !efTest( tk1Freigabe)) {
  } state gestoppt
  when( !startFreigabe) {
  } state bereit
  when( !CV106zu || CV106>0.01) {
   snlMsgLog( "Eintrittsventil nicht geschlossen");
  } state bereit
  when( !UZV618offen || !UZV628offen || !UZV638offen) {
   snlMsgLog( "Lagergasventile nicht alle offen");
  } state bereit
  when( PDA619!=1 || PDA629!=1 || PDA639!=1) {
   snlMsgLog( "Differenzdruecke nicht alle OK");
  } state bereit
  when( FA613!=1) {
   snlMsgLog( "Kuehlwasser Sammelalarm");
  } state bereit
  when( TK1run) {
   snlMsgLog( "Starte Turbinenkreis");
  } state run
 }
 state run {
  entry {
   snlMsgLog( "TuKr 1 laeuft");
   TK1state = 4;
   pvPut( TK1state);
   sprintf( buttonString, "Stop\nTuKr 1");
   pvPut( buttonString);
   }
  when( !efTest( tk1Freigabe)) {
  } state stopit


  when( TI126 < TI126LOW) {
  } state manDrosselBetrieb
  when( TC126ERR < -2.0) {
  } state manDrosselBetrieb
  when( SC610OUT < 2.0 || SC620OUT < 2.0 || SC630OUT < 2.0) {
  } state manDrosselBetrieb

  when( abs(TC126ERR) < 2.0) {
  } state autoBetrieb


  when( TC126ERR > 2.0
   && ( SC610OUT > 5.0 && SC620OUT > 5.0 && SC630OUT > 5.0)
   && PI105 > (PI012-0.3)
   && TC126OUT < 90.0) {
  } state manOeffneBetrieb
  when( !TK1run) {
   snlMsgLog( "Stoppe Turbinenkreis");
  } state softStop
 }
 state autoBetrieb {
  entry {
   TC126AM = 0;
   TC126OROC = 0;
   pvPut( TC126OROC);
   pvPut( TC126AM);
   snlMsgLog( "TuKr 1 im Regelbetrieb");
   TK1state = 5;
   pvPut( TK1state);
   }
  when( !efTest( tk1Freigabe)) {
  } state stopit


  when( TI126 < TI126LOW) {
  } state manDrosselBetrieb
  when( TC126ERR < -2.0) {
  } state manDrosselBetrieb
  when( SC610OUT < 2.0 || SC620OUT < 2.0 || SC630OUT < 2.0) {
  } state manDrosselBetrieb


  when( TC126ERR > 2.0
   && ( SC610OUT > 5.0 && SC620OUT > 5.0 && SC630OUT > 5.0)
   && PI105 > (PI012-0.3)
   && TC126OUT < 90.0) {
  } state manOeffneBetrieb
  when( !TK1run) {
   snlMsgLog( "Stoppe Turbinenkreis");
  } state softStop
 }
 state manDrosselBetrieb {
  entry {
   TC126AM = 1;
   pvPut( TC126AM);
   pvGet( TC126OUT);
   TC126OROC = 0.5;
   TC126SOUT = TC126SOUT-2.0;
   pvPut( TC126OROC);
   pvPut( TC126SOUT);
   snlMsgLog( "TuKr 1 im Drosselbetrieb");
   TK1state = 6;
   pvPut( TK1state);
  }
  when( !efTest( tk1Freigabe)) {
  } state stopit




  when( TI126 > TI126LOW
   && SC610OUT > 2.0 && SC620OUT > 2.0 && SC630OUT > 2.0
   && abs(TC126ERR) < 2.0) {
  } state autoBetrieb
  when( delay(5.0)) {
   TC126SOUT = TC126SOUT-2.0;
   pvPut( TC126SOUT);
  } state manDrosselBetrieb


  when( TC126ERR > 2.0
   && ( SC610OUT > 5.0 && SC620OUT > 5.0 && SC630OUT > 5.0)
   && PI105 > (PI012-0.3)
   && TC126OUT < 90.0) {
  } state manOeffneBetrieb
  when( !TK1run) {
   snlMsgLog( "Stoppe Turbinenkreis");
  } state softStop
 }
 state manOeffneBetrieb {
  entry {
   TC126AM = 1;
   pvPut( TC126AM);
   pvGet( TC126OUT);
   TC126OROC = 0.2;
   TC126SOUT = TC126SOUT+1.0;
   pvPut( TC126OROC);
   pvPut( TC126SOUT);
   snlMsgLog( "TuKr 1 im man. Oeffnebetrieb");
   TK1state = 7;
   pvPut( TK1state);
  }
  when( !efTest( tk1Freigabe)) {
  } state stopit


  when( TI126 < TI126LOW) {
  } state manDrosselBetrieb
  when( TC126ERR < -2.0) {
  } state manDrosselBetrieb
  when( SC610OUT < 2.0 || SC620OUT < 2.0 || SC630OUT < 2.0) {
  } state manDrosselBetrieb

  when( abs(TC126ERR) < 2.0) {
  } state autoBetrieb
  when( delay(6.0)) {
   TC126SOUT = TC126SOUT+1.0;
   pvPut( TC126SOUT);
  } state manOeffneBetrieb
  when( !TK1run) {
   snlMsgLog( "Stoppe Turbinenkreis");
  } state softStop
  }
 state softStop {
  entry {
   TC126AM = 1;
   TC126OROC = 0.5;
   TC126SOUT = 0.0;
   pvPut( TC126AM);
   pvPut( TC126OROC);
   pvPut( TC126SOUT);
   snlMsgLog( "TuKr 1 soft Stop");
   TK1state = 8;
   pvPut( TK1state);
  }
  when( CV106OVAL == 0.0 || delay(210.0)) {
   freigabeCMD = 1;
   pvPut( freigabeCMD);
  } state gestoppt
 }
 state stopit {
  entry {
   freigabeCMD = 1;
   pvPut( freigabeCMD);
   TC126AM = 1;
   TC126SOUT = 0.0;
   pvPut( TC126AM);
   pvPut( TC126SOUT);
   snlMsgLog( "TuKr 1 Stop");
   TK1state = 9;
   pvPut( TK1state);
  }
  when( TRUE) {
  } state gestoppt
 }
}
ss tk1ueberw {
 state not_ok {
  entry {
   efClear( tk1Freigabe);
  }
  when( UZV120offen && UZV121offen && UZV122offen
   && !adsorberZu
   && freigabe
   && CV127offen
   && summenAlarm==1
   && !PI012roc && !PI127roc
   && kstrOK && cboxOK) {
    snlMsgLog( "Turbinenkreis 1 freigegeben");
  } state ok
  when ( TK1run) {
   snlMsgLog( "UZV120,1,2: %d,%d,%d",
      UZV120offen, UZV121offen, UZV122offen);
   snlMsgLog( "Adsorber offen: %d", !adsorberZu);
   snlMsgLog( "Freigabe: %d", freigabe);
   snlMsgLog( "CV127 offen: %d", CV127offen);
   snlMsgLog( "Summenalarm: %d", summenAlarm);
   snlMsgLog( "ROC PI012,PI127: %f,%f", PI012roc, PI127roc);
   snlMsgLog( "KS2ok,CBXok: %d,%d", kstrOK, cboxOK);
   TK1run = 0;
   pvPut( TK1run);
   } state not_ok
 }
 state ok {
  entry {
   efSet( tk1Freigabe);
  }
  when( !UZV120offen || !UZV121offen || !UZV122offen) {
   snlMsgLog( "Coldbox Eingangsventile nicht (alle) offen");
  } state not_ok
  when( adsorberZu) {
   snlMsgLog( "Adsorber nicht offen");
  } state not_ok
  when( !freigabe) {
   snlMsgLog( "keine Freigabe TuKreis Eingangsventil");
  } state not_ok
  when( !CV127offen ) {
   snlMsgLog( "TuKreis Ausgangsventil nicht offen");
  } state not_ok
  when( summenAlarm==0) {
   snlMsgLog( "TuKreis Summenalarm");
  } state not_ok
  when( PI012roc || PI127roc) {
   snlMsgLog( "Rate of Change Alarm fuer PI012 oder PI127");
  } state not_ok
  when( !kstrOK) {
   snlMsgLog( "Kompressor Strasse nicht OK");
  } state not_ok
  when( !cboxOK) {
   snlMsgLog( "Coldbox nicht OK");
  } state not_ok
 }
}
ss tuDrehzahlen {
 state gross {
  entry {
   lagerg1BitSIklein = 0;
   pvPut( lagerg1BitSIklein);
   lagerg1BitSIgross = 1;
   pvPut( lagerg1BitSIgross);
   snlMsgLog( "Turbinendrehzahlen gross");
  }
  when ( (SI1<400.0 || SI2<400.0 || SI3<400.0)
   && (SI1>100.0 || SI2>100.0 || SI3>100.0)) {
  } state mittel
  when ( SI1<100.0 && SI2<100.0 && SI3<100.0){
  } state klein
  when (delay( 60.0)) {
   programRunnig=1;
   startFreigabe=0;
   pvPut( programRunnig);
   pvPut( startFreigabe);
  } state gross
 }
 state mittel {
  entry {
   lagerg1BitSIklein = 1;
   pvPut( lagerg1BitSIklein);
   lagerg1BitSIgross = 1;
   pvPut( lagerg1BitSIgross);
   snlMsgLog( "Turbinendrehzahlen mittel");
  }
  when ( SI1>400.0 && SI2>400.0 && SI3>400.0){
  } state gross
  when ( SI1<100.0 && SI2<100.0 && SI3<100.0){
  } state klein
  when (delay( 60.0)) {
   programRunnig=1;
   startFreigabe=0;
   pvPut( programRunnig);
   pvPut( startFreigabe);
  } state mittel
 }
 state klein {
  entry {
   lagerg1BitSIklein = 1;
   pvPut( lagerg1BitSIklein);
   lagerg1BitSIgross = 0;
   pvPut( lagerg1BitSIgross);
   snlMsgLog( "Turbinendrehzahlen klein");
  }
  when ( SI1>400.0 && SI2>400.0 && SI3>400.0){
  } state gross
  when ( (SI1<400.0 || SI2<400.0 || SI3<400.0)
   && (SI1>100.0 || SI2>100.0 || SI3>100.0)) {
  } state mittel
  when( CV106zu && CV106<0.01
   && UZV618offen && UZV628offen && UZV638offen
   && PDA619==1 && PDA629==1 && PDA639==1
   && FA613==1) {
  } state bereit
  when (delay( 60.0)) {
   programRunnig=1;
   startFreigabe=0;
   pvPut( programRunnig);
   pvPut( startFreigabe);
  } state klein
 }
 state bereit {
  entry {
   lagerg1BitSIklein = 1;
   pvPut( lagerg1BitSIklein);
   lagerg1BitSIgross = 0;
   pvPut( lagerg1BitSIgross);
   snlMsgLog( "Turbinendrehzahlen bereit");
  }
  when ( SI1>400.0 && SI2>400.0 && SI3>400.0){
  } state gross
  when ( (SI1<400.0 || SI2<400.0 || SI3<400.0)
   && (SI1>100.0 || SI2>100.0 || SI3>100.0)) {
  } state mittel
  when( !CV106zu || CV106>0.01 ) {
   snlMsgLog( "CV106 nicht geschlossen");
  } state klein
  when( !UZV618offen || !UZV628offen || !UZV638offen) {
   snlMsgLog( "UZV6x8 nicht offen");
  } state klein
  when( PDA619!=1 || PDA629!=1 || PDA639!=1) {
   snlMsgLog( "Differenzdruck PDA6x9 nicht OK");
  } state klein
  when( FA613!=1) {
   snlMsgLog( "Kuehlwassersammelalarm FA613");
  } state klein
  when (delay( 60.0)) {
   programRunnig=1;
   startFreigabe=1;
   pvPut( programRunnig);
   pvPut( startFreigabe);
  } state bereit
 }
}
