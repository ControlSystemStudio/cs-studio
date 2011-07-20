
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
 *
 */

package org.csstudio.alarm.jms2ora;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import org.csstudio.alarm.jms2ora.preferences.PreferenceConstants;
import org.csstudio.platform.management.CommandDescription;
import org.csstudio.platform.management.CommandParameters;
import org.csstudio.platform.management.CommandResult;
import org.csstudio.platform.management.IManagementCommandService;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.ecf.presence.roster.IRoster;
import org.eclipse.ecf.presence.roster.IRosterEntry;
import org.eclipse.ecf.presence.roster.IRosterGroup;
import org.eclipse.ecf.presence.roster.IRosterItem;
import org.eclipse.ecf.presence.roster.IRosterManager;
import org.eclipse.ecf.core.identity.ID;
import org.remotercp.common.tracker.IGenericServiceListener;
import org.remotercp.service.connection.session.ISessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Markus Moeller
 *
 */
public class ApplicationStopper implements IGenericServiceListener<ISessionService> {
    
    /** The class logger */
    private static final Logger LOG = LoggerFactory.getLogger(ApplicationStopper.class);

    /** */
    private ISessionService xmppSession;

    public ApplicationStopper() {
        xmppSession = null;
    }
    
    /**
     * 
     * @param applicationName
     * @param host
     * @param user
     */
    public boolean stopExternInstance(String applicationName, String host, String user) {
        
        Vector<IRosterItem> rosterItems = null;
        IRosterGroup jmsApplics = null;
        IRosterEntry currentApplic = null;
        String stopPassword = null;
        boolean success = false;
        
        IPreferencesService prefs = Platform.getPreferencesService();
        stopPassword = prefs.getString(Jms2OraPlugin.PLUGIN_ID, PreferenceConstants.XMPP_SHUTDOWN_PASSWORD, "", null);

        Jms2OraPlugin.getDefault().addSessionServiceListener(this);
        
        Object lock = new Object();
        synchronized (lock) {
            try {
                lock.wait(3000);
            } catch (InterruptedException ie) {
                // Can be ignored
            }
        }

        if (xmppSession == null) {
            LOG.info("XMPP login failed. Cannot stop the application.");
            return success;
        }

        rosterItems = getRosterItems();

        synchronized(lock) {
            try {
                lock.wait(5000);
            } catch(InterruptedException ie) {
                LOG.error("[*** InterruptedException ***]: " + ie.getMessage());
            }
        }
        
        if(rosterItems.size() == 0) {
            LOG.info("XMPP roster not found. Leaving application.");
            
            // Allways 'false' here
            return success;
        }
        
        LOG.info("Manager initialized");
        LOG.info("Anzahl Directory-Elemente: " + rosterItems.size());
        
        jmsApplics = this.getRosterGroup(rosterItems, "jms-applications");
        currentApplic = this.getRosterApplication(jmsApplics, applicationName, host, user);
        
        if(currentApplic != null) {
            success = this.stopApplication(currentApplic, stopPassword);
        }
        
        if (xmppSession != null) {
            xmppSession.disconnect();
        }
        
        return success;
    }

    /**
     * 
     * @return
     */
    @SuppressWarnings("unchecked")
    private Vector<IRosterItem> getRosterItems() {
        
        IRoster roster = null;
        Vector<IRosterItem> rosterItems = null;
        int count = 0;
        
        IRosterManager rosterManager = xmppSession.getRosterManager();
        
        // We have to wait until the ECF connection manager have been initialized
        synchronized(this) {
            
            do {
            
                try {
                    this.wait(2000);
                } catch(InterruptedException ie) {
                    LOG.error("[*** InterruptedException ***]: ", ie.getMessage());
                }
  
                roster = rosterManager.getRoster();

                // Get the roster items
                rosterItems = new Vector<IRosterItem>(roster.getItems());
            
            } while((++count <= 10) && (rosterItems.isEmpty()));
        }
        
        return rosterItems;
    }
    
