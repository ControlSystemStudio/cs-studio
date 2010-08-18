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
 package org.csstudio.platform.libs.dcf.actions.internal;

import org.csstudio.platform.libs.dcf.actions.IAction;
import org.eclipse.core.runtime.IConfigurationElement;

public abstract class ActionProxyBase implements IAction {
	
	protected String getAttribute(
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

	public abstract String getId();

	public abstract String getName();

	/**
	 * Gets the type of the action represented by the proxy,
	 * which matches the proxy type as that is the only way
	 * actions are distinguished. Subclasses must implement this
	 * method to return their class value.
	 * @return
	 */
	public abstract Class getType();
	
	public abstract boolean isVisible();
}
