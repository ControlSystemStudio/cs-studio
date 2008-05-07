package org.csstudio.nams.service.messaging.impl.jms;

import javax.jms.JMSException;
import javax.naming.NamingException;

import org.csstudio.nams.service.messaging.declaration.Consumer;
import org.csstudio.nams.service.messaging.declaration.ConsumerFactoryService;
import org.csstudio.nams.service.messaging.declaration.PostfachArt;
import org.csstudio.nams.service.messaging.exceptions.MessagingException;

class ConsumerFactoryServiceImpl implements ConsumerFactoryService {

	public Consumer createConsumer(String clientId, String messageSourceName,
			PostfachArt artDesPostfaches, String[] messageServerURLs)
			throws MessagingException {
		try {
			return new JMSConsumer(clientId, messageSourceName,
					messageServerURLs, artDesPostfaches);
		} catch (NamingException e) {
			throw new MessagingException(e);
		} catch (JMSException e) {
			throw new MessagingException(e);
		}
		// TODO Exception handling
	}

}
