package org.csstudio.logbook;

/**
 * A Factory to create clients to the Logbook service
 * 
 * @author shroffk
 * 
 */
public interface LogbookClientFactory {

	/** ID of the extension point for providing an LogbookClientFactory */
	final public static String EXTENSION_ID = "org.csstudio.logbook.logbookclientfactory"; //$NON-NLS-1$

	/**
	 * Get an instance of the client object used to make log entries
	 * 
	 * @return
	 * @throws Exception 
	 */
	public LogbookClient getClient() throws Exception;

	/**
	 * Get an instance of the logbookClient with the usercredentails.
	 * 
	 * @param username
	 * @param password
	 * @return
	 * @throws Exception 
	 */
	public LogbookClient getClient(String username, String password) throws Exception;

}
