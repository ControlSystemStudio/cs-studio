/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast;

import java.util.logging.Level;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.Topic;

import org.csstudio.platform.utility.jms.JMSConnectionFactory;
import org.csstudio.platform.utility.jms.JMSConnectionListener;

/** Base for creating a JMS Communicator:
 *  Thread that handles the connection
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
abstract public class JMSCommunicationThread
{
    /** Delay used in worker to wait for work */
    final protected static int WORKER_DELAY = 500;

    /** JMS Server URL */
    final private String url;

    /** Thread that handles the connection && communication */
    final private Thread thread;

    /** Server name, <code>null</code> if not connected */
    private String jms_server = null;

    /** 'run' flag to thread */
    private volatile boolean run = true;

    /** JMS Connection */
    private Connection connection;

    /** JMS Session */
    private Session session;

    /** Initialize
     *  @param url JMS Server URL
     *  @throws NullPointerException for <code>null</code> URL
     */
    public JMSCommunicationThread(final String url)
    {
        if (url == null)
            throw new NullPointerException("JMS URL must not be null");
        this.url = url;
        thread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                thread_run();
            }
        }, "JMS Communicator");
        thread.setDaemon(true);
    }

    /** @return <code>true</code> when connected */
    public synchronized boolean isConnected()
    {
        return jms_server != null;
    }

    /** @return Name of JMS server or some text that indicates
     *          disconnected state. For information, not to determine
     *          exact connection state.
     */
    public synchronized String getJMSServerName()
    {
        return jms_server == null ? Messages.NoJMSConnection : jms_server;
    }

    /** Start the communication thread
     *  @exception IllegalThreadStateException  if the thread was already
     *               started.
     */
    public void start() throws IllegalThreadStateException
    {
        thread.start();
    }

    /** Stop the communication thread.
     *  Must be called to release resources.
     */
    public void stop()
    {
        // Inform thread to quit
        synchronized (this)
        {   // 'run' is volatile, but setting it inside sync' avoids FindBugs warning
            run = false;
            notifyAll();
        }
        // Not actually waiting for the thread to exit
    }

    /** Create a producer.
     *  Derived class can use this to create one or more producers,
     *  sending MapMessages to them in the communicator thread.
     *  @param topic_name Name of topic for the new producer
     *  @return MessageProducer
     *  @throws JMSException on error
     */
    protected MessageProducer createProducer(final String topic_name) throws JMSException
    {
        final Topic topic = session.createTopic(topic_name);
        final MessageProducer producer = session.createProducer(topic);
        producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
        return producer;
    }

    /** Create a consumer.
     *  @param topic_name Name of topic for the new consumer
     *  @return MessageProducer
     *  @throws JMSException on error
     */
    protected MessageConsumer createConsumer(final String topic_name) throws JMSException
    {
        final Topic topic = session.createTopic(topic_name);
        final MessageConsumer consumer = session.createConsumer(topic);
        return consumer;
    }


    /** Create JMS producers and consumers.
     *  To be implemented by derived classes.
     *  @throws Exception on error
     */
    abstract protected void createProducersAndConsumers() throws Exception;

    /** Close previously created JMS producers and consumers.
     *  To be implemented by derived classes.
     *  @see #createProducersAndConsumers()
     *  @throws Exception on error
     */
    abstract protected void closeProducersAndConsumers() throws Exception;

    /** Create empty map message on the communicator's session
     *  @return MapMessage
     *  @throws JMSException on error
     */
    protected synchronized MapMessage createMapMessage() throws JMSException
    {
        return session.createMapMessage();
    }

    /** Perform communication.
     *  To be implemented by derived classes.
     *  Default simply waits a short time for
     *  a possible request to 'stop'.
     *  @param session JMS Session
     *  @throws Exception on error
     */
    protected void communicate(final Session session) throws Exception
    {
        synchronized (this)
        {
            try
            {
                wait(WORKER_DELAY);
            }
            catch (InterruptedException ex)
            {
                // Ignored
            }
        }
    }

    /** 'Runnable' for the thread */
    private void thread_run()
    {
        try
        {
            connect();
        }
        catch (Exception ex)
        {   // Error in connect: Quit
            Activator.getLogger().log(Level.SEVERE, "JMS connection error", ex);
            return;
        }
        synchronized (this)
        {
            // Use URL as server name.
            // Better name might come from connection listener
            jms_server = url;
        }
        while (run)
        {
            try
            {
                communicate(session);
            }
            catch (Exception ex)
            {
                Activator.getLogger().log(Level.WARNING, "JMS communication error", ex);
            }
        }
        disconnect();
        synchronized (this)
        {
            jms_server = null;
        }
    }

    /** Connect to JMS
     *  @throws Exception on error
     */
    private void connect() throws Exception
    {
        connection = JMSConnectionFactory.connect(url);
        // Try to update JMS server info via connection listener
        JMSConnectionFactory.addListener(connection, new JMSConnectionListener()
        {
            @Override
            public void linkUp(final String server)
            {
                synchronized (this)
                {
                    jms_server = server;
                }
            }

            @Override
            public void linkDown()
            {
                synchronized (this)
                {
                    jms_server = null;
                }
            }
        });
        // Log exceptions
        connection.setExceptionListener(new ExceptionListener()
        {
            @Override
            public void onException(final JMSException ex)
            {
                Activator.getLogger().log(Level.SEVERE, "JMS Exception", ex);
            }
        });
        try
        {
            // When server is unavailable, we'll hang in here
            connection.start();
        }
        catch (JMSException ex)
        {
            // Not an error if we already gave up
            if (run == false)
                return;
            throw ex;
        }
        session = connection.createSession(/* transacted */ false,
                                           Session.AUTO_ACKNOWLEDGE);
        createProducersAndConsumers();
    }

    /** Disconnect from JMS */
    private void disconnect()
    {
        try
        {
            closeProducersAndConsumers();
        }
        catch (Exception ex)
        {
            Activator.getLogger().log(Level.WARNING, "JMS shutdown error", ex);
        }
        try
        {
            session.close();
        }
        catch (JMSException ex)
        {
            Activator.getLogger().log(Level.WARNING, "JMS shutdown error", ex);
        }
    }

    /** @return JMS server info */
    @Override
    public String toString()
    {
        return getJMSServerName();
    }
}
