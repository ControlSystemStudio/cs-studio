/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.server;

import org.csstudio.apputil.time.SecondsParser;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;

/** Alarm Server Preferences
 *
 *  Defaults for the application are provided in preferences.ini, see there
 *  for more detailed explanations.
 *
 *  Final product can override in plugin_preferences.ini.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class AlarmServerPreferences
{
    final public static String GLOBAL_ALARM_DELAY = "global_alarm_delay";
    final public static String NAG_PERIOD = "nag_period";

    /** @return Period for repeated 'There are .. active alarms' annunciations [seconds]
     *  @throws Exception on error in period specification
     */
    public static double getNagPeriod() throws Exception
    {
        String nag_spec = "00:15:00";
        final IPreferencesService service = Platform.getPreferencesService();
        if (service != null)
            nag_spec = service.getString(Activator.ID, NAG_PERIOD, nag_spec, null);
        return SecondsParser.parseSeconds(nag_spec);
    }

    /** @return Delay for sending 'global' notification for un-acknowledged alarms [seconds] */
    public static int getGlobalAlarmDelay()
    {
        final IPreferencesService service = Platform.getPreferencesService();
        if (service == null)
            return 0;
        return service.getInt(Activator.ID, GLOBAL_ALARM_DELAY, 0, null);
    }
}
