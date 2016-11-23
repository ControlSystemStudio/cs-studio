/*******************************************************************************
 * Copyright (c) 2015 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.reader.rdb;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.platform.utility.rdb.RDBUtil;
import org.csstudio.platform.utility.rdb.RDBUtil.Dialect;

/** RDB Connection cache.
 *
 *  <p>RDBArchiveReaders tend to be created in bursts as data for all channels
 *  in a plot is updated.
 *  This helper caches the connection so that only the first reader will
 *  actually connect. Other 'concurrent' readers re-use the same connection,
 *  and finally the connection is closed as all readers release it.
 *
 *  <p>Connections are marked read-only which helps at least MySQL.
 *
 *  @author Kay Kasemir
 */
public class ConnectionCache
{
    private static final Logger logger = Logger.getLogger(ConnectionCache.class.getName());

    /** Connection identifier */
    private static class ID
    {
        private final String url, user, password;

        ID(final String url, final String user, final String password)
        {
            this.url = Objects.requireNonNull(url);
            this.user = Objects.requireNonNull(user);
            this.password = password;
        }

        @Override
        public boolean equals(final Object obj)
        {
            if (! (obj instanceof ID))
                return false;
            final ID other = (ID) obj;
            return url.equals(other.url)   &&
                   user.equals(other.user) &&
                   Objects.equals(password, other.password);
        }

        @Override
        public int hashCode() {
            return Objects.hash(url,user,password);
        }
    }

    /** Cached RDB connection with reference count */
    public static class Entry
    {
        private final ID id;
        private final AtomicInteger references = new AtomicInteger(1);
        private final RDBUtil rdb;

        Entry(final ID id, final RDBUtil rdb)
        {
            this.id = id;
            this.rdb = rdb;
        }

        /** @return JDBC connection, MUST NOT BE CLOSED
         *  @throws Exception
         */
        public Connection getConnection() throws Exception
        {
            return rdb.getConnection();
        }

        /** @return RDB Dialect */
        public Dialect getDialect()
        {
            return rdb.getDialect();
        }
    }

    /** Cache */
    // Expecting only very few entries, so list & linear search good enough
    private final static List<Entry> cache = new ArrayList<>();

    /** Find entry
     *  @param id ID
     *  @return Existing entry with added reference or <code>null</code>
     */
    private static Entry find(final ID id)
    {
        for (Entry entry : cache)
            if (entry.id.equals(id))
            {
                entry.references.incrementAndGet();
                return entry;
            }
        return null;
    }

    /** Get a cached RDB connection
     *  @param url Database URL
     *  @param user .. user
     *  @param password .. password
     *  @return {@link Entry}
     *  @throws Exception on error
     *  @see #release(Entry)
     */
    public static Entry get(final String url, final String user, final String password) throws Exception
    {
        final ID id = new ID(url, user, password);
        synchronized (cache)
        {
            Entry entry = find(id);
            if (entry == null)
            {
                logger.log(Level.FINE, "Connecting to {0}", url);
                entry = new Entry(id, RDBUtil.connect(url, user, password, false));
                // Read-only allows MySQL to use load balancing
                entry.getConnection().setReadOnly(true);
                // Avoid caching for PostgreSQL
                if(entry.getDialect() != RDBUtil.Dialect.PostgreSQL)
                {
                    cache.add(entry);
                }
            }
            return entry;
        }
    }

    /** Release cache entry when no longer used
     *  @param entry Entry to release
     */
    public static void release(final Entry entry)
    {
        synchronized (cache)
        {
            if (entry.references.decrementAndGet() > 0)
                return; // Still in use
            cache.remove(entry);
        }
        entry.rdb.close();
        logger.log(Level.FINE, "Closed {0}", entry.id.url);
    }

    /**
     * Clear cache content
     */
    public static void clean()
    {
        synchronized (cache)
        {
            cache.clear();
        }
    }
}
