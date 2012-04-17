/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.server;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import org.csstudio.alarm.beast.SQL;
import org.csstudio.alarm.beast.SeverityLevel;
import org.csstudio.platform.utility.rdb.RDBUtil;

/** Mapping from SeverityLevel to RDB severity ID
 *  @author Kay Kasemir
 *  @author Lana Abadie - Disable autocommit as needed.
 */
public class SeverityMapping
{
    /** Mapping from SeverityLevel to Severity ID in RDB
     *  <p>
     *  severity_ids[SeverityLevel.ordinal] = severity ID in RDB
     */
    final private int severity_ids[];
    
    public SeverityMapping(final RDBUtil rdb, final SQL sql) throws Exception
    {
        final PreparedStatement statement =
            rdb.getConnection().prepareStatement(sql.sel_severity);
        try
        {
            final SeverityLevel severities[] = SeverityLevel.values();
            severity_ids = new int[severities.length];
            for (SeverityLevel level : severities)
            {
                statement.setString(1, level.name());
                final ResultSet result = statement.executeQuery();
                int id;
                if (result.next())
                    id = result.getInt(1);
                else
                    id = addSeverity(rdb, sql, level.name());
                severity_ids[level.ordinal()] = id;
            }
        }
        finally
        {
            statement.close();
        }
    }

    /** Map severity to RDB ID
     *  @param level SeverityLevel
     *  @return ID in RDB
     */
    public int getSeverityID(final SeverityLevel level)
    {
        return severity_ids[level.ordinal()];
    }

    /** Map severity ID in RDB to SeverityLevel
     *  @param id ID in RDB 
     *  @return SeverityLevel
     *  @throws Exception on error
     */
    @SuppressWarnings("nls")
    public SeverityLevel getSeverityLevel(final int id) throws Exception
    {
        for (int ordinal = 0; ordinal<severity_ids.length; ++ordinal)
            if (severity_ids[ordinal] == id)
                return SeverityLevel.values()[ordinal];
        throw new Exception("Cannot map ID " + id + " to SeverityLevel");
    }

    /** Add severity to RDB
     *  @param rdb
     *  @param sql
     *  @param name Name of severity
     *  @return ID used for this in RDB
     *  @throws Exception
     */
    private int addSeverity(final RDBUtil rdb, final SQL sql, final String name)
        throws Exception
    {
        final int id = getNextID(rdb, sql);
        final PreparedStatement statement =
            rdb.getConnection().prepareStatement(sql.insert_severity);
        try
        {
        	rdb.getConnection().setAutoCommit(false);
            statement.setInt(1, id);
            statement.setString(2, name);
            statement.executeUpdate();
            rdb.getConnection().commit();
        }
        catch(Exception e)
        {
        	rdb.getConnection().rollback();
        	throw e;
        }
        finally
        {
            statement.close();
            rdb.getConnection().setAutoCommit(true);
        }
        return id;
    }

    /** Get next available severity ID
     *  @param rdb
     *  @param sql
     *  @return ID
     *  @throws Exception on error
     */
    private int getNextID(final RDBUtil rdb, final SQL sql) throws Exception
    {
        final Statement statement = rdb.getConnection().createStatement();
        try
        {
            final ResultSet result = statement.executeQuery(sql.sel_last_severity);
            return result.next() ? result.getInt(1) + 1 : 1;
        }
        finally
        {
            statement.close();
        }
    }
}
