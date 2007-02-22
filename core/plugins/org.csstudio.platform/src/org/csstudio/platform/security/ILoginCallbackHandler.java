package org.csstudio.platform.security;

/**
 * Implementations of this will be called at startup
 * to retrieve the credentials of the entity running 
 * this instance and forward them to the chosen Login
 * Module.
 *
 */
public interface ILoginCallbackHandler {
	/**
	 * Uses one of the provided login modules to
	 * perform the login process.
	 * @param modules A list of all the login modules registered
	 * to the system.
	 */
	public Credentials getCredentials();
}
