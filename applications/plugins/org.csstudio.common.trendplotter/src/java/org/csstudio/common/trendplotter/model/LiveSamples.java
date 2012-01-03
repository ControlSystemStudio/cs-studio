/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.common.trendplotter.model;

import org.csstudio.domain.common.collection.LimitedArrayCircularQueue;


/** Ring buffer for 'live' samples.
 *  <p>
 *  New samples are always added to the end of a ring buffer.
 *
 *  @author Kay Kasemir
 */
public class LiveSamples extends PlotSamples {

    protected LimitedArrayCircularQueue<PlotSample> _samples;

    /**
     * Constructor.
     */
    public LiveSamples(final int capacity) {
        _samples = new LimitedArrayCircularQueue<PlotSample>(capacity);
    }

    /** @return Maximum number of samples in ring buffer */
    synchronized public int getCapacity() {
        return _samples.getCapacity();
    }

    /** Set new capacity.
     *  <p>
     *  Tries to preserve the newest samples.
     *  @param new_capacity New sample count capacity
     *  @throws Exception on out-of-memory error
     */
    synchronized public void setCapacity(final int new_capacity) throws Exception
    {
        _samples.setCapacity(new_capacity);
    }

    /** @param sample Sample to add to ring buffer */
    synchronized void add(final PlotSample sample)
    {
        sample.setDeadband(deadband);
        _samples.add(sample);
        have_new_samples = true;
    }

    @Override
    synchronized public int getSize()
    {
        return _samples.size();
    }

    @Override
    synchronized public PlotSample getSample(final int i)
    {
        return _samples.get(i);
    }

    /** Delete all samples */
    synchronized public void clear()
    {
        _samples.clear();
    }
}
