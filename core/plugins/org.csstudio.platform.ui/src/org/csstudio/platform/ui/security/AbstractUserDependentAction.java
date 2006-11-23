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
package org.csstudio.platform.ui.security;

import org.csstudio.platform.internal.rightsmanagement.IRightsManagementListener;
import org.csstudio.platform.internal.rightsmanagement.RightsManagementEvent;
import org.csstudio.platform.internal.rightsmanagement.RightsManagementService;
import org.csstudio.platform.internal.usermanagement.IUserManagementListener;
import org.csstudio.platform.internal.usermanagement.UserManagementEvent;
import org.csstudio.platform.internal.usermanagement.UserManagementService;
import org.csstudio.platform.security.ExecutionService;
import org.eclipse.jface.action.Action;

/**
 * This is the superclass for any Action that depends on the rights of the current user.
 * @author Kai Meyer & Torsten Witte & Alexander Will & Sven Wende
 */
public abstract class AbstractUserDependentAction extends Action implements IUserManagementListener, IRightsManagementListener {

	/**
	 * 
	 */
	private String _rightId;
	
	/**
	 * Constructor.
	 * Registers this action as UserManagementListener and RightsManagementListener.
	 */
	public AbstractUserDependentAction(String rightId) {
		assert rightId != null;
		_rightId = rightId;
		
		UserManagementService.getInstance().addUserManagementListener(this);
		RightsManagementService.getInstance().addRightsManagementListener(this);
		updateState();
	}


	/**
	 * 
	 * 
	 * @return 
	 */
	protected String getRightId() {
		return _rightId;
	}
	
	/**
	 * 
	 */
	protected void updateState() {
		setEnabled(ExecutionService.getInstance().canExecute(getRightId()));
	}
	

	/**
	 * @see org.eclipse.jface.action.Action#run()
	 */
	@Override
	public final void run() {
		if (ExecutionService.getInstance().canExecute(getRightId())) {
			doWork();
		}
	}
	
	/**
	 * This method holds the protected code.
	 * It's called by <code>run()</code>.
	 */
	protected abstract void doWork();
	

	/**
	 * @see org.csstudio.platform.internal.usermanagement.IUserManagementListener#handleUserManagementEvent(org.csstudio.platform.internal.usermanagement.AbstractUserManagementEvent)
	 * @param event the UserManagementEvent to handle
	 */
	public final void handleUserManagementEvent(final UserManagementEvent event) {
		updateState();
	}
	
	
	/**
	 * @see org.csstudio.platform.internal.rightsmanagement.IRightsManagementListener#handleRightsManagementEvent(org.csstudio.platform.internal.rightsmanagement.AbstractRightsManagementEvent)
	 * @param event the RightsManagementEvent to handle
	 */
	public final void handleRightsManagementEvent(final RightsManagementEvent event) {
		updateState();
	}

	
	/**
	 * 
	 * 
	 * @throws Throwable 
	 */
	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		UserManagementService.getInstance().removeListener(this);
		RightsManagementService.getInstance().removeListener(this);
	}

		
}
