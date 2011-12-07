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
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;

/** GUI for the scan tree
 *  @author Kay Kasemir
 */
public class GUI
{
    private TreeViewer tree_view;

    /** Initialize
     *  @param parent
     */
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
        
        ScanCommandAdapterFactory.setGUI(this);
        
        parent.addDisposeListener(new DisposeListener()
        {
            @Override
            public void widgetDisposed(DisposeEvent e)
            {
                ScanCommandAdapterFactory.setGUI(null);
            }
        });
    }

    /** Set focus */
    public void setFocus()
    {
        tree_view.getTree().setFocus();
    }
    
    /** @param commands Commands to display/edit */
    public void setCommands(final List<ScanCommand> commands)
    {
        tree_view.setInput(commands);
    }
    
    /** @param command Command that has been updated, requiring a refresh of the GUI */
    public void refreshCommand(final ScanCommand command)
    {
        tree_view.refresh(command);
    }

    /** @return Selection provider for commands in scan tree */
    public ISelectionProvider getSelectionProvider()
    {
        return tree_view;
    }
}
