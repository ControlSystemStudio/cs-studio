package org.csstudio.utility.jmssendcmd;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.Topic;

import org.csstudio.apputil.args.ArgParser;
import org.csstudio.apputil.args.BooleanOption;
import org.csstudio.apputil.args.StringOption;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.utility.jms.JMSConnectionFactory;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

/** Eclipse application for JMS 'Send' command
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class Application implements IApplication, ExceptionListener
{
    private Connection connection;
    private Session session;
    private Topic topic;
    private MessageProducer producer;

    /** @see IApplication */
    public Object start(IApplicationContext context) throws Exception
    {
        System.out.println("Hello, World");
        
        // Create parser for arguments and run it.
        final String args[] =
            (String []) context.getArguments().get("application.args");

        final ArgParser parser = new ArgParser();
        final StringOption url = new StringOption(parser, "url", "JMS Server URL", "tcp://localhost:61616");
        final StringOption topic = new StringOption(parser, "topic", "JMS Topic", "TEST");
        final BooleanOption help = new BooleanOption(parser, "h", "Help");
        
        try
        {
            parser.parse(args);
        }
        catch (Exception ex)
        {
            System.err.println("Error: " + ex.getMessage());
            System.err.println(parser.getHelp());
            return IApplication.EXIT_OK;
        }
        
        if (help.get())
        {
            System.out.println(parser.getHelp());
            return IApplication.EXIT_OK;
        }
        
        System.out.println("URL  : " + url.get());
        System.out.println("Topic: " + topic.get());
        
        try
        {
            connect(url.get(), topic.get());
            run();
            disconnect();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        
        return IApplication.EXIT_OK;
    }

    /** Connect to JMS
     *  @param url
     *  @param topic_name
     *  @throws Exception
     */
    private void connect(final String url, final String topic_name) throws Exception
    {
        connection = JMSConnectionFactory.connect(url);
        connection.setExceptionListener(this);
        connection.start();
        session = connection.createSession(/* transacted */ false,
                                           Session.AUTO_ACKNOWLEDGE);
        topic = session.createTopic(topic_name);
        producer = session.createProducer(topic);
        producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
    }

    /** Send Messages to JMS */
    private void run()
    {
        // TODO Auto-generated method stub
        
    }

    /** Disconnect from JMS */
    private void disconnect()
    {
        try
        {
            producer.close();
            session.close();
        }
        catch (Exception ex)
        {
            CentralLogger.getInstance().getLogger(this).warn(
                    "JMS shutdown error " + ex.getMessage(), ex);
        }
    }
    
    /** @see ExceptionListener */
    public void onException(final JMSException ex)
    {
        CentralLogger.getInstance().getLogger(this).error(
                "JMS Exception " + ex.getMessage(), ex); //$NON-NLS-1$
    }

    /** @see IApplication */
    public void stop()
    {
        // Ignore
    }
}
