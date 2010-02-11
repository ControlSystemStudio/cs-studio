package org.csstudio.archivereader.channelarchiver;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.csstudio.archivereader.ArchiveInfo;
import org.csstudio.archivereader.ValueIterator;
import org.csstudio.platform.data.IMetaData;
import org.csstudio.platform.data.ITimestamp;
import org.csstudio.platform.data.IValue;
import org.csstudio.platform.data.TimestampFactory;
import org.junit.Test;

/** JUnit test of the ChannelArchiverReader
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ChannelArchiverReaderTest
{
    final private static String URL =
        "xnds://ics-srv-web2.sns.ornl.gov/archive/cgi/ArchiveDataServer.cgi";
    @Test
    public void testChannelArchiverReader() throws Exception
    {
        // Connect, dump basic info
        final ChannelArchiverReader reader = new ChannelArchiverReader(URL);
        
        System.out.println(reader.getServerName());
        System.out.println(reader.getURL());
        System.out.println(reader.getDescription());
        for (ArchiveInfo info : reader.getArchiveInfos())
            System.out.println(info);
        
        // Locate names
        final String names[] = reader.getNamesByPattern(4600, "CCL_LLRF:IOC?:Load");
        for (String name : names)
            System.out.println(name);   
        
        // Get Values
        final DateFormat parser = new SimpleDateFormat("yyyy/MM/dd");
        final ITimestamp end = TimestampFactory.fromMillisecs(parser.parse("2009/06/29").getTime());
        final ITimestamp start = TimestampFactory.fromDouble(end.toDouble() - 60*60*0.5); // 0.5 hours
  
        System.out.println("Get one batch of samples directly:");
        IValue[] samples = reader.getSamples(4600, "CCL_LLRF:IOC1:Load",
                start, end, false, 10);
        for (IValue sample : samples)
            System.out.println(sample);

        System.out.println("Use ValueIterator:");
        final ValueIterator values = reader.getRawValues(4600, "CCL_LLRF:IOC1:Load",
                start, end);
        IMetaData meta = null;
        while (values.hasNext())
        {
            IValue value = values.next();
            System.out.println(value);
            if (meta == null)
                meta = value.getMetaData();
        }
        values.close();
        System.out.println("Meta data: " + meta);
        
        
        reader.close();
    }
}
