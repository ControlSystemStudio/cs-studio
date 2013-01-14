/* SNC Version 2.0.12: Mon May 18 12:05:50 2009: /scratch/workspace/kstr12/generated/tukreis1.i */

/* Event flags */
#define tk1Freigabe	1

/* Program "tukreis1" */
#include "seqCom.h"

#define NUM_SS 3
#define NUM_CHANNELS 48
#define NUM_EVENTS 1
#define NUM_QUEUES 0
#define NUM_LOCVARS 48

#define MAX_STRING_SIZE 40

#define ASYNC_OPT FALSE
#define CONN_OPT TRUE
#define DEBUG_OPT FALSE
#define MAIN_OPT TRUE
#define NEWEF_OPT TRUE
#define REENT_OPT TRUE

extern struct seqProgram tukreis1;

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
    threadId = seq((void *)&tukreis1, macro_def, 0);
    if(callIocsh) {
        iocsh(0);
    } else {
        epicsThreadExitMain();
    }
    return(0);
}

/* Variable declarations */
struct UserVar {
	char	TK1state;
	char	kstrOK;
	char	cboxOK;
	char	TK1run;
	char	buttonString[MAX_STRING_SIZE];
	char	UZV120offen;
	char	UZV121offen;
	char	UZV122offen;
	char	adsorberZu;
	char	freigabeCMD;
	char	freigabe;
	char	startFreigabe;
	char	programRunnig;
	char	CV106zu;
	double	CV106;
	char	CV127offen;
	char	summenAlarm;
	char	UZV618offen;
	char	UZV628offen;
	char	UZV638offen;
	char	lagerg1BitSIklein;
	char	lagerg2BitSIklein;
	char	lagerg3BitSIklein;
	char	lagerg1BitSIgross;
	char	lagerg2BitSIgross;
	char	lagerg3BitSIgross;
	char	PDA619;
	char	PDA629;
	char	PDA639;
	char	FA613;
	double	SI1;
	double	SI2;
	double	SI3;
	double	PI012roc;
	double	PI127roc;
	double	TI126LOW;
	double	TI126;
	double	TC126ERR;
	double	TC126OUT;
	double	TC126OROC;
	double	CV106OVAL;
	double	TC126SOUT;
	int	TC126AM;
	double	SC610OUT;
	double	SC620OUT;
	double	SC630OUT;
	double	PI105;
	double	PI012;
};

/* C code definitions */
# line 112 "/scratch/workspace/kstr12/source/tukreis1.st"
#include <string.h>
# line 113 "/scratch/workspace/kstr12/source/tukreis1.st"
#include <math.h>
# line 114 "/scratch/workspace/kstr12/source/tukreis1.st"
#include <snlMsgLog.h>

/* Entry handler */
static void entry_handler(SS_ID ssId, struct UserVar *pVar)
{
}

/* Code for state "gestoppt" in state set "tukreis1" */

/* Entry function for state "gestoppt" in state set "tukreis1" */
static void I_tukreis1_gestoppt(SS_ID ssId, struct UserVar *pVar)
{
/* Entry 1: */
# line 208 "/scratch/workspace/kstr12/source/tukreis1.st"
	(pVar->TK1run) = 0;
# line 209 "/scratch/workspace/kstr12/source/tukreis1.st"
	sprintf((pVar->buttonString), "Start\nTuKr 1");
# line 210 "/scratch/workspace/kstr12/source/tukreis1.st"
	seq_pvPut(ssId, 3 /* TK1run */, 0);
# line 211 "/scratch/workspace/kstr12/source/tukreis1.st"
	seq_pvPut(ssId, 4 /* buttonString */, 0);
# line 212 "/scratch/workspace/kstr12/source/tukreis1.st"
	snlMsgLog("TuKr 1 gestoppt");
# line 213 "/scratch/workspace/kstr12/source/tukreis1.st"
	(pVar->TK1state) = 1;
# line 214 "/scratch/workspace/kstr12/source/tukreis1.st"
	seq_pvPut(ssId, 0 /* TK1state */, 0);
}

/* Delay function for state "gestoppt" in state set "tukreis1" */
static void D_tukreis1_gestoppt(SS_ID ssId, struct UserVar *pVar)
{
# line 217 "/scratch/workspace/kstr12/source/tukreis1.st"
}

/* Event function for state "gestoppt" in state set "tukreis1" */
static long E_tukreis1_gestoppt(SS_ID ssId, struct UserVar *pVar, short *pTransNum, short *pNextState)
{
# line 217 "/scratch/workspace/kstr12/source/tukreis1.st"
	if (seq_efTest(ssId, tk1Freigabe))
	{
		*pNextState = 1;
		*pTransNum = 0;
		return TRUE;
	}
	return FALSE;
}

/* Action function for state "gestoppt" in state set "tukreis1" */
static void A_tukreis1_gestoppt(SS_ID ssId, struct UserVar *pVar, short transNum)
{
	switch(transNum)
	{
	case 0:
		{
		}
		return;
	}
}
/* Code for state "bereit" in state set "tukreis1" */

/* Entry function for state "bereit" in state set "tukreis1" */
static void I_tukreis1_bereit(SS_ID ssId, struct UserVar *pVar)
{
/* Entry 1: */
# line 221 "/scratch/workspace/kstr12/source/tukreis1.st"
	snlMsgLog("TuKr 1 freigegeben");
# line 222 "/scratch/workspace/kstr12/source/tukreis1.st"
	(pVar->TK1state) = 2;
# line 223 "/scratch/workspace/kstr12/source/tukreis1.st"
	seq_pvPut(ssId, 0 /* TK1state */, 0);
}

/* Delay function for state "bereit" in state set "tukreis1" */
static void D_tukreis1_bereit(SS_ID ssId, struct UserVar *pVar)
{
# line 226 "/scratch/workspace/kstr12/source/tukreis1.st"
# line 228 "/scratch/workspace/kstr12/source/tukreis1.st"
# line 230 "/scratch/workspace/kstr12/source/tukreis1.st"
	seq_delayInit(ssId, 0, (60.0));
}

/* Event function for state "bereit" in state set "tukreis1" */
static long E_tukreis1_bereit(SS_ID ssId, struct UserVar *pVar, short *pTransNum, short *pNextState)
{
# line 226 "/scratch/workspace/kstr12/source/tukreis1.st"
	if (!seq_efTest(ssId, tk1Freigabe))
	{
		*pNextState = 0;
		*pTransNum = 0;
		return TRUE;
	}
# line 228 "/scratch/workspace/kstr12/source/tukreis1.st"
	if ((pVar->startFreigabe))
	{
		*pNextState = 2;
		*pTransNum = 1;
		return TRUE;
	}
# line 230 "/scratch/workspace/kstr12/source/tukreis1.st"
	if (seq_delay(ssId, 0))
	{
		*pNextState = 1;
		*pTransNum = 2;
		return TRUE;
	}
	return FALSE;
}

/* Action function for state "bereit" in state set "tukreis1" */
static void A_tukreis1_bereit(SS_ID ssId, struct UserVar *pVar, short transNum)
{
	switch(transNum)
	{
	case 0:
		{
		}
		return;
	case 1:
		{
		}
		return;
	case 2:
		{
		}
		return;
	}
}
/* Code for state "startBereit" in state set "tukreis1" */

/* Entry function for state "startBereit" in state set "tukreis1" */
static void I_tukreis1_startBereit(SS_ID ssId, struct UserVar *pVar)
{
/* Entry 1: */
# line 234 "/scratch/workspace/kstr12/source/tukreis1.st"
	snlMsgLog("TuKr 1 startbereit");
# line 235 "/scratch/workspace/kstr12/source/tukreis1.st"
	(pVar->TK1state) = 3;
# line 236 "/scratch/workspace/kstr12/source/tukreis1.st"
	seq_pvPut(ssId, 0 /* TK1state */, 0);
}

/* Delay function for state "startBereit" in state set "tukreis1" */
static void D_tukreis1_startBereit(SS_ID ssId, struct UserVar *pVar)
{
# line 239 "/scratch/workspace/kstr12/source/tukreis1.st"
# line 241 "/scratch/workspace/kstr12/source/tukreis1.st"
# line 244 "/scratch/workspace/kstr12/source/tukreis1.st"
# line 247 "/scratch/workspace/kstr12/source/tukreis1.st"
# line 250 "/scratch/workspace/kstr12/source/tukreis1.st"
# line 253 "/scratch/workspace/kstr12/source/tukreis1.st"
# line 256 "/scratch/workspace/kstr12/source/tukreis1.st"
}

/* Event function for state "startBereit" in state set "tukreis1" */
static long E_tukreis1_startBereit(SS_ID ssId, struct UserVar *pVar, short *pTransNum, short *pNextState)
{
# line 239 "/scratch/workspace/kstr12/source/tukreis1.st"
	if (!seq_efTest(ssId, tk1Freigabe))
	{
		*pNextState = 0;
		*pTransNum = 0;
		return TRUE;
	}
# line 241 "/scratch/workspace/kstr12/source/tukreis1.st"
	if (!(pVar->startFreigabe))
	{
		*pNextState = 1;
		*pTransNum = 1;
		return TRUE;
	}
# line 244 "/scratch/workspace/kstr12/source/tukreis1.st"
	if (!(pVar->CV106zu) || (pVar->CV106) > 0.01)
	{
		*pNextState = 1;
		*pTransNum = 2;
		return TRUE;
	}
# line 247 "/scratch/workspace/kstr12/source/tukreis1.st"
	if (!(pVar->UZV618offen) || !(pVar->UZV628offen) || !(pVar->UZV638offen))
	{
		*pNextState = 1;
		*pTransNum = 3;
		return TRUE;
	}
# line 250 "/scratch/workspace/kstr12/source/tukreis1.st"
	if ((pVar->PDA619) != 1 || (pVar->PDA629) != 1 || (pVar->PDA639) != 1)
	{
		*pNextState = 1;
		*pTransNum = 4;
		return TRUE;
	}
# line 253 "/scratch/workspace/kstr12/source/tukreis1.st"
	if ((pVar->FA613) != 1)
	{
		*pNextState = 1;
		*pTransNum = 5;
		return TRUE;
	}
# line 256 "/scratch/workspace/kstr12/source/tukreis1.st"
	if ((pVar->TK1run))
	{
		*pNextState = 3;
		*pTransNum = 6;
		return TRUE;
	}
	return FALSE;
}

