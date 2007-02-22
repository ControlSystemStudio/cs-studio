package org.csstudio.platform.security;

/**
 * Represents the user's credential, like the
 * username and password.
 *
 */
public final class Credentials {
	private String _username;
	private String _password;
	
	public Credentials(String username, String password) {
		this._username = username;
		this._password = password;
	}
	
	public String getUsername() {
		return _username;
	}
	
	public String getPassword() {
		return _password;
	}
	
}
