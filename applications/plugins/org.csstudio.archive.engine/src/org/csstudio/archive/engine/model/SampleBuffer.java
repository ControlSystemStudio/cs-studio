package org.csstudio.archive.engine.model;

import java.util.concurrent.ArrayBlockingQueue;

import org.csstudio.platform.data.IValue;

/** Buffer for the samples of one channel.
 *  <p>
 *  Assumes that one thread adds samples, while a different
 *  thread removes them.
 *  When the queue size is reached, older samples get dropped.
 *  
 *  @author Kay Kasemir
 */
public class SampleBuffer
{
    /** Name of channel that writes to this buffer.
     *  (we keep only the name, not the full channel,
     *  to decouple stuff).
     */
    final private String channel_name;
    
    /** Max. number of samples in buffer. */
    final private int capacity;
    
    /** The actual samples in a thread-save queue. */
    final private ArrayBlockingQueue<IValue> samples;
    
    /** Statistics */
    final private BufferStats stats = new BufferStats();
    
    /** Is the buffer in an error state because of RDB write errors?
     *  Note that this is global for all buffers, not per instance!
     */
    private static boolean error = false;

    /** Create sample buffer of given capacity */
    SampleBuffer(final String channel_name, final int capacity)
    {
        this.channel_name = channel_name;
        this.capacity = capacity;
        samples = new ArrayBlockingQueue<IValue>(capacity);
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

    /** @return Current queue size. */
    public int getQueueSize()
    {
        return samples.size();
    }

    /** @return <code>true</code> if current;y experiencing write errors */
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
    void add(final IValue value)
    {
        final int size = samples.size();
        if (size >= capacity)
        {
            samples.poll();
            stats.addOverrun();
        }
        samples.add(value);
    }
    
    /** @return latest sample in queue or <code>null</code> if empty */
    IValue remove()
    {
        return samples.poll();
    }

    /** Update stats with current values */
    void updateStats()
    {
        stats.updateSizes(samples.size());
    }

    /** @return Buffer statistics. */
    public BufferStats getBufferStats()
    {
        return stats;
    }

    @SuppressWarnings("nls")
    @Override
    public String toString()
    {
        return String.format(
        "Sample buffer '%s': %d samples max, %.1f samples average, %d overruns",
            channel_name,
            stats.getMaxSize(),
            stats.getAverageSize(),
            stats.getOverruns());
    }
}
