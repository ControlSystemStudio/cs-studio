/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.logging.test;

import static org.junit.Assert.*;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import org.csstudio.logging.LogFormatDetail;
import org.csstudio.logging.JMSLogWriter;
import org.csstudio.logging.LogFormatter;
import org.junit.Test;

/** JUnit Demo of the {@link JMSLogWriter}
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class JMSLogWriterDemo
{
    @Test
    public void testJMSWriteThread() throws Exception
    {
        final BlockingQueue<LogRecord> records = new LinkedBlockingQueue<LogRecord>();
        final JMSLogWriter writer = new JMSLogWriter("JMSWriteThreadTest", DemoSetup.url, DemoSetup.topic, records, new LogFormatter(LogFormatDetail.HIGH));
        writer.start();
        for (int i=0; i<10; ++i)
        {
            if (!records.offer(new LogRecord(Level.INFO, "Test " + (i+1))))
                fail("Cannot add to queue");
            Thread.sleep(1000);
        }
        // Restart writer, use it again
        writer.stop();
        writer.start();
        for (int i=0; i<10; ++i)
        {
            if (!records.offer(new LogRecord(Level.INFO, "Test " + (i+1))))
                fail("Cannot add to queue");
            Thread.sleep(1000);
        }
    }
}
