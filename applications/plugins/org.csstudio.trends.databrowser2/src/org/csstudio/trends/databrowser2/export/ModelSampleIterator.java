/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.databrowser2.export;

import org.csstudio.archive.reader.ValueIterator;
import org.csstudio.archive.vtype.VTypeHelper;
import org.csstudio.trends.databrowser2.model.ModelItem;
import org.csstudio.trends.databrowser2.model.PlotSample;
import org.csstudio.trends.databrowser2.model.PlotSamples;
import org.epics.pvmanager.data.VType;
import org.epics.util.time.Timestamp;

/** Iterator for the samples in a ModelItem, not fetching archived data
 *  @author Kay Kasemir
 */
public class ModelSampleIterator implements ValueIterator
{
    /** Samples from which to return values from 'start' to 'end' */
    final private PlotSamples samples;

    /** End time */
    final private Timestamp end;

    /** The value returned by 'next' or undefined for 'index' < 0 */
    private VType value;

    /** Index of 'value' in 'samples', -1 for end-of-sequence */
    private int index;

    /** Initialize
     *  @param item Item from which to get samples
    /** @param start Start time
    /** @param end End time
     */
    public ModelSampleIterator(final ModelItem item, final Timestamp start,
            final Timestamp end)
    {
        this.samples = item.getSamples();
        this.end = end;
        synchronized (samples)
        {
            // Anything?
            if (samples.getSize() <= 0)
                index = -1;
            // All data after start time?
            else if (samples.getSample(0).getTime().compareTo(start) >= 0)
                index = 0;
            else
            {   // There is data before the start time. Find sample just before start time.
                index = findSampleLessOrEqual(start);
            }
            // Is first sample already after end time?
            if (index >= 0)
            {
                final PlotSample sample = samples.getSample(index);
                value = sample.getValue();
                if (sample.getTime().compareTo(end) > 0)
                    index = -1;
            }
        }
    }

    /** @param start Start time
     *  @return Index sample with time stamp at-or-before start time, or -1.
     */
    private int findSampleLessOrEqual(final Timestamp start)
    {
        // Would like to use PlotSampleSearch, but that operates on array
        // of PlotSample[]
        int low = 0;
        int high = samples.getSize()-1;
        int cmp = 0;
        int mid = -1;
        while (low <= high)
        {
            mid = (low + high) / 2;
            // Compare 'mid' sample to goal
            final Timestamp time = samples.getSample(mid).getTime();
            final int compare = time.compareTo(start);
            if (compare > 0)
            {   // 'mid' too big, search lower half
                cmp = 1;
                high = mid - 1;
            }
            else if (compare < 0)
            {   // 'mid' too small, search upper half
                cmp = -1;
                low = mid + 1;
            }
            else
            {
                cmp = 0;
                return mid; // found exact time
            }
        }
        // Didn't find exact match.
        if (cmp < 0) // 'mid' sample is smaller than x, so it's OK
            return mid;
        // cmp > 0, 'mid' sample is greater than x.
        // If there is a sample before, use that
        if (mid > 0)
            return mid-1;
        return -1;
    }

    /** {@inheritDoc} */
    @Override
    public boolean hasNext()
    {
        return index >= 0;
    }

    /** {@inheritDoc} */
    @Override
    public VType next() throws Exception
    {
        if (index < 0)
            throw new Exception("End of samples"); //$NON-NLS-1$
        // Remember value, prepare the next value
        final VType result = value;
        synchronized (samples)
        {
            ++index;
            if (index >= samples.getSize())
                index = -1; // No more samples
            else
            {
                value = samples.getSample(index).getValue();
                if (VTypeHelper.getTimestamp(value).compareTo(end) > 0)
                    index = -1; // Beyond end time
            }
        }
        return result;
    }

    /** {@inheritDoc} */
    @Override
    public void close()
    {
        // NOP
    }
}
