/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.logging;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.Topic;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

/** Thread that reads log messages from queue and writes them to JMS.
 *  <p>
 *  Uses a {@link Formatter}, but only invokes <code>Formatter.formatMessage()</code>
 *  to expand parameters in the message text.
 *  If the log record contains exception info, that is added to the message text.
 *  <p>
 *  The remaining {@link LogRecord} elements are directly placed
 *  in the JMS log message and not formatted into (possibly) one
 *  long string via a call to  <code>Formatter.format()</code>.
 *
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class JMSLogWriter implements ExceptionListener
{
    /** Re-connection delay in milliseconds */
    private static final int CONNECT_DELAY_MS = 5000;

    /** Application ID included in log messages */
    final private String application_id;

    /** JMS server URL */
    final private String jms_url;

    /** JMS topic */
    final private String topic;

    /** {@link BlockingQueue} log messages to be writtem to JMS */
    final private BlockingQueue<LogRecord> records;

    /** {@link Formatter} used to partially generate the log message */
    final private Formatter formatter;

    /** JMS Connection or <code>null</code> */
    private Connection connection = null;

    /** JMS Session or <code>null</code> */
    private Session session = null;

    /** JMS message producer, bound to topic, or <code>null</code> */
    private MessageProducer producer = null;

    /** User name */
    final private String user = System.getProperty("user.name");

    /** Host name for this host */
    private String host;

    /** Background thread that performs the actual work */
    private Thread thread;

    /** <code>thread</code> will run while this flag is <code>true</code> */
    private volatile boolean run;

    /** Date format for JMS message time info */
    final private static SimpleDateFormat date_format =
        new SimpleDateFormat(JMSLogMessage.DATE_FORMAT);

    /** Initialize
     *  @param application_id Application ID to include in JMS messages
     *  @param jms_url JMS server URL
     *  @param topic JMS topic
     *  @param records {@link BlockingQueue} from which log messages are read
     *  @param formatter {@link Formatter} that's partially used to format the message text
     */
    public JMSLogWriter(final String application_id, final String jms_url, final String topic,
            final BlockingQueue<LogRecord> records, final Formatter formatter)
    {
        this.application_id = application_id;
        this.jms_url = jms_url;
        this.topic = topic;
        this.records = records;
        this.formatter = formatter;

        try
        {
            host = InetAddress.getLocalHost().getHostName();
        }
        catch (UnknownHostException ex)
        {
            host = "unknown";
        }
    }

    /** Start the writer's background thread */
    public void start()
    {
        thread = new Thread("JMSWriteThread")
        {
            @Override
            public void run()
            {
                while (run)
                {
                    if (connect())
                    {
                        try
                        {
                            handleRecordQueue();
                        }
                        catch (JMSException ex)
                        {
                            // Cannot 'log' the error, because this _is_ part of the logger
                            System.out.println("JMSLogWriter error. " + ex.getMessage());
                        }
                    }
                    // Ran into error or were interrupted
                    disconnect();
                    // Wait and try again, or stop?
                    if (run)
                    {
                        try
                        {
                            sleep(CONNECT_DELAY_MS);
                        }
                        catch (InterruptedException ex)
                        { /* NOP */ }
                    }
                }
            }
        };
        thread.setDaemon(true);
        run = true;
        thread.start();
    }

    /** Stop the writer's background thread */
    public void stop()
    {
        run = false;
        thread.interrupt();
        // Do not join the thread, i.e. do not wait for it to stop:
        // JMS library could be hung in a connect for a "failover://" URL
        // Just hope that the thread will end, ignore it from now on.
        thread = null;
    }

    /** Connect to JMS, setup session, producer
     *  @return <code>true</code> when OK
     */
    private boolean connect()
    {
        // Connect to ActiveMQ JMS
        final ActiveMQConnectionFactory factory =
            new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_USER,
                    ActiveMQConnection.DEFAULT_PASSWORD, jms_url);

        try
        {
            connection = factory.createConnection();

            connection.setExceptionListener(this);
            connection.start();
            session = connection.createSession(/* transacted */false,
                                               Session.AUTO_ACKNOWLEDGE);
            final Topic jms_topic = session.createTopic(topic);
            producer = session.createProducer(jms_topic);
            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
        }
        catch (JMSException ex)
        {   // Cannot 'log' the error, because this _is_ part of the logger
            System.out.println("JMSLogWriter connection error. " + ex.getMessage());
            return false;
        }

        return true;
    }

    /** Disconnect from JMS.
     *  Safe to call even when already disconnected.
     */
    private void disconnect()
    {
        session = null;
        producer = null;
        if (connection != null)
        {
            try
            {
                connection.close();
            }
            catch (Exception ex)
            {   // Ignored since we're shutting down anyway
                ex = null;
            }
            connection = null;
        }
    }

    /** Log messages from queue to JMS
     *  @throws JMSException on internal JMS error
     */
    private void handleRecordQueue() throws JMSException
    {
        try
        {
            LogRecord record;
            while (run  &&  (record = records.take()) != null)
            {
                // Format text portion of the message
                String message = formatter.formatMessage(record);

                // Add optional exception info
                final Throwable exception = record.getThrown();
                if (exception != null)
                {
                    message += " (" + exception.getClass().getName() + ": " +
                        exception.getMessage() + ")";
                }

                // Create MapMessage
                final MapMessage map = session.createMapMessage();
                map.setString(JMSLogMessage.TYPE, JMSLogMessage.TYPE_LOG);
                map.setString(JMSLogMessage.TEXT, message);
                map.setString(JMSLogMessage.SEVERITY, record.getLevel().getName());

                String time_text = date_format.format(new Date(record.getMillis()));
                map.setString(JMSLogMessage.CREATETIME, time_text);

                if (record.getSourceClassName() != null)
                    map.setString(JMSLogMessage.CLASS, record.getSourceClassName());

                if (record.getSourceMethodName() != null)
                    map.setString(JMSLogMessage.NAME, record.getSourceMethodName());

                map.setString(JMSLogMessage.APPLICATION_ID, application_id);
                map.setString(JMSLogMessage.HOST, host);
                map.setString(JMSLogMessage.USER, user);

                // Send to JMS
                producer.send(map);
            }
        }
        catch (InterruptedException ex)
        {
            // Probably interrupted on purpose; quit silently
        }
    }

    /** JMS Exception handler */
    @Override
    public void onException(final JMSException exception)
    {
        // Cannot log this because we _are_ the logger
        exception.printStackTrace();
    }
}
