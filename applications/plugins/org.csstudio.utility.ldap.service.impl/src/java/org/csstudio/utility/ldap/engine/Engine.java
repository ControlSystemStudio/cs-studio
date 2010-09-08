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
 */
package org.csstudio.utility.ldap.engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.naming.InvalidNameException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.LdapName;

import org.apache.log4j.Logger;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.statistic.Collector;
import org.csstudio.utility.ldap.LdapActivator;
import org.csstudio.utility.ldap.engine.LdapReferences.Entry;
import org.csstudio.utility.ldap.model.LdapEpicsControlsConfiguration;
import org.csstudio.utility.ldap.preference.LdapPreference;
import org.csstudio.utility.ldap.service.ILdapSearchResult;
import org.csstudio.utility.ldap.service.ILdapService;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

/**
 * TODO (bknerr) : - Does it have to be a job?
 *                 - if not so, refactor wrong singleton pattern
 *                 - use a proper synchronized producer-consumer pattern with blocking queues and completion service
 *                 - refactor all ldap access methods to ldap service structure
 *                 - refactor all hard coded strings
 *                 - extract types
 *
 * @author bknerr
 * @author $Author$
 * @version $Revision$
 * @since 12.04.2010
 */
public final class Engine extends Job {

    private static final Logger LOG = CentralLogger.getInstance().getLogger(Engine.class);

    private static int LDAP_MAX_BUFFER_SIZE = 10000; // 1000 too small!!

    private volatile boolean _running = true;
    private int _reStartSendDiff = 0;

    /**
     * An Objectclass as return Object for the setAttribute and getAttribute
     * method.
     *
     * @author $Author$
     * @version $Revision$
     * @since 23.01.2008
     */
    private static class AttributeSet {
        private final SearchControls _ctrl;
        private String _path;
        private String _filter;

        /**
         * The default Constructor. Set default the Timelimit to 1000 ms.
         */
        public AttributeSet() {
            _ctrl = new SearchControls();
            _ctrl.setTimeLimit(1000);
        }

        /**
         * @param searchScope
         *            set the SearchScope of {@link SearchControls}.
         */
        public void setSearchScope(final int searchScope) {
            _ctrl.setSearchScope(searchScope);
        }

        /**
         * @param filter
         *            set the Filter for the Search.
         */
        public void setFilter(@Nonnull final String filter) {
            _filter = filter;
        }

        /**
         * @param path
         *            set the path for the Search.
         */
        public void setPath(@Nonnull final String path) {
            _path = path;
        }

        /**
         *
         * @return the SearchControls.
         */
        @Nonnull
        public SearchControls getSearchControls() {
            return _ctrl;
        }

        /**
         * @param path
         *            get the path for the Search.
         */
        @Nonnull
        public String getPath() {
            return _path;
        }

        /**
         * @param filter
         *            get the Filter for the Search.
         */
        @Nonnull
        public String getFilter() {
            return _filter;
        }

        /*
         * (non-Javadoc)
         *
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            String scope;
            switch (_ctrl.getSearchScope()) {
                case SearchControls.OBJECT_SCOPE:
                    scope = "OBJECT_SCOPE";
                    break;
                case SearchControls.ONELEVEL_SCOPE:
                    scope = "ONELEVEL_SCOPE";
                    break;
                case SearchControls.SUBTREE_SCOPE:
                    scope = "SUBTREE_SCOPE";
                    break;
                default:
                    scope = "Unknown scope"; // TODO (bknerr) : error? probably!

            }
            return String.format("Path: %s - Filter: %s - Scope: %s", _path, _filter, scope);
        }
    }

    /**
     * Contain all Attributes for Records.
     */
    public static enum ChannelAttribute {
        epicsCssType,
        epicsAlarmStatus,
        epicsAlarmAcknTimeStamp,
        epicsAlarmAckn,
        epicsDatabaseType,
        epicsAlarmSeverity,
        epicsAlarmTimeStamp,
        epicsRecordType,
        epicsAlarmHighUnAckn,
        epicsCssAlarmDisplay,
        epicsCssDisplay,
        epicsHelpGuidance,
        epicsHelpPage
    }

