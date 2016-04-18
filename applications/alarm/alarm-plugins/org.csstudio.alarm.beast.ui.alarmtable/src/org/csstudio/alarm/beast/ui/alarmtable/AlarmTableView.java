/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.ui.alarmtable;

import java.text.SimpleDateFormat;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.alarm.beast.client.AlarmTreeItem;
import org.csstudio.alarm.beast.client.AlarmTreePV;
import org.csstudio.alarm.beast.client.AlarmTreeRoot;
import org.csstudio.alarm.beast.ui.actions.AcknowledgeAction;
import org.csstudio.alarm.beast.ui.actions.MaintenanceModeAction;
import org.csstudio.alarm.beast.ui.alarmtable.actions.ColumnConfigureAction;
import org.csstudio.alarm.beast.ui.alarmtable.actions.FilterAction;
import org.csstudio.alarm.beast.ui.alarmtable.actions.NewTableAction;
import org.csstudio.alarm.beast.ui.alarmtable.actions.ResetColumnsAction;
import org.csstudio.alarm.beast.ui.alarmtable.actions.SeparateCombineTablesAction;
import org.csstudio.alarm.beast.ui.alarmtable.actions.ShowFilterAction;
import org.csstudio.alarm.beast.ui.clientmodel.AlarmClientModel;
import org.csstudio.alarm.beast.ui.clientmodel.AlarmClientModelListener;
import org.csstudio.ui.util.dnd.ControlSystemDropTarget;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

/**
 * Eclipse View for the alarm table.
 *
 * @author Kay Kasemir
 * @author Jaka Bobnar - Combined/split alarm tables, configurable columns
 */
public class AlarmTableView extends ViewPart
{
    public static final int PROP_FILTER = 555444;
    public static final int PROP_FILTER_ITEM = 555445;

    private static AtomicInteger secondaryId = new AtomicInteger(1);

    /**
     * Return the next secondary id that has not been opened.
     *
     * @return part
     */
    public static String newSecondaryID(IViewPart part)
    {
        while (part.getSite().getPage().findViewReference(part.getSite().getId(),
                String.valueOf(secondaryId.get())) != null)
        {
            secondaryId.incrementAndGet();
        }

        return String.valueOf(secondaryId.get());
    }

    private AlarmClientModel model;
    private AlarmClientModel defaultModel;
    private AlarmClientModelListener modelListener = new AlarmClientModelListener()
    {
        @Override
        public void newAlarmConfiguration(AlarmClientModel model)
        {
            parent.getDisplay().asyncExec(()->updateFilterItem());
        }

        @Override
        public void serverTimeout(AlarmClientModel model)
        {
        }
        @Override
        public void serverModeUpdate(AlarmClientModel model, boolean maintenance_mode)
        {
        }
        @Override
        public void newAlarmState(AlarmClientModel model, AlarmTreePV pv, boolean parent_changed)
        {
        }
    };

    private Composite parent;
    private GUI gui;
    private MaintenanceModeAction maintenanceModeAction;
    private IMemento memento;

    private FilterType filterType;
    /** Combined active and acknowledge alarms, group into separate tables? */
    private boolean combinedTables;
    /** Should severity icons blink or not */
    private boolean blinkingIcons;
    /** The time format string used for formatting the alarm time label */
    private String timeFormat;
    /** The name of the filter item */
    private String filterItemPath;
    /** The filter item, which should match the filterItemName and configurationName if the model is available */
    private AlarmTreeItem filterItem;

    private ColumnWrapper[] columns = ColumnWrapper.getNewWrappers();

    @Override
    public void init(IViewSite site, IMemento memento) throws PartInitException
    {
        this.memento = memento;
        this.blinkingIcons = Preferences.isBlinkUnacknowledged();
        restoreState(memento,site);
        super.init(site);
    }

    @Override
    public void dispose()
    {
        if (secondaryId.get() > 1)
        {
            secondaryId.decrementAndGet();
        }
        super.dispose();
    }

