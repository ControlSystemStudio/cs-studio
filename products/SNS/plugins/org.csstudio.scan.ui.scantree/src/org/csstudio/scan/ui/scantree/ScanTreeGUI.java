/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.ui.scantree;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.apputil.ui.workbench.OpenPerspectiveAction;
import org.csstudio.scan.command.LoopCommand;
import org.csstudio.scan.command.ScanCommand;
import org.csstudio.scan.ui.scantree.actions.AddCommandAction;
import org.csstudio.scan.ui.scantree.actions.OpenCommandListAction;
import org.csstudio.scan.ui.scantree.actions.OpenPropertiesAction;
import org.csstudio.scan.ui.scantree.actions.RemoveCommandAction;
import org.csstudio.scan.ui.scantree.actions.SubmitCurrentScanAction;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
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
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;

/** GUI for the scan tree
 *  @author Kay Kasemir
 */
public class ScanTreeGUI
{
    /** Listener */
    final private ScanTreeGUIListener listener;

    /** Commands displayed and edited in this GUI */
    private List<ScanCommand> commands = new ArrayList<ScanCommand>();

    /** Tree that shows commands */
    private TreeViewer tree_view;

    /** Initialize
     *  @param parent
     */
    public ScanTreeGUI(final Composite parent, final ScanTreeGUIListener listener)
    {
        this.listener = listener;
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
        manager.add(new AddCommandAction());
        manager.add(new RemoveCommandAction(this));
        manager.add(new Separator());
        manager.add(new SubmitCurrentScanAction());
        manager.add(new Separator());
        manager.add(new OpenPropertiesAction());
        manager.add(new OpenCommandListAction());
        manager.add(new Separator());
        manager.add(new OpenPerspectiveAction(Activator.getImageDescriptor("icons/perspective.gif"), //$NON-NLS-1$
                Messages.OpenScanTreePerspective, Perspective.ID));
        manager.add(new Separator("additions")); //$NON-NLS-1$

        final Menu menu = manager.createContextMenu(tree_view.getControl());
        tree_view.getControl().setMenu(menu);
    }

    /** @return Currently selected scan command or <code>null</code> */
    public ScanCommand getSelectedCommand()
    {
        final IStructuredSelection sel = (IStructuredSelection)  tree_view.getSelection();
        if (sel.isEmpty())
            return null;
        return (ScanCommand) sel.getFirstElement();
    }

    /** Information about Pointer location relative to a tree item */
    static class TreeItemInfo
    {
        enum Section { UPPER, CENTER, LOWER };
        final public ScanCommand command;
        final public Section section;

        public TreeItemInfo(final ScanCommand command, final Section section)
        {
            this.command = command;
            this.section = section;
        }
    };

    /** Determine where mouse pointer is relative to a tree item
     *  @param x Mouse coordinate
     *  @param y Mouse coordinate
     *  @return {@link TreeItemInfo} or <code>null</code>
     */
    private TreeItemInfo getTreeItemInfo(final int x, final int y)
    {
        // Get cell under mouse pointer
        final Control tree = tree_view.getControl();
        final Point point = tree.getDisplay().map(null, tree, x, y);
        final ViewerCell cell = tree_view.getCell(point);
        if (cell == null)
            return null;

        final ScanCommand command = (ScanCommand) cell.getElement();

        final Rectangle bounds = cell.getBounds();
        // Determine if we are in upper, middle or lower 1/3 of the cell
        if (point.y < bounds.y + bounds.height/3)
            return new TreeItemInfo(command, TreeItemInfo.Section.UPPER);
        else if (point.y > bounds.y + 2*bounds.height/3)
            return new TreeItemInfo(command, TreeItemInfo.Section.LOWER);
        else
            return new TreeItemInfo(command, TreeItemInfo.Section.CENTER);
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
                // Remove 'original' command that was moved to new location
                TreeManipulator.remove(commands, command);
                refresh();
            }
        });

        tree_view.addDropSupport(DND.DROP_MOVE | DND.DROP_COPY, transfers, new DropTargetAdapter()
        {
            @Override
            public void dragOver(final DropTargetEvent event)
            {   // Modify feedback when dropping 'before', 'on' or 'after' existing command
                event.feedback = DND.FEEDBACK_EXPAND | DND.FEEDBACK_SCROLL;
                final TreeItemInfo info = getTreeItemInfo(event.x, event.y);
                if (info == null)
                    return;
                switch (info.section)
                {
                case UPPER:
                    event.feedback |= DND.FEEDBACK_INSERT_BEFORE;
                    break;
                case LOWER:
                    event.feedback |= DND.FEEDBACK_INSERT_AFTER;
                    break;
                default:
                    event.feedback |= DND.FEEDBACK_SELECT;
                }
            }

            @Override
            public void drop(final DropTargetEvent event)
            {
                // Add dropped command
                if (! (event.data instanceof ScanCommand))
                    return;
                final ScanCommand dropped_command = (ScanCommand) event.data;

                // Determine _where_ it was dropped
                final TreeItemInfo target = getTreeItemInfo(event.x, event.y);
                if (target == null)
                {   // Add to end of commands
                    commands.add(dropped_command);
                }
                else
                {   // System.out.println("Dropped: " + command + " onto " + target.command);
                    // Special handling for loop
                    if (target.command instanceof LoopCommand  &&
                        target.section == TreeItemInfo.Section.CENTER)
                    {   // Dropping exactly onto a loop means add to that loop
                        final LoopCommand loop = (LoopCommand) target.command;
                        TreeManipulator.addToLoop(loop, dropped_command);
                    }
                    else
                    {
                        final boolean after = target.section != TreeItemInfo.Section.UPPER;
                        TreeManipulator.insert(commands, target.command, dropped_command, after);
                    }
                }
                refresh();
                // Set selection to new command, which also asserts that it is visible
                tree_view.setSelection(new StructuredSelection(dropped_command));
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
        tree_view.expandAll();
    }

    /** Perform full GUI refresh */
    public void refresh()
    {
        setCommands(commands);
        if (listener != null)
            listener.scanTreeChanged();
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
        if (listener != null)
            listener.scanTreeChanged();
    }

    /** @return Selection provider for commands in scan tree */
    public ISelectionProvider getSelectionProvider()
    {
        return tree_view;
    }
}
