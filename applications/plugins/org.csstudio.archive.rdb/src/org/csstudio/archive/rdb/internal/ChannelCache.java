/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.rdb.internal;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

import org.csstudio.archive.rdb.ChannelConfig;
import org.csstudio.archive.rdb.RDBArchive;
import org.csstudio.archive.rdb.SampleMode;

/** Caching RDB interface to channel info.
 *  @author Kay Kasemir
 */
public class ChannelCache
{
    private RDBArchive archive;

    /** Cache that maps names to channels */
    final private HashMap<String, ChannelConfig> cache_by_name =
        new HashMap<String, ChannelConfig>();
	
	/** Constructor */
	public ChannelCache(final RDBArchive archive)
	{
	    this.archive = archive;
	}
	
	/** Close prepared statements, clear cache. */
    public void dispose()
    {
        cache_by_name.clear();
    }

    /** Add Channel to cache */
	public void memorize(final ChannelConfig channel)
	{
		cache_by_name.put(channel.getName(), channel);
	}

	/** Get channel by name.
	 *  @param name channel name
	 *  @return channel or <code>null</code>
	 *  @throws Exception on error
	 */
	public ChannelConfig find(final String name) throws Exception
	{
		// Check cache
	    ChannelConfig channel = cache_by_name.get(name);
		if (channel != null)
			return channel;
		// Query RDB
		final PreparedStatement statement =
		    archive.getRDB().getConnection().prepareStatement(
		        archive.getSQL().channel_sel_by_name);
		try
		{
		    // SELECT channel_id, grp_id, smpl_mode_id, smpl_val, smpl_per FROM channel WHERE name=?
		    statement.setString(1, name);
		    final ResultSet result = statement.executeQuery();
		    if (result.next())
		    {
                final SampleMode sample_mode =
                    archive.getSampleMode(result.getInt(3));
                channel = new ChannelConfig(archive,
                        result.getInt(1), name, result.getInt(2),
                        sample_mode, result.getDouble(4), result.getDouble(5));
                memorize(channel);
            }
		}
		finally
		{
		    statement.close();
		}
        // else: Nothing found
        return channel;
	}
	
	/** Find or create a channel by name.
	 *  @param name Channel name
	 *  @return Channel
	 *  @throws Exception on error
	 */
	public ChannelConfig findOrCreate(final String name) throws Exception
	{
    	// Existing entry?
	    ChannelConfig channel = find(name);
        if (channel != null)
            return channel;
        
        final PreparedStatement statement =
            archive.getRDB().getConnection().prepareStatement(
                    archive.getSQL().channel_insert);
        try
        {
            channel = new ChannelConfig(archive, getNextId(), name,
                    0, archive.getSampleModes()[0], 0.0, 60.0);
            // channel_id, name, smpl_mode_id, smpl_per
            statement.setInt(1, channel.getId());
            statement.setString(2, name);
            statement.setInt(3, channel.getSampleMode().getId());
            statement.setDouble(4, channel.getSampleValue());
            statement.setDouble(5, channel.getSamplePeriod());
            statement.executeUpdate();
            memorize(channel);
            return channel;
        }
        finally
        {
            statement.close();
        }
	}

	/** @return Next available channel ID */
    private int getNextId() throws Exception
    {
        final PreparedStatement statement =
            archive.getRDB().getConnection().prepareStatement(
                    archive.getSQL().channel_sel_next_id);
        try
        {
            final ResultSet result = statement.executeQuery();
            if (result.next())
                return result.getInt(1) + 1;
            return 1;
        }
        finally
        {
            statement.close();
        }
    }
}
