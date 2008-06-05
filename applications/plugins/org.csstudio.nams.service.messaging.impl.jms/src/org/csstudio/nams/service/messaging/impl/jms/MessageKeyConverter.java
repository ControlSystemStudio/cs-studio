package org.csstudio.nams.service.messaging.impl.jms;

import java.util.HashMap;
import java.util.Map;

import org.csstudio.nams.common.fachwert.MessageKeyEnum;

public final class MessageKeyConverter {

	private static Map<String, MessageKeyEnum> keyMapping = new HashMap<String, MessageKeyEnum>();
	static {
		keyMapping.put("ams-filterid", MessageKeyEnum.AMS_REINSERTED);
		keyMapping.put("application-id", MessageKeyEnum.APPLICATION_ID);
		keyMapping.put("class", MessageKeyEnum.CLASS);
		keyMapping.put("destination", MessageKeyEnum.DESTINATION);
		keyMapping.put("domain", MessageKeyEnum.DOMAIN);
		keyMapping.put("eventtime", MessageKeyEnum.EVENTTIME);
		keyMapping.put("facility", MessageKeyEnum.FACILITY);
		keyMapping.put("host", MessageKeyEnum.HOST);
		keyMapping.put("location", MessageKeyEnum.LOCATION);
		keyMapping.put("command", MessageKeyEnum.MSGPROP_COMMAND); // COMMAND
		keyMapping.put("name", MessageKeyEnum.NAME);
		keyMapping.put("process-id", MessageKeyEnum.PROCESS_ID);
		keyMapping.put("severity", MessageKeyEnum.SEVERITY);
		keyMapping.put("status", MessageKeyEnum.STATUS);
		keyMapping.put("text", MessageKeyEnum.TEXT);
		keyMapping.put("type", MessageKeyEnum.TYPE);
		keyMapping.put("user", MessageKeyEnum.USER);
		keyMapping.put("value", MessageKeyEnum.VALUE);
	}

	public static MessageKeyEnum getEnumKeyFor(String jmsKey) {
		MessageKeyEnum result = keyMapping.get(jmsKey.toLowerCase());
		return result;
	}
	
	private MessageKeyConverter() {}
	
	private final static String MSGVALUE_TCMD_RELOAD = "AMS_RELOAD_CFG";
	private final static String MSGVALUE_TCMD_RELOAD_CFG_START = MSGVALUE_TCMD_RELOAD + "_START";
	private final static String MSGVALUE_TCMD_RELOAD_CFG_END = MSGVALUE_TCMD_RELOAD + "_END";
	
	public static boolean istSynchronisationBestaetigung(
			Map<MessageKeyEnum, String> map) {
		if (map.containsKey(MessageKeyEnum.MSGPROP_COMMAND)) {
			String command = map.get(MessageKeyEnum.MSGPROP_COMMAND);
			return command.equals(MSGVALUE_TCMD_RELOAD_CFG_END);
		}
		
		return false;
	}

	public static boolean istSynchronisationAuforderung(
			Map<MessageKeyEnum, String> map) {
		if (map.containsKey(MessageKeyEnum.MSGPROP_COMMAND)) {
			String command = map.get(MessageKeyEnum.MSGPROP_COMMAND);
			return command.equals(MSGVALUE_TCMD_RELOAD_CFG_START);
		}
		
		return false;
	}
}
