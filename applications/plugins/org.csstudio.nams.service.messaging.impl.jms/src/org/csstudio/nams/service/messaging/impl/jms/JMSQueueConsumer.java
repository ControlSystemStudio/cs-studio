package org.csstudio.nams.service.messaging.impl.jms;

import org.csstudio.nams.common.material.AlarmNachricht;
import org.csstudio.nams.service.messaging.declaration.Consumer;

class JMSQueueConsumer implements Consumer {

	public JMSQueueConsumer(String clientId, String messageSourceName,
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

	public AlarmNachricht recieveMessage() {
		// TODO Auto-generated method stub
		return null;
	}

}