    private static Engine INSTANCE = null;

    private boolean _doWrite = false;

    private Collector _ldapReadTimeCollector = null;
    private Collector _ldapWriteTimeCollector = null;
    private Collector _ldapWriteRequests = null;
    private LdapReferences _ldapReferences = null;

    private Vector<WriteRequest> _writeVector = new Vector<WriteRequest>();

    private boolean _addVectorOK = true;

    /**
     * Constructor.
     * @param name the name
     */
    private Engine(@Nonnull final String name) {
        super(name);
        this._ldapReferences = new LdapReferences();
        /*
         * initialize statistic
         */
        _ldapWriteTimeCollector = new Collector();
        _ldapWriteTimeCollector.setApplication(name);
        _ldapWriteTimeCollector.setDescriptor("Time to write to LDAP server");
        _ldapWriteTimeCollector.setContinuousPrint(false);
        _ldapWriteTimeCollector.setContinuousPrintCount(1000.0);
        _ldapWriteTimeCollector.getAlarmHandler().setDeadband(5.0);
        _ldapWriteTimeCollector.getAlarmHandler().setHighAbsoluteLimit(500.0); // 500ms
        _ldapWriteTimeCollector.getAlarmHandler().setHighRelativeLimit(200.0); // 200%

        _ldapReadTimeCollector = new Collector();
        _ldapReadTimeCollector.setApplication(name);
        _ldapReadTimeCollector.setDescriptor("Time to find LDAP entries");
        _ldapReadTimeCollector.setContinuousPrint(false);
        _ldapReadTimeCollector.setContinuousPrintCount(1000.0);
        _ldapReadTimeCollector.getAlarmHandler().setDeadband(5.0);
        _ldapReadTimeCollector.getAlarmHandler().setHighAbsoluteLimit(500.0); // 500ms
        _ldapReadTimeCollector.getAlarmHandler().setHighRelativeLimit(200.0); // 200%

        _ldapWriteRequests = new Collector();
        _ldapWriteRequests.setApplication(name);
        _ldapWriteRequests.setDescriptor("LDAP Write Request Buffer Size");
        _ldapWriteRequests.setContinuousPrint(false);
        _ldapWriteRequests.setContinuousPrintCount(1000.0);
        _ldapWriteRequests.getAlarmHandler().setDeadband(5.0);
        _ldapWriteRequests.getAlarmHandler().setHighAbsoluteLimit(250.0); // number of entries in list
        _ldapWriteRequests.getAlarmHandler().setHighRelativeLimit(200.0); // 200%
    }

    /**
     * @param args
     */
    @Override
    protected IStatus run(@Nonnull final IProgressMonitor monitor) {
        Integer intSleepTimer = null;

        LOG.info("Ldap Engine started");

        // TODO:
        /*
         * create message ONCE retry forever if ctx == null BUT do NOT block
         * caller (calling singleton) submit ctx = new
         * LDAPConnector().getDirContext(); to 'background process'
         *
         */
        LOG.debug("Engine.run - start");

        while (isRunning() || _doWrite) {
            //
            // do the work actually prepared
            //
            if (_doWrite) {
                performLdapWrite();
            }
            /*
             * sleep before we check for work again
             */
            try {
                final String protocol = LdapPreference.SECURITY_PROTOCOL.getValue();

                if (protocol.trim().length() > 0) {
                    intSleepTimer = new Integer(protocol);
                } else {
                    intSleepTimer = 10; // default
                }
                Thread.sleep(intSleepTimer);
            } catch (final InterruptedException e) {
                return null;
            }
        }
        return Job.ASYNC_FINISH;
    }

    /**
     * @return get an instance of our singleton.
     */
    @Nonnull
    public static Engine getInstance() {
        synchronized (Engine.class) {
            if (INSTANCE == null) {
                INSTANCE = new Engine("LdapEngine");
                INSTANCE.setSystem(true);
                INSTANCE.schedule();
            }
        }
        return INSTANCE;
    }

