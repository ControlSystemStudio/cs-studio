
/* 
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchrotron, 
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

package org.csstudio.alarm.jms2ora;

import org.csstudio.alarm.jms2ora.preferences.PreferenceConstants;
import org.csstudio.alarm.jms2ora.util.CommandLine;
import org.csstudio.alarm.jms2ora.util.Hostname;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.remotercp.common.tracker.IGenericServiceListener;
import org.remotercp.service.connection.session.ISessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The starting class.
 * 
 * @author Markus Moeller
 *
 */

public class Jms2OraApplication implements IApplication, Stoppable, RemotelyAccesible,
                                           IGenericServiceListener<ISessionService> {
    
    /** The class logger */
    private static final Logger LOG = LoggerFactory.getLogger(Jms2OraApplication.class);

    /** The MessageProcessor does all the work on messages */
    private MessageProcessor messageProcessor;
    
    /**  */
    private Object lock;

    /** Object that holds the credentials for XMPP login */
    private XmppInfo xmppInfo;
    
    /** The ECF service */
    private ISessionService xmppService;
    
    /** Name of the folder that holds the stored message content */
    //private String objectDir;

    /** Flag that indicates whether or not the application is/should running */
    private boolean running;
    
    /** Flag that indicates whether or not the application should stop. */
    public boolean shutdown;
    
    /** Time to sleep in ms */
    private static long SLEEPING_TIME = 60000 ;

    /** Time to sleep in ms */
    private long WAITFORTHREAD = 20000 ;

    public Jms2OraApplication() {
        lock = new Object();
        xmppInfo = null;
        xmppService = null;
        running = true;
        shutdown = false;
    }
    
    public Object start(IApplicationContext context) throws Exception {
        
        CommandLine cmd = null;
        String[] args = null;
        String host = null;
        String user = null;

        args = (String[])context.getArguments().get(IApplicationContext.APPLICATION_ARGS);
        
        IPreferencesService prefs = Platform.getPreferencesService();
        String xmppUser = prefs.getString(Jms2OraPlugin.PLUGIN_ID,
                PreferenceConstants.XMPP_USER_NAME, "anonymous", null);
        String xmppPassword = prefs.getString(Jms2OraPlugin.PLUGIN_ID,
                PreferenceConstants.XMPP_PASSWORD, "anonymous", null);
        String xmppServer = prefs.getString(Jms2OraPlugin.PLUGIN_ID,
                PreferenceConstants.XMPP_SERVER, "krynfs.desy.de", null);
        
        xmppInfo = new XmppInfo(xmppServer, xmppUser, xmppPassword);
        
        /*
         *  Applikationsoptionen, um den Check zu starten
         *  -check -host krynfs -username archiver
         */
        cmd = new CommandLine(args);
        if(cmd.exists("help") || cmd.exists("h") || cmd.exists("?")) {
            
            System.out.println(VersionInfo.getAll());
            System.out.println("Usage: jms2ora [-check] [-stop] [-host <hostname>] [-username <username>] [-help | -h | -?]");
            System.out.println("       -check               - Checks if the application hangs using the XMPP command.");
            System.out.println("       -stop                - Stopps the application using the XMPP command.");
            System.out.println("       -host <hostname>     - Name of host where the application is running.");
            System.out.println("       -username <username> - Name of the user that is running the application.");
            System.out.println("       -help | -h | -?      - This help text.");
            
            return IApplication.EXIT_OK;
        }
        
        if(cmd.exists("stop")) {
            
            host = cmd.value("host", Hostname.getInstance().getHostname());
            user = cmd.value("username", "");
            
            ApplicationStopper stopper = new ApplicationStopper();
            boolean success = stopper.stopExternInstance("jms2oracle", host, user);
        
            if(success) {
                LOG.info("jms2ora stopped.");
            } else {
                LOG.error("jms2ora cannot be stopped.");
            }
            
            return IApplication.EXIT_OK;
        }
        
        if(cmd.exists("check")) {
            
            host = cmd.value("host", Hostname.getInstance().getHostname());
            user = cmd.value("username", "");

            ApplicationChecker checker = new ApplicationChecker();
            boolean success = checker.checkExternInstance("jms2oracle", host, user);
        
            if(success) {
                LOG.info("jms2ora is working.");
            } else {
                LOG.error("jms2ora is NOT working.");
            }
            
            return IApplication.EXIT_OK;
        }

        // Create an object from this class
        messageProcessor = new MessageProcessor();
        messageProcessor.start();

        Jms2OraPlugin.getDefault().addSessionServiceListener(this);
        
        context.applicationRunning();
        

        while (running) {
            synchronized (lock) {
                try {
                    lock.wait(SLEEPING_TIME);
                } catch(InterruptedException ie) { /* Can be ignored */}
            }
            
            // TODO: Check the worker...
            LOG.debug("TODO: Check the worker...");
        }
        
        if(messageProcessor != null) {
            
            // Clean stop of the working thread
            messageProcessor.stopWorking();
                
            try {
                messageProcessor.join(WAITFORTHREAD);
            } catch(InterruptedException ie) { /* Can be ignored */ }
            
            if(messageProcessor.stoppedClean()) {
                LOG.info("Restart/Exit: Thread stopped clean.");
                messageProcessor = null;
            } else {
                LOG.warn("Restart/Exit: Thread did NOT stop clean.");
                messageProcessor = null;
            }
        }
        
        if (xmppService != null) {
            xmppService.disconnect();
        }
        
        Integer exitCode;
        if (shutdown) {
            exitCode = IApplication.EXIT_OK;
            LOG.info("Stopping application.");
        } else {
            exitCode = IApplication.EXIT_RESTART;
            LOG.info("Restarting application.");
        }
        
        return exitCode;
    }
    
    public void bindService(ISessionService sessionService) {
        
    	if (xmppInfo == null) {
    	    return;
    	}
    	
    	try {
			sessionService.connect(xmppInfo.getXmppUser(), xmppInfo.getXmppPassword(), xmppInfo.getXmppServer());
			xmppService = sessionService;
    	} catch (Exception e) {
		    LOG.warn("XMPP connection is not available: {}", e.toString());
		}
    }
    
    public void unbindService(ISessionService service) {
    	// Nothing to do here
    }
        
    public void stopWorking() {
        
        running = false;
        shutdown = true;
        
        LOG.info("The application will shutdown...");
        
        synchronized(lock) {
            lock.notify();
        }
    }

    public void setRestart() {
        
        running = false;
        shutdown = false;
        
        LOG.info("The application will restart...");
        
        synchronized(lock) {
            lock.notify();
        }
    }

    public void stop() {
        
        running = false;
        shutdown = true;
        
        LOG.info("The application will shutdown...");
        
        synchronized(lock) {
            lock.notify();
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public int getMessageQueueSize() {
        return messageProcessor.getMessageQueueSize();
    }
}