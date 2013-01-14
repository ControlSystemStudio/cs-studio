/* $$Revision: 1.43 $$ $$Date: 2009/10/15 16:41:41 $$ **

* File:        cueberw.st
* Author:      Christian Gerke
* Created:     2009/03/04
*
* Revision history:
*
*
*******************************************************************************/

program cueberw

#define OK 1
#define NOT_OK 0
#define SUPERVISORY 0
#define CLOSED_LOOP 1
#define PID_MANUELL 1
#define AUF 1
#define ZU 0
#define EIN 1
#define SCAN_PASSIVE 0
#define SCAN_1_SECOND 6

#define PV_SET(pv, val) pv = val; pvPut (pv);
#define PV_SET_SYNC(pv, val) pv = val; pvPut (pv, SYNC);

%%#include <string.h>
%%#include <alarm.h>
%%#include <snlMsgLog.h>

char text[40];
short absaugventilnr;
short i;
short softStopErkannt;

evflag ad300StopFlag;

double QI108;			assign QI108			to "42QI108_ai";					monitor QI108;
double QI109;			assign QI109			to "42QI109_ai";					monitor QI109;
double QI208;			assign QI208			to "42QI208_ai";					monitor QI208;
double QI209;			assign QI209			to "42QI209_ai";					monitor QI209;
short  Ad1Betr;	/*100*/	assign Ad1Betr			to "KS2:SNL:ADS:AD1BETR_mbbi";
short  Ad2Betr;	/*110*/	assign Ad2Betr			to "KS2:SNL:ADS:AD2BETR_mbbi";
short  Ad3Betr;	/*200*/	assign Ad3Betr			to "KS2:SNL:ADS:AD3BETR_mbbi";
short  Ad4Betr;	/*210*/	assign Ad4Betr			to "KS2:SNL:ADS:AD4BETR_mbbi";
short  Ad1Mess;			assign Ad1Mess			to "42AD100Mess_bi";
short  Ad2Mess;			assign Ad2Mess			to "42AD110Mess_bi";
short  Ad3Mess;			assign Ad3Mess			to "42AD200Mess_bi";
short  Ad4Mess;			assign Ad4Mess			to "42AD210Mess_bi";
short cboxOK;			assign cboxOK			to "KS2:SNL:CUE:CBOXOK_bi";
short cboxOKout;		assign cboxOKout		to "42CBOXOK_bo";	/* fuer Trip Signal an D3 */
short cboxState;		assign cboxState		to "KS2:SNL:CUE:CUEBERW_mbbi";
short kstrOK;			assign kstrOK			to "KS2:SNL:KUE:KSTROK_bi";			monitor kstrOK;
short TK1run;			assign TK1run			to "KS2:SNL:TK1:RUN_bi";
short TK2run;			assign TK2run			to "KS2:SNL:TK2:RUN_bi";
short TK3run;			assign TK3run			to "KS2:SNL:TK3:RUN_bi";
short TK4run;			assign TK4run			to "KS2:SNL:TK4:RUN_bi";
short SoftStop;			assign SoftStop			to "KS2:SNL:CUE:SOFTSTOP_bi";		monitor SoftStop;
string SoftStopString;	assign SoftStopString	to "KS2:SNL:CUE:SOFTSTOP_si";

short pas;				assign pas				to "42PAS700_bi";					monitor pas;

