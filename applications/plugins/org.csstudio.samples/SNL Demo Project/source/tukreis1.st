/* $$Revision: 1.38 $$ Letzte Aenderung von $$Author: schoeneb $$ am $$Date: 2009/10/14 15:59:00 $$ ** 

* File:        tukreis1.st
* Author:      Christian Gerke
* Created:     2009/07/30
*
	3 State-Sets zur Ueberwachung des Turbinenkreises.
	  sollen die Arbeit erledigen,
	  die frueher CTUKRx2, CFREIGA und CTUBTR erledigt haben (s.u.)
	  
	State-Set tuDrehzahlen ersetzt CFREIGA,
	======================  
	setzt bits in Abhaengigkeit von Turbinendrehzahlen in der Logik LAGERG:
		lagerg1BitSIklein	kleine Drehzahlen Bit fuer Turbine 1	
		lagerg2BitSIklein	kleine Drehzahlen Bit fuer Turbine 2	
		lagerg3BitSIklein	kleine Drehzahlen Bit fuer Turbine 3	
		lagerg1BitSIgross	grosse Drehzahlen Bit fuer Turbine 1	
		lagerg2BitSIgross	grosse Drehzahlen Bit fuer Turbine 2	
		lagerg3BitSIgross	grosse Drehzahlen Bit fuer Turbine 3	
	das Programm befindet sich entsprechend den Drehzahlen in den States
		gross,
		mittel oder
		klein
			das Programm geht von klein nach 
		bereit und
			setzt das Freigabebit _I13 in der Logik fuer SV106,
			wenn (wie bei CFREIGA)	
	        	Coldbox-Eintrittsventile UZV120/122/122 offen
	        	Turbinenkreis-Eintrittsentil CV106 geschlossen
	        	Lagergaseinspeisungen UZV6x8 offen
	        	kein Differenzdruckalarm PDAx9
	        	kein Kuehlwasseralarm FA613
			das "program running" Bit _I14 wird in jedem State gesetzt

	State-Set tk1ueberw
	===================
	ueberprueft die gesammelten Bedingungen fuer Turbinenkreisfreigabe
	u.a. aus CTUKRx2
	und befindet sich entsprechend entweder in Zustand
	not_ok oder
	ok
	Ausserdem wird entsprechend das eventflag tk1FreigabeFlag gesetzt
	Wenn im State not_ok das Kommando TK1run gegeben wird,
	 werden alle Bedingungen fuer eine Freigabe ueberprueft;
	 so kann nachvollzogen werden, warum wir in not_ok sind.
	 
	State-Set tukreis1
	==================
	beschreibt den Zustand des Turbinenkreises.
	Die States sind:
	gestoppt
		hierher wird aus stopit gesprungen, 
		das Programm bleitb hier solange das eventflag tk1FreigabeFlag false ist.
	bereit
		das eventflag tk1FreigabeFlag ist true
	startBereit
		das eventflag tk1FreigabeFlag ist true und
		in tuDrehzahlen wurde die Start-Freigabe erteilt
	abwartBetrieb
		nach dem Kommando TK1run landet das Programm hier und bleibt dann
		im Wesentlichen abhaengig von TK-Austrittstemperatur
	        und Drehzahlen in einem der naechsten 3 States
	    wenn hier oder in einem der naechsten 3 States 
	    	as eventflag tk1FreigabeFlag false wird,
	    	springt das Programm nach stopit
	    wenn TK1run wieder false wird,
	    	sprint das Programm nach softStop 
	autoBetrieb
	manDrosselBetrieb
	manOeffneBetrieb
	
	softStop
		Eingangsventil sanft schliessen
	stopit
	
	mit D3 gab es mehrere Ueberwachungsprogramme:
	===========================================
	CTUKRx2 hat den Hartverdrahteten Sammelalarm XA106,
			die ROC Alarme fuer PvorTu und PI127 sowie
			Trip-Signale von Kompressorstrasse oder Coldbxox ueberwacht.
			Bei Bedarf wurde das Magnetventil SV106 am TuKreis Eintritt entregt.
	CFREIGA hat in Abhaengigkeit von den Turbinendrehzahlen T-Bits in den 
	          Logiken UZV6x8 (MDV LAGERG) gesetzt
	        und das Freigabebit in der Logik fuer SV106 nur gesetzt,
	        wenn
	        	Coldbox-Eintrittsventile UZV120/122/122 offen
	        	Turbinenkreis-Eintrittsentil CV106 geschlossen
	        	Lagergaseinspeisungen UZV6x8 offen
	        	kein Differenzdruckalarm PDAx9
	        	kein Kuehlwasseralarm FA613
	CTUBTR hat die Leistungsregelung gesteuert,
	       im Wesentlichen abhaengig von TK-Austrittstemperatur
	        und Drehzahlen
	       
	  		
* Revision history: 
*
*
*******************************************************************************/

