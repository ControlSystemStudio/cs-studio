/* SNC Version 2.0.12: Mon May 18 12:05:50 2009: /scratch/workspace/kstr12/generated/cueberw.i */

/* Event flags */
#define softstop	1

/* Program "cueberw" */
#include "seqCom.h"

#define NUM_SS 2
#define NUM_CHANNELS 20
#define NUM_EVENTS 1
#define NUM_QUEUES 0
#define NUM_LOCVARS 25

#define MAX_STRING_SIZE 40

#define ASYNC_OPT FALSE
#define CONN_OPT TRUE
#define DEBUG_OPT FALSE
#define MAIN_OPT TRUE
#define NEWEF_OPT TRUE
#define REENT_OPT TRUE

extern struct seqProgram cueberw;

/* Main program */
#include <string.h>
#include "epicsThread.h"
#include "iocsh.h"

int main(int argc,char *argv[]) {
    char * macro_def;
    epicsThreadId threadId;
    int callIocsh = 0;
    if(argc>1 && strcmp(argv[1],"-s")==0) {
        callIocsh=1;
        --argc; ++argv;
    }
    macro_def = (argc>1)?argv[1]:NULL;
    seqRegisterSequencerCommands();
    threadId = seq((void *)&cueberw, macro_def, 0);
    if(callIocsh) {
        iocsh(0);
    } else {
        epicsThreadExitMain();
    }
    return(0);
}

/* Variable declarations */
struct UserVar {
	char	text[40];
	char	cboxOK;
	char	cboxState;
	char	kstrOK;
	char	TK1run;
	char	SoftStop;
	char	SoftStopString[MAX_STRING_SIZE];
	char	pas;
	double	QI108;
	double	QI109;
	double	QI208;
	double	QI209;
	short	CV340OMSL;
	double	CV340;
	short	CV400OMSL;
	double	CV400;
	short	CV104OMSL;
	double	CV104;
	short	TC528SMSL;
	double	TC528;
	short	TC026SMSL;
	double	TC026;
	char	UZV122;
	char	UZV122ZU;
	char	CV106ZU;
};

/* C code definitions */
# line 21 "/scratch/workspace/kstr12/source/cueberw.st"
#include <string.h>
# line 22 "/scratch/workspace/kstr12/source/cueberw.st"
#include <snlMsgLog.h>

/* Entry handler */
static void entry_handler(SS_ID ssId, struct UserVar *pVar)
{
}

/* Code for state "ini" in state set "cueberw" */

/* Entry function for state "ini" in state set "cueberw" */
static void I_cueberw_ini(SS_ID ssId, struct UserVar *pVar)
{
/* Entry 1: */
# line 65 "/scratch/workspace/kstr12/source/cueberw.st"
	(pVar->cboxState) = 0;
# line 66 "/scratch/workspace/kstr12/source/cueberw.st"
	seq_pvPut(ssId, 1 /* cboxState */, 0);
# line 67 "/scratch/workspace/kstr12/source/cueberw.st"
	snlMsgLog("cueberw started");
# line 68 "/scratch/workspace/kstr12/source/cueberw.st"
	(pVar->SoftStop) = 0;
# line 69 "/scratch/workspace/kstr12/source/cueberw.st"
	seq_pvPut(ssId, 4 /* SoftStop */, 0);
}

/* Delay function for state "ini" in state set "cueberw" */
static void D_cueberw_ini(SS_ID ssId, struct UserVar *pVar)
{
# line 72 "/scratch/workspace/kstr12/source/cueberw.st"
	seq_delayInit(ssId, 0, (2));
}

/* Event function for state "ini" in state set "cueberw" */
static long E_cueberw_ini(SS_ID ssId, struct UserVar *pVar, short *pTransNum, short *pNextState)
{
# line 72 "/scratch/workspace/kstr12/source/cueberw.st"
	if (seq_delay(ssId, 0))
	{
		*pNextState = 1;
		*pTransNum = 0;
		return TRUE;
	}
	return FALSE;
}

/* Action function for state "ini" in state set "cueberw" */
static void A_cueberw_ini(SS_ID ssId, struct UserVar *pVar, short transNum)
{
	switch(transNum)
	{
	case 0:
		{
		}
		return;
	}
}
/* Code for state "not_ok" in state set "cueberw" */

/* Entry function for state "not_ok" in state set "cueberw" */
static void I_cueberw_not_ok(SS_ID ssId, struct UserVar *pVar)
{
/* Entry 1: */
# line 77 "/scratch/workspace/kstr12/source/cueberw.st"
	sprintf((pVar->SoftStopString), "Box stopped");
# line 78 "/scratch/workspace/kstr12/source/cueberw.st"
	seq_pvPut(ssId, 5 /* SoftStopString */, 0);
# line 79 "/scratch/workspace/kstr12/source/cueberw.st"
	(pVar->cboxState) = 1;
# line 80 "/scratch/workspace/kstr12/source/cueberw.st"
	seq_pvPut(ssId, 1 /* cboxState */, 0);
# line 81 "/scratch/workspace/kstr12/source/cueberw.st"
	snlMsgLog("cueberw not_ok");
}

/* Delay function for state "not_ok" in state set "cueberw" */
static void D_cueberw_not_ok(SS_ID ssId, struct UserVar *pVar)
{
# line 87 "/scratch/workspace/kstr12/source/cueberw.st"
}

/* Event function for state "not_ok" in state set "cueberw" */
static long E_cueberw_not_ok(SS_ID ssId, struct UserVar *pVar, short *pTransNum, short *pNextState)
{
# line 87 "/scratch/workspace/kstr12/source/cueberw.st"
	if (((pVar->SoftStop) == 0) && ((pVar->pas) == 1) && ((pVar->kstrOK) == 1))
	{
		*pNextState = 2;
		*pTransNum = 0;
		return TRUE;
	}
	return FALSE;
}

