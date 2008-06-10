package org.csstudio.platform.logging;

import static org.junit.Assert.*;

import java.util.Calendar;

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

/** Standalone test of the JMSLogThread */
@SuppressWarnings("nls")
public class JMSLogThreadTest
{
    private static final int MESSAGE_COUNT = 3;
    final private static String URL = "tcp://ics-srv02.sns.ornl.gov:61616";
    final private static String TOPIC = "LOG";

    class Receiver extends Thread implements ExceptionListener, MessageListener
    {
        private int message_count = 0;
        
        @Override
        public void run()
        {
            try
            {
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
                    this.wait();
                }
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
        public void onMessage(final Message message)
        {
            ++message_count;
            try
            {
                if (message instanceof MapMessage)
                {   // CSS 'LOG' messages are MapMessage with many fields.
                    // 'TEXT' has the actual message.
                    final MapMessage map = (MapMessage) message;
                    String text = map.getString("TEXT").trim();
                    System.out.println("Received " + text);
                    return;
                }
                else
                    System.out.println("Received " + message.getClass().getName());
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
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
        
        final JMSLogThread log_thread = new JMSLogThread();
        log_thread.start();
        for (int i=0; i<MESSAGE_COUNT; ++i)
        {
            final JMSLogMessage log_msg = new JMSLogMessage("Test " + i,
                    Calendar.getInstance(),
                    "SomeClass", "some_method",
                    "SomeClass.java:315",
                    "MyApp", "localhost", "fred");
            log_thread.addLogMessage(log_msg);
            Thread.sleep(1000);
        }
        // Simplistic wait for the send thread to start and send messages:
        Thread.sleep(5000);
        
        // Stop sender
        log_thread.cancel();
        log_thread.join();
        
        // Stop receiver
        synchronized (receiver)
        {
            receiver.notifyAll();
        }
        receiver.join();
        
        // Did we receive all messages?
        assertEquals(MESSAGE_COUNT, receiver.getMessageCount());
    }
}
