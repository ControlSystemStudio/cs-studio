/* $$Revision: 1.8 $$ Letzte Aenderung von $$Author: gerke $$ am $$Date: 2009/10/15 14:49:57 $$ ** 
 
* File:        tukreis34.st
* Author:      Jan Hatje, Bernd Schoeneburg, Christian Gerke
* Created:     2009/10/09
*
	3 State-Sets zur Ueberwachung des Turbinenkreises.
	  sollen die Arbeit erledigen,
	  die frueher CTUKRx2, CFREIGA und CTUBTR erledigt haben (s.u.)
	  
	State-Set tuDrehzahlen ersetzt CFREIGA,
	======================  
	setzt bits in Abhaengigkeit von Turbinendrehzahlen in der Logik LAGERG:
		lagergBitSIklein	kleine Drehzahlen Bit fuer Turbine 6(7)
		lagergBitSIgross	grosse Drehzahlen Bit fuer Turbine 6(7)
	das Programm befindet sich entsprechend den Drehzahlen in den States
		gross,
		mittel oder
		klein
			das Programm geht von klein nach 
		bereit und
			setzt das Freigabebit _I13 in der Logik fuer SV306/7,
			wenn (wie bei CFREIGA)	
	        	Coldbox-Eintrittsventile UZV120/122/122 offen
	        	Turbinenkreis-Eintrittsentil CV306/7 geschlossen
	        	Lagergaseinspeisungen UZV6x8 offen
	        	kein Differenzdruckalarm PDAx9
	        	kein Kuehlwasseralarm KuehlwTemp
			das "program running" Bit _I14 wird in jedem State gesetzt

	State-Set tkueberw
	===================
	ueberprueft die gesammelten Bedingungen fuer Turbinenkreisfreigabe
	u.a. aus CTUKRx2
	und befindet sich entsprechend entweder in Zustand
	not_ok oder ok
	Ausserdem wird entsprechend das eventflag tkFreigabeFlag gesetzt
	Wenn im State not_ok das Kommando TKrun gegeben wird,
	werden alle Bedingungen fuer eine Freigabe ueberprueft;
	so kann nachvollzogen werden, warum wir in not_ok sind.
	 
	State-Set tukreis3
	==================
	beschreibt den Zustand des Turbinenkreises.
	Die States sind:
	gestoppt
		Hierher wird aus stopit gesprungen, 
		das Programm bleibt hier solange, wie das eventflag tkFreigabeFlag
		false ist.
	bereit
		Das eventflag tkFreigabeFlag ist true.
	startBereit
		Das eventflag tkFreigabeFlag ist true und
		in tuDrehzahlen wurde die Start-Freigabe erteilt.
	abwartBetrieb
		Nach dem Kommando TKrun landet das Programm hier und bleibt dann
		im Wesentlichen abhaengig von Enddruck und Drehzahlen diesem oder
		in einem der naechsten 2 States. Wenn hier oder in einem anderen
		State das eventflag tkFreigabeFlag false wird, springt das Programm
		nach stopit (NOT-Stop). Wenn TKrun wieder false wird, springt das
		Programm nach softStop.
	manDrosselBetrieb
	manOeffneBetrieb
	
	handBetrieb
		Das Programm laesst alles stehen und liegen und bleibt hier bis
		HAND wieder auf null gesetzt wird oder eine Stop-Situation eintritt.
	
	softStop
		Eingangsventil sanft schliessen.
	stopit
		NOT-Stopp: Pilotventil fuer Eingangsventil schliessen.
	
	mit D3 gab es mehrere Ueberwachungsprogramme:
	===========================================
	CTUKRx2 hat den Hartverdrahteten Sammelalarm XA306,
			die ROC Alarme fuer PeintrittTu(405) und PI401 sowie
			Trip-Signale von Kompressorstrasse oder Coldbxox ueberwacht.
			Bei Bedarf wurde das Magnetventil SV306(406) am TuKreis Eintritt entregt.
	CFREIGA hat in Abhaengigkeit von den Turbinendrehzahlen T-Bits in den 
	        Logiken UZV6x8 (MDV LAGERG) gesetzt
	        und das Freigabebit in der Logik fuer SV306(406) nur gesetzt,
	        wenn
	        	Coldbox-Eintrittsventile UZV120/122/122 offen
	        	Turbinenkreis-Eintrittsentil CV306(406) geschlossen
	        	Lagergaseinspeisungen UZV6x8 offen
	        	kein Differenzdruckalarm PDAx9
	        	kein Kuehlwasseralarm KuehlwTemp FA663(673)
	CTUBTR hat die Leistungsregelung gesteuert,
	       im Wesentlichen abhaengig von Enddruck und Drehzahlen
	       
	  		
* Revision history: 
* ... lang
*
*******************************************************************************/

