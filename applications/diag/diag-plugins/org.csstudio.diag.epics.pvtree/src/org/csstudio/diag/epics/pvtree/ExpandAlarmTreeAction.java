/*******************************************************************************
 * Copyright (c) 2013 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.diag.epics.pvtree;

import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.TreeViewer;

/** Action to expand all PV tree items that are in alarm
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ExpandAlarmTreeAction extends Action
{
    final private TreeViewer viewer;

    public ExpandAlarmTreeAction(final TreeViewer viewer)
    {
        super(Messages.ExpandAlarms,
            Plugin.imageDescriptorFromPlugin(Plugin.ID, "icons/alarmtree.png"));
        setToolTipText(Messages.ExpandAlarmsTT);
        this.viewer = viewer;
    }

    /** {@inheritDoc} */
    @Override
    public void run()
    {
        final PVTreeModel model = (PVTreeModel) viewer.getInput();
        final List<PVTreeItem> pvs = model.getAlarmPVs();


        System.out.println("Expanded: ");
        for (Object pv : viewer.getExpandedElements())
            System.out.println(pv);

        System.out.println("PVS in alarm: ");
        for (PVTreeItem pv : pvs)
            System.out.println(pv);

        viewer.setExpandedElements(pvs.toArray(new PVTreeItem[pvs.size()]));
    }
}
