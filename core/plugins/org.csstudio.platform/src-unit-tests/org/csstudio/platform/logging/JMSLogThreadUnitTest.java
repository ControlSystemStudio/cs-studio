package org.csstudio.platform.logging;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Properties;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import javax.jms.Connection;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.Topic;

import org.apache.log4j.Level;
import org.csstudio.platform.utility.jms.JMSConnectionFactory;
import org.junit.Test;

/** [Headless] JUnit Plug-in test of the JMSLogThread.
 * 
 *  For this test to work, it needs the URL of a JMS server
 *  and a test topic, both of which are site-specific and
 *  obtained from the TestDataProvider.
 *  
 *  Without the TestDataProvider this could be a plain JUnit test.
 *  That's wrong. You could run it as plain test as well. The test data conf file would have been
 *  located without bundle framework. But now I've refactored the test data provider out of 
 *  platform anyway.
 *  
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class JMSLogThreadUnitTest
{
    private static final int MESSAGE_COUNT = 3;
    final private String URL;
    final private String TOPIC;
    
    public JMSLogThreadUnitTest() throws Exception
    {
        InputStream inStream = null;
        Properties props = new Properties();
        try {
            final String curDir = System.getProperty("user.dir");
            final File configFile = new File(curDir + File.separator + "snsTestConfiguration.ini");
            URL resource = configFile.toURL();

            inStream =  resource.openStream();
            props.load(inStream);
        } finally {
            if (inStream != null) {
                inStream.close();
            }
        }

    	URL = (String) props.get("jms_url");
    	TOPIC = (String) props.get("jms_topic");
    }

    /** JMS Receiver that runs until MESSAGE_COUNT messages were received */
    class Receiver extends Thread implements ExceptionListener, MessageListener
    {
        private int message_count = 0;
        final private Semaphore connected = new Semaphore(0);
        final private Semaphore done = new Semaphore(0);
        
        public boolean waitForStartup(final long seconds) throws Exception
        {
        	return connected.tryAcquire(seconds, TimeUnit.SECONDS);
        }
        
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
                System.out.println("Receiver is listening...");
                connected.release();
                
                // Wait for messages to arrive
                done.acquire();
                
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
            	done.release();
        }

        public int getMessageCount()
        {
            return message_count;
        }
    }
    
    @Test(timeout=10000)
    public void testRun() throws Exception
    {
    	if (URL == null   ||   TOPIC == null)
    	{
        	System.out.println("Missing JMS settings, skipping test");
        	return;
        }

    	final Receiver receiver = new Receiver();
        receiver.start();
        if (!receiver.waitForStartup(5))
        {
        	System.out.println("Cannot connect to JMS, skipping test");
        	return;
        }
        
        final JMSLogThread log_thread = new JMSLogThread(URL, TOPIC, null, null);
        log_thread.start();
        for (int i=0; i<MESSAGE_COUNT; ++i)
        {
            final Calendar now = Calendar.getInstance();
            Calendar earlier = (Calendar) now.clone();
            earlier.add(Calendar.HOUR, -1);
            final JMSLogMessage log_msg = new JMSLogMessage("Test " + i,
            		Level.INFO.toString(),
                    now, earlier,
                    "SomeClass", "some_method",
                    "SomeClass.java:315",
                    "MyApp", "localhost", "fred");
            log_thread.addMessage(log_msg);
            Thread.sleep(100);
        }
        
        // Wait for receiver to have received what it wants to receive
        receiver.join(10 * 1000);

        // Stop sender
        log_thread.cancel();
        log_thread.join();
        
        // Did we receive all messages?
        assertEquals(MESSAGE_COUNT, receiver.getMessageCount());
    }
}
