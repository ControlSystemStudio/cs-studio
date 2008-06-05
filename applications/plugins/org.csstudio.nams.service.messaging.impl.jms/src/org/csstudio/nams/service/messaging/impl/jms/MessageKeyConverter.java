package org.csstudio.nams.service.messaging.impl.jms;

import java.util.HashMap;
import java.util.Map;

import org.csstudio.nams.common.fachwert.MessageKeyEnum;

public class MessageKeyConverter {

	private static Map<String, MessageKeyEnum> keyMapping = new HashMap<String, MessageKeyEnum>();
	static {
		keyMapping.put("type", MessageKeyEnum.TYPE);
		keyMapping.put("command", MessageKeyEnum.MSGPROP_COMMAND); // COMMAND
		// ....
	}

	public static MessageKeyEnum getEnumKeyFor(String jmsKey) {
		MessageKeyEnum result = keyMapping.get(jmsKey.toLowerCase());
		return result;
	}
	
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
