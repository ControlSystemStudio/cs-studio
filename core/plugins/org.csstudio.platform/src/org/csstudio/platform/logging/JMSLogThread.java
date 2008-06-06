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
    /** Connection delay in milliseconds */
    private static final int CONNECT_DELAY_MS = 5000;
    
    /** JMS server URL.
     *  TODO cannot be a fixed URL
     */
    final private static String URL = "tcp://ics-srv02.sns.ornl.gov:61616";
    
    /** JMS topic
     *  TODO cannot be a fixed topic
     */
    final private static String TOPIC = "LOG";
    
    /** Flag to stop the thread.
     *  @see #cancel()
     */
    private boolean run = true;

    /** Queue of log messages */
    private LinkedBlockingQueue<MapMessage> queue =
        new LinkedBlockingQueue<MapMessage>();

    /** JMS Connection or <code>null</code> */
    private Connection connection = null;

    /** JMS Session or <code>null</code> */
    private Session session = null;

    /** JMS message producer, bound to topic, or <code>null</code> */
    private MessageProducer producer = null;
    
    /** Add a message to the queue.
     *  @param message Message to add. Message content is not checked.
     */
    public void addLogMessage(final MapMessage message)
    {
        try
        {
            queue.put(message);
        }
        catch (InterruptedException ex)
        {
            // TODO what now?
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
    }

    /** Connect to JMS
     *  @return <code>true</code> if successful
     */
    private boolean connect()
    {
        try
        {
            connection = JMSConnectionFactory.connect(URL);
            connection.start();
            session = connection.createSession(/* transacted */false,
                    Session.AUTO_ACKNOWLEDGE);
            final Topic topic = session.createTopic(TOPIC);
            producer = session.createProducer(topic);
            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
            
            return true;
        }
        catch (Exception ex)
        {
            // TODO log
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
    }

    /** Log messages until there's an error.
     *  Only returns if there's an error. 
     */
    private void perform_logging()
    {
        while (true)
        {
            MapMessage message;
            try
            {
                message = queue.take();
            }
            catch (InterruptedException ex)
            {   // Should be the result of cancel(), so quit
                return;
            }
            // Try to send message to JMS
            try
            {
                producer.send(message);
            }
            catch (Exception ex)
            {
                // TODO Re-queue failed message?
            }
        }
    }
}
