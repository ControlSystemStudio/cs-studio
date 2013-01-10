/*
	Version: $$Revision: 1.2 $$
	Letzte Aenderung von $$Name:  $$
	Letzte Aenderung von $$Author: hrickens $$
	am $$Date: 2009/10/13 14:49:52 $$
 
 	Verwendungszweck:  SICHERHEITS UEBERWACHUNG DEWARS 51			       
                                                                              
    Beschreibung:  Dieses Programm ueberwacht:                               
                                                                              
		DEWAR 51 fuer fluessges Helium auf Niveau ,Druck und Abschaltung der
		zuliefernden Coldbox.	       
                                                                              
        FALLS DAS FLUESSIGKEITS-NIVEAU UNTER 20% ABSINKT DER DRUCK UEBER 1.8
        BAR ANSTEIGT ODER DIE COLDBOX ABSTELLT WIRD, WIRD DIE HEIZUNG
        ABGESCHALTET. FALLS DER DRUCK UEBER 2.2 BAR STEIGT WERDEN ALLE VENTILE
		ZUM DEWAR GESCHLOSSEN.				       
                                                                              
    Gestartet von:  
    	DIESES PROGRAMM WIRD IM "PCMLOD" BEFEHL INTEGRIERT UND SOMIT NACH DEM 
    	AUFSTARTEN VON PCM2 ZUR VERFUEGUNG STEHEN. ES LAEUFT AUF UNIT VBX50A                                 
                                                                              
 Referenz Dokumentation:   Teil H , Abschnitt 850 , Register 4               
              Rev: 1                                                          
                                                                              
------------------------------------------------------------------------------
	Rev 1:	Im Fall ueberkritischen Drucks wird zu Beginn der Output       
		des 51PC541 auf 100 (zu) gesetzt, dann nur noch auf 60 (falls  
		er darunter liegt)					       
------------------------------------------------------------------------------
                                                                              
			VARIABLEN VON UNIT VBX50A			       
			=========================			       
                                                                              
 RPF's :	ADIOCM	--->	ADI fuer OCM				       
			ADIPNT  --->	ADI fuer Drucker			       
                                                                              
 UPF's :	PRNT51	--->	Flag fuer Meldungs Ausgabe		       
                                                                              
 EPN's :	51PI620 --->	Druck Dewar 51				       
			51E650	--->	Heizung Dewar 51 in %			       
			51HS650	--->	Schalter Heizung Dewar 51		       
                                                                              
------------------------------------------------------------------------------
                                                                              
			VARIABLEN VON UNIT VBOX50			       
			=========================			       
                                                                              
 RPF's :	keine							       
                                                                              
 UPF's :	keine							       
                                                                              
 EPN's :	51LI610 --->	Fuellstand Dewar 51			       
			51PC312	--->	Eintrittsdruckregler Dewar 51		       
			51PC541	--->	Druckregler Dewar 51			       
                                                                              
			51HC311	--->	Eintritt Subcooler Dewar51		       
			51HC312	--->	Austritt Subcooler Dewar51		       
			51HC314	--->	Austritt Subcooler Dewar51 zu MTH	       
			51HC318	--->	Dampf Entnahme Dewar 51			       
			51HC416	--->	Bypass HD-Ein zu ND-Ruecklauf Dewar51	       
			51HC514	--->	Austritt zu Bypassleitung Dewar51	       
			50HC110	--->	Fluessigkeits Entnahme Dewar51		       
                                                                              
------------------------------------------------------------------------------
                                                                              
			VARIABLEN VON UNIT INTERF			       
			=========================			       
                                                                              
 RPF's :	keine							       
                                                                              
 UPF's :	DEWAR51	--->	Integer Zuordnung zu Coldbox		       
			CTRP41	--->	Trip fuer Stoerung bei CBX 41		       
			CTRP42	--->	Trip fuer Stoerung bei CBX 42		       
			CTRP43	--->	Trip fuer Stoerung bei CBX 43		       
                                                                              
 EPN's :	keine							       
                                                                              
*/

program Duwar