short  CV340OMSL;		assign CV340OMSL		to "42CV340_ao.OMSL"; 
double CV340; 			assign CV340			to "42CV340_ao"; 
string PI072SCAN;		assign PI072SCAN		to "42PI072_ai.SCAN";
double CV300;			assign CV300			to "42CV300_ao";
double CV301;			assign CV301			to "42CV301_ao";
double CV302;			assign CV302			to "42CV302_ao";
double CV300OVAL;		assign CV300OVAL		to "42CV300_ao.OVAL";
double CV301OVAL;		assign CV301OVAL		to "42CV301_ao.OVAL";
double CV302OVAL;		assign CV302OVAL		to "42CV302_ao.OVAL";
//short  CV300SCAN;		assign CV300SCAN		to "42CV300_ao.SCAN";
//short  CV301SCAN;		assign CV301SCAN		to "42CV301_ao.SCAN";
//short  CV302SCAN;		assign CV302SCAN		to "42CV302_ao.SCAN";
short  CV300auf;		assign CV300auf			to "42CV300_dlog.SB2";
short  CV301zu;			assign CV301zu			to "42CV301_dlog.SB0";
short  CV302zu;			assign CV302zu			to "42CV302_dlog.SB0";
short  CV400OMSL;		assign CV400OMSL		to "42CV400_ao.OMSL";
double CV400OROC;		assign CV400OROC		to "42CV400_ao.OROC";
short  CV400SCAN;		assign CV400SCAN		to "42CV400_ao.SCAN";
double CV400OVAL;		assign CV400OVAL		to "42CV400_ao.OVAL";
double CV400;			assign CV400			to "42CV400_ao";
short  PI072STAT;		assign PI072STAT		to "42PI072_ai.STAT";				monitor PI072STAT;
double PI012;		    assign PI012			to "42PI012_ai";
double PI302;			assign PI302			to "42PI302_ai";
short  CV408OMSL;		assign CV408OMSL		to "42CV408_ao.OMSL";
string CV408SCAN;		assign CV408SCAN		to "42CV408_ao.SCAN";
double CV408OROC;		assign CV408OROC		to "42CV408_ao.OROC";
double CV408OVAL;		assign CV408OVAL		to "42CV408_ao.OVAL";
double CV408;			assign CV408			to "42CV408_ao";
short  CV104OMSL;		assign CV104OMSL		to "42CV104_ao.OMSL";
double CV104;			assign CV104			to "42CV104_ao";
double TC528;			assign TC528			to "42TC528_pid.SOUT";
short  TC026SMSL;		assign TC026SMSL		to "42TC026_pid.SMSL";
double TC026;			assign TC026			to "42TC026_pid.SOUT";
short  UZV122;			assign UZV122			to "32UZV122_dlog.CMD";
short  UZV121;			assign UZV121			to "32UZV121_dlog.CMD";
short  UZV120;			assign UZV120			to "32UZV120_dlog.CMD";
short  UZV122zu;		assign UZV122zu			to "32UZV122_dlog.SB0";
short  UZV121zu;		assign UZV121zu			to "32UZV121_dlog.SB0";
short  UZV120zu;		assign UZV120zu			to "32UZV120_dlog.SB0";
double CV106OVAL;		assign CV106OVAL		to "42CV106_ao.OVAL";
double CV206OVAL;		assign CV206OVAL		to "42CV206_ao.OVAL";
double CV306OVAL;		assign CV306OVAL		to "42CV306_ao.OVAL";
double CV406OVAL;		assign CV406OVAL		to "42CV406_ao.OVAL";
short  CV106zu;			assign CV106zu			to "42CV106_dlog.SB0";				monitor CV106zu;
short  CV206zu;			assign CV206zu			to "42CV206_dlog.SB0";				monitor CV206zu;
short  CV306zu;			assign CV306zu			to "42CV306_dlog.SB0";				monitor CV306zu;
short  CV406zu;			assign CV406zu			to "42CV406_dlog.SB0";				monitor CV406zu;
short  CV104zu;			assign CV104zu			to "42G104L_bi";					monitor CV104zu;
short  CV150zu;			assign CV150zu			to "42G150L_bi";					monitor CV150zu;
short  CV250zu;			assign CV250zu			to "42G250L_bi";					monitor CV250zu;
short  CV350zu;			assign CV350zu			to "42G350L_bi";					monitor CV350zu;
short  CV500zu;			assign CV500zu			to "42G500L_bi";					monitor CV500zu;
short  UZV6x8zu[8];		assign UZV6x8zu[1]		to "42UZV618_dlog.SB0";
						assign UZV6x8zu[2]		to "42UZV628_dlog.SB0";
						assign UZV6x8zu[3]		to "42UZV638_dlog.SB0";
						assign UZV6x8zu[4]		to "42UZV648_dlog.SB0";
						assign UZV6x8zu[5]		to "42UZV658_dlog.SB0";
						assign UZV6x8zu[6]		to "42UZV668_dlog.SB0";
						assign UZV6x8zu[7]		to "42UZV678_dlog.SB0";				monitor UZV6x8zu;
double CV150;			assign CV150			to "42CV150_ao";
double CV250;			assign CV250			to "42CV250_ao";
double CV350;			assign CV350			to "42CV350_ao";
double TC241;			assign TC241			to "42TC241_pid.SOUT";
double PDC230;			assign PDC230			to "42PDC230_pid.SOUT";
short  TC241AM;			assign TC241AM			to "42TC241_pid.AM";
short  PDC230AM;		assign PDC230AM			to "42PDC230_pid.AM";
short  TC528AM;			assign TC528AM			to "42TC528_pid.AM";
short  TC026AM;			assign TC026AM			to "42TC026_pid.AM";

