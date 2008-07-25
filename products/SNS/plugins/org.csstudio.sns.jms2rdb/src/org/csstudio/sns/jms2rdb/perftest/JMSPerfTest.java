package org.csstudio.sns.jms2rdb.perftest;

import javax.jms.Connection;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.csstudio.platform.utility.jms.JMSConnectionFactory;
import org.junit.Test;

/** Simple send/receive performance test.
 *  <p>
 *  Receiver checks the sequence of received messages
 *  and will detect missing or double messages.
 *  <p>
 *  Because the start and stop aren't further coordinated,
 *  the receiver will miss some initial or final messages.
 *  <p>
 *  Test with laptop to srv02 and back: about 1000 msg/sec.
 *  
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class JMSPerfTest
{
    /** JMS Server URL */
    final private static String URL = "tcp://ics-srv02.sns.ornl.gov:61616";
    /** Topic.
     *  'LOG' might get logged to RDB, 'TEST' should be only for this test.
     */ 
    final private static String TOPIC = "TEST";
    
    /** Test runtime */
    final private static int SECONDS = 30;

    @Test
    public void perfTest() throws Exception
    {
        // Log4j
        BasicConfigurator.configure();
        Logger.getRootLogger().setLevel(Level.WARN);
        
        // Create Receiver, then Sender
        final Connection connection = JMSConnectionFactory.connect(URL);
        
        final Sender sender = new Sender(connection, TOPIC);
        final Receiver receiver = new Receiver(connection, TOPIC);

        // Run for some time
        System.out.println("Runtime: " + SECONDS + " seconds");
        Thread.sleep(SECONDS * 1000);
        
        // Stop sender, then receiver
        sender.shutdown();
        receiver.shutdown();
        
        // Stats
        int count = sender.getMessageCount();
        System.out.format("Sender  : %10d messages = %10.1f msg/sec\n",
                count, ((double) count)/SECONDS);
        count = receiver.getMessageCount();
        System.out.format("Receiver: %10d messages = %10.1f msg/sec\n",
                count, ((double) count)/SECONDS);
    }
}
