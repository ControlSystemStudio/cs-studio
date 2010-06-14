package org.csstudio.platform.utility.rdb.internal;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;

import org.apache.log4j.Logger;
import org.csstudio.platform.logging.CentralLogger;
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
    public MySQL_RDB(final String url, final String user, final String password, 
    		final boolean autoReconnect) throws Exception
    {
        super(url, user, password, Dialect.MySQL, autoReconnect);
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
        final Logger logger = CentralLogger.getInstance().getLogger(this);
        if (logger.isDebugEnabled())
        {
            final DatabaseMetaData meta = connection.getMetaData();
            logger.debug("MySQL connection: " + meta.getDatabaseProductName()
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
