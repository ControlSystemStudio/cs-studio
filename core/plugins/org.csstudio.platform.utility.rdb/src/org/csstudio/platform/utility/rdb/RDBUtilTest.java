package org.csstudio.platform.utility.rdb;

import java.sql.Connection;

import org.junit.Test;

/** JUnit Test of automated re-connect.
 *  <p>
 *  Not really a test because it has no way to force a connection error.
 *  While running this test, one needs to stop the RDB or disconnect
 *  the network cable, see if the disconnect is noticed (after a rather
 *  long timeout).
 *  Then see if reconnection succeeds when the network cable or RDB are restored.
 *  
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class RDBUtilTest
{

    private static final String URL = "jdbc:mysql://ics-web.sns.ornl.gov/alarm";
    private static final String USER = "alarm";
    private static final String PASSWORD = "$alarm";

    @Test
    public void testGetConnection() throws Exception
    {
        final RDBUtil rdb = RDBUtil.connect(URL, USER, PASSWORD, true);

        while (true)
        {
            try
            {
                final Connection connection = rdb.getConnection();
                System.out.println("Connection: " + connection);
            }
            catch (Exception e)
            {
                System.out.println("Error: " + e.getMessage());
            }
            Thread.sleep(5000);
        }
    }
}