short  UZV394;			assign UZV394			to "42UZV394_dlog.CMD";
short  UZV384;			assign UZV384			to "42UZV384_dlog.CMD";
short  UZV372;			assign UZV372			to "42UZV372_dlog.CMD";
short  UZV374;			assign UZV374			to "42UZV374_dlog.CMD";
short  UZV383;			assign UZV383			to "42UZV383_dlog.CMD";
short  UZV393;			assign UZV393			to "42UZV393_dlog.CMD";
short  UZV394zu;		assign UZV394zu			to "42UZV394_dlog.SB0";
short  UZV384zu;		assign UZV384zu			to "42UZV384_dlog.SB0";
short  UZV372zu;		assign UZV372zu			to "42UZV372_dlog.SB0";
short  UZV374zu;		assign UZV374zu			to "42UZV374_dlog.SB0";
short  UZV383zu;		assign UZV383zu			to "42UZV383_dlog.SB0";
short  UZV393zu;		assign UZV393zu			to "42UZV393_dlog.SB0";
short  HS130;			assign HS130			to "42HS130_dlog.CMD";

ss cueberw {

	state ini {
		entry {
			cboxState = 0;
			pvPut( cboxState);
			snlMsgLog( "<ini> cueberw started");
			SoftStop = FALSE;
			pvPut( SoftStop);
		}
		when( delay(2.)) {
		} state not_ok
	}

	state not_ok {
		entry {
			sprintf( SoftStopString, "Box stopped");
			pvPut( SoftStopString);
			cboxState = 1;
			pvPut( cboxState);
			snlMsgLog( "<not_ok> CBX not_ok");
		}
		when(	(SoftStop == FALSE) &&
				(pas == OK) &&
				(kstrOK == OK)
			) {	
		} state ok
	}
	
	state ok {
		entry {
			cboxState = 2;
			pvPut( cboxState);
			snlMsgLog( "<ok> cueberw ok");
			cboxOK = OK;
			pvPut( cboxOK);
			sprintf( SoftStopString, "SoftStop");
			pvPut( SoftStopString);
			pvMonitor( CV106OVAL);
			pvMonitor( CV206OVAL);
			pvMonitor( CV306OVAL);
			pvMonitor( CV406OVAL);
		}
		when( SoftStop
			&& CV106zu && CV206zu && CV306zu && CV406zu
			&& CV106OVAL < 0.15 && CV206OVAL < 0.15
			&& CV306OVAL < 0.15 && CV406OVAL < 0.15 ) {
			SoftStop = FALSE;	/* Bit zuruecksetzen fuer Bestaetigung durch Operator */
			pvPut( SoftStop, SYNC);
			snlMsgLog( "<ok> Coldbox SoftStop angefordert");
			sprintf( SoftStopString, "Coldbox Stop?\nJa: druecke nochmal!");
			pvPut( SoftStopString);
		} state softstop
		when( SoftStop) /* Softstopp und Turbinen laufen noch */ {
			SoftStop = FALSE;	/* Bit zuruecksetzen fuer Bestaetigung durch Operator */
			pvPut( SoftStop, SYNC);
			snlMsgLog( "<ok> Softstop angefordert, Turbinenkreise laufen noch!");
			sprintf( SoftStopString, "Tu-Kreise laufen\nTrotzdem Coldbox Stop?");
			pvPut( SoftStopString);
		} state check_turbinenkreise
		when( pas != OK) {
			snlMsgLog( "<ok> Steuerluft ausgefallen");
		} state coldbox_ausschalten
		when( kstrOK != OK) {
			snlMsgLog( "<ok> Kompressorstrasse ausgefallen");
		} state coldbox_ausschalten
		exit {
			pvStopMonitor( CV106OVAL);
			pvStopMonitor( CV206OVAL);
			pvStopMonitor( CV306OVAL);
			pvStopMonitor( CV406OVAL);
		}
	}

	state softstop {
		entry {
			cboxState = 3;
			pvPut( cboxState);
		}
		when( SoftStop) {
			SoftStop = FALSE;	/* Bit zuruecksetzen fuers naechste Mal*/
			pvPut( SoftStop, SYNC);
			sprintf( SoftStopString, "SoftStop bestaetigt");
			pvPut( SoftStopString);
			snlMsgLog( "<softstop> Softstop CBX bestaetigt");
			softStopErkannt = TRUE;
		} state coldbox_ausschalten
		when( pas != OK) {
			snlMsgLog( "<softstop> Steuerluft ausgefallen");
		} state coldbox_ausschalten
		when( kstrOK != OK) {
			snlMsgLog( "<softstop> Kompressorstrasse ausgefallen");
		} state coldbox_ausschalten
		when (delay( 30.)) {
		// Operator hat Softstop nicht innerhalb 30 sec bestaetigt
			snlMsgLog( "<softstop> Softstop nicht bestaetigt");
		} state ok
	}

	state check_turbinenkreise {
		entry {
			cboxState = 4;
			pvPut( cboxState);
		}
		when( SoftStop) {
			SoftStop = FALSE;	/* Bit zuruecksetzen fuers naechste Mal*/
			pvPut( SoftStop, SYNC);
			sprintf( SoftStopString, "Stop Tu-Kreise\nbestaetigt");
			pvPut( SoftStopString);
			snlMsgLog( "<check_turbinenkreise> Stop Tu-Kreise bestaetigt");
			softStopErkannt = TRUE;
		} state stop_turbinenkreise
		when ( delay( 30.)) {
		// Operator hat Stop der Turbinenkreise nicht innerhalb 30 sec bestaetigt
			sprintf( SoftStopString, "Kein Tu_Kreise-Stop\nKein SoftStop!");
			pvPut( SoftStopString);
			snlMsgLog( "<check_turbinenkreise> Stop Tu-Kreise nicht bestaetigt");
		} state ok
		when( pas != OK) {
			snlMsgLog( "<check_turbinenkreise> Steuerluft ausgefallen");
		} state coldbox_ausschalten
		when( kstrOK != OK) {
			snlMsgLog( "<check_turbinenkreise> Kompressorstrasse ausgefallen");
		} state coldbox_ausschalten
	}
	state stop_turbinenkreise {
		entry {
			cboxState = 5;
			pvPut( cboxState);
			/* veranlasse Softstop der Turbinenkreise */
			PV_SET( TK1run, 0)
			PV_SET( TK2run, 0)
			PV_SET( TK3run, 0)
			PV_SET( TK4run, 0)
			pvMonitor( CV106OVAL);
			pvMonitor( CV206OVAL);
			pvMonitor( CV306OVAL);
			pvMonitor( CV406OVAL);
		}
		when(  CV106OVAL < 1.
			&& CV206OVAL < 1.
			&& CV306OVAL < 1.
			&& CV406OVAL < 1.) {
			snlMsgLog( "<stop_turbinenkreise> Turbinenkreise sind geschlossen!");
		} state coldbox_ausschalten
		when( delay( 60.)) {
			snlMsgLog( "<stop_turbinenkreise> Turbinenkreise immer noch nicht geschlossen!");
		} state stop_turbinenkreise
		when( pas != OK) {
			snlMsgLog( "<stop_turbinenkreise> Steuerluft ausgefallen");
			softStopErkannt = FALSE;
		} state coldbox_ausschalten
		when( kstrOK != OK) {
			snlMsgLog( "<stop_turbinenkreise> Kompressorstrasse ausgefallen");
			softStopErkannt = FALSE;
		} state coldbox_ausschalten
		exit {
			pvStopMonitor( CV106OVAL);
			pvStopMonitor( CV206OVAL);
			pvStopMonitor( CV306OVAL);
			pvStopMonitor( CV406OVAL);
		}
	}

	state coldbox_ausschalten {
		entry {
			cboxState = 6;
			pvPut( cboxState);
			cboxOK = NOT_OK;			/*CBX Trip*/
			pvPut( cboxOK);
//	es fehlt noch das Stoppen der Adsorber!
			efSet( ad300StopFlag);
// close 42HS345_dlog	
	/* Shield simulation heating no longer present! JZa 2009-08-26*/
// close 32UZV122 input to Coldbox
			UZV122 = ZU;
			pvPut( UZV122);
// close valves between HD, ND branches
 		 	CV340OMSL= SUPERVISORY;
 	 	 	CV340 = 0.0;
 	 	 	pvPut( CV340OMSL, SYNC);
 	 	 	pvPut( CV340);
 	 	 	CV400SCAN = SCAN_1_SECOND;
		 	CV400OMSL= SUPERVISORY;
	 		CV400 = 0.0;
	 		CV400OROC = softStopErkannt ? 1. : 0.;
	 		pvPut( CV400OROC, SYNC);
	 		pvPut( CV400OMSL, SYNC);
	 		pvPut( CV400SCAN);
	 		pvPut( CV400);
			CV104OMSL= SUPERVISORY;
	 		CV104 = 0.0;
	 		pvPut( CV104OMSL, SYNC);
	 		pvPut( CV104);
// close N2-supply
			TC528AM = PID_MANUELL;
			TC528 = 0.0;
	 		pvPut( TC528AM, SYNC);
	 		pvPut( TC528);
			TC026AM = PID_MANUELL;
			TC026 = 0.0;
	 		pvPut( TC026AM, SYNC);
	 		pvPut( TC026);
	 		pvMonitor( CV400OVAL);
			pvMonitor( UZV122zu);
		}
		when( !softStopErkannt || pas != OK || kstrOK != OK) {
// falls Steuerluft- oder Kompressorenausfall: weiter ohne Absaugen
		} state stopit
		when( UZV122zu && CV400OVAL < 0.15)  /* Softstop und Boxeintritt zu */{
			snlMsgLog( "<coldbox_ausschalten> Coldbox wird abgesaugt");
		} state absaugen408
		when( delay( 120.)) {
			snlMsgLog( "<coldbox_ausschalten> HD Eintritt oder Bypass schliesst nicht");
		} state coldbox_ausschalten
		exit {
			pvStopMonitor( CV400OVAL);
			pvStopMonitor( UZV122zu);
  			CV400OROC = 1.;
 			pvPut( CV400OROC);
		}
	}

	state absaugen408 {
		entry {
			cboxState = 7;
			pvPut( cboxState);
			sprintf( PI072SCAN, "1 second");
			pvPut( PI072SCAN);
			CV408OMSL = SUPERVISORY;
			CV408OROC = 0.5;
			CV408 = 30;
			pvPut( CV408OMSL, SYNC);
			pvPut( CV408OROC, SYNC);
			pvPut( CV408);
			sprintf( CV408SCAN, "1 second");
			pvPut( CV408SCAN);
			pvMonitor( CV408OVAL);
		}
		when( kstrOK != OK) {
			snlMsgLog( "<absaugen408> Kompressorstrasse ausgefallen");
		} state stopit
		when( pas != OK) {
			snlMsgLog( "<absaugen408> Steuerluft ausgefallen");
		} state stopit
		when( PI072STAT==HIGH_ALARM || PI072STAT==HIHI_ALARM) {
			CV408 = CV408OVAL; /* nicht weiter rampen, erst Druckabbau abwarten*/
			pvPut( CV408);
			absaugventilnr = 408;
		} state hochdruck
		when( CV408OVAL < 30.5) /* Rampen beendet */ {
			snlMsgLog( "<absaugen408> Oeffnen CV408 fertig");
		} state absaugen400
		when( delay( 120.)) {
			snlMsgLog( "<absaugen408> Versuche CV408 erneut zu stellen");
			CV408OROC = 0.5;
			CV408 = 30;
			pvPut( CV408OROC, SYNC);
			pvPut( CV408);
		} state absaugen408
		
		exit {pvStopMonitor( CV408OVAL);}
	}

	state absaugen400 {
		entry {
			cboxState = 8;
			pvPut( cboxState);
			CV400OMSL = SUPERVISORY;
			CV400OROC = 0.5;
 	 	 	CV400SCAN = SCAN_1_SECOND;
			CV400 = 50;
			pvPut( CV400OMSL, SYNC);
			pvPut( CV400OROC, SYNC);
			pvPut( CV400SCAN);
			pvPut( CV400);
			pvMonitor( CV400OVAL);
			pvMonitor( PI012);
		}
		when( kstrOK != OK) {
			snlMsgLog( "<absaugen400> Kompressorstrasse ausgefallen");
		} state stopit
		when( pas != OK) {
			snlMsgLog( "<absaugen400> Steuerluft ausgefallen");
		} state stopit
		when( PI072STAT==HIGH_ALARM || PI072STAT==HIHI_ALARM) {
			CV400 = CV400OVAL; /* nicht weiter rampen, erst Druckabbau abwarten*/
			pvPut( CV400);
			absaugventilnr = 400;
		} state hochdruck
		when( CV400OVAL > 49.5 /* Rampen beendet */
				&& PI012 < 6.0)  /* Absaugen beendet */ {
			snlMsgLog( "<absaugen400> Absaugen Coldbox42 fertig");
		} state stopit
		when( delay( 300.)) {
			snlMsgLog( "<absaugen400> State absaugen400 schon 5 Minuten aktiv");
			if (PI012>=6.0)
				snlMsgLog( "<absaugen400> PI012 = %.2f", PI012);
			pvGet( CV400);
			if (CV400 < 50.) {
				snlMsgLog( "<absaugen400> Wer fummelt am CV400?");
				CV400 = 50.;
				pvPut( CV400);
			}
		} state absaugen400
		exit {
			pvStopMonitor( CV400OVAL);
			pvStopMonitor( PI012);
		}
	}

	state hochdruck {
		entry {
			cboxState = 9;
			pvPut( cboxState);
		}
		when( kstrOK != OK) {
			snlMsgLog( "<hochdruck> Kompressorstrasse ausgefallen");
		} state stopit
		when( pas != OK) {
			snlMsgLog( "<hochdruck> Steuerluft ausgefallen");
		} state stopit
		when( PI072STAT!=HIGH_ALARM && PI072STAT!=HIHI_ALARM && absaugventilnr==408) {
		} state absaugen408
		when( PI072STAT!=HIGH_ALARM && PI072STAT!=HIHI_ALARM && absaugventilnr==400) {
		} state absaugen400
		when( delay( 120.)) {
			snlMsgLog( "<hochdruck> Druck schon 2 Minuten zu hoch, VentilNr=%d", absaugventilnr);
		} state hochdruck
	}

	state stopit {
		option -e; /* fuehre entry immer wieder aus */
		entry {
			cboxState = 10;
			pvPut( cboxState);
			sprintf( PI072SCAN, "1 second");
			pvPut( PI072SCAN);
			sprintf( CV408SCAN, "Passive");
			CV408OROC = 0.0;
			pvPut( CV408SCAN);
			pvPut( CV408OROC);
			CV400OROC = softStopErkannt ? 1. : 0.;
			CV400 = 0.0;
			pvPut( CV400OROC, SYNC);
			pvPut( CV400);
			CV150 = 100.0; /* normal offen */
			CV250 = 0.0;
			CV350 = 0.0;
			pvPut( CV150);
			pvPut( CV250);
			pvPut( CV350);
			TC241AM = PID_MANUELL;
			TC241 = 0.0;
			pvPut( TC241AM, SYNC);
			pvPut( TC241);
			PDC230AM = PID_MANUELL;
			PDC230 = 0.0;
			pvPut( PDC230AM, SYNC);
			pvPut( PDC230);
			TC528AM = PID_MANUELL;
			TC528 = 0.0;
			pvPut( TC528AM, SYNC);
			pvPut( TC528);
			pvMonitor( CV400OVAL);
		}
		when( CV106zu && CV206zu && CV306zu && CV406zu
				&& CV104zu && CV150zu && CV250zu && CV350zu
				&& CV500zu && CV400OVAL < 0.15) {
		} state stopit2
		when( delay( 120.)) {
			snlMsgLog( "<stopit> CBX42: Abschaltung nicht erfolgreich");
			if (!CV106zu) { snlMsgLog( "<stopit> Grund: CV106 ist nicht geschlossen"); }
			if (!CV206zu) { snlMsgLog( "<stopit> Grund: CV206 ist nicht geschlossen"); }
			if (!CV306zu) { snlMsgLog( "<stopit> Grund: CV306 ist nicht geschlossen"); }
			if (!CV406zu) { snlMsgLog( "<stopit> Grund: CV406 ist nicht geschlossen"); }
			if (!CV104zu) { snlMsgLog( "<stopit> Grund: CV104 ist nicht geschlossen"); }
			if (!CV150zu) { snlMsgLog( "<stopit> Grund: CV150 ist nicht geschlossen"); }
			if (!CV250zu) { snlMsgLog( "<stopit> Grund: CV250 ist nicht geschlossen"); }
			if (!CV350zu) { snlMsgLog( "<stopit> Grund: CV350 ist nicht geschlossen"); }
			if (!CV500zu) { snlMsgLog( "<stopit> Grund: CV500 ist nicht geschlossen"); }
			if (!CV400OVAL >= 0.15) { snlMsgLog( "<stopit> Grund: CV400 schliesst nicht"); }
		} state stopit
		
		exit {
  			CV400OROC = 1.;
 			pvPut( CV400OROC);
 		}
	}

	state stopit2 {
		entry {
			cboxState = 11;
			pvPut( cboxState);
		// hier fehlt noch Ruecksetzen Flag fuer Schildbetrieb
		}
		when( UZV6x8zu[1] && UZV6x8zu[2] && UZV6x8zu[3] && UZV6x8zu[4]
			 	&& UZV6x8zu[5] && UZV6x8zu[6] && UZV6x8zu[7]) {
			UZV122 = ZU;
			UZV121 = ZU;
			UZV120 = ZU;
			pvPut( UZV122);
			pvPut( UZV121);
			pvPut( UZV120);
			softStopErkannt = FALSE;
			snlMsgLog( "<stopit2> CBX Ventile werden geschlossen");
		} state stopit3 	
		when( delay( 900.)) {
			snlMsgLog( "<stopit2> Turbinenabschaltung nicht erfolgreich");
			for (i=1;i<=7;i++) {
				if (!UZV6x8zu[i]) {
					snlMsgLog( "<stopit> Grund: UZV6%d8 ist nicht geschlossen", i);
				}
			}
		} state stopit2
	}
	state stopit3 {
		entry {
			pvMonitor( UZV120zu);
			pvMonitor( UZV121zu);
			pvMonitor( UZV122zu);
		}
		when (UZV120zu && UZV121zu && UZV122zu) {
			snlMsgLog( "<stopit3> CBX abgeschaltet");
		} state not_ok
		when( delay( 60.)) {
			snlMsgLog( "<stopit3> CBX Ventile schliessen nicht!");
		} state stopit3
		exit {
			pvStopMonitor( UZV120zu);
			pvStopMonitor( UZV121zu);
			pvStopMonitor( UZV122zu);
		}
	}
}

