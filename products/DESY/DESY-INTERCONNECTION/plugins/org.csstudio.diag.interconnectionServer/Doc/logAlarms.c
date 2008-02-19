/*******************************************************************************

Project:
    Alarm Logger for EPICS

File:
    logAlarms.c

Description:
    Alarm hook routine for record scanning and alarm logging client
    and a server waiting for commands from alarm servers.

Author:
    Bernd Schoeneburg
Created:
    24 October 2006
Version:
    0.2

Modification:
V0.1	2006-10-24	Sbg	Initial implementation
V0.2	2007-07-01	Sbg	Problem with bucketLib usage fixed, more msgTasks,
				receiver task.

(c) 2006,2007 DESY

*******************************************************************************/

#if 0 /* activate debugging here */
#define DEBUG
#endif

#ifdef DEBUG
int logAlarmsDebug=0;
#define DEBUG0(level,format) {if(logAlarmsDebug>=level) epicsPrintf(format);}
#define DEBUG1(level,format,p1) {if(logAlarmsDebug>=level) epicsPrintf(format,p1);}
#define DEBUG2(level,format,p1,p2) {if(logAlarmsDebug>=level) epicsPrintf(format,p1,p2);}
#define DEBUG3(level,format,p1,p2,p3) {if(logAlarmsDebug>=level) epicsPrintf(format,p1,p2,p3);}
#else
#define DEBUG0(level,format) {}
#define DEBUG1(level,format,p1) {}
#define DEBUG2(level,format,p1,p2) {}
#define DEBUG3(level,format,p1,p2,p3) {}
#endif

/* vxWorks */
#include <vxWorks.h>
#include <semLib.h>		/* semCreate()... */
#include <sysLib.h>		/* sysClkRateGet() */
#include <hostLib.h>		/* gethostname() */
#include <taskLib.h>		/* taskSpawn() */
#include <tickLib.h>		/* tickGet() */
#include <sockLib.h>		/* socket() */
#include <ioLib.h>		/* ioctl() */
#include <selectLib.h>		/* select() */
#include <intLib.h>		/* intLock() */
#include <assert.h>		/* assert() */

/* C */
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>

/* EPICS */
#include <alarmString.h>	/* alarm strings */
#include <tsDefs.h>		/* TS_STAMP */
#include <recGbl.h>		/* Hook */
#include <dbStaticLib.h>	/* DBENTRY */
#include <bucketLib.h>		/* EPICS hash facility */
#include <bsdSocketResource.h>	/* aToIPAddr */


#ifndef TRUE
#define TRUE 1
#define FALSE 0
#endif

/*
 * Task stack sizes
 *
 * (original stack sizes are appropriate for the 68k)
 * ARCH_STACK_FACTOR allows scaling the stacks on particular
 * processor architectures
 */
#if CPU_FAMILY == MC680X0
#define ARCH_STACK_FACTOR 1
#elif CPU_FAMILY == SPARC
#define ARCH_STACK_FACTOR 2
#else
#define ARCH_STACK_FACTOR 2
#endif

#define DEFAULT_MSG_PORT	18324		/* port for messages */
#define DEFAULT_CMD_PORT	18325		/* port for commands */
/* task options */
#define CLIENT_TASK_PRIO	120		/* vxWorks task priority */
#define CLIENT_TASK_OPTIONS	0		/* no task options */
#define CLIENT_TASK_STACK	5000*ARCH_STACK_FACTOR		/* task stack size */
#define BEACON_TASK_PRIO	119		/* vxWorks task priority */
#define BEACON_TASK_OPTIONS	0		/* no task options */
#define BEACON_TASK_STACK	4000*ARCH_STACK_FACTOR		/* task stack size */
#define CONTROL_TASK_PRIO	119		/* vxWorks task priority */
#define CONTROL_TASK_OPTIONS	0		/* no task options */
#define CONTROL_TASK_STACK	2000*ARCH_STACK_FACTOR		/* task stack size */
#define REPLY_TASK_PRIO		56		/* vxWorks task priority */
#define REPLY_TASK_OPTIONS	0		/* no task options */
#define REPLY_TASK_STACK	2000*ARCH_STACK_FACTOR		/* task stack size */

#define SERVERS_MAX		4		/* may number of servers */

#define MAX_MESSAGE_LENGTH	511		/* length of messages */
#define RX_BUF_SIZE		127		/* server replies and commands */

#define MSG_TASK_NUM		5		/* num unconfirmed msg */

/* Ring buffer size relative to number of records in database */
#define RING_NORMAL_MULTI	0.8		/* ring has 80% of rec */
#define RING_OVER_DIVI		10		/* over has rec + rec/10 */

#define INIT_DELAY_SEC		15.0		/* wait at init for .. sec */
#define BEACON_PERIOD_SEC	3.0		/* check sending beacon every .. sec */
#define BEACON_IDLE_SEC		2.5		/* send beacon after .. sec idle */
/* define MSG_RETRY_SEC		0.1		** retry send msg after .. sec */
#define RETRY_MAX		8		/* max retries to send beacon
					to the selected server until switch */
#define BEACON_REPLY_TMO_SEC	0.6		/* tmo server response ... sec */
#define MESSAGE_REPLY_TMO_SEC	1.0		/* tmo server response ... sec */
#define MESSAGE_RETRY_WAIT_SEC	0.02		/* time after try to send msg */

/* some macros */
#define RINGPOS(p)		(((p + ringSize) - pRingRead) % ringSize)
#define LOCK_BUCK		semTake (bucketSem, WAIT_FOREVER);
#define UNLOCK_BUCK		semGive (bucketSem);
#define LOCK_RING		semTake (ringSem, WAIT_FOREVER);
#define UNLOCK_RING		semGive (ringSem);
#define LOCK_RING_W		semTake (ringWriteSem, WAIT_FOREVER);
#define UNLOCK_RING_W		semGive (ringWriteSem);
#define LOCK_MSG_SOCK		semTake (msgSockSem, WAIT_FOREVER);
#define UNLOCK_MSG_SOCK		semGive (msgSockSem);
/******************************************************************************/

typedef enum {
    mt_Event,
    mt_Status
} msgType_t;

typedef struct {
    dbCommon		*pdbc;
    unsigned short	ostat;
    unsigned short	osevr;
    unsigned short	nstat;
    unsigned short	nsevr;
    TS_STAMP		stamp;
    msgType_t		msgType;
} msg_t;

typedef struct {
    msg_t		msg;
    enum {
	hs_Non,
	hs_Passive,
	hs_Active
    }			hashStatus;
    unsigned short	msevr;
    unsigned short	overwritten;
} msgBuffer_t;

/* these name-value-pairs can appear in messages */
typedef enum {
    name_SERIAL,
    name_COMMAND,
    name_NAME,
    name_STATUS
} name_t;

/* these commands are accepted */
typedef enum {
    cmd_TAKEOVER,
    cmd_DISCONNECT,
    cmd_SENDALLALARMS,
    cmd_SENDALARM,
    cmd_SENDSTATUS
} cmd_t;

