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
package org.csstudio.archive.engine2;

import org.csstudio.platform.security.SecureStorage;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;

/**
 * Access to engine related RDB archive preferences.
 *
 * Copied from archive.rdb.RDBArchivePreferences in order to decouple plugins.
 * FIXME (bknerr) : the prefs belong to the service implementation.
 *
 * @author bknerr
 * @since 16.11.2010
 */
public class RDBArchiveEnginePreferences {
    public static final String URL = "url";
    public static final String SCHEMA = "schema";
    public static final String USER = "user";
    public static final String PASSWORD = "password";
    public static final String MAX_TEXT_SAMPLE_LENGTH = "max_text_sample_length";
    public static final String MIN_SAMPLE_PERIOD = "min_sample_period";
    public static final String SQL_TIMEOUT = "sql_timeout";

    /** @return URL of RDB archive server */
    public static String getURL()
    {
        return getString(URL);
    }

    /** @return Schema for RDB tables or <code>null</code> */
    public static String getSchema()
    {
        return getString(SCHEMA);
    }

    /** @return User name for RDB archive server */
    public static String getUser()
    {
        return getString(USER);
    }

    /** @return Password for RDB archive server */
    public static String getPassword()
    {
        // Try 'secure' preference file
        final String password = SecureStorage.retrieveSecureStorage(Activator.PLUGIN_ID, PASSWORD);
        if (password != null) {
            return password;
        }
        // Fall back to plain prefs
        return getString(PASSWORD);
    }

    /** @return Maximum length of text samples written to SAMPLE.STR_VAL */
    public static int getMaxStringSampleLength()
    {
        final IPreferencesService prefs = Platform.getPreferencesService();
        if (prefs == null) {
            return 80;
        }
        return prefs.getInt(Activator.PLUGIN_ID, MAX_TEXT_SAMPLE_LENGTH, 80, null);
    }

    /** @return Minimum sample period in seconds */
    public static double getMinSamplePeriod()
    {
        final IPreferencesService prefs = Platform.getPreferencesService();
        if (prefs == null) {
            return 0.1;
        }
        return prefs.getDouble(Activator.PLUGIN_ID, MIN_SAMPLE_PERIOD, 0.1, null);
    }

    /** @return SQL Timeout in seconds */
    public static int getSQLTimeout()
    {
        final IPreferencesService prefs = Platform.getPreferencesService();
        if (prefs == null) {
            return 30*60;
        }
        return prefs.getInt(Activator.PLUGIN_ID, SQL_TIMEOUT, 30*60, null);
    }

    /** Get string preference
     *  @param key Preference key
     *  @return String or <code>null</code>
     */
    private static String getString(final String key)
    {
        final IPreferencesService prefs = Platform.getPreferencesService();
        if (prefs == null) {
            return null;
        }
        return prefs.getString(Activator.PLUGIN_ID, key, null, null);
    }
}
