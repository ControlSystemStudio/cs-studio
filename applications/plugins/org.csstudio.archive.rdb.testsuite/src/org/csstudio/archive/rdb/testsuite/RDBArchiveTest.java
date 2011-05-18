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
import static org.junit.Assert.assertTrue;

import org.csstudio.archive.rdb.ChannelConfig;
import org.csstudio.archive.rdb.RDBArchive;
import org.csstudio.archive.rdb.SampleIterator;
import org.csstudio.archive.rdb.internal.TimestampUtil;
import org.csstudio.archive.rdb.testsuite.TestSetup.TestType;
import org.csstudio.data.values.IDoubleValue;
import org.csstudio.data.values.INumericMetaData;
import org.csstudio.data.values.ISeverity;
import org.csstudio.data.values.ITimestamp;
import org.csstudio.data.values.IValue;
import org.csstudio.data.values.TimestampFactory;
import org.csstudio.data.values.ValueFactory;
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
