package org.csstudio.archive.engine.model;

import org.csstudio.apputil.time.PeriodFormat;
import org.csstudio.archive.engine.Activator;
import org.csstudio.platform.data.IValue;

/** An ArchiveChannel that stores value in a periodic scan.
 *  TODO Check for repeat counts?
 *  @author Kay Kasemir
 */
public class ScannedArchiveChannel extends ArchiveChannel implements Runnable
{
    /** Scan period in seconds */
    final private double scan_period;
    
    /** @see ArchiveChannel#ArchiveChannel(String, int, IValue) */
    public ScannedArchiveChannel(final String name,
                                 Enablement enablement, final int buffer_capacity,
                                 final IValue last_archived_value,
                                 final double scan_period) throws Exception
    {
        super(name, enablement, buffer_capacity, last_archived_value);
        this.scan_period = scan_period;
    }
    
    /** @return Scan period in seconds */
    final public double getPeriod()
    {
        return scan_period;
    }

    @Override
    public String getMechanism()
    {
        return PeriodFormat.formatSeconds(scan_period) + " scan"; //$NON-NLS-1$
    }

    /** Run one scan of this channel, i.e. try to add the current value
     *  to the archive
     */
    @SuppressWarnings("nls")
    final public void run()
    {
        if (! isEnabled())
            return;
        IValue recent;
        synchronized (this)
        {
            recent = most_recent_value;
        }
        if (recent == null)
            return;
        final IValue value = ValueButcher.transformTimestamp(recent);
        if (value == null)
            Activator.getLogger().error("Channel " + getName()
                            + ": Cannot handle value type "
                            + recent.getClass().getName());
        addValueToBuffer(value);
    }
}
