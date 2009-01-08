package org.csstudio.platform.utility.rdb;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.csstudio.platform.utility.rdb.internal.MySQL_RDB;
import org.csstudio.platform.utility.rdb.internal.OracleRDB;

/** Obtain database connection for various RDB systems.
 *  @author Kay Kasemir
 *  @author Xihui Chen
 */
@SuppressWarnings("nls")
abstract public class RDBUtil
{
    /** Start of MySQL URL */
    private static final String JDBC_MYSQL = "jdbc:mysql://";

    /** Start of Oracle URL */
    private static final String JDBC_ORACLE = "jdbc:oracle:";

    /** Database URL */
	final private String url;
	
    /** Database User */
	final private String user;
    
	/** Database Password */
	final private String password;
	
    /** Whether reconnect to RDB automatically in case of connection lost */
    final private boolean autoReconnect;

    /** Connection to the SQL server */
	private Connection connection;
	
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

    /** Statement used to check the connection */
    private PreparedStatement test_query;
    
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
     *  @return RDBUtil
     *  @throws Exception on error
     *  @see #close()
     *  @deprecated Use the version with autoReconnect: {@link #connect(String, String, String, boolean)}
     */
    public static RDBUtil connect(final String url,
            final String user, final String password) throws Exception
    {
    	Activator.getLogger().debug("RDBUtil connects to " + url);
        if (url.startsWith(JDBC_MYSQL))
            return new MySQL_RDB(url, user, password, false);
        if (url.startsWith(JDBC_ORACLE))
            return new OracleRDB(url, user, password, false);
        throw new Error("Unsupported database dialect");
    }

    /** Connect with only a url.
     *  @see #connect(String, String, String)
     *  @deprecated Use the version with autoReconnect: {@link #connect(String, boolean)}
     */
    public static RDBUtil connect(final String url) throws Exception
    {
        return connect(url, null, null);
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
     *  @param autoReconnect If true, reconnect to RDB automatically 
     *  in case of connection lost
     *  @return RDBUtil
     *  @throws Exception on error
     *  @see #close()   
     */
    public static RDBUtil connect(final String url,
            final String user, final String password, final boolean autoReconnect) throws Exception
    {
    	Activator.getLogger().debug("RDBUtil connects to " + url);
        if (url.startsWith(JDBC_MYSQL))
            return new MySQL_RDB(url, user, password, autoReconnect);
        if (url.startsWith(JDBC_ORACLE))
            return new OracleRDB(url, user, password, autoReconnect);
        throw new Error("Unsupported database dialect");
    }

    /** Connect with only a url.
     *  @see #connect(String, String, String, Boolean)  
     */
    public static RDBUtil connect(final String url, final boolean autoReconnect) throws Exception
    {
        return connect(url, null, null, autoReconnect);
    }
    
    
    
    /** Constructor for derived classes.
     *  @param url Database URL
     *  @param user ... user
     *  @param password ... password
     *  @param dialect
     *  @throws Exception on error
     *  @see #connect(String, String, String)
     */
    protected RDBUtil(final String url, final String user, final String password,
                      final Dialect dialect, final boolean autoReconnect) throws Exception
    {
    	this.url = url;
    	this.user = user;
    	this.password = password;
    	this.autoReconnect = autoReconnect;
    	this.dialect = dialect;
    	this.connection = do_connect(url, user, password);
        connection.setAutoCommit(false);
        if(autoReconnect)
        	test_query = connection.prepareStatement(getConnectionTestQuery());
    }

    /** @return Dialect info. */
    public Dialect getDialect()
    {
    	return dialect;
    }

    /** Derived class must implement to create the database connection.
     *  @param url RDB URL
     *  @param user User name or <code>null</code> if part of url
     *  @param password Password or <code>null</code> if part of url
     *  @return JDBC connection
     *  @throws Exception on error
     */
    abstract protected Connection do_connect(final String url,
            final String user, final String password) throws Exception;

    /** Get the JDBC connection.
	 *  This method will try to return a connection that's
	 *  valid after network errors or RDB timeouts by checking
	 *  the validity of the connection and re-connecting if
	 *  necessary.
	 *  It will <u>not</u> re-open a connection that was
	 *  specifically closed by calling <code>close()</code>
	 *  because that would indicate a logical error in the code.
	 *  @return SQL connection
	 *  @throws Exception when necessary re-connection fails or
	 *          when called on a closed connection
	 */
	public Connection getConnection() throws Exception
	{
	    if(autoReconnect) {
			if (connection.isClosed())
		        throw new Exception("Connection " + url + " was closed");
	        if (!isConnected())
	        {
	            Activator.getLogger().debug(
	                    "Connection Lost! Reconnect to " + url);
	            close();
	            connection = do_connect(url, user, password);
	            connection.setAutoCommit(false);
	            test_query = connection.prepareStatement(getConnectionTestQuery());
	        }
	    }
        return connection;
        
	}

	/** Close the RDB connection. */
	public void close()
	{
		Activator.getLogger().debug("RDBUtil closes " + url);
		
		try
		{	if(autoReconnect) {
		    	test_query.close();
		    	test_query = null;
			}
			connection.close();
			connection = null;
		}
		catch (SQLException ex)
		{
			//simply discard this exception since in most cases,
			//this method is called due to connection lost.
		}
	}

	/** Determine if the connection is still usable by executing a simple
	 *  statement.
	 *  @return <code>true</code> if connection still OK
	 *  @see #getConnectionTestQuery()
	 */
    private boolean isConnected()
    {
        try
        {
            test_query.execute();
        }
        catch (SQLException e)
        {
            return false;
        }
        return true;
    }
    
    /** Derived classes must implement this to provide a statement that's
     *  suitable for testing the connection state.
     *  @return SQL for statement that gives a cheap way of testing the
     *          connection state
     */
    abstract protected String getConnectionTestQuery();

    /** @return String representation for debugging */
    @Override
    public String toString()
    {
        return getClass().getName() + " for " + url;
    }
}
