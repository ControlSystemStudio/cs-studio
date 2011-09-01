package org.csstudio.nams.service.messaging.impl.jms;

import java.util.Map;

import org.csstudio.nams.common.fachwert.MessageKeyEnum;

public final class MessageKeyUtil {

	public final static String MSGVALUE_TCMD_RELOAD = "AMS_RELOAD_CFG";

	public final static String MSGVALUE_TCMD_RELOAD_CFG_START = MessageKeyUtil.MSGVALUE_TCMD_RELOAD
			+ "_START";
	public final static String MSGVALUE_TCMD_RELOAD_CFG_END = MessageKeyUtil.MSGVALUE_TCMD_RELOAD
			+ "_END";

	public static boolean istSynchronisationAuforderung(
			final Map<MessageKeyEnum, String> map) {
		if (map.containsKey(MessageKeyEnum.MSGPROP_COMMAND)) {
			final String command = map.get(MessageKeyEnum.MSGPROP_COMMAND);
			return command
					.equals(MessageKeyUtil.MSGVALUE_TCMD_RELOAD_CFG_START);
		}

		return false;
	}

	public static boolean istSynchronisationBestaetigung(
			final Map<MessageKeyEnum, String> map) {
		if (map.containsKey(MessageKeyEnum.MSGPROP_COMMAND)) {
			final String command = map.get(MessageKeyEnum.MSGPROP_COMMAND);
			return command.equals(MessageKeyUtil.MSGVALUE_TCMD_RELOAD_CFG_END);
		}

		return false;
	}

	// private static Map<String, MessageKeyEnum> keyMapping = new
	// HashMap<String, MessageKeyEnum>();
	// static {
	// keyMapping.put("ams-filterid", MessageKeyEnum.AMS_REINSERTED);
	// keyMapping.put("application-id", MessageKeyEnum.APPLICATION_ID);
	// keyMapping.put("class", MessageKeyEnum.CLASS);
	// keyMapping.put("destination", MessageKeyEnum.DESTINATION);
	// keyMapping.put("domain", MessageKeyEnum.DOMAIN);
	// keyMapping.put("eventtime", MessageKeyEnum.EVENTTIME);
	// keyMapping.put("facility", MessageKeyEnum.FACILITY);
	// keyMapping.put("host", MessageKeyEnum.HOST);
	// keyMapping.put("location", MessageKeyEnum.LOCATION);
	// keyMapping.put("command", MessageKeyEnum.MSGPROP_COMMAND); // COMMAND
	// keyMapping.put("name", MessageKeyEnum.NAME);
	// keyMapping.put("process-id", MessageKeyEnum.PROCESS_ID);
	// keyMapping.put("severity", MessageKeyEnum.SEVERITY);
	// keyMapping.put("status", MessageKeyEnum.STATUS);
	// keyMapping.put("text", MessageKeyEnum.TEXT);
	// keyMapping.put("type", MessageKeyEnum.TYPE);
	// keyMapping.put("user", MessageKeyEnum.USER);
	// keyMapping.put("value", MessageKeyEnum.VALUE);
	// }
	//
	// public static MessageKeyEnum getEnumKeyFor(String jmsKey) {
	// MessageKeyEnum result = keyMapping.get(jmsKey.toLowerCase());
	// return result;
	// }
	//	
	private MessageKeyUtil() {
	    // Avoid instantiation
	}

	// public static String getStringFor(MessageKeyEnum msgprop_command) {
	// // return keyMapping.v;
	// }
}
