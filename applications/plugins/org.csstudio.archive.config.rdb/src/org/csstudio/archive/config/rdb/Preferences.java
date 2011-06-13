/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.config.rdb;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;

/** Access to RDB archive preferences
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")public class Preferences
{
    public static final String RDB_URL = "rdb_url";
    public static final String RDB_USER = "rdb_user";
    public static final String RDB_PASSWORD = "rdb_password";
    public static final String RDB_SCHEMA = "rdb_schema";
    
    // TODO Move all to archive.rdb
	public static String getURL()
    {
        final IPreferencesService prefs = Platform.getPreferencesService();
        return prefs.getString(Activator.ID, RDB_URL, null, null);
    }

	public static String getUser()
    {
        final IPreferencesService prefs = Platform.getPreferencesService();
        return prefs.getString(Activator.ID, RDB_USER, null, null);
    }

	// TODO Use secure prefs for password
	public static String getPassword()
    {
        final IPreferencesService prefs = Platform.getPreferencesService();
        return prefs.getString(Activator.ID, RDB_PASSWORD, null, null);
    }

	public static String getSchema()
    {
        final IPreferencesService prefs = Platform.getPreferencesService();
        String schema = "CHAN_ARCH.";
        if (prefs == null)
        	return schema;
        return prefs.getString(Activator.ID, RDB_SCHEMA, schema, null);
    }
}
