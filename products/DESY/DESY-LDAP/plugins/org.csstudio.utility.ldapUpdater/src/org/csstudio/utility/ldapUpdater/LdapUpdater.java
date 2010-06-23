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

import static org.csstudio.utility.ldap.LdapFieldsAndAttributes.ECON_FIELD_NAME;
import static org.csstudio.utility.ldap.LdapFieldsAndAttributes.EPICS_CTRL_FIELD_VALUE;
import static org.csstudio.utility.ldap.LdapFieldsAndAttributes.OU_FIELD_NAME;
import static org.csstudio.utility.ldap.LdapUtils.any;
import static org.csstudio.utility.ldapUpdater.preferences.LdapUpdaterPreferenceKey.IOC_DBL_DUMP_PATH;
import static org.csstudio.utility.ldapUpdater.preferences.LdapUpdaterPreferences.getValueFromPreferences;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.annotation.Nonnull;
import javax.naming.InvalidNameException;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.SearchControls;

import org.apache.log4j.Logger;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.utility.ldap.LdapFieldsAndAttributes;
import org.csstudio.utility.ldap.LdapUtils;
import org.csstudio.utility.ldap.model.IOC;
import org.csstudio.utility.ldap.model.LdapEpicsControlsTreeConfiguration;
import org.csstudio.utility.ldap.model.builder.LdapContentModelBuilder;
import org.csstudio.utility.ldap.reader.LdapSearchResult;
import org.csstudio.utility.ldap.service.ILdapService;
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
        final Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        final DateFormat formatter = new SimpleDateFormat(datetimeFormat);
        final String now = formatter.format(calendar.getTime());
        return now;
    }

    private static final String UPDATE_ACTION_NAME = "LDAP Update Action";
    private static final String TIDYUP_ACTION_NAME = "LDAP Tidy Up Action";


    private static final Logger LOG = CentralLogger.getInstance().getLogger(LdapUpdater.class);

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
     */
    public void tidyUpLdapFromIOCFiles() throws InvalidNameException, InterruptedException, CreateContentModelException {
        if ( isBusy() ) {
            return;
        }
        setBusy(true);

        final long startTime = logHeader(TIDYUP_ACTION_NAME);

        try {
            final ILdapService service = Activator.getDefault().getLdapService();
            final LdapSearchResult result =
                service.retrieveSearchResultSynchronously(LdapUtils.createLdapQuery(OU_FIELD_NAME, EPICS_CTRL_FIELD_VALUE),
                                                          any(ECON_FIELD_NAME),
                                                          SearchControls.ONELEVEL_SCOPE);

            final String dumpPath = getValueFromPreferences(IOC_DBL_DUMP_PATH);
            if (dumpPath != null) {
                final Map<String, IOC> iocMapFromFS = IOCFilesDirTree.findIOCFiles(dumpPath, 1);
                final LdapContentModelBuilder<LdapEpicsControlsTreeConfiguration> builder =
                    new LdapContentModelBuilder<LdapEpicsControlsTreeConfiguration>(LdapEpicsControlsTreeConfiguration.ROOT, result);
                builder.build();

                LdapAccess.tidyUpLDAPFromIOCList(builder.getModel(),
                                                 iocMapFromFS);
            } else {
                LOG.warn("No preference for IOC dump path could be found. Tidy up cancelled!");
            }

        } finally {
            setBusy(false);
        }
        logFooter(TIDYUP_ACTION_NAME, startTime);
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
            final ILdapService service = Activator.getDefault().getLdapService();
            final LdapSearchResult searchResult =
                service.retrieveSearchResultSynchronously(LdapUtils.createLdapQuery(OU_FIELD_NAME, EPICS_CTRL_FIELD_VALUE),
                                                          any(ECON_FIELD_NAME),
                                                          SearchControls.SUBTREE_SCOPE);


            final LdapContentModelBuilder<LdapEpicsControlsTreeConfiguration> builder =
                new LdapContentModelBuilder<LdapEpicsControlsTreeConfiguration>(LdapEpicsControlsTreeConfiguration.ROOT, searchResult);
            builder.build();
            final ContentModel<LdapEpicsControlsTreeConfiguration> model = builder.getModel();

            validateHistoryFileEntriesVsLDAPEntries(model, historyFileModel);

            final String dumpPath = getValueFromPreferences(IOC_DBL_DUMP_PATH);
            if (dumpPath != null) {
                final Map<String, IOC> iocMap = IOCFilesDirTree.findIOCFiles(dumpPath, 1);
                LdapAccess.updateLDAPFromIOCList(model, iocMap, historyFileModel);
            } else {
                LOG.warn("No preference for IOC dump path could be found. Update cancelled!");
            }

        } catch (final InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        } catch (final Exception e) {
            e.printStackTrace();
        } finally {
            setBusy(false);
        }
        logFooter(UPDATE_ACTION_NAME, startTime);
    }


    private void validateHistoryFileEntriesVsLDAPEntries(@Nonnull final ContentModel<LdapEpicsControlsTreeConfiguration> ldapModel,
                                                         @Nonnull final HistoryFileContentModel historyFileModel) {

        Set<String> iocsFromLDAP = ldapModel.getSimpleNames(LdapEpicsControlsTreeConfiguration.IOC);

        final Set<String> iocsFromHistFile = historyFileModel.getIOCNameKeys();

        iocsFromLDAP.removeAll(iocsFromHistFile);

        final Map<String, List<String>> missingIOCsPerPerson = new HashMap<String, List<String>>();

        try {
            for (final String iocNameKey : iocsFromLDAP) {
                LOG.warn("IOC " + iocNameKey + " from LDAP is not present in history file!");

                final ISubtreeNodeComponent<LdapEpicsControlsTreeConfiguration> ioc = ldapModel.getByTypeAndSimpleName(LdapEpicsControlsTreeConfiguration.IOC, iocNameKey);
                if (ioc != null) {
                    final Attribute personAttr = ioc.getAttribute(LdapFieldsAndAttributes.ATTR_FIELD_RESPONSIBLE_PERSON);
                    String person = DEFAULT_RESPONSIBLE_PERSON;
                    if ((personAttr != null) && (personAttr.get() != null)) {
                        person = (String) personAttr.get();
                    }
                    if (!missingIOCsPerPerson.containsKey(person)) {
                        missingIOCsPerPerson.put(person, new ArrayList<String>());
                    }
                    missingIOCsPerPerson.get(person).add(ioc.getName());
                }
            }
        } catch (final NamingException e) {
            // TODO (bknerr)
            e.printStackTrace();
        }

        final String iocFilePath = getValueFromPreferences(IOC_DBL_DUMP_PATH);
        for (final Entry<String, List<String>> entry : missingIOCsPerPerson.entrySet()) {
            NotificationMail.sendMail(NotificationType.UNKNOWN_IOCS_IN_LDAP,
                                      entry.getKey(),
                                      "\n(in directory " + iocFilePath + ")" +
                                      "\n\n" + entry.getValue());
        }


        iocsFromLDAP = ldapModel.getSimpleNames(LdapEpicsControlsTreeConfiguration.IOC);
        iocsFromHistFile.removeAll(iocsFromLDAP);
        for (final String ioc : iocsFromHistFile) {
            LOG.warn("IOC " + ioc + " found in history file is not present in LDAP!");
        }
    }

}


