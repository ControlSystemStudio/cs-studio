package org.csstudio.sns.jms2rdb.perftest;

import javax.jms.Connection;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.Topic;

import org.csstudio.platform.logging.JMSLogMessage;

/** Receives and counts JMS messages.
 *  <p>
 *  Uses CSS 'log' message format.
 *  
 *  @author Kay Kasemir
 *  reviewed by Katia Danilova 08/20/08
 */
@SuppressWarnings("nls")
public class Receiver implements ExceptionListener, MessageListener
{
    final private Session session;
    final private MessageConsumer consumer;
    private int count;
    private int next_num;

    /** Create and start the sender
     *  @param connection
     *  @param topic_name
     *  @throws Exception
     */
    public Receiver(final Connection connection, final String topic_name)
        throws Exception
    {
        connection.setExceptionListener(this);
        connection.start();
        session = connection.createSession(/* transacted */false,
                                           Session.AUTO_ACKNOWLEDGE);
        final Topic topic = session.createTopic(topic_name);
        consumer = session.createConsumer(topic);
        consumer.setMessageListener(this);
        
        count = 0;
        next_num = -1;
    }

    /** {@inhericDoc} */
    /*TODO: descr */
    public void onMessage(final Message msg)
    {
        if (! (msg instanceof MapMessage))
        {
            System.out.println("Received unknown " + msg.getClass().getName());
            return;
        }
        final MapMessage map = (MapMessage) msg;
        try
        {
            final JMSLogMessage log_msg = JMSLogMessage.fromMapMessage(map);
            final int num = Integer.parseInt(log_msg.getText());
            if (next_num > 0  &&  num != next_num)
            {
                System.out.println("Expected " + next_num + ", got " + num);
            }
            next_num = num+1;
            ++count;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    /** Stop the receiver
     *  @throws Exception
     */
    public void shutdown() throws Exception
    {
        consumer.close();
        session.close();
    }

    /** @see ExceptionListener */
    public void onException(final JMSException ex)
    {
        ex.printStackTrace();
    }

    /** TODO: descr */
    public int getMessageCount()
    {
        return count;
    }
}
