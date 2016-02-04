/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.ui.alarmtable;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;

/**
 * Read preference settings. Defaults for the application are provided in preferences.ini. Final product can override in
 * plugin_preferences.ini.
 *
 * @author Kay Kasemir
 * @author Boris Versic - settings for Background colors
 */
@SuppressWarnings("nls")
public class Preferences
{
    /** Preference and memento tag for combined vs. separate alarm tables */
    final public static String ALARM_TABLE_COMBINED_TABLES = "combined_alarm_table"; //$NON-NLS-1$

    /** Preference and memento tag for column names */
    final public static String ALARM_TABLE_COLUMN_SETTING = "alarm_table_columns"; //$NON-NLS-1$

    /** Preference and memento tag for synchronise alarms with the tree selection */
    final public static String ALARM_TABLE_FILTER_TYPE = "alarm_table_filter_type"; //$NON-NLS-1$

    /** Memento tag for the selected filter */
    final public static String ALARM_TABLE_FILTER_ITEM = "alarm_table_filter_item"; //$NON-NLS-1$

    /** Preference tag for blinking of unacknowledged alarms icons */
    final public static String ALARM_TABLE_BLINK_UNACKNOWLEDGED = "blink_unacknowledged"; //$NON-NLS-1$

    /** Preference tag for blinking period */
    final public static String ALARM_TABLE_BLINK_PERIOD = "blinking_period"; //$NON-NLS-1$

    /** Preference tag for painting row background with the alarm's severity color */
    final public static String ALARM_TABLE_BACKGROUND_ALARM_SENSITIVE = "background_alarm_sensitive"; //$NON-NLS-1$

    /** Preference tag for reversing message color of cleared (unacknowledged) alarms (if background colors are enabled) */
    final public static String ALARM_TABLE_REVERSE_COLORS = "reverse_colors_cleared"; //$NON-NLS-1$

    /** Preference tag for text colors of alarm message, per severity: Severity,R,G,B|... */
    final public static String ALARM_TABLE_SEVERITY_PAIR_COLORS = "severity_pair_colors"; //$NON-NLS-1$

    /** Preference and memento tag for the time format in the time column */
    final public static String ALARM_TABLE_TIME_FORMAT = "time_format"; //$NON-NLS-1$

    /** Memento tag for the sorting column name */
    final public static String ALARM_TABLE_SORT_COLUMN = "alarm_table_sort_column"; //$NON-NLS-1$

    /** Memento tag for the sorting direction */
    final public static String ALARM_TABLE_SORT_UP = "alarm_table_sort_up"; //$NON-NLS-1$

    /** @return Alarm table row limit */
    public static int getAlarmTableRowLimit()
    {
        final IPreferencesService service = Platform.getPreferencesService();
        return service.getInt(Activator.ID, "alarm_table_row_limit", 2500, null);
    }

    /** @return true if acknowledged an unacknowledged alarms are shown in a single table or false otherwise */
    public static boolean isCombinedAlarmTable()
    {
        final IPreferencesService service = Platform.getPreferencesService();
        return service.getBoolean(Activator.ID, ALARM_TABLE_COMBINED_TABLES, false, null);
    }

    /** @return list of default columns for the table and their order */
    public static String[] getColumns()
    {
        final IPreferencesService service = Platform.getPreferencesService();
        final String pref = service.getString(Activator.ID, ALARM_TABLE_COLUMN_SETTING,
                "ICON,35,0| PV,80,50| DESCRIPTION,80,100| TIME,80,70| CURRENT_SEVERITY,50,30|" //$NON-NLS-1$
                        + "CURRENT_STATUS,45,30| SEVERITY,50,30| STATUS,45,30| VALUE,45,30", null); //$NON-NLS-1$
        return pref.split(" *\\| *"); // Vertical line-separated, allowing for spaces //$NON-NLS-1$
    }

    /** @return blinking icons of unacknowledged alarms */
    public static boolean isBlinkUnacknowledged()
    {
        final IPreferencesService service = Platform.getPreferencesService();
        return service.getBoolean(Activator.ID, ALARM_TABLE_BLINK_UNACKNOWLEDGED, false, null);
    }

    /** @return  */
    public static int getBlinkingPeriod()
    {
        final IPreferencesService service = Platform.getPreferencesService();
        return service.getInt(Activator.ID, ALARM_TABLE_BLINK_PERIOD, 500, null);
    }

    /** @return use reverse colors for recovered unacknowledged alarms */
    public static boolean isBackgroundColorAlarmSensitive()
    {
        final IPreferencesService service = Platform.getPreferencesService();
        return service.getBoolean(Activator.ID, ALARM_TABLE_BACKGROUND_ALARM_SENSITIVE, false, null);
    }

    /** @return use reverse colors for recovered unacknowledged alarms (if background colors are enabled) */
    public static boolean isColorsReversed()
    {
        final IPreferencesService service = Platform.getPreferencesService();
        return service.getBoolean(Activator.ID, ALARM_TABLE_REVERSE_COLORS, false, null);
    }

    /** @return use reverse colors for recovered unacknowledged alarms */
    public static String[] getSeverityPairColors()
    {
        final IPreferencesService service = Platform.getPreferencesService();
        final String pref = service.getString(Activator.ID, ALARM_TABLE_SEVERITY_PAIR_COLORS, "", null); //$NON-NLS-1$
        return pref.split(" *\\| *"); // Vertical line-separated, allowing for spaces //$NON-NLS-1$
    }

    /** @return the time format */
    public static String getTimeFormat()
    {
        final IPreferencesService service = Platform.getPreferencesService();
        return service.getString(Activator.ID, ALARM_TABLE_TIME_FORMAT, null, null);
    }

}