program tukreis1

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
#define TC126ERR (TC126SETP - TC126CVAL)
#define SC610ERR (SC610SETP - SC610CVAL)
#define SC620ERR (SC620SETP - SC620CVAL)
#define SC630ERR (SC630SETP - SC630CVAL)
#define drehzahlenHoch (SC610ERR < -50. || SC620ERR < -50. || SC630ERR < -50.)

%%#include <string.h>
%%#include <math.h>
%%#include <alarm.h>
%%#include <snlMsgLog.h>

//char text[40];
int     drehzahlenNiedrig;

evflag	tk1FreigabeFlag;

short	programRunning;		assign programRunning		to "42SV106_dlog.I14";
short	programRunningI15;	assign programRunningI15	to "42SV106_dlog.I15";
short	lagerg1BitSIklein;	assign lagerg1BitSIklein	to "42UZV618_dlog.I13";
short	lagerg2BitSIklein;	assign lagerg2BitSIklein	to "42UZV628_dlog.I13";
short	lagerg3BitSIklein;	assign lagerg3BitSIklein	to "42UZV638_dlog.I13";
short	lagerg1BitSIgross;	assign lagerg1BitSIgross	to "42UZV618_dlog.I14";
short	lagerg2BitSIgross;	assign lagerg2BitSIgross	to "42UZV628_dlog.I14";
short	lagerg3BitSIgross;	assign lagerg3BitSIgross	to "42UZV638_dlog.I14";

short	TK1state;		assign TK1state			to "KS2:SNL:TK1:STATE_mbbi";
short	kstrOK;			assign kstrOK			to "KS2:SNL:KUE:KSTROK_bi";		monitor kstrOK;
short	cboxOK;			assign cboxOK			to "KS2:SNL:CUE:CBOXOK_bi";		monitor cboxOK;
short	TK1run;			assign TK1run			to "KS2:SNL:TK1:RUN_bi";		monitor TK1run;
short	TK1hand;		assign TK1hand			to "KS2:SNL:TK1:HAND_bi";		monitor TK1hand;
string	buttonString;	assign buttonString		to "KS2:SNL:TK1:RUN_si";
short	UZV120offen;	assign UZV120offen		to "32UZV120_dlog.SB2";			monitor UZV120offen;
short	UZV121offen;	assign UZV121offen		to "32UZV121_dlog.SB2";			monitor UZV121offen;
short	UZV122offen;	assign UZV122offen		to "32UZV122_dlog.SB2";			monitor UZV122offen;
short	adsorberZu;		assign adsorberZu		to "42SV106_dlog.SB4";			monitor adsorberZu;
short	freigabeCMD;	assign freigabeCMD		to "42SV106_dlog.CMD";
short	SV106bereit;	assign SV106bereit		to "42SV106_dlog.SB3";			monitor SV106bereit;
short	startFreigabe;	assign startFreigabe	to "42SV106_dlog.I13";			monitor startFreigabe;

