/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.jms2rdb;

import java.net.InetAddress;
import java.util.Date;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.Topic;

import org.csstudio.logging.JMSLogMessage;

/** Thread that sends as many JMS messages as possible.
 *  <p>
 *  Uses CSS 'log' message format.
 *
 *  @author Kay Kasemir
 *  reviewed by Katia Danilova 08/20/08
 */
@SuppressWarnings("nls")
public class Sender implements ExceptionListener, Runnable
{
    final private Session session;
    final private MessageProducer producer;
    final private Thread thread;
    private boolean run;
    private int count;

    /** Create and start the sender
     *  @param connection
     *  @param topic_name
     *  @throws Exception
     */
    public Sender(final Connection connection, final String topic_name)
        throws Exception
    {
        connection.setExceptionListener(this);
        connection.start();
        session = connection.createSession(/* transacted */false,
                                           Session.AUTO_ACKNOWLEDGE);
        final Topic topic = session.createTopic(topic_name);
        producer = session.createProducer(topic);
        producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

        run = true;
        count = 0;
        thread = new Thread(this, "Sender");
    }

    public void start()
    {
        thread.start();
    }

    /** @see Runnable */
    @Override
    public void run()
    {
        try
        {
            final String host = InetAddress.getLocalHost().getHostName();
            final String user = System.getProperty("user.name");
            while (run)
            {
                ++count;
                final MapMessage map = session.createMapMessage();
                map.setString(JMSLogMessage.TEXT, Integer.toString(count));
                map.setString(JMSLogMessage.SEVERITY, "INFO");
                map.setString(JMSLogMessage.CREATETIME, new Date().toString());
                map.setString(JMSLogMessage.NAME, "run");
                map.setString(JMSLogMessage.CLASS, "Sender.java");
                map.setString(JMSLogMessage.APPLICATION_ID, "JMSPerfTest");
                map.setString(JMSLogMessage.HOST, host);
                map.setString(JMSLogMessage.USER, user);
                producer.send(map);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    /** Stop the sender (blocks until done)
     *  @throws Exception
     */
    public void shutdown() throws Exception
    {
        run = false;
        thread.join();
        producer.close();
        session.close();
    }

    /** @see ExceptionListener */
    @Override
    public void onException(final JMSException ex)
    {
        ex.printStackTrace();
    }

    /** @return Number of messages sent */
    public int getMessageCount()
    {
        return count;
    }
}
