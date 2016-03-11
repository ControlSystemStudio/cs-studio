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

/** Connect to an Oracle-based RDB
 *  @author Kay Kasemir
 *  @author Xihui Chen
 */
@SuppressWarnings("nls")
public class OracleRDB implements RDBImpl
{
    /** {@inheritDoc} */
    @Override
    public Dialect getDialect()
    {
        return RDBUtil.Dialect.Oracle;
    }

    /** {@inheritDoc} */
    @Override
    public Connection connect(final String url,
            final String user, final String password) throws Exception
    {
        // Get class loader to find the driver
        Class.forName("oracle.jdbc.driver.OracleDriver").newInstance();
        // Connect such that Java float and double map to Oracle
        // BINARY_FLOAT resp. BINARY_DOUBLE
        final Properties info = new Properties();
        info.put("SetFloatAndDoubleUseBinary", "true");
        if (user != null)
            info.put("user", user);
        if (password != null)
            info.put("password", password);
        final Connection connection = DriverManager.getConnection(url, info);
        // Basic database info
        final Logger logger = Activator.getLogger();
        if (logger.isLoggable(Level.FINER))
        {
            final DatabaseMetaData meta = connection.getMetaData();
            logger.finer(meta.getDatabaseProductVersion());
        }
        return connection;
    }

    /** {@inheritDoc} */
    @Override
    public String getConnectionTestQuery()
    {
        return "SELECT 1 FROM DUAL";
    }
}