/* structure to distribute replies */
typedef struct {
    unsigned long	id;
    int			serverId;
    int			replyStatus;
    SEM_ID		replySem;
    SEM_ID		privateSem;
} taskPrivate_t;

/* the handle to the ioc database */
extern struct dbBase *pdbbase;

LOCAL int logEvent (dbCommon *, unsigned short, unsigned short); /* hook */
LOCAL int alarmMessageTask (int);		/* process */
LOCAL int alarmBeaconTask ();			/* process */
LOCAL int alarmCommandTask ();			/* server process */
LOCAL int replyReceiveTask ();			/* waiting for replies */
LOCAL int ringInsert (msg_t *);			/* msg into ring */
LOCAL int ringRead (msgBuffer_t *);		/* msg from ring */
LOCAL void queueAllRecords ();			/* get status of ALL rec */
LOCAL int queueRecord (const char *);		/* get status of one rec */
LOCAL int msgToText (char *, short,
  msgBuffer_t *, unsigned long);		/* generate text message */
LOCAL int sendMessageToServer (const char *,
  int, unsigned long, taskPrivate_t *);		/* msg to server x (tmo) */
LOCAL int sendBeaconToServer (			/* beacon to server x (tmo) */
  int, unsigned long, taskPrivate_t *);
LOCAL int iName (const char *, name_t *);	/* recognize name */
LOCAL int iCmd (const char *, cmd_t *);		/* recognize command */
LOCAL void cleanup (int);			/* cleanup if return ERROR */
LOCAL char * stampToText (TS_STAMP *pStamp, char *textBuffer); /* EPICS TS to Text */
LOCAL unsigned long getMessageId ();		/* get net unique message id */


static BOOL		initialized=FALSE, allAcceptOption=FALSE;
static int		ringSize, ringHighWater, msgLost;
static msgBuffer_t	*pRingBottom, *pRingTop;
static msgBuffer_t	*pRingRead, *pRingWrite;
static SEM_ID		wakeupSem;
static SEM_ID		ringSem;
static SEM_ID		ringWriteSem;
static SEM_ID		msgSockSem;
static SEM_ID		bucketSem;
static BUCKET		*pbuck;
static unsigned long	tickInitWait;
static unsigned long	tickMaxSilent;
static unsigned long	tickBeaconPeriod;
static unsigned long	tickBeaconReplyTmo;
static unsigned long	tickMessageReplyTmo;
static unsigned long	tickMsgRetryWait;
static int		numServersConfigured, serverSelected, serverSelectedLast;
static struct {int up; unsigned long tickMsg;} serverStatus[SERVERS_MAX];
static char		messageHeader[MAX_MESSAGE_LENGTH+1];
static short		textMsgSize;
static int		clkRate;
static struct sockaddr_in	msgReplyInAddr, msgServerAddr[SERVERS_MAX];
static struct sockaddr_in	cmdServerAddr; /* we are cmd_server */
static int		sockAddrSize=sizeof(struct sockaddr_in);
static unsigned short	msg_port, cmd_port;
static int		msg_soc, cmd_soc;
static unsigned long	beaconId=2000000000;
/* for reply distribution */
static taskPrivate_t	*taskPrivateTab[MSG_TASK_NUM+1];

/*******************************************************************************
Routine:
    logAlarmsInit

Purpose:
    Initialize AlarmLogClient and set AlarmHook pointer

Description:

Returns:
    OK, or ERROR

*/

