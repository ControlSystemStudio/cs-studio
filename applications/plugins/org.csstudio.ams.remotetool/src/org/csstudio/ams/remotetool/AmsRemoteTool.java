
/* 
 * Copyright (c) 2009 Stiftung Deutsches Elektronen-Synchrotron, 
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

package org.csstudio.ams.remotetool;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import org.csstudio.ams.remotetool.internal.PreferenceKeys;
import org.csstudio.platform.management.CommandDescription;
import org.csstudio.platform.management.CommandParameters;
import org.csstudio.platform.management.CommandResult;
import org.csstudio.platform.management.IManagementCommandService;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.presence.roster.IRoster;
import org.eclipse.ecf.presence.roster.IRosterEntry;
import org.eclipse.ecf.presence.roster.IRosterGroup;
import org.eclipse.ecf.presence.roster.IRosterItem;
import org.eclipse.ecf.presence.roster.IRosterManager;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.remotercp.common.tracker.IGenericServiceListener;
import org.remotercp.service.connection.session.ISessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Markus Moeller
 * @version 2.0, 2011-07-06
 */
public class AmsRemoteTool implements IApplication, IGenericServiceListener<ISessionService> {
    
    /** The class logger */
    private static final Logger LOG = LoggerFactory.getLogger(AmsRemoteTool.class);
    
    /** Command line helper */
    private CommandLine cl;
    
    private ISessionService xmppSession;
    
    public AmsRemoteTool() {
        cl = null;
        xmppSession = null;
    }
    
    /**
     * @see org.eclipse.equinox.app.IApplication#start(org.eclipse.equinox.app.IApplicationContext)
     */
    @SuppressWarnings("unchecked")
    public Object start(IApplicationContext context) throws Exception {
        
        Vector<IRosterItem> rosterItems = null;
        Vector<IRosterEntry> rosterEntries = null;
        IRosterGroup jmsApplics = null;
        IRosterEntry currentApplic = null;
        CommandParameters parameter = null;
        CommandDescription stopAction = null;
        int iResult = ApplicResult.RESULT_ERROR_GENERAL.getApplicResultNumber();
        
        String[] args = (String[])context.getArguments().get(IApplicationContext.APPLICATION_ARGS);
        cl = new CommandLine(args);
        
        LOG.info("AmsRemoteTool started...");
        
        if(cl.exists("help") || cl.exists("?")) {
            usage();
            return iResult;
        }
        
        // Check the command line arguments
        // We expect:
        // -applicname - Name of the application to stop
        // -host - Name of the computer on which the application runs
        // -username - Name of the user
        // -pw - Password for stopping
        if(!cl.exists("host") 
                || !cl.exists("applicname") 
                || !cl.exists("username") 
                || !cl.exists("pw"))  {
            
            LOG.error("One or more application arguments are missing.");
            usage();
            return iResult;
        }
        
        Activator.getDefault().addSessionServiceListener(this);

        String applicName = cl.value("applicname");
        String host = cl.value("host");
        String user = cl.value("username");
        String pw = cl.value("pw");
                
        LOG.info("Try to stop " + applicName + " on host " + host + ". Running under the account: " + user);

        synchronized (this) {
            try {
                this.wait(2000);
            } catch(InterruptedException ie) {
                LOG.error("*** InterruptedException ***: " + ie.getMessage());
            } 
        }
        
        if (xmppSession == null) {
            return ApplicResult.RESULT_ERROR_XMPP.getApplicResultNumber();
        }
        
        IRosterManager rosterManager = xmppSession.getRosterManager();

        IRoster roster = null;
        int count = 0;
  
        // We have to wait until the DCF connection manager have been initialized
        synchronized(this) {
            
            do {
                try {
                    this.wait(2000);
                } catch(InterruptedException ie) {
                    LOG.error("*** InterruptedException ***: " + ie.getMessage());
                }
  
                roster = rosterManager.getRoster();

                // Get the roster items
                rosterItems = new Vector<IRosterItem>(roster.getItems());
            } while((++count <= 10) && (rosterItems.isEmpty()));
        }

        // We have to wait until the DCF connection manager have been initialized
        synchronized(this) {
            try {
                this.wait(2000);
            } catch(InterruptedException ie) {
                LOG.error("*** InterruptedException ***: " + ie.getMessage());
            }
        }
        
        if(rosterItems.size() == 0) {
            LOG.info("XMPP roster not found. Stopping application.");
            return ApplicResult.RESULT_ERROR_XMPP.getApplicResultNumber();
        }
        
        LOG.info("Manager initialized");
        LOG.info("Anzahl Directory-Elemente: " + rosterItems.size());
        
        // Get the group of JMS applications
        for(IRosterItem ri : rosterItems) {
            if(ri.getName().compareToIgnoreCase("jms-applications") == 0) {
                jmsApplics = (IRosterGroup)ri;
                break;
            }
        }
        
        String name = null;
        
        // Get the application container
        if(jmsApplics != null) {
            
            rosterEntries = new Vector<IRosterEntry>(jmsApplics.getEntries());
            
            Iterator<IRosterEntry> list = rosterEntries.iterator();
            while(list.hasNext()) {
                IRosterEntry ce = list.next();
                name = ce.getUser().getID().toExternalForm();
                if(name.contains(applicName)) {
                    if((name.indexOf(host) > -1) && (name.indexOf(user) > -1)) {
                        currentApplic = ce;
                        break;
                    }
                }
            }
        } else {
            iResult = ApplicResult.RESULT_ERROR_UNKNOWN.getApplicResultNumber();
        }
        
        IManagementCommandService service = null;
        
        if(currentApplic != null) {
            
            LOG.info("Anwendung gefunden: " + currentApplic.getUser().getID().getName());
            
            List<IManagementCommandService> managementServices =
                xmppSession.getRemoteServiceProxies(
                    IManagementCommandService.class, new ID[] {currentApplic.getUser().getID()});
            
            if (managementServices.size() == 1) {
                service = managementServices.get(0);
                CommandDescription[] commands = service.getSupportedCommands();
                
                for (int i = 0; i < commands.length; i++) {
                    if(commands[i].getLabel().compareToIgnoreCase("stop") == 0) {
                        stopAction = commands[i];
                        break;
                    }
                }
            }
            
            if((stopAction != null) && (service != null)) {
                
                parameter = new CommandParameters();
                parameter.set("Password", pw);
                
                CommandResult retValue = service.execute(stopAction.getIdentifier(), parameter);
                if(retValue != null) {
                    String result = (String)retValue.getValue();
                    if((result.trim().startsWith("OK:")) || (result.indexOf("stopping") > -1)) {
                        LOG.info("Application stopped: " + result);
                        iResult = ApplicResult.RESULT_OK.getApplicResultNumber();
                    } else {
                        LOG.error("Something went wrong: " + result);
                        iResult = ApplicResult.RESULT_ERROR_INVALID_PASSWORD.getApplicResultNumber();
                    }
                } else {
                    LOG.info("Return value is null!");
                    iResult = ApplicResult.RESULT_ERROR_UNKNOWN.getApplicResultNumber();
                }
            }
        } else {
            iResult = ApplicResult.RESULT_ERROR_NOT_FOUND.getApplicResultNumber();
        }
        
        if (xmppSession != null) {
            xmppSession.disconnect();
        }
        
        return iResult;
    }
    
