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
package org.csstudio.auth.security;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.csstudio.auth.internal.rightsmanagement.IRightsManagementListener;
import org.csstudio.auth.internal.rightsmanagement.RightsManagementEvent;
import org.csstudio.auth.internal.rightsmanagement.RightsManagementService;
import org.csstudio.auth.internal.security.ActivateableList;
import org.csstudio.auth.internal.security.NoActivationAdapterFoundException;
import org.csstudio.auth.internal.usermanagement.IUserManagementListener;
import org.csstudio.auth.internal.usermanagement.UserManagementEvent;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;

/**
 * This class (de-)activates the registered objects if the current user has the
 * permission for the id of the object.
 *
 * Memory complication:
 * This is used by for example the AbstractUserDependentAction to automatically
 * enable/disable an action based on changing authorization of the user.
 * These Actions can be used in dynamically created context menues.
 * The menu API allows the creation of such context menues, but there is no
 * obvious way to remove menu entries. The menu is simply garbage-collected
 * when Eclipse decides that it is no longer needed.
 * If the AbstractUserDependentAction registered itself with the ActivationService,
 * it cannot be garbage collected as long as the ActivationService still
 * has a reference to the action.
 * For this reason, the ActivateableList must only keep week references to
 * registered objects.
 *
 *
 * @author Kai Meyer & Torsten Witte & Alexander Will & Sven Wende
 * @author Kay Kasemir Weak Reference handling
 */
public final class ActivationService implements IUserManagementListener,
        IRightsManagementListener {

    /**
     * A HashMap which contains a ActivateableList under an ID.
     *
     * Contains entries like
     *   "alarm_ack" -> ActivateableList { object, adapter }
     * for all the items that need to be enabled/disabled based on
     * the "alarm_ack" right.
     */
    private final Map<String, ActivateableList> _activatesMap;

    /**
     * The singleton instance of this class.
     */
    private static ActivationService _instance;

    /**
     * Constructor of this class. Registers this ActivationManagement as
     * UserManagementListener and RightsManagementListener
     */
    private ActivationService() {
        _activatesMap = new HashMap<String, ActivateableList>();
        SecurityFacade.getInstance().addUserManagementListener(this);
        RightsManagementService.getInstance().addRightsManagementListener(this);
    }

    /**
     * Returns the singleton instance of this class.
     * @return The singleton instance of this class
     */
    public static ActivationService getInstance() {
        if (_instance == null) {
            _instance = new ActivationService();
        }
        return _instance;
    }

    /**
     * Registers the given object with the given ID.
     * It is necessary that an {@link IActivationAdapter} is registered by the
     * AdapterManager of the Platform or the object is an {@link IAdaptable} and
     * delivers an {@link IActivationAdapter}.
     * If no IActivationAdapter is found the registration is denied.
     *
     * @param id
     *            The ID for the object
     * @param defaultRight
     *            An IRight as default value if the ID is unknown; can be null
     * @param object
     *            The object to manage
     * @throws NoActivationAdapterFoundException
     *             If no standard IActivationAdapter is available for the object
     */
    public void registerObject(final String id, final String defaultRight,
            final Object object) throws NoActivationAdapterFoundException {
        IActivationAdapter standard = this.getActivationAdapter(object);
        if (standard == null) {
            throw new NoActivationAdapterFoundException(object.getClass());
        } else {
            this.registerWidget(id, defaultRight, object, standard);
        }
    }

    /**
     * Returns a IActivationAdapter for the object or null.
     * @param object
     *             The object, which {@link IActivationAdapter} should be returned
     * @return IActivationAdapter
     *             The IActivationAdapter for the object or null
     */
    private IActivationAdapter getActivationAdapter(final Object object) {
        IActivationAdapter adapter = null;
        if (object instanceof IAdaptable) {
            adapter = (IActivationAdapter) ((IAdaptable)object).getAdapter(IActivationAdapter.class);
        }
        if (adapter==null) {
            adapter = (IActivationAdapter) Platform.getAdapterManager()
                .getAdapter(object, IActivationAdapter.class);
        }
        return adapter;
    }

    /**
     * Registers the given object.
     * Calls <i>registerOject(String, String, Object)</i>
     * @param id
     *             The id for the object
     * @param object
     *             The object to manage
     */
    public void registerObject(final String id,    final Object object) {
        registerObject(id, null, object);
    }

    /**
     * Registers the given object with the given ID and the given
     * {@link IActivationAdapter}.
     *
     * @param id
     *            The ID for the object
     * @param defaultRight
     *            An IRight as default value if the ID is unknown; can be null
     * @param object
     *            The object to manage
     * @param adapter
     *            The {@link IActivationAdapter} for the given object; must not be null
     */
    public void registerWidget(final String id, final String defaultRight,
            final Object object, final IActivationAdapter adapter) {
        if (adapter == null) {
            throw new NullPointerException("IActivationAdapter was null");
        }
        if (id != null) {
            if (_activatesMap.containsKey(id)) {
                ActivateableList liste = _activatesMap.get(id);
                if (!liste.contains(object)) {
                    liste.addObject(object, adapter);
                    adapter.activate(object, SecurityFacade.getInstance()
                            .canExecute(id));
                }
            } else {
                ActivateableList liste = new ActivateableList(defaultRight);
                liste.addObject(object, adapter);
                _activatesMap.put(id, liste);
                adapter.activate(object, SecurityFacade.getInstance()
                        .canExecute(id));
            }
        }
    }

    /**
     * Unregisters the given object.
     *
     * @param id
     *            The ID, which was used to register the widget
     * @param object
     *            The object, which should be unregistered
     * @return True if the object could be unregistered; false otherwise
     */
    public boolean unregisterObject(final String id, final Object object) {
        if (_activatesMap.containsKey(id)) {
            boolean bool = _activatesMap.get(id).removeObject(object);
            if (_activatesMap.get(id).isEmpty()) {
                _activatesMap.remove(id);
            }
            return bool;
        } else {
            return false;
        }
    }

    /**
     * Unregisters the given object.
     *
     * @param object
     *            The object, which should be unregistered
     * @return True if the object could be unregistered; false otherwise
     */
    public boolean unregisterObject(final Object object) {
        boolean bool = false;
        String[] keys = _activatesMap.keySet().toArray(new String[0]);
        for (int i = 0; i < keys.length; i++) {
            ActivateableList liste = _activatesMap.get(keys[i]);
            if (liste.removeObject(object)) {
                if (liste.isEmpty()) {
                    _activatesMap.remove(keys[i]);
                }
                bool = true;
            }
        }
        return bool;
    }

    /**
     * Disposes the current {@link ActivationService}. All registered objects
     * are removed and unregisters this {@link ActivationService} as
     * UserManagementListener and RightsManagementListener
     */
    public void dispose() {
        SecurityFacade.getInstance().removeUserManagementListener(this);
        RightsManagementService.getInstance().removeListener(this);
        _activatesMap.clear();
        _instance = null;
    }

    /**
     * Checks all registered objects if they should be activated or deactivated.
     */
    private void doRefreshState()
    {
        final Set<String> key_set = _activatesMap.keySet();
        final String keys[] = key_set.toArray(new String[key_set.size()]);
        for (int i = 0; i < keys.length; ++i)
        {
            final ActivateableList activatable_list = _activatesMap.get(keys[i]);
            final boolean activate =
                SecurityFacade.getInstance().canExecute(keys[i]);
            activatable_list.activate(activate);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void handleUserManagementEvent(UserManagementEvent event) {
        this.doRefreshState();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void handleRightsManagementEvent(RightsManagementEvent event) {
        this.doRefreshState();
    }

}