/* Action function for state "startBereit" in state set "tukreis1" */
static void A_tukreis1_startBereit(SS_ID ssId, struct UserVar *pVar, short transNum)
{
	switch(transNum)
	{
	case 0:
		{
		}
		return;
	case 1:
		{
		}
		return;
	case 2:
		{
# line 243 "/scratch/workspace/kstr12/source/tukreis1.st"
			snlMsgLog("Eintrittsventil nicht geschlossen");
		}
		return;
	case 3:
		{
# line 246 "/scratch/workspace/kstr12/source/tukreis1.st"
			snlMsgLog("Lagergasventile nicht alle offen");
		}
		return;
	case 4:
		{
# line 249 "/scratch/workspace/kstr12/source/tukreis1.st"
			snlMsgLog("Differenzdruecke nicht alle OK");
		}
		return;
	case 5:
		{
# line 252 "/scratch/workspace/kstr12/source/tukreis1.st"
			snlMsgLog("Kuehlwasser Sammelalarm");
		}
		return;
	case 6:
		{
# line 255 "/scratch/workspace/kstr12/source/tukreis1.st"
			snlMsgLog("Starte Turbinenkreis");
		}
		return;
	}
}
/* Code for state "run" in state set "tukreis1" */

/* Entry function for state "run" in state set "tukreis1" */
static void I_tukreis1_run(SS_ID ssId, struct UserVar *pVar)
{
/* Entry 1: */
# line 260 "/scratch/workspace/kstr12/source/tukreis1.st"
	snlMsgLog("TuKr 1 laeuft");
# line 261 "/scratch/workspace/kstr12/source/tukreis1.st"
	(pVar->TK1state) = 4;
# line 262 "/scratch/workspace/kstr12/source/tukreis1.st"
	seq_pvPut(ssId, 0 /* TK1state */, 0);
# line 263 "/scratch/workspace/kstr12/source/tukreis1.st"
	sprintf((pVar->buttonString), "Stop\nTuKr 1");
# line 264 "/scratch/workspace/kstr12/source/tukreis1.st"
	seq_pvPut(ssId, 4 /* buttonString */, 0);
}

/* Delay function for state "run" in state set "tukreis1" */
static void D_tukreis1_run(SS_ID ssId, struct UserVar *pVar)
{
# line 267 "/scratch/workspace/kstr12/source/tukreis1.st"
# line 271 "/scratch/workspace/kstr12/source/tukreis1.st"
# line 273 "/scratch/workspace/kstr12/source/tukreis1.st"
# line 275 "/scratch/workspace/kstr12/source/tukreis1.st"
# line 278 "/scratch/workspace/kstr12/source/tukreis1.st"
# line 285 "/scratch/workspace/kstr12/source/tukreis1.st"
# line 288 "/scratch/workspace/kstr12/source/tukreis1.st"
}

/* Event function for state "run" in state set "tukreis1" */
static long E_tukreis1_run(SS_ID ssId, struct UserVar *pVar, short *pTransNum, short *pNextState)
{
# line 267 "/scratch/workspace/kstr12/source/tukreis1.st"
	if (!seq_efTest(ssId, tk1Freigabe))
	{
		*pNextState = 8;
		*pTransNum = 0;
		return TRUE;
	}
# line 271 "/scratch/workspace/kstr12/source/tukreis1.st"
	if ((pVar->TI126) < (pVar->TI126LOW))
	{
		*pNextState = 5;
		*pTransNum = 1;
		return TRUE;
	}
# line 273 "/scratch/workspace/kstr12/source/tukreis1.st"
	if ((pVar->TC126ERR) < -2.0)
	{
		*pNextState = 5;
		*pTransNum = 2;
		return TRUE;
	}
# line 275 "/scratch/workspace/kstr12/source/tukreis1.st"
	if ((pVar->SC610OUT) < 2.0 || (pVar->SC620OUT) < 2.0 || (pVar->SC630OUT) < 2.0)
	{
		*pNextState = 5;
		*pTransNum = 3;
		return TRUE;
	}
# line 278 "/scratch/workspace/kstr12/source/tukreis1.st"
	if (abs((pVar->TC126ERR)) < 2.0)
	{
		*pNextState = 4;
		*pTransNum = 4;
		return TRUE;
	}
# line 285 "/scratch/workspace/kstr12/source/tukreis1.st"
	if ((pVar->TC126ERR) > 2.0 && ((pVar->SC610OUT) > 5.0 && (pVar->SC620OUT) > 5.0 && (pVar->SC630OUT) > 5.0) && (pVar->PI105) > ((pVar->PI012) - 0.3) && (pVar->TC126OUT) < 90.0)
	{
		*pNextState = 6;
		*pTransNum = 5;
		return TRUE;
	}
# line 288 "/scratch/workspace/kstr12/source/tukreis1.st"
	if (!(pVar->TK1run))
	{
		*pNextState = 7;
		*pTransNum = 6;
		return TRUE;
	}
	return FALSE;
}

/* Action function for state "run" in state set "tukreis1" */
static void A_tukreis1_run(SS_ID ssId, struct UserVar *pVar, short transNum)
{
	switch(transNum)
	{
	case 0:
		{
		}
		return;
	case 1:
		{
		}
		return;
	case 2:
		{
		}
		return;
	case 3:
		{
		}
		return;
	case 4:
		{
		}
		return;
	case 5:
		{
		}
		return;
	case 6:
		{
# line 287 "/scratch/workspace/kstr12/source/tukreis1.st"
			snlMsgLog("Stoppe Turbinenkreis");
		}
		return;
	}
}
/* Code for state "autoBetrieb" in state set "tukreis1" */

/* Entry function for state "autoBetrieb" in state set "tukreis1" */
static void I_tukreis1_autoBetrieb(SS_ID ssId, struct UserVar *pVar)
{
/* Entry 1: */
# line 292 "/scratch/workspace/kstr12/source/tukreis1.st"
	(pVar->TC126AM) = 0;
# line 293 "/scratch/workspace/kstr12/source/tukreis1.st"
	(pVar->TC126OROC) = 0;
# line 294 "/scratch/workspace/kstr12/source/tukreis1.st"
	seq_pvPut(ssId, 39 /* TC126OROC */, 0);
# line 295 "/scratch/workspace/kstr12/source/tukreis1.st"
	seq_pvPut(ssId, 42 /* TC126AM */, 0);
# line 296 "/scratch/workspace/kstr12/source/tukreis1.st"
	snlMsgLog("TuKr 1 im Regelbetrieb");
# line 297 "/scratch/workspace/kstr12/source/tukreis1.st"
	(pVar->TK1state) = 5;
# line 298 "/scratch/workspace/kstr12/source/tukreis1.st"
	seq_pvPut(ssId, 0 /* TK1state */, 0);
}

/* Delay function for state "autoBetrieb" in state set "tukreis1" */
static void D_tukreis1_autoBetrieb(SS_ID ssId, struct UserVar *pVar)
{
# line 301 "/scratch/workspace/kstr12/source/tukreis1.st"
# line 305 "/scratch/workspace/kstr12/source/tukreis1.st"
# line 307 "/scratch/workspace/kstr12/source/tukreis1.st"
# line 309 "/scratch/workspace/kstr12/source/tukreis1.st"
# line 316 "/scratch/workspace/kstr12/source/tukreis1.st"
# line 319 "/scratch/workspace/kstr12/source/tukreis1.st"
}

/* Event function for state "autoBetrieb" in state set "tukreis1" */
static long E_tukreis1_autoBetrieb(SS_ID ssId, struct UserVar *pVar, short *pTransNum, short *pNextState)
{
# line 301 "/scratch/workspace/kstr12/source/tukreis1.st"
	if (!seq_efTest(ssId, tk1Freigabe))
	{
		*pNextState = 8;
		*pTransNum = 0;
		return TRUE;
	}
# line 305 "/scratch/workspace/kstr12/source/tukreis1.st"
	if ((pVar->TI126) < (pVar->TI126LOW))
	{
		*pNextState = 5;
		*pTransNum = 1;
		return TRUE;
	}
# line 307 "/scratch/workspace/kstr12/source/tukreis1.st"
	if ((pVar->TC126ERR) < -2.0)
	{
		*pNextState = 5;
		*pTransNum = 2;
		return TRUE;
	}
# line 309 "/scratch/workspace/kstr12/source/tukreis1.st"
	if ((pVar->SC610OUT) < 2.0 || (pVar->SC620OUT) < 2.0 || (pVar->SC630OUT) < 2.0)
	{
		*pNextState = 5;
		*pTransNum = 3;
		return TRUE;
	}
# line 316 "/scratch/workspace/kstr12/source/tukreis1.st"
	if ((pVar->TC126ERR) > 2.0 && ((pVar->SC610OUT) > 5.0 && (pVar->SC620OUT) > 5.0 && (pVar->SC630OUT) > 5.0) && (pVar->PI105) > ((pVar->PI012) - 0.3) && (pVar->TC126OUT) < 90.0)
	{
		*pNextState = 6;
		*pTransNum = 4;
		return TRUE;
	}
# line 319 "/scratch/workspace/kstr12/source/tukreis1.st"
	if (!(pVar->TK1run))
	{
		*pNextState = 7;
		*pTransNum = 5;
		return TRUE;
	}
	return FALSE;
}

/* Action function for state "autoBetrieb" in state set "tukreis1" */
static void A_tukreis1_autoBetrieb(SS_ID ssId, struct UserVar *pVar, short transNum)
{
	switch(transNum)
	{
	case 0:
		{
		}
		return;
	case 1:
		{
		}
		return;
	case 2:
		{
		}
		return;
	case 3:
		{
		}
		return;
	case 4:
		{
		}
		return;
	case 5:
		{
# line 318 "/scratch/workspace/kstr12/source/tukreis1.st"
			snlMsgLog("Stoppe Turbinenkreis");
		}
		return;
	}
}
/* Code for state "manDrosselBetrieb" in state set "tukreis1" */

/* Entry function for state "manDrosselBetrieb" in state set "tukreis1" */
static void I_tukreis1_manDrosselBetrieb(SS_ID ssId, struct UserVar *pVar)
{
/* Entry 1: */
# line 323 "/scratch/workspace/kstr12/source/tukreis1.st"
	(pVar->TC126AM) = 1;
# line 324 "/scratch/workspace/kstr12/source/tukreis1.st"
	seq_pvPut(ssId, 42 /* TC126AM */, 0);
# line 325 "/scratch/workspace/kstr12/source/tukreis1.st"
	seq_pvGet(ssId, 38 /* TC126OUT */, 0);
# line 326 "/scratch/workspace/kstr12/source/tukreis1.st"
	(pVar->TC126OROC) = 0.5;
# line 327 "/scratch/workspace/kstr12/source/tukreis1.st"
	(pVar->TC126SOUT) = (pVar->TC126SOUT) - 2.0;
# line 328 "/scratch/workspace/kstr12/source/tukreis1.st"
	seq_pvPut(ssId, 39 /* TC126OROC */, 0);
# line 329 "/scratch/workspace/kstr12/source/tukreis1.st"
	seq_pvPut(ssId, 41 /* TC126SOUT */, 0);
# line 330 "/scratch/workspace/kstr12/source/tukreis1.st"
	snlMsgLog("TuKr 1 im Drosselbetrieb");
# line 331 "/scratch/workspace/kstr12/source/tukreis1.st"
	(pVar->TK1state) = 6;
# line 332 "/scratch/workspace/kstr12/source/tukreis1.st"
	seq_pvPut(ssId, 0 /* TK1state */, 0);
}

