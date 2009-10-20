/*
Datum: 25.09.2009
LAST UPDATE: $$Date: 2009/10/12 12:02:48 $$ 
BY : $$Author: tpaetz $$ 
Version: $$Revision: 1.57 $$                              
                                                                                            
Verwendungszweck:  KALTFAHREN,WECHSEL UND REGENERATION AD100/110/200/210                
                                                                                            
Beschreibung: Dieses Programm dient zur Regeneration der N2-Adsorber in CBX 41/43                                                                           
                                                                                            
Es wird geprueft ob ein Adsorber zur Regeneration angelmeldet ist.                                                                           
Kriterium: Adsorber-Betriebsstatus auf 1                                                   
AD1BTR fuer 4xAD100                                                                        
AD2BTR fuer 4xAD110                                                                        
AD3BTR fuer 4xAD200                                                                        
AD4BTR fuer 4xAD210                                                                        
                                                                                            
Anschliessend wird geprueft ob der parallele Adsorber einsatzbereit ist (Betriebsstatus = 0)                                                     
Sind beide Adsorber zur Regeneration angemeldet,so wird der im Prozess befindliche zuerst regeneriert.                                        
                                                                                            
Anschliessend wird der parallele Adsorber kaltgefahren. Nach dem Kaltfahren werden die Adsorber gewechselt.                                         
                                                                                            
Um Fehlschaltungen zu vermeiden wird dem zur Regeneration angemeldeten und aus dem Prozess genommenen Adsorber der Status = 2 zugewiesen.                                                        
Wird im Programm ein Adsorber im Status =2 erkannt wird kein Wechsel vorgenommen und sofort mit der Regeneration begonnen.                                                                             
                                                                                            
Zu Beginn der Regeneration wird der Adsorber in die ND-??? bis sein Druck < 1.2 bar a betraegt.                                                       
Dann wird der Geblaese-Kreis und der Abblas geoeffnet. Nachdem eine Adsorbertemperatur von 250 K erreicht ist,                                     
wird der Adsorber evakuiert. Dabei wird der Operator gebeten die Vorvakuum Leitung zu checken (HV800 und                                        
ev. Rootspumpstaende). Ist nach jeweils 10 Min. ein Vakuum von 0.1 bar a nicht erreicht wird diese Meldung                                     
wiederholt um die Operateure aufmerksam zu machen. Ist nach ca 1/2 Std. ein Vakuum von 0.05 bar a nicht                                       
erreicht wird angenommen das der Adsorber "feucht" ist. (Es gibt selbstverstaendlich noch andere Ursachen.)                                         
In diesem Fall kann der Operateur die Regeneration wiederholen lassen (Fluten Entlasten Aufheizen Evakuieren) oder abbrechen.                                                                     
                                                                                            
Nach beendetem Evakuiervorgang wird der Adsorber auf ueber 5 Bar a gefuellt die Regeneration ist beendet.                                      
Der Adsorberstatus wird auf =0 gesetzt -> regeneriert.                                      
                                                                                            
Die Einzelnen Programmabschnitte sind als Subroutinen ausgefuehrt, die nacheinander ausgefuehrt werden.                                          
Eine Aufteilung in mehrere Programme wurde vermieden, um gleichzeitige Regenerationsversuche fuer mehr als einen Adsorber
auf jeden Fall zu verhindern.                                                     
                                                                                            
Gestartet von:  Das Programm kann auf Zwei Arten gestartet werden:                         
1: Durch das Programm CUEBERW1 wenn bei der N2 Analse ein N2 Gehalt von ueber 5 ppm festgestellt wird                                         
2: Ueber Die Skid Tasten 20 bzw. 22 um dem Oprateur unabhaengig von der Analyse eine Moeglichkeit zur                                       
   Regeneration zu geben. -> Programm ADSTART11
*/ 
                                                                               
program Adsorber

#define OK 1
#define NOT_OK 0
#define SUPERVISORY 0
#define CLOSED_LOOP 1
#define AUF 1
#define ZU 0
#define EIN 1
#define AUS 0
#define START 1
#define STOP 2
// Adsorber zustände
#define BETRIEBSBEREIT 0
#define VERSCHMUTZT 1
#define REGENERIERT 2
// Debug
#define DEBUG_ADS_EIN FALSE			//TRUE=aktiviert   FALSE=deaktiviert
#define DEBUG_ADSSTOP_EIN FALSE		//TRUE=aktiviert   FALSE=deaktiviert

%%#include <string.h>
%%#include <alarm.h>
%%#include <snlMsgLog.h>


// DEBUG
 short  debugNextState;	assign debugNextState to "KS2:SNL:ADS:STEP_bi";
 string debugText;		assign debugText	  to "KS2:SNL:ADS:STEP_si";
  
 evflag adsStop;
 
 // Zwischenspeicher
 string ovalZwischenSpeicher1;                                                               
 string ovalZwischenSpeicher2;                                                               
                                                                                           
 //AD_OPERATOR_ANT_JA ----> Integer Antwort auf Query
 short	AD_OPERATOR_ANT_JA; 		assign AD_OPERATOR_ANT_JA  			to "KS2:SNL:ADS:REG_JA_bi";
 string AD_OPERATOR_ANT_JA_TEXT;	assign AD_OPERATOR_ANT_JA_TEXT 		to "KS2:SNL:ADS:REG_JA_si";
 short	AD_OPERATOR_ANT_NEIN; 		assign AD_OPERATOR_ANT_NEIN  		to "KS2:SNL:ADS:REG_NEIN_bi";
 string AD_OPERATOR_ANT_NEIN_TEXT;	assign AD_OPERATOR_ANT_NEIN_TEXT 	to "KS2:SNL:ADS:REG_NEIN_si";
 // Start /Stop
 short 	AD_OPERATOR_START; 		assign AD_OPERATOR_START			to "KS2:SNL:ADS:START_bi"; monitor AD_OPERATOR_START;
 string AD_OPERATOR_START_TEXT;	assign AD_OPERATOR_START_TEXT 		to "KS2:SNL:ADS:START_si";
 short 	AD_OPERATOR_STOP; 		assign AD_OPERATOR_STOP				to "KS2:SNL:ADS:STOP_bi";  monitor AD_OPERATOR_STOP;
 string AD_OPERATOR_STOP_TEXT;	assign AD_OPERATOR_STOP_TEXT 		to "KS2:SNL:ADS:STOP_si";
 
 
 //AD1BTR ----> Integer Betriebsstatus 42AD100
 short AD1BTR; 	assign AD1BTR to "KS2:SNL:ADS:AD1BETR_mbbi";
                                                  
 //AD2BTR ----> Integer Betriebsstatus 42AD110                                                 
 short AD2BTR; 	assign AD2BTR to "KS2:SNL:ADS:AD2BETR_mbbi";
                  
 //AD3BTR ----> Integer Betriebsstatus 42AD200                                                 
 short AD3BTR; 	assign AD3BTR to "KS2:SNL:ADS:AD3BETR_mbbi";
                  
 //AD4BTR ----> Integer Betriebsstatus 42AD210                                                 
 short AD4BTR; 	assign AD4BTR to "KS2:SNL:ADS:AD4BETR_mbbi";                 
                                      
// 41HC111 ----> Eintrittsventil AD100                                                         
 short  CV111auf; 	assign CV111auf  to "42CV111_dlog.SB2";
 short  CV111zu; 	assign CV111zu 	 to "42CV111_dlog.SB0";
 short  CV111OMSL; 	assign CV111OMSL to "42CV111_ao.OMSL";
 string CV111SCAN; 	assign CV111SCAN to "42CV111_ao.SCAN";
 double CV111OROC; 	assign CV111OROC to "42CV111_ao.OROC";
 double CV111OVAL; 	assign CV111OVAL to "42CV111_ao.OVAL";
 double CV111; 		assign CV111 	 to "42CV111_ao";
 
// 41HC112 ----> Austrittsventil AD100                                                         
 short  CV112auf; 	assign CV112auf  to "42CV112_dlog.SB2";
 short  CV112zu; 	assign CV112zu 	 to "42CV112_dlog.SB0";
 short  CV112OMSL; 	assign CV112OMSL to "42CV112_ao.OMSL";
 string CV112SCAN; 	assign CV112SCAN to "42CV112_ao.SCAN";
 double CV112OROC; 	assign CV112OROC to "42CV112_ao.OROC";
 double CV112OVAL; 	assign CV112OVAL to "42CV112_ao.OVAL";
 double CV112; 		assign CV112 	 to "42CV112_ao";
 
 // 41HC113 ----> Eintrittsventil AD110
 short  CV113auf; 	assign CV113auf  to "42CV113_dlog.SB2";
 short  CV113zu; 	assign CV113zu 	 to "42CV113_dlog.SB0";
 short  CV113OMSL; 	assign CV113OMSL to "42CV113_ao.OMSL";
 string CV113SCAN; 	assign CV113SCAN to "42CV113_ao.SCAN";
 double CV113OROC; 	assign CV113OROC to "42CV113_ao.OROC";
 double CV113OVAL; 	assign CV113OVAL to "42CV113_ao.OVAL";
 double CV113; 		assign CV113 	 to "42CV113_ao";
 
 // 41HC114 ----> Austrittsventil AD110                                                         
 short  CV114auf; 	assign CV114auf  to "42CV114_dlog.SB2";
 short  CV114zu; 	assign CV114zu 	 to "42CV114_dlog.SB0";
 short  CV114OMSL; 	assign CV114OMSL to "42CV114_ao.OMSL";
 string CV114SCAN; 	assign CV114SCAN to "42CV114_ao.SCAN";
 double CV114OROC; 	assign CV114OROC to "42CV114_ao.OROC";
 double CV114OVAL; 	assign CV114OVAL to "42CV114_ao.OVAL";
 double CV114; 		assign CV114 	 to "42CV114_ao";
 
 // 41HC201 ----> Eintrittsventil AD200
 short  CV201auf; 	assign CV201auf  to "42CV201_dlog.SB2";
 short  CV201zu; 	assign CV201zu 	 to "42CV201_dlog.SB0";
 short  CV201OMSL; 	assign CV201OMSL to "42CV201_ao.OMSL";
 string CV201SCAN; 	assign CV201SCAN to "42CV201_ao.SCAN";
 double CV201OROC; 	assign CV201OROC to "42CV201_ao.OROC";
 double CV201OVAL; 	assign CV201OVAL to "42CV201_ao.OVAL";
 double CV201; 		assign CV201 	 to "42CV201_ao";
 
 
// 41HC202 ----> Austrittsventil AD200                        
 short  CV202auf; 	assign CV202auf  to "42CV202_dlog.SB2";
 short  CV202zu; 	assign CV202zu 	 to "42CV202_dlog.SB0";
 short  CV202OMSL; 	assign CV202OMSL to "42CV202_ao.OMSL";
 string CV202SCAN; 	assign CV202SCAN to "42CV202_ao.SCAN";
 double CV202OROC; 	assign CV202OROC to "42CV202_ao.OROC";
 double CV202OVAL; 	assign CV202OVAL to "42CV202_ao.OVAL";
 double CV202; 		assign CV202 	 to "42CV202_ao";
 
 
// 41HC203 ----> Eintrittsventil AD210                        
 short  CV203auf; 	assign CV203auf  to "42CV203_dlog.SB2";
 short  CV203zu; 	assign CV203zu 	 to "42CV203_dlog.SB0";
 short  CV203OMSL; 	assign CV203OMSL to "42CV203_ao.OMSL";
 string CV203SCAN; 	assign CV203SCAN to "42CV203_ao.SCAN";
 double CV203OROC; 	assign CV203OROC to "42CV203_ao.OROC";
 double CV203OVAL; 	assign CV203OVAL to "42CV203_ao.OVAL";
 double CV203; 		assign CV203 	 to "42CV203_ao";
 

// 41HC204 ----> Austrittsventil AD210
 short  CV204auf; 	assign CV204auf  to "42CV204_dlog.SB2";
 short  CV204zu; 	assign CV204zu 	 to "42CV204_dlog.SB0";
 short  CV204OMSL; 	assign CV204OMSL to "42CV204_ao.OMSL";
 string CV204SCAN; 	assign CV204SCAN to "42CV204_ao.SCAN";
 double CV204OROC; 	assign CV204OROC to "42CV204_ao.OROC";
 double CV204OVAL; 	assign CV204OVAL to "42CV204_ao.OVAL";
 double CV204; 		assign CV204 	 to "42CV204_ao";
 
 
// 41PI201 ----> Druck AD100
 double PI111;	assign PI111	to "42PI111_ai";

// 41TI110 ----> Temperatur AD100
 double TI110;	assign TI110	to "42TI110_ai";                                                              

// 41PI113 ----> Druck AD110
 double PI113;	assign PI113	to "42PI113_ai";                                                                   

// 41TI112 ----> Temperatur AD110                                                              
 double TI112;	assign TI112	to "42TI112_ai";
                                                               
// 41PI201 ----> Druck AD200   
 double PI201;	assign PI201	to "42PI201_ai";                                                                   
                                                                
// 41TI200 ----> Temperatur AD200                                                              
 double TI200;	assign TI200	to "42TI200_ai";
 
// 41PI203 ----> Druck AD210                                                                   
 double PI203;	assign PI203	to "42PI203_ai";                                                                   

// 41TI202 ----> Temperatur AD210                                                              
 double TI202;	assign TI202	to "42TI202_ai";

// 41HS130 ----> Einschaltbefehl Heizung R125
 short HS130CMD; 	assign HS130CMD 	to "42HS130_dlog.CMD";
 short HS130ein; 	assign HS130ein 	to "42HS130_dlog.SB1";
                                                    
// 41HS151 ----> Einschaltbefehl Geblaese K150                                                 
 short HS151CMD; 	assign HS151CMD		to "42HS151_dlog.CMD";
 short HS151ein; 	assign HS151ein		to "42HS151_dlog.SB1";
 short HS151aus; 	assign HS151aus		to "42HS151_dlog.SB0";
 short HS151STOER;  assign HS151STOER 	to "42HS151_dlog.SB3";
 
                                                                                            
 // 41UZV172 ---> Verbindung Regeneration 100/110 - ND
 short UZV172CMD;	assign UZV172CMD    to "42UZV172_dlog.CMD";
 short UZV172auf; 	assign UZV172auf    to "42UZV172_dlog.SB2";
 short UZV172zu; 	assign UZV172zu     to "42UZV172_dlog.SB0";
 
 // 41UZV173 ---> Ansaug Geblaese Kreis 100/110
 short UZV173CMD; 	assign UZV173CMD	to "42UZV173_dlog.CMD";
 short UZV173auf; 	assign UZV173auf	to "42UZV173_dlog.SB2";
 short UZV173zu;  	assign UZV173zu		to "42UZV173_dlog.SB0";
 
 // 41UZV174 --->
 short UZV174CMD;   assign UZV174CMD 	to "42UZV174_dlog.CMD";
 short UZV174auf;   assign UZV174auf    to "42UZV174_dlog.SB2";
 short UZV174zu;    assign UZV174zu     to "42UZV174_dlog.SB0";
 
 // 41UZV182 ---> Ausblas Geblaese AD100                                                        
 short UZV182CMD;   assign UZV182CMD	to "42UZV182_dlog.CMD";
 short UZV182auf;   assign UZV182auf    to "42UZV182_dlog.SB2";
 short UZV182zu;    assign UZV182zu     to "42UZV182_dlog.SB0";

 // 41UZV183 ---> Ausblas Geblaese AD110
 short UZV183CMD;   assign UZV183CMD 	to "42UZV183_dlog.CMD";
 short UZV183auf;   assign UZV183auf  	to "42UZV183_dlog.SB2";
 short UZV183zu;    assign UZV183zu   	to "42UZV183_dlog.SB0";

 // 41UZV184 ---> Verbindung Regeneration 100/110 - Vakuum                                    
 short UZV184CMD;   assign UZV184CMD	to "42UZV184_dlog.CMD";
 short UZV184auf;   assign UZV184auf 	to "42UZV184_dlog.SB2";
 short UZV184zu;    assign UZV184zu  	to "42UZV184_dlog.SB0";
                                                          
 // 41UZV192 ---> Ansaug Geblaese AD100
 short UZV192CMD;   assign UZV192CMD	to "42UZV192_dlog.CMD";
 short UZV192auf;   assign UZV192auf 	to "42UZV192_dlog.SB2";
 short UZV192zu;    assign UZV192zu  	to "42UZV192_dlog.SB0";
 
 // 41UZV193 ---> Ansaug Geblaese AD110
 short UZV193CMD; 	assign UZV193CMD    to "42UZV193_dlog.CMD";
 short UZV193auf; 	assign UZV193auf    to "42UZV193_dlog.SB2";
 short UZV193zu; 	assign UZV193zu     to "42UZV193_dlog.SB0";
 
 // 41UZV194 ---> Verbindung Regeneration 100/110 - HD                                          
 short UZV194CMD;	assign UZV194CMD    to "42UZV194_dlog.CMD";
 short UZV194auf; 	assign UZV194auf    to "42UZV194_dlog.SB2";
 short UZV194zu; 	assign UZV194zu     to "42UZV194_dlog.SB0";
                                                        
 // 41UZV272 ---> Verbindung Regeneration 200/210 - ND
 short UZV272CMD;	assign UZV272CMD 	to "42UZV272_dlog.CMD";
 short UZV272auf; 	assign UZV272auf    to "42UZV272_dlog.SB2";
 short UZV272zu; 	assign UZV272zu     to "42UZV272_dlog.SB0";
 
                                           
 // 41UZV273 ---> Ansaug Geblaese Kreis 200/210                                                 
 short UZV273CMD;	assign UZV273CMD 	to "42UZV273_dlog.CMD";
 short UZV273auf; 	assign UZV273auf    to "42UZV273_dlog.SB2";
 short UZV273zu; 	assign UZV273zu     to "42UZV273_dlog.SB0";
 
 // 41UZV274 --->
 short UZV274CMD;	assign UZV274CMD 	to "42UZV274_dlog.CMD";
 short UZV274auf; 	assign UZV274auf    to "42UZV274_dlog.SB2";
 short UZV274zu; 	assign UZV274zu     to "42UZV274_dlog.SB0";
 
 // 41UZV282 ---> Ausblas Geblaese AD200                                                        
 short UZV282CMD;	assign UZV282CMD 	to "42UZV282_dlog.CMD";
 short UZV282auf; 	assign UZV282auf    to "42UZV282_dlog.SB2";
 short UZV282zu; 	assign UZV282zu     to "42UZV282_dlog.SB0";
 
 // 41UZV283 ---> Ausblas Geblaese AD210                                                        
 short UZV283CMD;	assign UZV283CMD 	to "42UZV283_dlog.CMD";
 short UZV283auf; 	assign UZV283auf    to "42UZV283_dlog.SB2";
 short UZV283zu; 	assign UZV283zu     to "42UZV283_dlog.SB0";
 
 // 41UZV284 --->   Verbindung Regeneration 200/210 - Vakuum                                    
 short UZV284CMD;	assign UZV284CMD 	to "42UZV284_dlog.CMD";
 short UZV284auf; 	assign UZV284auf    to "42UZV284_dlog.SB2";
 short UZV284zu; 	assign UZV284zu     to "42UZV284_dlog.SB0";
  
 // 41UZV292 ---> Ansaug Geblaese AD200                                                         
 short UZV292CMD;	assign UZV292CMD 	to "42UZV292_dlog.CMD";
 short UZV292auf; 	assign UZV292auf    to "42UZV292_dlog.SB2";
 short UZV292zu; 	assign UZV292zu     to "42UZV292_dlog.SB0";
 
 // 41UZV293 ---> Ansaug Geblaese AD210                                                         
 short UZV293CMD;	assign UZV293CMD 	to "42UZV293_dlog.CMD";
 short UZV293auf; 	assign UZV293auf    to "42UZV293_dlog.SB2";
 short UZV293zu; 	assign UZV293zu     to "42UZV293_dlog.SB0";
 
 
 // 41UZV294 ---> Verbindung Regeneration 200/210 - HD                                          
 short UZV294CMD;	assign UZV294CMD 	to "42UZV294_dlog.CMD";
 short UZV294auf; 	assign UZV294auf    to "42UZV294_dlog.SB2";
 short UZV294zu; 	assign UZV294zu     to "42UZV294_dlog.SB0";

