/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.engine;

import java.util.logging.Level;

import org.csstudio.apputil.time.PeriodFormat;
import org.csstudio.util.time.Throttle;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;

/** Logger that only allows a certain message rate.
 *  @author Kay Kasemir
 */
public class ThrottledLogger
{
    /** Log level */
    final private Level level;

    /** Throttle for the message rate */
    final private Throttle throttle;

    /** <code>true</code> when in the 'be quiet' state */
    private boolean throttled = false;

    /** Initialize
     *  @param info Log level to use
     *  @param preference_name Preference name
     */
    public ThrottledLogger(final Level info,
            final String preference_name)
    {
        this.level = info;
        double secs = 60.0;
        final IPreferencesService prefs = Platform.getPreferencesService();
        if (prefs != null)
            secs = prefs.getDouble(Activator.ID, preference_name, secs, null);
        this.throttle = new Throttle(secs);
    }

    /** Initialize
     *  @param level Log level to use
     *  @param seconds_between_messages Seconds between allowed messages
     */
    public ThrottledLogger(final Level level,
            final double seconds_between_messages)
    {
        this.level = level;
        throttle = new Throttle(seconds_between_messages);
    }

    /** Add throttled info message to the plugin log. */
    @SuppressWarnings("nls")
    public boolean log(final String message)
    {
        if (throttle.isPermitted())
        {   // OK, show
            Activator.getLogger().log(level, message);
            throttled = false;
            return true;
        }
        // Show nothing
        if (throttled)
            return false;
        // Last message to be shown for a while
        Activator.getLogger().log(level, message
                + "\n... More messsages suppressed for "
                + PeriodFormat.formatSeconds(throttle.getPeriod())
                + " ....");
        throttled = true;
        return true;
    }
}