    @Override
    public void createPartControl(final Composite parent)
    {
        this.parent = parent;
        parent.setLayout(new FillLayout());
        try
        {
            defaultModel = AlarmClientModel.getInstance();
            defaultModel.addListener(modelListener);
            if (filterItemPath != null)
            {
                model = AlarmClientModel.getInstance(getConfigNameFromPath(filterItemPath));
                model.addListener(modelListener);
            }
        }
        catch (final Throwable ex)
        {   // Instead of actual GUI, create error message
            final String error = ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();
            final String message = NLS.bind(org.csstudio.alarm.beast.ui.Messages.ServerErrorFmt, error);
            // Add to log, also display in text widget
            Logger.getLogger(Activator.ID).log(Level.SEVERE, "Cannot load alarm model", ex); //$NON-NLS-1$
            parent.setLayout(new FillLayout());
            new Text(parent, SWT.READ_ONLY | SWT.BORDER | SWT.MULTI).setText(message);
            return;
        }

        // Arrange for model to be released
        parent.addDisposeListener(new DisposeListener()
        {
            @Override
            public void widgetDisposed(DisposeEvent e)
            {
                releaseModel(defaultModel);
                defaultModel = null;
                releaseModel(model);
                model = null;;
            }
        });

        makeGUI();
        createToolbar();
        updateFilterItem();
    }

    private void restoreState(IMemento memento, IViewSite site)
    {
        if (memento == null)
        {
            this.combinedTables = Preferences.isCombinedAlarmTable();
            this.filterType = FilterType.TREE;
            this.columns = ColumnWrapper.fromSaveArray(Preferences.getColumns());
            this.timeFormat = Preferences.getTimeFormat();
            //if timeformat is null, the default format is used, which is AlarmTreeLeaf#getTimestampText
        }
        else
        {
            Boolean groupSet = memento.getBoolean(Preferences.ALARM_TABLE_COMBINED_TABLES);
            this.combinedTables = groupSet == null ? Preferences.isCombinedAlarmTable() : groupSet;

            String filterTypeSet = memento.getString(Preferences.ALARM_TABLE_FILTER_TYPE);
            this.filterType = filterTypeSet == null || filterTypeSet.isEmpty() ?
                    FilterType.TREE : FilterType.valueOf(filterTypeSet.toUpperCase());

            this.timeFormat = memento.getString(Preferences.ALARM_TABLE_TIME_FORMAT);
            if (this.timeFormat == null || this.timeFormat.isEmpty())
                this.timeFormat = Preferences.getTimeFormat();
            //if timeformat is null, the default format is used, which is AlarmTreeLeaf#getTimestampText

            this.columns = ColumnWrapper.restoreColumns(memento.getChild(Preferences.ALARM_TABLE_COLUMN_SETTING));

            String name = memento.getString(Preferences.ALARM_TABLE_FILTER_ITEM);
            this.filterItemPath = name == null || name.isEmpty() ? null : name;
        }
    }

    @Override
    public void saveState(IMemento memento)
    {
        super.saveState(memento);
        memento.putBoolean(Preferences.ALARM_TABLE_COMBINED_TABLES, combinedTables);
        memento.putString(Preferences.ALARM_TABLE_FILTER_TYPE, filterType.name());
        if (filterItem != null)
            memento.putString(Preferences.ALARM_TABLE_FILTER_ITEM, filterItem.getPathName());
        else if (filterItemPath != null)
            memento.putString(Preferences.ALARM_TABLE_FILTER_ITEM, filterItemPath);

        if (this.timeFormat != null)
            memento.putString(Preferences.ALARM_TABLE_TIME_FORMAT, timeFormat);

        IMemento columnsMemento = memento.createChild(Preferences.ALARM_TABLE_COLUMN_SETTING);
        ColumnWrapper.saveColumns(columnsMemento, getUpdatedColumns());
        if (gui != null)
        {
            ColumnInfo info = gui.getSortingColumn();
            if (info != null)
                memento.putString(Preferences.ALARM_TABLE_SORT_COLUMN, info.name());
            memento.putBoolean(Preferences.ALARM_TABLE_SORT_UP, gui.isSortingUp());
        }
    }

    private void createToolbar()
    {
        final IToolBarManager toolbar = getViewSite().getActionBars().getToolBarManager();
        toolbar.removeAll();
        if (defaultModel.isWriteAllowed())
        {
            maintenanceModeAction = new MaintenanceModeAction(defaultModel);
            toolbar.add(maintenanceModeAction);
            toolbar.add(new Separator());
            AcknowledgeAction action = new AcknowledgeAction(true, gui.getActiveAlarmTable());
            action.clearSelectionOnAcknowledgement(gui.getActiveAlarmTable());
            toolbar.add(action);
            action = new AcknowledgeAction(false, gui.getAcknowledgedAlarmTable());
            action.clearSelectionOnAcknowledgement(gui.getAcknowledgedAlarmTable());
            toolbar.add(action);
            toolbar.add(new Separator());
        }

        for (FilterType f : FilterType.values())
            toolbar.add(new FilterAction(this, f, this.filterType == f));
        toolbar.add(new Separator());

        final IMenuManager menu = getViewSite().getActionBars().getMenuManager();
        menu.add(new NewTableAction(this));
        menu.add(new Separator());
        menu.add(new SeparateCombineTablesAction(this, true, combinedTables));
        menu.add(new SeparateCombineTablesAction(this, false, !combinedTables));
        menu.add(new Separator());
        menu.add(new ColumnConfigureAction(this));
        menu.add(new ResetColumnsAction(this));
        menu.add(new Separator());
        menu.add(new ShowFilterAction(this));
    }

