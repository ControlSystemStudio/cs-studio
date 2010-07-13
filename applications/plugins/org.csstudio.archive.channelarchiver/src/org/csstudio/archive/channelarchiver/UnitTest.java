package org.csstudio.archive.channelarchiver;

import java.util.Calendar;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.csstudio.archive.ArchiveInfo;
import org.csstudio.archive.ArchiveValues;
import org.csstudio.archive.NameInfo;
import org.csstudio.platform.data.ITimestamp;
import org.csstudio.platform.data.IValue;
import org.csstudio.platform.data.TimestampFactory;

/** Tests of the ChannelArchiver interface.
 *  <p>
 *  <b>Requires access to the SNS archive server!</b>
 *  @author Kay Kasemir
 *
 *  FIXME (bknerr) : remove sysos, use assertions, parameterize URL and Channel
 */
@SuppressWarnings("nls")
public class UnitTest extends TestCase
{
	private ArchiveServer server;
	private final String url =
        "http://ics-srv-web2.sns.ornl.gov/archive/cgi/ArchiveDataServer.cgi";

	@Override
    protected void setUp() throws Exception
	{
		server = new ArchiveServer(url);
	}

	/** Check basic server info based on the version 1 implementation. */
	public void testInfo() throws Exception
	{
		// Tests are based on version 0.
		Assert.assertEquals(1, server.getVersion());

		// These 'how' numbers could actually change, but for now that's it:
		Assert.assertEquals(0, server.getRequestCode(org.csstudio.archive.ArchiveServer.GET_RAW));
		Assert.assertEquals(3, server.getRequestCode("plot-binning"));

		// Basic severities
        Assert.assertEquals("", server.getSeverity(0).toString());
        Assert.assertTrue(server.getSeverity(0).isOK());
        Assert.assertFalse(server.getSeverity(0).isMinor());
        Assert.assertFalse(server.getSeverity(0).isMajor());
        Assert.assertFalse(server.getSeverity(0).isInvalid());

        Assert.assertEquals("MINOR", server.getSeverity(1).toString());
        Assert.assertTrue(server.getSeverity(1).isMinor());

        Assert.assertEquals("MAJOR", server.getSeverity(2).toString());
        Assert.assertTrue(server.getSeverity(2).isMajor());

		Assert.assertEquals("INVALID", server.getSeverity(3).toString());
        Assert.assertTrue(server.getSeverity(3).isInvalid());
        Assert.assertEquals(true, server.getSeverity(3).hasValue());
		Assert.assertEquals(true, server.getSeverity(3).statusIsText());

		// Repeat stuff
		Assert.assertEquals("Repeat", server.getSeverity(3856).toString());
		Assert.assertEquals(true, server.getSeverity(3856).hasValue());
		Assert.assertEquals(false, server.getSeverity(3856).statusIsText());

		// Archive engine stuff
		Assert.assertEquals("Disconnected", server.getSeverity(3904).toString());
		Assert.assertEquals(false, server.getSeverity(3904).hasValue());
		Assert.assertEquals(true, server.getSeverity(3904).statusIsText());
	}

	/** Look for some archives.
	 *
	 *  Outcome depends on your server. This is for the SNS server.
     */
	public void testInfoArchives() throws Exception
	{
		final ArchiveInfo archives[] = server.getArchiveInfos();
        for (final ArchiveInfo archive : archives) {
            if (archive.getName().equals("- All -"))
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
            final int key = server.getArchiveKey("- All -");
            Assert.assertEquals(1, key);
        }
        catch (final Exception ex)
        {
            ex.printStackTrace();
        }
	}

	/** Name lookup tests.
	 *
	 *  Outcome depends on your server. This is for the SNS server.
     */
	public void testNames() throws Exception
	{
		final int key = server.getArchiveKey("RF");
		final NameInfo names[] =
			server.getNames(key, "CCL_LLRF:FCM.*:cavAmpAvg");
		for (int i=0; i<names.length; ++i) {
            System.out.println(names[i].toString());
        }
		Assert.assertEquals(4, names.length);
	}

    /** Get some numeric values. */
	public void testValues() throws Exception
	{
		final int key = server.getArchiveKey("RF");
		final String names[] = new String[] { "CCL_LLRF:FCM1:cavAmpAvg" };

		// Includes some 'off' sample
        final Calendar cal = Calendar.getInstance();
        cal.set(2006, 1, 18, 10, 18, 0);
		ITimestamp start = TimestampFactory.fromCalendar(cal);
        cal.set(2006, 1, 18, 12, 0, 0);
		ITimestamp end = TimestampFactory.fromCalendar(cal);
        final String request_type = org.csstudio.archive.ArchiveServer.GET_RAW;
		int count = 10;
		ArchiveValues[] ass =
			server.getSamples(key, names, start, end, request_type,
                            new Object[] { new Integer(count) } );
		for (final ArchiveValues as : ass)
		{
			System.out.println("Channel '" + as.getChannelName() + "':");
			final IValue samples[] = as.getSamples();
			for (int i=0; i<samples.length; ++i) {
                System.out.println(samples[i]);
            }
		}

		// Includes some 'repeat' sample
        cal.set(2006, 1, 19, 5, 38, 0);
        start = TimestampFactory.fromCalendar(cal);
        cal.set(2006, 1, 19, 6, 0, 0);
		end = TimestampFactory.fromCalendar(cal);
		count = 1000;
		ass = server.getSamples(key, names, start, end, request_type,
                        new Object[] { new Integer(count) } );
		for (final ArchiveValues as : ass)
		{
			System.out.println("Channel '" + as.getChannelName() + "':");
			final IValue samples[] = as.getSamples();
			for (int i=0; i<samples.length; ++i) {
                System.out.println(samples[i]);
            }
		}

		// Includes enum-type data
        cal.set(2006, 1, 1, 0, 0, 0);
		start = TimestampFactory.fromCalendar(cal);
		end = TimestampFactory.now();
        names[0] = "SCL_LLRF:HPM01a:RF7_Ctl";
		ass = server.getSamples(key, names, start, end, request_type,
                        new Object[] { new Integer(count) } );
		for (final ArchiveValues as : ass)
		{
			System.out.println("Channel '" + as.getChannelName() + "':");
			final IValue samples[] = as.getSamples();
			for (int i=0; i<samples.length; ++i) {
                System.out.println(samples[i]);
            }
		}
	}

    /** Get some string PVs. */
	public void testStringValues() throws Exception
	{
		final int key = server.getArchiveKey("RF");
		final String names[] = new String[] { "CCL_HPRF:Mod1:FltLog0" };

        final Calendar cal = Calendar.getInstance();
        cal.set(2006, 1, 18, 10, 18, 0);
		final ITimestamp start = TimestampFactory.fromCalendar(cal);
		final ITimestamp end = TimestampFactory.now();
		final String how = org.csstudio.archive.ArchiveServer.GET_RAW;
        final int count = 10;
		final ArchiveValues[] ass =
			server.getSamples(key, names, start, end, how,
                            new Object[] { new Integer(count) } );
		for (final ArchiveValues as : ass)
		{
			System.out.println("Channel '" + as.getChannelName() + "':");
			final IValue samples[] = as.getSamples();
			for (int i=0; i<samples.length; ++i) {
                System.out.println(samples[i]);
            }
		}
	}

	@Override
    protected void tearDown() throws Exception
	{
		server = null;
	}
}
