/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.display.pace.gui;

import java.util.ArrayList;

import org.csstudio.display.pace.Messages;
import org.csstudio.display.pace.model.Cell;
import org.csstudio.display.pace.model.Column;
import org.csstudio.display.pace.model.Instance;
import org.csstudio.display.pace.model.Model;
import org.csstudio.display.pace.model.ModelListener;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IWorkbenchPartSite;

/** GUI for the Model
 *  <p>
 *  Creates TableViewer for displaying and editing the Model's Instance rows,
 *  updating the GUI in response to model changes.
 *  Can also act as an ISelectionProvider, handing out the currently
 *  selected Cell (PV).
 *  @author Delphy Nypaver Armstrong
 *  @author Kay Kasemir
 *
 *
 *    reviewed by Delphy 01/28/09
 */
public class GUI implements ModelListener, IMenuListener, ISelectionProvider
{
    /** Minimum column width */
    private static final int MIN_SIZE = 80;

    /** Model handed by this GUI */
    final private Model model;

    /** Table Viewer for Model's "Instance" rows */
    private TableViewer table_viewer;

    /** Currently selected Cell in Model or <code>null</code> */
    private Cell selected_cell = null;

    /** Listeners that registered for this ISelectionProvider */
    final private ArrayList<ISelectionChangedListener> listeners =
        new ArrayList<ISelectionChangedListener>();

    /** Throttle for cell updates.
     *  The model will 'trigger' this throttle in <code>cellUpdate</code>.
     *  For individual cell changes, the throttle causes an <code>update</code>
     *  after a small delay, but bursts of cell changes get combined into a
     *  delayed overall refresh of the table, since that is more efficient
     *  than many individual cell updates.
     *
     *  Statistics are always iffy, but with the "LLRF_HPM_ADC_Limits"
     *  config file which contains about 7 * 97 * 2Hz, i.e. about 1000
     *  updates per second, the CPU load dropped from near 100% to about
     *  15% after replacing direct cell updates with this throttle
     *  on a 2.8GHz Mac.
     */
    final private GUIUpdateThrottle<Cell> gui_update_throttle
       = new GUIUpdateThrottle<Cell>(300, 2000)
    {
        @Override
        protected void update(final Cell cell)
        {
            final Table table = table_viewer.getTable();
            if (table.isDisposed())
                return;
            // Call can originate from non-UI thread in case of PV updates,
            // so transfer to UI thread when accessing the SWT table
            table.getDisplay().asyncExec(new Runnable()
            {
                @Override
                public void run()
                {
                    if (table.isDisposed())
                        return;
                    // If currently in a cell editor, do not refresh the table
                    // because that would close the cell editor
                    if (table_viewer.isCellEditorActive())
                    {   // re-schedule
                        GUI.this.gui_update_throttle.trigger(cell);
                        return;
                    }
                    if (cell == null)
                        table_viewer.refresh();
                    else
                        table_viewer.update(cell.getInstance(), null);
                }
            });
        }
    };

    /** Initialize GUI: Create widgets that display the model
     *  @param parent Parent widget
     *  @param model Model to display
     *  @param site Workbench site where GUI can act as ISelectionProvider,
     *              or <code>null</code>.
     */
    public GUI(final Composite parent, final Model model,
            final IWorkbenchPartSite site)
    {
        this.model = model;
        createComponents(parent, model);
        addCellTracker();
        model.addListener(this);
        createContextMenu(site);
        if (site != null)
            site.setSelectionProvider(this);
    }

    // @see ISelectionProvider
    @Override
    public void addSelectionChangedListener(
            final ISelectionChangedListener listener)
    {
        listeners.add(listener);
    }

    // @see ISelectionProvider
    @Override
    public void removeSelectionChangedListener(
            final ISelectionChangedListener listener)
    {
        listeners.remove(listener);
    }

    // @see ISelectionProvider
    @Override
    public void setSelection(final ISelection selection)
    {
        // NOP, don't allow outside code to change selection
    }

