/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.rdb.engineconfig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.csstudio.archive.rdb.ChannelConfig;
import org.csstudio.archive.rdb.RDBArchive;
import org.csstudio.archive.rdb.SampleMode;
import org.csstudio.platform.utility.rdb.StringID;

/** RDB info for a group of sampled channels.
 *  @author Kay Kasemir
 */
public class ChannelGroupConfig extends StringID
{
    final private RDBArchive archive;
    final private int engine_id;
    private int enabling_channel_id;

    /** Constructor, only used within package. */
    ChannelGroupConfig(final RDBArchive archive,
            final int id, final String name, final int engine_id,
            final int enabling_channel_id)
    {
        super(id, name.trim());
        this.archive = archive;
        this.engine_id = engine_id;
        this.enabling_channel_id = enabling_channel_id;
    }

    public int getEngineId()
    {
        return engine_id;
    }

    /** @return ID of enabling channel or 0 */
    public int getEnablingChannelId()
    {
        return enabling_channel_id;
    }

    /** @return Configuration of channels in this group */
    public List<ChannelConfig> getChannels() throws Exception
    {
        final ArrayList<ChannelConfig> channels = new ArrayList<ChannelConfig>();
        final Connection connection = archive.getRDB().getConnection();
        final PreparedStatement statement = connection.prepareStatement(
                archive.getSQL().channel_sel_by_group_id);
        try
        {
            statement.setInt(1, getId());
            final ResultSet result = statement.executeQuery();
            while (result.next())
            {   // channel_id, name, smpl_mode_id, smpl_val, smpl_per
                final SampleMode sample_mode =
                    archive.getSampleMode(result.getInt(3));
                channels.add(new ChannelConfig(archive,
                        result.getInt(1),
                        result.getString(2),
                        getId(),
                        sample_mode,
                        result.getDouble(4),
                        result.getDouble(5)));
            }
        }
        finally
        {
            statement.close();
        }

        // Sort by channel name in Java.
        // SQL should already give sorted result, but handling of upper/lowercase
        // names seems to differ between Oracle and MySQL, resulting in
        // files that were hard to compare
        Collections.sort(channels, new Comparator<ChannelConfig>()
        {
            @Override
            public int compare(final ChannelConfig a, final ChannelConfig b)
            {
                return a.getName().compareTo(b.getName());
            }
        });
        return channels;
    }

    /** Define the 'enabling' channel.
     *  @param channel Channel that enables the group or <code>null</code>
     *  @throws Exception on error
     */
    public void setEnablingChannel(final ChannelConfig channel)  throws Exception
    {
        final Connection connection = archive.getRDB().getConnection();
        final PreparedStatement statement = connection.prepareStatement(
                archive.getSQL().chan_grp_set_enable_channel);
        try
        {   // UPDATE chan_grp SET enabling_chan_id=? WHERE grp_id=?
            statement.setInt(1, channel.getId());
            statement.setInt(2, getId());
            statement.executeUpdate();
            connection.commit();
        }
        finally
        {
            statement.close();
        }
        enabling_channel_id = channel.getId();
    }

    @SuppressWarnings("nls")
    @Override
    public String toString()
    {
        return String.format(
                "Group '%s' (%d): Engine %d, enabled by %d",
                getName(), getId(), engine_id, enabling_channel_id);
    }
}
