package org.csstudio.startup;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.Platform;

/**
 * Enumerates the startup services connected to the
 * extension point provided by this plug-in.
 * 
 * @author avodovnik
 *
 */
public class StartupServiceEnumerator {
	private static final String TAG_SERVICE = "startupService";
	private static ServiceProxy[] cachedServices = null;
	
	/**
	 * Retruns the actions by parsing the extension point.
	 * 
	 * @return An array of actions.
	 */
	public static ServiceProxy[] getServices() {
		// see if the items are cached
		if (cachedServices != null)
			// returned the items already cached
			return cachedServices;

		// ok, get the extension
		IExtension[] extensions = Platform.getExtensionRegistry()
				.getExtensionPoint("org.csstudio.startup.startupListener")
				.getExtensions();

		// define an array
		List<ServiceProxy> found = new ArrayList<ServiceProxy>();
		// define an array to hold the configuration elements
		IConfigurationElement[] configElements;
		// define a variable to hold the parsed services
		ServiceProxy service;

		for (IExtension extension : extensions) {
			// load the config elements
			configElements = extension.getConfigurationElements();

			for (IConfigurationElement configElement : configElements) {
				// get the action proxy
				service = parseService(configElement, found
						.size());
				if (service != null)
					// add the action
					found.add(service);
			}
		}

		// cache the providers
		cachedServices = found.toArray(new ServiceProxy[found
				.size()]);

		// return the cached providers
		return cachedServices;
	}

	private static ServiceProxy parseService(IConfigurationElement configElement, int i) {
		if(TAG_SERVICE.equals(configElement.getName())) {
			// get the proxy
			return new ServiceProxy(configElement, i);
		}
		// return null by default
		return null;
	}
}
