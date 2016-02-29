/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.ui.alarmtable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.regex.Pattern;

import org.csstudio.alarm.beast.client.AlarmTreeItem;
import org.csstudio.alarm.beast.client.AlarmTreePV;
import org.csstudio.alarm.beast.client.AlarmTreeRoot;
import org.csstudio.alarm.beast.client.GUIUpdateThrottle;
import org.csstudio.alarm.beast.ui.AuthIDs;
import org.csstudio.alarm.beast.ui.ContextMenuHelper;
import org.csstudio.alarm.beast.ui.SelectionHelper;
import org.csstudio.alarm.beast.ui.SeverityColorProvider;
import org.csstudio.alarm.beast.ui.actions.AlarmPerspectiveAction;
import org.csstudio.alarm.beast.ui.actions.ConfigureItemAction;
import org.csstudio.alarm.beast.ui.actions.DisableComponentAction;
import org.csstudio.alarm.beast.ui.clientmodel.AlarmClientModel;
import org.csstudio.alarm.beast.ui.clientmodel.AlarmClientModelListener;
import org.csstudio.apputil.text.RegExHelper;
import org.csstudio.security.SecuritySupport;
import org.csstudio.ui.util.MinSizeTableColumnLayout;
import org.csstudio.ui.util.dnd.ControlSystemDragSource;
import org.csstudio.ui.util.helpers.ComboHistoryHelper;
import org.csstudio.utility.singlesource.SingleSourcePlugin;
import org.csstudio.utility.singlesource.UIHelper.UI;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPartSite;

/** Alarm table GUI
 *
 *  @author Kay Kasemir
 *  @author Jaka Bobnar - Combined/split alarm tables, configurable columns, filtering, blinking
 */
public class GUI extends Composite implements AlarmClientModelListener
{
    /**
     * <code>Blinker</code> is a singleton responsible for synchronous blinking of icons
     * on all existing alarm tables.
     *
     * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
     *
     */
    private static enum Blinker
    {
        INSTANCE;

        private final int period = Preferences.getBlinkingPeriod();
        private final Set<GUI> guis = Collections.synchronizedSet(Collections.newSetFromMap(new WeakHashMap<>()));
        private final Display display = Display.getDefault();
        private boolean active = false;

        void blink()
        {
            if (!display.isDisposed())
            {
                display.timerExec(period, ()->
                {
                    if (active)
                    {
                        for (GUI g : guis)
                        {
                            //if the GUI thread is this thread, blink on this thread,
                            //otherwise delegate to the GUI's thread and wait for it to finish
                            if (display == g.display)
                                g.blink();
                            else if (!g.display.isDisposed())
                                g.display.syncExec(() -> g.blink());
                        }
                        blink();
                    }
                });
            }
        }
        void reset()
        {
            if (!display.isDisposed())
            {
                display.syncExec(() ->
                {
                    for (GUI g : guis)
                        g.icon_provider.reset();
                });
            }
        }
        void add(GUI gui)
        {
            if (!display.isDisposed())
            {
                display.syncExec(() ->
                {
                    guis.add(gui);
                    //when a new gui is added, always reset everything so that all tables are synchronised
                    reset();
                    if (!active)
                    {
                        active = true;
                        blink();
                    }
                });
            }
        }
        void remove(GUI gui)
        {
            if (!display.isDisposed())
            {
                display.syncExec(() ->
                {
                    guis.remove(gui);
                    if (guis.isEmpty())
                        active = false;
                });
            }
        }
    }

    /**
     * Initial place holder for display of alarm counts to allocate enough screen space
     */
    final private static String ALARM_COUNT_PLACEHOLDER = "999999"; //$NON-NLS-1$

    final private Display display;

    /** Labels to show alarm counts */
    private Label current_alarms, acknowledged_alarms;

    /** TableViewer for active alarms */
    private TableViewer active_table_viewer;

    /** TableViewer for acknowledged alarms */
    private TableViewer acknowledged_table_viewer;

    /** PV selection filter text box */
    private Combo filter;

    /** PV un-select button */
    private Button unselect;

    private SeverityColorProvider color_provider;
    private SeverityColorPairProvider color_pair_provider;
    private SeverityIconProvider icon_provider;
    private AlarmTableLabelProvider table_label_provider;

    /** Is something displayed in <code>error_message</code>? */
    private volatile boolean have_error_message = false;

