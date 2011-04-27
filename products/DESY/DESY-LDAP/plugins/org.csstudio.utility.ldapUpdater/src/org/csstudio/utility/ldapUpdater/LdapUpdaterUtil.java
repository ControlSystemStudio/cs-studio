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

import static org.csstudio.utility.ldapUpdater.preferences.LdapUpdaterPreference.IOC_DBL_DUMP_PATH;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.GuardedBy;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;

import org.apache.log4j.Logger;
import org.csstudio.domain.desy.time.TimeInstant;
import org.csstudio.domain.desy.time.TimeInstant.TimeInstantBuilder;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.util.StringUtil;
import org.csstudio.utility.ldap.treeconfiguration.LdapEpicsControlsConfiguration;
import org.csstudio.utility.ldap.treeconfiguration.LdapEpicsControlsFieldsAndAttributes;
import org.csstudio.utility.ldapUpdater.mail.NotificationMail;
import org.csstudio.utility.ldapUpdater.mail.NotificationType;
import org.csstudio.utility.treemodel.ISubtreeNodeComponent;


/**
 * Updates the IOC information in the LDAP directory.
 *
 * @author valett
 * @author $Author$
 * @version $Revision$
 * @since 17.04.2008
 */
public enum LdapUpdaterUtil {
    INSTANCE;
    
    public static final String DEFAULT_RESPONSIBLE_PERSON = "bastian.knerr@desy.de";

    private static final Logger LOG = CentralLogger.getInstance().getLogger(LdapUpdaterUtil.class);

    @GuardedBy("this")
    private boolean _busy;
    
    /**
     * Don't instantiate with constructor.
     */
    private LdapUpdaterUtil() {
        setBusy(false);
    }

    /**
     * Returns true if the updater is busy.
     * @return true if busy
     */
    public synchronized boolean isBusy() {
        return _busy;
    }

    public synchronized void setBusy(final boolean busy) {
        _busy = busy;
    }

    public void logFooter(@Nonnull final String actionName, 
                          @Nonnull final TimeInstant startTime) {
        final TimeInstant endTime = TimeInstantBuilder.fromNow();
        final long deltaTime = TimeInstant.deltaInMillis(startTime, endTime);

        LOG.info( actionName + " ends at " + endTime.formatted() + ". " +
                  "Time used : " + deltaTime/1000. + "s\n.End.\n" +
                  "-------------------------------------------------------------------\n" );
    }

    @Nonnull
    public TimeInstant logHeader(@Nonnull final String action) {
        TimeInstant startTime = TimeInstantBuilder.fromNow();
        LOG.info("\n-------------------------------------------------------------------\n" +
                 action + " starts at " + startTime.formatted() + ".");
        return startTime;
    }


    public void sendNotificationMails(@Nonnull final Map<String, List<String>> missingIOCsPerPerson) {
        for (final Entry<String, List<String>> entry : missingIOCsPerPerson.entrySet()) {
            NotificationMail.sendMail(NotificationType.UNKNOWN_IOCS_IN_LDAP,
                                      entry.getKey(),
                                      "\n(in directory " + IOC_DBL_DUMP_PATH.getValue() + ")" +
                                      "\n\n" + entry.getValue());
        }
    }
    
    public static void sendUnallowedCharsNotification(@Nonnull final ISubtreeNodeComponent<LdapEpicsControlsConfiguration> iocFromLDAP,
                                                       @Nonnull final String iocName,
                                                       @Nonnull final StringBuilder forbiddenRecords) throws NamingException {
        if (forbiddenRecords.length() > 0) {
            final Attribute attr = iocFromLDAP.getAttribute(LdapEpicsControlsFieldsAndAttributes.ATTR_FIELD_RESPONSIBLE_PERSON);
            String person;
            if (attr != null && !StringUtil.hasLength((String) attr.get())) {
                person = (String) attr.get();
            } else {
                person = DEFAULT_RESPONSIBLE_PERSON;
            }
            NotificationMail.sendMail(NotificationType.UNALLOWED_CHARS,
                                      person,
                                      "\nIn IOC " + iocName + ":\n\n" + forbiddenRecords.toString());
        }
    }
}


