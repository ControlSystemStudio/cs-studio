/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.ui.alarmtable;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.csstudio.alarm.beast.client.AlarmTreeItem;
import org.csstudio.alarm.beast.client.AlarmTreePV;
import org.csstudio.alarm.beast.client.GUIUpdateThrottle;
import org.csstudio.alarm.beast.ui.ContextMenuHelper;
import org.csstudio.alarm.beast.ui.Messages;
import org.csstudio.alarm.beast.ui.SelectionHelper;
import org.csstudio.alarm.beast.ui.SeverityColorProvider;
import org.csstudio.alarm.beast.ui.actions.AlarmPerspectiveAction;
import org.csstudio.alarm.beast.ui.actions.ConfigureItemAction;
import org.csstudio.alarm.beast.ui.actions.DisableComponentAction;
import org.csstudio.alarm.beast.ui.clientmodel.AlarmClientModel;
import org.csstudio.alarm.beast.ui.clientmodel.AlarmClientModelListener;
import org.csstudio.apputil.text.RegExHelper;
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
import org.eclipse.jface.dialogs.IDialogSettings;
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
 *  @author Jaka Bobnar - Combined/split alarm tables, configurable columns
 */
public class GUI implements AlarmClientModelListener
{
    /**
     * Persistence: Tags within dialog settings, actually written to
     * WORKSPACE/.metadata/.plugins/org.csstudio.alarm.beast.ui.alarmtable/dialog_settings.xml
     */
    final private static String ALARM_TABLE_SORT_COLUMN = "alarm_table_sort_column", //$NON-NLS-1$
            ALARM_TABLE_SORT_UP = "alarm_table_sort_up"; //$NON-NLS-1$

    /**
     * Initial place holder for display of alarm counts to allocate enough screen space
     */
    final private static String ALARM_COUNT_PLACEHOLDER = "999999"; //$NON-NLS-1$

    final private Display display;

    /** Model with all the alarm information */
    final private AlarmClientModel model;

    final private IDialogSettings settings;

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

    /** Is something displayed in <code>error_message</code>? */
    private volatile boolean have_error_message = false;

    /** Error message (no server...) */
    private Label error_message;

    private Composite baseComposite;

    private final boolean group;

    private final ColumnWrapper[] columns;