    public void usage() {
        LOG.info("AmsRemoteTool, Markus Moeller, MKS 2, (C)2011");
        LOG.info("This application stops an AMS process via XMPP action call.");
        LOG.info("Options:");
        LOG.info("-host - Name of the computer on which the AMS application is running.");
        LOG.info("-applicname - XMPP account name of the AMS application.");
        LOG.info("-username - Local computer account name under which the AMS application is running.");
        LOG.info("-pw - Password that is needed to stop an application.");
        LOG.info("[-help | -?] - Print this text. All other parameters will be ignored.");
    }
    
    /**
     * @see org.eclipse.equinox.app.IApplication#stop()
     */
    public void stop() {
        // Nothing to do here
    }
    
    public void bindService(ISessionService sessionService) {
    	
        IPreferencesService pref = Platform.getPreferencesService();
        String xmppServer = pref.getString(Activator.PLUGIN_ID, PreferenceKeys.P_XMPP_SERVER, "localhost", null);
        String xmppUser = pref.getString(Activator.PLUGIN_ID, PreferenceKeys.P_XMPP_USER, "anonymous", null);
        String xmppPassword = pref.getString(Activator.PLUGIN_ID, PreferenceKeys.P_XMPP_PASSWORD, "anonymous", null);
    	
    	try {
			sessionService.connect(xmppUser, xmppPassword, xmppServer);
			xmppSession = sessionService;
		} catch (Exception e) {
			LOG.warn("XMPP connection is not available: " + e.getMessage());
			xmppSession = null;
		}
    }
    
    public void unbindService(ISessionService service) {
    	// Nothing to do here
    }
}