int epicsShareAPI logAlarmsInit (
) {
    DBENTRY     dbentry;
    DBENTRY     *pdbentry=&dbentry;
    long        status;
    int		numRecords=0;
    int		tid[MSG_TASK_NUM], btid, ctid, rtid;
    int		normalRangeSize, overRangeSize;
    struct sockaddr_in	*psin;
    int		node;
    char	string[32];
    char	*pstring, *serverList, *ptok, *pheader;
    int		i;

    if (initialized) {
	errlogPrintf ("logAlarmsInit: already initialized\n");
	return ERROR;
    }

    clkRate = sysClkRateGet();

    tickInitWait = (unsigned long)(clkRate * INIT_DELAY_SEC + 0.5);

    tickMaxSilent = (unsigned long)(clkRate * BEACON_IDLE_SEC + 0.5);
    tickBeaconPeriod = (unsigned long)(clkRate * BEACON_PERIOD_SEC + 0.5);
    tickBeaconReplyTmo = (unsigned long)(clkRate * BEACON_REPLY_TMO_SEC + 0.5);
    tickMessageReplyTmo = (unsigned long)(clkRate * MESSAGE_REPLY_TMO_SEC + 0.5);
    /* wait time between retrying to send messages (>= 1 tick) */
    tickMsgRetryWait = (unsigned long)(clkRate * MESSAGE_RETRY_WAIT_SEC + 0.5);
    if (tickMsgRetryWait == 0UL) tickMsgRetryWait = 1UL;

    if(!pdbbase) {
	errlogPrintf ("logAlarmsInit: No database has been loaded\n");
        return ERROR;
    }

    serverList = getenv ("EPICS_ALARM_SERVER_INET");
    if (!serverList) {
	errlogPrintf ("logAlarmsInit: EPICS_ALARM_SERVER_INET env not defined\n");
	return ERROR;
    }

    pstring = getenv ("EPICS_ALARM_MESSAGE_PORT");
    if (!pstring)
	msg_port = DEFAULT_MSG_PORT;
    else {
	msg_port = (unsigned short) atoi (pstring);
	if (msg_port < 1024) {
	    msg_port = DEFAULT_MSG_PORT;
	    errlogPrintf ("Port number EPICS_ALARM_MESSAGE_PORT is wrong\n");
	}
    }

    pstring = getenv ("EPICS_ALARM_COMMAND_PORT");
    if (!pstring)
	cmd_port = DEFAULT_CMD_PORT;
    else {
	cmd_port = (unsigned short) atoi (pstring);
	if (cmd_port < 1024) {
	    cmd_port = DEFAULT_CMD_PORT;
	    errlogPrintf ("Port number EPICS_ALARM_COMMAND_PORT is wrong\n");
	}
    }

    /* if allAcceptOption is set, commands are accepted from all servers */
    pstring = getenv ("EPICS_ALARM_COMMAND_ACCEPT_ALL");
    if (strcmp (pstring, "YES") == 0)
	allAcceptOption = TRUE;

    psin = &msgServerAddr[0];
    node = 0;

    ptok = strtok_r (serverList, " ", &pstring);

    while (ptok && node < SERVERS_MAX && aToIPAddr (ptok, msg_port, psin) == 0) {
        node++;
        psin++;
	ptok = strtok_r (NULL, " ", &pstring);
    }
    numServersConfigured = node;

    if (numServersConfigured == 0) {
	errlogPrintf (
	  "logAlarmsInit: No server correctly defined in EPICS_ALARM_SERVER_INET\n");
    }

    wakeupSem = semBCreate (SEM_Q_FIFO, SEM_EMPTY);
    if (!wakeupSem) {
	errlogPrintf ("logAlarmsInit: Reader wakeup semaphore could not be created\n");
        return ERROR;
    }

    bucketSem = semMCreate (SEM_Q_PRIORITY | SEM_DELETE_SAFE | SEM_INVERSION_SAFE);
    if (!bucketSem) {
	errlogPrintf ("logAlarmsInit: Hash facility mutex could not be created\n");
	cleanup (1);
        return ERROR;
    }

    ringSem = semMCreate (SEM_Q_PRIORITY | SEM_DELETE_SAFE | SEM_INVERSION_SAFE);
    if (!ringSem) {
	errlogPrintf ("logAlarmsInit: Ring r/w mutex could not be created\n");
	cleanup (2);
        return ERROR;
    }

    ringWriteSem = semMCreate (SEM_Q_PRIORITY | SEM_DELETE_SAFE | SEM_INVERSION_SAFE);
    if (!ringWriteSem) {
	errlogPrintf ("logAlarmsInit: Ring (write) mutex could not be created\n");
	cleanup (3);
        return ERROR;
    }

    msgSockSem = semMCreate (SEM_Q_PRIORITY | SEM_DELETE_SAFE | SEM_INVERSION_SAFE);
    if (!msgSockSem) {
	errlogPrintf ("logAlarmsInit: MsgSocket mutex could not be created\n");
	cleanup (4);
        return ERROR;
    }

    dbInitEntry(pdbbase,pdbentry);

    status = dbFirstRecordType(pdbentry);
    while(!status) {
	int	numRecordsType;

	numRecordsType = dbGetNRecords(pdbentry);
	DEBUG2(4,"There are %d records of type %s\n",
	  numRecordsType, dbGetRecordTypeName(pdbentry))
	numRecords += numRecordsType;
	status = dbNextRecordType(pdbentry);
    }
    dbFinishEntry(pdbentry);

    normalRangeSize = (int)(numRecords * RING_NORMAL_MULTI) + 1;
    overRangeSize = numRecords + numRecords / RING_OVER_DIVI + 1;

    ringSize = normalRangeSize + overRangeSize;

    pRingBottom = (msgBuffer_t *)calloc (ringSize, sizeof(msgBuffer_t));
    if (!pRingBottom) {
	errlogPrintf ("logAlarmsInit: Ring buffer could not be created\n");
	cleanup (5);
	return ERROR;
    }
    pRingTop = pRingBottom + ringSize;
    DEBUG2(2,"pRingBottom:%lu  pRingTop:%lu\n",
      (unsigned long)pRingBottom, (unsigned long)pRingTop)

    ringHighWater = normalRangeSize;
    DEBUG2(2,"Ring buffer size:%d  highwater:%d\n",ringSize,ringHighWater)
    pRingRead = pRingBottom;
    pRingWrite = pRingBottom;
    msgLost = 0;

    pbuck = bucketCreate (numRecords);
    if (!pbuck) {
	errlogPrintf ("logAlarmsInit: Hash table could not be initalized\n");
	cleanup (6);
	return ERROR;
    }

    serverSelected = -1;
    serverSelectedLast = -1;

    queueAllRecords();

    /* spawn alarm beacon task */
    btid = taskSpawn ("Al'Beacon", BEACON_TASK_PRIO, BEACON_TASK_OPTIONS,
      BEACON_TASK_STACK, (FUNCPTR)alarmBeaconTask,0,0,0,0,0,0,0,0,0,0);

    if (!btid) {
	errlogPrintf ("logAlarmsInit: Beacon task could not be spawned\n");
	cleanup (7);
	return ERROR;
    }
    DEBUG1(1,"alarmBeaconTask started. Task-ID = 0x%x\n", btid);

    msg_soc = socket (AF_INET, SOCK_DGRAM, 0);
    if (msg_soc < 0) {
	errlogPrintf ("Message socket create failed\n");
	cleanup (7);
	return ERROR;
    }
    bzero ((char *) &msgReplyInAddr, sockAddrSize);
    msgReplyInAddr.sin_port = htons(msg_port);
    msgReplyInAddr.sin_family = AF_INET;
    msgReplyInAddr.sin_addr.s_addr = htonl(INADDR_ANY);
    if (bind (msg_soc, (struct sockaddr*)&msgReplyInAddr, sockAddrSize) < 0 ) {
	errlogPrintf ("Message socket bind failed\n");
	cleanup (8);
	return ERROR;
    }

    cmd_soc = socket (AF_INET, SOCK_DGRAM, 0);
    if (cmd_soc < 0) {
	errlogPrintf ("Command socket create failed\n");
	cleanup (8);
	return ERROR;
    }
    bzero ((char *) &cmdServerAddr, sockAddrSize);
    cmdServerAddr.sin_port = htons(cmd_port);
    cmdServerAddr.sin_family = AF_INET;
    cmdServerAddr.sin_addr.s_addr = htonl(INADDR_ANY);
    if (bind (cmd_soc, (struct sockaddr*)&cmdServerAddr, sockAddrSize) < 0 ) {
	errlogPrintf ("Command socket bind failed\n");
	cleanup (9);
	return ERROR;
    }

    /* spawn alarm message tasks */
    for (i=0; i<MSG_TASK_NUM; i++) {
	sprintf (string, "Al'Msg%d", i+1);
	tid[i] = taskSpawn (string, CLIENT_TASK_PRIO, CLIENT_TASK_OPTIONS,
	 CLIENT_TASK_STACK, (FUNCPTR)alarmMessageTask,i+1,0,0,0,0,0,0,0,0,0);

	if (!tid[i]) {
	    errlogPrintf (
	     "logAlarmsInit: Message task %d could not be spawned\n");
	    cleanup (9);
	    while (i > 0) taskDelete (tid[--i]);
	    taskDelete (btid);
	    return ERROR;
	}
    }

    /* spawn alarm log control (command receiver) server task */
    ctid = taskSpawn ("Al'Command", CONTROL_TASK_PRIO, CONTROL_TASK_OPTIONS,
      CONTROL_TASK_STACK, (FUNCPTR)alarmCommandTask,0,0,0,0,0,0,0,0,0,0);

    if (!ctid) {
	errlogPrintf ("logAlarmsInit: Control task could not be spawned\n");
	cleanup (9);
	taskDelete (btid);
	for (i=0; i<MSG_TASK_NUM; i++) taskDelete (tid[i]);
	return ERROR;
    }

    /* spawn message reply receiver task */
    rtid = taskSpawn ("Al'ReplyRx", REPLY_TASK_PRIO, REPLY_TASK_OPTIONS,
      REPLY_TASK_STACK, (FUNCPTR)replyReceiveTask,0,0,0,0,0,0,0,0,0,0);

    if (!rtid) {
	errlogPrintf ("logAlarmsInit: Reply receiver task could not be spawned\n");
	cleanup (9);
	taskDelete (btid);
	taskDelete (ctid);
	for (i=0; i<MSG_TASK_NUM; i++) taskDelete (tid[i]);
	return ERROR;
    }

    pheader = messageHeader;

    pstring = getenv ("EPICS_IOC_NAME");

    if (pstring)		/* given IOC name */
	pheader = messageHeader +
	  sprintf (messageHeader, "HOST=%s;", pstring);
    pheader +=
      sprintf (pheader, "HOST-PHYS=%s;APPLICATION=logAlarms;",
        gethostname (string, sizeof(string)) ? "TOO_LONG" : string);

    pstring = getenv ("EPICS_FACILITY");

    if (pstring)		/* name of application facility */
	pheader +=
	  sprintf (pheader, "FACILITY=%s;", pstring);

    /* calculate the remaining space in the message string */
    textMsgSize = MAX_MESSAGE_LENGTH - (pheader-messageHeader);

    epicsPrintf("logAlarms started\n");

    recGblAlarmHook = (RECGBL_ALARM_HOOK_ROUTINE)logEvent;
    DEBUG1(3,"recGblAlarmHook = 0x%lx\n",(unsigned long)recGblAlarmHook)

    initialized = TRUE;

    return OK;
}
/*******************************************************************************
Routine:
    queueRecord

Purpose:
    Put status message for one record into the ring buffer

Description:
    Use dbFindRecord to find the record and place the alarm status
    into the ring buffer.
*/