    synchronized public void addLdapWriteRequest(final String attribute, final String channel, final String value) {
        // boolean addVectorOK = true;
        final WriteRequest writeRequest = new WriteRequest(attribute, channel, value);
        final int maxBuffersize = LDAP_MAX_BUFFER_SIZE;
        //
        // add request to vector
        //
        final int bufferSize = _writeVector.size();
        /*
         * statistic information
         */
        _ldapWriteRequests.setValue(bufferSize);

        // / LOG.info("Engine.addLdapWriteRequest actual buffer size:
        // " + bufferSize);
        /**
         *  Start the sending, after a Buffer overflow,
         *  when the buffer have minimum 10% free space
         */
        if (bufferSize > maxBuffersize-_reStartSendDiff) {
            if (_addVectorOK) {
                LOG.info("Engine.addLdapWriteRequest writeVector > " + maxBuffersize
                                   + " - cannot store more!");
                LOG.warn("writeVector > " + maxBuffersize + " - cannot store more!");
                _reStartSendDiff = (int)(LDAP_MAX_BUFFER_SIZE*0.1);
                _addVectorOK = false;
            }
        } else {
            if (!_addVectorOK) {
                LOG.info("Engine.addLdapWriteRequest writeVector - continue writing");
                LOG.warn("writeVector < " + maxBuffersize + " - resume writing");
                _reStartSendDiff = 0;
                _addVectorOK = true;
            }
            _writeVector.add(writeRequest);
        }
        //
        // always trigger writing
        //
        _doWrite = true;
    }

    @CheckForNull
    public DirContext getLdapDirContext() {
        final ILdapService service = LdapActivator.getDefault().getLdapService();
        if (service == null) {
            LOG.error("LDAP service unavailable.");
            return null;
        }
        return service.getContext();
    }

//    synchronized public DirContext reconnectDirContext() {
//        final ILdapService service = LdapActivator.getDefault().getLdapService();
//        if (service == null) {
//            LOG.error("LDAP service unavailable.");
//            return null;
//        }
//
//        service.reInitializeLdapConnection(null);
//
//        return service.getContext();
//    }

    /**
     * Get the Value of an record attribute.
     *
     * @param recordPath
     *            The Record-Name or the complete LDAP path of the record. Which
     *            the attribute change.
     * @param attribute
     *            The attribute from where get the value.
     * @return the value of the record attribute.
     * @throws NamingException
     */
    synchronized public String getAttribute(final String recordPath,
                                            final ChannelAttribute attribute) throws NamingException {
        if (recordPath == null || attribute == null) {
            return null;
        }

        final ILdapService service = LdapActivator.getDefault().getLdapService();
        if (service == null) {
            LOG.error("LDAP service unavailable.");
            return null;
        }

        final AttributeSet attributeSet = createAttributeSet(recordPath);

        final ILdapSearchResult result =
            service.retrieveSearchResultSynchronously(new LdapName(attributeSet.getPath()),
                                                      attributeSet.getFilter(),
                                                      attributeSet.getSearchControls().getSearchScope());
        if (result == null || result.getAnswerSet().isEmpty()) {
            return "NOT_FOUND";
        }
        final Set<SearchResult> answerSet = result.getAnswerSet();
        if (answerSet.size() > 1) {
            return "RESULT_NOT_UNIQUE";
        }

        final SearchResult entry = answerSet.iterator().next();

        final Attributes attrs = entry.getAttributes();
        if (attrs == null || attrs.get(attribute.name()) == null) {
            return "NONE";
        }

        final Attribute value = attrs.get(attribute.name());
        return value.get() == null ? "NONE" : (String) value.get();
    }