short	CV106zu;		assign CV106zu			to "42CV106_dlog.SB0";			monitor CV106zu;
short	CV127offen;		assign CV127offen		to "42CV127_dlog.SB2";			monitor CV127offen;
short	summenAlarm;	assign summenAlarm		to "42XA106_bi.VAL";			monitor summenAlarm;
short	UZV618offen;	assign UZV618offen		to "42UZV618_dlog.SB1";			monitor UZV618offen;
short	UZV628offen;	assign UZV628offen		to "42UZV628_dlog.SB1";			monitor UZV628offen;
short	UZV638offen;	assign UZV638offen		to "42UZV638_dlog.SB1";			monitor UZV638offen;
short	PDA619;			assign PDA619			to "42PDA619_bi.VAL";			monitor PDA619;
short	PDA629;			assign PDA629			to "42PDA629_bi.VAL";			monitor PDA629;
short	PDA639;			assign PDA639			to "42PDA639_bi.VAL";			monitor PDA639;
short	FA613;			assign FA613			to "42FA613_bi.VAL";			monitor FA613;
double	SI1;			assign SI1				to "42SI610_ai.VAL";			monitor SI1;
double	SI2;			assign SI2				to "42SI620_ai.VAL";			monitor SI2;
double	SI3;			assign SI3				to "42SI630_ai.VAL";			monitor SI3;
short	PvorTuSTAT;		assign PvorTuSTAT		to "42PI012_calc.STAT";			monitor PvorTuSTAT;
short	PnachTuSTAT;	assign PnachTuSTAT		to "42PI127_calc.STAT";			monitor PnachTuSTAT;
double	TI126LOW;		assign TI126LOW			to "42TI126_temp.LOW";			monitor TI126LOW;
double	TI126;			assign TI126			to "42TI126_temp.VAL";			monitor TI126;
double	TC126SOUT;		assign TC126SOUT		to "42TC126_pid.SOUT";
short	TC126AM;		assign TC126AM			to "42TC126_pid.AM";
double	TC126CVAL;		assign TC126CVAL		to "42TC126_pid.CVAL";			monitor TC126CVAL;
double	TC126SETP;		assign TC126SETP		to "42TC126_pid.VAL";			monitor TC126SETP;
double	TC126VOUT;		assign TC126VOUT		to "42TC126_pid.VOUT";			monitor TC126VOUT;
double	CV106OVAL;		assign CV106OVAL		to "42CV106_ao.OVAL";			monitor CV106OVAL;
double	CV106OROC;		assign CV106OROC		to "42CV106_ao.OROC";
double	SC610OUT;		assign SC610OUT			to "42SC610_pid.OUT";			monitor SC610OUT;
double	SC620OUT;		assign SC620OUT			to "42SC620_pid.OUT";			monitor SC620OUT;
double	SC630OUT;		assign SC630OUT			to "42SC630_pid.OUT";			monitor SC630OUT;
double	SC610SETP;		assign SC610SETP			to "42SC610_pid.VAL";
double	SC620SETP;		assign SC620SETP			to "42SC620_pid.VAL";
double	SC630SETP;		assign SC630SETP			to "42SC630_pid.VAL";
double	SC610CVAL;		assign SC610CVAL		to "42SC610_pid.CVAL";
double	SC620CVAL;		assign SC620CVAL		to "42SC620_pid.CVAL";
double	SC630CVAL;		assign SC630CVAL		to "42SC630_pid.CVAL";
double	PI105;			assign PI105			to "42PI105_ai.VAL";			monitor PI105;
double	Pref;			assign Pref				to "42PI012_ai.VAL";			monitor Pref;

