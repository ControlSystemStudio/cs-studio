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
 package org.csstudio.platform.libs.dcf.messaging.internal;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.platform.libs.dcf.messaging.ConnectionManager;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.Platform;

/**
 * Enumerates all the avaiable protocol implementations
 * registered on the extension point.
 * 
 * @author avodovnik
 *
 */
public class ProtocolEnumerator {
	
	private static final String TAG_PROTOCOL = "protocolImplementation";
	
	private static ProtocolProxy[] cachedProtocols = null;
	
	/**
	 * Returns a cached array of protocol implementations
	 * avaiable by parsing the extension point.
	 * 
	 * @return Returns an array of protocols. If the cache does not
	 * yet exist, it is created by iterating through the extension point.
	 */
	public static ProtocolProxy[] getProtocols() {
		if(cachedProtocols != null)
			// return the cached items
			return cachedProtocols;
		
		// no cache, create
		// get extensions
		IExtension[] extensions = Platform.getExtensionRegistry()
			.getExtensionPoint("org.csstudio.platform.libs.dcf.protocol")
			.getExtensions();
		
		List<ProtocolProxy> found = new ArrayList<ProtocolProxy>();
		// define an array to hold config elements
		IConfigurationElement[] configElements;
		ProtocolProxy protocol;
		
		for(IExtension extension : extensions) {
			// load the config elements
			configElements = extension.getConfigurationElements();
			
			for(IConfigurationElement configElement : configElements) {
				// get the protocol proxy
				protocol = parseProtocol(configElement, found.size());
				
				if(protocol != null)
					found.add(protocol); // add the protocol
			}
		}
		
		// cache the providers
		cachedProtocols = found.toArray(new ProtocolProxy[found.size()]);
		
		return cachedProtocols;
	}

	private static ProtocolProxy parseProtocol(IConfigurationElement configElement, int i) {
		if(TAG_PROTOCOL.equals(configElement.getName())) {
			// get the proxy, and return it
			return new ProtocolProxy(configElement, i);
		}
		return null;
	}
	
	/**
	 * Gets the connection manager behing the protocol id. 
	 * @param id The identifiaction used by the protocol to register 
	 * itself with the extension point.
	 * @return Returns an instance of the connection manager implementation
	 * introduced by the protocol.
	 */
	public static ConnectionManager getProtocol(String id) {
		for(ProtocolProxy proxy : getProtocols()) {
			if(proxy.getId().equals(id))
				return proxy;
		}
		return null;
	}
}
