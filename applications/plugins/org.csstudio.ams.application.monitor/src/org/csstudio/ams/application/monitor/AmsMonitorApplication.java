
/* 
 * Copyright (c) 2012 Stiftung Deutsches Elektronen-Synchrotron, 
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

package org.csstudio.ams.application.monitor;

import org.csstudio.ams.application.monitor.check.SystemCheckWorker;
import org.csstudio.ams.application.monitor.internal.AmsMonitorPreference;
import org.csstudio.ams.application.monitor.management.Restart;
import org.csstudio.ams.application.monitor.management.Stop;
import org.csstudio.utility.jms.sharedconnection.SharedJmsConnections;
import org.eclipse.core.runtime.Platform;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.remotercp.common.tracker.IGenericServiceListener;
import org.remotercp.service.connection.session.ISessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class controls all aspects of the application's execution
 */
public class AmsMonitorApplication implements IApplication,
                                              RemotelyManageable,
                                              IGenericServiceListener<ISessionService> {

    private static final Logger LOG = LoggerFactory.getLogger(AmsMonitorApplication.class);
    
    /** The session service for the XMPP login */
    private ISessionService xmppService;

    private SystemCheckWorker checkWorker;
    
    private boolean running;
    
    private boolean restart;
    
    public AmsMonitorApplication() {
        xmppService = null;
        running = true;
        restart = false;
    }
    
	@Override
    public Object start(IApplicationContext context) throws Exception {
		
	    AmsMonitorActivator.getDefault().addSessionServiceListener(this);
        
	    // FIRST read the JMS preferences and prepare the SharedJmsConnection class
        String url1 = AmsMonitorPreference.JMS_CONSUMER_URL1.getValue();
        String url2 = AmsMonitorPreference.JMS_CONSUMER_URL2.getValue();
        String topic = AmsMonitorPreference.JMS_CONSUMER_TOPIC_MONITOR.getValue();
        LOG.debug("JMS Consumer URL 1:         {}", url1);
        LOG.debug("JMS Consumer URL 2:         {}", url2);
        LOG.debug("JMS Consumer Monitor Topic: {}", topic);
        SharedJmsConnections.staticInjectConsumerUrlAndClientId(url1, url2, "AmsSystemMonitorConsumer");
        
        url1 = AmsMonitorPreference.JMS_PUBLISHER_URL.getValue();
        topic = AmsMonitorPreference.JMS_PUBLISHER_TOPIC_ALARM.getValue();
        LOG.debug("JMS Publisher URL:   {}", url1);
        LOG.debug("JMS Publisher Topic: {}", topic);
        SharedJmsConnections.staticInjectPublisherUrlAndClientId(url1, "AmsSystemMonitorPublisher");
        
        
        // Retrieve the location of the workspace directory
        String workspaceLocation = null;
        try {
            workspaceLocation = Platform.getLocation().toPortableString();
            if(!workspaceLocation.endsWith("/")) {
                workspaceLocation = workspaceLocation + "/";
            }
        } catch(IllegalStateException ise) {
            LOG.warn("Workspace location could not be found. Using working directory '.'");
            workspaceLocation = "./";
        }
        
        // Now create the check worker
        checkWorker = new SystemCheckWorker(xmppService, workspaceLocation);
        checkWorker.start();
        
        context.applicationRunning();
	    
	    while (running) {
	        synchronized (checkWorker) {
	            try {
	                checkWorker.join();
	                running = checkWorker.isInterrupted();
	            } catch (InterruptedException ie) {
	                LOG.warn("{} has been interrupted outside.", this.getClass().getSimpleName());
	            }
	        }
	    }
	    
        if (xmppService != null) {
            synchronized (xmppService) {
                try {
                    xmppService.wait(500);
                } catch (InterruptedException ie) {
                    LOG.warn("XMPP service waited and was interrupted.");
                }
            }
            xmppService.disconnect();
            LOG.info("XMPP disconnected.");
        }
        
        Integer exitCode = IApplication.EXIT_OK;
        if (restart) {
            LOG.info("Restarting application...");
            exitCode = IApplication.EXIT_RESTART;
        } else {
            LOG.info("Leaving application...");
        }
        
        LOG.info("-------------------------------------------------------------------------------------");
        
        return exitCode;
	}

	@Override
    public synchronized void stop() {
		running = false;
	    this.notify();
	}

    @Override
    public void bindService(ISessionService service) {

        Stop.staticInject(this);
        Restart.staticInject(this);
        
        String xmppServer = AmsMonitorPreference.XMPP_SERVER.getValue();
        String xmppUser = AmsMonitorPreference.XMPP_USER.getValue();
        String xmppPassword = AmsMonitorPreference.XMPP_PASSWORD.getValue();
        
        try {
            service.connect(xmppUser, xmppPassword, xmppServer);
            xmppService = service;
            LOG.info("XMPP connected.");
        } catch (Exception e) {
            LOG.warn("XMPP connection is not available: " + e.getMessage());
            xmppService = null;
        }
    }

    @Override
    public void unbindService(ISessionService service) {
        // Nothing to do
    }

    @Override
    public void setRestart(boolean restartApplication) {
        synchronized (checkWorker) {
            running = false;
            restart = restartApplication;
            checkWorker.interrupt();
        }
    }
}
