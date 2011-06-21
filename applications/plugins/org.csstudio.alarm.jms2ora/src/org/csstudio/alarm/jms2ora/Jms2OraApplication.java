
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

import java.io.File;
import org.csstudio.alarm.jms2ora.preferences.PreferenceConstants;
import org.csstudio.alarm.jms2ora.util.ApplicState;
import org.csstudio.alarm.jms2ora.util.CommandLine;
import org.csstudio.alarm.jms2ora.util.Hostname;
import org.csstudio.alarm.jms2ora.util.SynchObject;
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

public class Jms2OraApplication implements IApplication, Stoppable, IGenericServiceListener<ISessionService> {
    
    private static Jms2OraApplication instance = null;
    
    /** The class logger */
    private static final Logger LOG = LoggerFactory.getLogger(Jms2OraApplication.class);

    /** The MessageProcessor does all the work on messages */
    private MessageProcessor messageProcessor;
    
    /**  */
    private SynchObject sync;
    
    /** Name of the folder that holds the stored message content */
    private String objectDir;

    /** Last state */
    private int lastState = 0;
    
    /** Flag that indicates whether or not the application is/should running */
    private boolean running;
    
    /** Flag that indicates whether or not the application should stop. */
    public boolean shutdown;
    
    /** Time to sleep in ms */
    private static long SLEEPING_TIME = 60000 ;

    /** Time to sleep in ms */
    private long WAITFORTHREAD = 20000 ;

    public Jms2OraApplication() {
        
        instance = this;

        IPreferencesService prefs = Platform.getPreferencesService();
        objectDir = prefs.getString(Jms2OraPlugin.PLUGIN_ID, PreferenceConstants.STORAGE_DIRECTORY, "./var/", null);
        if(objectDir.endsWith("/") == false) {
            objectDir += "/";
        }

        createObjectFolder();
    
        sync = new SynchObject(ApplicState.INIT, System.currentTimeMillis());
        running = true;
        shutdown = false;
    }
    
    public static Jms2OraApplication getInstance() {
        return instance;
    }
    
    public Object start(IApplicationContext context) throws Exception {
        
        CommandLine cmd = null;
        String[] args = null;
        String stateText = null;
        String host = null;
        String user = null;
        int currentState = 0;

        args = (String[])context.getArguments().get(IApplicationContext.APPLICATION_ARGS);
              
        cmd = new CommandLine(args);
        if(cmd.exists("help") || cmd.exists("h") || cmd.exists("?")) {
            
            System.out.println(VersionInfo.getAll());
            System.out.println("Usage: jms2ora [-stop] [-host <hostname>] [-username <username>] [-help | -h | -?]");
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
            boolean success = stopper.stopExternInstance(Jms2OraPlugin.getDefault().getBundleContext(), "jms2oracle", host, user);
        
            if(success) {
                LOG.info("jms2ora stopped.");
            } else {
                LOG.error("jms2ora cannot be stopped.");
            }
            
            return IApplication.EXIT_OK;
        }
        
        context.applicationRunning();
        
        // Create an object from this class
        messageProcessor = MessageProcessor.getInstance();
        messageProcessor.setParent(this);
        messageProcessor.start();

        sync.setSynchStatus(ApplicState.OK);
        stateText = "ok";
        
        while(running) {
            
            synchronized(this) {
                try {
                    this.wait(SLEEPING_TIME);
                } catch(InterruptedException ie) { /* Can be ignored */}
            }
            
            SynchObject actSynch = new SynchObject(ApplicState.INIT, 0);
            if(!sync.hasStatusSet(actSynch, 300, ApplicState.TIMEOUT)) {
                LOG.error("TIMEOUT: State has not changed the last 5 minute(s).");
            }

            currentState = actSynch.getStatus();
            if(currentState != lastState) {
                
                switch(currentState) {
                    
                    case ApplicState.INIT:
                        stateText = "init";
                        break;
                        
                    case ApplicState.OK:
                        stateText = "ok";
                        break;
                        
                    case ApplicState.WORKING:
                        stateText = "working";
                        break;

                    case ApplicState.SLEEPING:
                        stateText = "sleeping";
                        break;

                    case ApplicState.LEAVING:
                        stateText = "leaving";
                        break;

                    case ApplicState.ERROR:
                        stateText = "error";                        
                        running = false;                        
                        break;
                        
                    case ApplicState.FATAL:
                        stateText = "fatal";
                        running = false;                        
                        break;
                    
                    case ApplicState.TIMEOUT:
                        stateText = "timeout";
                        running = false;                        
                        break;
                }
                
                LOG.debug("set state to " + stateText + "(" + currentState + ")");
                lastState = currentState;               
            }
            
            LOG.debug("Current state: " + stateText + "(" + currentState + ")");
        }

        if(messageProcessor != null) {
            
            // Clean stop of the working thread
            messageProcessor.stopWorking();
            
            do {
                
                try {
                    messageProcessor.join(WAITFORTHREAD);
                } catch(InterruptedException ie) { /* Can be ignored */ }
            } while(sync.getSynchStatus() == ApplicState.LEAVING);
            
            if(messageProcessor.stoppedClean()) {
                LOG.info("Restart/Exit: Thread stopped clean.");
                messageProcessor = null;
            } else {
                LOG.warn("Restart/Exit: Thread did NOT stop clean.");
                messageProcessor = null;
            }
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
        
        IPreferencesService prefs = Platform.getPreferencesService();
        String xmppUser = prefs.getString(Jms2OraPlugin.PLUGIN_ID,
                PreferenceConstants.XMPP_USER_NAME, "anonymous", null);
        String xmppPassword = prefs.getString(Jms2OraPlugin.PLUGIN_ID,
                PreferenceConstants.XMPP_PASSWORD, "anonymous", null);
        String xmppServer = prefs.getString(Jms2OraPlugin.PLUGIN_ID,
                PreferenceConstants.XMPP_SERVER, "krynfs.desy.de", null);
    	
    	try {
			sessionService.connect(xmppUser, xmppPassword, xmppServer);
		} catch (Exception e) {
		    LOG.warn("XMPP connection is not available, " + e.toString());
		}
    }
    
    public void unbindService(ISessionService service) {
    	service.disconnect();
    }
        
    public int getState() {
        return sync.getSynchStatus();
    }
    
    public void setStatus(int status) {
        sync.setSynchStatus(status);
    }

    public void stopWorking() {
        
        running = false;
        shutdown = true;
        
        LOG.info("The application will shutdown...");
        
        synchronized(this) {
            notify();
        }
    }

    public void setRestart() {
        
        running = false;
        shutdown = false;
        
        LOG.info("The application will restart...");
        
        synchronized(this) {
            notify();
        }
    }

    public void stop() {
        
        running = false;
        shutdown = true;
        
        LOG.info("The application will shutdown...");
        
        synchronized(this) {
            notify();
        }
    }
    
    private void createObjectFolder() {
        
        File folder = new File(objectDir);
                
        if(!folder.exists()) {
            
            boolean result = folder.mkdir();
            if(result) {
                LOG.info("Folder " + objectDir + " was created.");                
            } else {
                LOG.warn("Folder " + objectDir + " was NOT created.");
            }
        }
    }
}