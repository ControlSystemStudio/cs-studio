/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.diag.epics.pvtree.swt;

import java.util.List;

import org.csstudio.diag.epics.pvtree.model.TreeModel;
import org.csstudio.diag.epics.pvtree.model.TreeModelItem;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;

/** The PV Tree Model
 *  <p>
 *  Unfortunately, this is not a generic model of the PV Tree data.
 *  It is tightly coupled to the TreeViewer, acting as the content provider,
 *  and directly updating/refreshing the tree GUI.
 *  <p>
 *  Note that most of the logic is actually inside the PVTreeItem.
 *  @see PVTreeItem
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
class TreeModelContentProvider implements IStructuredContentProvider, ITreeContentProvider
{
    /** The view to which we are connected. */
    private final TreeViewer viewer;

    /** PV Tree model to represent */
    private final TreeModel model;

    /** @param viewer
     *  @param model
     */
    TreeModelContentProvider(final TreeViewer viewer, final TreeModel model)
    {
        this.viewer = viewer;
        this.model = model;
    }

//    /** Re-initialize the model with a new root PV. */
//    public void setRootPV(final String name)
//    {
//        final boolean was_frozen = frozen;
//        frozen = false;
//        if (was_frozen)
//        {
//            final Tree tree = viewer.getTree();
//            tree.setBackground(tree.getDisplay().getSystemColor(SWT.COLOR_WHITE));
//        }
//        viewer.collapseAll();
//        if (root != null)
//        {
//            root.dispose();
//            value_throttle.clearPendingUpdates();
//            root = null;
//        }
//        links_to_resolve.set(0);
//        root = new PVTreeItem(this, null, Messages.PV, name);
//        itemChanged(root);
//    }

    @Override
    public void inputChanged(Viewer v, Object oldInput, Object newInput)
    {
        // NOP
    }

    @Override
    public void dispose()
    {
        // NOP
    }

    @Override
    public Object[] getElements(final Object parent)
    {
        if (parent instanceof TreeModelItem)
            return getChildren(parent);
        final TreeModelItem root = model.getRoot();
        if (root != null)
            return new Object[] { root };
        return new Object[0];
    }

    @Override
    public Object getParent(final Object child)
    {
        if (child instanceof TreeModelItem)
            return ((TreeModelItem) child).getParent();
        return null;
    }

    @Override
    public Object[] getChildren(final Object parent)
    {
        if (parent instanceof TreeModelItem)
        {
            final List<TreeModelItem> links = ((TreeModelItem) parent).getLinks();
            return links.toArray(new TreeModelItem[links.size()]);
        }
        return new Object[0];
    }

    @Override
    public boolean hasChildren(final Object parent)
    {
        if (parent instanceof TreeModelItem)
            return ((TreeModelItem) parent).getLinks().size() > 0;
        return false;
    }
}
