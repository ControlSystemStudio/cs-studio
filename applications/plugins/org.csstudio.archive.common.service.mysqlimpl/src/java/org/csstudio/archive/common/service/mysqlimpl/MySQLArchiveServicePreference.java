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

/**
 * Constant definitions for archive service preferences (mimicked enum with inheritance).
 *
 * @param <T> the type of the preference. It must match the type of the default value.
 *
 * @author bknerr
 * @since 08.11.2010
 */
public final class MySQLArchiveServicePreference<T> extends AbstractPreference<T> {

    public static final MySQLArchiveServicePreference<String> HOST =
        new MySQLArchiveServicePreference<String>("host", "NOT PUBLIC");

    public static final MySQLArchiveServicePreference<String> FAILOVER_HOST =
        new MySQLArchiveServicePreference<String>("failoverHost", "NOT PUBLIC");

    public static final MySQLArchiveServicePreference<Integer> PERIOD =
        new MySQLArchiveServicePreference<Integer>("periodInMS", 5000);

    public static final MySQLArchiveServicePreference<Integer> PORT =
        new MySQLArchiveServicePreference<Integer>("port", 3306);

    public static final MySQLArchiveServicePreference<String> DATABASE_NAME =
        new MySQLArchiveServicePreference<String>("databaseName", "archive");

    public static final MySQLArchiveServicePreference<String> USER =
        new MySQLArchiveServicePreference<String>("user", "NOT PUBLIC");

    public static final MySQLArchiveServicePreference<String> PASSWORD =
        new MySQLArchiveServicePreference<String>("password", "NOT PUBLIC");

    public static final MySQLArchiveServicePreference<Integer> MAX_ALLOWED_PACKET_IN_KB =
        new MySQLArchiveServicePreference<Integer>("maxAllowedPacketInKB", 32768);

    public static final MySQLArchiveServicePreference<String> SMTP_HOST =
        new MySQLArchiveServicePreference<String>("mailhost", "NOT PUBLIC");

    public static final MySQLArchiveServicePreference<File> DATA_RESCUE_DIR =
        new MySQLArchiveServicePreference<File>("dataRescueDir", new File("C:\\temp\\persistDataRescue"));

    public static final MySQLArchiveServicePreference<String> EMAIL_ADDRESS =
        new MySQLArchiveServicePreference<String>("emailAddress", "NOT PUBLIC");




    /**
     * Constructor.
     * @param keyAsString
     * @param defaultValue
     */
    protected MySQLArchiveServicePreference(@Nonnull final String keyAsString,
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
        return Activator.PLUGIN_ID;
    }



}