/* Delay function for state "manDrosselBetrieb" in state set "tukreis1" */
static void D_tukreis1_manDrosselBetrieb(SS_ID ssId, struct UserVar *pVar)
{
# line 335 "/scratch/workspace/kstr12/source/tukreis1.st"
# line 343 "/scratch/workspace/kstr12/source/tukreis1.st"
# line 347 "/scratch/workspace/kstr12/source/tukreis1.st"
	seq_delayInit(ssId, 0, (5.0));
# line 354 "/scratch/workspace/kstr12/source/tukreis1.st"
# line 357 "/scratch/workspace/kstr12/source/tukreis1.st"
}

/* Event function for state "manDrosselBetrieb" in state set "tukreis1" */
static long E_tukreis1_manDrosselBetrieb(SS_ID ssId, struct UserVar *pVar, short *pTransNum, short *pNextState)
{
# line 335 "/scratch/workspace/kstr12/source/tukreis1.st"
	if (!seq_efTest(ssId, tk1Freigabe))
	{
		*pNextState = 8;
		*pTransNum = 0;
		return TRUE;
	}
# line 343 "/scratch/workspace/kstr12/source/tukreis1.st"
	if ((pVar->TI126) > (pVar->TI126LOW) && (pVar->SC610OUT) > 2.0 && (pVar->SC620OUT) > 2.0 && (pVar->SC630OUT) > 2.0 && abs((pVar->TC126ERR)) < 2.0)
	{
		*pNextState = 4;
		*pTransNum = 1;
		return TRUE;
	}
# line 347 "/scratch/workspace/kstr12/source/tukreis1.st"
	if (seq_delay(ssId, 0))
	{
		*pNextState = 5;
		*pTransNum = 2;
		return TRUE;
	}
# line 354 "/scratch/workspace/kstr12/source/tukreis1.st"
	if ((pVar->TC126ERR) > 2.0 && ((pVar->SC610OUT) > 5.0 && (pVar->SC620OUT) > 5.0 && (pVar->SC630OUT) > 5.0) && (pVar->PI105) > ((pVar->PI012) - 0.3) && (pVar->TC126OUT) < 90.0)
	{
		*pNextState = 6;
		*pTransNum = 3;
		return TRUE;
	}
# line 357 "/scratch/workspace/kstr12/source/tukreis1.st"
	if (!(pVar->TK1run))
	{
		*pNextState = 7;
		*pTransNum = 4;
		return TRUE;
	}
	return FALSE;
}

/* Action function for state "manDrosselBetrieb" in state set "tukreis1" */
static void A_tukreis1_manDrosselBetrieb(SS_ID ssId, struct UserVar *pVar, short transNum)
{
	switch(transNum)
	{
	case 0:
		{
		}
		return;
	case 1:
		{
		}
		return;
	case 2:
		{
# line 345 "/scratch/workspace/kstr12/source/tukreis1.st"
			(pVar->TC126SOUT) = (pVar->TC126SOUT) - 2.0;
			seq_pvPut(ssId, 41 /* TC126SOUT */, 0);
		}
		return;
	case 3:
		{
		}
		return;
	case 4:
		{
# line 356 "/scratch/workspace/kstr12/source/tukreis1.st"
			snlMsgLog("Stoppe Turbinenkreis");
		}
		return;
	}
}
/* Code for state "manOeffneBetrieb" in state set "tukreis1" */

/* Entry function for state "manOeffneBetrieb" in state set "tukreis1" */
static void I_tukreis1_manOeffneBetrieb(SS_ID ssId, struct UserVar *pVar)
{
/* Entry 1: */
# line 361 "/scratch/workspace/kstr12/source/tukreis1.st"
	(pVar->TC126AM) = 1;
# line 362 "/scratch/workspace/kstr12/source/tukreis1.st"
	seq_pvPut(ssId, 42 /* TC126AM */, 0);
# line 363 "/scratch/workspace/kstr12/source/tukreis1.st"
	seq_pvGet(ssId, 38 /* TC126OUT */, 0);
# line 364 "/scratch/workspace/kstr12/source/tukreis1.st"
	(pVar->TC126OROC) = 0.2;
# line 365 "/scratch/workspace/kstr12/source/tukreis1.st"
	(pVar->TC126SOUT) = (pVar->TC126SOUT) + 1.0;
# line 366 "/scratch/workspace/kstr12/source/tukreis1.st"
	seq_pvPut(ssId, 39 /* TC126OROC */, 0);
# line 367 "/scratch/workspace/kstr12/source/tukreis1.st"
	seq_pvPut(ssId, 41 /* TC126SOUT */, 0);
# line 368 "/scratch/workspace/kstr12/source/tukreis1.st"
	snlMsgLog("TuKr 1 im man. Oeffnebetrieb");
# line 369 "/scratch/workspace/kstr12/source/tukreis1.st"
	(pVar->TK1state) = 7;
# line 370 "/scratch/workspace/kstr12/source/tukreis1.st"
	seq_pvPut(ssId, 0 /* TK1state */, 0);
}

/* Delay function for state "manOeffneBetrieb" in state set "tukreis1" */
static void D_tukreis1_manOeffneBetrieb(SS_ID ssId, struct UserVar *pVar)
{
# line 373 "/scratch/workspace/kstr12/source/tukreis1.st"
# line 377 "/scratch/workspace/kstr12/source/tukreis1.st"
# line 379 "/scratch/workspace/kstr12/source/tukreis1.st"
# line 381 "/scratch/workspace/kstr12/source/tukreis1.st"
# line 384 "/scratch/workspace/kstr12/source/tukreis1.st"
# line 388 "/scratch/workspace/kstr12/source/tukreis1.st"
	seq_delayInit(ssId, 0, (6.0));
# line 391 "/scratch/workspace/kstr12/source/tukreis1.st"
}

/* Event function for state "manOeffneBetrieb" in state set "tukreis1" */
static long E_tukreis1_manOeffneBetrieb(SS_ID ssId, struct UserVar *pVar, short *pTransNum, short *pNextState)
{
# line 373 "/scratch/workspace/kstr12/source/tukreis1.st"
	if (!seq_efTest(ssId, tk1Freigabe))
	{
		*pNextState = 8;
		*pTransNum = 0;
		return TRUE;
	}
# line 377 "/scratch/workspace/kstr12/source/tukreis1.st"
	if ((pVar->TI126) < (pVar->TI126LOW))
	{
		*pNextState = 5;
		*pTransNum = 1;
		return TRUE;
	}
# line 379 "/scratch/workspace/kstr12/source/tukreis1.st"
	if ((pVar->TC126ERR) < -2.0)
	{
		*pNextState = 5;
		*pTransNum = 2;
		return TRUE;
	}
# line 381 "/scratch/workspace/kstr12/source/tukreis1.st"
	if ((pVar->SC610OUT) < 2.0 || (pVar->SC620OUT) < 2.0 || (pVar->SC630OUT) < 2.0)
	{
		*pNextState = 5;
		*pTransNum = 3;
		return TRUE;
	}
# line 384 "/scratch/workspace/kstr12/source/tukreis1.st"
	if (abs((pVar->TC126ERR)) < 2.0)
	{
		*pNextState = 4;
		*pTransNum = 4;
		return TRUE;
	}
# line 388 "/scratch/workspace/kstr12/source/tukreis1.st"
	if (seq_delay(ssId, 0))
	{
		*pNextState = 6;
		*pTransNum = 5;
		return TRUE;
	}
# line 391 "/scratch/workspace/kstr12/source/tukreis1.st"
	if (!(pVar->TK1run))
	{
		*pNextState = 7;
		*pTransNum = 6;
		return TRUE;
	}
	return FALSE;
}

/* Action function for state "manOeffneBetrieb" in state set "tukreis1" */
static void A_tukreis1_manOeffneBetrieb(SS_ID ssId, struct UserVar *pVar, short transNum)
{
	switch(transNum)
	{
	case 0:
		{
		}
		return;
	case 1:
		{
		}
		return;
	case 2:
		{
		}
		return;
	case 3:
		{
		}
		return;
	case 4:
		{
		}
		return;
	case 5:
		{
# line 386 "/scratch/workspace/kstr12/source/tukreis1.st"
			(pVar->TC126SOUT) = (pVar->TC126SOUT) + 1.0;
			seq_pvPut(ssId, 41 /* TC126SOUT */, 0);
		}
		return;
	case 6:
		{
# line 390 "/scratch/workspace/kstr12/source/tukreis1.st"
			snlMsgLog("Stoppe Turbinenkreis");
		}
		return;
	}
}
/* Code for state "softStop" in state set "tukreis1" */

/* Entry function for state "softStop" in state set "tukreis1" */
static void I_tukreis1_softStop(SS_ID ssId, struct UserVar *pVar)
{
/* Entry 1: */
# line 395 "/scratch/workspace/kstr12/source/tukreis1.st"
	(pVar->TC126AM) = 1;
# line 396 "/scratch/workspace/kstr12/source/tukreis1.st"
	(pVar->TC126OROC) = 0.5;
# line 397 "/scratch/workspace/kstr12/source/tukreis1.st"
	(pVar->TC126SOUT) = 0.0;
# line 398 "/scratch/workspace/kstr12/source/tukreis1.st"
	seq_pvPut(ssId, 42 /* TC126AM */, 0);
# line 399 "/scratch/workspace/kstr12/source/tukreis1.st"
	seq_pvPut(ssId, 39 /* TC126OROC */, 0);
# line 400 "/scratch/workspace/kstr12/source/tukreis1.st"
	seq_pvPut(ssId, 41 /* TC126SOUT */, 0);
# line 401 "/scratch/workspace/kstr12/source/tukreis1.st"
	snlMsgLog("TuKr 1 soft Stop");
# line 402 "/scratch/workspace/kstr12/source/tukreis1.st"
	(pVar->TK1state) = 8;
# line 403 "/scratch/workspace/kstr12/source/tukreis1.st"
	seq_pvPut(ssId, 0 /* TK1state */, 0);
}

/* Delay function for state "softStop" in state set "tukreis1" */
static void D_tukreis1_softStop(SS_ID ssId, struct UserVar *pVar)
{
# line 408 "/scratch/workspace/kstr12/source/tukreis1.st"
	seq_delayInit(ssId, 0, (210.0));
}

/* Event function for state "softStop" in state set "tukreis1" */
static long E_tukreis1_softStop(SS_ID ssId, struct UserVar *pVar, short *pTransNum, short *pNextState)
{
# line 408 "/scratch/workspace/kstr12/source/tukreis1.st"
	if ((pVar->CV106OVAL) == 0.0 || seq_delay(ssId, 0))
	{
		*pNextState = 0;
		*pTransNum = 0;
		return TRUE;
	}
	return FALSE;
}