    /** Error message (no server...) */
    private Label error_message;

    private Composite base_composite;

    private final boolean separate_tables;

    private final ColumnWrapper[] columns;

    /** The item used for filtering the alarms */
    private AlarmTreeItem filter_item_parent;
    /** Model with all the alarm information */
    private AlarmClientModel model;
    /** Flag indicating if unacknowledged alarms should blink */
    private boolean blink_unacknowledged;
    /** Are the models writable or not */
    private final boolean writable;
    /** Should the labels with the number of alarms be displayed or not */
    private final boolean show_header;

    /** GUI updates are throttled to reduce flicker */
    final private GUIUpdateThrottle gui_update = new GUIUpdateThrottle()
    {
        @Override
        protected void fire()
        {
            if (display.isDisposed() || model == null)
                return;

            AlarmTreePV[] rawAlarms = model.getActiveAlarms();
            final AlarmTreePV[] filteredAlarms = filter(rawAlarms);
            final int rawAlarmsLength = rawAlarms.length;
            rawAlarms = model.getAcknowledgedAlarms();
            final AlarmTreePV[] filteredAcknowledged = filter(rawAlarms);
            final int rawAcknowledgedAlarmsLength = rawAlarms.length;
            final AlarmTreePV[] combinedAlarms;
            if (separate_tables)
                combinedAlarms = null;
            else
            {
                combinedAlarms = new AlarmTreePV[filteredAlarms.length + filteredAcknowledged.length];
                System.arraycopy(filteredAlarms, 0, combinedAlarms, 0, filteredAlarms.length);
                System.arraycopy(filteredAcknowledged, 0, combinedAlarms, filteredAlarms.length,
                        filteredAcknowledged.length);
            }
            display.syncExec(() ->
            {
                // Display counts, update tables

                // Don't use TableViewer.setInput(), it causes flicker on Linux!
                // active_table_viewer.setInput(model.getActiveAlarms());
                // acknowledged_table_viewer.setInput(model.getAcknowledgedAlarms());
                //
                // Instead, tell ModelInstanceProvider about the data,
                // which then updates the table with setItemCount(), refresh(),
                // as that happens to not flicker.
                updateGUI(rawAlarmsLength,filteredAlarms,rawAcknowledgedAlarmsLength,
                        filteredAcknowledged,combinedAlarms);
            });
        }
    };

    /** Column editor for the 'ACK' column that acknowledges or un-ack's
     *  alarm in that row
     */
    private static class AcknowledgeEditingSupport extends EditingSupport
    {
        public AcknowledgeEditingSupport(ColumnViewer viewer)
        {
            super(viewer);
        }

        @Override
        protected CellEditor getCellEditor(final Object element)
        {
            return new CheckboxCellEditor(((TableViewer) getViewer()).getTable());
        }

        @Override
        protected Object getValue(final Object element)
        {
            return ((AlarmTreePV) element).getSeverity().isActive();
        }

        @Override
        protected void setValue(final Object element, final Object value)
        {
            if (getViewer().isBusy()) return;
            if (value instanceof Boolean)
            {
                if (SecuritySupport.havePermission(AuthIDs.ACKNOWLEDGE))
                    ((AlarmTreePV) element).acknowledge(!(Boolean) value);
            }
        }

        @Override
        protected boolean canEdit(Object element)
        {
            return element instanceof AlarmTreePV;
        }
    }

