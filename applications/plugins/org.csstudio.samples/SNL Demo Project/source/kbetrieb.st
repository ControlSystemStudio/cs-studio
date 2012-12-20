/*
* File:        kbetrieb.st
* Author:      Christian Gerke
* Created:     2009/01/09
*
* Revision history:
* 2009/09/28	Sbg		KBETRIEB0 integriert
*
*******************************************************************************/

program kbetrieb

#define SPJTBT 1.03
#define SPTUBT 2.
#define RLTUBT 0.5
#define DLTUBT 0.2
#define SPHDBT 3.2
#define RLHDBT 0.5
#define DLHDBT 1.5
#define SPAABT 13.1
#define RLAABT 0.5
#define DLAABT 1.
#define RLJTIN 0.
#define DLJTIN 0.2
#define DZNDMX 3200.
#define DZUDMX 3210.
#define StabMessageTime 300.
#define DZ108Z 1900.
#define DZ108R 1100.
#define ORC108 0.1
// max Reglervorgabe NC157 etc. 89.0% = 3000 U/min, 92.8% = 3200 U/min
#define NCNDMX 92.8

#define OK 1
#define NOT_OK 0
#define SUPERVISORY 0
#define CLOSED_LOOP 1
#define AUF 1
#define ZU 0
#define PID_MANUELL 1
#define PID_AUTO 0
#define STOP 0
#define START 1
#define CLOSE 0
#define OPEN 1
#define BETRIEB 3

#define EQS(x, y) (!strcmp( x, y))
#define PV_SET(pv, val) pv = val; pvPut (pv);
#define PV_SET_SYNC(pv, val) pv = val; pvPut (pv, SYNC);
#define PV_SET_ASYNC(pv, val) pv = val; pvPut (pv, ASYNC);
#define PV_ASSIGNED(pv, type, rname) type pv; assign pv to rname;
#define PV_ASSIGNED_M(pv, type, rname) PV_ASSIGNED(pv, type, rname) monitor pv;

#define NDK1 (kompBits & K1)
#define NDK2 (kompBits & K2)
#define NDK3 (kompBits & K3)
#define NDKR12 (kompBits & NDRED) // korrekt?? Red. fuer JT-ND

%%#include <stdio.h>
%%#include <time.h>
%%time_t t_abs_secs;
%%#include <string.h>
%%#include <snlMsgLog.h>
%%#include <alarm.h>

char text[40];

//Rezept-Var.
double tlimit;
assign tlimit to "KS2:SNL:KUE:TLIMIT_ai";
monitor tlimit;
double thyst;
assign thyst to "KS2:SNL:KUE:THYST_ai";
monitor thyst;
double dtime;
assign dtime to "KS2:SNL:KUE:DTIME_ai";

short kompBits; /* Bits 0,1,2,3,4,5 = ndRed, K1, K2, K3, hdRed, HD*/
assign kompBits to "KS2:SNL:KUE:KUEBERW_mbbiD";

#define NDRED	  1 
#define K1   	 (1<<1)
#define K2   	 (1<<2)
#define K3   	 (1<<3)
#define HDRED	 (1<<4)
#define HD   	 (1<<5)

evflag watchFlag;
evflag kompOkFlag;
evflag softStopEnableFlag;
evflag softStopFlag;

string	SoftStopString;		assign SoftStopString	to "KS2:SNL:KUE:SOFTSTOP_si";
string	KompStartString;	assign KompStartString	to "KS2:SNL:KUE:KOMPSTART_si";
short	SoftStopCmd;		assign SoftStopCmd		to "KS2:SNL:KUE:SOFTSTOP_bi";	monitor SoftStopCmd;
short	KompStartCmd;		assign KompStartCmd		to "KS2:SNL:KUE:KOMPSTART_bi";	monitor KompStartCmd;
short	kstrOK;				assign kstrOK			to "KS2:SNL:KUE:KSTROK_bi";

