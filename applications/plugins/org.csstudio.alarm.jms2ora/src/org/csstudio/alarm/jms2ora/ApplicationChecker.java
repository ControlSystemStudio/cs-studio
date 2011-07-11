
/* 
 * Copyright (c) 2010 Stiftung Deutsches Elektronen-Synchrotron, 
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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import org.csstudio.alarm.jms2ora.preferences.PreferenceConstants;
import org.csstudio.alarm.jms2ora.util.JmsSender;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.management.CommandDescription;
import org.csstudio.platform.management.CommandResult;
import org.csstudio.platform.management.IManagementCommandService;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.presence.roster.IRoster;
import org.eclipse.ecf.presence.roster.IRosterEntry;
import org.eclipse.ecf.presence.roster.IRosterGroup;
import org.eclipse.ecf.presence.roster.IRosterItem;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.util.tracker.ServiceTracker;
import org.remotercp.service.connection.session.ISessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO (mmoeller) : 
 * 
 * @author mmoeller
 * @version 
 * @since 30.09.2010
 */
public class ApplicationChecker {

    /** */
    private ISessionService session;

    /** The class logger */
    private static final Logger LOG = LoggerFactory.getLogger(ApplicationChecker.class);

    /**
     * Max. time difference between now and the last received message (ms).
     * The preference value is given as minutes and has to be converted. 
     */
    private long maxReceiveDiffTime;
    
    /**
     *  Max. time difference between now and the last stored message (ms).
     * The preference value is given as minutes and has to be converted. 
     */
    private long maxStoreDiffTime;
    
    /** */
    private boolean errorState;

    /**
     * 
     */
    public ApplicationChecker() {
        
        IPreferencesService prefs = Platform.getPreferencesService();
        maxReceiveDiffTime = prefs.getLong(Jms2OraPlugin.PLUGIN_ID,
                               PreferenceConstants.MAX_RECEIVE_DIFF_TIME, 10, null);
        
        maxStoreDiffTime = prefs.getLong(Jms2OraPlugin.PLUGIN_ID,
                                           PreferenceConstants.MAX_STORE_DIFF_TIME, 10, null);

        // Convert the time to ms
        maxReceiveDiffTime *= 60000L;
        LOG.info("Max receive time difference: ", maxReceiveDiffTime);
        
        maxStoreDiffTime *= 60000L;
        LOG.info("Max store time difference: ", maxStoreDiffTime);
        
        errorState = this.existsErrorFile();
    }

    /**
     * 
     * @param bundleContext
     * @param applicationName
     * @param host
     * @param user
     * @param password
     */
    public boolean checkExternInstance(BundleContext bundleContext, String applicationName, String host, String user)
    {
        Vector<IRosterItem> rosterItems = null;
        IRosterGroup jmsApplics = null;
        IRosterEntry currentApplic = null;
        boolean success = false;
        
        IPreferencesService prefs = Platform.getPreferencesService();
        String xmppUser = prefs.getString(Jms2OraPlugin.PLUGIN_ID,
                PreferenceConstants.XMPP_REMOTE_USER_NAME, "anonymous", null);
        String xmppPassword = prefs.getString(Jms2OraPlugin.PLUGIN_ID,
                PreferenceConstants.XMPP_REMOTE_PASSWORD, "anonymous", null);
        String xmppServer = prefs.getString(Jms2OraPlugin.PLUGIN_ID,
                PreferenceConstants.XMPP_SERVER, "krynfs.desy.de", null);
        
        connectToXMPPServer(xmppServer, xmppUser, xmppPassword);

        String serviceFilter = "(&(objectClass=" +
        ISessionService.class.getName() + "))";

        // Get the Session Service from the Application Admin Service
        ServiceTracker tracker = null;
        try {
            tracker = new ServiceTracker(bundleContext, bundleContext.createFilter(serviceFilter), null);
            tracker.open();
        
            Object[] allServices = tracker.getServices();
            if(allServices != null) {
                
                List<Object> services = Arrays.asList(allServices);
                ISessionService[] regApps = services.toArray(new ISessionService[0]);
                
                if(regApps.length > 0) {
                    
                    session = regApps[0];
                    success = true;
                }
            } else {
                success = false;
            }
            
            tracker.close();
        } catch(InvalidSyntaxException e) {
            success = false;
        }

        rosterItems = getRosterItems();

        synchronized(this) {
            
            try {
                this.wait(5000);
            } catch(InterruptedException ie) {
                LOG.error("[*** InterruptedException ***]: ", ie.getMessage());
            }
        }
        
        if(rosterItems.size() == 0) {
            LOG.info("XMPP roster not found. Stopping application.");
            
            // Allways 'false' here
            return success;
        } else {
            LOG.info("Manager initialized");
        }

        LOG.info("Anzahl Directory-Elemente: ", rosterItems.size());
        
        jmsApplics = this.getRosterGroup(rosterItems, "jms-applications");
        currentApplic = this.getRosterApplication(jmsApplics, "jms2oracle", host, user);
        
        if(currentApplic != null) {
            success = this.getStatisticsAndCheck(currentApplic);
        }
        
        return success;
    }
    
