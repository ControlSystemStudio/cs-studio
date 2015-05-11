/*******************************************************************************
 * Copyright (c) 2013 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.diag.epics.pvtree;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

/** Action to collapse the PV tree
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class CollapseTreeAction extends Action
{
    final private Tree tree;
    
    public CollapseTreeAction(final Tree tree)
    {
        super(Messages.Collapse,
            Plugin.imageDescriptorFromPlugin(Plugin.ID, "icons/collapse.gif"));
        setToolTipText(Messages.CollapseTT);
        this.tree = tree;
    }
    
    /** {@inheritDoc} */
    @Override
    public void run()
    {
        tree.setRedraw(false);
     
        // While working on the alarm tree,
        // this turned out to be very slow:
        // tree_viewer.collapseAll();
        // tree_viewer.refresh(false);

        // Fastest: collapse just the first level of elements
        final TreeItem[] items = tree.getItems();
        for (TreeItem item : items)
            item.setExpanded(false);
        
        tree.setRedraw(true);
    }
}
