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
package org.csstudio.auth.security;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.auth.internal.AuthActivator;
import org.csstudio.auth.internal.rightsmanagement.RightsManagementService;
import org.csstudio.auth.internal.subnet.CSSPlatformInfo;
import org.csstudio.auth.internal.usermanagement.IUserManagementListener;
import org.csstudio.auth.internal.usermanagement.LoginContext;
import org.csstudio.auth.internal.usermanagement.UserManagementEvent;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;

/**
 * This Service executes instances of
 * AbstractExecuteable if the current user has the permission to
 * run them. It also provides some methods to ask whether the current user has
 * the permission to access the objects behind an identifier. Offers methods to
 * get the current instance of the IRights- and IUserManagement.
 * 
 * Allows registration of <code>IUserManagementListener</code> to be notified
 * when the currently logged in user changes.
 * That listener list uses weak references to allow for example actions from
 * dynamically created context menues to register. The MenuManager can then
 * simply garbage collect the menu actions. Since the actions have no way
 * to know when they should remove themselves as IUserManagementListener,
 * the weak references allow them to be garbage collected.
 * 
 * @author Kai Meyer & Torsten Witte & Alexander Will & Sven Wende
 * & Jörg Rathlev & Anze Vodovnik & Xihui Chen
 * @author Kay Kasemir Weak Reference handling
 */
