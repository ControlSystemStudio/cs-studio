/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.annunciator.model;

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

import org.csstudio.alarm.beast.annunciator.Activator;
import org.csstudio.logging.JMSLogMessage;
import org.csstudio.platform.utility.jms.JMSConnectionFactory;
import org.csstudio.platform.utility.jms.JMSConnectionListener;
import org.csstudio.utility.speech.Translation;

/** JMSAnnunciator connects to JMS, puts received messages into a queue,
 *  and starts a QueueManager which in turn sends them to a speech library.
 *
 *  @author Kay Kasemir
 *  @author Katia Danilova
 *
 *        reviewed by Delphy 1/29/09
 */
@SuppressWarnings("nls")
public class JMSAnnunciator implements ExceptionListener, MessageListener
{
    final private JMSAnnunciatorListener listener;
    final private int threshold;
    private Translation translations[];

    final private Connection connection;
    final private Session session;
    final private MessageConsumer consumers[];

    /** Queue of prioritized received messages.
     *  onMessage will add messages received from JMS.
     */
    final private SpeechPriorityQueue queue = new SpeechPriorityQueue();

    /** QueueManager thread picks messages off the queue for annunciation */
	private QueueManager queuemanager;

    /** Create the JMS consumers.

     *  This call will hang on JMS connection problems, so it should be called
     *  from a non-GUI thread.
     *
     *  @param listener Listener
     *  @param connection JMS library Connection
     *  @param topics Names of JMS topic from where to read messages
     *  @param translations_file Name of translations file
     *  @param threshold max. number of queues messages to allow
     *  @throws Exception on error: Translation file problem, JMS error, ..
     */
    public JMSAnnunciator(
            final JMSAnnunciatorListener listener,
    		final Connection connection,
    		final String topics[],
    		final String translations_file,
    		final int threshold)
        throws Exception
    {
        this.listener = listener;
        this.threshold = threshold;

       	translations = null;
        if (translations_file.length() <= 0)
            Activator.getLogger().fine("No translations file name => no translations will be used ");
        else
        {
   	        // Read translations from translations_file (in preferences.ini)
        	translations = TranslationFileReader.getTranslations(translations_file);
        }

        this.connection = connection;
        // Handle connection errors by putting informational messages
        // onto the annunciation queue
        JMSConnectionFactory.addListener(connection, new JMSConnectionListener()
        {
            @Override
            public void linkDown()
            {
                queue.add(Severity.forInfo(), "Annunciator disconnected from network");
            }

            @Override
            public void linkUp(final String server)
            {
                queue.add(Severity.forInfo(), "Annunciator connected to network");
            }
        });

        // Start JMS.
        connection.setExceptionListener(this);
        connection.start();
        // Create one JMS "session"
        session = connection.createSession(/* transacted */false,
                                           Session.AUTO_ACKNOWLEDGE);

        // Subscribe to incoming messages for each topic, separated by ','
        consumers = new MessageConsumer[topics.length];
        for (int i = 0; i < topics.length; i++)
        {
            final Topic topic = session.createTopic(topics[i]);
            consumers[i] = session.createConsumer(topic);
            consumers[i].setMessageListener(this);
        }
    }

    /** Start the QueueManager, the 'speaker' thread.
     *
     *  This is split out of the JMS connection because the speech library uses
     *  AWT, and in an SWT program there seem to be problems when AWT is accessed
     *  too early or from a non-GUI thread.
     *  This way the AnnunciatorView can perform the start() on a GUI thread.
     */
    public void start()
    {
        // Initialize the QueueManager.
        queuemanager = new QueueManager(listener, queue, translations, threshold);
        queuemanager.start();
    }

    /** @param enabled Enable the voice annunciations? */
    public void setEnabled(final boolean enabled)
    {
        queuemanager.setEnabled(enabled);
    }

    /** {@inhericDoc} */
    @Override
    public void onMessage(final Message msg)
    {
        // Handle only MapMessages
    	if (! (msg instanceof MapMessage))
        {
            Activator.getLogger().log(Level.WARNING, "Received unknown message type {0}", msg.getClass().getName());
            return;
        }
        final MapMessage map = (MapMessage) msg;
        // Extract info from map:
        // 'TEXT' is actual message
        // 'SEVERITY' is optional severity name of the message
 		try
		{
 			final String text = map.getString(JMSLogMessage.TEXT);
 			String sevr_text = map.getString(JMSLogMessage.SEVERITY);
 			// Use low-priority default severity
 			if (sevr_text == null)
 			    sevr_text = "NONE";
 			// Enqueue message text with severity
 			queue.add(Severity.fromString(sevr_text), text);
		}
		catch (JMSException ex)
		{
		    listener.annunciatorError(ex);
		}
    }

    /** Stop the receiver: Disconnect from JMS, stop QueueManager...
     *  @throws Exception
     */
    public void close()
    {
    	queuemanager.stop();
    	if (consumers != null)
    	{
    	    for (MessageConsumer consumer : consumers)
    	    {
    	        try
    	        {
    	            consumer.close();
    	        }
    	        catch (Exception ex)
    	        {
    	            // Ignore, we're closing down anyway
    	        }
    	    }
    	}
    	try
    	{
    	    session.close();
    	    connection.close();
    	}
        catch (Exception ex)
        {
            // Ignore, we're closing down anyway
        }
    }

    /** @see ExceptionListener */
    @Override
    public void onException(final JMSException ex)
    {
        listener.annunciatorError(ex);
    }
}
