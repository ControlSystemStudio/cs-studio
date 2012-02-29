/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.ui.scantree;

import java.util.List;

import org.csstudio.scan.command.ScanCommand;
import org.eclipse.jface.viewers.ILazyTreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;

/** "Lazy" content provider for {@link TreeViewer}
 *  for input of {@link ScanCommand} list.
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

    /** The current scan, i.e. the 'root' elements */
    private List<ScanCommand> elements = null;

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    @Override
    public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput)
    {
        this.tree_viewer = (TreeViewer) viewer;

        if (! (newInput instanceof List))
            elements = null;
        else
            elements = (List<ScanCommand>) newInput;
    }

    /** {@inheritDoc} */
    @Override
    public void dispose()
    {
        elements = null;
    }

    /** {@inheritDoc} */
    @Override
    public void updateElement(final Object parent, final int index)
    {
        final ScanCommand child;
        if (parent == elements)
            child = elements.get(index);
        else
        {
            final List<ScanCommand> children = getChildren(parent);
            child = children.get(index);
        }
        tree_viewer.replace(parent, index, child);
        tree_viewer.setChildCount(child, getChildCount(child));
    }

    /** {@inheritDoc} */
    @Override
    public void updateChildCount(final Object element, final int current_child_count)
    {
        final int count = getChildCount(element);
        if (count != current_child_count)
            tree_viewer.setChildCount(element, count);
    }

    /** @param element List of root elements or a specific command
     *  @return Child count
     */
    private int getChildCount(final Object element)
    {
        if (element == elements)
            return elements.size();
        if (! (element instanceof ScanCommand))
            return 0;
        return TreeManipulator.getChildCount((ScanCommand) element);
    }

    /** Determine child elements in tree
     *  @param element List of root elements or a specific command
     *  @return child commands or <code>null</code>
     */
    public List<ScanCommand> getChildren(final Object element)
    {
        if (element == elements)
            return elements;
        if (! (element instanceof ScanCommand))
            return null;
        return TreeManipulator.getChildren((ScanCommand) element);
    }

    /** {@inheritDoc} */
    @Override
    public Object getParent(final Object element)
    {
        if (! (element instanceof ScanCommand))
            return null;
        return TreeManipulator.getParent(elements, (ScanCommand) element);
    }
}
