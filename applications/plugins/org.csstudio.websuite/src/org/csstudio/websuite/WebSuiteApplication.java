
/*
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */

package org.csstudio.websuite;

import java.io.IOException;
import java.net.URL;
import javax.servlet.ServletException;
import org.csstudio.platform.httpd.HttpServiceHelper;
import org.csstudio.websuite.ams.servlet.AmsServlet;
import org.csstudio.websuite.dao.AlarmMessageListProvider;
import org.csstudio.websuite.dao.ChannelMessagesProvider;
import org.csstudio.websuite.internal.PreferenceConstants;
import org.csstudio.websuite.management.MessageCounter;
import org.csstudio.websuite.management.MessageDeleter;
import org.csstudio.websuite.management.Restart;
import org.csstudio.websuite.management.Stop;
import org.csstudio.websuite.servlet.AlarmViewServletHtml;
import org.csstudio.websuite.servlet.AlarmViewServletXml;
import org.csstudio.websuite.servlet.ChannelViewServlet;
import org.csstudio.websuite.servlet.ChannelViewServletHtml;
import org.csstudio.websuite.servlet.ChannelViewServletXml;
import org.csstudio.websuite.servlet.DataExporter;
import org.csstudio.websuite.servlet.FlashInfoServlet;
import org.csstudio.websuite.servlet.Halle55;
import org.csstudio.websuite.servlet.HowToSearchServlet;
import org.csstudio.websuite.servlet.HowToServlet;
import org.csstudio.websuite.servlet.HowToViewServletHtml;
import org.csstudio.websuite.servlet.InfoServlet;
import org.csstudio.websuite.servlet.IocListServlet;
import org.csstudio.websuite.servlet.IocViewServlet;
import org.csstudio.websuite.servlet.PersonalPVInfoEditServlet;
import org.csstudio.websuite.servlet.PersonalPVInfoListServlet;
import org.csstudio.websuite.servlet.PersonalPVInfoServlet;
import org.csstudio.websuite.servlet.RedirectServlet;
import org.csstudio.websuite.servlet.Wetter;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;
import org.remotercp.common.tracker.IGenericServiceListener;
import org.remotercp.service.connection.session.ISessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The main application class.
 *
 * @author Markus Moeller
 * @version 1.0
 */
