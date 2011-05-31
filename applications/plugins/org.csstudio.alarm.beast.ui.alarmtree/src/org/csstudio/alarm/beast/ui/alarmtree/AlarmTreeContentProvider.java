/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.ui.alarmtree;

import org.csstudio.alarm.beast.client.AlarmTreeItem;
import org.eclipse.jface.viewers.ILazyTreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;

/** Content provider that interfaces between the AlarmClientModel's
 *  AlarmTreeItem hierarchy and the TreeViewer
 *  @author Kay Kasemir
 */
public class AlarmTreeContentProvider implements ILazyTreeContentProvider
{
    final private GUI gui;
    private TreeViewer tree_viewer;

    /** Initialize
     *  @param gui TreeViewer; Input must be AlarmTreeItem
     */
    public AlarmTreeContentProvider(final GUI gui)
    {
        this.gui = gui;
    }

    /** @see ILazyTreeContentProvider */
    @Override
    public void dispose()
    {
        // NOP
    }

    /** @see ILazyTreeContentProvider */
    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
    {
        tree_viewer = (TreeViewer) viewer;
    }

    /** TreeViewer calls this to request model item
     *  @param parent Parent of the model item
     *  @param index Index into the parent's child array
     *  @see ILazyTreeContentProvider
     */
    @Override
    public void updateElement(final Object parent, final int index)
    {
        final AlarmTreeItem item = (AlarmTreeItem) parent;
        AlarmTreeItem child;
        int count;

        if (gui.getAlarmDisplayMode())
        {
            child = item.getAlarmChild(index);
            count = child.getAlarmChildCount();
        }
        else
        {
            child = item.getClientChild(index);
            count = child.getChildCount();
        }
        //System.out.println("Tree update: " + child.getName());
        tree_viewer.replace(parent, index, child);
        // Must be called to trigger tree viewer to descend further
        tree_viewer.setChildCount(child, count);
    }

    /** Called by TreeViewer to request child count
     *  @param element AlarmTreeItem
     *  @param TreeViewer's current idea of the item's child count
     *  @see ILazyTreeContentProvider
     */
    @Override
    public void updateChildCount(Object element, int currentChildCount)
    {
        final AlarmTreeItem item = (AlarmTreeItem) element;
        final int count = gui.getAlarmDisplayMode()
           ? item.getAlarmChildCount()
           : item.getChildCount();
           if (count != currentChildCount)
            tree_viewer.setChildCount(element, count);
    }

    /** @see ILazyTreeContentProvider */
    @Override
    public Object getParent(Object element)
    {
        final AlarmTreeItem item = (AlarmTreeItem) element;
        //System.out.println("getParent of " + item.toString());
        return item.getParent();
    }
}
