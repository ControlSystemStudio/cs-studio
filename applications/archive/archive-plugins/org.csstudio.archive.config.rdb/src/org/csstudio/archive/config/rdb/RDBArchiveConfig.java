/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.config.rdb;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.csstudio.archive.config.ArchiveConfig;
import org.csstudio.archive.config.ChannelConfig;
import org.csstudio.archive.config.EngineConfig;
import org.csstudio.archive.config.GroupConfig;
import org.csstudio.archive.config.ImportableArchiveConfig;
import org.csstudio.archive.config.SampleMode;
import org.csstudio.archive.rdb.RDBArchivePreferences;
import org.csstudio.archive.vtype.TimestampHelper;
import org.csstudio.platform.utility.rdb.RDBUtil;
import org.csstudio.platform.utility.rdb.RDBUtil.Dialect;

/** RDB implementation (Oracle, MySQL, PostgreSQL) of {@link ArchiveConfig}
 *
 *  <p>Provides read access via {@link ArchiveConfig} API,
 *  and also allows write access via additional RDB-only methods.
 *
 *  @author Kay Kasemir
 *  @author Lana Abadie - Disable autocommit as needed.
 *  @author Takashi Nakamoto - Added an option to skip reading last sampled time.
 */
@SuppressWarnings("nls")
public class RDBArchiveConfig implements ImportableArchiveConfig
{
    /** RDB connection */
    private RDBUtil rdb;

    /** SQL statements */
    private SQL sql;

    /** Numeric ID of 'monitor' mode stored in RDB */
    private int monitor_mode_id = -1;

    /** Numeric ID of 'scan' mode stored in RDB */
    private int scan_mode_id = -1;

    /** Re-used statement for selecting time of last archived sample of a channel */
    private PreparedStatement last_sample_time_statement;

    /** Initialize.
     *  This constructor will be invoked when an {@link ArchiveConfig}
     *  is created via the extension point.
     *  @throws Exception on error, for example RDB connection error
     */
    public RDBArchiveConfig() throws Exception
    {
        this(RDBArchivePreferences.getURL(), RDBArchivePreferences.getUser(),
                RDBArchivePreferences.getPassword(), RDBArchivePreferences.getSchema());
    }

    /** Initialize.
     *  This constructor can be invoked by test code.
     *  @param url RDB URL
     *  @param user .. user name
     *  @param password .. password
     *  @param schema Schema/table prefix, ending in ".". May be empty
     *  @throws Exception on error, for example RDB connection error
     */
    public RDBArchiveConfig(final String url, final String user, final String password,
            final String schema) throws Exception
    {
        rdb = RDBUtil.connect(url, user, password, false);
        sql = new SQL(rdb.getDialect(), schema);
        loadSampleModes();
    }

    /** {@inheritDoc} */
    @Override
    public EngineConfig[] getEngines() throws Exception
    {
        final List<EngineConfig> engines = new ArrayList<EngineConfig>();
        try
        (
            final Statement statement = rdb.getConnection().createStatement();
            final ResultSet result = statement.executeQuery(sql.smpl_eng_list);
        )
        {
            while (result.next())
                engines.add(new RDBEngineConfig(result.getInt(1),
                        result.getString(2), result.getString(3), result.getString(4)));
        }
        return engines.toArray(new EngineConfig[engines.size()]);
    }

    /** Load RDB information about sample modes */
    private void loadSampleModes() throws Exception
    {
        try
        (
            final Statement statement = rdb.getConnection().createStatement();
            final ResultSet result = statement.executeQuery(sql.sample_mode_sel);
        )
        {
            while (result.next())
            {
                final String name = result.getString(2);
                if (RDBSampleMode.determineMonitor(name))
                    monitor_mode_id = result.getInt(1);
                else
                    scan_mode_id = result.getInt(1);
            }
        }
        if (monitor_mode_id < 0  ||  scan_mode_id < 0)
            throw new Exception("Undefined sample modes");
    }

