package org.csstudio.archive.crawl;

import org.csstudio.archive.ArchiveServer;
import org.csstudio.archive.ArchiveValues;
import org.csstudio.archive.crawl.ValueIterator;
import org.csstudio.platform.data.ITimestamp;
import org.csstudio.value.Value;

/** An archive sample crawler.
 *  <p>
 *  This crawler implements an iterator-type interface to the raw
 *  samples found in the archive. In case the requested data is
 *  too big to be returned by the data server in a single response,
 *  the crawler performs follow-up requests.
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
	private static final int BATCH_COUNT = 5000;

    // Information about the overall request
    private final ArchiveServer servers[];
    private final int keys[];
    private final String channel_name;
    private final ITimestamp end;
    private final String request_type;
    private final Object request_parms[];

    /** Index of the current servers/keys entry. */
    private int arch_idx;
    
    // The current batch of retrieved samples
    private Value samples[] = null;
    private int next_sample_index;

    /** Construct crawler for raw samples of a channel.
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
                             int keys[], String channel,
                             ITimestamp start, ITimestamp end,
                             String request_type,
                             Object request_parms[]) throws Exception
    {
        if (servers.length != keys.length)
            throw new Exception("servers/keys must have equal length."); //$NON-NLS-1$
        this.servers = servers;
        this.keys = keys;
        arch_idx = 0;
        this.channel_name = channel;
        this.end = end;
        this.request_type = request_type;
        this.request_parms = request_parms;

        while (arch_idx < servers.length)
        {        
            if (fetch(start))
            {
                // If this arch_idx archive has no data at 'start',
                // the result can be a single(!) last sample in the archive
                // before(!) that start time.
                //
                // Subsequent calls could be the same, so we'd get a bunch
                // of really old values before the actual start time.
                if (samples.length > 1 || samples[0].getSeverity().hasValue())
                    return; // OK, we found something useful.
            }
            // Nothing, or only a single useless 'off'/'disconnected' sample.
            ++ arch_idx;
        }
        // Found nothing
        samples = null;
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

    /** Constructor for single server and key.
     *  @see #RawSampleIterator(ArchiveServer[], int[], String, ITimestamp, ITimestamp) */
    public RawValueIterator(ArchiveServer server, int key, String channel,
                    ITimestamp start, ITimestamp end) throws Exception
    {
        this(new ArchiveServer[] { server }, new int[] { key },
             channel, start, end);
    }
    
    /** Fetch another batch of samples from arch_idx index.
     * 
     *  @param fetch_start Start time for this batch
     *         (greater or equal to overall start time)
     *  @return Returns <code>true</code> if there were any more samples.
     *  @throws Exception
     */
	@SuppressWarnings("nls")
    private boolean fetch(ITimestamp fetch_start) throws Exception
    {
        // Issue the request
        String names[] = new String[] { channel_name };
        ArchiveServer server = servers[arch_idx];
        ArchiveValues responses[] = server.getSamples(
                keys[arch_idx], names, fetch_start, end,
                server.getRequestType(request_type), request_parms);
        if (responses.length != 1)
            throw new Exception("Got " + responses.length
                            + " responses instead of 1");
        ArchiveValues response = responses[0];
        if (! channel_name.equals(response.getChannelName()))
            throw new Exception("Got channel '" + response.getChannelName()
                    + "' instead of '" + channel_name + "'");
        // Remember what's to remember from the response
        samples = response.getSamples();
        next_sample_index = 0;
        return samples.length > 0;
    }

    /**
     * @return Returns <code>true</code> if there is another sample available.
     * @see #next()
     */
    public boolean hasNext()
    {
        return samples != null  &&  next_sample_index < samples.length;
    }

    /** @return Returns the next sample.
     *  @see #hasNext()
     */
    public Value next()
    {
        // Obtain current sample, the one to return
        Value current_sample = samples[next_sample_index];
        // Prepare what to return next
        ++next_sample_index;
        if (next_sample_index >= samples.length)
        {   // Hit the last sample
            try
            {   // Get data from then on
                ITimestamp last_timestamp = current_sample.getTime();
                while (arch_idx < servers.length)
                {
                    // Query the current archive again from that time on:
                    // Maybe the previous request ended at the BATCH_COUNT,
                    // or new samples have been added to the archive since the
                    // last request.
                    if (fetch(last_timestamp))
                    {
                        // This fetch should return the current_sample again.
                        // Assume the following situation, where the last batch ended
                        // in a range of data that had the same time stamp:
                        //   some_timestamp value A
                        //   last_timestamp value B
                        //   last_timestamp value C <-- current_sample
                        //   last_timestamp value D (not yet retrieved)
                        //
                        // When we request new data from 'last_timestamp' on,
                        // we actually get value B, value C, ... again.
                        // So we have to skip forward, just past the 'current_sample'.
                        while (next_sample_index < samples.length)
                        {
                            Value skip_sample = samples[next_sample_index];
                            if (skip_sample.getTime().isGreaterThan(last_timestamp))
                                break; // Found new data, so we're done
                            // Sample is timed at-or-before last_timestamps, so skip
                            ++next_sample_index;
                            if (skip_sample.equals(current_sample))
                                break;
                                // Recognized the last sample, so that was the
                                // last sample to skip
                                // ** This is was should happen in 99% of the cases **
                                // The remaining problem with this code:
                                // If the batch size is N, and there are >=N identical samples
                                // (same time stamp, same value, ...) in the archive,
                                // we will indefinetely iterate over those same samples,
                                // because each batch will be identical.
                                // --> highly unlikely?
                        }
                        // Did we break with something left to continue?
                        if (next_sample_index < samples.length)
                            return current_sample;
                    }
                    // Fetch failed, or nothing in the fetched block: try next key
                    ++arch_idx;
                }
            }
            catch (Exception e)
            {   // Would like to pass exception up, but Iterator interface
                // does not allow next() to throw Exception.
                // Brute force: Elevate to Error.
                throw new Error("Cannot get next batch of samples, " //$NON-NLS-1$
                        + e.getMessage(), e);
            }
        }
        return current_sample;
    }

    /** Required by the <code>Iterator</code> interface, but not supported for
     *  the archived data.
     *  @see java.util.Iterator#remove()
     */
    public void remove()
    {
        throw new UnsupportedOperationException();
    }
}
