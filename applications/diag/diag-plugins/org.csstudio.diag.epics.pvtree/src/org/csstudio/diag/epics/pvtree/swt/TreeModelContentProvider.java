/*******************************************************************************
 * Copyright (c) 2010-2017 Oak Ridge National Laboratory.
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
import org.eclipse.jface.viewers.Viewer;

/** JFace Tree Content Provider for TreeModel
 *  @author Kay Kasemir
 */
class TreeModelContentProvider implements IStructuredContentProvider, ITreeContentProvider
{
    /** PV Tree model to represent */
    private TreeModel model;

    @Override
    public void inputChanged(Viewer v, Object oldInput, Object newInput)
    {
        model = (TreeModel) newInput;
    }

    @Override
    public void dispose()
    {
        model = null;
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
