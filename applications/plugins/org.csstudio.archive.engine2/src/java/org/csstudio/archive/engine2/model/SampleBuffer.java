/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.engine2.model;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Level;
import org.csstudio.archive.engine2.ThrottledLogger;
import org.csstudio.platform.data.IValue;

import com.google.common.util.concurrent.ForwardingBlockingQueue;

/** Buffer for the samples of one channel.
 *  <p>
 *  Assumes that one thread adds samples, while a different
 *  thread removes them.
 *  When the queue capacity is exceeded, older samples get dropped.
 *
 *  @author Kay Kasemir
 */
public class SampleBuffer extends ForwardingBlockingQueue<IValue>
{
    /** Name of channel that writes to this buffer.
     *  (we keep only the name, not the full channel,
     *  to decouple stuff).
     */
    final private String channel_name;

    /** The actual samples in a thread-save queue. */
//    final private RingBuffer<IValue> samples;
    private final BlockingQueue<IValue> samples;
    private final int capacity;

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
    SampleBuffer(final String channel_name, final int cap)
    {
        super();

        this.channel_name = channel_name;
        //samples = new RingBuffer<IValue>(capacity);
        capacity = cap;
        samples = new ArrayBlockingQueue<IValue>(capacity, true); // step by step to a producer consumer pattern
    }

    /** @return channel name of this buffer */
    String getChannelName()
    {
        return channel_name;
    }

    /** @return Queue capacity, i.e. maximum queue size. */
    public int getCapacity()
    {
        return capacity;
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
    public boolean add(final IValue value)
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
    protected BlockingQueue<IValue> delegate() {
        return samples;
    }
}
