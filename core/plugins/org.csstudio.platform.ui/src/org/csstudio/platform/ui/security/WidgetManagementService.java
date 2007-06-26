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
import java.util.Map;

import org.csstudio.platform.internal.rightsmanagement.IRightsManagementListener;
import org.csstudio.platform.internal.rightsmanagement.RightsManagementEvent;
import org.csstudio.platform.internal.rightsmanagement.RightsManagementService;
import org.csstudio.platform.internal.usermanagement.IUserManagementListener;
import org.csstudio.platform.internal.usermanagement.UserManagementEvent;
import org.csstudio.platform.security.SecurityFacade;
import org.csstudio.platform.ui.internal.security.WidgetList;
import org.csstudio.platform.ui.security.adapter.EnableControlAdapter;
import org.csstudio.platform.ui.security.adapter.IWidgetAdapter;
import org.csstudio.platform.ui.security.exceptions.NoWidgetAdapterFoundException;
import org.eclipse.core.runtime.Platform;

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
	 * A Hashmap which contains a WidgetList under an ID.
	 */
	private final Map<String, WidgetList> _widgetsMap;

//	/**
//	 * Map of controls and their right-dependent behavior.
//	 */
//	private HashMap<Control, WidgetConfiguration> _configurations;

	/**
	 * The singleton instance of this class.
	 */
	private static WidgetManagementService _instance;

	/**
	 * Constructor of this class. Registers this WidgetManagement as
	 * UserManagementListener and RightsManagementListener
	 */
	private WidgetManagementService() {
		_widgetsMap = new HashMap<String, WidgetList>();
		SecurityFacade.getInstance().addUserManagementListener(this);
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
	 * Registers the given widget with the given ID.
	 * The registered standard IWidgetAdapter for the widgets class is used as the needed IWidgetAdapter. If no entry is found the registration is denied. 
	 * @param id The ID for the widget
	 * @param defaultRight An IRight as default value if the ID is unknown; can be null
	 * @param widget The widget to manage
	 * @throws NoWidgetAdapterFoundException If no standard WidgetAdapter is registered for the class of the widget 
	 */
	public void registerWidget(final String id, final String defaultRight, final Object widget) throws NoWidgetAdapterFoundException {
		IWidgetAdapter standard = new EnableControlAdapter();  
		standard = (IWidgetAdapter) Platform.getAdapterManager().getAdapter(widget, IWidgetAdapter.class);
		if (standard==null) {
			throw new NoWidgetAdapterFoundException(widget.getClass());
		} else {
			this.registerWidget(id, defaultRight, widget, standard);
		}
	}
	
	/**
	 * Registers the given widget with the given ID and the given IWidgetAdapter.
	 * @param id The ID for the widget
	 * @param defaultRight An IRight as default value if the ID is unknown; can be null
	 * @param widget The widget to manage
	 * @param adapter The IWidgetAdapter for the given widget; must not be null
	 */
	public void registerWidget(final String id, final String defaultRight, final Object widget, final IWidgetAdapter adapter) {
		if (adapter==null) {
			throw new NullPointerException("IWidgetAdapter was null");
		}
		if (_widgetsMap.containsKey(id)) {
			WidgetList liste = _widgetsMap.get(id);
			if (!liste.contains(widget)) {
				liste.addWidget(widget,adapter);
				adapter.activate(widget, SecurityFacade.getInstance().canExecute(id));
			}
		} else {
			WidgetList liste = new WidgetList(defaultRight);
			liste.addWidget(widget,adapter);
			_widgetsMap.put(id, liste);
			adapter.activate(widget, SecurityFacade.getInstance().canExecute(id));
		}
	}
	
	/**
	 * Unregisters the given widget.
	 * @param id The ID, which was used to register the widget 
	 * @param widget The widget, which should be unregistered
	 * @return True if the widget could be unregistered; false otherwise
	 */
	public boolean unregisterWidget(final String id, final Object widget) {
		if (_widgetsMap.containsKey(id)) {
			boolean bool = _widgetsMap.get(id).removeWidget(widget);
			if (_widgetsMap.get(id).isEmpty()) {
				_widgetsMap.remove(id);
			}
			return bool;
		} else {
			return false;
		}
	}
	
	/**
	 * Unregisters the given widget.
	 * @param widget The widget, which should be unregistered
	 * @return True if the widget could be unregistered; false otherwise
	 */
	public boolean unregisterWidget(final Object widget) {
		boolean bool = false;
		String[] keys = _widgetsMap.keySet().toArray(new String[0]);
		for (int i=0;i<keys.length;i++) {
			WidgetList liste = _widgetsMap.get(keys[i]);
			if (liste.removeWidget(widget)) {
				if (liste.isEmpty()) {
					_widgetsMap.remove(keys[i]);
				}
				bool = true;
			}
		}
		return bool;
	}

	/**
	 * Disposes the current WidgetManagement.
	 * All registered widgets and registered standard IWidgetApapter are removed and unregisters this WidgetManagement 
	 * as UserManagementListener and RightsManagementListener
	 */
	public void dispose() {
		SecurityFacade.getInstance().removeUserManagementListener(this);
		RightsManagementService.getInstance().removeListener(this);
		_widgetsMap.clear();		
		_instance = null;
	}
	
	/**
	 * Checks all registered objects if they should be activated or deactivated.
	 */
	private void doRefreshState() {
		String[] keys = _widgetsMap.keySet().toArray(new String[0]);
		for (int i=0;i<keys.length;i++) {
			for (int j=0;j<_widgetsMap.get(keys[i]).size();j++) {
				WidgetList liste = _widgetsMap.get(keys[i]);
				liste.activate(j, SecurityFacade.getInstance().canExecute(keys[i]));
			}
		}
	}

	public void handleUserManagementEvent(UserManagementEvent event) {
		this.doRefreshState();
	}

	public void handleRightsManagementEvent(RightsManagementEvent event) {
		this.doRefreshState();
	}

}
