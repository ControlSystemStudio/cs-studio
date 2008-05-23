package org.csstudio.nams.service.messaging.impl.jms;

import java.util.Map;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.csstudio.nams.common.material.SystemNachricht;
import org.csstudio.nams.service.logging.declaration.Logger;
import org.csstudio.nams.service.messaging.declaration.PostfachArt;
import org.csstudio.nams.service.messaging.declaration.Producer;

public class JMSProducer implements Producer {

	private MessageProducer[] producers;
	private boolean isClosed;
	private Logger logger;

	public JMSProducer(String messageDestinationName,
			PostfachArt artDesPostfaches, Session[] sessions) throws JMSException {
		
		logger = JMSActivator.getLogger();
		
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
			logger.logErrorMessage(this, e.getLocalizedMessage(), e);
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
		logger.logDebugMessage(this, "Producer closed");
	}

	public boolean isClosed() {
		return isClosed;
	}

	public void sendeSystemnachricht(SystemNachricht systemNachricht) {
		// TODO implementieren
	}

	@Deprecated
	public void sendeMap(Map<String, String> map) {
		// TODO Auto-generated method stub
		
	}

}
