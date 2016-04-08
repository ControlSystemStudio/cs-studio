/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.engine.model;

import java.time.Instant;

import org.csstudio.apputil.test.TestProperties;
import org.csstudio.archive.vtype.ArchiveVNumber;
import org.diirt.vtype.AlarmSeverity;
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
            System.out.println("Skipping, no name for archive_write_channel");
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
        final long seconds = Instant.now().getEpochSecond();
        final AlarmSeverity severity = AlarmSeverity.NONE;
        final String status = "Test";
        for (int i=0; i<10; ++i)
        {
            final Instant time = Instant.ofEpochSecond(seconds, i);
            buffer.add(new ArchiveVNumber(time, severity, status, TestHelper.display, Double.valueOf(i)));
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
