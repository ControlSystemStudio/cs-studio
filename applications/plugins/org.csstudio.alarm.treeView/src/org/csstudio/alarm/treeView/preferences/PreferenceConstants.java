package org.csstudio.alarm.treeView.preferences;

/**
 * Constant definitions for plug-in preferences
 */
public class PreferenceConstants {
	
	// prevent instanciation of this class
	private PreferenceConstants() {
	}

	/**
	 * A preference that stores the URL of the LDAP server to read from.
	 */
	public static final String LDAP_URL = "url";

	/**
	 * A preference that stores the username for the LDAP server.
	 */
	public static final String LDAP_USER = "user";

	/**
	 * A preference that stores the password for the LDAP server.
	 */
	public static final String LDAP_PASSWORD = "password";
	
	/**
	 * A preference that stores the URL for the primary JMS server.
	 */
	public static final String JMS_URL_PRIMARY = "jmsurl";
	
	/**
	 * A preference that stores the URL for the secondary JMS server.
	 */
	public static final String JMS_URL_SECONDARY = "jms.url.2";
	
	/**
	 * A preference that stores the class name of the context factory to use
	 * for the primary JMS server.
	 */
	public static final String JMS_CONTEXT_FACTORY_PRIMARY = "jms.contextfactory.1";

	/**
	 * A preference that stores the class name of the context factory to use
	 * for the secondary JMS server.
	 */
	public static final String JMS_CONTEXT_FACTORY_SECONDARY = "jms.contextfactory.2";
	
	/**
	 * A preference that stores the name or names of the JMS queue to connect
	 * to.
	 */
	public static final String JMS_QUEUE = "jms.queue";
	
	/**
	 * A preference that stores the facility names that should be displayed
	 * in the tree.
	 */
	public static final String FACILITIES = "NODE";

}
