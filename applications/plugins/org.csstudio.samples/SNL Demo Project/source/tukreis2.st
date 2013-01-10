/* $$Revision: 1.12 $$ Letzte Aenderung von $$Author: schoeneb $$ am $$Date: 2009/10/15 15:49:29 $$ ** 
 
* File:        tukreis2.st
* Author:      Christian Gerke
* Created:     2009/07/30
*
	3 State-Sets zur Ueberwachung des Turbinenkreises.
	  sollen die Arbeit erledigen,
	  die frueher CTUKRx2, CFREIGA und CTUBTR erledigt haben (s.u.)
	  
	State-Set tuDrehzahlen ersetzt CFREIGA,
	======================  
	setzt bits in Abhaengigkeit von Turbinendrehzahlen in der Logik LAGERG:
		lagerg4BitSIklein	kleine Drehzahlen Bit fuer Turbine 4	
		lagerg5BitSIklein	kleine Drehzahlen Bit fuer Turbine 5	
		lagerg4BitSIgross	grosse Drehzahlen Bit fuer Turbine 4	
		lagerg5BitSIgross	grosse Drehzahlen Bit fuer Turbine 5	
	das Programm befindet sich entsprechend den Drehzahlen in den States
		gross,
		mittel oder
		klein
			das Programm geht von klein nach 
		bereit und
			setzt das Freigabebit _I13 in der Logik fuer SV206,
			wenn (wie bei CFREIGA)	
	        	Coldbox-Eintrittsventile UZV120/122/122 offen
	        	Turbinenkreis-Eintrittsentil CV206 geschlossen
	        	Lagergaseinspeisungen UZV6x8 offen
	        	kein Differenzdruckalarm PDAx9
	        	kein Kuehlwasseralarm FA643
			das "program running" Bit _I14 wird in jedem State gesetzt

	State-Set tk2ueberw
	===================
	ueberprueft die gesammelten Bedingungen fuer Turbinenkreisfreigabe
	u.a. aus CTUKRx2
	und befindet sich entsprechend entweder in Zustand
	not_ok oder
	ok
	Ausserdem wird entsprechend das eventflag tk2FreigabeFlag gesetzt
	Wenn im State not_ok das Kommando TK2run gegeben wird,
	 werden alle Bedingungen fuer eine Freigabe ueberprueft;
	 so kann nachvollzogen werden, warum wir in not_ok sind.
	 
	State-Set tukreis2
	==================
	beschreibt den Zustand des Turbinenkreises.
	Die States sind:
	gestoppt
		hierher wird aus stopit gesprungen, 
		das Programm bleitb hier solange das eventflag tk2FreigabeFlag false ist.
	bereit
		das eventflag tk2FreigabeFlag ist true
	startBereit
		das eventflag tk2FreigabeFlag ist true und
		in tuDrehzahlen wurde die Start-Freigabe erteilt
	abwartBetrieb
		nach dem Kommando TK2run landet das Programm hier und bleibt dann
		im Wesentlichen abhaengig von TK-Austrittstemperatur
	        und Drehzahlen in einem der naechsten 3 States
	    wenn hier oder in einem der naechsten 3 States 
	    	as eventflag tk2FreigabeFlag false wird,
	    	springt das Programm nach stopit
	    wenn TK2run wieder false wird,
	    	springt das Programm nach softStop 
	autoBetrieb
	manDrosselBetrieb
	manOeffneBetrieb
	
	softStop
		Eingangsventil sanft schliessen
	stopit
	
	mit D3 gab es mehrere Ueberwachungsprogramme:
	===========================================
	CTUKRx2 hat den Hartverdrahteten Sammelalarm XA206,
			die ROC Alarme fuer PvorTu und PI217 sowie
			Trip-Signale von Kompressorstrasse oder Coldbxox ueberwacht.
			Bei Bedarf wurde das Magnetventil SV206 am TuKreis Eintritt entregt.
	CFREIGA hat in Abhaengigkeit von den Turbinendrehzahlen T-Bits in den 
	          Logiken UZV6x8 (MDV LAGERG) gesetzt
	        und das Freigabebit in der Logik fuer SV206 nur gesetzt,
	        wenn
	        	Coldbox-Eintrittsventile UZV120/122/122 offen
	        	Turbinenkreis-Eintrittsentil CV206 geschlossen
	        	Lagergaseinspeisungen UZV6x8 offen
	        	kein Differenzdruckalarm PDAx9
	        	kein Kuehlwasseralarm FA643
	CTUBTR hat die Leistungsregelung gesteuert,
	       im Wesentlichen abhaengig von TK-Austrittstemperatur
	        und Drehzahlen
	       
	  		
* Revision history: 
*
*
*******************************************************************************/

