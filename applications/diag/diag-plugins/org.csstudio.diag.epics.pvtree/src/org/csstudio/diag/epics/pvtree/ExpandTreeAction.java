/*******************************************************************************
 * Copyright (c) 2013 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.diag.epics.pvtree;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.TreeViewer;

/** Action to expand the full PV tree
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ExpandTreeAction extends Action
{
    final private TreeViewer viewer;

    public ExpandTreeAction(final TreeViewer viewer)
    {
        super(Messages.ExpandAll,
            Plugin.imageDescriptorFromPlugin(Plugin.ID, "icons/pvtree.png"));
        setToolTipText(Messages.ExpandAllTT);
        this.viewer = viewer;
    }

    /** {@inheritDoc} */
    @Override
    public void run()
    {
        viewer.expandAll();
    }
}
