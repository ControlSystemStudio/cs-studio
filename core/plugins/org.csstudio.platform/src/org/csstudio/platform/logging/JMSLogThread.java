package org.csstudio.platform.logging;

import java.util.Calendar;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.Topic;

import org.apache.log4j.helpers.LogLog;
import org.csstudio.platform.utility.jms.JMSConnectionFactory;

/** Thread that reads log messages from a queue and tries to send them to JMS.
 *  <p>
 *  This thread will disconnect and try to re-connect in case
 *  of errors, but is is preferred to have the underlying JMS library
 *  handle this, for example ActiveMQ with "failover:..." JMS server URLs.
 *  <p>
 *  One drawback of ActiveMQ and "failover:..." is that the library can
 *  hang in infinite reconnect attempts when all JMS servers are inaccessible,
 *  and then there is no graceful way to interrrupt/cancel this thread.
 *  <p>
 *  To debug this and Log4j in general, set the property
 *  <pre>log4j.debug=true</pre>
 *  Unfortunately this cannot be done via the CSS preferences page for
 *  system properties because LogLog will check before those become
 *  effective. So it has to be done via a command-line parameter.
 *  
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class JMSLogThread extends Thread implements ExceptionListener
{
    /** Interval between queue polls in ms.
     *  Determines the response time to <code>cancel()</code>.
     */
    private static final int POLL_PERIOD_MS = 500;

    /** Re-connection delay in milliseconds */
    private static final int CONNECT_DELAY_MS = 5000;
    
    /** Maximum number of messages that will queue up */
    private static final int MAX_QUEUE_SIZE = 100;
    
    /** Queue of log messages */
    final private BlockingQueue<JMSLogMessage> queue =
        new LinkedBlockingQueue<JMSLogMessage>();
    
    /** Flag used to throttle messages when hitting MAX_QUEUE_SIZE */
    private boolean queue_is_full = false;
    
    /** Message that we should have sent or <code>null</code> */
    private JMSLogMessage pending_message = null;
    
    /** URL of the JMS server */
    final private String server_url;

    /** Name of the JMS topic */
    final private String topic_name;
    
    /** JMS user */
    final private String user_name;

    /** JMS password */
    final private String password;

    /** Flag to stop the thread.
     *  @see #cancel()
     */
    private boolean run = true;

    /** JMS Connection or <code>null</code> */
    private Connection connection = null;

    /** JMS Session or <code>null</code> */
    private Session session = null;

    /** JMS message producer, bound to topic, or <code>null</code> */
    private MessageProducer producer = null;

    /** Create JMS log thread
     *  @param server_url Initial JMS server URL
     *  @param topic_name Initial JMS queue topic
     *  @param user_name JMS user
     *  @param password  JMS password
     */
    public JMSLogThread(final String server_url, final String topic_name,
            final String user_name, final String password)
    {
        super("JMSLogThread");
        this.server_url = server_url;
        this.topic_name = topic_name;
        this.user_name = user_name;
        this.password = password;
    }
    
    /** Add message to queue.
     *  @param message
     */
    public synchronized void addMessage(final JMSLogMessage message)
    {
        // Limit the queue size
        if (queue.size() < MAX_QUEUE_SIZE)
        {
            LogLog.debug("Adding " + message);
            queue.offer(message);
            queue_is_full = false;
            return;
        }
        if (queue_is_full)
            return;
        // Entering 'full' mode, add one final note about it
        queue_is_full = true;
        final Calendar now = Calendar.getInstance();
        final JMSLogMessage error = new JMSLogMessage("WARN: JMSLogThread queue is full",
                now, now, null, null, null, null, null, null);
        LogLog.error(error.toString());
        queue.offer(error);
    }
    
    /** Ask thread to stop.
     *  Doesn't wait for the thread to stop.
     *  Ideally, thread will soon notice that there's nothing more on the queue
     *  and quit. But it it's stuck in an ongoing JMS library call,
     *  there is no good way to stop it.
     *  @see JMSLogThread
     */
    public void cancel()
    {
        run = false;
    }
    
    /** Thread's Runnable */
    @Override
    public void run()
    {
        LogLog.debug("JMSLogThread start");
        while (run)
        {
            if (connect())
                perform_logging();
            // Ran into error....
            disconnect();
            if (run)
            {   // Wait a little, then try again
                try
                {
                    sleep(CONNECT_DELAY_MS);
                }
                catch (InterruptedException ex)
                { /* NOP */ }
            }
        }
        LogLog.debug("JMSLogThread ends");
    }

    /** Connect to JMS
     *  @return <code>true</code> if successful
     */
    private boolean connect()
    {
        try
        {
            if (user_name == null || user_name.length() <= 0)
                connection = JMSConnectionFactory.connect(server_url);
            else
                connection = JMSConnectionFactory.connect(server_url,
                        user_name, password);
            connection.setExceptionListener(this);
            connection.start();
            session = connection.createSession(/* transacted */false,
                                               Session.AUTO_ACKNOWLEDGE);
            final Topic topic = session.createTopic(topic_name);
            producer = session.createProducer(topic);
            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
            LogLog.debug("JMSLogThread connected " + server_url
                        + " (" + producer.getDestination() + ")");
            return true;
        }
        catch (Throwable ex)
        {
            LogLog.error("JMSLogThread connect error for " + server_url
                    +": " + ex.getMessage(), ex);
        }
        return false;
    }        

    /** Disconnect from JMS.
     *  Safe to call even when already disconnected.
     *  Depending on the <code>run</code> flag, thread should
     *  attempt a re-connect or quit.
     */
    private void disconnect()
    {
        if (connection != null)
        {
            try
            {
                connection.close();
            }
            catch (Exception ex) { /* NOP */ }
            connection = null;
        }
        session = null;
        producer = null;
        LogLog.debug("JMSLogThread disconnected");
    }

    /** Log messages until there's an error
     *  or <code>cancel()</code> is called.
     */
    private void perform_logging()
    {
        LogLog.debug("JMSLogThread waiting for messages");
        while (run)
        {   
            final JMSLogMessage log_message = getNextMessage();
            if (log_message == null)
                continue;
            // Try to send message to JMS. This could fail because the
            // connection was closed.
            try
            {
                final MapMessage map = session.createMapMessage();
                log_message.toMapMessage(map);
                producer.send(map);
                LogLog.debug("JMSLogThread sent " + log_message);
            }
            catch (Throwable ex)
            {
                LogLog.error("Error sending log message to JMS", ex);
                // Queue again, then return to trigger re-connect
                pending_message = log_message;
                return;
            }
        }
    }

    /** @return Next message or <code>null</code> if there is none */
    private JMSLogMessage getNextMessage()
    {
        // Previously undelivered message?
        if (pending_message != null)
        {
            final JMSLogMessage result = pending_message;
            pending_message = null;
            return result;
        }
        // Else: Get a new message from queue
        try
        {
            return queue.poll(POLL_PERIOD_MS, TimeUnit.MILLISECONDS);
        }
        catch (InterruptedException ex)
        { /* NOP */ }
        return null;
    }

    /** @see javax.jms.ExceptionListener */
    public void onException(final JMSException ex)
    {
        LogLog.error("JMSLogThread received JMS Exception", ex);
    }
}
