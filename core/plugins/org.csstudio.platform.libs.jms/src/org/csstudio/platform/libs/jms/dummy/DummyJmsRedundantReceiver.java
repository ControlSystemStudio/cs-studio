package org.csstudio.platform.libs.jms.dummy;

import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.jms.Message;

import org.csstudio.platform.libs.jms.IJmsRedundantReceiver;

/**
 * 
 * @author Goesta Steen
 */
public class DummyJmsRedundantReceiver implements IJmsRedundantReceiver {

	private boolean isConnected = true;
	private HashMap<String,  LinkedBlockingQueue<Message>> map = new HashMap<String,  LinkedBlockingQueue<Message>>();
	 
	
	public void closeAll() {
		map = null;
		isConnected = false;
	}

	public boolean createRedundantSubscriber(String name, String destination) {
		map.put(name, new LinkedBlockingQueue<Message>());
		return true;
	}

	public boolean isConnected() {
		return isConnected;
	}

	public Message receive(String subscriberName) {
		return map.get(subscriberName).poll();
	}

	public Message receive(String subscriberName, long waitTime) {
		try {
			return map.get(subscriberName).poll(waitTime, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			// nothing to do here
		}
		return null;
	}
	
	public void addMessage(String subscriberName, Message message) {
		LinkedBlockingQueue<Message> queue = map.get(subscriberName);
		if (queue != null) {
			queue.add(message);
		}
	}

}
