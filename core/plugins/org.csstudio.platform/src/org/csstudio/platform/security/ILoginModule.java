package org.csstudio.platform.security;

/**
 * LoginModules provide a particular type of authentication. 
 *
 */
public interface ILoginModule {
	public User login(ILoginCallbackHandler handler);
	public void logout();
}
