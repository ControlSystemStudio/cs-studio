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
    private static final class MySQLArchiveServicePreference<T> extends AbstractPreference<T> {

        public static final Integer MIN_PACKET_SIZE_KB = 1024;
        public static final Integer MAX_PACKET_SIZE_KB = 65536;
        public static final int MIN_PERIOD_MS = 2000;
        public static final int MAX_PERIOD_MS = 60000;
        public static final int MIN_TERM_TIME_MS = 10000;
        public static final int MAX_TERM_TIME_MS = 100000;

        public static final MySQLArchiveServicePreference<String> HOST =
            new MySQLArchiveServicePreference<String>("host", "NOT PUBLIC");

        public static final MySQLArchiveServicePreference<String> FAILOVER_HOST =
            new MySQLArchiveServicePreference<String>("failoverHost", "");

        public static final MySQLArchiveServicePreference<Integer> PERIOD_IN_MS =
            new MySQLArchiveServicePreference<Integer>("periodInMS", 5000)
                .with(new MinMaxPreferenceValidator<Integer>(MIN_PERIOD_MS, MAX_PERIOD_MS));

        public static final MySQLArchiveServicePreference<Integer> TERM_TIME_IN_MS =
            new MySQLArchiveServicePreference<Integer>("terminationTimeInMS", 25000)
            .with(new MinMaxPreferenceValidator<Integer>(MIN_TERM_TIME_MS, MAX_TERM_TIME_MS));

        public static final MySQLArchiveServicePreference<Integer> PORT =
            new MySQLArchiveServicePreference<Integer>("port", 3306);

        public static final MySQLArchiveServicePreference<String> DATABASE_NAME =
            new MySQLArchiveServicePreference<String>("databaseName", "archive");

        public static final MySQLArchiveServicePreference<String> USER =
            new MySQLArchiveServicePreference<String>("user", "NOT PUBLIC");

        public static final MySQLArchiveServicePreference<String> PASSWORD =
            new MySQLArchiveServicePreference<String>("password", "NOT PUBLIC");

        public static final MySQLArchiveServicePreference<Integer> MAX_ALLOWED_PACKET_IN_KB =
            new MySQLArchiveServicePreference<Integer>("maxAllowedPacketInKB", 32768)
                .with(new MinMaxPreferenceValidator<Integer>(MIN_PACKET_SIZE_KB, MAX_PACKET_SIZE_KB));

//        public static final MySQLArchiveServicePreference<String> SMTP_HOST =
//            new MySQLArchiveServicePreference<String>("mailhost", "NOT PUBLIC");
//
//        public static final MySQLArchiveServicePreference<File> DATA_RESCUE_DIR =
//            new MySQLArchiveServicePreference<File>("dataRescueDir", new File("C:\\temp\\persistDataRescue"));
//
//        public static final MySQLArchiveServicePreference<String> EMAIL_ADDRESS =
//            new MySQLArchiveServicePreference<String>("emailAddress", "NOT PUBLIC");


        /**
         * Constructor.
         * @param keyAsString
         * @param defaultValue
         */
        private MySQLArchiveServicePreference(@Nonnull final String keyAsString,
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
            return (Class<? extends AbstractPreference<T>>) MySQLArchiveServicePreference.class;
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
        private MySQLArchiveServicePreference<T> with(@Nonnull final IPreferenceValidator<T> val) {
            return (MySQLArchiveServicePreference<T>) super.addValidator(val);
        }
    }

    /**
     * Constructor.
     */
    public MySQLArchivePreferenceService() {
        // Empty
    }
    @Nonnull
    public String getHost() {
        return MySQLArchiveServicePreference.HOST.getValue();
    }
    @Nonnull
    public String getFailOverHost() {
        return MySQLArchiveServicePreference.FAILOVER_HOST.getValue();
    }
//    @Nonnull
//    public File getDataRescueDir() {
//        return MySQLArchiveServicePreference.DATA_RESCUE_DIR.getValue();
//    }
    @Nonnull
    public String getDatabaseName() {
        return MySQLArchiveServicePreference.DATABASE_NAME.getValue();
    }
//    @Nonnull
//    public String getEmailAddress() {
//        return MySQLArchiveServicePreference.EMAIL_ADDRESS.getValue();
//    }
    @Nonnull
    public Integer getMaxAllowedPacketSizeInKB() {
        return MySQLArchiveServicePreference.MAX_ALLOWED_PACKET_IN_KB.getValue();
    }
    @Nonnull
    public Integer getPeriodInMS() {
        return MySQLArchiveServicePreference.PERIOD_IN_MS.getValue();
    }
    @Nonnull
    public String getPassword() {
        return MySQLArchiveServicePreference.PASSWORD.getValue();
    }
//    @Nonnull
//    public String getSmtpHost() {
//        return MySQLArchiveServicePreference.SMTP_HOST.getValue();
//    }
    @Nonnull
    public String getUser() {
        return MySQLArchiveServicePreference.USER.getValue();
    }
    @Nonnull
    public Integer getPort() {
        return MySQLArchiveServicePreference.PORT.getValue();
    }
    @Nonnull
    public Integer getTerminationTimeInMS() {
        return MySQLArchiveServicePreference.TERM_TIME_IN_MS.getValue();
    }
}
