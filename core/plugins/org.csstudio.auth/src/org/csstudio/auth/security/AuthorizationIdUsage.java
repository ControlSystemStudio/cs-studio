/*
 * Copyright (c) 2009 Stiftung Deutsches Elektronen-Synchrotron,
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
package org.csstudio.auth.security;

/**
 * Information about the usage of a registered authorization ID. Instances of
 * this class are immutable.
 * 
 * @author Joerg Rathlev
 */
public final class AuthorizationIdUsage {

	private final String _location;
	private final boolean _allowByDefault;
	private final String _plugin;

	/**
	 * Creates a new authorization ID usage description.
	 * 
	 * @param location
	 *            a description of the location where the ID is used. For
	 *            example, a description of the action which is associated with
	 *            the authorization ID.
	 * @param allowByDefault
	 *            <code>true</code> if usage of the associated action is allowed
	 *            by default, <code>false</code> otherwise.
	 * @param plugin
	 *            the name of the plug-in which contributed this usage
	 *            description.
	 */
	public AuthorizationIdUsage(String location, boolean allowByDefault,
			String plugin) {
		_location = location;
		_allowByDefault = allowByDefault;
		_plugin = plugin;
	}

	/**
	 * Returns a description of the location where the authorization ID is used.
	 * For example, a description of the action which is associated with the
	 * authorization ID.
	 * 
	 * @return a description of the location where the authorization ID is used.
	 */
	public String getLocation() {
		return _location;
	}

	/**
	 * Returns whether using the associated action is allowed by default.
	 * 
	 * @return <code>true</code> if using the associated action is allowed by
	 *         default, <code>false</code> otherwise.
	 */
	public boolean isAllowedByDefault() {
		return _allowByDefault;
	}
	
	/**
	 * Returns the name of the plug-in which contributed this usage description.
	 * 
	 * @return the name of the plug-in which contributed this usage description.
	 */
	public String getPlugIn() {
		return _plugin;
	}
}