LOCAL int queueRecord (const char *precName)
{
    DBENTRY	dbentry;
    DBENTRY	*pdbentry=&dbentry;
    dbCommon	*pdbc;
    msg_t	msg;

    dbInitEntry (pdbbase, pdbentry);
    if (dbFindRecord (pdbentry, precName) != 0) {
	dbFinishEntry (pdbentry);
	return ERROR;
    }
    pdbc = dbentry.precnode->precord;
    dbFinishEntry (pdbentry);

    DEBUG2(4,"queueRecord: Name: %s ptr: 0x%x\n",precName,(unsigned)pdbc)
    msg.pdbc = pdbc;
    msg.osevr = pdbc->sevr;
    msg.ostat = pdbc->stat;
    msg.nsevr = pdbc->sevr;
    msg.nstat = pdbc->stat;
    tsLocalTime (&msg.stamp);
    msg.msgType = mt_Status;

    ringInsert (&msg);

    return OK;
}

/*******************************************************************************
Routine:
    queueAllRecords

Purpose:
    Put status messages of all records into the ring buffer

Description:
    Go through the database and create status messages for all records of all
    record types. Status messages are normally queued in the ring buffer. They
    do not overwrite event messages.

*/

LOCAL void queueAllRecords ()
{
    DBENTRY     dbentry;
    DBENTRY     *pdbentry=&dbentry;
    dbCommon	*pdbc;
    long        status;

    dbInitEntry(pdbbase,pdbentry);
    status = dbFirstRecordType(pdbentry);
    while(!status) {
	status = dbFirstRecord(pdbentry);
	while(!status) {
	    msg_t msg;

	    pdbc = pdbentry->precnode->precord;
	    DEBUG2(4,"queueAllRecords:Name: %s ptr: 0x%x\n",
	      dbGetRecordName(pdbentry),(unsigned)pdbc)
	    msg.pdbc = pdbc;
	    msg.osevr = pdbc->sevr;
	    msg.ostat = pdbc->stat;
	    msg.nsevr = pdbc->sevr;
	    msg.nstat = pdbc->stat;
	    tsLocalTime (&msg.stamp);
	    msg.msgType = mt_Status;

	    ringInsert (&msg);

	    status = dbNextRecord(pdbentry);
	}
	status = dbNextRecordType(pdbentry);
    }
    dbFinishEntry(pdbentry);
}


/*******************************************************************************
Routine:
    logAlarmsReport

Purpose:
    Report status of alarm logging client

Description:

Returns:
    OK

*/

int epicsShareAPI logAlarmsReport (
    int interest
) {
    char        inetAddr[32];
    int		serverId;

    if (!initialized) {
	printf ("logAlarms utility is not initialized! Use logAlarmsInit.\n");
	return OK;
    }

    printf ("logAlarmsReport! use level parameter of [0..4]\n");
    printf ("\n%d servers are configured\n", numServersConfigured);
    printf ("%d messages have been lost\n", msgLost);

    if (serverSelected >= 0)
	printf ("\nThe server number %d is selected\n", serverSelected);
    else
	printf ("\nNo server is selected\n");

    if (interest > 0 && numServersConfigured > 0) {
	if (numServersConfigured > 0)
	printf ("\nConfigured Servers\n");
	for (serverId=0; serverId < numServersConfigured; serverId++) {
	    ipAddrToA (&msgServerAddr[serverId], inetAddr, 32);
	    printf ("Server %d: %s ", serverId, inetAddr);
	    if (serverId == serverSelected) printf ("selected + ");
	    if (serverStatus[serverId].up)
		printf ("online\n");
	    else
		printf ("offline\n");
	}
	printf ("\nMessage port: %d  Command port: %d\n", msg_port, cmd_port);
    }

    if (interest > 1) {
	printf ("\nRing buffer size is %d messages\n", ringSize);
	printf ("Ring base area is %d messages, extention is %d\n",
	  ringHighWater, ringSize - ringHighWater);
    }

    printf ("\nRing buffer usage: %ld messages are waiting in the ring buffer\n",
      (long)RINGPOS(pRingWrite));

    if (interest > 3) {
	printf ("\nRing address is 0x%lx\n", (unsigned long)pRingBottom);
	printf ("Read pointer: 0x%lx  Write pointer: 0x%lx\n",
	  (unsigned long)pRingRead, (unsigned long)pRingWrite);
	printf ("\nTiming: Initial wait time: %ld ticks\n", tickInitWait);
	printf ("        Max time between beacons: %ld ticks\n", tickMaxSilent);
	printf ("        Timeout waiting for message replies: %ld ticks\n", tickMessageReplyTmo);
	printf ("        Timeout waiting for beacon replies: %ld ticks\n", tickBeaconReplyTmo);
    }

    if (interest > 2) {
	printf ("\nThe hash facility to find messages in the ring buffer:\n");
	if (pbuck)
	    bucketShow (pbuck);
	else
	    printf ("Hash table not created\n");
    }
    return OK;
}


/*******************************************************************************

Routine:
    logEvent

Purpose:
    Log a new alarm event

Description:
    This funktion is called by the record support. It tries to place the
    alarm event into the alarm ring buffer. If not possible, it writes the event
    into an ordered list.

Returns:
    void   

Example:
    logEvent (precord, stat, sevr);

*/

LOCAL int logEvent (
    dbCommon *pdbc,
    unsigned short ostat,
    unsigned short osevr
) {
    msg_t msg;
    int status;

    msg.pdbc = pdbc;
    msg.osevr = osevr;
    msg.ostat = ostat;
    msg.nsevr = pdbc->sevr;
    msg.nstat = pdbc->stat;
    msg.stamp = pdbc->time;
    msg.msgType = mt_Event;

    status = ringInsert (&msg);
    DEBUG1(3,"logEvent: ringInsert returned %d\n",status)

    return status;
}