    /**
     * 
     * @param rosterItems
     * @param groupName
     * @return
     */
    private IRosterGroup getRosterGroup(Vector<IRosterItem> rosterItems, String groupName)
    {
        IRosterGroup jmsApplics = null;

        // Get the group of JMS applications
        for(IRosterItem ri : rosterItems)
        {
            if(ri.getName().compareToIgnoreCase(groupName) == 0)
            {
                jmsApplics = (IRosterGroup)ri;
                break;
            }
        }

        return jmsApplics;
    }
    
    /**
     * 
     * @param jmsApplics
     * @param applicName
     * @param host
     * @param user
     * @return
     */
    @SuppressWarnings("unchecked")
    private IRosterEntry getRosterApplication(IRosterGroup jmsApplics, String applicName, String host, String user)
    {
        Vector<IRosterEntry> rosterEntries = null;
        IRosterEntry currentApplic = null;
        String name = null;
        
        if(jmsApplics != null)
        {
            rosterEntries = new Vector<IRosterEntry>(jmsApplics.getEntries());
            
            Iterator<IRosterEntry> list = rosterEntries.iterator();
            while(list.hasNext())
            {
                IRosterEntry ce = list.next();
                
                name = ce.getUser().getID().toExternalForm();
                if(name.contains(applicName))
                {
                    if((name.indexOf(host) > -1) && (name.indexOf(user) > -1))
                    {
                        currentApplic = ce;
                        break;
                    }
                }
            }
        }
        
        return currentApplic;
    }
    
    /**
     * 
     * @param currentApplic
     * @param password
     * @return
     */
    private boolean stopApplication(IRosterEntry currentApplic, String password) {
        
        CommandDescription stopAction = null;
        CommandParameters parameter = null;
        String returnValue = null;
        boolean result = false;

        IManagementCommandService service = null;
        
        if(currentApplic != null) {
            
            LOG.info("Anwendung gefunden: " + currentApplic.getUser().getID().getName());

            List<IManagementCommandService> managementServices =
                xmppSession.getRemoteServiceProxies(
                    IManagementCommandService.class, new ID[] {currentApplic.getUser().getID()});

            if(managementServices.size() == 1) {
                
                service = managementServices.get(0);
                CommandDescription[] commands = service.getSupportedCommands();
                
                for(int i = 0; i < commands.length; i++) {
                    if(commands[i].getLabel().compareToIgnoreCase("stop") == 0) {
                        stopAction = commands[i];
                        break;
                    }
                }
            }
            
            if((stopAction != null) && (service != null)) {
                
                parameter = new CommandParameters();
                parameter.set("Password", password);
                
                CommandResult retValue = service.execute(stopAction.getIdentifier(), parameter);
                if(retValue != null) {
                    returnValue = (String)retValue.getValue();
                    if((returnValue.trim().startsWith("OK:")) || (returnValue.indexOf("stopping") > -1)) {
                        result = true;
                        LOG.info("Application stopped: " + result);
                        // iResult = ApplicResult.RESULT_OK.ordinal();
                    } else {
                        LOG.error("Something went wrong: " + result);
                        // iResult = ApplicResult.RESULT_ERROR_INVALID_PASSWORD.ordinal();
                    }
                } else {
                    LOG.info("Return value is null!");
                    // iResult = ApplicResult.RESULT_ERROR_UNKNOWN.ordinal();
                }
            }
        }        

        return result;
    }

    public void bindService(ISessionService sessionService) {
                
        IPreferencesService prefs = Platform.getPreferencesService();
        String xmppUser = prefs.getString(Jms2OraPlugin.PLUGIN_ID,
                PreferenceConstants.XMPP_REMOTE_USER_NAME, "anonymous", null);
        String xmppPassword = prefs.getString(Jms2OraPlugin.PLUGIN_ID,
                PreferenceConstants.XMPP_REMOTE_PASSWORD, "anonymous", null);
        String xmppServer = prefs.getString(Jms2OraPlugin.PLUGIN_ID,
                PreferenceConstants.XMPP_SERVER, "krynfs.desy.de", null);

        try {
            sessionService.connect(xmppUser, xmppPassword, xmppServer);
            xmppSession = sessionService;
        } catch (Exception e) {
            xmppSession = null;
            LOG.warn("XMPP connection is not available, " + e.getMessage());
        }
    }

    public void unbindService(ISessionService service) {
        // Nothing to do here
    }
}
