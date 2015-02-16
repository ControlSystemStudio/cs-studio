/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.client;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.csstudio.alarm.beast.SQL;
import org.csstudio.alarm.beast.SeverityLevel;
import org.csstudio.platform.utility.rdb.RDBUtil;

/** Helper for getting alarm message info from RDB
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class MessageReader
{
    /** Map IDs in RDB to message */
    final private Map<Integer, String> message_by_id = new HashMap<Integer, String>();

    /** Initialize
     *  @param rdb RDB connection
     *  @param sql SQL statements
     *  @throws Exception on RDB error
     */
    public MessageReader(final RDBUtil rdb, final SQL sql) throws Exception
    {
        final Statement statement = rdb.getConnection().createStatement();
        try
        {
            final ResultSet result = statement.executeQuery("SELECT " + sql.message_id_col + ", " + sql.message_name_col + " FROM " + sql.schema_prefix + sql.message_table);
            while (result.next())
            {
                final int id = result.getInt(1);
                final String message = result.getString(2);
                message_by_id.put(id, message);
            }
            result.close();
        }
        finally
        {
            statement.close();
        }
    }

    /** (Slow) lookup of severity ID
     *  @param severity {@link SeverityLevel}
     *  @return ID
     *  @throws Exception when not found
     */
    public int getID(final String message) throws Exception
    {
        final Iterator<Entry<Integer, String>> entries = message_by_id.entrySet().iterator();
        while (entries.hasNext())
        {
            final Entry<Integer, String> entry = entries.next();
            if (entry.getValue().equals(message))
                return entry.getKey();
        }
        throw new Exception("Unknown " + message);
    }

    /** Lookup message by ID
     *  @param id Message ID in RDB
     *  @return Message
     *  @throws Exception when not found
     */
    public String getMessage(final int id) throws Exception
    {
        final String message = message_by_id.get(id);
        if (message == null)
            throw new Exception("Unknown message ID " + id);
        return message;
    }
}
