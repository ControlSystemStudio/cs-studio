package org.csstudio.nams.service.messaging.declaration;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import junit.framework.Assert;

import org.csstudio.nams.service.messaging.exceptions.MessagingException;

public class MessagingServiceMock implements MessagingService {

	private final Set<String> expectedEnvironmentUniqueClientIds;
	private final Map<String, String[]> expectedUrlsToConnectTo;
	private final Map<String, MessagingSession> sessions;

	public MessagingServiceMock(
			final Map<String, String[]> expectedUrlsToConnectTo,
			final Map<String, MessagingSession> sessions) {
		Assert.assertEquals(expectedUrlsToConnectTo.keySet().size(), sessions
				.keySet().size());
		this.expectedEnvironmentUniqueClientIds = expectedUrlsToConnectTo
				.keySet();
		Assert.assertEquals(this.expectedEnvironmentUniqueClientIds.size(),
				sessions.keySet().size());
		Assert.assertTrue(sessions.keySet().containsAll(
				this.expectedEnvironmentUniqueClientIds));

		this.expectedUrlsToConnectTo = expectedUrlsToConnectTo;
		this.sessions = sessions;
	}

	public MessagingSession createNewMessagingSession(
			final String environmentUniqueClientId, final String[] urls)
			throws MessagingException, IllegalArgumentException {
		Assert.assertTrue("An expected environmentUniqueClientId is used.",
				this.checkClientId(environmentUniqueClientId));
		Assert.assertTrue("An expected array of urls to connect to are used.",
				this.checkURLs(environmentUniqueClientId, urls));

		final MessagingSession session = this.sessions
				.get(environmentUniqueClientId);
		Assert.assertNotNull(session);
		return session;
	}

	private boolean checkClientId(final String clientId) {
		for (final String expectedClientId : this.expectedEnvironmentUniqueClientIds) {
			if (expectedClientId.equals(clientId)) {
				return true;
			}
		}
		return false;
	}

	private boolean checkURLs(final String clientId, final String[] jmsURLs) {
		final String[] expectedURLs = this.expectedUrlsToConnectTo
				.get(clientId);
		if (Arrays.equals(expectedURLs, jmsURLs)) {
			return true;
		}
		return false;
	}

}
