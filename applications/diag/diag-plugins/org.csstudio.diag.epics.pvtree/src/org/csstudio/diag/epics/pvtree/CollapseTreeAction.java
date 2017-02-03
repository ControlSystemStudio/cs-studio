/*******************************************************************************
 * Copyright (c) 2013 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.diag.epics.pvtree;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.TreeViewer;

/** Action to collapse the PV tree
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class CollapseTreeAction extends Action
{
    final private TreeViewer viewer;

    public CollapseTreeAction(final TreeViewer viewer)
    {
        super(Messages.Collapse,
            Plugin.imageDescriptorFromPlugin(Plugin.ID, "icons/collapse.gif"));
        setToolTipText(Messages.CollapseTT);
        this.viewer = viewer;
    }

    /** {@inheritDoc} */
    @Override
    public void run()
    {
        viewer.collapseAll();

        // While working on earlier iteration of the alarm tree,
        // this turned out to be very slow:
        // tree_viewer.collapseAll();
        // tree_viewer.refresh(false);
        //
        // This was faster, but using both the Tree and the TreeeViewer API
        // results in inconsistent tree expansion for larger trees.
        //
        //        tree.setRedraw(false);
        //
        //
        //        // Fastest: collapse just the first level of elements
        //        final TreeItem[] items = tree.getItems();
        //        for (TreeItem item : items)
        //            item.setExpanded(false);
        //
        //        tree.setRedraw(true);
    }
}
