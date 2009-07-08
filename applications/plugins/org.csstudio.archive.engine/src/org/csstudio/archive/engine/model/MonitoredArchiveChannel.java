package org.csstudio.archive.engine.model;

import org.apache.log4j.Logger;
import org.csstudio.apputil.time.PeriodFormat;
import org.csstudio.platform.data.IValue;
import org.csstudio.platform.logging.CentralLogger;

/** An ArchiveChannel that stores each incoming value.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class MonitoredArchiveChannel extends ArchiveChannel
{
    /** Estimated period of change in seconds */
    final private double period_estimate;
    private Logger log;

    /** @see ArchiveChannel#ArchiveChannel(String, int, IValue) */
    public MonitoredArchiveChannel(final String name,
                                   final Enablement enablement,
                                   final int buffer_capacity,
                                   final IValue last_archived_value,
                                   final double period_estimate) throws Exception
    {
        super(name, enablement, buffer_capacity, last_archived_value);
        this.period_estimate = period_estimate;
        log = CentralLogger.getInstance().getLogger(this);
        if (! log.isDebugEnabled())
            log = null;
    }

    @Override
    public String getMechanism()
    {
        return "on change [" + PeriodFormat.formatSeconds(period_estimate) + "]";
    }

    /** Attempt to add each new value to the buffer. */
    @Override
    protected boolean handleNewValue(final IValue value)
    {
        if (super.handleNewValue(value))
        {
            if (log != null)
                log.debug(getName() + " wrote first sample " + value);
            return true;
        }
        if (isEnabled())
        {
            if (log != null)
                log.debug(getName() + " writes " + value);
            addValueToBuffer(value);
            return true;
        }
        return false;
    }
}
