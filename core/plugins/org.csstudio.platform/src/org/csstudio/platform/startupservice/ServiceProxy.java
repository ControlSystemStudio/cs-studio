package org.csstudio.platform.startupservice;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;

public class ServiceProxy implements IStartupServiceListener{

	private static final String ATT_CLASS = "class";
	private static final String ATT_ID = "id";
	private static final String ATT_NAME = "name";
	private static final String ATT_PRIORITY = "isHighPriority";
	
	private final IConfigurationElement _configElement;
	private IStartupServiceListener _serviceListener;
	private final String _id;
	private final String _name;
	private final boolean _isHighPriority;
	
	public ServiceProxy(IConfigurationElement configElement, int orindal) {
		this._configElement = configElement;
		// ensure that the attribute is there
		getAttribute(configElement, ATT_CLASS, null);
		_id = getAttribute(configElement, ATT_ID, null);
		_name = getAttribute(configElement, ATT_NAME, _id);
		_isHighPriority = Boolean.parseBoolean(getAttribute(configElement, ATT_PRIORITY, "false"));
	}
	
	private String getAttribute(
			IConfigurationElement configElem,
			String name,
			String defaultValue) {
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
	
	public void run() {
		if(_serviceListener == null)
			getServiceListener();
		// if there was an error getting the action,
		// there will be a null pointer exception here
		// TODO: think about how to avoid this exception
		_serviceListener.run();
	}

	private void getServiceListener() {
		try {
			this._serviceListener =
				(IStartupServiceListener) 
				this._configElement.createExecutableExtension(ATT_CLASS);
		} catch (CoreException e) {
			e.printStackTrace();
			_serviceListener = null;
		}
	}
	
	public String getId() {
		return this._id;
	}
	
	public String getName() {
		return this._name;
	}
	
	public boolean isHighPriority() {
		return this._isHighPriority;
	}

}
