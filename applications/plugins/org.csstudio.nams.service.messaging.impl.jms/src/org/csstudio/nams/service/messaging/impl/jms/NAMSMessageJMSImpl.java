package org.csstudio.nams.service.messaging.impl.jms;

import java.util.Map;

import javax.jms.JMSException;
import javax.jms.Message;

import org.csstudio.nams.common.contract.Contract;
import org.csstudio.nams.common.material.AlarmNachricht;
import org.csstudio.nams.common.material.SystemNachricht;
import org.csstudio.nams.service.messaging.declaration.NAMSMessage;
import org.csstudio.nams.service.messaging.exceptions.MessagingException;

public class NAMSMessageJMSImpl implements NAMSMessage {

	private final Message message;

	public NAMSMessageJMSImpl(Message message) {
		this.message = message;

	}

	@Override
	public String toString() {
		return message.toString();
	}

	public AlarmNachricht alsAlarmnachricht() {
		Contract.require(enthaeltAlarmnachricht(), "istAlarmnachricht()");

		// TODO Auto-generated method stub
		return null;
	}

	public SystemNachricht alsSystemachricht() {
		Contract.require(enthaeltSystemnachricht(), "istSystemnachricht()");

		// TODO Auto-generated method stub
		return null;
	}

	public boolean enthaeltAlarmnachricht() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean enthaeltSystemnachricht() {
		// TODO Auto-generated method stub
		return false;
	}

	public void acknowledge() throws MessagingException {
		try {
			message.acknowledge();
		} catch (JMSException e) {
			throw new MessagingException("acknowledgment failed", e);
		}
	}

	public Map<String, String> alsMap() {
		// TODO Auto-generated method stub
		return null;
	}

}
