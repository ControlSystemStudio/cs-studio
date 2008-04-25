package org.csstudio.nams.service.messaging.impl.jms;

import org.csstudio.nams.common.material.AlarmNachricht;
import org.csstudio.nams.service.messaging.declaration.Consumer;

public class JMSTopicConsumer implements Consumer {

	

	public JMSTopicConsumer(String clientId, String messageSourceName,
			String[] messageServerURLs) {
		
		
		for (String url : messageServerURLs) {
			
		}
		
		
	}

	public void close() {
		
	}

	public boolean isClosed() {
		return false;
	}

	public AlarmNachricht recieveMessage() {
		// TODO Auto-generated method stub
		return null;
	}

	
}
