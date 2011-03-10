package org.csstudio.archive.crawl;

import org.csstudio.archive.ArchiveServer;
import org.csstudio.data.values.ITimestamp;
import org.csstudio.data.values.IValue;

/** An archive sample crawler.
 *  <p>
 *  This crawler implements an iterator-type interface to the raw
 *  samples found in the archive. In case the requested data is
 *  too big to be returned by the data server in a single response,
 *  the crawler performs follow-up requests.
 *  <p>
 *  This crawler also merges multiple sources for the same channel
 *  by time.
 *  <p>
 *  @see org.csstudio.archive.crawl.DoubleValueIterator
 *  @author Kay Kasemir
 */
public class RawValueIterator implements ValueIterator
{
    /** Size of the batched requests
     *  Server currently emposes a limit of 10000.
     *  Smaller count results in more but smaller requests.
     *  Probably a good thing:
     *  Saves memory when samples are used iteratively and not kept;
     *  allows receipient to digest a few samples sooner,
     *  before we need to wait again for new samples.
     */
    final private static int BATCH_COUNT = 5000;

    /** Marker for 'no more samples' */
    private static final int NO_MORE = -1;

    /** End of the requested time range. */
    final public ITimestamp end;

    /** Batch iterators for the archive sources. */
    final private BatchIterator batch[];

    /** Current indices within each batch, or -1 if batch is excausted. */
    final private int sample_idx[];

    /** Index of the batch with the current value, or -1 if nothing. */
    private int batch_idx;

    /** Constructor for single server and key.
     *  @see #RawSampleIterator(ArchiveServer[], int[], String, ITimestamp, ITimestamp) */
    public RawValueIterator(ArchiveServer server, int key, String channel,
                    ITimestamp start, ITimestamp end) throws Exception
    {
        this(new ArchiveServer[] { server }, new int[] { key },
             channel, start, end);
    }

    /** Constructor for 'raw' data.
     *  @see #RawSampleIterator(ArchiveServer[], int[], String,
     *                          ITimestamp, ITimestamp, String, int) */
    public RawValueIterator(ArchiveServer servers[],
                    int keys[], String channel,
                    ITimestamp start, ITimestamp end) throws Exception
    {
        this(servers, keys, channel, start, end,
            ArchiveServer.GET_RAW, new Object[] { new Integer(BATCH_COUNT) });
    }

    /** Construct crawler for a channel.
     *  <p>
     *  The crawler will iterate over all samples from start to end
     *  in { servers[0], keys[0] }, then continue with the next servers/keys
     *  entry and so on, until either reaching the end time or the end of the
     *  servers/keys lists.
     *  <p>
     *  Consequently, the <code>servers</code> and <code>keys</code> arrays
     *  must be of the same length!
     *  <p>
     *  Since we won't go "back in time", any samples in { servers[1], keys[1] }
     *  time-stamped before the last sample in { servers[0], keys[0] }
     *  will be ignored.
     *  Meaning: The archive data sources should be listed in time order,
     *  i.e. the oldest sub-archive first, followed by the next one,
     *  ending in the most recent one.
     *
     *  @param servers The Archive Servers to use.
     *  @param keys Archive keys. They have to be in time-order,
     *          i.e. oldest sub-archive first.
     *  @param channels Name of channel.
     *  @param start Start time of retrieval.
     *  @param end End time of retrieval.
     *  @param request_type How to request the data
     *  @param request_parm Meaning depends on request_type
     *  @throws Exception on error.
     */
	public RawValueIterator(ArchiveServer servers[],
                            int keys[], String channel_name,
                            ITimestamp start, ITimestamp end,
                            String request_type,
                            Object request_parms[]) throws Exception
    {
        final int N = servers.length;
        if (N != keys.length)
            throw new IllegalArgumentException("servers/keys must have equal length."); //$NON-NLS-1$
        this.end = end;
        batch = new BatchIterator[N];
        sample_idx = new int[N];
        for (int i=0; i<N; ++i)
        {
            batch[i] = new BatchIterator(servers[i], keys[i],
                                         channel_name, start, end,
                                         request_type, request_parms);
            sample_idx[i] = 0;
        }
        determineNextSample();
    }

    private void determineNextSample() throws Exception
    {
        // Determine batch with the next sample, i.e. the oldest time stamp
        final int N = batch.length;
        ITimestamp time = null;
        for (int i=0; i<N; ++i)
        {
            // Batch exhausted?
            if (sample_idx[i] < 0)
                continue;
            IValue[] curr_batch = batch[i].getBatch();
            // Reached end of batch?
            if (curr_batch == null)
            {
                sample_idx[i] = NO_MORE;
                continue;
            }
            // Reached end of batch's current values?
            if (sample_idx[i] >= curr_batch.length)
            {   // Get another batch of samples
                curr_batch = batch[i].next();
                if (curr_batch == null  ||  curr_batch.length < 1)
                {
                    sample_idx[i] = NO_MORE;
                    continue;
                }
                sample_idx[i] = 0;
            }
            // Check time stamp of bunch's current sample
            final ITimestamp sample_time = curr_batch[sample_idx[i]].getTime();
            if (time == null || sample_time.isLessThan(time))
            {
                time = sample_time;
                batch_idx = i;
            }
        }
        // Found anything?
        if (time == null)
            batch_idx = NO_MORE;
    }

    /**
     * @return Returns <code>true</code> if there is another sample available.
     * @see #next()
     */
    public boolean hasNext()
    {
        return batch_idx >= 0;
    }

    /** @return Returns the next sample.
     *  @throws Exception on error
     *  @see #hasNext()
     */
    public IValue next() throws Exception
    {
        if (batch_idx < 0)
            return null;
        final IValue[] curr_batch = batch[batch_idx].getBatch();
        // Obtain current sample, the one to return
        final IValue sample = curr_batch[sample_idx[batch_idx]];
        // Prepare what to return next
        ++sample_idx[batch_idx];
        determineNextSample();
        return sample;
    }
}
