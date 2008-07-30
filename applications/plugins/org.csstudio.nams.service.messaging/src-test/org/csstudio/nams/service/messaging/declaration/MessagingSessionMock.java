package org.csstudio.nams.service.messaging.declaration;

import java.util.Map;

import junit.framework.Assert;

import org.csstudio.nams.service.messaging.exceptions.MessagingException;

public class MessagingSessionMock implements MessagingSession {

	private boolean isClosed = false;
	private final Map<String, PostfachArt> expectedPostfachArtenForSources;
	private final Map<String, Consumer> consumerForSources;
	private final Map<String, PostfachArt> expectedPostfachArtenForDestination;
	private final Map<String, Producer> producerForDestination;

	public MessagingSessionMock(
			final Map<String, PostfachArt> expectedPostfachArtenForSources,
			final Map<String, Consumer> consumerForSources,
			final Map<String, PostfachArt> expectedPostfachArtenForDestination,
			final Map<String, Producer> producerForDestination) {

		this.expectedPostfachArtenForSources = expectedPostfachArtenForSources;
		this.consumerForSources = consumerForSources;
		Assert.assertEquals(expectedPostfachArtenForSources.keySet(),
				consumerForSources.keySet());
		Assert.assertFalse(expectedPostfachArtenForSources.keySet().contains(
				null));
		Assert.assertFalse(expectedPostfachArtenForSources.values().contains(
				null));
		Assert.assertFalse(consumerForSources.keySet().contains(null));
		Assert.assertFalse(consumerForSources.values().contains(null));

		this.expectedPostfachArtenForDestination = expectedPostfachArtenForDestination;
		this.producerForDestination = producerForDestination;
		Assert.assertEquals(expectedPostfachArtenForDestination.keySet(),
				producerForDestination.keySet());
		Assert.assertFalse(expectedPostfachArtenForDestination.keySet()
				.contains(null));
		Assert.assertFalse(expectedPostfachArtenForDestination.values()
				.contains(null));
		Assert.assertFalse(producerForDestination.keySet().contains(null));
		Assert.assertFalse(producerForDestination.values().contains(null));
	}

	public void close() {
		this.isClosed = true;
	}

	public Consumer createConsumer(final String messageSourceName,
			final PostfachArt artDesPostfaches) throws MessagingException {
		Assert.assertFalse("Session is not closed.", this.isClosed());

		Assert.assertTrue(this.expectedPostfachArtenForSources.keySet()
				.contains(messageSourceName));
		Assert.assertEquals(this.expectedPostfachArtenForSources
				.get(messageSourceName), artDesPostfaches);

		final Consumer consumer = this.consumerForSources
				.get(messageSourceName);
		Assert.assertNotNull(consumer);
		return consumer;
	}

	public Producer createProducer(final String messageDestinationName,
			final PostfachArt artDesPostfaches) throws MessagingException {
		Assert.assertFalse("Session is not closed.", this.isClosed());

		Assert.assertTrue(this.expectedPostfachArtenForDestination.keySet()
				.contains(messageDestinationName));
		Assert.assertEquals(this.expectedPostfachArtenForDestination
				.get(messageDestinationName), artDesPostfaches);

		final Producer producer = this.producerForDestination
				.get(messageDestinationName);
		Assert.assertNotNull(producer);
		return producer;
	}

	public boolean isClosed() {
		return this.isClosed;
	}

}
