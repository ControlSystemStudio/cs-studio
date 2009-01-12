package org.csstudio.platform.utility.jms;

import javax.jms.Message;

public interface IJmsRedundantReceiver {

	/**
	 * Create a non-durable subscriber identified by the given name. The name is just used within the object.
	 * 
	 * @param name Object internal name of the subscriber
	 * @param destination Name of the topic
	 * @return True - Subscriber have been created, false - Subscriber have not been created
	 * 
	 */
	public abstract boolean createRedundantSubscriber(String name,
			String destination);

	
    /**
     * Create a durable subscriber identified by the given name. The name is just used within the object.
     * The parameter durableName is used to register this durable subscriber to the JMS server.
     * 
     * @param name Object internal name of the subscriber
     * @param destination Name of the topic
     * @param durableName Name of the durable subscriber
     * @param durable true, if the subscriber should be durable
     * @return True - Subscriber have been created, false - Subscriber have not been created
     * 
     */
	public abstract boolean createRedundantSubscriber(String name,
	            String destination, String durableName, boolean durable);

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
	 * Returns the current message. The method waits <code>waitTime</code> miliseconds for <b>every</b>
	 * subscriber and returns if no message have been received.
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