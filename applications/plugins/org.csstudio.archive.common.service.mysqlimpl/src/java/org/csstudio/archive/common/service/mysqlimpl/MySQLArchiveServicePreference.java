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

import org.csstudio.platform.AbstractPreference;

/**
 * Constant definitions for archive service preferences (mimicked enum with inheritance).
 *
 * @param <T> the type of the preference. It must match the type of the default value.
 *
 * @author bknerr
 * @since 08.11.2010
 */
public final class MySQLArchiveServicePreference<T> extends AbstractPreference<T> {

    public static final MySQLArchiveServicePreference<String> URL =
        new MySQLArchiveServicePreference<String>("url", "NOT PUBLIC");

    public static final MySQLArchiveServicePreference<String> FAILOVER_URL =
        new MySQLArchiveServicePreference<String>("failover_url", "NOT PUBLIC");

    public static final MySQLArchiveServicePreference<String> USER =
        new MySQLArchiveServicePreference<String>("user", "NOT PUBLIC");

    public static final MySQLArchiveServicePreference<String> PASSWORD =
        new MySQLArchiveServicePreference<String>("password", "NOT PUBLIC");

    public static final MySQLArchiveServicePreference<String> DATABASE_NAME =
        new MySQLArchiveServicePreference<String>("database_name", "NOT PUBLIC");

    public static final MySQLArchiveServicePreference<Boolean> AUTO_CONNECT =
        new MySQLArchiveServicePreference<Boolean>("auto_connect", Boolean.FALSE);

    public static final MySQLArchiveServicePreference<Integer> SQL_TIMEOUT =
        new MySQLArchiveServicePreference<Integer>("sql_timeout", 30*60);

    /**
     * Constructor.
     * @param keyAsString
     * @param defaultValue
     */
    protected MySQLArchiveServicePreference(final String keyAsString, final T defaultValue) {
        super(keyAsString, defaultValue);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    protected Class<? extends AbstractPreference<T>> getClassType() {
        return (Class<? extends AbstractPreference<T>>) MySQLArchiveServicePreference.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getPluginID() {
        return Activator.PLUGIN_ID;
    }

}
