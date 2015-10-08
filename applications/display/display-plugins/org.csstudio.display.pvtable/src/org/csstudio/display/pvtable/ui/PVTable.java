/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.display.pvtable.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import org.csstudio.autocomplete.ui.AutoCompleteTypes;
import org.csstudio.autocomplete.ui.AutoCompleteUIHelper;
import org.csstudio.csdata.ProcessVariable;
import org.csstudio.display.pvtable.Messages;
import org.csstudio.display.pvtable.Preferences;
import org.csstudio.display.pvtable.model.PVTableItem;
import org.csstudio.display.pvtable.model.PVTableModel;
import org.csstudio.display.pvtable.model.PVTableModelListener;
import org.csstudio.display.pvtable.model.SavedValue;
import org.csstudio.display.pvtable.model.TimestampHelper;
import org.csstudio.display.pvtable.model.VTypeHelper;
import org.csstudio.ui.util.MinSizeTableColumnLayout;
import org.csstudio.ui.util.dnd.ControlSystemDragSource;
import org.csstudio.ui.util.dnd.SerializableItemTransfer;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPartSite;
import org.diirt.vtype.AlarmSeverity;
import org.diirt.vtype.VEnum;
import org.diirt.vtype.VType;

/** PV Table GUI
 *  @author Kay Kasemir
 *  @author Kunal Shroff - Original PVManager version of PVTable that used similar editing behavior
 */
public class PVTable implements PVTableModelListener
{
    private PVTableModel model = null;
    private TableViewer viewer;
    private Color changed_background;
    private Map<AlarmSeverity, Color> alarm_colors = new HashMap<AlarmSeverity, Color>();

    /** Initialize
     *  @param parent Parent widget
     *  @param site Workbench site or <code>null</code>
     */
    public PVTable(final Composite parent, final IWorkbenchPartSite site)
    {
        initColors(parent.getDisplay());
        createComponents(parent);
        createContextMenu(viewer, site);

        hookDragDrop();

        // Disconnect from model when disposed
        parent.addDisposeListener((DisposeEvent e) ->
        {
            if (model != null)
            {
                model.removeListener(PVTable.this);
                model.dispose();
            }
        });
    }

    /** Initialize colors
     *  @param display Display
     */
    private void initColors(final Display display)
    {
        changed_background = display.getSystemColor(SWT.COLOR_CYAN);
        alarm_colors.put(AlarmSeverity.MINOR, display.getSystemColor(SWT.COLOR_DARK_YELLOW));
        alarm_colors.put(AlarmSeverity.MAJOR, display.getSystemColor(SWT.COLOR_RED));
        alarm_colors.put(AlarmSeverity.INVALID, display.getSystemColor(SWT.COLOR_MAGENTA));
        alarm_colors.put(AlarmSeverity.UNDEFINED, display.getSystemColor(SWT.COLOR_DARK_RED));
    }

