package org.csstudio.nams.service.messaging.impl.jms;

import javax.jms.JMSException;
import javax.naming.NamingException;

import org.csstudio.nams.service.messaging.declaration.MessagingService;
import org.csstudio.nams.service.messaging.declaration.MessagingSession;
import org.csstudio.nams.service.messaging.exceptions.MessagingException;

public class JMSMessagingServiceImpl implements MessagingService {

	public MessagingSession createNewMessagingSession(
			String environmentUniqueClientId, String[] urls)
			throws MessagingException, IllegalArgumentException {
		
		//TODO urls prüfen
		
		try {
			return new JMSMessagingSessionImpl(environmentUniqueClientId, urls);
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			throw new MessagingException(e);
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			throw new MessagingException(e);
		}
	}

}
