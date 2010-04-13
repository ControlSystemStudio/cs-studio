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

import static org.csstudio.utility.ldap.LdapUtils.ECON_FIELD_NAME;
import static org.csstudio.utility.ldap.LdapUtils.EPICS_CTRL_FIELD_VALUE;
import static org.csstudio.utility.ldap.LdapUtils.FIELD_ASSIGNMENT;
import static org.csstudio.utility.ldap.LdapUtils.OU_FIELD_NAME;
import static org.csstudio.utility.ldap.LdapUtils.any;
import static org.csstudio.utility.ldapUpdater.preferences.LdapUpdaterPreferenceKey.IOC_DBL_DUMP_PATH;
import static org.csstudio.utility.ldapUpdater.preferences.LdapUpdaterPreferences.getValueFromPreferences;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.naming.directory.SearchControls;

import org.apache.log4j.Logger;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.utility.ldap.LdapUtils;
import org.csstudio.utility.ldap.model.IOC;
import org.csstudio.utility.ldap.model.LdapContentModel;
import org.csstudio.utility.ldap.reader.LdapSearchResult;
import org.csstudio.utility.ldapUpdater.files.HistoryFileAccess;
import org.csstudio.utility.ldapUpdater.files.HistoryFileContentModel;
import org.csstudio.utility.ldapUpdater.files.IOCFilesDirTree;
import org.csstudio.utility.ldapUpdater.mail.NotificationMail;
import org.csstudio.utility.ldapUpdater.mail.NotificationType;

import service.LdapService;
import service.impl.LdapServiceImpl;

/**
 * Updates the IOC information in the LDAP directory.
 *
 * @author valett
 * @author $Author$
 * @version $Revision$
 * @since 17.04.2008
 */
public final class LdapUpdater {

    public static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private static final String UPDATE_ACTION_NAME = "LDAP Update Action.";
    private static final String TIDYUP_ACTION_NAME = "LDAP Tidy Up Action.";

    private static LdapUpdater INSTANCE;

    /**
     * Factory method for creating a singleton instance.
     * @return the singleton instance of this class
     */
    @Nonnull
    public static LdapUpdater getInstance() {
        synchronized (LdapUpdater.class) {
            if ( INSTANCE == null) {
                INSTANCE = new LdapUpdater();
            }
        }
        return INSTANCE;
    }

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


    private final Logger _log = CentralLogger.getInstance().getLogger(this);

    private final LdapService _service = LdapServiceImpl.getInstance();

    private volatile boolean _busy = false;


    /**
     * Don't instantiate with constructor.
     */
    private LdapUpdater() {
        // empty
    }

    /**
     * Returns true if the updater is busy.
     * @return true if busy
     */
    public boolean isBusy() {
        return _busy;
    }

    private void logFooter(@Nonnull final String actionName, final long startTime) {
        final long endTime = System.currentTimeMillis();
        final long deltaTime = endTime - startTime;
        final String now = convertMillisToDateTimeString(endTime, DATETIME_FORMAT);

        final StringBuilder builder = new StringBuilder();
        builder.append(actionName).append(" ends at").append(now).append("  (").append(endTime).append(")\n")
        .append("time used : ").append(deltaTime/1000.).append("s\n")
        .append("End.\n")
        .append("-------------------------------------------------------------------\n");
        _log.info( builder.toString() );
    }

    private long logHeader(@Nonnull final String action) {
        final long startTime = System.currentTimeMillis();
        final String now = convertMillisToDateTimeString(startTime, DATETIME_FORMAT);

        final StringBuilder strBuilder = new StringBuilder();
        strBuilder.append("\n-------------------------------------------------------------------\n" )
        .append(action)
        .append(" start at ").append(now).append("  ( ")
        .append(startTime).append(" )");
        _log.info(strBuilder.toString() );
        return startTime;
    }


    /**
     * Scans the IOC file directory on /applic and the contents of the IOC files.
     * Removes any record from LDAP that is not found in any of the IOC files.
     */
    public void tidyUpLdapFromIOCFiles() {
        if ( _busy ) {
            return;
        }
        _busy = true;

        final long startTime = logHeader(TIDYUP_ACTION_NAME);

        try {
            final LdapSearchResult result =
                _service.retrieveSearchResult(OU_FIELD_NAME + FIELD_ASSIGNMENT + EPICS_CTRL_FIELD_VALUE,
                                              any(ECON_FIELD_NAME),
                                              SearchControls.ONELEVEL_SCOPE);

            final Map<String, IOC> iocMapFromFS = IOCFilesDirTree.findIOCFiles(getValueFromPreferences(IOC_DBL_DUMP_PATH), 1);

            LdapAccess.tidyUpLDAPFromIOCList(new LdapContentModel(result), iocMapFromFS);

        } finally {
            _busy = false;
        }
        logFooter(TIDYUP_ACTION_NAME, startTime);
    }

    /**
     * Scans the IOC files on /applic and checks whether contained records are already listed in LDAP.
     * If not so, these entries are added to LDAP.
     */
    public void updateLdapFromIOCFiles() {

        if ( _busy ) {
            return;
        }
        _busy = true;

        final long startTime = logHeader(UPDATE_ACTION_NAME);

        final HistoryFileAccess histFileReader = new HistoryFileAccess();
        final HistoryFileContentModel historyFileModel = histFileReader.readFile(); /* liest das history file */

        try {
            final LdapSearchResult searchResult =
                _service.retrieveSearchResult(LdapUtils.createLdapQuery(OU_FIELD_NAME, EPICS_CTRL_FIELD_VALUE),
                                              any(ECON_FIELD_NAME),
                                              SearchControls.SUBTREE_SCOPE);
            final LdapContentModel model = new LdapContentModel(searchResult);

            validateHistoryFileEntriesVsLDAPEntries(model, historyFileModel);

            final Map<String, IOC> iocMap = IOCFilesDirTree.findIOCFiles(getValueFromPreferences(IOC_DBL_DUMP_PATH), 1);

            LdapAccess.updateLDAPFromIOCList(model, iocMap, historyFileModel);

        } catch (final InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        } finally {
            _busy = false;
        }
        logFooter(UPDATE_ACTION_NAME, startTime);
    }


    private void validateHistoryFileEntriesVsLDAPEntries(@Nonnull final LdapContentModel ldapContentModel,
                                                         @Nonnull final HistoryFileContentModel historyFileModel) {

        Set<String> iocsFromLDAP = ldapContentModel.getIOCNames();
        final Set<String> iocsFromHistFile = historyFileModel.getIOCNames();

        iocsFromLDAP.removeAll(iocsFromHistFile);
        for (final String ioc : iocsFromLDAP) {
            _log.warn("IOC " + ioc + " from LDAP is not present in history file!");
            NotificationMail.sendMail(NotificationType.UNKNOWN_IOCS_IN_LDAP,
                                      ldapContentModel.getIOC(ioc).getResponsible(),
                                      ":\n\n" + ioc);
        }


        iocsFromLDAP = ldapContentModel.getIOCNames();
        iocsFromHistFile.removeAll(iocsFromLDAP);
        for (final String ioc : iocsFromHistFile) {
            _log.warn("IOC " + ioc + " found in history file is not present in LDAP!");
        }
    }

}


