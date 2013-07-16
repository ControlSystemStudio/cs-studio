/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.jms2rdb;

import java.util.logging.Level;

import javax.jms.Connection;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.Topic;

import org.csstudio.jms2rdb.rdb.RDBWriter;
import org.csstudio.platform.utility.jms.JMSConnectionFactory;

/** Thread that receives log messages and sends them to the RDB.
 *  <p>
 *  There is no further queuing in here.
 *  All messages are directly written to the RDB.
 *  <p>
 *  If messages arrive quicker than they can be written to RDB,
 *  simple test showed that ActiveMQ JMS server will queue them up
 *  for this "consumer" as long as the consumer stays connected.
 *  TODO: Limits and config. of JMS server queue unclear.
 *        Queue in here?
 *
 *  @author Kay Kasemir
 *  reviewed by Katia Danilova 08/20/08
 */
@SuppressWarnings("nls")
public class LogClientThread extends Thread
    implements ExceptionListener, MessageListener
{
    /** On JMS or RDB errors, thread will disconnect, wait, then re-connect
     *  using this delay.
     */
    private static final int RETRY_DELAY_MS = 20000;

    /** JMS Server URL */
    final private String jms_url;

    /** JMS topic */
    final private String jms_topic;

    /** RDB Server URL */
    final private String rdb_url;

    /** RDB Schema */
    final private String rdb_schema;

    /** Message filters */
    final private Filter filters[];

    /** Flag that tells thread to run or stop. */
    private volatile boolean run = true;

    /** Flag that tells thread main loop to wait. */
    private boolean do_wait;

    /** RDB Writer for log messages */
    private RDBWriter rdb_writer;

    /** Counter for received JMS messages */
    private int message_count = 0;

    /** Last JMS Message */
    private MapMessage last_message = null;

    /** Last error message or <code>null</code> */
    private String last_error = "";

    /** Constructor
     *  @param jms_url JMS server URL
     *  @param jms_topic JMS topic (or list of topics, separated by ',')
     *  @param rdb_url RDB server URL
     *  @param rdb_schema RDB schema or ""
     */
    public LogClientThread(final String jms_url, final String jms_topic,
            final String rdb_url, final String rdb_schema,
            final Filter filters[])
    {
        super("LogClientThread");
        this.jms_url = jms_url;
        this.jms_topic = jms_topic;
        this.rdb_url = rdb_url;
        this.rdb_schema = rdb_schema;
        this.filters = filters;

        for (Filter filter : filters)
            Activator.getLogger().config(filter.toString());
    }

    /** @return Number of messages received */
    public synchronized int getMessageCount()
    {
        return message_count;
    }

    /** @return Last messages received or <code>null</code> */
    public synchronized MapMessage getLastMessage()
    {
        return last_message;
    }

    /** @return Last error received or empty string */
    public synchronized String getLastError()
    {
        return last_error;
    }

    /** Connect to JMS, handle messages */
    @Override
    public void run()
    {
        while (run)
        {
            Connection jms_connection = null;
            rdb_writer = null;
            try
            {
                // First open RDB, then the JMS client that writes to RDB
                rdb_writer = new RDBWriter(rdb_url, rdb_schema);
                Activator.getLogger().log(Level.INFO, "Connected to RDB {0}", rdb_url);
                jms_connection = connectJMS();

                // Add start message
                rdb_writer.write("JMS Log Tool started");

                // Incoming JMS messages are handled in onMessage,
                // so nothing to do here but wait...
                synchronized (this)
                {
                    do_wait = true;
                    // Check some condition to please FindBugs
                    while (do_wait)
                        wait();
                }
            }
            catch (Exception ex)
            {
                synchronized (this)
                {
                    last_error = ex.getMessage();
                }
                Activator.getLogger().log(Level.WARNING, "Log thread error", ex);
            }
            finally
            {
                // Stop JMS...
                if (jms_connection != null)
                {
                    try
                    {
                        jms_connection.close();
                    }
                    catch (JMSException e)
                    {
                        Activator.getLogger().log(Level.WARNING, "JMS disconnect error", e);
                    }
                }
                // .. then the RDB used by the JMS client.
                if (rdb_writer != null)
                {
                    rdb_writer.close();
                    rdb_writer = null;
                }
            }
            // Did we wake up & close connections because of error
            // or because of requested shutdown?
            if (run)
            {   // Error. Wait a little before trying again
                try
                {
                    synchronized (this)
                    {
                        wait(RETRY_DELAY_MS);
                    }
                }
                catch (InterruptedException ex)
                {
                    // Ignore
                    ex = null;
                }
            }
        }
    }

    /** Connect to JMS server
     *  @return JMS Connection
     *  @throws JMSException on error
     */
    private Connection connectJMS() throws JMSException
    {
        final Connection connection = JMSConnectionFactory.connect(jms_url);
        connection.setExceptionListener(this);
        connection.start();
        final Session session = connection.createSession(/* transacted */false,
                                           Session.AUTO_ACKNOWLEDGE);
        // Subscribe to list of topics
        final String[] topic_names = jms_topic.split(", *");
        for (String topic_name : topic_names)
        {
            final Topic topic = session.createTopic(topic_name);
            final MessageConsumer consumer = session.createConsumer(topic);
            consumer.setMessageListener(this);

            Activator.getLogger().log(Level.CONFIG,
                    "Accepting messages for {0} at {1}",
                    new Object[] { topic_name, jms_url });
        }
        return connection;
    }

    /** Ask thread to stop. Does not block for thread to actually exit */
    public void cancel()
    {
        run = false;
        synchronized (this)
        {
            do_wait = false;
            notifyAll();
        }
    }

    /** @see JMS ExceptionListener */
    @Override
    public void onException(final JMSException ex)
    {
        Activator.getLogger().log(Level.WARNING, "JMS Exception", ex);
    }

    /** @see JMS MessageListener */
    @Override
    public void onMessage(final Message message)
    {
        try
        {
            if (message instanceof MapMessage)
            {
                final MapMessage map = (MapMessage) message;
                for (Filter fil : filters)
                    if (fil.matches(map))
                        return;
                synchronized (this)
                {
                    ++message_count;
                    last_message  = map;
                }
                rdb_writer.write(map);
            }
            else
                Activator.getLogger().log(Level.WARNING, "Received unhandled message type {0}", message.getClass().getName());
        }
        catch (Exception ex)
        {
            synchronized (this)
            {
                last_error = ex.getMessage();
            }
            Activator.getLogger().log(Level.WARNING, "Message handling error", ex);
            // Leave run == true, toggle a restart
            run = true;
            synchronized (this)
            {
                do_wait = false;
                notifyAll();
            }
        }
    }
}
