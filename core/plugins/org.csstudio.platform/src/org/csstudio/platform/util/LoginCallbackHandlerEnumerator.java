/**
 * 
 */
package org.csstudio.platform.util;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.platform.security.ILoginCallbackHandler;
import org.csstudio.platform.security.LoginCallbackHandlerProxy;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.Platform;

/**
 * This utility enumerates through any given extension point
 * to retrieve a list of callback handlers. The list is not
 * cached.
 * 
 * @author avodovnik
 *
 */
public class LoginCallbackHandlerEnumerator {
	/**
	 * Retrieves an uncached array of proxies from the extension
	 * point specified.
	 * @param extensionPointName The extension point identifier.
	 * @param configElementName The element name of the identifying
	 * element in the extension point schema.
	 * @return Returns a list of proxies.
	 * @throws IllegalArgumentException In case the extension point
	 * is not found or does not conform to the expected shchema.
	 */
	public static LoginCallbackHandlerProxy[] getProxies(
			String extensionPointName, String configElementName) throws IllegalArgumentException {
		try {
			// ok, get the extension
			IExtension[] extensions = Platform.getExtensionRegistry()
					.getExtensionPoint(extensionPointName)
					.getExtensions();
			
			List<LoginCallbackHandlerProxy> found = 
				new ArrayList<LoginCallbackHandlerProxy>();
			
			IConfigurationElement[] configElements;
			
			for(IExtension extension : extensions) {
				configElements = extension.getConfigurationElements();
				
				for(IConfigurationElement configElement : configElements) {
					if(configElementName.equals(configElement.getName())) {
						found.add(new LoginCallbackHandlerProxy(configElement));
					}
				}
			}
			return found.toArray(new LoginCallbackHandlerProxy[0]);
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}
	
	/**
	 * Retrieves a specifid login handler from the extension point.
	 * 
	 * @param extension The extension point identifier.
	 * @param elementName The element name f the identifying
	 * element in the extension point shcema.
	 * @param handlerId The handler id to be returned.
	 * @return Returns a login callback handler if found,
	 * null otherwise.
	 */
	public static ILoginCallbackHandler getLoginCallbackHandler(String extension, 
			String elementName, String handlerId) {
		for(LoginCallbackHandlerProxy proxy : getProxies(extension, elementName)) {
			if(proxy.getId().equals(handlerId))
				return proxy;
		}
		// no proxies matched
		return null;
	}
}
