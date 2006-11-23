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
import java.util.List;

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
 * permission for the id of the widget. A widget can be an ordernary object. The
 * IWidgetAdapter for this object have to manage what happens if the "widget"
 * should be (de-)activated.
 * 
 * @author Kai Meyer & Torsten Witte & Alexander Will & Sven Wende
 * 
 */
public final class WidgetManagementService implements IUserManagementListener,
		IRightsManagementListener {

	/**
	 * A Hashmap which contains a WidgetList under an ID.
	 */
	private final HashMap<String, List<WidgetConfiguration>> _widgetsMap = new HashMap<String, List<WidgetConfiguration>>();

	/**
	 * 
	 */
	private HashMap<Control, String> _controls;

	/**
	 * 
	 */
	private HashMap<Control, WidgetConfiguration> _configurations;

	/**
	 * The current instance of the WidgetManagement.
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
	 * Delivers the current instance of the WidgetManagement.
	 * 
	 * @return The current WidgetManagement
	 */
	public static WidgetManagementService getInstance() {
		if (_instance == null) {
			_instance = new WidgetManagementService();
		}
		return _instance;
	}

	/**
	 * 
	 * 
	 * @param changeEnablement 
	 * @param control 
	 * @param changeVisibility 
	 * @param id 
	 */
	public void registerControl(Control control, String id, boolean changeEnablement, boolean changeVisibility) {
		if (_controls.containsKey(control)) {
			throw new IllegalArgumentException("Control is already registered.");
		} else {
			_controls.put(control, id);
			_configurations.put(control, new WidgetConfiguration(changeEnablement, changeVisibility));
			doRefreshControl(control, id);
		}
	}

	/**
	 * 
	 * 
	 * @param control 
	 */
	public void unregisterControl(Control control) {
		if (_controls.containsKey(control)) {
			_controls.remove(control);
			_configurations.remove(control);
		}
	}

	/**
	 * 
	 */
	private void doRefreshState() {
		for (Control control : _controls.keySet()) {
			String rightId = _controls.get(control);
			doRefreshControl(control, rightId);
		}
	}

	/**
	 * 
	 * 
	 * @param rightId 
	 * @param control 
	 */
	private void doRefreshControl(Control control, String rightId) {
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
	public void handleUserManagementEvent(
			final UserManagementEvent event) {
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
