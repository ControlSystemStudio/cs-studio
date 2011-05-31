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

/** Helper for getting severity info from RDB
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class SeverityReader
{
    /** Map IDs in RDB to SeverityLevel */
    final private Map<Integer, SeverityLevel> severity_by_id = new HashMap<Integer, SeverityLevel>();

    /** Initialize
     *  @param rdb RDB connection
     *  @param sql SQL statements
     *  @throws Exception on RDB error
     */
    public SeverityReader(final RDBUtil rdb, final SQL sql) throws Exception
    {
        final Statement statement = rdb.getConnection().createStatement();
        try
        {
            final ResultSet result = statement.executeQuery("SELECT " + sql.severity_id_col + ", " + sql.severity_name_col + " FROM " + sql.schema_prefix + sql.severity_table);
            while (result.next())
            {
                final int id = result.getInt(1);
                final SeverityLevel severity = SeverityLevel.parse(result.getString(2));
                severity_by_id.put(id, severity);
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
    public int getID(final SeverityLevel severity) throws Exception
    {
        final Iterator<Entry<Integer, SeverityLevel>> entries = severity_by_id.entrySet().iterator();
        while (entries.hasNext())
        {
            final Entry<Integer, SeverityLevel> entry = entries.next();
            if (entry.getValue().equals(severity))
                return entry.getKey();
        }
        throw new Exception("Unknown " + severity);
    }

    /** Lookup Severity by ID
     *  @param id Severity ID in RDB
     *  @return {@link SeverityLevel}
     *  @throws Exception when not found
     */
    public SeverityLevel getSeverity(final int id) throws Exception
    {
        final SeverityLevel level = severity_by_id.get(id);
        if (level == null)
            throw new Exception("Unknown severity ID " + id);
        return level;
    }
}
