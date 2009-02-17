package org.csstudio.utility.jmssendcmd;

import java.net.InetAddress;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.Topic;

import org.csstudio.platform.logging.JMSLogMessage;
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
    * @param edm_mode 
     *  @throws Exception on error
     */
    public void send(final String type, final String application,
            final String text, boolean edm_mode) throws Exception
    {
       final MapMessage map = session.createMapMessage();
/**
 * If the type is "put", it is an EDM option.  Different options are added to 
 * the map to reflect the input EDM string.
 */
        if(edm_mode)
        {
           EDMParser parser = new EDMParser(text);
           /**
            * If the parsed EDM input string has an error, return.
            * Otherwise fill the MapMessage with the input EDM values.
            */
           if(parser.hasError()) return;
           map.setString(JMSLogMessage.TYPE, type);
           map.setString(JMSLogMessage.APPLICATION_ID, application);
           map.setString(JMSLogMessage.HOST, parser.getHost());
           map.setString(JMSLogMessage.USER, parser.getUser());
           map.setString(JMSLogMessage.NAME, parser.getPVName());
           map.setString(JMSLogMessage.TEXT, parser.getPVText());
           map.setString(JMSLogEDMMessage.PVVALUE, parser.getValue());
           map.setString(JMSLogEDMMessage.OLDPVVALUE, parser.getOriginalValue());

        }
        else
        {
           map.setString(JMSLogMessage.TYPE, type);
           map.setString(JMSLogMessage.APPLICATION_ID, application);
           map.setString(JMSLogMessage.HOST, host);
           map.setString(JMSLogMessage.USER, user);
           map.setString(JMSLogMessage.TEXT, text);
        }
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
           System.err.println("Error: " + ex.getMessage());
        }
    }
    
    /** @see ExceptionListener */
    public void onException(final JMSException ex)
    {
        ex.printStackTrace();
    }
}
