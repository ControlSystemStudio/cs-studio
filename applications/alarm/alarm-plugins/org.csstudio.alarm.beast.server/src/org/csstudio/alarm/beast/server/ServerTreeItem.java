/*******************************************************************************
 * Copyright (c) 2018 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.server;

import org.csstudio.alarm.beast.SeverityLevel;
import org.csstudio.alarm.beast.TreeItem;

/** Node in the alarm tree as used by the server
 *
 *  <p>Tracks the alarm severity at this level in the hierarchy
 *  @author Kay Kasemir
 */
public class ServerTreeItem extends TreeItem
{
    private static final long serialVersionUID = -2991781205177465014L;

    protected volatile SeverityLevel severity = SeverityLevel.UNDEFINED;

    public ServerTreeItem(ServerTreeItem parent, String name, int id)
    {
        super(parent, name, id);
    }

    /** {@inheritDoc} */
    @Override
    public ServerTreeItem getParent()
    {
        return (ServerTreeItem) super.getParent();
    }

    /** {@inheritDoc} */
    @Override
    public ServerTreeItem getChild(final int index)
    {
        return (ServerTreeItem) super.getChild(index);
    }

    /** Set severity of this item by maximizing over its child severities.
     *  Recursively updates parent items.
     */
    public void maximizeSeverity()
    {
        boolean changed = false;

        SeverityLevel new_severity = SeverityLevel.OK;

        synchronized (this)
        {
            final int n = getChildCount();
            for (int i=0; i<n; ++i)
            {
                // Maximize severity of all child elements
                final ServerTreeItem child = getChild(i);
                if (child instanceof AlarmPV)
                {
                    final AlarmPV pv = (AlarmPV) child;
                    if (! pv.getAlarmLogic().isEnabled())
                        continue;
                }
                if (child.severity.ordinal() > new_severity.ordinal())
                    new_severity = child.severity;
            }
        }

        if (new_severity != severity)
        {
            severity = new_severity;
            changed = true;
        }

        // Percolate changes towards root
        final ServerTreeItem parent = getParent();
        if (parent != null)
            parent.maximizeSeverity();

        // TODO If _this_ node changed its severity,
        //      update optional severity PV
        if (changed)
        {
            System.out.println("Update severity PV for " + getName() + " to " + new_severity.name());
            // TODO Write to queue, handle that in other thread
        }
    }
}
