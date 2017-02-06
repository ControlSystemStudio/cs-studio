/*******************************************************************************
 * Copyright (c) 2015-2017 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.csstudio.diag.epics.pvtree.jfx;

import javafx.event.Event;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeItem.TreeModificationEvent;
import javafx.scene.control.TreeView;

/** Helper for TreeView
 *  @author Kay Kasemir
 */
public class TreeHelper
{
    /** Trigger a redraw of a tree item
     *
     *  <p>Call when the model item's representation changed,
     *  so tree item with existing value object needs to be
     *  redrawn.
     *
     *  @param item {@link TreeItem}
     */
    public static <TV> void triggerTreeItemRefresh(final TreeItem<TV> item)
    {
        // TreeView or TreeItem has no 'refresh()', update or redraw method.
        // 'setValue' only triggers a refresh of the item if value is different
        //
        // final TV value = item.getValue();
        // item.setValue(null);
        // item.setValue(value);

        // The API does expose the valueChangedEvent(), so send that
        Event.fireEvent(item, new TreeModificationEvent<TV>(TreeItem.<TV>valueChangedEvent(), item, item.getValue()));
    }

    /** Expand or collapse complete tree
     *
     *  @param tree {@link TreeView}
     *  @param expand Expand or collapse?
     */
    public static void setExpanded(final TreeView<?> tree, final boolean expand)
    {
        final TreeItem<?> root = tree.getRoot();
        if (root != null)
            setExpanded(root, expand);
    }

    /** Expand or collapse complete sub tree
     *
     *  @param node Node from which on to expand
     *  @param expand Expand or collapse?
     */
    public static void setExpanded(final TreeItem<?> node, final boolean expand)
    {
        if (node.isLeaf())
            return;
        node.setExpanded(expand);
        for (TreeItem<?> sub : node.getChildren())
            setExpanded(sub, expand);
    }

    /** Expand path from root to given node
     *
     *  @param node Node to expand
     */
    public static void expandItemPath(final TreeItem<?> node)
    {
        TreeItem<?> parent = node.getParent();
        while (parent != null)
        {
            parent.setExpanded(true);
            parent = parent.getParent();
        }
    }
}
