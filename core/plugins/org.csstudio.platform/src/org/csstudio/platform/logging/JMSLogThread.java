package org.csstudio.platform.logging;

import java.util.concurrent.LinkedBlockingQueue;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.JMSException;
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
    public static boolean debug = true;
    
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
    private LinkedBlockingQueue<JMSLogMessage> queue =
        new LinkedBlockingQueue<JMSLogMessage>();

    /** JMS Connection or <code>null</code> */
    private Connection connection = null;

    /** JMS Session or <code>null</code> */
    private Session session = null;

    /** JMS message producer, bound to topic, or <code>null</code> */
    private MessageProducer producer = null;
    
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
            connection = JMSConnectionFactory.connect(URL);
            connection.start();
            session = connection.createSession(/* transacted */false,
                    Session.AUTO_ACKNOWLEDGE);
            final Topic topic = session.createTopic(TOPIC);
            producer = session.createProducer(topic);
            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
            if (debug)
                System.out.println("JMSLogThread connected " + URL);
            return true;
        }
        catch (Exception ex)
        {
            System.out.println("JMSLogThread connect error for " + URL
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
        {
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
                final MapMessage map = createMapMessage(log_message);
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

    /** Create MapMessage from JMSLogMessage
     *  @param log JMSLogMessage to convert
     *  @return MapMessage
     *  @throws JMSException on error
     */
    private MapMessage createMapMessage(final JMSLogMessage log) throws JMSException
    {
        final MapMessage map = session.createMapMessage();
        map.setString(JMSLogMessage.TYPE, JMSLogMessage.TYPE_LOG);
        map.setString(JMSLogMessage.TEXT, log.getText());
        final String time = JMSLogMessage.date_format.format(log.getTime().getTime());
        map.setString(JMSLogMessage.CREATETIME, time);
        map.setString(JMSLogMessage.EVENTTIME, time);
        setMapValue(map, JMSLogMessage.CLASS, log.getClassName());
        setMapValue(map, JMSLogMessage.NAME, log.getMethodName());
        setMapValue(map, JMSLogMessage.FILENAME, log.getFileName());
        setMapValue(map, JMSLogMessage.APPLICATION_ID, log.getApplicationID());
        setMapValue(map, JMSLogMessage.HOST, log.getHost());
        setMapValue(map, JMSLogMessage.USER, log.getUser());
        return map;
    }

    /** Set element of map to value UNLESS value is <code>null</code>
     *  @param map
     *  @param element
     *  @param value
     *  @throws JMSException 
     */
    private void setMapValue(final MapMessage map,
            final String element, final String value) throws JMSException
    {
        if (value != null)
            map.setString(element, value);
    }
}