    /** GUI updates are throttled to reduce flicker */
    final private GUIUpdateThrottle gui_update = new GUIUpdateThrottle()
    {
        @Override
        protected void fire()
        {
            if (display.isDisposed())
                return;
            display.syncExec(() ->
            {
                if (current_alarms.isDisposed())
                    return;

                // Display counts, update tables

                // Don't use TableViewer.setInput(), it causes flicker on Linux!
                // active_table_viewer.setInput(model.getActiveAlarms());
                // acknowledged_table_viewer.setInput(model.getAcknowledgedAlarms());
                //
                // Instead, tell ModelInstanceProvider about the data,
                // which then updates the table with setItemCount(), refresh(),
                // as that happens to not flicker.
                updateGUI();
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
            if (value instanceof Boolean)
            {
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
     * @param parent
     *            Parent widget
     * @param model
     *            Alarm model
     * @param site
     *            Workbench site or <code>null</code>
     * @param settings
     * @param group
     *            true if two tables should be created (one for acked and one for unacked alarms) or false if only one
     *            table should be created
     * @param columns
     *            column configuration for the tables
     */
    public GUI(final Composite parent, final AlarmClientModel model, final IWorkbenchPartSite site,
            final IDialogSettings settings, boolean group, ColumnWrapper[] columns)
    {
        display = parent.getDisplay();
        this.group = group;
        this.model = model;
        this.settings = settings;
        this.columns = columns;
        createComponents(parent);

        if (model.isServerAlive())
            setErrorMessage(null);
        else
            setErrorMessage(Messages.WaitingForServer);

        // Subscribe to model updates, arrange to un-subscribe
        model.addListener(this);
        baseComposite.addDisposeListener(new DisposeListener()
        {
            @Override
            public void widgetDisposed(DisposeEvent e)
            {
                saveSettings();
                model.removeListener(GUI.this);
                gui_update.dispose();
            }
        });

        connectContextMenu(active_table_viewer, site);
        if (group)
        {
            connectContextMenu(acknowledged_table_viewer, site);
        }

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
        if (group)
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
        updateGUI();
    }

    /** @return Table of active alarms */
    public TableViewer getActiveAlarmTable()
    {
        return active_table_viewer;
    }

    /** @return Table of acknowledged alarms */
    public TableViewer getAcknowledgedAlarmTable()
    {
        return group ? acknowledged_table_viewer : active_table_viewer;
    }

    /**
     * Create GUI elements
     *
     * @param parent
     *            Parent widget
     */
    private void createComponents(final Composite parent)
    {
        parent.setLayout(new FillLayout());

        int sort_column = ColumnInfo.SEVERITY.ordinal();
        boolean sort_up = false;
        if (settings != null)
        {
            try
            {
                sort_column = settings.getInt(ALARM_TABLE_SORT_COLUMN);
                sort_up = settings.getBoolean(ALARM_TABLE_SORT_UP);
            }
            catch (NumberFormatException ex)
            {
                // Ignore, use default
            }
        }

        if (group)
        {
            baseComposite = new SashForm(parent, SWT.VERTICAL | SWT.SMOOTH);
            baseComposite.setLayout(new FillLayout());
            color_provider = new SeverityColorProvider(baseComposite);
            // TODO Sync the table's sorting
            // Tables are currently separate. Sorting one table by 'time' should
            // probably cause both tables to sort by time.
            addActiveAlarmSashElement(baseComposite, sort_column, sort_up);
            addAcknowledgedAlarmSashElement(baseComposite, sort_column, sort_up);
            ((SashForm) baseComposite).setWeights(new int[]
            { 80, 20 });
        }
        else
        {
            baseComposite = new Composite(parent, SWT.NONE);
            baseComposite.setLayout(new FillLayout());
            color_provider = new SeverityColorProvider(baseComposite);
            addActiveAlarmSashElement(baseComposite, sort_column, sort_up);
        }
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
                if (group)
                    selectFilteredPVs(pattern, acknowledged_table_viewer);
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
                if (group)
                    acknowledged_table_viewer.setSelection(null, true);
            }
        });

        gui_update.start();
    }

    /** Save table settings */
    private void saveSettings()
    {
        if (settings == null)
            return;

        final Table table = active_table_viewer.getTable();
        final TableColumn sort_column = table.getSortColumn();
        if (sort_column == null)
            return;

        final int col_count = table.getColumnCount();
        for (int column = 0; column < col_count; ++column)
        {
            if (table.getColumn(column) == sort_column)
            {
                settings.put(ALARM_TABLE_SORT_COLUMN, column);
                settings.put(ALARM_TABLE_SORT_UP, table.getSortDirection() == SWT.UP);
                return;
            }
        }
    }

    /**
     * Add the sash element for active alarms
     *
     * @param sash
     *            SashForm
     * @param sort_column
     * @param sort_up
     */
    private void addActiveAlarmSashElement(final Composite sash, final int sort_column, final boolean sort_up)
    {
        final Composite box = new Composite(sash, SWT.BORDER);
        final GridLayout layout = new GridLayout();
        layout.numColumns = group ? 5 : 6;
        box.setLayout(layout);

        current_alarms = new Label(box, 0);
        current_alarms.setText(NLS.bind(Messages.CurrentAlarmsFmt, ALARM_COUNT_PLACEHOLDER));
        current_alarms.setLayoutData(new GridData());

        if (!group)
        {
            acknowledged_alarms = new Label(box, 0);
            acknowledged_alarms.setText(NLS.bind(Messages.AcknowledgedAlarmsFmt, ALARM_COUNT_PLACEHOLDER));
            acknowledged_alarms.setLayoutData(new GridData());
        }

        error_message = new Label(box, 0);
        GridData gd = new GridData();
        gd.horizontalAlignment = SWT.RIGHT;
        gd.grabExcessHorizontalSpace = true;
        error_message.setLayoutData(gd);

        Label l = new Label(box, 0);
        l.setText(Messages.Filter);
        l.setLayoutData(new GridData());

        filter = new Combo(box, SWT.BORDER);
        filter.setToolTipText(Messages.FilterTT);
        gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        filter.setLayoutData(gd);

        unselect = new Button(box, SWT.PUSH);
        unselect.setText(Messages.Unselect);
        unselect.setToolTipText(Messages.UnselectTT);
        gd = new GridData();
        gd.horizontalAlignment = SWT.RIGHT;
        unselect.setLayoutData(gd);

        // Table w/ active alarms
        active_table_viewer = createAlarmTable(box, sort_column, sort_up, true);
        active_table_viewer.setInput(null);
        ((AlarmTableContentProvider) active_table_viewer.getContentProvider()).setAlarms(model.getActiveAlarms());
    }

    /**
     * Add the sash element for acknowledged alarms
     *
     * @param sash
     *            SashForm
     * @param sort_column
     * @param sort_up
     */
    private void addAcknowledgedAlarmSashElement(final Composite sash, final int sort_column, final boolean sort_up)
    {
        final Composite box = new Composite(sash, SWT.BORDER);
        box.setLayout(new GridLayout());

        acknowledged_alarms = new Label(box, 0);
        acknowledged_alarms.setText(NLS.bind(Messages.AcknowledgedAlarmsFmt, ALARM_COUNT_PLACEHOLDER));
        acknowledged_alarms.setLayoutData(new GridData());

        // Table w/ ack'ed alarms
        acknowledged_table_viewer = createAlarmTable(box, sort_column, sort_up, false);
        acknowledged_table_viewer.setInput(null);
        ((AlarmTableContentProvider) acknowledged_table_viewer.getContentProvider()).setAlarms(model
                .getAcknowledgedAlarms());
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
     * @param parent
     *            Parent widget, uses GridLayout
     * @param sort_column
     * @param sort_up
     * @param is_active_alarm_table
     *            true if the table is for the active alarms or false if for acknowledged alarms
     * @return TableViewer, still needs input
     */
    private TableViewer createAlarmTable(final Composite parent, final int sort_column, final boolean sort_up,
            final boolean is_active_alarm_table)
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
        int column_number = 0;
        for (ColumnWrapper cw : columns)
        {
            if (!cw.isVisible())
                continue;
            ColumnInfo col_info = cw.getColumnInfo();
            // Create auto-size column
            final TableViewerColumn view_col = new TableViewerColumn(table_viewer, 0);
            final TableColumn table_col = view_col.getColumn();
            table_layout.setColumnData(table_col, new ColumnWeightData(col_info.getWeight(), col_info.getMinWidth()));
            if (col_info != ColumnInfo.ICON)
                table_col.setText(col_info.getTitle());
            table_col.setMoveable(true);
            // Tell column how to display the model elements
            view_col.setLabelProvider(new AlarmTableLabelProvider(table, color_provider, col_info));
            // Sort support
            final AlarmColumnSortingSelector sel_listener = new AlarmColumnSortingSelector(table_viewer, table_col,
                    col_info);
            table_col.addSelectionListener(sel_listener);
            // Sort on severity right away
            if (column_number == sort_column)
                sel_listener.setSortDirection(sort_up);
            ++column_number;

            if (col_info == ColumnInfo.ACK)
            {
                if (model.isWriteAllowed())
                    view_col.setEditingSupport(new AcknowledgeEditingSupport(table_viewer));
                table_col.setToolTipText(Messages.AcknowledgeColumnHeaderTooltip);
            }
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

                new ContextMenuHelper(table_viewer, manager, shell, items, model.isWriteAllowed());
                manager.add(new Separator());
                // Add edit items
                if (items.size() == 1 && model.isWriteAllowed())
                {
                    final AlarmTreeItem item = items.get(0);
                    manager.add(new ConfigureItemAction(shell, model, item));
                }
                if (items.size() >= 1 && model.isWriteAllowed())
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
    public void setErrorMessage(final String error)
    {
        final Table act_table = active_table_viewer.getTable();
        if (act_table.isDisposed())
            return;
        if (error == null)
        {
            if (!have_error_message)
                return; // msg already cleared, GUI already enabled
            error_message.setText(""); //$NON-NLS-1$
            error_message.setBackground(null);
            act_table.setEnabled(true);
            if (group)
                acknowledged_table_viewer.getTable().setEnabled(true);
            have_error_message = false;
        }
        else
        {
            // Update the message
            error_message.setText(error);
            error_message.setBackground(display.getSystemColor(SWT.COLOR_MAGENTA));
            error_message.getParent().layout();
            if (have_error_message)
                return; // GUI already disabled
            act_table.setEnabled(false);
            if (group)
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
        display.asyncExec(() -> setErrorMessage(Messages.ServerTimeout));
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
            {
                setErrorMessage(null);
            }
            else
            {
                setErrorMessage(Messages.WaitingForServer);
            }
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

    private void updateGUI()
    {
        AlarmTreePV[] alarms = model.getActiveAlarms();
        current_alarms.setText(NLS.bind(Messages.CurrentAlarmsFmt, alarms.length));
        current_alarms.pack();

        AlarmTreePV[] ackalarms = model.getAcknowledgedAlarms();
        acknowledged_alarms.setText(NLS.bind(Messages.AcknowledgedAlarmsFmt, ackalarms.length));
        acknowledged_alarms.pack();
        if (group)
        {
            ((AlarmTableContentProvider) active_table_viewer.getContentProvider()).setAlarms(alarms);
            ((AlarmTableContentProvider) acknowledged_table_viewer.getContentProvider()).setAlarms(ackalarms);
        }
        else
        {
            AlarmTreePV[] items = new AlarmTreePV[alarms.length + ackalarms.length];
            System.arraycopy(alarms, 0, items, 0, alarms.length);
            System.arraycopy(ackalarms, 0, items, alarms.length, ackalarms.length);
            ((AlarmTableContentProvider) active_table_viewer.getContentProvider()).setAlarms(items);
        }
    }

    void dispose()
    {
        baseComposite.dispose();
    }
}
