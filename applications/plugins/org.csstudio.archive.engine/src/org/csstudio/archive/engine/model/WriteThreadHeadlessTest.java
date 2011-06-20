/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.engine.model;

import org.csstudio.apputil.test.TestProperties;
import org.csstudio.data.values.INumericMetaData;
import org.csstudio.data.values.ISeverity;
import org.csstudio.data.values.ITimestamp;
import org.csstudio.data.values.IValue;
import org.csstudio.data.values.TimestampFactory;
import org.csstudio.data.values.ValueFactory;
import org.junit.Test;

/** [Headless] JUnit write thread tests, writing from a queue with fake samples.
 *  @author Kay Kasemir
 */
public class WriteThreadHeadlessTest
{
    @SuppressWarnings("nls")
    @Test(timeout=20000)
    public void testWriteThread() throws Exception
    {
    	// Get test configuration
    	final TestProperties settings = new TestProperties();
    	final String channel = settings.getString("archive_write_channel");
    	if (channel == null)
    	{
    		System.out.println("Skipping, no name for write_channel");
    		return;
    	}
		System.out.println("Writing samples for channel " + channel);

    	// Setup buffer
        final SampleBuffer buffer = new SampleBuffer(channel, 1000);

        // Connect writer to it
        final WriteThread writer = new WriteThread();
        writer.addSampleBuffer(buffer);

        // Trigger thread to write
        writer.start(5.0, 500);

        // Add some samples
        final long seconds = TimestampFactory.now().seconds();
        final ISeverity severity = ValueFactory.createOKSeverity();
        final String status = "Test";
        final INumericMetaData meta_data =
            ValueFactory.createNumericMetaData(0, 10, 2, 8, 1, 9, 2, "Eggs");
        for (int i=0; i<10; ++i)
        {
            final ITimestamp time = TimestampFactory.createTimestamp(seconds, i);
            buffer.add(ValueFactory.createDoubleValue(time,
                            severity, status, meta_data,
                            IValue.Quality.Original,
                            new double[] { i } ));
            Thread.sleep(1);
        }

        // Wait for the thread to write all the samples
        while (buffer.getQueueSize() > 0)
            Thread.sleep(500);
        writer.shutdown();

        // Show stats
        System.out.println(buffer);
    }
}
