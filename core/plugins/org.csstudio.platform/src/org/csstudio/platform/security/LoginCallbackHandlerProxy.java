/**
 * 
 */
package org.csstudio.platform.security;

import org.eclipse.core.runtime.IConfigurationElement;

/**
 * Represents the Login Callback Proxy for any login callback 
 * handlers that are enumerated through the enumerator located
 * in the utility class.
 * 
 * @author avodovnik
 *
 */
public class LoginCallbackHandlerProxy implements ILoginCallbackHandler {

	private static final String ATT_CLASS = "class";
	private static final String ATT_ID = "id";
	private static final String ATT_NAME = "name";
	
	private IConfigurationElement _configElement;
	private ILoginCallbackHandler _lch;
	
	private String _id;
	private String _name;
	public LoginCallbackHandlerProxy(IConfigurationElement configElement) throws IllegalArgumentException{
		_configElement = configElement;
		
		// ensure the availabiliy of the attribute
		getAttribute(configElement, ATT_CLASS, null);
		_id = getAttribute(configElement, ATT_ID, null);
		_name = getAttribute(configElement, ATT_NAME, _id);
	}
	
	private String getAttribute(
			IConfigurationElement configElem,
			String name,
			String defaultValue) throws IllegalArgumentException {
		// get the value from the configuration element
		String value = configElem.getAttribute(name);
		// is value not null
		if(value != null)
			// ok, return that value
			return value;
		// it was null, do we have a default value?
		if(defaultValue != null)
			// return the default value
			return defaultValue;
		// we don't have any possible values, throw an exception
		throw new IllegalArgumentException("Missing " + name + " attribute!");
	}
	
	/* (non-Javadoc)
	 * @see org.csstudio.platform.security.ILoginCallbackHandler#getCredentials()
	 */
	public Credentials getCredentials() {
		ILoginCallbackHandler lch = getHandler();
		if(lch != null)
			return lch.getCredentials();
		return null;
	}
	
	public String getId() {
		return _id;
	}
	
	public String getName() {
		return _name;
	}
	
	private ILoginCallbackHandler getHandler() {
		try {
			if(_lch == null)
				_lch = (ILoginCallbackHandler) this._configElement
					.createExecutableExtension(ATT_CLASS);
			

			return _lch;			
		} catch (Exception e){
			e.printStackTrace();
			return null;
		}
	}

	public void signalFailedLoginAttempt() {
		_lch.signalFailedLoginAttempt();
	}

}
