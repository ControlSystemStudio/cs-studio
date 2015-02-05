/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.ui.clientmodel;

import org.csstudio.alarm.beast.client.AlarmTreeLeaf;
import org.csstudio.alarm.beast.client.AlarmTreePV;
import org.csstudio.alarm.beast.client.AlarmTreeRoot;

/** Root of the alarm configuration tree that the client model uses.
 *  Severity changes that percolate up to the root send signal to model.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class AlarmClientModelRoot extends AlarmTreeRoot
{
    final private AlarmClientModel model;

    /** Initialize alarm tree root
     *  @param id RDB ID of root element
     *  @param name Name of root element
     *  @param model Model to which the configuration belongs
     */
    protected AlarmClientModelRoot(final int id, final String name,
            final AlarmClientModel model)
    {
        super(name, id);
        this.model = model;
    }

    @Override
    protected void acknowledge(AlarmTreePV pv, boolean acknowledge)
    {
        model.acknowledge(pv, acknowledge);
    }

    @Override
    protected void notifyListeners(final AlarmTreeLeaf leaf, final boolean parent_changed)
    {
        if (model == null)
            return;
        // In the client model, the leaf items are PVs
        if (leaf instanceof AlarmTreePV)
            model.fireNewAlarmState((AlarmTreePV) leaf, parent_changed);
        else
            throw new IllegalArgumentException("Expected PV");
    }
}