short	fu12SY157OMSL;		assign fu12SY157OMSL	to "12SY157_ao.OMSL";
double	fu12SY157;			assign fu12SY157		to "12SY157_ao";				monitor fu12SY157;
double	fu12SY157DRVH;		assign fu12SY157DRVH	to "12SY157_ao.DRVH";
short	fu12SY257OMSL;		assign fu12SY257OMSL	to "12SY257_ao.OMSL";
double	fu12SY257;			assign fu12SY257 		to "12SY257_ao";				monitor fu12SY257;
double	fu12SY257DRVH;		assign fu12SY257DRVH	to "12SY257_ao.DRVH";
short	fu12SY357OMSL;		assign fu12SY357OMSL	to "12SY357_ao.OMSL";
double	fu12SY357;			assign fu12SY357		to "12SY357_ao";
//short  byp12sy120OMSL;	assign byp12sy120OMSL	to "12SY120_bo.OMSL";
short	byp12sy120;			assign byp12sy120		to "12SY120_dlog.CMD";
//short  byp12sy220OMSL;	assign byp12sy220OMSL	to "12SY220_bo.OMSL";
short	byp12sy220;			assign byp12sy220		to "12SY220_dlog.CMD";
//short  byp12sy320OMSL;	assign byp12sy320OMSL	to "12SY320_bo.OMSL";
short	byp12sy320;			assign byp12sy320		to "12SY320_dlog.CMD";

double	HDoelTemp;		assign HDoelTemp	to "22TI560_ai";		monitor HDoelTemp; 
double	NDoelTemp;		assign NDoelTemp	to "12TI459_ai";		monitor NDoelTemp;
double	PC102SETP;		assign PC102SETP	to "12PC102_pid.VAL";
double	PC102RLIM;		assign PC102RLIM	to "12PC102_pid.RLIM";
double	PC102DLIM;		assign PC102DLIM	to "12PC102_pid.DLIM";
short	PC102AM;		assign PC102AM		to "12PC102_pid.AM";
short	PC102STAT;		assign PC102STAT	to "12PC102_pid.STAT";
string	PC102STATs;		assign	PC102STATs	to "12PC102_pid.STAT";
short	PC302STAT;		assign PC302STAT	to "12PC302_pid.STAT";
string	PC302STATs;		assign PC302STATs	to "12PC302_pid.STAT";
double	PC302SETP;		assign PC302SETP	to "12PC302_pid.VAL";
double	PC302RLIM;		assign PC302RLIM	to "12PC302_pid.RLIM";
double	PC302DLIM;		assign PC302DLIM	to "12PC302_pid.DLIM";
short	PC302AM;		assign PC302AM		to "12PC302_pid.AM";

// Sammel-Trip von der SPS ND Red:
//double  T0; assign  T0 to "10Y235_bi"; monitor T0;
// Sammel-Trip von der SPS ND K1:
short T1;				assign T1			to "12Y135_bi";			monitor T1;
// Sammel-Trip von der SPS ND K2:
short T2;				assign T2			to "12Y235_bi";			monitor T2;
// Sammel-Trip von der SPS ND K3:
short T3;				assign T3			to "12Y335_bi";			monitor T3;
// Sammel-Trip von der SPS HD Red:
//double T4;			assign T4			to "20Y535_bi";			monitor T4;
// Sammel-Trip von der SPS HD K1:
short T5;				assign T5			to "22Y535_bi";			monitor T5;
// He Temp nach Kuehler E1 2. Strasse:
double T6;				assign T6			to "12TI402_ai";		monitor T6;
double T6hihi;			assign T6hihi		to "12TI402_ai.HIHI";	monitor T6hihi;

