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

import java.util.ArrayList;
import java.util.List;

import org.csstudio.platform.logging.CentralLogger;

/**
 * The <code>UserManagementService</code> provides the central CSS core
 * functionalities for the management of <code>Users</code>. 
 * 
 * @author Kai Meyer & Torsten Witte & Alexander Will & Sven Wende
 */
public final class UserManagementService {
	/**
	 * The user that is currently logged into the running CSS instance.
	 */
	private static IUser _user;

	/**
	 * Receivers of user management events..
	 */
	private final List<IUserManagementListener> _listeners = new ArrayList<IUserManagementListener>();

	/**
	 * The only one instance of the <code>UserManagementService</code>.
	 */
	private static UserManagementService _instance;

	/**
	 * Private constructor due to singleton pattern.
	 */
	private UserManagementService() {
	}

	/**
	 * Return the only one instance of the <code>UserManagementService</code>.
	 * 
	 * @return The only one instance of the <code>UserManagementService</code>.
	 */
	public static UserManagementService getInstance() {
		if (_instance == null) {
			_instance = new UserManagementService();
		}
		return _instance;
	}

	/**
	 * Check if the user with the given name can log into the CSS instance with
	 * the given password.
	 * 
	 * @param name
	 *            the user name.
	 * @param password
	 *            the password.
	 * @return True if the user with the given name can log into the CSS
	 *         instance with the given password.
	 */
	public boolean checkUser(final String name, final String password) {
		return true;
	}

	/**
	 * Set the currently logged in user.
	 * 
	 * @param user
	 *            The user that is currently logged into the running CSS
	 *            instance.
	 */
	public void setUser(final IUser user) {
		_user = user;
		CentralLogger.getInstance().info(this, "User " + user.getName() + " logged in."); //$NON-NLS-1$ //$NON-NLS-2$
		notifyListener(new UserManagementEvent());
	}

	/**
	 * Return the user that is currently logged into the running CSS instance.
	 * 
	 * @return The user that is currently logged into the running CSS instance.
	 */
	public IUser getUser() {
		return _user;
	}

	/**
	 * Attach a user management listener to the
	 * <code>UserManagementService</code>.
	 * 
	 * @param listener
	 *            The listener.
	 */
	public void addUserManagementListener(final IUserManagementListener listener) {
		_listeners.add(listener);
	}

	/**
	 * Detach a user management listener from the
	 * <code>UserManagementService</code>.
	 * 
	 * @param listener
	 *            The listener.
	 */
	public void removeListener(final IUserManagementListener listener) {
		_listeners.remove(listener);
	}

	/**
	 * Notifies all registered user management listener with the given event.
	 * 
	 * @param event
	 *            The event which should be sent.
	 */
	private void notifyListener(final UserManagementEvent event) {
		for (IUserManagementListener listener : _listeners) {
			listener.handleUserManagementEvent(event);
		}
	}
}
