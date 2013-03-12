/*******************************************************************************
 * Copyright (c) 2013 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.sns.pvnames;

import java.io.FileInputStream;
import java.util.Properties;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;

/** Preference settings
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class Preferences
{
    /** @return RDB URL */
    public static String getURL()
    {
        return get("url");
    }
    
    /** @return RDB user name */
    public static String getUser()
    {
        return get("user");
    }
    
    /** @return RDB password */
    public static String getPassword()
    {
        return get("password");
    }
    
    /** Locate setting from Eclipse preferences
     *  or directly read preferences.ini for Unit test
     * 
     *  @param key Preference key
     *  @return Value
     */
    private static String get(final String key)
    {
        final IPreferencesService prefs = Platform.getPreferencesService();
        if (prefs == null)
        {
            final Properties props = new Properties();
            try
            {
                props.load(new FileInputStream("preferences.ini"));
            }
            catch (Exception ex)
            {
                // Ignore
            }
            return props.getProperty(key);
        }
        else
            return prefs.getString("org.csstudio.sns.pvnames", key, null, null);
    }
}