#define OK 1
#define NOT_OK 0
#define SUPERVISORY 0
#define CLOSED_LOOP 1
#define AUF 1
#define ZU 0

#define PV_AUF(pv) pv = AUF; pvPut (pv);
#define PV_ZU(pv)  pv = ZU;  pvPut (pv);
#define PV_SET(pv, val) pv = val; pvPut (pv);
#define PV_SET_SYNC(pv, val) pv = val; pvPut (pv, SYNC);

#define DEBUG_DUWAR_AN TRUE
#define DBG (DEBUG_DUWAR_AN|debugNextStep)
#define DBG_MSG(msg) sprintf(debugText, msg); pvPut(debugText); snlMsgLog( "dbg:<%s>" ,debugMsgLog);

// DEBUG
short  debugNextState; 	assign debugNextState to "KS2:SNL:ADS:STEP_bi";
string debugText;		assign debugText	  to "KS2:SNL:ADS:STEP_si";

short DEWAR3;	assign DEWAR3	to ""; 
short CTRP41;   assign CTRP41   to "";
short CTRP42;   assign CTRP42   to "";
short CTRP43;   assign CTRP43   to "";
short PT;       assign PT       to "";
short PRNT53;   assign PRNT53   to "";
short HC311;    assign HC311    to "53HC311";
short HC312;    assign HC312    to "53HC312";
short PC312;    assign PC312    to "53PC312";
short HC314;    assign HC314    to "53HC311";
short E650;     assign E650     to "";
short HS650;    assign HS650    to "";
short PC312;    assign PC312    to "";
short HC311;    assign HC311    to "";
short HC314;    assign HC314	to "";
short L53; 		assign L53		to "53LI610";
short P53; 		assign P53		to "53PI610";	monitor P53;

