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
package org.csstudio.auth.internal.rightsmanagement;

import java.util.HashMap;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.auth.security.IAuthorizationProvider;
import org.csstudio.auth.security.IRight;
import org.csstudio.auth.security.RightSet;
import org.csstudio.auth.security.User;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.Platform;

/**
 * The <code>RightsManagementService</code> provides the central CSS core
 * functionalities for the management of <code>Rights</code>.
 * 
 * @author Kai Meyer & Torsten Witte & Alexander Will & Sven Wende & Joerg Rathlev
 * @author Kay Kasemir Removed unused listener list
 */
@SuppressWarnings("nls")
public final class RightsManagementService {

	/**
	 * The only one instance of the <code>RightsManagementService</code>.
	 */
	private static RightsManagementService _instance = null;

	/**
	 * Stores the rights of the users.
	 */
	private Map<User, RightSet> _rights = new HashMap<User, RightSet>();
	
	/**
	 * The authorization provider.
	 */
	private IAuthorizationProvider _authProvider = null;

	private static final Logger log = Logger.getLogger(RightsManagementService.class.getName());

	/**
	 * Private constructor due to singleton pattern.
	 */
	private RightsManagementService() {
	}

	/**
	 * Return the only one instance of the <code>RightsManagementService</code>.
	 * 
	 * @return The only one instance of the <code>RightsManagementService</code>.
	 */
	public static RightsManagementService getInstance() {
		if (_instance == null) {
			_instance = new RightsManagementService();
		}
		return _instance;
	}

	/**
	 * Checks if the given user has the rights to perform the action with the
	 * given id. This method will return <code>false</code> if no rights are
	 * configured for the given action, i.e. by default, all actions can be
	 * executed by all non-anonymous users.
	 * 
	 * @param user
	 *            the user.
	 * @param id
	 *            the action id.
	 * @return <code>true</code> if the user is permitted to perform the
	 *         action, <code>false</code> otherwise.
	 */
	public boolean hasRights(final User user, final String id) {
		return hasRights(user, id, false);
	}
	
	/**
	 * Checks if the given user has the rights to perform the action with the
	 * given id.
	 * 
	 * @param user
	 *            the user.
	 * @param id
	 *            the action id.
	 * @param defaultPermission
	 *            this value will be returned if no rights are configured for
	 *            the given action. Note that if you pass <code>false</code>
	 *            here, the action will be disabled if no authorization plug-in
	 *            is loaded.
	 * @return <code>true</code> if the user is permitted to perform the
	 *         action, <code>false</code> otherwise.
	 */
	public boolean hasRights(final User user, final String id,
			final boolean defaultPermission) {
		RightSet userRights = _rights.get(user);
		RightSet actionRights = getRightsForAction(id);
		if (user == null  ||  actionRights.isEmpty()) {
			// If no rights are configured for the action, the default
			// permission passed by the caller is used.
			return defaultPermission;
		} else {
			if (userRights == null) {
				// Rights are configured for the action, but the user doesn't
				// have any rights. This happens for example when the CSS is
				// used anonymously. Executing the action is not permitted in
				// this case.
				return false;
			} else {
				// Rights are configured for the action, and the user is
				// authenticated. Check if the user has a right that permits
				// executing the action. The rights configured for the action
				// are interpreted as independently permitting its execution,
				// i.e. the user needs at least one of those rights.
				for (IRight r : actionRights) {
					if (userRights.hasRight(r)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * Returns the rights for the action with the given id.
	 * @param id the id of the action.
	 */
    private RightSet getRightsForAction(String id) {
		IAuthorizationProvider provider = getAuthorizationProvider();
		if (provider != null) {
			RightSet result = provider.getRights(id);
			// protect against providers that return null
			if (result == null) {
				result = new RightSet("EMPTY");
			}
			return result;
		} else {
			return new RightSet("EMPTY");
		}
	}

	/**
	 * Attach a rights management listener to the
	 * <code>RightsManagementService</code>.
	 * 
	 * @param listener
	 *            The listener.
	 * @deprecated Nobody ever invokes the listener, so why would you want to register?
	 */
	@Deprecated
	public void addRightsManagementListener(
			final IRightsManagementListener listener) {
        // NOP
	}

	/**
	 * Detach a rights management listener from the
	 * <code>RightsManagementService</code>.
	 * 
	 * @param listener
	 *            The listener.
     * @deprecated Nobody ever invokes the listener, so why would you want to register?
     */
    @Deprecated
	public void removeListener(final IRightsManagementListener listener) {
        // NOP
	}

	/**
	 * Reads the rights of the given user.
	 * @param user the user.
	 */
    public void readRightsForUser(final User user) {
		IAuthorizationProvider provider = getAuthorizationProvider();
		if (provider != null) {
			RightSet userRights = provider.getRights(user);
        	log.log(Level.FINE, "Rights for " + user + ": " + userRights);
			_rights.put(user, userRights);
		} else {
			if (_rights.containsKey(user)) {
				_rights.remove(user);
			}
		}
	}

	/**
	 * Returns the authorization provider.
	 */
	public IAuthorizationProvider getAuthorizationProvider() {
		if (_authProvider == null) {
			_authProvider = loadAuthorizationProviderExtension(); 
		}
		return _authProvider;
	}

	/**
	 * Loads the authorization provider extension.
	 */
	private IAuthorizationProvider loadAuthorizationProviderExtension() {
		IExtension[] extensions = Platform.getExtensionRegistry()
			.getExtensionPoint("org.csstudio.auth.authorizationProvider")
			.getExtensions();
		for (IExtension ext : extensions) {
			IConfigurationElement[] elements = ext.getConfigurationElements();
			for (IConfigurationElement element : elements) {
				try {
					return (IAuthorizationProvider) element.createExecutableExtension("class");
				} catch (CoreException e) {
		        	log.log(Level.SEVERE, "Could not create extension", e);
				}
			}
		}
		return null;
	}

}
