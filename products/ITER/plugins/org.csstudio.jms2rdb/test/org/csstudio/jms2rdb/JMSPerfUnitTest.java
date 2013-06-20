/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.jms2rdb;

import static org.junit.Assert.*;

import javax.jms.Connection;

import org.csstudio.apputil.test.TestProperties;
import org.csstudio.platform.utility.jms.JMSConnectionFactory;
import org.junit.Test;

/** JUnit test of simple send/receive performance.
 *  <p>
 *  Receiver checks the sequence of received messages
 *  and will detect missing or double messages.
 *  <p>
 *  Because the start and stop aren't further coordinated,
 *  the receiver will miss some initial or final messages.
 *  <p>
 *  Test with laptop to srv02 and back: about 1000 msg/sec.
 *  Test with new imac to srv02 and back: about 4000 msg/sec.
 *
 *  @author Kay Kasemir
 *  reviewed by Katia Danilova 08/20/08
 */
@SuppressWarnings("nls")
public class JMSPerfUnitTest
{
    /** Topic.
     *  'LOG' might get logged to RDB, 'TEST' should be only for tests.
     */
    final private static String TOPIC = "TEST";

    /** Test runtime */
    final private static int SECONDS = 30;

    /** Simple read/write performance test that counts log message throughput */
    @Test
    public void perfTest() throws Exception
    {
        final TestProperties settings = new TestProperties();
        final String url = settings.getString("alarm_jms_url");

        if (url == null)
        {
            System.out.println("Skipping, no JMS URL");
            return;
        }

        // Create Receiver, then Sender
        final Connection connection = JMSConnectionFactory.connect(url);
        final Receiver receiver = new Receiver(connection, TOPIC);
        // Wait a little to allow receiver to set up
        Thread.sleep(5 * 1000);
        final Sender sender = new Sender(connection, TOPIC);
        sender.start();

        // Run for some time
        System.out.println("Runtime: " + SECONDS + " seconds");
        Thread.sleep(SECONDS * 1000);

        // Stop sender, then receiver
        sender.shutdown();
        receiver.shutdown();

        // Stats
        int count = sender.getMessageCount();
        System.out.format("Sender  : %10d messages = %10.1f msg/sec\n",
                count, ((double) count)/SECONDS);
        assertTrue(count > 1000);

        count = receiver.getMessageCount();
        System.out.format("Receiver: %10d messages = %10.1f msg/sec\n",
                count, ((double) count)/SECONDS);
        assertTrue(count > 1000);
    }
}
