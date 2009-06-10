package org.csstudio.archive.engine.model;

import org.csstudio.apputil.time.PeriodFormat;
import org.csstudio.platform.data.IValue;

/** An ArchiveChannel that stores each incoming value.
 *  @author Kay Kasemir
 */
public class MonitoredArchiveChannel extends ArchiveChannel
{
    /** Estimated period of change in seconds */
    final private double period_estimate;

    /** @see ArchiveChannel#ArchiveChannel(String, int, IValue) */
    public MonitoredArchiveChannel(final String name,
                                   final Enablement enablement,
                                   final int buffer_capacity,
                                   final IValue last_archived_value,
                                   final double period_estimate) throws Exception
    {
        super(name, enablement, buffer_capacity, last_archived_value);
        this.period_estimate = period_estimate;
    }

    @SuppressWarnings("nls")
    @Override
    public String getMechanism()
    {
        return "on change [" + PeriodFormat.formatSeconds(period_estimate) + "]";
    }

    /** Attempt to add each new value to the buffer. */
    @Override
    protected void handleNewValue(final IValue value)
    {
        super.handleNewValue(value);
        if (isEnabled())
            addValueToBuffer(value);
    }
}