    /** Determine sample mode
     *  @param sample_mode_id Sample mode ID from RDB
     *  @param sample_value Sample value, i.e. monitor threshold
     *  @param period Scan period, estimated monitor period
     *  @return {@link SampleMode}
     *  @throws Exception
     */
    private RDBSampleMode getSampleMode(final int sample_mode_id, final double sample_value, final double period) throws Exception
    {
        return new RDBSampleMode(sample_mode_id, sample_mode_id == monitor_mode_id, sample_value, period);
    }

    /** Determine sample mode
     *  @param sample_mode_id Sample mode ID from RDB
     *  @param sample_value Sample value, i.e. monitor threshold
     *  @param period Scan period, estimated monitor period
     *  @return {@link SampleMode}
     *  @throws Exception
     */
    @Override
    public RDBSampleMode getSampleMode(final boolean monitor, final double sample_value, final double period) throws Exception
    {
        return new RDBSampleMode(monitor ? monitor_mode_id : scan_mode_id, monitor, sample_value, period);
    }

    /** @return Next available engine ID */
    private int getNextEngineId() throws Exception
    {
        try
        (
            final Statement statement = rdb.getConnection().createStatement();
            final ResultSet result = statement.executeQuery(sql.smpl_eng_next_id);
        )
        {
            if (result.next())
                return result.getInt(1) + 1;
            return 1;
        }
    }

    /** Create new engine config in RDB
     *  @param engine_name
     *  @param description
     *  @param engine_url
     *  @return
     *  @throws Exception
     */
    @Override
    public EngineConfig createEngine(final String engine_name, final String description,
            final String engine_url) throws Exception
    {
        final int id = getNextEngineId();
        rdb.getConnection().setAutoCommit(false);
        try
        (
            final PreparedStatement statement =
                rdb.getConnection().prepareStatement(sql.smpl_eng_insert);
        )
        {
            statement.setInt(1, id);
            statement.setString(2, engine_name);
            statement.setString(3, description);
            statement.setString(4, engine_url);
            statement.executeUpdate();
            rdb.getConnection().commit();
        }
        catch (Exception ex)
        {
            rdb.getConnection().rollback();
            throw ex;
        }
        finally
        {
            rdb.getConnection().setAutoCommit(true);
        }
        return new RDBEngineConfig(id, engine_name, description, engine_url);
    }

    /** {@inheritDoc} */
    @Override
    public EngineConfig findEngine(final String name) throws Exception
    {
        try
        (
            final PreparedStatement statement =
                rdb.getConnection().prepareStatement(sql.smpl_eng_sel_by_name);
        )
        {
            statement.setString(1, name);
            final ResultSet res = statement.executeQuery();
            if (res.next())
                return new RDBEngineConfig(res.getInt(1), name,
                        res.getString(2), res.getString(3));
        }
        return null;
    }

    /** Get engine for group
     *  @param group {@link RDBGroupConfig}
     *  @return {@link EngineConfig} for that group or <code>null</code>
     *  @throws Exception on error
     */
    @Override
    public EngineConfig getEngine(final GroupConfig the_group) throws Exception
    {
        RDBGroupConfig group = (RDBGroupConfig) the_group;
        try
        (
            final PreparedStatement statement = rdb.getConnection().prepareStatement(sql.smpl_eng_sel_by_group_id);
        )
        {
            statement.setInt(1, group.getId());
            final ResultSet result = statement.executeQuery();
            final EngineConfig engine;
            if (result.next())
                engine = new EngineConfig(result.getString(1), result.getString(2), result.getString(3));
            else
                engine = null;
            result.close();
            return engine;
        }
    }

