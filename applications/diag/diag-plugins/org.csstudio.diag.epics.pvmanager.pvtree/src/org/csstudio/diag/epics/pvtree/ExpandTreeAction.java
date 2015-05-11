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

/** Action to expand the full PV tree
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ExpandTreeAction extends Action
{
    final private Tree tree;
    
    public ExpandTreeAction(final Tree tree)
    {
        super(Messages.ExpandAll,
            Plugin.imageDescriptorFromPlugin(Plugin.ID, "icons/pvtree.png"));
        setToolTipText(Messages.ExpandAllTT);
        this.tree = tree;
    }
    
    /** {@inheritDoc} */
    @Override
    public void run()
    {
        tree.setRedraw(false);
     
        final TreeItem[] items = tree.getItems();
        for (TreeItem item : items)
            expand(item);
        
        tree.setRedraw(true);
    }

    private void expand(TreeItem item)
    {
        item.setExpanded(true);
        for (int i=0; i<item.getItemCount(); ++i)
            expand(item.getItem(i));
    }
}
