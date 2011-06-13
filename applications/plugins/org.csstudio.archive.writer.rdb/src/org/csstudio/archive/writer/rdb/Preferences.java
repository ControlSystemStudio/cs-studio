/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.writer.rdb;

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
    public static final String SQL_TIMEOUT = "sql_timeout";
    public static final String MAX_TEXT_SAMPLE_LENGTH = "max_text_sample_length";
    
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
    
    /** @return Maximum length of text samples written to SAMPLE.STR_VAL */
    public static int getMaxStringSampleLength()
    {
        final IPreferencesService prefs = Platform.getPreferencesService();
        if (prefs == null)
            return 80;
        return prefs.getInt(Activator.ID, MAX_TEXT_SAMPLE_LENGTH, 80, null);
    }

	/** @return SQL Timeout in seconds */
    public static int getSQLTimeoutSecs()
    {
        final IPreferencesService prefs = Platform.getPreferencesService();
        if (prefs == null)
            return 30*60;
        return prefs.getInt(Activator.ID, SQL_TIMEOUT, 30*60, null);
    }
}
