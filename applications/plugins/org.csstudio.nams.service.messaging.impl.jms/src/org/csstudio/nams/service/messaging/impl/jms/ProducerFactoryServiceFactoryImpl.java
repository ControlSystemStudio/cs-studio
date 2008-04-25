package org.csstudio.nams.service.messaging.impl.jms;

import org.csstudio.nams.service.messaging.declaration.ProducerFactoryService;
import org.csstudio.nams.service.messaging.extensionPoint.ProducerFactoryServiceFactory;

public class ProducerFactoryServiceFactoryImpl extends
		ProducerFactoryServiceFactory {

	@Override
	protected ProducerFactoryService doCreateProducerFactoryService() {
		return new ProducerFactoryServiceImpl();
	}

}