    /**
     * Initialize GUI
     *
     * @param parent Parent widget
     * @param site Workbench site or <code>null</code>
     * @param writable indicates if this GUI is used in a writable or read only environment
     * @param separate_tables true if two tables should be created (one for acked and one for unacked alarms) or false
     *          if only one table should be created
     * @param columns column configuration for the tables
     * @param sorting_column default sorting column
     * @param sort_up default sorting direction
     * @param show_header true if the header showing the number of all alarms and select field should be displayed or
     *          false otherwise
     */
    public GUI(final Composite parent, final IWorkbenchPartSite site,
            final boolean writable, final boolean separate_tables, final ColumnWrapper[] columns,
            final ColumnInfo sorting_column, boolean sort_up, boolean show_header)
    {
        super(parent,SWT.NONE);
        this.display = parent.getDisplay();
        this.separate_tables = separate_tables;
        this.columns = columns;
        this.writable = writable;
        this.show_header = show_header;
        createComponents(sorting_column, sort_up);

        base_composite.addDisposeListener(new DisposeListener()
        {
            @Override
            public void widgetDisposed(DisposeEvent e)
            {
                if (model != null)
                    model.removeListener(GUI.this);
                gui_update.dispose();
            }
        });
        connectContextMenu(active_table_viewer, site);
        if (separate_tables)
            connectContextMenu(acknowledged_table_viewer, site);


        // Allow 'drag' of alarm info as text
        new ControlSystemDragSource(active_table_viewer.getTable())
        {
            @Override
            public Object getSelection()
            {
                return SelectionHelper.getAlarmTreePVsForDragging((IStructuredSelection) active_table_viewer
                        .getSelection());
            }
        };

        if (separate_tables)
        {
            new ControlSystemDragSource(acknowledged_table_viewer.getTable())
            {
                @Override
                public Object getSelection()
                {
                    return SelectionHelper.getAlarmTreePVsForDragging((IStructuredSelection) acknowledged_table_viewer
                            .getSelection());
                }
            };
        }
        gui_update.trigger();
    }

    @Override
    public void dispose() {
        Blinker.INSTANCE.remove(this);
        super.dispose();
    }

    private void setUpModel()
    {
        if (model == null) return;
        if (model.isServerAlive())
            setErrorMessage(null);
        else
            setErrorMessage(org.csstudio.alarm.beast.ui.Messages.WaitingForServer);
        // Subscribe to model updates, arrange to un-subscribe
        model.addListener(this);
    }

    /** @return Table of active alarms */
    public TableViewer getActiveAlarmTable()
    {
        return active_table_viewer;
    }

    /** @return Table of acknowledged alarms */
    public TableViewer getAcknowledgedAlarmTable()
    {
        return separate_tables ? acknowledged_table_viewer : active_table_viewer;
    }

    /**
     * Create GUI elements
     *
     * @param parent Parent widget
     * @param memento the memento that provides the sorting information
     */
    private void createComponents(ColumnInfo sorting_column, boolean sort_up)
    {
        setLayout(new FillLayout());

        if (separate_tables)
        {
            base_composite = new SashForm(this, SWT.VERTICAL | SWT.SMOOTH);
            base_composite.setLayout(new FillLayout());
            color_provider = new SeverityColorProvider(base_composite);
            color_pair_provider = new SeverityColorPairProvider(base_composite);
            icon_provider = new SeverityIconProvider(base_composite);
            table_label_provider = new AlarmTableLabelProvider(icon_provider, color_provider, color_pair_provider, ColumnInfo.TIME);
            addActiveAlarmSashElement(base_composite);
            addAcknowledgedAlarmSashElement(base_composite);
            ((SashForm) base_composite).setWeights(new int[]{ 80, 20 });
        }
        else
        {
            base_composite = new Composite(this, SWT.NONE);
            base_composite.setLayout(new FillLayout());
            color_provider = new SeverityColorProvider(base_composite);
            color_pair_provider = new SeverityColorPairProvider(base_composite);
            icon_provider = new SeverityIconProvider(base_composite);
            table_label_provider = new AlarmTableLabelProvider(icon_provider, color_provider, color_pair_provider, ColumnInfo.TIME);
            addActiveAlarmSashElement(base_composite);
        }
        syncTables(active_table_viewer, acknowledged_table_viewer, sorting_column, sort_up);
        syncTables(acknowledged_table_viewer, active_table_viewer, sorting_column, sort_up);

        if (show_header) {
            // Update selection in active & ack'ed alarm table
            // in response to filter changes
            final ComboHistoryHelper filter_history = new ComboHistoryHelper(Activator.getDefault().getDialogSettings(),
                    "FILTER", filter) //$NON-NLS-1$
            {
                @Override
                public void itemSelected(final String selection)
                {
                    final String filter_text = filter.getText().trim();
                    // Turn glob-type filter into regex, then pattern
                    final Pattern pattern = Pattern.compile(RegExHelper.fullRegexFromGlob(filter_text),
                            Pattern.CASE_INSENSITIVE);
                    selectFilteredPVs(pattern, active_table_viewer);
                    if (separate_tables)
                    {
                        selectFilteredPVs(pattern, acknowledged_table_viewer);
                    }
                }
            };
            filter_history.loadSettings();

            // Clear filter, un-select all items
            unselect.addSelectionListener(new SelectionAdapter()
            {
                @Override
                public void widgetSelected(SelectionEvent e)
                {
                    filter.setText(""); //$NON-NLS-1$
                    active_table_viewer.setSelection(null, true);
                    if (separate_tables)
                    {
                        acknowledged_table_viewer.setSelection(null, true);
                    }
                }
            });
        }
        gui_update.start();
        blink();
    }

