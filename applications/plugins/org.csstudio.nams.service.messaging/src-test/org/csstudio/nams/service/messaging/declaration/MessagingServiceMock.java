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

	public MessagingServiceMock(Map<String, String[]> expectedUrlsToConnectTo,
			Map<String, MessagingSession> sessions) {
		Assert.assertEquals(expectedUrlsToConnectTo.size(), sessions.size());
		this.expectedEnvironmentUniqueClientIds = expectedUrlsToConnectTo
				.keySet();
		Assert.assertEquals(this.expectedEnvironmentUniqueClientIds.size(),
				sessions.keySet().size());
		Assert.assertTrue(sessions.keySet().containsAll(
				this.expectedEnvironmentUniqueClientIds));

		this.expectedUrlsToConnectTo = expectedUrlsToConnectTo;
		this.sessions = sessions;
	}

	private boolean checkClientId(String clientId) {
		for (String expectedClientId : expectedEnvironmentUniqueClientIds) {
			if (expectedClientId.equals(clientId)) {
				return true;
			}
		}
		return false;
	}

	private boolean checkURLs(String clientId, String[] jmsURLs) {
		String[] expectedURLs = this.expectedUrlsToConnectTo.get(clientId);
		if (Arrays.equals(expectedURLs, jmsURLs)) {
			return true;
		}
		return false;
	}

	public MessagingSession createNewMessagingSession(
			String environmentUniqueClientId, String[] urls)
			throws MessagingException, IllegalArgumentException {
		Assert.assertTrue("An expected environmentUniqueClientId is used.",
				checkClientId(environmentUniqueClientId));
		Assert.assertTrue("An expected array of urls to connect to are used.",
				checkURLs(environmentUniqueClientId, urls));

		MessagingSession session = this.sessions.get(environmentUniqueClientId);
		Assert.assertNotNull(session);
		return session;
	}

}
