/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.logging;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

import org.csstudio.java.time.TimestampFormats;

/** Log output formatter based on {@link SimpleFormatter}
 *  but one-line summary, maybe followed by exception stack
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class LogFormatter extends Formatter
{
    /** Level of detail */
    final private LogFormatDetail detail;

    /** Date format */
    final private DateTimeFormatter date_format =  TimestampFormats.MILLI_FORMAT;

    /** As in SimpleFormatter, re-use Date to minimize memory allocations */
    final private Date date = new Date();

    /** Initialize
     *  @param detail Level of detail
     */
    public LogFormatter(final LogFormatDetail detail)
    {
        this.detail = detail;
    }

    /** Thread-save format
     *  {@inheritDoc}
     */
    @Override
    public String format(final LogRecord record)
    {
        final StringBuilder sb = new StringBuilder();

        // Date/time
        synchronized (this)
        {
            date.setTime(record.getMillis());
            sb.append(date_format.format(Instant.ofEpochMilli(record.getMillis())));
        }

        // Level
        sb.append(" ");
        sb.append(record.getLevel().getLocalizedName());

        if (detail == LogFormatDetail.HIGH)
        {
            sb.append(" [");
            sb.append(getThreadName(record.getThreadID()));
            sb.append("]");

            // Class, method
            if (record.getSourceClassName() != null)
            {
                sb.append(" ");
                sb.append(record.getSourceClassName());
            }
            if (record.getSourceMethodName() != null)
            {
                sb.append(" (");
                sb.append(record.getSourceMethodName());
                sb.append(")");
            }
        }

        // Message
        sb.append(" - ");
        sb.append(formatMessage(record));
        if (detail == LogFormatDetail.HIGH)
        {
            // Stack trace
            final Throwable thrown = record.getThrown();
            if (thrown != null)
            {
                try
                {
                    sb.append("\n");
                    final StringWriter sw = new StringWriter();
                    final PrintWriter pw = new PrintWriter(sw);
                    thrown.printStackTrace(pw);
                    pw.close();
                    sb.append(sw.toString());
                }
                catch (Exception ex)
                {   // Cannot dump detail of logged exception?
                    // Log just the class name.
                    sb.append(" (");
                    sb.append(thrown.getClass().getName());
                    sb.append(")");
                }
            }
        }
        sb.append("\n");

        return sb.toString();
    }

    /** Get thread name for tread ID.
     *
     *  For the time being, this is useless because it shows a log-specific thread ID,
     *  not the real thread ID.
     *
     *  @param id Thread ID
     *  @return Thread name
     */
    private String getThreadName(final long id)
    {
//        // Get 'root' thread
//        ThreadGroup group = Thread.currentThread().getThreadGroup();
//        ThreadGroup parent = group.getParent();
//        while (parent != null)
//        {
//            group = parent;
//            parent = group.getParent();
//        }
//
//        // Get all threads, guessing how many there are, trying until we get all
//        int count = 5, actual;
//        Thread threads[];
//        do
//        {
//            count *= 2;
//            threads = new Thread[count];
//            actual = group.enumerate(threads);
//        }
//        while (actual > count);
//
//        for (int i=0; i<actual; ++i)
//        {
//            System.out.println(threads[i].getName() + " (" + threads[i].getId() + ")");
//            if (threads[i].getId() == id)
//                return threads[i].getName();
//        }
        return "Thread " + id;
    }
}
