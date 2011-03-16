/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.logging;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/** Log handler that sends messages to JMS
 *  <p>
 *  See {@link JMSLogWriter} for details on usage of {@link Formatter}.
 *  <p>
 *  Note that this handler needs an explicit <code>start()</code>.
 *
 *  @author Kay Kasemir
 */
public class JMSLogHandler extends Handler
{
    /** Maximum number of messages that are queued up
     *  to prevent the JMS handler from exhausting memory
     */
    final private static int MAX_CAPACITY = 100;

    /** JMS server URL */
    final private String jms_url;

    /** JMS topic */
    final private String topic;

    /** Log message queue filled by handler, read by JMSWriter task */
    final private BlockingQueue<LogRecord> records = new LinkedBlockingQueue<LogRecord>(MAX_CAPACITY);

    /** Has there been a queuing error? */
    private boolean queuing_failed = false;

    /** Log writer thread */
    private JMSLogWriter log_writer = null;

    /** Initialize
     *  @param jms_url JMS server URL
     *  @param topic JMS topic
     */
    public JMSLogHandler(final String jms_url, final String topic)
    {
        this.jms_url = jms_url;
        this.topic = topic;
    }

    /** Start background thread that handles the JMS communication.
     *  @throws IllegalStateException when already started
     */
    public void start()
    {
        if (log_writer != null)
            throw new IllegalStateException();
        log_writer = new JMSLogWriter(Messages.ApplicationID, jms_url, topic, records, getFormatter());
        log_writer.start();
    }

    /** {@inheritDoc} */
    @SuppressWarnings("nls")
    @Override
    public synchronized void publish(final LogRecord record)
    {
        if (! isLoggable(record))
            return;

        // JMS/ActiveMQ itself uses java.util.logging.
        // To prevent infinite loops, do not log messages from JMS
        // back to JMS.
        final String class_name = record.getSourceClassName();
        if (class_name != null   &&
            class_name.startsWith("org.apache.activemq"))
            return;

        boolean added = records.offer(record);
        if (added)
        {   // Message was added OK.
            // Are we recovering from a previous queuing error?
            if (queuing_failed)
            {   // Try to log that there was a queuing error
                queuing_failed =
                    !records.offer(new LogRecord(Level.WARNING, "Recovering from JMS Queuing Error"));
            }
        }
        else // Probably reached queue capacity.
            queuing_failed = true;
    }

    /** {@inheritDoc} */
    @Override
    public void flush()
    {
        // NOP
    }

    /** {@inheritDoc} */
    @Override
    public void close() throws SecurityException
    {
        if (log_writer != null)
        {
            log_writer.stop();
            log_writer = null;
        }
    }
}
