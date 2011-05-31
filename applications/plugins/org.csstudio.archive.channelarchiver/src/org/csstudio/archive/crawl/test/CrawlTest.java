package org.csstudio.archive.crawl.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import junit.framework.Assert;

import org.csstudio.archive.channelarchiver.ArchiveServer;
import org.csstudio.archive.crawl.BatchIterator;
import org.csstudio.archive.crawl.RawValueIterator;
import org.csstudio.archive.crawl.SpreadsheetIterator;
import org.csstudio.archive.crawl.ValueIterator;
import org.csstudio.data.values.ITimestamp;
import org.csstudio.data.values.IValue;
import org.csstudio.data.values.TimestampFactory;
import org.csstudio.testsuite.util.TestDataProvider;
import org.junit.BeforeClass;
import org.junit.Test;

/** Demo/test code for using the 'crawl' API
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class CrawlTest
{
    final static String start_stamp = "2007/02/04 00:00:00";
    final static String end_stamp = "2007/02/05 00:00:00";

    private static DateFormat parser =
        new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");


    private static org.csstudio.testsuite.util.TestDataProvider PROV;
    private static String URL;

    @BeforeClass
    public static void setUp()
    {
        try {
            PROV = TestDataProvider.getInstance("org.csstudio.archive.channelarchiver");
        } catch (final Exception e) {
            Assert.fail("Unexpected exception");
        }
        URL = (String) PROV.get("snsDataServerUrl");
    }

    @Test
    public void testBatchIterator()
    {
        try
        {
            // Connect to archive data server
            final ArchiveServer server = new ArchiveServer(URL);
            final int key = server.getArchiveKey((String) PROV.get("archiveName2"));

            final Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(parser.parse(start_stamp).getTime());
            final ITimestamp start = TimestampFactory.fromCalendar(cal);

            cal.setTimeInMillis(parser.parse(end_stamp).getTime());
            final ITimestamp end = TimestampFactory.fromCalendar(cal);
            final int count = 500;

            // Get raw samples
            final BatchIterator batch = new BatchIterator(server, key,
                                        (String) PROV.get("CrawlTest.channel01"),
                                        start, end,
                                        org.csstudio.archive.ArchiveServer.GET_RAW,
                                        new Object[] { new Integer(count) });
            // dump them
            int value_count = 0;
            for (IValue samples[] = batch.getBatch();
                 samples != null;
                 samples = batch.next())
            {
                for (final IValue value : samples)
                {
                    ++value_count;
                    //System.out.println(value);
                }
            }
            //System.out.println("Got " + value_count + " values");
            assertEquals(1440, value_count);
        }
        catch (final Exception ex)
        {
            ex.printStackTrace();
            fail("Got exception");
        }
    }

    @Test
    public void testRawIterator()
    {
        try
        {
            // Connect to archive data server
            final ArchiveServer server = new ArchiveServer(URL);
            final int key = server.getArchiveKey((String) PROV.get("archiveName2"));

            final ArchiveServer servers[] = new ArchiveServer[] { server };
            final int keys[] = new int[] { key };

            final Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(parser.parse(start_stamp).getTime());
            final ITimestamp start = TimestampFactory.fromCalendar(cal);

            cal.setTimeInMillis(parser.parse(end_stamp).getTime());
            final ITimestamp end = TimestampFactory.fromCalendar(cal);
            final int count = 1000;

            // Get raw samples
            final RawValueIterator iter = new RawValueIterator(servers, keys,
                            (String) PROV.get("CrawlTest.channel01"),
                            start, end,
                            org.csstudio.archive.ArchiveServer.GET_RAW,
                            new Object[] { new Integer(count) });
            // dump them
            int value_count = 0;
            while (iter.hasNext())
            {
                final IValue value = iter.next();
                //System.out.println(value);
                ++value_count;
            }
            //System.out.println("Got " + value_count + " values");
            assertEquals(1440, value_count);
        }
        catch (final Exception ex)
        {
            ex.printStackTrace();
            fail("Got exception");
        }
    }

    private static String formatValue(final IValue value)
    {
        if (value == null) {
            return "    --    ";
        }
        return String.format("%-20s  %-10s %-10s",
                        value.format(),
                        value.getSeverity(),
                        value.getStatus());
    }

    @Test
    public void testSpreadsheet()
    {
        try
        {
            final ArchiveServer server = new ArchiveServer(URL);
            final int key = server.getArchiveKey((String) PROV.get("archiveName2"));

            final ArchiveServer servers[] = new ArchiveServer[] { server };
            final int keys[] = new int[] { key };

            final Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(parser.parse(start_stamp).getTime());
            final ITimestamp start = TimestampFactory.fromCalendar(cal);

            cal.setTimeInMillis(parser.parse(end_stamp).getTime());
            final ITimestamp end = TimestampFactory.fromCalendar(cal);

            final int count = 10000;

            // Get two iterators for raw data
            final RawValueIterator dtl1 = new RawValueIterator(servers, keys,
                           (String) PROV.get("CrawlTest.channel01"),
                            start, end,
                            org.csstudio.archive.ArchiveServer.GET_RAW,
                            new Object[] { new Integer(count) });
            final RawValueIterator dtl2 = new RawValueIterator(servers, keys,
                            (String) PROV.get("CrawlTest.channel02"),
                            start, end,
                            org.csstudio.archive.ArchiveServer.GET_RAW,
                            new Object[] { new Integer(count) });

            // Iterate over them as a spreadsheet
            final SpreadsheetIterator sheet = new SpreadsheetIterator(
                            new ValueIterator[] { dtl1, dtl2 });

//            while (sheet.hasNext())
//            {
//                final ITimestamp time = sheet.getTime();
//                final IValue values[] = sheet.next();
                //System.out.format("%s\t%s\t%s\n",
//                                  time,
//                                  formatValue(values[0]),
//                                  formatValue(values[1]));
//            }
        }
        catch (final Exception ex)
        {
            ex.printStackTrace();
        }
    }
}
