/* SNC Version 2.0.12: Mon May 18 12:05:50 2009: /scratch/workspace/kstr12/generated/kueberw.i */

/* Event flags */

/* Program "kueberw" */
#include "seqCom.h"

#define NUM_SS 3
#define NUM_CHANNELS 51
#define NUM_EVENTS 0
#define NUM_QUEUES 0
#define NUM_LOCVARS 52

#define MAX_STRING_SIZE 40

#define ASYNC_OPT FALSE
#define CONN_OPT TRUE
#define DEBUG_OPT FALSE
#define MAIN_OPT TRUE
#define NEWEF_OPT TRUE
#define REENT_OPT TRUE

extern struct seqProgram kueberw;

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
    threadId = seq((void *)&kueberw, macro_def, 0);
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
	double	tlimit;
	double	thyst;
	double	dtime;
	unsigned short	kompBits;
	char	kstrOK;
	char	SoftStop;
	char	SoftStopString[MAX_STRING_SIZE];
	char	ndoel;
	char	hdoel;
	double	HDoelTemp;
	double	NDoelTemp;
	short	T1;
	short	T2;
	short	T3;
	short	T5;
	double	T6;
	double	T6hihi;
	short	freigabeHD;
	short	freigabeND;
	double	hdSchieber;
	short	CV520OMSL;
	short	CV520Endschalter_AUF;
	short	enddruckReglerAM;
	double	enddruckReglerSOUT;
	short	CV107OMSL;
	double	CV107;
	short	NC157AM;
	double	NC157SOUT;
	short	CV108OMSL;
	double	CV108;
	short	CV109OMSL;
	double	CV109;
	short	PC503AM;
	double	PC503SOUT;
	short	fumf12sy157OMSL;
	double	fumf12sy157;
	short	fumf12sy257OMSL;
	double	fumf12sy257;
	short	fumf12sy357OMSL;
	double	fumf12sy357;
	short	byp12sy120;
	short	byp12sy220;
	short	byp12sy320;
	short	K1BETR;
	short	K2BETR;
	short	K3BETR;
	short	hdK1BETR;
	short	K1BETRcmd;
	short	K2BETRcmd;
	short	K3BETRcmd;
	short	hdK1BETRcmd;
};

/* C code definitions */
# line 28 "/scratch/workspace/kstr12/source/kueberw.st"
#include <stdio.h>
# line 29 "/scratch/workspace/kstr12/source/kueberw.st"
#include <time.h>
# line 30 "/scratch/workspace/kstr12/source/kueberw.st"
time_t t_abs_secs;
# line 31 "/scratch/workspace/kstr12/source/kueberw.st"
#include <string.h>
# line 32 "/scratch/workspace/kstr12/source/kueberw.st"
#include <snlMsgLog.h>

/* Entry handler */
static void entry_handler(SS_ID ssId, struct UserVar *pVar)
{
}

/* Code for state "ok" in state set "HDTemp" */

/* Entry function for state "ok" in state set "HDTemp" */
static void I_HDTemp_ok(SS_ID ssId, struct UserVar *pVar)
{
/* Entry 1: */
# line 191 "/scratch/workspace/kstr12/source/kueberw.st"
	(pVar->kompBits) |= (1 << 7);
# line 192 "/scratch/workspace/kstr12/source/kueberw.st"
	seq_pvPut(ssId, 3 /* kompBits */, 0);
}

/* Delay function for state "ok" in state set "HDTemp" */
static void D_HDTemp_ok(SS_ID ssId, struct UserVar *pVar)
{
# line 196 "/scratch/workspace/kstr12/source/kueberw.st"
}

/* Event function for state "ok" in state set "HDTemp" */
static long E_HDTemp_ok(SS_ID ssId, struct UserVar *pVar, short *pTransNum, short *pNextState)
{
# line 196 "/scratch/workspace/kstr12/source/kueberw.st"
	if ((pVar->HDoelTemp) > (pVar->tlimit))
	{
		*pNextState = 1;
		*pTransNum = 0;
		return TRUE;
	}
	return FALSE;
}

/* Action function for state "ok" in state set "HDTemp" */
static void A_HDTemp_ok(SS_ID ssId, struct UserVar *pVar, short transNum)
{
	switch(transNum)
	{
	case 0:
		{
# line 195 "/scratch/workspace/kstr12/source/kueberw.st"
			snlMsgLog("HDoelTemp=%f > %f", (pVar->HDoelTemp), (pVar->tlimit));
		}
		return;
	}
}
/* Code for state "not_ok" in state set "HDTemp" */

/* Entry function for state "not_ok" in state set "HDTemp" */
static void I_HDTemp_not_ok(SS_ID ssId, struct UserVar *pVar)
{
/* Entry 1: */
# line 200 "/scratch/workspace/kstr12/source/kueberw.st"
	(pVar->kompBits) &= ~(1 << 7);
# line 201 "/scratch/workspace/kstr12/source/kueberw.st"
	seq_pvPut(ssId, 3 /* kompBits */, 0);
}

/* Delay function for state "not_ok" in state set "HDTemp" */
static void D_HDTemp_not_ok(SS_ID ssId, struct UserVar *pVar)
{
# line 205 "/scratch/workspace/kstr12/source/kueberw.st"
}

/* Event function for state "not_ok" in state set "HDTemp" */
static long E_HDTemp_not_ok(SS_ID ssId, struct UserVar *pVar, short *pTransNum, short *pNextState)
{
# line 205 "/scratch/workspace/kstr12/source/kueberw.st"
	if ((pVar->HDoelTemp) < (pVar->tlimit) - (pVar->thyst))
	{
		*pNextState = 0;
		*pTransNum = 0;
		return TRUE;
	}
	return FALSE;
}

/* Action function for state "not_ok" in state set "HDTemp" */
static void A_HDTemp_not_ok(SS_ID ssId, struct UserVar *pVar, short transNum)
{
	switch(transNum)
	{
	case 0:
		{
# line 204 "/scratch/workspace/kstr12/source/kueberw.st"
			snlMsgLog("HDoelTemp=%f < %f", (pVar->HDoelTemp), (pVar->tlimit));
		}
		return;
	}
}
/* Code for state "ok" in state set "NDTemp" */

/* Entry function for state "ok" in state set "NDTemp" */
static void I_NDTemp_ok(SS_ID ssId, struct UserVar *pVar)
{
/* Entry 1: */
# line 211 "/scratch/workspace/kstr12/source/kueberw.st"
	(pVar->kompBits) |= (1 << 6);
# line 212 "/scratch/workspace/kstr12/source/kueberw.st"
	seq_pvPut(ssId, 3 /* kompBits */, 0);
}

/* Delay function for state "ok" in state set "NDTemp" */
static void D_NDTemp_ok(SS_ID ssId, struct UserVar *pVar)
{
# line 216 "/scratch/workspace/kstr12/source/kueberw.st"
}

/* Event function for state "ok" in state set "NDTemp" */
static long E_NDTemp_ok(SS_ID ssId, struct UserVar *pVar, short *pTransNum, short *pNextState)
{
# line 216 "/scratch/workspace/kstr12/source/kueberw.st"
	if ((pVar->NDoelTemp) > (pVar->tlimit))
	{
		*pNextState = 1;
		*pTransNum = 0;
		return TRUE;
	}
	return FALSE;
}

/* Action function for state "ok" in state set "NDTemp" */
static void A_NDTemp_ok(SS_ID ssId, struct UserVar *pVar, short transNum)
{
	switch(transNum)
	{
	case 0:
		{
# line 215 "/scratch/workspace/kstr12/source/kueberw.st"
			snlMsgLog("NDoelTemp=%f > %f", (pVar->NDoelTemp), (pVar->tlimit));
		}
		return;
	}
}
/* Code for state "not_ok" in state set "NDTemp" */

/* Entry function for state "not_ok" in state set "NDTemp" */
static void I_NDTemp_not_ok(SS_ID ssId, struct UserVar *pVar)
{
/* Entry 1: */
# line 220 "/scratch/workspace/kstr12/source/kueberw.st"
	(pVar->kompBits) &= ~(1 << 6);
# line 221 "/scratch/workspace/kstr12/source/kueberw.st"
	seq_pvPut(ssId, 3 /* kompBits */, 0);
}

/* Delay function for state "not_ok" in state set "NDTemp" */
static void D_NDTemp_not_ok(SS_ID ssId, struct UserVar *pVar)
{
# line 225 "/scratch/workspace/kstr12/source/kueberw.st"
}

/* Event function for state "not_ok" in state set "NDTemp" */
static long E_NDTemp_not_ok(SS_ID ssId, struct UserVar *pVar, short *pTransNum, short *pNextState)
{
# line 225 "/scratch/workspace/kstr12/source/kueberw.st"
	if ((pVar->NDoelTemp) < (pVar->tlimit) - (pVar->thyst))
	{
		*pNextState = 0;
		*pTransNum = 0;
		return TRUE;
	}
	return FALSE;
}

