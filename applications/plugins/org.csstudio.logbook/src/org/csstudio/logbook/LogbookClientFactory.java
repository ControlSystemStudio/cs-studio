package org.csstudio.logbook;

/**
 * @author shroffk
 * 
 */
public interface LogbookClientFactory {
	
	/** ID of the extension point for providing an LogbookClientFactory */
    final public static String EXTENSION_ID =
        "org.csstudio.logbook.logbookclientfactory"; //$NON-NLS-1$
    
	/**
	 * @return
	 */
	public LogbookClient getClient();
	
	/**
	 * @param username
	 * @param password
	 * @return
	 */
	public LogbookClient getClient(String username, String password);

}