    /** Create GUI components
     *  @param parent
     */
    private void createComponents(final Composite parent)
    {
        // TableColumnLayout requires table to be only child of parent.
        // To assert that'll always be the case, create box.
        final Composite table_box = new Composite(parent, 0);
        parent.setLayout(new FillLayout());
        final TableColumnLayout layout = new MinSizeTableColumnLayout(50);
        table_box.setLayout(layout);

        // Tried CheckboxTableViewer, but it lead to inconsistent refreshes:
        // Rows would appear blank. Didn't investigate further, stuck with TableViewer.
        viewer = new TableViewer(table_box, SWT.CHECK | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | SWT.VIRTUAL);

        final Table table = viewer.getTable();
        table.setHeaderVisible(true);
        table.setLinesVisible(true);

        // PV Name column: Has the 'check box' to select, allows editing
        final TableViewerColumn pv_column = createColumn(viewer, layout, Messages.PV, 75, 100,
            new PVTableCellLabelProvider()
            {
                @Override
                public void update(final ViewerCell cell)
                {
                    final TableItem tab_item = (TableItem) cell.getItem();
                    final PVTableItem item = (PVTableItem) cell.getElement();

                    if (item.isComment())
                    {
                        cell.setText(item.getComment());
                        cell.setForeground(tab_item.getDisplay().getSystemColor(SWT.COLOR_BLUE));
                    }
                    else
                    {
                        tab_item.setChecked(item.isSelected());
                        cell.setText(item.getName());
                        cell.setForeground(null);
                    }
                }
            });
        pv_column.setEditingSupport(new EditingSupport(viewer)
        {
            @Override
            protected boolean canEdit(final Object element)
            {
                return true;
            }

            @Override
            protected CellEditor getCellEditor(final Object element)
            {
                return AutoCompleteUIHelper
                        .createAutoCompleteTextCellEditor(table, AutoCompleteTypes.PV);
            }

            @Override
            protected void setValue(final Object element, final Object value)
            {
                final String new_name = value.toString().trim();
                final PVTableItem item = (PVTableItem) element;

                if (item == PVTableModelContentProvider.NEW_ITEM)
                {   // Set name of magic NEW_ITEM: Add new item
                    if (new_name.isEmpty())
                        return;
                    model.addItem(new_name);
                    viewer.setInput(model);
                }
                else if (new_name.isEmpty())
                {   // Setting name to nothing: Remove item
                    model.removeItem(item);
                    viewer.remove(item);
                }
                else // Change name of existing item
                {
                    item.updateName(new_name);
                    model.fireModelChange();
                }
            }

            @Override
            protected Object getValue(final Object element)
            {
                final PVTableItem item = (PVTableItem) element;
                return item.getName();
            }
        });
        // Allow check/uncheck to select items for restore
        viewer.getTable().addListener(SWT.Selection, new Listener()
        {
            @Override
            public void handleEvent(final Event event)
            {
                final TableItem tab_item = (TableItem) event.item;
                final PVTableItem item = (PVTableItem) tab_item.getData();
                if (event.detail != SWT.CHECK)
                    return;
                if (item.isComment())
                {
                    tab_item.setChecked(false);
                    return;
                }
                // Toggle selection of PVTableItem, then update
                // the TableItem to reflect current state.
                // When instead updating the PVTableItem from the
                // TableItem's check mark, the result was inconsistent
                // behavior for selected rows: Could not un-check the
                // checkbox for a selected row...
                if (item == PVTableModelContentProvider.NEW_ITEM)
                    item.setSelected(false);
                else
                    item.setSelected(! item.isSelected());
                tab_item.setChecked(item.isSelected());
            }
        });

        // Optionally, add column to display item.getDescription()
        if (Preferences.showDescription())
            createColumn(viewer, layout, Messages.Description, 50, 40,
                new PVTableCellLabelProvider()
                {
                    @Override
                    public void update(final ViewerCell cell)
                    {
                        final PVTableItem item = (PVTableItem) cell.getElement();
                        cell.setText(item.getDescription());
                    }
                });

        // Read-only time stamp
        createColumn(viewer, layout, Messages.Time, 50, 100,
            new PVTableCellLabelProvider()
            {
                @Override
                public void update(final ViewerCell cell)
                {
                    final PVTableItem item = (PVTableItem) cell.getElement();
                    final VType value = item.getValue();
                    if (value == null  ||  item.isComment())
                        cell.setText(""); //$NON-NLS-1$
                    else
                        cell.setText(TimestampHelper.format(VTypeHelper.getTimestamp(value)));
                }
            });
        // Editable value
        final TableViewerColumn value_column = createColumn(viewer, layout, Messages.Value, 100, 50,
            new PVTableCellLabelProviderWithChangeIndicator(changed_background)
            {
                @Override
                public void update(final ViewerCell cell)
                {
                    final PVTableItem item = (PVTableItem) cell.getElement();
                    final VType value = item.getValue();
                    if (value == null)
                        cell.setText(""); //$NON-NLS-1$
                    else
                        cell.setText(VTypeHelper.toString(value));
                    super.update(cell);
                }
            });
        value_column.setEditingSupport(new EditingSupport(viewer)
        {
            /** When a combo box editor is created, its value must be the integer index.
             *  Note that this variable is shared for all rows.
             *  When editing, the UI thread calls getCellEditor() for the row,
             *  then get/setValue().
             */
            private boolean need_index = false;

            @Override
            protected boolean canEdit(final Object element)
            {
                final PVTableItem item = (PVTableItem) element;
                return item.isWritable();
            }

            @Override
            protected CellEditor getCellEditor(final Object element)
            {
                final PVTableItem item = (PVTableItem) element;
                final String[] options = item.getValueOptions();
                need_index = options != null;
                if (need_index)
                    return new ComboBoxCellEditor(table, options, SWT.READ_ONLY);
                return new TextCellEditor(table);
            }

            @Override
            protected void setValue(final Object element, final Object value)
            {
                final PVTableItem item = (PVTableItem) element;
                item.setValue(value.toString());
            }

            @Override
            protected Object getValue(final Object element)
            {
                final PVTableItem item = (PVTableItem) element;
                final VType value = item.getValue();
                if (need_index)
                {
                    if (value == null  ||  ! (value instanceof VEnum))
                        return 0;
                    return ((VEnum)value).getIndex();
                }
                if (value == null)
                    return ""; //$NON-NLS-1$
                return VTypeHelper.toString(value);
            }
        });
        // Remaining columns are read-only
        createColumn(viewer, layout, Messages.Alarm, 100, 50,
            new PVTableCellLabelProvider()
            {
                @Override
                public void update(final ViewerCell cell)
                {
                    final PVTableItem item = (PVTableItem) cell.getElement();
                    final VType value = item.getValue();
                    if (value == null)
                        cell.setText(""); //$NON-NLS-1$
                    else
                    {
                        cell.setText(VTypeHelper.formatAlarm(value));
                        cell.setForeground(alarm_colors.get(VTypeHelper.getSeverity(value)));
                    }
                }
            });
        createColumn(viewer, layout, Messages.Saved, 100, 50,
            new PVTableCellLabelProviderWithChangeIndicator(changed_background)
            {
                @Override
                public void update(final ViewerCell cell)
                {
                    final PVTableItem item = (PVTableItem) cell.getElement();
                    final SavedValue value = item.getSavedValue().orElse(null);
                    if (value == null)
                        cell.setText(""); //$NON-NLS-1$
                    else
                        cell.setText(value.toString());
                    super.update(cell);
                }
            });

        ColumnViewerToolTipSupport.enableFor(viewer);

        viewer.setContentProvider(new PVTableModelContentProvider());
    }

