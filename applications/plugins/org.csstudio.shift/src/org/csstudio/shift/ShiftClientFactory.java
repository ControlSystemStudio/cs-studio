package org.csstudio.shift;

import gov.bnl.shiftClient.ShiftClient;

public interface ShiftClientFactory {

	/** ID of the extension point for providing an ShiftClientFactory */
	final public static String EXTENSION_ID = "org.csstudio.shift.shiftclientfactory"; //$NON-NLS-1$

	/**
	 * Get an instance of the client object used to make shift entries
	 * 
	 * @return
	 * @throws Exception 
	 */
	ShiftClient getClient() throws Exception;

	/**
	 * Get an instance of the shiftClient with the usercredentails.
	 * 
	 * @param username
	 * @param password
	 * @return
	 * @throws Exception 
	 */
	ShiftClient getClient(final String username, final String password) throws Exception;

}
