
package org.csstudio.nams.service.messaging.extensionPoint;

import org.csstudio.nams.service.messaging.declaration.MessagingService;

public interface MessagingServiceFactory {
	public MessagingService createService();
}