/* Action function for state "softStop" in state set "tukreis1" */
static void A_tukreis1_softStop(SS_ID ssId, struct UserVar *pVar, short transNum)
{
	switch(transNum)
	{
	case 0:
		{
# line 406 "/scratch/workspace/kstr12/source/tukreis1.st"
			(pVar->freigabeCMD) = 1;
			seq_pvPut(ssId, 9 /* freigabeCMD */, 0);
		}
		return;
	}
}
/* Code for state "stopit" in state set "tukreis1" */

/* Entry function for state "stopit" in state set "tukreis1" */
static void I_tukreis1_stopit(SS_ID ssId, struct UserVar *pVar)
{
/* Entry 1: */
# line 412 "/scratch/workspace/kstr12/source/tukreis1.st"
	(pVar->freigabeCMD) = 1;
# line 413 "/scratch/workspace/kstr12/source/tukreis1.st"
	seq_pvPut(ssId, 9 /* freigabeCMD */, 0);
# line 414 "/scratch/workspace/kstr12/source/tukreis1.st"
	(pVar->TC126AM) = 1;
# line 415 "/scratch/workspace/kstr12/source/tukreis1.st"
	(pVar->TC126SOUT) = 0.0;
# line 416 "/scratch/workspace/kstr12/source/tukreis1.st"
	seq_pvPut(ssId, 42 /* TC126AM */, 0);
# line 417 "/scratch/workspace/kstr12/source/tukreis1.st"
	seq_pvPut(ssId, 41 /* TC126SOUT */, 0);
# line 418 "/scratch/workspace/kstr12/source/tukreis1.st"
	snlMsgLog("TuKr 1 Stop");
# line 419 "/scratch/workspace/kstr12/source/tukreis1.st"
	(pVar->TK1state) = 9;
# line 420 "/scratch/workspace/kstr12/source/tukreis1.st"
	seq_pvPut(ssId, 0 /* TK1state */, 0);
}

/* Delay function for state "stopit" in state set "tukreis1" */
static void D_tukreis1_stopit(SS_ID ssId, struct UserVar *pVar)
{
# line 423 "/scratch/workspace/kstr12/source/tukreis1.st"
}

/* Event function for state "stopit" in state set "tukreis1" */
static long E_tukreis1_stopit(SS_ID ssId, struct UserVar *pVar, short *pTransNum, short *pNextState)
{
# line 423 "/scratch/workspace/kstr12/source/tukreis1.st"
	if (1)
	{
		*pNextState = 0;
		*pTransNum = 0;
		return TRUE;
	}
	return FALSE;
}

/* Action function for state "stopit" in state set "tukreis1" */
static void A_tukreis1_stopit(SS_ID ssId, struct UserVar *pVar, short transNum)
{
	switch(transNum)
	{
	case 0:
		{
		}
		return;
	}
}
/* Code for state "not_ok" in state set "tk1ueberw" */

/* Entry function for state "not_ok" in state set "tk1ueberw" */
static void I_tk1ueberw_not_ok(SS_ID ssId, struct UserVar *pVar)
{
/* Entry 1: */
# line 429 "/scratch/workspace/kstr12/source/tukreis1.st"
	seq_efClear(ssId, tk1Freigabe);
}

/* Delay function for state "not_ok" in state set "tk1ueberw" */
static void D_tk1ueberw_not_ok(SS_ID ssId, struct UserVar *pVar)
{
# line 439 "/scratch/workspace/kstr12/source/tukreis1.st"
# line 451 "/scratch/workspace/kstr12/source/tukreis1.st"
}

/* Event function for state "not_ok" in state set "tk1ueberw" */
static long E_tk1ueberw_not_ok(SS_ID ssId, struct UserVar *pVar, short *pTransNum, short *pNextState)
{
# line 439 "/scratch/workspace/kstr12/source/tukreis1.st"
	if ((pVar->UZV120offen) && (pVar->UZV121offen) && (pVar->UZV122offen) && !(pVar->adsorberZu) && (pVar->freigabe) && (pVar->CV127offen) && (pVar->summenAlarm) == 1 && !(pVar->PI012roc) && !(pVar->PI127roc) && (pVar->kstrOK) && (pVar->cboxOK))
	{
		*pNextState = 1;
		*pTransNum = 0;
		return TRUE;
	}
# line 451 "/scratch/workspace/kstr12/source/tukreis1.st"
	if ((pVar->TK1run))
	{
		*pNextState = 0;
		*pTransNum = 1;
		return TRUE;
	}
	return FALSE;
}

/* Action function for state "not_ok" in state set "tk1ueberw" */
static void A_tk1ueberw_not_ok(SS_ID ssId, struct UserVar *pVar, short transNum)
{
	switch(transNum)
	{
	case 0:
		{
# line 438 "/scratch/workspace/kstr12/source/tukreis1.st"
			snlMsgLog("Turbinenkreis 1 freigegeben");
		}
		return;
	case 1:
		{
# line 442 "/scratch/workspace/kstr12/source/tukreis1.st"
			snlMsgLog("UZV120,1,2: %d,%d,%d", (pVar->UZV120offen), (pVar->UZV121offen), (pVar->UZV122offen));
			snlMsgLog("Adsorber offen: %d", !(pVar->adsorberZu));
			snlMsgLog("Freigabe: %d", (pVar->freigabe));
			snlMsgLog("CV127 offen: %d", (pVar->CV127offen));
			snlMsgLog("Summenalarm: %d", (pVar->summenAlarm));
			snlMsgLog("ROC PI012,PI127: %f,%f", (pVar->PI012roc), (pVar->PI127roc));
			snlMsgLog("KS2ok,CBXok: %d,%d", (pVar->kstrOK), (pVar->cboxOK));
			(pVar->TK1run) = 0;
			seq_pvPut(ssId, 3 /* TK1run */, 0);
		}
		return;
	}
}
/* Code for state "ok" in state set "tk1ueberw" */

/* Entry function for state "ok" in state set "tk1ueberw" */
static void I_tk1ueberw_ok(SS_ID ssId, struct UserVar *pVar)
{
/* Entry 1: */
# line 455 "/scratch/workspace/kstr12/source/tukreis1.st"
	seq_efSet(ssId, tk1Freigabe);
}

/* Delay function for state "ok" in state set "tk1ueberw" */
static void D_tk1ueberw_ok(SS_ID ssId, struct UserVar *pVar)
{
# line 459 "/scratch/workspace/kstr12/source/tukreis1.st"
# line 462 "/scratch/workspace/kstr12/source/tukreis1.st"
# line 465 "/scratch/workspace/kstr12/source/tukreis1.st"
# line 468 "/scratch/workspace/kstr12/source/tukreis1.st"
# line 471 "/scratch/workspace/kstr12/source/tukreis1.st"
# line 474 "/scratch/workspace/kstr12/source/tukreis1.st"
# line 477 "/scratch/workspace/kstr12/source/tukreis1.st"
# line 480 "/scratch/workspace/kstr12/source/tukreis1.st"
}

/* Event function for state "ok" in state set "tk1ueberw" */
static long E_tk1ueberw_ok(SS_ID ssId, struct UserVar *pVar, short *pTransNum, short *pNextState)
{
# line 459 "/scratch/workspace/kstr12/source/tukreis1.st"
	if (!(pVar->UZV120offen) || !(pVar->UZV121offen) || !(pVar->UZV122offen))
	{
		*pNextState = 0;
		*pTransNum = 0;
		return TRUE;
	}
# line 462 "/scratch/workspace/kstr12/source/tukreis1.st"
	if ((pVar->adsorberZu))
	{
		*pNextState = 0;
		*pTransNum = 1;
		return TRUE;
	}
# line 465 "/scratch/workspace/kstr12/source/tukreis1.st"
	if (!(pVar->freigabe))
	{
		*pNextState = 0;
		*pTransNum = 2;
		return TRUE;
	}
# line 468 "/scratch/workspace/kstr12/source/tukreis1.st"
	if (!(pVar->CV127offen))
	{
		*pNextState = 0;
		*pTransNum = 3;
		return TRUE;
	}
# line 471 "/scratch/workspace/kstr12/source/tukreis1.st"
	if ((pVar->summenAlarm) == 0)
	{
		*pNextState = 0;
		*pTransNum = 4;
		return TRUE;
	}
# line 474 "/scratch/workspace/kstr12/source/tukreis1.st"
	if ((pVar->PI012roc) || (pVar->PI127roc))
	{
		*pNextState = 0;
		*pTransNum = 5;
		return TRUE;
	}
# line 477 "/scratch/workspace/kstr12/source/tukreis1.st"
	if (!(pVar->kstrOK))
	{
		*pNextState = 0;
		*pTransNum = 6;
		return TRUE;
	}
# line 480 "/scratch/workspace/kstr12/source/tukreis1.st"
	if (!(pVar->cboxOK))
	{
		*pNextState = 0;
		*pTransNum = 7;
		return TRUE;
	}
	return FALSE;
}

/* Action function for state "ok" in state set "tk1ueberw" */
static void A_tk1ueberw_ok(SS_ID ssId, struct UserVar *pVar, short transNum)
{
	switch(transNum)
	{
	case 0:
		{
# line 458 "/scratch/workspace/kstr12/source/tukreis1.st"
			snlMsgLog("Coldbox Eingangsventile nicht (alle) offen");
		}
		return;
	case 1:
		{
# line 461 "/scratch/workspace/kstr12/source/tukreis1.st"
			snlMsgLog("Adsorber nicht offen");
		}
		return;
	case 2:
		{
# line 464 "/scratch/workspace/kstr12/source/tukreis1.st"
			snlMsgLog("keine Freigabe TuKreis Eingangsventil");
		}
		return;
	case 3:
		{
# line 467 "/scratch/workspace/kstr12/source/tukreis1.st"
			snlMsgLog("TuKreis Ausgangsventil nicht offen");
		}
		return;
	case 4:
		{
# line 470 "/scratch/workspace/kstr12/source/tukreis1.st"
			snlMsgLog("TuKreis Summenalarm");
		}
		return;
	case 5:
		{
# line 473 "/scratch/workspace/kstr12/source/tukreis1.st"
			snlMsgLog("Rate of Change Alarm fuer PI012 oder PI127");
		}
		return;
	case 6:
		{
# line 476 "/scratch/workspace/kstr12/source/tukreis1.st"
			snlMsgLog("Kompressor Strasse nicht OK");
		}
		return;
	case 7:
		{
# line 479 "/scratch/workspace/kstr12/source/tukreis1.st"
			snlMsgLog("Coldbox nicht OK");
		}
		return;
	}
}
/* Code for state "gross" in state set "tuDrehzahlen" */

