package org.csstudio.nams.service.messaging.impl.jms;

import org.csstudio.nams.service.messaging.declaration.PostfachArt;
import org.csstudio.nams.service.messaging.declaration.Producer;
import org.csstudio.nams.service.messaging.declaration.ProducerFactoryService;

public class ProducerFactoryServiceImpl implements ProducerFactoryService {

	public Producer createProducer(String clientId,
			String messageDestinationName, PostfachArt artDesPostfaches,
			String[] messageServerURLs) {
		switch (artDesPostfaches) {
		case QUEUE:
			return new JMSQueueProducer(clientId, messageDestinationName, messageServerURLs);	
		case TOPIC:
			return new JMSTopicProducer(clientId, messageDestinationName, messageServerURLs);
		}
		return null;
	}

}
