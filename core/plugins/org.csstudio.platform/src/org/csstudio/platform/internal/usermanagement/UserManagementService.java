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

import java.util.LinkedList;


/**
 * This class is responsible for the current user object.
 * @author Kai Meyer & Torsten Witte & Alexander Will & Sven Wende
 */
public final class UserManagementService  {
	
	/**
	 * The current user.
	 */
	private static IUser _user;
	
	/**
	 * The listeners.
	 */
	private static final LinkedList<IUserManagementListener> LISTENERS = new LinkedList<IUserManagementListener>();

	/**
	 * For singleton pattern.
	 */
	private static UserManagementService _instance;
	
	/**
	 * The constructor.
	 */
	private UserManagementService() {
	}
	
	/**
	 * Delivers the current instance of the UserManagement.
	 * @return The current UserManagement
	 */
	public static UserManagementService getInstance() {
		if (_instance==null) {
			_instance = new UserManagementService();
		}
		return _instance;
	}
	
	
	/**
	 * @see testrcp.usermanagement.IUserManagement#checkUser(java.lang.String, java.lang.String)
	 * @param name the name
	 * @param password the password
	 * @return true if an account exists, false otherwise
	 */
	public boolean checkUser(final String name, final String password) {
		//TODO implement correctly
		return true;
	}
	

	/**
	 * @see testrcp.usermanagement.IUserManagement#setUser(org.csstudio.platform.internal.usermanagement.IUser)
	 * @param user the user
	 */
	public void setUser(final IUser user) {
		_user = user;
		notifyListener(new UserManagementEvent());
	}


	/**
	 * @see testrcp.usermanagement.IUserManagement#getUser()
	 * @return the current user
	 */
	public IUser getUser() {
		return _user;
	}
	
	/**
	 * @see testrcp.usermanagement.IUserManagement#addUserManagementListener(testrcp.usermanagement.internal.IUserManagementListener)
	 * @param listener The IUserManagementListener, which should be added
	 */
	public void addUserManagementListener(final IUserManagementListener listener) {
		LISTENERS.add(listener);
	}
	
	/**
	 * @see testrcp.usermanagement.IUserManagement#removeListener(testrcp.usermanagement.internal.IUserManagementListener)
	 * @param listener The IUserManagementListener, which should be deleted
	 */
	public void removeListener(final IUserManagementListener listener) {
		LISTENERS.remove(listener);
	}
	
	/**
	 * Notifies all registered IUserManagementListener with the given AbstractUserManagementEvent.
	 * @param event The AbstractUsermanagementEvent, which should be send
	 */
	private void notifyListener(final UserManagementEvent event) {
		for (IUserManagementListener listener : LISTENERS) {
			listener.handleUserManagementEvent(event);
		}
	}
}