/* Action function for state "not_ok" in state set "cueberw" */
static void A_cueberw_not_ok(SS_ID ssId, struct UserVar *pVar, short transNum)
{
	switch(transNum)
	{
	case 0:
		{
		}
		return;
	}
}
/* Code for state "ok" in state set "cueberw" */

/* Entry function for state "ok" in state set "cueberw" */
static void I_cueberw_ok(SS_ID ssId, struct UserVar *pVar)
{
/* Entry 1: */
# line 92 "/scratch/workspace/kstr12/source/cueberw.st"
	(pVar->cboxState) = 2;
# line 93 "/scratch/workspace/kstr12/source/cueberw.st"
	seq_pvPut(ssId, 1 /* cboxState */, 0);
# line 94 "/scratch/workspace/kstr12/source/cueberw.st"
	snlMsgLog("cueberw ok");
# line 95 "/scratch/workspace/kstr12/source/cueberw.st"
	(pVar->cboxOK) = 1;
# line 96 "/scratch/workspace/kstr12/source/cueberw.st"
	seq_pvPut(ssId, 0 /* cboxOK */, 0);
# line 97 "/scratch/workspace/kstr12/source/cueberw.st"
	sprintf((pVar->SoftStopString), "SoftStop");
# line 98 "/scratch/workspace/kstr12/source/cueberw.st"
	seq_pvPut(ssId, 5 /* SoftStopString */, 0);
# line 99 "/scratch/workspace/kstr12/source/cueberw.st"
	seq_efClear(ssId, softstop);
}

/* Delay function for state "ok" in state set "cueberw" */
static void D_cueberw_ok(SS_ID ssId, struct UserVar *pVar)
{
# line 107 "/scratch/workspace/kstr12/source/cueberw.st"
# line 114 "/scratch/workspace/kstr12/source/cueberw.st"
# line 117 "/scratch/workspace/kstr12/source/cueberw.st"
# line 120 "/scratch/workspace/kstr12/source/cueberw.st"
}

/* Event function for state "ok" in state set "cueberw" */
static long E_cueberw_ok(SS_ID ssId, struct UserVar *pVar, short *pTransNum, short *pNextState)
{
# line 107 "/scratch/workspace/kstr12/source/cueberw.st"
	if ((pVar->SoftStop) && (pVar->CV106ZU))
	{
		*pNextState = 3;
		*pTransNum = 0;
		return TRUE;
	}
# line 114 "/scratch/workspace/kstr12/source/cueberw.st"
	if ((pVar->SoftStop) && !(pVar->CV106ZU))
	{
		*pNextState = 4;
		*pTransNum = 1;
		return TRUE;
	}
# line 117 "/scratch/workspace/kstr12/source/cueberw.st"
	if ((pVar->pas) != 1)
	{
		*pNextState = 6;
		*pTransNum = 2;
		return TRUE;
	}
# line 120 "/scratch/workspace/kstr12/source/cueberw.st"
	if ((pVar->kstrOK) != 1)
	{
		*pNextState = 6;
		*pTransNum = 3;
		return TRUE;
	}
	return FALSE;
}

/* Action function for state "ok" in state set "cueberw" */
static void A_cueberw_ok(SS_ID ssId, struct UserVar *pVar, short transNum)
{
	switch(transNum)
	{
	case 0:
		{
# line 102 "/scratch/workspace/kstr12/source/cueberw.st"
			(pVar->SoftStop) = 0;
			seq_pvPut(ssId, 4 /* SoftStop */, 0);
			snlMsgLog("Coldbox SoftStop?");
			sprintf((pVar->SoftStopString), "Coldbox Stop?\nJa: druecke nochmal!");
			seq_pvPut(ssId, 5 /* SoftStopString */, 0);
		}
		return;
	case 1:
		{
# line 109 "/scratch/workspace/kstr12/source/cueberw.st"
			(pVar->SoftStop) = 0;
			seq_pvPut(ssId, 4 /* SoftStop */, 0);
			snlMsgLog("Turbinenkreise laufen noch: %d", (pVar->CV106ZU));
			sprintf((pVar->SoftStopString), "Tu-Kreise laufen\nTrotzdem Coldbox Stop?");
			seq_pvPut(ssId, 5 /* SoftStopString */, 0);
		}
		return;
	case 2:
		{
# line 116 "/scratch/workspace/kstr12/source/cueberw.st"
			snlMsgLog("Steuerluft ausgefallen");
		}
		return;
	case 3:
		{
# line 119 "/scratch/workspace/kstr12/source/cueberw.st"
			snlMsgLog("Kompressorstrasse ausgefallen");
		}
		return;
	}
}
/* Code for state "softstop" in state set "cueberw" */

/* Entry function for state "softstop" in state set "cueberw" */
static void I_cueberw_softstop(SS_ID ssId, struct UserVar *pVar)
{
/* Entry 1: */
# line 125 "/scratch/workspace/kstr12/source/cueberw.st"
	(pVar->cboxState) = 3;
# line 126 "/scratch/workspace/kstr12/source/cueberw.st"
	seq_pvPut(ssId, 1 /* cboxState */, 0);
}

/* Delay function for state "softstop" in state set "cueberw" */
static void D_cueberw_softstop(SS_ID ssId, struct UserVar *pVar)
{
# line 135 "/scratch/workspace/kstr12/source/cueberw.st"
# line 141 "/scratch/workspace/kstr12/source/cueberw.st"
	seq_delayInit(ssId, 0, (30));
}