short	freigabeHD;		assign freigabeHD	to "22Y545_bo";
short	freigabeND;		assign freigabeND	to "12KTRIP_dlog.I13";
double	hdSchieber;		assign hdSchieber	to "22GI519_ai";		monitor hdSchieber;
short	CV520OMSL;		assign CV520OMSL	to "22CV520_ao.OMSL";
short	CV520auf;		assign CV520auf		to "22CV520_dlog.SB2";	monitor CV520auf;
short	UZV120zu;		assign UZV120zu 	to "32UZV120_dlog.SB0";
short	UZV121zu;		assign UZV121zu 	to "32UZV121_dlog.SB0";
short	UZV122zu;		assign UZV122zu 	to "32UZV122_dlog.SB0";
double	PC106SETP;		assign PC106SETP	to "32PC106_pid.VAL";
double	PC106RLIM;		assign PC106RLIM	to "32PC106_pid.RLIM";
double	PC106DLIM;		assign PC106DLIM	to "32PC106_pid.DLIM";
short	PC106AM;		assign PC106AM		to "32PC106_pid.AM";
double	PC106SOUT;		assign PC106SOUT	to "32PC106_pid.SOUT";
short	PC106STAT;		assign PC106STAT	to "32PC106_pid.STAT";
string	PC106STATs;		assign PC106STATs	to "32PC106_pid.STAT";
short 	CV106OMSL;		assign CV106OMSL	to "32CV106_ao.OMSL";
short 	CV107OMSL;		assign CV107OMSL 	to "32CV107_ao.OMSL";
double	CV107;			assign CV107		to "32CV107_ao";
short	NC157AM;		assign NC157AM		to "12NC157_pid.AM";
double	NC157SOUT;		assign NC157SOUT	to "12NC157_pid.SOUT";
double	NC157DRVH;		assign NC157DRVH	to "12NC157_pid.DRVH";
double	CV108;			assign CV108		to "32CV108_ao";
short	CV108OMSL;		assign CV108OMSL	to "32CV108_ao.OMSL";
double	CV108DRVH;		assign CV108DRVH	to "32CV108_ao.DRVH";
double	CV108OROC;		assign CV108OROC	to "32CV108_ao.OROC";
double	CV109;			assign CV109		to "32CV109_ao";
short	CV109OMSL;		assign CV109OMSL	to "32CV109_ao.OMSL";
double	PC503SETP;		assign PC503SETP	to "22PC503_pid.VAL";
short	PC503AM;		assign PC503AM		to "22PC503_pid.AM";
double	PC503SOUT;		assign PC503SOUT	to "22PC503_pid.SOUT";
double	PC503RLIM;		assign PC503RLIM	to "22PC503_pid.RLIM";
double	PC503DLIM;		assign PC503DLIM	to "22PC503_pid.DLIM";
short 	PC503STAT;		assign PC503STAT	to "22PC503_pid.STAT";
string	PC503STATs;		assign PC503STATs	to "22PC503_pid.STAT";

//short   D0; assign  D0 to "10K2BETR_dlog";
short	K1BETR;			assign K1BETR		to "12K1BETR_dlog";		monitor K1BETR;
short	K2BETR;			assign K2BETR		to "12K2BETR_dlog";		monitor K2BETR;
short	K3BETR;			assign K3BETR		to "12K3BETR_dlog";		monitor K3BETR;
//short	D4;				assign D4			to "20K1BETR_dlog";
short	hdK1BETR;		assign hdK1BETR		to "22K1BETR_dlog";		monitor hdK1BETR;
short	K1BETRcmd;		assign K1BETRcmd	to "12K1BETR_dlog.CMD";
short	K2BETRcmd;		assign K2BETRcmd	to "12K2BETR_dlog.CMD";
short	K3BETRcmd;		assign K3BETRcmd	to "12K3BETR_dlog.CMD";
short	hdK1BETRcmd;	assign hdK1BETRcmd	to "22K1BETR_dlog.CMD";
short	HS591CMD;		assign HS591CMD		to "32HS591_dlog.CMD";


ss Ueberwachung {
	state inactive {
		when( efTest( watchFlag)) {
		} state kompOK
	}
	state kompOK {
		entry { efSet ( kompOkFlag); }

//		Check Oil-Temp
		when (HDoelTemp>tlimit) {
			snlMsgLog( "HDoelTemp=%.2f > %.2f", HDoelTemp, tlimit);                 
		} state kompFehler
		when (NDoelTemp>tlimit) {
			snlMsgLog( "NDoelTemp=%.2f > %.2f", NDoelTemp, tlimit);                 
		} state kompFehler
//		Check He-Temp
		when( (T6>T6hihi)) {
			snlMsgLog( "ND He-Kuehler Temp %.2f > %.2f", T6, T6hihi);
		} state kompFehler
//		Pruefe Sammeltrips von den Niederdruckmaschinen
		when((T1==NOT_OK) || (T2==NOT_OK) || (T3==NOT_OK)) {
			snlMsgLog( "ND-Trip K1.K2.K3 = %d.%d.%d", T1, T2, T3);
		} state kompFehler
//		Pruefe Sammeltrip von der Hochdruckmaschine
		when( T5==NOT_OK) {
			snlMsgLog( "HD-Trip");
		} state kompFehler
		when( !efTest( watchFlag)) {
		} state inactive

		exit { efClear( kompOkFlag); }
	}
	state kompFehler {
		when (	(HDoelTemp < tlimit-thyst) &&
				(NDoelTemp < tlimit-thyst) &&
				(T1 == OK) && (T2 == OK) && (T3 == OK) &&
				(T5 == OK) && 
				(T6 < (T6hihi-thyst))
			) {
			snlMsgLog( "Kompressorstrasse Bedingungen OK");                 
		} state kompOK
		when (!efTest( watchFlag)) {
		} state inactive
	}
}		

