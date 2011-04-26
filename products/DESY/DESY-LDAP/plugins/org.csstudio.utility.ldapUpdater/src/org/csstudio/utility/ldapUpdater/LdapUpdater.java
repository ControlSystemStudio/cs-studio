/*
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron,
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
/*
 * $Id$
 */

/*
 * todo : functionallity to delete one IOC's ldap data, but NOT the header info
 * should be startable via xmpp, prompt asking for IOCname.
 */
package org.csstudio.utility.ldapUpdater;

import static org.csstudio.utility.ldap.service.util.LdapUtils.any;
import static org.csstudio.utility.ldap.treeconfiguration.LdapEpicsControlsConfiguration.IOC;
import static org.csstudio.utility.ldap.treeconfiguration.LdapEpicsControlsConfiguration.UNIT;
import static org.csstudio.utility.ldapUpdater.preferences.LdapUpdaterPreference.IOC_DBL_DUMP_PATH;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TimeZone;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.GuardedBy;
import javax.naming.InvalidNameException;
import javax.naming.NamingException;
import javax.naming.ServiceUnavailableException;
import javax.naming.directory.Attribute;
import javax.naming.directory.SearchControls;
import javax.naming.ldap.LdapName;

import org.apache.log4j.Logger;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.utility.ldap.model.IOC;
import org.csstudio.utility.ldap.service.ILdapContentModelBuilder;
import org.csstudio.utility.ldap.service.ILdapSearchResult;
import org.csstudio.utility.ldap.service.ILdapService;
import org.csstudio.utility.ldap.service.util.LdapUtils;
import org.csstudio.utility.ldap.treeconfiguration.LdapEpicsControlsConfiguration;
import org.csstudio.utility.ldap.treeconfiguration.LdapEpicsControlsFieldsAndAttributes;
import org.csstudio.utility.ldapUpdater.files.HistoryFileAccess;
import org.csstudio.utility.ldapUpdater.files.HistoryFileContentModel;
import org.csstudio.utility.ldapUpdater.files.IOCFilesDirTree;
import org.csstudio.utility.ldapUpdater.mail.NotificationMail;
import org.csstudio.utility.ldapUpdater.mail.NotificationType;
import org.csstudio.utility.treemodel.ContentModel;
import org.csstudio.utility.treemodel.CreateContentModelException;
import org.csstudio.utility.treemodel.ISubtreeNodeComponent;


/**
 * Updates the IOC information in the LDAP directory.
 *
 * @author valett
 * @author $Author$
 * @version $Revision$
 * @since 17.04.2008
 */
public enum LdapUpdater {

    INSTANCE;

    public static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static final String DEFAULT_RESPONSIBLE_PERSON = "bastian.knerr@desy.de";


    /**
     * Converts milli seconds to a formatted date time string.
     * @param millis the millis
     * @param datetimeFormat the format string
     * @return a formatted string representation
     */
    @Nonnull
    public static String convertMillisToDateTimeString(final long millis, @Nonnull final String datetimeFormat) {
        final TimeZone timeZone = TimeZone.getTimeZone("ECT");
        final Calendar cal = new GregorianCalendar(timeZone);
        cal.setTimeInMillis(millis);
        final DateFormat formatter = new SimpleDateFormat(datetimeFormat);
        final String now = formatter.format(cal.getTime());
        return now;
    }

    private static final String UPDATE_ACTION_NAME = "LDAP Update Action";
    private static final String TIDYUP_ACTION_NAME = "LDAP Tidy Up Action";


    private static final Logger LOG = CentralLogger.getInstance().getLogger(LdapUpdater.class);

    @GuardedBy("this")
    private boolean _busy;

    /**
     * Don't instantiate with constructor.
     */
    private LdapUpdater() {
        setBusy(false);
    }

    /**
     * Returns true if the updater is busy.
     * @return true if busy
     */
    public synchronized boolean isBusy() {
        return _busy;
    }

    private synchronized void setBusy(final boolean busy) {
        _busy = busy;
    }

    private void logFooter(@Nonnull final String actionName, final long startTime) {
        final long endTime = System.currentTimeMillis();
        final long deltaTime = endTime - startTime;
        final String now = convertMillisToDateTimeString(endTime, DATETIME_FORMAT);

        final StringBuilder builder = new StringBuilder();
        builder.append(actionName).append(" ends at ").append(now).append("  (").append(endTime).append(")\n")
        .append("Time used : ").append(deltaTime/1000.).append("s\n")
        .append("End.\n")
        .append("-------------------------------------------------------------------\n");
        LOG.info( builder.toString() );
    }