/* Event function for state "softstop" in state set "cueberw" */
static long E_cueberw_softstop(SS_ID ssId, struct UserVar *pVar, short *pTransNum, short *pNextState)
{
# line 135 "/scratch/workspace/kstr12/source/cueberw.st"
	if ((pVar->SoftStop))
	{
		*pNextState = 6;
		*pTransNum = 0;
		return TRUE;
	}
# line 141 "/scratch/workspace/kstr12/source/cueberw.st"
	if (seq_delay(ssId, 0) && (pVar->SoftStop) == 0)
	{
		*pNextState = 2;
		*pTransNum = 1;
		return TRUE;
	}
	return FALSE;
}

/* Action function for state "softstop" in state set "cueberw" */
static void A_cueberw_softstop(SS_ID ssId, struct UserVar *pVar, short transNum)
{
	switch(transNum)
	{
	case 0:
		{
# line 129 "/scratch/workspace/kstr12/source/cueberw.st"
			(pVar->SoftStop) = 0;
			seq_pvPut(ssId, 4 /* SoftStop */, 0);
			sprintf((pVar->SoftStopString), "SoftStop bestaetigt");
			seq_pvPut(ssId, 5 /* SoftStopString */, 0);
			snlMsgLog("Softstop bestaetigt");
			seq_efSet(ssId, softstop);
		}
		return;
	case 1:
		{
# line 138 "/scratch/workspace/kstr12/source/cueberw.st"
			sprintf((pVar->SoftStopString), "Kein SoftStop!");
			seq_pvPut(ssId, 5 /* SoftStopString */, 0);
			snlMsgLog("Softstop nicht bestaetigt");
		}
		return;
	}
}
/* Code for state "check_turbinenkreise" in state set "cueberw" */

/* Entry function for state "check_turbinenkreise" in state set "cueberw" */
static void I_cueberw_check_turbinenkreise(SS_ID ssId, struct UserVar *pVar)
{
/* Entry 1: */
# line 146 "/scratch/workspace/kstr12/source/cueberw.st"
	(pVar->cboxState) = 4;
# line 147 "/scratch/workspace/kstr12/source/cueberw.st"
	seq_pvPut(ssId, 1 /* cboxState */, 0);
}

/* Delay function for state "check_turbinenkreise" in state set "cueberw" */
static void D_cueberw_check_turbinenkreise(SS_ID ssId, struct UserVar *pVar)
{
# line 156 "/scratch/workspace/kstr12/source/cueberw.st"
# line 162 "/scratch/workspace/kstr12/source/cueberw.st"
	seq_delayInit(ssId, 0, (30));
}

/* Event function for state "check_turbinenkreise" in state set "cueberw" */
static long E_cueberw_check_turbinenkreise(SS_ID ssId, struct UserVar *pVar, short *pTransNum, short *pNextState)
{
# line 156 "/scratch/workspace/kstr12/source/cueberw.st"
	if ((pVar->SoftStop))
	{
		*pNextState = 5;
		*pTransNum = 0;
		return TRUE;
	}
# line 162 "/scratch/workspace/kstr12/source/cueberw.st"
	if (seq_delay(ssId, 0) && (pVar->SoftStop) == 0)
	{
		*pNextState = 2;
		*pTransNum = 1;
		return TRUE;
	}
	return FALSE;
}

/* Action function for state "check_turbinenkreise" in state set "cueberw" */
static void A_cueberw_check_turbinenkreise(SS_ID ssId, struct UserVar *pVar, short transNum)
{
	switch(transNum)
	{
	case 0:
		{
# line 150 "/scratch/workspace/kstr12/source/cueberw.st"
			(pVar->SoftStop) = 0;
			seq_pvPut(ssId, 4 /* SoftStop */, 0);
			sprintf((pVar->SoftStopString), "Stop Tu-Kreise\nbestaetigt");
			seq_pvPut(ssId, 5 /* SoftStopString */, 0);
			snlMsgLog("Stop Tu-Kreise bestaetigt");
			seq_efSet(ssId, softstop);
		}
		return;
	case 1:
		{
# line 159 "/scratch/workspace/kstr12/source/cueberw.st"
			sprintf((pVar->SoftStopString), "Kein Tu_Kreise-Stop\nKein SoftStop!");
			seq_pvPut(ssId, 5 /* SoftStopString */, 0);
			snlMsgLog("Stop Tu-Kreise nicht bestaetigt");
		}
		return;
	}
}
/* Code for state "stop_turbinenkreise" in state set "cueberw" */

/* Entry function for state "stop_turbinenkreise" in state set "cueberw" */
static void I_cueberw_stop_turbinenkreise(SS_ID ssId, struct UserVar *pVar)
{
/* Entry 1: */
# line 166 "/scratch/workspace/kstr12/source/cueberw.st"
	(pVar->cboxState) = 5;
# line 167 "/scratch/workspace/kstr12/source/cueberw.st"
	seq_pvPut(ssId, 1 /* cboxState */, 0);
# line 168 "/scratch/workspace/kstr12/source/cueberw.st"
	(pVar->TK1run) = 0;
# line 169 "/scratch/workspace/kstr12/source/cueberw.st"
	seq_pvPut(ssId, 3 /* TK1run */, 0);
}

/* Delay function for state "stop_turbinenkreise" in state set "cueberw" */
static void D_cueberw_stop_turbinenkreise(SS_ID ssId, struct UserVar *pVar)
{
# line 173 "/scratch/workspace/kstr12/source/cueberw.st"
# line 176 "/scratch/workspace/kstr12/source/cueberw.st"
	seq_delayInit(ssId, 0, (60));
}

/* Event function for state "stop_turbinenkreise" in state set "cueberw" */
static long E_cueberw_stop_turbinenkreise(SS_ID ssId, struct UserVar *pVar, short *pTransNum, short *pNextState)
{
# line 173 "/scratch/workspace/kstr12/source/cueberw.st"
	if ((pVar->CV106ZU))
	{
		*pNextState = 6;
		*pTransNum = 0;
		return TRUE;
	}
# line 176 "/scratch/workspace/kstr12/source/cueberw.st"
	if (seq_delay(ssId, 0) && !(pVar->CV106ZU))
	{
		*pNextState = 5;
		*pTransNum = 1;
		return TRUE;
	}
	return FALSE;
}

