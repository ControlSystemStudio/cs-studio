
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

import javax.servlet.ServletException;
import org.apache.log4j.Logger;
import org.csstudio.platform.httpd.HttpServiceHelper;
import org.csstudio.platform.logging.CentralLogger;
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
import org.csstudio.websuite.servlet.PersonalPVInfoEditServlet;
import org.csstudio.websuite.servlet.PersonalPVInfoListServlet;
import org.csstudio.websuite.servlet.PersonalPVInfoServlet;
import org.csstudio.websuite.servlet.FlashInfoServlet;
import org.csstudio.websuite.servlet.Halle55;
import org.csstudio.websuite.servlet.HowToViewServletHtml;
import org.csstudio.websuite.servlet.InfoServlet;
import org.csstudio.websuite.servlet.IocViewServlet;
import org.csstudio.websuite.servlet.RedirectServlet;
import org.csstudio.websuite.servlet.Wetter;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;
import org.remotercp.common.tracker.IGenericServiceListener;
import org.remotercp.service.connection.session.ISessionService;

/**
 * @author Markus Moeller
 *
 */
public class WebSuiteApplication implements IApplication, Stoppable, RemotlyAccessible, IGenericServiceListener<ISessionService> {
    
    /** The provider of the alarm messages */
    private AlarmMessageListProvider alarmListProvider;
    
    /** Platform logger */
    private Logger logger;
    
    /** Object for synchronization */
    private Object lock;
    
    /** The port that is used by JETTY */
    private int jettyPort;
    
    /** Flag that indicates whether or not the application is running */
    private boolean running;
    
    /** Flag that indicates whether or not the application should be restarted */
    private boolean restart;

    /** Simple constructor */
    public WebSuiteApplication() {
        
        logger = CentralLogger.getInstance().getLogger(this);
        lock = new Object();
        running = true;
        restart = false;
    }
    
    /**
     * @see org.eclipse.equinox.app.IApplication#start(org.eclipse.equinox.app.IApplicationContext)
     */
    @Override
	public Object start(IApplicationContext context) throws Exception {
        
        IPreferencesService preferences = Platform.getPreferencesService();

        jettyPort = preferences.getInt(WebSuiteActivator.PLUGIN_ID, PreferenceConstants.JETTY_PORT, 8181, null);
        logger.info("Using port " + jettyPort + " for JETTY.");
        
        // Prepare the action classes
        Stop.staticInject(this);
        Restart.staticInject(this);
        MessageCounter.staticInject(this);
        MessageDeleter.staticInject(this);

        try {
            final HttpService http = HttpServiceHelper.createHttpService(WebSuiteActivator.getBundleContext(), jettyPort);
            configureHttpService(http);
        } catch(Exception e) {
            logger.error("[*** Exception ***]: " + e.getMessage());
        }
        
        //creates instances of DAO objects for messages
        AlarmMessageListProvider.createInstance();
        alarmListProvider = AlarmMessageListProvider.getInstance();
        
        ChannelMessagesProvider.createInstance();
        
        context.applicationRunning();
        
        while(running) {
            
            synchronized(lock) {
                
                try {
                    lock.wait();
                } catch(InterruptedException ie) {
                    logger.warn("Websuite was interrupted: " + ie.getMessage());
                }
            }
        }
        
        HttpServiceHelper.stopHttpService(jettyPort);
        AlarmMessageListProvider.getInstance().closeJms();
        
        if(restart) {
            logger.info("Restarting application.");
            return IApplication.EXIT_RESTART;
        } else {
            logger.info("Stopping application.");
            return IApplication.EXIT_OK;
        }
    }