ss ads_ueberw {
	
	state cbox_off {
		entry {
			snlMsgLog( "CBX Aus -> Adsorber nicht ueberwachen");
		}
		when( cboxOK == OK) {
		} state cbox_on
	}

	state cbox_on {
		entry {
			snlMsgLog( "CBX Aus -> Adsorber pruefen");
			pvMonitor( Ad1Betr);
			pvMonitor( Ad2Betr);
			pvMonitor( Ad3Betr);
			pvMonitor( Ad4Betr);
			pvMonitor( Ad1Mess);
			pvMonitor( Ad2Mess);
			pvMonitor( Ad3Mess);
			pvMonitor( Ad4Mess);
			}
		when( cboxOK != OK) {
		} state cbox_off
		when( Ad1Mess && QI108 >20. && Ad1Betr == 0) {
			snlMsgLog( "Analyse AD100 > 20 vpm -> Regeneration");
			PV_SET( Ad1Betr, 1)
			} state cbox_on
		when( Ad2Mess && QI109 >20. && Ad2Betr == 0) {
			snlMsgLog( "Analyse AD110 > 20 vpm -> Regeneration");
			PV_SET( Ad2Betr, 1)
			} state cbox_on
		when( Ad3Mess && QI208 >20. && Ad3Betr == 0) {
			snlMsgLog( "Analyse AD200 > 20 vpm -> Regeneration");
			PV_SET( Ad3Betr, 1)
			} state cbox_on
		when( Ad4Mess && QI209 >20. && Ad4Betr == 0) {
			snlMsgLog( "Analyse AD210 > 20 vpm -> Regeneration");
			PV_SET( Ad4Betr, 1)
			} state cbox_on
		exit {
			pvStopMonitor( Ad1Betr);
			pvStopMonitor( Ad2Betr);
			pvStopMonitor( Ad3Betr);
			pvStopMonitor( Ad4Betr);
			pvStopMonitor( Ad1Mess);
			pvStopMonitor( Ad2Mess);
			pvStopMonitor( Ad3Mess);
			pvStopMonitor( Ad4Mess);
		}
	}
}

