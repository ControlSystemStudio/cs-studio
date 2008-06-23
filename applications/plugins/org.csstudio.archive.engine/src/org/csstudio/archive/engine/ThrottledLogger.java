package org.csstudio.archive.engine;

import org.apache.log4j.Level;
import org.csstudio.apputil.time.PeriodFormat;
import org.csstudio.util.time.Throttle;

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
    
    /** @see Throttle */
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
