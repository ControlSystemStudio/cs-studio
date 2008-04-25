package org.csstudio.nams.service.messaging.declaration;


public interface ConsumerFactoryService {
	public Consumer createConsumer(String clientId, String messageSourceName, String[] messageServerURLs);
}
