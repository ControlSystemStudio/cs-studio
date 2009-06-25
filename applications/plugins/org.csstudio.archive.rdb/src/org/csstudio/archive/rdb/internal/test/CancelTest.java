package org.csstudio.archive.rdb.internal.test;

import org.csstudio.archive.rdb.ChannelConfig;
import org.csstudio.archive.rdb.RDBArchive;
import org.csstudio.archive.rdb.SampleIterator;
import org.csstudio.archive.rdb.TestSetup;
import org.csstudio.platform.data.TimestampFactory;
import org.junit.Test;

/** JUnit Plug-in test (headless)
 * 
 *  Must run as plug-in because is uses RDB SCHEMA from preferences.
 *  
 *  Try to 'cancel' a long running query.
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
        

        // This thread simulates a user who pressed "cancel" in the progress
        // view after 4 seconds
        final Thread user = new Thread("ImpatientUser")
        {
            @Override
            public void run()
            {
                try
                {
                    Thread.sleep(4000);
                    System.out.println("Cancelling...");
                    archive.cancel();
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }
            }
        };
        
        try
        {
            final ChannelConfig[] channels = archive.findChannels("DTL_LLRF:IOC1:Load");
            System.out.println("Starting query...");
            user.start();
            final SampleIterator samples =
                channels[0].getSamples(TimestampFactory.createTimestamp(10, 0),
                                       TimestampFactory.now());
            while (samples.hasNext())
                System.out.println(samples.next());
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        finally
        {
            archive.close();
        }
    }
}
