package org.csstudio.archive.engine.model;


import org.csstudio.apputil.test.TestProperties;
import org.csstudio.archive.rdb.RDBArchive;
import org.csstudio.platform.data.INumericMetaData;
import org.csstudio.platform.data.ISeverity;
import org.csstudio.platform.data.ITimestamp;
import org.csstudio.platform.data.IValue;
import org.csstudio.platform.data.TimestampFactory;
import org.csstudio.platform.data.ValueFactory;
import org.junit.Test;

/** [Headless] JUnit write thread tests, writing from a queue with fake samples.
 *  @author Kay Kasemir
 */
public class WriteThreadHeadlessTest
{
    @SuppressWarnings("nls")
    @Test
    public void testWriteThread() throws Exception
    {
    	// Get test configuration
    	final TestProperties settings = new TestProperties();
    	final String url = settings.getString("archive_rdb_url");
    	if (url == null)
    	{
    		System.out.println("Skipping, no archive test settings");
    		return;
    	}
    	final String user = settings.getString("archive_rdb_user");
    	final String password = settings.getString("archive_rdb_password");
    	final String channel = settings.getString("archive_write_channel");
    	if (channel == null)
    	{
    		System.out.println("Skipping, no name for write_channel");
    		return;
    	}

    	// Setup buffer
        final SampleBuffer buffer = new SampleBuffer(channel, 1000);

        // Connect writer to it
        final RDBArchive archive = RDBArchive.connect(url, user, password);
        final WriteThread writer = new WriteThread(archive);
        writer.addSampleBuffer(buffer);

        // Trigger thread to write
        writer.start(5.0, 500);
        
        // Add some samples
        final long seconds = TimestampFactory.now().seconds();
        final ISeverity severity = ValueFactory.createOKSeverity();
        final String status = "Test";
        final INumericMetaData meta_data = 
            ValueFactory.createNumericMetaData(0, 10, 2, 8, 1, 9, 2, "Eggs");
        for (int i=0; i<1; ++i)
        {
            final ITimestamp time = TimestampFactory.createTimestamp(seconds, i);
            buffer.add(ValueFactory.createDoubleValue(time,
                            severity, status, meta_data,
                            IValue.Quality.Original,
                            new double[] { Double.NaN } ));
            Thread.sleep(1);
        }
        
        // Wait for the thread to write all the samples
        while (buffer.getQueueSize() > 0)
            Thread.sleep(500);
        writer.shutdown();
        
        archive.close();
        
        // Show stats
        System.out.println(buffer);
    }
}
