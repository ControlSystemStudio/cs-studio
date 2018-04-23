/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.msghist;

import static org.junit.Assert.assertTrue;

import java.time.format.DateTimeFormatter;
import java.util.Calendar;

import org.csstudio.alarm.beast.msghist.model.Message;
import org.csstudio.alarm.beast.msghist.model.MessagePropertyFilter;
import org.csstudio.alarm.beast.msghist.rdb.MessageRDB;
import org.csstudio.apputil.time.BenchmarkTimer;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.Test;

/** JUnit test of LogRDB, gives basic 'read' performance.
 *  <p>
 *  Networked MySQL: >500 msg/sec
 *  SNS Oracle 'prod': ~45 msg/sec.
 *  <p>
 *  With MySQL, at one time it was faster after
 *    CREATE INDEX message_content_message_ids ON message_content (message_id);
 *  <p>
 *  With single filters for properties in the MESSAGE table,
 *  read performance is roughly the same.
 *  For properties in MESSAGE_COLUMN, it can be slow, bad or downright
 *  terrible.
 *  <p>
 *  Oracle performance degrades when more msgs are in the RDB
 *  (this was for ~2000). An index on DATUM might help.
 *  <p>
 *  For similar 'write' test, see org.csstudio.sns.jms2rdb
 *
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class MessageRDBIT
{
    /** URL for RDB that holds log messages */
    final public static String URL =
        "jdbc:oracle:thin:@(DESCRIPTION=(LOAD_BALANCE=OFF)(FAILOVER=ON)(ADDRESS=(PROTOCOL=TCP)(HOST=snsapp1a.sns.ornl.gov)(PORT=1610))(ADDRESS=(PROTOCOL=TCP)(HOST=snsapp1b.sns.ornl.gov)(PORT=1610))(CONNECT_DATA=(SERVICE_NAME=ics_prod_lba)))";
    final public static String USER = "sns_reports";
    final public static String PASSWORD = "sns";
    /** Database schema (Set to "" if not used) */
    final public static String SCHEMA = "MSG_LOG";

    /** Days to read in this test */
    private static final int DAYS_TO_READ = 1;

    /** Basic read with filter */
    @Test
    public void testLogRDB() throws Exception
    {
        final MessageRDB log_rdb = new MessageRDB(URL, USER, PASSWORD, SCHEMA);

        final Calendar end = Calendar.getInstance();
        final Calendar start = (Calendar) end.clone();
        start.add(Calendar.DATE, -DAYS_TO_READ);

        final MessagePropertyFilter filters[] = new MessagePropertyFilter[]
        {
              new MessagePropertyFilter("TYPE", "log"),
//              new MessagePropertyFilter("TYPE", "alarm"),
//              new MessagePropertyFilter("SEVERITY", "%"),
//              new MessagePropertyFilter("TEXT", "%19%"),
        };

        final BenchmarkTimer timer = new BenchmarkTimer();
        final Message messages[] = log_rdb.getMessages(
              new NullProgressMonitor(), start, end, filters, 50000,  DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        timer.stop();

        for (Message message : messages)
        {
            System.out.println(message);
            System.out.println("----------");
        }

        System.out.format("Read %d messages; %.1f msg/second\n",
                messages.length, messages.length / timer.getSeconds());
        assertTrue("Got some messages", messages.length > 0);
    }
}
