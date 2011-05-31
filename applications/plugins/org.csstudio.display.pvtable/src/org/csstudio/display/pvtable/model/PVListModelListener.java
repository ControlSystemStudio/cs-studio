/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.display.pvtable.model;


/** Interface used by listeners to the PVListModel.
 *  <p>
 *  This interface informs about changes to the model's number
 *  of entries or saved values, i.e. about things that are
 *  saved with the model structure.
 *  It does not inform about value updates from the monitored
 *  PVs.
 * 
 *  @see org.csstudio.display.pvtable.model.PVListModel
 *  
 *  @author Kay Kasemir
 */
public interface PVListModelListener
{
    /** The model was started or stopped. */
    public void runstateChanged(boolean isRunning);
    
    /** An entry has been added. */
    public void entryAdded(PVListEntry entry);

    /** An entry is about to be removed. */
    public void entryRemoved(PVListEntry entry);

    /** Multiple entries were added, removed, modified, or a snapshot was taken. */
    public void entriesChanged();
    
    /** Some of the PV values changed (main or readback PV).
     *  <p>
     *  This affects the 'dynamic' values of the model.
     *  It requires a 'redraw', but not a 'save'.
     *  <p>
     *  <B>Note:</B>
     *  This callback is invoked from a non-GUI thread!
     */
    public void valuesUpdated();
}
