
package org.csstudio.nams.service.messaging.declaration;

import org.csstudio.nams.service.messaging.exceptions.MessagingException;

/**
 * A service to provide {@link MessagingSession}s to comunicate via
 * {@link NAMSMessage}s.
 * 
 * @author <a href="mailto:gs@c1-wps.de">Goesta Steen</a>
 * @author <a href="mailto:mz@c1-wps.de">Matthias Zeimer</a>
 */
public interface MessagingService {

	/**
	 * Creates a new session with given unique id to servers specified with
	 * given URLs.
	 * 
	 * @param environmentUniqueClientId
	 *            This Id has to be unique on the entire environment this
	 *            application is placed in.
	 * @param urls
	 *            Possible redundant, fail-over URLs.
	 * @return The created {@link MessagingSession}, not null.
	 * @throws MessagingException
	 *             if failed to create session
	 * @throws IllegalArgumentException
	 *             if URLs invalid.
	 */
	public MessagingSession createNewMessagingSession(
			String environmentUniqueClientId, String[] urls)
			throws MessagingException, IllegalArgumentException;

}
