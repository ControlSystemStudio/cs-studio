
package org.csstudio.nams.service.messaging.declaration;

import org.csstudio.nams.common.decision.Vorgangsmappe;
import org.csstudio.nams.common.material.SystemNachricht;
import org.csstudio.nams.service.messaging.exceptions.MessagingException;

public interface Producer {
	/**
	 * Indicates weather this producer is closed.
	 */
	public boolean isClosed();

	/**
	 * Send a {@link SystemNachricht}
	 * 
	 * @throws MessagingException
	 *             If an send-error/exception occurred.
	 */
	public void sendeSystemnachricht(SystemNachricht systemNachricht)
			throws MessagingException;

	/**
	 * Send a {@link Vorgangsmappe}
	 * 
	 * @throws MessagingException
	 *             If an send-error/exception occurred.
	 */
	public void sendeVorgangsmappe(Vorgangsmappe vorgangsmappe)
			throws MessagingException;

	/**
	 * Tries to close the producer, errors/exceptions during closing will be
	 * ignored.
	 */
	public void tryToClose();
}
