package org.csstudio.platform.logging;

import java.util.concurrent.LinkedBlockingQueue;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.MapMessage;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.Topic;

import org.csstudio.platform.libs.jms.JMSConnectionFactory;

/** Thread that keeps log messages in a queue and tries to send them to JMS.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class JMSLogThread extends Thread
{
    /** Debug messages to stdout?
     *  Can't use Log4j because we handle Log4j messages...
     */
    public static boolean debug = false;
    
    /** Connection delay in milliseconds */
    private static final int CONNECT_DELAY_MS = 5000;
    
    /** URL of the JMS server */
    private String server_url;

    /** Name of the JMS topic */
    private String topic_name;

    /** Flag to stop the thread.
     *  @see #cancel()
     */
    private boolean run = true;

    /** Queue of log messages */
    private LinkedBlockingQueue<JMSLogMessage> queue =
        new LinkedBlockingQueue<JMSLogMessage>();

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
        this.server_url = server_url;
        this.topic_name = topic_name;
    }
    
    /** Switch thread to new JMS server/topic
     *  @param server_url New JMS server URL
     *  @param topic_name New JMS queue topic
     */
    public void setTarget(final String server_url, final String topic_name)
    {
        this.server_url = server_url;
        this.topic_name = topic_name;
        // TODO trigger (re-)connect
    }
    
    /** Add a message to the queue.
     *  @param message Message to add.
     */
    public void addLogMessage(final JMSLogMessage message)
    {
        try
        {
            if (debug)
                System.out.println("JMSLogThread addded " + message);
            queue.put(message);
        }
        catch (InterruptedException ex)
        {
            System.out.println(ex);
        }
    }
    
    /** Ask thread to stop. Doesn't wait for the thread to stop! */
    public void cancel()
    {
        run = false;
        interrupt();
    }
    
    /** {@inheritDoc} */
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
            System.out.println("JMSLogThread start");
    }

    /** Connect to JMS
     *  @return <code>true</code> if successful
     */
    private boolean connect()
    {
        try
        {
            connection = JMSConnectionFactory.connect(server_url);
            connection.start();
            session = connection.createSession(/* transacted */false,
                    Session.AUTO_ACKNOWLEDGE);
            final Topic topic = session.createTopic(topic_name);
            producer = session.createProducer(topic);
            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
            if (debug)
                System.out.println("JMSLogThread connected " + server_url);
            return true;
        }
        catch (Exception ex)
        {
            System.out.println("JMSLogThread connect error for " + server_url
                    +": " + ex.getMessage());
        }
        return false;
    }        

    /** Disconnect from JMS.
     *  Safe to call even when already disconnected.
     */
    private void disconnect()
    {
        if (producer != null)
        {
            try
            {
                producer.close();
            }
            catch (Exception ex) { /* NOP */ }
            producer = null;
        }
        if (session != null)
        {
            try
            {
                session.close();
            }
            catch (Exception ex) { /* NOP */ }
            session = null;
        }
        if (connection != null)
        {
            try
            {
                connection.close();
            }
            catch (Exception ex) { /* NOP */ }
            connection = null;
        }
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
        {   // Wait for next message
            JMSLogMessage log_message;
            try
            {
                log_message = queue.take();
            }
            catch (InterruptedException ex)
            {   // Should be the result of cancel(), so quit
                return;
            }
            // Try to send message to JMS
            try
            {
                final MapMessage map = session.createMapMessage();
                log_message.toMapMessage(map);
                producer.send(map);
                if (debug)
                    System.out.println("JMSLogThread sent " + log_message);
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
    }
}