program tukreis2

#define OK 1
#define NOT_OK 0
#define SUPERVISORY 0
#define CLOSED_LOOP 1
#define AUF 1
#define ZU 0
#define PID_AUTO 0
#define PID_MANUELL 1

#define FREIGABE_AUS 0

// pid.ERR = (internal) setpoint - measurement
#define TC216ERR (TC216SETP - TC216CVAL)
#define SC640ERR (SC640SETP - SC640CVAL)
#define SC650ERR (SC650SETP - SC650CVAL)
#define drehzahlenHoch (SC640ERR < -50. || SC650ERR < -50.)

%%#include <string.h>
%%#include <math.h>
%%#include <alarm.h>
%%#include <snlMsgLog.h>

//char text[40];
int     drehzahlenNiedrig;

evflag	tk2FreigabeFlag;

short	programRunning;		assign programRunning		to "42SV206_dlog.I14";
short	programRunningI15;	assign programRunningI15	to "42SV206_dlog.I15";
short	lagerg4BitSIklein;	assign lagerg4BitSIklein	to "42UZV648_dlog.I13";
short	lagerg5BitSIklein;	assign lagerg5BitSIklein	to "42UZV658_dlog.I13";
short	lagerg4BitSIgross;	assign lagerg4BitSIgross	to "42UZV648_dlog.I14";
short	lagerg5BitSIgross;	assign lagerg5BitSIgross	to "42UZV658_dlog.I14";

short	TK2state;		assign TK2state			to "KS2:SNL:TK2:STATE_mbbi";
short	kstrOK;			assign kstrOK			to "KS2:SNL:KUE:KSTROK_bi";		monitor kstrOK;
short	cboxOK;			assign cboxOK			to "KS2:SNL:CUE:CBOXOK_bi";		monitor cboxOK;
short	TK2run;			assign TK2run			to "KS2:SNL:TK2:RUN_bi";		monitor TK2run;
short	TK2hand;		assign TK2hand			to "KS2:SNL:TK2:HAND_bi";		monitor TK2hand;
string	buttonString;	assign buttonString		to "KS2:SNL:TK2:RUN_si";
short	UZV120offen;	assign UZV120offen		to "32UZV120_dlog.SB2";			monitor UZV120offen;
short	UZV121offen;	assign UZV121offen		to "32UZV121_dlog.SB2";			monitor UZV121offen;
short	UZV122offen;	assign UZV122offen		to "32UZV122_dlog.SB2";			monitor UZV122offen;
short	adsorberZu;		assign adsorberZu		to "42SV206_dlog.SB4";			monitor adsorberZu;
short	freigabeCMD;	assign freigabeCMD		to "42SV206_dlog.CMD";
short	SV206bereit;	assign SV206bereit		to "42SV206_dlog.SB3";			monitor SV206bereit;
short	startFreigabe;	assign startFreigabe	to "42SV206_dlog.I13";			monitor startFreigabe;

