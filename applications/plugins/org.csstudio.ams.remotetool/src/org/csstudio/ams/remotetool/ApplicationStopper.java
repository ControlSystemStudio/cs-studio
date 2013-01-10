
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
 * $Id: DesyKrykCodeTemplates.xml,v 1.7 2010/04/20 11:43:22 bknerr Exp $
 */

package org.csstudio.ams.remotetool;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.csstudio.remote.management.CommandDescription;
import org.csstudio.remote.management.CommandParameters;
import org.csstudio.remote.management.CommandResult;
import org.csstudio.remote.management.IManagementCommandService;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.presence.roster.IRoster;
import org.eclipse.ecf.presence.roster.IRosterEntry;
import org.eclipse.ecf.presence.roster.IRosterGroup;
import org.eclipse.ecf.presence.roster.IRosterItem;
import org.eclipse.ecf.presence.roster.IRosterManager;
import org.remotercp.service.connection.session.ISessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author mmoeller
 * @version 1.0
 * @since 16.12.2011
 */
public class ApplicationStopper {
    
    /** The class logger */
    private static final Logger LOG = LoggerFactory.getLogger(ApplicationStopper.class);

    private ISessionService xmppSession;
    
    private IRosterManager rosterManager;
    
    private String amsRosterGroup;
    
    public ApplicationStopper(ISessionService xmpp, String rosterGroup) {
        xmppSession = xmpp;
        amsRosterGroup = rosterGroup;
        rosterManager = xmppSession.getRosterManager();
    }
    
    @SuppressWarnings("unchecked")
    public int stopApplication(String host, String applicName, String user, String pw) {
        
        Vector<IRosterItem> rosterItems = null;
        
        int iResult = ApplicResult.RESULT_ERROR_GENERAL.getApplicResultNumber();

        IRoster roster = null;
        int count = 0;
  
        // We have to wait until the DCF connection manager have been initialized
        synchronized(this) {
            
            do {
                try {
                    this.wait(500);
                } catch(InterruptedException ie) {
                    LOG.error("*** InterruptedException ***: " + ie.getMessage());
                }
  
                roster = rosterManager.getRoster();

                // Get the roster items
                rosterItems = new Vector<IRosterItem>(roster.getItems());
            } while((++count <= 10) && (rosterItems.isEmpty()));
        }
        
        if(rosterItems.size() == 0) {
            LOG.info("XMPP roster not found. Stopping application.");
            return ApplicResult.RESULT_ERROR_XMPP.getApplicResultNumber();
        }
        
        LOG.info("Manager initialized");
        LOG.info("Anzahl Directory-Elemente: " + rosterItems.size());
        
        // Get the group of JMS applications
        IRosterGroup jmsApplics = getApplicationGroup(rosterItems, amsRosterGroup);
        
        // Get the application
        if(jmsApplics != null) {
            IRosterEntry currentApplic = getApplicationEntry(jmsApplics, host, applicName, user);
            if(currentApplic != null) {
                LOG.info("Anwendung gefunden: " + currentApplic.getUser().getID().getName());
                iResult = this.invokeStopMethod(currentApplic, pw);
            } else {
                iResult = ApplicResult.RESULT_ERROR_NOT_FOUND.getApplicResultNumber();
            }
        } else {
            iResult = ApplicResult.RESULT_ERROR_UNKNOWN.getApplicResultNumber();
        }

        return iResult;
    }
    
    private boolean containsItem(final String line, final String value, boolean caseSensitive) {
        
        String work = null;
        String search = null;
        if (caseSensitive == false) {
            work = line.toLowerCase();
            search = value.toLowerCase();
        } else {
            work = line;
            search = value;
        }
        
        boolean result = (work.indexOf(search) > -1); 
        return result;
    }
    
    private IRosterGroup getApplicationGroup(Vector<IRosterItem> rosterItems, String name) {
        IRosterGroup result = null;
        for(IRosterItem ri : rosterItems) {
            if(ri.getName().compareToIgnoreCase(name) == 0) {
                result = (IRosterGroup) ri;
                break;
            }
        }
        return result;
    }
    
    private IRosterEntry getApplicationEntry(IRosterGroup jmsApplics,
                                            String host,
                                            String applicName,
                                            String user) {
        IRosterEntry result = null;
        @SuppressWarnings("unchecked")
        Vector<IRosterEntry> rosterEntries = new Vector<IRosterEntry>(jmsApplics.getEntries());
        
        Iterator<IRosterEntry> list = rosterEntries.iterator();
        while(list.hasNext()) {
            IRosterEntry ce = list.next();
            String name = ce.getUser().getID().toExternalForm();
            if(name.contains(applicName)) {
                if(containsItem(name, host, false) && containsItem(name, user, true)) {
                    result = ce;
                    break;
                }
            }
        }

        return result;
    }
    
    private int invokeStopMethod(IRosterEntry currentApplic, String pw) {
        
        IManagementCommandService service = null;
        int iResult = ApplicResult.RESULT_ERROR_GENERAL.getApplicResultNumber();
        
        List<IManagementCommandService> managementServices =
                xmppSession.getRemoteServiceProxies(
                    IManagementCommandService.class, new ID[] {currentApplic.getUser().getID()});
            
        CommandParameters parameter = null;
        CommandDescription stopAction = null;

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
        
        return iResult;
    }
}
