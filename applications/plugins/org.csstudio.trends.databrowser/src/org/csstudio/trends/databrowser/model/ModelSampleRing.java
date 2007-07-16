package org.csstudio.trends.databrowser.model;

import org.csstudio.platform.data.IValue;
import org.csstudio.swt.chart.ChartSampleSequence;

/** Synchronized circular buffer implementation of a <code>ChartSampleSequence</code>.
 *  @see ChartSampleSequence
 *  
 *  @author Kay Kasemir
 */
public class ModelSampleRing 
{
    //  The Circular buffer implementation:
    //
    //  Initial: start = size = 0.
    //
    //  Indices of valid entries:
    //  [start], [start+1], ..., [start+size-1]
    //  with wrap-around at [capacity-1].
    //
    // Previous implementation used start/end indices,
    // and computed the size from that, but size() is called
    // quite often when data is plotted etc., so this optimizes
    // the get() and size() a little bit.
    private int capacity;
    private int start;
    private int size;
    private ModelSample samples[] = null;
   
    /** Construct SampleSequenceRing with given initial capacity) */
    public ModelSampleRing(int initial_capacity)
    {
        capacity = initial_capacity;
        samples = new ModelSample[capacity];
    }
    
    /** Clear sample memory */
    synchronized public void clear()
    {
        start = size = 0;
    }

    /** Remove memory associated with this object. */
    synchronized public void dispose()
    {
        clear();
        capacity = 0;
        samples = null;
    }
    
    /** Set new capacity.
     *  <p>
     *  Tries to preserve newest samples.
     */
    synchronized public void setCapacity(int new_capacity)
    {
        ModelSample new_samples[] = new ModelSample[new_capacity];
        // Copy old samples over
        if (samples != null)
        {   // How many can be copied?
            int copy_size = size;
            if (copy_size > new_capacity)
                copy_size = new_capacity;
            // First 'old' sample
            final int copy_start = size - copy_size;
            for (int i=0; i<copy_size; ++i)
                new_samples[i] = get(copy_start + i);
            size = copy_size;
        }
        else
            size = 0;
        samples = new_samples;
        start = 0;
        capacity = new_capacity;
    }

    /** @return Returns the current capacity.
     *  @see #size
     */
    synchronized public int getCapacity()
    {
        return capacity;
    }
    
    /** @return Returns the number of valid entries.
     *  @see org.csstudio.swt.chart.ChartSampleSequence#size()
     *  @see #getCapacity()
     */
    synchronized public int size()
    {
        return size;
    }

    // @see Series
    synchronized public ModelSample get(int i)
    {
        if (i<0 || i >= size())
            throw new ArrayIndexOutOfBoundsException(i);
        i = (start + i) % capacity;
        return samples[i];
    }

    /** Add a new sample. */
    synchronized public void add(IValue sample, String source)
    {
        // Obtain index of next element
        if (size >= capacity)
            ++start; // Overwrite oldest element
        else
            ++size; // Add to end of buffer
        final int i = (start + size - 1) % capacity;
        // Update that element
        samples[i] = new ModelSample(sample, source);
    }
}
