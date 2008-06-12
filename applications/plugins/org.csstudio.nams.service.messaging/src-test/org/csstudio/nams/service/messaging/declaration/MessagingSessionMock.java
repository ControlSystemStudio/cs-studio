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
			Map<String, PostfachArt> expectedPostfachArtenForSources,
			Map<String, Consumer> consumerForSources,
			Map<String, PostfachArt> expectedPostfachArtenForDestination,
			Map<String, Producer> producerForDestination) {

		this.expectedPostfachArtenForSources = expectedPostfachArtenForSources;
		this.consumerForSources = consumerForSources;
		Assert.assertEquals(expectedPostfachArtenForSources.keySet(), consumerForSources.keySet());
		Assert.assertFalse(expectedPostfachArtenForSources.keySet().contains(null));
		Assert.assertFalse(expectedPostfachArtenForSources.values().contains(null));
		Assert.assertFalse(consumerForSources.keySet().contains(null));
		Assert.assertFalse(consumerForSources.values().contains(null));

		this.expectedPostfachArtenForDestination = expectedPostfachArtenForDestination;
		this.producerForDestination = producerForDestination;
		Assert.assertEquals(expectedPostfachArtenForDestination.keySet(), producerForDestination.keySet());
		Assert.assertFalse(expectedPostfachArtenForDestination.keySet().contains(null));
		Assert.assertFalse(expectedPostfachArtenForDestination.values().contains(null));
		Assert.assertFalse(producerForDestination.keySet().contains(null));
		Assert.assertFalse(producerForDestination.values().contains(null));
	}

	public void close() {
		isClosed = true;
	}

	public Consumer createConsumer(String messageSourceName,
			PostfachArt artDesPostfaches) throws MessagingException {
		Assert.assertFalse("Session is not closed.", isClosed());

		Assert.assertTrue(expectedPostfachArtenForSources.keySet().contains(
				messageSourceName));
		Assert.assertEquals(expectedPostfachArtenForSources
				.get(messageSourceName), artDesPostfaches);

		Consumer consumer = consumerForSources.get(messageSourceName);
		Assert.assertNotNull(consumer);
		return consumer;
	}

	public Producer createProducer(String messageDestinationName,
			PostfachArt artDesPostfaches) throws MessagingException {
		Assert.assertFalse("Session is not closed.", isClosed());

		Assert.assertTrue(expectedPostfachArtenForDestination.keySet()
				.contains(messageDestinationName));
		Assert.assertEquals(expectedPostfachArtenForDestination
				.get(messageDestinationName), artDesPostfaches);

		Producer producer = producerForDestination.get(messageDestinationName);
		Assert.assertNotNull(producer);
		return producer;
	}

	public boolean isClosed() {
		return isClosed;
	}

}
