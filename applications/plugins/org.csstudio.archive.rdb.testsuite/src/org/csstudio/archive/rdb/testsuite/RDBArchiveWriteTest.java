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
import org.csstudio.archive.rdb.testsuite.TestSetup.TestType;
import org.csstudio.data.values.IEnumeratedMetaData;
import org.csstudio.data.values.INumericMetaData;
import org.csstudio.data.values.ISeverity;
import org.csstudio.data.values.ITimestamp;
import org.csstudio.data.values.IValue;
import org.csstudio.data.values.TimestampFactory;
import org.csstudio.data.values.ValueFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/** JUnit tests for writing to archive
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class RDBArchiveWriteTest
{
    /** Duration of the 'main' test in seconds */
    final private static long runtime_secs = 30;

    /** Archive to use */
    private static RDBArchive archive;

    final private static ISeverity severity = ValueFactory.createMinorSeverity();
    final private static String status = "Test";

    final private INumericMetaData numeric_meta =
        ValueFactory.createNumericMetaData(-10.0, 10.0, -8.0, 8.0, -9.0, 9.0, 1, "Tests");

    final private IEnumeratedMetaData enum_meta =
        ValueFactory.createEnumeratedMetaData(new String []
            { "One State", "Two State", "Red State", "Blue State"});

    /** @return a dummy value for given time stamp */
    private IValue createValue(final TestType type,
            final ITimestamp time, final long count)
    {
        switch (type)
        {
        case DOUBLE:
            return ValueFactory.createDoubleValue(time, severity,
                  status, numeric_meta, IValue.Quality.Original,
                  new double[] { count });
        case LONG:
            return ValueFactory.createLongValue(time, severity,
              status, numeric_meta, IValue.Quality.Original,
              new long[] { count });
        case ENUM:
            return ValueFactory.createEnumeratedValue(time, severity,
                status, enum_meta, IValue.Quality.Original,
                new int[] { (int)count });
        case ARRAY:
            // Array data is much slower than scalars:
            // A test where scalars allowed ~7000 inserts/sec
            // only gate ~150 100-element arrays per second
            // (which is of course 15.000 elements/sec seen another way...)
            final double data[] = new double[10];
            for (int i=0; i<data.length; ++i)
                data[i] = count+i;
            return ValueFactory.createDoubleValue(time, severity,
                    status, numeric_meta, IValue.Quality.Original, data);
        case STRING:
        default:
            return ValueFactory.createStringValue(time, severity, status,
                  IValue.Quality.Original, new String[]
                  { "Value " + count });
        }
    }

    private IValue createDisconnected(final ITimestamp time)
    {
        return ValueFactory.createStringValue(time,
                ValueFactory.createInvalidSeverity(),
                "Disconnected",
              IValue.Quality.Original, new String[] { "Disconnected" });
    }

    @BeforeClass
    public static void connect() throws Exception
    {
        archive = RDBArchive.connect(TestSetup.URL);
    }

    @AfterClass
    public static void disconnect()
    {
        archive.close();
    }

    /** Helper for writing PV of given type for some seconds */
    private void write(final TestType type, final long runtime) throws Exception
    {
        final ChannelConfig channel = archive.createChannel(type.getPvName());

        ITimestamp time = TimestampFactory.now();
        long seconds = time.seconds();
        long nanoseconds = time.nanoseconds();

        final long start = System.currentTimeMillis();
        final long end = start + (long) (1000 * runtime);
        long count = 0;
        System.out.println("Writing " + type.name() + " '" + type.getPvName()
                + "' for " + runtime + " seconds...");
        // Start with a special 'disconnected' sample
        channel.batchSample(createDisconnected(time));
        // Get next timestamp
        ++nanoseconds;
        if (nanoseconds >= 1000000000)
        {
            ++seconds;
            nanoseconds = 0;
        }
        time = TimestampFactory.createTimestamp(seconds, nanoseconds);
        while (System.currentTimeMillis() < end)
        {
            channel.batchSample(createValue(type, time, count));
            ++count;
            // Commit after N samples where added
            if ((count % 500) == 0)
                archive.commitBatch();
            // Get next timestamp
            ++nanoseconds;
            if (nanoseconds >= 1000000000)
            {
                ++seconds;
                nanoseconds = 0;
            }
            time = TimestampFactory.createTimestamp(seconds, nanoseconds);
        }
        // Final commit
        archive.commitBatch();

        System.out.println(type.getPvName() + ":");
        System.out.println(count + " values in " + runtime + " seconds");
        System.out.println(" ==> " + count / runtime + " vals/sec");
    }

    @Test
    public void writeTypes() throws Exception
    {
        // Write a little of everything
        for (TestType type : TestType.values())
            write(type, 5);
        // Then write the 'main' type for a longer time to get average speed
        write(TestType.DOUBLE, runtime_secs);
    }
}
