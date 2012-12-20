
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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import org.csstudio.alarm.jms2ora.preferences.PreferenceConstants;
import org.csstudio.alarm.jms2ora.util.CheckErrorState;
import org.csstudio.alarm.jms2ora.util.JmsSender;
import org.csstudio.remote.management.CommandDescription;
import org.csstudio.remote.management.CommandResult;
import org.csstudio.remote.management.IManagementCommandService;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.presence.roster.IRoster;
import org.eclipse.ecf.presence.roster.IRosterEntry;
import org.eclipse.ecf.presence.roster.IRosterGroup;
import org.eclipse.ecf.presence.roster.IRosterItem;
import org.eclipse.ecf.presence.roster.IRosterManager;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.joda.time.DateTime;
import org.remotercp.common.tracker.IGenericServiceListener;
import org.remotercp.service.connection.session.ISessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author mmoeller
 * @version
 * @since 30.09.2010
 */
public class ApplicationChecker implements IGenericServiceListener<ISessionService> {

    /** The class logger. */
    private static final Logger LOG = LoggerFactory.getLogger(ApplicationChecker.class);

    /** The SessionService for the XMPP login */
    private ISessionService xmppSession;

    /** */
    private CheckErrorState errorState;

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

    /**
     *
     */
    public ApplicationChecker() {

        final IPreferencesService prefs = Platform.getPreferencesService();
        maxReceiveDiffTime = prefs.getLong(Jms2OraActivator.PLUGIN_ID,
                               PreferenceConstants.MAX_RECEIVE_DIFF_TIME, 10, null);

        maxStoreDiffTime = prefs.getLong(Jms2OraActivator.PLUGIN_ID,
                                           PreferenceConstants.MAX_STORE_DIFF_TIME, 10, null);

        // Convert the time to ms
        maxReceiveDiffTime *= 60000L;
        LOG.info("Max receive time difference: {}", maxReceiveDiffTime);

        maxStoreDiffTime *= 60000L;
        LOG.info("Max store time difference: {}", maxStoreDiffTime);

        errorState = this.readErrorState();
        xmppSession = null;
    }

    /**
     * Method.
     * @param applicationName
     * @param host
     * @param user
     */
    public boolean checkExternInstance(final String applicationName,
                                       final String host,
                                       final String user) throws XmppLoginException {

        Vector<IRosterItem> rosterItems = null;
        IRosterGroup jmsApplics = null;
        IRosterEntry currentApplic = null;
        boolean success = false;

        Jms2OraActivator.getDefault().addSessionServiceListener(this);

        final Object lock = new Object();
        synchronized (lock) {
            try {
                lock.wait(10000L);
            } catch (final InterruptedException ie) {
                // Can be ignored
            }
        }

        if (xmppSession == null) {
            LOG.warn("XMPP login failed. Stopping application.");
            throw new XmppLoginException("Cannot check the status of Jms2Ora.");
        }

        rosterItems = getRosterItems();

        synchronized (this) {

            try {
                this.wait(5000);
            } catch (final InterruptedException ie) {
                LOG.error("[*** InterruptedException ***]: {}", ie.getMessage());
            }
        }

        if (rosterItems.size() == 0) {
            LOG.info("XMPP roster not found. Stopping application.");

            // Allways 'false' here
            return success;
        }

        if (errorState != null) {
            this.deleteErrorFile();
        }

        LOG.info("Manager initialized");
        LOG.info("Anzahl Directory-Elemente: {}", rosterItems.size());

        jmsApplics = this.getRosterGroup(rosterItems, "jms-applications");
        currentApplic = this.getRosterApplication(jmsApplics, applicationName, host, user);

        if(currentApplic != null) {
            success = this.getStatisticsAndCheck(currentApplic);
        }

        if (xmppSession != null) {
            synchronized (xmppSession) {
                try {
                    xmppSession.wait(500);
                } catch (InterruptedException ie) {
                    LOG.info("xmppService.wait(500) has been interrupted.");
                }
            }
            xmppSession.disconnect();
        }

        return success;
    }