program tukreis_3_4

#define OK 1
#define NOT_OK 0
#define SUPERVISORY 0
#define CLOSED_LOOP 1
#define AUF 1
#define ZU 0
#define PID_AUTO 0
#define PID_MANUELL 1

#define FREIGABE_AUS 0

#define BremsRegERR		(BremsRegSETP - BremsRegCVAL)
#define drehzahlenHoch	(BremsRegERR < -50.)

%%#include <string.h>
%%#include <math.h>
%%#include <alarm.h>
%%#include <snlMsgLog.h>

//char text[40];
char	tuKreis;
int     drehzahlenNiedrig;

evflag	tkFreigabeFlag;

short	programRunning;		assign programRunning		to "42SV{tKr}06_dlog.I14";
short	programRunningI15;	assign programRunningI15	to "42SV{tKr}06_dlog.I15";
short	lagergBitSIklein;	assign lagergBitSIklein		to "42UZV6{tNr}8_dlog.I13";
short	lagergBitSIgross;	assign lagergBitSIgross		to "42UZV6{tNr}8_dlog.I14";

short	TKstate;		assign TKstate			to "KS2:SNL:TK{tKr}:STATE_mbbi";
short	kstrOK;			assign kstrOK			to "KS2:SNL:KUE:KSTROK_bi";		monitor kstrOK;
short	cboxOK;			assign cboxOK			to "KS2:SNL:CUE:CBOXOK_bi";		monitor cboxOK;
short	TKrun;			assign TKrun			to "KS2:SNL:TK{tKr}:RUN_bi";	monitor TKrun;
short	TKhand;			assign TKhand			to "KS2:SNL:TK{tKr}:HAND_bi";	monitor TKhand;
string	buttonString;	assign buttonString		to "KS2:SNL:TK{tKr}:RUN_si";
short	UZV120offen;	assign UZV120offen		to "32UZV120_dlog.SB2";			monitor UZV120offen;
short	UZV121offen;	assign UZV121offen		to "32UZV121_dlog.SB2";			monitor UZV121offen;
short	UZV122offen;	assign UZV122offen		to "32UZV122_dlog.SB2";			monitor UZV122offen;
short	adsorberZu;		assign adsorberZu		to "42SV{tKr}06_dlog.SB4";		monitor adsorberZu;
short	freigabeCMD;	assign freigabeCMD		to "42SV{tKr}06_dlog.CMD";
short	SVx06bereit;	assign SVx06bereit		to "42SV{tKr}06_dlog.SB3";		monitor SVx06bereit;
short	startFreigabe;	assign startFreigabe	to "42SV{tKr}06_dlog.I13";		monitor startFreigabe;