    /** Delete engine info, all the groups under it, and clear all links
     *  from channels to those groups.
     *  @param engine Engine info to remove
     *  @throws Exception on error
     */
    @Override
    public void deleteEngine(final EngineConfig engine) throws Exception
    {
        // Unlink all channels from engine's groups
        final int engine_id = ((RDBEngineConfig)engine).getId();
        final Connection connection = rdb.getConnection();
        connection.setAutoCommit(false);
        try
        {
            try
            (
                final PreparedStatement statement = connection.prepareStatement(
                            sql.channel_clear_grp_for_engine);
            )
            {
                statement.setInt(1, engine_id);
                statement.executeUpdate();
            }
            // Delete all groups under engine...
            try
            (
                final PreparedStatement statement = connection.prepareStatement(
                        sql.chan_grp_delete_by_engine_id);
            )
            {
                statement.setInt(1, engine_id);
                statement.executeUpdate();
            }
            // Delete Engine entry
            try
            (
                final PreparedStatement statement = connection.prepareStatement(
                        sql.smpl_eng_delete);
            )
            {
                statement.setInt(1, engine_id);
                statement.executeUpdate();
            }
            connection.commit();
        }
        catch (Exception ex)
        {
            connection.rollback();
            throw ex;
        }
        finally
        {
            connection.setAutoCommit(true);
        }
    }

    /** @return Next available group ID
     *  @throws Exception on error
     */
    private int getNextGroupId() throws Exception
    {
        try
        (
            final Statement statement = rdb.getConnection().createStatement();
            final ResultSet result = statement.executeQuery(sql.chan_grp_next_id);
        )
        {
            if (result.next())
                return result.getInt(1) + 1;
            return 1;
        }
    }

    /** @param engine Engine to which to add group
     *  @param name Name of new group
     *  @return {@link RDBGroupConfig}
     *  @throws Exception on error
     */
    @Override
    public RDBGroupConfig addGroup(final EngineConfig engine, final String name) throws Exception
    {
        final Connection connection = rdb.getConnection();
        final int group_id = getNextGroupId();
        connection.setAutoCommit(false);
        try
        (
            final PreparedStatement statement = connection.prepareStatement(sql.chan_grp_insert);
        )
        {
            statement.setInt(1, group_id);
            statement.setString(2, name);
            statement.setInt(3, ((RDBEngineConfig)engine).getId());
            statement.executeUpdate();
            connection.commit();
        }
        catch (Exception ex)
        {
            connection.rollback();
            throw ex;
        }
        finally
        {
            connection.setAutoCommit(true);
        }
        return new RDBGroupConfig(group_id, name, null);
    }

    /** {@inheritDoc} */
    @Override
    public GroupConfig[] getGroups(final EngineConfig engine) throws Exception
    {
        final RDBEngineConfig rdb_engine = (RDBEngineConfig) engine;
        try
        (
            final PreparedStatement statement =
                rdb.getConnection().prepareStatement(sql.chan_grp_sel_by_eng_id);
        )
        {
            statement.setInt(1, rdb_engine.getId());
            final ResultSet result = statement.executeQuery();
            final List<GroupConfig> groups = new ArrayList<GroupConfig>();
            while (result.next())
            {
                // grp_id, name, enabling_chan_id, retent_id
                final int enabling_chan_id = result.getInt(3);
                final String enabling_channel;
                if (enabling_chan_id > 0)
                    enabling_channel = getChannelName(enabling_chan_id);
                else
                        enabling_channel = null;
                final GroupConfig group = new RDBGroupConfig(
                        result.getInt(1),
                        result.getString(2),
                        enabling_channel);
                groups.add(group);
            }
            result.close();
            final GroupConfig grp_arr[] = groups.toArray(new GroupConfig[groups.size()]);
            // Sort by group name in Java.
            // SQL should already give sorted result, but handling of upper/lowercase
            // names seems to differ between Oracle and MySQL, resulting in
            // files that were hard to compare
            Arrays.sort(grp_arr, new Comparator<GroupConfig>()
            {
                @Override
                public int compare(final GroupConfig a, final GroupConfig b)
                {
                    return a.getName().compareTo(b.getName());
                }
            });
            return grp_arr;
        }
    }

