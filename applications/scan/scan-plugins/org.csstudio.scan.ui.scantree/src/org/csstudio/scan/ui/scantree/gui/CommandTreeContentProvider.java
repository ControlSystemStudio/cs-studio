/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.ui.scantree.gui;

import java.util.List;

import org.csstudio.scan.command.ScanCommand;
import org.csstudio.scan.ui.scantree.model.ScanTreeModel;
import org.eclipse.jface.viewers.ILazyTreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;

/** "Lazy" content provider for {@link TreeViewer}
 *  for input of {@link ScanTreeModel}.
 *
 *  <p>This implementation is not very fast.
 *  It converts <code>List</code>s into arrays
 *  as required by the {@link ILazyTreeContentProvider},
 *  and resolves parent relationships via a recursive,
 *  linear search.
 *
 *  <p>The goal was to have an adapter from the original
 *  {@link ScanCommand} list to the tree viewer without
 *  creating actual tree links, including parent links.
 *
 *  @author Kay Kasemir
 */
public class CommandTreeContentProvider implements ILazyTreeContentProvider
{
    /** Tree viewer */
    private TreeViewer tree_viewer = null;

    /** The current scan tree model */
    private ScanTreeModel model = null;

    /** {@inheritDoc} */
    @Override
    public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput)
    {
        this.tree_viewer = (TreeViewer) viewer;

        if (! (newInput instanceof ScanTreeModel))
            model = null;
        else
            model = (ScanTreeModel) newInput;
    }

    /** {@inheritDoc} */
    @Override
    public void dispose()
    {
        model = null;
    }

    /** {@inheritDoc} */
    @Override
    public void updateElement(final Object parent, final int index)
    {
        final List<ScanCommand> children;
        if (parent == model)
            children = model.getCommands();
        else
            children = model.getChildren((ScanCommand)parent);

        final ScanCommand child;
        if (children != null  &&  index < children.size())
            child = children.get(index);
        else
            return;
        tree_viewer.replace(parent, index, child);
        tree_viewer.setChildCount(child, model.getChildCount(child));
    }

    /** {@inheritDoc} */
    @Override
    public void updateChildCount(final Object element, final int current_child_count)
    {
        final int count;
        if (element == model)
            count = model.getCommands().size();
        else
            count = model.getChildCount((ScanCommand)element);
        if (count != current_child_count)
            tree_viewer.setChildCount(element, count);
    }

    /** {@inheritDoc} */
    @Override
    public Object getParent(final Object element)
    {
        if (! (element instanceof ScanCommand))
            return null;
        return model.getParent((ScanCommand) element);
    }
}
