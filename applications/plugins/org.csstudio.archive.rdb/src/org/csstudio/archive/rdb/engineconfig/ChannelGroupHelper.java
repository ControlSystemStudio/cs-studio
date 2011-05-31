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
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import org.csstudio.archive.rdb.RDBArchive;

/** Helper for handling ChannelGroup entries in RDB
 *  @author Kay Kasemir
 */
public class ChannelGroupHelper
{
    final private RDBArchive archive;

    public ChannelGroupHelper(final RDBArchive archive)
    {
        this.archive = archive;
    }

    /** Add a channel group description.
     *  <p>
     *  The group must not previously exist for this engine!
     *  @param name Group name
     *  @param engine_id Engine that samples this group of channels
     *  @param enabling_channel_id ID of enabling channel, or &lt;= 0 if none
     *  @return ChannelGroup
     *  @throws Exception on error
     */
    public ChannelGroupConfig add(final String name, final int engine_id,
            final int enabling_channel_id)
        throws Exception
    {
        final Connection connection = archive.getRDB().getConnection();
        final ChannelGroupConfig found = find(name, engine_id);
        final int id;
        if (found != null)
        {
            throw new Exception(
                    String.format("Group '%s' already exists under engine # %d", //$NON-NLS-1$
                            name, engine_id));
            // How to fix? Delete existing group? Update?
            // Unlink all channels from engine's groups?
            // Adding/defining an engine already removes all groups and channel links...
        }
        else
            id = getNextID();
        final ChannelGroupConfig group = new ChannelGroupConfig(archive, id, name, engine_id,
                enabling_channel_id);
        final PreparedStatement statement = connection.prepareStatement(
                archive.getSQL().chan_grp_insert);
        try
        {
            statement.setInt(1, id);
            statement.setString(2, name);
            statement.setInt(3, engine_id);
            if (enabling_channel_id > 0)
                statement.setInt(4, enabling_channel_id);
            else
                statement.setNull(4, java.sql.Types.INTEGER);
            statement.executeUpdate();
        }
        finally
        {
            statement.close();
        }
        connection.commit();
        return group;
    }

    /** Get all groups under an engine.
     *  @param engine_id Engine ID
     *  @return ChannelGroup array
     *  @throws Exception on error
     */
    public ChannelGroupConfig[] get(final int engine_id) throws Exception
    {
        final PreparedStatement statement =
            archive.getRDB().getConnection().prepareStatement(
                archive.getSQL().chan_grp_sel_by_eng_id);
        try
        {
            statement.setInt(1, engine_id);
            final ResultSet result = statement.executeQuery();
            final ArrayList<ChannelGroupConfig> groups = new ArrayList<ChannelGroupConfig>();
            while (result.next())
            {
                // grp_id, name, enabling_chan_id, retent_id
                final ChannelGroupConfig group = new ChannelGroupConfig(
                        archive,
                        result.getInt(1),
                        result.getString(2),
                        engine_id,
                        result.getInt(3));
                groups.add(group);
            }
            final ChannelGroupConfig grp_arr[] = new ChannelGroupConfig[groups.size()];
            groups.toArray(grp_arr);
            // Sort by group name in Java.
            // SQL should already give sorted result, but handling of upper/lowercase
            // names seems to differ between Oracle and MySQL, resulting in
            // files that were hard to compare
            Arrays.sort(grp_arr, new Comparator<ChannelGroupConfig>()
            {
                @Override
                public int compare(final ChannelGroupConfig a, final ChannelGroupConfig b)
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

    private int getNextID() throws Exception
    {
        final Statement statement =
            archive.getRDB().getConnection().createStatement();
        try
        {
            final ResultSet res =
                statement.executeQuery(archive.getSQL().chan_grp_next_id);
            if (res.next())
            {
                final int id = res.getInt(1);
                if (id > 0)
                    return id + 1;
            }
            return 1;
        }
        finally
        {
            statement.close();
        }
    }

    /** Find group by name and engine. Same group name can be used
     *  for different engines.
     *  @param group_name Name of the group
     *  @param engine_id ID of the engine.
     *  @return ChannelGroup
     *  @throws Exception on error
     */
    public ChannelGroupConfig find(final String group_name, final int engine_id)
        throws Exception
    {
        // RDB Lookup
        final PreparedStatement statement =
            archive.getRDB().getConnection().prepareStatement(
                    archive.getSQL().chan_grp_sel_by_name_and_eng_id);
        try
        {
            statement.setString(1, group_name);
            statement.setInt(2, engine_id);
            final ResultSet res = statement.executeQuery();
            if (res.next())
            {
                final ChannelGroupConfig group =
                    new ChannelGroupConfig(archive, res.getInt(1), group_name,
                        engine_id, res.getInt(2));
                return group;
            }
        }
        finally
        {
            statement.close();
        }
        return null;
    }

    /** Find group by ID
     *  @param group_id Group ID
     *  @return ChannelGroup or <code>null</code>
     *  @throws Exception on error
     */
    public ChannelGroupConfig find(int group_id) throws Exception
    {
        // RDB Lookup
        final PreparedStatement statement =
            archive.getRDB().getConnection().prepareStatement(
                    archive.getSQL().chan_grp_sel_by_id);
        try
        {
            statement.setInt(1, group_id);
            final ResultSet res = statement.executeQuery();
            if (res.next())
            {   // name, eng_id, enabling_chan_id
                final ChannelGroupConfig group =
                    new ChannelGroupConfig(archive, group_id, res.getString(1),
                            res.getInt(2), res.getInt(3));
                return group;
            }
        }
        finally
        {
            statement.close();
        }
        return null;
    }
}
