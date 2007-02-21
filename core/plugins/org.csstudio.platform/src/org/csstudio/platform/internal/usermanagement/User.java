/* 
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton, 
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
package org.csstudio.platform.internal.usermanagement;

import java.util.HashMap;
import java.util.Map;

/**
 * Defines a CSS system user.
 * 
 * @author Kai Meyer & Torsten Witte & Alexander Will & Sven Wende
 */
public final class User implements IUser {

	/**
	 * Holds all properties.
	 */
	private Map <String, Object> _propetyMap = new HashMap <String, Object> ();
	
	/**
	 * Constructor with parameters.
	 * 
	 * @param name
	 *            the name of this user
	 */
	protected User(final String name) {
		_propetyMap.put(IUser.USERNAME, name);
	}

	/**
	 * @see org.csstudio.platform.internal.usermanagement.IUser#addProperty(java.lang.String,java.lang.Object)
	 * @param key  with which the specified value is to be associated 
	 * @param value  to be associated with the specified key
	 */
	public void addProperty(final String key, final Object value) {
		_propetyMap.put(key, value);
	}

	/**
	 * @see org.csstudio.platform.internal.usermanagement.IUser#getName()
	 * @return  the name of the user
	 */
	public String getName() {
		return (String) _propetyMap.get(IUser.USERNAME);
	}

	/**
	 * @see org.csstudio.platform.internal.usermanagement.IUser#getProperty(java.lang.String)
	 * @param key key whose associated value is to be returned
	 * @return the value associated to the specified key or null if there is no match for the key
	 */
	public Object getProperty(final String key) {
		return _propetyMap.get(key);
	}
}
