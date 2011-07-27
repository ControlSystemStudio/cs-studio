
/* 
 * Copyright (c) 2011 Stiftung Deutsches Elektronen-Synchrotron, 
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
 *
 */

package org.csstudio.alarm.syslog2jms;

import javax.jms.MapMessage;
import org.csstudio.alarm.syslog2jms.management.Restart;
import org.csstudio.platform.utility.jms.JmsSimpleProducer;
import org.csstudio.alarm.syslog2jms.preferences.PreferenceConstants;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.remotercp.common.tracker.IGenericServiceListener;
import org.remotercp.service.connection.session.ISessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class controls all aspects of the application's execution
 */
public class Syslog2JmsApplication implements IApplication, Stoppable,
                                              IGenericServiceListener<ISessionService> {

    /** The class logger */
    private static final Logger LOG = LoggerFactory.getLogger(Syslog2JmsApplication.class);

    /** The session service for the XMPP login */
    private ISessionService xmppService;
    
    /** The JMS producer / publisher */
    private JmsSimpleProducer jmsProducer;
    
    /** Object that is used as a lock */
    private Object lock;
    
    /** Flag that indicates if this application should run */
    private boolean running;
    
    /** Flag that indicates if this application should restart */
    private boolean restart;

    public Syslog2JmsApplication() {
        jmsProducer = null;
        lock = new Object();
        xmppService = null;
        running = true;
        restart = false;
    }
    
	/* (non-Javadoc)
	 * @see org.eclipse.equinox.app.IApplication#start(org.eclipse.equinox.app.IApplicationContext)
	 */
	@Override
    public Object start(IApplicationContext context) throws Exception {
		
	    LOG.info("Syslog2JmsApplication is starting.");
	    	    
	    if (initJms() == false) {
	        LOG.error("JMS connection failed.");
	        LOG.error("Stopping {}", Activator.PLUGIN_ID);
	        return IApplication.EXIT_OK;
	    }
	    
	    // Simple example to show how to create and send a JMS message
	    MapMessage message = jmsProducer.createMapMessage();
	    if (message != null) {
	        message.setString("TYPE", "log");
	        message.setString("EVENTTIME", jmsProducer.getCurrentDateAsString());
	        message.setString("STATUS", "NO_ALARM");
	        message.setString("SEVERITY", "NO_ALARM");
	        message.setString("TEXT", "Syslog2Jms lebt...");
	        message.setString("APPLICATION-ID", "Syslog2Jms");
	        
	        jmsProducer.sendMessage(message);
	    }
	    
        Restart.staticInject(this);
        Activator.getDefault().addSessionServiceListener(this);

	    // Before calling this, bring up all needed resources
	    context.applicationRunning();
	    
	    LOG.info("Start working...");
	    
	    while (running) {
    	    synchronized (lock) {
    	        try {
    	            lock.wait();
    	        } catch (InterruptedException ie) {
    	            // Can be ignored
    	        }
    	    }
	    }
	    
	    if (jmsProducer != null) {
	        jmsProducer.closeAll();
	    }
	    
	    if (xmppService != null) {
	           synchronized (xmppService) {
	                try {
	                    xmppService.wait(500);
	                } catch (InterruptedException ie) {
	                    // Can be ignored
	                }
	            }

	        xmppService.disconnect();
	    }
	    
	    Integer exitCode = IApplication.EXIT_OK;
	    if (restart) {
	        exitCode = IApplication.EXIT_RESTART;
	        LOG.info("Restarting {}", Activator.PLUGIN_ID);
	    } else {
	        LOG.info("Stopping {}", Activator.PLUGIN_ID);
	    }
	    
		return exitCode;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.equinox.app.IApplication#stop()
	 */
	@Override
    public void stop() {
	    running = false;
	    restart = false;
	    synchronized (lock) {
	        lock.notify();
		}
	}

    @Override
    public void stopWorking() {
        running = false;
        restart = false;
        synchronized (lock) {
            lock.notify();
        }
    }

    @Override
    public void setRestart() {
        running = false;
        restart = true;
        synchronized (lock) {
            lock.notify();
        }
    }

    private boolean initJms() {
        
        IPreferencesService pref = Platform.getPreferencesService();
        String url = pref.getString(Activator.PLUGIN_ID, PreferenceConstants.JMS_PRODUCER_URL, "", null);
        String factory = pref.getString(Activator.PLUGIN_ID, PreferenceConstants.JMS_PRODUCER_FACTORY, "", null);
        String topic = pref.getString(Activator.PLUGIN_ID, PreferenceConstants.JMS_PRODUCER_TOPIC_NAME, "", null);
        
        if ((url.length() * factory.length() * topic.length()) == 0) {
            LOG.error("The preferences do not contain a valid configuration for the JMS connection.");
            LOG.error("Stopping {}", Activator.PLUGIN_ID);
            return false;
        }
        
        LOG.info("Try to create a JMS producer with:");
        LOG.info(" URL           {}", url);
        LOG.info(" Factory class {}", factory);
        LOG.info(" TOPICS        {}", topic);
        
        jmsProducer = new JmsSimpleProducer("Syslog2JmsProducer", url, factory, topic);
        
        return jmsProducer.isConnected();
    }
    
    @Override
    public void bindService(ISessionService service) {
        
        IPreferencesService prefs = Platform.getPreferencesService();
        String xmppUser = prefs.getString(Activator.PLUGIN_ID,
                PreferenceConstants.XMPP_USER_NAME, "anonymous", null);
        String xmppPassword = prefs.getString(Activator.PLUGIN_ID,
                PreferenceConstants.XMPP_PASSWORD, "anonymous", null);
        String xmppServer = prefs.getString(Activator.PLUGIN_ID,
                PreferenceConstants.XMPP_SERVER, "krynfs.desy.de", null);

        try {
            service.connect(xmppUser, xmppPassword, xmppServer);
            xmppService = service;
        } catch (Exception e) {
            LOG.warn("XMPP connection is not available: {}", e.getMessage());
        }
    }

    @Override
    public void unbindService(ISessionService service) {
        // Nothing to do here
    }
}