    /**
     * Synchronise the UI actions between the two tables. When a sorting column is selected in the primary table the
     * same column should be selected in the secondary table. Similarly, the moving and resizing of columns is also
     * synchronised.
     *
     * @param primary the table, which is the source of the actions
     * @param secondary the table that is synchronised with the primary table
     * @param sortColumn the column that is being sorted by default
     * @param sortUp default sorting direction
     */
    private void syncTables(final TableViewer primary, final TableViewer secondary, ColumnInfo sortColumn,
            boolean sortUp)
    {
        if (primary == null)
            return;
        TableColumn[] priColumns = primary.getTable().getColumns();
        TableColumn[] secColumns = secondary == null ? priColumns : secondary.getTable().getColumns();
        boolean applied = false;
        AlarmColumnSortingSelector defaultListener = null;
        for (int i = 0; i < priColumns.length; i++)
        {
            final TableColumn c = priColumns[i];
            final TableColumn s = secColumns[i];
            final ColumnWrapper w = getColumnWrapper(i);
            AlarmColumnSortingSelector listener = new AlarmColumnSortingSelector(primary, secondary, c, s,
                    w.getColumnInfo());
            c.addSelectionListener(listener);
            if (secondary != null)
            {
                c.addControlListener(new ControlAdapter()
                {
                    @Override
                    public void controlResized(ControlEvent e)
                    {
                        if (Math.abs(s.getWidth() - c.getWidth()) > 1)
                        {
                            s.setWidth(c.getWidth());
                            w.setMinWidth(c.getWidth());
                        }
                    }

                    @Override
                    public void controlMoved(ControlEvent e)
                    {
                        secondary.getTable().setColumnOrder(primary.getTable().getColumnOrder());
                    }
                });
            }
            if (w.getColumnInfo() == sortColumn)
            {
                listener.setSortDirection(sortUp);
                applied = true;
            }
            else if (defaultListener == null && w.getColumnInfo() != ColumnInfo.ACK)
            {
                defaultListener = listener;
            }
        }
        if (!applied && defaultListener != null)
        {
            defaultListener.setSortDirection(sortUp);
        }
    }

    /**
     * Return the column wrapper that represents the table column at the given index.
     *
     * @param tableColumnIndex the table column index
     * @return the column wrapper that matches the table column (the n-th visible wrapper)
     */
    private ColumnWrapper getColumnWrapper(int tableColumnIndex)
    {
        int idx = -1;
        for (ColumnWrapper cw : columns)
        {
            if (cw.isVisible())
                idx++;
            if (tableColumnIndex == idx)
                return cw;
        }
        return null;
    }

    /**
     * Updates the given columns with the order that is currently applied to the tables. Most of the time the orders are
     * the same, unless user moved the columns around by dragging their headers to a different position.
     *
     * @param columns the columns to update with the order
     */
    public void updateColumnOrder(ColumnWrapper[] columns)
    {
        if (active_table_viewer.getTable().isDisposed())
            return;
        int[] order = active_table_viewer.getTable().getColumnOrder();
        ColumnWrapper[] ret = new ColumnWrapper[columns.length];
        for (int i = 0; i < order.length; i++)
            ret[i] = columns[order[i]];
        int j = 0;
        for (int i = 0; i < columns.length; i++)
        {
            if (!columns[i].isVisible())
            {
                for (; j < ret.length; j++)
                {
                    if (ret[j] == null)
                    {
                        ret[j] = columns[i];
                        break;
                    }
                }
            }
        }
        for (int i = 0; i < columns.length; i++)
            columns[i] = ret[i];
    }

    private void blink()
    {
        if (blink_unacknowledged)
        {
            icon_provider.toggle();
            if (!active_table_viewer.getTable().isDisposed())
            {
                // because of lazy label provider refresh is faster than updating individual cells
                if (!active_table_viewer.isBusy())
                    active_table_viewer.refresh();
                if (acknowledged_table_viewer != null && !acknowledged_table_viewer.isBusy())
                    acknowledged_table_viewer.refresh();
            }
        }
    }

