/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.rdb.testsuite;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;

import org.csstudio.archive.rdb.ChannelConfig;
import org.csstudio.archive.rdb.RDBArchive;
import org.csstudio.archive.rdb.SampleIterator;
import org.csstudio.archive.rdb.testsuite.TestSetup.TestType;
import org.csstudio.data.values.ITimestamp;
import org.csstudio.data.values.IValue;
import org.csstudio.data.values.TimestampFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/** Read-only archive tests.
 *  <p>
 *  Outcome depends on what's in the data base,
 *  so unclear how to check if all works.
 *
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class RDBArchiveReadTest
{
	private static RDBArchive archive;

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
	public void getChannel() throws Exception
	{
		final ChannelConfig channel = archive.getChannel(TestType.DOUBLE.getPvName());
		assertEquals(TestType.DOUBLE.getPvName(), channel.getName());
		System.out.println(channel);
	}

	@Test
	public void findChannelsWithTimerange() throws Exception
	{
		final ChannelConfig channels[] = archive.findChannels("fr.d");
		assertNotNull(channels);
		for (final ChannelConfig channel : channels)
		{
			// This should now be a cached lookup.
			final ChannelConfig same_channel = archive.getChannel(channel.getName());
			assertSame(channel, same_channel);

			final ITimestamp last = channel.getLastTimestamp();
			if (last != null) {
                System.out.println(channel.getName() + ": ... " + last);
            } else {
                System.out.println(channel.getName() + ": no samples");
            }
		}
	}

	/** @param pv_name
	 *  @return Raw data SampleIterator
	 */
    private SampleIterator getRawSampleIterator(final String pv_name) throws Exception
    {
        final ChannelConfig channel = archive.getChannel(pv_name);
        // Use last 10 days for start...end time
        final ITimestamp end = channel.getLastTimestamp();
        final Calendar cal = end.toCalendar();
        cal.add(Calendar.DAY_OF_MONTH, -10);
        final ITimestamp start = TimestampFactory.fromCalendar(cal);
        // Get iterators
        final SampleIterator samples = channel.getSamples(start, end);
        return samples;
    }

	@Test
	public void getRawSamples() throws Exception
	{
		final SampleIterator samples = getRawSampleIterator(TestType.DOUBLE.getPvName());
		long count = 0;
		while (samples.hasNext())
		{
			final IValue sample = samples.next();
			if (!sample.getSeverity().hasValue()) {
                System.out.println("Strictly speaking, this sample has no 'value':");
            }
			System.out.println(sample + " (" + sample.getClass().getName() + ")");
			++count;
			if (count > 20) {
                break;
            }
		}
		assertTrue("Some Samples", count > 0);
	}

	@Test
    public void testReconnect() throws Exception
    {
	    // Debug into this to see if most fields and subfields
	    // of 'archive' are set to null with proper close()/dispose()...
	    archive.reconnect();
        findChannelsWithTimerange();
        getRawSamples();
    }

	@Test
    public void testArrays() throws Exception
    {
        final ChannelConfig channel = archive.getChannel(TestType.ARRAY.getPvName());
        assertNotNull(channel);
        // Get last 10 minutes...
        final ITimestamp end = channel.getLastTimestamp();
        final ITimestamp start = TimestampFactory.createTimestamp(end.seconds()-60*10, 0);
        final SampleIterator samples = channel.getSamples(start, end);
        int count = 0;
        while (samples.hasNext())
        {
            System.out.println(samples.next());
            ++count;
            if (count > 10) {
                break;
            }
        }
    }

	@Test
    public void benchmark() throws Exception
    {
        // Write a little of everything
        for (final TestType type : TestType.values()) {
            benchmark(type, 5);
        }
    }

	/** @param type Test PV to use
	 *  @param runtime Test run time in seconds
	 *  @throws Exception on error
	 */
	private void benchmark(final TestType type, final int runtime) throws Exception
    {
	    final long start = System.currentTimeMillis();
        final long end = start + (1000 * runtime);
        final SampleIterator samples = getRawSampleIterator(type.getPvName());
        long count = 0;
        while (samples.hasNext() && (System.currentTimeMillis() < end))
        {
            final IValue sample = samples.next();
            assertNotNull(sample);
            ++count;
        }
        assertTrue("Some Samples", count > 0);
        final double actual = (System.currentTimeMillis() - start) / 1000.0;
        System.out.println(type.getPvName() + ":");
        System.out.println(count + " values in " + actual + " seconds");
        System.out.println(" ==> " + count / actual + " vals/sec\n");
    }
}
