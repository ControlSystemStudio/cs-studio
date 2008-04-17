package org.csstudio.platform.libs.jms;

import javax.jms.MapMessage;
import javax.jms.Message;

import org.csstudio.platform.libs.jms.JmsRedundantProducer.ProducerId;

public interface IJmsProducer {

	/**
	 * Closes all sessions/connections. The producer can not be used afterwards.
	 * 
	 * @require !isClosed()
	 */
	public abstract void closeAll();

	/**
	 * Creates a MessageProducer with given topic-destination.
	 * 
	 * @param topicName
	 *            Name of the destination topic (could be null)
	 * @return The Id for the created Producer.
	 * @throws RuntimeException
	 *             If no producer could be created for given topic (the nested
	 *             exception will be the JMSException)!
	 * @require !isClosed()
	 */
	public abstract ProducerId createProducer(String topicName)
			throws RuntimeException;

	/**
	 * Determines if producers on id have an initial destination topic.
	 * 
	 * @param id
	 *            The id of the producers
	 * @return {@code true}, if the producers on the id have an initial
	 *         destination topic, {@code false} otherwise.
	 * @throws RuntimeException
	 *             If destination couldn't be retrieved on at least one producer
	 *             (the nested exception will be the JMSException)!
	 * @require !isClosed()
	 * @require knowsProducer(id)
	 */
	public abstract boolean hasProducerDestiantion(ProducerId id)
			throws RuntimeException;

	/**
	 * Checks if this RedundantProducer knows the specified producer.
	 * 
	 * @param id
	 *            Id of producer to check
	 * @return {@code true} if producer is known, {@code false} otherwise.
	 */
	public abstract boolean knowsProducer(final ProducerId id);

	/**
	 * Determines if the producer has been closed.
	 * 
	 * @return {@code true} if producers is closed, {@code false} otherwise.
	 */
	public abstract boolean isClosed();

	/**
	 * Sends the given message to the producers specified by the id. Should only
	 * be called if the producers have an initial topic (see
	 * {@link #hasProducerDestiantion(org.csstudio.platform.libs.jms.JmsRedundantProducer.ProducerId)}).
	 * 
	 * @param id
	 *            The id of the producer
	 * @param message
	 *            The Message to send
	 * @return An array of URLs, where the message could successfully send to
	 * @throws RuntimeException
	 *             If the message couldn't be send to at least one producer (the
	 *             nested exception will be the JMSException)!
	 * @require !isClosed()
	 * @require knowsProducer(id)
	 */
	public abstract String[] send(ProducerId id, Message message)
			throws RuntimeException;

	/**
	 * Sends the given message to the producers specified by the id.
	 * 
	 * @param id
	 *            The id of the producer
	 * @param topicName
	 *            The name of the topic to send the message (could be null if
	 *            producer has initial topic (see
	 *            {@link #hasProducerDestiantion(org.csstudio.platform.libs.jms.JmsRedundantProducer.ProducerId)}))
	 * @param message
	 *            The Message to send
	 * @return An array of URLs, where the message could successfully send to
	 * @throws RuntimeException
	 *             If the message couldn't be send to at least one producer (the
	 *             nested exception will be the JMSException)!
	 * @require !isClosed()
	 * @require knowsProducer(id)
	 */
	public abstract String[] send(ProducerId id, String topicName,
			Message message) throws RuntimeException;

	
	public abstract MapMessage createMapMessage() throws RuntimeException;
}