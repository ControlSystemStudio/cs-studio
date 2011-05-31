/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.rdb;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

import org.csstudio.archive.rdb.engineconfig.ChannelGroupConfig;
import org.csstudio.data.values.IMetaData;
import org.csstudio.data.values.ITimestamp;
import org.csstudio.data.values.IValue;
import org.csstudio.data.values.TimestampFactory;
import org.csstudio.platform.utility.rdb.StringID;

/** Channel: Name, access to time range etc.
 *  @see RDBArchive
 *  @author Kay Kasemir
 */
public class ChannelConfig extends StringID
{
    /** Archive that holds this channel */
    final private RDBArchive archive;

    protected int group_id;

    private SampleMode sample_mode;

    private double sample_value;

    private double sample_period;

    /** The channel's meta data */
    private IMetaData meta = null;

    /** Constructor from pieces */
    public ChannelConfig(final RDBArchive archive, final int id,
            final String name,
            final int group_id,final SampleMode sample_mode,
            final double sample_value,
            final double sample_period)
    {
        super(id, name.trim());
        this.archive = archive;
        this.group_id = group_id;
        this.sample_mode = sample_mode;
        this.sample_value = sample_value;
        this.sample_period = Math.max(sample_period, RDBArchivePreferences.getMinSamplePeriod());
    }

    /** @return Numeric group ID */
    public int getGroupId()
    {
        return group_id;
    }

    /** @return SampleMode */
    public SampleMode getSampleMode()
    {
        return sample_mode;
    }

    /** @return Sample mode configuration value, e.g. 'delta' for Monitor */
    public double getSampleValue()
    {
        return sample_value;
    }

    /** @return Scan period or estimated monitor period in seconds */
    public double getSamplePeriod()
    {
        return sample_period;
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

    /** Define the sample mode of this channel
     *  @param sample_mode SampleMode
     *  @param sample_value Sample mode configuration value
     *  @param period_secs Sample period or estimated update rate in seconds
     */
    public void setSampleMode(final SampleMode sample_mode,
            final double sample_value,
            final double period_secs) throws Exception
    {
       this.sample_mode = sample_mode;
       this.sample_value = sample_value;
       this.sample_period = Math.max(period_secs, RDBArchivePreferences.getMinSamplePeriod());
       final PreparedStatement statement =
            archive.getRDB().getConnection().prepareStatement(
                        archive.getSQL().channel_set_sampling);
        try
        {   // UPDATE channel SET smpl_mode_id=?,smpl_val=?,smpl_per=? WHERE channel_id=?
            statement.setInt(1, sample_mode.getId());
            statement.setDouble(2, sample_value);
            statement.setDouble(3, sample_period);
            statement.setInt(4, getId());
            statement.executeUpdate();
        }
        finally
        {
            statement.close();
        }
    }

    /** Add a channel to this group.
     *  @param channel Channel to add
     *  @throws Exception on error
     */
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

    /** Get time range information.
     *  @return Last time stamp found for this channel,
     *          or <code>null</code>
     *  @throws Exception on error
     */
    public ITimestamp getLastTimestamp() throws Exception
    {
        final PreparedStatement statement =
            archive.getRDB().getConnection().prepareStatement(
                        archive.getSQL().channel_sel_last_time_by_id);
        try
        {
            statement.setQueryTimeout(RDBArchivePreferences.getSQLTimeout());
            statement.setInt(1, getId());
            ResultSet rs = statement.executeQuery();
            if (!rs.next())
                return null;
            final Timestamp end = rs.getTimestamp(1);
            // Channel without any samples?
            if (end == null)
                return null;
            return TimestampFactory.fromSQLTimestamp(end);
        }
        finally
        {
            statement.close();
        }
    }

    /** Read (raw) samples from start to end time.
     *  @param start Retrieval starts at-or-before this time
     *  @param end Read up to and including this time
     *  @return SampleIterator for the samples
     *  @throws Exception on error
     */
    public SampleIterator getSamples(final ITimestamp start,
            final ITimestamp end) throws Exception
    {
        return new RawSampleIterator(archive, this, start, end);
    }

    /** Add a sample to the archive.
     *  <p>
     *  For performance reasons, this call actually only adds
     *  the sample to a 'batch'.
     *  Need to follow up with <code>RDBArchive.commitBatch()</code> when done.
     *  @param sample
     *  @throws Exception on error
     *  @see RDBArchive#commitBatch()
     */
    public void batchSample(final IValue sample) throws Exception
    {
        // Need to write meta data?
        if (getMetaData() == null) {
            archive.writeMetaData(this, sample);
        }
        archive.batchSample(this.getId(), sample);

        archive.debugBatch(this, sample);
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