    /** Provide selected Cells of model or <code>null</code>
     *  @see ISelectionProvider
     */
    @Override
    public ISelection getSelection()
    {
        if (selected_cell == null)
            return null;
        return new StructuredSelection(selected_cell);
    }

    /** Create GUI elements
     *  @param parent Parent widget
     *  @param model Model to display
     */
    private void createComponents(final Composite parent, final Model model)
    {
        // Note: TableColumnLayout requires the TableViewer to be in its
        // own Composite!
        final TableColumnLayout table_layout = new TableColumnLayout();
        parent.setLayout(table_layout);

        // Create TableViewer that displays Model in Table
        table_viewer = new TableViewer(parent,
                SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI | SWT.VIRTUAL |
                SWT.FULL_SELECTION);
        // Some tweaks to the underlying table widget
        final Table table = table_viewer.getTable();
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        createColumns(table_viewer, table_layout);

        ColumnViewerToolTipSupport.enableFor(table_viewer, ToolTip.NO_RECREATE);

        // Connect TableViewer to the Model: Provide content from model...
        table_viewer.setContentProvider(new ModelInstanceProvider());

        // table viewer is set up to handle data of type Model.
        // Connect to specific model
        table_viewer.setInput(model);
    }

    /** @param table_viewer {@link TableViewer} to which to add columns for GlobalAlarm display
     *  @param table_layout {@link TableColumnLayout} to use for column auto-sizing
     */
    private void createColumns(final TableViewer table_viewer, final TableColumnLayout table_layout)
    {
        // Fixed 'System' column
        TableViewerColumn view_col = new TableViewerColumn(table_viewer, 0);
        TableColumn col = view_col.getColumn();
        col.setText(Messages.SystemColumn);
        table_layout.setColumnData(col, new ColumnWeightData(100, MIN_SIZE));
//        col.setMoveable(true);
        view_col.setLabelProvider(new InstanceLabelProvider(-1));

        // Model-driven data columns
        for (int c=0;  c<model.getColumnCount();  ++c)
        {
            final Column model_col = model.getColumn(c);

            view_col = new TableViewerColumn(table_viewer, 0);
            col = view_col.getColumn();
            col.setText(model_col.getName());
            table_layout.setColumnData(col, new ColumnWeightData(100, MIN_SIZE));
            col.setMoveable(true);
            // Tell column how to display the model elements
            view_col.setLabelProvider(new InstanceLabelProvider(c));
            if (! model_col.isReadonly())
                view_col.setEditingSupport(new ModelCellEditor(table_viewer, c));
            // Clicking on column header allows entry into _all_ cells of model
            col.addSelectionListener(new SelectionAdapter()
            {
                @Override
                public void widgetSelected(SelectionEvent e)
                {
                    setAllCellsInColumn(model_col);
                }
            });
        }
    }

    /** Set all cells in column to a user-entered value
     *  @param column Column to set
     */
    protected void setAllCellsInColumn(final Column column)
    {
        // Any rows to set?
        if (model.getInstanceCount() <= 0  ||
            column.isReadonly())
            return;
        // Using value of first selected test as suggestion,
        // prompt for value to be put into all selected cells
        final String message =
            NLS.bind(Messages.SetColumnValue_Msg, column.getName());
        final InputDialog input =
            new InputDialog(table_viewer.getTable().getShell(),
                            Messages.SetValue_Title,
                            message,
                            model.getInstance(0).getCell(column).getValue(), null);
        if (input.open() != Window.OK)
            return;
        // Update value of selected cells
        final String user_value = input.getValue();
        for (int row=0;  row < model.getInstanceCount();  ++row)
            model.getInstance(row).getCell(column).setUserValue(user_value);
    }

