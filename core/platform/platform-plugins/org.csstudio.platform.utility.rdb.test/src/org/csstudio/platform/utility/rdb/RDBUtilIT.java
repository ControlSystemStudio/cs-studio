/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.platform.utility.rdb;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;

import java.sql.Connection;

import org.junit.Test;

/** JUnit Tests for RDBUtil
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class RDBUtilIT
{
    private void test(final String url) throws Exception
    {
        final RDBUtil rdb = RDBUtil.connect(url);
        try
        {
            final Connection connection = rdb.getConnection();
            System.out.println("Connection: " + connection);
            assertThat(connection, notNullValue());
        }
        finally
        {
            rdb.close();
        }
    }

    @Test
    public void testMySQL() throws Exception
    {
        test(TestSetup.URL_MYSQL);
    }

    @Test
    public void testReconnect() throws Exception
    {
        final RDBUtil rdb = RDBUtil.connect(TestSetup.URL, true);
        try
        {
            Connection connection = rdb.getConnection();
            assertThat(connection, notNullValue());

            // Get same instance again
            Connection connection2 = rdb.getConnection();
            assertThat(connection2, sameInstance(connection));

            // Fake an error by closing the connection on purpose
            connection.close();

            // Now get new instance, automatically re-connected
            connection2 = rdb.getConnection();
            assertThat(connection2, notNullValue());
            assertThat(connection2, not(sameInstance(connection)));
        }
        finally
        {
            rdb.close();
        }
    }
}
