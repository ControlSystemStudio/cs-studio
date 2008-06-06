package org.csstudio.platform.libs.jms;

import static org.junit.Assert.*;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.Topic;

import org.junit.Test;

/** Unit test of the basic JMS connection.
 *  <B>Will not work at other sites without adjusting the URL!</B>
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class JMSConnectionFactoryTest
{
    /** JMS server URL. <B>Adjust for your site!</B> */
    final private static String URL = "tcp://ics-srv02.sns.ornl.gov:61616";
    
    /** JMS topic that should be available at CSS sites. */
    final private static String TOPIC = "LOG";

    @Test
    public void testJMSConnection() throws Exception
    {
        // Connect
        System.out.println("Trying to connect to JMS");
        System.out.println("Server     : " + URL);
        System.out.println("Topic      : " + TOPIC);
        final Connection connection = JMSConnectionFactory.connect(URL);
        connection.start();
        
        System.out.println("Connected  : " + connection.getClientID());

        // Create (unused) producer and consumer
        final Session session = connection.createSession(
                /* transacted */false, Session.AUTO_ACKNOWLEDGE);
        final Topic topic = session.createTopic(TOPIC);
        final MessageProducer producer = session.createProducer(topic);
        producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
        
        final MessageConsumer consumer = session.createConsumer(topic);
        
        // Unclear what to check other than "no exception"
        System.out.println("Destination: " + producer.getDestination());
        assertTrue("LOG used in destination",
                producer.getDestination().toString().indexOf("LOG") >= 0);
        
        // Shutdown
        consumer.close();
        producer.close();
        session.close();
        connection.close();
    }
}