/* Action function for state "not_ok" in state set "NDTemp" */
static void A_NDTemp_not_ok(SS_ID ssId, struct UserVar *pVar, short transNum)
{
	switch(transNum)
	{
	case 0:
		{
# line 224 "/scratch/workspace/kstr12/source/kueberw.st"
			snlMsgLog("NDoelTemp=%f < %f", (pVar->NDoelTemp), (pVar->tlimit));
		}
		return;
	}
}
/* Code for state "top" in state set "Ueberw" */

/* Entry function for state "top" in state set "Ueberw" */
static void I_Ueberw_top(SS_ID ssId, struct UserVar *pVar)
{
/* Entry 1: */
# line 232 "/scratch/workspace/kstr12/source/kueberw.st"
	(pVar->kompBits) |= (1 << 1) + (1 << 2) + (1 << 3);
# line 233 "/scratch/workspace/kstr12/source/kueberw.st"
	(pVar->kompBits) += (1 << 5);
# line 234 "/scratch/workspace/kstr12/source/kueberw.st"
	seq_pvPut(ssId, 3 /* kompBits */, 0);
# line 235 "/scratch/workspace/kstr12/source/kueberw.st"
	(pVar->kstrOK) = 0;
# line 236 "/scratch/workspace/kstr12/source/kueberw.st"
	seq_pvPut(ssId, 4 /* kstrOK */, 0);
# line 237 "/scratch/workspace/kstr12/source/kueberw.st"
	(pVar->SoftStop) = 0;
# line 238 "/scratch/workspace/kstr12/source/kueberw.st"
	seq_pvPut(ssId, 5 /* SoftStop */, 0);
# line 241 "/scratch/workspace/kstr12/source/kueberw.st"
	snlMsgLog("selected Kompressors: %d.%d.%d.%d.%d.%d", (pVar->kompBits) & 1, (pVar->kompBits) & (1 << 1), (pVar->kompBits) & (1 << 2), (pVar->kompBits) & (1 << 3), (pVar->kompBits) & (1 << 4), (pVar->kompBits) & (1 << 5));
# line 242 "/scratch/workspace/kstr12/source/kueberw.st"
	(pVar->freigabeND) = 1;
# line 243 "/scratch/workspace/kstr12/source/kueberw.st"
	seq_pvPut(ssId, 18 /* freigabeND */, 0);
}

/* Delay function for state "top" in state set "Ueberw" */
static void D_Ueberw_top(SS_ID ssId, struct UserVar *pVar)
{
# line 246 "/scratch/workspace/kstr12/source/kueberw.st"
}

/* Event function for state "top" in state set "Ueberw" */
static long E_Ueberw_top(SS_ID ssId, struct UserVar *pVar, short *pTransNum, short *pNextState)
{
# line 246 "/scratch/workspace/kstr12/source/kueberw.st"
	if (1)
	{
		*pNextState = 1;
		*pTransNum = 0;
		return TRUE;
	}
	return FALSE;
}

/* Action function for state "top" in state set "Ueberw" */
static void A_Ueberw_top(SS_ID ssId, struct UserVar *pVar, short transNum)
{
	switch(transNum)
	{
	case 0:
		{
		}
		return;
	}
}
/* Code for state "Loop_NOTOK" in state set "Ueberw" */

/* Entry function for state "Loop_NOTOK" in state set "Ueberw" */
static void I_Ueberw_Loop_NOTOK(SS_ID ssId, struct UserVar *pVar)
{
/* Entry 1: */
# line 251 "/scratch/workspace/kstr12/source/kueberw.st"
	(pVar->kstrOK) = 0;
# line 252 "/scratch/workspace/kstr12/source/kueberw.st"
	seq_pvPut(ssId, 4 /* kstrOK */, 0);
# line 254 "/scratch/workspace/kstr12/source/kueberw.st"
	snlMsgLog("oeln/hd=%d.%d, T1235=%d.%d.%d.%d, T(He)=%f", (pVar->ndoel), (pVar->hdoel), (pVar->T1), (pVar->T2), (pVar->T3), (pVar->T5), (pVar->T6));
# line 255 "/scratch/workspace/kstr12/source/kueberw.st"
	(pVar->kompBits) &= ~(1 << 15);
# line 256 "/scratch/workspace/kstr12/source/kueberw.st"
	seq_pvPut(ssId, 3 /* kompBits */, 0);
}

/* Delay function for state "Loop_NOTOK" in state set "Ueberw" */
static void D_Ueberw_Loop_NOTOK(SS_ID ssId, struct UserVar *pVar)
{
# line 262 "/scratch/workspace/kstr12/source/kueberw.st"
}

/* Event function for state "Loop_NOTOK" in state set "Ueberw" */
static long E_Ueberw_Loop_NOTOK(SS_ID ssId, struct UserVar *pVar, short *pTransNum, short *pNextState)
{
# line 262 "/scratch/workspace/kstr12/source/kueberw.st"
	if (((pVar->ndoel) != 0) && ((pVar->hdoel) != 0) && ((pVar->T1) == 1) && ((pVar->T2) == 1) && ((pVar->T3) == 1) && ((pVar->T5) == 1) && ((pVar->T6) < ((pVar->T6hihi) - (pVar->thyst))))
	{
		*pNextState = 2;
		*pTransNum = 0;
		return TRUE;
	}
	return FALSE;
}

/* Action function for state "Loop_NOTOK" in state set "Ueberw" */
static void A_Ueberw_Loop_NOTOK(SS_ID ssId, struct UserVar *pVar, short transNum)
{
	switch(transNum)
	{
	case 0:
		{
		}
		return;
	}
}
/* Code for state "Loop_OK" in state set "Ueberw" */

/* Entry function for state "Loop_OK" in state set "Ueberw" */
static void I_Ueberw_Loop_OK(SS_ID ssId, struct UserVar *pVar)
{
/* Entry 1: */
# line 267 "/scratch/workspace/kstr12/source/kueberw.st"
	(pVar->kstrOK) = 1;
# line 268 "/scratch/workspace/kstr12/source/kueberw.st"
	seq_pvPut(ssId, 4 /* kstrOK */, 0);
# line 269 "/scratch/workspace/kstr12/source/kueberw.st"
	(pVar->freigabeHD) = 1;
# line 270 "/scratch/workspace/kstr12/source/kueberw.st"
	seq_pvPut(ssId, 17 /* freigabeHD */, 0);
# line 271 "/scratch/workspace/kstr12/source/kueberw.st"
	snlMsgLog("Kompressoren OK");
# line 272 "/scratch/workspace/kstr12/source/kueberw.st"
	seq_pvGet(ssId, 2 /* dtime */, 0);
# line 273 "/scratch/workspace/kstr12/source/kueberw.st"
	seq_pvMonitor(ssId, 2 /* dtime */);
# line 274 "/scratch/workspace/kstr12/source/kueberw.st"
	sprintf((pVar->SoftStopString), "SoftStop");
# line 275 "/scratch/workspace/kstr12/source/kueberw.st"
	seq_pvPut(ssId, 6 /* SoftStopString */, 0);
# line 276 "/scratch/workspace/kstr12/source/kueberw.st"
	(pVar->kompBits) &= ~(1 << 15);
# line 277 "/scratch/workspace/kstr12/source/kueberw.st"
	seq_pvPut(ssId, 3 /* kompBits */, 0);
}

/* Exit function for state "Loop_OK" in state set "Ueberw" */
static void O_Ueberw_Loop_OK(SS_ID ssId, struct UserVar *pVar)
{
/* Exit 1: */
# line 305 "/scratch/workspace/kstr12/source/kueberw.st"
	seq_pvStopMonitor(ssId, 2 /* dtime */);
}

/* Delay function for state "Loop_OK" in state set "Ueberw" */
static void D_Ueberw_Loop_OK(SS_ID ssId, struct UserVar *pVar)
{
# line 282 "/scratch/workspace/kstr12/source/kueberw.st"
# line 286 "/scratch/workspace/kstr12/source/kueberw.st"
# line 290 "/scratch/workspace/kstr12/source/kueberw.st"
# line 294 "/scratch/workspace/kstr12/source/kueberw.st"
# line 302 "/scratch/workspace/kstr12/source/kueberw.st"
}

