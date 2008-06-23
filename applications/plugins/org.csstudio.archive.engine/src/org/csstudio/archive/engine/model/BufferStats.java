package org.csstudio.archive.engine.model;

import org.csstudio.util.stats.Average;

/** Buffer statistics
 *  @author Kay Kasemir
 */
public class BufferStats
{
    private int max_size = 0;
    
    private Average average_size = new Average();

    private int overruns = 0;
    
    /** @return Maximum queue size so far
     *  @see #reset()
     */
    synchronized public final int getMaxSize()
    {
        return max_size;
    }

    /** @return (Exponential) moving average of queue size. */
    synchronized public final double getAverageSize()
    {
        return average_size.get();
    }

    /** @return Number of buffer overruns. */
    synchronized public final int getOverruns()
    {
        return overruns;
    }

    /** Reset the statistics. */
    synchronized public void reset()
    {
        max_size = 0;
        average_size.reset();
    }
    
    /** Update the buffer stats.
     *  @param size Current buffer size.
     */
    synchronized public void updateSizes(int size)
    {
        if (size > max_size)
            max_size = size;
        average_size.update(size);
    }
    
    /** Add an overrun. */
    synchronized public void addOverrun()
    {
        ++overruns;
    }
}
