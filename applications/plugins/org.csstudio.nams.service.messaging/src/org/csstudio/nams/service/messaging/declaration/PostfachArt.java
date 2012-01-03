
package org.csstudio.nams.service.messaging.declaration;

public enum PostfachArt {
	/**
	 * Liefert allen Klienten alle Nachrichten.
	 */
	TOPIC,

	/**
	 * Liefert allen Klienten alle Nachrichten.
	 */
	TOPIC_DURABLE,
	
	/**
	 * Liefert jede Nachricht nur an den jeweils zuerst fragenden Klienten aus.
	 */
	QUEUE;
}