    /**
     *
     * @param currentApplic
     * @param password
     * @return
     */
    private boolean getStatisticsAndCheck(final IRosterEntry currentApplic) {

        CommandDescription getStatisticsAction = null;
        String returnValue = null;
        IManagementCommandService service = null;
        boolean isWorking = false;

        if (currentApplic != null) {

            LOG.info("Anwendung gefunden: {}", currentApplic.getUser().getID().getName());

            final List<IManagementCommandService> managementServices =
                xmppSession.getRemoteServiceProxies(
                    IManagementCommandService.class, new ID[] {currentApplic.getUser().getID()});

            if (managementServices.size() == 1) {

                service = managementServices.get(0);
                final CommandDescription[] commands = service.getSupportedCommands();

                for (final CommandDescription command : commands) {
                    if (command.getLabel().compareToIgnoreCase("get statistics") == 0) {
                        getStatisticsAction = command;
                        break;
                    }
                }
            }

            if (service == null) {
                LOG.error("Cannot get the management command service.");
                return false;
            }

            if (getStatisticsAction != null) {

                final CommandResult retValue = service.execute(getStatisticsAction.getIdentifier(), null);
                if (retValue != null) {
                    returnValue = (String) retValue.getValue();
                    isWorking = isApplicWorking(returnValue);
                } else {
                    LOG.warn("Command action value is null!");
                    isWorking = false;
                }
            }

            if (isWorking == false) {
                if (errorState == null) {
                    final IPreferencesService pref = Platform.getPreferencesService();
                    final String url = pref.getString(Jms2OraActivator.PLUGIN_ID,
                                                PreferenceConstants.JMS_PRODUCER_URL, "", null);

                    JmsSender sender = new JmsSender(url, "ALARM");
                    sender.sendMessage("alarm", "APPLIC:Jms2Ora", "Jms2Ora is NOT working.", "MAJOR");
                    sender.closeAll();
                    sender = null;

                    // The object indicates that the alarm message was sent
                    errorState = new CheckErrorState(new DateTime(), true);
                }
            } else {
                if (errorState != null) {
                    if (errorState.isNotificationSent()) {
                        final IPreferencesService pref = Platform.getPreferencesService();
                        final String url = pref.getString(Jms2OraActivator.PLUGIN_ID,
                                                    PreferenceConstants.JMS_PRODUCER_URL, "", null);

                        JmsSender sender = new JmsSender(url, "ALARM");
                        sender.sendMessage("alarm", "APPLIC:Jms2Ora", "Jms2Ora is working again.", "NO_ALARM");
                        sender.closeAll();
                        sender = null;
                    }
                    errorState = null;
                }
            }
        }

        if(errorState != null) {
            this.createErrorFile(errorState);
        }

        return isWorking;
    }

