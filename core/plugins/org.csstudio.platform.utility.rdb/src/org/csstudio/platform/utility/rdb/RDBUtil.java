package org.csstudio.platform.utility.rdb;

import java.sql.Connection;

import org.csstudio.platform.utility.rdb.internal.MySQL_RDB;
import org.csstudio.platform.utility.rdb.internal.OracleRDB;

/** Obtain database connection for various RDB systems.
 *  @author Kay Kasemir
 */
public class RDBUtil
{
	/** Database URL */
	final private String url;
	
    /** Connection to the SQL server */
    final private Connection connection;
    
    /** Start of MySQL URL */
    private static final String JDBC_MYSQL = "jdbc:mysql://"; //$NON-NLS-1$

    /** Start of Oracle URL */
    private static final String JDBC_ORACLE = "jdbc:oracle:"; //$NON-NLS-1$
    
    /** Database dialect.
     *  For starters, the connection mechanisms vary, and since
     *  SQL isn't fully normed, there might be more differences
     *  that we need to handle, so we keep track of the dialect.
     */
    public enum Dialect
    {
        /** Database that understands MySQL commands */
        MySQL,
        /** Database that understands Oracle commands */
        Oracle
    }
    
    /** @see Dialect */
    final private Dialect dialect;
    
    /** Constructor for derived classes.
     *  @param dialect
     *  @param connection
     *  @throws Exception
     *  @see #connect(String)
     */
    protected RDBUtil(final String url, final Dialect dialect,
    		          final Connection connection) throws Exception
    {
    	this.url = url;
    	this.dialect = dialect;
    	this.connection = connection;
        connection.setAutoCommit(false);
    }

    /** Connect to the database.
     *  <p>
     *  The URL format depends on the database dialect.
     *  <p>
     *  For MySQL resp. Oracle, the formats are:
     *  <pre>
     *     jdbc:mysql://[host]:[port]/[database]?user=[user]&password=[password]
     *     jdbc:oracle:thin:[user]/[password]@//[host]:[port]/[database]
     *  </pre>
     *  
     *  For Oracle, the port is usually 1521.
     *  
     *  @param url Database URL
     *  @param user User name or <code>null</code> if part of url
     *  @param password Password or <code>null</code> if part of url
     *  @return RDBArchiveServer
     *  @exception on error
     *  @see #close()
     */
	public static RDBUtil connect(final String url,
	        final String user, final String password) throws Exception
    {
		Activator.getLogger().debug("RDBUtil connects to " + url);
        if (url.startsWith(JDBC_MYSQL))
            return MySQL_RDB.connect(url, user, password);
        if (url.startsWith(JDBC_ORACLE))
            return OracleRDB.connect(url, user, password);
        throw new Error("Unsupported database dialect"); //$NON-NLS-1$
    }

    /** Connect with only a url.
     *  @see #connect(String, String, String)
     */
    public static RDBUtil connect(final String url) throws Exception
    {
        return connect(url, null, null);
    }
	
	/** @return SQL connection */
	public Connection getConnection()
	{
		return connection;
	}

	/** @return Dialect info. */
	public Dialect getDialect()
	{
		return dialect;
	}
	
	/** Close the RDB connection. */
	public void close()
	{
		Activator.getLogger().debug("RDBUtil disconnects " + url);
		try
		{
			connection.close();
		}
		catch (Exception ex)
		{
			Activator.getLogger().error("Connection close error", ex); //$NON-NLS-1$
		}
	}
}
