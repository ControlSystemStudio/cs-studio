/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.writer.rdb;

import java.sql.Connection;
import java.sql.PreparedStatement;

import org.csstudio.data.values.INumericMetaData;
import org.csstudio.platform.utility.rdb.RDBUtil;

/** Helper for handling the numeric meta data table.
 *  @author Kay Kasemir
 */
public class NumericMetaDataHelper
{
    private NumericMetaDataHelper()
    {
        // prevent instantiation
    }

    /** Delete meta data for channel
     *  @param rdb RDBUtil
     *  @param sql SQL statements
     *  @param channel Channel
     *  @throws Exception on error
     */
    public static void delete(final RDBUtil rdb, final SQL sql,
            final RDBWriteChannel channel) throws Exception
    {
        // Delete any existing entries
        final Connection connection = rdb.getConnection();
        final PreparedStatement del = connection.prepareStatement(
                sql.numeric_meta_delete_by_channel);
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

    /** Insert meta data for channel into archive
     *  @param rdb RDBUtil
     *  @param sql SQL statements
     *  @param channel Channel
     *  @param meta Meta data
     *  @throws Exception on error
     */
    public static void insert(final RDBUtil rdb, final SQL sql,
            final RDBWriteChannel channel, final INumericMetaData meta) throws Exception
    {
        final Connection connection = rdb.getConnection();
        final PreparedStatement insert = connection.prepareStatement(sql.numeric_meta_insert);
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
}
