package org.csstudio.archive.rdb;

import org.csstudio.archive.rdb.engineconfig.ChannelGroupConfig;
import org.csstudio.platform.data.ITimestamp;
import org.csstudio.platform.data.IValue;
import org.csstudio.platform.utility.rdb.StringID;

/** Channel: Name, access to time range etc.
 *  @see RDBArchive
 *  @author Kay Kasemir
 */
abstract public class ChannelConfig extends StringID
{
    protected int group_id;
    private SampleMode sample_mode;
    private double sample_period;
    
    protected ChannelConfig(final int id, final String name,
            final int group_id, final SampleMode sample_mode,
            final double sample_period)
    {
        super(id, name);
        this.group_id = group_id;
        this.sample_mode = sample_mode;
        this.sample_period = sample_period;
    }

    public int getGroupId()
    {
        return group_id;
    }

    /** @return SampleMode */
    public SampleMode getSampleMode()
    {
        return sample_mode;
    }

    /** @return Scan period or estimated monitor period in seconds */
    public double getSamplePeriod()
    {
        return sample_period;
    }

    /** Define the sample mode of this channel
     *  @param sample_mode SampleMode
     *  @param period_secs Sample period or estimated update rate in seconds
     */
    abstract public void setSampleMode(final SampleMode sample_mode,
            final double period_secs) throws Exception;
    
    /** Add a channel to this group.
     *  @param channel Channel to add
     *  @throws Exception on error
     */
    abstract public void addToGroup(final ChannelGroupConfig group) throws Exception;
    
    /** Get time range information.
     *  @return Last time stamp found for this channel,
     *          or <code>null</code>
     *  @throws Exception on error
     */
    abstract public ITimestamp getLastTimestamp() throws Exception;
        
    /** Read (raw) samples from start to end time.
     *  @param start Retrieval starts at-or-before this time
     *  @param end Read up to and including this time
     *  @return SampleIterator for the samples
     *  @throws Exception on error
     */
    abstract public SampleIterator getSamples(final ITimestamp start,
            final ITimestamp end) throws Exception;
    
    /** Add a sample to the archive.
     *  <p>
     *  For performance reasons, this call actually only adds
     *  the sample to a 'batch'.
     *  Need to follow up with <code>RDBArchive.commitBatch()</code> when done.
     *  @param sample
     *  @throws Exception on error
     *  @see RDBArchive#commitBatch()
     */
    abstract public void batchSample(final IValue sample) throws Exception;
}