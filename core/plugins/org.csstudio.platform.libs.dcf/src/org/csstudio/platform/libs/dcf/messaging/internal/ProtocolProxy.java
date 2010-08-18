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

import org.csstudio.platform.libs.dcf.directory.ContactElement;
import org.csstudio.platform.libs.dcf.directory.IDirectoryChangeListener;
import org.csstudio.platform.libs.dcf.messaging.ConnectionManager;
import org.csstudio.platform.libs.dcf.messaging.IMessageListener;
import org.csstudio.platform.libs.dcf.messaging.IMessageListenerFilter;
import org.csstudio.platform.libs.dcf.messaging.Message;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;

/**
 * Represents a connection manager.
 * 
 * @author Anze Vodovnik
 */
public class ProtocolProxy extends ConnectionManager {

	private static final String ATT_CLASS = "class";
	private static final String ATT_ID = "id";
	private static final String ATT_NAME = "name";
	
	private final IConfigurationElement _configElement;
	private ConnectionManager protocol;
	private final String id;
	private final String name;
	
	public ProtocolProxy(IConfigurationElement configElement, int i) {
		this._configElement = configElement;
		// ensure we have the class
		getAttribute(configElement, ATT_CLASS, null);
		id = getAttribute(configElement, ATT_ID, null);
		name = getAttribute(configElement, ATT_NAME, id);
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
	
	public String getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}
	
	@Override
	public void addMessageListener(IMessageListener listener, IMessageListenerFilter filter) {
		getProtocol().addMessageListener(listener, filter);
	}

	@Override
	public void removeMessageListener(IMessageListener listener) {
		getProtocol().removeMessageListener(listener);
	}

	@Override
	public Throwable sendMessage(Message message) {
		return getProtocol().sendMessage(message);
	}
	
	private ConnectionManager getProtocol() {
		try {
			if(protocol == null)
				protocol = (ConnectionManager)this._configElement
					.createExecutableExtension(ATT_CLASS);
		} catch (CoreException e){
			e.printStackTrace();
			protocol = null;
		}
		return protocol;
	}

	@Override
	public void initManager() {
		getProtocol().initManager();
	}

	@Override
	public ContactElement[] getDirectory() {
		return getProtocol().getDirectory();
	}
	
	@Override
	public void addDirectoryChangeListener(IDirectoryChangeListener listener) {
		getProtocol().addDirectoryChangeListener(listener);
	}
	
	@Override
	public void removeDirectoryChangeListener(IDirectoryChangeListener listener) {
		getProtocol().removeDirectoryChangeListener(listener);
	}

}
