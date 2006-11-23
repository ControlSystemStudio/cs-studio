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
package org.csstudio.platform.internal.rightsmanagement;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.platform.internal.usermanagement.IUser;

/**
 * The <code>RightsManagementService</code> provides the central CSS core
 * functionalities for the management of <code>Rights</code>.
 * 
 * @author Kai Meyer & Torsten Witte & Alexander Will & Sven Wende
 */
public final class RightsManagementService {

	/**
	 * The only one instance of the <code>RightsManagementService</code>.
	 */
	private static RightsManagementService _instance = null;

	/**
	 * Receivers of rights management events.
	 */
	private List<IRightsManagementListener> _listener = new ArrayList<IRightsManagementListener>();

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
	 * Check if the given user has the rights to perform the action with the
	 * given ID.
	 * 
	 * @param user
	 *            The user.
	 * @param id
	 *            The id of the requested action.
	 * @return True, if the user has the permission to perform the action with
	 *         the given ID.
	 */
	public boolean hasRights(final IUser user, final String id) {
		return user != null;
	}

	/**
	 * Attach a rights management listener to the
	 * <code>RightsManagementService</code>.
	 * 
	 * @param listener
	 *            The listener.
	 */
	public void addRightsManagementListener(
			final IRightsManagementListener listener) {
		_listener.add(listener);
	}

	/**
	 * Detach a rights management listener from the
	 * <code>RightsManagementService</code>.
	 * 
	 * @param listener
	 *            The listener.
	 */
	public void removeListener(final IRightsManagementListener listener) {
		_listener.remove(listener);
	}

}
