/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.rdb.internal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.csstudio.archive.rdb.ChannelConfig;
import org.csstudio.archive.rdb.RDBArchive;
import org.csstudio.data.values.INumericMetaData;
import org.csstudio.data.values.ValueFactory;

/** Helper for handling the numeric meta data table.
 *  @author Kay Kasemir
 */
public class NumericMetaDataHelper
{
    private NumericMetaDataHelper()
    {
        // prevent instantiation
    }

    /** Locate numeric meta data for a channel.
     *  @param archive Archive to search
     *  @param channel Channel for which to get the numeric meta data
     *  @return {@link INumericMetaData} or <code>null</code> if nothing found
     *  @throws Exception on error
     */
    public static INumericMetaData get(final RDBArchive archive, final ChannelConfig channel)
        throws Exception
    {
        final PreparedStatement sel = archive.getRDB().getConnection().prepareStatement(
                archive.getSQL().numeric_meta_sel_by_channel);
        try
        {
            sel.setInt(1, channel.getId());
            final ResultSet res = sel.executeQuery();
            if (res.next())
                return ValueFactory.createNumericMetaData(
                        res.getDouble(1), res.getDouble(2), // display range
                        res.getDouble(3), res.getDouble(4), // warn range
                        res.getDouble(5), res.getDouble(6), // alarm range
                        res.getInt(7), res.getString(8));   // prev, units
        }
        finally
        {   // RDB Cleanup
            sel.close();
        }
        return null;
    }

    /** Set the numeric meta data for a channel.
     *  This either sets new meta data or modifies the existing entry.
     *  @param archive Archive connection
     *  @param channel Channel for which to set the meta data
     *  @param meta INumericMetaData. May be <code>null</code> to clear.
     *  @throws Exception on error
     */
    public static void set(final RDBArchive archive,
            final ChannelConfig channel, final INumericMetaData meta) throws Exception
    {
        // See if already defined as needed.
        final INumericMetaData found = get(archive, channel);
        if (meta == null)
        {
            if (found != null)
            {   // Clear existing info
                delete(archive, channel);
                archive.getRDB().getConnection().commit();
            }
            return;
        }
        if (meta.equals(found)) // also handles found == null
            return;
        // Delete old entries, then insert current ones.
        delete(archive, channel);
        insert(archive, channel, meta);
        archive.getRDB().getConnection().commit();
    }

    /** Helper: Insert meta data into archive. */
    private static void insert(final RDBArchive archive,
            final ChannelConfig channel, final INumericMetaData meta) throws Exception
    {
        final Connection connection = archive.getRDB().getConnection();
        // Define the new ones
        final PreparedStatement insert = connection.prepareStatement(
                archive.getSQL().numeric_meta_insert);
        try
        {
            insert.setInt(1, channel.getId());
            insert.setDouble(2, meta.getDisplayLow());
            insert.setDouble(3, meta.getDisplayHigh());
            insert.setDouble(4, meta.getWarnLow());
            insert.setDouble(5, meta.getWarnHigh());
            insert.setDouble(6, meta.getAlarmLow());
            insert.setDouble(7, meta.getAlarmHigh());
            insert.setInt(8, meta.getPrecision());
            // Oracle schema has NOT NULL units...
            String units = meta.getUnits();
            if (units == null  ||  units.length() < 1)
                units = " "; //$NON-NLS-1$
            insert.setString(9, units);
            insert.executeUpdate();
        }
        finally
        {
            insert.close();
        }
    }

    /** Helper: Delete meta data for channel. */
    private static void delete(final RDBArchive archive,
            final ChannelConfig channel) throws Exception
    {
        // Delete any existing entries
        final Connection connection = archive.getRDB().getConnection();
        final PreparedStatement del = connection.prepareStatement(
                archive.getSQL().numeric_meta_delete_by_channel);
        try
        {
            del.setInt(1, channel.getId());
            del.executeUpdate();
        }
        finally
        {
            del.close();
        }
    }
}