ss StopQuery {
	state inactive {
		entry {
			sprintf( SoftStopString, "- - - -");
			pvPut( SoftStopString);
		}
		when( efTest( softStopEnableFlag)) {
		} state watchStop
	}
	state watchStop {
		entry {
//			SoftStopCmd = FALSE;	/* Bit zuruecksetzen */
//			pvPut( SoftStopCmd);
			sprintf( SoftStopString, "Kompressoren STOP");
			pvPut( SoftStopString);
		}
		when (SoftStopCmd) {
			// Bit zuruecksetzen fuer Bestaetigung durch Operator
			PV_SET( SoftStopCmd, FALSE)	
			sprintf( SoftStopString, "Komp Stop?\nJa: druecke nochmal!");
			pvPut( SoftStopString);
			snlMsgLog( "Kompressoren SOFT-STOP angefordert");
		} state SoftStop

		when( !efTest( softStopEnableFlag)) {
		} state inactive
	}
	state SoftStop {
		entry {
			pvGet( dtime);
			pvGet( UZV120zu);
			pvGet( UZV121zu);
			pvGet( UZV122zu);
			}
		// check if operator confirmed SoftStop
		when( !(UZV120zu && UZV121zu && UZV122zu)) {
			PV_SET( SoftStopCmd, FALSE);
			snlMsgLog( "Stop nicht moeglich weil CBX in Betrieb");
		} state watchStop
		
		when( SoftStopCmd) {
			PV_SET( SoftStopCmd, FALSE);
			snlMsgLog( "Softstop bestaetigt");
			efSet( softStopFlag);
		} state watchStop

		when (delay( dtime)) {
		// Operator hat Softstop nicht innerhalb Zeit dtime bestaetigt
//			sprintf( SoftStopString, "Kein SoftStop!");
//			pvPut( SoftStopString);
			snlMsgLog( "Softstop nicht bestaetigt");
		} state watchStop

		when( !efTest( softStopEnableFlag)) {
		} state inactive
	}
}

