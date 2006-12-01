package org.csstudio.trends.databrowser.model;

import org.csstudio.archive.Sample;
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
    //  Initial: tail = head = 0.
    //
    //  Indices of valid entries:
    //  tail+1, tail+2 ... wrap around at samples.size() ... head
    //
    // That means that we can actually only hold samples.size()-1 elements.
    // The get/setCapacity calls compensate for that.
    private int real_capacity;
    private int head;
    private int tail;
    private ModelSample samples[];
   
    /** Construct SampleSequenceRing with given initial capacity) */
    public ModelSampleRing(int initial_capacity)
    {
        setCapacity(initial_capacity);
    }

    /** Remove memory associated with this object. */
    synchronized public void dispose()
    {
        real_capacity = 0;
        head = tail = 0;
        samples = null;
    }
    
    /** Set new capacity, which also clears the container. */
    synchronized public void setCapacity(int new_capacity)
    {
        real_capacity = new_capacity + 1;
        head = tail = 0;
        if (samples == null  ||  samples.length < real_capacity)
            samples = new ModelSample[real_capacity];
    }

    /** @return Returns the current capacity.
     *  @see #size
     */
    synchronized public int getCapacity()
    {
        return real_capacity-1;
    }
    
    /** Add a new sample. */
    synchronized public void add(Sample sample)
    {
        // Obtain index of next element
        if (++head >= real_capacity)
            head = 0;
        // here is the over write of the queue
        if (head == tail)
        {
            if (++tail >= real_capacity)
                tail = 0;
        }
        // Update that element
        samples[head] = new ModelSample(sample);
    }
    
    /** @return Returns the number of valid entries.
     *  @see org.csstudio.swt.chart.ChartSampleSequence#size()
     *  @see #getCapacity()
     */
    synchronized public int size()
    {
        int count;
        if (head >= tail)
            count = head - tail;
        else //     #(tail .. end) + #(start .. head)
            count = (real_capacity - tail - 1) + (head + 1);
        return count;
    }

    // @see Series
    synchronized public ModelSample get(int i)
    {
        if (i<0 || i >= size())
            throw new ArrayIndexOutOfBoundsException(i);
        i = (tail + 1 + i) % real_capacity;
        return samples[i];
    }
}
