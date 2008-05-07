package org.csstudio.nams.service.messaging.impl.jms;

import javax.jms.MapMessage;

import org.csstudio.nams.service.messaging.declaration.NAMSMessage;
import org.csstudio.nams.service.messaging.declaration.Producer;
import org.csstudio.platform.libs.jms.JmsRedundantProducer;
import org.csstudio.platform.libs.jms.JmsRedundantProducer.ProducerId;
// TODO exception handling in RedundantPruducer
class JMSTopicProducer implements Producer {

	private JmsRedundantProducer jmsRedundantProducer;
	private ProducerId producerId;

	public JMSTopicProducer(String clientId, String messageDestinationName,
			String[] messageServerURLs) {
		jmsRedundantProducer = new JmsRedundantProducer(clientId, messageServerURLs);
		producerId = jmsRedundantProducer.createProducer(messageDestinationName);
	}

	public void close() {
		jmsRedundantProducer.closeAll();
	}

	public boolean isClosed() {
		return jmsRedundantProducer.isClosed();
	}

	public void sendMessage(NAMSMessage message) {
		MapMessage mapMessage = jmsRedundantProducer.createMapMessage();
		// TODO aus der NAMSMessage eine jms Message holen
		jmsRedundantProducer.send(producerId, mapMessage);
	}

}
