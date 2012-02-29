/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.ui.scantree;

import java.util.List;

import org.csstudio.scan.command.LoopCommand;
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

    /** Determine child elements in tree
     *  @param element List of root elements or a specific command
     *  @return child commands or <code>null</code>
     */
    public List<ScanCommand> getChildren(final Object element)
    {
        if (element == elements)
            return elements;
        if (element instanceof LoopCommand)
        {
            final LoopCommand loop = (LoopCommand) element;
            return loop.getBody();
        }
        return null;
    }

    /** Determine child count
     *  @param element List of root elements or a specific command
     *  @return child count, may be 0
     */
    private int getChildCount(final Object element)
    {
        final List<ScanCommand> children = getChildren(element);
        if (children != null)
            return children.size();
        return 0;
    }

    /** {@inheritDoc} */
    @Override
    public Object getParent(final Object element)
    {
        if (! (element instanceof ScanCommand))
            return null;
        return checkChildren(null, elements, (ScanCommand) element);
    }

    /** Recursively determine parent item
     *  @param parent Possible parent
     *  @param children child entries of parent
     *  @param desired_child Desired child element
     *  @return Parent element of child or <code>null</code>
     */
    private ScanCommand checkChildren(final ScanCommand parent, final List<ScanCommand> children, final ScanCommand desired_child)
    {
        for (ScanCommand child : children)
        {
            if (child == desired_child)
                return parent;
            if (getChildCount(child) > 0)
            {
                final ScanCommand found = checkChildren(child, getChildren(child), desired_child);
                if (found != null)
                    return found;
            }
        }
        return null;
    }
}
