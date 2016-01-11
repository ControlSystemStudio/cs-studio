/*******************************************************************************
* Copyright (c) 2010-2016 ITER Organization.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
******************************************************************************/
package org.csstudio.alarm.beast.notifier;

import java.util.logging.Level;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;

/** Read preferences
 *  <p>
 *  See preferences.ini for explanation of supported preferences.
 *  @author Fred Arnaud (Sopra Group)
 *  @author Xinyu Wu - notify only on escalating alarms
 */
@SuppressWarnings("nls")
public class Preferences {
    final public static String TIMER_THRESHOLD = "timer_threshold";
    final public static String VERBOSE_LOG_LEVEL = "verbose_log.level";
    final public static String NOTIFY_ESCALATING_ALARMS_ONLY = "notify_escalating_alarms_only";

    /**
     * @param setting Preference identifier
     * @return String from preference system, or <code>null</code>
     */
    private static String getString(final String setting) {
        return getString(setting, null);
    }

    /**
     * @param setting Preference identifier
     * @param default_value Default value when preferences unavailable
     * @return String from preference system, or <code>null</code>
     */
    private static String getString(final String setting,
            final String default_value) {
        final IPreferencesService service = Platform.getPreferencesService();
        if (service == null)
            return default_value;
        return service.getString(Activator.ID, setting, default_value, null);
    }

    /** @return threshold for automated actions */
    public static int getTimerThreshold() {
        final IPreferencesService service = Platform.getPreferencesService();
        if (service == null)
            return 100; // default
        return service.getInt(Activator.ID, TIMER_THRESHOLD, 100, null);
    }

    /** @return {@link Level} for verbose log */
    public static Level getVerboseLogLevel() {
        String levelStr = getString(VERBOSE_LOG_LEVEL);
        try {
            return Level.parse(levelStr);
        } catch (Exception e) {
            Activator.getLogger().log(Level.WARNING,
                    "Illegal console log level '" + levelStr + "'");
            return Level.WARNING;
        }
    }

    /** @return NOTIFY_ESCALATING_ALARMS_ONLY for automated actions */
    public static boolean getNotifyEscalatingAlarmsOnly() {
        final IPreferencesService service = Platform.getPreferencesService();
        if (service == null)
            return false; // default
        return service.getBoolean(Activator.ID, NOTIFY_ESCALATING_ALARMS_ONLY, false, null);
    }
}
