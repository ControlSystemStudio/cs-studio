package org.csstudio.platform.security;

/**
 * Provides access to the rights of users and actions.
 * 
 * @author Joerg Rathlev
 */
public interface IAuthorizationProvider {

	/**
	 * Returns the rights of the given user.
	 * @param user the user.
	 */
	public abstract RightSet getRights(User user);
	
	/**
	 * Returns the rights associated with the action with the given id. 
	 * @param actionId the id of the action.
	 */
	public abstract RightSet getRights(String actionId);

}