short	CVx06zu;		assign CVx06zu			to "42CV{tKr}06_dlog.SB0";		monitor CVx06zu;
short	CVx07offen;		assign CVx07offen		to "42CV{tKr}07_dlog.SB2";		monitor CVx07offen;
short	summenAlarm;	assign summenAlarm		to "42XA{tKr}06_bi.VAL";		monitor summenAlarm;
short	LagergasOffen;	assign LagergasOffen	to "42UZV6{tNr}8_dlog.SB1";		monitor LagergasOffen;
short	LagergasDruck;	assign LagergasDruck	to "42PDA6{tNr}9_bi.VAL";		monitor LagergasDruck;
short	KuehlwTemp;		assign KuehlwTemp		to "42FA6{tNr}3_bi.VAL";		monitor KuehlwTemp;
double	Drehzahl;		assign Drehzahl			to "42SI6{tNr}0_ai.VAL";		monitor Drehzahl;
short	DruckTuEinROC;	assign DruckTuEinROC	to "42PI{tKr}05_calc.STAT";		monitor DruckTuEinROC;
short	EnddruckROC;	assign EnddruckROC		to "42PI401_calc.STAT";			monitor EnddruckROC;
double	TIx06LOW;		assign TIx06LOW			to "42TI{tKr}06_temp.LOW";		monitor TIx06LOW;
double	TIx06;			assign TIx06			to "42TI{tKr}06_temp.VAL";		monitor TIx06;
double	CVx06;			assign CVx06			to "42CV{tKr}06_ao.VAL";
double	CVx06OVAL;		assign CVx06OVAL		to "42CV{tKr}06_ao.OVAL";		monitor CVx06OVAL;
double	CVx06OROC;		assign CVx06OROC		to "42CV{tKr}06_ao.OROC";
double	BremsRegOUT;	assign BremsRegOUT		to "42SC6{tNr}0_pid.OUT";		monitor BremsRegOUT;
double	BremsRegSETP;	assign BremsRegSETP		to "42SC6{tNr}0_pid.VAL";
double	BremsRegCVAL;	assign BremsRegCVAL		to "42SC6{tNr}0_pid.CVAL";
double	PeintrittTu;	assign PeintrittTu		to "42PI{tKr}05_ai.VAL";		monitor PeintrittTu;
double	PvorTuV;		assign PvorTuV			to "42PI138_ai.VAL";			monitor PvorTuV;
double	Enddruck;		assign Enddruck			to "42PI401_ai.VAL";			monitor Enddruck;

option +r;

entry {
	tuKreis = *macValueGet( "tKr");
}

