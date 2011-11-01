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
package org.csstudio.archive.common.engine;

import javax.annotation.Nonnull;

import org.csstudio.domain.desy.preferences.AbstractPreference;


/**
 * Access to engine related RDB archive preferences.
 *
 * @author bknerr
 * @since 16.11.2010
 */
public class ArchiveEnginePreferencesService {

    /**
     * Type safe delegator to Eclipse preference service.
     *
     * @author bknerr
     * @since 24.08.2011
     * @param <T> the type of the preference
     */
    private static final class ArchiveEnginePreference<T> extends AbstractPreference<T>{

        public static final ArchiveEnginePreference<Integer> WRITE_PERIOD_IN_S =
            new ArchiveEnginePreference<Integer>("writePeriodInS", Integer.valueOf(5));
        public static final ArchiveEnginePreference<Integer> HEARTBEAT_PERIOD_IN_S =
            new ArchiveEnginePreference<Integer>("heartBeatPeriodInS", Integer.valueOf(1));
        public static final ArchiveEnginePreference<String> VERSION =
            new ArchiveEnginePreference<String>("version", "0.0.1-beta");
        public static final ArchiveEnginePreference<String> HTTP_ADMIN_VALUE =
            new ArchiveEnginePreference<String>("httpAdmin", "");

        /**
         * Constructor.
         */
        protected ArchiveEnginePreference(@Nonnull final String keyAsString,
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
            return (Class<? extends AbstractPreference<T>>) ArchiveEnginePreference.class;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        @Nonnull
        public String getPluginID() {
            return ArchiveEngineActivator.PLUGIN_ID;
        }
    }

    /**
     * Constructor.
     */
    public ArchiveEnginePreferencesService() {
        // Empty
    }

    @Nonnull
    public String getVersion() {
        return ArchiveEnginePreference.VERSION.getValue();
    }
    @Nonnull
    public Integer getWritePeriodInS() {
        return ArchiveEnginePreference.WRITE_PERIOD_IN_S.getValue();
    }
    @Nonnull
    public Integer getHeartBeatPeriodInS() {
        return ArchiveEnginePreference.HEARTBEAT_PERIOD_IN_S.getValue();
    }
    @Nonnull
    public String getHttpAdminValue() {
        return ArchiveEnginePreference.HTTP_ADMIN_VALUE.getValue();
    }
    @Nonnull
    public String getHttpAdminKey() {
        return ArchiveEnginePreference.HTTP_ADMIN_VALUE.getKeyAsString();
    }
}

