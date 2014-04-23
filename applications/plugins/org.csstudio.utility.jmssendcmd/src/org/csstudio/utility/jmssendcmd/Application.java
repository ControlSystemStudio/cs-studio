package org.csstudio.utility.jmssendcmd;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.apputil.args.ArgParser;
import org.csstudio.apputil.args.BooleanOption;
import org.csstudio.apputil.args.StringOption;
import org.csstudio.logging.LogConfigurator;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

/** Eclipse application for JMS 'Send' command
 *  @author Kay Kasemir
 *  @author Delphy Armstrong
 */
@SuppressWarnings("nls")
public class Application implements IApplication
{
    /** Plugin ID registered in MANIFEST.MF */
    final public static String PLUGIN_ID = "org.csstudio.utility.jmssendcmd";

    final private static Logger logger = Logger.getLogger(PLUGIN_ID);

    final private static String DEFAULT_URL = "tcp://localhost:61616";
    final private static String DEFAULT_TOPIC = "TEST";
    final private static String DEFAULT_TYPE = "log";
    private static final String DEFAULT_APP = "JMSSender";
    private String application;
    private String type;
    private boolean edm_mode;

    /** @return Logger for plugin ID */
    public static Logger getLogger()
    {
        return logger;
    }

    /** @see IApplication */
    @Override
    public Object start(IApplicationContext context) throws Exception
    {
    	// Display configuration info
        final String version = (String) context.getBrandingBundle().getHeaders().get("Bundle-Version");
        final String app_info = context.getBrandingName() + " " + version;
    	
        // Create parser for arguments and run it.
        final String args[] =
            (String []) context.getArguments().get("application.args");
        final ArgParser parser = new ArgParser();
        final BooleanOption help_opt = new BooleanOption(parser, "-help", "Display help");
        final BooleanOption version_opt = new BooleanOption(parser, "-version", "Display version info");
        final StringOption url = new StringOption(parser,
                "-url", "tcp:...", "JMS Server URL (default: " + DEFAULT_URL + ")", DEFAULT_URL);
        final StringOption jms_user = new StringOption(parser,
                "-jms_user", "fred", "JMS User Name", null);
        final StringOption jms_pass = new StringOption(parser,
                "-jms_pass", "secret", "JMS Password", null);
        final StringOption topic = new StringOption(parser,
                "-topic", DEFAULT_TOPIC, "JMS Topic (default: " + DEFAULT_TOPIC + ")", DEFAULT_TOPIC);
        final StringOption type = new StringOption(parser,
                "-type", DEFAULT_TYPE, "Message type (default: " + DEFAULT_TYPE + ")", DEFAULT_TYPE);
        final StringOption app = new StringOption(parser,
                "-app", DEFAULT_APP, "Application type (default: " + DEFAULT_APP + ")", DEFAULT_APP);
        final StringOption text = new StringOption(parser,
                "-text", "'This is a test'", "Send given text (default: read from stdin)", null);

        /**
         * Use the -edm_mode if the string being parsed is from EDM.  This will set
         * the edm_mode variable to true.
         */
        final BooleanOption edm_mode = new BooleanOption(parser,
              "-edm_mode", "Parse EDM 'write' log formatted input");

        try
        {
            parser.parse(args);
        }
        catch (Exception ex)
        {
            System.out.println(app_info + " error: " + ex.getMessage());
            System.err.println(parser.getHelp());
            return IApplication.EXIT_OK;
        }

        if (help_opt.get())
        {
            System.out.println(app_info + "\n\n" + parser.getHelp());
            return IApplication.EXIT_OK;
        }
        if (version_opt.get())
        {
            System.out.println(app_info);
            return IApplication.EXIT_OK;
        }

        this.type = type.get();
        application = app.get();
        this.edm_mode = edm_mode.get();

        LogConfigurator.configureFromPreferences();

        // Show basic info unless in 'EDM' mode
        if (! this.edm_mode)
        {
            System.out.println(app_info);
            System.out.println("URL        : " + url.get());
            System.out.println("Topic      : " + topic.get());
            System.out.println("Type       : " + type.get());
            System.out.println("Application: " + app.get());
        }
        try
        {
            final JMSSender sender = new JMSSender(url.get(), jms_user.get(),
                    jms_pass.get(), topic.get());
            if (text.get() != null)
                sender.send(type.get(), application, text.get(), false);
            else
                sendMsgFromInput(sender);
            sender.disconnect();
        }
        catch (Exception ex)
        {
            getLogger().log(Level.SEVERE, "Application error", ex);
        }

        return IApplication.EXIT_OK;
    }

    /** Read message text from stdin, send to JMS
     *  @param sender JMSSender
     *  @throws Exception on error
     */
    private void sendMsgFromInput(final JMSSender sender) throws Exception
    {
        final BufferedReader in =
            new BufferedReader(new InputStreamReader(System.in));
        // Prompt unless in EDM mode
        if (!edm_mode)
            System.out.println("Enter message lines. Ctrl-D to exit.");
        while (true)
        {
            if (!edm_mode)
                System.out.print(">");
            final String text = in.readLine();
            if (text == null)
                break;
            sender.send(type, application, text, edm_mode);
        }
    }

    /** @see IApplication */
    @Override
    public void stop()
    {
        // Ignore
    }
}
