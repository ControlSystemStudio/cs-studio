
/* 
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron, 
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. 
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED 
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND 
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE 
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, 
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE 
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR 
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. 
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, 
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION, 
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS 
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY 
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */

package org.csstudio.ams;

public interface AmsConstants 
{
	//Distributor Msg Type
//not needed anymore	public final static int DIST_MESSAGE_TYPE_NEW = 1;
//not needed anymore	public final static int DIST_MESSAGE_TYPE_REPLY = 2;
	
//not needed anymore	public final static int DIST_CHAIN_EVENT_WORK = 1;
//not needed anymore	public final static int DIST_CHAIN_EVENT_REPLY = 2;
	
	//used Alarm Message Properties
	//public final static String MSGPROP_TYPE = "TYPE"; 
	public final static String MSGPROP_EVENTTIME = "EVENTTIME"; 
	//public final static String MSGPROP_TEXT = "TEXT"; 
	//public final static String MSGPROP_USER = "USER"; 
	public final static String MSGPROP_HOST = "HOST"; 							//5
	//public final static String MSGPROP_APPLICATIONID = "APPLICATION-ID"; 
	public final static String MSGPROP_PROCESSID = "PROCESS-ID"; 
	public final static String MSGPROP_NAME = "NAME"; 
	//public final static String MSGPROP_CLASS = "CLASS"; 
	//public final static String MSGPROP_DOMAIN = "DOMAIN"; 						//10
	//public final static String MSGPROP_FACILITY = "FACILITY"; 
	//public final static String MSGPROP_LOCATION = "LOCATION"; 
	//public final static String MSGPROP_SEVERITY = "SEVERITY"; 
	//public final static String MSGPROP_STATUS = "STATUS"; 
	//public final static String MSGPROP_VALUE = "VALUE"; 						//15
	//public final static String MSGPROP_DESTINATION = "DESTINATION"; 
	
	public final static String MSGPROP_COMMAND = "COMMAND"; 					// only FMR to Dist, for reload cfg (Sync DB)
	
	// AMS Prefix
	public final static String AMS_PREFIX = "AMS-"; 
	 
	//Additional Alarm Message Properties FilterMgr=>Distributor
	public final static String MSGPROP_FILTERID = AMS_PREFIX + "FILTERID"; 
	
	//Distributor=>Connector
	public final static String MSGPROP_RECEIVERTEXT = AMS_PREFIX + "RECEIVER-TEXT"; 
	public final static String MSGPROP_RECEIVERADDR = AMS_PREFIX + "RECEIVER-ADDR"; 
	public final static String MSGPROP_SUBJECT_USERNAME = AMS_PREFIX + "SUBJECT-USERNAME"; 
	public final static String MSGPROP_TEXTTYPE = AMS_PREFIX + "RECEIVER-TEXTTYPE"; 
    public final static String MSGPROP_GROUP_WAIT_TIME = AMS_PREFIX + "RECEIVER-WAITTIME"; 
    public final static String MSGPROP_EXTENDED_MESSAGE = AMS_PREFIX + "EXTENDED-MESSAGE"; 
	
	//common
	public final static String MSGPROP_MESSAGECHAINID_AND_POS = AMS_PREFIX + "MESSAGECHAIN-ID_AND_POS";// append UserGroup_User.iPos as String with length = MSG_POS_LENGTH_FOR_MSGPROP
	
	//Connector=>Distributor
	public static final int MSG_POS_LENGTH_FOR_MSGPROP = 3;						// 3 -> max. 999 User in UserGroup ; 4 -> max. 9999 User in UserGroup
																				// chars in String of UserGroup_User.iPos, append to MESSAGECHAIN-ID (must >= 1;  3 or 4 is a good value)
	public final static String MSGPROP_CONFIRMCODE = AMS_PREFIX + "CONFIRM-CODE"; 

	public final static String MSGPROP_REPLY_ADRESS = AMS_PREFIX + "REPLY_ADRESS";
	public final static String MSGPROP_REPLY_TYPE = AMS_PREFIX + "REPLY_TYPE";
	public final static String MSG_REPLY_TYPE_SMS = "SMS";
	public final static String MSG_REPLY_TYPE_JMS = "JMS";
	public final static String MSG_REPLY_TYPE_EMAIL = "EMAIL";
	public final static String MSG_REPLY_TYPE_VOICEMAIL = "VMAIL";