ss Adsorber{

	// Welcher Adsorber ist zur Regeneration angemeldet ?                                          
	// AD 200/210 haben Prioritaet vor AD100/110                                                    
	state ueberwachung{
		entry{
			if(DEBUG_ADS_EIN){
				debugNextState = 0; 				pvPut(debugNextState);
				sprintf(debugText, "ueberwachung");	pvPut(debugText);
				pvMonitor(debugNextState);
				snlMsgLog("dbg:<ueberwachung>");
			}
			pvMonitor(AD3BTR);
			pvMonitor(AD4BTR);
			pvMonitor(AD1BTR);
			pvMonitor(AD2BTR);
			pvMonitor(CV201OVAL);
			pvMonitor(CV202OVAL);
			pvMonitor(CV203OVAL);
			pvMonitor(CV204OVAL);
			pvMonitor(CV111OVAL);
			pvMonitor(CV112OVAL);
			pvMonitor(CV113OVAL);
			pvMonitor(CV114OVAL);
			
		}

		// Old LABL3
		when((!DEBUG_ADS_EIN || debugNextState!=0) && !efTest(adsStop) && AD3BTR==1 && AD4BTR != 0 && CV201zu!=0 && CV201OVAL < 1 && CV202zu!=0 && CV202OVAL < 1){
 		}state CHNG4                                      

 		when((!DEBUG_ADS_EIN || debugNextState!=0) && !efTest(adsStop) && AD3BTR==1){
 		} state CHNG3
	
		// IF (AD3BTR=2) THEN GOSUB REG200
		when((!DEBUG_ADS_EIN || debugNextState!=0) && !efTest(adsStop) && AD3BTR==2){
		} state REG200       
		                                                       
		// old LABL4
		when((!DEBUG_ADS_EIN || debugNextState!=0) && !efTest(adsStop) && AD4BTR==1 && AD3BTR != 0 && CV203zu!=0 && CV203OVAL < 1 && CV204zu!=0 && CV204OVAL < 1){
 		}state CHNG3                                      

 		when((!DEBUG_ADS_EIN || debugNextState!=0) && !efTest(adsStop) && AD4BTR==1){
 		} state CHNG4

		// IF (AD4BTR=2) THEN GOSUB REG210
		when((!DEBUG_ADS_EIN || debugNextState!=0) && !efTest(adsStop) && AD4BTR==2){
		}state REG210

		// old LABL 1		                                                              
		when((!DEBUG_ADS_EIN || debugNextState!=0) && !efTest(adsStop) && AD1BTR==1 && AD2BTR != 0 && CV111zu!=0 && CV111OVAL < 1 && CV112zu!=0 && CV112OVAL < 1){
		}state CHNG2
		
		when((!DEBUG_ADS_EIN || debugNextState!=0) && !efTest(adsStop) && AD1BTR==1){ 
		}state CHNG1
		                                                               
		// IF (AD1BTR=2) THEN GOSUB REG100
		when((!DEBUG_ADS_EIN || debugNextState!=0) && !efTest(adsStop) && AD1BTR==2){
		}state REG100
		          
		// old LABL2
		when((!DEBUG_ADS_EIN || debugNextState!=0) && !efTest(adsStop) && AD2BTR==1 && AD1BTR != 0 && CV113zu!=0 && CV113OVAL < 1 && CV114zu!=0 && CV114OVAL < 1){
	 	}state CHNG1                                      

		when((!DEBUG_ADS_EIN || debugNextState!=0) && !efTest(adsStop) && AD2BTR==1){
		} state CHNG2
		                                                               
		// IF (AD2BTR=2) THEN GOSUB REG110
		when((!DEBUG_ADS_EIN || debugNextState!=0) && !efTest(adsStop) && AD2BTR==2){
		}state REG110    
		
		exit{
			pvStopMonitor(AD3BTR);
			pvStopMonitor(AD4BTR);
			pvStopMonitor(AD1BTR);
			pvStopMonitor(AD2BTR);
			pvStopMonitor(CV201OVAL);
			pvStopMonitor(CV202OVAL);
			pvStopMonitor(CV203OVAL);
			pvStopMonitor(CV204OVAL);
			pvStopMonitor(CV111OVAL);
			pvStopMonitor(CV112OVAL);
			pvStopMonitor(CV113OVAL);
			pvStopMonitor(CV114OVAL);
		} 
	}


// ---------------------------- Wechsel AD110 fuer AD100 -------------------------------------------------------------------------------
	// CHNG1:                                                                                        
 	// Wechsel AD110 fuer AD100                                                                 
	// Erst alle Regenerationsventile schliessen ( definierter Zustand)                            
	// Kaltfahren AD110                                                                            
	state CHNG1{
		entry{
			if(DEBUG_ADS_EIN){ 
				debugNextState = 0; 	pvPut(debugNextState);
				sprintf(debugText, "CHNG1");	pvPut(debugText);
				snlMsgLog("dbg:<CHNG1>");
			}
			// Erst alle Regenerationsventile schliessen ( definierter Zustand)                            
	 		UZV174CMD = ZU;	 		pvPut(UZV174CMD);                                                                             
	 		UZV184CMD = ZU;	 		pvPut(UZV184CMD);                                                                             
			UZV172CMD = ZU;			pvPut(UZV172CMD);                                                                             
			UZV194CMD = ZU;			pvPut(UZV194CMD);                                                                             
			UZV193CMD = ZU;			pvPut(UZV193CMD);                                                                             
			UZV192CMD = ZU;			pvPut(UZV192CMD);                                                                             
			UZV173CMD = ZU;			pvPut(UZV173CMD);                                                                             
			UZV273CMD = ZU;			pvPut(UZV273CMD);                                                                             
			UZV183CMD = ZU;			pvPut(UZV183CMD);                                                                             
			UZV182CMD = ZU;			pvPut(UZV182CMD);                                                                             
			UZV283CMD = ZU;			pvPut(UZV283CMD);                                                                             
			UZV282CMD = ZU;			pvPut(UZV282CMD);
			
			// Kaltfahren AD110                                                                            
	 		// IF ('41HC114':CB_OTVL:1 < 10.0) THEN                                                         
	 		pvGet(CV114OVAL);
	 		if(CV114OVAL < 10.0){
		 		// PUTMANL '41HC114'
		 		// PUTOUT '41HC114',10          
		 		pvGet(CV114SCAN,SYNC);	 		strcpy(ovalZwischenSpeicher1,CV114SCAN); 
		 		CV114OMSL = SUPERVISORY;		pvPut(CV114OMSL, SYNC);
		 		CV114OROC = 1.0;				pvPut(CV114OROC, SYNC);
		 		CV114 = 10;						pvPut(CV114, SYNC);
		 		sprintf(CV114SCAN, "1 second");	pvPut(CV114SCAN, SYNC);
			}	 		
			pvMonitor(PI113);
			pvMonitor(PI111);
		}
		when (efTest(adsStop)) {
		}state ueberwachung
	 		// Druck angleichen                                                                            
	 		// WAITUNTIL('41PI113':AI_INVL > '41PI111':AI_INVL-1.5 )
		when((!DEBUG_ADS_EIN || debugNextState!=0) && PI113 > (PI111-1.5)){
			// PUTMANL nicht vergessen?
			// PUTOUT '41HC114',100
		 	pvGet(CV114SCAN,SYNC);	 		strcpy(ovalZwischenSpeicher1,CV114SCAN); 
	 		CV114OMSL = SUPERVISORY;		pvPut(CV114OMSL, SYNC);
	 		CV114OROC = 1.0;				pvPut(CV114OROC, SYNC);
	 		CV114 = 100;					pvPut(CV114, SYNC);
	 		strcpy(CV114SCAN, "1 second");	pvPut(CV114SCAN, SYNC);
		}state CHNG1_HEIZUNG_EINSCHALTEN
		
		when((!DEBUG_ADS_EIN || debugNextState!=0) && delay(60)){
			snlMsgLog("<CHNG1>Druckangleich PI113 / PI111 nicht ok!");
		}state CHNG1
		exit{
			pvStopMonitor(PI113);
			pvStopMonitor(PI111);
			strcpy(CV114SCAN, ovalZwischenSpeicher1);	pvPut(CV114SCAN, SYNC);
		}
	}
	
	state CHNG1_HEIZUNG_EINSCHALTEN{
		entry{
			if(DEBUG_ADS_EIN){ 
				debugNextState = 0; 								pvPut(debugNextState);
				sprintf(debugText, "CHNG1_HEIZUNG_EINSCHALTEN");	pvPut(debugText);
				snlMsgLog("dbg:<CHNG1_HEIZUNG_EINSCHALTEN>");
			}
	 		// Heizung eingeschalten                                                                      
		 	// OPEN '41HS130'         
		 	HS130CMD = EIN;	 		pvPut(HS130CMD);
		 	pvMonitor(HS130ein);
		}
		when (efTest(adsStop)) {
		}state ueberwachung
		//AND('41HS130':DEV_STAT,2)=2
		when((!DEBUG_ADS_EIN || debugNextState!=0) && HS130ein!=0){
		}state CHNG1_UZV193_OEFFNEN
		when((!DEBUG_ADS_EIN || debugNextState!=0) && delay(60)){
			snlMsgLog("<CHNG1_HEIZUNG_EINSCHALTEN>HS130 - keine Rückmeldung");
		}state CHNG1_HEIZUNG_EINSCHALTEN
		
		exit{	
		 	pvStopMonitor(HS130ein);
	 	}
	}
	
	state CHNG1_UZV193_OEFFNEN{
		entry{
			if(DEBUG_ADS_EIN){ 
				debugNextState = 0; 						pvPut(debugNextState);
				sprintf(debugText, "CHNG1_UZV193_OEFFNEN");	pvPut(debugText);
				snlMsgLog("dbg:<CHNG1_UZV193_OEFFNEN>");
			}
			//OPEN '41UZV193'  
		 	UZV193CMD = AUF;	 		pvPut(UZV193CMD);
		 	pvMonitor(UZV193auf);
		}
		when (efTest(adsStop)) {
		}state ueberwachung
		//AND('41UZV193':DEV_STAT,2)=2
		when((!DEBUG_ADS_EIN || debugNextState!=0) && UZV193auf!=0){
		}state CHNG1_UZV172_OEFFNEN
		when((!DEBUG_ADS_EIN || debugNextState!=0) && delay(60)){
			snlMsgLog("<CHNG1_UZV193_OEFFNEN>UZV193 - öffnet nicht!");
		}state CHNG1_UZV193_OEFFNEN
		
		exit{
		 	pvStopMonitor(UZV193auf);
	 	}
	}
	
	state CHNG1_UZV172_OEFFNEN{
		entry{
			if(DEBUG_ADS_EIN){ 
				debugNextState = 0; 						pvPut(debugNextState);
				sprintf(debugText, "CHNG1_UZV172_OEFFNEN");	pvPut(debugText);
				snlMsgLog("dbg:<CHNG1_UZV172_OEFFNEN>");
			}
			// OPEN '41UZV172'
		 	UZV172CMD = AUF;	 		pvPut(UZV172CMD);
		 	pvMonitor(UZV172auf);
		}
		when (efTest(adsStop)) {
		}state ueberwachung
		//AND('41UZV172':DEV_STAT,2)=2
		when((!DEBUG_ADS_EIN || debugNextState!=0) && UZV172auf!=0){
		}state CHNG1_AD110_KALTFAHREN
		when((!DEBUG_ADS_EIN || debugNextState!=0) && delay(60)){
			snlMsgLog("<CHNG1_UZV172_OEFFNEN>UZV172 - öffnet nicht!");
		}state CHNG1_UZV172_OEFFNEN
		
		exit{
		 	pvStopMonitor(UZV172auf);
	 	}
	}
	
	state CHNG1_AD110_KALTFAHREN{
		entry{
			if(DEBUG_ADS_EIN){ 
				debugNextState = 0; 							pvPut(debugNextState);
				sprintf(debugText, "CHNG1_AD110_KALTFAHREN");	pvPut(debugText);
				snlMsgLog("dbg:<CHNG1_AD110_KALTFAHREN>");
			}
			pvMonitor(TI112);
			pvMonitor(TI110);
		}
		when (efTest(adsStop)) {
		}state ueberwachung
		//'41TI112':AI_INVL < '41TI110':AI_INVL+5.0
		// Verbesserung: Nicht nur mit TI110 Vergleich könnte auch TI106 sein wenn CV100 offen.
		// Für die andern entsprechend auch ändern. 
		when((!DEBUG_ADS_EIN || debugNextState!=0) && TI112 < (TI110+5.0)){
		}state CHNG1_UZV193_SCHLIESSEN
		when((!DEBUG_ADS_EIN || debugNextState!=0) && delay(60)){
			snlMsgLog("<CHNG1_AD110_KALTFAHREN>Kaltfahren nicht erfolgreich! TI112 zu hoch.");
		} state CHNG1_AD110_KALTFAHREN
		exit{
			pvStopMonitor(TI112);
			pvStopMonitor(TI110);
		}
	}          
	
	state CHNG1_UZV193_SCHLIESSEN{
		entry{
			if(DEBUG_ADS_EIN){ 
				debugNextState = 0; 							pvPut(debugNextState);
				sprintf(debugText, "CHNG1_UZV193_SCHLIESSEN");	pvPut(debugText);
				snlMsgLog("dbg:<CHNG1_UZV193_SCHLIESSEN>");
			}
		 	// Nun ist AD110 kalt                                                                          
			// CLOSE '41UZV193'
			UZV193CMD = ZU;	 		pvPut(UZV193CMD);
		 	pvMonitor(UZV193zu);
		}
		
		when (efTest(adsStop)) {
		}state ueberwachung
		//(AND('41UZV193':DEV_STAT,1)=1
		when((!DEBUG_ADS_EIN || debugNextState!=0) && UZV193zu!=0){
		} state CHNG1_UZV172_SCHLIESSEN
		when((!DEBUG_ADS_EIN || debugNextState!=0) && delay(60)){
			snlMsgLog("<CHNG1_UZV193_SCHLIESSEN>UZV193 - schliesst nicht!");
		}state CHNG1_UZV193_SCHLIESSEN
		exit{
		 	pvStopMonitor(UZV193zu);
	 	}
	} 
	
	state CHNG1_UZV172_SCHLIESSEN{
		entry{
			if(DEBUG_ADS_EIN){ 
				debugNextState = 0; 							pvPut(debugNextState);
				sprintf(debugText, "CHNG1_UZV172_SCHLIESSEN");	pvPut(debugText);
				snlMsgLog("dbg:<CHNG1_UZV172_SCHLIESSEN>");
			}
			// CLOSE '41UZV172'                                                                             
			UZV172CMD = ZU;	 		pvPut(UZV172CMD);
		 	pvMonitor(UZV172zu);
		}
		when (efTest(adsStop)) {
		}state ueberwachung
		//AND('41UZV172':DEV_STAT,1)=1
		when((!DEBUG_ADS_EIN || debugNextState!=0) && UZV172zu!=0){
		} state FLA11
		when((!DEBUG_ADS_EIN || debugNextState!=0) && delay(60)){
			snlMsgLog("<CHNG1_UZV172_SCHLIESSEN>UZV172 - schliesst nicht!");
		}state CHNG1_UZV172_SCHLIESSEN
		exit{
		 	pvStopMonitor(UZV172zu);
	 	}
	}
	
	/*
	FLA11:                                                                                        
 		PUTMANL '41HC114'                                                                            
 		PUTOUT '41HC114',100                                                                         
 		PUTMANL '41HC113'                                                                            
 		PUTOUT '41HC113',100                                                                         
 		WAITUNTIL(AND('41CV113EN':DEV_STAT,2)=2)COUNT 120,MSG11                                      
 		WAITUNTIL(AND('41CV114EN':DEV_STAT,2)=2)COUNT 120,MSG11                                      
	*/
	state FLA11{
		option -e; /* führt entry immer wieder aus */
		entry{
			if(DEBUG_ADS_EIN){ 
				debugNextState = 0; 			pvPut(debugNextState);
				sprintf(debugText, "FLA11");	pvPut(debugText);
				snlMsgLog("dbg:<FLA11>");
			}
			//PUTMANL '41HC114'                                                                            
 			//PUTOUT '41HC114',100
			pvGet(CV114SCAN,SYNC);	 		strcpy(ovalZwischenSpeicher1,CV114SCAN); 
 			CV114OMSL = SUPERVISORY;		pvPut(CV114OMSL, SYNC);
	 		CV114OROC = 1.0;				pvPut(CV114OROC, SYNC);
	 		CV114 = 100;					pvPut(CV114, SYNC);
	 		sprintf(CV114SCAN, "1 second");	pvPut(CV114SCAN, SYNC);
 			                                                                         
 			//PUTMANL '41HC113'                                                                            
 			//PUTOUT '41HC113',100
			pvGet(CV113SCAN,SYNC);	 		strcpy(ovalZwischenSpeicher2,CV113SCAN); 
 			CV113OMSL = SUPERVISORY;		pvPut(CV113OMSL, SYNC);
	 		CV113OROC = 1.0;				pvPut(CV113OROC, SYNC);
	 		CV113 = 100;					pvPut(CV113, SYNC);
	 		sprintf(CV113SCAN, "1 second");	pvPut(CV113SCAN, SYNC);
	 		
	 		pvMonitor(CV113auf);
	 		pvMonitor(CV114auf);
	 		pvMonitor(CV113OVAL);
	 		pvMonitor(CV114OVAL);
	 		
		}
		when (efTest(adsStop)) {
		}state ueberwachung
		/*
	 	WAITUNTIL(AND('41CV113EN':DEV_STAT,2)=2)COUNT 120,MSG11                                      
 		WAITUNTIL(AND('41CV114EN':DEV_STAT,2)=2)COUNT 120,MSG11                                      
		*/	
		when((!DEBUG_ADS_EIN || debugNextState!=0) && (CV113auf!=0) && (CV113OVAL>99) && (CV114auf!=0) && (CV114OVAL>99)){ 
		}state FLA12
		
		when((!DEBUG_ADS_EIN || debugNextState!=0) && delay(120)){
			if(CV113auf==0){
				snlMsgLog("Eintrittsventil 42CV113 am AD110 in CBX42 öffnet nicht");
			}
			if(CV114auf==0){
				snlMsgLog("Austrittsventil 42CV114 am AD110 in CBX42 öffnet nicht");
			}
		} state FLA11
		
		exit{
	 		pvStopMonitor(CV113auf);
	 		pvStopMonitor(CV114auf);
	 		pvStopMonitor(CV113OVAL);
	 		pvStopMonitor(CV114OVAL);
	 		strcpy(CV114SCAN, ovalZwischenSpeicher1);	pvPut(CV114SCAN, SYNC);
	 		strcpy(CV113SCAN, ovalZwischenSpeicher2);	pvPut(CV113SCAN, SYNC);
 		}
	}
	
	state FLA12{
		option -e;/* führt entry immer wieder aus */
		entry{
			if(DEBUG_ADS_EIN){ 
				debugNextState = 0; 			pvPut(debugNextState);
				sprintf(debugText, "FLA12");	pvPut(debugText);
				snlMsgLog("dbg:<FLA12>");
			}
			// PUTMANL '41HC111'                                                                            
 			// PUTOUT '41HC111',0
 			pvGet(CV111SCAN,SYNC);	 		strcpy(ovalZwischenSpeicher1,CV111SCAN);
 			CV111OMSL = SUPERVISORY;		pvPut(CV111OMSL, SYNC);
	 		CV111OROC = 1.0;				pvPut(CV111OROC, SYNC);
	 		CV111 = 0;						pvPut(CV111, SYNC);                                                                           
	 		sprintf(CV111SCAN, "1 second");	pvPut(CV111SCAN, SYNC);
 			// PUTMANL '41HC112'                                                                            
 			// PUTOUT '41HC112',0
 			pvGet(CV112SCAN,SYNC);	 		strcpy(ovalZwischenSpeicher2,CV112SCAN);
 			CV112OMSL = SUPERVISORY;		pvPut(CV112OMSL, SYNC);
	 		CV112OROC = 1.0;				pvPut(CV112OROC, SYNC);
	 		CV112 = 0;						pvPut(CV112, SYNC);
	 		sprintf(CV112SCAN, "1 second");	pvPut(CV112SCAN, SYNC);
	 		
	 		pvMonitor(CV111zu);                                                                           
	 		pvMonitor(CV112zu);
	 		pvMonitor(CV112OVAL);
	 		pvMonitor(CV111OVAL);

		}
		when (efTest(adsStop)) {
		}state ueberwachung
		// WAITUNTIL(AND('41CV112EN':DEV_STAT,1)=1)COUNT 120,MSG12                                      
 		// WAITUNTIL(AND('41CV111EN':DEV_STAT,1)=1)COUNT 120,MSG12
 		when((!DEBUG_ADS_EIN || debugNextState!=0) && (CV112zu!=0) && (CV112OVAL<1) && (CV111zu!=0) && (CV111OVAL<1)){
 			// ADSORBER 110 IM PROZESS ADSORBER 100 AUS DEM PROZESS                                        
 			// LET AD1BTR=2 // setze Adsorber 1 auf Regeneration
 			AD1BTR = REGENERIERT;
 			pvPut(AD1BTR);
 		} state ueberwachung         
	
		when((!DEBUG_ADS_EIN || debugNextState!=0) && delay(120)){
			if(CV111zu == 0){
				snlMsgLog("Eintrittsventil 42CV111 am AD110 in CBX42 schließt nicht");
			} 
			if(CV112zu == 0){
				snlMsgLog("Austrittsventil 42CV112 am AD110 in CBX42 schließt nicht");
			}
		} state	FLA12
		exit{
			pvStopMonitor(CV111zu);                                                                           
	 		pvStopMonitor(CV112zu);                                                                           
	 		pvStopMonitor(CV112OVAL);
	 		pvStopMonitor(CV111OVAL);
	 		strcpy(CV111SCAN, ovalZwischenSpeicher1);	pvPut(CV111SCAN, SYNC);
	 		strcpy(CV112SCAN, ovalZwischenSpeicher2);	pvPut(CV112SCAN, SYNC);
		}
		
	}

 	//--------- REGENERATION FUER AD100 --------                                                  
 	// OPEN '41UZV192'                                                                              
 	// WAITUNTIL(AND('41UZV192':DEV_STAT,2)=2)                                                      
	state REG100{
		entry{
			if(DEBUG_ADS_EIN){ 
				debugNextState = 0; 			pvPut(debugNextState);
				sprintf(debugText, "REG100");	pvPut(debugText);
				snlMsgLog("dbg:<REG100>");
			}
 			//OPEN '41UZV192'
 			UZV192CMD = AUF;	pvPut(UZV192CMD);
			snlMsgLog("<REG100>Regeneration für AD100 Startet");
 			pvMonitor(UZV192auf);                                                                              
		}
		when (efTest(adsStop)) {
		}state ueberwachung
		//'41UZV192':DEV_STAT,2)=2
		when((!DEBUG_ADS_EIN || debugNextState!=0) && UZV192auf!=0){
		} state DENT1
		when((!DEBUG_ADS_EIN || debugNextState!=0) && delay(60)){
			snlMsgLog("<REG100>UZV192 - öffnet nicht!");
		}state REG100
		exit{
			pvStopMonitor(UZV192auf);
		}
	}
	
	//DENT1:                                                                                        
	//OPEN '41UZV172'                                                                              
	//WAITUNTIL('41PI111':AI_INVL < 1.2)                                                           
	//CLOSE '41UZV172'                                                                             
	//WAITUNTIL(AND('41UZV172':DEV_STAT,1)=1 )                                                     
	//RETURN                                                                                       
	state DENT1{
		entry{
			if(DEBUG_ADS_EIN){ 
				debugNextState = 0; 			pvPut(debugNextState);
				sprintf(debugText, "DENT1");	pvPut(debugText);
				snlMsgLog("dbg:<DENT1>");
			}
	 		// OPEN '41UZV172'
	 		UZV172CMD = AUF; 	pvPut(UZV172CMD);
 			pvMonitor(PI111);   
 		}
		when (efTest(adsStop)) {
		}state ueberwachung
 		/*'41PI111':AI_INVL < 1.2*/
 		when((!DEBUG_ADS_EIN || debugNextState!=0) && PI111 < 1.2){
 		} state DENT1_UZV172_SCHLIESSEN
 		when((!DEBUG_ADS_EIN || debugNextState!=0) && delay(600)){
			snlMsgLog("<DENT1>PI111 - Druck zu hoch!");
		}state DENT1
 		exit{
 			pvStopMonitor(PI111);
 		}
	}

	state DENT1_UZV172_SCHLIESSEN{
		entry{
			if(DEBUG_ADS_EIN){ 
				debugNextState = 0; 							pvPut(debugNextState);
				sprintf(debugText, "DENT1_UZV172_SCHLIESSEN");	pvPut(debugText);
				snlMsgLog("dbg:<DENT1_UZV172_SCHLIESSEN>");
			}
	 		//CLOSE '41UZV172'
 			UZV172CMD = ZU; 	pvPut(UZV172CMD);
 			pvMonitor(UZV172zu);   
 		}                         
		when (efTest(adsStop)) {
		}state ueberwachung
 		//AND('41UZV172':DEV_STAT,1)=1                                                    
		when((!DEBUG_ADS_EIN || debugNextState!=0) && UZV172zu!=0){
		} state REG1
		when((!DEBUG_ADS_EIN || debugNextState!=0) && delay(60)){
			snlMsgLog("<DENT1_UZV172_SCHLIESSEN>UZV172 - schliesst nicht!");
		}state DENT1_UZV172_SCHLIESSEN
		exit{
			pvStopMonitor(UZV172zu);
		}
	}
	
	state REG1{
		entry{
			if(DEBUG_ADS_EIN){ 
				debugNextState = 0; 		pvPut(debugNextState);
				sprintf(debugText, "REG1");	pvPut(debugText);
				snlMsgLog("dbg:<REG1>");
			}
			// OPEN '41UZV174'                                                                              
 			UZV174CMD = AUF; 	pvPut(UZV174CMD);
 			// OPEN '41UZV182'    
 			UZV182CMD = AUF; 	pvPut(UZV182CMD);
 			pvMonitor(UZV174auf);
 			pvMonitor(UZV182auf);
		}
		when (efTest(adsStop)) {
		}state ueberwachung
		//AND('41UZV182':DEV_STAT,2)=2 
		when((!DEBUG_ADS_EIN || debugNextState!=0) && UZV182auf!=0 && UZV174auf!=0){
		} state REG1_UZV173_OEFFNEN
		when((!DEBUG_ADS_EIN || debugNextState!=0) && delay(60)){
			if(UZV174auf==0){
				snlMsgLog("<REG1>UZV174 - öffnet nicht!");
			}
			if(UZV182auf==0){
				snlMsgLog("<REG1>UZV182 - öffnet nicht!");
			}
		}state REG1
		exit{
 			pvStopMonitor(UZV174auf);
 			pvStopMonitor(UZV182auf);
		}
	}	                           
	
	state REG1_UZV173_OEFFNEN{
		entry{
			if(DEBUG_ADS_EIN){ 
				debugNextState = 0; 						pvPut(debugNextState);
				sprintf(debugText, "REG1_UZV173_OEFFNEN");	pvPut(debugText);
				snlMsgLog("dbg:<REG1_UZV173_OEFFNEN>");
			}
			// OPEN '41UZV173'                                                                              
 			UZV173CMD = AUF; 	pvPut(UZV173CMD);
 			pvMonitor(UZV173auf);
		}                          
		when (efTest(adsStop)) {
		}state ueberwachung
		//AND('41UZV173':DEV_STAT,2)=2
		when((!DEBUG_ADS_EIN || debugNextState!=0) && UZV173auf!=0){
		} state REG1_HS151_EINSCHALTEN
		when((!DEBUG_ADS_EIN || debugNextState!=0) && delay(60)){
			snlMsgLog("<REG1_UZV173_OEFFNEN>UZV173 - öffnet nicht!");
		}state REG1_UZV173_OEFFNEN
		exit{
 			pvStopMonitor(UZV173auf);
		}
	}
	
	state REG1_HS151_EINSCHALTEN{
		option -e;
		entry{
			if(DEBUG_ADS_EIN){ 
				debugNextState = 0; 							pvPut(debugNextState);
				sprintf(debugText, "REG1_HS151_EINSCHALTEN");	pvPut(debugText);
				snlMsgLog("dbg:<REG1_HS151_EINSCHALTEN>");
			}
			// 	START '41HS151'
			HS151CMD = EIN; 	pvPut(HS151CMD);
 			pvMonitor(HS151ein);
 			pvMonitor(TI110);
		}
		when (efTest(adsStop)) {
		}state ueberwachung
//		delay(30) && //AND('41HS151':DEV_STAT,2)!=2*/
		when((!DEBUG_ADS_EIN || debugNextState!=0) && delay(30) && HS151ein==0){
				// QUERYNR ADIOCM,"STOERUNG GEBLAESE REGENERATION AD100"
				snlMsgLog("<REG1_HS151_EINSCHALTEN>HS151 - keine Rückmeldung");                                        
				// STOP '41HS151'                                                                               
				HS151CMD = AUS; 	pvPut(HS151CMD);
				// CLOSE '41UZV174'                                                                             
				UZV174CMD = ZU; 	pvPut(UZV174CMD);
		}state DENT1
		
//		delay(30) && /*'41TI110':AI_INVL > 250.*/
		when((!DEBUG_ADS_EIN || debugNextState!=0) && delay(30) && (TI110 > 250.)){
		}state REG1_GEBLAESE_AUS
		
		when((!DEBUG_ADS_EIN || debugNextState!=0) && delay(7200)){
			snlMsgLog("<REG1_HS151_EINSCHALTEN>TI110 - Temperatur nicht erreicht!");
		}state REG1_HS151_EINSCHALTEN
		
		exit{
 			pvStopMonitor(HS151ein);
 			pvStopMonitor(TI110);
		}
	}
	
	state REG1_GEBLAESE_AUS{
		entry{
			if(DEBUG_ADS_EIN){ 
				debugNextState = 0; 						pvPut(debugNextState);
				sprintf(debugText, "REG1_GEBLAESE_AUS");	pvPut(debugText);
				snlMsgLog("dbg:<REG1_GEBLAESE_AUS>");
			}
			// STOP '41HS151'                
			HS151CMD = AUS; 	pvPut(HS151CMD);                                                               
 			// CLOSE '41UZV173'                                                                             
			UZV173CMD = ZU; 	pvPut(UZV173CMD);                                                               
			// CLOSE '41UZV182'
			UZV182CMD = ZU; 	pvPut(UZV182CMD);
			pvMonitor(UZV173zu);                                                               
			pvMonitor(UZV182zu);                                                               
			pvMonitor(HS151aus);                                                               
		}
		when (efTest(adsStop)) {
		}state ueberwachung
		//AND('41UZV173':DEV_STAT,1)=1 & AND('41UZV182':DEV_STAT,1)=1
		when((!DEBUG_ADS_EIN || debugNextState!=0) && (UZV173zu!=0) && (UZV182zu!=0) && (HS151aus!=0)){
		} state REG1_UZV174_SCHLIESSEN
		when((!DEBUG_ADS_EIN || debugNextState!=0) && delay(60)){
			if(UZV173zu==0){
				snlMsgLog("<REG1_GEBLAESE_AUS>UZV173 - Schliesst nicht");
			}
			if(UZV182zu==0){
				snlMsgLog("<REG1_GEBLAESE_AUS>UZV182 - Schliesst nicht");
			}
			if(HS151aus==0){
				snlMsgLog("<REG1_GEBLAESE_AUS>HS151  - Gebläse schaltet nicht ab");
			}
		}state REG1_GEBLAESE_AUS
		
		exit{
			pvStopMonitor(UZV173zu);                                                               
			pvStopMonitor(UZV182zu);                                                               
			pvStopMonitor(HS151aus);                                                               
		}
	}			
	
	state REG1_UZV174_SCHLIESSEN{
		entry{
			if(DEBUG_ADS_EIN){ 
				debugNextState = 0; 							pvPut(debugNextState);
				sprintf(debugText, "REG1_UZV174_SCHLIESSEN");	pvPut(debugText);
				snlMsgLog("dbg:<REG1_UZV174_SCHLIESSEN>");
			}
			// CLOSE '41UZV174'
			UZV174CMD = ZU; 	pvPut(UZV174CMD);
			
			pvMonitor(UZV174zu); 
		}
		when (efTest(adsStop)) {
		}state ueberwachung
		//AND('41UZV174':DEV_STAT,1)=1)
		when((!DEBUG_ADS_EIN || debugNextState!=0) && UZV174zu!=0){
		 	// OPEN '41UZV184'
			UZV184CMD = AUF; 	pvPut(UZV184CMD); 
		}state EVLA10 
		when((!DEBUG_ADS_EIN || debugNextState!=0) && delay(60)){
			snlMsgLog("<REG1_UZV174_SCHLIESSEN>UZV174zu - Schliesst nicht");
		}state REG1_UZV174_SCHLIESSEN
		exit{
			pvStopMonitor(UZV174zu);
		}
	}
	
	//EVLA10:                                                                                       
	// QUERYNR ADIOCM,"*** AD100 WIRD EVAKUIERT ***"                                                
	// QUERYNR ADIOCM,"BITTE VACUUMLEITUNG CHECKEN!"                                                
	// PRINT ADIPNT,"AD100 IN CBX 4%i WIRD EVAKUIERT",CUNIT                                         
	// PRINT ADIPNT,"BITTE VOR-VACUUMLEITUNG CHECKEN!"                                              
	//                                                                                             
	// nach 10 min. muss 0.5 bar unterschritten                                                    
	// werden ,dann wird gepumpt bis 0.1 bar                                                       
	//                                                                                             
	// WAITUNTIL('41PI111':AI_INVL < 0.5) COUNT 600,EVLA10                                          
	// WAITUNTIL('41PI111':AI_INVL < 0.1) COUNT 2000,EVFE10                                         
	state EVLA10{
		option -e;
		entry{
			if(DEBUG_ADS_EIN){ 
				debugNextState = 0; 			pvPut(debugNextState);
				sprintf(debugText, "EVLA10");	pvPut(debugText);
				snlMsgLog("dbg:<EVLA10>");
			}
			// PRINT ADIPNT,"AD100 IN CBX 4%i WIRD EVAKUIERT",CUNIT
			snlMsgLog("AD100 IN CBX 42 WIRD EVAKUIERT! BITTE VOR-VACUUMLEITUNG CHECKEN! ");                                         
			// PRINT ADIPNT,"BITTE VOR-VACUUMLEITUNG CHECKEN!"                                              
			
			pvMonitor(PI111);                                         
		}
		when (efTest(adsStop)) {
		}state ueberwachung
		when((!DEBUG_ADS_EIN || debugNextState!=0) && delay(600)){
		}state EVLA10
		
		/*'41PI111':AI_INVL < 0.5*/
		when((!DEBUG_ADS_EIN || debugNextState!=0) && PI111  < 0.5){
		} state EVLA10_01BAR
		
		exit{
			pvStopMonitor(PI111);
		}                                         
	}
	
	state EVLA10_01BAR{
		entry{
			if(DEBUG_ADS_EIN){ 
				debugNextState = 0; 				pvPut(debugNextState);
				sprintf(debugText, "EVLA10_01BAR");	pvPut(debugText);
				snlMsgLog("dbg:<EVLA10_01BAR>");
			}
			pvMonitor(PI111);
		}
		when (efTest(adsStop)) {
		}state ueberwachung
		when((!DEBUG_ADS_EIN || debugNextState!=0) && delay(2000)){
		}state EVFE10
		/*'41PI111':AI_INVL < 0.1*/
		when((!DEBUG_ADS_EIN || debugNextState!=0) && PI111 < 0.1){
		}state FLUT10	
		
		exit{
			pvStopMonitor(PI111);
		}
	}
	
	state FLUT10{
		entry{
			if(DEBUG_ADS_EIN){ 
				debugNextState = 0; 			pvPut(debugNextState);
				sprintf(debugText, "FLUT10");	pvPut(debugText);
				snlMsgLog("dbg:<FLUT10>");
			}
			// CLOSE  '41UZV184'
			UZV184CMD = ZU;	pvPut(UZV184CMD);
			
			pvMonitor(UZV184zu);                                                                            
		}
		when (efTest(adsStop)) {
		}state ueberwachung
		//AND('41UZV184':DEV_STAT,1)=1
		when((!DEBUG_ADS_EIN || debugNextState!=0) && UZV184zu!=0){
		}state FLUT10_UZV194_OEFFNEN
		when((!DEBUG_ADS_EIN || debugNextState!=0) && delay(60)){
			snlMsgLog("<FLUT10>UZV184 - Schliesst nicht");
		}state FLUT10
		exit{
			pvStopMonitor(UZV184zu);                                                                            
		}
	}
	
	state FLUT10_UZV194_OEFFNEN{
		entry{
			if(DEBUG_ADS_EIN){ 
				debugNextState = 0; 							pvPut(debugNextState);
				sprintf(debugText, "FLUT10_UZV194_OEFFNEN");	pvPut(debugText);
				snlMsgLog("dbg:<FLUT10_UZV194_OEFFNEN>");
			}
			// Fluten                                                                                      
            // OPEN '41UZV194'
            UZV194CMD = AUF;	pvPut(UZV194CMD);
            pvMonitor(PI111); 
		}
		when (efTest(adsStop)) {
		}state ueberwachung
		/*'41PI111':AI_INVL > 5.*/
		when((!DEBUG_ADS_EIN || debugNextState!=0) && PI111 > 5.){
		} state FLUT10_UZV194_SCHLIESSEN
		when((!DEBUG_ADS_EIN || debugNextState!=0) && delay(60)){
			snlMsgLog("<FLUT10_UZV194_OEFFNEN>PI111 - Druck ist zu niedrig");
		}state FLUT10_UZV194_OEFFNEN
		exit{
			pvStopMonitor(PI111);
		}
	}
	
	state FLUT10_UZV194_SCHLIESSEN{
		entry{
			if(DEBUG_ADS_EIN){ 
				debugNextState = 0; 							pvPut(debugNextState);
				sprintf(debugText, "FLUT10_UZV194_SCHLIESSEN");	pvPut(debugText);
				snlMsgLog("FLUT10_UZV194_SCHLIESSEN>");
			}
			//  CLOSE '41UZV194'
			UZV194CMD = ZU;	pvPut(UZV194CMD);
			pvMonitor(UZV194zu);                                                                             
 		}
		when (efTest(adsStop)) {
		}state ueberwachung
 		//AND('41UZV194':DEV_STAT,1)=1
 		when((!DEBUG_ADS_EIN || debugNextState!=0) && UZV194zu!=0){
 		} state FLUT10_UZV192_SCHLIESSEN
 		when((!DEBUG_ADS_EIN || debugNextState!=0) && delay(60)){
			snlMsgLog("<FLUT10_UZV194_SCHLIESSEN>UZV194 - schliesst nicht");
		}state FLUT10_UZV194_SCHLIESSEN
 		
 		exit{
 			pvStopMonitor(UZV194zu);
 		}    
	}
	
	state FLUT10_UZV192_SCHLIESSEN{
		entry{
			if(DEBUG_ADS_EIN){ 
				debugNextState = 0; 							pvPut(debugNextState);
				sprintf(debugText, "FLUT10_UZV192_SCHLIESSEN");	pvPut(debugText);
				snlMsgLog("FLUT10_UZV192_SCHLIESSEN>");
			}
			// Fertig                                                                                      
			// CLOSE '41UZV192'
			UZV192CMD = ZU;	pvPut(UZV192CMD);
			
			pvMonitor(UZV192zu);
		}
		when (efTest(adsStop)) {
		}state ueberwachung
		//AND('41UZV192':DEV_STAT,1)=1
		when((!DEBUG_ADS_EIN || debugNextState!=0) && UZV192zu!=0){
			// QUERYNR ADIOCM,"AD100 REGENERIERT UND MIT 5 BAR GEFUELLT"                                    
			snlMsgLog("AD100 REGENERIERT UND MIT 5 BAR GEFUELLT");                                    
 			// LET AD1BTR=0 
 			AD1BTR = BETRIEBSBEREIT;		pvPut(AD1BTR); 
		}state ueberwachung
		when((!DEBUG_ADS_EIN || debugNextState!=0) && delay(60)){
			snlMsgLog("<FLUT10_UZV192_SCHLIESSEN>UZV192 - schliesst nicht");
		}state FLUT10_UZV192_SCHLIESSEN
		
		exit{
			pvStopMonitor(UZV192zu);
		}  
	}
	
	//	EVFE10:                                                                                       
 	//	LET ANTW1 = 0                                                                                
 	//	QUERYNR ADIOCM,"AD100 VAKUUM 0.1 BAR NICHT ERREICHT !"                                       
 	//	QUERY ADIOCM,"DIE REGENERATION WIEDERHOLEN ? (J=1)",ANTW1                                    
    //                                                                                         
 	//	IF (ANTW1 = 1) THEN                                                                          
 	//	BEGIN                                                                                        
 	//		CLOSE  '41UZV184'                                                                            
 	//		WAITUNTIL(AND('41UZV184':DEV_STAT,1)=1)                                                      
 	//		OPEN '41UZV194'                                                                              
 	//		WAITUNTIL('41PI111':AI_INVL > 5.)                                                            
 	//		CLOSE '41UZV194'                                                                             
 	//		WAITUNTIL(AND('41UZV194':DEV_STAT,1)=1)                                                      
 	//		GOTO REG1 // hier muss zu DENT1 gegangen werden                                                                                   
 	//		END                                                                                          
 	//	ELSE                                                                                         
 	//	GOTO FLUT10
	state EVFE10{
		entry{
			if(DEBUG_ADS_EIN){ 
				debugNextState = 0; 			pvPut(debugNextState);
				sprintf(debugText, "EVFE10");	pvPut(debugText);
				snlMsgLog("dbg:<EVFE10>");
			}
			// LET ANTW1 = 0                                                                                
			AD_OPERATOR_ANT_JA=0;				pvPut(AD_OPERATOR_ANT_JA,SYNC);
			AD_OPERATOR_ANT_NEIN=0;				pvPut(AD_OPERATOR_ANT_NEIN,SYNC);
			pvMonitor(AD_OPERATOR_ANT_JA);
			pvMonitor(AD_OPERATOR_ANT_NEIN);
			// QUERYNR ADIOCM,"AD100 VAKUUM 0.1 BAR NICHT ERREICHT !"                                       
			// QUERY ADIOCM,"DIE REGENERATION WIEDERHOLEN ? (J=1)",ANTW1
			snlMsgLog("AD100 VAKUUM 0.1 BAR NICHT ERREICHT ! DIE REGENERATION WIEDERHOLEN ?");
			sprintf(AD_OPERATOR_ANT_JA_TEXT, "Regeneration Wiederholen ?");		pvPut(AD_OPERATOR_ANT_JA_TEXT);  
		}
		when (efTest(adsStop)) {
		}state ueberwachung
		// Ja (Regeneration Wiederholen)
		when((!DEBUG_ADS_EIN || debugNextState!=0) && AD_OPERATOR_ANT_JA){
		} state EVFE10_UZV184_SCHLIESSEN
		// Nein (Weiter)
		when((!DEBUG_ADS_EIN || debugNextState!=0) && AD_OPERATOR_ANT_NEIN){
		}state FLUT10
		exit{
			pvStopMonitor(AD_OPERATOR_ANT_JA);
			pvStopMonitor(AD_OPERATOR_ANT_NEIN);
			strcpy(AD_OPERATOR_ANT_JA_TEXT, "");		pvPut(AD_OPERATOR_ANT_JA_TEXT);  
		}
	}
	
	state EVFE10_UZV184_SCHLIESSEN{
		entry{
			if(DEBUG_ADS_EIN){ 
				debugNextState = 0; 							pvPut(debugNextState);
				sprintf(debugText, "EVFE10_UZV184_SCHLIESSEN");	pvPut(debugText);
				snlMsgLog("dbg:<EVFE10_UZV184_SCHLIESSEN>");
			}
			UZV184CMD = ZU;	pvPut(UZV184CMD);
			pvMonitor(UZV184zu);
		}

		when (efTest(adsStop)) {
		}state ueberwachung
		when((!DEBUG_ADS_EIN || debugNextState!=0) && UZV184zu!=0){
		} state EVFE10_UZV194_OEFFNEN
		when((!DEBUG_ADS_EIN || debugNextState!=0) && delay(60)){
			snlMsgLog("<EVFE10_UZV184_SCHLIESSEN>UZV184 - schliesst nicht");
		}state EVFE10_UZV184_SCHLIESSEN
		exit{
			pvStopMonitor(UZV184zu);
		}
	}
	
	state EVFE10_UZV194_OEFFNEN {	 
		entry{
			if(DEBUG_ADS_EIN){ 
				debugNextState = 0; 							pvPut(debugNextState);
				sprintf(debugText, "EVFE10_UZV194_OEFFNEN");	pvPut(debugText);
				snlMsgLog("dbg:<EVFE10_UZV194_OEFFNEN>");
			}
			UZV194CMD = AUF;	pvPut(UZV194CMD);
			pvMonitor(PI111);
		}
		when (efTest(adsStop)) {
		}state ueberwachung
		when((!DEBUG_ADS_EIN || debugNextState!=0) && PI111 > 5.){
		} state EVFE10_UZV194_SCHLIESSEN
		when((!DEBUG_ADS_EIN || debugNextState!=0) && delay(60)){
			snlMsgLog("<EVFE10_UZV194_OEFFNEN>PI111 - Druck zu niedrig");
		}state EVFE10_UZV194_OEFFNEN
		
		exit{
			pvStopMonitor(PI111);
		}
	}
	
	state EVFE10_UZV194_SCHLIESSEN {
		entry{
			if(DEBUG_ADS_EIN){ 
				debugNextState = 0; 							pvPut(debugNextState);
				sprintf(debugText, "EVFE10_UZV194_SCHLIESSEN");	pvPut(debugText);
				snlMsgLog("dbg:<EVFE10_UZV194_SCHLIESSEN>");
			}
			UZV194CMD = ZU;	pvPut(UZV194CMD);
			pvMonitor(UZV194zu);
		}
		when (efTest(adsStop)) {
		}state ueberwachung
		when((!DEBUG_ADS_EIN || debugNextState!=0) && UZV194zu!=0){
		} state DENT1
		when((!DEBUG_ADS_EIN || debugNextState!=0) && delay(60)){
			snlMsgLog("<EVFE10_UZV194_SCHLIESSEN>UZV194 - schliesst nicht");
		}state EVFE10_UZV194_SCHLIESSEN
		exit{
			pvStopMonitor(UZV194zu);
		}
	}
	
	// ---------------------------- Wechsel AD100 fuer AD110 -------------------------------------------------------------------------------
	// CHNG2:                                                                                        
	// Erst alle Regenerationsventile schliessen ( definierter Zustand)                            
	// Kaltfahren AD100                                                                            
	state CHNG2{
		entry{
			if(DEBUG_ADS_EIN){ 
				debugNextState = 0; 	pvPut(debugNextState);
				sprintf(debugText, "CHNG2");	pvPut(debugText);
				snlMsgLog("dbg:<CHNG2>");
			}
			// Erst alle Regenerationsventile schliessen ( definierter Zustand)                            
	 		UZV174CMD = ZU;	 		pvPut(UZV174CMD);                                                                             
	 		UZV184CMD = ZU;	 		pvPut(UZV184CMD);                                                                             
			UZV172CMD = ZU;			pvPut(UZV172CMD);                                                                             
			UZV194CMD = ZU;			pvPut(UZV194CMD);                                                                             
			UZV193CMD = ZU;			pvPut(UZV193CMD);                                                                             
			UZV192CMD = ZU;			pvPut(UZV192CMD);                                                                             
			UZV173CMD = ZU;			pvPut(UZV173CMD);                                                                             
			UZV273CMD = ZU;			pvPut(UZV273CMD);                                                                             
			UZV183CMD = ZU;			pvPut(UZV183CMD);                                                                             
			UZV182CMD = ZU;			pvPut(UZV182CMD);                                                                             
			UZV283CMD = ZU;			pvPut(UZV283CMD);                                                                             
			UZV282CMD = ZU;			pvPut(UZV282CMD);
			
			// Kaltfahren AD110                                                                            
	 		// IF ('41HC112':CB_OTVL:1 < 10.0) THEN                                                         
	 		pvGet(CV112OVAL);
	 		if(CV112OVAL < 10.0){
		 		// PUTMANL '41HC112'
		 		// PUTOUT '41HC112',10                                                                          
		 		pvGet(CV112SCAN,SYNC);	 		strcpy(ovalZwischenSpeicher1,CV112SCAN); 
		 		CV112OMSL = SUPERVISORY;		pvPut(CV112OMSL, SYNC);
		 		CV112OROC = 1.0;				pvPut(CV112OROC, SYNC);
		 		CV112 = 10;						pvPut(CV112, SYNC);
		 		sprintf(CV112SCAN, "1 second");	pvPut(CV112SCAN, SYNC);
			}	 		
			pvMonitor(PI113);
			pvMonitor(PI111);
		}
		when (efTest(adsStop)) {
		}state ueberwachung
	 		// Druck angleichen                                                                            
	 		// WAITUNTIL('41PI111':AI_INVL > '41PI113':AI_INVL-1.5 )
		when((!DEBUG_ADS_EIN || debugNextState!=0) && PI111 > (PI113-1.5)){
			// PUTMANL nicht vergessen?
			// PUTOUT '41HC112',100
		 	pvGet(CV112SCAN,SYNC);	 		strcpy(ovalZwischenSpeicher1,CV112SCAN); 
	 		CV112OMSL = SUPERVISORY;		pvPut(CV112OMSL, SYNC);
	 		CV112OROC = 1.0;				pvPut(CV112OROC, SYNC);
	 		CV112 = 100;					pvPut(CV112, SYNC);
	 		sprintf(CV112SCAN, "1 second");	pvPut(CV112SCAN, SYNC);
		}state CHNG2_HEIZUNG_EINSCHALTEN
		
		when((!DEBUG_ADS_EIN || debugNextState!=0) && delay(60)){
			snlMsgLog("<CHNG2>Druckangleich PI113 / PI111 nicht ok!");
		}state CHNG2
		exit{
			pvStopMonitor(PI113);
			pvStopMonitor(PI111);
			strcpy(CV112SCAN, ovalZwischenSpeicher1);	pvPut(CV112SCAN, SYNC);
		}
	}
	
	state CHNG2_HEIZUNG_EINSCHALTEN{
		entry{
			if(DEBUG_ADS_EIN){ 
				debugNextState = 0; 								pvPut(debugNextState);
				sprintf(debugText, "CHNG2_HEIZUNG_EINSCHALTEN");	pvPut(debugText);
				snlMsgLog("dbg:<CHNG2_HEIZUNG_EINSCHALTEN>");
			}
	 		// Heizung eingeschalten                                                                      
		 	// OPEN '41HS130'         
		 	HS130CMD = EIN;	 		pvPut(HS130CMD);
		 	pvMonitor(HS130ein);
		}
		when (efTest(adsStop)) {
		}state ueberwachung
		//AND('41HS130':DEV_STAT,2)=2
		when((!DEBUG_ADS_EIN || debugNextState!=0) && HS130ein!=0){
		}state CHNG2_UZV192_OEFFNEN
		when((!DEBUG_ADS_EIN || debugNextState!=0) && delay(60)){
			snlMsgLog("<CHNG2_HEIZUNG_EINSCHALTEN>HS130 - keine Rückmeldung");
		}state CHNG2_HEIZUNG_EINSCHALTEN
		
		exit{	
		 	pvStopMonitor(HS130ein);
	 	}
	}
	
	state CHNG2_UZV192_OEFFNEN{
		entry{
			if(DEBUG_ADS_EIN){ 
				debugNextState = 0; 						pvPut(debugNextState);
				sprintf(debugText, "CHNG2_UZV192_OEFFNEN");	pvPut(debugText);
				snlMsgLog("dbg:<CHNG2_UZV192_OEFFNEN>");
			}
			//OPEN '41UZV192'  
		 	UZV192CMD = AUF;	 		pvPut(UZV192CMD);
		 	pvMonitor(UZV192auf);
		}
		when (efTest(adsStop)) {
		}state ueberwachung
		//AND('41UZV192':DEV_STAT,2)=2
		when((!DEBUG_ADS_EIN || debugNextState!=0) && UZV192auf!=0){
		}state CHNG2_UZV172_OEFFNEN
		when((!DEBUG_ADS_EIN || debugNextState!=0) && delay(60)){
			snlMsgLog("<CHNG2_UZV192_OEFFNEN>UZV192 - öffnet nicht!");
		}state CHNG2_UZV192_OEFFNEN
		
		exit{
		 	pvStopMonitor(UZV192auf);
	 	}
	}
	
	state CHNG2_UZV172_OEFFNEN{
		entry{
			if(DEBUG_ADS_EIN){ 
				debugNextState = 0; 						pvPut(debugNextState);
				sprintf(debugText, "CHNG2_UZV172_OEFFNEN");	pvPut(debugText);
				snlMsgLog("dbg:<CHNG2_UZV172_OEFFNEN>");
			}
			// OPEN '41UZV172'
		 	UZV172CMD = AUF;	 		pvPut(UZV172CMD);
		 	pvMonitor(UZV172auf);
		}
		when (efTest(adsStop)) {
		}state ueberwachung
		//AND('41UZV172':DEV_STAT,2)=2
		when((!DEBUG_ADS_EIN || debugNextState!=0) && UZV172auf!=0){
		}state CHNG2_AD100_KALTFAHREN
		when((!DEBUG_ADS_EIN || debugNextState!=0) && delay(60)){
			snlMsgLog("<CHNG2_UZV172_OEFFNEN>UZV172 - öffnet nicht!");
		}state CHNG2_UZV172_OEFFNEN
		
		exit{
		 	pvStopMonitor(UZV172auf);
	 	}
	}
	
	state CHNG2_AD100_KALTFAHREN{
		entry{
			if(DEBUG_ADS_EIN){ 
				debugNextState = 0; 							pvPut(debugNextState);
				sprintf(debugText, "CHNG2_AD100_KALTFAHREN");	pvPut(debugText);
				snlMsgLog("dbg:<CHNG2_AD100_KALTFAHREN>");
			}
			pvMonitor(TI112);
			pvMonitor(TI110);
		}
		when (efTest(adsStop)) {
		}state ueberwachung
		//'41TI112':AI_INVL < '41TI110':AI_INVL+5.0
		when((!DEBUG_ADS_EIN || debugNextState!=0) && TI110 < (TI112+5.0)){
		}state CHNG2_UZV192_SCHLIESSEN
		when((!DEBUG_ADS_EIN || debugNextState!=0) && delay(60)){
			snlMsgLog("<CHNG2_AD100_KALTFAHREN>Kaltfahren nicht erfolgreich! TI110 zu hoch.");
		} state CHNG2_AD100_KALTFAHREN
		exit{
			pvStopMonitor(TI112);
			pvStopMonitor(TI110);
		}
	}
	
	state CHNG2_UZV192_SCHLIESSEN{
		entry{
			if(DEBUG_ADS_EIN){ 
				debugNextState = 0; 							pvPut(debugNextState);
				sprintf(debugText, "CHNG2_UZV192_SCHLIESSEN");	pvPut(debugText);
				snlMsgLog("dbg:<CHNG2_UZV192_SCHLIESSEN>");
			}
		 	// Nun ist AD110 kalt                                                                          
			// CLOSE '41UZV192'
			UZV192CMD = ZU;	 		pvPut(UZV192CMD);
		 	pvMonitor(UZV192zu);
		}
		
		when (efTest(adsStop)) {
		}state ueberwachung
		//(AND('41UZV192':DEV_STAT,1)=1
		when((!DEBUG_ADS_EIN || debugNextState!=0) && UZV192zu!=0){
		} state CHNG2_UZV172_SCHLIESSEN
		when((!DEBUG_ADS_EIN || debugNextState!=0) && delay(60)){
			snlMsgLog("<CHNG2_UZV192_SCHLIESSEN>UZV192 - schliesst nicht!");
		}state CHNG2_UZV192_SCHLIESSEN
		exit{
		 	pvStopMonitor(UZV192zu);
	 	}
	} 
	
	state CHNG2_UZV172_SCHLIESSEN{
		entry{
			if(DEBUG_ADS_EIN){ 
				debugNextState = 0; 							pvPut(debugNextState);
				sprintf(debugText, "CHNG2_UZV172_SCHLIESSEN");	pvPut(debugText);
				snlMsgLog("dbg:<CHNG2_UZV172_SCHLIESSEN>");
			}
			// CLOSE '41UZV172'                                                                             
			UZV172CMD = ZU;	 		pvPut(UZV172CMD);
		 	pvMonitor(UZV172zu);
		}
		when (efTest(adsStop)) {
		}state ueberwachung
		//AND('41UZV172':DEV_STAT,1)=1
		when((!DEBUG_ADS_EIN || debugNextState!=0) && UZV172zu!=0){
		} state FLA21
		when((!DEBUG_ADS_EIN || debugNextState!=0) && delay(60)){
			snlMsgLog("<CHNG2_UZV172_SCHLIESSEN>UZV172 - schliesst nicht!");
		}state CHNG2_UZV172_SCHLIESSEN
		exit{
		 	pvStopMonitor(UZV172zu);
	 	}
	}         
	
	state FLA21{
		option -e; /* führt entry immer wieder aus */
		entry{
			if(DEBUG_ADS_EIN){ 
				debugNextState = 0; 			pvPut(debugNextState);
				sprintf(debugText, "FLA21");	pvPut(debugText);
				snlMsgLog("dbg:<FLA21>");
			}
			//PUTMANL '41HC112'                                                                            
 			//PUTOUT '41HC112',100
			pvGet(CV112SCAN,SYNC);	 		strcpy(ovalZwischenSpeicher1,CV112SCAN); 
 			CV112OMSL = SUPERVISORY;		pvPut(CV112OMSL, SYNC);
	 		CV112OROC = 1.0;				pvPut(CV112OROC, SYNC);
	 		CV112 = 100;					pvPut(CV112, SYNC);
	 		sprintf(CV112SCAN, "1 second");	pvPut(CV112SCAN, SYNC);
 			                                                                         
 			//PUTMANL '41HC111'                                                                            
 			//PUTOUT '41HC111',100
			pvGet(CV111SCAN,SYNC);	 		strcpy(ovalZwischenSpeicher2,CV111SCAN); 
 			CV111OMSL = SUPERVISORY;		pvPut(CV111OMSL, SYNC);
	 		CV111OROC = 1.0;				pvPut(CV111OROC, SYNC);
	 		CV111 = 100;					pvPut(CV111, SYNC);
	 		sprintf(CV111SCAN, "1 second");	pvPut(CV111SCAN, SYNC);
	 		pvMonitor(CV111auf);
	 		pvMonitor(CV112auf);
	 		pvMonitor(CV111OVAL);
	 		pvMonitor(CV112OVAL);
		}
		when (efTest(adsStop)) {
		}state ueberwachung
		/*
	 	WAITUNTIL(AND('41CV111EN':DEV_STAT,2)=2)COUNT 120,MSG11                                      
 		WAITUNTIL(AND('41CV112EN':DEV_STAT,2)=2)COUNT 120,MSG11                                      
		*/	
		when((!DEBUG_ADS_EIN || debugNextState!=0) && (CV111auf!=0) && (CV111OVAL>99) && (CV112auf!=0) && (CV112OVAL>99)){ 
		}state FLA22
		
		when((!DEBUG_ADS_EIN || debugNextState!=0) && delay(120)){
			if(CV111auf==0){
				snlMsgLog("Eintrittsventil 42CV111 am AD100 in CBX42 öffnet nicht");
			}
			if(CV112auf==0){
				snlMsgLog("Austrittsventil 42CV112 am AD100 in CBX42 öffnet nicht");
			}
		} state FLA21
		
		exit{
	 		pvStopMonitor(CV111auf);
	 		pvStopMonitor(CV112auf);
	 		pvStopMonitor(CV111OVAL);
	 		pvStopMonitor(CV112OVAL);
	 		strcpy(CV112SCAN, ovalZwischenSpeicher1);	pvPut(CV112SCAN, SYNC);
	 		strcpy(CV111SCAN, ovalZwischenSpeicher2);	pvPut(CV111SCAN, SYNC);
 		}
	}
	
	state FLA22{
		option -e;/* führt entry immer wieder aus */
		entry{
			if(DEBUG_ADS_EIN){ 
				debugNextState = 0; 			pvPut(debugNextState);
				sprintf(debugText, "FLA22");	pvPut(debugText);
				snlMsgLog("dbg:<FLA22>");
			}
			// PUTMANL '41HC113'                                                                            
 			// PUTOUT '41HC113',0
 			pvGet(CV113SCAN,SYNC);	 		strcpy(ovalZwischenSpeicher1,CV113SCAN);
 			CV113OMSL = SUPERVISORY;		pvPut(CV113OMSL, SYNC);
	 		CV113OROC = 1.0;				pvPut(CV113OROC, SYNC);
	 		CV113 = 0;						pvPut(CV113, SYNC);                                                                           
	 		sprintf(CV113SCAN, "1 second");	pvPut(CV113SCAN, SYNC);
 			// PUTMANL '41HC114'                                                                            
 			// PUTOUT '41HC114',0
 			pvGet(CV114SCAN,SYNC);	 		strcpy(ovalZwischenSpeicher2,CV114SCAN);
 			CV114OMSL = SUPERVISORY;		pvPut(CV114OMSL, SYNC);
	 		CV114OROC = 1.0;				pvPut(CV114OROC, SYNC);
	 		CV114 = 0;						pvPut(CV114, SYNC);
	 		sprintf(CV114SCAN, "1 second");	pvPut(CV114SCAN, SYNC);
	 		
	 		pvMonitor(CV113zu);                                                                           
	 		pvMonitor(CV114zu);
	 		pvMonitor(CV114OVAL);
	 		pvMonitor(CV113OVAL);

		}
		when (efTest(adsStop)) {
		}state ueberwachung
		// WAITUNTIL(AND('41CV114EN':DEV_STAT,1)=1)COUNT 120,MSG12                                      
 		// WAITUNTIL(AND('41CV113EN':DEV_STAT,1)=1)COUNT 120,MSG12
 		when((!DEBUG_ADS_EIN || debugNextState!=0) && (CV114zu!=0) && (CV114OVAL<1) && (CV113zu!=0) && (CV113OVAL<1)){
 			// ADSORBER 100 IM PROZESS ADSORBER 110 AUS DEM PROZESS                                        
 			// LET AD2BTR=2 // setze Adsorber 2 auf Regeneration
 			AD2BTR = REGENERIERT;
 			pvPut(AD2BTR);
 		} state ueberwachung         
		when((!DEBUG_ADS_EIN || debugNextState!=0) && delay(120)){
			if(CV113zu == 0){
				snlMsgLog("Eintrittsventil 42CV113 am AD100 in CBX42 schließt nicht");
			} 
			if(CV114zu == 0){
				snlMsgLog("Austrittsventil 42CV114 am AD100 in CBX42 schließt nicht");
			}
		} state	FLA22
		exit{
			pvStopMonitor(CV113zu);                                                                           
	 		pvStopMonitor(CV114zu);                                                                           
	 		pvStopMonitor(CV114OVAL);
	 		pvStopMonitor(CV113OVAL);
	 		strcpy(CV113SCAN, ovalZwischenSpeicher1);	pvPut(CV113SCAN, SYNC);
	 		strcpy(CV114SCAN, ovalZwischenSpeicher2);	pvPut(CV114SCAN, SYNC);
		}
	}
	
	//--------- REGENERATION FUER AD110 --------                                                  
 	// OPEN '41UZV193'                                                                              
 	// WAITUNTIL(AND('41UZV193':DEV_STAT,2)=2)                                                      
	state REG110{
		entry{
			if(DEBUG_ADS_EIN){ 
				debugNextState = 0; 			pvPut(debugNextState);
				sprintf(debugText, "REG110");	pvPut(debugText);
				snlMsgLog("dbg:<REG110>");
			}
 			//OPEN '41UZV193'
 			UZV193CMD = AUF;	pvPut(UZV193CMD);
 			snlMsgLog("<REG110>Regeneration für AD110 Startet");
 			pvMonitor(UZV193auf);                                                                              
		}
		when (efTest(adsStop)) {
		}state ueberwachung
		//'41UZV193':DEV_STAT,2)=2
		when((!DEBUG_ADS_EIN || debugNextState!=0) && UZV193auf!=0){
		} state DENT2
		when((!DEBUG_ADS_EIN || debugNextState!=0) && delay(60)){
			snlMsgLog("<REG110>UZV193 - öffnet nicht!");
		}state REG110
		exit{
			pvStopMonitor(UZV193auf);
		}
	}
	
	//DENT2:                                                                                        
	//OPEN '41UZV172'                                                                              
	//WAITUNTIL('41PI113':AI_INVL < 1.2)                                                           
	//CLOSE '41UZV172'                                                                             
	//WAITUNTIL(AND('41UZV172':DEV_STAT,1)=1 )                                                     
	//RETURN                                                                                       
	state DENT2{
		entry{
			if(DEBUG_ADS_EIN){ 
				debugNextState = 0; 			pvPut(debugNextState);
				sprintf(debugText, "DENT2");	pvPut(debugText);
				snlMsgLog("dbg:<DENT2>");
			}
	 		// OPEN '41UZV172'
	 		UZV172CMD = AUF; 	pvPut(UZV172CMD);
 			pvMonitor(PI113);   
 		}
		when (efTest(adsStop)) {
		}state ueberwachung
 		/*'41PI113':AI_INVL < 1.2*/
 		when((!DEBUG_ADS_EIN || debugNextState!=0) && PI113 < 1.2){
 		} state DENT2_UZV172_SCHLIESSEN
 		when((!DEBUG_ADS_EIN || debugNextState!=0) && delay(600)){
			snlMsgLog("<DENT2>PI113 - Druck zu hoch!");
		}state DENT2
 		exit{
 			pvStopMonitor(PI113);
 		}
	}

	state DENT2_UZV172_SCHLIESSEN{
		entry{
			if(DEBUG_ADS_EIN){ 
				debugNextState = 0; 							pvPut(debugNextState);
				sprintf(debugText, "DENT2_UZV172_SCHLIESSEN");	pvPut(debugText);
				snlMsgLog("dbg:<DENT2_UZV172_SCHLIESSEN>");
			}
	 		//CLOSE '41UZV172'
 			UZV172CMD = ZU; 	pvPut(UZV172CMD);
 			pvMonitor(UZV172zu);   
 		}                         
		when (efTest(adsStop)) {
		}state ueberwachung
 		//AND('41UZV172':DEV_STAT,1)=1                                                    
		when((!DEBUG_ADS_EIN || debugNextState!=0) && UZV172zu!=0){
		} state REG2
		when((!DEBUG_ADS_EIN || debugNextState!=0) && delay(60)){
			snlMsgLog("<DENT2_UZV172_SCHLIESSEN>UZV172 - schliesst nicht!");
		}state DENT2_UZV172_SCHLIESSEN
		exit{
			pvStopMonitor(UZV172zu);
		}
	}
	
	state REG2{
		entry{
			if(DEBUG_ADS_EIN){ 
				debugNextState = 0; 		pvPut(debugNextState);
				sprintf(debugText, "REG2");	pvPut(debugText);
				snlMsgLog("dbg:<REG2>");
			}
			// OPEN '41UZV174'                                                                              
 			UZV174CMD = AUF; 	pvPut(UZV174CMD);
 			// OPEN '41UZV183'    
 			UZV183CMD = AUF; 	pvPut(UZV183CMD);
 			pvMonitor(UZV174auf);
 			pvMonitor(UZV183auf);
		}
		when (efTest(adsStop)) {
		}state ueberwachung
		//AND('41UZV183':DEV_STAT,2)=2 
		when((!DEBUG_ADS_EIN || debugNextState!=0) && UZV183auf!=0 && UZV174auf!=0){
		} state REG2_UZV173_OEFFNEN
		when((!DEBUG_ADS_EIN || debugNextState!=0) && delay(60)){
			if(UZV174auf==0){
				snlMsgLog("<REG2>UZV174 - öffnet nicht!");
			}
			if(UZV183auf==0){
				snlMsgLog("<REG2>UZV183 - öffnet nicht!");
			}
		}state REG2
		exit{
 			pvStopMonitor(UZV174auf);
 			pvStopMonitor(UZV183auf);
		}
	}	                           
	
	state REG2_UZV173_OEFFNEN{
		entry{
			if(DEBUG_ADS_EIN){ 
				debugNextState = 0; 						pvPut(debugNextState);
				sprintf(debugText, "REG2_UZV173_OEFFNEN");	pvPut(debugText);
				snlMsgLog("dbg:<REG2_UZV173_OEFFNEN>");
			}
			// OPEN '41UZV173'                                                                              
 			UZV173CMD = AUF; 	pvPut(UZV173CMD);
 			pvMonitor(UZV173auf);
		}                          
		when (efTest(adsStop)) {
		}state ueberwachung
		//AND('41UZV173':DEV_STAT,2)=2
		when((!DEBUG_ADS_EIN || debugNextState!=0) && UZV173auf!=0){
		} state REG2_HS151_EINSCHALTEN
		when((!DEBUG_ADS_EIN || debugNextState!=0) && delay(60)){
			snlMsgLog("<REG2_UZV173_OEFFNEN>UZV173 - öffnet nicht!");
		}state REG2_UZV173_OEFFNEN
		exit{
 			pvStopMonitor(UZV173auf);
		}
	}
	
	state REG2_HS151_EINSCHALTEN{
		option -e;
		entry{
			if(DEBUG_ADS_EIN){ 
				debugNextState = 0; 							pvPut(debugNextState);
				sprintf(debugText, "REG2_HS151_EINSCHALTEN");	pvPut(debugText);
				snlMsgLog("dbg:<REG2_HS151_EINSCHALTEN>");
			}
			// 	START '41HS151'
			HS151CMD = EIN; 	pvPut(HS151CMD);
 			pvMonitor(HS151ein);
 			pvMonitor(TI112);
		}
		when (efTest(adsStop)) {
		}state ueberwachung
//		delay(30) && //AND('41HS151':DEV_STAT,2)!=2*/
		when((!DEBUG_ADS_EIN || debugNextState!=0) && delay(30) && HS151ein==0){
				// QUERYNR ADIOCM,"STOERUNG GEBLAESE REGENERATION AD110"
				snlMsgLog("<REG2_HS151_EINSCHALTEN>HS151 - keine Rückmeldung");                                        
				// STOP '41HS151'                                                                               
				HS151CMD = AUS; 	pvPut(HS151CMD);
				// CLOSE '41UZV174'                                                                             
				UZV174CMD = ZU; 	pvPut(UZV174CMD);
		}state DENT2
//		delay(30) && /*'41TI112':AI_INVL > 250.*/
		when((!DEBUG_ADS_EIN || debugNextState!=0) && delay(30) && (TI112 > 250.)){
		}state REG2_GEBLAESE_AUS
		
		when((!DEBUG_ADS_EIN || debugNextState!=0) && delay(7200)){
			snlMsgLog("<REG2_HS151_EINSCHALTEN>TI112 - Temperatur nicht erreicht!");
		}state REG2_HS151_EINSCHALTEN
		
		exit{
 			pvStopMonitor(HS151ein);
 			pvStopMonitor(TI112);
		}
	}
	
	state REG2_GEBLAESE_AUS{
		entry{
			if(DEBUG_ADS_EIN){ 
				debugNextState = 0; 						pvPut(debugNextState);
				sprintf(debugText, "REG2_GEBLAESE_AUS");	pvPut(debugText);
				snlMsgLog("dbg:<REG2_GEBLAESE_AUS>");
			}
			// STOP '41HS151'                
			HS151CMD = AUS; 	pvPut(HS151CMD);                                                               
 			// CLOSE '41UZV173'                                                                             
			UZV173CMD = ZU; 	pvPut(UZV173CMD);                                                               
			// CLOSE '41UZV183'
			UZV183CMD = ZU; 	pvPut(UZV183CMD);
			
			pvMonitor(UZV173zu);                                                               
			pvMonitor(UZV183zu); 
			pvMonitor(HS151aus);                                                              
		}
		when (efTest(adsStop)) {
		}state ueberwachung
		//AND('41UZV173':DEV_STAT,1)=1 & AND('41UZV183':DEV_STAT,1)=1
		when((!DEBUG_ADS_EIN || debugNextState!=0) && (UZV173zu!=0) && (UZV183zu!=0) && (HS151aus!=0)){
		} state REG2_UZV174_SCHLIESSEN
		when((!DEBUG_ADS_EIN || debugNextState!=0) && delay(60)){
			if(UZV173zu==0){
				snlMsgLog("<REG2_GEBLAESE_AUS>UZV173 - Schliesst nicht");
			}
			if(UZV183zu==0){
				snlMsgLog("<REG2_GEBLAESE_AUS>UZV183 - Schliesst nicht");
			}
			if(HS151aus==0){
				snlMsgLog("<REG2_GEBLAESE_AUS>HS151  - Gebläse schaltet nicht ab");
			}
		}state REG2_GEBLAESE_AUS
		
		exit{
			pvStopMonitor(UZV173zu);                                                               
			pvStopMonitor(UZV183zu);  
			pvStopMonitor(HS151aus);                                                             
		}
	}			
	
	state REG2_UZV174_SCHLIESSEN{
		entry{
			if(DEBUG_ADS_EIN){ 
				debugNextState = 0; 							pvPut(debugNextState);
				sprintf(debugText, "REG2_UZV174_SCHLIESSEN");	pvPut(debugText);
				snlMsgLog("dbg:<REG2_UZV174_SCHLIESSEN>");
			}
			// CLOSE '41UZV174'
			UZV174CMD = ZU; 	pvPut(UZV174CMD);
			pvMonitor(UZV174zu); 
		}
		when (efTest(adsStop)) {
		}state ueberwachung
		//AND('41UZV174':DEV_STAT,1)=1)
		when((!DEBUG_ADS_EIN || debugNextState!=0) && UZV174zu!=0){
		 	// OPEN '41UZV184'
			UZV184CMD = AUF; 	pvPut(UZV184CMD); 
		}state EVLA11 
		when((!DEBUG_ADS_EIN || debugNextState!=0) && delay(60)){
			snlMsgLog("<REG2_UZV174_SCHLIESSEN>UZV174zu - Schliesst nicht");
		}state REG2_UZV174_SCHLIESSEN
		exit{
			pvStopMonitor(UZV174zu);
		}
	}
	
	//EVLA11:                                                                                       
	// QUERYNR ADIOCM,"*** AD110 WIRD EVAKUIERT ***"                                                
	// QUERYNR ADIOCM,"BITTE VACUUMLEITUNG CHECKEN!"                                                
	// PRINT ADIPNT,"AD110 IN CBX 4%i WIRD EVAKUIERT",CUNIT                                         
	// PRINT ADIPNT,"BITTE VOR-VACUUMLEITUNG CHECKEN!"                                              
	//                                                                                             
	// nach 10 min. muss 0.5 bar unterschritten                                                    
	// werden ,dann wird gepumpt bis 0.1 bar                                                       
	//                                                                                             
	// WAITUNTIL('41PI113':AI_INVL < 0.5) COUNT 600,EVLA11                                          
	// WAITUNTIL('41PI113':AI_INVL < 0.1) COUNT 2000,EVFE11                                         
	state EVLA11{
		option -e;
		entry{
			if(DEBUG_ADS_EIN){ 
				debugNextState = 0; 			pvPut(debugNextState);
				sprintf(debugText, "EVLA11");	pvPut(debugText);
				snlMsgLog("dbg:<EVLA11>");
			}
			// PRINT ADIPNT,"AD110 IN CBX 4%i WIRD EVAKUIERT",CUNIT
			snlMsgLog("AD110 IN CBX 42 WIRD EVAKUIERT! BITTE VOR-VACUUMLEITUNG CHECKEN! ");                                         
			
			pvMonitor(PI113);                                         
		}
		when (efTest(adsStop)) {
		}state ueberwachung
		when((!DEBUG_ADS_EIN || debugNextState!=0) && delay(600)){
		}state EVLA11
		/*'41PI113':AI_INVL < 0.5*/
		when((!DEBUG_ADS_EIN || debugNextState!=0) && PI113  < 0.5){
		} state EVLA11_01BAR
		
		exit{
			pvStopMonitor(PI113);
		}                                         
	}
	
	state EVLA11_01BAR{
		entry{
			if(DEBUG_ADS_EIN){ 
				debugNextState = 0; 				pvPut(debugNextState);
				sprintf(debugText, "EVLA11_01BAR");	pvPut(debugText);
				snlMsgLog("dbg:<EVLA11_01BAR>");
			}
			pvMonitor(PI113);
		}
		when (efTest(adsStop)) {
		}state ueberwachung
		when((!DEBUG_ADS_EIN || debugNextState!=0) && delay(2000)){
		}state EVFE11
		/*'41PI113':AI_INVL < 0.1*/
		when((!DEBUG_ADS_EIN || debugNextState!=0) && PI113 < 0.1){
		}state FLUT11	
		
		exit{
			pvStopMonitor(PI113);
		}
	}
	
	state FLUT11{
		entry{
			if(DEBUG_ADS_EIN){ 
				debugNextState = 0; 			pvPut(debugNextState);
				sprintf(debugText, "FLUT11");	pvPut(debugText);
				snlMsgLog("dbg:<FLUT11>");
			}
			// CLOSE  '41UZV184'
			UZV184CMD = ZU;	pvPut(UZV184CMD);
			pvMonitor(UZV184zu);                                                                            
		}
		when (efTest(adsStop)) {
		}state ueberwachung
		//AND('41UZV184':DEV_STAT,1)=1
		when((!DEBUG_ADS_EIN || debugNextState!=0) && UZV184zu!=0){
		}state FLUT11_UZV194_OEFFNEN
		when((!DEBUG_ADS_EIN || debugNextState!=0) && delay(60)){
			snlMsgLog("<FLUT11>UZV184 - Schliesst nicht");
		}state FLUT11
		exit{
			pvStopMonitor(UZV184zu);                                                                            
		}
	}
	
	state FLUT11_UZV194_OEFFNEN{
		entry{
			if(DEBUG_ADS_EIN){ 
				debugNextState = 0; 							pvPut(debugNextState);
				sprintf(debugText, "FLUT11_UZV194_OEFFNEN");	pvPut(debugText);
				snlMsgLog("dbg:<FLUT11_UZV194_OEFFNEN>");
			}
			// Fluten                                                                                      
            // OPEN '41UZV194'
            UZV194CMD = AUF;	pvPut(UZV194CMD);
            pvMonitor(PI113); 
		}
		when (efTest(adsStop)) {
		}state ueberwachung
		/*'41PI113':AI_INVL > 5.*/
		when((!DEBUG_ADS_EIN || debugNextState!=0) && PI113 > 5.){
		} state FLUT11_UZV194_SCHLIESSEN
		when((!DEBUG_ADS_EIN || debugNextState!=0) && delay(60)){
			snlMsgLog("<FLUT11_UZV194_OEFFNEN>PI113 - Druck ist zu niedrig");
		}state FLUT11_UZV194_OEFFNEN
		exit{
			pvStopMonitor(PI113);
		}
	}
	
	state FLUT11_UZV194_SCHLIESSEN{
		entry{
			if(DEBUG_ADS_EIN){ 
				debugNextState = 0; 							pvPut(debugNextState);
				sprintf(debugText, "FLUT11_UZV194_SCHLIESSEN");	pvPut(debugText);
				snlMsgLog("FLUT11_UZV194_SCHLIESSEN>");
			}
			//  CLOSE '41UZV194'
			UZV194CMD = ZU;	pvPut(UZV194CMD);
			pvMonitor(UZV194zu);                                                                             
 		}
		when (efTest(adsStop)) {
		}state ueberwachung
 		//AND('41UZV194':DEV_STAT,1)=1
 		when((!DEBUG_ADS_EIN || debugNextState!=0) && UZV194zu!=0){
 		} state FLUT11_UZV193_SCHLIESSEN
 		when((!DEBUG_ADS_EIN || debugNextState!=0) && delay(60)){
			snlMsgLog("<FLUT11_UZV194_SCHLIESSEN>UZV194 - schliesst nicht");
		}state FLUT11_UZV194_SCHLIESSEN
 		
 		exit{
 			pvStopMonitor(UZV194zu);
 		}    
	}
	
	state FLUT11_UZV193_SCHLIESSEN{
		entry{
			if(DEBUG_ADS_EIN){ 
				debugNextState = 0; 							pvPut(debugNextState);
				sprintf(debugText, "FLUT11_UZV193_SCHLIESSEN");	pvPut(debugText);
				snlMsgLog("FLUT11_UZV193_SCHLIESSEN>");
			}
			// Fertig                                                                                      
			// CLOSE '41UZV193'
			UZV193CMD = ZU;	pvPut(UZV193CMD);
			pvMonitor(UZV193zu);
		}
		when (efTest(adsStop)) {
		}state ueberwachung
		//AND('41UZV193':DEV_STAT,1)=1
		when((!DEBUG_ADS_EIN || debugNextState!=0) && UZV193zu!=0){
			// QUERYNR ADIOCM,"AD110 REGENERIERT UND MIT 5 BAR GEFUELLT"                                    
			snlMsgLog("AD110 REGENERIERT UND MIT 5 BAR GEFUELLT");                                    
 			// LET AD2BTR=0 
 			AD2BTR = BETRIEBSBEREIT;		pvPut(AD2BTR); 
		}state ueberwachung
		when((!DEBUG_ADS_EIN || debugNextState!=0) && delay(60)){
			snlMsgLog("<FLUT11_UZV193_SCHLIESSEN>UZV193 - schliesst nicht");
		}state FLUT11_UZV193_SCHLIESSEN
		
		exit{
			pvStopMonitor(UZV193zu);
		}  
	}
	
	//EVFE11:                                                                                       
 	//LET ANTW1 = 0                                                                                
 	//QUERYNR ADIOCM,"AD110 VAKUUM 0.1 BAR NICHT ERREICHT !"                                       
 	//QUERY ADIOCM,"DIE REGENERATION WIEDERHOLEN ? (J=1)",ANTW1                                    
    //                                                                                     
 	//IF (ANTW1 = 1) THEN                                                                          
 	//BEGIN                                                                                        
 	//	CLOSE  '41UZV184'                                                                            
 	//	WAITUNTIL(AND('41UZV184':DEV_STAT,1)=1)                                                      
 	//	OPEN '41UZV194'                                                                              
 	//	WAITUNTIL('41PI111':AI_INVL > 5.)                                                            
 	//	CLOSE '41UZV194'                                                                             
 	//	WAITUNTIL(AND('41UZV194':DEV_STAT,1)=1)                                                      
 	//	GOTO REG2 // hier muss zu DENT2 gegangen werden                                                                                   
 	//	END                                                                                          
 	//ELSE                                                                                         
 	//GOTO FLUT11
	state EVFE11{
		entry{
			if(DEBUG_ADS_EIN){ 
				debugNextState = 0; 			pvPut(debugNextState);
				sprintf(debugText, "EVFE11");	pvPut(debugText);
				snlMsgLog("dbg:<EVFE11>");
			}
			// LET ANTW1 = 0                                                                                
			AD_OPERATOR_ANT_JA=0;				pvPut(AD_OPERATOR_ANT_JA,SYNC);
			AD_OPERATOR_ANT_NEIN=0;			pvPut(AD_OPERATOR_ANT_NEIN,SYNC);
			pvMonitor(AD_OPERATOR_ANT_JA);
			pvMonitor(AD_OPERATOR_ANT_NEIN);
			// QUERYNR ADIOCM,"AD110 VAKUUM 0.1 BAR NICHT ERREICHT !"                                       
			// QUERY ADIOCM,"DIE REGENERATION WIEDERHOLEN ? (J=1)",ANTW1
			snlMsgLog("AD110 VAKUUM 0.1 BAR NICHT ERREICHT ! DIE REGENERATION WIEDERHOLEN ?");
			sprintf(AD_OPERATOR_ANT_JA_TEXT, "Regeneration Wiederholen ?");		pvPut(AD_OPERATOR_ANT_JA_TEXT);  
		}
		when (efTest(adsStop)) {
		}state ueberwachung
		// Ja (Regeneration Wiederholen)
		when((!DEBUG_ADS_EIN || debugNextState!=0) && AD_OPERATOR_ANT_JA){
		} state EVFE11_UZV184_SCHLIESSEN
		// Nein (Weiter)
		when((!DEBUG_ADS_EIN || debugNextState!=0) && AD_OPERATOR_ANT_NEIN){
		}state FLUT11
		exit{
			strcpy(AD_OPERATOR_ANT_JA_TEXT, "");		pvPut(AD_OPERATOR_ANT_JA_TEXT);
			pvStopMonitor(AD_OPERATOR_ANT_JA);  
		   	pvStopMonitor(AD_OPERATOR_ANT_NEIN);
		}
	}
	
	state EVFE11_UZV184_SCHLIESSEN{
		entry{
			if(DEBUG_ADS_EIN){ 
				debugNextState = 0; 							pvPut(debugNextState);
				sprintf(debugText, "EVFE11_UZV184_SCHLIESSEN");	pvPut(debugText);
				snlMsgLog("dbg:<EVFE11_UZV184_SCHLIESSEN>");
			}
			UZV184CMD = ZU;	pvPut(UZV184CMD);
			pvMonitor(UZV184zu);
		}
		when (efTest(adsStop)) {
		}state ueberwachung
		when((!DEBUG_ADS_EIN || debugNextState!=0) && UZV184zu!=0){
		} state EVFE11_UZV194_OEFFNEN
		when((!DEBUG_ADS_EIN || debugNextState!=0) && delay(60)){
			snlMsgLog("<EVFE11_UZV184_SCHLIESSEN>UZV184 - schliesst nicht");
		}state EVFE11_UZV184_SCHLIESSEN
		exit{
			pvStopMonitor(UZV184zu);
		}
	}
	
	state EVFE11_UZV194_OEFFNEN {	 
		entry{
			if(DEBUG_ADS_EIN){ 
				debugNextState = 0; 							pvPut(debugNextState);
				sprintf(debugText, "EVFE11_UZV194_OEFFNEN");	pvPut(debugText);
				snlMsgLog("dbg:<EVFE11_UZV194_OEFFNEN>");
			}
			UZV194CMD = AUF;	pvPut(UZV194CMD);
			pvMonitor(PI113);
		}
		when (efTest(adsStop)) {
		}state ueberwachung
		when((!DEBUG_ADS_EIN || debugNextState!=0) && PI113 > 5.){
		} state EVFE11_UZV194_SCHLIESSEN
		when((!DEBUG_ADS_EIN || debugNextState!=0) && delay(60)){
			snlMsgLog("<EVFE11_UZV194_OEFFNEN>PI113 - Druck zu niedrig");
		}state EVFE11_UZV194_OEFFNEN
		
		exit{
			pvStopMonitor(PI113);
		}
	}
	
	state EVFE11_UZV194_SCHLIESSEN {
		entry{
			if(DEBUG_ADS_EIN){ 
				debugNextState = 0; 							pvPut(debugNextState);
				sprintf(debugText, "EVFE11_UZV194_SCHLIESSEN");	pvPut(debugText);
				snlMsgLog("dbg:<EVFE11_UZV194_SCHLIESSEN>");
			}
			UZV194CMD = ZU;	pvPut(UZV194CMD);
			pvMonitor(UZV194zu);
		}
		when (efTest(adsStop)) {
		}state ueberwachung
		when((!DEBUG_ADS_EIN || debugNextState!=0) && UZV194zu!=0){
		} state DENT2
		when((!DEBUG_ADS_EIN || debugNextState!=0) && delay(60)){
			snlMsgLog("<EVFE11_UZV194_SCHLIESSEN>UZV194 - schliesst nicht");
		}state EVFE11_UZV194_SCHLIESSEN
		exit{
			pvStopMonitor(UZV194zu);
		}
	}

	// ---------------------------- Wechsel AD210 fuer AD200 -------------------------------------------------------------------------------
	// CHNG3:                                                                                       
	// Erst alle Regenerationsventile schliessen ( definierter Zustand)                            
	// Kaltfahren AD200                                                                            
	state CHNG3{
		entry{
			if(DEBUG_ADS_EIN){ 
				debugNextState = 0; 	pvPut(debugNextState);
				sprintf(debugText, "CHNG3");	pvPut(debugText);
				snlMsgLog("dbg:<CHNG3>");
			}
			// Erst alle Regenerationsventile schliessen ( definierter Zustand)                            
	 		UZV274CMD = ZU;	 		pvPut(UZV274CMD);                                                                             
	 		UZV284CMD = ZU;	 		pvPut(UZV284CMD);                                                                             
			UZV272CMD = ZU;			pvPut(UZV272CMD);                                                                             
			UZV294CMD = ZU;			pvPut(UZV294CMD);                                                                             
			UZV293CMD = ZU;			pvPut(UZV293CMD);                                                                             
			UZV292CMD = ZU;			pvPut(UZV292CMD);                                                                             

			UZV273CMD = ZU;			pvPut(UZV273CMD);
			UZV173CMD = ZU;			pvPut(UZV173CMD);                                                                             
			UZV183CMD = ZU;			pvPut(UZV183CMD);                                                                             
			UZV182CMD = ZU;			pvPut(UZV182CMD);                                                                             
			UZV283CMD = ZU;			pvPut(UZV283CMD);                                                                             
			UZV282CMD = ZU;			pvPut(UZV282CMD);
			
			// Kaltfahren AD210                                                                            
	 		// IF ('41HC204':CB_OTVL:1 < 10.0) THEN                                                         
	 		pvGet(CV204OVAL);
	 		if(CV204OVAL < 10.0){
		 		// PUTMANL '41HC204'
		 		// PUTOUT '41HC204',10                                                                          
		 		pvGet(CV204SCAN,SYNC);	 		strcpy(ovalZwischenSpeicher1,CV204SCAN); 
		 		CV204OMSL = SUPERVISORY;		pvPut(CV204OMSL, SYNC);
		 		CV204OROC = 1.0;				pvPut(CV204OROC, SYNC);
		 		CV204 = 10;						pvPut(CV204, SYNC);
		 		sprintf(CV204SCAN, "1 second");	pvPut(CV204SCAN, SYNC);
			}	 		
			pvMonitor(PI201);
			pvMonitor(PI203);
		}
		when (efTest(adsStop)) {
		}state ueberwachung
	 		// Druck angleichen                                                                            
	 		// WAITUNTIL('41PI203':AI_INVL > '41PI201':AI_INVL-1.5 )
		when((!DEBUG_ADS_EIN || debugNextState!=0) && PI203 > (PI201-1.5)){
			// PUTMANL nicht vergessen?
			// PUTOUT '41HC204',100
			                                                                         
		 	pvGet(CV204SCAN,SYNC);	 		strcpy(ovalZwischenSpeicher1,CV204SCAN); 
	 		CV204OMSL = SUPERVISORY;		pvPut(CV204OMSL, SYNC);
	 		CV204OROC = 1.0;				pvPut(CV204OROC, SYNC);
	 		CV204 = 100;					pvPut(CV204, SYNC);
	 		sprintf(CV204SCAN, "1 second");	pvPut(CV204SCAN, SYNC);
		}state CHNG3_HEIZUNG_EINSCHALTEN
		
		when((!DEBUG_ADS_EIN || debugNextState!=0) && delay(60)){
			snlMsgLog("<CHNG3>Druckangleich PI201 / PI203 nicht ok!");
		}state CHNG3
		exit{
			pvStopMonitor(PI201);
			pvStopMonitor(PI203);
			strcpy(CV204SCAN, ovalZwischenSpeicher1);	pvPut(CV204SCAN, SYNC);
		}
	}
	
	state CHNG3_HEIZUNG_EINSCHALTEN{
		entry{
			if(DEBUG_ADS_EIN){ 
				debugNextState = 0; 								pvPut(debugNextState);
				sprintf(debugText, "CHNG3_HEIZUNG_EINSCHALTEN");	pvPut(debugText);
				snlMsgLog("dbg:<CHNG3_HEIZUNG_EINSCHALTEN>");
			}
	 		// Heizung eingeschalten                                                                      
		 	// OPEN '41HS130'         
		 	HS130CMD = EIN;	 		pvPut(HS130CMD);
		 	pvMonitor(HS130ein);
		}
		when (efTest(adsStop)) {
		}state ueberwachung
		//AND('41HS130':DEV_STAT,2)=2
		when((!DEBUG_ADS_EIN || debugNextState!=0) && HS130ein!=0){
		}state CHNG3_UZV293_OEFFNEN
		when((!DEBUG_ADS_EIN || debugNextState!=0) && delay(60)){
			snlMsgLog("<CHNG3_HEIZUNG_EINSCHALTEN>HS130 - keine Rückmeldung");
		}state CHNG3_HEIZUNG_EINSCHALTEN
		
		exit{	
		 	pvStopMonitor(HS130ein);
	 	}
	}
	
	state CHNG3_UZV293_OEFFNEN{
		entry{
			if(DEBUG_ADS_EIN){ 
				debugNextState = 0; 						pvPut(debugNextState);
				sprintf(debugText, "CHNG3_UZV293_OEFFNEN");	pvPut(debugText);
				snlMsgLog("dbg:<CHNG3_UZV293_OEFFNEN>");
			}
			//OPEN '41UZV293'  
		 	UZV293CMD = AUF;	 		pvPut(UZV293CMD);
		 	pvMonitor(UZV293auf);
		}
		when (efTest(adsStop)) {
		}state ueberwachung
		//AND('41UZV293':DEV_STAT,2)=2
		when((!DEBUG_ADS_EIN || debugNextState!=0) && UZV293auf!=0){
		}state CHNG3_UZV272_OEFFNEN
		when((!DEBUG_ADS_EIN || debugNextState!=0) && delay(60)){
			snlMsgLog("<CHNG3_UZV293_OEFFNEN>UZV293 - öffnet nicht!");
		}state CHNG3_UZV293_OEFFNEN
		
		exit{
		 	pvStopMonitor(UZV293auf);
	 	}
	}
	
	state CHNG3_UZV272_OEFFNEN{
		entry{
			if(DEBUG_ADS_EIN){ 
				debugNextState = 0; 						pvPut(debugNextState);
				sprintf(debugText, "CHNG3_UZV272_OEFFNEN");	pvPut(debugText);
				snlMsgLog("dbg:<CHNG3_UZV272_OEFFNEN>");
			}
			// OPEN '41UZV272'
		 	UZV272CMD = AUF;	 		pvPut(UZV272CMD);
		 	pvMonitor(UZV272auf);
		}
		when (efTest(adsStop)) {
		}state ueberwachung
		//AND('41UZV272':DEV_STAT,2)=2
		when((!DEBUG_ADS_EIN || debugNextState!=0) && UZV272auf!=0){
		}state CHNG3_AD210_KALTFAHREN
		when((!DEBUG_ADS_EIN || debugNextState!=0) && delay(60)){
			snlMsgLog("<CHNG3_UZV272_OEFFNEN>UZV272 - öffnet nicht!");
		}state CHNG3_UZV272_OEFFNEN
		
		exit{
		 	pvStopMonitor(UZV272auf);
	 	}
	}
	
	state CHNG3_AD210_KALTFAHREN{
		entry{
			if(DEBUG_ADS_EIN){ 
				debugNextState = 0; 							pvPut(debugNextState);
				sprintf(debugText, "CHNG3_AD210_KALTFAHREN");	pvPut(debugText);
				snlMsgLog("dbg:<CHNG3_AD210_KALTFAHREN>");
			}
			pvMonitor(TI202);
			pvMonitor(TI200);
		}
		when (efTest(adsStop)) {
		}state ueberwachung
		//'41TI202':AI_INVL < '41TI200':AI_INVL+5.0
		when((!DEBUG_ADS_EIN || debugNextState!=0) && TI202 < (TI200+5.0)){
		}state CHNG3_UZV293_SCHLIESSEN
		when((!DEBUG_ADS_EIN || debugNextState!=0) && delay(60)){
			snlMsgLog("<CHNG3_AD210_KALTFAHREN>Kaltfahren nicht erfolgreich! TI202 zu hoch.");
		} state CHNG3_AD210_KALTFAHREN
		exit{
			pvStopMonitor(TI202);
			pvStopMonitor(TI200);
		}
	}
	
	state CHNG3_UZV293_SCHLIESSEN{
		entry{
			if(DEBUG_ADS_EIN){ 
				debugNextState = 0; 							pvPut(debugNextState);
				sprintf(debugText, "CHNG3_UZV293_SCHLIESSEN");	pvPut(debugText);
				snlMsgLog("dbg:<CHNG3_UZV293_SCHLIESSEN>");
			}
		 	// Nun ist AD110 kalt                                                                          
			// CLOSE '41UZV293'
			UZV293CMD = ZU;	 		pvPut(UZV293CMD);
		 	pvMonitor(UZV293zu);
		}
		
		when (efTest(adsStop)) {
		}state ueberwachung
		//(AND('41UZV293':DEV_STAT,1)=1
		when((!DEBUG_ADS_EIN || debugNextState!=0) && UZV293zu!=0){
		} state CHNG3_UZV272_SCHLIESSEN
		when((!DEBUG_ADS_EIN || debugNextState!=0) && delay(60)){
			snlMsgLog("<CHNG3_UZV293_SCHLIESSEN>UZV293 - schliesst nicht!");
		}state CHNG3_UZV293_SCHLIESSEN
		exit{
		 	pvStopMonitor(UZV293zu);
	 	}
	} 
	
	state CHNG3_UZV272_SCHLIESSEN{
		entry{
			if(DEBUG_ADS_EIN){ 
				debugNextState = 0; 							pvPut(debugNextState);
				sprintf(debugText, "CHNG3_UZV272_SCHLIESSEN");	pvPut(debugText);
				snlMsgLog("dbg:<CHNG3_UZV272_SCHLIESSEN>");
			}
			// CLOSE '41UZV272'                                                                             
			UZV272CMD = ZU;	 		pvPut(UZV272CMD);
		 	pvMonitor(UZV272zu);
		}
		when (efTest(adsStop)) {
		}state ueberwachung
		//AND('41UZV272':DEV_STAT,1)=1
		when((!DEBUG_ADS_EIN || debugNextState!=0) && UZV272zu!=0){
		} state FLA31
		when((!DEBUG_ADS_EIN || debugNextState!=0) && delay(60)){
			snlMsgLog("<CHNG3_UZV272_SCHLIESSEN>UZV272 - schliesst nicht!");
		}state CHNG3_UZV272_SCHLIESSEN
		exit{
		 	pvStopMonitor(UZV272zu);
	 	}
	}         
	
	state FLA31{
		option -e; /* führt entry immer wieder aus */
		entry{
			if(DEBUG_ADS_EIN){ 
				debugNextState = 0; 			pvPut(debugNextState);
				sprintf(debugText, "FLA31");	pvPut(debugText);
				snlMsgLog("dbg:<FLA31>");
			}
			//PUTMANL '41HC204'                                                                            
 			//PUTOUT '41HC204',100
			pvGet(CV204SCAN,SYNC);	 		strcpy(ovalZwischenSpeicher1,CV204SCAN); 
 			CV204OMSL = SUPERVISORY;		pvPut(CV204OMSL, SYNC);
	 		CV204OROC = 1.0;				pvPut(CV204OROC, SYNC);
	 		CV204 = 100;					pvPut(CV204, SYNC);
	 		sprintf(CV204SCAN, "1 second");	pvPut(CV204SCAN, SYNC);
 			                                                                         
 			//PUTMANL '41HC203'                                                                            
 			//PUTOUT '41HC203',100
			pvGet(CV203SCAN,SYNC);	 		strcpy(ovalZwischenSpeicher2,CV203SCAN); 
 			CV203OMSL = SUPERVISORY;		pvPut(CV203OMSL, SYNC);
	 		CV203OROC = 1.0;				pvPut(CV203OROC, SYNC);
	 		CV203 = 100;					pvPut(CV203, SYNC);
	 		sprintf(CV203SCAN, "1 second");	pvPut(CV203SCAN, SYNC);
	 		
	 		pvMonitor(CV203auf);
	 		pvMonitor(CV204auf);
	 		pvMonitor(CV203OVAL);
	 		pvMonitor(CV204OVAL);
		}
		when (efTest(adsStop)) {
		}state ueberwachung
		/*
	 	WAITUNTIL(AND('41CV203EN':DEV_STAT,2)=2)COUNT 120,MSG21                                      
 		WAITUNTIL(AND('41CV204EN':DEV_STAT,2)=2)COUNT 120,MSG21                                      
		*/	
		when((!DEBUG_ADS_EIN || debugNextState!=0) && (CV203auf!=0) && (CV203OVAL>99) && (CV204auf!=0) && (CV204OVAL>99)){ 
		}state FLA32
		
		when((!DEBUG_ADS_EIN || debugNextState!=0) && delay(120)){
			if(CV203auf==0){
				snlMsgLog("Eintrittsventil 42CV203 am AD210 in CBX42 öffnet nicht");
			}
			if(CV204auf==0){
				snlMsgLog("Austrittsventil 42CV204 am AD210 in CBX42 öffnet nicht");
			}
		} state FLA31
		
		exit{
	 		pvStopMonitor(CV203auf);
	 		pvStopMonitor(CV204auf);
	 		pvStopMonitor(CV203OVAL);
	 		pvStopMonitor(CV204OVAL);
	 		strcpy(CV204SCAN, ovalZwischenSpeicher1);	pvPut(CV204SCAN, SYNC);
	 		strcpy(CV203SCAN, ovalZwischenSpeicher2);	pvPut(CV203SCAN, SYNC);
 		}
	}
	
	state FLA32{
		option -e;/* führt entry immer wieder aus */
		entry{
			if(DEBUG_ADS_EIN){ 
				debugNextState = 0; 			pvPut(debugNextState);
				sprintf(debugText, "FLA32");	pvPut(debugText);
				snlMsgLog("dbg:<FLA32>");
			}
			// PUTMANL '41HC201'                                                                            
 			// PUTOUT '41HC201',0
 			pvGet(CV201SCAN,SYNC);	 		strcpy(ovalZwischenSpeicher1,CV201SCAN);
 			CV201OMSL = SUPERVISORY;		pvPut(CV201OMSL, SYNC);
	 		CV201OROC = 1.0;				pvPut(CV201OROC, SYNC);
	 		CV201 = 0;						pvPut(CV201, SYNC);                                                                           
	 		sprintf(CV201SCAN, "1 second");	pvPut(CV201SCAN, SYNC);
 			// PUTMANL '41HC202'                                                                            
 			// PUTOUT '41HC202',0
 			pvGet(CV202SCAN,SYNC);	 		strcpy(ovalZwischenSpeicher2,CV202SCAN);
 			CV202OMSL = SUPERVISORY;		pvPut(CV202OMSL, SYNC);
	 		CV202OROC = 1.0;				pvPut(CV202OROC, SYNC);
	 		CV202 = 0;						pvPut(CV202, SYNC);
	 		sprintf(CV202SCAN, "1 second");	pvPut(CV202SCAN, SYNC);
	 		
	 		pvMonitor(CV201zu);                                                                           
	 		pvMonitor(CV202zu);
	 		pvMonitor(CV202OVAL);
	 		pvMonitor(CV201OVAL);

		}
		when (efTest(adsStop)) {
		}state ueberwachung
		// WAITUNTIL(AND('41CV202EN':DEV_STAT,1)=1)COUNT 120,MSG12                                      
 		// WAITUNTIL(AND('41CV201EN':DEV_STAT,1)=1)COUNT 120,MSG12
 		when((!DEBUG_ADS_EIN || debugNextState!=0) && (CV202zu!=0) && (CV202OVAL<1) && (CV201zu!=0) && (CV201OVAL<1)){
 			// ADSORBER 210 IM PROZESS ADSORBER 200 AUS DEM PROZESS                                        
 			// LET AD3BTR=2 // setze Adsorber 3 auf Regeneration
 			AD3BTR = REGENERIERT;
 			pvPut(AD3BTR);
 		} state ueberwachung         
	
		when((!DEBUG_ADS_EIN || debugNextState!=0) && delay(120)){
			if(CV201zu == 0){
				snlMsgLog("Eintrittsventil 42CV201 am AD210 in CBX42 schließt nicht");
			} 
			if(CV202zu == 0){
				snlMsgLog("Austrittsventil 42CV202 am AD210 in CBX42 schließt nicht");
			}
		} state	FLA32
		exit{
			pvStopMonitor(CV201zu);                                                                           
	 		pvStopMonitor(CV202zu);                                                                           
	 		pvStopMonitor(CV202OVAL);
	 		pvStopMonitor(CV201OVAL);
	 		strcpy(CV201SCAN, ovalZwischenSpeicher1);	pvPut(CV201SCAN, SYNC);
	 		strcpy(CV202SCAN, ovalZwischenSpeicher2);	pvPut(CV202SCAN, SYNC);
		}
		
	}
	
	//--------- REGENERATION FUER AD200 --------                                                  
 	// OPEN '41UZV292'                                                                              
 	// WAITUNTIL(AND('41UZV292':DEV_STAT,2)=2)                                                      
	state REG200{
		entry{
			if(DEBUG_ADS_EIN){ 
				debugNextState = 0; 			pvPut(debugNextState);
				sprintf(debugText, "REG200");	pvPut(debugText);
				snlMsgLog("dbg:<REG200>");
			}
 			//OPEN '41UZV292'
 			UZV292CMD = AUF;	pvPut(UZV292CMD);
 			snlMsgLog("<REG200>Regeneration für AD200 Startet");
 			pvMonitor(UZV292auf);                                                                              
		}
		when (efTest(adsStop)) {
		}state ueberwachung
		//'41UZV292':DEV_STAT,2)=2
		when((!DEBUG_ADS_EIN || debugNextState!=0) && UZV292auf!=0){
		} state DENT3
		when((!DEBUG_ADS_EIN || debugNextState!=0) && delay(60)){
			snlMsgLog("<REG200>UZV292 - öffnet nicht!");
		}state REG200
		exit{
			pvStopMonitor(UZV292auf);
		}
	}
	
	//DENT3:                                                                                        
	//OPEN '41UZV272'                                                                              
	//WAITUNTIL('41PI201':AI_INVL < 1.2)                                                           
	//CLOSE '41UZV272'                                                                             
	//WAITUNTIL(AND('41UZV272':DEV_STAT,1)=1 )                                                     
	//RETURN                                                                                       
	state DENT3{
		entry{
			if(DEBUG_ADS_EIN){ 
				debugNextState = 0; 			pvPut(debugNextState);
				sprintf(debugText, "DENT3");	pvPut(debugText);
				snlMsgLog("dbg:<DENT3>");
			}
	 		// OPEN '41UZV272'
	 		UZV272CMD = AUF; 	pvPut(UZV272CMD);
 			pvMonitor(PI201);   
 		}
		when (efTest(adsStop)) {
		}state ueberwachung
 		/*'41PI201':AI_INVL < 1.2*/
 		when((!DEBUG_ADS_EIN || debugNextState!=0) && PI201 < 1.2){
 		} state DENT3_UZV272_SCHLIESSEN
 		when((!DEBUG_ADS_EIN || debugNextState!=0) && delay(600)){
			snlMsgLog("<DENT3>PI201 - Druck zu hoch!");
		}state DENT3
 		exit{
 			pvStopMonitor(PI201);
 		}
	}

	state DENT3_UZV272_SCHLIESSEN{
		entry{
			if(DEBUG_ADS_EIN){ 
				debugNextState = 0; 							pvPut(debugNextState);
				sprintf(debugText, "DENT3_UZV272_SCHLIESSEN");	pvPut(debugText);
				snlMsgLog("dbg:<DENT3_UZV272_SCHLIESSEN>");
			}
	 		//CLOSE '41UZV272'
 			UZV272CMD = ZU; 	pvPut(UZV272CMD);
 			
 			pvMonitor(UZV272zu);   
	 		
 		}                         
		when (efTest(adsStop)) {
		}state ueberwachung
 		//AND('41UZV272':DEV_STAT,1)=1                                                    
		when((!DEBUG_ADS_EIN || debugNextState!=0) && UZV272zu!=0){
		} state REG3
		when((!DEBUG_ADS_EIN || debugNextState!=0) && delay(60)){
			snlMsgLog("<DENT3_UZV272_SCHLIESSEN>UZV272 - schliesst nicht!");
		}state DENT3_UZV272_SCHLIESSEN
		exit{
			pvStopMonitor(UZV272zu);
		}
	}
	
	state REG3{
		entry{
			if(DEBUG_ADS_EIN){ 
				debugNextState = 0; 		pvPut(debugNextState);
				sprintf(debugText, "REG3");	pvPut(debugText);
				snlMsgLog("dbg:<REG3>");
			}
			// OPEN '41UZV274'                                                                              
 			UZV274CMD = AUF; 	pvPut(UZV274CMD);
 			// OPEN '41UZV282'    
 			UZV282CMD = AUF; 	pvPut(UZV282CMD);
 			pvMonitor(UZV274auf);
 			pvMonitor(UZV282auf);
		}
		when (efTest(adsStop)) {
		}state ueberwachung
		//AND('41UZV282':DEV_STAT,2)=2 
		when((!DEBUG_ADS_EIN || debugNextState!=0) && UZV282auf!=0 && UZV274auf!=0){
		} state REG3_UZV273_OEFFNEN
		when((!DEBUG_ADS_EIN || debugNextState!=0) && delay(60)){
			if(UZV274auf==0){
				snlMsgLog("<REG3>UZV274 - öffnet nicht!");
			}
			if(UZV282auf==0){
				snlMsgLog("<REG3>UZV282 - öffnet nicht!");
			}
		}state REG3
		exit{
 			pvStopMonitor(UZV274auf);
 			pvStopMonitor(UZV282auf);
		}
	}	                           
	
	state REG3_UZV273_OEFFNEN{
		entry{
			if(DEBUG_ADS_EIN){ 
				debugNextState = 0; 						pvPut(debugNextState);
				sprintf(debugText, "REG3_UZV273_OEFFNEN");	pvPut(debugText);
				snlMsgLog("dbg:<REG3_UZV273_OEFFNEN>");
			}
			// OPEN '41UZV273'                                                                              
 			UZV273CMD = AUF; 	pvPut(UZV273CMD);
 			pvMonitor(UZV273auf);
		}                          
		when (efTest(adsStop)) {
		}state ueberwachung
		//AND('41UZV273':DEV_STAT,2)=2
		when((!DEBUG_ADS_EIN || debugNextState!=0) && UZV273auf!=0){
		} state REG3_HS151_EINSCHALTEN
		when((!DEBUG_ADS_EIN || debugNextState!=0) && delay(60)){
			snlMsgLog("<REG3_UZV273_OEFFNEN>UZV273 - öffnet nicht!");
		}state REG3_UZV273_OEFFNEN
		exit{
 			pvStopMonitor(UZV273auf);
		}
	}
	
	state REG3_HS151_EINSCHALTEN{
		option -e;
		entry{
			if(DEBUG_ADS_EIN){ 
				debugNextState = 0; 							pvPut(debugNextState);
				sprintf(debugText, "REG3_HS151_EINSCHALTEN");	pvPut(debugText);
				snlMsgLog("dbg:<REG3_HS151_EINSCHALTEN>");
			}
			// 	START '41HS151'
			HS151CMD = EIN; 	pvPut(HS151CMD);
 			pvMonitor(HS151ein);
 			pvMonitor(TI200);
		}
		when (efTest(adsStop)) {
		}state ueberwachung
//		delay(30) && //AND('41HS151':DEV_STAT,2)!=2*/
		when((!DEBUG_ADS_EIN || debugNextState!=0) && delay(30) && HS151ein==0){
				// QUERYNR ADIOCM,"STOERUNG GEBLAESE REGENERATION AD200"
				snlMsgLog("<REG3_HS151_EINSCHALTEN>HS151 - keine Rückmeldung");                                        
				// STOP '41HS151'                                                                               
				HS151CMD = AUS; 	pvPut(HS151CMD);
				// CLOSE '41UZV274'                                                                             
				UZV274CMD = ZU; 	pvPut(UZV274CMD);
		}state DENT3
//		delay(30) && /*'41TI200':AI_INVL > 250.*/
		when((!DEBUG_ADS_EIN || debugNextState!=0) && delay(30) && (TI200 > 250.)){
		}state REG3_GEBLAESE_AUS
		when((!DEBUG_ADS_EIN || debugNextState!=0) && delay(7200)){
			snlMsgLog("<REG3_HS151_EINSCHALTEN>TI200 - Temperatur nicht erreicht!");
		}state REG3_HS151_EINSCHALTEN
		
		exit{
 			pvStopMonitor(HS151ein);
 			pvStopMonitor(TI200);
		}
	}
	
	state REG3_GEBLAESE_AUS{
		entry{
			if(DEBUG_ADS_EIN){ 
				debugNextState = 0; 						pvPut(debugNextState);
				sprintf(debugText, "REG3_GEBLAESE_AUS");	pvPut(debugText);
				snlMsgLog("dbg:<REG3_GEBLAESE_AUS>");
			}
			// STOP '41HS151'                
			HS151CMD = AUS; 	pvPut(HS151CMD);                                                               
 			// CLOSE '41UZV273'                                                                             
			UZV273CMD = ZU; 	pvPut(UZV273CMD);                                                               
			// CLOSE '41UZV282'
			UZV282CMD = ZU; 	pvPut(UZV282CMD);
			
			pvMonitor(UZV273zu);                                                               
			pvMonitor(UZV282zu);
			pvMonitor(HS151aus);                                                               
		}
		when (efTest(adsStop)) {
		}state ueberwachung
		//AND('41UZV273':DEV_STAT,1)=1 & AND('41UZV282':DEV_STAT,1)=1
		when((!DEBUG_ADS_EIN || debugNextState!=0) && (UZV273zu!=0) && (UZV282zu!=0) && (HS151aus!=0)){
		} state REG3_UZV274_SCHLIESSEN
		when((!DEBUG_ADS_EIN || debugNextState!=0) && delay(60)){
			if(UZV273zu==0){
				snlMsgLog("<REG3_GEBLAESE_AUS>UZV273 - Schliesst nicht");
			}
			if(UZV282zu==0){
				snlMsgLog("<REG3_GEBLAESE_AUS>UZV282 - Schliesst nicht");
			}
			if(HS151aus==0){
				snlMsgLog("<REG3_GEBLAESE_AUS>HS151  - Gebläse schaltet nicht ab");
			}
		}state REG3_GEBLAESE_AUS
		
		exit{
			pvStopMonitor(UZV273zu);                                                               
			pvStopMonitor(UZV282zu);   
			pvStopMonitor(HS151aus);                                                            
		}
	}			
	
	state REG3_UZV274_SCHLIESSEN{
		entry{
			if(DEBUG_ADS_EIN){ 
				debugNextState = 0; 							pvPut(debugNextState);
				sprintf(debugText, "REG3_UZV274_SCHLIESSEN");	pvPut(debugText);
				snlMsgLog("dbg:<REG3_UZV274_SCHLIESSEN>");
			}
			// CLOSE '41UZV274'
			UZV274CMD = ZU; 	pvPut(UZV274CMD);
			pvMonitor(UZV274zu); 
		}
		when (efTest(adsStop)) {
		}state ueberwachung
		//AND('41UZV274':DEV_STAT,1)=1)
		when((!DEBUG_ADS_EIN || debugNextState!=0) && UZV274zu!=0){
		 	// OPEN '41UZV284'
			UZV284CMD = AUF; 	pvPut(UZV284CMD); 
		}state EVLA20 
		when((!DEBUG_ADS_EIN || debugNextState!=0) && delay(60)){
			snlMsgLog("<REG3_UZV274_SCHLIESSEN>UZV274zu - Schliesst nicht");
		}state REG3_UZV274_SCHLIESSEN
		exit{
			pvStopMonitor(UZV274zu);
		}
	}
	
	//EVLA20:                                                                                       
	// QUERYNR ADIOCM,"*** AD200 WIRD EVAKUIERT ***"                                                
	// QUERYNR ADIOCM,"BITTE VACUUMLEITUNG CHECKEN!"                                                
	// nach 10 min. muss 0.5 bar unterschritten                                                    
	// werden ,dann wird gepumpt bis 0.1 bar                                                       
	//                                                                                             
	// WAITUNTIL('41PI201':AI_INVL < 0.5) COUNT 600,EVLA20                                          
	// WAITUNTIL('41PI201':AI_INVL < 0.1) COUNT 2000,EVFE20                                         
	state EVLA20{
		option -e;
		entry{
			if(DEBUG_ADS_EIN){ 
				debugNextState = 0; 			pvPut(debugNextState);
				sprintf(debugText, "EVLA20");	pvPut(debugText);
				snlMsgLog("dbg:<EVLA20>");
			}
			// QUERYNR ADIOCM,"*** AD200 WIRD EVAKUIERT ***"                                                
			// QUERYNR ADIOCM,"BITTE VACUUMLEITUNG CHECKEN!"                                                
			snlMsgLog("AD200 IN CBX 42 WIRD EVAKUIERT! BITTE VOR-VACUUMLEITUNG CHECKEN! ");                                         
			pvMonitor(PI201);                                         
		}
		when (efTest(adsStop)) {
		}state ueberwachung
		when((!DEBUG_ADS_EIN || debugNextState!=0) && delay(600)){
		}state EVLA20
		
		/*'41PI201':AI_INVL < 0.5*/
		when((!DEBUG_ADS_EIN || debugNextState!=0) && PI201  < 0.5){
		} state EVLA20_01BAR
		
		exit{
			pvStopMonitor(PI201);
		}                                         
	}
	
	state EVLA20_01BAR{
		entry{
			if(DEBUG_ADS_EIN){ 
				debugNextState = 0; 				pvPut(debugNextState);
				sprintf(debugText, "EVLA20_01BAR");	pvPut(debugText);
				snlMsgLog("dbg:<EVLA20_01BAR>");
			}
			pvMonitor(PI201);
		}
		when (efTest(adsStop)) {
		}state ueberwachung
		when((!DEBUG_ADS_EIN || debugNextState!=0) && delay(2000)){
		}state EVFE20
		/*'41PI201':AI_INVL < 0.1*/
		when((!DEBUG_ADS_EIN || debugNextState!=0) && PI201 < 0.1){
		}state FLUT20	
		
		exit{
			pvStopMonitor(PI201);
		}
	}
	
	state FLUT20{
		entry{
			if(DEBUG_ADS_EIN){ 
				debugNextState = 0; 			pvPut(debugNextState);
				sprintf(debugText, "FLUT20");	pvPut(debugText);
				snlMsgLog("dbg:<FLUT20>");
			}
			// CLOSE  '41UZV284'
			UZV284CMD = ZU;	pvPut(UZV284CMD);
			pvMonitor(UZV284zu);                                                                            
		}
		when (efTest(adsStop)) {
		}state ueberwachung
		//AND('41UZV284':DEV_STAT,1)=1
		when((!DEBUG_ADS_EIN || debugNextState!=0) && UZV284zu!=0){
		}state FLUT20_UZV294_OEFFNEN
		when((!DEBUG_ADS_EIN || debugNextState!=0) && delay(60)){
			snlMsgLog("<FLUT20>UZV284 - Schliesst nicht");
		}state FLUT20
		exit{
			pvStopMonitor(UZV284zu);                                                                            
		}
	}
	
	state FLUT20_UZV294_OEFFNEN{
		entry{
			if(DEBUG_ADS_EIN){ 
				debugNextState = 0; 							pvPut(debugNextState);
				sprintf(debugText, "FLUT20_UZV294_OEFFNEN");	pvPut(debugText);
				snlMsgLog("dbg:<FLUT20_UZV294_OEFFNEN>");
			}
			// Fluten                                                                                      
            // OPEN '41UZV294'
            UZV294CMD = AUF;	pvPut(UZV294CMD);
            pvMonitor(PI201); 
		}
		when (efTest(adsStop)) {
		}state ueberwachung
		/*'41PI201':AI_INVL > 5.*/
		when((!DEBUG_ADS_EIN || debugNextState!=0) && PI201 > 5.){
		} state FLUT20_UZV294_SCHLIESSEN
		when((!DEBUG_ADS_EIN || debugNextState!=0) && delay(60)){
			snlMsgLog("<FLUT20_UZV294_OEFFNEN>PI201 - Druck ist zu niedrig");
		}state FLUT20_UZV294_OEFFNEN
		exit{
			pvStopMonitor(PI201);
		}
	}
	
	state FLUT20_UZV294_SCHLIESSEN{
		entry{
			if(DEBUG_ADS_EIN){ 
				debugNextState = 0; 							pvPut(debugNextState);
				sprintf(debugText, "FLUT20_UZV294_SCHLIESSEN");	pvPut(debugText);
				snlMsgLog("FLUT20_UZV294_SCHLIESSEN>");
			}
			//  CLOSE '41UZV294'
			UZV294CMD = ZU;	pvPut(UZV294CMD);
			pvMonitor(UZV294zu);                                                                             
 		}
		when (efTest(adsStop)) {
		}state ueberwachung
 		//AND('41UZV294':DEV_STAT,1)=1
 		when((!DEBUG_ADS_EIN || debugNextState!=0) && UZV294zu!=0){
 		} state FLUT20_UZV292_SCHLIESSEN
 		when((!DEBUG_ADS_EIN || debugNextState!=0) && delay(60)){
			snlMsgLog("<FLUT20_UZV294_SCHLIESSEN>UZV294 - schliesst nicht");
		}state FLUT20_UZV294_SCHLIESSEN
 		
 		exit{
 			pvStopMonitor(UZV294zu);
 		}    
	}
	
	state FLUT20_UZV292_SCHLIESSEN{
		entry{
			if(DEBUG_ADS_EIN){ 
				debugNextState = 0; 							pvPut(debugNextState);
				sprintf(debugText, "FLUT20_UZV292_SCHLIESSEN");	pvPut(debugText);
				snlMsgLog("FLUT20_UZV292_SCHLIESSEN>");
			}
			// Fertig                                                                                      
			// CLOSE '41UZV292'
			UZV292CMD = ZU;	pvPut(UZV292CMD);
			
			pvMonitor(UZV292zu);
		}
		when (efTest(adsStop)) {
		}state ueberwachung
		//AND('41UZV292':DEV_STAT,1)=1
		when((!DEBUG_ADS_EIN || debugNextState!=0) && UZV292zu!=0){
			// QUERYNR ADIOCM,"AD100 REGENERIERT UND MIT 5 BAR GEFUELLT"                                    
			snlMsgLog("AD200 REGENERIERT UND MIT 5 BAR GEFUELLT");                                    
 			// PRINT ADIPNT,"AD100 in CBX4%i ist regeneriert",CUNIT                                         
 			// LET AD3BTR=0 
 			AD3BTR = BETRIEBSBEREIT;		pvPut(AD3BTR); 
		}state ueberwachung
		when((!DEBUG_ADS_EIN || debugNextState!=0) && delay(60)){
			snlMsgLog("<FLUT20_UZV292_SCHLIESSEN>UZV292 - schliesst nicht");
		}state FLUT20_UZV292_SCHLIESSEN
		
		exit{
			pvStopMonitor(UZV292zu);
		}  
	}
	
	//EVFE20:                                                                                       
 	//LET ANTW1 = 0                                                                                
 	//QUERYNR ADIOCM,"AD100 VAKUUM 0.1 BAR NICHT ERREICHT !"                                       
 	//QUERY ADIOCM,"DIE REGENERATION WIEDERHOLEN ? (J=1)",ANTW1                                    
    //                                                                                     
 	//IF (ANTW1 = 1) THEN                                                                          
 	//BEGIN                                                                                        
 	//	CLOSE  '41UZV284'                                                                            
 	//	WAITUNTIL(AND('41UZV284':DEV_STAT,1)=1)                                                      
 	//	OPEN '41UZV294'                                                                              
 	//	WAITUNTIL('41PI201':AI_INVL > 5.)                                                            
 	//	CLOSE '41UZV294'                                                                             
 	//	WAITUNTIL(AND('41UZV294':DEV_STAT,1)=1)                                                      
 	//	GOTO REG3 // hier muss zu DENT3 gegangen werden                                                                                   
 	//	END                                                                                          
 	//ELSE                                                                                         
 	//GOTO FLUT20
	state EVFE20{
		entry{
			if(DEBUG_ADS_EIN){ 
				debugNextState = 0; 			pvPut(debugNextState);
				sprintf(debugText, "EVFE20");	pvPut(debugText);
				snlMsgLog("dbg:<EVFE20>");
			}
			// LET ANTW1 = 0                                                                                
			AD_OPERATOR_ANT_JA=0;				pvPut(AD_OPERATOR_ANT_JA,SYNC);
			AD_OPERATOR_ANT_NEIN=0;			pvPut(AD_OPERATOR_ANT_NEIN,SYNC);
			pvStopMonitor(AD_OPERATOR_ANT_JA);
			pvStopMonitor(AD_OPERATOR_ANT_NEIN);
			// QUERYNR ADIOCM,"AD100 VAKUUM 0.1 BAR NICHT ERREICHT !"                                       
			// QUERY ADIOCM,"DIE REGENERATION WIEDERHOLEN ? (J=1)",ANTW1
			snlMsgLog("AD100 VAKUUM 0.1 BAR NICHT ERREICHT ! DIE REGENERATION WIEDERHOLEN ?");
			sprintf(AD_OPERATOR_ANT_JA_TEXT, "Regeneration Wiederholen ?");		pvPut(AD_OPERATOR_ANT_JA_TEXT);  
		}
		when (efTest(adsStop)) {
		}state ueberwachung
		// Ja (Regeneration Wiederholen)
		when((!DEBUG_ADS_EIN || debugNextState!=0) && AD_OPERATOR_ANT_JA){
		} state EVFE20_UZV284_SCHLIESSEN
		// Nein (Weiter)
		when((!DEBUG_ADS_EIN || debugNextState!=0) && AD_OPERATOR_ANT_NEIN){
		}state FLUT20
		exit{
			strcpy(AD_OPERATOR_ANT_JA_TEXT, "");		pvPut(AD_OPERATOR_ANT_JA_TEXT);
			pvStopMonitor(AD_OPERATOR_ANT_JA);  
			pvStopMonitor(AD_OPERATOR_ANT_NEIN);
		}   
	}
	
	state EVFE20_UZV284_SCHLIESSEN{
		entry{
			if(DEBUG_ADS_EIN){ 
				debugNextState = 0; 							pvPut(debugNextState);
				sprintf(debugText, "EVFE20_UZV284_SCHLIESSEN");	pvPut(debugText);
				snlMsgLog("dbg:<EVFE20_UZV284_SCHLIESSEN>");
			}
			UZV284CMD = ZU;	pvPut(UZV284CMD);
			pvMonitor(UZV284zu);
		}

		when (efTest(adsStop)) {
		}state ueberwachung
		when((!DEBUG_ADS_EIN || debugNextState!=0) && UZV284zu!=0){
		} state EVFE20_UZV294_OEFFNEN
		when((!DEBUG_ADS_EIN || debugNextState!=0) && delay(60)){
			snlMsgLog("<EVFE20_UZV284_SCHLIESSEN>UZV284 - schliesst nicht");
		}state EVFE20_UZV284_SCHLIESSEN
		exit{
			pvStopMonitor(UZV284zu);
		}
	}
	
	state EVFE20_UZV294_OEFFNEN {	 
		entry{
			if(DEBUG_ADS_EIN){ 
				debugNextState = 0; 							pvPut(debugNextState);
				sprintf(debugText, "EVFE20_UZV294_OEFFNEN");	pvPut(debugText);
				snlMsgLog("dbg:<EVFE20_UZV294_OEFFNEN>");
			}
			UZV294CMD = AUF;	pvPut(UZV294CMD);
			pvMonitor(PI201);
		}
		when (efTest(adsStop)) {
		}state ueberwachung
		when((!DEBUG_ADS_EIN || debugNextState!=0) && PI201 > 5.){
		} state EVFE20_UZV294_SCHLIESSEN
		when((!DEBUG_ADS_EIN || debugNextState!=0) && delay(60)){
			snlMsgLog("<EVFE20_UZV294_OEFFNEN>PI201 - Druck zu niedrig");
		}state EVFE20_UZV294_OEFFNEN
		
		exit{
			pvStopMonitor(PI201);
		}
	}
	
	state EVFE20_UZV294_SCHLIESSEN {
		entry{
			if(DEBUG_ADS_EIN){ 
				debugNextState = 0; 							pvPut(debugNextState);
				sprintf(debugText, "EVFE20_UZV294_SCHLIESSEN");	pvPut(debugText);
				snlMsgLog("dbg:<EVFE20_UZV294_SCHLIESSEN>");
			}
			UZV294CMD = ZU;	pvPut(UZV294CMD);
			pvMonitor(UZV294zu);
		}
		when (efTest(adsStop)) {
		}state ueberwachung
		when((!DEBUG_ADS_EIN || debugNextState!=0) && UZV294zu!=0){
		} state DENT3
		when((!DEBUG_ADS_EIN || debugNextState!=0) && delay(60)){
			snlMsgLog("<EVFE20_UZV294_SCHLIESSEN>UZV294 - schliesst nicht");
		}state EVFE20_UZV294_SCHLIESSEN
		exit{
			pvStopMonitor(UZV294zu);
		}
	}
	
	
	// ---------------------------- Wechsel AD200 fuer AD210 -------------------------------------------------------------------------------
	// CHNG4:                                                                                        
 	// Wechsel AD200 fuer AD210                                                                 
	// Erst alle Regenerationsventile schliessen ( definierter Zustand)                            
	// Kaltfahren AD200                                                                            
	state CHNG4{
		entry{
			if(DEBUG_ADS_EIN){ 
				debugNextState = 0; 	pvPut(debugNextState);
				sprintf(debugText, "CHNG4");	pvPut(debugText);
				snlMsgLog("dbg:<CHNG4>");
			}
			// Erst alle Regenerationsventile schliessen ( definierter Zustand)                            
	 		UZV274CMD = ZU;	 		pvPut(UZV274CMD);                                                                             
	 		UZV284CMD = ZU;	 		pvPut(UZV284CMD);                                                                             
			UZV272CMD = ZU;			pvPut(UZV272CMD);                                                                             
			UZV294CMD = ZU;			pvPut(UZV294CMD);                                                                             
			UZV293CMD = ZU;			pvPut(UZV293CMD);                                                                             
			UZV292CMD = ZU;			pvPut(UZV292CMD);
			                                                                             
			UZV173CMD = ZU;			pvPut(UZV173CMD);                                                                             
			UZV273CMD = ZU;			pvPut(UZV273CMD);                                                                             
			UZV183CMD = ZU;			pvPut(UZV183CMD);                                                                             
			UZV182CMD = ZU;			pvPut(UZV182CMD);                                                                             
			UZV283CMD = ZU;			pvPut(UZV283CMD);                                                                             
			UZV282CMD = ZU;			pvPut(UZV282CMD);
			// Kaltfahren AD200                                                                            
	 		// IF ('41HC202':CB_OTVL:1 < 10.0) THEN                                                         
	 		pvGet(CV202OVAL);
	 		if(CV202OVAL < 10.0){
		 		// PUTMANL '41HC202'
		 		// PUTOUT '41HC202',10          
		 		pvGet(CV202SCAN,SYNC);	 		strcpy(ovalZwischenSpeicher1,CV202SCAN); 
		 		CV202OMSL = SUPERVISORY;		pvPut(CV202OMSL, SYNC);
		 		CV202OROC = 1.0;				pvPut(CV202OROC, SYNC);
		 		CV202 = 10;						pvPut(CV202, SYNC);
		 		sprintf(CV202SCAN, "1 second");	pvPut(CV202SCAN, SYNC);
			}	 		
			pvMonitor(PI201);
			pvMonitor(PI203);
		}
		when (efTest(adsStop)) {
		}state ueberwachung
	 		// Druck angleichen                                                                            
	 		// WAITUNTIL('41PI201':AI_INVL > '41PI203':AI_INVL-1.5 )
		when((!DEBUG_ADS_EIN || debugNextState!=0) && PI201 > (PI203-1.5)){
			// PUTMANL nicht vergessen?
			// PUTOUT '41HC202',100
			                                                                         
		 	pvGet(CV202SCAN,SYNC);	 		strcpy(ovalZwischenSpeicher1,CV202SCAN); 
	 		CV202OMSL = SUPERVISORY;		pvPut(CV202OMSL, SYNC);
	 		CV202OROC = 1.0;				pvPut(CV202OROC, SYNC);
	 		CV202 = 100;					pvPut(CV202, SYNC);
	 		strcpy(CV202SCAN, "1 second");	pvPut(CV202SCAN, SYNC);
		}state CHNG4_HEIZUNG_EINSCHALTEN
		
		when((!DEBUG_ADS_EIN || debugNextState!=0) && delay(60)){
			snlMsgLog("<CHNG4>Druckangleich PI201 / PI203 nicht ok!");
		}state CHNG4
		exit{
			pvStopMonitor(PI201);
			pvStopMonitor(PI203);
			strcpy(CV202SCAN, ovalZwischenSpeicher1);	pvPut(CV202SCAN, SYNC);
		}
	}
	
	state CHNG4_HEIZUNG_EINSCHALTEN{
		entry{
			if(DEBUG_ADS_EIN){ 
				debugNextState = 0; 								pvPut(debugNextState);
				sprintf(debugText, "CHNG4_HEIZUNG_EINSCHALTEN");	pvPut(debugText);
				snlMsgLog("dbg:<CHNG4_HEIZUNG_EINSCHALTEN>");
			}
	 		// Heizung eingeschalten                                                                      
		 	// OPEN '41HS130'         
		 	HS130CMD = EIN;	 		pvPut(HS130CMD);
		 	pvMonitor(HS130ein);
		}
		when (efTest(adsStop)) {
		}state ueberwachung
		//AND('41HS130':DEV_STAT,2)=2
		when((!DEBUG_ADS_EIN || debugNextState!=0) && HS130ein!=0){
		}state CHNG4_UZV292_OEFFNEN
		when((!DEBUG_ADS_EIN || debugNextState!=0) && delay(60)){
			snlMsgLog("<CHNG4_HEIZUNG_EINSCHALTEN>HS130 - keine Rückmeldung");
		}state CHNG4_HEIZUNG_EINSCHALTEN
		
		exit{	
		 	pvStopMonitor(HS130ein);
	 	}
	}
	state CHNG4_UZV292_OEFFNEN{
		entry{
			if(DEBUG_ADS_EIN){ 
				debugNextState = 0; 						pvPut(debugNextState);
				sprintf(debugText, "CHNG4_UZV292_OEFFNEN");	pvPut(debugText);
				snlMsgLog("dbg:<CHNG4_UZV292_OEFFNEN>");
			}
			//OPEN '41UZV292'  
		 	UZV292CMD = AUF;	 		pvPut(UZV292CMD);
		 	pvMonitor(UZV292auf);
		}
		when (efTest(adsStop)) {
		}state ueberwachung
		//AND('41UZV292':DEV_STAT,2)=2
		when((!DEBUG_ADS_EIN || debugNextState!=0) && UZV292auf!=0){
		}state CHNG4_UZV272_OEFFNEN
		when((!DEBUG_ADS_EIN || debugNextState!=0) && delay(60)){
			snlMsgLog("<CHNG4_UZV292_OEFFNEN>UZV292 - öffnet nicht!");
		}state CHNG4_UZV292_OEFFNEN
		
		exit{
		 	pvStopMonitor(UZV292auf);
	 	}
	}
	
	state CHNG4_UZV272_OEFFNEN{
		entry{
			if(DEBUG_ADS_EIN){ 
				debugNextState = 0; 						pvPut(debugNextState);
				sprintf(debugText, "CHNG4_UZV272_OEFFNEN");	pvPut(debugText);
				snlMsgLog("dbg:<CHNG4_UZV272_OEFFNEN>");
			}
			// OPEN '41UZV272'
		 	UZV272CMD = AUF;	 		pvPut(UZV272CMD);
		 	pvMonitor(UZV272auf);
		}
		when (efTest(adsStop)) {
		}state ueberwachung
		//AND('41UZV272':DEV_STAT,2)=2
		when((!DEBUG_ADS_EIN || debugNextState!=0) && UZV272auf!=0){
		}state CHNG4_AD200_KALTFAHREN
		when((!DEBUG_ADS_EIN || debugNextState!=0) && delay(60)){
			snlMsgLog("<CHNG4_UZV272_OEFFNEN>UZV272 - öffnet nicht!");
		}state CHNG4_UZV272_OEFFNEN
		
		exit{
		 	pvStopMonitor(UZV272auf);
	 	}
	}
	
	state CHNG4_AD200_KALTFAHREN{
		entry{
			if(DEBUG_ADS_EIN){ 
				debugNextState = 0; 							pvPut(debugNextState);
				sprintf(debugText, "CHNG4_AD200_KALTFAHREN");	pvPut(debugText);
				snlMsgLog("dbg:<CHNG4_AD200_KALTFAHREN>");
			}
			pvMonitor(TI200);
			pvMonitor(TI202);
		}
		when (efTest(adsStop)) {
		}state ueberwachung
		//'41TI200':AI_INVL < '41TI202':AI_INVL+5.0
		when((!DEBUG_ADS_EIN || debugNextState!=0) && TI200 < (TI202+5.0)){
		}state CHNG4_UZV292_SCHLIESSEN
		when((!DEBUG_ADS_EIN || debugNextState!=0) && delay(60)){
			snlMsgLog("<CHNG4_AD200_KALTFAHREN>Kaltfahren nicht erfolgreich! TI200 zu hoch.");
		} state CHNG4_AD200_KALTFAHREN
		exit{
			pvStopMonitor(TI200);
			pvStopMonitor(TI202);
		}
	}          
	
	state CHNG4_UZV292_SCHLIESSEN{
		entry{
			if(DEBUG_ADS_EIN){ 
				debugNextState = 0; 							pvPut(debugNextState);
				sprintf(debugText, "CHNG4_UZV292_SCHLIESSEN");	pvPut(debugText);
				snlMsgLog("dbg:<CHNG4_UZV292_SCHLIESSEN>");
			}
		 	// Nun ist AD200 kalt                                                                          
			// CLOSE '41UZV292'
			UZV292CMD = ZU;	 		pvPut(UZV292CMD);
		 	pvMonitor(UZV292zu);
		}
		
		when (efTest(adsStop)) {
		}state ueberwachung
		//(AND('41UZV292':DEV_STAT,1)=1
		when((!DEBUG_ADS_EIN || debugNextState!=0) && UZV292zu!=0){
		} state CHNG4_UZV272_SCHLIESSEN
		when((!DEBUG_ADS_EIN || debugNextState!=0) && delay(60)){
			snlMsgLog("<CHNG4_UZV292_SCHLIESSEN>UZV292 - schliesst nicht!");
		}state CHNG4_UZV292_SCHLIESSEN
		exit{
		 	pvStopMonitor(UZV292zu);
	 	}
	} 
	
	state CHNG4_UZV272_SCHLIESSEN{
		entry{
			if(DEBUG_ADS_EIN){ 
				debugNextState = 0; 							pvPut(debugNextState);
				sprintf(debugText, "CHNG4_UZV272_SCHLIESSEN");	pvPut(debugText);
				snlMsgLog("dbg:<CHNG4_UZV272_SCHLIESSEN>");
			}
			// CLOSE '41UZV272'                                                                             
			UZV272CMD = ZU;	 		pvPut(UZV272CMD);
		 	pvMonitor(UZV272zu);
		}
		when (efTest(adsStop)) {
		}state ueberwachung
		//AND('41UZV272':DEV_STAT,1)=1
		when((!DEBUG_ADS_EIN || debugNextState!=0) && UZV272zu!=0){
		} state FLA41
		when((!DEBUG_ADS_EIN || debugNextState!=0) && delay(60)){
			snlMsgLog("<CHNG4_UZV272_SCHLIESSEN>UZV272 - schliesst nicht!");
		}state CHNG4_UZV272_SCHLIESSEN
		exit{
		 	pvStopMonitor(UZV272zu);
	 	}
	}
	
	/*
	FLA41:                                                                                        
 		PUTMANL '41HC114'                                                                            
 		PUTOUT '41HC114',100                                                                         
 		PUTMANL '41HC113'                                                                            
 		PUTOUT '41HC113',100                                                                         
 		WAITUNTIL(AND('41CV113EN':DEV_STAT,2)=2)COUNT 120,MSG11                                      
 		WAITUNTIL(AND('41CV114EN':DEV_STAT,2)=2)COUNT 120,MSG11                                      
	*/
	state FLA41{
		option -e; /* führt entry immer wieder aus */
		entry{
			if(DEBUG_ADS_EIN){ 
				debugNextState = 0; 			pvPut(debugNextState);
				sprintf(debugText, "FLA41");	pvPut(debugText);
				snlMsgLog("dbg:<FLA41>");
			}
			//PUTMANL '41HC202'                                                                            
 			//PUTOUT '41HC202',100
			pvGet(CV202SCAN,SYNC);	 		strcpy(ovalZwischenSpeicher1,CV202SCAN); 
 			CV202OMSL = SUPERVISORY;		pvPut(CV202OMSL, SYNC);
	 		CV202OROC = 1.0;				pvPut(CV202OROC, SYNC);
	 		CV202 = 100;					pvPut(CV202, SYNC);
	 		sprintf(CV202SCAN, "1 second");	pvPut(CV202SCAN, SYNC);
 			                                                                         
 			//PUTMANL '41HC201'                                                                            
 			//PUTOUT '41HC201',100
			pvGet(CV201SCAN,SYNC);	 		strcpy(ovalZwischenSpeicher2,CV201SCAN); 
 			CV201OMSL = SUPERVISORY;		pvPut(CV201OMSL, SYNC);
	 		CV201OROC = 1.0;				pvPut(CV201OROC, SYNC);
	 		CV201 = 100;					pvPut(CV201, SYNC);
	 		sprintf(CV201SCAN, "1 second");	pvPut(CV201SCAN, SYNC);
	 		
	 		pvMonitor(CV201auf);
	 		pvMonitor(CV202auf);
	 		pvMonitor(CV201OVAL);
	 		pvMonitor(CV202OVAL);
		}
		when (efTest(adsStop)) {
		}state ueberwachung
	 	//WAITUNTIL(AND('41CV201EN':DEV_STAT,2)=2)COUNT 120,MSG42                                      
 		//WAITUNTIL(AND('41CV202EN':DEV_STAT,2)=2)COUNT 120,MSG42                                      
		when((!DEBUG_ADS_EIN || debugNextState!=0) && (CV201auf!=0) && (CV201OVAL>99) && (CV202auf!=0) && (CV202OVAL>99)){ 
		}state FLA42
		when((!DEBUG_ADS_EIN || debugNextState!=0) && delay(120)){
			if(CV201auf==0){
				snlMsgLog("Eintrittsventil 42CV201 am AD200 in CBX42 öffnet nicht");
			}
			if(CV202auf==0){
				snlMsgLog("Austrittsventil 42CV202 am AD200 in CBX42 öffnet nicht");
			}
		} state FLA41
		
		exit{
	 		pvStopMonitor(CV201auf);
	 		pvStopMonitor(CV202auf);
	 		pvStopMonitor(CV201OVAL);
	 		pvStopMonitor(CV202OVAL);
	 		strcpy(CV202SCAN, ovalZwischenSpeicher1);	pvPut(CV202SCAN, SYNC);
	 		strcpy(CV201SCAN, ovalZwischenSpeicher2);	pvPut(CV201SCAN, SYNC);
 		}
	}
	
	state FLA42{
		option -e;/* führt entry immer wieder aus */
		entry{
			if(DEBUG_ADS_EIN){ 
				debugNextState = 0; 			pvPut(debugNextState);
				sprintf(debugText, "FLA42");	pvPut(debugText);
				snlMsgLog("dbg:<FLA42>");
			}
			// PUTMANL '41HC203'                                                                            
 			// PUTOUT '41HC203',0
 			pvGet(CV203SCAN,SYNC);	 		strcpy(ovalZwischenSpeicher1,CV203SCAN);
 			CV203OMSL = SUPERVISORY;		pvPut(CV203OMSL, SYNC);
	 		CV203OROC = 1.0;				pvPut(CV203OROC, SYNC);
	 		CV203 = 0;						pvPut(CV203, SYNC);                                                                           
	 		sprintf(CV203SCAN, "1 second");	pvPut(CV203SCAN, SYNC);
 			// PUTMANL '41HC204'                                                                            
 			// PUTOUT '41HC204',0
 			pvGet(CV204SCAN,SYNC);	 		strcpy(ovalZwischenSpeicher2,CV204SCAN);
 			CV204OMSL = SUPERVISORY;		pvPut(CV204OMSL, SYNC);
	 		CV204OROC = 1.0;				pvPut(CV204OROC, SYNC);
	 		CV204 = 0;						pvPut(CV204, SYNC);
	 		sprintf(CV204SCAN, "1 second");	pvPut(CV204SCAN, SYNC);
	 		
	 		pvMonitor(CV203zu);                                                                           
	 		pvMonitor(CV204zu);
	 		pvMonitor(CV204OVAL);
	 		pvMonitor(CV203OVAL);
		}
		when (efTest(adsStop)) {
		}state ueberwachung
		// WAITUNTIL(AND('41CV204EN':DEV_STAT,1)=1)COUNT 120,MSG12                                      
 		// WAITUNTIL(AND('41CV203EN':DEV_STAT,1)=1)COUNT 120,MSG12
 		when((!DEBUG_ADS_EIN || debugNextState!=0) && (CV204zu!=0) && (CV204OVAL<1) && (CV203zu!=0) && (CV203OVAL<1)){
 			// ADSORBER 110 IM PROZESS ADSORBER 100 AUS DEM PROZESS                                        
 			// LET AD4BTR=2 // setze Adsorber 1 auf Regeneration
 			AD4BTR = REGENERIERT;
 			pvPut(AD4BTR);
 		} state ueberwachung         
		when((!DEBUG_ADS_EIN || debugNextState!=0) && delay(120)){
			if(CV203zu == 0){
				snlMsgLog("Eintrittsventil 42CV203 am AD200 in CBX42 schließt nicht");
			} 
			if(CV204zu == 0){
				snlMsgLog("Austrittsventil 42CV204 am AD200 in CBX42 schließt nicht");
			}
		} state	FLA42
		exit{
			pvStopMonitor(CV203zu);                                                                           
	 		pvStopMonitor(CV204zu);                                                                           
	 		pvStopMonitor(CV204OVAL);
	 		pvStopMonitor(CV203OVAL);
	 		strcpy(CV203SCAN, ovalZwischenSpeicher1);	pvPut(CV203SCAN, SYNC);
	 		strcpy(CV204SCAN, ovalZwischenSpeicher2);	pvPut(CV204SCAN, SYNC);
		}
	}

	//--------- REGENERATION FUER AD210 --------                                                  
 	// OPEN '41UZV293'                                                                              
 	// WAITUNTIL(AND('41UZV293':DEV_STAT,2)=2)                                                      
	state REG210{
		entry{
			if(DEBUG_ADS_EIN){ 
				debugNextState = 0; 			pvPut(debugNextState);
				sprintf(debugText, "REG210");	pvPut(debugText);
				snlMsgLog("dbg:<REG210>");
			}
 			//OPEN '41UZV293'
 			UZV293CMD = AUF;	pvPut(UZV293CMD);
 			snlMsgLog("<REG210>Regeneration für AD210 Startet");
 			pvMonitor(UZV293auf);                                                                              
		}
		when (efTest(adsStop)) {
		}state ueberwachung
		//'41UZV293':DEV_STAT,2)=2
		when((!DEBUG_ADS_EIN || debugNextState!=0) && UZV293auf!=0){
		} state DENT4
		when((!DEBUG_ADS_EIN || debugNextState!=0) && delay(60)){
			snlMsgLog("<REG210>UZV293 - öffnet nicht!");
		}state REG210
		exit{
			pvStopMonitor(UZV293auf);
		}
	}
	
	//DENT4:                                                                                        
	//OPEN '41UZV272'                                                                              
	//WAITUNTIL('41PI203':AI_INVL < 1.2)                                                           
	//CLOSE '41UZV272'                                                                             
	//WAITUNTIL(AND('41UZV272':DEV_STAT,1)=1 )                                                     
	//RETURN                                                                                       
	state DENT4{
		entry{
			if(DEBUG_ADS_EIN){ 
				debugNextState = 0; 			pvPut(debugNextState);
				sprintf(debugText, "DENT4");	pvPut(debugText);
				snlMsgLog("dbg:<DENT4>");
			}
	 		// OPEN '41UZV272'
	 		UZV272CMD = AUF; 	pvPut(UZV272CMD);
 			pvMonitor(PI203);   
 		}
		when (efTest(adsStop)) {
		}state ueberwachung
 		/*'41PI203':AI_INVL < 1.2*/
 		when((!DEBUG_ADS_EIN || debugNextState!=0) && PI203 < 1.2){
 		} state DENT4_UZV272_SCHLIESSEN
 		when((!DEBUG_ADS_EIN || debugNextState!=0) && delay(600)){
			snlMsgLog("<DENT4>PI203 - Druck zu hoch!");
		}state DENT4
 		exit{
 			pvStopMonitor(PI203);
 		}
	}

	state DENT4_UZV272_SCHLIESSEN{
		entry{
			if(DEBUG_ADS_EIN){ 
				debugNextState = 0; 							pvPut(debugNextState);
				sprintf(debugText, "DENT4_UZV272_SCHLIESSEN");	pvPut(debugText);
				snlMsgLog("dbg:<DENT4_UZV272_SCHLIESSEN>");
			}
	 		//CLOSE '41UZV272'
 			UZV272CMD = ZU; 	pvPut(UZV272CMD);
 			pvMonitor(UZV272zu);   
 		}                         
		when (efTest(adsStop)) {
		}state ueberwachung
 		//AND('41UZV272':DEV_STAT,1)=1                                                    
		when((!DEBUG_ADS_EIN || debugNextState!=0) && UZV272zu!=0){
		} state REG4
		when((!DEBUG_ADS_EIN || debugNextState!=0) && delay(60)){
			snlMsgLog("<DENT4_UZV272_SCHLIESSEN>UZV272 - schliesst nicht!");
		}state DENT4_UZV272_SCHLIESSEN
		exit{
			pvStopMonitor(UZV272zu);
		}
	}
	
	state REG4{
		entry{
			if(DEBUG_ADS_EIN){ 
				debugNextState = 0; 		pvPut(debugNextState);
				sprintf(debugText, "REG4");	pvPut(debugText);
				snlMsgLog("dbg:<REG4>");
			}
			// OPEN '41UZV274'                                                                              
 			UZV274CMD = AUF; 	pvPut(UZV274CMD);
 			// OPEN '41UZV283'    
 			UZV283CMD = AUF; 	pvPut(UZV283CMD);
 			pvMonitor(UZV274auf);
 			pvMonitor(UZV283auf);
		}
		when (efTest(adsStop)) {
		}state ueberwachung
		//AND('41UZV283':DEV_STAT,2)=2 
		when((!DEBUG_ADS_EIN || debugNextState!=0) && UZV283auf!=0 && UZV274auf!=0){
		} state REG4_UZV273_OEFFNEN
		when((!DEBUG_ADS_EIN || debugNextState!=0) && delay(60)){
			if(UZV274auf==0){
				snlMsgLog("<REG4>UZV274 - öffnet nicht!");
			}
			if(UZV283auf==0){
				snlMsgLog("<REG4>UZV283 - öffnet nicht!");
			}
		}state REG4
		exit{
 			pvStopMonitor(UZV274auf);
 			pvStopMonitor(UZV283auf);
		}
	}	                           
	
	state REG4_UZV273_OEFFNEN{
		entry{
			if(DEBUG_ADS_EIN){ 
				debugNextState = 0; 						pvPut(debugNextState);
				sprintf(debugText, "REG4_UZV273_OEFFNEN");	pvPut(debugText);
				snlMsgLog("dbg:<REG4_UZV273_OEFFNEN>");
			}
			// OPEN '41UZV273'                                                                              
 			UZV273CMD = AUF; 	pvPut(UZV273CMD);
 			pvMonitor(UZV273auf);
		}                          
		when (efTest(adsStop)) {
		}state ueberwachung
		//AND('41UZV273':DEV_STAT,2)=2
		when((!DEBUG_ADS_EIN || debugNextState!=0) && UZV273auf!=0){
		} state REG4_HS151_EINSCHALTEN
		when((!DEBUG_ADS_EIN || debugNextState!=0) && delay(60)){
			snlMsgLog("<REG4_UZV273_OEFFNEN>UZV273 - öffnet nicht!");
		}state REG4_UZV273_OEFFNEN
		exit{
 			pvStopMonitor(UZV273auf);
		}
	}
	
	state REG4_HS151_EINSCHALTEN{
		option -e;
		entry{
			if(DEBUG_ADS_EIN){ 
				debugNextState = 0; 							pvPut(debugNextState);
				sprintf(debugText, "REG4_HS151_EINSCHALTEN");	pvPut(debugText);
				snlMsgLog("dbg:<REG4_HS151_EINSCHALTEN>");
			}
			// 	START '41HS151'
			HS151CMD = EIN; 	pvPut(HS151CMD);
 			pvMonitor(HS151ein);
 			pvMonitor(TI202);
		}
		when (efTest(adsStop)) {
		}state ueberwachung
//		delay(30) && //AND('41HS151':DEV_STAT,2)!=2*/
		when((!DEBUG_ADS_EIN || debugNextState!=0) && delay(30) && HS151ein==0){
				// QUERYNR ADIOCM,"STOERUNG GEBLAESE REGENERATION AD210"
				snlMsgLog("<REG4_HS151_EINSCHALTEN>HS151 - keine Rückmeldung");                                        
				// STOP '41HS151'                                                                               
				HS151CMD = AUS; 	pvPut(HS151CMD);
				// CLOSE '41UZV274'                                                                             
				UZV274CMD = ZU; 	pvPut(UZV274CMD);
		}state DENT4
//		delay(30) && /*'41TI202':AI_INVL > 250.*/
		when((!DEBUG_ADS_EIN || debugNextState!=0) && delay(30) && (TI202 > 250.)){
		}state REG4_GEBLAESE_AUS
		when((!DEBUG_ADS_EIN || debugNextState!=0) && delay(7200)){
			snlMsgLog("<REG4_HS151_EINSCHALTEN>TI202 - Temperatur nicht erreicht!");
		}state REG4_HS151_EINSCHALTEN
		
		exit{
 			pvStopMonitor(HS151ein);
 			pvStopMonitor(TI202);
		}
	}
	
	state REG4_GEBLAESE_AUS{
		entry{
			if(DEBUG_ADS_EIN){ 
				debugNextState = 0; 						pvPut(debugNextState);
				sprintf(debugText, "REG4_GEBLAESE_AUS");	pvPut(debugText);
				snlMsgLog("dbg:<REG4_GEBLAESE_AUS>");
			}
			// STOP '41HS151'                
			HS151CMD = AUS; 	pvPut(HS151CMD);                                                               
 			// CLOSE '41UZV273'                                                                             
			UZV273CMD = ZU; 	pvPut(UZV273CMD);                                                               
			// CLOSE '41UZV283'
			UZV283CMD = ZU; 	pvPut(UZV283CMD);
			
			pvMonitor(UZV273zu);                                                               
			pvMonitor(UZV283zu);
			pvMonitor(HS151aus);                                                               
		}
		when (efTest(adsStop)) {
		}state ueberwachung
		//AND('41UZV273':DEV_STAT,1)=1 & AND('41UZV283':DEV_STAT,1)=1
		when((!DEBUG_ADS_EIN || debugNextState!=0) && (UZV273zu!=0) && (UZV283zu!=0) && (HS151aus!=0)){
		} state REG4_UZV274_SCHLIESSEN
		when((!DEBUG_ADS_EIN || debugNextState!=0) && delay(60)){
			if(UZV273zu==0){
				snlMsgLog("<REG4_GEBLAESE_AUS>UZV273 - Schliesst nicht");
			}
			if(UZV283zu==0){
				snlMsgLog("<REG4_GEBLAESE_AUS>UZV283 - Schliesst nicht");
			}
			if(HS151aus==0){
				snlMsgLog("<REG4_GEBLAESE_AUS>HS151  - Gebläse schaltet nicht ab");
			}
		}state REG4_GEBLAESE_AUS
		
		exit{
			pvStopMonitor(UZV273zu);                                                               
			pvStopMonitor(UZV283zu);
			pvStopMonitor(HS151aus);                                                               
		}
	}			
	
	state REG4_UZV274_SCHLIESSEN{
		entry{
			if(DEBUG_ADS_EIN){ 
				debugNextState = 0; 							pvPut(debugNextState);
				sprintf(debugText, "REG4_UZV274_SCHLIESSEN");	pvPut(debugText);
				snlMsgLog("dbg:<REG4_UZV274_SCHLIESSEN>");
			}
			// CLOSE '41UZV274'
			UZV274CMD = ZU; 	pvPut(UZV274CMD);
			pvMonitor(UZV274zu); 
		}
		when (efTest(adsStop)) {
		}state ueberwachung
		//AND('41UZV274':DEV_STAT,1)=1)
		when((!DEBUG_ADS_EIN || debugNextState!=0) && UZV274zu!=0){
		 	// OPEN '41UZV284'
			UZV284CMD = AUF; 	pvPut(UZV284CMD); 
		}state EVLA21 
		when((!DEBUG_ADS_EIN || debugNextState!=0) && delay(60)){
			snlMsgLog("<REG4_UZV274_SCHLIESSEN>UZV274zu - Schliesst nicht");
		}state REG4_UZV274_SCHLIESSEN
		exit{
			pvStopMonitor(UZV274zu);
		}
	}
	
	//EVLA21:                                                                                       
	// QUERYNR ADIOCM,"*** AD210 WIRD EVAKUIERT ***"                                                
	// QUERYNR ADIOCM,"BITTE VACUUMLEITUNG CHECKEN!"                                                
	// nach 10 min. muss 0.5 bar unterschritten                                                    
	// werden ,dann wird gepumpt bis 0.1 bar                                                       
	//                                                                                             
	// WAITUNTIL('41PI203':AI_INVL < 0.5) COUNT 600,EVLA21                                          
	// WAITUNTIL('41PI203':AI_INVL < 0.1) COUNT 2000,EVFE21                                         
	state EVLA21{
		option -e;
		entry{
			if(DEBUG_ADS_EIN){ 
				debugNextState = 0; 			pvPut(debugNextState);
				sprintf(debugText, "EVLA21");	pvPut(debugText);
				snlMsgLog("dbg:<EVLA21>");
			}
			// QUERYNR ADIOCM,"*** AD210 WIRD EVAKUIERT ***"                                                
			// QUERYNR ADIOCM,"BITTE VACUUMLEITUNG CHECKEN!"                                                
			snlMsgLog("AD210 IN CBX 42 WIRD EVAKUIERT! BITTE VOR-VACUUMLEITUNG CHECKEN! ");                                         
			pvMonitor(PI203);                                         
		}
		when (efTest(adsStop)) {
		}state ueberwachung
		when((!DEBUG_ADS_EIN || debugNextState!=0) && delay(600)){
		}state EVLA21
		/*'41PI203':AI_INVL < 0.5*/
		when((!DEBUG_ADS_EIN || debugNextState!=0) && PI203  < 0.5){
		} state EVLA21_01BAR
		
		exit{
			pvStopMonitor(PI203);
		}                                         
	}
	
	state EVLA21_01BAR{
		entry{
			if(DEBUG_ADS_EIN){ 
				debugNextState = 0; 				pvPut(debugNextState);
				sprintf(debugText, "EVLA21_01BAR");	pvPut(debugText);
				snlMsgLog("dbg:<EVLA21_01BAR>");
			}
			pvMonitor(PI203);
		}
		when (efTest(adsStop)) {
		}state ueberwachung
		when((!DEBUG_ADS_EIN || debugNextState!=0) && delay(2000)){
		}state EVFE21
		/*'41PI203':AI_INVL < 0.1*/
		when((!DEBUG_ADS_EIN || debugNextState!=0) && PI203 < 0.1){
		}state FLUT21	
		
		exit{
			pvStopMonitor(PI203);
		}
	}
	
	state FLUT21{
		entry{
			if(DEBUG_ADS_EIN){ 
				debugNextState = 0; 			pvPut(debugNextState);
				sprintf(debugText, "FLUT21");	pvPut(debugText);
				snlMsgLog("dbg:<FLUT21>");
			}
			// CLOSE  '41UZV284'
			UZV284CMD = ZU;	pvPut(UZV284CMD);
			pvMonitor(UZV284zu);                                                                            
		}
		when (efTest(adsStop)) {
		}state ueberwachung
		//AND('41UZV284':DEV_STAT,1)=1
		when((!DEBUG_ADS_EIN || debugNextState!=0) && UZV284zu!=0){
		}state FLUT21_UZV294_OEFFNEN
		when((!DEBUG_ADS_EIN || debugNextState!=0) && delay(60)){
			snlMsgLog("<FLUT21>UZV284 - Schliesst nicht");
		}state FLUT21
		exit{
			pvStopMonitor(UZV284zu);                                                                            
		}
	}
	
	state FLUT21_UZV294_OEFFNEN{
		entry{
			if(DEBUG_ADS_EIN){ 
				debugNextState = 0; 							pvPut(debugNextState);
				sprintf(debugText, "FLUT21_UZV294_OEFFNEN");	pvPut(debugText);
				snlMsgLog("dbg:<FLUT21_UZV294_OEFFNEN>");
			}
			// Fluten                                                                                      
            // OPEN '41UZV294'
            UZV294CMD = AUF;	pvPut(UZV294CMD);
            pvMonitor(PI203); 
		}
		when (efTest(adsStop)) {
		}state ueberwachung
		/*'41PI203':AI_INVL > 5.*/
		when((!DEBUG_ADS_EIN || debugNextState!=0) && PI203 > 5.){
		} state FLUT21_UZV294_SCHLIESSEN
		when((!DEBUG_ADS_EIN || debugNextState!=0) && delay(60)){
			snlMsgLog("<FLUT21_UZV294_OEFFNEN>PI203 - Druck ist zu niedrig");
		}state FLUT21_UZV294_OEFFNEN
		exit{
			pvStopMonitor(PI203);
		}
	}
	
	state FLUT21_UZV294_SCHLIESSEN{
		entry{
			if(DEBUG_ADS_EIN){ 
				debugNextState = 0; 							pvPut(debugNextState);
				sprintf(debugText, "FLUT21_UZV294_SCHLIESSEN");	pvPut(debugText);
				snlMsgLog("FLUT21_UZV294_SCHLIESSEN>");
			}
			//  CLOSE '41UZV294'
			UZV294CMD = ZU;	pvPut(UZV294CMD);
			pvMonitor(UZV294zu);                                                                             
 		}
		when (efTest(adsStop)) {
		}state ueberwachung
 		//AND('41UZV294':DEV_STAT,1)=1
 		when((!DEBUG_ADS_EIN || debugNextState!=0) && UZV294zu!=0){
 		} state FLUT21_UZV293_SCHLIESSEN
 		when((!DEBUG_ADS_EIN || debugNextState!=0) && delay(60)){
			snlMsgLog("<FLUT21_UZV294_SCHLIESSEN>UZV294 - schliesst nicht");
		}state FLUT21_UZV294_SCHLIESSEN
 		
 		exit{
 			pvStopMonitor(UZV294zu);
 		}    
	}
	
	state FLUT21_UZV293_SCHLIESSEN{
		entry{
			if(DEBUG_ADS_EIN){ 
				debugNextState = 0; 							pvPut(debugNextState);
				sprintf(debugText, "FLUT21_UZV293_SCHLIESSEN");	pvPut(debugText);
				snlMsgLog("FLUT21_UZV293_SCHLIESSEN>");
			}
			// Fertig                                                                                      
			// CLOSE '41UZV293'
			UZV293CMD = ZU;	pvPut(UZV293CMD);
			pvMonitor(UZV293zu);
		}
		when (efTest(adsStop)) {
		}state ueberwachung
		//AND('41UZV293':DEV_STAT,1)=1
		when((!DEBUG_ADS_EIN || debugNextState!=0) && UZV293zu!=0){
			// QUERYNR ADIOCM,"AD210 REGENERIERT UND MIT 5 BAR GEFUELLT"                                    
			snlMsgLog("AD210 REGENERIERT UND MIT 5 BAR GEFUELLT");                                    
 			// LET AD4BTR=0 
 			AD4BTR = BETRIEBSBEREIT;		pvPut(AD4BTR); 
		}state ueberwachung
		when((!DEBUG_ADS_EIN || debugNextState!=0) && delay(60)){
			snlMsgLog("<FLUT21_UZV293_SCHLIESSEN>UZV293 - schliesst nicht");
		}state FLUT21_UZV293_SCHLIESSEN
		
		exit{
			pvStopMonitor(UZV293zu);
		}  
	}
	
	//EVFE21:                                                                                       
 	//LET ANTW1 = 0                                                                                
 	//QUERYNR ADIOCM,"AD210 VAKUUM 0.1 BAR NICHT ERREICHT !"                                       
 	//QUERY ADIOCM,"DIE REGENERATION WIEDERHOLEN ? (J=1)",ANTW1                                    
    //                                                                                     
 	//IF (ANTW1 = 1) THEN                                                                          
 	//BEGIN                                                                                        
 	//	CLOSE  '41UZV284'                                                                            
 	//	WAITUNTIL(AND('41UZV284':DEV_STAT,1)=1)                                                      
 	//	OPEN '41UZV294'                                                                              
 	//	WAITUNTIL('41PI201':AI_INVL > 5.)                                                            
 	//	CLOSE '41UZV294'                                                                             
 	//	WAITUNTIL(AND('41UZV294':DEV_STAT,1)=1)                                                      
 	//	GOTO REG4 // hier muss zu DENT4 gegangen werden                                                                                   
 	//	END                                                                                          
 	//ELSE                                                                                         
 	//GOTO FLUT21
	state EVFE21{
		entry{
			if(DEBUG_ADS_EIN){ 
				debugNextState = 0; 			pvPut(debugNextState);
				sprintf(debugText, "EVFE21");	pvPut(debugText);
				snlMsgLog("dbg:<EVFE21>");
			}
			// LET ANTW1 = 0                                                                                
			AD_OPERATOR_ANT_JA=0;				pvPut(AD_OPERATOR_ANT_JA,SYNC);
			AD_OPERATOR_ANT_NEIN=0;			pvPut(AD_OPERATOR_ANT_NEIN,SYNC);
			pvStopMonitor(AD_OPERATOR_ANT_JA);
			pvStopMonitor(AD_OPERATOR_ANT_NEIN);
			// QUERYNR ADIOCM,"AD210 VAKUUM 0.1 BAR NICHT ERREICHT !"                                       
			// QUERY ADIOCM,"DIE REGENERATION WIEDERHOLEN ? (J=1)",ANTW1
			snlMsgLog("AD210 VAKUUM 0.1 BAR NICHT ERREICHT ! DIE REGENERATION WIEDERHOLEN ?");
			sprintf(AD_OPERATOR_ANT_JA_TEXT, "Regeneration Wiederholen ?");		pvPut(AD_OPERATOR_ANT_JA_TEXT);  
		}
		when (efTest(adsStop)) {
		}state ueberwachung
		// Ja (Regeneration Wiederholen)
		when((!DEBUG_ADS_EIN || debugNextState!=0) && AD_OPERATOR_ANT_JA){
		} state EVFE21_UZV284_SCHLIESSEN
		// Nein (Weiter)
		when((!DEBUG_ADS_EIN || debugNextState!=0) && AD_OPERATOR_ANT_NEIN){
		}state FLUT21
		exit{
			strcpy(AD_OPERATOR_ANT_JA_TEXT, "");		pvPut(AD_OPERATOR_ANT_JA_TEXT);
			pvStopMonitor(AD_OPERATOR_ANT_JA);
			pvStopMonitor(AD_OPERATOR_ANT_NEIN);  
		}
	}
	
	state EVFE21_UZV284_SCHLIESSEN{
		entry{
			if(DEBUG_ADS_EIN){ 
				debugNextState = 0; 							pvPut(debugNextState);
				sprintf(debugText, "EVFE21_UZV284_SCHLIESSEN");	pvPut(debugText);
				snlMsgLog("dbg:<EVFE21_UZV284_SCHLIESSEN>");
			}
			UZV284CMD = ZU;	pvPut(UZV284CMD);
			pvMonitor(UZV284zu);
		}

		when (efTest(adsStop)) {
		}state ueberwachung
		when((!DEBUG_ADS_EIN || debugNextState!=0) && UZV284zu!=0){
		} state EVFE21_UZV294_OEFFNEN
		when((!DEBUG_ADS_EIN || debugNextState!=0) && delay(60)){
			snlMsgLog("<EVFE21_UZV284_SCHLIESSEN>UZV284 - schliesst nicht");
		}state EVFE21_UZV284_SCHLIESSEN
		exit{
			pvStopMonitor(UZV284zu);
		}
	}
	
	state EVFE21_UZV294_OEFFNEN {	 
		entry{
			if(DEBUG_ADS_EIN){ 
				debugNextState = 0; 							pvPut(debugNextState);
				sprintf(debugText, "EVFE21_UZV294_OEFFNEN");	pvPut(debugText);
				snlMsgLog("dbg:<EVFE21_UZV294_OEFFNEN>");
			}
			UZV294CMD = AUF;	pvPut(UZV294CMD);
			pvMonitor(PI203);
		}
		when (efTest(adsStop)) {
		}state ueberwachung
		when((!DEBUG_ADS_EIN || debugNextState!=0) && PI203 > 5.){
		} state EVFE21_UZV294_SCHLIESSEN
		when((!DEBUG_ADS_EIN || debugNextState!=0) && delay(60)){
			snlMsgLog("<EVFE21_UZV294_OEFFNEN>PI203 - Druck zu niedrig");
		}state EVFE21_UZV294_OEFFNEN
		
		exit{
			pvStopMonitor(PI203);
		}
	}
	
	state EVFE21_UZV294_SCHLIESSEN {
		entry{
			if(DEBUG_ADS_EIN){ 
				debugNextState = 0; 							pvPut(debugNextState);
				sprintf(debugText, "EVFE21_UZV294_SCHLIESSEN");	pvPut(debugText);
				snlMsgLog("dbg:<EVFE21_UZV294_SCHLIESSEN>");
			}
			UZV294CMD = ZU;	pvPut(UZV294CMD);
			pvMonitor(UZV294zu);
		}
		when (efTest(adsStop)) {
		}state ueberwachung
		when((!DEBUG_ADS_EIN || debugNextState!=0) && UZV294zu!=0){
		} state DENT4
		when((!DEBUG_ADS_EIN || debugNextState!=0) && delay(60)){
			snlMsgLog("<EVFE21_UZV294_SCHLIESSEN>UZV294 - schliesst nicht");
		}state EVFE21_UZV294_SCHLIESSEN
		exit{
			pvStopMonitor(UZV294zu);
		}
	}
}
 // ---------------------------------- Adsorber Stop -----------------------------------------------------
