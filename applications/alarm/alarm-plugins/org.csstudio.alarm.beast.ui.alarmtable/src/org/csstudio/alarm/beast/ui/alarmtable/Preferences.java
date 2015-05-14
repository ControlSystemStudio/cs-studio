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

/** Read preference settings.
 *  Defaults for the application are provided in preferences.ini.
 *  Final product can override in plugin_preferences.ini.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class Preferences
{
    /** Preference and dialog tag for combined vs. separate alarm tables */
    final public static String ALARM_TABLE_GROUP_SETTING = "combined_alarm_table";

    /** Preference and dialog tag for column names */
    final public static String ALARM_TABLE_COLUMN_SETTING = "alarm_table_columns";

    /** @return Alarm table row limit */
    public static int getAlarmTableRowLimit()
    {
        final IPreferencesService service = Platform.getPreferencesService();
        return service.getInt(Activator.ID, "alarm_table_row_limit", 2500, null);
    }

    public static boolean isCombinedAlarmTable()
    {
        final IPreferencesService service = Platform.getPreferencesService();
        return service.getBoolean(Activator.ID, ALARM_TABLE_GROUP_SETTING, false, null);
    }

    public static String[] getColumns()
    {
        final IPreferencesService service = Platform.getPreferencesService();
        final String pref = service.getString(Activator.ID, ALARM_TABLE_COLUMN_SETTING, "ICON, PV, DESCRIPTION, TIME, CURRENT_SEVERITY, CURRENT_STATUS, SEVERITY, STATUS, VALUE", null);
        return pref.split(" *, *"); // Comma-separated, allowing for spaces
    }

}