ss Kompressoren {
	state initial {
		entry {
			kompBits = K1 | K2 | K3;	/*-Red+K1+K2+K3 selected*/
			kompBits |= HD;				/*-Red+K22 selected*/
			pvPut( kompBits);
			PV_SET( kstrOK, NOT_OK)
			PV_SET( SoftStopCmd, FALSE)
			PV_SET( KompStartCmd, FALSE)
			snlMsgLog( "selected Kompressors: %d.%d.%d.%d.%d.%d", 
				kompBits & NDRED, NDK1, NDK2, NDK3, 
				kompBits & HDRED, kompBits & HD);
			PV_SET( freigabeND, OK)		/* Bit I13 in KOMTRIP */
		}
		when( TRUE) {
		} state NichtBereit
	}

	state NichtBereit {
		entry{
			efClear( watchFlag);
			PV_SET( kstrOK, NOT_OK)
			snlMsgLog( "T1235=%d.%d.%d.%d, T(He)=%.2f", T1, T2, T3, T5, T6);
		}
		when (	(HDoelTemp < tlimit-thyst) &&
				(NDoelTemp < tlimit-thyst) &&
				(T1 == OK) && (T2 == OK) && (T3 == OK) &&
				(T5 == OK) && 
				(T6 < (T6hihi-thyst))
			) {
			efSet( watchFlag);
		} state Bereit
	}

	state Bereit {
		entry {
			PV_SET( kstrOK, OK)
	 		PV_SET( freigabeHD, OK)
			snlMsgLog( "Kompressoren bereit");

			sprintf( SoftStopString, "- - - -");
			pvPut( SoftStopString);

			PV_SET( KompStartCmd, FALSE)	/* Bit zuruecksetzen fuer Start */
			sprintf( KompStartString, "START");
			pvPut( KompStartString);
		}
		when( !efTest (kompOkFlag)) {
			snlMsgLog( "Kompressoren nicht bereit");
		} state NichtBereit

		when (KompStartCmd) {
			PV_SET (KompStartCmd, FALSE)	/* Bit zuruecksetzen fuer Bestaetigung durch Operator */
			sprintf( KompStartString, "Komp Start?\nJa: druecke nochmal!");
			pvPut( KompStartString);
			snlMsgLog( "Kompressoren Start-Befehl");
		} state KompStart
	}

	state KompStart {
		entry {
			pvGet( dtime);
			PV_SET( SoftStopCmd, FALSE)
			sprintf( SoftStopString, "STOP");
			pvPut( SoftStopString);
		}
		when( !efTest (kompOkFlag)) {
			snlMsgLog( "Kompressoren nicht bereit");
		} state NichtBereit

		when( SoftStopCmd) {
			snlMsgLog( "Kompressoren Start Abbruch");
		} state Stopit1
		
		// check if operator confirmed Kompressor Start
		when( KompStartCmd) {
			efSet( softStopEnableFlag);
			sprintf( KompStartString, "Startet...");
			pvPut( KompStartString);
			snlMsgLog( "Kompressoren Start bestaetigt");
			PV_SET_SYNC( PC102SETP, SPJTBT)
			PV_SET_SYNC( PC302SETP, SPTUBT)
			PV_SET_SYNC( PC503SETP, SPHDBT)
			PV_SET_SYNC( PC106SETP, SPAABT)

			PV_SET_SYNC( PC302RLIM, RLTUBT)
			PV_SET_SYNC( PC302DLIM, DLTUBT)
			PV_SET_SYNC( PC503RLIM, RLHDBT)
			PV_SET_SYNC( PC503DLIM, DLHDBT)
			PV_SET_SYNC( PC106RLIM, RLAABT)
			PV_SET_SYNC( PC106DLIM, DLAABT)

			PV_SET( CV108OMSL, CLOSED_LOOP)
			PV_SET( NC157AM, PID_AUTO)
			PV_SET( fu12SY357OMSL, CLOSED_LOOP)
			PV_SET( CV109OMSL, CLOSED_LOOP)
			// hier alternativer Code fuer Redundanz
			// Auf- Abladeregelung
			PV_SET( CV106OMSL, CLOSED_LOOP)
			PV_SET( CV107OMSL, CLOSED_LOOP)
			// hier alternativer Code fuer Redundanz
			// folgendes HS591 UNIT abhaengig (Box?)
			PV_SET( HS591CMD, OPEN)
			snlMsgLog( "<Stabilize> Der Trockner wurde zugeschaltet");
		} state KompStart1Complete
		when (delay( dtime)) {
		// Operator hat Start nicht innerhalb Zeit dtime bestaetigt
			snlMsgLog( "Kompressoren Start nicht bestaetigt");
		} state Bereit
	}

	state KompStart1Complete {
		entry {
			PV_SET_SYNC( PC102AM, PID_AUTO)
			PV_SET_SYNC( PC302AM, PID_AUTO)
			PV_SET_SYNC( PC503AM, PID_AUTO)
			PV_SET_SYNC( PC106AM, PID_AUTO)
		}

		when( !efTest (kompOkFlag)) {
			snlMsgLog( "Kompressoren nicht bereit");
		} state EmergencyStop

		when (efTestAndClear( softStopFlag)) {
		} state Stopit1

		when( delay (1.5)) {
			snlMsgLog( "Regler auf AUTO");
		} state Stabilize
	}
	
	state Stabilize {
		entry {
			pvMonitor (PC102STAT);
			pvMonitor (PC302STAT);
			pvMonitor (PC503STAT);
			pvMonitor (PC106STAT);
		}

		when( !efTest (kompOkFlag)) {
			snlMsgLog( "Kompressoren nicht bereit");
		} state EmergencyStop

		when (efTestAndClear( softStopFlag)) {
		} state Stopit1

		when (	(PC102STAT == NO_ALARM) &&
				(!(kompBits &(K3|NDRED)) || PC302STAT == NO_ALARM) &&
				(PC503STAT == NO_ALARM) &&
				(PC106STAT == NO_ALARM)	) {
			// set RUN Flag fuer Strasse
			snlMsgLog( "<Stabilize> Kompressorstrasse bereit fuer CBX Start");
			PV_SET( PC102RLIM, RLJTIN)
			PV_SET( PC102DLIM, DLJTIN)
		} state Warte_CBX

		when (delay (StabMessageTime)) {
			snlMsgLog( "<Stabilize> Kompressorstrasse wird nicht stabil");
			if (PC102STAT != NO_ALARM) {
				pvGet( PC102STATs);
				snlMsgLog( "PC102 %s", PC102STATs);
			}
			if (PC503STAT != NO_ALARM) {
				pvGet( PC503STATs);
				snlMsgLog( "PC503 %s", PC503STATs);
			}
			if (PC106STAT != NO_ALARM) {
				pvGet( PC106STATs);
				snlMsgLog( "PC106 %s", PC106STATs);
			}
		} state Stabilize
		
		exit {
			pvStopMonitor (PC102STAT);
			pvStopMonitor (PC302STAT);
			pvStopMonitor (PC503STAT);
			pvStopMonitor (PC106STAT);
		}
	}

	state Warte_CBX {
		// Hier warten wir auf die Zuordnung einer Coldbox
		when( !efTest (kompOkFlag)) {
			snlMsgLog( "Kompressoren nicht bereit");
		} state EmergencyStop

		when (efTestAndClear( softStopFlag)) {
		} state Stopit1
		
		when (TRUE) { // Annahme: Box zugeteilt
			snlMsgLog( "CBX zugeordnet");
			PV_SET( fu12SY157DRVH, DZNDMX)	
			PV_SET( fu12SY257DRVH, DZNDMX)
//			if (NDKR12) {
//				PV_SET( fumf10sy257DRVH, DZNDMX)
//			}
			PV_SET( NC157DRVH, NCNDMX) // kein Unterdruckbetrieb
		} state BypassBetrieb
	}
	
	state LastBetrieb {
		entry {
			PV_SET_SYNC( CV108OMSL, SUPERVISORY)
			PV_SET_SYNC( CV108OROC, ORC108)
			PV_SET( CV108, 0.)
			snlMsgLog( "JT-Maschinen Last-Betrieb");
		}
		when( !efTest (kompOkFlag)) {
			snlMsgLog( "Kompressoren stoppen");
		} state EmergencyStop
		
		when (efTestAndClear( softStopFlag)) {
		} state Stopit1

		when(!(	(NDK1   && fu12SY157 > DZ108R) ||
				(NDK2   && fu12SY257 > DZ108R)
//				(NDK2   && fu12SY257 > DZ108R) ||
//				(NDKR12 && fumf10sy257 > DZ108R)
			 )) {
		} state BypassBetrieb
		
//		when( /box nicht mehr zugeteilt/ ) {
//		} state Warte_CBX
	}
	
	state BypassBetrieb {
		entry {
			PV_SET( CV108OROC, 100.)
			PV_SET( CV108OMSL, CLOSED_LOOP)
			snlMsgLog( "JT-Maschinen Bypass-Betrieb");
		}
		when( !efTest (kompOkFlag)) {
			snlMsgLog( "Kompressoren stoppen");
		} state EmergencyStop
		
		when (efTestAndClear( softStopFlag)) {
		} state Stopit1

		when(	(NDK1   && fu12SY157 > DZ108Z) ||
				(NDK2   && fu12SY257 > DZ108Z)
//				(NDK2   && fu12SY257 > DZ108Z) ||
//				(NDKR12 && fumf10sy257 > DZ108Z)
			 ) {
		} state LastBetrieb
	}
	

	state EmergencyStop {
		entry {
			kstrOK=NOT_OK;
			pvPut( kstrOK);
// Sofort Hochdruckfreigabe Y545 wegnehmen
 	 		freigabeHD = NOT_OK;
	 		pvPut( freigabeHD);
	 		/*
			es fehlt:	Trip-Signal fuer Coldbox
						KSTR-runflag = 0
			*/
		}
//Alles weitere wie bei SoftStop
 		when (TRUE) {
			snlMsgLog( "NotStop");
		} state Stopit1
	}

	state Stopit1 {
		entry {
// Aufladeventil 32CV107 manuell und schliessen
	 		PV_SET_SYNC( CV107OMSL, SUPERVISORY)
	 		PV_SET( CV107, 0.)
// Sollwerte von allen ND-Frequenzumformern auf 0	 		
	 		PV_SET_SYNC( fu12SY157OMSL, SUPERVISORY)
	 		PV_SET( fu12SY157, 0.)
	 		PV_SET_SYNC( fu12SY257OMSL, SUPERVISORY)
	 		PV_SET( fu12SY257, 0.)
	 		PV_SET_SYNC( fu12SY357OMSL, SUPERVISORY)
	 		PV_SET( fu12SY357, 0.)

// 1. ND-Kompressor Bypass oeffnen	 		
			PV_SET( byp12sy120, AUF)
		}
		when (delay( 15.)) {
		// Beruhigungszeit fuer den Prozess
		} state Stopit2
	}	
	state Stopit2 {
		entry {
// 2. ND-Kompressor Bypass oeffnen	 		
			PV_SET( byp12sy220, AUF)
		}
		when (delay( 15.)) {
		// Beruhigungszeit fuer den Prozess
		} state Stopit3
	}	
	state Stopit3 {
		entry {
// 3. ND-Kompressor Bypass oeffnen	 		
			PV_SET( byp12sy320, AUF)
		}
		when (delay( 15.) && hdSchieber < 4.) {
		// a) Beruhigungszeit fuer den Prozess
		// b) HD-Schieber soll fast 0 sein vor Ausschalten
		} state Stopit4
		when (delay( 60.)) {
			snlMsgLog( "HD-Schieber schliesst nicht");
		} state Stopit4
	}	
	state Stopit4 {
		entry {
//Bypass-Ventile schliessen
/*1. CV108*/
			PV_SET_SYNC( CV108OMSL, SUPERVISORY)
	 		PV_SET( CV108, 0.)
/*2. CV109 (Turbine)*/
			PV_SET_SYNC( CV109OMSL, SUPERVISORY)
	 		PV_SET( CV109, 0.)
//Drehzahl-Regler auf 0
	 		PV_SET_SYNC( NC157AM, PID_MANUELL)
	 		PV_SET( NC157SOUT, 0.)
// koennte bei alleinigem HD-Betrieb auf supervisory stehen
	 		PV_SET( CV520OMSL, CLOSED_LOOP)
//HochdruckRegler auf 0% stellen
	 		PV_SET_SYNC( PC503AM, PID_MANUELL)
	 		PV_SET( PC503SOUT, 0.)
		}
		when( CV520auf) {
		} state kStop
		when (delay( 30.)) {
			snlMsgLog( "HD-Regler oeffnet nicht");
		} state kStop
	}	
	state kStop {
		option -e; /* fuehre entry jedes Mal aus */
		entry {
/*
;       + Alle Schieber sind auf MINIMUM und   +
;		+ Alle Kompressor Bypaesse sind OFFEN. +
;
; Jetzt koennen alle Kompressoren gestoppt werden.
; Und zwar in der Sequenz ND,dann HD.
; Wenn Redundanz zuordnungen,dann auch diese.
; Am Schluss werden noch die entsprechende ND/HD Bypass Ventile geschlossen:
*/
			PV_SET( K1BETRcmd, STOP)
			PV_SET( K2BETRcmd, STOP)
			PV_SET( K3BETRcmd, STOP)
			PV_SET( hdK1BETRcmd, STOP)
			}
			when( K1BETR != BETRIEB &&
				  K2BETR != BETRIEB &&
				  K3BETR != BETRIEB &&
				  hdK1BETR != BETRIEB) {
			} state Stopit5
			when( delay(2.)) {
			snlMsgLog( "Kompressoren stoppen nicht");
			} state kStop
		}	
	state Stopit5 {
		entry {
// Enddruckregler 32PC106 manuell und 50%
	 		PV_SET_SYNC( PC106AM, PID_MANUELL)
	 		PV_SET( PC106SOUT, 50.)
	
// Meldung machen	 		
			snlMsgLog( "Stopit fertig");
			PV_SET( SoftStopCmd, FALSE)
		}
		when (delay( 1.)) {
			efClear( softStopEnableFlag);
		} state NichtBereit
	}
}
