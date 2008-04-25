package org.csstudio.nams.service.messaging.impl.jms;

import org.csstudio.nams.service.messaging.declaration.Consumer;
import org.csstudio.nams.service.messaging.declaration.ConsumerFactoryService;
import org.csstudio.nams.service.messaging.declaration.PostfachArt;

class ConsumerFactoryServiceImpl implements ConsumerFactoryService {

	public Consumer createConsumer(String clientId, String messageSourceName,
			PostfachArt artDesPostfaches, String[] messageServerURLs) {
		switch (artDesPostfaches) {
		case QUEUE:
			return new JMSQueueConsumer(clientId, messageSourceName, messageServerURLs);	
		case TOPIC:
			return new JMSTopicConsumer(clientId, messageSourceName, messageServerURLs);
		}
		return null; // TODO Exception handling
	}

}
