package org.csstudio.platform.security;

/**
 * Objects of this class are used to pass a user's username and password
 * from an {@link ILoginCallbackHandler} to a {@link ILoginModule}.
 * References to {@code Credentials} objects should be discarded as early
 * as possible to prevent passwords from being kept in memory
 * unnecessarily.
 *
 * @author Anze Vodovnik, Jörg Rathlev
 */
public final class Credentials {

	/**
	 * The username.
	 */
	private String _username;
	
	/**
	 * The password.
	 */
	private String _password;
	
	/**
	 * Creates a new {@code Credentials} object.
	 * 
	 * @param username the username.
	 * @param password the password.
	 */
	public Credentials(String username, String password) {
		this._username = username;
		this._password = password;
	}
	
	/**
	 * Returns the username.
	 * @return the username.
	 */
	public String getUsername() {
		return _username;
	}
	
	/**
	 * Returns the password.
	 * @return the password.
	 */
	public String getPassword() {
		return _password;
	}
}