    /** Helper for creating table column
     *  @param viewer
     *  @param layout
     *  @param header
     *  @param weight
     *  @param min_width
     *  @param label_provider
     *  @return Created viewer column
     */
    private TableViewerColumn createColumn(final TableViewer viewer,
            final TableColumnLayout layout,
            final String header,
            final int weight,
            final int min_width,
            final CellLabelProvider label_provider)
    {
        final TableViewerColumn view_col = new TableViewerColumn(viewer, 0);
        final TableColumn col = view_col.getColumn();
        col.setText(header);
        col.setResizable(true);
        col.setMoveable(true);
        layout.setColumnData(col, new ColumnWeightData(weight, min_width));
        view_col.setLabelProvider(label_provider);
        return view_col;
    }

    /** Helper for creating context menu
     *  @param viewer
     *  @param site
     */
    private void createContextMenu(final TableViewer viewer, IWorkbenchPartSite site)
    {
        final MenuManager manager = new MenuManager();
        manager.add(new SnapshotAction(viewer));
        manager.add(new RestoreAction(viewer));
        manager.add(new SelectAllAction(viewer));
        manager.add(new DeSelectAllAction(viewer));
        manager.add(new Separator());
        manager.add(new SnapshotCurrentSelectionAction(viewer));
        manager.add(new RestoreCurrentSelectionAction(viewer));
        manager.add(new Separator());
        manager.add(new ToleranceAction(viewer));
        manager.add(new Separator());
        manager.add(new InsertAction(viewer));
        manager.add(new DeleteAction(viewer));
        manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));

        final Control control = viewer.getControl();
        final Menu menu = manager.createContextMenu(control);
        control.setMenu(menu);

        if (site != null)
            site.registerContextMenu(manager, viewer);
    }

    /** Set to currently dragged items to allow 'drop'
     *  to move them instead of adding duplicates.
     */
    private final AtomicReference<List<PVTableItem>> dragged_items = new AtomicReference<>(Collections.emptyList());

    private void hookDragDrop()
    {
        // Support 'dragging' ProcessVariable[]
        // Tried to drag PVTableItem[] to ease accepting that within this or other table,
        // but combination of ControlSystemDragSource and registered adapter from PVTableItem to ProcessVariable
        // always resulted in receiving ProcessVariable[]
        new ControlSystemDragSource(viewer.getTable())
        {
            @Override
            public Object getSelection()
            {
                final List<PVTableItem> items = new ArrayList<>();
                final IStructuredSelection sel = (IStructuredSelection)viewer.getSelection();
                if (sel != null)
                {
                    final Iterator<?> iterator = sel.iterator();
                    while (iterator.hasNext())
                        items.add((PVTableItem)iterator.next());
                }
                dragged_items.set(items);
                final ProcessVariable[] pvs = new ProcessVariable[items.size()];
                for (int i=0; i<pvs.length; ++i)
                    pvs[i] = new ProcessVariable(items.get(i).getName());
                return pvs;
            }
        };

        // Allow 'dropping' PV names
        final DropTarget target = new DropTarget(viewer.getTable(), DND.DROP_COPY | DND.DROP_MOVE | DND.DROP_LINK);
        target.setTransfer(new Transfer[]
        {
            // Tried also receiving PVTableItem[], but always got ProcessVariable[]
            SerializableItemTransfer.getTransfer(ProcessVariable[].class.getName()),
            SerializableItemTransfer.getTransfer(ProcessVariable.class.getName()),
            TextTransfer.getInstance()
        });
        target.addDropListener(new DropTargetAdapter()
        {
            @Override
            public void dragEnter(final DropTargetEvent event)
            {
                if ((event.operations & DND.DROP_COPY) != 0)
                    event.detail = DND.DROP_COPY;
                else
                    event.detail = DND.DROP_NONE;
            }

            @Override
            public void drop(final DropTargetEvent event)
            {
                // Dropping on a valid existing item?
                PVTableItem existing = null;
                if (event.item instanceof TableItem)
                {
                    final TableItem tab_item = (TableItem) event.item;
                    if (tab_item.getData() instanceof PVTableItem)
                    {
                        existing = (PVTableItem) tab_item.getData();
                        if (existing == PVTableModelContentProvider.NEW_ITEM)
                            existing = null;
                    }
                }

                // Was this data dragged from the PV table?
                final List<PVTableItem> moved = dragged_items.getAndSet(Collections.emptyList());
                if (moved.size() > 0)
                {   // Move items within the table
                    for (PVTableItem item : moved)
                    {
                        System.out.println("Moving original " + item);
                        model.removeItem(item);
                        model.addItemAbove(existing, item);
                    }
                }
                else
                {   // Add items received from outside this table
                    final Object item = event.data;
                    if (item instanceof ProcessVariable)
                        model.addItemAbove(existing, ((ProcessVariable)item).getName());
                    else if (item instanceof String)
                        model.addItemAbove(existing, (String)item);
                    else if (item instanceof ProcessVariable[])
                    {
                        for (ProcessVariable pv : (ProcessVariable[]) item)
                            model.addItemAbove(existing, pv.getName());
                    }
                    else
                        return;
                }

                viewer.setInput(model);
            }
        });
    }

    /** @return Table viewer */
    public TableViewer getTableViewer()
    {
        return viewer;
    }

    /** @return PV table model */
    public PVTableModel getModel()
    {
        return  model;
    }

    /** @param model Model to display in table */
    public void setModel(final PVTableModel model)
    {
        // Remove this as listener from previous model
        if (this.model != null)
            this.model.removeListener(this);
        this.model = model;
        viewer.setInput(model);
        model.addListener(this);
    }

    /** {@inheritDoc} */
    @Override
    public void tableItemSelectionChanged(final PVTableItem item)
    {
        // Ignore
    }

    /** {@inheritDoc} */
    @Override
    public void tableItemChanged(final PVTableItem item)
    {
        final Table table = viewer.getTable();
        if (table.isDisposed())
            return;
        table.getDisplay().asyncExec(() ->
        {
            if (!table.isDisposed()  &&  !viewer.isCellEditorActive())
                viewer.refresh(item);
        });
    }

    /** {@inheritDoc} */
    @Override
    public void tableItemsChanged()
    {
        final Table table = viewer.getTable();
        if (table.isDisposed())
            return;
        table.getDisplay().asyncExec(() ->
        {
            if (!table.isDisposed()  &&  !viewer.isCellEditorActive())
                viewer.refresh();
        });
    }

    /** {@inheritDoc} */
    @Override
    public void modelChanged()
    {
        // Ignore
    }
}
