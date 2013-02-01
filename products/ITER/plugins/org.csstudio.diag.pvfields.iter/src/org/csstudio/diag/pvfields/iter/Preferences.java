/*******************************************************************************
* Copyright (c) 2010-2013 ITER Organization.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
******************************************************************************/
package org.csstudio.diag.pvfields.iter;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;

public class Preferences {
	
	final public static String RDB_URL = "rdb_url";
    final public static String RDB_USER = "rdb_user";
    final public static String RDB_PASSWORD = "rdb_password";
    
    /** @param setting Preference identifier
     *  @return String from preference system, or <code>null</code>
     */
    private static String getString(final String setting)
    {
        return getString(setting, null);
    }

    /** @param setting Preference identifier
     *  @param default_value Default value when preferences unavailable
     *  @return String from preference system, or <code>null</code>
     */
    private static String getString(final String setting, final String default_value)
    {
        final IPreferencesService service = Platform.getPreferencesService();
        if (service == null)
            return default_value;
        return service.getString(Activator.ID, setting, default_value, null);
    }
    
    /** @return RDB URL */
    public static String getRDB_Url()
    {
        return getString(RDB_URL);
    }

    /** @return RDB User name */
    public static String getRDB_User()
    {
    	return getString(RDB_USER);
    }

	/** @return RDB Password */
    public static String getRDB_Password()
    {
        return getString(RDB_PASSWORD);
    }

}
