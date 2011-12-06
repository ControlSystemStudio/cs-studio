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
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;

/** GUI for the scan tree
 *  @author Kay Kasemir
 */
public class GUI
{
    private TreeViewer tree_view;

    public GUI(final Composite parent)
    {
        parent.setLayout(new FillLayout());

        tree_view = new TreeViewer(parent,
                SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
        final Tree tree = tree_view.getTree();
        tree.setLinesVisible(true);
        tree_view.setUseHashlookup(true);
        tree_view.setContentProvider(new CommandTreeContentProvider());
        tree_view.setLabelProvider(new CommandTreeLabelProvider());
        
        ColumnViewerToolTipSupport.enableFor(tree_view);
    }

    public void setCommands(final List<ScanCommand> commands)
    {
        tree_view.setInput(commands);
    }
}
