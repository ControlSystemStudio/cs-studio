/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.display.pvtable.model;

import org.csstudio.utility.pv.PV;

/** Interface to one entry in the PVListModel.
 *
 *  A PV with saved value, which would typically be a "setpoint" PV
 *  and its value at some time.
 *  <p>
 *  In addition, there can be a readback PV, if one wants to log
 *  how well the readback matched the setpoint at the time of the snapshot.
 *
 *  @see PVListModel
 *
 *  @author Kay Kasemir
 */
public interface PVListEntry
{
    /** Since entries can be removed from the model while there's still
     *  a pending redraw or new value from the network,
     *  users should check if this entry is still valid.
     * @return Returns <code>true</code> if this entry has already been disposed.
     */
    public boolean isDisposed();

    /** @return Returns true if selected. */
    public boolean isSelected();

    /** @return Returns the name of the 'main' PV. */
    public String getName();

    /** @return Returns the PV. */
    public PV getPV();

    /** @return Returns the saved_value. */
    public SavedValue getSavedValue();

    /** @return Returns the readback PV. */
    public PV getReadbackPV();

    /** @return Returns the saved readback value. */
    public SavedValue getSavedReadbackValue();
}
