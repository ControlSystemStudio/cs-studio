package org.csstudio.archive.rdb.engineconfig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

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
    public ChannelConfig[] getChannels() throws Exception
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
            {   // channel_id, name, smpl_mode_id, smpl_per
                final SampleMode sample_mode =
                    archive.getSampleMode(result.getInt(3));
                channels.add(new ChannelConfig(archive,
                        result.getInt(1),
                        result.getString(2),
                        getId(),
                        sample_mode,
                        result.getDouble(4)));
            }
        }
        finally
        {
            statement.close();
        }
        // Convert to array
        final ChannelConfig chan_arr[] = new ChannelConfig[channels.size()];
        channels.toArray(chan_arr);
        // Sort by channel name in Java.
        // SQL should already give sorted result, but handling of upper/lowercase
        // names seems to differ between Oracle and MySQL, resulting in
        // files that were hard to compare
        Arrays.sort(chan_arr, new Comparator<ChannelConfig>()
        {
            public int compare(ChannelConfig a, ChannelConfig b)
            {
                return a.getName().compareTo(b.getName());
            }
        });
        return chan_arr;
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
