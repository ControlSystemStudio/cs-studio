package org.csstudio.platform.security;

/**
 * A {@code LoginModule} provides a particular type of authentication. 
 *
 * @author Anze Vodovnik, Jörg Rathlev
 */
public interface ILoginModule {
	
	/**
	 * Authenticates the user.
	 * 
	 * <p>The username and password used for the authentication are queried
	 * from the {@code handler} passed to this method.
	 * 
	 * @param handler The handler used by this {@code LoginModule} to get the
	 * username and password.
	 * @return A {@code User} object representing the user that was
	 * authenticated by this module, or {@code null} if the authentication
	 * failed.
	 * 
	 * @see ILoginCallbackHandler#getCredentials()
	 */
	public User login(ILoginCallbackHandler handler);
	
	/**
	 * Logs out the user.
	 */
	/* XXX: semantics are unclear if more than one user is authenticated.
	 * When implementing Kerberos authentication, check what interface is
	 * actually needed.
	 */
	public void logout();
}
