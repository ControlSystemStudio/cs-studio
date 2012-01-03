
package org.csstudio.nams.service.messaging.impl.jms;

import org.csstudio.nams.service.messaging.declaration.MessagingService;
import org.csstudio.nams.service.messaging.extensionPoint.MessagingServiceFactory;

public class MessagingServiceFactoryImpl implements MessagingServiceFactory {

	public MessagingServiceFactoryImpl() {
	    // Nothing to do here
	}

	@Override
    public MessagingService createService() {
		return new JMSMessagingServiceImpl();
	}
}