    /** @param channel_name Name of a channel
     *  @return {@link GroupConfig} for that channel or <code>null</code>
     *  @throws Exception on error
     */
    @Override
    public RDBGroupConfig getChannelGroup(final String channel_name) throws Exception
    {
        try
        (
            final PreparedStatement statement = rdb.getConnection().prepareStatement(sql.chan_grp_sel_by_channel);
        )
        {
            statement.setString(1, channel_name);
            final ResultSet result = statement.executeQuery();
            final RDBGroupConfig group;
            if (result.next())
                group = new RDBGroupConfig(result.getInt(1), result.getString(2), null);
            else
                group = null;
            result.close();
            return group;
        }
    }

    /** Set a group's enabling channel
     *  @param group Group that should enable based on a channel
     *  @param channel Channel or <code>null</code> to 'always' activate the group
     *  @throws Exception on error
     */
    @Override
    public void setEnablingChannel(final GroupConfig the_group, final ChannelConfig the_channel) throws Exception
    {
        RDBGroupConfig group = (RDBGroupConfig) the_group;
        RDBChannelConfig channel = (RDBChannelConfig) the_channel;

        final Connection connection = rdb.getConnection();
        connection.setAutoCommit(false);
        try
        (
            final PreparedStatement statement = connection.prepareStatement(sql.chan_grp_set_enable_channel);
        )
        {
            if (channel == null)
                statement.setNull(1, Types.INTEGER);
            else
                statement.setInt(1, channel.getId());
            statement.setInt(2, group.getId());
            final int rows = statement.executeUpdate();
            if (rows != 1)
                throw new Exception("Setting enabling channel of " + group + " to " + channel +
                        " changed " + rows + " rows instead of 1");
            connection.commit();
        }
        catch (Exception ex)
        {
            connection.rollback();
            throw ex;
        }
        finally
        {
            connection.setAutoCommit(true);
        }
    }

    /** @return Next available channel ID
     *  @throws Exception on error
     */
    private int getNextChannelId() throws Exception
    {
        try
        (
            final Statement statement = rdb.getConnection().createStatement();
            final ResultSet result = statement.executeQuery(sql.channel_next_id);
        )
        {
            if (result.next())
                return result.getInt(1) + 1;
            return 1;
        }
    }

    /** Add a channel.
     *
     *  <p>The channel might already exist in the RDB, but maybe it is not attached
     *  to a sample engine's group, or it's attached to a different group.
     *
     *  @param group {@link RDBGroupConfig} to which to add the channel
     *  @param name Name of channel
     *  @param mode Sample mode
     *  @return {@link RDBChannelConfig}
     *  @throws Exception on error
     */
    @Override
    public RDBChannelConfig addChannel(final GroupConfig the_group, final String name, final SampleMode the_mode)
            throws Exception
    {
        RDBGroupConfig group = (RDBGroupConfig) the_group;
        RDBSampleMode mode = (RDBSampleMode) the_mode;

        boolean new_channel = true;
        int channel_id = -1;

        final Connection connection = rdb.getConnection();
        connection.setAutoCommit(false);
        try
        {
            // Check for existing channel
            try
            (
                final PreparedStatement statement = connection.prepareStatement(sql.channel_sel_by_name);
            )
            {
                statement.setString(1, name);
                final ResultSet result = statement.executeQuery();
                if (result.next())
                {
                    channel_id = result.getInt(1);
                    new_channel = false;
                }
                result.close();
            }

            if (new_channel)
                channel_id = getNextChannelId();

            try
            (
                final PreparedStatement statement = connection.prepareStatement(new_channel ? sql.channel_insert : sql.channel_update);
            )
            {    // grp_id, name, smpl_mode_id, smpl_val, smpl_per, channel_id
                statement.setInt(1, group.getId());
                statement.setString(2, name);
                statement.setInt(3, mode.getId());
                statement.setDouble(4, mode.getDelta());
                statement.setDouble(5, mode.getPeriod());
                statement.setInt(6, channel_id);
                final int rows = statement.executeUpdate();
                if (rows != 1)
                    throw new Exception("Insert of " + group.getName() + " - " + name + " updated " + rows + " rows");
                connection.commit();
            }
            catch (Exception ex)
            {
                connection.rollback();
                throw ex;
            }
        }
        finally
        {
            connection.setAutoCommit(true);
        }
        return new RDBChannelConfig(channel_id, name, mode, null);
    }

