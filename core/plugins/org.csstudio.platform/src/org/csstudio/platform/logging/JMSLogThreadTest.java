package org.csstudio.platform.logging;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.Enumeration;

import javax.jms.Connection;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.Topic;

import org.csstudio.platform.libs.jms.JMSConnectionFactory;
import org.junit.Test;

/** Stand-alone test of the JMSLogThread.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class JMSLogThreadTest
{
    private static final int MESSAGE_COUNT = 3;
    final private static String URL = "tcp://ics-srv02.sns.ornl.gov:61616";
    final private static String TOPIC = "LOG";

    /** JMS Receiver that runs until MESSAGE_COUNT messages were received */
    class Receiver extends Thread implements ExceptionListener, MessageListener
    {
        private int message_count = 0;
        
        /** {@inheritDoc} */
        @Override
        public void run()
        {
            try
            {   // Connect
                final Connection connection = JMSConnectionFactory.connect(URL);
                connection.setExceptionListener(this);
                connection.start();
                final Session session = connection.createSession(
                        /* transacted */false, Session.AUTO_ACKNOWLEDGE);
                final Topic topic = session.createTopic(TOPIC);
                final MessageConsumer consumer = session.createConsumer(topic);
                consumer.setMessageListener(this);
                // Wait for notification
                System.out.println("Receiver is listening...");
                synchronized (this)
                {
                    wait();
                }
                // Shutdown
                consumer.close();
                session.close();
                connection.close();
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
            System.out.println("Receiver exits.");
        }

        /** @see ExceptionListener */
        public void onException(final JMSException ex)
        {
            ex.printStackTrace();
        }

        /** @see MessageListener */
        @SuppressWarnings("unchecked")
        public void onMessage(final Message message)
        {
            try
            {
                if (message instanceof MapMessage)
                {
                    final MapMessage map = (MapMessage) message;
                    final JMSLogMessage log = JMSLogMessage.fromMapMessage(map);
                    System.out.println("Received " + log);
                    final Enumeration<String> elements = map.getMapNames();
                    while (elements.hasMoreElements())
                    {
                        final String element = elements.nextElement();
                        System.out.format("%20s: %s\n",
                                element, map.getString(element));
                    }
                }
                else
                    System.out.println("Received " + message.getClass().getName());
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
            if (++message_count >= MESSAGE_COUNT)
            {
                synchronized (this)
                {
                    notifyAll();
                }
            }
        }

        public int getMessageCount()
        {
            return message_count;
        }
    }
    
    @Test
    public void testRun() throws Exception
    {
        final Receiver receiver = new Receiver();
        receiver.start();
        // Simplistic wait for the receiver to actually receive:
        Thread.sleep(5000);        
        
        final JMSLogThread log_thread = new JMSLogThread(URL, TOPIC);
        log_thread.start();
        for (int i=0; i<MESSAGE_COUNT; ++i)
        {
            final Calendar now = Calendar.getInstance();
            Calendar earlier = (Calendar) now.clone();
            earlier.add(Calendar.HOUR, -1);
            final JMSLogMessage log_msg = new JMSLogMessage("Test " + i,
                    now, earlier,
                    "SomeClass", "some_method",
                    "SomeClass.java:315",
                    "MyApp", "localhost", "fred");
            log_thread.addLogMessage(log_msg);
            Thread.sleep(100);
        }
        
        // Wait for receiver to have received what it wants to receive
        receiver.join(10000);
        
        // Stop sender
        log_thread.cancel();
        log_thread.join();
        
        // Did we receive all messages?
        assertEquals(MESSAGE_COUNT, receiver.getMessageCount());
    }
}
