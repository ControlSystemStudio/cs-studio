package org.csstudio.nams.service.messaging.declaration;

import org.csstudio.nams.service.messaging.exceptions.MessagingException;
/**
 * @deprecated Use MessagingService
 *
 */

@Deprecated
public interface ProducerFactoryService {
	public Producer createProducer(String clientId, String messageDestinationName, PostfachArt artDesPostfaches, String[] messageServerURLs) throws MessagingException;
}
