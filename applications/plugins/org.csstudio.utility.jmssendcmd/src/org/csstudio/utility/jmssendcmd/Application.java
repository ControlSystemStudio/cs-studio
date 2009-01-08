package org.csstudio.utility.jmssendcmd;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.csstudio.apputil.args.ArgParser;
import org.csstudio.apputil.args.BooleanOption;
import org.csstudio.apputil.args.StringOption;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

/** Eclipse application for JMS 'Send' command
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class Application implements IApplication
{
    final private static String DEFAULT_URL = "tcp://localhost:61616";
    final private static String DEFAULT_TOPIC = "TEST";
    final private static String DEFAULT_TYPE = "log";
    private static final String DEFAULT_APP = "JMSSender";
    private String application;
    private String type;
    
    /** @see IApplication */
    public Object start(IApplicationContext context) throws Exception
    {
        // Create parser for arguments and run it.
        final String args[] =
            (String []) context.getArguments().get("application.args");
        final ArgParser parser = new ArgParser();
        final StringOption url = new StringOption(parser,
                "-url", "JMS Server URL (default: " + DEFAULT_URL + ")", DEFAULT_URL);
        final StringOption jms_user = new StringOption(parser,
                "-jms_user", "JMS User Name", null);
        final StringOption jms_pass = new StringOption(parser,
                "-jms_pass", "JMS Password", null);
        final StringOption topic = new StringOption(parser,
                "-topic", "JMS Topic (default: " + DEFAULT_TOPIC + ")", DEFAULT_TOPIC);
        final StringOption type = new StringOption(parser,
                "-type", "Message type (default: " + DEFAULT_TYPE + ")", DEFAULT_TYPE);
        final StringOption app = new StringOption(parser,
                "-app", "Application type (default: " + DEFAULT_APP + ")", DEFAULT_APP);
        final StringOption text = new StringOption(parser,
                "-text", "Send given text (default: read from stdin)", null);
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
            final JMSSender sender = new JMSSender(url.get(), jms_user.get(),
                    jms_pass.get(), topic.get());
            if (text.get() != null)
                sender.send(type.get(), application, text.get());
            else
                sendMsgFromInput(sender);
            sender.disconnect();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        
        return IApplication.EXIT_OK;
    }

    /** Read message text from stdin, send to JMS
     *  @param sender JMSSender
     */
    private void sendMsgFromInput(final JMSSender sender)
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
                // TODO Option to decode more message properties from input.
                //
                // EDM will send text lines with tag/value pairs,
                // one message per line:
                // user="..." host="..." dsp="..."  <more tags>\n
                // user="..." host="..." dsp="..."  <more tags>\n
                //
                // Meaning of the tags:
                // user = user name
                // host = host name
                // dsp = X Display name
                // name = pv name
                // old = old pv value
                // new = new pv value
                //
                // Example:
                // ssh = "remote-ip remote-port local-ip local-port"
                // user="sinclair" host="orib36"
                // ssh="::ffff:192.168.18.51 43902 ffff:160.91.72.139 22"
                // dsp=":0.0" name="orib36:ao0" old="8.131325" new="8.231325"
                sender.send(type, application, text);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    /** @see IApplication */
    public void stop()
    {
        // Ignore
    }
}
