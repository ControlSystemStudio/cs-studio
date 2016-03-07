/*******************************************************************************
 * Copyright (c) 2013 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.platform.utility.rdb;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

import java.sql.Connection;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

/** JUnit test of the {@link RDBCache}
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class RDBCacheUnitTest
{
    final private static String CACHE_NAME = "Test";

    /** @return Thread that runs the RDBCache timer or <code>null</code> */
    public Thread findThread()
    {
        final Set<Thread> threads = Thread.getAllStackTraces().keySet();
        for (Thread thread : threads)
            if (thread.getName().equals(CACHE_NAME))
                return thread;
        return null;
    }

    @Test
    public void testCache() throws Exception
    {
        // Create cache
        final RDBCache cache =
            new RDBCache(CACHE_NAME, TestSetup.URL_MYSQL, null, null,
                        1, TimeUnit.SECONDS);
        // Get initial connection
        final Connection connection = cache.getConnection();
        System.out.println(connection);
        assertThat(connection, notNullValue());

        // Check time-out thread
        // NOTE:
        // This is beyond testing the basic API.
        // End user should not care how the RDBCache
        // does its work, but for asserting that it works
        // as supposed right now, we check it
        Thread thread = findThread();
        assertThat(thread, nullValue());

        // When released, the cleanup thread should run
        cache.releaseConnection();
        thread = findThread();
        assertThat(thread, notNullValue());

        // Get same back
        Connection connection2 = cache.getConnection();
        System.out.println(connection2);
        assertThat(connection2, sameInstance(connection));
        cache.releaseConnection();

        // .. once more within timeout
        Thread.sleep(500);
        connection2 = cache.getConnection();
        System.out.println(connection2);
        assertThat(connection2, sameInstance(connection));
        cache.releaseConnection();

        // Allow to time out
        Thread.sleep(1500);
        // Thread should be gone
        Thread thread2 = findThread();
        System.out.println(thread2);
        assertThat(thread2, nullValue());
        // .. but we still get a connection. This time, it's a new one.
        connection2 = cache.getConnection();
        System.out.println(connection2);
        assertThat(connection2, not(sameInstance(connection)));
        cache.releaseConnection();

        // Assert that timer thread quits once more
        Thread.sleep(1500);
        thread2 = findThread();
        System.out.println(thread2);
        assertThat(thread2, nullValue());
    }
}
