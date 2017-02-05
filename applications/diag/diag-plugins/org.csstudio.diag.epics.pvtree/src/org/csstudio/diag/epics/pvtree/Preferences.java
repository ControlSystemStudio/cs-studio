/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.diag.epics.pvtree;

import static org.csstudio.diag.epics.pvtree.Plugin.logger;

import java.io.FileInputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;

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
        double period = 0.2;
        if (preferences != null)
            period = preferences.getDouble(Plugin.ID, UPDATE_PERIOD, period, null);
        return period;
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
    public static Map<String, List<String>> getFieldInfo()
    {
        try
        {
            final IPreferencesService preferences = Platform.getPreferencesService();
            final String fields_pref;
            if (preferences == null)
            {   // For unit tests without OSGi, read local file
                final Properties props = new Properties();
                props.load(new FileInputStream("../org.csstudio.diag.epics.pvtree/preferences.ini"));
                fields_pref = props.getProperty(FIELDS);
            }
            else
                fields_pref = preferences.getString(Plugin.ID, FIELDS, null, null);
            return FieldParser.parse(fields_pref);
        }
        catch (Exception ex)
        {
            logger.log(Level.SEVERE, "Cannot get field information", ex);
        }
        return Collections.emptyMap();
    }
}
