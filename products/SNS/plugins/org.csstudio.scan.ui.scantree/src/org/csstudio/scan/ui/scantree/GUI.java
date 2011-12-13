/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.ui.scantree;

import java.util.Collections;
import java.util.List;

import org.csstudio.scan.command.ScanCommand;
import org.csstudio.scan.ui.scantree.actions.OpenPropertiesAction;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;

/** GUI for the scan tree
 *  @author Kay Kasemir
 */
public class GUI
{
    /** Commands displayed and edited in this GUI */
    private List<ScanCommand> commands = Collections.emptyList();
    
    /** Tree that shows commands */
    private TreeViewer tree_view;

    /** Initialize
     *  @param parent
     */
    public GUI(final Composite parent)
    {
        createComponents(parent);
        createContextMenu();
        addDragDrop();
    }

    /** Create GUI elements
     *  @param parent Parent widget
     */
    private void createComponents(final Composite parent)
    {
        parent.setLayout(new FillLayout());

        tree_view = new TreeViewer(parent,
                SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
        final Tree tree = tree_view.getTree();
        tree.setLinesVisible(true);
        tree_view.setUseHashlookup(true);
        tree_view.setContentProvider(new CommandTreeContentProvider());
        tree_view.setLabelProvider(new CommandTreeLabelProvider());
        
        // Double-click opens property panel
        tree_view.addDoubleClickListener(new IDoubleClickListener()
        {
            @Override
            public void doubleClick(DoubleClickEvent event)
            {
                new OpenPropertiesAction().run();
            }
        });
        
        ColumnViewerToolTipSupport.enableFor(tree_view);
    }

    /** Create context menu */
    private void createContextMenu()
    {
        final MenuManager manager = new MenuManager();
        manager.add(new OpenPropertiesAction());
        
        final Menu menu = manager.createContextMenu(tree_view.getControl());
        tree_view.getControl().setMenu(menu);
    }
    
    /** @return Currently selected scan command or <code>null</code> */
    private ScanCommand getSelectedCommand()
    {
        final IStructuredSelection sel = (IStructuredSelection)  tree_view.getSelection();
        if (sel.isEmpty())
            return null;
        return (ScanCommand) sel.getFirstElement();
    }
    
    /** Add drag-and-drop support */
    private void addDragDrop()
    {
        final Transfer[] transfers = new Transfer[]
        {
            ScanCommandTransfer.getInstance()
        };
        tree_view.addDragSupport(DND.DROP_MOVE | DND.DROP_COPY, transfers, new DragSourceAdapter()
        {
            private ScanCommand command = null;
            
            @Override
            public void dragStart(final DragSourceEvent event)
            {
                command = getSelectedCommand();
                if (command == null)
                    event.doit = false;
            }
            
            @Override
            public void dragSetData(final DragSourceEvent event)
            {
                event.data = command;
            }
            
            @Override
            public void dragFinished(final DragSourceEvent event)
            {
                // Anything at all? Or was this a 'copy', not a 'move'?
                if (command == null  ||  event.detail != DND.DROP_MOVE)
                    return;
                // TODO Remove 'original' command that was moved to new location?
                System.out.println("Should remove original " + command);
            }
        });

        tree_view.addDropSupport(DND.DROP_MOVE | DND.DROP_COPY, transfers, new DropTargetAdapter()
        {

            @Override
            public void drop(final DropTargetEvent event)
            {
                // TODO Add dropped command
                if (! (event.data instanceof ScanCommand))
                    return;
                final ScanCommand command = (ScanCommand) event.data;
                
                // Determine _where_ it was dropped
                final Control tree = tree_view.getControl();
                final Point point = tree.getDisplay().map(null, tree, event.x, event.y);
                final ViewerCell cell = tree_view.getCell(point);
                if (cell == null)
                    return;
                final ScanCommand target = (ScanCommand) cell.getElement();
                System.out.println("Dropped: " + command + " onto " + target);
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
        this.commands = commands;
        tree_view.setInput(commands);
    }
    
    /** @return Commands displayed/edited in GUI */
    public List<ScanCommand> getCommands()
    {
        return commands;
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