    private void releaseModel(AlarmClientModel model) {
        if (model != null)
        {
            model.removeListener(modelListener);
            model.release();
        }
    }

    /**
     * Set the filter item by its path. The path is transformed to an actual item, which is then applied as the filter
     * item. If the item does not exist a null filter is applied.
     *
     * @see AlarmTableView#setFilterItem(AlarmTreeItem)
     *
     * @param path the path to filter on
     * @throws Exception if the model for the given path could not be created
     */
    public void setFilterItemPath(String path) throws Exception
    {
        this.filterItemPath = path;
        if (filterItemPath == null || filterItemPath.isEmpty())
        {
            releaseModel(model);
            model = null;
            setFilterType(FilterType.TREE);
        }
        else
        {
            String configName = getConfigNameFromPath(filterItemPath);
            if (model == null || !model.getConfigurationName().equals(configName))
            {
                releaseModel(model);
                this.model = AlarmClientModel.getInstance(configName);
                this.model.addListener(modelListener);
            }
            setFilterType(FilterType.ITEM);
        }
        updateFilterItem();
    }

    /**
     * @return the currently applied filter item
     */
    public String getFilterItemPath()
    {
        return filterItemPath;
    }


    /**
     * Updated the filter item according to the selected filter type and filter item path. The view title
     * is also updated.
     */
    private void updateFilterItem()
    {
        AlarmClientModel activeModel = null;
        String name;
        if (filterType == FilterType.TREE)
        {
            if (defaultModel == null)
                return;
            activeModel = defaultModel;
            name = activeModel.getConfigurationName();
            if (gui != null)
                gui.setFilterItem(null, activeModel);
        }
        else
        {
            activeModel = model;
            AlarmTreeRoot root = activeModel.getConfigTree().getRoot();
            this.filterItem = root.getItemByPath(filterItemPath);
            if (filterItem != null)
            {
                if (filterType == FilterType.ITEM)
                    name = filterItem.getName();
                else
                    name = activeModel.getConfigurationName();
            }
            else
            {
                //filter path is set, but the item is null, because it
                //either does not exist, or the model is not yet connected
                int idx = filterItemPath.lastIndexOf('/');
                name = idx < 0 ? filterItemPath : filterItemPath.substring(idx + 1);
                name = "\u00BF" + name + "?";
            }
            if (gui != null)
                gui.setFilterItem(filterType == FilterType.ITEM ? filterItem : null, activeModel);
        }
        if (maintenanceModeAction != null)
            maintenanceModeAction.setModel(activeModel);
        setPartName(NLS.bind(Messages.AlarmTablePartName, name));
        setTitleToolTip(NLS.bind(Messages.AlarmTableTitleTT, name));
        firePropertyChange(PROP_FILTER_ITEM);
    }



    /**
     * @return the columns as they are currently visible and ordered in the table
     */
    public ColumnWrapper[] getUpdatedColumns()
    {
        ColumnWrapper[] columns = ColumnWrapper.getCopy(this.columns);
        if (gui != null)
            gui.updateColumnOrder(columns);
        return columns;
    }

    /**
     * Set the columns for the table. The table will display the columns in the provided order and will show only those
     * columns that have the visible flag set to true
     *
     * @param columns the columns to set on the table
     */
    public void setColumns(ColumnWrapper[] columns)
    {
        this.columns = columns;
        redoGUI();
    }

