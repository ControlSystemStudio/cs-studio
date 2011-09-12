/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.common.trendplotter.model;

import org.csstudio.apputil.ringbuffer.RingBuffer;
import org.csstudio.common.trendplotter.preferences.Preferences;

/** Ring buffer for 'live' samples.
 *  <p>
 *  New samples are always added to the end of a ring buffer.
 * 
 *  @author Kay Kasemir
 */
public class LiveSamples extends PlotSamples
{
    private RingBuffer<PlotSample> samples =
        new RingBuffer<PlotSample>(Preferences.getLiveSampleBufferSize());
    
    /** @return Maximum number of samples in ring buffer */
    synchronized public int getCapacity()
    {
        return samples.getCapacity();
    }
    
    /** Set new capacity.
     *  <p>
     *  Tries to preserve the newest samples.
     *  @param new_capacity New sample count capacity
     *  @throws Exception on out-of-memory error
     */
    synchronized public void setCapacity(int new_capacity) throws Exception
    {
        if (new_capacity < 10)
            new_capacity = 10;
        samples.setCapacity(new_capacity);
    }

    /** @param sample Sample to add to ring buffer */
    synchronized void add(final PlotSample sample)
    {
        sample.setDeadband(deadband);
        samples.add(sample);
        have_new_samples = true;
    }

    @Override
    synchronized public int getSize()
    {
        return samples.size();
    }

    @Override
    synchronized public PlotSample getSample(final int i)
    {
        return samples.get(i);
    }

    /** Delete all samples */
    synchronized public void clear()
    {
        samples.clear();
    }
}
