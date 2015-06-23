/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.platform.utility.rdb;

import java.sql.Connection;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import org.csstudio.platform.utility.rdb.RDBUtil.Dialect;
import org.csstudio.platform.utility.rdb.internal.RDBImpl;

/** Database (RDB) connection cache
 *
 *  <p>Provide RDB connection that is kept open for some time
 *  after its last access.
 *  After a timeout, the connection is closed.
 *  On next access, it is re-opened.
 *
 *  <p>In Sept. 2013, <code>releaseConnection()</code>
 *  was added, and must be called.
 *  Before, the connection would simply be closed after the timeout,
 *  even if the user of the connection was still within an operation.
 *  Now, the user of the connection needs to release it, and only
 *  then will the timeout start.
 *
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class RDBCache
{
    /** Name of this cache (used for timer thread) */
    final private String name;

    /** RDB Implementation (Oracle, MySQL, PostgreSQL) */
    final private RDBImpl impl;

    /** Database URL */
    final private String url;

    /** Database User */
    final private String user;

    /** Database Password */
    final private String password;

    /** Duration for keeping the connection cached */
    final private long milli_duration;

    /** Timer used to handle expirations */
    private Timer timeout = null;

    /** Timer task that expires the RDBUtil */
    private TimerTask expire = null;

    /** Connection to the SQL server */
    private Connection connection;

    /** Initialize
     *
     *  <p>URL format depends on the database dialect.
     *
     *  <p>For MySQL resp. Oracle, the formats are:
     *  <pre>
     *     jdbc:mysql://[host]:[port]/[database]?user=[user]&password=[password]
     *     jdbc:oracle:thin:[user]/[password]@//[host]:[port]/[database]
     *  </pre>
     *
     *  For Oracle, the port is usually 1521.
     *
     *  @param name Name of this cache (used for timer thread)
     *  @param url Database URL
     *  @param user User name or <code>null</code> if part of URL
     *  @param password Password or <code>null</code> if part of URL
     *  @param duration Duration for keeping a connection cached
     *  @param units Units of the duration
     *  @throws Exception on error
     */
    public RDBCache(final String name,
            final String url,
            final String user,
            final String password,
            final long duration,
            final TimeUnit units) throws Exception
    {
        this.name = name;
        impl = RDBUtil.getRDBImpl(url);
        this.url = url;
        this.user = user;
        this.password = password;
        milli_duration = units.toMillis(duration);
    }

    /** @return Dialect info. */
    public Dialect getDialect()
    {
        return impl.getDialect();
    }

    /** Obtain database connection.
     *
     *  <p>This may be a cached connection,
     *  or a new one is created after the cached connection
     *  has expired
     *
     *  @return JDBC {@link Connection}
     *  @throws Exception on error connecting to the RDB
     */
    public synchronized Connection getConnection() throws Exception
    {
        if (expire != null)
            expire.cancel();
        if (connection == null)
        {
            Activator.getLogger().log(Level.FINE, toString() + " connecting");
            connection = impl.connect(url, user, password);
        }
        return connection;
    }

    /** Release the connection.
     *  Starts an expiration timer to close
     *  the connection unless it's used again within the timeout.
     */
    public synchronized void releaseConnection()
    {
        if (expire != null)
            expire.cancel();
        expire = new TimerTask()
        {
            @Override
            public void run()
            {
                handleExpiration();
            }
        };
        if (timeout == null)
            timeout = new Timer(name, true);
        timeout.schedule(expire, milli_duration);
    }

    /** Close RDB, no longer cache it */
    private synchronized void handleExpiration()
    {
        if (connection == null)
            return;
        Activator.getLogger().log(Level.FINE, toString() + " disconnecting");
        try
        {
            connection.close();
        }
        catch (Throwable ex)
        {
            // Ignore, closing anyway
        }
        connection = null;
        timeout.cancel();
        timeout = null;
    }

    /** @return String representation for debugging */
    @Override
    public String toString()
    {
        return "RDB Cache '" + name + "' for " + impl.getDialect();
    }
}
