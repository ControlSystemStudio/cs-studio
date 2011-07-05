/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.reader;

import org.csstudio.data.values.ITimestamp;
import org.csstudio.data.values.IValue;

/** Iterates several <code>ValueIterator</code> instances, merging
 *  the samples from them in time.
 *  @author Kay Kasemir
 */
public class MergingValueIterator implements ValueIterator
{
    /** The iterators for the individual channels. */
    final private ValueIterator iters[];

    /** The 'current' values of each <code>iter</code>. */
    private IValue raw_data[];

    private IValue value;

    /** Constructor.
     *  @param iters The 'base' iterators.
     *  @throws Exception on error in archive access
     */
    public MergingValueIterator(ValueIterator iters[]) throws Exception
    {
        this.iters = iters;

        // Get first sample from each base iterator
        raw_data = new IValue[iters.length];
        for (int i=0; i<iters.length; ++i)
        {
            raw_data[i] = iters[i].hasNext() ? iters[i].next() :  null;
        }
        fetchNext();
    }

    /** Determine the next value, i.e. the oldest sample from the base iterators
     *  @throws Exception on error
     */
    private void fetchNext() throws Exception
    {
        // Find oldest time stamp
        ITimestamp time = null;
        int index = -1;
        for (int i=0; i<raw_data.length; ++i)
        {
            if (raw_data[i] == null)
                continue;
            final ITimestamp sample_time = raw_data[i].getTime();
            if (time == null  ||  sample_time.isLessThan(time))
            {
                time = sample_time;
                index = i;
            }
        }
        if (time == null)
        {   // No channel left with any data.
            raw_data = null;
            value = null;
            return;
        }
        value = raw_data[index];
        raw_data[index] = iters[index].hasNext() ? iters[index].next() :  null;
    }

    /** {@inheritDoc} */
    @Override
    public boolean hasNext()
    {
        return raw_data != null;
    }

    /** {@inheritDoc} */
    @Override
    public IValue next() throws Exception
    {
        if (! hasNext())
            throw new IllegalStateException();
        final IValue result = value;
        fetchNext();
        return result;
    }

    /** {@inheritDoc} */
    @Override
    public void close()
    {
        for (ValueIterator iter : iters)
            iter.close();
    }
}
