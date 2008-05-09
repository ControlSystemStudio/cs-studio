package org.csstudio.nams.service.messaging.declaration;

import org.csstudio.nams.service.messaging.exceptions.MessagingException;

/**
 * @deprecated Use MessagingService
 *
 */

@Deprecated
public interface ConsumerFactoryService {
	public Consumer createConsumer(String clientId, String messageSourceName, PostfachArt artDesPostfaches, String[] messageServerURLs) throws MessagingException;
}
