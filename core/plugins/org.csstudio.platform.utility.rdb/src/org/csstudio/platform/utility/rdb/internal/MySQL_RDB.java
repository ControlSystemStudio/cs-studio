package org.csstudio.platform.utility.rdb.internal;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;

import org.csstudio.platform.utility.rdb.Activator;
import org.csstudio.platform.utility.rdb.RDBUtil;

/** Connect to a MySQL-based RDB
 *  @author Kay Kasemir
 *  @author Xihui Chen
 */
@SuppressWarnings("nls")
public class MySQL_RDB extends RDBUtil
{
    /** Initialize
     *  @param url Database URL
     *  @param user ... user
     *  @param password ... password
     *  @throws Exception on error
     */
    public MySQL_RDB(final String url, final String user, final String password) throws Exception
    {
        super(url, user, password, Dialect.MySQL);
    }

    /** {@inheritDoc} */
    @Override
    protected Connection do_connect(final String url,
            final String user, final String password) throws Exception
    {
        // Get class loader to find the driver
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        // Connect
        final Connection connection;
        if (user != null  ||  password != null)
            connection = DriverManager.getConnection(url, user, password);
        else
            connection = DriverManager.getConnection(url);
        // Basic database info
        final DatabaseMetaData meta = connection.getMetaData();
        Activator.getLogger().debug("MySQL connection: " + meta.getDatabaseProductName()
                        + " " + meta.getDatabaseProductVersion());
        return connection;
    }
    
    /** {@inheritDoc} */
    @Override
    protected String getConnectionTestQuery()
    {
        return "SHOW DATABASES";
    }
}
