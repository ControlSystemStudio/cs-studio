package org.csstudio.nams.service.messaging.extensionPoint;

import org.csstudio.nams.service.messaging.declaration.ProducerFactoryService;

public abstract class ProducerFactoryServiceFactory {
	public ProducerFactoryService createProducerFactoryService()
	{
		return doCreateProducerFactoryService(); 
	}
	
	protected abstract ProducerFactoryService doCreateProducerFactoryService();
}
