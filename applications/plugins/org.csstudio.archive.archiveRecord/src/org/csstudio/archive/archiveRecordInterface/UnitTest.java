/* 
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron, 
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. 
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED 
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND 
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE 
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, 
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE 
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR 
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. 
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, 
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION, 
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS 
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY 
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
 package org.csstudio.archive.archiveRecordInterface;

import java.util.Calendar;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.csstudio.archive.ArchiveInfo;
import org.csstudio.archive.ArchiveValues;
import org.csstudio.archive.NameInfo;
import org.csstudio.platform.data.ITimestamp;
import org.csstudio.platform.data.TimestampFactory;


public class UnitTest //extends TestCase
{
//	private org.csstudio.archive.ArchiveServer server;
//	private final String url = "";
//
//	protected void setUp() throws Exception
//	{
////		server = ArchiveServer.getInstance(url);
//	}
//	
//	/** Check basic server info based on the version 0 implementation. */
//	public void testInfo() throws Exception
//	{
//		// Tests are based on version 0.
//		Assert.assertEquals(0, server.getVersion());
//		
//		// These 'how' numbers could actually change, but for now that's it:
//		Assert.assertEquals(0, server.getRequestType(ArchiveServer.GET_RAW));
//		Assert.assertEquals(3, server.getRequestType(ArchiveServer.GET_PLOTBINNED));
//
//		// Basic severities
////        Assert.assertEquals("", server.getSeverity(0).getText());
////        Assert.assertTrue(server.getSeverity(0).isOK());
////        Assert.assertFalse(server.getSeverity(0).isMinor());
////        Assert.assertFalse(server.getSeverity(0).isMajor());
////        Assert.assertFalse(server.getSeverity(0).isInvalid());
//
////        Assert.assertEquals("MINOR", server.getSeverity(1).getText());
////       Assert.assertTrue(server.getSeverity(1).isMinor());
//
////        Assert.assertEquals("MAJOR", server.getSeverity(2).getText());
////        Assert.assertTrue(server.getSeverity(2).isMajor());
//        
////		Assert.assertEquals("INVALID", server.getSeverity(3).getText());
////        Assert.assertTrue(server.getSeverity(3).isInvalid());
////        Assert.assertEquals(true, server.getSeverity(3).hasValue());
////		Assert.assertEquals(true, server.getSeverity(3).statusIsText());
//        
//		// Repeat stuff
////		Assert.assertEquals("Repeat", server.getSeverity(3856).getText());
////		Assert.assertEquals(true, server.getSeverity(3856).hasValue());
////		Assert.assertEquals(false, server.getSeverity(3856).statusIsText());
//        
//		// Archive engine stuff
////		Assert.assertEquals("Disconnected", server.getSeverity(3904).getText());
////		Assert.assertEquals(false, server.getSeverity(3904).hasValue());
////		Assert.assertEquals(true, server.getSeverity(3904).statusIsText());
//	}
//
//	/** Look for some archives.
//	 * 
//	 *  Outcome depends on your server. This is for the SNS server.
//     */
//	public void testInfoArchives() throws Exception
//	{
//		ArchiveInfo archives[] = server.getArchiveInfos();
//        for (int i = 0; i < archives.length; i++)
//            if (archives[i].getName().equals("- All -"))
//            {
//    			// If we find it, it should be keyed to '1'.
//    			Assert.assertEquals(1, archives[i].getKey());
//                break;
//            }
//		// .. but it's OK to not find it on some servers.
//
//        // Same via convenience routine:
//        try
//        {
//            int key = server.getArchiveKey("- All -");
//            Assert.assertEquals(1, key);
//        }
//        catch (Exception e) {}
//	}
//
//	/** Name lookup tests.
//	 * 
//	 *  Outcome depends on your server. This is for the SNS server.
//     */
//	public void testNames() throws Exception
//	{
//		int key = server.getArchiveKey("RF");
//		final NameInfo names[] = 
//			server.getNames(key, "CCL_LLRF:FCM.*:cavAmpAvg");
//		for (int i=0; i<names.length; ++i)
//			System.out.println(names[i].toString());
//		Assert.assertEquals(4, names.length);
//	}
//
//	public void testValues() throws Exception
//	{
//		int key = server.getArchiveKey("RF");
//		String names[] = new String[] { "CCL_LLRF:FCM1:cavAmpAvg" };
//
//		// Includes some 'off' sample
//		ITimestamp start = TimestampFactory.fromCalendar(Calendar.)TimestampUtil.fromPieces(2006, 1, 18, 10, 18, 0, 0);
//		ITimestamp end = TimestampUtil.fromPieces(2006, 1, 18, 12, 0, 0, 0);
//        int request_type = server.getRequestType(ArchiveServer.GET_RAW);
//		int count = 10;
//        Object parms[] = new Object[] { new Integer(count) };
//		ArchiveValues[] ass = 
//			server.getSamples(key, names, start, end, request_type, parms);
//		for (ArchiveValues as : ass)
//		{
//			System.out.println("Channel '" + as.getChannelName() + "':");
//			Value samples[] = as.getSamples();
//			for (int i=0; i<samples.length; ++i)
//				System.out.println(samples[i]);
//		}		
//
//		// Includes some 'repeat' sample
//		start = TimestampUtil.fromPieces(2006, 1, 19, 5, 38, 0, 0);
//		end = TimestampUtil.fromPieces(2006, 1, 19, 6, 0, 0, 0);
//		count = 1000;
//        parms = new Object[] { new Integer(count) };
//		ass = server.getSamples(key, names, start, end, request_type, parms);
//		for (ArchiveValues as : ass)
//		{
//			System.out.println("Channel '" + as.getChannelName() + "':");
//			Value samples[] = as.getSamples();
//			for (int i=0; i<samples.length; ++i)
//				System.out.println(samples[i]);
//		}		
//
//		// Includes enum-type data
//		start = TimestampUtil.fromPieces(2006, 1, 1, 0, 0, 0, 0);
//		end = TimestampFactory.now();
//        names[0] = "SCL_LLRF:HPM01a:RF7_Ctl";
//		ass = server.getSamples(key, names, start, end, request_type, parms);
//		for (ArchiveValues as : ass)
//		{
//			System.out.println("Channel '" + as.getChannelName() + "':");
//			Value samples[] = as.getSamples();
//			for (int i=0; i<samples.length; ++i)
//				System.out.println(samples[i]);
//		}		
//	}
//	
//	public void testStringValues() throws Exception
//	{
//		int key = server.getArchiveKey("RF");
//		String names[] = new String[] { "CCL_HPRF:Mod1:FltLog0" };
//	
//		ITimestamp start = TimestampUtil.fromPieces(2006, 1, 18, 10, 18, 0, 0);
//		ITimestamp end = TimestampFactory.now();
//		int how = 0;
//        int count = 10;
//        Object parms[] = new Object[] { new Integer(count) };
//		ArchiveValues[] ass = 
//			server.getSamples(key, names, start, end, how, parms);
//		for (ArchiveValues as : ass)
//		{
//			System.out.println("Channel '" + as.getChannelName() + "':");
//			Value samples[] = as.getSamples();
//			for (int i=0; i<samples.length; ++i)
//				System.out.println(samples[i]);
//		}		
//	}
//	
//	protected void tearDown() throws Exception
//	{
//		server = null;
//	}
}