public class WebSuiteApplication implements IApplication, Stoppable,
                                            RemotlyAccessible, IGenericServiceListener<ISessionService> {

    /** The provider of the alarm messages */
    private AlarmMessageListProvider alarmListProvider;

    /** Platform logger */
    private static final Logger LOG = LoggerFactory.getLogger(WebSuiteApplication.class);

    /** Object for synchronization */
    private final Object lock;

    /** Service for the XMPP login */
    private ISessionService xmppService;

    /** The port that is used by JETTY */
    private int jettyPort;

    /** Flag that indicates whether or not the application is running */
    private boolean running;

    /** Flag that indicates whether or not the application should be restarted */
    private boolean restart;

    /** Simple constructor */
    public WebSuiteApplication() {
        lock = new Object();
        running = true;
        restart = false;
        xmppService = null;
    }

    /**
     * @see org.eclipse.equinox.app.IApplication#start(org.eclipse.equinox.app.IApplicationContext)
     */
    @Override
	public Object start(final IApplicationContext context) throws Exception {

        final IPreferencesService preferences = Platform.getPreferencesService();

        jettyPort = preferences.getInt(WebSuiteActivator.PLUGIN_ID, PreferenceConstants.JETTY_PORT, 8181, null);
        LOG.info("Using port {} for JETTY.", jettyPort);

        connectToXMPPServer();
        
        try {
            final HttpService http = HttpServiceHelper.createHttpService(WebSuiteActivator.getBundleContext(), jettyPort);
            configureHttpService(http);
        } catch(final Exception e) {
            LOG.error("[*** Exception ***]: " + e.getMessage());
        }

        // Creates instances of DAO objects for messages
        AlarmMessageListProvider.createInstance();
        alarmListProvider = AlarmMessageListProvider.getInstance();

        ChannelMessagesProvider.createInstance();

        context.applicationRunning();

        while(running) {

            synchronized(lock) {

                try {
                    lock.wait();
                } catch(final InterruptedException ie) {
                    LOG.warn("Websuite was interrupted: ", ie);
                }
            }
        }

        HttpServiceHelper.stopHttpService(jettyPort);
        AlarmMessageListProvider.getInstance().closeJms();

        if (xmppService != null) {
            xmppService.disconnect();
            LOG.info("XMPP connection disconnected.");
        }

        Integer exitCode;
        if(restart) {
            LOG.info("Restarting application.");
            exitCode = IApplication.EXIT_RESTART;
        } else {
            LOG.info("Stopping application.");
            exitCode = IApplication.EXIT_OK;
        }

        return exitCode;
    }

    /** Register resources and servlet */
    private void configureHttpService(final HttpService http) throws NamespaceException, ServletException {

        final IPreferencesService preferences = Platform.getPreferencesService();
        final HttpContext httpContext = http.createDefaultHttpContext();

        //Collect servlet path from configuration
        final String htmlServletAddress = preferences.getString(WebSuiteActivator.PLUGIN_ID, PreferenceConstants.HTML_SERVLET_ADDRESS, "", null);
        final String xmlServletAddress = preferences.getString(WebSuiteActivator.PLUGIN_ID, PreferenceConstants.XML_SERVLET_ADDRESS, "", null);
        final String xmlChannelServletAddress = preferences.getString(WebSuiteActivator.PLUGIN_ID, PreferenceConstants.XML_CHANNEL_SERVLET_ADDRESS, "", null);
        final String htmlChannelServletAddress = preferences.getString(WebSuiteActivator.PLUGIN_ID, PreferenceConstants.HTML_CHANNEL_SERVLET_ADDRESS, "", null);

        http.registerResources("/style", "/webapp/style", httpContext);
        http.registerResources("/images", "/webapp/images", httpContext);
        http.registerResources("/html", "/webapp/html", httpContext);
        http.registerResources("/config", "/webapp/config", httpContext);
        
        //creates servlet according to the configurations
        if(preferences.getBoolean(WebSuiteActivator.PLUGIN_ID, PreferenceConstants.ACTIVATE_HTML_SERVLET, true, null)){
            http.registerServlet(htmlServletAddress, new AlarmViewServletHtml(), null, httpContext);
            LOG.debug("Html servlet registred on {}", htmlServletAddress);
        }

        if(preferences.getBoolean(WebSuiteActivator.PLUGIN_ID, PreferenceConstants.ACTIVATE_XML_SERVLET, true, null)){
            http.registerServlet(xmlServletAddress, new AlarmViewServletXml(), null, httpContext);
            LOG.debug("Xml servlet registred on {}", xmlServletAddress);
        }

        if(preferences.getBoolean(WebSuiteActivator.PLUGIN_ID, PreferenceConstants.ACTIVATE_CHANNEL_XML_SERVLET, true, null)){
            http.registerServlet(xmlChannelServletAddress, new ChannelViewServletXml(), null, httpContext);
            LOG.debug("Xml Channel registred on {}", xmlChannelServletAddress);
        }

        if(preferences.getBoolean(WebSuiteActivator.PLUGIN_ID, PreferenceConstants.ACTIVATE_CHANNEL_HTML_SERVLET, true, null)){
            http.registerServlet(htmlChannelServletAddress, new ChannelViewServletHtml(), null, httpContext);
            LOG.debug("Html Channel registred on {}", htmlChannelServletAddress);
        }

        http.registerServlet("/", new RedirectServlet(), null, httpContext);
        http.registerServlet("/ChannelViewer", new ChannelViewServlet(), null, httpContext);
        http.registerServlet("/IocViewer", new IocViewServlet(), null, httpContext);

        String xmlPath = null;
        URL url = WebSuiteActivator.getBundleContext().getBundle().getResource("webapp/config/ioc-status.xml");
        try {
            xmlPath = FileLocator.toFileURL(url).getPath();
        } catch (IOException ioe) {
            LOG.warn("Cannot resolve the path to webapp/config/ioc-status.xml");
        }
        http.registerServlet("/IocList", new IocListServlet(xmlPath), null, httpContext);

        http.registerServlet("/HowToViewer", new HowToViewServletHtml(), null, httpContext);
        http.registerServlet("/HowToSearch", new HowToServlet(), null, httpContext);
        http.registerServlet("/HowToSearchHandler", new HowToSearchServlet(), null, httpContext);
        http.registerServlet("/Info", new InfoServlet(), null, httpContext);
        http.registerServlet("/FlashInfo", new FlashInfoServlet(), null, httpContext);
        http.registerServlet("/PersonalPVInfo", new PersonalPVInfoServlet(), null, httpContext);
        http.registerServlet("/PersonalPVInfoList", new PersonalPVInfoListServlet(), null, httpContext);
        http.registerServlet("/PersonalPVInfoEdit", new PersonalPVInfoEditServlet(), null, httpContext);
        
        boolean enableAmsServlet = preferences.getBoolean(WebSuiteActivator.PLUGIN_ID,
                                                          PreferenceConstants.ENABLE_AMS_SERVLET,
                                                          false,
                                                          null);
        if (enableAmsServlet) {
            http.registerServlet("/AmsConfiguration", new AmsServlet(), null, httpContext);
        }
        
        // Two servlets from project MeasuredData
        http.registerServlet("/Halle55", new Halle55(), null, httpContext);
        http.registerServlet("/Wetter", new Wetter(), null, httpContext);
        http.registerServlet("/data.txt", new DataExporter(), null, httpContext);
    }

    /**
     * Creates the connection to the XMPP server.
     */
    public void connectToXMPPServer() {

        // Prepare the action classes
        Stop.staticInject(this);
        Restart.staticInject(this);
        MessageCounter.staticInject(this);
        MessageDeleter.staticInject(this);

        WebSuiteActivator.getDefault().addSessionServiceListener(this);
    }

    @Override
    public void bindService(final ISessionService sessionService) {
        final IPreferencesService prefs = Platform.getPreferencesService();
        final String xmppUser = prefs.getString(WebSuiteActivator.PLUGIN_ID, PreferenceConstants.XMPP_USER_NAME, "anonymous", null);
        final String xmppPassword = prefs.getString(WebSuiteActivator.PLUGIN_ID, PreferenceConstants.XMPP_PASSWORD, "anonymous", null);
        final String xmppServer = prefs.getString(WebSuiteActivator.PLUGIN_ID, PreferenceConstants.XMPP_SERVER, "", null);

    	try {
			sessionService.connect(xmppUser, xmppPassword, xmppServer);
			xmppService = sessionService;
		} catch (final Exception e) {
			LOG.warn("XMPP connection is not available: " + e.getMessage());
		}
    }

    @Override
    public void unbindService(final ISessionService service) {
    	// Nothing to do here
    }

    /**
     * @see org.eclipse.equinox.app.IApplication#stop()
     */
    @Override
	public void stop() {
    	// Nothing to do
    }

    /**
     *
     */
    @Override
	public void setRestart() {

        running = false;
        restart = true;

        synchronized(lock) {
            lock.notify();
        }
    }

    /**
     *
     */
    @Override
	public void stopWorking() {

        running = false;

        synchronized(lock) {
            lock.notify();
        }
    }

    /**
     *
     */
    @Override
    public int getMessageCount() {
        return alarmListProvider.countMessages();
    }

    /**
     *
     */
    @Override
    public void deleteAllMessages() {
        alarmListProvider.deleteAllMessages();
    }
}