    /**
     * @return the column info of the column that is currently used as the sorting key in the table
     */
    public ColumnInfo getSortingColumn()
    {
        final Table table = active_table_viewer.getTable();
        final TableColumn sort_column = table.getSortColumn();
        if (sort_column == null)
            return null;

        final int col_count = table.getColumnCount();
        for (int column = 0; column < col_count; ++column)
        {
            if (table.getColumn(column) == sort_column)
            {
                int count = 0;
                for (int i = 0; i < columns.length; i++)
                {
                    if (columns[i].isVisible())
                    {
                        if (count == column)
                        {
                            return columns[i].getColumnInfo();
                        }
                        count++;
                    }
                }
            }
        }
        return null;
    }

    /**
     * @return true if sorting direction is equals to {@link SWT#UP} or false otherwise
     */
    public boolean isSortingUp()
    {
        final Table table = active_table_viewer.getTable();
        return table.getSortDirection() == SWT.UP;
    }

    /**
     * Add the sash element for active alarms
     *
     * @param parent the parent composite
     */
    private void addActiveAlarmSashElement(final Composite parent)
    {
        final Composite box = new Composite(parent, SWT.BORDER);
        box.setLayout(new GridLayout(5,false));

        if (show_header) {
            current_alarms = new Label(box, SWT.NONE);
            current_alarms.setText(NLS.bind(Messages.CurrentAlarmsFmt, new Object[]
            { ALARM_COUNT_PLACEHOLDER, ALARM_COUNT_PLACEHOLDER, ALARM_COUNT_PLACEHOLDER }));
            current_alarms.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,true,false));

            error_message = new Label(box, SWT.NONE);
            error_message.setLayoutData(new GridData(SWT.END,SWT.CENTER,false,false));

            Label l = new Label(box, SWT.NONE);
            l.setText(Messages.Filter);
            l.setLayoutData(new GridData());