ss tukreis {

	state gestoppt {
		entry {
			TKrun = FALSE; /* Stop */
			TKhand = FALSE;
			sprintf( buttonString, "Start\nTuKr %c" ,tuKreis);
			pvPut( TKrun);
			pvPut( TKhand);
			freigabeCMD = FREIGABE_AUS;
			pvPut( freigabeCMD);
			pvPut( buttonString);
			snlMsgLog( "<gestoppt> TuKr %c gestoppt" ,tuKreis);
			TKstate = 1;
			pvPut( TKstate);
		}
		when( efTest( tkFreigabeFlag)) {
			snlMsgLog( "<gestoppt> TuKr %c freigegeben" ,tuKreis);
		} state bereit
	}
	state bereit {
		entry {
			TKstate = 2;
			pvPut( TKstate);
		}
		when( !CVx06zu && !efTest( tkFreigabeFlag)) {
			snlMsgLog( "<bereit> Notstopp bei Bereit");
		} state stopit

		when( !efTest( tkFreigabeFlag)) {
		} state gestoppt

		when( startFreigabe) {
			snlMsgLog( "<bereit> TuKr %c startbereit" ,tuKreis);
		}   state startBereit
	}
	state startBereit {
		option -e;
		entry {
			pvGet( CVx06OVAL); /* um das Monitor-Deadband zu ueberlisten! */
			TKstate = 3;
			pvPut( TKstate);
// folgendes ist ueberfluessig, weil die Drehzahlueberw schon die startfreigabe wegnimmt
			if (!CVx06zu || CVx06OVAL > 0.15)
				snlMsgLog( "<startBereit> Eintrittsventil nicht geschlossen");
			if (!LagergasOffen)
				snlMsgLog( "<startBereit> Lagergasventil nicht offen");
			if (LagergasDruck != OK)
				snlMsgLog( "<startBereit> Differenzdruck nicht OK");
			if (KuehlwTemp != OK)
				snlMsgLog( "<startBereit> Kuehlwasser Sammelalarm");
		}
		when( !CVx06zu && !efTest( tkFreigabeFlag)) {
			snlMsgLog( "<startBereit> Notstopp bei startBereit");
		} state stopit

		when( !efTest( tkFreigabeFlag)) {
		} state gestoppt
		
		when( CVx06OVAL > 0.) {
			snlMsgLog( "<startBereit> Eintrittsventil wird geoeffnet -> Handbetrieb");
		} state handBetrieb

		when( !startFreigabe) {
			snlMsgLog( "<startBereit> Turbine Startfreigabe weggenommen");
		} state bereit
		
		when(	CVx06zu && CVx06OVAL < 0.15 &&
				LagergasOffen &&
				LagergasDruck == OK &&
				KuehlwTemp == OK && TKrun
			) {
		} state abwartBetrieb

		when( delay( 60.0)) {
		} state startBereit
	}

	state handBetrieb {
		entry {
			TKstate = 10;
			pvPut( TKstate);
			TKrun = TRUE;
			TKhand = TRUE;
			pvPut (TKrun, SYNC);
			pvPut( TKhand, SYNC);
			CVx06OROC = 1.;
			pvPut( CVx06OROC);
			snlMsgLog( "<handBetrieb> Jetzt Handbetrieb TK%c" ,tuKreis);
		}
		when( !efTest( tkFreigabeFlag)
		    && (CVx06OVAL > 1. || !CVx06zu) ) {
		} state stopit
		
		when (!TKrun) {
		} state softStop
		
		when (!TKhand) {
			snlMsgLog( "<handBetrieb> Verlasse Handbetrieb, gehe nach Betrieb");		
		} state abwartBetrieb
	}

	state abwartBetrieb {
		entry {
			snlMsgLog( "<abwartBetrieb> TuKr %c laeuft" ,tuKreis);
			TKstate = 4;
			pvPut( TKstate);
			sprintf( buttonString, "Stop\nTuKr");
			pvPut( buttonString);
			CVx06OROC = 1.;
			pvGet( CVx06OVAL);
			CVx06 = CVx06OVAL;	/* Ventil anhalten */
			pvPut( CVx06OROC, SYNC);
			pvPut( CVx06);
			}
		when( !efTest( tkFreigabeFlag)) {
		} state stopit

// bei zu niedriger Austritts-Temperatur
// oder fast geschlossenem Drehzahlregler wird das Eintrittsventil gedrosselt
		when( TIx06 < TIx06LOW) {
			snlMsgLog( "<abwartBetrieb> TIx06 niedrig -> Eintritt drosseln");
		} state manDrosselBetrieb

		when( BremsRegOUT < 5.0) {
			snlMsgLog( "<abwartBetrieb> CV6x0 < 5% -> Eintritt drosseln");
		} state manDrosselBetrieb

// bei nicht zu hohem Enddruck, geoeffnetem Drehzahlregler,
// und ausreichend Vordruck wird das Eintrittsventil bis 90% geoeffnet 
		 when( Enddruck < 4.5
			&& BremsRegOUT > 8.0
			&& PeintrittTu < (PvorTuV - 0.3)
			&& CVx06OVAL < 90.0) {
		} state manOeffneBetrieb

		when( !TKrun) {
			snlMsgLog( "<abwartBetrieb> Stoppe Turbinenkreis");
		} state softStop

		when (TKhand) {
			snlMsgLog( "<abwartBetrieb> Verlasse AUTO-Betrieb, gehe nach Handbetieb");		
		} state handBetrieb
	}
	
	state manDrosselBetrieb {
		entry {
			CVx06OROC = 0.05;
			CVx06 = CVx06OVAL - 2.;
			pvPut( CVx06OROC, SYNC);
			pvPut( CVx06);
			pvMonitor( BremsRegCVAL);
			pvMonitor( BremsRegSETP);
			snlMsgLog( "<manDrosselBetrieb> TuKr %c im Drosselbetrieb" ,tuKreis);
			TKstate = 6;
			pvPut( TKstate);
		}
		when( !efTest( tkFreigabeFlag)) {
		} state stopit
		
		when( drehzahlenHoch && CVx06OROC < 0.5) {
			CVx06OROC = 0.5;
			pvPut( CVx06OROC);
		} state manDrosselBetrieb

		when( delay(5.0)) {
			CVx06OROC = drehzahlenHoch ? 0.5 : 0.05;
			pvPut( CVx06OROC);
			CVx06 = CVx06OVAL - 2.;
			pvPut( CVx06);
		} state manDrosselBetrieb

// bei nicht zu niedriger Austritts-Temperatur, geoeffnetem Drehzahlregler,
// und ausreichend Vordruck wird das Eintrittsventil bis 90% geoeffnet
// wenn 4.5 bar Enddruck nicht ueberschritten werden
		when( !(TIx06 < TIx06LOW)
			&& BremsRegOUT > 8.0
			&& PeintrittTu < (PvorTuV - 0.3)
			&& CVx06OVAL < 90.0
			&& Enddruck < 4.5) {
		} state manOeffneBetrieb

// wenn eine der Bedingungen fuer den Drosselbetrieb nicht mehr zutreffen und
// auch kein OeffneBetrieb sein soll, dann gehe zurueck zu abwartBetrieb und halte
// das Eingangsventil fest
		when( !(TIx06 < TIx06LOW)
			&& BremsRegOUT > 6.0
			&& (	Enddruck > 4.5 
				||	BremsRegOUT < 8.0
				||	PeintrittTu > (PvorTuV - 0.3)
				||	CVx06OVAL > 90.)) {
			CVx06OROC = 1.0;
			pvGet( CVx06OVAL);
			CVx06 = CVx06OVAL;
			pvPut( CVx06, SYNC);
			pvPut( CVx06OROC);
		} state abwartBetrieb

		when( !TKrun) {
			snlMsgLog( "<manDrosselBetrieb> Stoppe Turbinenkreis");
		} state softStop

		when (TKhand) {
			snlMsgLog( "<manDrosselBetrieb> Verlasse AUTO-Betrieb, gehe nach Handbetieb");		
		} state handBetrieb
		exit {
			pvStopMonitor( BremsRegCVAL);
			pvStopMonitor( BremsRegSETP);
		}
	}
	
	state manOeffneBetrieb {
		entry {
			CVx06OROC = 0.1;
			pvGet( CVx06OVAL);
			CVx06 = CVx06OVAL + 0.3;
			pvPut( CVx06OROC, SYNC);
			pvPut( CVx06);
			pvGet( BremsRegSETP);
			pvGet( BremsRegCVAL);
			drehzahlenNiedrig = BremsRegERR > 200.;
			snlMsgLog( "<manOeffneBetrieb> TuKr %c im man. Oeffnebetrieb" ,tuKreis);
			TKstate = 7;
			pvPut( TKstate);
		}
		when( !efTest( tkFreigabeFlag)) {
		} state stopit

// bei zu niedriger Austritts-Temperatur
// oder fast geschlossenem Drehzahlregler wird das Eintrittsventil gedrosselt
		when( TIx06 < TIx06LOW) {
			snlMsgLog( "<manOeffneBetrieb> TI%c06 niedrig -> Eintritt drosseln" ,tuKreis);
		} state manDrosselBetrieb

		when( BremsRegOUT < 5.0) {
			snlMsgLog( "<manOeffneBetrieb> CV6x0 < 5% -> Eintritt drosseln");
		} state manDrosselBetrieb

		when( Enddruck > 4.5 
			|| BremsRegOUT < 7.5
			|| PeintrittTu > (PvorTuV - 0.3)
			|| CVx06OVAL > 90) {
			CVx06OROC = 1.0;
			pvGet( CVx06OVAL);
			CVx06 = CVx06OVAL;
			pvPut( CVx06, SYNC);
			pvPut( CVx06OROC);
		} state abwartBetrieb

		when( delay(drehzahlenNiedrig ? 3.5 : 15.5)) {
			pvGet( CVx06OVAL);
			CVx06OROC = 0.1;
			CVx06 = CVx06OVAL + 0.3;
			pvPut( CVx06OROC, SYNC);
			pvPut( CVx06);
			pvGet( BremsRegSETP);
			pvGet( BremsRegCVAL);
			drehzahlenNiedrig = BremsRegERR > 200.;
		} state manOeffneBetrieb

		when( !TKrun) {
			snlMsgLog( "<manOeffneBetrieb> Stoppe Turbinenkreis");
		} state softStop

		when (TKhand) {
			snlMsgLog( "<manOeffneBetrieb> Verlasse AUTO-Betrieb, gehe nach Handbetieb");		
		} state handBetrieb
 	}

	state softStop {
		entry {
			CVx06OROC = 0.5;
			CVx06 = 0.0;
			pvPut( CVx06OROC, SYNC);
			pvPut( CVx06);
			snlMsgLog( "<softStop> TuKr %c soft Stop" ,tuKreis);
			TKstate = 8;
			pvPut( TKstate);
		}
		when( CVx06OVAL < 0.15) {
			freigabeCMD = FREIGABE_AUS;
			pvPut( freigabeCMD);
			CVx06OROC = 1.;
			pvPut( CVx06OROC);
		} state gestoppt

		when( delay( 210.)) {
			snlMsgLog("<softStop> TuKr %c Eintrittsventil schliesst nicht!" ,tuKreis);
		} state softStop
	}

	state stopit {
		entry {
			freigabeCMD = FREIGABE_AUS;
			pvPut( freigabeCMD);
			CVx06OROC = 1.;
			CVx06 = 0.;
			pvPut( CVx06OROC, SYNC);
			pvPut( CVx06, SYNC);
			snlMsgLog( "<stopit> TuKr %c NOT-Stop" ,tuKreis);
			TKstate = 9;
			pvPut( TKstate);
		}
		when( CVx06OVAL < 0.15) {
		} state gestoppt

		when( delay( 60.)) {
			snlMsgLog("<stopit> TuKr %c Eintrittsventil schliesst nicht!" ,tuKreis);
		} state stopit
	}
}

