package org.csstudio.nams.service.messaging.impl.jms;

import org.csstudio.nams.service.messaging.declaration.ConsumerFactoryService;
import org.csstudio.nams.service.messaging.extensionPoint.ConsumerFactoryServiceFactory;

public class ConsumerFactoryServiceFactoryImpl extends
		ConsumerFactoryServiceFactory {

	@Override
	protected ConsumerFactoryService doCreateConsumerFactoryService() {
		return new ConsumerFactoryServiceImpl();
	}

}
