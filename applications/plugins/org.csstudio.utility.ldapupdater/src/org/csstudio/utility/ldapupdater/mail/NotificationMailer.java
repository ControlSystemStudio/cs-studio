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
package org.csstudio.utility.ldapupdater.mail;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;

import org.csstudio.domain.desy.net.HostAddress;
import org.csstudio.domain.desy.net.IpAddress;
import org.csstudio.email.EMailSender;
import org.csstudio.utility.ldap.treeconfiguration.LdapEpicsControlsConfiguration;
import org.csstudio.utility.ldap.treeconfiguration.LdapEpicsControlsFieldsAndAttributes;
import org.csstudio.utility.ldapupdater.LdapUpdaterActivator;
import org.csstudio.utility.ldapupdater.preferences.LdapUpdaterPreferencesService;
import org.csstudio.utility.treemodel.INodeComponent;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;

/**
 * Encapsulates LDAP Updater specific mail functionality.
 *
 * @author bknerr 24.03.2010
 */
public final class NotificationMailer {
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
                                    @Nonnull final HostAddress host,
                                    @Nullable final String additionalBody,
                                    @Nonnull final InternetAddress...receivers) {
        try {
            for (final InternetAddress receiver : receivers) {
                sendSingleMail(type, host, receiver, additionalBody);
            }
        } catch (final IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    private static void sendSingleMail(@Nonnull final NotificationType type,
                                       @Nonnull final HostAddress host,
                                       @Nonnull final InternetAddress receiver,
                                       @Nullable final String additionalBody) throws IOException {
        final EMailSender mailer = new EMailSender(host.getHostAddress(),
                                                   FROM,
                                                   receiver.getAddress(),
                                                   type.getSubject());

        mailer.addText(type.getText() + (additionalBody != null ? additionalBody : ""));
        mailer.close();
    }


    public static void sendMissingIOCsNotificationMails(@Nonnull final Map<InternetAddress, List<String>> missingIOCsPerPerson,
                                                        @Nonnull final HostAddress host) {
        final LdapUpdaterPreferencesService prefs = LdapUpdaterActivator.getDefault().getLdapUpdaterPreferencesService();
        for (final Entry<InternetAddress, List<String>> entry : missingIOCsPerPerson.entrySet()) {
            sendMail(NotificationType.UNKNOWN_IOCS_IN_LDAP,
                     host,
                     "\n(in directory " + prefs.getIocDblDumpPath() + ")\n\n" + entry.getValue(),
                     entry.getKey());
        }
    }

    public static void sendUnallowedCharsNotification(@Nonnull final INodeComponent<LdapEpicsControlsConfiguration> iocFromLDAP,
                                                      @Nonnull final HostAddress host,
                                                      @Nonnull final InternetAddress defaultReceiver,
                                                      @Nonnull final String iocName,
                                                      @Nonnull final StringBuilder forbiddenRecords) throws NamingException, AddressException {
       if (forbiddenRecords.length() > 0) {
           final Attribute attr = iocFromLDAP.getAttribute(LdapEpicsControlsFieldsAndAttributes.ATTR_FIELD_RESPONSIBLE_PERSON);
           InternetAddress person = null;
           if (attr != null) {
               final String strAttr = (String) attr.get();
               if (!Strings.isNullOrEmpty(strAttr)) {
                   person = new InternetAddress(strAttr);
               }
           }
           if (person == null) {
               person = defaultReceiver;
           }
           sendMail(NotificationType.UNALLOWED_CHARS,
                    host,
                    "\nIn IOC " + iocName + ":\n\n" + forbiddenRecords.toString(),
                    person);
       }
   }

    public static void sendMissingFilesNotification(@Nonnull final String message,
                                                    @Nonnull final HostAddress host,
                                                    @Nonnull final InternetAddress...receivers) {
        sendMail(NotificationType.BOOT_DIR_FILE_MISMATCH,
                 host,
                 message,
                 receivers);
    }

    public static void sendIpAddressNotUniqueNotification(@Nonnull final HostAddress host,
                                                          @Nonnull final IpAddress ipAddress,
                                                          @Nonnull final INodeComponent<LdapEpicsControlsConfiguration> iocFromLdap,
                                                          @Nonnull final InternetAddress...receivers) {
        sendMail(NotificationType.IP_ADDRESS_NOT_UNIQUE,
                 host,
                 "IP Address: " + ipAddress.toString() + "\n" +
                 "Formerly used by (now removed): " + iocFromLdap.getLdapName().toString(),
                 receivers);
    }

    public static void sendIpAddressNotSetInLDAP(@Nonnull final HostAddress host,
                                                 @Nonnull final List<String> iocsWithoutAttribute,
                                                 @Nonnull final InternetAddress...receivers) {
        sendMail(NotificationType.IP_ADDRESS_NOT_SET_IN_LDAP,
                 host,
                 "IOCs without epicsAddress attribute in LDAP:\n" + Joiner.on("\n").join(iocsWithoutAttribute),
                 receivers);
    }
}
