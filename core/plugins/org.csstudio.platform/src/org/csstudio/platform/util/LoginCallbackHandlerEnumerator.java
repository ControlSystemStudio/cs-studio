/* 
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron, 
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. 
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED 
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND 
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE 
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, 
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE 
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR 
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. 
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, 
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION, 
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS 
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY 
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
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
