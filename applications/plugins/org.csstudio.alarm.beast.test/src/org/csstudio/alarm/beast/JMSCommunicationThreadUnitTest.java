/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import javax.jms.MessageProducer;
import javax.jms.Session;
import org.csstudio.apputil.test.TestProperties;
import org.junit.Test;

/** JUnit test of the {@link JMSCommunicationThread}
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class JMSCommunicationThreadUnitTest
{
    /** Demo communicator that sends text messages */
    private static class TestCommunicator extends JMSCommunicationThread
    {
        private MessageProducer producer;

        private int counter = 0;

        public TestCommunicator(final String url)
        {
            super(url);
        }

        @Override
        protected void createProducersAndConsumers() throws Exception
        {
            producer = createProducer("TEST");
        }

        @Override
        protected void closeProducersAndConsumers() throws Exception
        {
            producer.close();
        }

        @Override
        protected void communicate(final Session session) throws Exception
        {
            super.communicate(session);
            producer.send(session.createTextMessage("Hello " + (++counter)));
        }
    };

    @Test
    public void testCommunicationThreadStartRunStop() throws Exception
    {
        final TestProperties settings = new TestProperties();
        final String url = settings.getString("alarm_jms_url");
        if (url == null)
        {
            System.out.println("No test URL, skipping test");
            return;
        }

        final JMSCommunicationThread communicator = new TestCommunicator(url);
        System.out.println(communicator.getJMSServerName());
        assertFalse(communicator.isConnected());

        // Start & wait for connection
        communicator.start();
        for (int i=0; !communicator.isConnected()  &&   i<20; ++i)
            Thread.sleep(100);
        System.out.println(communicator.getJMSServerName());
        assertTrue(communicator.isConnected());

        final int seconds = settings.getInteger("alarm_jms_connection_test_seconds", 0);
        System.out.println("Monitoring for " + seconds + ": Manually disconnect network cable, stop JMS server, ...");
        for (int s=0; s<seconds; ++s)
        {
            Thread.sleep(1000);
            System.out.println(communicator.getJMSServerName());
        }

        // Stop & wait for connection to end
        communicator.stop();
        for (int i=0; communicator.isConnected()  &&   i<20; ++i)
            Thread.sleep(100);
        System.out.println(communicator.getJMSServerName());
        assertFalse(communicator.isConnected());
    }
}
