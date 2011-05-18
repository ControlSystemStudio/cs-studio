/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.rdb.testsuite;

import org.csstudio.archive.rdb.ChannelConfig;
import org.csstudio.archive.rdb.RDBArchive;
import org.csstudio.data.values.INumericMetaData;
import org.csstudio.data.values.ISeverity;
import org.csstudio.data.values.ITimestamp;
import org.csstudio.data.values.IValue;
import org.csstudio.data.values.TimestampFactory;
import org.csstudio.data.values.ValueFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/** JUnit tests for writing samples w/ same time stamp,
 *  causing error in batch submission.
 *  With RDBArchiveImpl.debug_batch=true flag this
 *  causes a dump of the batched samples.
 *  Still no way to see which sample exactly caused the error.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class RDBArchiveBatchTest
{
    private static final String URL = "jdbc:oracle:thin:chan_arch/sns@172.31.75.138:1521:prod1";

    private static final String CHANNEL = "DemoChannel";

    /** Archive to use */
    private static RDBArchive archive;

    final private ISeverity severity = ValueFactory.createMinorSeverity();
    final static private String status = "Test";

    final private INumericMetaData numeric_meta =
        ValueFactory.createNumericMetaData(-10.0, 10.0, -8.0, 8.0, -9.0, 9.0, 1, "Tests");

    /** @return a dummy value for given time stamp */
    private IValue createValue(final ITimestamp time, final long count)
    {
        return ValueFactory.createDoubleValue(time, severity,
              status, numeric_meta, IValue.Quality.Original,
              new double[] { count });
    }

    @BeforeClass
    public static void connect() throws Exception
    {
        archive = RDBArchive.connect(URL);
    }

    @AfterClass
    public static void disconnect()
    {
        archive.close();
    }

    /** Helper for writing PV of given type for some seconds */
    @Test
    public void testBatching() throws Exception
    {
        System.out.println("Debug mode: " +
                           archive.debug_batch);
        final ChannelConfig channel = archive.createChannel(CHANNEL);

        ITimestamp time = TimestampFactory.now();
        long seconds = time.seconds();
        long nanoseconds = time.nanoseconds();

        System.out.println("Writing " + channel.getName());
        // Write a few samples
        for (int i=1; i<3; ++i)
        {
            // Get next timestamp
            ++nanoseconds;
            if (nanoseconds >= 1000000000)
            {
                ++seconds;
                nanoseconds = 0;
            }
            time = TimestampFactory.createTimestamp(seconds, nanoseconds);
            channel.batchSample(createValue(time, i));
        }
        // Another with same time stamp
        channel.batchSample(createValue(time, 3));
        for (int i=3; i<6; ++i)
        {
            // Get next timestamp
            ++nanoseconds;
            if (nanoseconds >= 1000000000)
            {
                ++seconds;
                nanoseconds = 0;
            }
            time = TimestampFactory.createTimestamp(seconds, nanoseconds);
            channel.batchSample(createValue(time, i));
        }
        // Final commit
        archive.commitBatch();
    }
}
