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

		this.expectedPostfachArtenForDestination = expectedPostfachArtenForDestination;
		this.producerForDestination = producerForDestination;
	}

	public void close() {
		isClosed = true;
	}

	public Consumer createConsumer(String messageSourceName,
			PostfachArt artDesPostfaches) throws MessagingException {
		Assert.assertFalse("Session is not closed.", isClosed());
		// TODO Auto-generated method stub
		return null;
	}

	public Producer createProducer(String messageDestinationName,
			PostfachArt artDesPostfaches) throws MessagingException {
		Assert.assertFalse("Session is not closed.", isClosed());
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isClosed() {
		return isClosed;
	}

}
