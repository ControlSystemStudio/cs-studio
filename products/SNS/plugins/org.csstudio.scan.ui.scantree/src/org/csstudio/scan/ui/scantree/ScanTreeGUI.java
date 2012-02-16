/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.ui.scantree;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.csstudio.apputil.ui.workbench.OpenPerspectiveAction;
import org.csstudio.scan.command.LoopCommand;
import org.csstudio.scan.command.ScanCommand;
import org.csstudio.scan.command.ScanCommandFactory;
import org.csstudio.scan.command.XMLCommandReader;
import org.csstudio.scan.command.XMLCommandWriter;
import org.csstudio.scan.ui.scantree.operations.AddCommandAction;
import org.csstudio.scan.ui.scantree.operations.CutOperation;
import org.csstudio.scan.ui.scantree.operations.InsertOperation;
import org.csstudio.scan.ui.scantree.operations.OpenCommandListAction;
import org.csstudio.scan.ui.scantree.operations.OpenPropertiesAction;
import org.csstudio.scan.ui.scantree.operations.SubmitCurrentScanAction;
import org.csstudio.ui.util.dialogs.ExceptionDetailsErrorDialog;
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
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IWorkbenchPartSite;

/** GUI for the scan tree
 *  @author Kay Kasemir
 */
public class ScanTreeGUI
{
    /** Associated editor */
    final private ScanEditor editor;

    /** Commands displayed and edited in this GUI */
    private List<ScanCommand> commands = new ArrayList<ScanCommand>();

    /** Tree that shows commands */
    private TreeViewer tree_view;

    /** Initialize
     *  @param parent
     *  @param editor Scan editor. Limited demo functionality when <code>null</code>
     */
    public ScanTreeGUI(final Composite parent, final ScanEditor editor)
    {
        this.editor = editor;
        createComponents(parent);
        createContextMenu(editor == null ? null : editor.getSite());
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
    private void createContextMenu(final IWorkbenchPartSite site)
    {
        final MenuManager manager = new MenuManager();

        // plugin.xml will contribute the default (global)
        // edit commands cut/copy/paste/delete here,
        // using localization and default key bindings
        manager.add(new Separator("edit")); //$NON-NLS-1$

        manager.add(new SubmitCurrentScanAction());
        manager.add(new Separator());
        manager.add(new AddCommandAction());
        manager.add(new OpenPropertiesAction());
        manager.add(new OpenCommandListAction());
        manager.add(new Separator());
        manager.add(new OpenPerspectiveAction(Activator.getImageDescriptor("icons/perspective.gif"), //$NON-NLS-1$
                Messages.OpenScanTreePerspective, Perspective.ID));
        manager.add(new Separator("additions")); //$NON-NLS-1$

        final Menu menu = manager.createContextMenu(tree_view.getControl());
        tree_view.getControl().setMenu(menu);

        // Menu ID will be the ID of the part, i.e. editor
        if (site != null)
            site.registerContextMenu(manager, tree_view);
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
            ScanCommandTransfer.getInstance(),
            TextTransfer.getInstance()
        };

        // Allow dragging 'out',
        // possible resulting in a 'cut' operation of the dragged command
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
                if (transfers[0].isSupportedType(event.dataType))
                    event.data = command;
                else
                {
                    try
                    {
                        // Format as XML
                        final ByteArrayOutputStream buf = new ByteArrayOutputStream();
                        XMLCommandWriter.write(buf, Arrays.asList(command));
                        buf.close();
                        event.data = buf.toString();
                    }
                    catch (Exception ex)
                    {
                        event.data = null;
                    }
                }
            }

            @Override
            public void dragFinished(final DragSourceEvent event)
            {
                // Anything at all? Or was this a 'copy', not a 'move'?
                if (command == null  ||  event.detail != DND.DROP_MOVE)
                    return;
                // Remove 'original' command that was moved to new location
                try
                {
                    editor.executeForUndo(new CutOperation(editor, commands, command));
                }
                catch (Exception ex)
                {
                    ExceptionDetailsErrorDialog.openError(tree_view.getControl().getShell(), Messages.Error, ex);
                }
                refresh();
            }
        });

        // Allow 'dropping' a command, resulting in a 'paste' operation
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
                final ScanCommand dropped_command;

                // Determine dropped command
                if (event.data instanceof ScanCommand)
                    dropped_command = (ScanCommand) event.data;
                else
                {
                    // Get command from XML
                    final String text = event.data.toString();
                    try
                    {
                        final ByteArrayInputStream stream = new ByteArrayInputStream(text.getBytes());
                        final XMLCommandReader reader = new XMLCommandReader(new ScanCommandFactory());
                        final List<ScanCommand> received_commands;
                        received_commands = reader.readXMLStream(stream);
                        stream.close();
                        dropped_command = received_commands.get(0);
                    }
                    catch (Exception ex)
                    {
                        ExceptionDetailsErrorDialog.openError(tree_view.getControl().getShell(),
                            Messages.Error,
                            NLS.bind(Messages.XMLCommandErrorFmt, text),
                            ex);
                        return;
                    }
                }

                try
                {
                    // Determine _where_ it was dropped
                    final TreeItemInfo target = getTreeItemInfo(event.x, event.y);
                    if (target == null)
                    {
                        // Add to end of commands
                        final ScanCommand location = commands.size() > 0
                                ? commands.get(commands.size()-1)
                                : null;
                        editor.executeForUndo(new InsertOperation(editor, commands, location,
                                dropped_command, true));
                    }
                    else
                    {   // Special handling for loop
                        if (target.command instanceof LoopCommand  &&
                            target.section == TreeItemInfo.Section.CENTER)
                        {   // Dropping exactly onto a loop means add to that loop
                            final LoopCommand loop = (LoopCommand) target.command;
                            final List<ScanCommand> body = loop.getBody();
                            final ScanCommand location = body.size() > 0
                                    ? body.get(body.size()-1)
                                    : null;
                            editor.executeForUndo(new InsertOperation(editor, body, location,
                                    dropped_command, true));
                        }
                        else
                        {
                            final boolean after = target.section != TreeItemInfo.Section.UPPER;
                            editor.executeForUndo(new InsertOperation(editor, commands, target.command,
                                    dropped_command, after));
                        }
                    }
                }
                catch (Exception ex)
                {
                    ExceptionDetailsErrorDialog.openError(tree_view.getControl().getShell(), Messages.Error, ex);
                }

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
        if (editor != null)
            editor.setDirty(true);
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
        if (editor != null)
            editor.setDirty(true);
    }

    /** @return Selection provider for commands in scan tree */
    public ISelectionProvider getSelectionProvider()
    {
        return tree_view;
    }
}