    /**
     *
     * @return
     */
    private boolean isApplicWorking(final String xmlText) {

        final StringReader reader = new StringReader(xmlText);
        final SAXBuilder builder = new SAXBuilder();
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
            if (rootElement.getName().compareTo("StatisticProtocol") != 0) {
                LOG.warn(" NOT a statistic XML output. Discarding...");
                return result;
            }

            child = rootElement.getChild("CollectionSupervisor");

            receivedDateStr = getActualDate(child, "Jms2Ora", "Received messages");
            storedDateStr = getActualDate(child, "Jms2Ora", "Stored messages");
            filteredDateStr = getActualDate(child, "Jms2Ora", "Filtered messages");
            discardedDateStr = getActualDate(child, "Jms2Ora", "Discarded messages");

            final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

            if (receivedDateStr != null && storedDateStr != null
                && filteredDateStr != null && discardedDateStr != null) {
                try {
                    final Date receivedDate = sdf.parse(receivedDateStr);
                    final Date storedDate = sdf.parse(storedDateStr);
                    final Date filteredDate = sdf.parse(filteredDateStr);
                    final Date discardedDate = sdf.parse(discardedDateStr);

                    final long currentTime = System.currentTimeMillis();
                    final Date currentDate = new Date(currentTime);

                    LOG.info("Current time:       " + sdf.format(currentDate) + " (" + currentTime + ")");
                    LOG.info("Received messages:  " + receivedDateStr + " (" + receivedDate.getTime() + ")");
                    LOG.info("Stored messages:    " + storedDateStr + " (" + storedDate.getTime() + ")");
                    LOG.info("Filtered messages:  " + filteredDateStr + " (" + filteredDate.getTime() + ")");
                    LOG.info("Discarded messages: " + discardedDateStr + " (" + discardedDate.getTime() + ")");

                    final long timeReceivedDiff = currentTime - receivedDate.getTime();
                    final long timeStoredDiff = currentTime - storedDate.getTime();
                    if (timeReceivedDiff > maxReceiveDiffTime) {
                        return false;
                    }

                    if (timeStoredDiff > maxStoreDiffTime) {
                        result = false;
//                        if (filteredDate.getTime() > storedDate.getTime()
//                            || discardedDate.getTime() > storedDate.getTime()) {
//
//                            result = true;
//                        } else {
//                            result = false;
//                        }
                    } else {
                        result = true;
                    }
                } catch(final ParseException pe) {
                    result = false;
                }
            }
       } catch(final JDOMException jdome) {
           LOG.error("[*** JDOMException ***]: {}", jdome.getMessage());
        } catch(final IOException ioe) {
            LOG.error("[*** IOException ***]: {}", ioe.getMessage());
        } finally {
            reader.close();
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
    private String getActualDate(final Element child, final String app, final String desc) {

        Element subElement = null;
        Element column = null;
        Attribute attr = null;
        String value = null;
        String result = null;
        boolean doNext = false;

        // Collectors
        final List<?> list = child.getChildren();
        for (int i = 0;i < list.size();i++) {

            subElement = (Element) list.get(i);
            if (subElement.getName().toLowerCase().compareTo("collector") == 0) {

                // Columns
                final List<?> colList = subElement.getChildren();
                for(int c = 0;c < colList.size();c++) {

                    column = (Element)colList.get(c);
                    attr = column.getAttribute("Name");
                    value = attr.getValue();
                    if (value.toLowerCase().compareTo("application") == 0) {

                        if (column.getValue().compareTo(app) == 0) {
                            doNext = true;
                            break;
                        }
                    }
                }

                if (doNext) {

                    for (int c = 0; c < colList.size(); c++) {

                        column = (Element) colList.get(c);
                        attr = column.getAttribute("Name");
                        value = attr.getValue();
                        if (value.toLowerCase().compareTo("descriptor") == 0) {

                            if(column.getValue().compareTo(desc) == 0) {
                                doNext = true;
                            } else {
                                doNext = false;
                            }

                            break;
                        }
                    }
                }

                if (doNext) {

                    for (int c = 0; c < colList.size(); c++) {

                        column = (Element) colList.get(c);
                        attr = column.getAttribute("Name");
                        value = attr.getValue();
                        if (value.toLowerCase().compareTo("date actual") == 0) {
                            result = column.getValue();
                            break;
                        }
                    }
                }

                if (result != null) {
                    break;
                }
            }
        }

        return result;
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

        final IRosterManager rosterManager = xmppSession.getRosterManager();

        // We have to wait until the ECF connection manager have been initialized
        synchronized (this) {

            do {

                try {
                    this.wait(2000);
                } catch (final InterruptedException ie) {
                    LOG.error("[*** InterruptedException ***]: {}", ie.getMessage());
                }

                roster = rosterManager.getRoster();

                // Get the roster items
                rosterItems = new Vector<IRosterItem>(roster.getItems());

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
    private IRosterGroup getRosterGroup(final Vector<IRosterItem> rosterItems, final String groupName) {

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
    private IRosterEntry getRosterApplication(final IRosterGroup jmsApplics,
                                              final String applicName,
                                              final String host, final String user) {

        Vector<IRosterEntry> rosterEntries = null;
        IRosterEntry currentApplic = null;
        String name = null;

        if (jmsApplics != null) {

            rosterEntries = new Vector<IRosterEntry>(jmsApplics.getEntries());

            final Iterator<IRosterEntry> list = rosterEntries.iterator();
            while (list.hasNext()) {
                final IRosterEntry ce = list.next();
                name = ce.getUser().getID().toExternalForm();
                if (name.contains(applicName)) {
                    if (name.indexOf(host) > -1 && name.indexOf(user) > -1) {
                        currentApplic = ce;
                        break;
                    }
                }
            }
        }

        return currentApplic;
    }

    private CheckErrorState readErrorState() {

        String workspaceLocation = null;

        // Retrieve the location of the workspace directory
        try {
            workspaceLocation = Platform.getLocation().toPortableString();
            if (!workspaceLocation.endsWith("/")) {
                workspaceLocation = workspaceLocation + "/";
            }
        } catch (final IllegalStateException ise) {
            LOG.warn("Workspace location could not be found. Using working directory '.'");
            workspaceLocation = "./";
        }

        final File errorFile = new File(workspaceLocation + "errorState.ser");
        if (!errorFile.exists()) {
            return null;
        }

        CheckErrorState result = null;
        ObjectInputStream fileStream = null;
        
        try {
            fileStream = new ObjectInputStream(new FileInputStream(errorFile));
            Object o = fileStream.readObject();
            if (o instanceof CheckErrorState) {
                result = (CheckErrorState) o;
            }
        } catch (FileNotFoundException e) {
            LOG.error("[*** FileNotFoundException ***]: {}", e.getMessage());
        } catch (IOException e) {
            LOG.error("[*** IOException ***]: {}", e.getMessage());
        } catch (ClassNotFoundException e) {
            LOG.error("[*** ClassNotFoundException ***]: {}", e.getMessage());
        } finally {
            if (fileStream != null) {
                try{fileStream.close();}catch(final Exception e) { /*Can be ignored */ }
                fileStream = null;
            }
        }
        
        return result;
    }

    private void createErrorFile(CheckErrorState state) {

        ObjectOutputStream errorFile = null;
        String workspaceLocation = null;

        // Retrieve the location of the workspace directory
        try {
            workspaceLocation = Platform.getLocation().toPortableString();
            if (!workspaceLocation.endsWith("/")) {
                workspaceLocation = workspaceLocation + "/";
            }
        } catch (final IllegalStateException ise) {
            LOG.warn("Workspace location could not be found. Using working directory '.'");
            workspaceLocation = "./";
        }

        final File file = new File(workspaceLocation + "errorState.ser");
        try {
            errorFile = new ObjectOutputStream(new FileOutputStream(file));
            errorFile.writeObject(state);
        } catch (final FileNotFoundException fnfe) {
            LOG.error("[*** FileNotFoundException ***]: {}", fnfe.getMessage());
        } catch (final IOException ioe) {
            LOG.error("[*** IOException ***]: {}", ioe.getMessage());
        } finally {
            if (errorFile != null) {
                try{errorFile.close();}catch(final Exception e) { /*Can be ignored */ }
                errorFile = null;
            }
        }
    }

    private void deleteErrorFile() {
        
        String workspaceLocation = null;
        
        // Retrieve the location of the workspace directory
        try {
            workspaceLocation = Platform.getLocation().toPortableString();
            if (!workspaceLocation.endsWith("/")) {
                workspaceLocation = workspaceLocation + "/";
            }
        } catch (final IllegalStateException ise) {
            LOG.warn("Workspace location could not be found. Using working directory '.'");
            workspaceLocation = "./";
        }
        
        File file = new File(workspaceLocation + "errorState.ser");
        if (file.exists()) {
            file.delete();
        }
    }
    
    @Override
    public final void bindService(final ISessionService service) {

        final IPreferencesService prefs = Platform.getPreferencesService();
        final String xmppUser = prefs.getString(Jms2OraActivator.PLUGIN_ID,
                PreferenceConstants.XMPP_REMOTE_USER_NAME, "anonymous", null);
        final String xmppPassword = prefs.getString(Jms2OraActivator.PLUGIN_ID,
                PreferenceConstants.XMPP_REMOTE_PASSWORD, "anonymous", null);
        final String xmppServer = prefs.getString(Jms2OraActivator.PLUGIN_ID,
                PreferenceConstants.XMPP_SERVER, "krynfs.desy.de", null);

        try {
            LOG.info("ISessionService: {}", service.toString());
            service.connect(xmppUser, xmppPassword, xmppServer);
            xmppSession = service;
        } catch (final Exception e) {
            LOG.error("XMPP login is NOT possible: {}", e.getMessage());
            xmppSession = null;
        }
    }

    @Override
    public void unbindService(final ISessionService service) {
        // Nothing to do here
    }
}
