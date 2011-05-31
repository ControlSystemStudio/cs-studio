package org.csstudio.archive.channelarchiver;

import java.util.Calendar;

import junit.framework.Assert;

import org.csstudio.archive.ArchiveAccessException;
import org.csstudio.archive.ArchiveInfo;
import org.csstudio.archive.ArchiveValues;
import org.csstudio.archive.NameInfo;
import org.csstudio.data.values.ITimestamp;
import org.csstudio.data.values.IValue;
import org.csstudio.data.values.TimestampFactory;
import org.csstudio.testsuite.util.TestDataProvider;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;



/** Tests of the ChannelArchiver interface.
 *  <p>
 *  <b>Requires access to the SNS archive server!</b>
 *  @author Kay Kasemir
 *
 *  FIXME (kasemir) : remove sysos, use assertions, parameterise URL and Channels
 */
@SuppressWarnings("nls")
public class ChannelArchiverInterfaceTest
{
	private static TestDataProvider PROV;

	private static ArchiveServer SERVER;

	@BeforeClass
    public static void setUp()
	{
	    try {
	        PROV = TestDataProvider.getInstance("org.csstudio.archive.channelarchiver");
	    } catch (final Exception e) {
	        Assert.fail("Unexpected exception");
	    }

	    try {
	        final String url = (String) PROV.get("archiveServerUrl");
	        SERVER = new ArchiveServer(url);

	    } catch (final Exception e) {
	        Assert.fail("Unexpected exception");
	    }
	}


	/** Check basic server info based on the version 1 implementation. */
	@Test
	public void testInfo() throws Exception
	{
		// Tests are based on version 0.
		Assert.assertEquals(PROV.get("UnitTest.serverVersion"), SERVER.getVersion());

		// These 'how' numbers could actually change, but for now that's it:
		Assert.assertEquals(0, SERVER.getRequestCode(org.csstudio.archive.ArchiveServer.GET_RAW));
		Assert.assertEquals(3, SERVER.getRequestCode("plot-binning"));

		// Basic severities
        Assert.assertEquals("", SERVER.getSeverity(0).toString());
        Assert.assertTrue(SERVER.getSeverity(0).isOK());
        Assert.assertFalse(SERVER.getSeverity(0).isMinor());
        Assert.assertFalse(SERVER.getSeverity(0).isMajor());
        Assert.assertFalse(SERVER.getSeverity(0).isInvalid());

        Assert.assertEquals("MINOR", SERVER.getSeverity(1).toString());
        Assert.assertTrue(SERVER.getSeverity(1).isMinor());

        Assert.assertEquals("MAJOR", SERVER.getSeverity(2).toString());
        Assert.assertTrue(SERVER.getSeverity(2).isMajor());

		Assert.assertEquals("INVALID", SERVER.getSeverity(3).toString());
        Assert.assertTrue(SERVER.getSeverity(3).isInvalid());
        Assert.assertEquals(true, SERVER.getSeverity(3).hasValue());
		Assert.assertEquals(true, SERVER.getSeverity(3).statusIsText());

		// Repeat stuff
		Assert.assertEquals("Repeat", SERVER.getSeverity(3856).toString());
		Assert.assertEquals(true, SERVER.getSeverity(3856).hasValue());
		Assert.assertEquals(false, SERVER.getSeverity(3856).statusIsText());

		// Archive engine stuff
		Assert.assertEquals("Disconnected", SERVER.getSeverity(3904).toString());
		Assert.assertEquals(false, SERVER.getSeverity(3904).hasValue());
		Assert.assertEquals(true, SERVER.getSeverity(3904).statusIsText());
	}

	/** Look for some archives.
	 *
	 *  Outcome depends on your server. This is for the SNS server.
     */
	@Test
	public void testInfoArchives()
	{
		final ArchiveInfo archives[] = SERVER.getArchiveInfos();
        for (final ArchiveInfo archive : archives) {
            if (archive.getName().equals(PROV.get("archiveName1")))
            {
    			// If we find it, it should be keyed to '1'.
    			Assert.assertEquals(1, archive.getKey());
                break;
            }
		// .. but it's OK to not find it on some servers.
        }

        // Same via convenience routine:
        try
        {
            final int key = SERVER.getArchiveKey((String)PROV.get("archiveName1"));
            Assert.assertEquals(1, key);
        }
        catch (final ArchiveAccessException ex)
        {
            Assert.fail("Getting archive key for name " + PROV.get("archiveName1") +  "failed.");
        }
	}

