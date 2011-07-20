/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.debugging.jmsmonitor;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jms.Connection;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

import org.csstudio.apputil.ringbuffer.RingBuffer;
import org.csstudio.logging.JMSLogMessage;
import org.csstudio.platform.utility.jms.JMSConnectionFactory;
import org.csstudio.platform.utility.jms.JMSConnectionListener;

/** Data model for the JMS Monitor
 *  @author Kay Kasemir
 */
public class Model implements ExceptionListener, MessageListener, JMSConnectionListener
{
    final private String topic_names;
    private volatile Connection connection = null;
    private volatile Session session = null;
    private volatile MessageConsumer consumer[] = new MessageConsumer[0];

    /** Run flag.
     *  In principle we try to close the model properly.
     *  But in case the main thread was still hung in the connection,
     *  it will not get to the proper shutdown,
     *  and there is no perfect way to interrupt an ongoing
     *  "failover" connection problem.
     *  So we set the 'run' flag to <code>false</code> to
     *  suppress notifications and stop the main thread
     *  in case it wakes up after a connection problem.
     */
    private volatile boolean run = true;

    final private RingBuffer<ReceivedMessage> messages =
    	new RingBuffer<ReceivedMessage>(Preferences.getMaxMessages());

    private CopyOnWriteArrayList<ModelListener> listeners =
        new CopyOnWriteArrayList<ModelListener>();

    private volatile String server_name = Messages.Disconnected;