    /** Register resources and servlet */
    private void configureHttpService(final HttpService http) throws NamespaceException, ServletException {
        
        IPreferencesService preferences = Platform.getPreferencesService();
        final HttpContext httpContext = http.createDefaultHttpContext();
        
        //Collect servlet path from configuration
        String htmlServletAddress = preferences.getString(WebSuiteActivator.PLUGIN_ID, PreferenceConstants.HTML_SERVLET_ADDRESS, "", null);
        String xmlServletAddress = preferences.getString(WebSuiteActivator.PLUGIN_ID, PreferenceConstants.XML_SERVLET_ADDRESS, "", null);
        String xmlChannelServletAddress = preferences.getString(WebSuiteActivator.PLUGIN_ID, PreferenceConstants.XML_CHANNEL_SERVLET_ADDRESS, "", null);
        String htmlChannelServletAddress = preferences.getString(WebSuiteActivator.PLUGIN_ID, PreferenceConstants.HTML_CHANNEL_SERVLET_ADDRESS, "", null);

        http.registerResources("/style", "/webapp/style", httpContext);
        http.registerResources("/images", "/webapp/images", httpContext);
        
        //creates servlet according to the configurations
        if(preferences.getBoolean(WebSuiteActivator.PLUGIN_ID, PreferenceConstants.ACTIVATE_HTML_SERVLET, true, null)){
            http.registerServlet(htmlServletAddress, new AlarmViewServletHtml(), null, httpContext);
            logger.debug("Html servlet registred on " + htmlServletAddress);
        }
        
        if(preferences.getBoolean(WebSuiteActivator.PLUGIN_ID, PreferenceConstants.ACTIVATE_XML_SERVLET, true, null)){
            http.registerServlet(xmlServletAddress, new AlarmViewServletXml(), null, httpContext);
            logger.debug("Xml servlet registred on " + xmlServletAddress);
        }
        
        if(preferences.getBoolean(WebSuiteActivator.PLUGIN_ID, PreferenceConstants.ACTIVATE_CHANNEL_XML_SERVLET, true, null)){
            http.registerServlet(xmlChannelServletAddress, new ChannelViewServletXml(), null, httpContext);
            logger.debug("Xml Channel registred on " + xmlChannelServletAddress);
        }
        
        if(preferences.getBoolean(WebSuiteActivator.PLUGIN_ID, PreferenceConstants.ACTIVATE_CHANNEL_HTML_SERVLET, true, null)){
            http.registerServlet(htmlChannelServletAddress, new ChannelViewServletHtml(), null, httpContext);
            logger.debug("Html Channel registred on " + htmlChannelServletAddress);
        }

        http.registerServlet("/", new RedirectServlet(), null, httpContext);
        http.registerServlet("/ChannelViewer", new ChannelViewServlet(), null, httpContext);
        http.registerServlet("/IocViewer", new IocViewServlet(), null, httpContext);
        http.registerServlet("/HowToViewer", new HowToViewServletHtml(), null, httpContext);
        http.registerServlet("/Info", new InfoServlet(), null, httpContext);
        http.registerServlet("/FlashInfo", new FlashInfoServlet(), null, httpContext);
        http.registerServlet("/PersonalPVInfo", new PersonalPVInfoServlet(), null, httpContext);
        http.registerServlet("/PersonalPVInfoList", new PersonalPVInfoListServlet(), null, httpContext);
        http.registerServlet("/PersonalPVInfoEdit", new PersonalPVInfoEditServlet(), null, httpContext);
        
        // Two servlets from project MeasuredData
        http.registerServlet("/Halle55", new Halle55(), null, httpContext);
        http.registerServlet("/Wetter", new Wetter(), null, httpContext);
        http.registerServlet("/data.txt", new DataExporter(), null, httpContext);
    }
    


    public void bindService(ISessionService sessionService) {
        IPreferencesService prefs = Platform.getPreferencesService();
        String xmppUser = prefs.getString(WebSuiteActivator.PLUGIN_ID, PreferenceConstants.XMPP_USER_NAME, "anonymous", null);
        String xmppPassword = prefs.getString(WebSuiteActivator.PLUGIN_ID, PreferenceConstants.XMPP_PASSWORD, "anonymous", null);
        String xmppServer = prefs.getString(WebSuiteActivator.PLUGIN_ID, PreferenceConstants.XMPP_SERVER, "", null);
    	
    	try {
			sessionService.connect(xmppUser, xmppPassword, xmppServer);
		} catch (Exception e) {
			CentralLogger.getInstance().warn(this,
					"XMPP connection is not available, " + e.toString());
		}
    }
    
    public void unbindService(ISessionService service) {
    	service.disconnect();
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
