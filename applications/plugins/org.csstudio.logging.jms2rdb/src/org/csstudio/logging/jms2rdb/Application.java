/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.logging.jms2rdb;

import java.util.logging.Level;

import org.csstudio.apputil.args.ArgParser;
import org.csstudio.apputil.args.BooleanOption;
import org.csstudio.logging.JMSLogMessage;
import org.csstudio.logging.LogConfigurator;
import org.csstudio.logging.jms2rdb.httpd.MainServlet;
import org.csstudio.logging.jms2rdb.httpd.StopServlet;
import org.csstudio.platform.httpd.HttpServiceHelper;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.HttpService;

/** Application's 'Main' class.
 *
 *  TODO Remove dependency on JMSLogMessage in platform
 *  @author Kay Kasemir
 *  reviewed by Katia Danilova 08/20/08
 */
@SuppressWarnings("nls")
public class Application implements IApplication
{
    /** HTTP Server port */
    private int httpd_port = 9500;

    /** JMS Server URL */
    private String jms_url = "tcp://localhost:61616";

    /** JMS Server topic */
    private String jms_topic = JMSLogMessage.DEFAULT_TOPIC;

    /** Filters for suppressed JMS messages
     *  @see Filter
     */
    private String jms_filters = "ALARM;TEXT=IDLE";

    /** RDB Server URL */
    private String rdb_url = "jdbc:mysql://[host]/[database]?user=[user]&password=[password]";

    /** RDB Schema */
    private String rdb_schema = "";

    /** Thread that handles the JMS messages */
    private LogClientThread log_client_thread;

    /** {@inheritDoc} */
    @Override
    public Object start(final IApplicationContext context) throws Exception
    {
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
    	// Display configuration info
        final String version = (String) context.getBrandingBundle().getHeaders().get("Bundle-Version");
        final String app_info = context.getBrandingName() + " " + version;
    	
    	// Create parser for arguments and run it.
        final String args[] = (String []) context.getArguments().get("application.args");

        final ArgParser parser = new ArgParser();
        final BooleanOption help_opt = new BooleanOption(parser, "-help", "Display help");
        final BooleanOption version_opt = new BooleanOption(parser, "-version", "Display version info");
        parser.addEclipseParameters();
		try {
			parser.parse(args);
		} catch (final Exception ex) {
			System.out.println(ex.getMessage() + "\n" + parser.getHelp());
			return IApplication.EXIT_OK;
		}
		if (help_opt.get()) {
			System.out.println(app_info + "\n\n" + parser.getHelp());
			return IApplication.EXIT_OK;
		}
		if (version_opt.get()) {
			System.out.println(app_info);
			return IApplication.EXIT_OK;
		}
    	
        final IPreferencesService service = Platform.getPreferencesService();
        httpd_port =
            service.getInt(Activator.ID, "httpd_port", httpd_port, null);
        jms_url =
            service.getString(Activator.ID, "jms_url", jms_url, null);
        jms_topic =
            service.getString(Activator.ID, "jms_topic", jms_topic, null);
        jms_filters =
            service.getString(Activator.ID, "jms_filters", jms_filters, null);
        rdb_url =
            service.getString(Activator.ID, "rdb_url", rdb_url, null);
        rdb_schema =
            service.getString(Activator.ID, "rdb_schema", rdb_schema, null);

        LogConfigurator.configureFromPreferences();

        Activator.getLogger().log(Level.CONFIG, "Started JMS Log Tool {0}", version);

        // Start log handler and web interface
        log_client_thread =
            new LogClientThread(jms_url, jms_topic, rdb_url, rdb_schema,
                                Filter.parse(jms_filters));
        startHttpd();
        log_client_thread.start();
        // .. Wait while thread is running ..
        log_client_thread.join();

        // Shutdown
        stopHttpd();

        return IApplication.EXIT_OK;
    }

    /** Start the web server */
    private void startHttpd() throws Exception
    {
        final HttpService httpd = HttpServiceHelper.createHttpService(
                Activator.getInstance().getBundle().getBundleContext(), httpd_port);
        final HttpContext context = httpd.createDefaultHttpContext();
        httpd.registerResources("/", "/webroot", context);
        httpd.registerServlet("/main", new MainServlet(log_client_thread), null, context);
        httpd.registerServlet("/stop", new StopServlet(this), null, context);

        // Format port as string because otherwise it'll show up as "4,913" in US locale
        // when using the log formatter for an integer
        Activator.getLogger().log(Level.CONFIG, "Web server at http://localhost:" + httpd_port + "/main");
    }

    /** Stop the web server */
    private void stopHttpd() throws Exception
    {
        HttpServiceHelper.stopHttpService(httpd_port);
    }

    /** {@inheritDoc} */
    @Override
    public void stop()
    {
        Activator.getLogger().info("Stop requested");
        log_client_thread.cancel();
    }
}