/* Event function for state "Loop_OK" in state set "Ueberw" */
static long E_Ueberw_Loop_OK(SS_ID ssId, struct UserVar *pVar, short *pTransNum, short *pNextState)
{
# line 282 "/scratch/workspace/kstr12/source/kueberw.st"
	if (((pVar->ndoel) == 0) || ((pVar->hdoel) == 0))
	{
		*pNextState = 3;
		*pTransNum = 0;
		return TRUE;
	}
# line 286 "/scratch/workspace/kstr12/source/kueberw.st"
	if (((pVar->T6) > (pVar->T6hihi)))
	{
		*pNextState = 3;
		*pTransNum = 1;
		return TRUE;
	}
# line 290 "/scratch/workspace/kstr12/source/kueberw.st"
	if (((pVar->T1) == 0) || ((pVar->T2) == 0) || ((pVar->T3) == 0))
	{
		*pNextState = 3;
		*pTransNum = 2;
		return TRUE;
	}
# line 294 "/scratch/workspace/kstr12/source/kueberw.st"
	if ((pVar->T5) == 0)
	{
		*pNextState = 3;
		*pTransNum = 3;
		return TRUE;
	}
# line 302 "/scratch/workspace/kstr12/source/kueberw.st"
	if ((pVar->SoftStop))
	{
		*pNextState = 4;
		*pTransNum = 4;
		return TRUE;
	}
	return FALSE;
}

/* Action function for state "Loop_OK" in state set "Ueberw" */
static void A_Ueberw_Loop_OK(SS_ID ssId, struct UserVar *pVar, short transNum)
{
	switch(transNum)
	{
	case 0:
		{
# line 281 "/scratch/workspace/kstr12/source/kueberw.st"
			snlMsgLog("Oel-Temp-Bits ND:%d, HD:%d", (pVar->ndoel), (pVar->hdoel));
		}
		return;
	case 1:
		{
# line 285 "/scratch/workspace/kstr12/source/kueberw.st"
			snlMsgLog("He-Temp %f>%f", (pVar->T6), (pVar->T6hihi));
		}
		return;
	case 2:
		{
# line 289 "/scratch/workspace/kstr12/source/kueberw.st"
			snlMsgLog("ND-Trip K1.K2.K3 = %d.%d.%d", (pVar->T1), (pVar->T2), (pVar->T3));
		}
		return;
	case 3:
		{
# line 293 "/scratch/workspace/kstr12/source/kueberw.st"
			snlMsgLog("HD-Trip");
		}
		return;
	case 4:
		{
# line 297 "/scratch/workspace/kstr12/source/kueberw.st"
			(pVar->SoftStop) = 0;
			seq_pvPut(ssId, 5 /* SoftStop */, 0);
			sprintf((pVar->SoftStopString), "Komp Stop?\nJa: dr�cke nochmal!");
			seq_pvPut(ssId, 6 /* SoftStopString */, 0);
			snlMsgLog("Kompressoren SoftStop");
		}
		return;
	}
}
/* Code for state "EmergencyStop" in state set "Ueberw" */

/* Entry function for state "EmergencyStop" in state set "Ueberw" */
static void I_Ueberw_EmergencyStop(SS_ID ssId, struct UserVar *pVar)
{
/* Entry 1: */
# line 310 "/scratch/workspace/kstr12/source/kueberw.st"
	(pVar->kstrOK) = 0;
# line 311 "/scratch/workspace/kstr12/source/kueberw.st"
	seq_pvPut(ssId, 4 /* kstrOK */, 0);
# line 313 "/scratch/workspace/kstr12/source/kueberw.st"
	(pVar->freigabeHD) = 0;
# line 314 "/scratch/workspace/kstr12/source/kueberw.st"
	seq_pvPut(ssId, 17 /* freigabeHD */, 0);
}

/* Delay function for state "EmergencyStop" in state set "Ueberw" */
static void D_Ueberw_EmergencyStop(SS_ID ssId, struct UserVar *pVar)
{
# line 323 "/scratch/workspace/kstr12/source/kueberw.st"
}

/* Event function for state "EmergencyStop" in state set "Ueberw" */
static long E_Ueberw_EmergencyStop(SS_ID ssId, struct UserVar *pVar, short *pTransNum, short *pNextState)
{
# line 323 "/scratch/workspace/kstr12/source/kueberw.st"
	if (1)
	{
		*pNextState = 5;
		*pTransNum = 0;
		return TRUE;
	}
	return FALSE;
}

/* Action function for state "EmergencyStop" in state set "Ueberw" */
static void A_Ueberw_EmergencyStop(SS_ID ssId, struct UserVar *pVar, short transNum)
{
	switch(transNum)
	{
	case 0:
		{
# line 322 "/scratch/workspace/kstr12/source/kueberw.st"
			snlMsgLog("NotStop");
		}
		return;
	}
}
/* Code for state "SoftStop" in state set "Ueberw" */

/* Entry function for state "SoftStop" in state set "Ueberw" */
static void I_Ueberw_SoftStop(SS_ID ssId, struct UserVar *pVar)
{
/* Entry 1: */
# line 328 "/scratch/workspace/kstr12/source/kueberw.st"
	(pVar->kompBits) |= (1 << 15);
# line 329 "/scratch/workspace/kstr12/source/kueberw.st"
	seq_pvPut(ssId, 3 /* kompBits */, 0);
}

/* Delay function for state "SoftStop" in state set "Ueberw" */
static void D_Ueberw_SoftStop(SS_ID ssId, struct UserVar *pVar)
{
# line 336 "/scratch/workspace/kstr12/source/kueberw.st"
# line 344 "/scratch/workspace/kstr12/source/kueberw.st"
	seq_delayInit(ssId, 0, ((pVar->dtime)));
}

/* Event function for state "SoftStop" in state set "Ueberw" */
static long E_Ueberw_SoftStop(SS_ID ssId, struct UserVar *pVar, short *pTransNum, short *pNextState)
{
# line 336 "/scratch/workspace/kstr12/source/kueberw.st"
	if ((pVar->SoftStop))
	{
		*pNextState = 5;
		*pTransNum = 0;
		return TRUE;
	}
# line 344 "/scratch/workspace/kstr12/source/kueberw.st"
	if (seq_delay(ssId, 0) && (pVar->SoftStop) == 0)
	{
		*pNextState = 2;
		*pTransNum = 1;
		return TRUE;
	}
	return FALSE;
}

/* Action function for state "SoftStop" in state set "Ueberw" */
static void A_Ueberw_SoftStop(SS_ID ssId, struct UserVar *pVar, short transNum)
{
	switch(transNum)
	{
	case 0:
		{
# line 333 "/scratch/workspace/kstr12/source/kueberw.st"
			sprintf((pVar->SoftStopString), "SoftStop bestaetigt");
			seq_pvPut(ssId, 6 /* SoftStopString */, 0);
			snlMsgLog("Softstop bestaetigt");
		}
		return;
	case 1:
		{
# line 339 "/scratch/workspace/kstr12/source/kueberw.st"
			sprintf((pVar->SoftStopString), "Kein SoftStop!");
			seq_pvPut(ssId, 6 /* SoftStopString */, 0);
			(pVar->kompBits) &= ~(1 << 15);
			seq_pvPut(ssId, 3 /* kompBits */, 0);
			snlMsgLog("Softstop nicht bestaetigt");
		}
		return;
	}
}
/* Code for state "Stopit1" in state set "Ueberw" */

/* Entry function for state "Stopit1" in state set "Ueberw" */
static void I_Ueberw_Stopit1(SS_ID ssId, struct UserVar *pVar)
{
/* Entry 1: */
# line 350 "/scratch/workspace/kstr12/source/kueberw.st"
	(pVar->CV107OMSL) = 0;
# line 351 "/scratch/workspace/kstr12/source/kueberw.st"
	(pVar->CV107) = 0.0;
# line 352 "/scratch/workspace/kstr12/source/kueberw.st"
	seq_pvPut(ssId, 24 /* CV107OMSL */, 0);
# line 353 "/scratch/workspace/kstr12/source/kueberw.st"
	seq_pvPut(ssId, 25 /* CV107 */, 0);
# line 355 "/scratch/workspace/kstr12/source/kueberw.st"
	(pVar->fumf12sy157OMSL) = 0;
# line 356 "/scratch/workspace/kstr12/source/kueberw.st"
	(pVar->fumf12sy157) = 0.0;
# line 357 "/scratch/workspace/kstr12/source/kueberw.st"
	(pVar->fumf12sy257OMSL) = 0;
# line 358 "/scratch/workspace/kstr12/source/kueberw.st"
	(pVar->fumf12sy257) = 0.0;
# line 359 "/scratch/workspace/kstr12/source/kueberw.st"
	(pVar->fumf12sy357OMSL) = 0;
# line 360 "/scratch/workspace/kstr12/source/kueberw.st"
	(pVar->fumf12sy357) = 0.0;
# line 361 "/scratch/workspace/kstr12/source/kueberw.st"
	seq_pvPut(ssId, 34 /* fumf12sy157OMSL */, 0);
# line 362 "/scratch/workspace/kstr12/source/kueberw.st"
	seq_pvPut(ssId, 35 /* fumf12sy157 */, 0);
# line 363 "/scratch/workspace/kstr12/source/kueberw.st"
	seq_pvPut(ssId, 36 /* fumf12sy257OMSL */, 0);
# line 364 "/scratch/workspace/kstr12/source/kueberw.st"
	seq_pvPut(ssId, 37 /* fumf12sy257 */, 0);
# line 365 "/scratch/workspace/kstr12/source/kueberw.st"
	seq_pvPut(ssId, 38 /* fumf12sy357OMSL */, 0);
# line 366 "/scratch/workspace/kstr12/source/kueberw.st"
	seq_pvPut(ssId, 39 /* fumf12sy357 */, 0);
# line 369 "/scratch/workspace/kstr12/source/kueberw.st"
	(pVar->byp12sy120) = 1;
# line 370 "/scratch/workspace/kstr12/source/kueberw.st"
	seq_pvPut(ssId, 40 /* byp12sy120 */, 0);
}

