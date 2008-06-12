package org.csstudio.nams.service.messaging.declaration;

import org.csstudio.nams.service.messaging.exceptions.MessagingException;


public interface Consumer {
	public void close();
	public boolean isClosed();
	/**
	 * blockiert bis eine neue Nachricht verfügbar ist
	 * liefert null wenn der Consumer beendet wird
	 */
	//einen eigenen Nachrichten Typ da nicht nur Alarmnachrichten rein kommen
	public NAMSMessage receiveMessage() throws MessagingException, InterruptedException;
}
