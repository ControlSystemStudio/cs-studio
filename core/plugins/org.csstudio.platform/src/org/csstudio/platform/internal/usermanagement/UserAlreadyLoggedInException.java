package org.csstudio.platform.internal.usermanagement;

/**
 * @author Kai Meyer & Torsten Witte
 * This Exception indicates that a user is already logged in. 
 */
public class UserAlreadyLoggedInException extends Exception {
	
	/**
	 * The serial Version UID.
	 */
	private static final long serialVersionUID = -7697913430495671645L;

	/**
	 * Constructor.
	 * @param message the message of this Exception
	 */
	public UserAlreadyLoggedInException(final String message) {
		super(message);
	}

}
