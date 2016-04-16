/*******************************************************************************
 * Copyright (c) 2010-2016 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.validation.core.ui;

import org.eclipse.swt.events.TreeEvent;
import org.eclipse.swt.events.TreeListener;

/**
 *
 * <code>TreeViewerListener</code> is an intercepter listener, which blocks the events that do not contain the
 * MarkerCategory as the data item.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class TreeViewerListener implements TreeListener {

    private TreeListener delegate;

    /**
     * Constructs a new listener that delegates the events to the given one.
     *
     * @param delegate the listener to receive events from this listener
     */
    public TreeViewerListener(TreeListener delegate) {
        this.delegate = delegate;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.swt.events.TreeListener#treeCollapsed(org.eclipse.swt.events.TreeEvent)
     */
    @Override
    public void treeCollapsed(TreeEvent e) {
        if ("MarkerCategory".equals(e.item.getData().getClass().getSimpleName())) {
            delegate.treeCollapsed(e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.swt.events.TreeListener#treeExpanded(org.eclipse.swt.events.TreeEvent)
     */
    @Override
    public void treeExpanded(TreeEvent e) {
        if ("MarkerCategory".equals(e.item.getData().getClass().getSimpleName())) {
            delegate.treeExpanded(e);
        }
    }
}