/* Delay function for state "Stopit1" in state set "Ueberw" */
static void D_Ueberw_Stopit1(SS_ID ssId, struct UserVar *pVar)
{
# line 374 "/scratch/workspace/kstr12/source/kueberw.st"
	seq_delayInit(ssId, 0, (15));
}

/* Event function for state "Stopit1" in state set "Ueberw" */
static long E_Ueberw_Stopit1(SS_ID ssId, struct UserVar *pVar, short *pTransNum, short *pNextState)
{
# line 374 "/scratch/workspace/kstr12/source/kueberw.st"
	if (seq_delay(ssId, 0))
	{
		*pNextState = 6;
		*pTransNum = 0;
		return TRUE;
	}
	return FALSE;
}

/* Action function for state "Stopit1" in state set "Ueberw" */
static void A_Ueberw_Stopit1(SS_ID ssId, struct UserVar *pVar, short transNum)
{
	switch(transNum)
	{
	case 0:
		{
		}
		return;
	}
}
/* Code for state "Stopit2" in state set "Ueberw" */

/* Entry function for state "Stopit2" in state set "Ueberw" */
static void I_Ueberw_Stopit2(SS_ID ssId, struct UserVar *pVar)
{
/* Entry 1: */
# line 379 "/scratch/workspace/kstr12/source/kueberw.st"
	(pVar->byp12sy220) = 1;
# line 380 "/scratch/workspace/kstr12/source/kueberw.st"
	seq_pvPut(ssId, 41 /* byp12sy220 */, 0);
}

/* Delay function for state "Stopit2" in state set "Ueberw" */
static void D_Ueberw_Stopit2(SS_ID ssId, struct UserVar *pVar)
{
# line 384 "/scratch/workspace/kstr12/source/kueberw.st"
	seq_delayInit(ssId, 0, (15));
}

/* Event function for state "Stopit2" in state set "Ueberw" */
static long E_Ueberw_Stopit2(SS_ID ssId, struct UserVar *pVar, short *pTransNum, short *pNextState)
{
# line 384 "/scratch/workspace/kstr12/source/kueberw.st"
	if (seq_delay(ssId, 0))
	{
		*pNextState = 7;
		*pTransNum = 0;
		return TRUE;
	}
	return FALSE;
}

/* Action function for state "Stopit2" in state set "Ueberw" */
static void A_Ueberw_Stopit2(SS_ID ssId, struct UserVar *pVar, short transNum)
{
	switch(transNum)
	{
	case 0:
		{
		}
		return;
	}
}
/* Code for state "Stopit3" in state set "Ueberw" */

/* Entry function for state "Stopit3" in state set "Ueberw" */
static void I_Ueberw_Stopit3(SS_ID ssId, struct UserVar *pVar)
{
/* Entry 1: */
# line 389 "/scratch/workspace/kstr12/source/kueberw.st"
	(pVar->byp12sy320) = 1;
# line 390 "/scratch/workspace/kstr12/source/kueberw.st"
	seq_pvPut(ssId, 42 /* byp12sy320 */, 0);
}

/* Delay function for state "Stopit3" in state set "Ueberw" */
static void D_Ueberw_Stopit3(SS_ID ssId, struct UserVar *pVar)
{
# line 395 "/scratch/workspace/kstr12/source/kueberw.st"
	seq_delayInit(ssId, 0, (15));
# line 398 "/scratch/workspace/kstr12/source/kueberw.st"
	seq_delayInit(ssId, 1, (30));
}

/* Event function for state "Stopit3" in state set "Ueberw" */
static long E_Ueberw_Stopit3(SS_ID ssId, struct UserVar *pVar, short *pTransNum, short *pNextState)
{
# line 395 "/scratch/workspace/kstr12/source/kueberw.st"
	if (seq_delay(ssId, 0) && (pVar->hdSchieber) < 10)
	{
		*pNextState = 8;
		*pTransNum = 0;
		return TRUE;
	}
# line 398 "/scratch/workspace/kstr12/source/kueberw.st"
	if (seq_delay(ssId, 1))
	{
		*pNextState = 8;
		*pTransNum = 1;
		return TRUE;
	}
	return FALSE;
}

/* Action function for state "Stopit3" in state set "Ueberw" */
static void A_Ueberw_Stopit3(SS_ID ssId, struct UserVar *pVar, short transNum)
{
	switch(transNum)
	{
	case 0:
		{
		}
		return;
	case 1:
		{
# line 397 "/scratch/workspace/kstr12/source/kueberw.st"
			snlMsgLog("HD-Schieber schliesst nicht");
		}
		return;
	}
}
/* Code for state "Stopit4" in state set "Ueberw" */

/* Entry function for state "Stopit4" in state set "Ueberw" */
static void I_Ueberw_Stopit4(SS_ID ssId, struct UserVar *pVar)
{
/* Entry 1: */
# line 404 "/scratch/workspace/kstr12/source/kueberw.st"
	(pVar->CV108OMSL) = 0;
# line 405 "/scratch/workspace/kstr12/source/kueberw.st"
	(pVar->CV108) = 0.0;
# line 406 "/scratch/workspace/kstr12/source/kueberw.st"
	seq_pvPut(ssId, 28 /* CV108OMSL */, 0);
# line 407 "/scratch/workspace/kstr12/source/kueberw.st"
	seq_pvPut(ssId, 29 /* CV108 */, 0);
# line 409 "/scratch/workspace/kstr12/source/kueberw.st"
	(pVar->CV109OMSL) = 0;
# line 410 "/scratch/workspace/kstr12/source/kueberw.st"
	(pVar->CV109) = 0.0;
# line 411 "/scratch/workspace/kstr12/source/kueberw.st"
	seq_pvPut(ssId, 30 /* CV109OMSL */, 0);
# line 412 "/scratch/workspace/kstr12/source/kueberw.st"
	seq_pvPut(ssId, 31 /* CV109 */, 0);
# line 414 "/scratch/workspace/kstr12/source/kueberw.st"
	(pVar->NC157AM) = 1;
# line 415 "/scratch/workspace/kstr12/source/kueberw.st"
	(pVar->NC157SOUT) = 0.0;
# line 416 "/scratch/workspace/kstr12/source/kueberw.st"
	seq_pvPut(ssId, 26 /* NC157AM */, 0);
# line 417 "/scratch/workspace/kstr12/source/kueberw.st"
	seq_pvPut(ssId, 27 /* NC157SOUT */, 0);
# line 419 "/scratch/workspace/kstr12/source/kueberw.st"
	(pVar->CV520OMSL) = 1;
# line 420 "/scratch/workspace/kstr12/source/kueberw.st"
	seq_pvPut(ssId, 20 /* CV520OMSL */, 0);
# line 422 "/scratch/workspace/kstr12/source/kueberw.st"
	(pVar->PC503AM) = 1;
# line 423 "/scratch/workspace/kstr12/source/kueberw.st"
	(pVar->PC503SOUT) = 0.0;
# line 424 "/scratch/workspace/kstr12/source/kueberw.st"
	seq_pvPut(ssId, 32 /* PC503AM */, 0);
# line 425 "/scratch/workspace/kstr12/source/kueberw.st"
	seq_pvPut(ssId, 33 /* PC503SOUT */, 0);
}

/* Delay function for state "Stopit4" in state set "Ueberw" */
static void D_Ueberw_Stopit4(SS_ID ssId, struct UserVar *pVar)
{
# line 428 "/scratch/workspace/kstr12/source/kueberw.st"
# line 431 "/scratch/workspace/kstr12/source/kueberw.st"
	seq_delayInit(ssId, 0, (30));
}

