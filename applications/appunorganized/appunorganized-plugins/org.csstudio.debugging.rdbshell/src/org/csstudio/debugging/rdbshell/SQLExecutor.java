/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.debugging.rdbshell;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;

import org.csstudio.platform.utility.rdb.RDBUtil;

/** Execute arbitrary SQL query, provide result as string matrix
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class SQLExecutor
{
    final private RDBUtil rdb;

    /** Initialize
     *  @param url RDB URL
     *  @param user RDB user
     *  @param password .. password
     *  @throws Exception on error
     */
    public SQLExecutor(final String url, final String user, final String password) throws Exception
    {
        rdb = RDBUtil.connect(url, user, password, true);
    }

    /** Execute query
     *  @param sql The query
     *  @return Array of rows, where each row is array of column texts.
     *          First how has headers.
     *  @throws Exception on error
     */
    public ArrayList<String[]> execute(final String sql) throws Exception
    {
        if (sql.toLowerCase().startsWith("select"))
            return executeQuery(sql);
        else
            return executeUpdate(sql);
    }

    /** Execute query
     *  @param sql SQL that's supposed to be a 'select'
     *  @see #execute(String)
     */
    private ArrayList<String[]> executeQuery(final String sql)
            throws Exception
    {
        final ArrayList<String[]> rows = new ArrayList<String[]>();
        final Statement statement = rdb.getConnection().createStatement();
        try
        {
            final ResultSet result = statement.executeQuery(sql);
            // Get headers from result
            final ResultSetMetaData meta = result.getMetaData();
            final int col_count = meta.getColumnCount();
            final String headers[] = new String[col_count];
            for (int c=1; c<=col_count; ++c)
                headers[c-1] = meta.getColumnLabel(c);
            rows.add(headers);
            // Get rows from result
            while (result.next())
            {
                final String row[] = new String[col_count];
                for (int c=1; c<=col_count; ++c)
                {
                    // Check type? final int type = meta.getColumnType(c);
                    row[c-1] = result.getString(c);
                    if (row[c-1] == null)
                        row[c-1] = "";
                }
                rows.add(row);
            }
        }
        finally
        {
            statement.close();
        }
        return rows;
    }

    /** Execute update
     *  @param sql SQL that's supposed to be an 'insert' or 'update' command
     *  @see #execute(String)
     */
    private ArrayList<String[]> executeUpdate(final String sql)
    throws Exception
    {
        final ArrayList<String[]> rows = new ArrayList<String[]>();
        final Statement statement = rdb.getConnection().createStatement();
        try
        {
            final int affected = statement.executeUpdate(sql);
            rows.add(new String[] { "Affected rows" });
            rows.add(new String[] { Integer.toString(affected) });
        }
        finally
        {
            statement.close();
        }
        return rows;
    }

    /** Must be called when done */
    public void close()
    {
        rdb.close();
    }
}
