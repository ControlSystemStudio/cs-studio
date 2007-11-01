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
package org.csstudio.platform.security;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.platform.CSSPlatformInfo;
import org.csstudio.platform.CSSPlatformPlugin;
import org.csstudio.platform.internal.rightsmanagement.RightsManagementService;
import org.csstudio.platform.internal.usermanagement.IUserManagementListener;
import org.csstudio.platform.internal.usermanagement.LoginContext;
import org.csstudio.platform.internal.usermanagement.UserManagementEvent;
import org.csstudio.platform.logging.CentralLogger;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.core.runtime.preferences.InstanceScope;

/**
 * This Service executes instances of
 * AbstractExecuteable if the current user has the permission to
 * run them. It also provides some methods to ask whether the current user has
 * the permission to access the objects behind an identifier. Offers methods to
 * get the current instance of the IRights- and IUserManagement.
 * 
 * @author Kai Meyer & Torsten Witte & Alexander Will & Sven Wende
 * & Jörg Rathlev & Anze Vodovnik
 */
public final class SecurityFacade {

	/**
	 * This holds the current LoginContext.
	 */
	private LoginContext _context;
	
	/**
	 * The only one instance of this class.
	 */
	private static SecurityFacade _instance;

	/**
	 * The listeners.
	 */
	private List<ISecurityListener> _listeners;

	private ArrayList<IUserManagementListener> _userListeners;

	/**
	 * The interactive callback handler to use for the application login.
	 * <code>null</code> if no user-interactive handler was set up (e.g. if
	 * the application is not used in UI mode). 
	 */
	private ILoginCallbackHandler _loginCallbackHandler;

	/**
	 * Preference key for the preference that stores whether the user should
	 * be authenticated when starting in <em>onsite</em> mode.
	 */
	public static final String ONSITE_LOGIN_PREFERECE = "auth_login"; //$NON-NLS-1$
	
	/**
	 * Preference key for the preference that stores whether the user should
	 * be authenticated when starting in <em>offsite</em> mode.
	 */
	public static final String OFFSITE_LOGIN_PREFERENCE = "offsite_login"; //$NON-NLS-1$

	/**
	 * System property that stores whether login is available.
	 */
	private static final String LOGIN_AVAILABLE_PROPERTY =
		"org.csstudio.platform.loginAvailable";

	/**
	 * Private constructor due to singleton pattern.
	 */
	private SecurityFacade() {
		_listeners = new ArrayList<ISecurityListener>();
		_userListeners = new ArrayList<IUserManagementListener>();
		_context = new LoginContext("PrimaryLoginContext");
		
		// Set the "loginAvailable" system property. This is used by the UI to
		// enable/disable the "Switch User" menu item. Currently (Eclipse 3.2)
		// there isn't really a better way to enable/disable a menu item based
		// on global application state.
		System.setProperty(LOGIN_AVAILABLE_PROPERTY,
				String.valueOf(_context.isLoginAvailable()));
	}

	/**
	 * @return The singleton instance of this class.
	 */
	public static synchronized SecurityFacade getInstance() {
		if (_instance == null) {
			_instance = new SecurityFacade();
		}

		return _instance;
	}
	
	/**
	 * Authenticates the user of the Control System Studio application. This
	 * will use the first available login module. If no login modules are
	 * available, no authentication takes place and the application will be
	 * used anonymously.
	 * 
	 * <p>This method will usually be called during system startup.
	 */
	public void authenticateApplicationUser() {
		ILoginCallbackHandler callbackHandler = getLoginCallbackHandler();
		login(callbackHandler);
	}
	
	/**
	 * Returns whether automatic login on startup is enabled.
	 * @return <code>true</code> if login on startup is enabled,
	 *         <code>false</code> otherwise.
	 */
	public boolean isLoginOnStartupEnabled() {
		IPreferencesService prefs = Platform.getPreferencesService();
		String key = CSSPlatformInfo.getInstance().isOnsite()
					? ONSITE_LOGIN_PREFERECE
					: OFFSITE_LOGIN_PREFERENCE;
		return prefs.getBoolean(CSSPlatformPlugin.ID, key, false, null);
	}

	/**
	 * Creates and returns a login callback handler for the primary
	 * (application) login.
	 * 
	 * @return an <code>ILoginCallbackHandler</code> for the application login.
	 */
	private ILoginCallbackHandler getLoginCallbackHandler() {
		// if a login handler was explicitly set up, return that handler,
		// otherwise return a handler that returns null for anonymous login
		if (_loginCallbackHandler != null) {
			return _loginCallbackHandler;
		} else {
			return new ILoginCallbackHandler() {
				public Credentials getCredentials() {
					return null;
				}
				public void signalFailedLoginAttempt() {
				}
			};
		}
	}
	
	/**
	 * Sets a callback handler to use during application login.
	 * @param handler the handler.
	 */
	public void setLoginCallbackHandler(final ILoginCallbackHandler handler) {
		_loginCallbackHandler = handler;
	}

	/**
	 * Checks if the current user has the permission referenced by the 
	 * given ID.
	 * 
	 * @param id
	 *            The ID of the right to check
	 * @return True if the user has the permission; false otherwise
	 */
	public boolean canExecute(final String id) {
		return canExecute(id, _context);
	}
	
	/**
	 * Checks if the current user has the permission referenced by the
	 * given ID based on the given login context.
	 * @param id The ID of the right to check.
	 * @param context The login context under which the right is needed.
	 * @return Returns true if the user has permission and false otherwise.
	 */
	public boolean canExecute(final String id, final LoginContext context) {
		return RightsManagementService.getInstance().hasRights(
				context.getUser(), id);
	}

	public User getCurrentUser() {
		return _context.getUser();
	}
	
	/**
	 * Performs the login procedure.
	 * @param handler
	 * 			The {@link ILoginCallbackHandler} for the login
	 */
	private void login(ILoginCallbackHandler handler) {
		this._context.login(handler);
		for (IUserManagementListener uml : _userListeners) {
			try {
				uml.handleUserManagementEvent(new UserManagementEvent());
			} catch (RuntimeException e) {
				CentralLogger.getInstance().warn(this,
						"User management event listener threw unexpected RuntimeException", e);
			}
		}
	}
	
	/**
	 * Adds the given listener to the internal list.
	 * 
	 * @param listener
	 *            The ISecurityListener, which sould be added
	 */
	public void addListener(
			final ISecurityListener listener) {
		_listeners.add(listener);
	}

	/**
	 * Deletes the given ISecurityListener from the internal list.
	 * 
	 * @param listener
	 *            The ISecurityListener, which should be deleted
	 */
	public void removeListener(final ISecurityListener listener) {
		_listeners.remove(listener);
	}

	/**
	 * Informs all registered ISecureContainerListeners.
	 * 
	 * @param event
	 *            The event that the listeners are interested in.
	 */
	private void notifyListener(final SecurityEvent event) {
		for (ISecurityListener listener : _listeners) {
			listener.handleSecurityEvent(event);
		}
	}

	public void addUserManagementListener(IUserManagementListener listener) {
		_userListeners.add(listener);
	}
	
	public void removeUserManagementListener(IUserManagementListener listener) {
		_userListeners.remove(listener);
	}

}
