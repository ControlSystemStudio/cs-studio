package org.csstudio.platform.libs.jms.dummy;

import java.util.HashMap;

import javax.jms.MapMessage;
import javax.jms.Message;

import org.apache.activemq.command.ActiveMQMapMessage;
import org.csstudio.platform.libs.jms.IJmsProducer;
import org.csstudio.platform.libs.jms.JmsRedundantProducer.ProducerId;

/**
 * 
 * @author Goesta Steen
 */
public class DummyJmsProducer implements IJmsProducer {

	private boolean isClosed = false;
	private HashMap<ProducerId, String> producerToDestination = new HashMap<ProducerId, String>();
	
	public void closeAll() {
		producerToDestination = null;
		isClosed = true;
	}

	public MapMessage createMapMessage() throws RuntimeException {
		return new ActiveMQMapMessage();
	}

	public ProducerId createProducer(String topicName) throws RuntimeException {
		ProducerId producerId = new ProducerId() {};
		producerToDestination.put(producerId, topicName);
		return producerId;
	}

	public boolean hasProducerDestiantion(ProducerId id)
			throws RuntimeException {
		String destination = producerToDestination.get(id);
		if (destination != null && destination.length() > 0) {
			return true;
		}
		return false;
	}

	public boolean isClosed() {
		return isClosed;
	}

	public boolean knowsProducer(ProducerId id) {
		return producerToDestination.keySet().contains(id);
	}

	public String[] send(ProducerId id, Message message)
			throws RuntimeException {
		return send(id, null, message);
	}

	public String[] send(ProducerId id, String topicName, Message message)
			throws RuntimeException {
		//TODO 
		System.out.println(topicName + " " + message.toString());
		return new String[]{"DummyJmsProducer"};
	}

}
