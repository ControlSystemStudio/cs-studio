/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.ui.clientmodel;

import org.csstudio.alarm.beast.client.AlarmTreePV;

/** Listener to the AlarmClientModel.
 *
 *  @see AlarmClientModelConfigListener
 *  @author Kay Kasemir
 */
public interface AlarmClientModelListener extends AlarmClientModelConfigListener
{
    /** Notification that server mode changed
     *  <p>
     *  May originate from non-UI thread
     *
     *  @param model Model
     *  @param maintenance_mode In maintenance mode? else 'normal'
     */
    void serverModeUpdate(AlarmClientModel model, boolean maintenance_mode);

    /** Notification which indicates absence of server messages
     *  <p>
     *  May originate from non-UI thread
     *
     *  @param model Model
     */
    void serverTimeout(AlarmClientModel model);

    /** Notification which indicates change in alarm state.
     *  No items were added or removed, but a PV changed its state
     *  <p>
     *  Typically, this is invoked with the PV that changed state.
     *  May be called with a <code>null</code> PV
     *  to indicate that messages were received after a server timeout.
     *  <p>
     *  May originate from non-UI thread
     *
     *  @param model Model
     *  @param pv    PV that changed state or <code>null</code>
     *  @param parent_changed true if a parent item was updated as well
     */
    void newAlarmState(AlarmClientModel model, AlarmTreePV pv, boolean parent_changed);
}