ss AdsorberStop{

	state NEU_STARTEN{
		entry{
			if(DEBUG_ADSSTOP_EIN){
				debugNextState = 0; 				pvPut(debugNextState);
				sprintf(debugText, "NEU_STARTEN");	pvPut(debugText);
				snlMsgLog("dbg:<NEU_STARTEN>");
				pvMonitor(debugNextState);
			}
			AD_OPERATOR_STOP=0;		pvPut(AD_OPERATOR_STOP, SYNC);
			AD_OPERATOR_START=0;	pvPut(AD_OPERATOR_START);
			sprintf(AD_OPERATOR_START_TEXT,"Adsorberprogramm Aktiv"); pvPut(AD_OPERATOR_START_TEXT);
			sprintf(AD_OPERATOR_STOP_TEXT,"Regeneration Bereit");  pvPut(AD_OPERATOR_STOP_TEXT);
			efClear(adsStop);
		}
		when((!DEBUG_ADSSTOP_EIN || debugNextState!=0) && AD_OPERATOR_STOP){	
		} state STOPPEN
	}
	
	state STOPPEN {
		entry{
			if(DEBUG_ADSSTOP_EIN){
				debugNextState = 0; 				pvPut(debugNextState);
				sprintf(debugText, "NEU_STARTEN");	pvPut(debugText);
				snlMsgLog("dbg:<NEU_STARTEN>");
			}
			AD_OPERATOR_STOP=0;		pvPut(AD_OPERATOR_STOP);
			AD_OPERATOR_START=0;	pvPut(AD_OPERATOR_START, SYNC);
			sprintf(AD_OPERATOR_START_TEXT,"Regeneration Gestoppt"); pvPut(AD_OPERATOR_START_TEXT);
			sprintf(AD_OPERATOR_STOP_TEXT,"Regeneration Gestoppt");	 pvPut(AD_OPERATOR_STOP_TEXT);
			efSet(adsStop);
			
			HS151CMD = AUS;	pvPut(HS151CMD);
			
			UZV174CMD = ZU;	pvPut(UZV174CMD);
			UZV184CMD = ZU;	pvPut(UZV184CMD);
			UZV172CMD = ZU;	pvPut(UZV172CMD);
			UZV194CMD = ZU;	pvPut(UZV194CMD);
			
			UZV274CMD = ZU;	pvPut(UZV274CMD);
			UZV284CMD = ZU;	pvPut(UZV284CMD);
			UZV272CMD = ZU;	pvPut(UZV272CMD);
			UZV294CMD = ZU;	pvPut(UZV294CMD);

			UZV193CMD = ZU;	pvPut(UZV193CMD);
			UZV192CMD = ZU;	pvPut(UZV192CMD);
			UZV183CMD = ZU;	pvPut(UZV183CMD);
			UZV182CMD = ZU;	pvPut(UZV182CMD);

			UZV173CMD = ZU;	pvPut(UZV173CMD);
			UZV273CMD = ZU;	pvPut(UZV273CMD);

			UZV293CMD = ZU;	pvPut(UZV293CMD);
			UZV292CMD = ZU;	pvPut(UZV292CMD);
			UZV283CMD = ZU;	pvPut(UZV283CMD);
			UZV282CMD = ZU;	pvPut(UZV282CMD);
			pvMonitor(HS151aus);
			pvMonitor(UZV174zu);
			pvMonitor(UZV184zu);
			pvMonitor(UZV172zu);
			pvMonitor(UZV194zu);
			pvMonitor(UZV274zu);
			pvMonitor(UZV284zu);
			pvMonitor(UZV272zu);
			pvMonitor(UZV294zu);
			pvMonitor(UZV193zu);
			pvMonitor(UZV192zu);
			pvMonitor(UZV183zu);
			pvMonitor(UZV182zu);
			pvMonitor(UZV173zu);
			pvMonitor(UZV273zu);
			pvMonitor(UZV293zu);
			pvMonitor(UZV292zu);
			pvMonitor(UZV283zu);
			pvMonitor(UZV282zu);
		}
		when((!DEBUG_ADSSTOP_EIN || debugNextState!=0) && AD_OPERATOR_START){	
		} state NEU_STARTEN
		
		when(delay(60)
		&& !(HS151aus && UZV174zu && UZV184zu && UZV172zu && UZV194zu && UZV274zu && UZV284zu && UZV272zu && UZV294zu && UZV193zu
		&& UZV192zu && UZV183zu && UZV182zu && UZV173zu && UZV273zu && UZV293zu && UZV292zu && UZV283zu && UZV282zu)){	
			if(!HS151aus){
				snlMsgLog("<STOPPEN>HS151 - Gebläse schaltet nicht ab!");
			}
			if(!UZV174zu){
				snlMsgLog("<STOPPEN>UZV174 - schliesst nicht");
			}
			if(!UZV184zu){
				snlMsgLog("<STOPPEN>UZV184 - schliesst nicht");
			}
			if(!UZV172zu){
				snlMsgLog("<STOPPEN>UZV172 - schliesst nicht");
			}
			if(!UZV194zu){
				snlMsgLog("<STOPPEN>UZV194 - schliesst nicht");
			}
			if(!UZV274zu){
				snlMsgLog("<STOPPEN>UZV274 - schliesst nicht");
			}
			if(!UZV284zu){
				snlMsgLog("<STOPPEN>UZV284 - schliesst nicht");
			}
			if(!UZV272zu){
				snlMsgLog("<STOPPEN>UZV272 - schliesst nicht");
			}
			if(!UZV294zu){
				snlMsgLog("<STOPPEN>UZV294 - schliesst nicht");
			}
			if(!UZV193zu){
				snlMsgLog("<STOPPEN>UZV193 - schliesst nicht");
			}
			if(!UZV192zu){
				snlMsgLog("<STOPPEN>UZV192 - schliesst nicht");
			}
			if(!UZV183zu){
				snlMsgLog("<STOPPEN>UZV183 - schliesst nicht");
			}
			if(!UZV182zu){
				snlMsgLog("<STOPPEN>UZV182 - schliesst nicht");
			}
			if(!UZV173zu){
				snlMsgLog("<STOPPEN>UZV173 - schliesst nicht");
			}
			if(!UZV273zu){
				snlMsgLog("<STOPPEN>UZV273 - schliesst nicht");
			}
			if(!UZV293zu){
				snlMsgLog("<STOPPEN>UZV293 - schliesst nicht");
			}
			if(!UZV292zu){
				snlMsgLog("<STOPPEN>UZV292 - schliesst nicht");
			}
			if(!UZV283zu){
				snlMsgLog("<STOPPEN>UZV283 - schliesst nicht");
			}
			if(!UZV282zu){
				snlMsgLog("<STOPPEN>UZV282 - schliesst nicht");
			}
		} state STOPPEN
		exit{
			pvStopMonitor(HS151aus);
			pvStopMonitor(UZV174zu);
			pvStopMonitor(UZV184zu);
			pvStopMonitor(UZV172zu);
			pvStopMonitor(UZV194zu);
			pvStopMonitor(UZV274zu);
			pvStopMonitor(UZV284zu);
			pvStopMonitor(UZV272zu);
			pvStopMonitor(UZV294zu);
			pvStopMonitor(UZV193zu);
			pvStopMonitor(UZV192zu);
			pvStopMonitor(UZV183zu);
			pvStopMonitor(UZV182zu);
			pvStopMonitor(UZV173zu);
			pvStopMonitor(UZV273zu);
			pvStopMonitor(UZV293zu);
			pvStopMonitor(UZV292zu);
			pvStopMonitor(UZV283zu);
			pvStopMonitor(UZV282zu);
		}
	}
} 
