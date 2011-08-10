
package org.csstudio.nams.service.messaging.impl.jms;

import javax.jms.JMSException;
import javax.naming.NamingException;

import org.csstudio.nams.service.messaging.declaration.MessagingService;
import org.csstudio.nams.service.messaging.declaration.MessagingSession;
import org.csstudio.nams.service.messaging.exceptions.MessagingException;

public class JMSMessagingServiceImpl implements MessagingService {

	@Override
    public MessagingSession createNewMessagingSession(
			final String environmentUniqueClientId, final String[] urls)
			throws MessagingException, IllegalArgumentException {

		// TODO urls prüfen

		try {
			return new JMSMessagingSessionImpl(environmentUniqueClientId, urls);
		} catch (final NamingException e) {
			// TODO exception handling
			throw new MessagingException(
					"NamingException during creation of JMSMessagingSessionImpl",
					e);
		} catch (final JMSException e) {
			// TODO exception handling
			throw new MessagingException(
					"JMSException during creation of JMSMessagingSessionImpl",
					e);
		}
	}
}
