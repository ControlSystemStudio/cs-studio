
package org.csstudio.nams.service.messaging.declaration;

/**
 * Bearbeiter für {@link NAMSMessage}s.
 */
public interface MessageHandler {
	/**
	 * Behandelt eine {@link NAMSMessage}. Fehler sollten intern behandelt
	 * werden.
	 * 
	 * @param message
	 *            {@link NAMSMessage} nicht <code>null</code>
	 */
	public void handleMessage(NAMSMessage message);
}
