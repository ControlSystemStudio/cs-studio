package org.csstudio.platform.security;

/**
 * Retrieves the username and password that will be used to authenticate
 * the user. Implementations of this interface are used by login modules.
 * 
 * <p>Implementations of this interface will usually display a login prompt
 * to the user (for example, a graphical login dialog), but this is not a
 * strict requirement.
 * 
 * @see ILoginModule
 * 
 * @author Anze Vodovnik, Jörg Rathlev
 */
public interface ILoginCallbackHandler {
	
	/**
	 * Returns the username and password that will be used to authenticate
	 * the user. Returns <code>null</code> if username and password could not
	 * be retrieved.
	 * 
	 * <p>Implementations of this method may prompt the user for username and
	 * password, for example by displaying a login dialog. Such implementations
	 * have to wait for user input before returning. Login modules calling this
	 * method therefore should not assume that it will return quickly.
	 * 
	 * <p>The username and password are returned in a {@link Credentials}
	 * object. Implementations must not keep references to this object, and
	 * callers should discard all references to the returned object as early as
	 * possible to prevent passwords from being kept in memory unnecessarily.
	 * 
	 * @return the username and password, or <code>null</code> if username and
	 *         password could not be retrieved.
	 */
	Credentials getCredentials();
	
	/**
	 * Informs this callback handler that the previous login attempt has failed.
	 * If this callback handler is interactive, it will display an error
	 * message to the user.
	 * 
	 * <p>If the login failed due to a wrong username/password, clients
	 * <em>must</em> call this method before calling {@link getCredentials}
	 * again, otherwise <code>getCredentials</code> may return the same
	 * credentials as from the previous call.
	 */
	void signalFailedLoginAttempt();
}
