
package org.csstudio.nams.service.messaging.declaration;

import org.csstudio.nams.service.messaging.exceptions.MessagingException;

public interface Consumer {
	public void close();

	public boolean isClosed();

	/**
	 * Liefert die nächste zur Verfügung stehende Nachricht. Blockiert bis eine
	 * neue Nachricht verfügbar ist. Liefert null wenn der Consumer durch
	 * schließen beendet wird; eine {@link InterruptedException}, wenn das
	 * empfangen unterbrochen wird (z.B. shutdown).
	 */
	public NAMSMessage receiveMessage() throws MessagingException,
			InterruptedException;
}