/* Action function for state "stop_turbinenkreise" in state set "cueberw" */
static void A_cueberw_stop_turbinenkreise(SS_ID ssId, struct UserVar *pVar, short transNum)
{
	switch(transNum)
	{
	case 0:
		{
# line 172 "/scratch/workspace/kstr12/source/cueberw.st"
			snlMsgLog("Turbinenkreise sind geschlossen!");
		}
		return;
	case 1:
		{
# line 175 "/scratch/workspace/kstr12/source/cueberw.st"
			snlMsgLog("Turbinenkreise immer noch nicht geschlossen!");
		}
		return;
	}
}
/* Code for state "coldbox_ausschalten" in state set "cueberw" */

/* Entry function for state "coldbox_ausschalten" in state set "cueberw" */
static void I_cueberw_coldbox_ausschalten(SS_ID ssId, struct UserVar *pVar)
{
/* Entry 1: */
# line 181 "/scratch/workspace/kstr12/source/cueberw.st"
	(pVar->cboxState) = 6;
# line 182 "/scratch/workspace/kstr12/source/cueberw.st"
	seq_pvPut(ssId, 1 /* cboxState */, 0);
# line 183 "/scratch/workspace/kstr12/source/cueberw.st"
	(pVar->cboxOK) = 0;
# line 184 "/scratch/workspace/kstr12/source/cueberw.st"
	seq_pvPut(ssId, 0 /* cboxOK */, 0);
# line 188 "/scratch/workspace/kstr12/source/cueberw.st"
	(pVar->UZV122) = 0;
# line 189 "/scratch/workspace/kstr12/source/cueberw.st"
	seq_pvPut(ssId, 17 /* UZV122 */, 0);
# line 191 "/scratch/workspace/kstr12/source/cueberw.st"
	(pVar->CV340OMSL) = 0;
# line 192 "/scratch/workspace/kstr12/source/cueberw.st"
	(pVar->CV340) = 0.0;
# line 193 "/scratch/workspace/kstr12/source/cueberw.st"
	seq_pvPut(ssId, 7 /* CV340OMSL */, 0);
# line 194 "/scratch/workspace/kstr12/source/cueberw.st"
	seq_pvPut(ssId, 8 /* CV340 */, 0);
# line 195 "/scratch/workspace/kstr12/source/cueberw.st"
	(pVar->CV400OMSL) = 0;
# line 196 "/scratch/workspace/kstr12/source/cueberw.st"
	(pVar->CV400) = 0.0;
# line 197 "/scratch/workspace/kstr12/source/cueberw.st"
	seq_pvPut(ssId, 9 /* CV400OMSL */, 0);
# line 198 "/scratch/workspace/kstr12/source/cueberw.st"
	seq_pvPut(ssId, 10 /* CV400 */, 0);
# line 199 "/scratch/workspace/kstr12/source/cueberw.st"
	(pVar->CV104OMSL) = 0;
# line 200 "/scratch/workspace/kstr12/source/cueberw.st"
	(pVar->CV104) = 0.0;
# line 201 "/scratch/workspace/kstr12/source/cueberw.st"
	seq_pvPut(ssId, 11 /* CV104OMSL */, 0);
# line 202 "/scratch/workspace/kstr12/source/cueberw.st"
	seq_pvPut(ssId, 12 /* CV104 */, 0);
# line 204 "/scratch/workspace/kstr12/source/cueberw.st"
	(pVar->TC528SMSL) = 0;
# line 205 "/scratch/workspace/kstr12/source/cueberw.st"
	(pVar->TC528) = 0.0;
# line 206 "/scratch/workspace/kstr12/source/cueberw.st"
	seq_pvPut(ssId, 13 /* TC528SMSL */, 0);
# line 207 "/scratch/workspace/kstr12/source/cueberw.st"
	seq_pvPut(ssId, 14 /* TC528 */, 0);
# line 208 "/scratch/workspace/kstr12/source/cueberw.st"
	(pVar->TC026SMSL) = 0;
# line 209 "/scratch/workspace/kstr12/source/cueberw.st"
	(pVar->TC026) = 0.0;
# line 210 "/scratch/workspace/kstr12/source/cueberw.st"
	seq_pvPut(ssId, 15 /* TC026SMSL */, 0);
# line 211 "/scratch/workspace/kstr12/source/cueberw.st"
	seq_pvPut(ssId, 16 /* TC026 */, 0);
}

/* Delay function for state "coldbox_ausschalten" in state set "cueberw" */
static void D_cueberw_coldbox_ausschalten(SS_ID ssId, struct UserVar *pVar)
{
# line 215 "/scratch/workspace/kstr12/source/cueberw.st"
	seq_delayInit(ssId, 0, (5));
# line 221 "/scratch/workspace/kstr12/source/cueberw.st"
}

/* Event function for state "coldbox_ausschalten" in state set "cueberw" */
static long E_cueberw_coldbox_ausschalten(SS_ID ssId, struct UserVar *pVar, short *pTransNum, short *pNextState)
{
# line 215 "/scratch/workspace/kstr12/source/cueberw.st"
	if (seq_delay(ssId, 0) && !seq_efTest(ssId, softstop))
	{
		*pNextState = 7;
		*pTransNum = 0;
		return TRUE;
	}
# line 221 "/scratch/workspace/kstr12/source/cueberw.st"
	if (seq_efTest(ssId, softstop) && (pVar->UZV122ZU))
	{
		*pNextState = 7;
		*pTransNum = 1;
		return TRUE;
	}
	return FALSE;
}

