package org.csstudio.sns.jms2rdb;

import java.net.InetAddress;
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

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.csstudio.platform.utility.jms.JMSConnectionFactory;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.logging.JMSLogMessage;
import org.csstudio.sns.jms2rdb.rdb.RDBWriter;

/** Thread that receives log messages and sends them to the RDB.
 *  <p>
 *  There is no further queuing in here.
 *  All messages are directly written to the RDB.
 *  <p>
 *  If messages arrive quicker than they can be written to RDB,
 *  simple test showed that ActiveMQ JMS server will queue them up
 *  for this "consumer" as long as the consumer stays connected.
 *  TODO: Limits and config. of JMS server queue unclear.
 *        Queue in here?
 *
 *  @author Kay Kasemir
 *  reviewed by Katia Danilova 08/20/08
 */
@SuppressWarnings("nls")
public class LogClientThread extends Thread
    implements ExceptionListener, MessageListener
{
    /** On JMS or RDB errors, thread will disconnect, wait, then re-connect
     *  using this delay.
     */
    private static final int RETRY_DELAY_MS = 5000;

    /** JMS Server URL */
    final private String jms_url;
    
    /** JMS topic */
    final private String jms_topic;
    
    /** RDB Server URL */
    final private String rdb_url;

    /** RDB Schema */
    final private String rdb_schema;

    /** Message filters */
    final private Filter filters[];
    
    /** Log4j Logger */
    final private Logger logger;

    /** Flag that tells thread to run or stop. */
    private volatile boolean run = true;

    /** RDB Writer for log messages */
    private RDBWriter rdb_writer;

    /** Counter for received JMS messages */
    private int message_count = 0;
    
    /** Last JMS Message */
    private MapMessage last_message = null;

    /** Last error message or <code>null</code> */
    private String last_error = "";

    /** Constructor
     *  @param jms_url JMS server URL
     *  @param jms_topic JMS topic (or list of topics, separated by ',')
     *  @param rdb_url RDB server URL
     *  @param rdb_schema RDB schema or ""
     */
    public LogClientThread(final String jms_url, final String jms_topic,
            final String rdb_url, final String rdb_schema,
            final Filter filters[])
    {
        super("LogClientThread");
        this.jms_url = jms_url;
        this.jms_topic = jms_topic;
        this.rdb_url = rdb_url;
        this.rdb_schema = rdb_schema;
        this.filters = filters;
        logger = CentralLogger.getInstance().getLogger(this);
        
        for (Filter filter : filters)
            logger.info(filter);
    }
    
    /** @return Number of messages received */
    public synchronized int getMessageCount()
    {
        return message_count;
    }
    
    /** @return Last messages received or <code>null</code> */
    public synchronized MapMessage getLastMessage()
    {
        return last_message;
    }

    /** @return Last messages received */
    public synchronized String getLastError()
    {
        return last_error;
    }

    /** Connect to JMS, handle messages */
    @Override
    public void run()
    {
        while (run)
        {
            Connection jms_connection = null;
            rdb_writer = null;
            try
            {
                // First open RDB, then the JMS client that writes to RDB
                rdb_writer = new RDBWriter(rdb_url, rdb_schema);
                logger.info("Connected to RDB " + rdb_url);
                jms_connection = connectJMS();
                
                addStartMessage();
                
                // Incoming JMS messages are handled in onMessage,
                // so nothing to do here but wait...
                synchronized (this)
                {
                    wait();
                }
            }
            catch (Exception ex)
            {
                synchronized (this)
                {
                    last_error = ex.getMessage();
                }
                logger.error(ex);
            }
            finally
            {
                // Stop JMS...
                if (jms_connection != null)
                {
                    try
                    {
                        jms_connection.close();
                    }
                    catch (JMSException e)
                    {
                        logger.error(e);
                    }
                }
                // .. then the RDB used by the JMS client.
                if (rdb_writer != null)
                {
                    rdb_writer.close();
                    rdb_writer = null;
                }
            }
            // Did we wake up & close connections because of error
            // or because of requested shutdown?
            if (run)
            {   // Error. Wait a little before trying again
                try
                {
                    Thread.sleep(RETRY_DELAY_MS);
                }
                catch (InterruptedException ex)
                {
                    logger.warn(ex);
                }
            }
        }
    }

    /** Add a startup message. */
    private void addStartMessage() throws Exception
    {
        final Calendar now = Calendar.getInstance();
        final String host = InetAddress.getLocalHost().getHostName();
        final String user = System.getProperty("user.name");
        final JMSLogMessage initial_msg = new JMSLogMessage(
                "INFO: JMS Log Tool started",
                Level.INFO.toString(), now, now,
                "LogClientThread", "run", "", "JMSLogTool", host, user);
        rdb_writer.write(initial_msg);
    }

    /** Connect to JMS server
     *  @return JMS Connection
     *  @throws JMSException on error
     */
    private Connection connectJMS() throws JMSException
    {
        final Connection connection = JMSConnectionFactory.connect(jms_url);
        connection.setExceptionListener(this);
        connection.start();
        final Session session = connection.createSession(/* transacted */false,
                                           Session.AUTO_ACKNOWLEDGE);
        // Subscribe to list of topics
        final String[] topic_names = jms_topic.split(", *");
        for (String topic_name : topic_names)
        {
            final Topic topic = session.createTopic(topic_name);
            final MessageConsumer consumer = session.createConsumer(topic);
            consumer.setMessageListener(this);
            logger.info("Accepting messages for '" + topic_name
                    + "' at " + jms_url);
        }
        return connection;
    }

    /** Ask thread to stop. Does not block for thread to actually exit */
    public void cancel()
    {
        run  = false;
        synchronized (this)
        {
            notifyAll();
        }
    }
    
    /** @see JMS ExceptionListener */
    public void onException(final JMSException ex)
    {
        logger.error("JMS Exception", ex);
    }

    /** @see JMS MessageListener */
    public void onMessage(final Message message)
    {
        try
        {
            if (message instanceof MapMessage)
            {
                final MapMessage map = (MapMessage) message;
                for (Filter fil : filters)
                    if (fil.matches(map))
                        return;
                synchronized (this)
                {
                    ++message_count;
                    last_message  = map;
                }
                rdb_writer.write(map);
                if (logger.isDebugEnabled())
                {
                	String text = map.getString("TEXT");
                	if (text == null)
                		text = "message";
            		logger.debug("Received and wrote " + text);
                }
            }
            else
                logger.debug("Received " + message.getClass().getName());                
        }
        catch (Exception ex)
        {
            synchronized (this)
            {
                last_error = ex.getMessage();
            }
            logger.error("Message handling error", ex);
            synchronized (this)
            {
                // Leave run == true, toggle a restart
                notifyAll();
            }
        }
    }
}
