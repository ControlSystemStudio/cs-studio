package org.csstudio.nams.service.messaging.impl.jms;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.csstudio.nams.common.material.SystemNachricht;
import org.csstudio.nams.service.messaging.declaration.PostfachArt;
import org.csstudio.nams.service.messaging.declaration.Producer;

public class JMSProducer implements Producer {

	private MessageProducer[] producers;
	private boolean isClosed;

	public JMSProducer(String messageDestinationName,
			PostfachArt artDesPostfaches, Session[] sessions) throws JMSException {
		
		producers = new MessageProducer[sessions.length];
		try {
			for (int i = 0; i < sessions.length; i++) {
				Destination destination = null;
				switch (artDesPostfaches) {
				case QUEUE:
					destination = sessions[i].createQueue(messageDestinationName);
					break;
				case TOPIC:
					destination = sessions[i].createTopic(messageDestinationName);
					break;
				}
				producers[i] = sessions[i].createProducer(destination);
			}
		} catch (JMSException e) {
			close();
			throw e;
		}
		isClosed = false;
	}

	public void close() {
		for (MessageProducer producer : producers) {
			if (producer != null) {
				try {
					producer.close();
				} catch (JMSException e) {}
			}
		}
		isClosed = true;
	}

	public boolean isClosed() {
		return isClosed;
	}

	public void sendeSystemnachricht(SystemNachricht vorgangsmappe) {
		// TODO implementieren
	}

}
