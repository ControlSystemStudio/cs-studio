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
		Assert.assertEquals(0, server.getRequestType(ArchiveServer.GET_RAW));
		Assert.assertEquals(3, server.getRequestType(ArchiveServer.GET_PLOTBINNED));

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
		ArchiveInfo archives[] = server.getArchiveInfos();
        for (int i = 0; i < archives.length; i++)
            if (archives[i].getName().equals("- All -"))
            {
    			// If we find it, it should be keyed to '1'.
    			Assert.assertEquals(1, archives[i].getKey());
                break;
            }
		// .. but it's OK to not find it on some servers.

        // Same via convenience routine:
        try
        {
            int key = server.getArchiveKey("- All -");
            Assert.assertEquals(1, key);
        }
        catch (Exception ex)
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
		int key = server.getArchiveKey("RF");
		final NameInfo names[] = 
			server.getNames(key, "CCL_LLRF:FCM.*:cavAmpAvg");
		for (int i=0; i<names.length; ++i)
			System.out.println(names[i].toString());
		Assert.assertEquals(4, names.length);
	}

    /** Get some numeric values. */
	public void testValues() throws Exception
	{
		int key = server.getArchiveKey("RF");
		String names[] = new String[] { "CCL_LLRF:FCM1:cavAmpAvg" };

		// Includes some 'off' sample
        Calendar cal = Calendar.getInstance();
        cal.set(2006, 1, 18, 10, 18, 0);
		ITimestamp start = TimestampFactory.fromCalendar(cal);
        cal.set(2006, 1, 18, 12, 0, 0);
		ITimestamp end = TimestampFactory.fromCalendar(cal);
        int request_type = server.getRequestType(ArchiveServer.GET_RAW);
		int count = 10;
		ArchiveValues[] ass = 
			server.getSamples(key, names, start, end, request_type,
                            new Object[] { new Integer(count) } );
		for (ArchiveValues as : ass)
		{
			System.out.println("Channel '" + as.getChannelName() + "':");
			IValue samples[] = as.getSamples();
			for (int i=0; i<samples.length; ++i)
				System.out.println(samples[i]);
		}		

		// Includes some 'repeat' sample
        cal.set(2006, 1, 19, 5, 38, 0);
        start = TimestampFactory.fromCalendar(cal);
        cal.set(2006, 1, 19, 6, 0, 0);
		end = TimestampFactory.fromCalendar(cal);
		count = 1000;
		ass = server.getSamples(key, names, start, end, request_type,
                        new Object[] { new Integer(count) } );
		for (ArchiveValues as : ass)
		{
			System.out.println("Channel '" + as.getChannelName() + "':");
			IValue samples[] = as.getSamples();
			for (int i=0; i<samples.length; ++i)
				System.out.println(samples[i]);
		}		

		// Includes enum-type data
        cal.set(2006, 1, 1, 0, 0, 0);
		start = TimestampFactory.fromCalendar(cal);
		end = TimestampFactory.now();
        names[0] = "SCL_LLRF:HPM01a:RF7_Ctl";
		ass = server.getSamples(key, names, start, end, request_type,
                        new Object[] { new Integer(count) } );
		for (ArchiveValues as : ass)
		{
			System.out.println("Channel '" + as.getChannelName() + "':");
			IValue samples[] = as.getSamples();
			for (int i=0; i<samples.length; ++i)
				System.out.println(samples[i]);
		}		
	}
	
    /** Get some string PVs. */
	public void testStringValues() throws Exception
	{
		int key = server.getArchiveKey("RF");
		String names[] = new String[] { "CCL_HPRF:Mod1:FltLog0" };
	
        Calendar cal = Calendar.getInstance();
        cal.set(2006, 1, 18, 10, 18, 0);
		ITimestamp start = TimestampFactory.fromCalendar(cal);
		ITimestamp end = TimestampFactory.now();
		int how = 0;
        int count = 10;
		ArchiveValues[] ass = 
			server.getSamples(key, names, start, end, how,
                            new Object[] { new Integer(count) } );
		for (ArchiveValues as : ass)
		{
			System.out.println("Channel '" + as.getChannelName() + "':");
			IValue samples[] = as.getSamples();
			for (int i=0; i<samples.length; ++i)
				System.out.println(samples[i]);
		}		
	}
	
	@Override
    protected void tearDown() throws Exception
	{
		server = null;
	}
}