ss tukreis1 {

	state gestoppt {
		entry {
			TK1run = FALSE; /* Stop */
			TK1hand = FALSE;
			sprintf( buttonString, "Start\nTuKr 1");
			pvPut( TK1run);
			pvPut( TK1hand);
			freigabeCMD = FREIGABE_AUS;
			pvPut( freigabeCMD);
			pvPut( buttonString);
			snlMsgLog( "<gestoppt> TuKr 1 gestoppt");
			TK1state = 1;
			pvPut( TK1state);
		}
		when( efTest( tk1FreigabeFlag)) {
			snlMsgLog( "<gestoppt> TuKr 1 freigegeben");
		} state bereit
	}
	state bereit {
		entry {
			TK1state = 2;
			pvPut( TK1state);
		}
		when( !CV106zu && !efTest( tk1FreigabeFlag)) {
			snlMsgLog( "<bereit> Notstopp bei Bereit");
		} state stopit

		when( !efTest( tk1FreigabeFlag)) {
		} state gestoppt

		when( startFreigabe) {
			snlMsgLog( "<bereit> TuKr 1 startbereit");
		}   state startBereit
	}
	state startBereit {
		option -e;
		entry {
			pvGet( CV106OVAL); /* um das Monitor-Deadband zu ueberlisten! */
			TK1state = 3;
			pvPut( TK1state);
// folgendes ist ueberfluessig, weil die Drehzahlueberw schon die startfreigabe wegnimmt
			if (!CV106zu || CV106OVAL > 0.15)
				snlMsgLog( "<startBereit> Eintrittsventil nicht geschlossen");
			if (!UZV618offen || !UZV628offen || !UZV638offen)
				snlMsgLog( "<startBereit> Lagergasventile nicht alle offen");
			if (PDA619 != OK || PDA629 != OK || PDA639 != OK)
				snlMsgLog( "<startBereit> Differenzdruecke nicht alle OK");
			if (FA613 != OK)
				snlMsgLog( "<startBereit> Kuehlwasser Sammelalarm");
		}
		when( !CV106zu && !efTest( tk1FreigabeFlag)) {
			snlMsgLog( "<startBereit> Notstopp bei startBereit");
		} state stopit

		when( !efTest( tk1FreigabeFlag)) {
		} state gestoppt
		
		when( CV106OVAL > 0.) {
			snlMsgLog( "<startBereit> CV106 wird geoeffnet -> Handbetrieb");
		} state handBetrieb

		when( !startFreigabe) {
			snlMsgLog( "<startBereit> Turbinen Startfreigabe weggenommen");
		} state bereit
		
		when(	CV106zu && CV106OVAL < 0.15 &&
				UZV618offen && UZV628offen && UZV638offen &&
				PDA619 == OK && PDA629 == OK && PDA639 == OK &&
				FA613 == OK && TK1run
			) {
		} state abwartBetrieb

		when( delay( 60.0)) {
		} state startBereit
	}

	state handBetrieb {
		entry {
			TK1state = 10;
			pvPut( TK1state);
			TK1run = TRUE;
			TK1hand = TRUE;
			pvPut (TK1run, SYNC);
			pvPut( TK1hand, SYNC);
			CV106OROC = 1.;
			pvPut( CV106OROC);
			snlMsgLog( "<handBetrieb> Jetzt Handbetrieb TK1");
		}
		when( !efTest( tk1FreigabeFlag)
			&& (CV106OVAL > 1. || !CV106zu) ) {
		} state stopit
		
		when (!TK1run) {
		} state softStop
		
		when (!TK1hand) {
			snlMsgLog( "<handBetrieb> Verlasse Handbetrieb, gehe nach AUTO");		
		} state abwartBetrieb
	}

	state abwartBetrieb {
		entry {
			snlMsgLog( "<abwartBetrieb> TuKr 1 stationaer");
			TK1state = 4;
			pvPut( TK1state);
			sprintf( buttonString, "Stop\nTuKr 1");
			pvPut( buttonString);
			TC126AM = PID_MANUELL;
			pvPut( TC126AM, SYNC);
			TC126SOUT = CV106OVAL;
			pvPut( TC126SOUT);
			}
		when( !efTest( tk1FreigabeFlag)) {
		} state stopit
// bei zu niedriger Austritts-Temperatur
// oder fast geschlossenem Drehzahlregler wird das Eintrittsventil gedrosselt
		when( TI126 < TI126LOW) {
		} state manDrosselBetrieb
		when( TC126ERR > 2.0) /* ERR = setpoint-measurement */ {
		} state manDrosselBetrieb
		when( SC610OUT < 2.0 || SC620OUT < 2.0 || SC630OUT < 2.0) {
		} state manDrosselBetrieb
// bei geringer Abweichung Ist-Sollwert der Austritts-Temperatur automatische Regelung
		when( abs(TC126ERR) < 2.0) {
		} state autoBetrieb
// bei zu hoher Austritts-Temperatur, geoeffnetem Drehzahlregler,
// und ausreichend Vordruck wird das Eibtrittsventil bis 90% geoeffnet 
		when( TC126ERR < -2.0
			&& ( SC610OUT > 5.0 && SC620OUT > 5.0 && SC630OUT > 5.0)
			&& PI105 < (Pref - 0.3)
			&& TC126VOUT < 90.0) {
		} state manOeffneBetrieb
		when( !TK1run) {
			snlMsgLog( "<abwartBetrieb> Stoppe Turbinenkreis");
		} state softStop
		when (TK1hand) {
			snlMsgLog( "<abwartBetrieb> Verlasse AUTO-Betrieb, gehe nach Handbetieb");		
		} state handBetrieb
	}
	state autoBetrieb {
		entry {
			TC126AM = PID_AUTO;
			CV106OROC = 1.;
			pvPut( CV106OROC, SYNC);
			pvPut( TC126AM);
			snlMsgLog( "<autoBetrieb> TuKr 1 im Regelbetrieb");
			TK1state = 5;
			pvPut( TK1state);
			}
		when( !efTest( tk1FreigabeFlag)) {
		} state stopit
// bei zu niedriger Austritts-Temperatur
// oder fast geschlossenem Drehzahlregler wird das Eintrittsventil gedrosselt
		when( TI126 < TI126LOW) {
		} state manDrosselBetrieb
		when( TC126ERR > 2.0) {
		} state manDrosselBetrieb
		when( SC610OUT < 2.0 || SC620OUT < 2.0 || SC630OUT < 2.0) {
		} state manDrosselBetrieb
// bei zu hoher Austritts-Temperatur, geoeffnetem Drehzahlregler,
// und ausreichend Vordruck wird das Eintrittsventil bis 90% geoeffnet 
		when( TC126ERR < -2.0
			&& ( SC610OUT > 5.0 && SC620OUT > 5.0 && SC630OUT > 5.0)
			&& PI105 < (Pref - 0.3)
			&& TC126VOUT < 90.0) {
		} state manOeffneBetrieb
		when( !TK1run) {
			snlMsgLog( "<autoBetrieb> Stoppe Turbinenkreis");
		} state softStop
		when (TK1hand) {
			snlMsgLog( "<autoBetrieb> Verlasse AUTO-Betrieb, gehe nach Handbetieb");		
		} state handBetrieb
	}
	state manDrosselBetrieb {
		entry {
			TC126AM = PID_MANUELL;
			pvPut( TC126AM, SYNC);
			CV106OROC = 0.05;
			TC126SOUT = CV106OVAL - 2.;
			pvPut( CV106OROC, SYNC);
			pvPut( TC126SOUT);
			pvMonitor( SC610CVAL);
			pvMonitor( SC620CVAL);
			pvMonitor( SC630CVAL);
			pvMonitor( SC610SETP);
			pvMonitor( SC620SETP);
			pvMonitor( SC630SETP);
			snlMsgLog( "<manDrosselBetrieb> TuKr 1 im Drosselbetrieb");
			TK1state = 6;
			pvPut( TK1state);
		}
		when( !efTest( tk1FreigabeFlag)) {
		} state stopit
// bei nicht zu niedriger Austritts-Temperatur
// und geoeffneten Drehzahlreglern
// und geringer Abweichung Ist-Sollwert der Austritts-Temperatur
// wird das Eintrittsventil automatisch geregelt
		when( !(TI126 < TI126LOW)
			&& SC610OUT > 2.0 && SC620OUT > 2.0 && SC630OUT > 2.0
			&& abs(TC126ERR) < 2.0) {
		} state autoBetrieb
		when( drehzahlenHoch && CV106OROC < 0.5) {
			CV106OROC = 0.5;
			pvPut( CV106OROC);
		} state manDrosselBetrieb
		when( delay(5.0)) {
			CV106OROC = drehzahlenHoch ? 0.5 : 0.05;
			pvPut( CV106OROC);
			TC126SOUT = CV106OVAL - 2.;
			pvPut( TC126SOUT);
		} state manDrosselBetrieb
// bei zu hoher Austritts-Temperatur, geoeffnetem Drehzahlregler,
// und ausreichend Vordruck wird das Eibtrittsventil bis 90% geoeffnet 
		when( !(TI126 < TI126LOW)
			&& TC126ERR < -2.0
			&& ( SC610OUT > 5.0 && SC620OUT > 5.0 && SC630OUT > 5.0)
			&& PI105 < (Pref - 0.3)
			&& TC126VOUT < 90.0) {
		} state manOeffneBetrieb
		when(  !(TI126 < TI126LOW)	/* kein LOW-Alarm Temperatur */
			&& !(TC126ERR > 2.0)	/* nicht zu kalt */
			&& ( SC610OUT > 2.3 && SC620OUT > 2.3 && SC630OUT > 2.3)) {
		} state abwartBetrieb
		when( !TK1run) {
			snlMsgLog( "<manDrosselBetrieb> Stoppe Turbinenkreis");
		} state softStop
		when (TK1hand) {
			snlMsgLog( "<manDrosselBetrieb> Verlasse AUTO-Betrieb, gehe nach Handbetieb");		
		} state handBetrieb
		exit {
			pvStopMonitor( SC610CVAL);
			pvStopMonitor( SC620CVAL);
			pvStopMonitor( SC630CVAL);
			pvStopMonitor( SC610SETP);
			pvStopMonitor( SC620SETP);
			pvStopMonitor( SC630SETP);
		}
	}
	state manOeffneBetrieb {
		entry {
			TC126AM = PID_MANUELL;
			pvPut( TC126AM, SYNC);
			TC126SOUT = CV106OVAL + 0.3;
			CV106OROC = 0.1;
			pvPut( CV106OROC, SYNC);
			pvPut( TC126SOUT);
			pvGet( SC610SETP); pvGet( SC620SETP); pvGet( SC630SETP);
			pvGet( SC610CVAL); pvGet( SC620CVAL); pvGet( SC630CVAL);
			drehzahlenNiedrig = SC610ERR > 200. && SC620ERR > 200. && SC630ERR > 200.;
			snlMsgLog( "<manOeffneBetrieb> TuKr 1 im man. Oeffnebetrieb");
			TK1state = 7;
			pvPut( TK1state);
		}
		when( !efTest( tk1FreigabeFlag)) {
		} state stopit
// bei zu niedriger Austritts-Temperatur
// oder fast geschlossenem Drehzahlregler wird das Eintrittsventil gedrosselt
		when( TI126 < TI126LOW) {
			snlMsgLog( "<manOeffneBetrieb> TI126 niedrig -> Eintritt drosseln");
		} state manDrosselBetrieb
		when( TC126ERR > 2.0) /* ERR = setpoint - measurement */ {
			snlMsgLog( "<manOeffneBetrieb> TC126 Setpoint wird nicht erreicht -> Eintritt drosseln");
		} state manDrosselBetrieb
// wenn Bremsregler zwischen 3 und 4% oder Vordruck zu gering oder Ventil > 90% nicht weiter oeffnen
		when(  (SC610OUT < 4.5 || SC620OUT < 4.5 || SC630OUT < 4.5)
			|| !(PI105 < (Pref - 0.3))	/* Druck-Diff. zu klein */
			||  TC126VOUT > 90.0 ) {	/* Eing.-Ventil schon zu weit offen */
		} state abwartBetrieb
// bei geringer Abweichung Ist-Sollwert der Austritts-Temperatur automatische Regelung
		when( abs(TC126ERR) < 2.0) {
		} state autoBetrieb
		when( delay(drehzahlenNiedrig ? 3.5 : 15.5)) {
			TC126SOUT = CV106OVAL + 0.3;
			CV106OROC = 0.1;
			pvPut( CV106OROC, SYNC);
			pvPut( TC126SOUT);
			pvGet( SC610SETP); pvGet( SC620SETP); pvGet( SC630SETP);
			pvGet( SC610CVAL); pvGet( SC620CVAL); pvGet( SC630CVAL);
			drehzahlenNiedrig = SC610ERR > 200. && SC620ERR > 200. && SC630ERR > 200.;
		} state manOeffneBetrieb
		when( !TK1run) {
			snlMsgLog( "<manOeffneBetrieb> Stoppe Turbinenkreis");
		} state softStop
		when (TK1hand) {
			snlMsgLog( "<manOeffneBetrieb> Verlasse AUTO-Betrieb, gehe nach Handbetieb");		
		} state handBetrieb
 	}
	state softStop {
		entry {
			TC126AM = PID_MANUELL;
			CV106OROC = 0.5;
			TC126SOUT = 0.0;
			pvPut( TC126AM, SYNC);
			pvPut( CV106OROC, SYNC);
			pvPut( TC126SOUT);
			snlMsgLog( "<softStop> TuKr 1 soft Stop");
			TK1state = 8;
			pvPut( TK1state);
		}
		when( CV106OVAL < 0.15) {
			freigabeCMD = FREIGABE_AUS;
			pvPut( freigabeCMD);
			CV106OROC = 1.;
			pvPut( CV106OROC);
		} state gestoppt
		when( delay( 210.)) {
			snlMsgLog("<softStop> TuKr 1 CV106 schliesst nicht!");
		} state softStop
	}
	state stopit {
		entry {
			freigabeCMD = FREIGABE_AUS;
			pvPut( freigabeCMD, SYNC);
			TC126AM = PID_MANUELL;
			TC126SOUT = 0.;
			CV106OROC = 1.;
			pvPut( TC126AM, SYNC);
			pvPut( CV106OROC, SYNC);
			pvPut( TC126SOUT, SYNC);
			snlMsgLog( "<stopit> TuKr 1 NOT-Stop");
			TK1state = 9;
			pvPut( TK1state);
		}
		when( CV106OVAL < 0.15) {
		} state gestoppt
		when( delay( 60.)) {
			snlMsgLog("<stopit> TuKr 1 CV106 schliesst nicht!");
		} state stopit
	}
}
ss tk1ueberw {
	state not_ok {
		entry {
//			if (TK1state > 1) /* TK war bisher bereit */ {
//				freigabeCMD = FREIGABE_AUS;
//				pvPut( freigabeCMD); 
//				TC126AM = PID_MANUELL;
//				TC126SOUT = 0.0;
//				pvPut( TC126AM, SYNC);
//				pvPut( TC126SOUT);
//				snlMsgLog( "<not_ok> TuKr 1 Notstop");
//			}
			efClear( tk1FreigabeFlag);
		}
		when( UZV120offen && UZV121offen && UZV122offen
			&& !adsorberZu
			&& SV106bereit
			&& CV127offen
			&& summenAlarm==OK
			&& PvorTuSTAT!=HIHI_ALARM && PnachTuSTAT!=HIHI_ALARM
			&& kstrOK && cboxOK) {
				snlMsgLog( "<not_ok> Turbinenkreis 1 freigegeben");
		} state ok
		when ( TK1run) /* welche Bedingungen verhindern Start? */ {
			if( !UZV120offen || !UZV121offen || !UZV122offen) /*Coldbox Eingang*/
				snlMsgLog( "<not_ok> Coldbox Eingangsventile nicht (alle) offen");                 
			if( adsorberZu)
				snlMsgLog( "<not_ok> Adsorber nicht offen");                 
			if( !SV106bereit)
				snlMsgLog( "<not_ok> keine Freigabe TuKreis 1 Eingangsventil");                 
			if( !CV127offen )
				snlMsgLog( "<not_ok> TuKreis 1 Ausgangsventil nicht offen");                 
			if( summenAlarm==NOT_OK)
				snlMsgLog( "<not_ok> TuKreis 1 Summenalarm");                 
			if( PvorTuSTAT==HIHI_ALARM)
				snlMsgLog( "<not_ok> Rate of Change Alarm fuer Druck vor CV106");                 
			if( PnachTuSTAT==HIHI_ALARM)
				snlMsgLog( "<not_ok> Rate of Change Alarm fuer PI127");                 
			if( !kstrOK)
				snlMsgLog( "<not_ok> Kompressor Strasse nicht OK");                 
			if( !cboxOK)
				snlMsgLog( "<not_ok> Coldbox nicht OK");                 
			TK1run = FALSE;
			pvPut( TK1run);
			} state not_ok
	}
	state ok {
		entry {
			efSet( tk1FreigabeFlag);
		}
		when( !UZV120offen || !UZV121offen || !UZV122offen) /*Coldbox Eingang*/ {
			snlMsgLog( "<ok> Coldbox Eingangsventile nicht (alle) offen");                 
		} state not_ok
		when( adsorberZu) {
			snlMsgLog( "<ok> Adsorber nicht offen");                 
		} state not_ok
		when( !SV106bereit) {
			snlMsgLog( "<ok> keine Freigabe TuKreis 1 Eingangsventil");                 
		} state not_ok
		when( !CV127offen ) {
			snlMsgLog( "<ok> TuKreis 1 Ausgangsventil nicht offen");                 
		} state not_ok
		when( summenAlarm==NOT_OK) {
			snlMsgLog( "<ok> TuKreis Summenalarm");                 
		} state not_ok
		when( PvorTuSTAT==HIHI_ALARM) {
			snlMsgLog( "<ok> Rate of Change Alarm fuer Druck vor CV106");                 
		} state not_ok
		when( PnachTuSTAT==HIHI_ALARM) {
			snlMsgLog( "<ok> Rate of Change Alarm fuer PI127");                 
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
		when ( SI1 > 400.0 && SI2 > 400.0 && SI3 > 400.0){
		} state gross
		when ( SI1 < 100.0 && SI2 < 100.0 && SI3 < 100.0){
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

			lagerg1BitSIklein = 0;
			pvPut( lagerg1BitSIklein);
			lagerg1BitSIgross = 1;
			pvPut( lagerg1BitSIgross);
			lagerg2BitSIklein = 0;
			pvPut( lagerg2BitSIklein);
			lagerg2BitSIgross = 1;
			pvPut( lagerg2BitSIgross);
			lagerg3BitSIklein = 0;
			pvPut( lagerg3BitSIklein);
			lagerg3BitSIgross = 1;
			pvPut( lagerg3BitSIgross);
			snlMsgLog( "<gross> Turbinendrehzahlen gross");
		}
		when ( (SI1 < 400.0 || SI2 < 400.0 || SI3 < 400.0)
			&& (SI1 > 100.0 || SI2 > 100.0 || SI3 > 100.0)) {
		} state mittel
		when ( SI1 < 100.0 && SI2 < 100.0 && SI3 < 100.0){
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

			lagerg1BitSIklein = 1;
			pvPut( lagerg1BitSIklein);
			lagerg1BitSIgross = 1;
			pvPut( lagerg1BitSIgross);
			lagerg2BitSIklein = 1;
			pvPut( lagerg2BitSIklein);
			lagerg2BitSIgross = 1;
			pvPut( lagerg2BitSIgross);
			lagerg3BitSIklein = 1;
			pvPut( lagerg3BitSIklein);
			lagerg3BitSIgross = 1;
			pvPut( lagerg3BitSIgross);
			snlMsgLog( "<mittel> Turbinendrehzahlen mittel");
		}
		when ( SI1 > 400.0 && SI2 > 400.0 && SI3 > 400.0){
		} state gross
		when ( SI1 < 100.0 && SI2 < 100.0 && SI3 < 100.0){
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

			lagerg1BitSIklein = 1;
			pvPut( lagerg1BitSIklein);
			lagerg1BitSIgross = 0;
			pvPut( lagerg1BitSIgross);
			lagerg2BitSIklein = 1;
			pvPut( lagerg2BitSIklein);
			lagerg2BitSIgross = 0;
			pvPut( lagerg2BitSIgross);
			lagerg3BitSIklein = 1;
			pvPut( lagerg3BitSIklein);
			lagerg3BitSIgross = 0;
			pvPut( lagerg3BitSIgross);
			snlMsgLog( "<klein> Turbinendrehzahlen klein");
		}
		when ( SI1 > 400.0 && SI2 > 400.0 && SI3 > 400.0){
		} state gross
		when ( (SI1 < 400.0 || SI2 < 400.0 || SI3 < 400.0)
			&& (SI1 > 100.0 || SI2 > 100.0 || SI3 > 100.0)) {
		} state mittel
		when( CV106zu && CV106OVAL < 0.15
			&& UZV618offen && UZV628offen && UZV638offen
			&& PDA619==OK && PDA629==OK && PDA639==OK
			&& FA613==OK) {
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

			lagerg1BitSIklein = 1;
			pvPut( lagerg1BitSIklein);
			lagerg1BitSIgross = 0;
			pvPut( lagerg1BitSIgross);
			lagerg2BitSIklein = 1;
			pvPut( lagerg2BitSIklein);
			lagerg2BitSIgross = 0;
			pvPut( lagerg2BitSIgross);
			lagerg3BitSIklein = 1;
			pvPut( lagerg3BitSIklein);
			lagerg3BitSIgross = 0;
			pvPut( lagerg3BitSIgross);
			snlMsgLog( "<bereit> Turbinendrehzahlen bereit");
		}
		when ( SI1 > 400.0 && SI2 > 400.0 && SI3 > 400.0){
		} state gross
		when ( (SI1 < 400.0 || SI2 < 400.0 || SI3 < 400.0)
			&& (SI1 > 100.0 || SI2 > 100.0 || SI3 > 100.0)) {
		} state mittel
		when( !CV106zu || CV106OVAL > 0.15 ) {
			snlMsgLog( "<bereit> CV106 nicht geschlossen");
		} state klein
		when( !UZV618offen || !UZV628offen || !UZV638offen) {
			snlMsgLog( "<bereit> UZV6x8 nicht offen");
		} state klein
		when( PDA619!=OK || PDA629!=OK || PDA639!=OK) {
			snlMsgLog( "<bereit> Differenzdruck PDA6x9 nicht OK");
		} state klein
		when( FA613!=OK) {
			snlMsgLog( "<bereit> Kuehlwassersammelalarm FA613");
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