	// ADDED BY Markus Moeller, 2007-12-05
	public final static String MSGPROP_CHANGESTAT_ACTION = AMS_PREFIX + "ACTION";
	
	public final static String MSGPROP_CHANGESTAT_GROUPNUM = AMS_PREFIX + "GROUP-NUM";
	public final static String MSGPROP_CHANGESTAT_USERNUM = AMS_PREFIX + "USER-NUM";
	public final static String MSGPROP_CHANGESTAT_STATUS = AMS_PREFIX + "STATUS";
	public final static String MSGPROP_CHANGESTAT_STATUSCODE = AMS_PREFIX + "STATUS-CODE";
	public final static String MSGPROP_CHANGESTAT_REASON = AMS_PREFIX + "REASON";

	//Distributor=>AlarmTopic
	public final static String MSGPROP_REINSERTED = AMS_PREFIX + "REINSERTED"; 
	
	//
	public final static String MSGCODE_OK = "ok";
	
	public final static String MSGCODE_NOT_OK = "not_ok";
	public final static String MSGCODE_NO_GROUP = MSGCODE_NOT_OK + " no_group";
	public final static String MSGCODE_NOT_IN_GROUP = MSGCODE_NOT_OK + " not_in_group";
	public final static String MSGCODE_MIN_USER_REACHED = MSGCODE_NOT_OK + " min_count_of_user_reached";
	public final static String MSGCODE_UNKNOWN_STATUS = MSGCODE_NOT_OK + " unknown_status";
	public final static String MSGCODE_NO_MAIN_SYSTEM = MSGCODE_NOT_OK + " main_system_temporarly_not_available";
	
	
	//database constants
	//FilterActionType.iFilterActionTypeID
	public final static int FILTERACTIONTYPE_SMS = 1; 
	public final static int FILTERACTIONTYPE_SMS_G = 2; 
	public final static int FILTERACTIONTYPE_SMS_GR = 3; 
	public final static int FILTERACTIONTYPE_VM = 4; 
	public final static int FILTERACTIONTYPE_VM_G = 5; 
	public final static int FILTERACTIONTYPE_VM_GR = 6; 
	public final static int FILTERACTIONTYPE_MAIL = 7; 
	public final static int FILTERACTIONTYPE_MAIL_G = 8; 
	public final static int FILTERACTIONTYPE_MAIL_GR = 9;
	public final static int FILTERACTIONTYPE_TO_JMS = 10;
	
	/**
	 * Fallback???
	 */
	public final static int FILTERACTIONTYPE_TOPICDEST = 100;
	//DB-Backup-Suffix
	public final static String DB_BACKUP_SUFFIX = "_Syn";
	//AMS_Flag.cFlagName
	public final static String FLG_BUP = "BupState";
	public final static String FLG_RPL = "ReplicationState";
	//AMS_Flag.sFlagValue
	public final static short FLAGVALUE_RPLCFG_IDLE = 0;
	public final static short FLAGVALUE_RPLCFG_CONF_SYNC = 1;
	public final static short FLAGVALUE_RPLCFG_DIST_SYNC = 2;

//	ORGINAL
	//StartSync (1. aus Command-Topic gelesen)
	// 2. FMR-Stop
	// 3. loesche alle MSG aus Command-Topic
	// 4. ALARM an Dist
	// ~~~~~~~~~~~~~~~~~
	// 4.1 Dist leert Dist-Topic, bis CMD von FMR
	// 4.2 Dist-Stop
	// 4.3 DB-Flag setzen
	// 4.4 Kopieren
	// 4.5 Dist neu starten
	// 4.6 Bestaetigung an FMR
	// ~~~~~~~~~~~~~~~~~
	// 5. Auf Bestaetigung von Dist warten, alle anderen MSG in Command-Topic loeschen,
	//    da jetzt auf aktuellen Stand
	// 6. FMR-Neu starten
	// 7. DB-Flag zuruecksetzen
	//EndSync
//	ORGINAL

