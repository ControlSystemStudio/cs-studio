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

import java.util.LinkedList;

import org.csstudio.platform.internal.usermanagement.IUser;


/**
 * This class implements the rightsmanagement.
 * @author Kai Meyer & Torsten Witte & Alexander Will & Sven Wende
 */
public final class RightsManagementService {
	
	/**
	 * For singleton pattern.
	 */
	private static RightsManagementService _instance = null;
	
	/**
	 * The listeners.
	 */
	private final LinkedList<IRightsManagementListener> _listener = new LinkedList<IRightsManagementListener>();
	
	/**
	 * Private constructor.
	 */
	private RightsManagementService() {
	}
	
	/**
	 * Delivers the current instance of this RightsManagement.  
	 * @return The current instance of this RightsManagement
	 */
	public static RightsManagementService getInstance() {
		if (_instance==null) {
			_instance = new RightsManagementService();
		}
		return _instance;
	}
	
	/**
	 * @see testrcp.rightsmanagement.interfaces.IRightsManagement#hasRights(testrcp.usermanagement.User, java.lang.String, testrcp.rightsmanagement.interfaces.IRight)
	 * @param user The user
	 * @param id The id of the requested object
	 * @param defaultRight An IRight as default value if the ID is unknown
	 * @return True, if the user has the permission; false otherwise
	 */
	public boolean hasRights(final IUser user, final String id, final IRight defaultRight) {
		return user!=null;
	}
	
	/**
	 * @see testrcp.rightsmanagement.interfaces.IRightsManagement#addRightsManagementListener(org.csstudio.platform.internal.rightsmanagement.IRightsManagementListener)
	 * @param listener The IRightsManagementListener, which should be added 
	 */
	public void addRightsManagementListener(final IRightsManagementListener listener) {
		_listener.add(listener);
	}
	
	/**
	 * @see testrcp.rightsmanagement.interfaces.IRightsManagement#removeListener(org.csstudio.platform.internal.rightsmanagement.IRightsManagementListener)
	 * @param listener The IRightsManagementListener, which should be deleted
	 */
	public void removeListener(final IRightsManagementListener listener) {
		_listener.remove(listener);
	}
	
}
