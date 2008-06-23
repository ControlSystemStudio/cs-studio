package org.csstudio.archive.engine.model;

import org.csstudio.platform.data.IValue;

/** An ArchiveChannel that stores each incoming value.
 *  @author Kay Kasemir
 */
public class MonitoredArchiveChannel extends ArchiveChannel
{
    /** @see ArchiveChannel#ArchiveChannel(String, int, IValue) */
    public MonitoredArchiveChannel(final String name,
                                   final Enablement enablement,
                                   final int buffer_capacity,
                                   final IValue last_archived_value) throws Exception
    {
        super(name, enablement, buffer_capacity, last_archived_value);
    }

    @Override
    public String getMechanism()
    {
        return "on change"; //$NON-NLS-1$
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
