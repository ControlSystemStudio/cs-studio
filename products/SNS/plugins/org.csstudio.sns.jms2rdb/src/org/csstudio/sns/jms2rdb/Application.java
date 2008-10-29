package org.csstudio.sns.jms2rdb;

import org.apache.log4j.Logger;
import org.csstudio.platform.httpd.HttpServiceHelper;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.logging.JMSLogMessage;
import org.csstudio.sns.jms2rdb.httpd.MainServlet;
import org.csstudio.sns.jms2rdb.httpd.StopServlet;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.HttpService;

/** Application's 'Main' class.
 *  @author Kay Kasemir
 *  reviewed by Katia Danilova 08/20/08
 *  
 *  TODO Add filter to suppress e.g. alarm/IDLE messages
 *  That could be done with configurable filter for
 *  TOPIC=ALARM
 *  TEXT=IDLE
 *  Check what else could be suppressed, then determine which
 *  properties need to be checked.
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

    /** RDB Server URL */
    private String rdb_url = "jdbc:mysql://[host]/[database]?user=[user]&password=[password]";

    /** RDB Schema */
    private String rdb_schema = "";

    /** Log4j Logger */
    private Logger logger;
    
    /** Thread that handles the JMS messages */
    private LogClientThread log_client_thread;
    
    /** {@inheritDoc} */
    public Object start(IApplicationContext context) throws Exception
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
        final IPreferencesService service = Platform.getPreferencesService();
        httpd_port =
            service.getInt(Activator.ID, "httpd_port", httpd_port, null);
        jms_url =
            service.getString(Activator.ID, "jms_url", jms_url, null);
        jms_topic =
            service.getString(Activator.ID, "jms_topic", jms_topic, null);
        rdb_url =
            service.getString(Activator.ID, "rdb_url", rdb_url, null);
        rdb_schema =
            service.getString(Activator.ID, "rdb_schema", rdb_schema, null);
        
        // Log4j and logging setup
        logger = CentralLogger.getInstance().getLogger(this);
        logger.info("JMS Log Tool started");

        // Start log handler and web interface
        log_client_thread =
            new LogClientThread(jms_url, jms_topic, rdb_url, rdb_schema);
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
        logger.info("Web server at http://localhost:" + httpd_port);
    }

    /** Stop the web server */
    private void stopHttpd() throws Exception
    {
        HttpServiceHelper.stopHttpService(httpd_port);
    }

    /** {@inheritDoc} */
    public void stop()
    {
        logger.info("Stop requested");
        log_client_thread.cancel();
    }
}
