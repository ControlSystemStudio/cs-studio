package org.csstudio.platform.utility.rdb.internal;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.util.Properties;

import org.csstudio.platform.utility.rdb.Activator;
import org.csstudio.platform.utility.rdb.RDBUtil;

/** Connect to an Oracle-based RDB
 *  @author Kay Kasemir
 *  @author Xihui Chen
 */
@SuppressWarnings("nls")
public class OracleRDB extends RDBUtil
{
    /** Initialize
     *  @param url Database URL
     *  @param user ... user
     *  @param password ... password
     *  @throws Exception on error
     */
    public OracleRDB(final String url, final String user, final String password
    		) throws Exception
    {
        super(url, user, password, Dialect.Oracle);
    }

    /** {@inheritDoc} */
    @Override
    public Connection do_connect(final String url,
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
        return connection;
    }
    
    /** {@inheritDoc} */
    @Override
    protected String getConnectionTestQuery()
    {
        return "SELECT 1 FROM DUAL";
    }
}
