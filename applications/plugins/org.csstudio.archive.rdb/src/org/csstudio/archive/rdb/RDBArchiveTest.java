package org.csstudio.archive.rdb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.csstudio.archive.rdb.TestSetup.TestType;
import org.csstudio.archive.rdb.internal.TimestampUtil;
import org.csstudio.platform.data.IDoubleValue;
import org.csstudio.platform.data.INumericMetaData;
import org.csstudio.platform.data.ISeverity;
import org.csstudio.platform.data.ITimestamp;
import org.csstudio.platform.data.IValue;
import org.csstudio.platform.data.TimestampFactory;
import org.csstudio.platform.data.ValueFactory;
import org.junit.Test;

@SuppressWarnings("nls")
public class RDBArchiveTest
{
    @Test
    public void testConnect() throws Exception
    {
        final RDBArchive archive = RDBArchive.connect(TestSetup.URL);
        assertNotNull(archive);
        archive.reconnect();
        archive.close();
    }

    @Test
    public void testChannelLookup() throws Exception
    {
        final RDBArchive archive = RDBArchive.connect(TestSetup.URL);
        
        final ChannelConfig channel = archive.getChannel(TestType.DOUBLE.getPvName());
        assertEquals(TestType.DOUBLE.getPvName(), channel.getName());
        
        final ChannelConfig[] channels = archive.findChannels("DTL_LLRF");
        for (ChannelConfig ch : channels)
        {
            System.out.println(ch);
        }
        
        archive.close();
    }

    @Test
    public void testChannelTimes() throws Exception
    {
        final RDBArchive archive = RDBArchive.connect(TestSetup.URL);
        
        final ChannelConfig channel = archive.getChannel(TestType.DOUBLE.getPvName());
        final ITimestamp last = channel.getLastTimestamp();
        System.out.println("Last:  " + last);
        final ITimestamp[] range = channel.getTimerange();
        System.out.println("Range: " + range[0] + " ... " + range[1]);
        
        archive.close();
    }

    @Test
    public void testWrite() throws Exception
    {
        final RDBArchive archive = RDBArchive.connect(TestSetup.URL);
        
        final ChannelConfig channel = archive.getChannel(TestType.DOUBLE.getPvName());
        
        final ISeverity severity = ValueFactory.createMinorSeverity();
        final String status = "Test";
        
        final INumericMetaData numeric_meta =
            ValueFactory.createNumericMetaData(-10.0, 10.0, -8.0, 8.0,
                    -9.0, 9.0, 1, "Tests");
        final ITimestamp now = TimestampFactory.now();
        for (int i=0; i<10; ++i)
        {
            final ITimestamp time =
                TimestampFactory.createTimestamp(now.seconds(), i);
            final IDoubleValue sample = ValueFactory.createDoubleValue(
                    time, severity,
                    status, numeric_meta, IValue.Quality.Original,
                    new double[] { i });
            channel.batchSample(sample);
        }
        archive.commitBatch();
        
        archive.close();
    }

    @Test
    public void testRawSamples() throws Exception
    {
        final RDBArchive archive = RDBArchive.connect(TestSetup.URL);
        
        final ChannelConfig channel = archive.getChannel(TestType.DOUBLE.getPvName());
        // Get last 5 minutes
        final ITimestamp end = channel.getLastTimestamp();
        final ITimestamp start = TimestampUtil.add(end, -60*5);
        final SampleIterator samples = channel.getSamples(start, end);
        int count = 0;
        while (samples.hasNext())
        {
            final IValue sample = samples.next();
            System.out.println(sample);
            ++count;
            if (count > 10)
                break;
        }
        assertTrue(count > 0);
        
        archive.close();
    }
}