/*******************************************************************************

Routine:
    alarmMessageTask

Purpose:
    Task sending messages to the alarm servers

Description:
    When a message is in the ring buffer, it sends it to the alarm server
    over the network.
    When the IOC has just started, the task waits some time to allow the
    records to get out of udf-alarm. Status message in the ring buffer will
    be overwritten by the udf-to-normal-events during this time.

*/

LOCAL int alarmMessageTask (int taskIndex)
{
    msgBuffer_t		msgBuffer;
    char		messageText[MAX_MESSAGE_LENGTH+1];
    char		*pmessage;
    short		messageSize;
    taskPrivate_t	private;

    strcpy (messageText, messageHeader);
    pmessage = messageText + strlen (messageText);
    messageSize = MAX_MESSAGE_LENGTH - strlen (messageHeader);

    /* create mutex for private manipulation */
    private.privateSem = semMCreate (SEM_Q_PRIORITY |
     SEM_DELETE_SAFE | SEM_INVERSION_SAFE);

    /* create semaphore for reply */
    private.replySem = semBCreate (0, SEM_EMPTY);

    private.serverId = -1;
    taskPrivateTab[taskIndex] = &private;
    
    taskDelay (tickInitWait);

    while (1) {
        unsigned long   myMsgId;
	int nmsg;

	semTake (wakeupSem, WAIT_FOREVER);
	nmsg = ringRead (&msgBuffer);
	if (nmsg > 0) {	/* msg to send */

	    if (nmsg > 1)		/* more messages to send? */
		semGive (wakeupSem);	/* wake up other msgTasks */

	    /* generate text message from message structure */
	    myMsgId = getMessageId();
	    DEBUG2(5,"msgTask %d sending Message %ld\n",taskIndex,myMsgId);

	    if (msgToText (pmessage, messageSize, &msgBuffer, myMsgId) == ERROR) {
		if (messageSize >= 40)
		    sprintf (pmessage, "TYPE=sysMsg;TEXT=Message create error;");
		else {
		    errlogPrintf (
		      "Message task %d terminated. Message buffer too small\n",
		      taskIndex);
		    return ERROR;
		}
	    }

	    while (1) {		/* send message again until success */
		volatile int serverSendTo;

		serverSendTo = serverSelected;	/* local copy */

		if (serverSendTo >= 0) {
		    if (sendMessageToServer (
		      messageText, serverSendTo, myMsgId, &private) == OK) {
			serverStatus[serverSendTo].tickMsg = tickGet();
			break; /* message sent! */
		    }
		    /* error in transmission, retry after short delay */
		    taskDelay (tickMsgRetryWait);
		}
		else {
		    /* no server is selected, wait until next beacon period */
		    taskDelay (tickBeaconPeriod);
		}
	    }
	    /* message sent */

	} /* while messages in ringbuffer */
    } /* forever */
	

    return OK; /* never reached - hopefully */
}

/*******************************************************************************

Routine:
    alarmBeaconTask

Purpose:
    Task sends beacons to the alarm servers and manage server states.

Description:
    Check if sending beacons is necesary. If the server is offline, beacons
    are sent every time. If the server is online beacons are send only
    if for longer than "tickMaxSilent" ticks no message is sent to a
    server.

*/

LOCAL int alarmBeaconTask (void)
{
    unsigned long	tickStart, tickDiff;
    int			serverId, retries;
    volatile int	ix;
    taskPrivate_t       private;

    /* create mutex for private manipulation */
    private.privateSem = semMCreate (SEM_Q_PRIORITY |
     SEM_DELETE_SAFE | SEM_INVERSION_SAFE);
    /* create semaphore for reply */
    private.replySem = semBCreate (0, SEM_EMPTY);
    private.serverId = -1;
    taskPrivateTab[0] = &private;

    while (1) {
	tickStart = tickGet();
	/* first check all servers and notify, which are running */
	for (serverId=0; serverId < numServersConfigured; serverId++) {
	    if (serverStatus[serverId].up) {
		if (tickGet() - serverStatus[serverId].tickMsg > tickMaxSilent) {
		    beaconId++;
		    for (retries=0; retries < RETRY_MAX; retries++) {
			if (sendBeaconToServer (serverId, beaconId, &private) == OK) {
			    serverStatus[serverId].tickMsg = tickGet();
			    break;
			}
		    }
		    if (retries == RETRY_MAX)
			serverStatus[serverId].up = FALSE;
		}
	    }
	    else {
		if (sendBeaconToServer (serverId, ++beaconId, &private) == OK) {
		    serverStatus[serverId].tickMsg = tickGet();
		    serverStatus[serverId].up = TRUE; 
		}
	    }
	}
	/* then select a new one if necessary */
	if ((ix = serverSelected) < 0 || !serverStatus[ix].up) {
	    /* no server is active or our selected one went down */
	    int index;

	    serverSelected = -1;
	    for (index = serverSelectedLast + 1;
	     index <= (serverSelectedLast + numServersConfigured);
	     index++) {
		serverId = index % numServersConfigured;
		if (serverStatus[serverId].up) {
		    serverSelected = serverId;	/* we found a new one */
		    break;
		}
	    }
	    serverSelectedLast = serverSelected;
	}
	tickDiff = tickGet() - tickStart;
	if (tickBeaconPeriod > tickDiff)
	    taskDelay (tickBeaconPeriod - tickDiff);
	else
	    taskDelay (clkRate/10);
    }
    return OK;
}
    
/*******************************************************************************

Routine:
    replyReceiveTask

Purpose:
    Task receiving replies for messages and beacons from the alarm servers

Description:
    When a reply arrives on the message port, it looks into a distribution
    table and copies the result to a buffer. Finally it releases a sema-
    phor to set the waiting message- or beacon task into ready state.

*/

/******************************************************************************/