short	CV206zu;		assign CV206zu			to "42CV206_dlog.SB0";			monitor CV206zu;
short	CV217offen;		assign CV217offen		to "42CV217_dlog.SB2";			monitor CV217offen;
short	summenAlarm;	assign summenAlarm		to "42XA206_bi.VAL";			monitor summenAlarm;
short	UZV648offen;	assign UZV648offen		to "42UZV648_dlog.SB1";			monitor UZV648offen;
short	UZV658offen;	assign UZV658offen		to "42UZV658_dlog.SB1";			monitor UZV658offen;
short	PDA649;			assign PDA649			to "42PDA649_bi.VAL";			monitor PDA649;
short	PDA659;			assign PDA659			to "42PDA659_bi.VAL";			monitor PDA659;
short	FA643;			assign FA643			to "42FA643_bi.VAL";			monitor FA643;
double	SI4;			assign SI4				to "42SI640_ai.VAL";			monitor SI4;
double	SI5;			assign SI5				to "42SI650_ai.VAL";			monitor SI5;
short	PvorTuSTAT;		assign PvorTuSTAT		to "42PI012_calc.STAT";			monitor PvorTuSTAT;
short	PnachTuSTAT;	assign PnachTuSTAT		to "42PI217_calc.STAT";			monitor PnachTuSTAT;
double	TI216LOW;		assign TI216LOW			to "42TI216_temp.LOW";			monitor TI216LOW;
double	TI216;			assign TI216			to "42TI216_temp.VAL";			monitor TI216;
double	TC216SOUT;		assign TC216SOUT		to "42TC216_pid.SOUT";
short	TC216AM;		assign TC216AM			to "42TC216_pid.AM";
double	TC216CVAL;		assign TC216CVAL		to "42TC216_pid.CVAL";			monitor TC216CVAL;
double	TC216SETP;		assign TC216SETP		to "42TC216_pid.VAL";			monitor TC216SETP;
double	TC216VOUT;		assign TC216VOUT		to "42TC216_pid.VOUT";			monitor TC216VOUT;
double	CV206OVAL;		assign CV206OVAL		to "42CV206_ao.OVAL";			monitor CV206OVAL;
double	CV206OROC;		assign CV206OROC		to "42CV206_ao.OROC";
double	SC640OUT;		assign SC640OUT			to "42SC640_pid.OUT";			monitor SC640OUT;
double	SC650OUT;		assign SC650OUT			to "42SC650_pid.OUT";			monitor SC650OUT;
double	SC640SETP;		assign SC640SETP		to "42SC640_pid.VAL";
double	SC650SETP;		assign SC650SETP		to "42SC650_pid.VAL";
double	SC640CVAL;		assign SC640CVAL		to "42SC640_pid.CVAL";
double	SC650CVAL;		assign SC650CVAL		to "42SC650_pid.CVAL";
double	PI205;			assign PI205			to "42PI205_ai.VAL";			monitor PI205;
double	Pref;			assign Pref				to "42PI242_ai.VAL";			monitor Pref;

