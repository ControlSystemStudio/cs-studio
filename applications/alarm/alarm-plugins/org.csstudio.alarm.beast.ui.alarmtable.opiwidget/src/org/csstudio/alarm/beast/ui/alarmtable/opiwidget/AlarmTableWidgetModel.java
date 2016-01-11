/*******************************************************************************
 * Copyright (c) 2010-2016 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.ui.alarmtable.opiwidget;

import org.csstudio.alarm.beast.ui.alarmtable.AlarmTableView;
import org.csstudio.alarm.beast.ui.alarmtable.ColumnInfo;
import org.csstudio.alarm.beast.ui.alarmtable.ColumnWrapper;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.properties.BooleanProperty;
import org.csstudio.opibuilder.properties.ComboProperty;
import org.csstudio.opibuilder.properties.IntegerProperty;
import org.csstudio.opibuilder.properties.StringProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;

/**
 *
 * <code>AlarmTableWidgetModel</code> is the OPI Builder model for the Alarm Table Widget.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class AlarmTableWidgetModel extends AbstractWidgetModel {

    private static final String ID = "org.csstudio.alarm.beast.ui.alarmtable";

    static final String PROP_WRITABLE = "writable";
    static final String PROP_SEPARATE_TABLES = "separate_tables";
    static final String PROP_SORTING_COLUMN = "sorting_column";
    static final String PROP_SORT_ASCENDING = "sort_ascending";
    static final String PROP_UNACKNOWLEDGED_BLINKING = "unacknowledged_blinking";
    static final String PROP_TIMEFORMAT = "time_format";
    static final String PROP_FILTER_ITEM = "filter_item";
    static final String PROP_COLUMNS = "columns";
    static final String PROP_MAX_NUMBER_OF_ALARMS = "max_number_of_alarms";
    static final String PROP_ACK_TABLE_WEIGHT = "table_weight_acknowledge";
    static final String PROP_UNACK_TABLE_WEIGHT = "table_weight_unacknowledge";
    static final String PROP_TABLE_HEADER_VISIBLE = "table_header_visible";
    static final String PROP_COLUMNS_HEADERS_VISIBLE = "columns_headers_visible";

    /*
     * (non-Javadoc)
     *
     * @see org.csstudio.opibuilder.model.AbstractWidgetModel#configureProperties()
     */
    @Override
    protected void configureProperties() {
        addProperty(new BooleanProperty(PROP_WRITABLE, Messages.Writable, WidgetPropertyCategory.Behavior, true));
        addProperty(new BooleanProperty(PROP_SORT_ASCENDING, Messages.SortAscending, WidgetPropertyCategory.Behavior,
                false));
        addProperty(new ComboProperty(PROP_SORTING_COLUMN, Messages.SortingColumn, WidgetPropertyCategory.Behavior,
                ColumnInfo.stringValues(), 4));
        addProperty(new BooleanProperty(PROP_SEPARATE_TABLES, Messages.SeparateTables, WidgetPropertyCategory.Display,
                true));
        addProperty(new BooleanProperty(PROP_UNACKNOWLEDGED_BLINKING, Messages.UnacknowledgedBlinking,
                WidgetPropertyCategory.Behavior, true));
        addProperty(new StringProperty(PROP_TIMEFORMAT, Messages.TimeFormat, WidgetPropertyCategory.Behavior,
                "dd. MMM HH:mm:ss.S"));
        addProperty(new ColumnsProperty(PROP_COLUMNS, Messages.Columns, WidgetPropertyCategory.Display));
        addProperty(
                new StringProperty(PROP_FILTER_ITEM, Messages.FilterItem, WidgetPropertyCategory.Behavior, "/demo"));
        addProperty(new IntegerProperty(PROP_MAX_NUMBER_OF_ALARMS, Messages.MaxNumberOfAlarms,
                WidgetPropertyCategory.Display, 50));
        addProperty(new IntegerProperty(PROP_ACK_TABLE_WEIGHT, Messages.AckTableWeight, WidgetPropertyCategory.Display,
                80));
        addProperty(new IntegerProperty(PROP_UNACK_TABLE_WEIGHT, Messages.UnAckTableWeight,
                WidgetPropertyCategory.Display, 20));
        addProperty(new BooleanProperty(PROP_TABLE_HEADER_VISIBLE, Messages.TableHeaderVisible,
                WidgetPropertyCategory.Display, false));
        addProperty(new BooleanProperty(PROP_COLUMNS_HEADERS_VISIBLE, Messages.ColumnsHeadersVisible,
                WidgetPropertyCategory.Display, true));
    }

    /*
     * (non-Javadoc)
     *
     * @see org.csstudio.opibuilder.model.AbstractWidgetModel#getTypeID()
     */
    @Override
    public String getTypeID() {
        return ID;
    }

    /**
     * @return the name of the alarm config root to set on the table
     */
    public String getAlarmConfigName() {
        String filterItem = getFilterItem();
        if (filterItem != null && filterItem.length() > 0) {
            return AlarmTableView.getConfigNameFromPath(filterItem);
        } else {
            return null;
        }
    }

    /**
     * @return the full path to the item used for filtering
     */
    public String getFilterItem() {
        return getCastedPropertyValue(PROP_FILTER_ITEM);
    }

    /**
     * @return true if writable actions are allowed on the table or false otherwise
     */
    public boolean isWritable() {
        return getCastedPropertyValue(PROP_WRITABLE);
    }

    /**
     * @return true if separate tables are used for acknowledged and unacknowledged alarms or false if all alarms are
     *         displayed in the same table
     */
    public boolean isSeparateTables() {
        return getCastedPropertyValue(PROP_SEPARATE_TABLES);
    }

    /**
     * @return true if sorting direction is ascending or false if descending
     */
    public boolean isSortAscending() {
        return getCastedPropertyValue(PROP_SORT_ASCENDING);
    }

    /**
     * @return the column used for sorting
     */
    public ColumnInfo getSortingColumn() {
        Integer i = getCastedPropertyValue(PROP_SORTING_COLUMN);
        return ColumnInfo.values()[i];
    }

    /**
     * @return the array of columns in proper order
     */
    public ColumnWrapper[] getColumns() {
        ColumnsInput ci = getCastedPropertyValue(PROP_COLUMNS);
        return ci.getColumns();
    }

    /**
     * @return true if unacknowledged alarms icons are blinking or false otherwise
     */
    public boolean isUnacknowledgedBlinking() {
        return getCastedPropertyValue(PROP_UNACKNOWLEDGED_BLINKING);
    }

    /**
     * @return the format used for formating the date and time column data
     */
    public String getTimeFormat() {
        return getCastedPropertyValue(PROP_TIMEFORMAT);
    }

    /**
     * @return maximum number of alarms displayed in the table
     */
    public int getMaxNumberOfAlarms() {
        Integer val = getCastedPropertyValue(PROP_MAX_NUMBER_OF_ALARMS);
        return val == null ? 0 : val;
    }

    /**
     * @return the weight of the acknowledged table (defines the default sash size if separate tables are used)
     */
    public int getAcknowledgeTableWeight() {
        Integer val = getCastedPropertyValue(PROP_ACK_TABLE_WEIGHT);
        return val == null ? 0 : val;
    }

    /**
     * @return the weight of the unkacknowledged table (defines the default sash size if separate tables are used)
     */
    public int getUnacknowledgeTableWeight() {
        Integer val = getCastedPropertyValue(PROP_UNACK_TABLE_WEIGHT);
        return val == null ? 0 : val;
    }

    /**
     * Set the column used for sorting.
     *
     * @param info the column info to set
     */
    public void setSortingColumn(ColumnInfo info) {
        ColumnInfo[] infos = ColumnInfo.values();
        for (int i = 0; i < infos.length; i++) {
            if (infos[i] == info) {
                setPropertyValue(PROP_SORTING_COLUMN, i);
                return;
            }
        }
    }

    /**
     * @return true if the columns headers should be visible or false if hidden
     */
    public boolean isColumnsHeadersVisible() {
        Boolean val = getCastedPropertyValue(PROP_COLUMNS_HEADERS_VISIBLE);
        return val == null ? true : val;
    }

    /**
     * @return true if the table header should be visible or false if hidden
     */
    public boolean isTableHeaderVisible() {
        Boolean val = getCastedPropertyValue(PROP_TABLE_HEADER_VISIBLE);
        return val == null ? true : val;
    }
}
