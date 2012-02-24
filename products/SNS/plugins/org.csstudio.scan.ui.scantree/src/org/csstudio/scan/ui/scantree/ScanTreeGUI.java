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
import java.util.Iterator;
import java.util.List;

import org.csstudio.apputil.ui.workbench.OpenPerspectiveAction;
import org.csstudio.csdata.ProcessVariable;
import org.csstudio.scan.command.LoopCommand;
import org.csstudio.scan.command.ScanCommand;
import org.csstudio.scan.command.ScanCommandFactory;
import org.csstudio.scan.command.ScanCommandProperty;
import org.csstudio.scan.command.XMLCommandReader;
import org.csstudio.scan.command.XMLCommandWriter;
import org.csstudio.scan.device.DeviceInfo;
import org.csstudio.scan.ui.scantree.operations.AddCommandAction;
import org.csstudio.scan.ui.scantree.operations.CutOperation;
import org.csstudio.scan.ui.scantree.operations.InsertOperation;
import org.csstudio.scan.ui.scantree.operations.OpenCommandListAction;
import org.csstudio.scan.ui.scantree.operations.OpenPropertiesAction;
import org.csstudio.scan.ui.scantree.operations.PropertyChangeOperation;
import org.csstudio.scan.ui.scantree.operations.SubmitCurrentScanAction;
import org.csstudio.ui.util.dialogs.ExceptionDetailsErrorDialog;
import org.csstudio.ui.util.dnd.SerializableItemTransfer;
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
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
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

    /** Label provider */
    private CommandTreeLabelProvider label_provider;

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
        tree_view = new TreeViewer(parent, SWT.MULTI |
                SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
        final Tree tree = tree_view.getTree();
        tree.setLinesVisible(true);
        tree_view.setUseHashlookup(true);
        tree_view.setContentProvider(new CommandTreeContentProvider());
        label_provider = new CommandTreeLabelProvider();
        tree_view.setLabelProvider(label_provider);

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

    /** @return Top-level control of the GUI */
    public Control getControl()
    {
        return tree_view.getControl();
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

    /** @return Currently selected scan commands or <code>null</code> */
    public List<ScanCommand> getSelectedCommands()
    {
        final IStructuredSelection sel = (IStructuredSelection)  tree_view.getSelection();
        if (sel.isEmpty())
            return null;
        final List<ScanCommand> items = new ArrayList<ScanCommand>();
        final Iterator<?> iterator = sel.iterator();
        while (iterator.hasNext())
           items.add((ScanCommand) iterator.next());
        if (items.size() <= 0)
            return null;
        return items;
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
        final ScanCommandTransfer scan_transfer = ScanCommandTransfer.getInstance();
        final SerializableItemTransfer pv_transfer = SerializableItemTransfer.getTransfer(ProcessVariable.class);
        final TextTransfer text_transfer = TextTransfer.getInstance();

        // Allow dragging 'out',
        // possible resulting in a 'cut' operation of the dragged command
        tree_view.addDragSupport(DND.DROP_MOVE | DND.DROP_COPY,
                new Transfer[] { scan_transfer, text_transfer },
                new DragSourceAdapter()
        {
            private List<ScanCommand> selection = null;

            @Override
            public void dragStart(final DragSourceEvent event)
            {
                selection = getSelectedCommands();
                if (selection == null)
                    event.doit = false;
            }

            @Override
            public void dragSetData(final DragSourceEvent event)
            {
                if (scan_transfer.isSupportedType(event.dataType))
                    event.data = selection;
                else
                {
                    try
                    {
                        // Format as XML
                        final ByteArrayOutputStream buf = new ByteArrayOutputStream();
                        XMLCommandWriter.write(buf, selection);
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
                if (selection == null  ||  event.detail != DND.DROP_MOVE)
                    return;
                // Remove 'original' command that was moved to new location
                try
                {
                    editor.executeForUndo(new CutOperation(editor, commands, selection));
                }
                catch (Exception ex)
                {
                    ExceptionDetailsErrorDialog.openError(tree_view.getControl().getShell(), Messages.Error, ex);
                }
                refresh();
            }
        });

        tree_view.addDropSupport(DND.DROP_MOVE | DND.DROP_COPY,
                new Transfer[] { scan_transfer, pv_transfer, text_transfer },
                new DropTargetAdapter()
        {
            @Override
            public void dragEnter(final DropTargetEvent event)
            {
                boolean is_pv = false;
                for (TransferData type : event.dataTypes)
                    if (pv_transfer.isSupportedType(type))
                    {
                        event.currentDataType = type;
                        is_pv = true;
                        break;
                    }

                if (is_pv)
                {   // Need to tell DND that we accept this type
                    // because the SerializableItemTransfer is
                    // too tricky for the basic DND to figure this out
                    if ((event.operations & DND.DROP_COPY) != 0)
                        event.detail = DND.DROP_COPY;
                    else
                        event.detail = DND.DROP_NONE;
                }
            }

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
                try
                {
                    handleDrop(event);
                }
                catch (Exception ex)
                {
                    ExceptionDetailsErrorDialog.openError(tree_view.getControl().getShell(), Messages.Error, ex);
                }
            }
        });
    }

    /** Allow 'dropping' of
     *  1) command, also as XML, resulting in a 'paste' operation,
     *  2) PV name, setting the PV of some underlying commands
     *  @param event Drop event
     *  @throws Exception on error
     */
    @SuppressWarnings("unchecked")
    private void handleDrop(final DropTargetEvent event) throws Exception
    {
        // Determine _where_ it was dropped
        final TreeItemInfo target = getTreeItemInfo(event.x, event.y);

        // Check if a PV name was dropped
        ProcessVariable pv = null;
        if (event.data instanceof ProcessVariable)
            pv = (ProcessVariable) event.data;
        else if (event.data instanceof String  &&  isPossiblePVName((String) event.data))
            pv = new ProcessVariable((String) event.data);
        if (pv != null)
        {
            if (target == null)
                throw new Exception(Messages.DroppedPVNameBesideCommand);

            // Does command have a device name property?
            if (target.command.getPropertyDescription(ScanCommandProperty.DEVICE_NAME.getID()) == null)
                throw new Exception(Messages.DroppedPVNameNotSupportedByCommand);

            // Update device name of command with dropped PV
            final String device = getAlias(pv.getName());
            editor.executeForUndo(new PropertyChangeOperation(editor, target.command,
                    ScanCommandProperty.DEVICE_NAME.getID(), device));
            return;
        }

        // Determine dropped command
        final List<ScanCommand> dropped_commands;

        if (event.data instanceof List)
            dropped_commands = (List<ScanCommand>) event.data;
        else
        {
            // Get command from XML
            final String text = event.data.toString().trim();
            try
            {
                final ByteArrayInputStream stream = new ByteArrayInputStream(text.getBytes());
                final XMLCommandReader reader = new XMLCommandReader(new ScanCommandFactory());
                final List<ScanCommand> received_commands;
                received_commands = reader.readXMLStream(stream);
                stream.close();
                dropped_commands = received_commands;
            }
            catch (Exception ex)
            {   // Cannot handle the text
                throw new Exception(NLS.bind(Messages.XMLCommandErrorFmt, text), ex);
            }
        }

        if (target == null)
        {
            // Add to end of commands
            final ScanCommand location = commands.size() > 0
                    ? commands.get(commands.size()-1)
                    : null;
            editor.executeForUndo(new InsertOperation(editor, commands, location,
                    dropped_commands, true));
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
                        dropped_commands, true));
            }
            else
            {
                final boolean after = target.section != TreeItemInfo.Section.UPPER;
                editor.executeForUndo(new InsertOperation(editor, commands, target.command,
                        dropped_commands, after));
            }
        }

        // Set selection to new command, which also asserts that it is visible
        tree_view.setSelection(new StructuredSelection(dropped_commands.get(0)));
    }

    /** Attempt conversion of device name into alias
     *  @param name Device name
     *  @return Alias, if known. Otherwise name is returned as is.
     */
    protected String getAlias(final String name)
    {
        final DeviceInfo[] devices = editor.getDevices();
        if (devices != null)
        {
            for (DeviceInfo device : devices)
                if (name.equals(device.getName()))
                    return device.getAlias();
        }
        return name;
    }

    /** @param text Text that was received via a 'drop'
     *  @return <code>true</code> if text could represent a PV name
     */
    protected boolean isPossiblePVName(final String text)
    {
        return text.matches("[A-Za-z0-9:-_]+"); //$NON-NLS-1$
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

    /** @param address Address of the 'active' command to highlight
     *  @return <code>true</code> if that was a change
     */
    public boolean setActiveCommand(final long address)
    {
        if (!label_provider.setActiveCommand(address))
            return false;
        final Control control = tree_view.getControl();
        if (control.isDisposed())
            return false;
        control.getDisplay().asyncExec(new Runnable()
        {
            @Override
            public void run()
            {
                if (! control.isDisposed())
                    tree_view.refresh();
            }
        });
        return true;
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
