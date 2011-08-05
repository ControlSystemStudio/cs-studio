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
package org.csstudio.utility.ldapupdater.preferences;

import java.io.File;

import javax.annotation.Nonnull;

import org.csstudio.domain.desy.net.HostAddress;
import org.csstudio.domain.desy.preferences.AbstractPreference;
import org.csstudio.utility.ldapupdater.LdapUpdaterActivator;

/**
 * Constant definitions for archive service preferences (mimicked enum with inheritance).
 *
 * @author bknerr
 * @since 08.11.2010
 */
public class LdapUpdaterPreferencesService {

    /**
     * Type safe delegator to Eclipse preference service.
     *
     * @author bknerr
     * @since 26.04.2011
     * @param <T> the type of the preference
     */
    private static final class LdapUpdaterPreference<T> extends AbstractPreference<T> {

        public static final LdapUpdaterPreference<File> IOC_DBL_DUMP_PATH =
            new LdapUpdaterPreference<File>("iocDblDumpPath", new File("Y:\\directoryServer\\"));

        public static final LdapUpdaterPreference<File> HEARTBEAT_FILEPATH =
            new LdapUpdaterPreference<File>("heartbeatfile", new File("Y:\\scripts\\ldap-tests\\ldapupdater.heartbeat"));

        public static final LdapUpdaterPreference<File> HISTORY_DAT_FILEPATH =
            new LdapUpdaterPreference<File>("ldapHistPath", new File("Y:\\scripts\\ldap-tests\\history.dat"));

        public static final LdapUpdaterPreference<String> XMPP_USER =
            new LdapUpdaterPreference<String>("xmppUser", "anonymous");

        public static final LdapUpdaterPreference<String> XMPP_PASSWORD =
            new LdapUpdaterPreference<String>("xmppPassword", "anonymous");

        public static final LdapUpdaterPreference<HostAddress> XMPP_SERVER =
            new LdapUpdaterPreference<HostAddress>("xmppServer", new HostAddress("krynfs.desy.de"));

        public static final LdapUpdaterPreference<Long> LDAP_AUTO_START =
            new LdapUpdaterPreference<Long>("ldapAutoStart", Long.valueOf(46800));

        public static final LdapUpdaterPreference<Long> LDAP_AUTO_INTERVAL =
            new LdapUpdaterPreference<Long>("ldapAutoInterval", Long.valueOf(43200));

        /**
         * Constructor.
         */
        protected LdapUpdaterPreference(@Nonnull final String keyAsString,
                                        @Nonnull final T defaultValue) {
            super(keyAsString, defaultValue);
        }

        /**
         * {@inheritDoc}
         */
        @SuppressWarnings("unchecked")
        @Override
        @Nonnull
        protected Class<? extends AbstractPreference<T>> getClassType() {
            return (Class<? extends AbstractPreference<T>>) LdapUpdaterPreference.class;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        @Nonnull
        public String getPluginID() {
            return LdapUpdaterActivator.PLUGIN_ID;
        }
    }

    /**
     * Constructor.
     */
    public LdapUpdaterPreferencesService() {
        // Empty
    }

    @Nonnull
    public File getIocDblDumpPath() {
        return LdapUpdaterPreference.IOC_DBL_DUMP_PATH.getValue();
    }
    @Nonnull
    public File getHistoryDatFilePath() {
        return LdapUpdaterPreference.HISTORY_DAT_FILEPATH.getValue();
    }
    @Nonnull
    public File getHeartBeatFile() {
        return LdapUpdaterPreference.HEARTBEAT_FILEPATH.getValue();
    }
    @Nonnull
    public String getXmppUser() {
        return LdapUpdaterPreference.XMPP_USER.getValue();
    }
    @Nonnull
    public String getXmppPassword() {
        return LdapUpdaterPreference.XMPP_PASSWORD.getValue();
    }
    @Nonnull
    public HostAddress getXmppServer() {
        return LdapUpdaterPreference.XMPP_SERVER.getValue();
    }
    @Nonnull
    public Long getLdapAutoStart() {
        return LdapUpdaterPreference.LDAP_AUTO_START.getValue();
    }
    @Nonnull
    public Long getLdapStartInterval() {
        return LdapUpdaterPreference.LDAP_AUTO_INTERVAL.getValue();
    }
}
