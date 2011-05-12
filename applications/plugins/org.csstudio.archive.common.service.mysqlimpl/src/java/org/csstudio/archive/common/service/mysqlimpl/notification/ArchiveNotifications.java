/*
 * Copyright (c) 2011 Stiftung Deutsches Elektronen-Synchrotron,
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
package org.csstudio.archive.common.service.mysqlimpl.notification;

import static org.csstudio.archive.common.service.mysqlimpl.MySQLArchiveServicePreference.EMAIL_ADDRESS;
import static org.csstudio.archive.common.service.mysqlimpl.MySQLArchiveServicePreference.SMTP_HOST;

import java.io.IOException;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.csstudio.archive.common.service.mysqlimpl.persistengine.NotificationType;
import org.csstudio.email.EMailSender;
import org.slf4j.LoggerFactory;

/**
 * Archive notification manager.
 *
 * @author bknerr
 * @since 11.04.2011
 */
public final class ArchiveNotifications {

    /**
     * Constructor.
     */
    private ArchiveNotifications() {
        // Don't instantiate
    }

    private static final Logger LOG =
            LoggerFactory.getLogger(ArchiveNotifications.class);

    public static void notify(@Nonnull final NotificationType type,
                              @Nonnull final String additionalBodyText) {

        final String prefMailHost = SMTP_HOST.getValue();
        final String prefEmailReceiver = EMAIL_ADDRESS.getValue();

        EMailSender mailer;
        try {
            mailer = new EMailSender(prefMailHost,
                                     "DontReply@MySQLArchiver",
                                     prefEmailReceiver,
                                     type.getSubject());
            mailer.addText(type.getText() + additionalBodyText);
            mailer.close();
        } catch (final IOException e) {
            LOG.error("Email notification for " + type.name() + " failed.");
        }
    }

}
