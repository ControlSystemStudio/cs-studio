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
package org.csstudio.archive.common.service.mysqlimpl;

import java.io.File;

import javax.annotation.Nonnull;

import org.csstudio.domain.desy.preferences.AbstractPreference;
import org.csstudio.domain.desy.preferences.IPreferenceValidator;
import org.csstudio.domain.desy.preferences.MinMaxPreferenceValidator;

/**
 * Constant definitions for archive service preferences (mimicked enum with inheritance).
 *
 * @author bknerr
 * @since 08.11.2010
 */
public class MySQLArchivePreferenceService {

    /**
     * Type safe delegator to Eclipse preference service.
     *
     * @author bknerr
     * @since 11.07.2011
     * @param <T> the type of the preference. It must match the type of the default value.
     */
    private static final class MySQLArchiveServicePreferenceInner<T> extends AbstractPreference<T> {

        private static final Integer MIN_PACKET_SIZE_KB = 1024;
        private static final Integer MAX_PACKET_SIZE_KB = 65536;
        private static final int MIN_PERIOD_MS = 2000;
        private static final int MAX_PERIOD_MS = 60000;

        public static final MySQLArchiveServicePreferenceInner<String> HOST =
            new MySQLArchiveServicePreferenceInner<String>("host", "NOT PUBLIC");

        public static final MySQLArchiveServicePreferenceInner<String> FAILOVER_HOST =
            new MySQLArchiveServicePreferenceInner<String>("failoverHost", "");

        public static final MySQLArchiveServicePreferenceInner<Integer> PERIOD_IN_MS =
            new MySQLArchiveServicePreferenceInner<Integer>("periodInMS", 5000)
                .with(new MinMaxPreferenceValidator<Integer>(MIN_PERIOD_MS, MAX_PERIOD_MS));

        public static final MySQLArchiveServicePreferenceInner<Integer> PORT =
            new MySQLArchiveServicePreferenceInner<Integer>("port", 3306);

        public static final MySQLArchiveServicePreferenceInner<String> DATABASE_NAME =
            new MySQLArchiveServicePreferenceInner<String>("databaseName", "archive");

        public static final MySQLArchiveServicePreferenceInner<String> USER =
            new MySQLArchiveServicePreferenceInner<String>("user", "NOT PUBLIC");

        public static final MySQLArchiveServicePreferenceInner<String> PASSWORD =
            new MySQLArchiveServicePreferenceInner<String>("password", "NOT PUBLIC");

        public static final MySQLArchiveServicePreferenceInner<Integer> MAX_ALLOWED_PACKET_IN_KB =
            new MySQLArchiveServicePreferenceInner<Integer>("maxAllowedPacketInKB", 32768)
                .with(new MinMaxPreferenceValidator<Integer>(MIN_PACKET_SIZE_KB, MAX_PACKET_SIZE_KB));

        public static final MySQLArchiveServicePreferenceInner<String> SMTP_HOST =
            new MySQLArchiveServicePreferenceInner<String>("mailhost", "NOT PUBLIC");

        public static final MySQLArchiveServicePreferenceInner<File> DATA_RESCUE_DIR =
            new MySQLArchiveServicePreferenceInner<File>("dataRescueDir", new File("C:\\temp\\persistDataRescue"));

        public static final MySQLArchiveServicePreferenceInner<String> EMAIL_ADDRESS =
            new MySQLArchiveServicePreferenceInner<String>("emailAddress", "NOT PUBLIC");


        /**
         * Constructor.
         * @param keyAsString
         * @param defaultValue
         */
        private MySQLArchiveServicePreferenceInner(@Nonnull final String keyAsString,
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
            return (Class<? extends AbstractPreference<T>>) MySQLArchiveServicePreferenceInner.class;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        @Nonnull
        public String getPluginID() {
            return MySqlServiceImplActivator.PLUGIN_ID;
        }

        @Nonnull
        private MySQLArchiveServicePreferenceInner<T> with(@Nonnull final IPreferenceValidator<T> val) {
            return (MySQLArchiveServicePreferenceInner<T>) super.addValidator(val);
        }
    }

    /**
     * Constructor.
     */
    public MySQLArchivePreferenceService() {
        // Empty
    }
    @Nonnull
    public final String getHost() {
        return MySQLArchiveServicePreferenceInner.HOST.getValue();
    }
    @Nonnull
    public final String getFailOverHost() {
        return MySQLArchiveServicePreferenceInner.FAILOVER_HOST.getValue();
    }
    @Nonnull
    public final File getDataRescueDir() {
        return MySQLArchiveServicePreferenceInner.DATA_RESCUE_DIR.getValue();
    }
    @Nonnull
    public final String getDatabaseName() {
        return MySQLArchiveServicePreferenceInner.DATABASE_NAME.getValue();
    }
    @Nonnull
    public final String getEmailAddress() {
        return MySQLArchiveServicePreferenceInner.EMAIL_ADDRESS.getValue();
    }
    @Nonnull
    public final Integer getMaxAllowedPacketSizeInKB() {
        return MySQLArchiveServicePreferenceInner.MAX_ALLOWED_PACKET_IN_KB.getValue();
    }
    @Nonnull
    public final Integer getPeriodInMS() {
        return MySQLArchiveServicePreferenceInner.PERIOD_IN_MS.getValue();
    }
    @Nonnull
    public final String getPassword() {
        return MySQLArchiveServicePreferenceInner.PASSWORD.getValue();
    }
    @Nonnull
    public final String getSmtpHost() {
        return MySQLArchiveServicePreferenceInner.SMTP_HOST.getValue();
    }
    @Nonnull
    public String getUser() {
        return MySQLArchiveServicePreferenceInner.USER.getValue();
    }
    @Nonnull
    public Integer getPort() {
        return MySQLArchiveServicePreferenceInner.PORT.getValue();
    }
}
