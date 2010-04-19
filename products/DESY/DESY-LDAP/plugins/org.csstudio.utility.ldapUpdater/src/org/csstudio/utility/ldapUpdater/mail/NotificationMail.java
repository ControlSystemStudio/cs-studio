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

import java.io.IOException;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.csstudio.email.EMailSender;
import org.csstudio.email.EmailUtils;

/**
 * Encapsulates LDAP Updater specific mail functionality.
 *
 * @author bknerr 24.03.2010
 */
public final class NotificationMail {
    private static final String HOST = "smtp.desy.de";
    private static final String FROM = "DontReply@LDAPUpdater";

    /**
     * Don't instantiate.
     */
    private NotificationMail() {
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
    public static boolean sendMail(@Nonnull final NotificationType type,
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
        EMailSender mailer = null;
            mailer = new EMailSender(HOST,
                                     FROM,
                                     receiver,
                                     type.getSubject());

            mailer.addText(type.getText() + (additionalBody != null ? additionalBody : ""));
            mailer.close();
    }

}
