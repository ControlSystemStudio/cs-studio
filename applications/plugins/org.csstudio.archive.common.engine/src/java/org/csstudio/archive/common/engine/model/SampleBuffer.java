/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.common.engine.model;

import java.io.Serializable;
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
 *
 * Is implemented atop of LinkedBlockingQueue with Forwarding pattern. Hence,
 * a producer consumer scenario can be installed.
 *
 *  @author Kay Kasemir
 *  @author Bastian Knerr
 *
 *  @param <V> the base value type
 *  @param <T> the css value type atop the base value
 *  @param <S> the archive sample type atop the channel and the css value type
 */
public class SampleBuffer<V extends Serializable,
                          T extends ISystemVariable<V>,
                          S extends IArchiveSample<V, T>> extends ForwardingBlockingQueue<S> {

    /** Name of channel that writes to this buffer.
     *  (we keep only the name, not the full channel,
     *  to decouple stuff).
     */
    private final String _channelName;

    /** The actual samples in a thread-save queue. */
    private final BlockingQueue<S> _samples;

    /** Statistics */
    private final SampleBufferStatistics _stats = new SampleBufferStatistics();


    /**
     * Create sample buffer with flexible capacity
     */
    SampleBuffer(@Nonnull final String channelName) {
        super();

        this._channelName = channelName;
        _samples = new LinkedBlockingQueue<S>();
    }

    /** @return channel name of this buffer */
    @Nonnull
    String getChannelName() {
        return _channelName;
    }


    /** Update stats with current values */
    void updateStats() {
        _stats.updateSizes(super.size());
    }

    /** @return Buffer statistics. */
    @Nonnull
    public SampleBufferStatistics getBufferStats() {
        return _stats;
    }

    /** Reset statistics */
    public void statsReset() {
        _stats.reset();
    }

    @Override
    @Nonnull
    public String toString() {
        return String.format(
        "Sample buffer '%s': %d samples, %d samples max, %.1f samples average",
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

    /**
     * {@inheritDoc}
     * Offers a sample to the wrapped queue.
     */
    @Override
    public boolean add(@Nonnull final S value) {
        return delegate().offer(value);
    }
}