LOCAL int replyReceiveTask ()
{
    char		rx_buf[RX_BUF_SIZE+1];
    int			mlen, flen;
    char		*pstring = NULL, *ptok, *pvalue;
    BOOL		idExist;
    int			replyStatus;
    unsigned long 	replyId;
    taskPrivate_t	*pPvt;
    struct sockaddr_in	reply_addr;
    int			ti;

    DEBUG1(1,"replyReceiveTask entry; msg_sock=%d\n",msg_soc);
    while (1) {
	flen = sockAddrSize;
	mlen = recvfrom (msg_soc, rx_buf, RX_BUF_SIZE, 0,
	  (struct sockaddr *)&reply_addr, &flen);
	if (mlen < 0) {
	    DEBUG0(2,"recvfrom failed - cheksum error?\n");
	    continue;
	}
	DEBUG1(3,"received %d chars\n",mlen);
	rx_buf[mlen] = '\0';

	/* we got something from somewhere on our port */

	/* now analyze the reply from the server */
	idExist = FALSE;
	replyStatus = ERROR;
	replyId = 0;

	ptok = strtok_r (rx_buf, "=", &pstring);
	while (ptok) {
	    char *pnext;
	    name_t i_name;

	    pvalue = strtok_r (NULL, ";", &pstring);

	    if (iName (ptok, &i_name) == OK) {
		switch (i_name) {
		    case name_SERIAL:
			replyId = strtoul (pvalue, &pnext, 10);
			idExist = (pvalue != pnext);
			break;
		    case name_STATUS:
			if (strcmp (pvalue, "Ok") == 0)
			    replyStatus = OK;
			break;
		/*  case ....break; */
		    default:
			DEBUG2(1,"Unexpected item in reply message %s=%s\n",
			 ptok, pvalue)
		}
	    }
	    else
		DEBUG2(1,"Undefined item in reply message %s=%s\n",
		 ptok, pvalue)

	    ptok = strtok_r (NULL, "=", &pstring);	/* next name-value-pair */
	}

	/* test serial number */
	if (!idExist) {
	    DEBUG0(1,"Serial number of server reply missing\n")
	    replyId = 0;
	    continue;
	}

	DEBUG2(3,"received reply: serial=%ld status=%d\n",replyId,replyStatus);

	for (ti=0; ti <= MSG_TASK_NUM; ti++) {
	    pPvt = taskPrivateTab[ti];

	    if (!pPvt) continue;

	    semTake (pPvt->privateSem, WAIT_FOREVER);

	    /* test if correct server answered and ID is what we wait for */
	    if ( (pPvt->serverId >= 0) && (reply_addr.sin_addr.s_addr ==
	      msgServerAddr[pPvt->serverId].sin_addr.s_addr) &&
	     (replyId == pPvt->id) ) {

		DEBUG3(5,"return status %d to task %d id=%ld\n",
		 replyStatus,ti,pPvt->id);
		pPvt->replyStatus = replyStatus;
		semGive (pPvt->replySem);	/* trigger msg or beacon task */
		semGive (pPvt->privateSem);	/* release private access */
		break;	/* dont try other, waiting task found */
	    }
	    semGive (pPvt->privateSem);		/* release private access */
	}

    } /* forever */

    return OK; /* never reached */
}

/*******************************************************************************

Routine:
    alarmCommandTask

Purpose:
    Task waits for commands from servers.

Description:
    If a command arrives on the command port, it is executed. The command
    "TakeOver" will switch the active server to that one the command has sent.
    Any messages from nodes which are not configured are ignored. Commands
    from servers which are not selected are refused except the TAKEOVER command.
*/

LOCAL int alarmCommandTask (void)
{
    int			serverIdRecvd, ix;
    int			mlen, flen;
    struct sockaddr_in	cmdRecvdAddr;
    char		cmd_buf[RX_BUF_SIZE+1];
    char		*pstring, *ptok;
    char		*pname, *pvalue=NULL, *pstatusString=NULL;
    char		replyString[64];
    BOOL		idExist, cmdExist;
    unsigned long	id=0;
    cmd_t		cmdId;
    int			cmdStatus=ERROR;


    while (1) {
	
	flen = sockAddrSize;
	mlen = recvfrom (cmd_soc, cmd_buf, RX_BUF_SIZE, 0,
	  (struct sockaddr *)&cmdRecvdAddr, &flen);
	if (mlen < 0) {
	    errlogPrintf ("recvfrom failed - cheksum error?\n");
	    continue;
	}
	cmd_buf[mlen] = '\0';

	/* we got something from somewhere on our port */

	/* now analyze the string from the server */

	idExist = FALSE;
	cmdExist = FALSE;
	pname = NULL;
	ptok = strtok_r (cmd_buf, "=", &pstring);
	while (ptok) {
	    name_t i_name;

	    pvalue = strtok_r (NULL, ";", &pstring);
	    if (iName (ptok, &i_name) == OK) {
		char *pnext;

		switch (i_name) {
		    case name_SERIAL:
			id = strtoul (pvalue, &pnext, 10);
			idExist = (pvalue != pnext);
			break;
		    case name_COMMAND:
			cmdExist = TRUE;
			cmdStatus = iCmd (pvalue, &cmdId);
			break;
		    case name_NAME:
			pname = pvalue;
			if (!*pname) pname = NULL;
			break;
		/*  case ....break; */
		    default:
			DEBUG2(1,"Unexpected item for ctlTask %s=%s\n",
			  ptok, pvalue)
		}
	    }
	    else
		DEBUG2(1,"Undefined item in control message %s=%s\n",
		  ptok, pvalue)

	    ptok = strtok_r (NULL, "=", &pstring);	/* next name-value-pair */
	}

	/* did this command message arrive from our selected server? */
	serverIdRecvd = -1;
	if ((ix = serverSelected) > 0 &&
	  cmdRecvdAddr.sin_addr.s_addr == msgServerAddr[ix].sin_addr.s_addr)
	    serverIdRecvd = serverSelected;
	else {
	    int serverId;

	    for (serverId=0; serverId < numServersConfigured; serverId++) {
		if (cmdRecvdAddr.sin_addr.s_addr == msgServerAddr[serverId].sin_addr.s_addr) {
		    serverIdRecvd = serverId;
		    break;
		}
	    }
	    if (serverIdRecvd < 0) {
#ifdef DEBUG
		if (logAlarmsDebug) {
		    char	inetAddr[32];

		    ipAddrToA (&cmdRecvdAddr, inetAddr, 32);
		    DEBUG1(1,"Message from unknown sender %s\n", inetAddr)
		}
#endif
		continue;	/* ignore this message */
	    }
	}


	if (cmdExist) {
	  if (cmdStatus == OK) {
	    switch (cmdId) {
		case cmd_TAKEOVER:
		    if (serverIdRecvd != serverSelected) {
			serverSelected = serverIdRecvd;
			pstatusString = "done;";
		    }
		    else
			pstatusString = "ok;";
		    break;
		case cmd_DISCONNECT:
		    if (serverIdRecvd == serverSelected) {
			serverSelected = -1;
			pstatusString = "done;";
		    }
		    else
			pstatusString = "ok;";
		    break;
		case cmd_SENDALLALARMS:
		    if (allAcceptOption || serverIdRecvd == serverSelected) {
			queueAllRecords();
			pstatusString = "done;";
		    }
		    else
			pstatusString = "refused;";
		    break;
		case cmd_SENDALARM:
		    if (allAcceptOption || serverIdRecvd == serverSelected) {
			if (pname && queueRecord (pname) == OK)
			    pstatusString = "done;";
			else
			    pstatusString = "error;";
		    }
		    else
			pstatusString = "refused;";
		    break;
		case cmd_SENDSTATUS:
		    pstatusString = (serverIdRecvd == serverSelected) ?
		      "selected;" : "notSelected;";
		    break;
		default:
		    DEBUG1(1,"Unexpected Command: %s\n", pvalue)
	    }
	  }
	  else {
	      DEBUG1(1,"Not a Command: %s\n", pvalue)
	      pstatusString = "cmdUnknown;";
	  }
	}
	else
	    pstatusString = "cmdMissing;";

	replyString[0] = '\0';

	if (idExist)
	    sprintf (replyString, "ID=%ld;", id);
	strcat (replyString, "REPLY=");
	strcat (replyString, pstatusString);

	/* send a reply to the server we got the message from */
	sendto (cmd_soc, replyString, strlen(replyString)+1, 0,
	  (struct sockaddr*)&cmdRecvdAddr, sockAddrSize);
    }
    return OK;
}

