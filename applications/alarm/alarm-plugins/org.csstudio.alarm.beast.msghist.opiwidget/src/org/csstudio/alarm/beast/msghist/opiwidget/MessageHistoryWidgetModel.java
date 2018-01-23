/*******************************************************************************
 * Copyright (c) 2010-2017 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.msghist.opiwidget;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.alarm.beast.msghist.Preferences;
import org.csstudio.alarm.beast.msghist.PropertyColumnPreference;
import org.csstudio.alarm.beast.msghist.model.FilterQuery;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.properties.BooleanProperty;
import org.csstudio.opibuilder.properties.ComboProperty;
import org.csstudio.opibuilder.properties.IntegerProperty;
import org.csstudio.opibuilder.properties.StringProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;
import org.eclipse.osgi.util.NLS;

/**
 *
 * <code>MessageHistoryWidgetModel</code> is the OPI Builder model for the
 * Message history widget.
 *
 * @author Borut Terpinc
 *
 */
public class MessageHistoryWidgetModel extends AbstractWidgetModel {

    private static final String ID = "org.csstudio.alarm.beast.msghist";

    private static final Logger LOGGER = Logger.getLogger(MessageHistoryWidgetModel.class.getName());

    static final String PROP_TIMEFORMAT = "time_format";
    static final String PROP_FILTER = "filter";
    static final String PROP_COLUMNS = "columns";
    static final String PROP_MAX_MESSAGES = "max_messages";
    static final String PROP_SORTING_COLUMN = "sorting_column";
    static final String PROP_SORT_ASCENDING = "sort_ascending";
    static final String PROP_COLUMN_HEADERS = "column_headers";

    private String[] defaultColumnNames;

    @Override
    protected void configureProperties() {
        String defaultTimeFormat = Preferences.getTimeFormat();
        addProperty(new StringProperty(PROP_TIMEFORMAT, Messages.TimeFormat, WidgetPropertyCategory.Behavior,
                defaultTimeFormat), false);

        String defaultQuery = FilterQuery.fromTimeSpec(Preferences.getDefaultStart(), Preferences.getDefaultEnd());
        addProperty(new FilterProperty(PROP_FILTER, Messages.Filter, WidgetPropertyCategory.Behavior, defaultQuery),
                false);

        int defaultMaxMessages = Preferences.getMaxMessages();
        addProperty(new IntegerProperty(PROP_MAX_MESSAGES, Messages.MaxMessages, WidgetPropertyCategory.Display,
                defaultMaxMessages, 1, Integer.MAX_VALUE), false);

        addProperty(
                new BooleanProperty(PROP_COLUMN_HEADERS, Messages.ColumnHeaders, WidgetPropertyCategory.Display, true));

        try {
            PropertyColumnPreference[] defaultColumns = Preferences.getPropertyColumns();
            addProperty(new ColumnsProperty(PROP_COLUMNS, Messages.Columns, WidgetPropertyCategory.Behavior,
                    new ColumnsInput(defaultColumns)), false);

            defaultColumnNames = Arrays.stream(defaultColumns).map(PropertyColumnPreference::toString)
                    .toArray(String[]::new);
            addProperty(new ComboProperty(PROP_SORTING_COLUMN, Messages.SortingColumn, WidgetPropertyCategory.Behavior,
                    defaultColumnNames, 0), false);

            addProperty(new BooleanProperty(PROP_SORT_ASCENDING, Messages.SortAscending,
                    WidgetPropertyCategory.Behavior, true), false);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, NLS.bind(Messages.PreferenceReadError, e.getMessage()));
        }

    }

    @Override
    public String getTypeID() {
        return ID;
    }

    /**
     * @return query used for filtering message history items.
     */
    public String getFilter() {
        return getCastedPropertyValue(PROP_FILTER);
    }

    /**
     * @return the array of columns and their properties in proper order
     */
    public PropertyColumnPreference[] getColumns() {
        ColumnsInput columns = getCastedPropertyValue(PROP_COLUMNS);
        return columns.getColumns();
    }

    /**
     * @return the format used for formating the date and time column data
     */
    public DateTimeFormatter getTimeFormat() {
        String format = getCastedPropertyValue(PROP_TIMEFORMAT);
        return DateTimeFormatter.ofPattern(format).withZone(ZoneId.systemDefault());
    }

    /**
     * @return maximum number of messages that should be queried/shown.
     */
    public int getMaxMessages() {
        return getCastedPropertyValue(PROP_MAX_MESSAGES);
    }

    /**
     * @return column/property that will be used for sorting.
     */
    public String getSortingColumn() {
        int col = getCastedPropertyValue(PROP_SORTING_COLUMN);
        return defaultColumnNames[col];
    }

    /**
     * @return true for ascending sorting and false for descending.
     */
    public boolean isSortAscending() {
        return getCastedPropertyValue(PROP_SORT_ASCENDING);
    }

    /**
     * @return true if column headers should be shown, false otherwise.
     */
    public boolean isColumnHeaders() {
        return getCastedPropertyValue(PROP_COLUMN_HEADERS);
    }
}
