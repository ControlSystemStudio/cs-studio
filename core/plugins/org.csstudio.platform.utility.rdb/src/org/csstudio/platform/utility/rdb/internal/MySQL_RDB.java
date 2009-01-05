package org.csstudio.platform.utility.rdb.internal;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;

import org.csstudio.platform.utility.rdb.Activator;
import org.csstudio.platform.utility.rdb.RDBUtil;


/** Connect to a MySQL-based RDB
 *  @author Kay Kasemir
 */
public class MySQL_RDB extends RDBUtil
{
    /** Hidden constructor.
     *  @see #connect(String)
     */
    private MySQL_RDB(final String url, final String user, final String password, final Connection connection) throws Exception
    {
        super(url, user, password, Dialect.MySQL, connection);
    }

    /** Connect to the database.
     *  @param url MySQL-type URL
     *  @param user User name or <code>null</code> if part of url
     *  @param password Password or <code>null</code> if part of url
     *  @return RDBArchiveServer
     *  @exception on error
     */
    @SuppressWarnings("nls")
    public static MySQL_RDB connect(final String url,
            final String user, final String password) throws Exception
    {
        // Get class loader to find the driver
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        // Connect
        Connection connection;
        if (user != null  ||  password != null)
            connection = DriverManager.getConnection(url, user, password);
        else
            connection = DriverManager.getConnection(url);
        // Basic database info
        final DatabaseMetaData meta = connection.getMetaData();
        Activator.getLogger().debug("MySQL connection: " + meta.getDatabaseProductName()
                        + " " + meta.getDatabaseProductVersion());
        return new MySQL_RDB(url, user, password, connection);
    }
}