/* Entry function for state "gross" in state set "tuDrehzahlen" */
static void I_tuDrehzahlen_gross(SS_ID ssId, struct UserVar *pVar)
{
/* Entry 1: */
# line 486 "/scratch/workspace/kstr12/source/tukreis1.st"
	(pVar->lagerg1BitSIklein) = 0;
# line 487 "/scratch/workspace/kstr12/source/tukreis1.st"
	seq_pvPut(ssId, 20 /* lagerg1BitSIklein */, 0);
# line 488 "/scratch/workspace/kstr12/source/tukreis1.st"
	(pVar->lagerg1BitSIgross) = 1;
# line 489 "/scratch/workspace/kstr12/source/tukreis1.st"
	seq_pvPut(ssId, 23 /* lagerg1BitSIgross */, 0);
# line 490 "/scratch/workspace/kstr12/source/tukreis1.st"
	snlMsgLog("Turbinendrehzahlen gross");
}

/* Delay function for state "gross" in state set "tuDrehzahlen" */
static void D_tuDrehzahlen_gross(SS_ID ssId, struct UserVar *pVar)
{
# line 494 "/scratch/workspace/kstr12/source/tukreis1.st"
# line 496 "/scratch/workspace/kstr12/source/tukreis1.st"
# line 502 "/scratch/workspace/kstr12/source/tukreis1.st"
	seq_delayInit(ssId, 0, (60.0));
}

/* Event function for state "gross" in state set "tuDrehzahlen" */
static long E_tuDrehzahlen_gross(SS_ID ssId, struct UserVar *pVar, short *pTransNum, short *pNextState)
{
# line 494 "/scratch/workspace/kstr12/source/tukreis1.st"
	if (((pVar->SI1) < 400.0 || (pVar->SI2) < 400.0 || (pVar->SI3) < 400.0) && ((pVar->SI1) > 100.0 || (pVar->SI2) > 100.0 || (pVar->SI3) > 100.0))
	{
		*pNextState = 1;
		*pTransNum = 0;
		return TRUE;
	}
# line 496 "/scratch/workspace/kstr12/source/tukreis1.st"
	if ((pVar->SI1) < 100.0 && (pVar->SI2) < 100.0 && (pVar->SI3) < 100.0)
	{
		*pNextState = 2;
		*pTransNum = 1;
		return TRUE;
	}
# line 502 "/scratch/workspace/kstr12/source/tukreis1.st"
	if (seq_delay(ssId, 0))
	{
		*pNextState = 0;
		*pTransNum = 2;
		return TRUE;
	}
	return FALSE;
}

/* Action function for state "gross" in state set "tuDrehzahlen" */
static void A_tuDrehzahlen_gross(SS_ID ssId, struct UserVar *pVar, short transNum)
{
	switch(transNum)
	{
	case 0:
		{
		}
		return;
	case 1:
		{
		}
		return;
	case 2:
		{
# line 498 "/scratch/workspace/kstr12/source/tukreis1.st"
			(pVar->programRunnig) = 1;
			(pVar->startFreigabe) = 0;
			seq_pvPut(ssId, 12 /* programRunnig */, 0);
			seq_pvPut(ssId, 11 /* startFreigabe */, 0);
		}
		return;
	}
}
/* Code for state "mittel" in state set "tuDrehzahlen" */

/* Entry function for state "mittel" in state set "tuDrehzahlen" */
static void I_tuDrehzahlen_mittel(SS_ID ssId, struct UserVar *pVar)
{
/* Entry 1: */
# line 506 "/scratch/workspace/kstr12/source/tukreis1.st"
	(pVar->lagerg1BitSIklein) = 1;
# line 507 "/scratch/workspace/kstr12/source/tukreis1.st"
	seq_pvPut(ssId, 20 /* lagerg1BitSIklein */, 0);
# line 508 "/scratch/workspace/kstr12/source/tukreis1.st"
	(pVar->lagerg1BitSIgross) = 1;
# line 509 "/scratch/workspace/kstr12/source/tukreis1.st"
	seq_pvPut(ssId, 23 /* lagerg1BitSIgross */, 0);
# line 510 "/scratch/workspace/kstr12/source/tukreis1.st"
	snlMsgLog("Turbinendrehzahlen mittel");
}

/* Delay function for state "mittel" in state set "tuDrehzahlen" */
static void D_tuDrehzahlen_mittel(SS_ID ssId, struct UserVar *pVar)
{
# line 513 "/scratch/workspace/kstr12/source/tukreis1.st"
# line 515 "/scratch/workspace/kstr12/source/tukreis1.st"
# line 521 "/scratch/workspace/kstr12/source/tukreis1.st"
	seq_delayInit(ssId, 0, (60.0));
}

/* Event function for state "mittel" in state set "tuDrehzahlen" */
static long E_tuDrehzahlen_mittel(SS_ID ssId, struct UserVar *pVar, short *pTransNum, short *pNextState)
{
# line 513 "/scratch/workspace/kstr12/source/tukreis1.st"
	if ((pVar->SI1) > 400.0 && (pVar->SI2) > 400.0 && (pVar->SI3) > 400.0)
	{
		*pNextState = 0;
		*pTransNum = 0;
		return TRUE;
	}
# line 515 "/scratch/workspace/kstr12/source/tukreis1.st"
	if ((pVar->SI1) < 100.0 && (pVar->SI2) < 100.0 && (pVar->SI3) < 100.0)
	{
		*pNextState = 2;
		*pTransNum = 1;
		return TRUE;
	}
# line 521 "/scratch/workspace/kstr12/source/tukreis1.st"
	if (seq_delay(ssId, 0))
	{
		*pNextState = 1;
		*pTransNum = 2;
		return TRUE;
	}
	return FALSE;
}

/* Action function for state "mittel" in state set "tuDrehzahlen" */
static void A_tuDrehzahlen_mittel(SS_ID ssId, struct UserVar *pVar, short transNum)
{
	switch(transNum)
	{
	case 0:
		{
		}
		return;
	case 1:
		{
		}
		return;
	case 2:
		{
# line 517 "/scratch/workspace/kstr12/source/tukreis1.st"
			(pVar->programRunnig) = 1;
			(pVar->startFreigabe) = 0;
			seq_pvPut(ssId, 12 /* programRunnig */, 0);
			seq_pvPut(ssId, 11 /* startFreigabe */, 0);
		}
		return;
	}
}
/* Code for state "klein" in state set "tuDrehzahlen" */

/* Entry function for state "klein" in state set "tuDrehzahlen" */
static void I_tuDrehzahlen_klein(SS_ID ssId, struct UserVar *pVar)
{
/* Entry 1: */
# line 525 "/scratch/workspace/kstr12/source/tukreis1.st"
	(pVar->lagerg1BitSIklein) = 1;
# line 526 "/scratch/workspace/kstr12/source/tukreis1.st"
	seq_pvPut(ssId, 20 /* lagerg1BitSIklein */, 0);
# line 527 "/scratch/workspace/kstr12/source/tukreis1.st"
	(pVar->lagerg1BitSIgross) = 0;
# line 528 "/scratch/workspace/kstr12/source/tukreis1.st"
	seq_pvPut(ssId, 23 /* lagerg1BitSIgross */, 0);
# line 529 "/scratch/workspace/kstr12/source/tukreis1.st"
	snlMsgLog("Turbinendrehzahlen klein");
}

/* Delay function for state "klein" in state set "tuDrehzahlen" */
static void D_tuDrehzahlen_klein(SS_ID ssId, struct UserVar *pVar)
{
# line 532 "/scratch/workspace/kstr12/source/tukreis1.st"
# line 535 "/scratch/workspace/kstr12/source/tukreis1.st"
# line 540 "/scratch/workspace/kstr12/source/tukreis1.st"
# line 546 "/scratch/workspace/kstr12/source/tukreis1.st"
	seq_delayInit(ssId, 0, (60.0));
}

/* Event function for state "klein" in state set "tuDrehzahlen" */
static long E_tuDrehzahlen_klein(SS_ID ssId, struct UserVar *pVar, short *pTransNum, short *pNextState)
{
# line 532 "/scratch/workspace/kstr12/source/tukreis1.st"
	if ((pVar->SI1) > 400.0 && (pVar->SI2) > 400.0 && (pVar->SI3) > 400.0)
	{
		*pNextState = 0;
		*pTransNum = 0;
		return TRUE;
	}
# line 535 "/scratch/workspace/kstr12/source/tukreis1.st"
	if (((pVar->SI1) < 400.0 || (pVar->SI2) < 400.0 || (pVar->SI3) < 400.0) && ((pVar->SI1) > 100.0 || (pVar->SI2) > 100.0 || (pVar->SI3) > 100.0))
	{
		*pNextState = 1;
		*pTransNum = 1;
		return TRUE;
	}
# line 540 "/scratch/workspace/kstr12/source/tukreis1.st"
	if ((pVar->CV106zu) && (pVar->CV106) < 0.01 && (pVar->UZV618offen) && (pVar->UZV628offen) && (pVar->UZV638offen) && (pVar->PDA619) == 1 && (pVar->PDA629) == 1 && (pVar->PDA639) == 1 && (pVar->FA613) == 1)
	{
		*pNextState = 3;
		*pTransNum = 2;
		return TRUE;
	}
# line 546 "/scratch/workspace/kstr12/source/tukreis1.st"
	if (seq_delay(ssId, 0))
	{
		*pNextState = 2;
		*pTransNum = 3;
		return TRUE;
	}
	return FALSE;
}

/* Action function for state "klein" in state set "tuDrehzahlen" */
static void A_tuDrehzahlen_klein(SS_ID ssId, struct UserVar *pVar, short transNum)
{
	switch(transNum)
	{
	case 0:
		{
		}
		return;
	case 1:
		{
		}
		return;
	case 2:
		{
		}
		return;
	case 3:
		{
# line 542 "/scratch/workspace/kstr12/source/tukreis1.st"
			(pVar->programRunnig) = 1;
			(pVar->startFreigabe) = 0;
			seq_pvPut(ssId, 12 /* programRunnig */, 0);
			seq_pvPut(ssId, 11 /* startFreigabe */, 0);
		}
		return;
	}
}
/* Code for state "bereit" in state set "tuDrehzahlen" */

/* Entry function for state "bereit" in state set "tuDrehzahlen" */
static void I_tuDrehzahlen_bereit(SS_ID ssId, struct UserVar *pVar)
{
/* Entry 1: */
# line 550 "/scratch/workspace/kstr12/source/tukreis1.st"
	(pVar->lagerg1BitSIklein) = 1;
# line 551 "/scratch/workspace/kstr12/source/tukreis1.st"
	seq_pvPut(ssId, 20 /* lagerg1BitSIklein */, 0);
# line 552 "/scratch/workspace/kstr12/source/tukreis1.st"
	(pVar->lagerg1BitSIgross) = 0;
# line 553 "/scratch/workspace/kstr12/source/tukreis1.st"
	seq_pvPut(ssId, 23 /* lagerg1BitSIgross */, 0);
# line 554 "/scratch/workspace/kstr12/source/tukreis1.st"
	snlMsgLog("Turbinendrehzahlen bereit");
}

