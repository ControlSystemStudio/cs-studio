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
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;

/** Content provider for {@link TreeViewer}
 *  for input of {@link ScanCommand} list.
 *  
 *  <p>This implementation is not very fast.
 *  It converts <code>List</code>s into arrays
 *  as required by the {@link ITreeContentProvider},
 *  and resolves parent relationships via a recursive,
 *  linear search.
 *  
 *  <p>The goal was to have an adapter from the original
 *  {@link ScanCommand} list to the tree viewer without
 *  creating actual tree links, including parent links.
 *  
 *  @author Kay Kasemir
 */
public class CommandTreeContentProvider implements ITreeContentProvider
{
    /** The current scan */
    private ScanCommand[] elements = null;

    /** Convert list into array
     *  @param list Original list of scan commands
     *  @return Array as required by {@link ITreeContentProvider}
     */
    private ScanCommand[] getScanCommands(final List<ScanCommand> list)
    {
        return list.toArray(new ScanCommand[list.size()]);
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    @Override
    public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput)
    {
        if (! (newInput instanceof List))
            elements = null;
        else
            elements = getScanCommands((List<ScanCommand>) newInput);
    }

    /** {@inheritDoc} */
    @Override
    public void dispose()
    {
        elements = null;
    }

    /** {@inheritDoc} */
    @Override
    public Object[] getElements(final Object inputElement)
    {
        return elements;
    }

    /** {@inheritDoc} */
    @Override
    public boolean hasChildren(final Object element)
    {
        return element instanceof LoopCommand;
    }

    /** {@inheritDoc} */
    @Override
    public ScanCommand[] getChildren(final Object parent)
    {
        if (parent instanceof LoopCommand)
        {
            final LoopCommand loop = (LoopCommand) parent;
            return getScanCommands(loop.getBody());
        }
        return null;
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
    private ScanCommand checkChildren(final ScanCommand parent, final ScanCommand[] children, final ScanCommand desired_child)
    {
        for (ScanCommand child : children)
        {
            if (child == desired_child)
                return parent;
            if (hasChildren(child))
            {
                final ScanCommand found = checkChildren(child, getChildren(child), desired_child);
                if (found != null)
                    return found;
            }
        }
        return null;
    }
}