ss DuwarBetrieb{

	state Init{
		entry{
			//TODO: UNIT VBX50A
			//TODO: COMMON VBOX50,INTERF
			//TODO:	L53 == '53LI610':AI_INVL
			//TODO: P53 == '53PI620':AI_INVL
			//TODO: HS53 == '53HS650':DEV_STAT
		when(DBG && TRUE){
		}state ColdBoxZuordnung
	}

	state ColdBoxZuordnung{ //TOP
		entry {
			if(DEBUG_DUWAR_AN){
				debugNextState = 0;		pvPut(debugNextState);
				DBG_MSG("ColdBoxZuordnung");
				pvMonitor(debugNextState);
			}
			pvMonitor(DEWAR3);
		}
		when(DBG && DEWAR3 == 1){
			pvAssign(PT3,"CTRP41");
		} state LBL1
		when(DBG && DEWAR3 == 2){
			pvAssign(PT3,"CTRP42");
		} state LBL1
		when(DBG && DEWAR3 == 3){
			pvAssign(PT3,"CTRP43");
		} state LBL1
		when(DBG && TRUE){
		} state LBL2
		exit{
			pvStopMonitor(DEWAR3);
		}
	}
	
	// Kontrolle ob die liefernde Coldbox ausgefallen ist, wenn ja:
	// Heizung aus!
	state LBL1{
		entry{
			if(DEBUG_DUWAR_AN){
				debugNextState = 0;		pvPut(debugNextState);
				DBG_MSG("LBL1");
			}
			pvGet(PT3);
			if(PT3 == 1){
				PV_SET(E650,0)
				PV_ZU(HS650)
				PV_SET(HC312,0)
				PV_SET(HC311,0)
				PV_SET(HC314,0)
				pvGet(PRNT53);
				if(PRNT53 = 0){
					snlMsgLog("<LBL1>DEWAR53:Heizung aus wegen CBX-STOP");
					PV_SET(PRNT53,1)
				}
			} else{
				PRNT53 = 0;				pvPut(PRNT53);
			}
		}
		when(DBG && TRUE){
		} state LBL2
	}
	
	state LBL2{
		entry{
			if(DEBUG_DUWAR_AN){
				debugNextState = 0;		pvPut(debugNextState);
				DBG_MSG("LBL2");
			}
		}
		when(DBG && (P53 < 2.2)){
			PV_SET(PRNT53,0)
		} LBL3
		when(DBG && TRUE){
		} LBL3
		exit{
			PV_ZU(HS650);
			PV_SET(PC541,100)
		}
	}
	
	state LBL3{
		entry{
			if(DEBUG_DUWAR_AN){
				debugNextState = 0;		pvPut(debugNextState);
				DBG_MSG("LBL3");
			}
			PV_SET(E650,0)
			PV_ZU(HS650)
	
			// TODO: PUTMANL '53PC312'
			PV_SET(PC312,0)
			if('53PC541':CB_OTVL:1 < 60.){
			// TODO: PUTMANL '53PC541'
				PV_SET(PC541,60.)
			}
			// TODO: PUTMANL '53HC311'
			PV_SET(HC311,100)
			// TODO: PUTMANL '53HC312'
			PV_SET(HC312,100)
			// TODO: PUTMANL '53HC314'
			PV_SET(HC314,0)

			// TODO: PUTMANL '53HC318'
			PV_SET(HC318,0)
			// TODO: PUTMANL '50HC310'
			PV_SET(HC310,0)
			// TODO: PUTMANL '53HC514'
			PV_SET(HC514,0)

			if(PRNT53 = 0) {
				snlMsgLog("<LBL3>DEWAR 53: DRUCK UEBERKRITISCH");
				// TODO: LET PRNT53 = 1
			}
		}
		when(DBG && P53 < 1.9){
			snlMsgLog("<LBL3>DEWAR 53: DRUCK WIEDER < 1.9 BAR");
		}state LBL4

		when(DBG && delay(30)){
		} state LBL3
	}
	
	state LBL4{
		entry{
			if(DEBUG_DUWAR_AN){
				debugNextState = 0;		pvPut(debugNextState);
				DBG_MSG("LBL4");
			}
		}
		
		when(DBG && (L53 > 20. & P53 < 1.8)){
		} state LBL6	
		when(DBG && TRUE){
			// TODO: LET PRNT53 = 0
		} state LBL5
	}
	
	state LBL5{
		entry{
			if(DEBUG_DUWAR_AN){
				debugNextState = 0;		pvPut(debugNextState);
				DBG_MSG("LBL5");
			}
			PV_ZU(HS650)
			// TODO: PUTMANL '53E650'
			PV_SET(E650,0)
		}
		when(DBG && (P53 > 2.2)){
		} state LBL2
		when(DBG && (PRNT53 = 0)){
			snlMsgLog("NOT-AUS HEIZUNG DEWAR 53");
			// TODO: LET PRNT53=1
		} state LBL5_2
		when(DBG && TRUE){
		} state LBL5_2
	}
	
	state LBL5_2{
		entry{
			if(DEBUG_DUWAR_AN){
				debugNextState = 0;		pvPut(debugNextState);
				DBG_MSG("LBL5_2");
			}
		}
		when(DBG && (P53 < 1.7 & L53 > 20.)| P53 > 2.2){
		} state LBL_3
		when(DBG && delay(30)){
		} state LBL5
	}
	
	state LBL5_3{
		entry{
			if(DEBUG_DUWAR_AN){
				debugNextState = 0;		pvPut(debugNextState);
				DBG_MSG("LBL5_3");
			}
		}
		when(DBG && (P53 > 2.2)){
		} state LBL2
		
		when(DBG && TRUE){
			snlMsgLog("<LBL5_2>DEWAR 53 WIEDER OK");
		} state LBL6
	}
	
	state LBL6{
			state LBL5_3{
		entry{
			if(DEBUG_DUWAR_AN){
				debugNextState = 0;		pvPut(debugNextState);
				DBG_MSG("LBL6");
			}
		}
		when(DBG && (AND(HS53,1)=1)){ //???
			// TODO: PUTMANL '53E650'
			PV_SET(E650,0.0)
		} state ColdBoxZuordnung 
		when(DBG && TRUE){
		} state ColdBoxZuordnung
	}
}
