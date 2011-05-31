package org.csstudio.archive.crawl;

import org.csstudio.archive.ArchiveServer;
import org.csstudio.archive.ArchiveValues;
import org.csstudio.data.values.ITimestamp;
import org.csstudio.data.values.IValue;

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
    final public String request_type;
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
        this.request_type = request_type;
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
        if (samples == null)
            return;
        // Empty samples?
        if (samples.length < 1)
            samples = null;
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

        // In most cases, this fetch should return the last_sample again:
        //   some_timestamp value A
        //   last_timestamp value B <-- last_sample of previous batch
        //   new_timestamp  value C
        // Since we ask from at-or-before last_timestamp on,
        // we get the last_sample once more and need to skip it.
        //
        // But also consider the following situation, where the last batch ended
        // in a range of data that had the same time stamp, and even some same
        // values:
        //   some_timestamp value A
        //   last_timestamp value B
        //   last_timestamp value C
        //   last_timestamp value C
        //   last_timestamp value C <-- last_sample of previous batch
        //   last_timestamp value C
        //   last_timestamp value C
        //   last_timestamp value D
        //   last_timestamp value E
        //   new_timestamp  value F
        // Reasons for same timestamps: Stuck IOC clock,
        // or sequences like .. Repeat N, next value, Disconnected, Arch. Off.
        // Reason for the same value: General mess-up.
        //
        // When we request new data from 'last_sample.getTime()' on,
        // i.e. from last_timestamp on, we could get any of the values B to E,
        // since they're all stamped at-or-before last_timestamp.
        // Which one exactly depends on optimization inside the data server.

        // From the end of the new samples, go backward:
        int skip = samples.length;
        for (int i=skip-1;  i>=0;  --i)
        {   // If we find the previous batch's last sample...
            if (samples[i].equals(last_sample))
            {   // Skip all the samples up to and including it
                skip = i + 1;
                break;
            }
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
        System.arraycopy(samples, skip, new_samples, 0, new_samples.length);
        //for (int i=0;  i<new_samples.length;  ++i)
        //    new_samples[i] = samples[skip + i];
        samples = new_samples;
        return samples;
    }
}