    /** Initialize
     *  @param url JMS server URL
     *  @param user JMS user name or <code>null</code>
     *  @param password JMS password or <code>null</code>
     *  @param topic_names JMS topics, separated by comma
     *  @throws Exception on error
     */
    @SuppressWarnings("nls")
    public Model(final String url, final String user, final String password,
                 final String topic_names) throws Exception
    {
        this.topic_names = topic_names;
        if (url == null  ||  url.length() <= 0)
            throw new Exception(Messages.ErrorNoURL);
        if (topic_names.length() <= 0)
            throw new Exception(Messages.ErrorNoTopic);
        final Runnable connector = new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
	                connect(url, user, password);
	                while (run)
	                {
    	                synchronized (Model.this)
    	                {
    	                	Model.this.wait();
    	                }
	                }
	                disconnect();
                }
                catch (Exception ex)
                {
                    Logger.getLogger(Activator.ID).log(Level.SEVERE, "JMSMonitorConnector thread error", ex);
                }
            }
        };
        final Thread thread = new Thread(connector, "JMSMonitorConnector");
        thread.setDaemon(true);
        thread.start();
    }

    /** Connect to JMS; run in background thread
     *  @param url JMS server URL
     *  @param user JMS user name or <code>null</code>
     *  @param password JMS password or <code>null</code>
     *  @throws Exception on error
     */
    private void connect(final String url, final String user, final String password)
    	throws Exception
    {
        connection = JMSConnectionFactory.connect(url, user, password);
        JMSConnectionFactory.addListener(connection, this);
        connection.setExceptionListener(this);
        connection.start();
        session = connection.createSession(/* transacted */ false,
                                           Session.AUTO_ACKNOWLEDGE);

        final String[] names = topic_names.split(", *"); //$NON-NLS-1$
        consumer = new MessageConsumer[names.length];
        for (int i=0; i<names.length; ++i)
        {
            final Topic topic = session.createTopic(names[i]);
            consumer[i] = session.createConsumer(topic);
            consumer[i].setMessageListener(this);
        }
        synchronized (messages)
        {
            messages.clear();
        }
        fireModelChanged();
    }

    /** Disconnect JMS. Called in background thread */
	private void disconnect()
	{
	    listeners.clear();
	    messages.clear();
	    try
	    {
	        for (MessageConsumer c : consumer)
                c.close();
	        if (session != null)
	            session.close();
	        if (connection != null)
	            connection.close();
	    }
	    catch (Exception ex)
	    {
            Logger.getLogger(Activator.ID).log(Level.WARNING, "JMS shutdown error", ex); //$NON-NLS-1$
	    }
	}

    /** Must be called to release resources when no longer used */
	public void close()
	{
	    run  = false;
		synchronized (this)
		{
			this.notifyAll();
		}
	}

	/** Add listener
     *  @param listener Listener to add
     */
    public void addListener(final ModelListener listener)
    {
        listeners.add(listener);
    }

    /** @return name of JMS server */
    public String getServerName()
    {
        return server_name;
    }

    /** @return Array of received messages */
    public ReceivedMessage[] getMessages()
    {
        synchronized (messages)
        {
            final ReceivedMessage retval[] =
                new ReceivedMessage[messages.size()];
            return messages.toArray(retval);
        }
    }

    /** Remove all messages */
    public void clear()
    {
        messages.clear();
        fireModelChanged();
    }

    /** @see JMSConnectionListener */
    @Override
    public void linkDown()
    {
        server_name = Messages.Disconnected;
        fireModelChanged();
    }

    /** @see JMSConnectionListener */
    @Override
    public void linkUp(final String server)
    {
        this.server_name = server;
        fireModelChanged();
    }

    /** @see MessageListener */
    @Override
    public void onMessage(final Message message)
    {
        if (! run)
            return;
        try
        {
            if (message instanceof MapMessage)
                handleMapMessage((MapMessage) message);
            else if (message instanceof TextMessage)
                handleTextMessage((TextMessage) message);
            else
                Logger.getLogger(Activator.ID).log(Level.WARNING, "Message type {0} not handled",  message.getClass().getName()); //$NON-NLS-1$
        }
        catch (Throwable ex)
        {
            Logger.getLogger(Activator.ID).log(Level.SEVERE, "Message handling error",  ex); //$NON-NLS-1$
        }
    }

    /** Handle received MapMessage
     *  @param message The MapMessage
     * @throws Exception on error
     */
    @SuppressWarnings("unchecked")
    private void handleMapMessage(final MapMessage message) throws Exception
    {
        final Enumeration<String> names = message.getMapNames();
        final ArrayList<MessageProperty> content =
            new ArrayList<MessageProperty>();
        content.add(new MessageProperty(Messages.MessageDestination, message.getJMSDestination().toString()));
        String type = Messages.UnknownType;
        while (names.hasMoreElements())
        {
            final String name = names.nextElement();
            final String value = message.getString(name);
            if (JMSLogMessage.TYPE.equals(name))
                type = value;
            else
                content.add(new MessageProperty(name, value));
        }
        final ReceivedMessage entry = new ReceivedMessage(type, content);
        // Add to end of list
        synchronized (messages)
        {
            messages.add(entry);
        }
        fireModelChanged();
    }

    /** Handle received TextMessage
     *  @param message The TextMessage
     * @throws Exception on error
     */
    private void handleTextMessage(final TextMessage message) throws Exception
    {
        final ArrayList<MessageProperty> content =
            new ArrayList<MessageProperty>();
        content.add(new MessageProperty(Messages.MessageDestination, message.getJMSDestination().toString()));
        content.add(new MessageProperty("TextMessage", message.getText())); //$NON-NLS-1$
        final ReceivedMessage entry = new ReceivedMessage("TEXT", content); //$NON-NLS-1$
        // Add to end of list
        synchronized (messages)
        {
            messages.add(entry);
        }
        fireModelChanged();
    }

    /** Notify listeners */
    private void fireModelChanged()
    {
        for (ModelListener listener : listeners)
        {
            try
            {
                listener.modelChanged(this);
            }
            catch (Throwable ex)
            {
                Logger.getLogger(Activator.ID).log(Level.SEVERE, "Model listener error",  ex); //$NON-NLS-1$
            }
        }
    }

    /** @see ExceptionListener */
    @Override
    public void onException(final JMSException ex)
    {
        Logger.getLogger(Activator.ID).log(Level.SEVERE, "JMS Exception",  ex); //$NON-NLS-1$
    }
}