/* Action function for state "coldbox_ausschalten" in state set "cueberw" */
static void A_cueberw_coldbox_ausschalten(SS_ID ssId, struct UserVar *pVar, short transNum)
{
	switch(transNum)
	{
	case 0:
		{
		}
		return;
	case 1:
		{
# line 217 "/scratch/workspace/kstr12/source/cueberw.st"
			(pVar->cboxState) = 7;
			seq_pvPut(ssId, 1 /* cboxState */, 0);
# line 220 "/scratch/workspace/kstr12/source/cueberw.st"
			snlMsgLog("Coldbox wird abgesaugt");
		}
		return;
	}
}
/* Code for state "stopit" in state set "cueberw" */

/* Entry function for state "stopit" in state set "cueberw" */
static void I_cueberw_stopit(SS_ID ssId, struct UserVar *pVar)
{
/* Entry 1: */
# line 226 "/scratch/workspace/kstr12/source/cueberw.st"
	(pVar->cboxState) = 8;
# line 227 "/scratch/workspace/kstr12/source/cueberw.st"
	seq_pvPut(ssId, 1 /* cboxState */, 0);
}

/* Delay function for state "stopit" in state set "cueberw" */
static void D_cueberw_stopit(SS_ID ssId, struct UserVar *pVar)
{
# line 230 "/scratch/workspace/kstr12/source/cueberw.st"
}

/* Event function for state "stopit" in state set "cueberw" */
static long E_cueberw_stopit(SS_ID ssId, struct UserVar *pVar, short *pTransNum, short *pNextState)
{
# line 230 "/scratch/workspace/kstr12/source/cueberw.st"
	if (1)
	{
		*pNextState = 1;
		*pTransNum = 0;
		return TRUE;
	}
	return FALSE;
}

/* Action function for state "stopit" in state set "cueberw" */
static void A_cueberw_stopit(SS_ID ssId, struct UserVar *pVar, short transNum)
{
	switch(transNum)
	{
	case 0:
		{
		}
		return;
	}
}
/* Code for state "cbox_off" in state set "ads_ueberw" */

/* Entry function for state "cbox_off" in state set "ads_ueberw" */
static void I_ads_ueberw_cbox_off(SS_ID ssId, struct UserVar *pVar)
{
/* Entry 1: */
# line 237 "/scratch/workspace/kstr12/source/cueberw.st"
	snlMsgLog("cbox off -> no adsorber");
}

/* Delay function for state "cbox_off" in state set "ads_ueberw" */
static void D_ads_ueberw_cbox_off(SS_ID ssId, struct UserVar *pVar)
{
# line 240 "/scratch/workspace/kstr12/source/cueberw.st"
}

/* Event function for state "cbox_off" in state set "ads_ueberw" */
static long E_ads_ueberw_cbox_off(SS_ID ssId, struct UserVar *pVar, short *pTransNum, short *pNextState)
{
# line 240 "/scratch/workspace/kstr12/source/cueberw.st"
	if ((pVar->cboxOK) == 1)
	{
		*pNextState = 1;
		*pTransNum = 0;
		return TRUE;
	}
	return FALSE;
}

/* Action function for state "cbox_off" in state set "ads_ueberw" */
static void A_ads_ueberw_cbox_off(SS_ID ssId, struct UserVar *pVar, short transNum)
{
	switch(transNum)
	{
	case 0:
		{
		}
		return;
	}
}
/* Code for state "cbox_on" in state set "ads_ueberw" */

/* Entry function for state "cbox_on" in state set "ads_ueberw" */
static void I_ads_ueberw_cbox_on(SS_ID ssId, struct UserVar *pVar)
{
/* Entry 1: */
# line 245 "/scratch/workspace/kstr12/source/cueberw.st"
	snlMsgLog("cbox off -> check adsorbers");
}

/* Delay function for state "cbox_on" in state set "ads_ueberw" */
static void D_ads_ueberw_cbox_on(SS_ID ssId, struct UserVar *pVar)
{
# line 248 "/scratch/workspace/kstr12/source/cueberw.st"
# line 252 "/scratch/workspace/kstr12/source/cueberw.st"
# line 256 "/scratch/workspace/kstr12/source/cueberw.st"
# line 260 "/scratch/workspace/kstr12/source/cueberw.st"
# line 264 "/scratch/workspace/kstr12/source/cueberw.st"
}

/* Event function for state "cbox_on" in state set "ads_ueberw" */
static long E_ads_ueberw_cbox_on(SS_ID ssId, struct UserVar *pVar, short *pTransNum, short *pNextState)
{
# line 248 "/scratch/workspace/kstr12/source/cueberw.st"
	if ((pVar->cboxOK) != 1)
	{
		*pNextState = 0;
		*pTransNum = 0;
		return TRUE;
	}
# line 252 "/scratch/workspace/kstr12/source/cueberw.st"
	if ((pVar->QI108) > 20.)
	{
		*pNextState = 1;
		*pTransNum = 1;
		return TRUE;
	}
# line 256 "/scratch/workspace/kstr12/source/cueberw.st"
	if ((pVar->QI109) > 20.)
	{
		*pNextState = 1;
		*pTransNum = 2;
		return TRUE;
	}
# line 260 "/scratch/workspace/kstr12/source/cueberw.st"
	if ((pVar->QI208) > 20.)
	{
		*pNextState = 1;
		*pTransNum = 3;
		return TRUE;
	}
# line 264 "/scratch/workspace/kstr12/source/cueberw.st"
	if ((pVar->QI209) > 20.)
	{
		*pNextState = 1;
		*pTransNum = 4;
		return TRUE;
	}
	return FALSE;
}

