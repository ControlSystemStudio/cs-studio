/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.platform.utility.rdb.internal;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.platform.utility.rdb.Activator;
import org.csstudio.platform.utility.rdb.RDBUtil;

/** Connect to a PostgreSQL-based RDB
 *  @author Lana Abadie
 *  @author Kay Kasemir
 *  @author Xihui Chen
 */
@SuppressWarnings("nls")
public class PostgreSQL_RDB extends RDBUtil
{
    /** Initialize
     *  @param url Database URL
     *  @param user ... user
     *  @param password ... password
     *  @throws Exception on error
     */
    public PostgreSQL_RDB(final String url, final String user, final String password,
    		final boolean autoReconnect) throws Exception
    {
        super(url, user, password, Dialect.PostgreSQL, autoReconnect);
    }

    /** {@inheritDoc} */
    @Override
    protected Connection do_connect(final String url,
            final String user, final String password) throws Exception
    {
        // Get class loader to find the driver
        Class.forName("org.postgresql.Driver").newInstance();
        // Connect
        final Connection connection;
        if (user != null  ||  password != null)
            connection = DriverManager.getConnection(url, user, password);
        else
            connection = DriverManager.getConnection(url);
        // Basic database info
        final Logger logger = Activator.getLogger();
        if (logger.isLoggable(Level.FINER))
        {
            final DatabaseMetaData meta = connection.getMetaData();
            logger.finer("PostgreSQL connection: " + meta.getDatabaseProductName()
                            + " " + meta.getDatabaseProductVersion());
        }
        return connection;
    }

    /** {@inheritDoc} */
    @Override
    protected String getConnectionTestQuery()
    {
        return "SELECT 1";
    }
}
