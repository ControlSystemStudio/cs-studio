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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.utility.ldap.LdapUtils;
import org.csstudio.utility.ldap.model.IOC;
import org.csstudio.utility.ldap.model.LdapContentModel;
import org.csstudio.utility.ldap.reader.LdapSeachResultObserver;
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
public class LdapUpdater {

    public static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private static final String UPDATE_ACTION_NAME = "LDAP Update Action.";
    private static final String TIDYUP_ACTION_NAME = "LDAP Tidy Up Action.";

    private static LdapUpdater INSTANCE;

    /**
     * Factory method for creating a singleton instance.
     * @return the singleton instance of this class
     */
    public static LdapUpdater getInstance() {
        synchronized (LdapUpdater.class) {
            if ( INSTANCE == null) {
                INSTANCE = new LdapUpdater();
            }
        }
        return INSTANCE;
    }

    public static String convertMillisToDateTimeString(final long millis, final String datetimeFormat) {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        final DateFormat formatter = new SimpleDateFormat(datetimeFormat);
        final String now = formatter.format(calendar.getTime());
        return now;
    }


    private final Logger LOGGER = CentralLogger.getInstance().getLogger(this);

    private final LdapService _service = LdapServiceImpl.getInstance();

    private volatile boolean _busy = false;


    /**
     * Don't instantiate with constructor.
     */
    private LdapUpdater()
    {
        // empty
    }


    public boolean isBusy() {
        return _busy;
    }

    public void setBusy(final boolean busy) {
        _busy = busy;
    }

    private void logFooter(final String actionName, final long startTime) {
        final long endTime = System.currentTimeMillis();
        final long deltaTime = endTime - startTime;
        final String now = convertMillisToDateTimeString(endTime, DATETIME_FORMAT);

        final StringBuilder builder = new StringBuilder();
        builder.append(actionName).append(" ends at").append(now).append("  (").append(endTime).append(")\n")
        .append("time used : ").append(deltaTime/1000.).append("s\n")
        .append("End.\n")
        .append("-------------------------------------------------------------------\n");
        LOGGER.info( builder.toString() );
    }

    private long logHeader(final String action) {
        final long startTime = System.currentTimeMillis();
        final String now = convertMillisToDateTimeString(startTime, DATETIME_FORMAT);

        final StringBuilder strBuilder = new StringBuilder();
        strBuilder.append("\n-------------------------------------------------------------------\n" )
        .append(action)
        .append(" start at ").append(now).append("  ( ")
        .append(startTime).append(" )");
        LOGGER.info(strBuilder.toString() );
        return startTime;
    }


    /**
     * TODO (bknerr) : Docu
     */
    public void tidyUpLdapFromIOCFiles() {
        if ( _busy ) {
            return;
        }
        _busy = true;

        final long startTime = logHeader(TIDYUP_ACTION_NAME);

        final LdapSeachResultObserver ldapDataObserver = new LdapSeachResultObserver();
        try {
            _service.getEntries(ldapDataObserver,
                                OU_FIELD_NAME + FIELD_ASSIGNMENT + EPICS_CTRL_FIELD_VALUE,
                                any(ECON_FIELD_NAME));
            final Map<String, IOC> iocMapFromFS = IOCFilesDirTree.findIOCFiles(1);

            LdapAccess.tidyUpLDAPFromIOCList(ldapDataObserver.getLdapModel(), iocMapFromFS);

        } finally {
            _busy = false;
        }
        logFooter(TIDYUP_ACTION_NAME, startTime);
    }

    /**
     * TODO (bknerr) : Docu
     */
    public final void updateLdapFromIOCFiles() {

        if ( _busy ) {
            return;
        }
        _busy = true;

        final long startTime = logHeader(UPDATE_ACTION_NAME);

        final HistoryFileAccess histFileReader = new HistoryFileAccess();
        final HistoryFileContentModel historyFileModel = histFileReader.readFile(); /* liest das history file */

        final LdapSeachResultObserver ldapDataObserver = new LdapSeachResultObserver();
        try {
            final LdapContentModel model = _service.getEntries(ldapDataObserver,
                                LdapUtils.createLdapQuery(OU_FIELD_NAME, EPICS_CTRL_FIELD_VALUE),
                                any(ECON_FIELD_NAME));

            validateHistoryFileEntriesVsLDAPEntries(model, historyFileModel);

            final Map<String, IOC> iocMap = IOCFilesDirTree.findIOCFiles(1);

            LdapAccess.updateLDAPFromIOCList(ldapDataObserver, iocMap, historyFileModel);

        } catch (final InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        } finally {
            _busy = false;
        }
        logFooter(UPDATE_ACTION_NAME, startTime);
    }


    private void validateHistoryFileEntriesVsLDAPEntries(final LdapContentModel ldapContentModel,
                                                         final HistoryFileContentModel historyFileModel) {

        Set<String> iocsFromLDAP = ldapContentModel.getIOCNames();
        final Set<String> iocsFromHistFile = historyFileModel.getIOCNames();

        iocsFromLDAP.removeAll(iocsFromHistFile);
        for (final String ioc : iocsFromLDAP) {
            LOGGER.warn("IOC " + ioc + " from LDAP is not present in history file!");
            NotificationMail.sendMail(NotificationType.UNKNOWN_IOCS_IN_LDAP,
                                      ldapContentModel.getIOC(ioc).getResponsible(),
                                      ":\n\n" + ioc);
        }


        iocsFromLDAP = ldapContentModel.getIOCNames();
        iocsFromHistFile.removeAll(iocsFromLDAP);
        for (final String ioc : iocsFromHistFile) {
            LOGGER.warn("IOC " + ioc + " found in history file is not present in LDAP!");
        }
    }

}