    /** Update <code>selected_cell</code> from mouse position */
    private void addCellTracker()
    {
        // This part is a bit unfortunate:
        // The TableViewer handles most of the interfacing between the
        // SWT table and the Model, but it only handles selections
        // on the row/Instance level, while we want to perform certain
        // tasks on the Cell level.
        // This code tracks the currently selected Model Cell, the Cell
        // under the last mouse click.
        // Since the Cell has a reference to its Column,
        // and the TableViewer provides all selected rows/Instances,
        // this then leads to all selected Cells.
        table_viewer.getTable().addListener(SWT.MouseDown, new Listener()
        {
            @Override
            public void handleEvent(final Event event)
            {
                // Get cell in SWT Table from mouse position
                final Point point = new Point(event.x, event.y);
                final ViewerCell viewer_cell = table_viewer.getCell(point);
                if (viewer_cell == null)
                {   // Didn't hit table??
                    selected_cell = null;
                    return;
                }
                // Get Model's row/Instance, then Model's Cell in there
                final Instance instance = (Instance) viewer_cell.getElement();
                final int col_idx = viewer_cell.getColumnIndex();
                if (col_idx <= 0)
                {   // Special first column with instance name
                    selected_cell = null;
                    return;
                }
                selected_cell = instance.getCell(col_idx-1);

                // Update selection listeners about newly selected cells
                for (ISelectionChangedListener listener : listeners)
                    listener.selectionChanged(new SelectionChangedEvent(GUI.this, getSelection()));
            }
        });
    }

    /** Create dynamic context menu
     *  @param site Workbench site where menu will get registered or <code>null</code>
     *  @see #menuAboutToShow(IMenuManager)
     */
    private void createContextMenu(final IWorkbenchPartSite site)
    {
        final MenuManager manager = new MenuManager();
        manager.setRemoveAllWhenShown(true);
        manager.addMenuListener(this);
        final Table table = table_viewer.getTable();
        table.setMenu(manager.createContextMenu(table));
        // Allow CSS extensions to add to the context menu
        if (site != null)
            site.registerContextMenu(manager, this);
    }

    /** Fill context menu with actions for the currently selected cells.
     *  @param manager Menu manager
     *  @see IMenuListener
     */
    @Override
    public void menuAboutToShow(final IMenuManager manager)
    {
        final Cell cells[] = getSelectedCells();
        manager.add(new RestoreCellAction(cells));
        manager.add(new SetCellValueAction(table_viewer.getTable().getShell(),
                                           cells));
        // Placeholder for CSS PV contributions
        manager.add(new GroupMarker("additions")); //$NON-NLS-1$
        manager.add(new Separator());
    }

    /** @return Currently selected editable(!) cells from column that
     *          was latest clicked, or <code>null</code>
     */
    private Cell [] getSelectedCells()
    {
        // Anything selected at all?
        if (selected_cell == null)
            return null;

        return getSelectedCells(selected_cell.getColumn());
    }

    /** @param column Column from which to get the cells
     *  @return Currently selected editable(!) cells or <code>null</code>
     */
    private Cell[] getSelectedCells(final Column column)
    {
        // Read-only column has no editable cells
        if (column.isReadonly())
            return null;
        // TableViewer selection has the currently selected _Instance_ entries
        // of the Model.
        // Use the selected Column (see addCellTracker) to
        // get the currently selected _Cells_
        final Object[] sel =
            ((IStructuredSelection) table_viewer.getSelection()).toArray();
        final Cell cells[] = new Cell[sel.length];
        for (int i = 0; i < sel.length; i++)
        {
            final Instance instance = (Instance) sel[i];
            cells[i] = instance.getCell(column);
        }
        return cells;
    }

    /** Update table when Cell in Model changed.
     *  <p>
     *  Could be called directly by Cell because PV sent new value.
     *  Could be called by cell after we edited it, or maybe in the
     *  future other forces will change the cell.
     *  In any case: A cell changed, and we have to update the GUI.
     *
     *  @see ModelListener
     */
    @Override
    public void cellUpdate(final Cell cell)
    {
        // Notify the throttle mechanism of changed cell
        gui_update_throttle.trigger(cell);
    }
}
