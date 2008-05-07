package org.csstudio.nams.service.messaging.impl.jms;

import javax.jms.Message;

import org.csstudio.nams.service.messaging.declaration.NAMSMessage;

public class NAMSMessageJMSImpl implements NAMSMessage {

	private final Message message;

	public NAMSMessageJMSImpl(Message message) {
		this.message = message;
		
	}
	
	@Override
	public String toString() {
		return message.toString();
	}

}