    /**
     * 
     * @param currentApplic
     * @param password
     * @return
     */
    private boolean getStatisticsAndCheck(IRosterEntry currentApplic) {
        
        CommandDescription getStatisticsAction = null;
        String returnValue = null;
        boolean result = false;

        IManagementCommandService service = null;
        
        if(currentApplic != null) {
            
            // TODO:
            // LOG.info("Anwendung gefunden: ", currentApplic.getUser().getID().getName());
            
//            List<IManagementCommandService> managementServices =
//                session.getRemoteServiceProxies(
//                    IManagementCommandService.class, new ID[] {currentApplic.getUser().getID()});
            
            List<IManagementCommandService> managementServices = null;

            if(managementServices.size() == 1) {
                
                service = managementServices.get(0);
                CommandDescription[] commands = service.getSupportedCommands();
                
                for(int i = 0; i < commands.length; i++) {
                    
                    if(commands[i].getLabel().compareToIgnoreCase("get statistics") == 0) {
                        
                        getStatisticsAction = commands[i];
                        break;
                    }
                }
            }
            
            if(getStatisticsAction != null) {
                
                CommandResult retValue = service.execute(getStatisticsAction.getIdentifier(), null);
                if(retValue != null) {
                    returnValue = (String)retValue.getValue();
                    result = isApplicWorking(returnValue);
                } else {
                    LOG.warn("Command action value is null!");
                    result = false;
                }
            }
            
            if(result == false) {
                
                if(errorState == false) {
                    
                    // Retrieve the check interval
                    IPreferencesService pref = Platform.getPreferencesService();
                    String url = pref.getString(Jms2OraPlugin.PLUGIN_ID,
                                                PreferenceConstants.JMS_PRODUCER_URL, "", null);
                    
                    JmsSender sender = new JmsSender(url, "ALARM");
                    sender.sendMessage("alarm", "APPLIC:Jms2Ora", "Jms2Ora does not work properly.", "MAJOR");
                    sender.closeAll();
                    sender = null;
                    
                    // The flag indicates that the alarm message was sent
                    errorState = true;
                }
            } else {
                errorState = false;
            }
        }        

        if(errorState) {
            this.createErrorFile();
        }
        
        return result;
    }

