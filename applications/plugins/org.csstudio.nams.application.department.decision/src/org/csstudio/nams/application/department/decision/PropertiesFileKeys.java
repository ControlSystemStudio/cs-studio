package org.csstudio.nams.application.department.decision;

/**
 * Schluesel für die Property-Datei dieser Anwendung. Die Namen der Elemente ({@link Enum#name()})
 * sind die Schlüssel der Einträge.
 * 
 * @deprecated War Entwurf für eine lokale Properties-Datei
 */
@Deprecated
enum PropertiesFileKeys {
	/**
	 * Id des Properties-Dateinamen in den System-Properties der VM.
	 * 
	 * Ehemalig: "configFile"
	 */
	CONFIG_FILE,

	/**
	 * Der Schlüssel des Properties-Datei-Eintrages für die client id des
	 * jms message consumer(s).
	 * 
	 * Ehemalig: "CONSUMER_CLIENT_ID"
	 */
	MESSAGING_CONSUMER_CLIENT_ID,

	/**
	 * Der Schlüssel des Properties-Datei-Eintrages für den source name des
	 * jms message consumer(s).
	 * 
	 * Ehemalig: "CONSUMER_SOURCE_NAME"
	 */
	MESSAGING_CONSUMER_SOURCE_NAME,

	/**
	 * Der Schlüssel des Properties-Datei-Eintrages für die server urls für
	 * den jms message consumer.
	 * 
	 * Ehemalig: "CONSUMER_SERVER_URLS"
	 */
	MESSAGING_CONSUMER_SERVER_URLS,
	
	/**
	 * Der Schlüssel des Properties-Datei-Eintrages für die client id des
	 * jms message producer(s).
	 */
	MESSAGING_PRODUCER_CLIENT_ID,

	/**
	 * Der Schlüssel des Properties-Datei-Eintrages für den destination name des
	 * jms message producer(s).
	 */
	MESSAGING_PRODUCER_DESTINATION_NAME,

	/**
	 * Der Schlüssel des Properties-Datei-Eintrages für die server urls für
	 * den jms message producer.
	 */
	MESSAGING_PRODUCER_SERVER_URLS
}