/* Action function for state "cbox_on" in state set "ads_ueberw" */
static void A_ads_ueberw_cbox_on(SS_ID ssId, struct UserVar *pVar, short transNum)
{
	switch(transNum)
	{
	case 0:
		{
		}
		return;
	case 1:
		{
# line 250 "/scratch/workspace/kstr12/source/cueberw.st"
			snlMsgLog(" * * * Analyse AD100 > 20 ppm * * *");
		}
		return;
	case 2:
		{
# line 254 "/scratch/workspace/kstr12/source/cueberw.st"
			snlMsgLog(" * * * Analyse AD110 > 20 ppm * * *");
		}
		return;
	case 3:
		{
# line 258 "/scratch/workspace/kstr12/source/cueberw.st"
			snlMsgLog(" * * * Analyse AD200 > 20 ppm * * *");
		}
		return;
	case 4:
		{
# line 262 "/scratch/workspace/kstr12/source/cueberw.st"
			snlMsgLog(" * * * Analyse AD210 > 20 ppm * * *");
		}
		return;
	}
}

/* Exit handler */
static void exit_handler(SS_ID ssId, struct UserVar *pVar)
{
}

/************************ Tables ***********************/

/* Database Blocks */
static struct seqChan seqChan[NUM_CHANNELS] = {
  {"KS2:SNL:CUE:CBOXOK_bi", (void *)OFFSET(struct UserVar, cboxOK), "cboxOK", 
    "char", 1, 2, 0, 0, 0, 0, 0},

  {"KS2:SNL:CUE:CUEBERW_mbbi", (void *)OFFSET(struct UserVar, cboxState), "cboxState", 
    "char", 1, 3, 0, 0, 0, 0, 0},

  {"KS2:SNL:KUE:KSTROK_bi", (void *)OFFSET(struct UserVar, kstrOK), "kstrOK", 
    "char", 1, 4, 0, 1, 0, 0, 0},

  {"KS2:SNL:TK1:RUN_bi", (void *)OFFSET(struct UserVar, TK1run), "TK1run", 
    "char", 1, 5, 0, 0, 0, 0, 0},

  {"KS2:SNL:CUE:SOFTSTOP_bi", (void *)OFFSET(struct UserVar, SoftStop), "SoftStop", 
    "char", 1, 6, 0, 1, 0, 0, 0},

  {"KS2:SNL:CUE:SOFTSTOP_si", (void *)OFFSET(struct UserVar, SoftStopString[0]), "SoftStopString", 
    "string", 1, 7, 0, 0, 0, 0, 0},

  {"42PAS700_bi", (void *)OFFSET(struct UserVar, pas), "pas", 
    "char", 1, 8, 0, 1, 0, 0, 0},

  {"42CV340_ao.OMSL", (void *)OFFSET(struct UserVar, CV340OMSL), "CV340OMSL", 
    "short", 1, 9, 0, 0, 0, 0, 0},

  {"42CV340_ao", (void *)OFFSET(struct UserVar, CV340), "CV340", 
    "double", 1, 10, 0, 0, 0, 0, 0},

  {"42CV400_ao.OMSL", (void *)OFFSET(struct UserVar, CV400OMSL), "CV400OMSL", 
    "short", 1, 11, 0, 0, 0, 0, 0},

  {"42CV400_ao", (void *)OFFSET(struct UserVar, CV400), "CV400", 
    "double", 1, 12, 0, 0, 0, 0, 0},

  {"42CV104_ao.OMSL", (void *)OFFSET(struct UserVar, CV104OMSL), "CV104OMSL", 
    "short", 1, 13, 0, 0, 0, 0, 0},

  {"42CV104_ao", (void *)OFFSET(struct UserVar, CV104), "CV104", 
    "double", 1, 14, 0, 0, 0, 0, 0},

  {"42TC528_pid.SMSL", (void *)OFFSET(struct UserVar, TC528SMSL), "TC528SMSL", 
    "short", 1, 15, 0, 0, 0, 0, 0},

  {"42TC528_pid.SOUT", (void *)OFFSET(struct UserVar, TC528), "TC528", 
    "double", 1, 16, 0, 0, 0, 0, 0},

  {"42TC026_pid.SMSL", (void *)OFFSET(struct UserVar, TC026SMSL), "TC026SMSL", 
    "short", 1, 17, 0, 0, 0, 0, 0},

  {"42TC026_pid.SOUT", (void *)OFFSET(struct UserVar, TC026), "TC026", 
    "double", 1, 18, 0, 0, 0, 0, 0},

  {"32UZV122_dlog.CMD", (void *)OFFSET(struct UserVar, UZV122), "UZV122", 
    "char", 1, 19, 0, 0, 0, 0, 0},

  {"32G122L_bi", (void *)OFFSET(struct UserVar, UZV122ZU), "UZV122ZU", 
    "char", 1, 20, 0, 1, 0, 0, 0},

  {"42G106L_bi", (void *)OFFSET(struct UserVar, CV106ZU), "CV106ZU", 
    "char", 1, 21, 0, 1, 0, 0, 0},

};

/* Local Variables (some may also be connected to PVs above)
 *   ... does not include escaped declarations
 */