ss tkUeberw {
	state not_ok {
		entry {
			efClear( tkFreigabeFlag);
		}
		when( UZV120offen && UZV121offen && UZV122offen
			&& !adsorberZu
			&& SVx06bereit
			&& CVx07offen
			&& summenAlarm == OK
			&& DruckTuEinROC != HIHI_ALARM && EnddruckROC != HIHI_ALARM
			&& kstrOK && cboxOK) {
				snlMsgLog( "<not_ok> Turbinenkreis %c freigegeben" ,tuKreis);
		} state ok

		when ( TKrun) /* welche Bedingungen verhindern Start? */ {
			if( !UZV120offen || !UZV121offen || !UZV122offen) /*Coldbox Eingang*/
				snlMsgLog( "<not_ok> Coldbox Eingangsventile nicht (alle) offen");                 
			if( adsorberZu)
				snlMsgLog( "<not_ok> Adsorber nicht offen");                 
			if( !SVx06bereit)
				snlMsgLog( "<not_ok> keine Freigabe TuKreis %c Eingangsventil" ,tuKreis);                 
			if( !CVx07offen )
				snlMsgLog( "<not_ok> TuKreis %c Ausgangsventil nicht offen" ,tuKreis);                 
			if( summenAlarm == NOT_OK)
				snlMsgLog( "<not_ok> TuKreis %c Summenalarm" ,tuKreis);                 
			if( DruckTuEinROC == HIHI_ALARM)
				snlMsgLog( "<not_ok> Rate of Change Alarm fuer PI%c05" ,tuKreis);                 
			if( EnddruckROC == HIHI_ALARM)
				snlMsgLog( "<not_ok> Rate of Change Alarm fuer PI401");                 
			if( !kstrOK)
				snlMsgLog( "<not_ok> Kompressor Strasse nicht OK");                 
			if( !cboxOK)
				snlMsgLog( "<not_ok> Coldbox nicht OK");                 
			TKrun = FALSE;
			pvPut( TKrun);
			} state not_ok
	}

 	state ok {
		entry {
			efSet( tkFreigabeFlag);
		}
		when( !UZV120offen || !UZV121offen || !UZV122offen) /*Coldbox Eingang*/ {
			snlMsgLog( "<ok> Coldbox Eingangsventile nicht (alle) offen");                 
		} state not_ok

		when( adsorberZu) {
			snlMsgLog( "<ok> Adsorber nicht offen");                 
		} state not_ok

		when( !SVx06bereit) {
			snlMsgLog( "<ok> keine Freigabe TuKreis %c Eingangsventil" ,tuKreis);                 
		} state not_ok

		when( !CVx07offen ) {
			snlMsgLog( "<ok> TuKreis %c Ausgangsventil nicht offen" ,tuKreis);                 
		} state not_ok

		when( summenAlarm == NOT_OK) {
			snlMsgLog( "<ok> TuKreis Summenalarm");                 
		} state not_ok

		when( DruckTuEinROC == HIHI_ALARM) {
			snlMsgLog( "<ok> Rate of Change Alarm fuer PI%c05" ,tuKreis);                 
		} state not_ok

		when( EnddruckROC == HIHI_ALARM) {
			snlMsgLog( "<ok> Rate of Change Alarm fuer PI401");                 
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
		when ( Drehzahl > 400.0){
		} state gross

		when ( Drehzahl < 100.0){
		} state klein

		when ( TRUE) {} state mittel
	}

	state gross {
		entry {
			programRunning = OK;
			startFreigabe = NOT_OK;
			pvPut( programRunning);
			pvPut( startFreigabe);
			programRunningI15 = OK;
			pvPut( programRunningI15);

			lagergBitSIklein = 0;
			pvPut( lagergBitSIklein);
			lagergBitSIgross = 1;
			pvPut( lagergBitSIgross);
			snlMsgLog( "<gross> Turbinendrehzahlen gross");
		}
		when ( (Drehzahl < 400.0)
			&& (Drehzahl > 100.0)) {
		} state mittel

		when ( Drehzahl < 100.0){
		} state klein

		when (delay( 60.0)) {
			programRunning = OK;
			startFreigabe = NOT_OK;
			pvPut( programRunning);
			pvPut( startFreigabe);
			programRunningI15 = OK;
			pvPut( programRunningI15);
		} state gross
	}

	state mittel {
		entry {
			programRunning = OK;
			startFreigabe = NOT_OK;
			pvPut( programRunning);
			pvPut( startFreigabe);
			programRunningI15 = OK;
			pvPut( programRunningI15);

			lagergBitSIklein = 1;
			pvPut( lagergBitSIklein);
			lagergBitSIgross = 1;
			pvPut( lagergBitSIgross);
			snlMsgLog( "<mittel> Turbinendrehzahlen mittel");
		}
		when ( Drehzahl > 400.0){
		} state gross

		when ( Drehzahl < 100.0){
		} state klein

		when (delay( 60.0)) {
			programRunning = OK;
			startFreigabe = NOT_OK;
			pvPut( programRunning);
			pvPut( startFreigabe);
			programRunningI15 = OK;
			pvPut( programRunningI15);
		} state mittel
	}

	state klein {
		entry {
			programRunning = OK;
			startFreigabe = NOT_OK;
			pvPut( programRunning);
			pvPut( startFreigabe);
			programRunningI15 = OK;
			pvPut( programRunningI15);

			lagergBitSIklein = 1;
			pvPut( lagergBitSIklein);
			lagergBitSIgross = 0;
			pvPut( lagergBitSIgross);
			snlMsgLog( "<klein> Turbinendrehzahlen klein");
		}
		when ( Drehzahl > 400.0){
		} state gross

 		when ( (Drehzahl < 400.0)
			&& (Drehzahl > 100.0)) {
		} state mittel

		when( CVx06zu && CVx06OVAL < 0.15
			&& LagergasOffen
			&& LagergasDruck==OK
			&& KuehlwTemp==OK) {
		} state bereit

		when (delay( 60.0)) {
			programRunning = OK;
			startFreigabe = NOT_OK;
			pvPut( programRunning);
			pvPut( startFreigabe);
			programRunningI15 = OK;
			pvPut( programRunningI15);
		} state klein
	}
 	state bereit {
		entry {
			programRunning = OK;
			startFreigabe = OK;
			pvPut( programRunning);
			pvPut( startFreigabe);
			programRunningI15 = OK;
			pvPut( programRunningI15);

			lagergBitSIklein = 1;
			pvPut( lagergBitSIklein);
			lagergBitSIgross = 0;
			pvPut( lagergBitSIgross);
			snlMsgLog( "<bereit> Turbinendrehzahlen bereit");
		}
		when ( Drehzahl > 400.0){
		} state gross

		when ( (Drehzahl < 400.0)
			&& (Drehzahl > 100.0)) {
		} state mittel

		when( !CVx06zu || CVx06OVAL > 0.15 ) {
			snlMsgLog( "<bereit> Tu Eintritt nicht geschlossen");
		} state klein

		when( !LagergasOffen) {
			snlMsgLog( "<bereit> UZV6x8 nicht offen");
		} state klein

		when( LagergasDruck != OK) {
			snlMsgLog( "<bereit> Differenzdruck PDA6x9 nicht OK");
		} state klein

		when( KuehlwTemp != OK) {
			snlMsgLog( "<bereit> Kuehlwassersammelalarm!");
		} state klein

		when (delay( 60.0)) {
			programRunning = OK;
			startFreigabe = OK;
			pvPut( programRunning);
			pvPut( startFreigabe);
			programRunningI15 = OK;
			pvPut( programRunningI15);
		} state bereit
	}
}
