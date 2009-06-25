package org.csstudio.archive.rdb.internal.test;

import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.csstudio.archive.rdb.RDBArchive;
import org.csstudio.archive.rdb.TestSetup;
import org.junit.Test;

/** Try to 'cancel' a long running query.
 *  @author kasemirk@ornl.gov
 */
@SuppressWarnings("nls")
public class CancelTest
{
    @Test
    public void testCancelLongRunningRequest() throws Exception
    {
        final RDBArchive archive =
            RDBArchive.connect(TestSetup.URL, TestSetup.USER, TestSetup.PASSWORD);
        // The database connection
        final Connection connection = archive.getRDB().getConnection();

        final PreparedStatement query =
            connection.prepareStatement("SELECT COUNT(*) FROM chan_arch.sample");

        // This thread simulates a user who pressed "cancel" in the progress
        // view after 4 seconds
        new Thread("ImpatientUser")
        {
            @Override
            public void run()
            {
                try
                {
                    Thread.sleep(4000);
                    // Note that connection.close() does NOT stop
                    // an ongoing Oracle query!
                    // Only this seems to do it:
                    query.cancel();
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }
            }
        }.start();
        
        try
        {
            final ResultSet rs = query.executeQuery();
            assertTrue(rs.next());
            System.out.println("Samples: " + rs.getInt(1));
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        finally
        {
            query.close();
            archive.close();
        }
    }
}
