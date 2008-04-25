package org.csstudio.nams.service.messaging.impl.jms;

import org.csstudio.nams.common.material.AlarmNachricht;
import org.csstudio.nams.service.messaging.declaration.Producer;

public class JMSQueueProducer implements Producer {

	public JMSQueueProducer(String clientId, String messageDestinationName,
			String[] messageServerURLs) {
		// TODO Auto-generated constructor stub
	}

	public void close() {
		// TODO Auto-generated method stub

	}

	public boolean isClosed() {
		// TODO Auto-generated method stub
		return false;
	}

	public void sendMessage(AlarmNachricht message) {
		// TODO Auto-generated method stub

	}

}