    /** {@inheritDoc} */
    @Override
    public ChannelConfig[] getChannels(final GroupConfig group, final boolean skip_last) throws Exception
    {
        final RDBGroupConfig rdb_group = (RDBGroupConfig) group;
        final List<ChannelConfig> channels = new ArrayList<ChannelConfig>();
        try
        (
            final PreparedStatement statement =
                rdb.getConnection().prepareStatement(sql.channel_sel_by_group_id);
        )
        {
            statement.setInt(1, rdb_group.getId());
            final ResultSet result = statement.executeQuery();
            while (result.next())
            {   // channel_id, name, smpl_mode_id, smpl_val, smpl_per
                final int id = result.getInt(1);
                final SampleMode sample_mode =
                    getSampleMode(result.getInt(3), result.getDouble(4), result.getDouble(5));
                Instant last_sample_time = null;
                if (!skip_last)
                     last_sample_time = getLastSampleTime(id);
                channels.add(new RDBChannelConfig(id, result.getString(2),
                                                  sample_mode, last_sample_time));
            }
            result.close();
        }

        final ChannelConfig[] chan_arr = channels.toArray(new ChannelConfig[channels.size()]);
        // Sort by channel name in Java.
        // SQL should already give sorted result, but handling of upper/lowercase
        // names seems to differ between Oracle and MySQL, resulting in
        // files that were hard to compare
        Arrays.sort(chan_arr, new Comparator<ChannelConfig>()
        {
            @Override
            public int compare(final ChannelConfig a, final ChannelConfig b)
            {
                return a.getName().compareTo(b.getName());
            }
        });
        return chan_arr;
    }

    /** @param channel_id Channel ID in RDB
     *  @return Name of channel
     *  @throws Exception on error
     */
    private String getChannelName(final int channel_id) throws Exception
    {
        try
        (
            final PreparedStatement statement =
                rdb.getConnection().prepareStatement(sql.channel_sel_by_id);
        )
        {
            statement.setInt(1, channel_id);
            final ResultSet result = statement.executeQuery();
            if (! result.next())
                throw new Exception("Invalid channel ID " + channel_id);
            final String name = result.getString(1);
            result.close();
            return name;
        }
    }

    /** Obtain time stamp of last sample in archive
     *  @param channel_id Channel's RDB ID
     *  @return Time stamp or <code>null</code> if not in archive, yet
     *  @throws Exception on RDB error
     */
    private Instant getLastSampleTime(final int channel_id) throws Exception
    {
        // This statement has a surprisingly complex execution plan for partitioned
        // Oracle setups, so re-use it
        if (last_sample_time_statement == null)
            last_sample_time_statement = rdb.getConnection().prepareStatement(sql.sel_last_sample_time_by_id);
        last_sample_time_statement.setInt(1, channel_id);
        try
        (
            final ResultSet result = last_sample_time_statement.executeQuery();
        )
        {
            if (result.next())
            {
                final Timestamp stamp = result.getTimestamp(1);
                if (stamp == null)
                    return null;

                if (rdb.getDialect() != Dialect.Oracle)
                {
                    // For Oracle, the time stamp is indeed the last time.
                    // For others, it's only the seconds, not the nanoseconds.
                    // Since this time stamp is only used to avoid going back in time,
                    // add a second to assert that we are _after_ the last sample
                    stamp.setTime(stamp.getTime() + 1000);
                }
                return TimestampHelper.fromSQLTimestamp(stamp);
            }
        }
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public void close()
    {
        if (last_sample_time_statement != null)
        {
            try
            {
                last_sample_time_statement.close();
            }
            catch (Exception ex)
            {
                // Ignore, closing down anyway
            }
            last_sample_time_statement = null;
        }
        rdb.close();
    }
}
