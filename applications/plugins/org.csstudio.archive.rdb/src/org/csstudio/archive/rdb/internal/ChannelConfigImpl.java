package org.csstudio.archive.rdb.internal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

import org.csstudio.archive.rdb.ChannelConfig;
import org.csstudio.archive.rdb.SampleIterator;
import org.csstudio.archive.rdb.SampleMode;
import org.csstudio.archive.rdb.engineconfig.ChannelGroupConfig;
import org.csstudio.platform.data.IMetaData;
import org.csstudio.platform.data.ITimestamp;
import org.csstudio.platform.data.IValue;
import org.csstudio.platform.utility.rdb.TimeWarp;

/** Channel: ID and possibly meta data.
 *  @author Kay Kasemir
 */
public class ChannelConfigImpl extends ChannelConfig
{
    private RDBArchiveImpl archive;

    /** The channel's meta data */
    private IMetaData meta;

    /** Constructor from pieces */
	public ChannelConfigImpl(final RDBArchiveImpl archive, final int id,
	        final String name,
            final int group_id,final SampleMode sample_mode,
            final double sample_period)
	{
		super(id, name, group_id, sample_mode, sample_period);
		this.archive = archive;
	}
	
	/** @return Meta data or <code>null</code> */
    public IMetaData getMetaData()
    {
        return meta;
    }

    /** @param meta The meta to set */
    public void setMetaData(final IMetaData meta)
    {
        this.meta = meta;
    }
    
    /** {@inheritDoc} */
    @Override
    public ITimestamp getLastTimestamp() throws Exception
    {
        final PreparedStatement statement =
            archive.getRDB().getConnection().prepareStatement(
                        archive.getSQL().channel_sel_last_time_by_id);
        try
        {
            statement.setInt(1, getId());
            ResultSet rs = statement.executeQuery();
            if (!rs.next())
                return null;
            final Timestamp end = rs.getTimestamp(1);
            // Channel without any samples?
            if (end == null)
                return null;
            return TimeWarp.getCSSTimestamp(end);
        }
        finally
        {
            statement.close();
        }
    }
    
    /** {@inheritDoc} */
    @Override
    public SampleIterator getSamples(final ITimestamp start,
            final ITimestamp end) throws Exception
    {
        return new RawSampleIterator(archive, this, start, end);
    }
    
    /** {@inheritDoc} */
    @Override
    public void batchSample(final IValue sample) throws Exception
    {
        archive.batchSample(this, sample);
    }
    
    /** {@inheritDoc} */
    @Override
    public void setSampleMode(final SampleMode sample_mode,
            double period_secs) throws Exception
    {
        if (period_secs < MIN_SAMPLE_PERIOD)
            period_secs = MIN_SAMPLE_PERIOD;
        final PreparedStatement statement =
            archive.getRDB().getConnection().prepareStatement(
                        archive.getSQL().channel_set_sampling);
        try
        {   // UPDATE channel SET smpl_mode_id=?,smpl_per=? WHERE channel_id=?
            statement.setInt(1, sample_mode.getId());
            statement.setDouble(2, period_secs);
            statement.setInt(3, getId());
            statement.executeUpdate();
        }
        finally
        {
            statement.close();
        }
    }

    /** {@inheritDoc} */
    @Override
    public void addToGroup(final ChannelGroupConfig group) throws Exception
    {
        final Connection connection = archive.getRDB().getConnection();
        final PreparedStatement statement = connection.prepareStatement(
                archive.getSQL().channel_set_grp_by_id);
        try
        {
            if (group.getId() > 0)
                statement.setInt(1, group.getId());
            else
                statement.setNull(1, java.sql.Types.INTEGER);
            statement.setInt(2, getId());
            statement.executeUpdate();
            connection.commit();
            this.group_id = group.getId();
        }
        finally
        {
            statement.close();
        }
    }
    
    @Override
    @SuppressWarnings("nls")
    final public String toString()
    {
        return String.format("Channel '%s' (%d): group %d, %s @ %.2f secs",
                getName(), getId(), getGroupId(),
                getSampleMode(), getSamplePeriod());
    }
}
