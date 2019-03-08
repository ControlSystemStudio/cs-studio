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
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.platform.utility.rdb.Activator;
import org.csstudio.platform.utility.rdb.RDBUtil;
import org.csstudio.platform.utility.rdb.RDBUtil.Dialect;

/** Connect to a MySQL-based RDB
 *  @author Kay Kasemir
 *  @author Xihui Chen
 *  @author Laurent Philippe MySQL ReplicationDriver
 */
@SuppressWarnings("nls")
public class MySQL_RDB implements RDBImpl
{
    /** {@inheritDoc} */
    @Override
    public Dialect getDialect()
    {
        return RDBUtil.Dialect.MySQL;
    }

    /** {@inheritDoc} */
    @Override
    public Connection connect(final String url,
            final String user, final String password) throws Exception
    {
        final Properties props = new Properties();

        // Connect
        final Connection connection;
        if (user != null  ||  password != null)
        {
            props.put("user", user);
            props.put("password", password);
        }
        // Test if the connection should use replication driver or not
        if (url.startsWith("jdbc:mysql:replication"))
        {
            // We want this for failover on the slaves
            props.put("autoReconnect", "true");

            // We want to load balance between the slaves
            props.put("roundRobinLoadBalance", "true");
            // Before Connector/J 8.0, this required ReplicationDriver(),
            // but now normal driver can handle replication
        }
        // Class loader locates the plain MySQL driver
        Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
        connection = DriverManager.getConnection(url, props);

        // Basic database info
        final Logger logger = Activator.getLogger();
        if (logger.isLoggable(Level.FINER))
        {
            final DatabaseMetaData meta = connection.getMetaData();
            logger.finer("MySQL connection: " + meta.getDatabaseProductName()
                           + " " + meta.getDatabaseProductVersion());
        }

        return connection;
    }

    /** {@inheritDoc} */
    @Override
    public String getConnectionTestQuery()
    {
        return "SELECT 1";
    }
}