    private long logHeader(@Nonnull final String action) {
        final long startTime = System.currentTimeMillis();
        final String now = convertMillisToDateTimeString(startTime, DATETIME_FORMAT);

        final StringBuilder strBuilder = new StringBuilder();
        strBuilder.append("\n-------------------------------------------------------------------\n" )
        .append(action)
        .append(" starts at ").append(now).append("  ( ")
        .append(startTime).append(" )");
        LOG.info(strBuilder.toString() );
        return startTime;
    }



    /**
     * Scans the IOC file directory on /applic and the contents of the IOC files.
     * Removes any record from LDAP that is not found in any of the IOC files.
     * @throws InterruptedException
     * @throws InvalidNameException
     * @throws CreateContentModelException
     * @throws ServiceUnavailableException
     */
    public void tidyUpLdapFromIOCFiles() throws InvalidNameException, InterruptedException, CreateContentModelException, ServiceUnavailableException {
        if ( isBusy() ) {
            return;
        }
        setBusy(true);

        final long startTime = logHeader(TIDYUP_ACTION_NAME);

        try {
            final ILdapService service = LdapUpdaterActivator.getDefault().getLdapService();
            if (service == null) {
                LOG.warn("NO LDAP service available. Tidying cancelled.");
                return;
            }
            final LdapName query = LdapUtils.createLdapName(UNIT.getNodeTypeName(), UNIT.getUnitTypeValue());
            final String filter = any(IOC.getNodeTypeName());
            final ILdapSearchResult result =
                service.retrieveSearchResultSynchronously(query,
                                                          filter,
                                                          SearchControls.ONELEVEL_SCOPE);
            if (result == null) {
                LOG.warn("LDAP search result is empty. No IOCs found for " + query + " and filter " + filter);
                return;
            }
            final File dumpPath = IOC_DBL_DUMP_PATH.getValue();
            
            final Map<String, IOC> iocMapFromFS = IOCFilesDirTree.findIOCFiles(dumpPath, 1);
            final ILdapContentModelBuilder<LdapEpicsControlsConfiguration> builder = 
                service.getLdapContentModelBuilder(LdapEpicsControlsConfiguration.VIRTUAL_ROOT, result);
            builder.build();

            final ContentModel<LdapEpicsControlsConfiguration> model = builder.getModel();
            if (model == null) {
                LOG.warn("Content model constructed from LDAP query is null.");
                return;
            }
            LdapAccess.tidyUpLDAPFromIOCList(model, iocMapFromFS);
        } finally {
            setBusy(false);
            logFooter(TIDYUP_ACTION_NAME, startTime);
        }
    }

