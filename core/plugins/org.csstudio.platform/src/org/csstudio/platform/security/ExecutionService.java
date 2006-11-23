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

import org.csstudio.platform.internal.rightsmanagement.RightsManagementService;
import org.csstudio.platform.internal.usermanagement.UserManagementService;

/**
 * This is the SecureContainer. It performs instances of
 * Abstract(Object)Executeable only if the current user has the permission to
 * run them. It also provides some methods to ask whether the current user has
 * the permission to access the objects behind an identifier. Offers methods to
 * get the current instance of the IRights- and IUserManagement.
 * 
 * @author Kai Meyer & Torsten Witte & Alexander Will & Sven Wende
 */
public final class ExecutionService {

	/**
	 * The only one instance of this class.
	 */
	private static ExecutionService _instance;

	/**
	 * The listeners.
	 */
	private List<ISecureContainerListener> _listeners;

	/**
	 * Property for the appearence of the CSS login window at system startup.
	 */
	public static final String PROP_AUTH_LOGIN = "auth_login"; //$NON-NLS-1$

	/**
	 * Private constructor due to singleton pattern.
	 */
	private ExecutionService() {
		_listeners = new ArrayList<ISecureContainerListener>();
	}

	/**
	 * 
	 * 
	 * @return
	 */
	public static ExecutionService getInstance() {
		if (_instance == null) {
			_instance = new ExecutionService();
		}

		return _instance;
	}

	/**
	 * Checks if the current user has the permission for the object behind the
	 * given ID by calling IRightsManagement.hasRights(current_user, id, null).
	 * 
	 * @param id
	 *            The ID to check
	 * @return True if the user has the permission; false otherwise
	 */
	public boolean canExecute(final String id) {
		return RightsManagementService.getInstance().hasRights(
				UserManagementService.getInstance().getUser(), id);
	}

	/**
	 * Adds the given ISecureContainerListener to the internal list.
	 * 
	 * @param listener
	 *            The ISecureContainerListener, which sould be added
	 */
	public void addISecureContainerListener(
			final ISecureContainerListener listener) {
		_listeners.add(listener);
	}

	/**
	 * Deletes the given ISecureContainerListener from the internal list.
	 * 
	 * @param listener
	 *            The ISecureContainerListener, which should be deleted
	 */
	public void removeListener(final ISecureContainerListener listener) {
		_listeners.remove(listener);
	}

	/**
	 * Informs all registered ISecureContainerListeners.
	 * 
	 * @param event
	 *            The ISecureContainerEvent, which should be send
	 */
	private void notifyListener(final SecurityEvent event) {
		for (ISecureContainerListener listener : _listeners) {
			listener.handleSecureContainerEvent(event);
		}
	}

}
