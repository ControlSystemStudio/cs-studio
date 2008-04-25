package org.csstudio.nams.service.messaging.extensionPoint;

import org.csstudio.nams.service.messaging.declaration.ConsumerFactoryService;

public abstract class ConsumerFactoryServiceFactory {
	public ConsumerFactoryService createConsumerFactoryService()
	{
		return doCreateConsumerFactoryService(); 
	}
	
	protected abstract ConsumerFactoryService doCreateConsumerFactoryService();
}
