/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.platform.utility.rdb;

import java.sql.Connection;

import org.junit.Ignore;
import org.junit.Test;

/** JUnit Tests for RDBUtil
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class RDBUtilTest
{
    /** Must adjust these for your site! */
//    private static final String URL = "jdbc:mysql://ics-web.sns.ornl.gov/alarm";
//    private static final String USER = "alarm";
//    private static final String PASSWORD = "$alarm";

    @SuppressWarnings("unused")
    private static final String URL1 =
        "jdbc:oracle:thin:@" +
        "(DESCRIPTION=" +
        " (ADDRESS_LIST=(LOAD_BALANCE=OFF)" +
        "  (ADDRESS=(PROTOCOL=TCP)(HOST=172.31.75.138)(PORT=1521))" +
        "  (ADDRESS=(PROTOCOL=TCP)(HOST=172.31.75.141)(PORT=1521))" +
        "  (ADDRESS=(PROTOCOL=TCP)(HOST=172.31.73.93) (PORT=1521))" +
        " )" +
        " (CONNECT_DATA=(SERVICE_NAME=ics_prod_lba))" +
        ")";

    private static final String URL =
        "jdbc:oracle:thin:@" +
        "(DESCRIPTION=(SOURCE_ROUTE=YES)" +
        " (ADDRESS_LIST=(LOAD_BALANCE=OFF)(FAILOVER=ON)" +
        "  (ADDRESS=(PROTOCOL=TCP)(HOST=snsapp1a.sns.ornl.gov)(PORT=1610))" +
        "  (ADDRESS=(PROTOCOL=TCP)(HOST=snsapp1b.sns.ornl.gov)(PORT=1610))" +
        " )" +
        " (ADDRESS_LIST=(LOAD_BALANCE=OFF)" +
        "  (ADDRESS=(PROTOCOL=TCP)(HOST=172.31.75.138)(PORT=1521))" +
        "  (ADDRESS=(PROTOCOL=TCP)(HOST=172.31.75.141)(PORT=1521))" +
        "  (ADDRESS=(PROTOCOL=TCP)(HOST=172.31.73.93) (PORT=1521))" +
        " )" +
        " (CONNECT_DATA=(SERVICE_NAME=ics_prod_lba))" +
        ")";

    private static final String USER = "sns_reports";
    private static final String PASSWORD = "sns";


    /** Basic connection */
    @Test
    @Ignore("Test with syso?")
    public void testConnection() throws Exception
    {
        final RDBUtil rdb = RDBUtil.connect(URL, USER, PASSWORD, true);
        final Connection connection = rdb.getConnection();
        System.out.println("Connection: " + connection);
    }


    /** Check re-connect
     *  Not really a test because it has no way to force a connection error.
     *  While running this test, one needs to stop the RDB or disconnect
     *  the network cable, see if the disconnect is noticed (after a rather
     *  long timeout).
     *  Then see if reconnection succeeds when the network cable or RDB are restored.
     */
    @Test
    @Ignore("Test with syso?")
    public void testReconnect() throws Exception
    {
        final RDBUtil rdb = RDBUtil.connect(URL, USER, PASSWORD, true);

        while (true)
        {
            try
            {
                final Connection connection = rdb.getConnection();
                System.out.println("Connection: " + connection);
            }
            catch (final Exception e)
            {
                System.out.println("Error: " + e.getMessage());
            }
            Thread.sleep(5000);
        }
    }
}
