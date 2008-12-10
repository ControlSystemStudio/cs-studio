/*
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron,
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

package org.csstudio.platform.ui.internal.workbench;

import org.csstudio.platform.internal.usermanagement.IUserManagementListener;
import org.csstudio.platform.internal.usermanagement.UserManagementEvent;
import org.csstudio.platform.security.SecurityFacade;
import org.csstudio.platform.security.User;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.menus.WorkbenchWindowControlContribution;

/**
 * Displays information about the logged in user in the status bar.
 * 
 * @author Joerg Rathlev
 */
public class LoginInformationToolbar extends WorkbenchWindowControlContribution {
	
	/**
	 * Listens for user management events and processes them by updating the
	 * text in the status bar.
	 */
	private class UserManagementListener implements IUserManagementListener {

		/**
		 * {@inheritDoc}
		 */
		public void handleUserManagementEvent(UserManagementEvent event) {
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					// Force an update of the contribution manager. This will
					// cause the contribution manager to ask the contribution
					// (i.e., the login information toolbar) to re-create its
					// control with the updated information.
					LoginInformationToolbar.this.getParent().update(true);
				}
			});
		}
	}
	
	/**
	 * Listens for the user management events.
	 */
	private IUserManagementListener _listener;

	/**
	 * Creates a new toolbar.
	 */
	public LoginInformationToolbar() {
	}

	/**
	 * Creates a new toolbar with an id.
	 * 
	 * @param id
	 *            the id.
	 */
	public LoginInformationToolbar(String id) {
		super(id);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Control createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));
		Label label = new Label(composite, SWT.NONE);
		label.setText(getUsername());
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		createListener();
		return composite;
	}

	/**
	 * Returns the name of the currently logged in user.
	 * 
	 * @return the name of the currently logged in user.
	 */
	private String getUsername() {
		User user = SecurityFacade.getInstance().getCurrentUser();
		final String username = (user != null) ? user.getUsername()
				: "Not logged in";
		return username;
	}
	
	/**
	 * Returns <code>true</code>.
	 * 
	 * @return <code>true</code>.
	 */
	@Override
	public boolean isDynamic() {
		// The login information toolbar must be marked as dynamic so that its
		// contribution manager will recreate the control (by calling the
		// createControl method) whenever it updates.
		return true;
	}
	
	/**
	 * Creates the user management listener.
	 */
	private void createListener() {
		// if there is still an old listener, close the old one first
		if (_listener != null) {
			closeListener();
		}
		
		_listener = new UserManagementListener();
		SecurityFacade.getInstance().addUserManagementListener(_listener);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dispose() {
		closeListener();
	}

	/**
	 * Removes the user management listener from the security system. 
	 */
	private void closeListener() {
		SecurityFacade.getInstance().removeUserManagementListener(_listener);
	}

}
