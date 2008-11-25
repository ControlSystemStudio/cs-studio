package org.csstudio.utility.jmssendcmd;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.Topic;

import org.csstudio.apputil.args.ArgParser;
import org.csstudio.apputil.args.BooleanOption;
import org.csstudio.apputil.args.StringOption;
import org.csstudio.platform.logging.JMSLogMessage;
import org.csstudio.platform.utility.jms.JMSConnectionFactory;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

/** Eclipse application for JMS 'Send' command
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class Application implements IApplication, ExceptionListener
{
    private String application = "JMSSender";
    private String type;
    private String user;
    private String host;

    private Connection connection;
    private Session session;
    private Topic topic;
    private MessageProducer producer;
    

    /** Initialize */
    public Application()
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
    }

    /** @see IApplication */
    public Object start(IApplicationContext context) throws Exception
    {
        // Create parser for arguments and run it.
        final String args[] =
            (String []) context.getArguments().get("application.args");
        final ArgParser parser = new ArgParser();
        final StringOption url = new StringOption(parser,
                "-url", "JMS Server URL", "tcp://localhost:61616");
        final StringOption jms_user = new StringOption(parser,
                "-jms_user", "JMS User Name", null);
        final StringOption jms_pass = new StringOption(parser,
                "-jms_pass", "JMS Password", null);
        final StringOption topic = new StringOption(parser,
                "-topic", "JMS Topic", "TEST");
        final StringOption type = new StringOption(parser,
                "-type", "Message type", "log");
        final StringOption app = new StringOption(parser,
                "-app", "Application type", "JMS Log Sender");
        final BooleanOption help = new BooleanOption(parser,
                "-h", "Help");
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
        
        System.out.println("URL        : " + url.get());
        System.out.println("Topic      : " + topic.get());
        System.out.println("Type       : " + type.get());
        System.out.println("Application: " + app.get());
        this.type = type.get();
        application = app.get();
        try
        {
            connect(url.get(), jms_user.get(), jms_pass.get(), topic.get());
            sendMsg();
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
    
    /** Send Messages to JMS */
    private void sendMsg()
    {
        final BufferedReader in =
            new BufferedReader(new InputStreamReader(System.in));
        try
        {
            System.out.println("Enter message lines. Ctrl-D to exit.");
            while (true)
            {
                System.out.print(">");
                final String text = in.readLine();
                if (text == null)
                    break;

                // TODO decode more message properties from input?
                final MapMessage map = session.createMapMessage();
                map.setString(JMSLogMessage.TYPE, type);
                map.setString(JMSLogMessage.APPLICATION_ID, application);
                map.setString(JMSLogMessage.HOST, host);
                map.setString(JMSLogMessage.USER, user);
                map.setString(JMSLogMessage.TEXT, text);
                producer.send(map);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
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
            ex.printStackTrace();
        }
    }
    
    /** @see ExceptionListener */
    public void onException(final JMSException ex)
    {
        ex.printStackTrace();
    }

    /** @see IApplication */
    public void stop()
    {
        // Ignore
    }
}
