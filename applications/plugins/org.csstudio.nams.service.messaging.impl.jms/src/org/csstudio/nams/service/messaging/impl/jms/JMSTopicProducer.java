package org.csstudio.nams.service.messaging.impl.jms;

import javax.jms.MapMessage;

import org.csstudio.nams.common.material.AlarmNachricht;
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

	public void sendMessage(AlarmNachricht message) {
		MapMessage mapMessage = jmsRedundantProducer.createMapMessage();
		// TODO Daten aus der AlarmNachricht in die MapMessage kopieren
		jmsRedundantProducer.send(producerId, mapMessage);
	}

}
