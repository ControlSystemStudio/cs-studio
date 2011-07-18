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
    /** @return Delay in millisecs for the initial update after trigger */
    public static long getInitialMillis()
    {
        final IPreferencesService service = Platform.getPreferencesService();
        return service.getLong(Activator.ID, "initial_millis", 100, null);
    }

    /** @return Delay in millisecs for the suppression of a burst of events */
    public static long getSuppressionMillis()
    {
        final IPreferencesService service = Platform.getPreferencesService();
        return service.getLong(Activator.ID, "suppression_millis", 1000, null);
    }

    /** @return Alarm table row limit */
	public static int getAlarmTableRowLimit()
    {
        final IPreferencesService service = Platform.getPreferencesService();
        return service.getInt(Activator.ID, "alarm_table_row_limit", 2500, null);
    }
}
