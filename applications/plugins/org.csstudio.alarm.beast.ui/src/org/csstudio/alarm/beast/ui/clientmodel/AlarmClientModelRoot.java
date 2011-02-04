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

    /** Recursive calls from updated PV via parent items reached the root;
     *  inform the model.
     *  @param pv PV that triggered this update
     *  @see org.csstudio.alarm.beast.AlarmTree#maximizeSeverity()
     */
    @Override
    public void maximizeSeverity(final AlarmTreeLeaf pv)
    {
        super.maximizeSeverity(pv);
        if (model != null)
            // In the client model, the leaf items are PVs
            if (pv instanceof AlarmTreePV)
                model.fireNewAlarmState((AlarmTreePV) pv);
            else
                throw new IllegalArgumentException("Expected PV");
    }
}
