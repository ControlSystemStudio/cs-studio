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

package org.csstudio.sds.cursorservice;

import java.util.HashSet;
import java.util.Set;

import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.csstudio.platform.simpledal.ProcessVariableConnectionServiceFactory;
import org.csstudio.platform.simpledal.SettableState;
import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.ActionData;

/**
 * The cursor selection rule used at DESY. This rule selects a cursor based on
 * the enablement of the widget and, if the widget has any output channels,
 * based on write access permissions for the output channels.
 *
 * @author swende, Joerg Rathlev
 */
public final class DesyRule extends CursorSelectionRule {

    /**
     * Cursor state ID of the default cursor state.
     */
    private static final String DEFAULT = "default";

    /**
     * Cursor state ID for disabled widgets.
     */
    private static final String WIDGET_DISABLED = "widgetDisabled";

    /**
     * Cursor state ID for widgets with an output channel which they cannot
     * access.
     */
    private static final String ACCESS_DENIED = "accessDenied";

    /**
     * Cursor state ID for widgets with a writable output channel.
     */
    private static final String ACCESS_ALLOWED = "accessAllowed";

    /**
     * Cursor state ID for widgets with an output channel whose access
     * permission are unknown.
     */
    private static final String ACCESS_UNKNOWN = "accessUnknown";

    /**
     * Cursor state ID for widgets which are disabled by the CSS security
     * system.
     */
    private static final String NO_PERMISSION = "noPermission";

    /**
     * Cursor state ID for widgets which have a configured output channel, but
     * access to it is denied by preference.
     */
    private static final String WRITE_ACCESS_DENIED = "writeAccessDenied";

    private static final String ACTION_AVAILABLE = "actionDataAvailable";

    /**
     * Returns the cursor state this rule associates with the given widget.
     *
     * @param widget
     *            the widget.
     * @return the cursor state this rule associates with the given widget.
     */
    @Override
    public String determineState(final AbstractWidgetModel widget) {
        // Default
        String state = ACCESS_ALLOWED;

        // Priority 1: Is the widget enabled/disabled ?

        if(!widget.isLive()) {
            state = widget.getCursorId();
        }else if (widget.isAccesible()) {

            // Is there any write access to a channel configured and is the user
            // allowed to set a value for that channel ?
            Set<SettableState> settableStates = getSettableStates(widget.getPvAdressesWithWriteAccess());

            if (settableStates.size() == 0) {
                state = calculateStateForActionData(widget, DEFAULT);
            } else if (settableStates.size() == 1) {
                state = calculateAccessibleState(widget, settableStates.iterator().next());
            } else if (settableStates.size() > 1) {
                // We may have a mix of pvs (possibly some are settable, some not)
                state = calculateStateForPvSet(widget, settableStates);
            }
        } else {
            state = calculateStateForWidget(widget);
        }

        assert state != null : "state must not be null";
        return state;
    }

    /**
     * @param widget
     * @param settableStates
     * @return
     */
    private String calculateStateForPvSet(final AbstractWidgetModel widget,
                                          final Set<SettableState> settableStates) {
        String state = null;
        for (SettableState settableState : settableStates) {
            String tmp = calculateAccessibleState(widget, settableState);
            if(state==null) {
                state = tmp;
            }
            if(!state.equals(tmp)) {
                state = ACCESS_UNKNOWN;
                break;
            }

        }
        return state;
    }

    /**
     * @param widget
     * @param settableState
     * @return
     */
    private String calculateAccessibleState(final AbstractWidgetModel widget,
                                            final SettableState settableState) {
        String state = ACCESS_ALLOWED;
        if (settableState.equals(SettableState.UNKNOWN)) {
            state = calculateStateForActionData(widget, ACCESS_UNKNOWN);
        } else if (settableState.equals(SettableState.NOT_SETTABLE)) {
            state = ACCESS_DENIED;
        }
        return state;
    }

    /*
     * Check if the widget has a permission id. If yes, check if the widget is disabled
     * because the user does not have the required permission.
     */
    private String calculateStateForWidget(final AbstractWidgetModel widget) {
        String state = WIDGET_DISABLED;
        if (!widget.isAccessGranted()) {
            state = NO_PERMISSION;
        } else if (!widget.isWriteAccessAllowed()) {
            state = WRITE_ACCESS_DENIED;
        }
        return state;
    }

    /**
     * @param widget
     * @param state
     * @return
     */
    private String calculateStateForActionData(final AbstractWidgetModel widget, final String state) {
        ActionData actionData = widget.getActionData();
        if((actionData!=null) && !actionData.getWidgetActions().isEmpty()) {
            return ACTION_AVAILABLE;
        }
        return state;
    }

    /**
     * @param pvs
     * @return
     */
    private Set<SettableState> getSettableStates(final Set<IProcessVariableAddress> pvs) {
        Set<SettableState> settableStates = new HashSet<SettableState>();

        for (IProcessVariableAddress pv : pvs) {
            settableStates.add(ProcessVariableConnectionServiceFactory.getDefault()
                    .createProcessVariableConnectionService()
                    .checkWriteAccessSynchronously(pv));
        }
        return settableStates;
    }

}