    private boolean makeGUI()
    {
        if (parent.isDisposed())
            return false;
        String s = memento == null ? null : memento.getString(Preferences.ALARM_TABLE_SORT_COLUMN);
        ColumnInfo sorting = s == null ? ColumnInfo.PV : ColumnInfo.valueOf(s);
        boolean sortUp = false;
        if (memento != null) {
            Boolean b = memento.getBoolean(Preferences.ALARM_TABLE_SORT_UP);
            sortUp = b == null ? false : b;
        }
        if (gui != null)
        {
            sorting = gui.getSortingColumn();
            sortUp = gui.isSortingUp();
            gui.dispose();
        }
        gui = new GUI(parent, getSite(), defaultModel.isWriteAllowed(),
                !combinedTables, columns, sorting, sortUp,true);
        gui.setBlinking(blinkingIcons);
        gui.setTimeFormat(timeFormat);
        gui.setTableColumnsHeadersVisible(true);
        setUpDrop(gui.getActiveAlarmTable().getTable());
        setUpDrop(gui.getAcknowledgedAlarmTable().getTable());
        updateFilterItem();

        return true;
    }

    private void setUpDrop(Control control)
    {
        if (control == null || control.getData(DND.DROP_TARGET_KEY) != null)
            return;
        new ControlSystemDropTarget(control, AlarmTreeItem.class, AlarmTreeItem[].class,
                AlarmTreePV.class, AlarmTreePV[].class)
        {
            @Override
            public void handleDrop(Object item)
            {
                try
                {
                    if (item instanceof AlarmTreeItem[])
                        setFilterItemPath(((AlarmTreeItem[])item)[0].getPathName());
                    else if (item instanceof AlarmTreeItem || item instanceof AlarmTreePV)
                        setFilterItemPath(((AlarmTreeItem)item).getPathName());
                    else if (item instanceof AlarmTreePV[])
                        setFilterItemPath(((AlarmTreePV[])item)[0].getPathName());
                }
                catch (Exception e)
                {
                    throw new IllegalArgumentException(e);
                }
            }
        };
    }

    private void redoGUI()
    {
        if (gui != null)
        {
            parent.getDisplay().asyncExec(() ->
            {
                if (makeGUI())
                    parent.layout();
            });
        }
    }

    @Override
    public void setFocus()
    {
        // NOP
    }

    /**
     * Combine all alarms into a single table or group the alarms into two separate tables (by the acknowledge status).
     *
     * @param combinedTables true if the acknowledged and unacknowledged alarms should be displayed in a single table,
     *            or false if they should be displayed in separate tables
     */
    public void setCombinedTables(boolean separated)
    {
        this.combinedTables = separated;
        redoGUI();
    }

    /**
     * Set the filter type for the table. The table will display alarms according to this filter. If the filter
     * is {@link FilterType#TREE} all alarms from the root currently selected in the alarm tree; if the filter type
     * is {@link FilterType#ROOT} all alarms from the root that is currently applied to this table (through filter item)
     * will be displayed; if filter type is {@link FilterType#ITEM} only the alarms belonging to the filter item will
     * be displayed.
     *
     * @param filterType the filter type to set
     */
    public void setFilterType(FilterType filterType)
    {
        if (filterItemPath == null && (filterType == FilterType.ROOT || filterType == FilterType.ITEM)) {
            throw new IllegalStateException("Cannot apply filter type " + filterType //$NON-NLS-1$
                    + " if no filter item is defined."); //$NON-NLS-1$
        }
        this.filterType = filterType;
        updateFilterItem();
        firePropertyChange(PROP_FILTER);
    }

    /**
     * @return the filter type currently selected for this table
     */
    public FilterType getFilterType()
    {
        return this.filterType;
    }

    /**
     * Enables or disables blinking of icons of the unacknowledged alarms.
     *
     * @param blinking true if the icons should be blinking or false otherwise
     */
    public void setBlinkingIcons(boolean blinking)
    {
        this.blinkingIcons = blinking;
        if (gui != null)
            gui.setBlinking(blinking);
    }

    /**
     * @return the alarm client model used by this table
     */
    public AlarmClientModel getModel()
    {
        return filterType == FilterType.TREE ? defaultModel : model;
    }

    /**
     * Sets the time format used for formatting the value in the time column. Format should be in the form acceptable by
     * the {@link SimpleDateFormat}.
     *
     * @param format the format
     */
    public void setTimeFormat(String format)
    {
        if (format != null && format.isEmpty())
            format = null;
        this.timeFormat = format;
        if (gui != null)
            gui.setTimeFormat(format);
    }

    /**
     * @return the currently used time format or null if default
     */
    public String getTimeFormat()
    {
        return timeFormat;
    }

    /**
     * Parses the configuration name from the given path.
     *
     * @param path the path to parse
     * @return configuration name
     */
    public static String getConfigNameFromPath(String path) {
        String name = path;
        if (name.charAt(0) == '/')
            name = name.substring(1);
        int idx = name.indexOf('/');
        return idx > 0 ? name.substring(0, idx) : name;
    }
}