/******************************************************************************/

LOCAL int iName (const char *text, name_t *index)
{
    int i;

    static struct {name_t index; const char *text;} nameTab[] = {
	{ name_SERIAL, "ID" },
	{ name_COMMAND, "COMMAND" },
	{ name_NAME, "NAME" },
	{ name_STATUS, "STATUS" }
    };

    for (i=0; i<4; i++) {
	if (strcmp (nameTab[i].text, text) == 0) {
	    *index = nameTab[i].index;
	    return OK;
	}
    }
    return ERROR;
}

LOCAL int iCmd (const char *text, name_t *index)
{
    int i;

    static struct {cmd_t index; const char *text;} cmdTab[] = {
	{ cmd_TAKEOVER, "takeOver" },
	{ cmd_DISCONNECT, "disconnect" },
	{ cmd_SENDALLALARMS, "sendAllAlarms" },
	{ cmd_SENDALARM, "sendAlarm" },
	{ cmd_SENDSTATUS, "sendStatus" }
    };

    for (i=0; i<5; i++) {
	if (strcmp (cmdTab[i].text, text) == 0) {
	    *index = cmdTab[i].index;
	    return OK;
	}
    }
    return ERROR;
}


/******************************************************************************/

LOCAL int sendMessageToServer (const char *ptext, int serverId,
unsigned long messageId, taskPrivate_t *pPvt)
{

    semTake (pPvt->privateSem, WAIT_FOREVER);

    pPvt->id = messageId;
    pPvt->serverId = serverId;
    semTake (pPvt->replySem, NO_WAIT);

    semGive (pPvt->privateSem);


    /* send to network */
    LOCK_MSG_SOCK

    if (sendto (msg_soc, (char*)ptext, strlen(ptext)+1, 0,
      (struct sockaddr*)&msgServerAddr[serverId], sockAddrSize) < 0) {
	UNLOCK_MSG_SOCK
	DEBUG2(1,"Send message %ld to server %d: sendto failed\n",
	 messageId, serverId);
	pPvt->serverId = -1;
	return ERROR;
    }
    UNLOCK_MSG_SOCK

    DEBUG1(5,"msg %ld sent, now wait...\n",pPvt->id);

    if (semTake (pPvt->replySem, tickMessageReplyTmo) == ERROR) {
	DEBUG1(2,"Message to server %d timed out\n", serverId);
	pPvt->serverId = -1;
	return ERROR;
    }

    pPvt->serverId = -1;
    return pPvt->replyStatus;
}

/******************************************************************************/

LOCAL int sendBeaconToServer (int serverId, unsigned long beaconId, taskPrivate_t *pPvt)
{
    char	message[32];

    sprintf(message, "TYPE=beacon;ID=%ld;", beaconId);

    semTake (pPvt->privateSem, WAIT_FOREVER);
    pPvt->id = beaconId;
    pPvt->serverId = serverId;
    semTake (pPvt->replySem, NO_WAIT);
    semGive (pPvt->privateSem);

    /* send to network */
    LOCK_MSG_SOCK
    if (sendto (msg_soc, (char*)message, strlen(message)+1, 0,
      (struct sockaddr*)&msgServerAddr[serverId], sockAddrSize) < 0) {
	UNLOCK_MSG_SOCK
	DEBUG1(1,"Send beacon to server %d: sendto failed\n", serverId);
	pPvt->serverId = -1;
	return ERROR;
    }
    UNLOCK_MSG_SOCK

    if (semTake (pPvt->replySem, tickBeaconReplyTmo) == ERROR) {
	DEBUG1(2,"Beacon to server %d timed out\n", serverId);
	pPvt->serverId = -1;
	return ERROR;
    }

    pPvt->serverId = -1;
    return pPvt->replyStatus;
}
	
/******************************************************************************/

LOCAL int msgToText (char *ptext, short size, msgBuffer_t *pmsgBuf, unsigned long Id)
{
    msg_t	*pmsg = &pmsgBuf->msg;
    int		nchar;
    char	textBuffer[32];

    if (15 > size) return ERROR;
    nchar = sprintf(ptext, "ID=%ld;", Id);

    if (nchar + 25 > size) return ERROR;
    stampToText (&pmsg->stamp, textBuffer);
    switch (pmsg->msgType) {
	case mt_Event:
	    nchar += sprintf(ptext+nchar, "TYPE=event;");
	    nchar += sprintf(ptext+nchar, "EVENTTIME=%s;", textBuffer);
	    break;
	case mt_Status:
	    nchar += sprintf(ptext+nchar, "TYPE=status;");
	    nchar += sprintf(ptext+nchar, "CREATETIME=%s;", textBuffer);
	    break;
	default:
	    nchar += sprintf(ptext+nchar, "TYPE=unknown;");
	    nchar += sprintf(ptext+nchar, "CREATETIME=%s;", textBuffer);
    }

    if (nchar + 35 > size) return ERROR;
    nchar += sprintf(ptext+nchar, "NAME=%.29s;",pmsg->pdbc->name);

    if (pmsg->msgType == mt_Event) {
	if (nchar + 44 > size) return ERROR;
	nchar += sprintf(ptext+nchar, "SEVERITY-OLD=%.8s;STATUS-OLD=%.8s;",
	  alarmSeverityString[pmsg->osevr],
	  alarmStatusString[pmsg->ostat]);
    }
    if (nchar + 34 > size) return ERROR;
    nchar += sprintf(ptext+nchar, "SEVERITY=%.8s;STATUS=%.8s;",
      alarmSeverityString[pmsg->nsevr],
      alarmStatusString[pmsg->nstat]);
    if (pmsgBuf->overwritten) {
	if (nchar + 41 > size) return ERROR;
	nchar += sprintf(ptext+nchar, "OVERWRITES=%d;SEVERITY-MAX=%.8s;",
	  pmsgBuf->overwritten,
	  alarmSeverityString[pmsgBuf->msevr]);
    }
    if (msgLost) {
	if (nchar + 17 > size) return ERROR;
	LOCK_RING_W
	nchar += sprintf(ptext+nchar, "LOST=%d;", msgLost);
	msgLost = 0;
	UNLOCK_RING_W
    }

    return nchar;
}

/*******************************************************************************
Routine:
    ringInsert

Purpose:
    Log a new alarm event

Description:
    This funktion is called by logEvent and logStatus. It tries to
    place the alarm event into the alarm ring buffer. This should
    work always if the ring buffer has at least as many places above
    its highwater mark as records in the database.
    If nevertheless insertion fails, a counter is incremented counting
    how many entries are lost. When the ring buffer is filled over
    its highwater mark, the message can overwrite an existing message
    of the same record in the 'upper' part of the ring. This happens
    if the records message is already in this part. Status messages
    in the ring can be overwritten wherever they are. Otherwise the
    new message is normally written to the next free position.
    To find a record in the ring a hash table is used.
    Locking: Writing to a new ring position can be done simultanously
    while reading but not while a second writer is active. If over-
    writing is nessecary it must be locked against reading as well.
    Third lock mechanism is needed for the bucket (hash) facility.

Returns:
    OK or ERROR if the ring buffer overflows.   

Example:
    ringInsert (&myNewMsg);

*/