    /**
     * Scans the IOC files on /applic and checks whether contained records are already listed in LDAP.
     * If not so, these entries are added to LDAP.
     */
    public void updateLdapFromIOCFiles() {

        if ( isBusy() ) {
            return;
        }
        setBusy(true);

        final long startTime = logHeader(UPDATE_ACTION_NAME);

        final HistoryFileAccess histFileReader = new HistoryFileAccess();
        final HistoryFileContentModel historyFileModel = histFileReader.readFile(); /* liest das history file */

        try {
            final ILdapService service = LdapUpdaterActivator.getDefault().getLdapService();
            if (service == null) {
                LOG.error("No LDAP service available. Updating canceled.");
                return;
            }

            final LdapName query = LdapUtils.createLdapName(UNIT.getNodeTypeName(), UNIT.getUnitTypeValue());
            final ILdapSearchResult searchResult =
                service.retrieveSearchResultSynchronously(query,
                                                          any(IOC.getNodeTypeName()),
                                                          SearchControls.SUBTREE_SCOPE);
            if (searchResult == null) {
                LOG.info("No LDAP search result for query " + query + " and filter " + any(IOC.getNodeTypeName()));
                return;
            }

            createModelAndUpdateLdap(historyFileModel, searchResult);

        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (final CreateContentModelException e) {
            LOG.error("Content model for search result (econ=*) could not be created. LDAP update cancelled.");

        } finally {
            setBusy(false);
            logFooter(UPDATE_ACTION_NAME, startTime);
        }
    }

    private void createModelAndUpdateLdap(@Nonnull final HistoryFileContentModel historyFileModel,
                                          @Nonnull final ILdapSearchResult searchResult)
    throws CreateContentModelException, InterruptedException {

        final ILdapService service = LdapUpdaterActivator.getDefault().getLdapService();
        if (service == null) {
            LOG.error("No LDAP service available.");
            return;
        }

        final ILdapContentModelBuilder<LdapEpicsControlsConfiguration> builder =
            service.getLdapContentModelBuilder(LdapEpicsControlsConfiguration.VIRTUAL_ROOT, searchResult);
        builder.build();
        final ContentModel<LdapEpicsControlsConfiguration> model = builder.getModel();

        if (model != null) {
            validateHistoryFileEntriesVsLDAPEntries(model, historyFileModel);

            final Map<String, IOC> iocMap = IOCFilesDirTree.findIOCFiles(IOC_DBL_DUMP_PATH.getValue(), 1);
            LdapAccess.updateLDAPFromIOCList(model, iocMap, historyFileModel);
        }
    }


    private void validateHistoryFileEntriesVsLDAPEntries(@Nonnull final ContentModel<LdapEpicsControlsConfiguration> ldapModel,
                                                         @Nonnull final HistoryFileContentModel historyFileModel) {

        Set<String> iocsFromLDAP = ldapModel.getSimpleNames(LdapEpicsControlsConfiguration.IOC);

        final Set<String> iocsFromHistFile = historyFileModel.getIOCNameKeys();

        iocsFromLDAP.removeAll(iocsFromHistFile);

        final Map<String, List<String>> missingIOCsPerPerson = new HashMap<String, List<String>>();

        for (final String iocNameKey : iocsFromLDAP) {
            LOG.warn("IOC " + iocNameKey + " from LDAP is not present in history file!");

            findMissingIocs(ldapModel, missingIOCsPerPerson, iocNameKey);
        }

        sendNotificationMails(missingIOCsPerPerson);


        iocsFromLDAP = ldapModel.getSimpleNames(LdapEpicsControlsConfiguration.IOC);
        iocsFromHistFile.removeAll(iocsFromLDAP);
        for (final String ioc : iocsFromHistFile) {
            LOG.warn("IOC " + ioc + " found in history file is not present in LDAP!");
        }
    }

    @CheckForNull
    private Map<String, List<String>> findMissingIocs(@Nonnull final ContentModel<LdapEpicsControlsConfiguration> ldapModel,
                                                      @Nonnull final Map<String, List<String>> missingIOCsPerPerson,
                                                      @Nonnull final String iocNameKey) {
        final ISubtreeNodeComponent<LdapEpicsControlsConfiguration> ioc = ldapModel.getByTypeAndSimpleName(LdapEpicsControlsConfiguration.IOC, iocNameKey);
        if (ioc != null) {
            final Attribute personAttr = ioc.getAttribute(LdapEpicsControlsFieldsAndAttributes.ATTR_FIELD_RESPONSIBLE_PERSON);
            String person = DEFAULT_RESPONSIBLE_PERSON;
            try {
                if (personAttr != null && personAttr.get() != null) {
                    person = (String) personAttr.get();
                }
                if (!missingIOCsPerPerson.containsKey(person)) {
                    missingIOCsPerPerson.put(person, new ArrayList<String>());
                }
                missingIOCsPerPerson.get(person).add(ioc.getName());
            } catch (final NamingException e) {
                LOG.error("Attribute for " + LdapEpicsControlsFieldsAndAttributes.ATTR_FIELD_RESPONSIBLE_PERSON +
                          " in IOC " + ioc.getName() + "could not be retrieved.");
            }
        }
        return missingIOCsPerPerson;
    }

    private void sendNotificationMails(@Nonnull final Map<String, List<String>> missingIOCsPerPerson) {
        for (final Entry<String, List<String>> entry : missingIOCsPerPerson.entrySet()) {
            NotificationMail.sendMail(NotificationType.UNKNOWN_IOCS_IN_LDAP,
                                      entry.getKey(),
                                      "\n(in directory " + IOC_DBL_DUMP_PATH.getValue() + ")" +
                                      "\n\n" + entry.getValue());
        }
    }

}


