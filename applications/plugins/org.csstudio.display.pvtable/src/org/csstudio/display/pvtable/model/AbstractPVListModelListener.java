/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.display.pvtable.model;

/** NOP implementation of PVListModelListener */
public class AbstractPVListModelListener implements PVListModelListener
{
    @Override
    public void runstateChanged(boolean isRunning)
    { /* NOP */ }

    @Override
    public void entryAdded(PVListEntry entry)
    { /* NOP */ }

    @Override
    public void entryRemoved(PVListEntry entry)
    { /* NOP */ }

    @Override
    public void entriesChanged()
    { /* NOP */ }

    @Override
    public void valuesUpdated()
    { /* NOP */ }
}
