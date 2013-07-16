/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.jms2rdb;

import static org.junit.Assert.assertTrue;

import org.csstudio.apputil.test.TestProperties;
import org.csstudio.jms2rdb.rdb.RDBWriter;
import org.junit.Test;

/** JUnit test of simple RDB 'write' performance.
 *  <p>
 *  Log messages have 10 properties;
 *  3 in message table, 7 as message content.
 *  One message insert really means:
 *  SELECT new message id
 *  INSERT message row
 *  bulk INSERT of 7 message_content rows
 *
 *  Using 'batched' inserts for the properties.
 *
 *  Local or networked MySQL: about 300 msg/sec after update to 'auto increment'.
 *  SNS Oracle 'devl': about 90 msg/sec.
 *
 *  For a similar 'read' test, see org.csstudio.sns.msghist
 *
 *  @author Kay Kasemir
 *  reviewed by Katia Danilova 08/20/08
 */
@SuppressWarnings("nls")
public class RDBPerfUnitTest
{
    private static final String MSG_LOG_URL = "msg_log_url";

    /** Test runtime */
    final private static int SECONDS = 30;

    @Test
    public void perfTest() throws Exception
    {
        final TestProperties settings = new TestProperties();
        final String url = settings.getString(MSG_LOG_URL);
        final String schema = settings.getString("msg_log_schema");
        if (url == null)
        {
            System.out.println("Skipping test, need " + MSG_LOG_URL);
            return;
        }

        final RDBWriter rdb_writer = new RDBWriter(url, schema);

        // Run for some time
        System.out.println("URL    : " + url);
        System.out.println("Runtime: " + SECONDS + " seconds");

        final long end = System.currentTimeMillis() + SECONDS*1000;
        int count = 0;
        while (System.currentTimeMillis() < end)
        {
            ++count;
            rdb_writer.write(Integer.toString(count));
        }
        rdb_writer.close();

        // Stats
        System.out.format("Wrote %d messages = %.1f msg/sec\n",
                count, ((double) count)/SECONDS);
        assertTrue(count > 1000);
    }
}