    /**
     * 
     * @return
     */
    private boolean isApplicWorking(String xmlText) {
        
        StringReader reader = new StringReader(xmlText);
        SAXBuilder builder = new SAXBuilder();
        Document xmlDoc = null;
        Element rootElement = null;
        Element child = null;
        String receivedDateStr = null;
        String storedDateStr = null;
        String filteredDateStr = null;
        String discardedDateStr = null;
        boolean result = false;
        
        try {
            xmlDoc = builder.build(reader);
            
            rootElement = xmlDoc.getRootElement();
            if(rootElement.getName().compareTo("StatisticProtocol") != 0) {
                LOG.warn(" NOT a statistic XML output. Discarding...");
                return result;
            }

            child = rootElement.getChild("CollectionSupervisor");
            
            receivedDateStr = getActualDate(child, "Jms2Ora", "Received messages");
            storedDateStr = getActualDate(child, "Jms2Ora", "Stored messages");
            filteredDateStr = getActualDate(child, "Jms2Ora", "Filtered messages");
            discardedDateStr = getActualDate(child, "Jms2Ora", "Discarded messages");

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

            if((receivedDateStr != null) && (storedDateStr != null)
                    && (filteredDateStr != null) && (discardedDateStr != null)) {
                try {
                    Date receivedDate = sdf.parse(receivedDateStr);
                    Date storedDate = sdf.parse(storedDateStr);
                    Date filteredDate = sdf.parse(filteredDateStr);
                    Date discardedDate = sdf.parse(discardedDateStr);
                
                    long currentTime = System.currentTimeMillis();
                    Date currentDate = new Date(currentTime);
    
                    LOG.info("Current time:       " + sdf.format(currentDate) + " (" + currentTime + ")");
                    LOG.info("Received messages:  " + receivedDateStr + " (" + receivedDate.getTime() + ")");
                    LOG.info("Stored messages:    " + storedDateStr + " (" + storedDate.getTime() + ")");
                    LOG.info("Filtered messages:  " + filteredDateStr + " (" + filteredDate.getTime() + ")");
                    LOG.info("Discarded messages: " + discardedDateStr + " (" + discardedDate.getTime() + ")");
                
                    long timeReceivedDiff = currentTime - receivedDate.getTime();
                    long timeStoredDiff = currentTime - storedDate.getTime();
                    if(timeReceivedDiff > maxReceiveDiffTime) {
                        return false;
                    }
                    
                    if(timeStoredDiff > maxStoreDiffTime) {
                        
                        if((filteredDate.getTime() > storedDate.getTime())
                            || (discardedDate.getTime() > storedDate.getTime())) {
                            
                            result = true;
                        } else {
                            result = false;
                        }
                    } else {
                        result = true;
                    }
                } catch(ParseException pe) {
                    result = false;
                }
            }
       } catch(JDOMException jdome) {
           LOG.error("ApplicationChecker.isApplicWorking(): [*** JDOMException ***]: ", jdome.getMessage());
        } catch(IOException ioe) {
            LOG.error("ApplicationChecker.isApplicWorking(): [*** IOException ***]: ", ioe.getMessage());
        } finally {
            if(reader != null) reader.close();
        }

        return result;
    }
    
    /**
     * 
     * @param child
     * @param app
     * @param desc
     * @return
     */
    private String getActualDate(Element child, String app, String desc) {
        
        Element subElement = null;
        Element column = null;
        Attribute attr = null;
        String value = null;
        String result = null;
        boolean doNext = false;
        
        // Collectors
        List<?> list = child.getChildren();
        for(int i = 0;i < list.size();i++) {

            subElement = (Element)list.get(i);
            if(subElement.getName().toLowerCase().compareTo("collector") == 0) {
                
                // Columns
                List<?> colList = subElement.getChildren();
                for(int c = 0;c < colList.size();c++) {
                    
                    column = (Element)colList.get(c);
                    attr = column.getAttribute("Name");
                    value = attr.getValue();
                    if(value.toLowerCase().compareTo("application") == 0) {
                        
                        if(column.getValue().compareTo(app) == 0) {
                            doNext = true;
                            break;
                        }
                    }
                }
                
                if(doNext) {
                    
                    for(int c = 0;c < colList.size();c++) {
                        
                        column = (Element)colList.get(c);
                        attr = column.getAttribute("Name");
                        value = attr.getValue();
                        if(value.toLowerCase().compareTo("descriptor") == 0) {
                            
                            if(column.getValue().compareTo(desc) == 0) {
                                doNext = true;
                            } else {
                                doNext = false;
                            }
                            
                            break;
                        }
                    }
                }
                
                if(doNext) {

                    for(int c = 0;c < colList.size();c++) {
                        
                        column = (Element)colList.get(c);
                        attr = column.getAttribute("Name");
                        value = attr.getValue();
                        if(value.toLowerCase().compareTo("date actual") == 0) {
                            result = column.getValue();
                            break;
                        }
                    }
                } else {
                    result = null;
                }
                
                if(result != null) {
                    break;
                }
            }
        }

        return result;
    }
    
