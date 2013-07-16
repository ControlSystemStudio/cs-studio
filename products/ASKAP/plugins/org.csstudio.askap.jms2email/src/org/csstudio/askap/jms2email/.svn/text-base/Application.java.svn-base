package org.csstudio.askap.jms2email;

import java.util.logging.Level;

import org.csstudio.askap.jms2email.httpd.ConfigurationServlet;
import org.csstudio.askap.jms2email.httpd.MainServlet;
import org.csstudio.askap.jms2email.httpd.StopServlet;
import org.csstudio.logging.LogConfigurator;
import org.csstudio.platform.httpd.HttpServiceHelper;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.osgi.framework.Constants;
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.HttpService;

public class Application implements IApplication {
	
    /** HTTP Server port */
    private int httpdPort = 9500;
	
	JMSListener jmsListener = null;

    /** JMS Server URL */
	private String jmsUrl = "tcp://localhost:61616";
	
    /** JMS Server topic */
    private String jmsTopic = "demo,LOG,demo_SERVER,demo_TALK,demo_CLIENT,ALARM_SERVER,ALARM_CLIENT,TALK,WRITE";

    /** Filters for suppressed JMS messages
      */
    private String jmsFilters = "ALARM;TEXT=IDLE";
    
    private String mailHost = "mailhost.atnf.CSIRO.AU";
    
    private String fromAddress = "xinyu.wu@csiro.au";
    private String toAddress = "xinyu.wu@csiro.au";
    private String subject = "Testing";
    
    private MessageHandler messageHandler;

	@Override
	public Object start(IApplicationContext context) throws Exception {
        // Read settings from preferences.
        // Order of preference lookup:
        // - GUI applications can provide a preferences GUI to allow
        //   users to change settings
        // - Command-line argument
        //      -pluginCustomization my_customization.ini
        //   used when running the application.
        //   File has lines of format:
        //      plugin-id/pref_key=value
        // - File plugin_customization.ini in the application plugin root dir.
        //      plugin-id/pref_key=value
        // - File preferences.ini in this plugin's root directory:
        //      pref_key=value
        // - If all fails, the 'default' argument to
        //   service.getXXX(.., .., default, ..) is used
        final IPreferencesService service = Platform.getPreferencesService();
        httpdPort  =
                service.getInt(Activator.ID, "httpd_port", httpdPort, null);
        jmsUrl  =
            service.getString(Activator.ID, "jms_url", jmsUrl, null);
        jmsTopic =
            service.getString(Activator.ID, "jms_topic", jmsTopic, null);
        jmsFilters =
            service.getString(Activator.ID, "jms_filters", jmsFilters, null);
        
        mailHost =
                service.getString(Activator.ID, "mail_host", mailHost, null);
        toAddress =
                service.getString(Activator.ID, "to_address", toAddress, null);
        fromAddress =
                service.getString(Activator.ID, "from_address", fromAddress, null);
        subject =
                service.getString(Activator.ID, "subject", subject, null);
        

        LogConfigurator.configureFromPreferences();

        final String version = (String) context.getBrandingBundle().getHeaders().get(Constants.BUNDLE_VERSION);
        Activator.getLogger().log(Level.CONFIG, "Started JMS2Email {0}", version);

        messageHandler = new EmailMessageHandler(mailHost, fromAddress, toAddress, subject);
		Activator
				.getLogger().log(Level.CONFIG,
						"Connecting to email server: {0}; toAddress: {1}; fromAddress: {2}; subject: {3}",
						new Object[] { version, toAddress, fromAddress, subject});
        
        // Start log handler and web interface
        jmsListener =
            new JMSListener(messageHandler, jmsUrl, jmsTopic, jmsFilters);
        
        startHttpd();
        
        jmsListener.start();
        // .. Wait while thread is running ..
        jmsListener.join();

        stopHttpd();
        return IApplication.EXIT_OK;
	}
	
    /** Start the web server */
    private void startHttpd() throws Exception
    {
        final HttpService httpd = HttpServiceHelper.createHttpService(
                Activator.getInstance().getBundle().getBundleContext(), httpdPort);
        final HttpContext context = httpd.createDefaultHttpContext();
        httpd.registerResources("/", "/webroot", context);
        httpd.registerServlet("/main", new MainServlet(jmsListener), null, context);
        httpd.registerServlet("/config", new ConfigurationServlet(this), null, context);
        httpd.registerServlet("/stop", new StopServlet(this), null, context);

        // Format port as string because otherwise it'll show up as "4,913" in US locale
        // when using the log formatter for an integer
        Activator.getLogger().log(Level.CONFIG, "Web server at http://localhost:" + httpdPort + "/main");
    }

    /** Stop the web server */
    private void stopHttpd() throws Exception
    {
        HttpServiceHelper.stopHttpService(httpdPort);
    }


	@Override
	public void stop() {
        Activator.getLogger().info("Stop JMS2Email Service");
        jmsListener.cancel();
	}

	public int getHttpdPort() {
		return httpdPort;
	}

	public String getJmsUrl() {
		return jmsUrl;
	}

	public String getJmsTopic() {
		return jmsTopic;
	}

	public String getJmsFilters() {
		return jmsFilters;
	}

	public String getMailHost() {
		return mailHost;
	}

	public String getFromAddress() {
		return fromAddress;
	}

	public String getToAddress() {
		return toAddress;
	}

	public String getSubject() {
		return subject;
	}
}