ss tukreis2 {

	state gestoppt {
		entry {
			TK2run = FALSE; /* Stop */
			TK2hand = FALSE;
			sprintf( buttonString, "Start\nTuKr 2");
			pvPut( TK2run);
			pvPut( TK2hand);
			freigabeCMD = FREIGABE_AUS;
			pvPut( freigabeCMD);
			pvPut( buttonString);
			snlMsgLog( "<gestoppt> TuKr 2 gestoppt");
			TK2state = 1;
			pvPut( TK2state);
		}
		when( efTest( tk2FreigabeFlag)) {
			snlMsgLog( "<gestoppt> TuKr 2 freigegeben");
		} state bereit
	}
	state bereit {
		entry {
			TK2state = 2;
			pvPut( TK2state);
		}
		when( !CV206zu && !efTest( tk2FreigabeFlag)) {
			snlMsgLog( "<bereit> Notstopp bei Bereit");
		} state stopit

		when( !efTest( tk2FreigabeFlag)) {
		} state gestoppt

		when( startFreigabe) {
			snlMsgLog( "<bereit> TuKr 2 startbereit");
		}   state startBereit
	}
	state startBereit {
		option -e;
		entry {
			pvGet( CV206OVAL); /* um das Monitor-Deadband zu ueberlisten! */
			TK2state = 3;
			pvPut( TK2state);
// folgendes ist ueberfluessig, weil die Drehzahlueberw schon die startfreigabe wegnimmt
			if (!CV206zu || CV206OVAL > 0.15)
				snlMsgLog( "<startBereit> Eintrittsventil nicht geschlossen");
			if (!UZV648offen || !UZV658offen)
				snlMsgLog( "<startBereit> Lagergasventile nicht alle offen");
			if (PDA649 != OK || PDA659 != OK)
				snlMsgLog( "<startBereit> Differenzdruecke nicht alle OK");
			if (FA643 != OK)
				snlMsgLog( "<startBereit> Kuehlwasser Sammelalarm");
		}
		when( !CV206zu && !efTest( tk2FreigabeFlag)) {
			snlMsgLog( "<startBereit> Notstopp bei startBereit");
		} state stopit

		when( !efTest( tk2FreigabeFlag)) {
		} state gestoppt
		
		when( CV206OVAL > 0.) {
			snlMsgLog( "<startBereit> CV206 wird geoeffnet -> Handbetrieb");
		} state handBetrieb

		when( !startFreigabe) {
			snlMsgLog( "<startBereit> Turbinen Startfreigabe weggenommen");
		} state bereit
		
		when(	CV206zu && CV206OVAL < 0.15 &&
				UZV648offen && UZV658offen &&
				PDA649 == OK && PDA659 == OK &&
				FA643 == OK && TK2run
			) {
		} state abwartBetrieb

		when( delay( 60.0)) {
		} state startBereit
	}

	state handBetrieb {
		entry {
			TK2state = 10;
			pvPut( TK2state);
			TK2run = TRUE;
			TK2hand = TRUE;
			pvPut (TK2run, SYNC);
			pvPut( TK2hand, SYNC);
			CV206OROC = 1.;
			pvPut( CV206OROC);
			snlMsgLog( "<handBetrieb> Jetzt Handbetrieb TK2");
		}
		when( !efTest( tk2FreigabeFlag)
		    && (CV206OVAL > 1. || !CV206zu )) {
		} state stopit
		
		when (!TK2run) {
		} state softStop
		
		when (!TK2hand) {
			snlMsgLog( "<handBetrieb> Verlasse Handbetrieb, gehe nach AUTO");		
		} state abwartBetrieb
	}

	state abwartBetrieb {
		entry {
			snlMsgLog( "<abwartBetrieb> TuKr 2 stationaer");
			TK2state = 4;
			pvPut( TK2state);
			sprintf( buttonString, "Stop\nTuKr 2");
			pvPut( buttonString);
			TC216AM = PID_MANUELL;
			pvPut( TC216AM, SYNC);
			TC216SOUT = CV206OVAL;
			pvPut( TC216SOUT);
			}
		when( !efTest( tk2FreigabeFlag)) {
		} state stopit
// bei zu niedriger Austritts-Temperatur
// oder fast geschlossenem Drehzahlregler wird das Eintrittsventil gedrosselt
		when( TI216 < TI216LOW) {
		} state manDrosselBetrieb
		when( TC216ERR > 2.0) /* ERR = setpoint - measurement */ {
		} state manDrosselBetrieb
		when( SC640OUT < 2.0 || SC650OUT < 2.0) {
		} state manDrosselBetrieb
// bei geringer Abweichung Ist-Sollwert der Austritts-Temperatur automatische Regelung
		when( abs(TC216ERR) < 2.0) {
		} state autoBetrieb
// bei zu hoher Austritts-Temperatur, geoeffnetem Drehzahlregler,
// und ausreichend Vordruck wird das Eibtrittsventil bis 90% geoeffnet 
		when( TC216ERR < -2.0
			&& ( SC640OUT > 6.0 && SC650OUT > 6.0)
			&& PI205 < (Pref - 0.3)
			&& TC216VOUT < 90.0) {
		} state manOeffneBetrieb
		when( !TK2run) {
			snlMsgLog( "<abwartBetrieb> Stoppe Turbinenkreis");
		} state softStop
		when (TK2hand) {
			snlMsgLog( "<abwartBetrieb> Verlasse AUTO-Betrieb, gehe nach Handbetieb");		
		} state handBetrieb
	}
	state autoBetrieb {
		entry {
			TC216AM = PID_AUTO;
			CV206OROC = 1.;
			pvPut( CV206OROC, SYNC);
			pvPut( TC216AM);
			snlMsgLog( "<autoBetrieb> TuKr 2 im Regelbetrieb");
			TK2state = 5;
			pvPut( TK2state);
			}
		when( !efTest( tk2FreigabeFlag)) {
		} state stopit
// bei zu niedriger Austritts-Temperatur
// oder fast geschlossenem Drehzahlregler wird das Eintrittsventil gedrosselt
		when( TI216 < TI216LOW) {
		} state manDrosselBetrieb
		when( TC216ERR > 2.0) {
		} state manDrosselBetrieb
		when( SC640OUT < 2.0 || SC650OUT < 2.0) {
		} state manDrosselBetrieb
// bei zu hoher Austritts-Temperatur, geoeffnetem Drehzahlregler,
// und ausreichend Vordruck wird das Eintrittsventil bis 90% geoeffnet 
		when( TC216ERR < -2.0
			&& ( SC640OUT > 6.0 && SC650OUT > 6.0)
			&& PI205 < (Pref - 0.3)
			&& TC216VOUT < 90.0) {
		} state manOeffneBetrieb
		when( !TK2run) {
			snlMsgLog( "<autoBetrieb> Stoppe Turbinenkreis");
		} state softStop
		when (TK2hand) {
			snlMsgLog( "<autoBetrieb> Verlasse AUTO-Betrieb, gehe nach Handbetieb");		
		} state handBetrieb
	}
	state manDrosselBetrieb {
		entry {
			TC216AM = PID_MANUELL;
			pvPut( TC216AM, SYNC);
			CV206OROC = 0.05;
			TC216SOUT = CV206OVAL - 2.;
			pvPut( CV206OROC, SYNC);
			pvPut( TC216SOUT);
			pvMonitor( SC640CVAL);
			pvMonitor( SC650CVAL);
			pvMonitor( SC640SETP);
			pvMonitor( SC650SETP);
			snlMsgLog( "<manDrosselBetrieb> TuKr 2 im Drosselbetrieb");
			TK2state = 6;
			pvPut( TK2state);
		}
		when( !efTest( tk2FreigabeFlag)) {
		} state stopit
// bei nicht zu niedriger Austritts-Temperatur
// und geoeffneten Drehzahlreglern
// und geringer Abweichung Ist-Sollwert der Austritts-Temperatur
// wird das Eintrittsventil automatisch geregelt
		when( !(TI216 < TI216LOW)
			&& SC640OUT > 2.0 && SC650OUT > 2.0
			&& abs(TC216ERR) < 2.0) {
		} state autoBetrieb
		when( drehzahlenHoch && CV206OROC < 0.5) {
			CV206OROC = 0.5;
			pvPut( CV206OROC, SYNC);
		} state manDrosselBetrieb
		when( delay(5.0)) {
			CV206OROC = drehzahlenHoch ? 0.5 : 0.05;
			TC216SOUT = CV206OVAL - 2.;
			pvPut( CV206OROC, SYNC);
			pvPut( TC216SOUT);
		} state manDrosselBetrieb
// bei zu hoher Austritts-Temperatur, geoeffnetem Drehzahlregler,
// und ausreichend Vordruck wird das Eintrittsventil bis 90% geoeffnet 
		when( !(TI216 < TI216LOW)
			&& TC216ERR < -2.0		
			&& ( SC640OUT > 6.0 && SC650OUT > 6.0)
			&& PI205 < (Pref - 0.3)
			&& TC216VOUT < 90.0) {
		} state manOeffneBetrieb
		when(  !(TI216 < TI216LOW)	/* kein LOW-Alarm Temperatur */
			&& !(TC216ERR > 2.0)	/* nicht zu kalt */
			&& ( SC640OUT > 2.3 && SC650OUT > 2.3)) {
		} state abwartBetrieb
		when( !TK2run) {
			snlMsgLog( "<manDrosselBetrieb> Stoppe Turbinenkreis");
		} state softStop
		when (TK2hand) {
			snlMsgLog( "<manDrosselBetrieb> Verlasse AUTO-Betrieb, gehe nach Handbetieb");		
		} state handBetrieb
		exit {
			pvStopMonitor( SC640CVAL);
			pvStopMonitor( SC650CVAL);
			pvStopMonitor( SC640SETP);
			pvStopMonitor( SC650SETP);
		}
	}
	state manOeffneBetrieb {
		entry {
			TC216AM = PID_MANUELL;
			pvPut( TC216AM, SYNC);
			TC216SOUT = CV206OVAL + 0.3;
			CV206OROC = 0.1;
			pvPut( CV206OROC, SYNC);
			pvPut( TC216SOUT);
			pvGet( SC640SETP); pvGet( SC650SETP);
			pvGet( SC640CVAL); pvGet( SC650CVAL);
			drehzahlenNiedrig = SC640ERR > 200. && SC650ERR > 200.;
			snlMsgLog( "<manOeffneBetrieb> TuKr 2 im man. Oeffnebetrieb");
			TK2state = 7;
			pvPut( TK2state);
		}
		when( !efTest( tk2FreigabeFlag)) {
		} state stopit
// bei zu niedriger Austritts-Temperatur
// oder fast geschlossenem Drehzahlregler wird das Eintrittsventil gedrosselt
		when( TI216 < TI216LOW) {
			snlMsgLog( "<manOeffneBetrieb> TI216 niedrig -> Eintritt drosseln");
		} state manDrosselBetrieb
		when( TC216ERR > 2.0) /* ERR = setpoint - measurement */ {
			snlMsgLog( "<manOeffneBetrieb> TC216 Setpoint wird nicht erreicht -> Eintritt drosseln");
		} state manDrosselBetrieb
// wenn Bremsregler zwischen 2.3 und 5.5% oder Vordruck zu gering oder Ventil > 90% nicht weiter oeffnen
		when(  (SC640OUT < 5.5 || SC650OUT < 5.5)
			|| !(PI205 < (Pref - 0.3))	/* Druck-Diff. zu klein */
			||  TC216VOUT > 90.0 ) {	/* Eing.-Ventil schon zu weit offen */
		} state abwartBetrieb
// bei geringer Abweichung Ist-Sollwert der Austritts-Temperatur automatische Regelung
		when( abs(TC216ERR) < 2.0) {
		} state autoBetrieb
		when( delay(drehzahlenNiedrig ? 3.5 : 15.5)) {
			TC216SOUT = CV206OVAL + 0.3;
			CV206OROC = 0.1;
			pvPut( CV206OROC, SYNC);
			pvPut( TC216SOUT);
			pvGet( SC640SETP); pvGet( SC650SETP);
			pvGet( SC640CVAL); pvGet( SC650CVAL);
			drehzahlenNiedrig = SC640ERR > 200. && SC650ERR > 200.;
		} state manOeffneBetrieb
		when( !TK2run) {
			snlMsgLog( "<manOeffneBetrieb> Stoppe Turbinenkreis");
		} state softStop
		when (TK2hand) {
			snlMsgLog( "<manOeffneBetrieb> Verlasse AUTO-Betrieb, gehe nach Handbetieb");		
		} state handBetrieb
 	}
	state softStop {
		entry {
			TC216AM = PID_MANUELL;
			CV206OROC = 0.5;
			TC216SOUT = 0.0;
			pvPut( TC216AM, SYNC);
			pvPut( CV206OROC, SYNC);
			pvPut( TC216SOUT);
			snlMsgLog( "<softStop> TuKr 2 soft Stop");
			TK2state = 8;
			pvPut( TK2state);
		}
		when( CV206OVAL < 0.15) {
			freigabeCMD = FREIGABE_AUS;
			pvPut( freigabeCMD);
			CV206OROC = 1.;
			pvPut( CV206OROC);
		} state gestoppt
		when( delay( 210.)) {
			snlMsgLog("<softStop> TuKr 2 CV206 schliesst nicht!");
		} state softStop
	}
	state stopit {
		entry {
			freigabeCMD = FREIGABE_AUS;
			pvPut( freigabeCMD, SYNC);
			TC216AM = PID_MANUELL;
			TC216SOUT = 0.;
			CV206OROC = 1.;
			pvPut( TC216AM, SYNC);
			pvPut( CV206OROC, SYNC);
			pvPut( TC216SOUT, SYNC);
			snlMsgLog( "<stopit> TuKr 2 NOT-Stop");
			TK2state = 9;
			pvPut( TK2state);
		}
		when( CV206OVAL < 0.15) {
		} state gestoppt
		when( delay( 60.)) {
			snlMsgLog("<stopit> TuKr 2 CV206 schliesst nicht!");
		} state stopit
	}
}
ss tk2ueberw {
	state not_ok {
		entry {
//			if (TK2state > 1) /* TK war bisher bereit */ {
//				freigabeCMD = FREIGABE_AUS;
//				pvPut( freigabeCMD); 
//				TC216AM = PID_MANUELL;
//				TC216SOUT = 0.0;
//				pvPut( TC216AM, SYNC);
//				pvPut( TC216SOUT);
//				snlMsgLog( "<not_ok> TuKr 2 Notstop");
//			}
			efClear( tk2FreigabeFlag);
		}
		when( UZV120offen && UZV121offen && UZV122offen
			&& !adsorberZu
			&& SV206bereit
			&& CV217offen
			&& summenAlarm==OK
			&& PvorTuSTAT!=HIHI_ALARM && PnachTuSTAT!=HIHI_ALARM
			&& kstrOK && cboxOK) {
				snlMsgLog( "<not_ok> Turbinenkreis 2 freigegeben");
		} state ok
		when ( TK2run) /* welche Bedingungen verhindern Start? */ {
			if( !UZV120offen || !UZV121offen || !UZV122offen) /*Coldbox Eingang*/
				snlMsgLog( "<not_ok> Coldbox Eingangsventile nicht (alle) offen");                 
			if( adsorberZu)
				snlMsgLog( "<not_ok> Adsorber nicht offen");                 
			if( !SV206bereit)
				snlMsgLog( "<not_ok> keine Freigabe TuKreis 2 Eingangsventil");                 
			if( !CV217offen )
				snlMsgLog( "<not_ok> TuKreis 2 Ausgangsventil nicht offen");                 
			if( summenAlarm==NOT_OK)
				snlMsgLog( "<not_ok> TuKreis 2 Summenalarm");                 
			if( PvorTuSTAT==HIHI_ALARM)
				snlMsgLog( "<not_ok> Rate of Change Alarm fuer PvorTu");                 
			if( PnachTuSTAT==HIHI_ALARM)
				snlMsgLog( "<not_ok> Rate of Change Alarm fuer PI217");                 
			if( !kstrOK)
				snlMsgLog( "<not_ok> Kompressor Strasse nicht OK");                 
			if( !cboxOK)
				snlMsgLog( "<not_ok> Coldbox nicht OK");                 
			TK2run = FALSE;
			pvPut( TK2run);
			} state not_ok
	}
	state ok {
		entry {
			efSet( tk2FreigabeFlag);
		}
		when( !UZV120offen || !UZV121offen || !UZV122offen) /*Coldbox Eingang*/ {
			snlMsgLog( "<ok> Coldbox Eingangsventile nicht (alle) offen");                 
		} state not_ok
		when( adsorberZu) {
			snlMsgLog( "<ok> Adsorber nicht offen");                 
		} state not_ok
		when( !SV206bereit) {
			snlMsgLog( "<ok> keine Freigabe TuKreis 2 Eingangsventil");                 
		} state not_ok
		when( !CV217offen ) {
			snlMsgLog( "<ok> TuKreis 2 Ausgangsventil nicht offen");                 
		} state not_ok
		when( summenAlarm==NOT_OK) {
			snlMsgLog( "<ok> TuKreis Summenalarm");                 
		} state not_ok
		when( PvorTuSTAT==HIHI_ALARM) {
			snlMsgLog( "<ok> Rate of Change Alarm fuer Druck vor CV206");                 
		} state not_ok
		when( PnachTuSTAT==HIHI_ALARM) {
			snlMsgLog( "<ok> Rate of Change Alarm fuer PI217");                 
		} state not_ok
		when( !kstrOK) {
			snlMsgLog( "<ok> Kompressor Strasse nicht OK");                 
		} state not_ok
		when( !cboxOK) {
			snlMsgLog( "<ok> Coldbox nicht OK");                 
		} state not_ok
	}
}
ss tuDrehzahlen {
	state initial {
		when ( SI4 > 400.0 && SI5 > 400.0){
		} state gross
		when ( SI4 < 100.0 && SI5 < 100.0){
		} state klein
		when ( TRUE) {} state mittel
	}
	state gross {
		entry {
			programRunning=OK;
			startFreigabe=NOT_OK;
			pvPut( programRunning);
			pvPut( startFreigabe);
			programRunningI15=OK;
			pvPut( programRunningI15);

			lagerg4BitSIklein = 0;
			pvPut( lagerg4BitSIklein);
			lagerg4BitSIgross = 1;
			pvPut( lagerg4BitSIgross);
			lagerg5BitSIklein = 0;
			pvPut( lagerg5BitSIklein);
			lagerg5BitSIgross = 1;
			pvPut( lagerg5BitSIgross);
			snlMsgLog( "<gross> Turbinendrehzahlen gross");
		}
		when ( (SI4 < 400.0 || SI5 < 400.0)
			&& (SI4 > 100.0 || SI5 > 100.0)) {
		} state mittel
		when ( SI4 < 100.0 && SI5 < 100.0){
		} state klein
		when (delay( 60.0)) {
			programRunning=OK;
			startFreigabe=NOT_OK;
			pvPut( programRunning);
			pvPut( startFreigabe);
			programRunningI15=OK;
			pvPut( programRunningI15);
		} state gross
	}
	state mittel {
		entry {
			programRunning=OK;
			startFreigabe=NOT_OK;
			pvPut( programRunning);
			pvPut( startFreigabe);
			programRunningI15=OK;
			pvPut( programRunningI15);

			lagerg4BitSIklein = 1;
			pvPut( lagerg4BitSIklein);
			lagerg4BitSIgross = 1;
			pvPut( lagerg4BitSIgross);
			lagerg5BitSIklein = 1;
			pvPut( lagerg5BitSIklein);
			lagerg5BitSIgross = 1;
			pvPut( lagerg5BitSIgross);
			snlMsgLog( "<mittel> Turbinendrehzahlen mittel");
		}
		when ( SI4 > 400.0 && SI5 > 400.0){
		} state gross
		when ( SI4 < 100.0 && SI5 < 100.0){
		} state klein
		when (delay( 60.0)) {
			programRunning=OK;
			startFreigabe=NOT_OK;
			pvPut( programRunning);
			pvPut( startFreigabe);
			programRunningI15=OK;
			pvPut( programRunningI15);
		} state mittel
	}
	state klein {
		entry {
			programRunning=OK;
			startFreigabe=NOT_OK;
			pvPut( programRunning);
			pvPut( startFreigabe);
			programRunningI15=OK;
			pvPut( programRunningI15);

			lagerg4BitSIklein = 1;
			pvPut( lagerg4BitSIklein);
			lagerg4BitSIgross = 0;
			pvPut( lagerg4BitSIgross);
			lagerg5BitSIklein = 1;
			pvPut( lagerg5BitSIklein);
			lagerg5BitSIgross = 0;
			pvPut( lagerg5BitSIgross);
			snlMsgLog( "<klein> Turbinendrehzahlen klein");
		}
		when ( SI4 > 400.0 && SI5 > 400.0){
		} state gross
		when ( (SI4 < 400.0 || SI5 < 400.0)
			&& (SI4 > 100.0 || SI5 > 100.0)) {
		} state mittel
		when( CV206zu && CV206OVAL < 0.15
			&& UZV648offen && UZV658offen
			&& PDA649==OK && PDA659==OK
			&& FA643==OK) {
		} state bereit
		when (delay( 60.0)) {
			programRunning=OK;
			startFreigabe=NOT_OK;
			pvPut( programRunning);
			pvPut( startFreigabe);
			programRunningI15=OK;
			pvPut( programRunningI15);
		} state klein
	}
	state bereit {
		entry {
			programRunning=OK;
			startFreigabe=OK;
			pvPut( programRunning);
			pvPut( startFreigabe);
			programRunningI15=OK;
			pvPut( programRunningI15);

			lagerg4BitSIklein = 1;
			pvPut( lagerg4BitSIklein);
			lagerg4BitSIgross = 0;
			pvPut( lagerg4BitSIgross);
			lagerg5BitSIklein = 1;
			pvPut( lagerg5BitSIklein);
			lagerg5BitSIgross = 0;
			pvPut( lagerg5BitSIgross);
			snlMsgLog( "<bereit> Turbinendrehzahlen bereit");
		}
		when ( SI4 > 400.0 && SI5 > 400.0){
		} state gross
		when ( (SI4 < 400.0 || SI5 < 400.0)
			&& (SI4 > 100.0 || SI5 > 100.0)) {
		} state mittel
		when( !CV206zu || CV206OVAL > 0.15 ) {
			snlMsgLog( "<bereit> CV206 nicht geschlossen");
		} state klein
		when( !UZV648offen || !UZV658offen) {
			snlMsgLog( "<bereit> UZV6x8 nicht offen");
		} state klein
		when( PDA649!=OK || PDA659!=OK) {
			snlMsgLog( "<bereit> Differenzdruck PDA6x9 nicht OK");
		} state klein
		when( FA643!=OK) {
			snlMsgLog( "<bereit> Kuehlwassersammelalarm FA643");
		} state klein
		when (delay( 60.0)) {
			programRunning=OK;
			startFreigabe=OK;
			pvPut( programRunning);
			pvPut( startFreigabe);
			programRunningI15=OK;
			pvPut( programRunningI15);
		} state bereit
	}
}
