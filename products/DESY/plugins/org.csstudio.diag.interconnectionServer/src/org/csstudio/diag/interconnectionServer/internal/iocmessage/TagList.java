/* 
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchroton, 
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

package org.csstudio.diag.interconnectionServer.internal.iocmessage;

import java.util.HashMap;
import java.util.Map;


/**
 * List of possible 'known' tags in alarm messages
 * 
 * @author Matthias Clausen, Joerg Rathlev
 */
public final class TagList {
	
	/*
	 * Zur Information: Nachrichtentypen, die vom JMS-System unterstützt werden
	 * (Info von Markus). Dieses sind die Typen, die in das Feld TYPE einer
	 * JMS-Nachricht eingetragen werden dürfen.
	 * 
	 * unknown
	 *   Alle undefinierten Nachrichtentypen werden auf diesen Typ abgebildet.
	 * css
	 *   Wird für vom CSS generierte Nachrichten verwendet. Unklar, ob dieser
	 *   Typ tatsächlich in Benutzung ist.
	 * sms
	 *   Nachricht enthält eine SMS. Wird verwendet für JMS-Nachrichten an den
	 *   entsprechenden Konnektor.
	 * smsmail
	 *   Alter Nachrichtentyp, wird eigentlich nicht mehr benutzt.
	 * command
	 *   Für Kommandos, die über JMS gesendet werden. Unklar, ob dieser
	 *   Nachrichtentyp derzeit tatsächlich eingesetzt wird.
	 * alarm
	 *   Nachrichtentyp für Alarm-Nachrichten. Veraltet, ersetzt durch 'event'.
	 * log
	 *   Log-Meldungen. Wird verwendet.
	 * d3-alarm
	 *   Für Alarme vom D3-Kontrollsystem.
	 * event
	 *   Für Alarm-Nachrichten von EPICS.
	 * ioc-alarm
	 *   Der Interconnection Server sollte Nachrichten dieses Typs generieren,
	 *   wenn sich der Verbindungszustand zu einem IOC ändert.
	 */
	
	// TODO: replace integer constants with enum types.
	
	private static Map<String,Integer> tagList = null;
	private static Map<String,Integer> messageTypes = null;
	private static Map<String,Integer> replyTypes = null;
	
	/**
	 * Old message type of alarm messages. This type is no longer in use.
	 * Changes of the alarm state are now reported with messages of type
	 * {@link #EVENT_MESSAGE}.
	 */
	public static final int ALARM_MESSAGE = 1;
	
	/**
	 * Undocumented message type. This is possibly an old message type that
	 * was replaced by {@link #STATUS_MESSAGE}. The current/old version of the
	 * Interconnection Server treats it as equivalent to {@link #EVENT_MESSAGE}.
	 */
	public static final int ALARM_STATUS_MESSAGE = 2;
	
	/**
	 * System log message. This is used for a new logging mechanism which will
	 * replace cmlog.
	 */
	public static final int SYSTEM_LOG_MESSAGE = 3;
	
	/**
	 * This message type is not used by the IOC. The old/current Interconnection
	 * Server treats it as equivalent to {@link #SYSTEM_LOG_MESSAGE}. 
	 */
	public static final int APPLICATION_LOG_MESSAGE = 4;
	
	/**
	 * This message type is used when the severity of a record changes. 
	 */
	public static final int EVENT_MESSAGE = 5;
	
	/**
	 * This message contains the current severity of a record. It is sent upon
	 * request, even when the severity did not recently change.
	 */
	public static final int STATUS_MESSAGE = 6;

	/**
	 * Old beacon message type. This type is no longer used. Current IOCs send
	 * one of {@link #BEACON_MESSAGE_SELECTED} or
	 * {@link #BEACON_MESSAGE_NOT_SELECTED}.
	 */
	public static final int BEACON_MESSAGE = 7;
	
	/**
	 * Beacon messages sent to the selected Interconnection Server.
	 */
	public static final int BEACON_MESSAGE_SELECTED = 8;
	
	/**
	 * Beacon message sent to an Interconnection Server which is not selected.
	 */
	public static final int BEACON_MESSAGE_NOT_SELECTED = 9;
	
	/**
	 * Message type used for unknown messages. 
	 */
	public static final int UNKNOWN_MESSAGE = 10;
	
	/**
	 * Put log message. This message type is not yet used. 
	 */
	public static final int PUT_LOG_MESSAGE = 11;
	
	/**
	 * Test message. 
	 */
	public static final int TEST_COMMAND = 12;
	
	/**
	 * Message type for system messages sent from the IOC to the ICS.
	 */
	public static final int IOC_SYSTEM_MESSAGE = 14;
	
	/**
	 * Message type for SNL log messages.
	 */
	public static final int SNL_LOG_MESSAGE = 15;
	
	/**
	 * Message type for SIM message (records in SIM mode)
	 */
	public static final int SIM_LOG_MESSAGE = 16;
	
	/**
	 * Message type for ADIS messages (records in alarm disable mode)
	 */
	public static final int ADIS_LOG_MESSAGE = 17;
	
