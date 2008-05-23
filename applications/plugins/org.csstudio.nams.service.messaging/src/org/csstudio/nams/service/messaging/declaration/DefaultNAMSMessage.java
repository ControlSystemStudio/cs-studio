package org.csstudio.nams.service.messaging.declaration;

import java.util.Map;

import org.csstudio.nams.common.material.AlarmNachricht;
import org.csstudio.nams.common.material.SyncronisationsAufforderungsSystemNachchricht;
import org.csstudio.nams.common.material.SyncronisationsBestaetigungSystemNachricht;
import org.csstudio.nams.common.material.SystemNachricht;
import org.csstudio.nams.service.messaging.exceptions.MessagingException;

public class DefaultNAMSMessage implements NAMSMessage {

	final static String MSGPROP_COMMAND = "COMMAND"; 
	final static String MSGVALUE_TCMD_RELOAD = "AMS_RELOAD_CFG";
	final static String MSGVALUE_TCMD_RELOAD_CFG_START = MSGVALUE_TCMD_RELOAD + "_START";
	final static String MSGVALUE_TCMD_RELOAD_CFG_END = MSGVALUE_TCMD_RELOAD + "_END";
	
	private final Map<String, String> map;
	private final AcknowledgeHandler handler;
	private SystemNachricht systemNachricht;

	public DefaultNAMSMessage(Map<String, String> map, AcknowledgeHandler handler) {
		this.map = map;
		this.handler = handler;
		
		if (map.containsKey(MSGPROP_COMMAND)) {
			String command = map.get(MSGPROP_COMMAND);
			if (command.equals(MSGVALUE_TCMD_RELOAD_CFG_START)) {
				systemNachricht = new SyncronisationsAufforderungsSystemNachchricht();
			} else if (command.equals(MSGVALUE_TCMD_RELOAD_CFG_END)) {
				systemNachricht = new SyncronisationsBestaetigungSystemNachricht();
			} else {
				throw new RuntimeException("unbekannte Systemnachricht: "+command);
			}
		} else {
			systemNachricht = null;
		}
	}
	
	public void acknowledge() throws MessagingException {
		try {
			this.handler.acknowledge();
		} catch (Throwable e) {
			throw new MessagingException(e);
		}
	}

	public AlarmNachricht alsAlarmnachricht() {
		// TODO Auto-generated method stub
		return null;
	}

	public Map<String, String> alsMap() {
		throw new UnsupportedOperationException();
	}

	public SystemNachricht alsSystemachricht() {
		return systemNachricht;
	}

	public boolean enthaeltAlarmnachricht() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean enthaeltSystemnachricht() {
		return systemNachricht != null;
	}

	public static interface AcknowledgeHandler {
		public void acknowledge() throws Throwable;
	}
	
}