@SuppressWarnings("nls")
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

	final private ArrayList<WeakReference<IUserManagementListener>> _userListeners =
	    new ArrayList<WeakReference<IUserManagementListener>>();

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
     * System property that stores where the last login User.
     */
    public static final String LOGIN_LAST_USER_NAME=
        "org.csstudio.platform.lastUserName";

	/**
	 * ID of the {@code authorizationId} extension point.
	 */
	private static final String AUTHORIZATION_ID_EXTENSION_POINT = "org.csstudio.auth.authorizationId";

	private static final Logger log = Logger.getLogger(SecurityFacade.class.getName());
	
	/**
	 * Private constructor due to singleton pattern.
	 */
	private SecurityFacade() {
		_listeners = new ArrayList<ISecurityListener>();
		_context = new LoginContext("PrimaryLoginContext");
		
		// Set the "loginAvailable" system property. This is used by the UI to
		// enable/disable the "Switch User" menu item. Currently (Eclipse 3.2)
		// there isn't really a better way to enable/disable a menu item based
		// on global application state.
		boolean loginAvailable = _context.isLoginAvailable();
		System.setProperty(LOGIN_AVAILABLE_PROPERTY,
				String.valueOf(loginAvailable));
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
		return prefs.getBoolean(AuthActivator.ID, key, false, null);
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
	 * get the currently registered LoginCallbackHandler
	 * @return the handler.
	 */
	public ILoginCallbackHandler getRegisteredLoginCallbackHandler() {
		return _loginCallbackHandler;
	}

	/**
	 * Checks if the current user has the permission referenced by the given ID.
	 * This method will return <code>false</code> if no rights are configured
	 * for the given action, i.e. by default, the action can be executed by all
	 * non-anonymous users.
	 * 
	 * @param id
	 *            The ID of the right to check
	 * @return <code>true</code> if the user has the permission;
	 *         <code>false</code> otherwise
	 */
	public boolean canExecute(final String id) {
		return canExecute(id, _context, false);
	}
	
	/**
	 * Checks if the current user has the permission referenced by the given ID.
	 * 
	 * @param id
	 *            The ID of the right to check
	 * @param defaultPermission
	 *            this value will be returned if no rights are configured for
	 *            the given action. Note that if you pass <code>false</code>
	 *            here, the action will be disabled if no authorization plug-in
	 *            is loaded.
	 * @return <code>true</code> if the user has the permission;
	 *         <code>false</code> otherwise
	 */
	public boolean canExecute(final String id, final boolean defaultPermission) {
		return canExecute(id, _context, defaultPermission);
	}
	
	/**
	 * Checks if the current user has the permission referenced by the given ID
	 * based on the given login context.
	 * 
	 * @param id
	 *            The ID of the right to check.
	 * @param context
	 *            The login context under which the right is needed.
	 * @param defaultPermission
	 *            this value will be returned if no rights are configured for
	 *            the given action. Note that if you pass <code>false</code>
	 *            here, the action will be disabled if no authorization plug-in
	 *            is loaded.
	 * @return Returns <code>true</code> if the user has permission and
	 *         <code>false</code> otherwise.
	 */
	public boolean canExecute(final String id, final LoginContext context,
			final boolean defaultPermission) {
		return RightsManagementService.getInstance().hasRights(
				context.getUser(), id, defaultPermission);
	}

	/**
	 * Returns the currently logged in user.
	 * 
	 * @return the currently logged in user. Returns <code>null</code> if no
	 *         user is currently logged in.
	 */
	public User getCurrentUser() {
		return _context.getUser();
	}
	
	/**
	 * Performs the login procedure.
	 * @param handler
	 * 			The {@link ILoginCallbackHandler} for the login
	 */
	private void login(final ILoginCallbackHandler handler)
	{
		this._context.login(handler);

		// Inform all listeners about changed user
        final UserManagementEvent event = new UserManagementEvent();
		int i=0;
		while (i < _userListeners.size())
		{
		    final IUserManagementListener uml = _userListeners.get(i).get();
		    if (uml == null)
		    {
		        // Listener was garbage collected
		        _userListeners.remove(i);
		    }
		    else
		    {   // Valid entry: Invoke listener, move to next
    		    try
    		    {
                    uml.handleUserManagementEvent(event);
    			}
    		    catch (RuntimeException e)
    		    {
    	        	log.log(Level.WARNING, "User management event listener threw unexpected RuntimeException", e);
    			}
    		    ++i;
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

	public void addUserManagementListener(final IUserManagementListener listener)
	{
		_userListeners.add(new WeakReference<IUserManagementListener>(listener));
	}
	
	public void removeUserManagementListener(final IUserManagementListener listener)
	{
	    int i=0;
	    while (i<_userListeners.size())
	    {
	        final IUserManagementListener uml = _userListeners.get(i).get();
	        if (uml == null)
	        {   // Listener was garbage collected, remove from list
	            _userListeners.remove(i);
	        }
	        else
	        {
	            if (uml == listener)
	            {   // Found it; remove; done
	                _userListeners.remove(i);
	                return;
	            }
	            // Keep looking
	            ++i;
	        }
	    }
	}

	/**
	 * Returns an unmodifiable collection of authorization IDs that are
	 * registered with the {@code authorizationId} extension point. If the same
	 * authorization ID is described by multiple extensions, only one
	 * description for that ID will be returned. The description for that ID
	 * will contain the usage declarations from all plug-ins declaring usages
	 * for that ID, and the description text for the ID itself from one of the
	 * plug-ins.
	 * 
	 * @return an unmodifiable collection of registered authorization IDs.
	 */
	public Collection<RegisteredAuthorizationId> getRegisteredAuthorizationIds() {
		Map<String, RegisteredAuthorizationId> authIds =
			new HashMap<String, RegisteredAuthorizationId>();
		IExtensionRegistry extensionRegistry = Platform.getExtensionRegistry();
		IExtension[] extensions = extensionRegistry
				.getExtensionPoint(AUTHORIZATION_ID_EXTENSION_POINT)
				.getExtensions();
		for (IExtension extension : extensions) {
			loadRegisteredAuthorizationIdExtension(extension, authIds);
		}
		
		return Collections.unmodifiableCollection(authIds.values());
	}

	/**
	 * Loads the registered authorization IDs from the specified extension.
	 * 
	 * @param extension
	 *            the extension.
	 * @param authIds
	 *            the map to which the registered IDs found in the extension
	 *            will be added.
	 */
	private void loadRegisteredAuthorizationIdExtension(IExtension extension,
			Map<String, RegisteredAuthorizationId> authIds) {
	    String contributor = extension.getContributor().getName();
	    
		IConfigurationElement[] elements = extension.getConfigurationElements();
		for (IConfigurationElement element : elements) {
			if (contributesAuthorizationId(element)) {
				loadRegisteredAuthorizationIdElement(element, contributor, authIds);
			}
		}
	}

	/**
	 * Determines whether a configuration element contributes a registered
	 * authorization ID.
	 * 
	 * @param element
	 *            the configuration element.
	 * @return <code>true</code> if the elment contributes an authorization ID,
	 *         <code>false</code> otherwise.
	 */
	private boolean contributesAuthorizationId(IConfigurationElement element) {
		return "id".equals(element.getName());
	}

	/**
	 * Loads a registered authorization ID from the specified configuration
	 * element.
	 * 
	 * @param element
	 *            the configuration element.
	 * @param authIds
	 *            the map to which the registered IDs found in the extension
	 *            will be added.
	 */
	private void loadRegisteredAuthorizationIdElement(
			IConfigurationElement element, String contributor,
			Map<String, RegisteredAuthorizationId> authIds) {
		String id = element.getAttribute("id");
		String description = element.getAttribute("description");
		List<AuthorizationIdUsage> usages = loadAuthorizationIdUsages(element);
		
		// If the map already contains an existing description, we
		// need to merge the existing description with the new one.
		RegisteredAuthorizationId existing = authIds.get(id);
		if (existing != null) {
			Collection<AuthorizationIdUsage> existingUsages =
				existing.getUsage();
			usages.addAll(existingUsages);
		}
		
		// Finally, create the new description.
		RegisteredAuthorizationId authId =
			new RegisteredAuthorizationId(id, description, contributor, usages);
		authIds.put(id, authId);
	}

	/**
	 * Loads the usages of the authorization ID from a configuration element.
	 * 
	 * @param element
	 *            the configuration element.
	 * @return a list of authorization ID usages. If the configuration element
	 *         does not contain any usage descriptions, the returned list will
	 *         be empty.
	 */
    private List<AuthorizationIdUsage> loadAuthorizationIdUsages(
			IConfigurationElement element) {
		IConfigurationElement[] usageElements = element.getChildren("usage");
		List<AuthorizationIdUsage> usages = new ArrayList<AuthorizationIdUsage>(usageElements.length);
		for (IConfigurationElement usageElement : usageElements) {
			String location = usageElement.getAttribute("location");
			boolean allowByDefault = "true".equals(usageElement.getAttribute("default"));
			String plugin = usageElement.getContributor().getName();
			usages.add(new AuthorizationIdUsage(location, allowByDefault, plugin));
		}
		return usages;
	}

}