static struct seqVar seqVar[NUM_LOCVARS] = {
  /* name type_i type_s class dim1 dim2 initial address */
  { "text",	  1, "char",	 1,  40,   1, NULL, (void *)OFFSET(struct UserVar, text[0]) },
  { "cboxOK",	  1, "char",	 0,   1,   1, NULL, (void *)OFFSET(struct UserVar, cboxOK) },
  { "cboxState",	  1, "char",	 0,   1,   1, NULL, (void *)OFFSET(struct UserVar, cboxState) },
  { "kstrOK",	  1, "char",	 0,   1,   1, NULL, (void *)OFFSET(struct UserVar, kstrOK) },
  { "TK1run",	  1, "char",	 0,   1,   1, NULL, (void *)OFFSET(struct UserVar, TK1run) },
  { "SoftStop",	  1, "char",	 0,   1,   1, NULL, (void *)OFFSET(struct UserVar, SoftStop) },
  { "SoftStopString",	  7, "string",	 0,   1,   1, NULL, (void *)OFFSET(struct UserVar, SoftStopString[0]) },
  { "pas",	  1, "char",	 0,   1,   1, NULL, (void *)OFFSET(struct UserVar, pas) },
  { "QI108",	  6, "double",	 0,   1,   1, NULL, (void *)OFFSET(struct UserVar, QI108) },
  { "QI109",	  6, "double",	 0,   1,   1, NULL, (void *)OFFSET(struct UserVar, QI109) },
  { "QI208",	  6, "double",	 0,   1,   1, NULL, (void *)OFFSET(struct UserVar, QI208) },
  { "QI209",	  6, "double",	 0,   1,   1, NULL, (void *)OFFSET(struct UserVar, QI209) },
  { "CV340OMSL",	  2, "short",	 0,   1,   1, NULL, (void *)OFFSET(struct UserVar, CV340OMSL) },
  { "CV340",	  6, "double",	 0,   1,   1, NULL, (void *)OFFSET(struct UserVar, CV340) },
  { "CV400OMSL",	  2, "short",	 0,   1,   1, NULL, (void *)OFFSET(struct UserVar, CV400OMSL) },
  { "CV400",	  6, "double",	 0,   1,   1, NULL, (void *)OFFSET(struct UserVar, CV400) },
  { "CV104OMSL",	  2, "short",	 0,   1,   1, NULL, (void *)OFFSET(struct UserVar, CV104OMSL) },
  { "CV104",	  6, "double",	 0,   1,   1, NULL, (void *)OFFSET(struct UserVar, CV104) },
  { "TC528SMSL",	  2, "short",	 0,   1,   1, NULL, (void *)OFFSET(struct UserVar, TC528SMSL) },
  { "TC528",	  6, "double",	 0,   1,   1, NULL, (void *)OFFSET(struct UserVar, TC528) },
  { "TC026SMSL",	  2, "short",	 0,   1,   1, NULL, (void *)OFFSET(struct UserVar, TC026SMSL) },
  { "TC026",	  6, "double",	 0,   1,   1, NULL, (void *)OFFSET(struct UserVar, TC026) },
  { "UZV122",	  1, "char",	 0,   1,   1, NULL, (void *)OFFSET(struct UserVar, UZV122) },
  { "UZV122ZU",	  1, "char",	 0,   1,   1, NULL, (void *)OFFSET(struct UserVar, UZV122ZU) },
  { "CV106ZU",	  1, "char",	 0,   1,   1, NULL, (void *)OFFSET(struct UserVar, CV106ZU) }
};

/* Event masks for state set cueberw */
	/* Event mask for state ini: */
static bitMask	EM_cueberw_ini[] = {
	0x00000000,
};
	/* Event mask for state not_ok: */
static bitMask	EM_cueberw_not_ok[] = {
	0x00000150,
};
	/* Event mask for state ok: */
static bitMask	EM_cueberw_ok[] = {
	0x00200150,
};
	/* Event mask for state softstop: */
static bitMask	EM_cueberw_softstop[] = {
	0x00000040,
};
	/* Event mask for state check_turbinenkreise: */
static bitMask	EM_cueberw_check_turbinenkreise[] = {
	0x00000040,
};
	/* Event mask for state stop_turbinenkreise: */
static bitMask	EM_cueberw_stop_turbinenkreise[] = {
	0x00200000,
};
	/* Event mask for state coldbox_ausschalten: */
static bitMask	EM_cueberw_coldbox_ausschalten[] = {
	0x00100002,
};
	/* Event mask for state stopit: */
static bitMask	EM_cueberw_stopit[] = {
	0x00000000,
};

/* State Blocks */

static struct seqState state_cueberw[] = {
	/* State "ini" */ {
	/* state name */       "ini",
	/* action function */ (ACTION_FUNC) A_cueberw_ini,
	/* event function */  (EVENT_FUNC) E_cueberw_ini,
	/* delay function */   (DELAY_FUNC) D_cueberw_ini,
	/* entry function */   (ENTRY_FUNC) I_cueberw_ini,
	/* exit function */   (EXIT_FUNC) 0,
	/* event mask array */ EM_cueberw_ini,
	/* state options */   (0)},

	/* State "not_ok" */ {
	/* state name */       "not_ok",
	/* action function */ (ACTION_FUNC) A_cueberw_not_ok,
	/* event function */  (EVENT_FUNC) E_cueberw_not_ok,
	/* delay function */   (DELAY_FUNC) D_cueberw_not_ok,
	/* entry function */   (ENTRY_FUNC) I_cueberw_not_ok,
	/* exit function */   (EXIT_FUNC) 0,
	/* event mask array */ EM_cueberw_not_ok,
	/* state options */   (0)},

	/* State "ok" */ {
	/* state name */       "ok",
	/* action function */ (ACTION_FUNC) A_cueberw_ok,
	/* event function */  (EVENT_FUNC) E_cueberw_ok,
	/* delay function */   (DELAY_FUNC) D_cueberw_ok,
	/* entry function */   (ENTRY_FUNC) I_cueberw_ok,
	/* exit function */   (EXIT_FUNC) 0,
	/* event mask array */ EM_cueberw_ok,
	/* state options */   (0)},

	/* State "softstop" */ {
	/* state name */       "softstop",
	/* action function */ (ACTION_FUNC) A_cueberw_softstop,
	/* event function */  (EVENT_FUNC) E_cueberw_softstop,
	/* delay function */   (DELAY_FUNC) D_cueberw_softstop,
	/* entry function */   (ENTRY_FUNC) I_cueberw_softstop,
	/* exit function */   (EXIT_FUNC) 0,
	/* event mask array */ EM_cueberw_softstop,
	/* state options */   (0)},

