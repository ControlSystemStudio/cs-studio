/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.reader.channelarchiver;

import org.csstudio.archive.reader.ValueIterator;
import org.csstudio.archive.vtype.VTypeHelper;
import org.epics.util.time.Timestamp;
import org.epics.vtype.VType;

/** ValueIterator that runs subsequent ValuesRequests until
 *  reaching the end time.
 *  This is necessary because individual an ValueRequest might
 *  end before reaching the end time because of limitations to
 *  the number of samples returned in one transaction by the server.
 *  @author Kay Kasemir
 */
public class ValueRequestIterator implements ValueIterator
{
    final private ChannelArchiverReader reader;
    final private int key;
    final private String name;
    final private Timestamp end;
    final private boolean optimized;
    final private int count;

    private int index;
    private VType samples[];

    /** Constructor for new value request.
     *  @param reader ChannelArchiverReader
     *  @param key Archive key
     *  @param channel Channel name
     *  @param start Start time for retrieval
     *  @param end  End time for retrieval
     *  @param optimized Get optimized or raw data?
     *  @param count Number of values
     * @throws Exception on error
     */
    public ValueRequestIterator(final ChannelArchiverReader reader,
    		final int key, final String name, final Timestamp start, final Timestamp end, final boolean optimized,
    		final int count) throws Exception
    {
        this.reader = reader;
        this.key = key;
        this.name = name;
        this.end = end;
        this.optimized = optimized;
        this.count = count;

        fetch(start);
    }

    /** Fetch another batch of samples
     *
     *  @param fetch_start Start time for this batch
     *         (greater or equal to overall start time)
     *  @throws Exception on error
     */
    private void fetch(final Timestamp fetch_start) throws Exception
    {
        index = 0;
        samples = reader.getSamples(key, name, fetch_start, end, optimized, count);
        // Empty samples?
        if (samples != null  &&  samples.length <= 0)
            samples = null;
    }

    /** {@inheritDoc} */
    @Override
    public boolean hasNext()
    {
        return samples != null;
    }

    /** {@inheritDoc} */
    @Override
    public VType next() throws Exception
    {
        final VType result = samples[index];
        ++index;
        if (index < samples.length)
            return result;

        // Prepare next batch of samples
        fetch(VTypeHelper.getTimestamp(result));
        if (samples == null)
            return result;

        // Inspect next batch of samples
        // In most cases, this fetch should return the 'result' again:
        //   some_timestamp value A
        //   last_timestamp value B <-- last_sample of previous batch
        //   new_timestamp  value C
        // Since we ask from at-or-before last_timestamp on,
        // we get the last_sample once more and need to skip it.
        //
        // But also consider the following situation, where the last batch ended
        // in a range of data that had the same time stamp, and even some same
        // values:
        //   some_timestamp value A
        //   last_timestamp value B
        //   last_timestamp value C
        //   last_timestamp value C
        //   last_timestamp value C <-- last_sample of previous batch
        //   last_timestamp value C
        //   last_timestamp value C
        //   last_timestamp value D
        //   last_timestamp value E
        //   new_timestamp  value F
        // Reasons for same timestamps: Stuck IOC clock,
        // or sequences like .. Repeat N, next value, Disconnected, Arch. Off.
        // Reason for the same value: General mess-up.
        //
        // When we request new data from 'last_sample.getTime()' on,
        // i.e. from last_timestamp on, we could get any of the values B to E,
        // since they're all stamped at-or-before last_timestamp.
        // Which one exactly depends on optimization inside the data server.

        // From the end of the new samples, go backward:
        for (index=samples.length-1;  index>=0;  --index)
        {   // If we find the previous batch's last sample...
            if (samples[index].equals(result))
                // Skip all the samples up to and including it
                break;
        }
        // Nothing to skip? Return as is.
        if (index < 0)
            index = 0;
        // Nothing left? Clear samples.
        if (index >= samples.length-1)
            samples = null;
        return result;
    }

    /** {@inheritDoc} */
    @Override
    public void close()
    {
        samples = null;
    }
}