ss ad300 {

	state waitAusschalten {
		when (efTestAndClear( ad300StopFlag)) {
			snlMsgLog( "AD300 Regenerationsventile schliessen");
			PV_SET( UZV394, ZU)
			PV_SET( UZV384, ZU)
			PV_SET( UZV372, ZU)
			PV_SET( UZV374, ZU)
			PV_SET( UZV383, ZU)
			PV_SET( UZV393, ZU)
		} state waitRegenerationZu
	}
	state waitRegenerationZu {
		entry {
			pvMonitor( UZV394zu);
			pvMonitor( UZV384zu);
			pvMonitor( UZV372zu);
			pvMonitor( UZV374zu);
			pvMonitor( UZV383zu);
			pvMonitor( UZV393zu);
		}
		when( UZV394zu && UZV384zu && UZV372zu && UZV374zu
		 && UZV383zu && UZV393zu) {
			snlMsgLog( "AD300 wird gestoppt");
//			PV_SET( CV300SCAN, SCAN_1_SECOND)
			PV_SET( CV300, 0.)
		} state waitBypassAuf
		
		when( delay( 120.)) {
			snlMsgLog( "AD300 Reg-Ventile schliessen nicht!");
		} state waitRegenerationZu
	}
	state waitBypassAuf {
		entry {
			pvMonitor( CV300OVAL);
			pvMonitor( CV300auf);
		}
		when( CV300OVAL < 0.15 && CV300auf) {
			snlMsgLog( "AD300 Bypass geschlossen");
//			PV_SET( CV301SCAN, SCAN_1_SECOND)
//			PV_SET( CV302SCAN, SCAN_1_SECOND)
			PV_SET( CV301, 0.)
			PV_SET( CV302, 0.)
		} state waitAnschlussZu
		when( delay( 120.)) {
			snlMsgLog( "AD300 Bypass schliesst nicht!");
		} state waitBypassAuf
		exit {
			pvStopMonitor( CV300OVAL);
			pvStopMonitor( CV300auf);
		}			
	}
	state waitAnschlussZu {
		entry {
			pvMonitor( CV301OVAL);
			pvMonitor( CV301zu);
			pvMonitor( CV302OVAL);
			pvMonitor( CV302zu);
		}
		when( CV301OVAL < 0.15 && CV301zu &&
			  CV302OVAL < 0.15 && CV302zu ) {
			PV_SET( HS130, EIN)
			PV_SET( UZV393, AUF)
			PV_SET( UZV372, AUF)
			snlMsgLog( "AD300 gestoppt - Druck entlasten auf 3.5 bar");
		} state waitDruckEntlast1
		when(  delay( 120.)) {
			snlMsgLog( "AD300 Ein-/Ausgangsventile schliessen nicht");
		} state waitAnschlussZu
		exit {
			pvStopMonitor( CV301OVAL);
			pvStopMonitor( CV301zu);
			pvStopMonitor( CV302OVAL);
			pvStopMonitor( CV302zu);
		}
	}
	state waitDruckEntlast1 {
		entry {
			pvMonitor( PI302);
		}
		when( PI302 < 3.5) {
			PV_SET( UZV372, ZU)
			snlMsgLog( "AD300 Druck < 3.5 bar - UZV372 schliessen");
		} state waitUZV372zu
	}
	state waitUZV372zu {
		when( UZV372zu) {
			snlMsgLog( "AD300 Druck entlasten auf 1.5 bar");
			PV_SET( UZV374, AUF)
		} state waitDruckEntlast2
		when( delay( 120.)) {
			snlMsgLog( "AD300 UZV372 schliesst nicht!");
		} state waitUZV372zu
	}
	state waitDruckEntlast2 {
		when( PI302 < 1.5) {
			snlMsgLog( "AD300 Druck = %.2f", PI302);
		} state waitAusschalten
		when( delay( 900.)) {
			snlMsgLog( "AD300 Entlasten Timeout P = %.2f", PI302);
		} state waitAusschalten
		exit {
			pvStopMonitor( UZV394zu);
			pvStopMonitor( UZV384zu);
			pvStopMonitor( UZV372zu);
			pvStopMonitor( UZV374zu);
			pvStopMonitor( UZV383zu);
			pvStopMonitor( UZV393zu);
			pvStopMonitor( PI302);
			PV_SET( UZV374, ZU)
			PV_SET( UZV393, ZU)
//			PV_SET( CV300SCAN, SCAN_PASSIVE)
//			PV_SET( CV301SCAN, SCAN_PASSIVE)
//			PV_SET( CV302SCAN, SCAN_PASSIVE)
			snlMsgLog( "AD300 Entlasten fertig");
		}
	}
}