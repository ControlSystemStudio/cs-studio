package org.csstudio.platform.utility.rdb.internal;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.util.Properties;

import org.csstudio.platform.utility.rdb.Activator;
import org.csstudio.platform.utility.rdb.RDBUtil;

/** Connect to an Oracle-based RDB
 *  @author Kay Kasemir
 */
public class OracleRDB extends RDBUtil
{
    /** Hidden constructor.
     *  @see #connect(String)
     */
    private OracleRDB(final Connection connection) throws Exception
    {
        super(Dialect.Oracle, connection);
    }

    /** Connect to the database.
     *  @param url Oracle thin driver URL
     *  @param user User name or <code>null</code> if part of url
     *  @param password Password or <code>null</code> if part of url
     *  @return RDBArchiveServer
     *  @exception on error
     */
    @SuppressWarnings("nls")
    public static OracleRDB connect(final String url,
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
        final DatabaseMetaData meta = connection.getMetaData();
        Activator.getLogger().debug(meta.getDatabaseProductVersion());
        return new OracleRDB(connection);
    }
}
