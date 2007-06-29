package org.csstudio.archive.crawl;

import org.csstudio.archive.ArchiveServer;
import org.csstudio.archive.ArchiveValues;
import org.csstudio.platform.data.ITimestamp;
import org.csstudio.platform.data.IValue;

/** Crawler that fetches 'batches' of samples until reaching an end time.
 *  <p>
 *  This crawler fetches samples from a given server/key,
 *  then fetches the next batch of samples from the end time
 *  of the previous batch on, continuing until reaching
 *  the given end time.
 *  <p>
 *  @see org.csstudio.archive.crawl.RawValueIterator
 *  @author Kay Kasemir
 */
public class BatchIterator
{
    // Information about the overall request
    final public ArchiveServer server;
    final public int key;
    final public String channel_name;
    final public ITimestamp end;
    final public int request_type;
    final public Object request_parms[];
 
    /** Current batch of samples. */
    private IValue[] samples;

    /** Construct batch iterator for a channel.
     *  <p>
     * 
     *  @param server       The Archive Server to use.
     *  @param key          Archive key.
     *  @param channel_name Name of channel.
     *  @param start        Start time of retrieval.
     *  @param end          End time of retrieval.
     *  @param request_type How to request the data
     *  @param request_parm Meaning depends on request_type
     *  @throws Exception on error.
     */
	public BatchIterator(final ArchiveServer server,
                         final int key,
                         final String channel_name,
                         final ITimestamp start,
                         final ITimestamp end,
                         final String request_type,
                         final Object request_parms[]) throws Exception
    {
        this.server = server;
        this.key = key;
        this.channel_name = channel_name;
        this.end = end;
        this.request_type = server.getRequestType(request_type);
        this.request_parms = request_parms;
        fetch(start);
    }

    /** Fetch another batch of samples
     * 
     *  @param fetch_start Start time for this batch
     *         (greater or equal to overall start time)
     *  @throws Exception on error
     */
	@SuppressWarnings("nls")
    private void fetch(ITimestamp fetch_start) throws Exception
    {
        // Clear any previous samples
        samples = null;
        // Issue the request
        final String names[] = new String[] { channel_name };
        final ArchiveValues responses[] = server.getSamples(
                key, names, fetch_start, end,
                request_type, request_parms);
        if (responses.length != 1)
            throw new Exception("Got " + responses.length
                                + " responses instead of 1");
        final ArchiveValues response = responses[0];
        if (! channel_name.equals(response.getChannelName()))
            throw new Exception("Got channel '" + response.getChannelName()
                                + "' instead of '" + channel_name + "'");
        // Remember the received samples
        samples = response.getSamples();
    }

    /** @return Returns the current batch of samples or <code>null</code>
     *          if there are none.
     * @see #next()
     */
    public IValue [] getBatch()
    {
        return samples;
    }

    /** @return Returns the next batch of samples or <code>null</code>
     *          if there are none.
     *  @see #getBatch()
     */
    public IValue[] next() throws Exception
    {
        if (samples == null)
            return null;
        if (samples.length < 1)
            return null;
        final IValue last_sample = samples[samples.length-1];
        fetch(last_sample.getTime());
        if (samples == null)
            return null;
        
        // This fetch should return the last_sample again.
        // Assume the following situation, where the last batch ended
        // in a range of data that had the same time stamp:
        //   some_timestamp value A
        //   last_timestamp value B
        //   last_timestamp value C <-- last_sample of previous batch
        //   last_timestamp value D (not yet retrieved)
        //
        // When we request new data from 'last_sample' on,
        // we actually get value B, value C, ... again
        // because of their time stamps.
        // So we have to skip forward, just past the 'last_sample'.
        int skip = 0;
        while (skip < samples.length)
        {
            final IValue skip_sample = samples[skip];
            // Did we reach new data?
            if (skip_sample.getTime().isGreaterThan(last_sample.getTime()))
                break; // Found new data, so we're done
            // Sample is timed at-or-before last_sample, so skip
            ++skip;
            // Is that the last sample, so we're done?
            // ** This is was should happen in 99% of the cases **
            if (skip_sample.equals(last_sample))
                break;
            // The remaining problem with this code:
            // If the batch size is N, and there are >=N identical samples
            // (same time stamp, same value, ...) in the archive,
            // we will indefinetely iterate over those same samples,
            // because each batch will be identical.
            // --> highly unlikely?
        }
        
        // Nothing to skip? Return as is.
        if (skip <= 0)
            return samples;
        // Nothing left? Clear samples.
        if (skip >= samples.length)
        {
            samples = null;
            return null;
        }
        // Reduce samples to those that are actually new
        IValue new_samples[] = new IValue[samples.length - skip];
        for (int i=0;  i<new_samples.length;  ++i)
            new_samples[i] = samples[skip + i];
        samples = new_samples;
        return samples;
    }
}
