/*******************************************************************************
 * Copyright (c) 2013 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.security;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;

/** Preference settings
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class SecurityPreferences
{
    /** @return JAAS config file path */
    public static String getConfigFile()
    {
        return getSetting("jaas_config_file", "platform:/plugin/org.csstudio.security/jaas.conf");
    }
    
    /** @return JAAS config name */
    public static String getConfigName()
    {
        return getSetting("jaas_config_name", "SNS_UCAMS");
    }

    /** @return Config file path for authorization */
    public static String getAuthorizationFile()
    {
        return getSetting("authorization_config_file", "platform:/plugin/org.csstudio.security/authorization.conf");
    }

    /** @param key Preference key
     *  @param value Default value
     *  @return Preference setting
     */
    private static String getSetting(final String key, String value)
    {
        final IPreferencesService prefs = Platform.getPreferencesService();
        if (prefs != null)
            value = prefs.getString(SecuritySupport.ID, key, value, null);
        return value;
    }

}
