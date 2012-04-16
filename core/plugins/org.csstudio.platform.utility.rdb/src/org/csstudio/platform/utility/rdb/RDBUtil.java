/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.platform.utility.rdb;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;

import org.csstudio.platform.utility.rdb.internal.MySQL_RDB;
import org.csstudio.platform.utility.rdb.internal.OracleRDB;
import org.csstudio.platform.utility.rdb.internal.PostgreSQL_RDB;

/** Obtain database connection for various RDB systems.
 *  <p>
 *  This utility supports an auto-reconnect feature to handle database
 *  timeouts: <code>getConnection()</code> will test if the connection
 *  is still active. If not, it automatically re-connects.
 *  <p>
 *  While this simplifies the code for clients that need to perform transactions
 *  every once in a while over a long run time, the connection test can be
 *  expensive for a short flurry of transactions.
 *  It can therefore be suppressed via <code>setAutoReconnect()</code>.
 *  <p>
 *  Note that versions 1.6.0 and earlier of this plugin defaulted
 *  to turning auto-commit <u>off</u>.
 *  Since 1.6.0, it uses the original JDBC default with auto-commit enabled,
 *  so code that needs transactions is supposed to disable auto-commit for
 *  the transaction, then commit or roll back, and re-enable auto-commit.
 *  <p>
 *  This change can cause problems in database dialects that consider it
 *  an error to call <code>commit</code> without specifically disabling auto-commit.
 *
 *  @author Kay Kasemir
 *  @author Xihui Chen
 *  @author Lana Abadie (PostgreSQL, autocommit)
 */
@SuppressWarnings("nls")
abstract public class RDBUtil
{
    /** Start of MySQL URL */
    private static final String JDBC_MYSQL = "jdbc:mysql://";
    private static final String JDBC_MYSQL_REPLICATION = "jdbc:mysql:replication://";

    /** Start of PostgreSQL URL */
    private static final String JDBC_POSTGRESQL = "jdbc:postgresql://";

    /** Start of Oracle URL */
    private static final String JDBC_ORACLE = "jdbc:oracle:";

    /** Database URL */
	final private String url;

    /** Database User */
	final private String user;

	/** Database Password */
	final private String password;

    /** Whether reconnect to RDB automatically in case of connection lost */
    private boolean autoReconnect;

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
        Oracle,
        /** Database that understands PostgreSQL commands */
        PostgreSQL
    }

    /** @see Dialect */
    final private Dialect dialect;

    /** Statement used to check the connection */
    private PreparedStatement test_query;

    /** Connect with only a url.
     *  @deprecated Use the version with autoReconnect: {@link #connect(String, boolean)}
     */
    @Deprecated
    public static RDBUtil connect(final String url) throws Exception
    {
        return connect(url, null, null, false);
    }

    /** Connect to the database.
     *  @deprecated Use the version with autoReconnect: {@link #connect(String, String, String, boolean)}
     */
    @Deprecated
    public static RDBUtil connect(final String url,
            final String user, final String password) throws Exception
    {
        return connect(url, user, password, false);
    }

    /** Connect with only a url.
     *  @param url URL
     *  @param autoReconnect Handle reconnect?
     *  @see #connect(String, String, String, Boolean)
     */
    public static RDBUtil connect(final String url, final boolean autoReconnect) throws Exception
    {
        return connect(url, null, null, autoReconnect);
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
     *  @param user User name or <code>null</code> if part of URL
     *  @param password Password or <code>null</code> if part of URL
     *  @param autoReconnect If true, reconnect to RDB automatically
     *                       in case of lost connection
     *  @return RDBUtil
     *  @throws Exception on error
     *  @see #close()
     */
    public static RDBUtil connect(final String url,
            final String user, final String password, final boolean autoReconnect) throws Exception
    {
        Activator.getLogger().log(Level.FINE, "RDBUtil connects to {0}", url);
        if (url.startsWith(JDBC_MYSQL) || url.startsWith(JDBC_MYSQL_REPLICATION))
            return new MySQL_RDB(url, user, password, autoReconnect);
        else if (url.startsWith(JDBC_ORACLE))
            return new OracleRDB(url, user, password, autoReconnect);
        else if (url.startsWith(JDBC_POSTGRESQL))
        	return new PostgreSQL_RDB(url, user, password, autoReconnect);
        else
            throw new Exception("Unsupported database dialect " + url);
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
    	// Auto-commit is the default, but just to make sure:
        connection.setAutoCommit(true);
        if(autoReconnect) {
            test_query = connection.prepareStatement(getConnectionTestQuery());
        }
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

    /** Temporarily disable or later re-enable the auto-reconnect feature.
     *  @param auto_reconnect <code>false</code> to disable, <code>true</code> to re-enable
     *  @throws Exception if this RDBUtil was not created with auto-reconnect support
     */
    public void setAutoReconnect(final boolean auto_reconnect) throws Exception
    {
        if (test_query == null)
            throw new IllegalStateException("Auto-reconnect support not available");
        autoReconnect = auto_reconnect;
    }

    /** Get the JDBC connection.
	 *  This method will try to return a connection that's
	 *  valid after network errors or RDB timeouts by checking
	 *  the validity of the connection and re-connecting if
	 *  necessary.
	 *  <p>
	 *  It cannot really distinguish between a connection that
	 *  was closed on purpose, or one that happens to be closed
	 *  because of a previous network error that caused this
	 *  very routine to close the connection and then attempt
	 *  a re-connect - which failed and left the connection as null.
	 *
	 *  @return SQL connection. In auto-reconnect mode this should never be
	 *          <code>null</code>: Either a valid connection or an exception.
	 *  @throws Exception when necessary re-connection fails
	 */
	public Connection getConnection() throws Exception
	{
	    if (autoReconnect)
	    {
	        if ((connection != null) && isConnected())
                return connection; // All OK
	        Activator.getLogger().log(Level.FINE, "Connection Lost! Reconnect to {0}", url);
	        if (connection != null)
                close();
            connection = do_connect(url, user, password);
            connection.setAutoCommit(false);
            test_query = connection.prepareStatement(getConnectionTestQuery());
	    }
        return connection;
	}

	/** Close the RDB connection. */
	public void close()
	{
        Activator.getLogger().log(Level.FINE, "RDBUtil closes {0}", url);
		try
		{
		    if (autoReconnect)
                test_query.close();
			connection.close();
		}
		catch (final SQLException ex)
		{
			//simply discard this exception since in most cases,
			//this method is called due to connection lost.
		}
		finally
		{
            test_query = null;
            connection = null;
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
        catch (final SQLException e)
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
