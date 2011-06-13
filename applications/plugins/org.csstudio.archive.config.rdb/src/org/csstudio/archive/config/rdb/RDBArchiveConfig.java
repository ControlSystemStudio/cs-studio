/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.config.rdb;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.csstudio.archive.config.ArchiveConfig;
import org.csstudio.archive.config.ChannelConfig;
import org.csstudio.archive.config.EngineConfig;
import org.csstudio.archive.config.GroupConfig;
import org.csstudio.archive.config.SampleMode;
import org.csstudio.data.values.ITimestamp;
import org.csstudio.data.values.TimestampFactory;
import org.csstudio.platform.utility.rdb.RDBUtil;

/** RDB implementation (Oracle, MySQL, PostgreSQL) of {@link ArchiveConfig}
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class RDBArchiveConfig implements ArchiveConfig
{
	/** RDB connection */
	private RDBUtil rdb;
	
	/** SQL statements */
	private SQL sql;
	
	/** Map of numeric sample mode IDs as stored in RDB to more detail */
	private Map<Integer, RDBSampleMode> sample_mode_map = null;

	/** Re-used statement for selecting time of last archived sample of a channel */
	private PreparedStatement last_sample_time_statement;

    /** Initialize.
     *  This constructor will be invoked when an {@link ArchiveConfig}
     *  is created via the extension point.
     *  @throws Exception on error, for example RDB connection error
     */
	public RDBArchiveConfig() throws Exception
    {
		this(Preferences.getURL(), Preferences.getUser(),
				Preferences.getPassword(), Preferences.getSchema());
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
    }

	/** {@inheritDoc} */
	@Override
	public EngineConfig findEngine(final String name) throws Exception
	{
		final PreparedStatement statement =
			rdb.getConnection().prepareStatement(sql.smpl_eng_sel_by_name);
		try
        {
            statement.setString(1, name);
            final ResultSet res = statement.executeQuery();
            if (res.next())
                return new RDBEngineConfig(res.getInt(1), name,
                        res.getString(2), res.getString(3));
        }
        finally
        {
            statement.close();
        }
        return null;
	}

	/** {@inheritDoc} */
	@Override
    public GroupConfig[] getGroups(final EngineConfig engine) throws Exception
    {
		final RDBEngineConfig rdb_engine = (RDBEngineConfig) engine;
        final PreparedStatement statement =
        	rdb.getConnection().prepareStatement(sql.chan_grp_sel_by_eng_id);
        try
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
        finally
        {
            statement.close();
        }
    }
	
	/** @param channel_id Channel ID in RDB
	 *  @return Name of channel
	 *  @throws Exception on error
	 */
    private String getChannelName(final int channel_id) throws Exception
	{
        final PreparedStatement statement =
        	rdb.getConnection().prepareStatement(sql.channel_sel_by_id);
        try
        {
            statement.setInt(1, channel_id);
            final ResultSet result = statement.executeQuery();
            if (! result.next())
            	throw new Exception("Invalid channel ID " + channel_id);
            final String name = result.getString(1);
            result.close();
            return name;
        }
        finally
        {
            statement.close();
        }
	}
	
	/** {@inheritDoc} */
	@Override
    public ChannelConfig[] getChannels(final GroupConfig group) throws Exception
    {
		final RDBGroupConfig rdb_group = (RDBGroupConfig) group;
        final List<ChannelConfig> channels = new ArrayList<ChannelConfig>();
        final PreparedStatement statement =
        	rdb.getConnection().prepareStatement(sql.channel_sel_by_group_id);
        try
        {
            statement.setInt(1, rdb_group.getId());
            final ResultSet result = statement.executeQuery();
            while (result.next())
            {   // channel_id, name, smpl_mode_id, smpl_val, smpl_per
                final int id = result.getInt(1);
                final SampleMode sample_mode =
                    getSampleMode(result.getInt(3), result.getDouble(4), result.getDouble(5));
                final ITimestamp last_sample_time = getLastSampleTime(id);
				channels.add(new RDBChannelConfig(id, result.getString(2),
                                                  sample_mode, last_sample_time));
            }
            result.close();
        }
        finally
        {
            statement.close();
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

	/** Obtain time stamp of last sample in archive
	 *  @param channel_id Channel's RDB ID
	 *  @return Time stamp or <code>null</code> if not in archive, yet
	 *  @throws Exception on RDB error
	 */
	private ITimestamp getLastSampleTime(final int channel_id) throws Exception
    {
		// This statement has a surprisingly complex execution plan for partitioned
		// Oracle setups, so re-use it
		if (last_sample_time_statement == null)
			last_sample_time_statement = rdb.getConnection().prepareStatement(sql.sel_last_sample_time_by_id);
		last_sample_time_statement.setInt(1, channel_id);
		final ResultSet result = last_sample_time_statement.executeQuery();
		try
		{
			if (result.next())
				return TimestampFactory.fromSQLTimestamp(result.getTimestamp(1));
		}
		finally
		{
			result.close();
		}
	    return null;
    }

	/** Determine sample mode
	 *  @param sample_mode_id Sample mode ID from RDB
	 *  @param sample_value Sample value, i.e. monitor threshold
	 *  @param period Scan period, estimated monitor period
	 *  @return {@link SampleMode}
	 *  @throws Exception
	 */
	private SampleMode getSampleMode(final int sample_mode_id, final double sample_value, final double period) throws Exception
    {
	    final boolean monitor = isMonitor(sample_mode_id);
	    return new SampleMode(monitor , sample_value, period);
    }

	/** Determine if sample mode is 'monitored'
	 * 
	 *  @param sample_mode_id Numeric sample mode as stored in RDB
	 *  @return <code>true</code> for monitor
	 *  @throws Exception
	 */
	private boolean isMonitor(final int sample_mode_id) throws Exception
    {
		if (sample_mode_map == null)
		{
			sample_mode_map = new HashMap<Integer, RDBSampleMode>();
			final Statement statement = rdb.getConnection().createStatement();
			try
			{
				final ResultSet result = statement.executeQuery(sql.sample_mode_sel);
				while (result.next())
				{
					sample_mode_map.put(result.getInt(1),
							new RDBSampleMode(result.getString(2), result.getString(3)));
				}
				result.close();
			}
			finally
			{
				statement.close();
			}
		}
		final RDBSampleMode rdb_mode = sample_mode_map.get(sample_mode_id);
	    return rdb_mode == null  ||  rdb_mode.isMonitor();
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
