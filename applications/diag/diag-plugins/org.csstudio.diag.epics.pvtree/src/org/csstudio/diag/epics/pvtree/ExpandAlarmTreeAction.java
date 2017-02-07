/*******************************************************************************
 * Copyright (c) 2013 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.diag.epics.pvtree;

import java.util.List;

import org.csstudio.diag.epics.pvtree.model.TreeModel;
import org.csstudio.diag.epics.pvtree.model.TreeModelItem;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.TreeViewer;

/** Action to expand all PV tree items that are in alarm
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ExpandAlarmTreeAction extends Action
{
    private final static int max = Preferences.getMaxAlarmPVs();
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
        final TreeModel model = (TreeModel) viewer.getInput();
        List<TreeModelItem> pvs = model.getAlarmItems();

        if (pvs.size() > max)
        {
            MessageDialog.openInformation(viewer.getControl().getShell(),
                    "PVTree",
                    "There are " + pvs.size() + " PVs in alarm, showing only the first " + max);
            pvs = pvs.subList(0, max);
        }

        viewer.collapseAll();
        // For each PV in alarm, expand its path
        for (TreeModelItem pv : pvs)
            viewer.reveal(pv);
    }
}