LOCAL int ringInsert (msg_t *pmsg)
{
    msgBuffer_t *prng;

    LOCK_RING_W
    LOCK_RING
    LOCK_BUCK
    prng = (msgBuffer_t *)bucketLookupItemPointerId
      (pbuck, (void * const *)&pmsg->pdbc);
    UNLOCK_BUCK

    if (prng && prng->hashStatus == hs_Active) {/* hash entry exist	*/
	prng->msg = *pmsg;			/* -> overwrite!	*/
	if (pmsg->nsevr > prng->msevr)
	    prng->msevr = pmsg->nsevr;

        prng->overwritten++;

	/* if event and low ring position do not overwrite in future */
	if (prng->msg.msgType == mt_Event && RINGPOS(prng) <= ringHighWater)
	    prng->hashStatus = hs_Passive;

	UNLOCK_RING
    }
    else {					/* hash entry doesn't exist */
	msgBuffer_t *pnext;			/* ... or not active	*/

	UNLOCK_RING		/* we are not accessing the read position */
	pnext = pRingWrite + 1;			/* prepare next write pos */
	if (pnext >= pRingTop)
	    pnext = pRingBottom;
	if (pnext == pRingRead) {		/* do not write to read pos */
	    msgLost++;				/* msg not placed into ring! */
	    UNLOCK_RING_W
	    return ERROR;
	}
	pRingWrite->msg = *pmsg;		/* write message into ring */
	pRingWrite->msevr = pmsg->nsevr;
	pRingWrite->overwritten = 0;

	if (RINGPOS(pRingWrite) > ringHighWater ||
	  pmsg->msgType == mt_Status) {		/* free for overwrite */

	    pRingWrite->hashStatus = hs_Active;
	    if (prng) {
		/* REPLACE HASH TABLE ITEM (bad performance here) */
		/* better: use bucketLookupItemAddressPointerId */
		/* (has to be implemented, easy) */
		/* *pprng = (void *)pRingWrite; */

		LOCK_BUCK
		if (bucketRemoveItemPointerId (pbuck,
		 (void * const *)&prng->msg.pdbc) != 0)
		    errlogPrintf ("logAlarmsRingInsert: Remove hash for %d failed\n",
		     prng->msg.pdbc);

		if (bucketAddItemPointerId (pbuck,
		 (void * const *)&prng->msg.pdbc, (void *)pRingWrite) != 0)
		    errlogPrintf ("logAlarmsRingInsert: Add hash for %d failed\n",
		     prng->msg.pdbc);

		UNLOCK_BUCK
		prng->hashStatus = hs_Non;
	    }
	    else {
		LOCK_BUCK
		if (bucketAddItemPointerId ( pbuck,
		 (void * const *)&pRingWrite->msg.pdbc, (void *)pRingWrite) != 0)
		    errlogPrintf ("logAlarmsRingInsert: Add hash for %d failed\n",
		     pRingWrite->msg.pdbc);
		UNLOCK_BUCK
	    }
	}
	else {
	    pRingWrite->hashStatus = hs_Non;
	    if (prng)
		prng->hashStatus = hs_Passive;
	}

	pRingWrite = pnext;
	semGive (wakeupSem);
    }
    UNLOCK_RING_W
    return OK;
}

LOCAL int ringRead (msgBuffer_t *pbuffer)
{
    msgBuffer_t *pnext;
    msgBuffer_t *prng;
    int 	status;

    if (pRingRead == pRingWrite)		/* ring empty? */
	return 0;				/* ring is empty */

    LOCK_RING
    pnext = pRingRead + 1;			/* next read position */
    if (pnext >= pRingTop) pnext = pRingBottom;

    *pbuffer = *pRingRead;			/* copy msg to buffer */

    if (pRingRead->hashStatus != hs_Non) {	/* need to delete from hash */
	LOCK_BUCK
	if (bucketRemoveItemPointerId (pbuck,
	 (void * const *)&pRingRead->msg.pdbc) != 0)
	    errlogPrintf ("logAlarmsRingRead: Remove hash for %d failed\n",
                     pRingRead->msg.pdbc);

	UNLOCK_BUCK
    }
    pRingRead = pnext;				/* now increment read pointer */

    /* protect event msg in ring from overwriting if they are close to read
       pointer. It doesn't matter if we modify unused memory in the ring  */
    if (RINGPOS(pRingWrite) > ringHighWater) {
	prng = pRingRead + ringHighWater;	/* calculate position to test */
	if (prng >= pRingTop)
	    prng -= ringSize;

	if (prng->hashStatus == hs_Active && prng->msg.msgType == mt_Event)
	    prng->hashStatus = hs_Passive;	/* set element protected */
    }
    status = (pnext != pRingWrite) ? 2 : 1;	/* are there more messages ? */

    UNLOCK_RING
    return status;
}

LOCAL void cleanup (int step)
{
    switch (step) {
    case 9: close (cmd_soc);
    case 8: close (msg_soc);
    case 7: bucketFree (pbuck);
    case 6: free ((void *)pRingBottom);
    case 5: semDelete (msgSockSem);
    case 4: semDelete (ringWriteSem);
    case 3: semDelete (ringSem);
    case 2: semDelete (bucketSem);
    case 1: semDelete (wakeupSem);
    }
}

/***********************************************************************
* NAME	stampToText - convert a time stamp to text JMS conform
*
* DESCRIPTION
*	A EPICS standard time stamp is converted to text.  The text
*	contains the time stamp's representation in the local time zone,
*	taking daylight savings time into account.
*
*	The required size of the caller's text buffer depends on the type
*	of conversion being requested.  The conversion types, buffer sizes,
*	and conversion format is:
*
*	yyyy-mm-dd hh:mm:ss.mmm		mmm=milliseconds
*
* RETURNS
*	pointer to buffer
*
*-*/
LOCAL char * stampToText (TS_STAMP *pStamp, char *textBuffer)
{
    struct	tsDetail t;	/* detailed breakdown of time stamp */

    tsStampToLocal(*pStamp, &t);

    if (textBuffer != NULL) {
	sprintf(textBuffer, "%04d-%02d-%02d %02d:%02d:%02d.%03d",
	  t.year, t.monthNum+1, t.dayMonth+1, t.hours, t.minutes,
	  t.seconds, (pStamp->nsec + 500000)/1000000);
	return textBuffer;
    }
    else
	return NULL;
}
LOCAL unsigned long getMessageId ()
{
    int handle;
    static unsigned long	messageId = 0L;
    volatile unsigned long	value;

    handle = intLock();
    value = ++messageId;
    intUnlock (handle);

    return value;
}
