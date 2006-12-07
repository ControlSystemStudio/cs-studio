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

import java.util.HashMap;

import org.csstudio.platform.internal.rightsmanagement.IRightsManagementListener;
import org.csstudio.platform.internal.rightsmanagement.RightsManagementEvent;
import org.csstudio.platform.internal.rightsmanagement.RightsManagementService;
import org.csstudio.platform.internal.usermanagement.IUserManagementListener;
import org.csstudio.platform.internal.usermanagement.UserManagementEvent;
import org.csstudio.platform.internal.usermanagement.UserManagementService;
import org.csstudio.platform.security.ExecutionService;
import org.csstudio.platform.ui.internal.security.WidgetConfiguration;
import org.eclipse.swt.widgets.Control;

/**
 * This class (de-)activates the registered widgets if the current user has the
 * permission for the id of the widget.
 * 
 * @author Kai Meyer & Torsten Witte & Alexander Will & Sven Wende
 * 
 */
public final class WidgetManagementService implements IUserManagementListener,
		IRightsManagementListener {

	/**
	 * Map of controls and the right IDs necessary to use them.
	 */
	private HashMap<Control, String> _controls;

	/**
	 * Map of controls and their right-dependent behavior.
	 */
	private HashMap<Control, WidgetConfiguration> _configurations;

	/**
	 * The singleton instance of this class.
	 */
	private static WidgetManagementService _instance;

	/**
	 * Constructor of this class. Registers this WidgetManagement as
	 * UserManagementListener and RightsManagementListener
	 */
	private WidgetManagementService() {
		_controls = new HashMap<Control, String>();
		_configurations = new HashMap<Control, WidgetConfiguration>();

		UserManagementService.getInstance().addUserManagementListener(this);
		RightsManagementService.getInstance().addRightsManagementListener(this);
	}

	/**
	 * @return The singleton instance of this class.
	 */
	public static WidgetManagementService getInstance() {
		if (_instance == null) {
			_instance = new WidgetManagementService();
		}
		return _instance;
	}

	/**
	 * Register the given control for widget management. Controls can only be
	 * registered once.
	 * 
	 * @param control
	 *            The control to register
	 * @param rightID
	 *            The right necessary to use the control
	 * @param changeEnablement
	 *            Enable or disable the control depending on user permission.
	 * @param changeVisibility
	 *            View or hide the control depending on user permission.
	 */
	public void registerControl(final Control control, final String rightID,
			final boolean changeEnablement, final boolean changeVisibility) {
		if (_controls.containsKey(control)) {
			throw new IllegalArgumentException("Control is already registered."); //$NON-NLS-1$
		}

		_controls.put(control, rightID);
		_configurations.put(control, new WidgetConfiguration(changeEnablement,
				changeVisibility));
		doRefreshControl(control, rightID);
	}

	/**
	 * Unregister the given control from widget management.
	 * 
	 * @param control
	 *            The control to unregister.
	 */
	public void unregisterControl(final Control control) {
		if (_controls.containsKey(control)) {
			_controls.remove(control);
			_configurations.remove(control);
		}
	}

	/**
	 * Refreshes all registered widgets.
	 */
	private void doRefreshState() {
		for (Control control : _controls.keySet()) {
			String rightId = _controls.get(control);
			doRefreshControl(control, rightId);
		}
	}

	/**
	 * Performs refresh of controls.
	 * 
	 * @param control
	 *            The control to refresh.
	 * @param rightId
	 *            The right associated with the control.
	 */
	private void doRefreshControl(final Control control, final String rightId) {
		WidgetConfiguration configuration = _configurations.get(control);

		boolean flag = ExecutionService.getInstance().canExecute(rightId);

		if (configuration.isChangeEnablement()) {
			control.setEnabled(flag);
		}
		if (configuration.isChangeVisibility()) {
			control.setVisible(flag);
		}
	}

	/**
	 * @see org.csstudio.platform.internal.usermanagement.IUserManagementListener#handleUserManagementEvent(testrcp.usermanagement.listener.IUserManagementEvent)
	 * @param event
	 *            the UserManagementEvent to handle
	 */
	public void handleUserManagementEvent(final UserManagementEvent event) {
		doRefreshState();
	}

	/**
	 * @see org.csstudio.platform.internal.rightsmanagement.IRightsManagementListener#handleRightsManagementEvent(org.csstudio.platform.internal.rightsmanagement.AbstractRightsManagementEvent)
	 * @param event
	 *            the RightsManagementEvent to handle
	 */
	public void handleRightsManagementEvent(final RightsManagementEvent event) {
		doRefreshState();
	}

}
