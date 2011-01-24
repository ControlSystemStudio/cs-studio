/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.common.engine.model;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Level;
import org.csstudio.archive.common.engine.ThrottledLogger;
import org.csstudio.domain.desy.types.ICssAlarmValueType;

import com.google.common.util.concurrent.ForwardingBlockingQueue;

/** Buffer for the samples of one channel.
 *  <p>
 *  Assumes that one thread adds samples, while a different
 *  thread removes them.
 *  When the queue capacity is exceeded, older samples get dropped.
 *
 *  @author Kay Kasemir
 */
public class SampleBuffer<T> extends ForwardingBlockingQueue<ICssAlarmValueType<T>>
{
    /** Name of channel that writes to this buffer.
     *  (we keep only the name, not the full channel,
     *  to decouple stuff).
     */
    final private String channel_name;

    /** The actual samples in a thread-save queue. */
//    final private RingBuffer<IValue> samples;
    private final BlockingQueue<ICssAlarmValueType<T>> samples;
//    private final int capacity;

    /** Statistics */
    final private BufferStats stats = new BufferStats();

    /** Number of overruns when new string of overruns started, or <code>null</code> */
    private Integer start_of_overruns;

    /** Logger for overrun messages */
    final private static ThrottledLogger overrun_msg =
        new ThrottledLogger(Level.WARN, "log_overrun"); //$NON-NLS-1$

    /** Is the buffer in an error state because of RDB write errors?
     *  Note that this is global for all buffers, not per instance!
     */
    private static volatile boolean error = false;

    /** Create sample buffer of given capacity */
    SampleBuffer(final String channel_name/*, final int cap */)
    {
        super();

        this.channel_name = channel_name;
        //samples = new RingBuffer<IValue>(capacity);
        //capacity = cap;
        samples = new LinkedBlockingQueue<ICssAlarmValueType<T>>(); // step by step to a producer consumer pattern
    }

    /** @return channel name of this buffer */
    String getChannelName()
    {
        return channel_name;
    }

    /** @return Queue capacity, i.e. maximum queue size. */
    public int getCapacity()
    {
        // for being a linked queue now, there isn't a fixed capacity
        return -1;
    }

    /** @return <code>true</code> if currently experiencing write errors */
    public static boolean isInErrorState()
    {
        return error;
    }

    /** Set the error state. */
    static void setErrorState(final boolean error)
    {
        SampleBuffer.error = error;
    }

    /** Add a sample to the queue, maybe dropping older samples */
    @Override
    @SuppressWarnings("nls")
    public boolean add(final ICssAlarmValueType<T> value)
    {
    	synchronized (samples)
        {
    	    //if (samples.isFull())
    	    if (!super.offer(value)) // is full, value not appended to queue
            {   // Note start of overruns, then drop older sample
                if (start_of_overruns == null) {
                    start_of_overruns = Integer.valueOf(stats.getOverruns());
                }
                stats.addOverrun();

                while (!super.offer(value)) { // TODO (bknerr) : not yet a strategy
                    if (super.poll() == null) { // drop samples as long appending doesn't work.
                        throw new IllegalStateException("Sample buffer cannot append value, although queue is empty (poll returns null).");
                    }
                }
            }
            else if (start_of_overruns != null)
            {   // Ending a string of overruns. Maybe log it.
                final int overruns = stats.getOverruns() - start_of_overruns;
                overrun_msg.log(channel_name + ": " + overruns + " overruns");
                start_of_overruns = null;
            }
        }
    	return true;
    }

    /** Update stats with current values */
    void updateStats()
    {
        stats.updateSizes(super.size());
    }

    /** @return Buffer statistics. */
    public BufferStats getBufferStats()
    {
        return stats;
    }

    /** Reset statistics */
    public void reset()
    {
        start_of_overruns = null;
        stats.reset();
    }

    @SuppressWarnings("nls")
    @Override
    public String toString()
    {
        return String.format(
        "Sample buffer '%s': %d samples, %d samples max, %.1f samples average, %d overruns",
            channel_name,
            super.size(),
            stats.getMaxSize(),
            stats.getAverageSize(),
            stats.getOverruns());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected BlockingQueue<ICssAlarmValueType<T>> delegate() {
        return samples;
    }
}
