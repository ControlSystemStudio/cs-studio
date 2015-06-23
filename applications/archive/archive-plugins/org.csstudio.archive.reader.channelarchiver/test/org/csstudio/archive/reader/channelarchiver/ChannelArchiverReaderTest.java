/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.reader.channelarchiver;

import static org.junit.Assert.assertTrue;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;

import org.csstudio.archive.reader.ArchiveInfo;
import org.csstudio.archive.reader.ValueIterator;
import org.csstudio.archive.vtype.VTypeHelper;
import org.epics.util.time.TimeDuration;
import org.epics.util.time.Timestamp;
import org.epics.vtype.VType;
import org.junit.Test;

/** JUnit test of the ChannelArchiverReader
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ChannelArchiverReaderTest
{
    private static final double HOURS = 24.0;
    private static final String END = "2005/11/09";
    private static final String CHANNEL = "TGT_HE:Tnk_PT1166:P";
    private static final int KEY = 4805;
    final private static String URL =
        "xnds://ics-srv-web2.sns.ornl.gov/archive/cgi/ArchiveDataServer.cgi";

    /** Connect, dump basic info */
    @Test
    public void testInfo() throws Exception
    {
        final ChannelArchiverReader reader = new ChannelArchiverReader(URL);
        System.out.println(reader.getServerName());
        System.out.println(reader.getURL());
        System.out.println(reader.getDescription());
        for (final ArchiveInfo info : reader.getArchiveInfos())
            System.out.println(info);
        reader.close();
    }

    /** Locate names */
    @Test
    public void testLookup() throws Exception
    {
        final ChannelArchiverReader reader = new ChannelArchiverReader(URL);
        final String pattern = "*" + CHANNEL.substring(3);
        System.out.println("Channels with pattern '" + pattern + "':");
        final String names[] = reader.getNamesByPattern(KEY, pattern);
        for (final String name : names)
            System.out.println(name);
        reader.close();
        assertTrue(Arrays.asList(names).contains(CHANNEL));
    }

    /** Get raw samples via iterator */
    @Test
    public void testRawData() throws Exception
    {
        final ChannelArchiverReader reader = new ChannelArchiverReader(URL);
        final DateFormat parser = new SimpleDateFormat("yyyy/MM/dd");
        final Timestamp end = Timestamp.of(parser.parse(END));
        final Timestamp start = end.minus(TimeDuration.ofHours(HOURS));

        final ValueIterator values = reader.getRawValues(4805, CHANNEL, start, end);
        int count = 0;
        while (values.hasNext())
        {
            final VType sample = values.next();
            System.out.println(VTypeHelper.toString(sample));
            ++count;
        }
        values.close();
        reader.close();

        assertTrue(count > 0);
    }

    /** Get optimized samples directly as batch, not via iterator */
    @Test
    public void testOptimizedData() throws Exception
    {
        final ChannelArchiverReader reader = new ChannelArchiverReader(URL);
        final DateFormat parser = new SimpleDateFormat("yyyy/MM/dd");
        final Timestamp end = Timestamp.of(parser.parse(END));
        final Timestamp start = end.minus(TimeDuration.ofHours(24.0));

        final VType[] samples =
            reader.getSamples(4805, CHANNEL, start, end, false, 10);
        int count = 0;
        for (final VType sample : samples)
        {
            System.out.println(VTypeHelper.toString(sample));
            ++count;
        }
        reader.close();
        assertTrue(count > 0);
    }
}
