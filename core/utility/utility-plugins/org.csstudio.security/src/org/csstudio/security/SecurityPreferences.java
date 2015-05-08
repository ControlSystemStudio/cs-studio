/*******************************************************************************
 * Copyright (c) 2013 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.security;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.security.preferences.SecurePreferences;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;

/** Preference settings
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class SecurityPreferences
{
    /** @return JAAS config file path */
    public static String getJaasConfigFile()
    {
        return getSetting("jaas_config_file", "platform:/plugin/org.csstudio.security/jaas.conf");
    }

    /** @return JAAS config name */
    public static String getJaasConfigName()
    {
        return getSetting("jaas_config_name", "SNS_UCAMS");
    }

    /** @return JAAS configuration (instead of file/name) */
    public static String getJaasConfig()
    {
        return getSetting("jaas_config", "");
    }

    /** @return Config file path for authorization */
    public static String getAuthorizationProvider()
    {
        return getSetting("authorization_provider", "FileBased");
    }

    /** @return Config file path for FileBased authorization */
    public static String getAuthorizationFile()
    {
        return getSetting("authorization_file_name", "platform:/plugin/org.csstudio.security/authorization.conf");
    }

    /** @return Path to command used by Script authorization */
    public static String getAuthorizationScript()
    {
        return getSetting("authorization_script_name", "/usr/local/bin/id_auth");
    }

    /** @return URL of LDAP for group-based authorization */
    public static String getLDAPGroupURL()
    {
        return getSetting("ldap_group_url", "ldap://localhost/dc=test,dc=ics");
    }

    /** @return LDAP Base for group-based authorization */
    public static String getLDAPGroupBase()
    {
        return getSetting("ldap_group_base", "ou=Group");
    }

    /** @return {@link SecurePreferences} location type */
    public static SecurePreferences.Type getSecurePreferenceLocation()
    {
        final String setting = getSetting("secure_preference_location", "Default");
        try
        {
            return SecurePreferences.Type.valueOf(setting);
        }
        catch (Exception ex)
        {
            Logger.getLogger(SecurityPreferences.class.getName())
                .log(Level.WARNING, "Invalid preference location {0}", setting);
        }
        return SecurePreferences.Type.Default;
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
