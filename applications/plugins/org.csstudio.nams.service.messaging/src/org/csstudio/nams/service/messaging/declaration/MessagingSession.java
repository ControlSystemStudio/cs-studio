
package org.csstudio.nams.service.messaging.declaration;

import org.csstudio.nams.service.messaging.exceptions.MessagingException;

public interface MessagingSession {

	public void close();

	/**
	 * 
	 * 
	 * @param messageSourceName
	 * @param artDesPostfaches
	 *            Wenn TOPIC dann durrable!
	 * @return A consumer that delivers all avail messages occurred for that
	 *         client-id since first registration of client (see JMS durable
	 *         consuming).
	 * @throws MessagingException
	 */
	public Consumer createConsumer(String messageSourceName,
			PostfachArt artDesPostfaches) throws MessagingException;

	public Producer createProducer(String messageDestinationName,
			PostfachArt artDesPostfaches) throws MessagingException;

	public boolean isClosed();
}