            filter = new Combo(box, SWT.BORDER);
            filter.setToolTipText(Messages.FilterTT);
            filter.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,true,false));

            unselect = new Button(box, SWT.PUSH);
            unselect.setText(Messages.Unselect);
            unselect.setToolTipText(Messages.UnselectTT);
            unselect.setLayoutData(new GridData(SWT.END,SWT.CENTER,false,false));

            if (!separate_tables)
            {
                acknowledged_alarms = new Label(box, SWT.NONE);
                acknowledged_alarms.setText(NLS.bind(Messages.AcknowledgedAlarmsFmt, new Object[]
                { ALARM_COUNT_PLACEHOLDER, ALARM_COUNT_PLACEHOLDER, ALARM_COUNT_PLACEHOLDER }));
                acknowledged_alarms.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,true,false,5,1));
            }
        }


        // Table w/ active alarms
        active_table_viewer = createAlarmTable(box, true);
        active_table_viewer.setInput(null);
    }

    /**
     * Add the sash element for acknowledged alarms
     *
     * @param parent the parent composite
     */
    private void addAcknowledgedAlarmSashElement(final Composite parent)
    {
        final Composite box = new Composite(parent, SWT.BORDER);
        box.setLayout(new GridLayout(1,false));

        if (show_header) {
            acknowledged_alarms = new Label(box, SWT.NONE);
            acknowledged_alarms.setText(NLS.bind(Messages.AcknowledgedAlarmsFmt, ALARM_COUNT_PLACEHOLDER));
            acknowledged_alarms.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,true,false));
        }

        // Table w/ ack'ed alarms
        acknowledged_table_viewer = createAlarmTable(box, false);
        acknowledged_table_viewer.setInput(null);
    }

    /**
     * Select PVs in table that match filter expression
     *
     * @param pattern
     *            PV name pattern ('vac', 'amp*trip')
     * @param table_viewer
     *            Table in which to select PVs
     */
    private void selectFilteredPVs(final Pattern pattern, final TableViewer table_viewer)
    {
        final AlarmTreePV pvs[] = ((AlarmTableContentProvider) table_viewer.getContentProvider()).getAlarms();
        final ArrayList<AlarmTreePV> selected = new ArrayList<AlarmTreePV>();
        for (AlarmTreePV pv : pvs)
        {
            if (pattern.matcher(pv.getName()).matches() || pattern.matcher(pv.getDescription()).matches())
                selected.add(pv);
        }
        table_viewer.setSelection(new StructuredSelection(selected), true);
    }

    /**
     * Create a table viewer for displaying alarms
     *
     * @param parent Parent widget, uses GridLayout
     * @param is_active_alarm_table true if the table is for the active alarms or false if for acknowledged alarms
     *
     * @return TableViewer, still needs input
     */
    private TableViewer createAlarmTable(final Composite parent, final boolean is_active_alarm_table)
    {
        // TableColumnLayout requires the TableViewer to be in its own Composite
        final GridLayout parent_layout = (GridLayout) parent.getLayout();
        final Composite table_parent = new Composite(parent, 0);
        table_parent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, parent_layout.numColumns, 1));

        // Auto-size table columns
        final TableColumnLayout table_layout = new MinSizeTableColumnLayout(10);
        table_parent.setLayout(table_layout);

        final TableViewer table_viewer = new TableViewer(table_parent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI
                | SWT.FULL_SELECTION | SWT.VIRTUAL);

        // Some tweaks to the underlying table widget
        final Table table = table_viewer.getTable();
        table.setHeaderVisible(true);
        table.setLinesVisible(true);

        ColumnViewerToolTipSupport.enableFor(table_viewer);

        // Connect TableViewer to the Model: Provide content from model...
        table_viewer.setContentProvider(new AlarmTableContentProvider());

        // Create the columns of the table, using a fixed initial width.
        for (ColumnWrapper cw : columns)
        {
            if (!cw.isVisible())
                continue;

            // Create auto-size column
            final TableViewerColumn view_col = new TableViewerColumn(table_viewer, 0);
            final TableColumn table_col = view_col.getColumn();
            table_layout.setColumnData(table_col, new ColumnWeightData(cw.getWeight(), cw.getMinWidth()));
            table_col.setText(cw.getName());
            table_col.setMoveable(true);

            ColumnInfo col_info = cw.getColumnInfo();
            // Tell column how to display the model elements

            if (col_info == ColumnInfo.TIME)
                view_col.setLabelProvider(table_label_provider);
            else
                view_col.setLabelProvider(new AlarmTableLabelProvider(icon_provider, color_provider, color_pair_provider, col_info));

            if (col_info == ColumnInfo.ACK)
            {
                if (writable)
                    view_col.setEditingSupport(new AcknowledgeEditingSupport(table_viewer));
            }
            table_col.setToolTipText(col_info.getTooltip());
        }

        return table_viewer;
    }

    /**
     * Add context menu to tree
     *
     * @param table_viewer
     *            TableViewer to which to add the menu
     * @param site
     *            Workbench site or <code>null</code>
     */
    private void connectContextMenu(final TableViewer table_viewer, final IWorkbenchPartSite site)
    {
        final Table table = table_viewer.getTable();
        final boolean isRcp = UI.RCP.equals(SingleSourcePlugin.getUIHelper().getUI());

        final MenuManager manager = new MenuManager();
        manager.setRemoveAllWhenShown(true);
        manager.addMenuListener(new IMenuListener()
        {
            // Dynamically build menu based on current selection
            @Override
            @SuppressWarnings("unchecked")
            public void menuAboutToShow(IMenuManager manager)
            {
                final Shell shell = table.getShell();
                final List<AlarmTreeItem> items = ((IStructuredSelection) table_viewer.getSelection()).toList();

                new ContextMenuHelper(table_viewer, manager, shell, items, writable);
                manager.add(new Separator());
                // Add edit items
                if (items.size() == 1 && writable && model != null)
                {
                    final AlarmTreeItem item = items.get(0);
                    manager.add(new ConfigureItemAction(shell, model, item));
                }
                if (items.size() >= 1 && writable && model != null)
                    manager.add(new DisableComponentAction(shell, model, items));
                manager.add(new Separator());
                if (isRcp)
                {
                    manager.add(new AlarmPerspectiveAction());
                    manager.add(new Separator());
                }
                // Placeholder for CSS PV contributions
                manager.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
            }
        });
        table.setMenu(manager.createContextMenu(table));

        // Allow extensions to add to the context menu
        if (site != null)
            site.registerContextMenu(manager, table_viewer);
    }

    /**
     * Set or clear error message. Setting an error message also disables the GUI.
     * <p>
     * OK to call multiple times or after disposal.
     *
     * @param error
     *            Error message or <code>null</code> to clear error
     */
    private void setErrorMessage(final String error)
    {
        final Table act_table = active_table_viewer.getTable();
        if (act_table.isDisposed())
            return;
        if (error == null)
        {
            if (!have_error_message)
                return; // msg already cleared, GUI already enabled
            if (show_header)
            {
                error_message.setText(""); //$NON-NLS-1$
                error_message.setBackground(null);
                error_message.setVisible(false);
                error_message.getParent().layout();
            }
            act_table.setEnabled(true);
            if (separate_tables)
                acknowledged_table_viewer.getTable().setEnabled(true);
            have_error_message = false;
        }
        else
        {
            // Update the message
            if (show_header)
            {
                error_message.setText(error);
                error_message.setBackground(display.getSystemColor(SWT.COLOR_MAGENTA));
                error_message.setVisible(true);
                error_message.getParent().layout();
            }
            if (have_error_message)
                return; // GUI already disabled
            act_table.setEnabled(false);
            if (separate_tables)
                acknowledged_table_viewer.getTable().setEnabled(false);
            have_error_message = true;
        }
    }

    // @see AlarmClientModelListener
    @Override
    public void serverModeUpdate(AlarmClientModel model, boolean maintenanceMode)
    {
        // Ignored
    }

    // @see AlarmClientModelListener
    @Override
    public void serverTimeout(final AlarmClientModel model)
    {
        display.asyncExec(() -> setErrorMessage(org.csstudio.alarm.beast.ui.Messages.ServerTimeout));
    }

    // For now, the table responds to any changes with a full update
    // @see AlarmClientModelListener
    @Override
    public void newAlarmConfiguration(final AlarmClientModel model)
    {
        gui_update.trigger();
        display.asyncExec(() ->
        {
            if (model.isServerAlive())
                setErrorMessage(null);
            else
                setErrorMessage(org.csstudio.alarm.beast.ui.Messages.WaitingForServer);
        });
    }

    // @see AlarmClientModelListener
    @Override
    public void newAlarmState(final AlarmClientModel model, final AlarmTreePV pv, final boolean parent_changed)
    {
        gui_update.trigger();

        if (model.isServerAlive() && have_error_message)
        {
            // Clear error message now that we have info from the alarm server
            display.asyncExec(() -> setErrorMessage(null));
        }
    }

    private AlarmTreePV[] filter(AlarmTreePV[] alarms)
    {
        if (filter_item_parent == null || filter_item_parent instanceof AlarmTreeRoot || alarms.length == 0)
            return alarms;
        else
        {
            List<AlarmTreePV> items = new ArrayList<>(alarms.length);
            AlarmTreeItem item;
            for (AlarmTreePV pv : alarms)
            {
                item = pv;
                do
                {
                    if (item == filter_item_parent)
                    {
                        items.add(pv);
                        break;
                    }
                    item = item.getParent();
                } while (item != null && !(item instanceof AlarmTreeRoot));

            }
            return items.toArray(new AlarmTreePV[items.size()]);
        }
    }

    private void updateGUI(int numberOfRawAlarms, AlarmTreePV[] filteredAlarms,
            int numberOfRawAcknowledgedAlarms, AlarmTreePV[] filteredAcknowledgedAlarms,
            AlarmTreePV[] combinedAlarms)
    {
        if (model == null || active_table_viewer.getTable().isDisposed()) return;
        //if GUI is currently busy, do not update anything, just trigger another update
        if (active_table_viewer.isBusy()) {
            gui_update.trigger();
            return;
        }

        if (show_header) {
            if (current_alarms.isDisposed())
                return;

            String text = null;
            if (filter_item_parent == null || filter_item_parent instanceof AlarmTreeRoot)
                text = NLS.bind(Messages.CurrentAlarmsFmtAll, filteredAlarms.length);
            else
                text = NLS.bind(Messages.CurrentAlarmsFmt, new Object[]
                            { filteredAlarms.length, numberOfRawAlarms, filter_item_parent.getPathName() });
            current_alarms.setText(text);
            current_alarms.setToolTipText(text);

            if (filteredAlarms.length != numberOfRawAlarms)
                current_alarms.setForeground(current_alarms.getDisplay().getSystemColor(SWT.COLOR_RED));
            else
                current_alarms.setForeground(null);
            current_alarms.pack();


            if (filter_item_parent == null || filter_item_parent instanceof AlarmTreeRoot)
                text = NLS.bind(Messages.AcknowledgedAlarmsFmtAll, filteredAcknowledgedAlarms.length);
            else
                text = NLS.bind(Messages.AcknowledgedAlarmsFmt, new Object[]
                        { filteredAcknowledgedAlarms.length, numberOfRawAcknowledgedAlarms,
                          filter_item_parent.getPathName() });
            acknowledged_alarms.setText(text);
            acknowledged_alarms.setToolTipText(text);

            if (filteredAcknowledgedAlarms.length != numberOfRawAcknowledgedAlarms)
                acknowledged_alarms.setForeground(acknowledged_alarms.getDisplay().getSystemColor(SWT.COLOR_RED));
            else
                acknowledged_alarms.setForeground(null);
            acknowledged_alarms.pack();
        }
        if (separate_tables)
        {
            ((AlarmTableContentProvider) active_table_viewer.getContentProvider()).setAlarms(filteredAlarms);
            ((AlarmTableContentProvider) acknowledged_table_viewer.getContentProvider()).setAlarms(
                    filteredAcknowledgedAlarms);
        }
        else
        {
            ((AlarmTableContentProvider) active_table_viewer.getContentProvider()).setAlarms(combinedAlarms);
        }
    }

    /**
     * Sets the model and the filter item to use. Only the alarms that are blow the filter
     * item parent will be displayed, providing that the filter item parent is from the
     * given model.
     *
     * @param filterItemParent the item used for filtering alarms
     * @param model the model to show events for
     */
    public void setFilterItem(AlarmTreeItem filterItemParent, AlarmClientModel model)
    {
        this.filter_item_parent = filterItemParent;
        if (this.model != null)
            this.model.removeListener(this);
        this.model = model;
        setUpModel();
        gui_update.trigger();
    }

    /**
     * Enables or disables the blinking of unacknowledged alarms icons.
     *
     * @param blinking true if icons should blink or stop otherwise
     */
    public void setBlinking(boolean blinking)
    {
        if (this.blink_unacknowledged == blinking) return;
        this.blink_unacknowledged = blinking;
        if (this.blink_unacknowledged)
            Blinker.INSTANCE.add(this);
        else
        {
            Blinker.INSTANCE.remove(this);
            icon_provider.reset();
            active_table_viewer.refresh();
            if (acknowledged_table_viewer != null)
                acknowledged_table_viewer.refresh();
        }
    }

    /**
     * Sets the format used for formatting the date and time column. The format has
     * to be acceptable by the {@link SimpleDateFormat}.
     *
     * @param timeFormat the format string
     */
    public void setTimeFormat(String timeFormat)
    {
        table_label_provider.setTimeFormat(timeFormat);
    }

    /**
     * Set the maximum number of alarms displayed in the table.
     *
     * @param limit the maximum number of alarms
     */
    public void setNumberOfAlarmsLimit(int limit)
    {
        ((AlarmTableContentProvider) active_table_viewer.getContentProvider()).setNumberOfAlarmsLimit(limit);
        if (acknowledged_table_viewer != null)
            ((AlarmTableContentProvider) acknowledged_table_viewer.getContentProvider()).setNumberOfAlarmsLimit(limit);
    }

    /**
     * Set the weights of the sash form used for displaying acknowledged and unacknowledged tables. If the
     * GUI only shows one table, this method as no effect.
     *
     * @param ack the weight for the acknowledged table
     * @param unack the weight for the unacknowledged table
     */
    public void setSashWeights(int ack, int unack)
    {
        if (base_composite instanceof SashForm)
            ((SashForm) base_composite).setWeights(new int[]{ ack, unack });
    }

    /**
     * Toggles the visibility of both table column headers.
     *
     * @param visible true if headers should be visible or false if hidden
     */
    public void setTableColumnsHeadersVisible(boolean visible)
    {
        Table table = active_table_viewer.getTable();
        if (table.isDisposed())
            return;
        table.setHeaderVisible(visible);
        if (separate_tables)
            acknowledged_table_viewer.getTable().setHeaderVisible(visible);
    }
}
