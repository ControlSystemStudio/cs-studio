package org.csstudio.alarm.beast.configtool;

import java.net.InetAddress;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.Topic;

import org.csstudio.logging.JMSLogMessage;
import org.csstudio.platform.utility.jms.JMSConnectionFactory;

/** Send messages (MapMessage) to JMS
 *  @author Kay Kasemir
 *  @author Delphy Armstrong
 */
@SuppressWarnings("nls")
public class JMSSender implements ExceptionListener
{
    private String user;
    private String host;

    private Connection connection;
    private Session session;
    private Topic topic;
    private MessageProducer producer;

    /** Initialize
     *  @param url JMS Server URL
     *  @param jms_user ... user name
     *  @param jms_pass ... password
     *  @param topic_name JMS topic
     *  @throws Exception on error
     */
    public JMSSender(final String url, final String jms_user,
            final String jms_pass, final String topic_name) throws Exception
    {
        user = System.getProperty("user.name");
        if (user == null  ||  user.length() <= 0)
            user = "<unknown>";
        try
        {
            host = InetAddress.getLocalHost().getHostName();
        }
        catch (Exception ex)
        {
            host = "<unknown>";
        }
        connect(url, jms_user, jms_pass, topic_name);
    }

    /** Connect to JMS
     *  @param url
     *  @param jms_user
     *  @param jms_pass
     *  @param topic_name
     *  @throws Exception
     */
    private void connect(final String url, final String jms_user,
            final String jms_pass, final String topic_name) throws Exception
    {
        connection = JMSConnectionFactory.connect(url, jms_user, jms_pass);
        connection.setExceptionListener(this);
        connection.start();
        session = connection.createSession(/* transacted */ false,
                                           Session.AUTO_ACKNOWLEDGE);
        topic = session.createTopic(topic_name);
        producer = session.createProducer(topic);
        producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
    }

    /** Send Message to JMS
     *  @param type Message TYPE
     *  @param application Message APPLICATION
     *  @param text Message TEXT
     *  @throws Exception on error
     */
    public void send(final String type, final String application,
            final String text) throws Exception
    {
       final MapMessage map = session.createMapMessage();

       map.setString(JMSLogMessage.TYPE, type);
       map.setString(JMSLogMessage.APPLICATION_ID, application);
       map.setString(JMSLogMessage.HOST, host);
       map.setString(JMSLogMessage.USER, user);
       map.setString(JMSLogMessage.TEXT, text);

        /**
         * Send the MessageMap to the JMS producer to produce and send the
         * JMS message.
         */
        producer.send(map);
    }

    /** Disconnect from JMS */
    public void disconnect()
    {
        try
        {
            producer.close();
            session.close();
        }
        catch (Exception ex)
        {
            System.out.println("JMS shutdown error:");
            ex.printStackTrace();
        }
    }

    /** @see ExceptionListener */
    @Override
    public void onException(final JMSException ex)
    {
        System.out.println("JMS exception:");
        ex.printStackTrace();
    }
}