    /**
     * Set a Value of an record Attribute.
     *
     * @param recordPath
     *            The Record-Name or the complete LDAP path of the record. Which
     *            the attribute change.
     * @param attribute
     *            The attribute to set the Value.
     * @param value
     *            the value was set.
     * @throws InvalidNameException
     */
    synchronized public void setAttribute(final String recordPath,
                                          final ChannelAttribute attribute,
                                          final String value) throws InvalidNameException {
        assert recordPath != null && attribute != null && value != null : "The recordPath, attribute and/or value are NULL";

        final ILdapService service = LdapActivator.getDefault().getLdapService();
        if (service == null) {
            LOG.error("LDAP service unavailable.");
            return;
        }
        AttributeSet attributeSet = createAttributeSet(recordPath);

        final int searchScope = attributeSet.getSearchControls().getSearchScope();
        if (searchScope == SearchControls.SUBTREE_SCOPE) {
            final ILdapSearchResult result =
                service.retrieveSearchResultSynchronously(new LdapName(attributeSet.getPath()),
                                                          attributeSet.getFilter(),
                                                          searchScope);
            if (result != null && !result.getAnswerSet().isEmpty()) {
                final SearchResult row = result.getAnswerSet().iterator().next();
                attributeSet = createAttributeSet(row.getNameInNamespace()); // TODO (bknerr) : if more than one result???
            }
        }
        if (attributeSet != null && attributeSet.getFilter() != null && attributeSet.getPath() != null) {

            final String ldapChannelName = attributeSet.getFilter() + "," + attributeSet.getPath();
            final BasicAttribute ba = new BasicAttribute(attribute.name(), value);
            final List<ModificationItem> modItems = new ArrayList<ModificationItem>();
            modItems.add(new ModificationItem(DirContext.REPLACE_ATTRIBUTE, ba));

            final String channel = ldapChannelName.split("[=,]")[0];
            modifyAttributes(ldapChannelName, modItems, channel, new GregorianCalendar());
            // _ctx.modifyAttributes(ldapChannelName,modItemTemp);
        } else {
            LOG.warn("Set attribute faild. Record: " + recordPath + " with attriebute: "
                     + attribute.name() + " not found!");
        }
    }



    public int gregorianTimeDifference(final GregorianCalendar fromTime, final GregorianCalendar toTime) {
        //
        // calculate time difference
        //
        final Date fromDate = fromTime.getTime();
        final Date toDate = toTime.getTime();
        final long fromLong = fromDate.getTime();
        final long toLong = toDate.getTime();
        final long timeDifference = toLong - fromLong;


        final int intDiff = (int) timeDifference;
        return intDiff;
    }

    private void performLdapWrite() {
        final int maxNumberOfWritesProcessed = 200;
        List<ModificationItem> modItems = new ArrayList<ModificationItem>(maxNumberOfWritesProcessed);
        int i = 0;
        String currentChannel = null;

        while (_writeVector.size() > 0) {

            //
            // return first element and remove it from list
            //
            final WriteRequest writeReq = _writeVector.remove(0);

            //
            // prepare LDAP request for all entries matching the same channel
            //
            final String nextChannel = writeReq.getChannel();

            if (currentChannel == null) {
                // first time setting
                currentChannel = nextChannel;
            }
            if (!currentChannel.equals(nextChannel)) {
                // System.out.print("write: ");
                // TODO this hard coded string must be removed to the
                // preferences
                try {
                    changeValue(LdapEpicsControlsConfiguration.RECORD.getNodeTypeName(), currentChannel, modItems);
                } catch (final InvalidNameException e) {
                    LOG.error("Ldap name could not be composed properly.", e);
                }
                // LOG.info(" finished!!!");
                modItems = new ArrayList<ModificationItem>(maxNumberOfWritesProcessed);
                i = 0;
                //
                // define next channel name
                //
                currentChannel = nextChannel;
            }
            //
            // combine all items that are related to the same channel
            //
            final BasicAttribute ba = new BasicAttribute(writeReq.getAttribute(), writeReq.getValue());
            modItems.add(new ModificationItem(DirContext.REPLACE_ATTRIBUTE, ba));

            if (_writeVector.size() > 100 && _writeVector.size() % 100 == 0) {
                LOG.info("Engine.performLdapWrite buffer size: " + _writeVector.size());
            }
        }
        //
        // still something left to do?
        //
        if (i != 0) {
            //
            try {
                // LOG.info("Vector leer jetzt den Rest zum LDAP
                // Server schreiben");
                changeValue("eren", currentChannel, modItems);
            } catch (final Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                //
                // too bad it did not work
                _doWrite = true; // retry!
                return;
            }
        } else {
            // LOG.info("Vector leer - nothing left to do");
        }

        _doWrite = false;
    }

