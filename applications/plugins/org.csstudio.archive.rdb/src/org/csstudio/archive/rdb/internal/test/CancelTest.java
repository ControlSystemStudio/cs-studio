package org.csstudio.archive.rdb.internal.test;

import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.csstudio.archive.rdb.RDBArchive;
import org.csstudio.archive.rdb.TestSetup;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/** Try to 'cancel' a long running query.
 *  @author kasemirk@ornl.gov
 */
@SuppressWarnings("nls")
public class CancelTest
{
    private static RDBArchive archive;
    private PreparedStatement query;

    @BeforeClass
    public static void connect() throws Exception
    {
        archive = RDBArchive.connect(TestSetup.URL, TestSetup.USER, TestSetup.PASSWORD);
    }
    
    @AfterClass
    public static void disconnect()
    {
        archive.close();
    }

    @Test
    public void testCancelLongRunningRequest() throws Exception
    {
        // The database connection
        final Connection connection = archive.getRDB().getConnection();
        
        // This RDB request would normally run in a background thread,
        // so that the GUI stays responsive while we wait for the
        // database.
        // One would probably use an Eclipse "Job" instead
        // of a plain Java "Thread" because that way we get
        // a progress monitor that's connected to the Eclipse
        // Progress View where users can see active jobs, request
        // to cancel them etc.
        // In this test, we use a fake progress monitor:
        final IProgressMonitor progress = new NullProgressMonitor();

        // Generate a query that could take a long time...
        progress.beginTask("Getting sample count", IProgressMonitor.UNKNOWN);
        query = connection.prepareStatement("SELECT COUNT(*) FROM chan_arch.sample");

        // We will be stuck executing the query.
        // Create background thread to check if the user tried to cancel.
        final StatementCancelThread cancel_check =
            new StatementCancelThread("CancelCheck", progress, query);
        cancel_check.start();

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
                    progress.setCanceled(true);
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
            cancel_check.end();
            progress.beginTask("Analysing result", IProgressMonitor.UNKNOWN);
            assertTrue(rs.next());
            System.out.println("Samples: " + rs.getInt(1));
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        query.close();
        progress.done();
    }
}