    private boolean connectToXMPPServer(String xmppServer, String xmppUser, String xmppPassword)
    {
        boolean success = false;
        
        // TODO:
//        try
//        {
//            HeadlessConnection.connect(xmppUser, xmppPassword, xmppServer, ECFConstants.XMPP);
//            ServiceLauncher.startRemoteServices();
//            success = true;
//        }
//        catch(Exception e)
//        {
//            CentralLogger.getInstance().warn(this, "Could not connect to XMPP server: " + e.getMessage());
//            success = false;
//        }
        
        return success;
    }

    /**
     * 
     * @return
     */
    @SuppressWarnings("unchecked")
    private Vector<IRosterItem> getRosterItems()
    {
        IRoster roster = null;
        Vector<IRosterItem> rosterItems = null;
        int count = 0;

        // We have to wait until the DCF connection manager have been initialized
        synchronized(this)
        {
            do
            {
                try
                {
                    this.wait(2000);
                }
                catch(InterruptedException ie)
                {
                    LOG.error("[*** InterruptedException ***]: ", ie.getMessage());
                }
  
                roster = session.getRoster();

                // Get the roster items
                rosterItems = new Vector<IRosterItem>((Collection<IRosterItem>)roster.getItems());
            }
            while((++count <= 10) && (rosterItems.isEmpty()));
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
            rosterEntries = new Vector<IRosterEntry>((Collection<IRosterEntry>)jmsApplics.getEntries());
            
            Iterator<IRosterEntry> list = rosterEntries.iterator();
            while(list.hasNext())
            {
                IRosterEntry ce = list.next();
                // TODO:
                name = ""; //ce.getUser().getID().toExternalForm();
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
    
    private boolean existsErrorFile() {
        
        String workspaceLocation = null;
        boolean fileExists = false;
        
        // Retrieve the location of the workspace directory
        try {
            workspaceLocation = Platform.getLocation().toPortableString();
            if(workspaceLocation.endsWith("/") == false) {
                workspaceLocation = workspaceLocation + "/";
            }
        } catch(IllegalStateException ise) {
            LOG.warn("Workspace location could not be found. Using working directory '.'");
            workspaceLocation = "./";
        }
        
        File errorFile = new File(workspaceLocation + "error");
        if(fileExists = errorFile.exists()) {
            
            errorFile.delete();
        }
        
        return fileExists;
    }
    
    private void createErrorFile() {
        
        FileOutputStream errorFileStream = null;
        String workspaceLocation = null;
        
        // Retrieve the location of the workspace directory
        try {
            workspaceLocation = Platform.getLocation().toPortableString();
            if(workspaceLocation.endsWith("/") == false) {
                workspaceLocation = workspaceLocation + "/";
            }
        } catch(IllegalStateException ise) {
            LOG.warn("Workspace location could not be found. Using working directory '.'");
            workspaceLocation = "./";
        }
        
        File errorFile = new File(workspaceLocation + "error");
        try {
            errorFileStream = new FileOutputStream(errorFile);
            errorFileStream.write(1);
        } catch(FileNotFoundException fnfe) {
            LOG.error("[*** FileNotFoundException ***]: ", fnfe.getMessage());
        } catch(IOException ioe) {
            LOG.error("[*** IOException ***]: ", ioe.getMessage());
        } finally {
            if(errorFileStream != null) {
                try{errorFileStream.close();}catch(Exception e) { /*Can be ignored */ }
                errorFileStream = null;
            }
        }
    }
}