/* Event function for state "Stopit4" in state set "Ueberw" */
static long E_Ueberw_Stopit4(SS_ID ssId, struct UserVar *pVar, short *pTransNum, short *pNextState)
{
# line 428 "/scratch/workspace/kstr12/source/kueberw.st"
	if ((pVar->CV520Endschalter_AUF))
	{
		*pNextState = 9;
		*pTransNum = 0;
		return TRUE;
	}
# line 431 "/scratch/workspace/kstr12/source/kueberw.st"
	if (seq_delay(ssId, 0))
	{
		*pNextState = 9;
		*pTransNum = 1;
		return TRUE;
	}
	return FALSE;
}

/* Action function for state "Stopit4" in state set "Ueberw" */
static void A_Ueberw_Stopit4(SS_ID ssId, struct UserVar *pVar, short transNum)
{
	switch(transNum)
	{
	case 0:
		{
		}
		return;
	case 1:
		{
# line 430 "/scratch/workspace/kstr12/source/kueberw.st"
			snlMsgLog("HD-Regler oeffnet nicht");
		}
		return;
	}
}
/* Code for state "kStop" in state set "Ueberw" */

/* Entry function for state "kStop" in state set "Ueberw" */
static void I_Ueberw_kStop(SS_ID ssId, struct UserVar *pVar)
{
/* Entry 1: */
# line 444 "/scratch/workspace/kstr12/source/kueberw.st"
	(pVar->K1BETRcmd) = 0;
# line 445 "/scratch/workspace/kstr12/source/kueberw.st"
	(pVar->K2BETRcmd) = 0;
# line 446 "/scratch/workspace/kstr12/source/kueberw.st"
	(pVar->K3BETRcmd) = 0;
# line 447 "/scratch/workspace/kstr12/source/kueberw.st"
	(pVar->hdK1BETRcmd) = 0;
# line 448 "/scratch/workspace/kstr12/source/kueberw.st"
	seq_pvPut(ssId, 47 /* K1BETRcmd */, 0);
# line 449 "/scratch/workspace/kstr12/source/kueberw.st"
	seq_pvPut(ssId, 48 /* K2BETRcmd */, 0);
# line 450 "/scratch/workspace/kstr12/source/kueberw.st"
	seq_pvPut(ssId, 49 /* K3BETRcmd */, 0);
# line 451 "/scratch/workspace/kstr12/source/kueberw.st"
	seq_pvPut(ssId, 50 /* hdK1BETRcmd */, 0);
}

/* Delay function for state "kStop" in state set "Ueberw" */
static void D_Ueberw_kStop(SS_ID ssId, struct UserVar *pVar)
{
# line 457 "/scratch/workspace/kstr12/source/kueberw.st"
# line 460 "/scratch/workspace/kstr12/source/kueberw.st"
	seq_delayInit(ssId, 0, (2));
}

/* Event function for state "kStop" in state set "Ueberw" */
static long E_Ueberw_kStop(SS_ID ssId, struct UserVar *pVar, short *pTransNum, short *pNextState)
{
# line 457 "/scratch/workspace/kstr12/source/kueberw.st"
	if ((pVar->K1BETR) != 8 && (pVar->K2BETR) != 8 && (pVar->K3BETR) != 8 && (pVar->hdK1BETR) != 8)
	{
		*pNextState = 10;
		*pTransNum = 0;
		return TRUE;
	}
# line 460 "/scratch/workspace/kstr12/source/kueberw.st"
	if (seq_delay(ssId, 0))
	{
		*pNextState = 9;
		*pTransNum = 1;
		return TRUE;
	}
	return FALSE;
}

/* Action function for state "kStop" in state set "Ueberw" */
static void A_Ueberw_kStop(SS_ID ssId, struct UserVar *pVar, short transNum)
{
	switch(transNum)
	{
	case 0:
		{
		}
		return;
	case 1:
		{
# line 459 "/scratch/workspace/kstr12/source/kueberw.st"
			snlMsgLog("Kompressoren stoppen nicht");
		}
		return;
	}
}
/* Code for state "Stopit5" in state set "Ueberw" */

/* Entry function for state "Stopit5" in state set "Ueberw" */
static void I_Ueberw_Stopit5(SS_ID ssId, struct UserVar *pVar)
{
/* Entry 1: */
# line 465 "/scratch/workspace/kstr12/source/kueberw.st"
	(pVar->enddruckReglerAM) = 1;
# line 466 "/scratch/workspace/kstr12/source/kueberw.st"
	(pVar->enddruckReglerSOUT) = 50.0;
# line 467 "/scratch/workspace/kstr12/source/kueberw.st"
	seq_pvPut(ssId, 22 /* enddruckReglerAM */, 0);
# line 468 "/scratch/workspace/kstr12/source/kueberw.st"
	seq_pvPut(ssId, 23 /* enddruckReglerSOUT */, 0);
# line 471 "/scratch/workspace/kstr12/source/kueberw.st"
	snlMsgLog("Stopit fertig");
# line 472 "/scratch/workspace/kstr12/source/kueberw.st"
	(pVar->SoftStop) = 0;
# line 473 "/scratch/workspace/kstr12/source/kueberw.st"
	seq_pvPut(ssId, 5 /* SoftStop */, 0);
}

/* Delay function for state "Stopit5" in state set "Ueberw" */
static void D_Ueberw_Stopit5(SS_ID ssId, struct UserVar *pVar)
{
# line 476 "/scratch/workspace/kstr12/source/kueberw.st"
	seq_delayInit(ssId, 0, (1));
}

/* Event function for state "Stopit5" in state set "Ueberw" */
static long E_Ueberw_Stopit5(SS_ID ssId, struct UserVar *pVar, short *pTransNum, short *pNextState)
{
# line 476 "/scratch/workspace/kstr12/source/kueberw.st"
	if (seq_delay(ssId, 0))
	{
		*pNextState = 1;
		*pTransNum = 0;
		return TRUE;
	}
	return FALSE;
}