	/* State "check_turbinenkreise" */ {
	/* state name */       "check_turbinenkreise",
	/* action function */ (ACTION_FUNC) A_cueberw_check_turbinenkreise,
	/* event function */  (EVENT_FUNC) E_cueberw_check_turbinenkreise,
	/* delay function */   (DELAY_FUNC) D_cueberw_check_turbinenkreise,
	/* entry function */   (ENTRY_FUNC) I_cueberw_check_turbinenkreise,
	/* exit function */   (EXIT_FUNC) 0,
	/* event mask array */ EM_cueberw_check_turbinenkreise,
	/* state options */   (0)},

	/* State "stop_turbinenkreise" */ {
	/* state name */       "stop_turbinenkreise",
	/* action function */ (ACTION_FUNC) A_cueberw_stop_turbinenkreise,
	/* event function */  (EVENT_FUNC) E_cueberw_stop_turbinenkreise,
	/* delay function */   (DELAY_FUNC) D_cueberw_stop_turbinenkreise,
	/* entry function */   (ENTRY_FUNC) I_cueberw_stop_turbinenkreise,
	/* exit function */   (EXIT_FUNC) 0,
	/* event mask array */ EM_cueberw_stop_turbinenkreise,
	/* state options */   (0)},

	/* State "coldbox_ausschalten" */ {
	/* state name */       "coldbox_ausschalten",
	/* action function */ (ACTION_FUNC) A_cueberw_coldbox_ausschalten,
	/* event function */  (EVENT_FUNC) E_cueberw_coldbox_ausschalten,
	/* delay function */   (DELAY_FUNC) D_cueberw_coldbox_ausschalten,
	/* entry function */   (ENTRY_FUNC) I_cueberw_coldbox_ausschalten,
	/* exit function */   (EXIT_FUNC) 0,
	/* event mask array */ EM_cueberw_coldbox_ausschalten,
	/* state options */   (0)},

	/* State "stopit" */ {
	/* state name */       "stopit",
	/* action function */ (ACTION_FUNC) A_cueberw_stopit,
	/* event function */  (EVENT_FUNC) E_cueberw_stopit,
	/* delay function */   (DELAY_FUNC) D_cueberw_stopit,
	/* entry function */   (ENTRY_FUNC) I_cueberw_stopit,
	/* exit function */   (EXIT_FUNC) 0,
	/* event mask array */ EM_cueberw_stopit,
	/* state options */   (0)},


};

/* Event masks for state set ads_ueberw */
	/* Event mask for state cbox_off: */
static bitMask	EM_ads_ueberw_cbox_off[] = {
	0x00000004,
};
	/* Event mask for state cbox_on: */
static bitMask	EM_ads_ueberw_cbox_on[] = {
	0x00000004,
};

/* State Blocks */

static struct seqState state_ads_ueberw[] = {
	/* State "cbox_off" */ {
	/* state name */       "cbox_off",
	/* action function */ (ACTION_FUNC) A_ads_ueberw_cbox_off,
	/* event function */  (EVENT_FUNC) E_ads_ueberw_cbox_off,
	/* delay function */   (DELAY_FUNC) D_ads_ueberw_cbox_off,
	/* entry function */   (ENTRY_FUNC) I_ads_ueberw_cbox_off,
	/* exit function */   (EXIT_FUNC) 0,
	/* event mask array */ EM_ads_ueberw_cbox_off,
	/* state options */   (0)},

	/* State "cbox_on" */ {
	/* state name */       "cbox_on",
	/* action function */ (ACTION_FUNC) A_ads_ueberw_cbox_on,
	/* event function */  (EVENT_FUNC) E_ads_ueberw_cbox_on,
	/* delay function */   (DELAY_FUNC) D_ads_ueberw_cbox_on,
	/* entry function */   (ENTRY_FUNC) I_ads_ueberw_cbox_on,
	/* exit function */   (EXIT_FUNC) 0,
	/* event mask array */ EM_ads_ueberw_cbox_on,
	/* state options */   (0)},


};

/* State Set Blocks */
static struct seqSS seqSS[NUM_SS] = {
	/* State set "cueberw" */ {
	/* ss name */            "cueberw",
	/* ptr to state block */ state_cueberw,
	/* number of states */   8,
	/* error state */        -1},


	/* State set "ads_ueberw" */ {
	/* ss name */            "ads_ueberw",
	/* ptr to state block */ state_ads_ueberw,
	/* number of states */   2,
	/* error state */        -1},
};

/* Program parameter list */
static char prog_param[] = "";

/* State Program table (global) */
struct seqProgram cueberw = {
	/* magic number */       20060421,
	/* *name */              "cueberw",
	/* *pChannels */         seqChan,
	/* numChans */           NUM_CHANNELS,
	/* *pVars */             seqVar,
	/* numVars */            NUM_LOCVARS,
	/* *pSS */               seqSS,
	/* numSS */              NUM_SS,
	/* user variable size */ sizeof(struct UserVar),
	/* *pParams */           prog_param,
	/* numEvents */          NUM_EVENTS,
	/* encoded options */    (0 | OPT_CONN | OPT_NEWEF | OPT_REENT | OPT_MAIN),
	/* entry handler */      (ENTRY_FUNC) entry_handler,
	/* exit handler */       (EXIT_FUNC) exit_handler,
	/* numQueues */          NUM_QUEUES,
};


/* Register sequencer commands and program */

void cueberwRegistrar (void) {
    seqRegisterSequencerCommands();
    seqRegisterSequencerProgram (&cueberw);
}
epicsExportRegistrar(cueberwRegistrar);

