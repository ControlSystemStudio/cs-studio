package org.csstudio.platform.libs.jms;

import javax.jms.Message;

public interface IJmsRedundantReceiver {

	/**
	 * 
	 * @param name Name of the subscriber
	 * @param destination Name of the topic
	 * @return True - Subscriber have been created, false - Subscriber have not been created
	 * 
	 */

	public abstract boolean createRedundantSubscriber(String name,
			String destination);

	/**
	 * Returns the current message. The method does not wait and returns immediately if no message
	 * have been received.
	 * <p>
	 * It takes the messages from the internal queue first. If the
	 * queue does not contain a message, the method calls the receive() method of the MessageConsumer
	 * object.
	 * If more then one server hold a message, the messages will be stored in the internal queue.
	 * 
	 * @param name The internal name of the message consumer
	 * @return Current message from the JMS server
	 */
	public abstract Message receive(String name);

	/**
	 * Returns the current message. The method waits <code>waitTime</code> miliseconds and returns if
	 * no message have been received.
	 * <p>
	 * It takes the messages from the internal queue first. If the
	 * queue does not contain a message, the method calls the receive() method of the MessageConsumer
	 * object.
	 * If more then one server hold a message, the messages will be stored in the internal queue.
	 * 
	 * @param name The internal name of the message consumer
	 * @param waitTime The time to wait(in ms) until the receive method returns
	 * @return Current message from the JMS server
	 */
	public abstract Message receive(String name, long waitTime);

	/**
	 * 
	 * @return True, if the receiver is connected. False otherwise
	 */

	public abstract boolean isConnected();

	public abstract void closeAll();

}