
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
 *
 * $Id: DesyKrykCodeTemplates.xml,v 1.7 2010/04/20 11:43:22 bknerr Exp $
 */

package org.csstudio.ams.application.monitor.service;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.csstudio.ams.application.monitor.IRemoteService;
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
 * @since 03.05.2012
 */
public class XmppRemoteService implements IRemoteService {
    
    private static final Logger LOG = LoggerFactory.getLogger(XmppRemoteService.class);
    
    private static final long RESTART_TIME_DIFF = 900000L;
    
    private ISessionService xmppService;
    
    private String xmppGroupName;
    
    private String workspaceLocation;
    
    private long restartWaitTime;
    
    public XmppRemoteService(ISessionService xmpp, String groupName, String ws, long waitTime) {
        xmppService = xmpp;
        xmppGroupName = groupName;
        workspaceLocation = ws;
        restartWaitTime = waitTime;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean restart(String[] name, String host, String user) {
        boolean callCommand = true;
        long lastRestartTime = readRestartTimestamp();
        if (lastRestartTime != -1) {
            long diff = lastRestartTime + RESTART_TIME_DIFF;
            if (System.currentTimeMillis() < diff) {
                callCommand = false;
            }
        }
        boolean result = false;
        if (callCommand) {
            result = callRemoteCommand("restart", name, host, user);
        }
        return result;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean stop(String[] name, String host, String user) {
        return callRemoteCommand("stop", name, host, user);
    }
    
     
    /**
    *
    * @param applicationName
    * @param host
    * @param user
    */
   private boolean callRemoteCommand(final String method,
                                     final String[] applicationName,
                                     final String host,
                                     final String user) {

       String stopPassword = "";
       boolean success = false;

       final Object lock = new Object();
       synchronized (lock) {
           try {
               lock.wait(3000);
           } catch (final InterruptedException ie) {
               // Can be ignored
           }
       }

       if (xmppService == null) {
           LOG.info("XMPP login failed. Cannot stop the application.");
           return success;
       }

       Vector<IRosterItem> rosterItems = getRosterItems();

       synchronized(lock) {
           try {
               lock.wait(5000);
           } catch(final InterruptedException ie) {
               LOG.error("[*** InterruptedException ***]: {}", ie.getMessage());
           }
       }

       if(rosterItems.size() == 0) {
           LOG.info("XMPP roster not found. Leaving application.");

           // Allways 'false' here
           return success;
       }

       LOG.info("Manager initialized");
       LOG.info("Anzahl Directory-Elemente: {}", rosterItems.size());

       IRosterGroup jmsApplics = this.getRosterGroup(rosterItems, xmppGroupName);
       
       Collection<?> groupApplics = jmsApplics.getEntries();
       Iterator<?> iter = groupApplics.iterator();
       while (iter.hasNext()) {
           IRosterEntry o = (IRosterEntry) iter.next();
           LOG.info("Found: {} {}", o.getName(), o.getUser().getID().getName());
       }
       
       for (String s : applicationName) {
           if (s != null) {
               LOG.info("Application: {}, Method: {}", s, method);
               if (s.trim().length() > 0) {
                   IRosterEntry currentApplic = this.getRosterApplication(jmsApplics, s.trim(), host, user);
                   if(currentApplic != null) {
                       success = this.callCommand(method, currentApplic, stopPassword);
                       if (success == false) {
                           LOG.error("Breaking!");
                           break;
                       }
                   } else {
                       LOG.error("Cannot find application {}", s);
                       success = false;
                       break;
                   }
                   synchronized (this) {
                       try {
                        this.wait(restartWaitTime);
                       } catch (InterruptedException ie) {
                        LOG.warn("[*** InterruptedException ***]: {} has been interrupted.",
                                 this.getClass().getSimpleName());
                       }
                   }
               }
           }
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

       final IRosterManager rosterManager = xmppService.getRosterManager();

       // We have to wait until the ECF connection manager have been initialized
       synchronized(this) {

           do {

               try {
                   this.wait(2000);
               } catch(final InterruptedException ie) {
                   LOG.error("[*** InterruptedException ***]: {}", ie.getMessage());
               }

               roster = rosterManager.getRoster();

               // Get the roster items
               rosterItems = new Vector<IRosterItem> (roster.getItems());

           } while(++count <= 10 && rosterItems.isEmpty());
       }

       return rosterItems;
   }

   /**
    *
    * @param rosterItems
    * @param groupName
    * @return
    */
   private IRosterGroup getRosterGroup(final Vector<IRosterItem> rosterItems, final String groupName)
   {
       IRosterGroup jmsApplics = null;

       // Get the group of JMS applications
       for(final IRosterItem ri : rosterItems) {
           if(ri.getName().compareToIgnoreCase(groupName) == 0) {
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
   private IRosterEntry getRosterApplication(final IRosterGroup jmsApplics, final String applicName,
                                             final String host, final String user) {

       Vector<IRosterEntry> rosterEntries = null;
       IRosterEntry currentApplic = null;
       String name = null;

       if(jmsApplics != null) {

           rosterEntries = new Vector<IRosterEntry>(jmsApplics.getEntries());

           final Iterator<IRosterEntry> list = rosterEntries.iterator();
           while(list.hasNext()) {
               final IRosterEntry ce = list.next();
               name = ce.getUser().getID().toExternalForm().toLowerCase();
               if(name.contains(applicName)) {
                   if(name.indexOf(host) > -1 && name.indexOf(user) > -1) {
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
   private boolean callCommand(final String method, final IRosterEntry currentApplic, final String password) {

       CommandDescription stopAction = null;
       CommandParameters parameter = null;
       String returnValue = null;
       boolean result = false;

       IManagementCommandService service = null;

       if(currentApplic != null) {

           LOG.info("Anwendung gefunden: {}", currentApplic.getUser().getID().getName());

           final List<IManagementCommandService> managementServices =
                   xmppService.getRemoteServiceProxies(
                   IManagementCommandService.class, new ID[] {currentApplic.getUser().getID()});

           if(managementServices.size() == 1) {

               service = managementServices.get(0);
               final CommandDescription[] commands = service.getSupportedCommands();

               for (final CommandDescription command : commands) {
                   if(command.getLabel().compareToIgnoreCase(method) == 0) {
                       stopAction = command;
                       break;
                   }
               }
           }

           if(stopAction != null && service != null) {

               parameter = new CommandParameters();
               parameter.set("Password", password);

               final CommandResult retValue = service.execute(stopAction.getIdentifier(), parameter);
               if(retValue != null) {
                   returnValue = (String) retValue.getValue();
                   LOG.info(returnValue);
                   boolean resultOk = returnValue.trim().startsWith("OK:");
                   boolean methodResultOk = false;
                   if (method.equals("stop")) {
                       methodResultOk = (returnValue.toLowerCase().indexOf("stopping") > -1);
                   } else if (method.equals("restart")) {
                       methodResultOk = (returnValue.toLowerCase().indexOf("restarting") > -1);
                   }
                   
                   if(resultOk || methodResultOk) {
                       result = true;
                       LOG.info("Application {}ed: {}", method, result);
                       if (method.equalsIgnoreCase("restart")) {
                           saveRestartTimestamp(System.currentTimeMillis());
                       }
                   } else {
                       LOG.error("Something went wrong: {}", result);
                   }
               } else {
                   LOG.info("Return value is null!");
               }
           }
       }

       return result;
   }
   
   private boolean saveRestartTimestamp(long timestamp) {
       
       boolean success = false;
       
       File file = new File(workspaceLocation + "amsRestarted");
       if (file.exists()) {
           file.delete();
       }

       DataOutputStream out = null;
       try {
        out = new DataOutputStream(new FileOutputStream(file));
        out.writeLong(timestamp);
        success = true;
       } catch (FileNotFoundException e) {
           LOG.error("[*** FileNotFoundException ***]: {}", e.getMessage());
       } catch (IOException e) {
           LOG.error("[*** IOException ***]: {}", e.getMessage());
       } finally {
           if (out!=null){try{out.close();}catch(Exception e){/**/}}
       }
       
       return success;
   }
   
   private long readRestartTimestamp() {
       
       long timestamp = -1L;
       
       File file = new File(workspaceLocation + "amsRestarted");
       if (!file.exists()) {
           LOG.warn("The file, containing the timestamp of the last restart, does not exist!");
           return timestamp;
       }
       
       DataInputStream in = null;
       try {
           in = new DataInputStream(new FileInputStream(file));
           timestamp = in.readLong();
//           if (file.delete()) {
//               LOG.info("File that indicates a restart of AMS has been deleted.");
//           } else {
//               LOG.warn("CANNOT delete the file that indicates a restart of AMS");
//           }
       } catch (FileNotFoundException e) {
           LOG.error("[*** FileNotFoundException ***]: {}", e.getMessage());
       } catch (IOException e) {
           LOG.error("[*** IOException ***]: {}", e.getMessage());
       } finally {
           if (in!=null){try{in.close();}catch(Exception e){/**/}}
       }
       
       return timestamp;
   }
}