/* Delay function for state "bereit" in state set "tuDrehzahlen" */
static void D_tuDrehzahlen_bereit(SS_ID ssId, struct UserVar *pVar)
{
# line 557 "/scratch/workspace/kstr12/source/tukreis1.st"
# line 560 "/scratch/workspace/kstr12/source/tukreis1.st"
# line 563 "/scratch/workspace/kstr12/source/tukreis1.st"
# line 566 "/scratch/workspace/kstr12/source/tukreis1.st"
# line 569 "/scratch/workspace/kstr12/source/tukreis1.st"
# line 572 "/scratch/workspace/kstr12/source/tukreis1.st"
# line 578 "/scratch/workspace/kstr12/source/tukreis1.st"
	seq_delayInit(ssId, 0, (60.0));
}

/* Event function for state "bereit" in state set "tuDrehzahlen" */
static long E_tuDrehzahlen_bereit(SS_ID ssId, struct UserVar *pVar, short *pTransNum, short *pNextState)
{
# line 557 "/scratch/workspace/kstr12/source/tukreis1.st"
	if ((pVar->SI1) > 400.0 && (pVar->SI2) > 400.0 && (pVar->SI3) > 400.0)
	{
		*pNextState = 0;
		*pTransNum = 0;
		return TRUE;
	}
# line 560 "/scratch/workspace/kstr12/source/tukreis1.st"
	if (((pVar->SI1) < 400.0 || (pVar->SI2) < 400.0 || (pVar->SI3) < 400.0) && ((pVar->SI1) > 100.0 || (pVar->SI2) > 100.0 || (pVar->SI3) > 100.0))
	{
		*pNextState = 1;
		*pTransNum = 1;
		return TRUE;
	}
# line 563 "/scratch/workspace/kstr12/source/tukreis1.st"
	if (!(pVar->CV106zu) || (pVar->CV106) > 0.01)
	{
		*pNextState = 2;
		*pTransNum = 2;
		return TRUE;
	}
# line 566 "/scratch/workspace/kstr12/source/tukreis1.st"
	if (!(pVar->UZV618offen) || !(pVar->UZV628offen) || !(pVar->UZV638offen))
	{
		*pNextState = 2;
		*pTransNum = 3;
		return TRUE;
	}
# line 569 "/scratch/workspace/kstr12/source/tukreis1.st"
	if ((pVar->PDA619) != 1 || (pVar->PDA629) != 1 || (pVar->PDA639) != 1)
	{
		*pNextState = 2;
		*pTransNum = 4;
		return TRUE;
	}
# line 572 "/scratch/workspace/kstr12/source/tukreis1.st"
	if ((pVar->FA613) != 1)
	{
		*pNextState = 2;
		*pTransNum = 5;
		return TRUE;
	}
# line 578 "/scratch/workspace/kstr12/source/tukreis1.st"
	if (seq_delay(ssId, 0))
	{
		*pNextState = 3;
		*pTransNum = 6;
		return TRUE;
	}
	return FALSE;
}