	public static final int REPLY_TYPE_DONE = 1;
	public static final int REPLY_TYPE_OK = 2;
	public static final int REPLY_TYPE_ERROR = 3;
	public static final int REPLY_TYPE_CMD_UNKNOWN = 4;
	public static final int REPLY_TYPE_CMD_MISSING = 5;
	public static final int REPLY_TYPE_REFUSED = 6;
	public static final int REPLY_TYPE_SELECTED = 7;
	public static final int REPLY_TYPE_NOT_SELECTED = 8;

	public static final int TAG_TYPE_LOG_SERVER_REPLY = 1;
	public static final int TAG_TYPE_TYPE = 2;
	public static final int TAG_TYPE_ID = 3;
	public static final int TAG_TYPE_REPLY = 4;
	public static final int TAG_TYPE_COMMAND = 5;
	public static final int TAG_TYPE_UNKNOWN = -1;
	
	static {
		tagList = new HashMap<String, Integer>();
		messageTypes = new HashMap<String, Integer>();
		replyTypes = new HashMap<String, Integer>();
		fillTagList();
		fillMessageTypes();
		fillReplyTypes();
	}
	
	// prevent instantiation
	private TagList () {
	}
	
	private static void fillMessageTypes() {
		messageTypes.put("alarm", 				ALARM_MESSAGE); // replaced with 'event'
		messageTypes.put("alarmStatus", 		ALARM_STATUS_MESSAGE); // not used?
		messageTypes.put("sysLog", 				SYSTEM_LOG_MESSAGE); // new logging
		messageTypes.put("sysMsg", 				IOC_SYSTEM_MESSAGE); // message to the ics: error, switchOver, ...
		messageTypes.put("appLog", 				APPLICATION_LOG_MESSAGE); // not used by ioc. compatibility?
		messageTypes.put("event", 				EVENT_MESSAGE);
		messageTypes.put("status", 				STATUS_MESSAGE);
		messageTypes.put("beacon", 				BEACON_MESSAGE); // old
		messageTypes.put("beaconSelected", 		BEACON_MESSAGE_SELECTED);
		messageTypes.put("beaconNotSelected", 	BEACON_MESSAGE_NOT_SELECTED);
		messageTypes.put("unknown", 			UNKNOWN_MESSAGE);
		messageTypes.put("putLog", 				PUT_LOG_MESSAGE); // not yet
		messageTypes.put("TCom", 				TEST_COMMAND);
		messageTypes.put("snlLog", 				SNL_LOG_MESSAGE);
		messageTypes.put("sim", 				SIM_LOG_MESSAGE);
		messageTypes.put("adis", 				ADIS_LOG_MESSAGE);
	}
	
	private static void fillReplyTypes() {
		replyTypes.put("done", 			REPLY_TYPE_DONE);
		
		/*
		 * Note: the IOC sends a lowercase "ok", but EXPECTS "Ok" when the
		 * Interconnection server sends a reply!
		 */
		replyTypes.put("ok", 			REPLY_TYPE_OK);
		replyTypes.put("error", 		REPLY_TYPE_ERROR);
		replyTypes.put("cmdUnknown", 	REPLY_TYPE_CMD_UNKNOWN);
		replyTypes.put("cmdMissing", 	REPLY_TYPE_CMD_MISSING);
		replyTypes.put("refused", 		REPLY_TYPE_REFUSED);
		replyTypes.put("selected", 		REPLY_TYPE_SELECTED);
		replyTypes.put("notSelected", 	REPLY_TYPE_NOT_SELECTED);
	}
	
	private static void fillTagList() {
		tagList.put("TEST-KEY", TagList.TAG_TYPE_LOG_SERVER_REPLY);
		tagList.put("ID",       TagList.TAG_TYPE_ID);
		tagList.put("TYPE",     TagList.TAG_TYPE_TYPE);
		tagList.put("REPLY",    TagList.TAG_TYPE_REPLY);
		tagList.put("COMMAND",	TagList.TAG_TYPE_COMMAND);
	}

	/**
	 * Returns the type of the specified tag.
	 * 
	 * @param tag
	 *            the tag.
	 * @return a type constant, or {#TAG_TYPE_UNKNOWN} if the tag does not have
	 *         a known type.
	 */
	public static int getTagType(String tag) {
		Integer type = tagList.get(tag);
		return type != null ? type.intValue() : TAG_TYPE_UNKNOWN;
	}
	
	/**
	 * Returns a constant representing the specified message type.
	 * 
	 * @param messageType
	 *            the message type, represented as a string.
	 * @return the message type, represented as an integer constant.
	 *         {@link #UNKNOWN_MESSAGE} if the message type is unknown.
	 */
	public static int getMessageType(String messageType) {
		Integer type = messageTypes.get(messageType);
		return type != null ? type.intValue() : UNKNOWN_MESSAGE;
	}

	/**
	 * Returns a constant representing the specified reply type.
	 * 
	 * @param replyType
	 *            the reply type, represented as a string.
	 * @return the reply type, represented as an integer constant.
	 *         {@link #REPLY_TYPE_CMD_UNKNOWN} if the reply type is unknown.
	 */
	public static int getReplyType(String replyType) {
		Integer type = replyTypes.get(replyType);
		return type != null ? type.intValue() : REPLY_TYPE_CMD_UNKNOWN;
	}
}
