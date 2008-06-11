package org.csstudio.platform.logging;

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

import org.csstudio.platform.libs.jms.JMSConnectionFactory;

/** Thread that reads log messages from a queue and tries to send them to JMS.
 *  <p>
 *  This thread will disconnect and try to re-connect in case
 *  of errors, but is is preferred to have the underlying JMS library
 *  handle this, for example ActiveMQ with "failover:..." JMS server URLs.
 *  <p>
 *  One drawback of ActiveMQ and "failover:..." is that the library can
 *  hang in infinite reconnect attempts when all JMS servers are inaccessible,
 *  and then there is no graceful way to interrrupt/cancel this thread.
 *  
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class JMSLogThread extends Thread implements ExceptionListener
{
    /** Debug messages to stdout?
     *  Can't use Log4j because we handle Log4j messages...
     */
    public static boolean debug = true;

    /** Interval between queue polls in ms.
     *  Determines the response time to <code>cancel()</code>.
     */
    private static final int POLL_PERIOD_MS = 500;

    /** Re-connection delay in milliseconds */
    private static final int CONNECT_DELAY_MS = 5000;
    
    /** Queue of log messages */
    final private BlockingQueue<JMSLogMessage> queue =
        new LinkedBlockingQueue<JMSLogMessage>();
    
    /** Message that we should have sent or <code>null</code> */
    private JMSLogMessage pending_message = null;
    
    /** URL of the JMS server */
    final private String server_url;

    /** Name of the JMS topic */
    final private String topic_name;

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
     */
    public JMSLogThread(final String server_url, final String topic_name)
    {
        super("JMSLogThread");
        this.server_url = server_url;
        this.topic_name = topic_name;
    }
    
    /** Add message to queue.
     *  @param message
     */
    public void addMessage(final JMSLogMessage message)
    {
        // TODO limit the queue size
        queue.offer(message);
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
        if (debug)
            System.out.println("JMSLogThread start");
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
        if (debug)
            System.out.println("JMSLogThread ends");
    }

    /** Connect to JMS
     *  @return <code>true</code> if successful
     */
    private boolean connect()
    {
        try
        {
            connection = JMSConnectionFactory.connect(server_url);
            connection.setExceptionListener(this);
            connection.start();
            session = connection.createSession(/* transacted */false,
                                               Session.AUTO_ACKNOWLEDGE);
            final Topic topic = session.createTopic(topic_name);
            producer = session.createProducer(topic);
            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
            if (debug)
                System.out.println("JMSLogThread connected " + server_url
                        + " (" + producer.getDestination() + ")");
            return true;
        }
        catch (Throwable ex)
        {
            System.out.println("JMSLogThread connect error for " + server_url
                    +": " + ex.getMessage());
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
        if (debug)
            System.out.println("JMSLogThread disconnected");
    }

    /** Log messages until there's an error
     *  or <code>cancel()</code> is called.
     */
    private void perform_logging()
    {
        if (debug)
            System.out.println("JMSLogThread waiting for messages");
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
                if (debug)
                    System.out.println("JMSLogThread sent " + log_message);
            }
            catch (Throwable ex)
            {
                ex.printStackTrace();
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
        ex.printStackTrace();
    }
}