	// State 1 = FMR Replicating 
	// 2. FMR-Stop
	// 3. lösche alle MSG aus Command-Topic
	// State 2 = Alarm => Dist gesendet
	// 4. ALARM an Dist
	// ~~~~~~~~~~~~~~~~~
	// liest CMD 
	// State 3 = Dist Replicating
	// 4.2 Dist-Stop Work
	// 4.1 Dist leert Dist-Topic, bis CMD von FMR
	
	// 4.3 DB-Flag setzen
	// 4.4 Kopieren
	// 7. DB-Flag zuruecksetzen

	// State = 4 = Dist Replicating End, Notify FMR
	// 4.6 Bestaetigung an FMR
	// 4.5 Dist neu starten
	// State = 0 = fertig

	public final static short FLAGVALUE_SYNCH_IDLE = 0;
	public final static short FLAGVALUE_SYNCH_FMR_RPL = 1;
	public final static short FLAGVALUE_SYNCH_FMR_TO_DIST_SENDED = 2;
	public final static short FLAGVALUE_SYNCH_DIST_RPL = 3;
	public final static short FLAGVALUE_SYNCH_DIST_NOTIFY_FMR = 4;

	//AMS_User.sPreferredAlarmingTypeRR
	public final static short USERFILTERALARMTYPE_SMS = 1;
	public final static short USERFILTERALARMTYPE_VM = 2;
	public final static short USERFILTERALARMTYPE_MAIL = 3;
	public final static short USERFILTERALARMTYPE_JMS = 4;
	
	//AMS_MessageChain.sChainState
	public final static short MESSAGECHAIN_WORK = 0;
	public final static short MESSAGECHAIN_FAILED = 1;
	public final static short MESSAGECHAIN_REPLIED = 2;
	
	//Command Topic
	public final static String MSGPROP_TCMD_COMMAND = MSGPROP_COMMAND;			// is the same
	public final static String MSGVALUE_TCMD_RELOAD= "AMS_RELOAD_CFG";
	public final static String MSGVALUE_TCMD_RELOAD_CFG_START = MSGVALUE_TCMD_RELOAD + "_START";
	public final static String MSGVALUE_TCMD_RELOAD_CFG_END = MSGVALUE_TCMD_RELOAD + "_END";

	//ExitExceptions
	public static final int EXITERR_GENERALLY  = 0;
	public static final int EXITERR_BUP_UPDATEFLAG_START = 1;
	public static final int EXITERR_BUP_UPDATEFLAG_END = 2;

	public static final int EXITERR_RPL_FMR_START = 3;
	public static final int EXITERR_RPL_FMR_TO_DIST_SENDED = 4;
	public static final int EXITERR_RPL_DIST_START = 5;
	public static final int EXITERR_RPL_DIST_NOTIFY_FMR = 6;
	public static final int EXITERR_RPL_DIST_IDLE = 7;
	
	
	//AMS_FilterCond_TimeBasedItems.sState
	public static final short STATE_WAITING = 0;
	public static final short STATE_CONFIRMED = 1;
	public static final short STATE_CONFIRMED_ALARM = 2;
	public static final short STATE_TIMEOUT = 3;
	public static final short STATE_TIMEOUT_ALARM = 4;
	
	//AMS_FilterCond_TimeBasedItems.sTimeBehavior
	public static final short TIMEBEHAVIOR_CONFIRMED_THEN_ALARM = 0;
	public static final short TIMEBEHAVIOR_TIMEOUT_THEN_ALARM = 1;
	
	//TypeIDs for default filter conditions
	public static final int FILTERCONDITIONTYPEID_STRING = 1;
	public static final int FILTERCONDITIONTYPEID_TIMEBASED = 2;
	public static final int FILTERCONDITIONTYPEID_ARRAYSTRING = 3;


	//CSS Check Constants
	public final static String CHECK_PREFIX = "CHECK-";
	public final static String MSGPROP_CHECK_TYPE = CHECK_PREFIX + "TYPE";
	public final static String MSGPROP_CHECK_PURL = CHECK_PREFIX + "PURL";
	public final static String MSGPROP_CHECK_PLUGINID = CHECK_PREFIX + "PLUGINID";
	public final static String MSGPROP_CHECK_STATUSTIME = CHECK_PREFIX + "STATUSTIME";
	public final static String MSGPROP_CHECK_STATUS = CHECK_PREFIX + "STATUS";
	public final static String MSGPROP_CHECK_TEXT = CHECK_PREFIX + "TEXT";
	
}