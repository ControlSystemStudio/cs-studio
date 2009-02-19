package org.csstudio.platform.utility.jms;

import java.io.IOException;

import javax.jms.Connection;
import javax.jms.JMSException;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.transport.TransportListener;

/** Helper for connecting to a JMS server.
 *  Shields from the underling ActiveMQ API,
 *  only providing a <code>javax.jms.Connection</code>.
 *  
 *  @author Kay Kasemir
 */
public class JMSConnectionFactory
{
    /** Connect to JMS
     *  @param url URL of server. Details can differ for ActiveMQ
     *             or other implementations.
     *  @return Connection
     *  @throws JMSException on error
     */
    public static Connection connect(final String url) throws JMSException
    {
        return connect(url, ActiveMQConnection.DEFAULT_USER,
                ActiveMQConnection.DEFAULT_PASSWORD);
    }

    /** Connect to JMS
     *  @param url URL of server. Details can differ for ActiveMQ
     *             or other implementations.
     *  @param user JMS user name
     *  @param password JMS password
     *  @return Connection
     *  @throws JMSException on error
     */
    public static Connection connect(final String url,
            final String user, final String password) throws JMSException
    {
        // Instead of using JNDI lookup like this...
        //   Context ctx = new InitialContext();
        //   QueueConnectionFactory queueConnectionFactory = 
        //     (QueueConnectionFactory) ctx.lookup("SomeConnectionFactory");
        // ... which requires an appropriate jndi.properties file,
        // we directly use the ActiveMQConnectionFactory.
        final ActiveMQConnectionFactory factory =
            new ActiveMQConnectionFactory(user, password, url);
        return factory.createConnection();
    }
    
    /** Add a listener that is notified about JMS connection issues
     *  to a connection.
     *  <p>
     *  The implementation depends on the underlying API.
     *  What works for ActiveMQ might not be available for
     *  other implementations, in which case the listener
     *  might never get called.
     *  
     *  @param connection Connection to monitor
     *  @param listener JMSConnectionListener to notify
     */
    public static void addListener(final Connection connection,
            final JMSConnectionListener listener)
    {
        final ActiveMQConnection amq_connection =
                                               (ActiveMQConnection) connection;
        amq_connection.addTransportListener(new TransportListener()
        {
            public void onCommand(Object cmd)
            {
                // Ignore
                // Looks like one could track almost every send/receive
                // in here
            }

            public void onException(IOException ex)
            {
                // Ignore
            }

            public void transportInterupted()
            {
                listener.linkDown();
            }

            public void transportResumed()
            {
                listener.linkUp(amq_connection.getTransport().getRemoteAddress());
            }
        });
    }
}