/* Action function for state "Stopit5" in state set "Ueberw" */
static void A_Ueberw_Stopit5(SS_ID ssId, struct UserVar *pVar, short transNum)
{
	switch(transNum)
	{
	case 0:
		{
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
  {"KS2:SNL:KUE:TLIMIT_ai", (void *)OFFSET(struct UserVar, tlimit), "tlimit", 
    "double", 1, 1, 0, 1, 0, 0, 0},

  {"KS2:SNL:KUE:THYST_ai", (void *)OFFSET(struct UserVar, thyst), "thyst", 
    "double", 1, 2, 0, 1, 0, 0, 0},

  {"KS2:SNL:KUE:DTIME_ai", (void *)OFFSET(struct UserVar, dtime), "dtime", 
    "double", 1, 3, 0, 0, 0, 0, 0},

  {"KS2:SNL:KUE:KUEBERW_mbbiD", (void *)OFFSET(struct UserVar, kompBits), "kompBits", 
    "unsigned short", 1, 4, 0, 0, 0, 0, 0},

  {"KS2:SNL:KUE:KSTROK_bi", (void *)OFFSET(struct UserVar, kstrOK), "kstrOK", 
    "char", 1, 5, 0, 0, 0, 0, 0},

  {"KS2:SNL:KUE:SOFTSTOP_bi", (void *)OFFSET(struct UserVar, SoftStop), "SoftStop", 
    "char", 1, 6, 0, 1, 0, 0, 0},

  {"KS2:SNL:KUE:SOFTSTOP_si", (void *)OFFSET(struct UserVar, SoftStopString[0]), "SoftStopString", 
    "string", 1, 7, 0, 0, 0, 0, 0},

  {"KS2:SNL:KUE:KUEBERW_mbbiD.B6", (void *)OFFSET(struct UserVar, ndoel), "ndoel", 
    "char", 1, 8, 0, 1, 0, 0, 0},

  {"KS2:SNL:KUE:KUEBERW_mbbiD.B7", (void *)OFFSET(struct UserVar, hdoel), "hdoel", 
    "char", 1, 9, 0, 1, 0, 0, 0},

  {"22TI560_ai", (void *)OFFSET(struct UserVar, HDoelTemp), "HDoelTemp", 
    "double", 1, 10, 0, 1, 0, 0, 0},

  {"12TI459_ai", (void *)OFFSET(struct UserVar, NDoelTemp), "NDoelTemp", 
    "double", 1, 11, 0, 1, 0, 0, 0},

  {"12Y135_bi", (void *)OFFSET(struct UserVar, T1), "T1", 
    "short", 1, 12, 0, 1, 0, 0, 0},

  {"12Y235_bi", (void *)OFFSET(struct UserVar, T2), "T2", 
    "short", 1, 13, 0, 1, 0, 0, 0},

  {"12Y335_bi", (void *)OFFSET(struct UserVar, T3), "T3", 
    "short", 1, 14, 0, 1, 0, 0, 0},

  {"22Y535_bi", (void *)OFFSET(struct UserVar, T5), "T5", 
    "short", 1, 15, 0, 1, 0, 0, 0},

  {"12TI402_ai", (void *)OFFSET(struct UserVar, T6), "T6", 
    "double", 1, 16, 0, 1, 0, 0, 0},

  {"12TI402_ai.HIHI", (void *)OFFSET(struct UserVar, T6hihi), "T6hihi", 
    "double", 1, 17, 0, 1, 0, 0, 0},

  {"22Y545_bo", (void *)OFFSET(struct UserVar, freigabeHD), "freigabeHD", 
    "short", 1, 18, 0, 0, 0, 0, 0},

  {"12KTRIP_dlog.I13", (void *)OFFSET(struct UserVar, freigabeND), "freigabeND", 
    "short", 1, 19, 0, 0, 0, 0, 0},

  {"22GI519_ai", (void *)OFFSET(struct UserVar, hdSchieber), "hdSchieber", 
    "double", 1, 20, 0, 1, 0, 0, 0},

  {"22CV520_ao.OMSL", (void *)OFFSET(struct UserVar, CV520OMSL), "CV520OMSL", 
    "short", 1, 21, 0, 0, 0, 0, 0},

  {"22CV520_dlog.SB2", (void *)OFFSET(struct UserVar, CV520Endschalter_AUF), "CV520Endschalter_AUF", 
    "short", 1, 22, 0, 1, 0, 0, 0},

  {"32PC106_pid.AM", (void *)OFFSET(struct UserVar, enddruckReglerAM), "enddruckReglerAM", 
    "short", 1, 23, 0, 0, 0, 0, 0},

  {"32PC106_pid.SOUT", (void *)OFFSET(struct UserVar, enddruckReglerSOUT), "enddruckReglerSOUT", 
    "double", 1, 24, 0, 0, 0, 0, 0},

  {"32CV107_ao.OMSL", (void *)OFFSET(struct UserVar, CV107OMSL), "CV107OMSL", 
    "short", 1, 25, 0, 0, 0, 0, 0},

  {"32CV107_ao", (void *)OFFSET(struct UserVar, CV107), "CV107", 
    "double", 1, 26, 0, 0, 0, 0, 0},

  {"12NC157_pid.AM", (void *)OFFSET(struct UserVar, NC157AM), "NC157AM", 
    "short", 1, 27, 0, 0, 0, 0, 0},

  {"12NC157_pid.SOUT", (void *)OFFSET(struct UserVar, NC157SOUT), "NC157SOUT", 
    "double", 1, 28, 0, 0, 0, 0, 0},

  {"32CV108_ao.OMSL", (void *)OFFSET(struct UserVar, CV108OMSL), "CV108OMSL", 
    "short", 1, 29, 0, 0, 0, 0, 0},

  {"32CV108_ao", (void *)OFFSET(struct UserVar, CV108), "CV108", 
    "double", 1, 30, 0, 0, 0, 0, 0},

  {"32CV109_ao.OMSL", (void *)OFFSET(struct UserVar, CV109OMSL), "CV109OMSL", 
    "short", 1, 31, 0, 0, 0, 0, 0},

  {"32CV109_ao", (void *)OFFSET(struct UserVar, CV109), "CV109", 
    "double", 1, 32, 0, 0, 0, 0, 0},

  {"22PC503_pid.AM", (void *)OFFSET(struct UserVar, PC503AM), "PC503AM", 
    "short", 1, 33, 0, 0, 0, 0, 0},

  {"22PC503_pid.SOUT", (void *)OFFSET(struct UserVar, PC503SOUT), "PC503SOUT", 
    "double", 1, 34, 0, 0, 0, 0, 0},

  {"12SY157_ao.OMSL", (void *)OFFSET(struct UserVar, fumf12sy157OMSL), "fumf12sy157OMSL", 
    "short", 1, 35, 0, 0, 0, 0, 0},

  {"12SY157_ao", (void *)OFFSET(struct UserVar, fumf12sy157), "fumf12sy157", 
    "double", 1, 36, 0, 0, 0, 0, 0},

  {"12SY257_ao.OMSL", (void *)OFFSET(struct UserVar, fumf12sy257OMSL), "fumf12sy257OMSL", 
    "short", 1, 37, 0, 0, 0, 0, 0},

  {"12SY257_ao", (void *)OFFSET(struct UserVar, fumf12sy257), "fumf12sy257", 
    "double", 1, 38, 0, 0, 0, 0, 0},

  {"12SY357_ao.OMSL", (void *)OFFSET(struct UserVar, fumf12sy357OMSL), "fumf12sy357OMSL", 
    "short", 1, 39, 0, 0, 0, 0, 0},

  {"12SY357_ao", (void *)OFFSET(struct UserVar, fumf12sy357), "fumf12sy357", 
    "double", 1, 40, 0, 0, 0, 0, 0},

  {"12SY120_dlog.CMD", (void *)OFFSET(struct UserVar, byp12sy120), "byp12sy120", 
    "short", 1, 41, 0, 0, 0, 0, 0},

  {"12SY220_dlog.CMD", (void *)OFFSET(struct UserVar, byp12sy220), "byp12sy220", 
    "short", 1, 42, 0, 0, 0, 0, 0},

  {"12SY320_dlog.CMD", (void *)OFFSET(struct UserVar, byp12sy320), "byp12sy320", 
    "short", 1, 43, 0, 0, 0, 0, 0},

  {"12K1BETR_dlog", (void *)OFFSET(struct UserVar, K1BETR), "K1BETR", 
    "short", 1, 44, 0, 0, 0, 0, 0},

  {"12K2BETR_dlog", (void *)OFFSET(struct UserVar, K2BETR), "K2BETR", 
    "short", 1, 45, 0, 0, 0, 0, 0},

  {"12K3BETR_dlog", (void *)OFFSET(struct UserVar, K3BETR), "K3BETR", 
    "short", 1, 46, 0, 0, 0, 0, 0},

  {"22K1BETR_dlog", (void *)OFFSET(struct UserVar, hdK1BETR), "hdK1BETR", 
    "short", 1, 47, 0, 0, 0, 0, 0},

  {"12K1BETR_dlog.CMD", (void *)OFFSET(struct UserVar, K1BETRcmd), "K1BETRcmd", 
    "short", 1, 48, 0, 0, 0, 0, 0},

  {"12K2BETR_dlog.CMD", (void *)OFFSET(struct UserVar, K2BETRcmd), "K2BETRcmd", 
    "short", 1, 49, 0, 0, 0, 0, 0},

  {"12K3BETR_dlog.CMD", (void *)OFFSET(struct UserVar, K3BETRcmd), "K3BETRcmd", 
    "short", 1, 50, 0, 0, 0, 0, 0},

  {"22K1BETR_dlog.CMD", (void *)OFFSET(struct UserVar, hdK1BETRcmd), "hdK1BETRcmd", 
    "short", 1, 51, 0, 0, 0, 0, 0},

};

/* Local Variables (some may also be connected to PVs above)
 *   ... does not include escaped declarations
 */
static struct seqVar seqVar[NUM_LOCVARS] = {
  /* name type_i type_s class dim1 dim2 initial address */
  { "text",	  1, "char",	 1,  40,   1, NULL, (void *)OFFSET(struct UserVar, text[0]) },
  { "tlimit",	  6, "double",	 0,   1,   1, NULL, (void *)OFFSET(struct UserVar, tlimit) },
  { "thyst",	  6, "double",	 0,   1,   1, NULL, (void *)OFFSET(struct UserVar, thyst) },
  { "dtime",	  6, "double",	 0,   1,   1, NULL, (void *)OFFSET(struct UserVar, dtime) },
  { "kompBits",	 12, "unsigned short",	 0,   1,   1, NULL, (void *)OFFSET(struct UserVar, kompBits) },
  { "kstrOK",	  1, "char",	 0,   1,   1, NULL, (void *)OFFSET(struct UserVar, kstrOK) },
  { "SoftStop",	  1, "char",	 0,   1,   1, NULL, (void *)OFFSET(struct UserVar, SoftStop) },
  { "SoftStopString",	  7, "string",	 0,   1,   1, NULL, (void *)OFFSET(struct UserVar, SoftStopString[0]) },
  { "ndoel",	  1, "char",	 0,   1,   1, NULL, (void *)OFFSET(struct UserVar, ndoel) },
  { "hdoel",	  1, "char",	 0,   1,   1, NULL, (void *)OFFSET(struct UserVar, hdoel) },
  { "HDoelTemp",	  6, "double",	 0,   1,   1, NULL, (void *)OFFSET(struct UserVar, HDoelTemp) },
  { "NDoelTemp",	  6, "double",	 0,   1,   1, NULL, (void *)OFFSET(struct UserVar, NDoelTemp) },
  { "T1",	  2, "short",	 0,   1,   1, NULL, (void *)OFFSET(struct UserVar, T1) },
  { "T2",	  2, "short",	 0,   1,   1, NULL, (void *)OFFSET(struct UserVar, T2) },
  { "T3",	  2, "short",	 0,   1,   1, NULL, (void *)OFFSET(struct UserVar, T3) },
  { "T5",	  2, "short",	 0,   1,   1, NULL, (void *)OFFSET(struct UserVar, T5) },
  { "T6",	  6, "double",	 0,   1,   1, NULL, (void *)OFFSET(struct UserVar, T6) },
  { "T6hihi",	  6, "double",	 0,   1,   1, NULL, (void *)OFFSET(struct UserVar, T6hihi) },
  { "freigabeHD",	  2, "short",	 0,   1,   1, NULL, (void *)OFFSET(struct UserVar, freigabeHD) },
  { "freigabeND",	  2, "short",	 0,   1,   1, NULL, (void *)OFFSET(struct UserVar, freigabeND) },
  { "hdSchieber",	  6, "double",	 0,   1,   1, NULL, (void *)OFFSET(struct UserVar, hdSchieber) },
  { "CV520OMSL",	  2, "short",	 0,   1,   1, NULL, (void *)OFFSET(struct UserVar, CV520OMSL) },
  { "CV520Endschalter_AUF",	  2, "short",	 0,   1,   1, NULL, (void *)OFFSET(struct UserVar, CV520Endschalter_AUF) },
  { "enddruckReglerAM",	  2, "short",	 0,   1,   1, NULL, (void *)OFFSET(struct UserVar, enddruckReglerAM) },
  { "enddruckReglerSOUT",	  6, "double",	 0,   1,   1, NULL, (void *)OFFSET(struct UserVar, enddruckReglerSOUT) },
  { "CV107OMSL",	  2, "short",	 0,   1,   1, NULL, (void *)OFFSET(struct UserVar, CV107OMSL) },
  { "CV107",	  6, "double",	 0,   1,   1, NULL, (void *)OFFSET(struct UserVar, CV107) },
  { "NC157AM",	  2, "short",	 0,   1,   1, NULL, (void *)OFFSET(struct UserVar, NC157AM) },
  { "NC157SOUT",	  6, "double",	 0,   1,   1, NULL, (void *)OFFSET(struct UserVar, NC157SOUT) },
  { "CV108OMSL",	  2, "short",	 0,   1,   1, NULL, (void *)OFFSET(struct UserVar, CV108OMSL) },
  { "CV108",	  6, "double",	 0,   1,   1, NULL, (void *)OFFSET(struct UserVar, CV108) },
  { "CV109OMSL",	  2, "short",	 0,   1,   1, NULL, (void *)OFFSET(struct UserVar, CV109OMSL) },
  { "CV109",	  6, "double",	 0,   1,   1, NULL, (void *)OFFSET(struct UserVar, CV109) },
  { "PC503AM",	  2, "short",	 0,   1,   1, NULL, (void *)OFFSET(struct UserVar, PC503AM) },
  { "PC503SOUT",	  6, "double",	 0,   1,   1, NULL, (void *)OFFSET(struct UserVar, PC503SOUT) },
  { "fumf12sy157OMSL",	  2, "short",	 0,   1,   1, NULL, (void *)OFFSET(struct UserVar, fumf12sy157OMSL) },
  { "fumf12sy157",	  6, "double",	 0,   1,   1, NULL, (void *)OFFSET(struct UserVar, fumf12sy157) },
  { "fumf12sy257OMSL",	  2, "short",	 0,   1,   1, NULL, (void *)OFFSET(struct UserVar, fumf12sy257OMSL) },
  { "fumf12sy257",	  6, "double",	 0,   1,   1, NULL, (void *)OFFSET(struct UserVar, fumf12sy257) },
  { "fumf12sy357OMSL",	  2, "short",	 0,   1,   1, NULL, (void *)OFFSET(struct UserVar, fumf12sy357OMSL) },
  { "fumf12sy357",	  6, "double",	 0,   1,   1, NULL, (void *)OFFSET(struct UserVar, fumf12sy357) },
  { "byp12sy120",	  2, "short",	 0,   1,   1, NULL, (void *)OFFSET(struct UserVar, byp12sy120) },
  { "byp12sy220",	  2, "short",	 0,   1,   1, NULL, (void *)OFFSET(struct UserVar, byp12sy220) },
  { "byp12sy320",	  2, "short",	 0,   1,   1, NULL, (void *)OFFSET(struct UserVar, byp12sy320) },
  { "K1BETR",	  2, "short",	 0,   1,   1, NULL, (void *)OFFSET(struct UserVar, K1BETR) },
  { "K2BETR",	  2, "short",	 0,   1,   1, NULL, (void *)OFFSET(struct UserVar, K2BETR) },
  { "K3BETR",	  2, "short",	 0,   1,   1, NULL, (void *)OFFSET(struct UserVar, K3BETR) },
  { "hdK1BETR",	  2, "short",	 0,   1,   1, NULL, (void *)OFFSET(struct UserVar, hdK1BETR) },
  { "K1BETRcmd",	  2, "short",	 0,   1,   1, NULL, (void *)OFFSET(struct UserVar, K1BETRcmd) },
  { "K2BETRcmd",	  2, "short",	 0,   1,   1, NULL, (void *)OFFSET(struct UserVar, K2BETRcmd) },
  { "K3BETRcmd",	  2, "short",	 0,   1,   1, NULL, (void *)OFFSET(struct UserVar, K3BETRcmd) },
  { "hdK1BETRcmd",	  2, "short",	 0,   1,   1, NULL, (void *)OFFSET(struct UserVar, hdK1BETRcmd) }
};

/* Event masks for state set HDTemp */
	/* Event mask for state ok: */
static bitMask	EM_HDTemp_ok[] = {
	0x00000402,
	0x00000000,
};
	/* Event mask for state not_ok: */
static bitMask	EM_HDTemp_not_ok[] = {
	0x00000406,
	0x00000000,
};

/* State Blocks */

static struct seqState state_HDTemp[] = {
	/* State "ok" */ {
	/* state name */       "ok",
	/* action function */ (ACTION_FUNC) A_HDTemp_ok,
	/* event function */  (EVENT_FUNC) E_HDTemp_ok,
	/* delay function */   (DELAY_FUNC) D_HDTemp_ok,
	/* entry function */   (ENTRY_FUNC) I_HDTemp_ok,
	/* exit function */   (EXIT_FUNC) 0,
	/* event mask array */ EM_HDTemp_ok,
	/* state options */   (0)},

	/* State "not_ok" */ {
	/* state name */       "not_ok",
	/* action function */ (ACTION_FUNC) A_HDTemp_not_ok,
	/* event function */  (EVENT_FUNC) E_HDTemp_not_ok,
	/* delay function */   (DELAY_FUNC) D_HDTemp_not_ok,
	/* entry function */   (ENTRY_FUNC) I_HDTemp_not_ok,
	/* exit function */   (EXIT_FUNC) 0,
	/* event mask array */ EM_HDTemp_not_ok,
	/* state options */   (0)},


};

/* Event masks for state set NDTemp */
	/* Event mask for state ok: */
static bitMask	EM_NDTemp_ok[] = {
	0x00000802,
	0x00000000,
};
	/* Event mask for state not_ok: */
static bitMask	EM_NDTemp_not_ok[] = {
	0x00000806,
	0x00000000,
};

/* State Blocks */

static struct seqState state_NDTemp[] = {
	/* State "ok" */ {
	/* state name */       "ok",
	/* action function */ (ACTION_FUNC) A_NDTemp_ok,
	/* event function */  (EVENT_FUNC) E_NDTemp_ok,
	/* delay function */   (DELAY_FUNC) D_NDTemp_ok,
	/* entry function */   (ENTRY_FUNC) I_NDTemp_ok,
	/* exit function */   (EXIT_FUNC) 0,
	/* event mask array */ EM_NDTemp_ok,
	/* state options */   (0)},

	/* State "not_ok" */ {
	/* state name */       "not_ok",
	/* action function */ (ACTION_FUNC) A_NDTemp_not_ok,
	/* event function */  (EVENT_FUNC) E_NDTemp_not_ok,
	/* delay function */   (DELAY_FUNC) D_NDTemp_not_ok,
	/* entry function */   (ENTRY_FUNC) I_NDTemp_not_ok,
	/* exit function */   (EXIT_FUNC) 0,
	/* event mask array */ EM_NDTemp_not_ok,
	/* state options */   (0)},


};

/* Event masks for state set Ueberw */
	/* Event mask for state top: */
static bitMask	EM_Ueberw_top[] = {
	0x00000000,
	0x00000000,
};
	/* Event mask for state Loop_NOTOK: */
static bitMask	EM_Ueberw_Loop_NOTOK[] = {
	0x0003f304,
	0x00000000,
};
	/* Event mask for state Loop_OK: */
static bitMask	EM_Ueberw_Loop_OK[] = {
	0x0003f340,
	0x00000000,
};
	/* Event mask for state EmergencyStop: */
static bitMask	EM_Ueberw_EmergencyStop[] = {
	0x00000000,
	0x00000000,
};
	/* Event mask for state SoftStop: */
static bitMask	EM_Ueberw_SoftStop[] = {
	0x00000048,
	0x00000000,
};
	/* Event mask for state Stopit1: */
static bitMask	EM_Ueberw_Stopit1[] = {
	0x00000000,
	0x00000000,
};
	/* Event mask for state Stopit2: */
static bitMask	EM_Ueberw_Stopit2[] = {
	0x00000000,
	0x00000000,
};
	/* Event mask for state Stopit3: */
static bitMask	EM_Ueberw_Stopit3[] = {
	0x00100000,
	0x00000000,
};
	/* Event mask for state Stopit4: */
static bitMask	EM_Ueberw_Stopit4[] = {
	0x00400000,
	0x00000000,
};
	/* Event mask for state kStop: */
static bitMask	EM_Ueberw_kStop[] = {
	0x00000000,
	0x0000f000,
};
	/* Event mask for state Stopit5: */
static bitMask	EM_Ueberw_Stopit5[] = {
	0x00000000,
	0x00000000,
};

/* State Blocks */

static struct seqState state_Ueberw[] = {
	/* State "top" */ {
	/* state name */       "top",
	/* action function */ (ACTION_FUNC) A_Ueberw_top,
	/* event function */  (EVENT_FUNC) E_Ueberw_top,
	/* delay function */   (DELAY_FUNC) D_Ueberw_top,
	/* entry function */   (ENTRY_FUNC) I_Ueberw_top,
	/* exit function */   (EXIT_FUNC) 0,
	/* event mask array */ EM_Ueberw_top,
	/* state options */   (0)},

	/* State "Loop_NOTOK" */ {
	/* state name */       "Loop_NOTOK",
	/* action function */ (ACTION_FUNC) A_Ueberw_Loop_NOTOK,
	/* event function */  (EVENT_FUNC) E_Ueberw_Loop_NOTOK,
	/* delay function */   (DELAY_FUNC) D_Ueberw_Loop_NOTOK,
	/* entry function */   (ENTRY_FUNC) I_Ueberw_Loop_NOTOK,
	/* exit function */   (EXIT_FUNC) 0,
	/* event mask array */ EM_Ueberw_Loop_NOTOK,
	/* state options */   (0)},

	/* State "Loop_OK" */ {
	/* state name */       "Loop_OK",
	/* action function */ (ACTION_FUNC) A_Ueberw_Loop_OK,
	/* event function */  (EVENT_FUNC) E_Ueberw_Loop_OK,
	/* delay function */   (DELAY_FUNC) D_Ueberw_Loop_OK,
	/* entry function */   (ENTRY_FUNC) I_Ueberw_Loop_OK,
	/* exit function */   (EXIT_FUNC) O_Ueberw_Loop_OK,
	/* event mask array */ EM_Ueberw_Loop_OK,
	/* state options */   (0)},

	/* State "EmergencyStop" */ {
	/* state name */       "EmergencyStop",
	/* action function */ (ACTION_FUNC) A_Ueberw_EmergencyStop,
	/* event function */  (EVENT_FUNC) E_Ueberw_EmergencyStop,
	/* delay function */   (DELAY_FUNC) D_Ueberw_EmergencyStop,
	/* entry function */   (ENTRY_FUNC) I_Ueberw_EmergencyStop,
	/* exit function */   (EXIT_FUNC) 0,
	/* event mask array */ EM_Ueberw_EmergencyStop,
	/* state options */   (0)},

	/* State "SoftStop" */ {
	/* state name */       "SoftStop",
	/* action function */ (ACTION_FUNC) A_Ueberw_SoftStop,
	/* event function */  (EVENT_FUNC) E_Ueberw_SoftStop,
	/* delay function */   (DELAY_FUNC) D_Ueberw_SoftStop,
	/* entry function */   (ENTRY_FUNC) I_Ueberw_SoftStop,
	/* exit function */   (EXIT_FUNC) 0,
	/* event mask array */ EM_Ueberw_SoftStop,
	/* state options */   (0)},

	/* State "Stopit1" */ {
	/* state name */       "Stopit1",
	/* action function */ (ACTION_FUNC) A_Ueberw_Stopit1,
	/* event function */  (EVENT_FUNC) E_Ueberw_Stopit1,
	/* delay function */   (DELAY_FUNC) D_Ueberw_Stopit1,
	/* entry function */   (ENTRY_FUNC) I_Ueberw_Stopit1,
	/* exit function */   (EXIT_FUNC) 0,
	/* event mask array */ EM_Ueberw_Stopit1,
	/* state options */   (0)},

	/* State "Stopit2" */ {
	/* state name */       "Stopit2",
	/* action function */ (ACTION_FUNC) A_Ueberw_Stopit2,
	/* event function */  (EVENT_FUNC) E_Ueberw_Stopit2,
	/* delay function */   (DELAY_FUNC) D_Ueberw_Stopit2,
	/* entry function */   (ENTRY_FUNC) I_Ueberw_Stopit2,
	/* exit function */   (EXIT_FUNC) 0,
	/* event mask array */ EM_Ueberw_Stopit2,
	/* state options */   (0)},

	/* State "Stopit3" */ {
	/* state name */       "Stopit3",
	/* action function */ (ACTION_FUNC) A_Ueberw_Stopit3,
	/* event function */  (EVENT_FUNC) E_Ueberw_Stopit3,
	/* delay function */   (DELAY_FUNC) D_Ueberw_Stopit3,
	/* entry function */   (ENTRY_FUNC) I_Ueberw_Stopit3,
	/* exit function */   (EXIT_FUNC) 0,
	/* event mask array */ EM_Ueberw_Stopit3,
	/* state options */   (0)},

	/* State "Stopit4" */ {
	/* state name */       "Stopit4",
	/* action function */ (ACTION_FUNC) A_Ueberw_Stopit4,
	/* event function */  (EVENT_FUNC) E_Ueberw_Stopit4,
	/* delay function */   (DELAY_FUNC) D_Ueberw_Stopit4,
	/* entry function */   (ENTRY_FUNC) I_Ueberw_Stopit4,
	/* exit function */   (EXIT_FUNC) 0,
	/* event mask array */ EM_Ueberw_Stopit4,
	/* state options */   (0)},

	/* State "kStop" */ {
	/* state name */       "kStop",
	/* action function */ (ACTION_FUNC) A_Ueberw_kStop,
	/* event function */  (EVENT_FUNC) E_Ueberw_kStop,
	/* delay function */   (DELAY_FUNC) D_Ueberw_kStop,
	/* entry function */   (ENTRY_FUNC) I_Ueberw_kStop,
	/* exit function */   (EXIT_FUNC) 0,
	/* event mask array */ EM_Ueberw_kStop,
	/* state options */   (0)},

	/* State "Stopit5" */ {
	/* state name */       "Stopit5",
	/* action function */ (ACTION_FUNC) A_Ueberw_Stopit5,
	/* event function */  (EVENT_FUNC) E_Ueberw_Stopit5,
	/* delay function */   (DELAY_FUNC) D_Ueberw_Stopit5,
	/* entry function */   (ENTRY_FUNC) I_Ueberw_Stopit5,
	/* exit function */   (EXIT_FUNC) 0,
	/* event mask array */ EM_Ueberw_Stopit5,
	/* state options */   (0)},


};

/* State Set Blocks */
static struct seqSS seqSS[NUM_SS] = {
	/* State set "HDTemp" */ {
	/* ss name */            "HDTemp",
	/* ptr to state block */ state_HDTemp,
	/* number of states */   2,
	/* error state */        -1},


	/* State set "NDTemp" */ {
	/* ss name */            "NDTemp",
	/* ptr to state block */ state_NDTemp,
	/* number of states */   2,
	/* error state */        -1},


	/* State set "Ueberw" */ {
	/* ss name */            "Ueberw",
	/* ptr to state block */ state_Ueberw,
	/* number of states */   11,
	/* error state */        -1},
};

/* Program parameter list */
static char prog_param[] = "";

/* State Program table (global) */
struct seqProgram kueberw = {
	/* magic number */       20060421,
	/* *name */              "kueberw",
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

void kueberwRegistrar (void) {
    seqRegisterSequencerCommands();
    seqRegisterSequencerProgram (&kueberw);
}
epicsExportRegistrar(kueberwRegistrar);