    /**
     * @param string
     * @param channel
     * @param modItemTemp
     * @throws InvalidNameException
     */
    private void changeValue(final String string,
                             final String channel,
                             final List<ModificationItem> modItems) throws InvalidNameException {

        List<String> namesInNamespace = Collections.emptyList();
        final GregorianCalendar startTime = new GregorianCalendar();

        //
        // is channel name already in ldearReference hash table?
        //
        if (_ldapReferences.hasEntry(channel)) {
            // if ( false) { // test case with no hash table
            //
            // take what's already stored
            //
            LOG.debug("Engine.changeValue : found entry for channel: " + channel);
            // LOG.info ("Engine.changeValue : found entry for
            // channel: " + channel);
            namesInNamespace = this._ldapReferences.getEntry(channel).getNamesInNamespace();
            for (final String ldapChannelName : namesInNamespace) {
                modifyAttributes(ldapChannelName, modItems, channel, startTime);
            }
        } else {
            //
            // search for channel in ldap server
            //
            final ILdapService service = LdapActivator.getDefault().getLdapService();
            if (service == null) {
                LOG.error("Engine.changeValue: LDAP service unavailable!");
                return;
            }
            final ILdapSearchResult result =
                service.retrieveSearchResultSynchronously(new LdapName(""),
                                                          string + "=" + channel,
                                                          SearchControls.SUBTREE_SCOPE);
            if (result == null || result.getAnswerSet().isEmpty()) {
                LOG.info("No results for LDAP search query.\n" + string + "=" + channel);
                return;
            }
            for (final SearchResult row : result.getAnswerSet()) {
                final String ldapChannelName = row.getNameInNamespace();
                namesInNamespace.add(ldapChannelName);
                modifyAttributes(ldapChannelName, modItems, channel, startTime);
            }
            //
            // save ldapEntries
            //
            if (namesInNamespace.size() > 0) {
                //
                // Write if really something found
                //
                _ldapReferences.newLdapEntry(channel, namesInNamespace);
                // LOG.info ("Engine.changeValue : add entry for
                // channel: " + channel);
            }
        }
        //
        // calculate time difference
        //
        // LOG.info ("Engine.changeValue : Time to write to
        // LDAP-total: " + gregorianTimeDifference ( startTime, new
        // GregorianCalendar()));
    }