/* Action function for state "bereit" in state set "tuDrehzahlen" */
static void A_tuDrehzahlen_bereit(SS_ID ssId, struct UserVar *pVar, short transNum)
{
	switch(transNum)
	{
	case 0:
		{
		}
		return;
	case 1:
		{
		}
		return;
	case 2:
		{
# line 562 "/scratch/workspace/kstr12/source/tukreis1.st"
			snlMsgLog("CV106 nicht geschlossen");
		}
		return;
	case 3:
		{
# line 565 "/scratch/workspace/kstr12/source/tukreis1.st"
			snlMsgLog("UZV6x8 nicht offen");
		}
		return;
	case 4:
		{
# line 568 "/scratch/workspace/kstr12/source/tukreis1.st"
			snlMsgLog("Differenzdruck PDA6x9 nicht OK");
		}
		return;
	case 5:
		{
# line 571 "/scratch/workspace/kstr12/source/tukreis1.st"
			snlMsgLog("Kuehlwassersammelalarm FA613");
		}
		return;
	case 6:
		{
# line 574 "/scratch/workspace/kstr12/source/tukreis1.st"
			(pVar->programRunnig) = 1;
			(pVar->startFreigabe) = 1;
			seq_pvPut(ssId, 12 /* programRunnig */, 0);
			seq_pvPut(ssId, 11 /* startFreigabe */, 0);
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
  {"KS2:SNL:TK1:STATE_mbbi", (void *)OFFSET(struct UserVar, TK1state), "TK1state", 
    "char", 1, 2, 0, 0, 0, 0, 0},

  {"KS2:SNL:KUE:KSTROK_bi", (void *)OFFSET(struct UserVar, kstrOK), "kstrOK", 
    "char", 1, 3, 0, 1, 0, 0, 0},

  {"KS2:SNL:CUE:CBOXOK_bi", (void *)OFFSET(struct UserVar, cboxOK), "cboxOK", 
    "char", 1, 4, 0, 1, 0, 0, 0},

  {"KS2:SNL:TK1:RUN_bi", (void *)OFFSET(struct UserVar, TK1run), "TK1run", 
    "char", 1, 5, 0, 1, 0, 0, 0},

  {"KS2:SNL:TK1:RUN_si", (void *)OFFSET(struct UserVar, buttonString[0]), "buttonString", 
    "string", 1, 6, 0, 0, 0, 0, 0},

  {"32UZV120_dlog.SB2", (void *)OFFSET(struct UserVar, UZV120offen), "UZV120offen", 
    "char", 1, 7, 0, 1, 0, 0, 0},

  {"32UZV121_dlog.SB2", (void *)OFFSET(struct UserVar, UZV121offen), "UZV121offen", 
    "char", 1, 8, 0, 1, 0, 0, 0},

  {"32UZV122_dlog.SB2", (void *)OFFSET(struct UserVar, UZV122offen), "UZV122offen", 
    "char", 1, 9, 0, 1, 0, 0, 0},

  {"42SV106_dlog.SB4", (void *)OFFSET(struct UserVar, adsorberZu), "adsorberZu", 
    "char", 1, 10, 0, 1, 0, 0, 0},

  {"42SV106_dlog.CMD", (void *)OFFSET(struct UserVar, freigabeCMD), "freigabeCMD", 
    "char", 1, 11, 0, 0, 0, 0, 0},

  {"42SV106_dlog.SB0", (void *)OFFSET(struct UserVar, freigabe), "freigabe", 
    "char", 1, 12, 0, 1, 0, 0, 0},

  {"42SV106_dlog.I13", (void *)OFFSET(struct UserVar, startFreigabe), "startFreigabe", 
    "char", 1, 13, 0, 1, 0, 0, 0},

  {"42SV106_dlog.I14", (void *)OFFSET(struct UserVar, programRunnig), "programRunnig", 
    "char", 1, 14, 0, 0, 0, 0, 0},

  {"42G106L_bi", (void *)OFFSET(struct UserVar, CV106zu), "CV106zu", 
    "char", 1, 15, 0, 1, 0, 0, 0},

  {"42CV106_ao.VAL", (void *)OFFSET(struct UserVar, CV106), "CV106", 
    "double", 1, 16, 0, 1, 0, 0, 0},

  {"42CV127_dlog.SB2", (void *)OFFSET(struct UserVar, CV127offen), "CV127offen", 
    "char", 1, 17, 0, 1, 0, 0, 0},

  {"42XA106_bi.VAL", (void *)OFFSET(struct UserVar, summenAlarm), "summenAlarm", 
    "char", 1, 18, 0, 1, 0, 0, 0},

  {"42UZV618_dlog.SB1", (void *)OFFSET(struct UserVar, UZV618offen), "UZV618offen", 
    "char", 1, 19, 0, 1, 0, 0, 0},

  {"42UZV628_dlog.SB1", (void *)OFFSET(struct UserVar, UZV628offen), "UZV628offen", 
    "char", 1, 20, 0, 1, 0, 0, 0},

  {"42UZV638_dlog.SB1", (void *)OFFSET(struct UserVar, UZV638offen), "UZV638offen", 
    "char", 1, 21, 0, 1, 0, 0, 0},

  {"42UZV618_dlog.I13", (void *)OFFSET(struct UserVar, lagerg1BitSIklein), "lagerg1BitSIklein", 
    "char", 1, 22, 0, 0, 0, 0, 0},

  {"42UZV628_dlog.I13", (void *)OFFSET(struct UserVar, lagerg2BitSIklein), "lagerg2BitSIklein", 
    "char", 1, 23, 0, 0, 0, 0, 0},

  {"42UZV638_dlog.I13", (void *)OFFSET(struct UserVar, lagerg3BitSIklein), "lagerg3BitSIklein", 
    "char", 1, 24, 0, 0, 0, 0, 0},

  {"42UZV618_dlog.I14", (void *)OFFSET(struct UserVar, lagerg1BitSIgross), "lagerg1BitSIgross", 
    "char", 1, 25, 0, 0, 0, 0, 0},

  {"42UZV628_dlog.I14", (void *)OFFSET(struct UserVar, lagerg2BitSIgross), "lagerg2BitSIgross", 
    "char", 1, 26, 0, 0, 0, 0, 0},

  {"42UZV638_dlog.I14", (void *)OFFSET(struct UserVar, lagerg3BitSIgross), "lagerg3BitSIgross", 
    "char", 1, 27, 0, 0, 0, 0, 0},

  {"42PDA619_bi.VAL", (void *)OFFSET(struct UserVar, PDA619), "PDA619", 
    "char", 1, 28, 0, 1, 0, 0, 0},

  {"42PDA629_bi.VAL", (void *)OFFSET(struct UserVar, PDA629), "PDA629", 
    "char", 1, 29, 0, 1, 0, 0, 0},

  {"42PDA639_bi.VAL", (void *)OFFSET(struct UserVar, PDA639), "PDA639", 
    "char", 1, 30, 0, 1, 0, 0, 0},

  {"42FA613_bi.VAL", (void *)OFFSET(struct UserVar, FA613), "FA613", 
    "char", 1, 31, 0, 1, 0, 0, 0},

  {"42SI610_ai.VAL", (void *)OFFSET(struct UserVar, SI1), "SI1", 
    "double", 1, 32, 0, 1, 0, 0, 0},

  {"42SI620_ai.VAL", (void *)OFFSET(struct UserVar, SI2), "SI2", 
    "double", 1, 33, 0, 1, 0, 0, 0},

  {"42SI630_ai.VAL", (void *)OFFSET(struct UserVar, SI3), "SI3", 
    "double", 1, 34, 0, 1, 0, 0, 0},

  {"42PI012_calc.HIHI", (void *)OFFSET(struct UserVar, PI012roc), "PI012roc", 
    "double", 1, 35, 0, 1, 0, 0, 0},

  {"42PI127_calc.HIHI", (void *)OFFSET(struct UserVar, PI127roc), "PI127roc", 
    "double", 1, 36, 0, 1, 0, 0, 0},

  {"42TI126_temp.LOW", (void *)OFFSET(struct UserVar, TI126LOW), "TI126LOW", 
    "double", 1, 37, 0, 1, 0, 0, 0},

  {"42TI126_temp.VAL", (void *)OFFSET(struct UserVar, TI126), "TI126", 
    "double", 1, 38, 0, 1, 0, 0, 0},

  {"42TC126_pid.ERR", (void *)OFFSET(struct UserVar, TC126ERR), "TC126ERR", 
    "double", 1, 39, 0, 1, 0, 0, 0},

  {"42TC126_pid.OUT", (void *)OFFSET(struct UserVar, TC126OUT), "TC126OUT", 
    "double", 1, 40, 0, 1, 0, 0, 0},

  {"42CV106_ao.OROC", (void *)OFFSET(struct UserVar, TC126OROC), "TC126OROC", 
    "double", 1, 41, 0, 0, 0, 0, 0},

  {"42CV106_ao.OVAL", (void *)OFFSET(struct UserVar, CV106OVAL), "CV106OVAL", 
    "double", 1, 42, 0, 1, 0, 0, 0},

  {"42TC126_pid.SOUT", (void *)OFFSET(struct UserVar, TC126SOUT), "TC126SOUT", 
    "double", 1, 43, 0, 0, 0, 0, 0},

  {"42TC126_pid.AM", (void *)OFFSET(struct UserVar, TC126AM), "TC126AM", 
    "int", 1, 44, 0, 0, 0, 0, 0},

  {"42SC610_pid.OUT", (void *)OFFSET(struct UserVar, SC610OUT), "SC610OUT", 
    "double", 1, 45, 0, 1, 0, 0, 0},

  {"42SC620_pid.OUT", (void *)OFFSET(struct UserVar, SC620OUT), "SC620OUT", 
    "double", 1, 46, 0, 1, 0, 0, 0},

  {"42SC630_pid.OUT", (void *)OFFSET(struct UserVar, SC630OUT), "SC630OUT", 
    "double", 1, 47, 0, 1, 0, 0, 0},

  {"42PI105_ai.VAL", (void *)OFFSET(struct UserVar, PI105), "PI105", 
    "double", 1, 48, 0, 1, 0, 0, 0},

  {"42PI012_ai.VAL", (void *)OFFSET(struct UserVar, PI012), "PI012", 
    "double", 1, 49, 0, 1, 0, 0, 0},

};

/* Local Variables (some may also be connected to PVs above)
 *   ... does not include escaped declarations
 */
static struct seqVar seqVar[NUM_LOCVARS] = {
  /* name type_i type_s class dim1 dim2 initial address */
  { "TK1state",	  1, "char",	 0,   1,   1, NULL, (void *)OFFSET(struct UserVar, TK1state) },
  { "kstrOK",	  1, "char",	 0,   1,   1, NULL, (void *)OFFSET(struct UserVar, kstrOK) },
  { "cboxOK",	  1, "char",	 0,   1,   1, NULL, (void *)OFFSET(struct UserVar, cboxOK) },
  { "TK1run",	  1, "char",	 0,   1,   1, NULL, (void *)OFFSET(struct UserVar, TK1run) },
  { "buttonString",	  7, "string",	 0,   1,   1, NULL, (void *)OFFSET(struct UserVar, buttonString[0]) },
  { "UZV120offen",	  1, "char",	 0,   1,   1, NULL, (void *)OFFSET(struct UserVar, UZV120offen) },
  { "UZV121offen",	  1, "char",	 0,   1,   1, NULL, (void *)OFFSET(struct UserVar, UZV121offen) },
  { "UZV122offen",	  1, "char",	 0,   1,   1, NULL, (void *)OFFSET(struct UserVar, UZV122offen) },
  { "adsorberZu",	  1, "char",	 0,   1,   1, NULL, (void *)OFFSET(struct UserVar, adsorberZu) },
  { "freigabeCMD",	  1, "char",	 0,   1,   1, NULL, (void *)OFFSET(struct UserVar, freigabeCMD) },
  { "freigabe",	  1, "char",	 0,   1,   1, NULL, (void *)OFFSET(struct UserVar, freigabe) },
  { "startFreigabe",	  1, "char",	 0,   1,   1, NULL, (void *)OFFSET(struct UserVar, startFreigabe) },
  { "programRunnig",	  1, "char",	 0,   1,   1, NULL, (void *)OFFSET(struct UserVar, programRunnig) },
  { "CV106zu",	  1, "char",	 0,   1,   1, NULL, (void *)OFFSET(struct UserVar, CV106zu) },
  { "CV106",	  6, "double",	 0,   1,   1, NULL, (void *)OFFSET(struct UserVar, CV106) },
  { "CV127offen",	  1, "char",	 0,   1,   1, NULL, (void *)OFFSET(struct UserVar, CV127offen) },
  { "summenAlarm",	  1, "char",	 0,   1,   1, NULL, (void *)OFFSET(struct UserVar, summenAlarm) },
  { "UZV618offen",	  1, "char",	 0,   1,   1, NULL, (void *)OFFSET(struct UserVar, UZV618offen) },
  { "UZV628offen",	  1, "char",	 0,   1,   1, NULL, (void *)OFFSET(struct UserVar, UZV628offen) },
  { "UZV638offen",	  1, "char",	 0,   1,   1, NULL, (void *)OFFSET(struct UserVar, UZV638offen) },
  { "lagerg1BitSIklein",	  1, "char",	 0,   1,   1, NULL, (void *)OFFSET(struct UserVar, lagerg1BitSIklein) },
  { "lagerg2BitSIklein",	  1, "char",	 0,   1,   1, NULL, (void *)OFFSET(struct UserVar, lagerg2BitSIklein) },
  { "lagerg3BitSIklein",	  1, "char",	 0,   1,   1, NULL, (void *)OFFSET(struct UserVar, lagerg3BitSIklein) },
  { "lagerg1BitSIgross",	  1, "char",	 0,   1,   1, NULL, (void *)OFFSET(struct UserVar, lagerg1BitSIgross) },
  { "lagerg2BitSIgross",	  1, "char",	 0,   1,   1, NULL, (void *)OFFSET(struct UserVar, lagerg2BitSIgross) },
  { "lagerg3BitSIgross",	  1, "char",	 0,   1,   1, NULL, (void *)OFFSET(struct UserVar, lagerg3BitSIgross) },
  { "PDA619",	  1, "char",	 0,   1,   1, NULL, (void *)OFFSET(struct UserVar, PDA619) },
  { "PDA629",	  1, "char",	 0,   1,   1, NULL, (void *)OFFSET(struct UserVar, PDA629) },
  { "PDA639",	  1, "char",	 0,   1,   1, NULL, (void *)OFFSET(struct UserVar, PDA639) },
  { "FA613",	  1, "char",	 0,   1,   1, NULL, (void *)OFFSET(struct UserVar, FA613) },
  { "SI1",	  6, "double",	 0,   1,   1, NULL, (void *)OFFSET(struct UserVar, SI1) },
  { "SI2",	  6, "double",	 0,   1,   1, NULL, (void *)OFFSET(struct UserVar, SI2) },
  { "SI3",	  6, "double",	 0,   1,   1, NULL, (void *)OFFSET(struct UserVar, SI3) },
  { "PI012roc",	  6, "double",	 0,   1,   1, NULL, (void *)OFFSET(struct UserVar, PI012roc) },
  { "PI127roc",	  6, "double",	 0,   1,   1, NULL, (void *)OFFSET(struct UserVar, PI127roc) },
  { "TI126LOW",	  6, "double",	 0,   1,   1, NULL, (void *)OFFSET(struct UserVar, TI126LOW) },
  { "TI126",	  6, "double",	 0,   1,   1, NULL, (void *)OFFSET(struct UserVar, TI126) },
  { "TC126ERR",	  6, "double",	 0,   1,   1, NULL, (void *)OFFSET(struct UserVar, TC126ERR) },
  { "TC126OUT",	  6, "double",	 0,   1,   1, NULL, (void *)OFFSET(struct UserVar, TC126OUT) },
  { "TC126OROC",	  6, "double",	 0,   1,   1, NULL, (void *)OFFSET(struct UserVar, TC126OROC) },
  { "CV106OVAL",	  6, "double",	 0,   1,   1, NULL, (void *)OFFSET(struct UserVar, CV106OVAL) },
  { "TC126SOUT",	  6, "double",	 0,   1,   1, NULL, (void *)OFFSET(struct UserVar, TC126SOUT) },
  { "TC126AM",	  3, "int",	 0,   1,   1, NULL, (void *)OFFSET(struct UserVar, TC126AM) },
  { "SC610OUT",	  6, "double",	 0,   1,   1, NULL, (void *)OFFSET(struct UserVar, SC610OUT) },
  { "SC620OUT",	  6, "double",	 0,   1,   1, NULL, (void *)OFFSET(struct UserVar, SC620OUT) },
  { "SC630OUT",	  6, "double",	 0,   1,   1, NULL, (void *)OFFSET(struct UserVar, SC630OUT) },
  { "PI105",	  6, "double",	 0,   1,   1, NULL, (void *)OFFSET(struct UserVar, PI105) },
  { "PI012",	  6, "double",	 0,   1,   1, NULL, (void *)OFFSET(struct UserVar, PI012) }
};

/* Event masks for state set tukreis1 */
	/* Event mask for state gestoppt: */
static bitMask	EM_tukreis1_gestoppt[] = {
	0x00000002,
	0x00000000,
};
	/* Event mask for state bereit: */
static bitMask	EM_tukreis1_bereit[] = {
	0x00002002,
	0x00000000,
};
	/* Event mask for state startBereit: */
static bitMask	EM_tukreis1_startBereit[] = {
	0xf039a022,
	0x00000000,
};
	/* Event mask for state run: */
static bitMask	EM_tukreis1_run[] = {
	0x00000022,
	0x0003e1e0,
};
	/* Event mask for state autoBetrieb: */
static bitMask	EM_tukreis1_autoBetrieb[] = {
	0x00000022,
	0x0003e1e0,
};
	/* Event mask for state manDrosselBetrieb: */
static bitMask	EM_tukreis1_manDrosselBetrieb[] = {
	0x00000022,
	0x0003e1e0,
};
	/* Event mask for state manOeffneBetrieb: */
static bitMask	EM_tukreis1_manOeffneBetrieb[] = {
	0x00000022,
	0x0000e0e0,
};
	/* Event mask for state softStop: */
static bitMask	EM_tukreis1_softStop[] = {
	0x00000000,
	0x00000400,
};
	/* Event mask for state stopit: */
static bitMask	EM_tukreis1_stopit[] = {
	0x00000000,
	0x00000000,
};

/* State Blocks */

static struct seqState state_tukreis1[] = {
	/* State "gestoppt" */ {
	/* state name */       "gestoppt",
	/* action function */ (ACTION_FUNC) A_tukreis1_gestoppt,
	/* event function */  (EVENT_FUNC) E_tukreis1_gestoppt,
	/* delay function */   (DELAY_FUNC) D_tukreis1_gestoppt,
	/* entry function */   (ENTRY_FUNC) I_tukreis1_gestoppt,
	/* exit function */   (EXIT_FUNC) 0,
	/* event mask array */ EM_tukreis1_gestoppt,
	/* state options */   (0)},

	/* State "bereit" */ {
	/* state name */       "bereit",
	/* action function */ (ACTION_FUNC) A_tukreis1_bereit,
	/* event function */  (EVENT_FUNC) E_tukreis1_bereit,
	/* delay function */   (DELAY_FUNC) D_tukreis1_bereit,
	/* entry function */   (ENTRY_FUNC) I_tukreis1_bereit,
	/* exit function */   (EXIT_FUNC) 0,
	/* event mask array */ EM_tukreis1_bereit,
	/* state options */   (0)},

	/* State "startBereit" */ {
	/* state name */       "startBereit",
	/* action function */ (ACTION_FUNC) A_tukreis1_startBereit,
	/* event function */  (EVENT_FUNC) E_tukreis1_startBereit,
	/* delay function */   (DELAY_FUNC) D_tukreis1_startBereit,
	/* entry function */   (ENTRY_FUNC) I_tukreis1_startBereit,
	/* exit function */   (EXIT_FUNC) 0,
	/* event mask array */ EM_tukreis1_startBereit,
	/* state options */   (0)},

	/* State "run" */ {
	/* state name */       "run",
	/* action function */ (ACTION_FUNC) A_tukreis1_run,
	/* event function */  (EVENT_FUNC) E_tukreis1_run,
	/* delay function */   (DELAY_FUNC) D_tukreis1_run,
	/* entry function */   (ENTRY_FUNC) I_tukreis1_run,
	/* exit function */   (EXIT_FUNC) 0,
	/* event mask array */ EM_tukreis1_run,
	/* state options */   (0)},

	/* State "autoBetrieb" */ {
	/* state name */       "autoBetrieb",
	/* action function */ (ACTION_FUNC) A_tukreis1_autoBetrieb,
	/* event function */  (EVENT_FUNC) E_tukreis1_autoBetrieb,
	/* delay function */   (DELAY_FUNC) D_tukreis1_autoBetrieb,
	/* entry function */   (ENTRY_FUNC) I_tukreis1_autoBetrieb,
	/* exit function */   (EXIT_FUNC) 0,
	/* event mask array */ EM_tukreis1_autoBetrieb,
	/* state options */   (0)},

	/* State "manDrosselBetrieb" */ {
	/* state name */       "manDrosselBetrieb",
	/* action function */ (ACTION_FUNC) A_tukreis1_manDrosselBetrieb,
	/* event function */  (EVENT_FUNC) E_tukreis1_manDrosselBetrieb,
	/* delay function */   (DELAY_FUNC) D_tukreis1_manDrosselBetrieb,
	/* entry function */   (ENTRY_FUNC) I_tukreis1_manDrosselBetrieb,
	/* exit function */   (EXIT_FUNC) 0,
	/* event mask array */ EM_tukreis1_manDrosselBetrieb,
	/* state options */   (0)},

	/* State "manOeffneBetrieb" */ {
	/* state name */       "manOeffneBetrieb",
	/* action function */ (ACTION_FUNC) A_tukreis1_manOeffneBetrieb,
	/* event function */  (EVENT_FUNC) E_tukreis1_manOeffneBetrieb,
	/* delay function */   (DELAY_FUNC) D_tukreis1_manOeffneBetrieb,
	/* entry function */   (ENTRY_FUNC) I_tukreis1_manOeffneBetrieb,
	/* exit function */   (EXIT_FUNC) 0,
	/* event mask array */ EM_tukreis1_manOeffneBetrieb,
	/* state options */   (0)},

	/* State "softStop" */ {
	/* state name */       "softStop",
	/* action function */ (ACTION_FUNC) A_tukreis1_softStop,
	/* event function */  (EVENT_FUNC) E_tukreis1_softStop,
	/* delay function */   (DELAY_FUNC) D_tukreis1_softStop,
	/* entry function */   (ENTRY_FUNC) I_tukreis1_softStop,
	/* exit function */   (EXIT_FUNC) 0,
	/* event mask array */ EM_tukreis1_softStop,
	/* state options */   (0)},

	/* State "stopit" */ {
	/* state name */       "stopit",
	/* action function */ (ACTION_FUNC) A_tukreis1_stopit,
	/* event function */  (EVENT_FUNC) E_tukreis1_stopit,
	/* delay function */   (DELAY_FUNC) D_tukreis1_stopit,
	/* entry function */   (ENTRY_FUNC) I_tukreis1_stopit,
	/* exit function */   (EXIT_FUNC) 0,
	/* event mask array */ EM_tukreis1_stopit,
	/* state options */   (0)},


};

/* Event masks for state set tk1ueberw */
	/* Event mask for state not_ok: */
static bitMask	EM_tk1ueberw_not_ok[] = {
	0x000617b8,
	0x00000018,
};
	/* Event mask for state ok: */
static bitMask	EM_tk1ueberw_ok[] = {
	0x00061798,
	0x00000018,
};

/* State Blocks */

static struct seqState state_tk1ueberw[] = {
	/* State "not_ok" */ {
	/* state name */       "not_ok",
	/* action function */ (ACTION_FUNC) A_tk1ueberw_not_ok,
	/* event function */  (EVENT_FUNC) E_tk1ueberw_not_ok,
	/* delay function */   (DELAY_FUNC) D_tk1ueberw_not_ok,
	/* entry function */   (ENTRY_FUNC) I_tk1ueberw_not_ok,
	/* exit function */   (EXIT_FUNC) 0,
	/* event mask array */ EM_tk1ueberw_not_ok,
	/* state options */   (0)},

	/* State "ok" */ {
	/* state name */       "ok",
	/* action function */ (ACTION_FUNC) A_tk1ueberw_ok,
	/* event function */  (EVENT_FUNC) E_tk1ueberw_ok,
	/* delay function */   (DELAY_FUNC) D_tk1ueberw_ok,
	/* entry function */   (ENTRY_FUNC) I_tk1ueberw_ok,
	/* exit function */   (EXIT_FUNC) 0,
	/* event mask array */ EM_tk1ueberw_ok,
	/* state options */   (0)},


};

/* Event masks for state set tuDrehzahlen */
	/* Event mask for state gross: */
static bitMask	EM_tuDrehzahlen_gross[] = {
	0x00000000,
	0x00000007,
};
	/* Event mask for state mittel: */
static bitMask	EM_tuDrehzahlen_mittel[] = {
	0x00000000,
	0x00000007,
};
	/* Event mask for state klein: */
static bitMask	EM_tuDrehzahlen_klein[] = {
	0xf0398000,
	0x00000007,
};
	/* Event mask for state bereit: */
static bitMask	EM_tuDrehzahlen_bereit[] = {
	0xf0398000,
	0x00000007,
};

/* State Blocks */

static struct seqState state_tuDrehzahlen[] = {
	/* State "gross" */ {
	/* state name */       "gross",
	/* action function */ (ACTION_FUNC) A_tuDrehzahlen_gross,
	/* event function */  (EVENT_FUNC) E_tuDrehzahlen_gross,
	/* delay function */   (DELAY_FUNC) D_tuDrehzahlen_gross,
	/* entry function */   (ENTRY_FUNC) I_tuDrehzahlen_gross,
	/* exit function */   (EXIT_FUNC) 0,
	/* event mask array */ EM_tuDrehzahlen_gross,
	/* state options */   (0)},

	/* State "mittel" */ {
	/* state name */       "mittel",
	/* action function */ (ACTION_FUNC) A_tuDrehzahlen_mittel,
	/* event function */  (EVENT_FUNC) E_tuDrehzahlen_mittel,
	/* delay function */   (DELAY_FUNC) D_tuDrehzahlen_mittel,
	/* entry function */   (ENTRY_FUNC) I_tuDrehzahlen_mittel,
	/* exit function */   (EXIT_FUNC) 0,
	/* event mask array */ EM_tuDrehzahlen_mittel,
	/* state options */   (0)},

	/* State "klein" */ {
	/* state name */       "klein",
	/* action function */ (ACTION_FUNC) A_tuDrehzahlen_klein,
	/* event function */  (EVENT_FUNC) E_tuDrehzahlen_klein,
	/* delay function */   (DELAY_FUNC) D_tuDrehzahlen_klein,
	/* entry function */   (ENTRY_FUNC) I_tuDrehzahlen_klein,
	/* exit function */   (EXIT_FUNC) 0,
	/* event mask array */ EM_tuDrehzahlen_klein,
	/* state options */   (0)},

	/* State "bereit" */ {
	/* state name */       "bereit",
	/* action function */ (ACTION_FUNC) A_tuDrehzahlen_bereit,
	/* event function */  (EVENT_FUNC) E_tuDrehzahlen_bereit,
	/* delay function */   (DELAY_FUNC) D_tuDrehzahlen_bereit,
	/* entry function */   (ENTRY_FUNC) I_tuDrehzahlen_bereit,
	/* exit function */   (EXIT_FUNC) 0,
	/* event mask array */ EM_tuDrehzahlen_bereit,
	/* state options */   (0)},


};

/* State Set Blocks */
static struct seqSS seqSS[NUM_SS] = {
	/* State set "tukreis1" */ {
	/* ss name */            "tukreis1",
	/* ptr to state block */ state_tukreis1,
	/* number of states */   9,
	/* error state */        -1},


	/* State set "tk1ueberw" */ {
	/* ss name */            "tk1ueberw",
	/* ptr to state block */ state_tk1ueberw,
	/* number of states */   2,
	/* error state */        -1},


	/* State set "tuDrehzahlen" */ {
	/* ss name */            "tuDrehzahlen",
	/* ptr to state block */ state_tuDrehzahlen,
	/* number of states */   4,
	/* error state */        -1},
};

/* Program parameter list */
static char prog_param[] = "";

/* State Program table (global) */
struct seqProgram tukreis1 = {
	/* magic number */       20060421,
	/* *name */              "tukreis1",
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

void tukreis1Registrar (void) {
    seqRegisterSequencerCommands();
    seqRegisterSequencerProgram (&tukreis1);
}
epicsExportRegistrar(tukreis1Registrar);

