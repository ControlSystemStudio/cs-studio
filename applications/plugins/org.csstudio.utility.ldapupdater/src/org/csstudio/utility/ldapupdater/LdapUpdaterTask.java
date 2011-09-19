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
package org.csstudio.utility.ldapupdater;

import javax.annotation.Nonnull;

import org.csstudio.utility.ldapupdater.action.UpdateLdapAction;
import org.csstudio.utility.ldapupdater.mail.NotificationMailer;
import org.csstudio.utility.ldapupdater.preferences.LdapUpdaterPreferencesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * LDAP updater task.
 *
 * @author bknerr
 * @since 28.09.2010
 */
public class LdapUpdaterTask implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(LdapUpdaterTask.class);

    private final LdapUpdaterPreferencesService _prefsService;

    /**
     * Constructor.
     */
    public LdapUpdaterTask(@Nonnull final LdapUpdaterPreferencesService prefsService) {
        _prefsService = prefsService;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {

        try {
            new UpdateLdapAction().updateLdapFromIOCFiles();
        } catch (final Throwable t) {
            LOG.error("Throwable in LDAP Updater.", t);
            NotificationMailer.sendUnknownErrorMessage(_prefsService.getSmtpHostAddress(),
                                                       "Message:\n" + t.getMessage() + "\n\nCause:\n" + t.getCause(),
                                                       _prefsService.getDefaultResponsiblePerson());
        }
    }
}
