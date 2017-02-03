/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.diag.epics.pvtree;

import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;

/** Access to preferences
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class Preferences
{
    public static final String UPDATE_PERIOD = "update_period";
    public static final String MAX_ALARM_PVs = "max_alarm_pvs";
    public static final String FIELDS = "fields";

    /** @return Max update period in seconds */
    public static double getUpdatePeriod()
    {
        final IPreferencesService preferences = Platform.getPreferencesService();
        return preferences.getDouble(Plugin.ID, UPDATE_PERIOD, 0.1, null);
    }

    /** @return Max number of alarm PVs to reveal */
    public static int getMaxAlarmPVs()
    {
        final IPreferencesService preferences = Platform.getPreferencesService();
        return preferences.getInt(Plugin.ID, MAX_ALARM_PVs, 100, null);
    }

    /** @return Field info for all record types
     *  @throws Exception on error in the preference setting
     *  @see FieldParser
     */
    public static Map<String, List<String>> getFieldInfo() throws Exception
    {
        final IPreferencesService preferences = Platform.getPreferencesService();
        final String fields_pref =
            preferences.getString(Plugin.ID, FIELDS, null, null);
        if (fields_pref == null)
            throw new Exception("Missing preference setting");
        return FieldParser.parse(fields_pref);
    }
}
