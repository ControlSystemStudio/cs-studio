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

import javax.annotation.Nonnull;

import org.csstudio.archive.common.service.sample.IArchiveSample;
import org.csstudio.domain.desy.system.ISystemVariable;

import com.google.common.util.concurrent.ForwardingBlockingQueue;

/**
 * Buffer for the samples of one channel.
 * <p>
 * Assumes that one thread adds samples, while a different
 * thread removes them.
 * When the queue capacity is exceeded, older samples get dropped.
 *
 * Is implemented atop of LinkedBlockingQueue with Forwarding pattern. Hence,
 * a producer consumer scenario can be assumed.
 *
 *  @author Kay Kasemir
 *  @author Bastian Knerr
 *
 *  @param <V> the base value type
 *  @param <T> the css value type atop the base value
 *  @param <S> the archive sample type atop the channel and the css value type
 */
public class SampleBuffer<V,
                          T extends ISystemVariable<V>,
                          S extends IArchiveSample<V, T>> extends ForwardingBlockingQueue<S>
{
    /** Name of channel that writes to this buffer.
     *  (we keep only the name, not the full channel,
     *  to decouple stuff).
     */
    private final String _channelName;

    /** The actual samples in a thread-save queue. */
    private final BlockingQueue<S> _samples;

    /** Statistics */
    private final BufferStats _stats = new BufferStats();

    /** Is the buffer in an error state because of RDB write errors?
     *  Note that this is global for all buffers, not per instance!
     */
    private static volatile boolean ERROR = false;

    /**
     * Create sample buffer with flexible capacity
     */
    SampleBuffer(@Nonnull final String channelName) {
        super();

        this._channelName = channelName;
        _samples = new LinkedBlockingQueue<S>(); // step by step to a producer consumer pattern
    }

    /** @return channel name of this buffer */
    @Nonnull
    String getChannelName() {
        return _channelName;
    }

    /** @return <code>true</code> if currently experiencing write errors */
    public static boolean isInErrorState() {
        return ERROR;
    }

    /** Set the error state. */
    static void setErrorState(final boolean error) {
        SampleBuffer.ERROR = error;
    }

    /**
     * Add a sample to the queue, maybe dropping older samples
     */
    @Override
    @SuppressWarnings("nls")
    public boolean add(@Nonnull final S value) {
	    if (!super.offer(value)) {
            // FIXME (bknerr) : data rescue if adding to sample buffer failed.
	        return false;
        }
    	return true;
    }

    /** Update stats with current values */
    void updateStats() {
        _stats.updateSizes(super.size());
    }

    /** @return Buffer statistics. */
    @Nonnull
    public BufferStats getBufferStats() {
        return _stats;
    }

    /** Reset statistics */
    public void statsReset() {
        _stats.reset();
    }

    @SuppressWarnings("nls")
    @Override
    @Nonnull
    public String toString() {
        return String.format(
        "Sample buffer '%s': %d samples, %d samples max, %.1f samples average, %d overruns",
            _channelName,
            super.size(),
            _stats.getMaxSize(),
            _stats.getAverageSize());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    protected BlockingQueue<S> delegate() {
        return _samples;
    }
}