	/** Name lookup tests.
	 *
	 *  Outcome depends on your server. This is for the SNS server.
     */
	@Test
	public void testNames()
	{
		int key = -1;
        try {
            key = SERVER.getArchiveKey((String)PROV.get("archiveName2"));
        } catch (final ArchiveAccessException e) {
            Assert.fail("Getting archive key for name " + PROV.get("archiveName2") +  "failed.");
        }
		NameInfo[] names = new NameInfo[0];
        try {
            names = SERVER.getNames(key, (String)PROV.get("UnitTest.channelWithWildcard"));
        } catch (final ArchiveAccessException e) {
            Assert.fail("Getting names for key " + key + " for name " + PROV.get("UnitTest.channelWithWildcard") +  "failed.");
        }
		//for (int i=0; i<names.length; ++i) {
        //    System.out.println(names[i].toString());
        //}
		Assert.assertEquals(PROV.get("UnitTest.numChannelsWithWildcard"), Integer.valueOf(names.length));
	}

    /** Get some numeric values.
     * @throws ArchiveAccessException */
	@Test
	public void testValues() throws ArchiveAccessException
	{
		final int key = SERVER.getArchiveKey((String) PROV.get("archiveName2"));
		final String names[] = new String[] { (String) PROV.get("UnitTest.channel01") };

		// Includes some 'off' sample
        final Calendar cal = Calendar.getInstance();
        cal.set(2006, 1, 18, 10, 18, 0);
		ITimestamp start = TimestampFactory.fromCalendar(cal);
        cal.set(2006, 1, 18, 12, 0, 0);
		ITimestamp end = TimestampFactory.fromCalendar(cal);
        final String request_type = org.csstudio.archive.ArchiveServer.GET_RAW;
		int count = 10;
		ArchiveValues[] ass =
			SERVER.getSamples(key, names, start, end, request_type,
                            new Object[] { new Integer(count) } );
		for (final ArchiveValues as : ass)
		{
//			System.out.println("Channel '" + as.getChannelName() + "':");
			final IValue samples[] = as.getSamples();
//			for (int i=0; i<samples.length; ++i) {
//                System.out.println(samples[i]);
//            }
		}

		// Includes some 'repeat' sample
        cal.set(2006, 1, 19, 5, 38, 0);
        start = TimestampFactory.fromCalendar(cal);
        cal.set(2006, 1, 19, 6, 0, 0);
		end = TimestampFactory.fromCalendar(cal);
		count = 1000;
		ass = SERVER.getSamples(key, names, start, end, request_type,
                        new Object[] { new Integer(count) } );
		for (final ArchiveValues as : ass)
		{
//			System.out.println("Channel '" + as.getChannelName() + "':");
			final IValue samples[] = as.getSamples();
//			for (int i=0; i<samples.length; ++i) {
//                System.out.println(samples[i]);
//            }
		}

		// Includes enum-type data
        cal.set(2006, 1, 1, 0, 0, 0);
		start = TimestampFactory.fromCalendar(cal);
		end = TimestampFactory.now();
        names[0] = (String) PROV.get("UnitTest.channel02");
		ass = SERVER.getSamples(key, names, start, end, request_type,
                        new Object[] { new Integer(count) } );
		for (final ArchiveValues as : ass)
		{
//			System.out.println("Channel '" + as.getChannelName() + "':");
			final IValue samples[] = as.getSamples();
//			for (int i=0; i<samples.length; ++i) {
//                System.out.println(samples[i]);
//            }
		}
	}

    /** Get some string PVs.
     * @throws ArchiveAccessException */
	@Test
	public void testStringValues() throws ArchiveAccessException
	{
		final int key = SERVER.getArchiveKey((String) PROV.get("archiveName2"));
		final String names[] = new String[] { (String) PROV.get("UnitTest.channel03") };

        final Calendar cal = Calendar.getInstance();
        cal.set(2006, 1, 18, 10, 18, 0);
		final ITimestamp start = TimestampFactory.fromCalendar(cal);
		final ITimestamp end = TimestampFactory.now();
		final String how = org.csstudio.archive.ArchiveServer.GET_RAW;
        final int count = 10;
		final ArchiveValues[] ass =
			SERVER.getSamples(key, names, start, end, how,
                            new Object[] { new Integer(count) } );
		for (final ArchiveValues as : ass)
		{
//			System.out.println("Channel '" + as.getChannelName() + "':");
			final IValue samples[] = as.getSamples();
//			for (int i=0; i<samples.length; ++i) {
//                System.out.println(samples[i]);
//            }
		}
	}

	@AfterClass
    public static void tearDown() throws Exception
	{
		SERVER = null; // FIXME (kasemir) : that doesn't make sense  - the server wouldn't notice
		               // perhaps server.stop() or server.disconnect() if any of those just would exist...
	}
}