    private void modifyAttributes(@Nonnull final String ldapChannelName,
                                  final List<ModificationItem> modItems,
                                  final String channel,
                                  final GregorianCalendar startTime) {
        //
        // TODO put 'endsWith' into preference page
        //
        String channelName = ldapChannelName;

        if (channelName.endsWith(",o=DESY,c=DE")) {
            channelName = channelName.substring(0, channelName.length() - 12);
        }
        try {
            channelName = channelName.replace("/", "\\/");

            final ILdapService service = LdapActivator.getDefault().getLdapService();
            if (service == null) {
                LOG.error("Engine.changeValue: LDAP service unavailable! Channel: " + channelName);
                return;
            }
            service.modifyAttributes(new LdapName(channelName), modItems.toArray(new ModificationItem[0]));

            _ldapWriteTimeCollector.setInfo(channel);
            _ldapWriteTimeCollector.setValue(gregorianTimeDifference(startTime,
                                                                     new GregorianCalendar())
                                                                     / modItems.size());
            // LOG.info ("Engine.changeValue : Time to write to LDAP:
            // (" + channel + ")" + gregorianTimeDifference ( startTime, new
            // GregorianCalendar()));
        } catch (final NamingException e) {
            LOG.warn("Engine.changeValue: Naming Exception in modifyAttributes! Channel: "
                     + channelName);
            LOG.info("Engine.changeValue: Naming Exceptionin modifyAttributes! Channel: " + channelName);
            // for (ModificationItem modificationItem : modItemTemp) {
            // LOG.info(" - ModificationItem is:
            // "+modificationItem.getAttribute().get().toString());
            // }
            final String errorCode = e.getExplanation();
            if (errorCode.contains("10")) {
                LOG.info("Error code 10: Please check LDAP replica! - replica may be out of synch - use: [start accepting updates] in SUN-LDAP Console");
                LOG.warn("Error code 10: Please check LDAP replica! - replica may be out of synch - use: [start accepting updates] in SUN-LDAP Console");
            }
            // e.printStackTrace();
            //
            // too bad it did not work
            _doWrite = false; // wait for next time
            return;
        } catch (final Exception e) {
            e.printStackTrace();
            //
            // too bad it did not work
            _doWrite = false; // wait for next time
            return;
        }
    }

    @CheckForNull
    private AttributeSet createAttributeSet(@Nonnull final String record) {
        final AttributeSet attributeSet = new AttributeSet();

        String nRecord = record;

        if (nRecord.length() > 0) {
            // Prüft ob der nRecord schon in der ldapReferences gespeichert ist.
            if (!nRecord.contains("ou=epicsControls") &&
                !nRecord.contains("econ=") &&
                _ldapReferences != null &&
                _ldapReferences.hasEntry(nRecord)) {// &&!nRecord.contains("ou=")){

                final Entry entry = _ldapReferences.getEntry(nRecord);
                final List<String> vector = entry.getNamesInNamespace();
                for (final String entryStr : vector) {
                    if (entryStr.contains("ou=EpicsControls")) {
                        nRecord = entryStr;
                    }
                }
                attributeSet.setSearchScope(SearchControls.ONELEVEL_SCOPE);
            } else if (nRecord.contains("ou=epicsControls") && nRecord.contains("econ=")) {
                // TODO: Der nRecord ist und wird noch nicht im ldapReferences
                // gecachet. Enthält aber den kompletten Pfad.
                attributeSet.setSearchScope(SearchControls.ONELEVEL_SCOPE);
            } else {
                // TODO: Der nRecord ist und wird noch nicht im ldapReferences
                // gecachet.
                attributeSet.setSearchScope(SearchControls.SUBTREE_SCOPE);
            }
            if (nRecord.endsWith(",o=DESY,c=DE")) {
                nRecord = nRecord.substring(0, nRecord.length() - 12);
            } else if (nRecord.endsWith(",o=DESY")) {
                nRecord = nRecord.substring(0, nRecord.length() - 7);
            }
            if (nRecord.contains(",")) {
                attributeSet.setFilter(nRecord.split(",")[0]);
                attributeSet.setPath(nRecord.substring(attributeSet.getFilter().length() + 1));
            } else {
                attributeSet.setPath("ou=epicsControls");
                attributeSet.setFilter("eren=" + nRecord);
            }
            return attributeSet;
        }
        return null;
    }


    private static class WriteRequest {
        private final String _attribute;
        private final String _channel;
        private final String _value;

        public WriteRequest(final String attribute, final String channel, final String value) {

            this._attribute = attribute;
            this._channel = channel;
            this._value = value;
        }

        public String getAttribute() {
            return this._attribute;
        }

        public String getChannel() {
            return this._channel;
        }

        public String getValue() {
            return this._value;
        }

    }

    public boolean isRunning() {
        return _running;
    }

    public void setRunning(final boolean running) {
        this._running = running;
    }

    public Vector<WriteRequest> getWriteVector() {
        return _writeVector;
    }

    public void setWriteVector(final Vector<WriteRequest> writeVector) {
        this._writeVector = writeVector;
    }
}
