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
 */
package org.csstudio.utility.ldapUpdater.mail;

import static org.csstudio.utility.ldapUpdater.preferences.LdapUpdaterPreference.IOC_DBL_DUMP_PATH;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;

import org.csstudio.domain.desy.net.IpAddress;
import org.csstudio.email.EMailSender;
import org.csstudio.email.EmailUtils;
import org.csstudio.platform.util.StringUtil;
import org.csstudio.utility.ldap.treeconfiguration.LdapEpicsControlsConfiguration;
import org.csstudio.utility.ldap.treeconfiguration.LdapEpicsControlsFieldsAndAttributes;
import org.csstudio.utility.treemodel.INodeComponent;

/**
 * Encapsulates LDAP Updater specific mail functionality.
 *
 * @author bknerr 24.03.2010
 */
public final class NotificationMailer {
    public static final String DEFAULT_RESPONSIBLE_PERSON = "bastian.knerr@desy.de";

    private static final String HOST = "smtp.desy.de";
    private static final String FROM = "DontReply@LDAPUpdater";


    /**
     * Don't instantiate.
     */
    private NotificationMailer() {
        // Empty.
    }

    /**
     * Sends an email of a certain notification type to the email receiver contained in the receiverString.
     * These can be separated by any valid email separating symbol.
     *
     * @param type the notification type
     * @param receiverString a string containing email addresses
     * @param additionalBody additional email body text
     * @return true if all mails could be sent
     */
    private static boolean sendMail(@Nonnull final NotificationType type,
                                    @Nonnull final String receiverString,
                                    @Nullable final String additionalBody) {
        final Set<String> receivers = EmailUtils.extractEmailAddresses(receiverString);
        try {
            for (final String receiver : receivers) {
                sendSingleMail(type, receiver, additionalBody);
            }
        } catch (final IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    private static void sendSingleMail(@Nonnull final NotificationType type,
                                       @Nonnull final String receiver,
                                       @Nullable final String additionalBody) throws IOException {
        final EMailSender mailer = new EMailSender(HOST,
                                             FROM,
                                             receiver,
                                             type.getSubject());

        mailer.addText(type.getText() + (additionalBody != null ? additionalBody : ""));
        mailer.close();
    }


    public static void sendMissingIOCsNotificationMails(@Nonnull final Map<String, List<String>> missingIOCsPerPerson) {
        for (final Entry<String, List<String>> entry : missingIOCsPerPerson.entrySet()) {
            sendMail(NotificationType.UNKNOWN_IOCS_IN_LDAP,
                                      entry.getKey(),
                                      "\n(in directory " + IOC_DBL_DUMP_PATH.getValue() + ")" +
                                      "\n\n" + entry.getValue());
        }
    }

    public static void sendUnallowedCharsNotification(@Nonnull final INodeComponent<LdapEpicsControlsConfiguration> iocFromLDAP,
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
           sendMail(NotificationType.UNALLOWED_CHARS,
                                     person,
                                     "\nIn IOC " + iocName + ":\n\n" + forbiddenRecords.toString());
       }
   }

    public static void sendMissingFilesNotification(@Nonnull final String message) {
        sendMail(NotificationType.BOOT_DIR_FILE_MISMATCH,
                 DEFAULT_RESPONSIBLE_PERSON,
                 message);
    }

    public static void sendIpAddressNotUniqueNotification(@Nonnull final IpAddress ipAddress,
                                                          @Nonnull final INodeComponent<LdapEpicsControlsConfiguration> iocFromLdap) {
        sendMail(NotificationType.IP_ADDRESS_NOT_UNIQUE,
                 DEFAULT_RESPONSIBLE_PERSON,
                 "IP Address: " + ipAddress.toString() + "\n" +
                 "Formerly used by (now removed): " + iocFromLdap.getLdapName().toString());
    }
}
