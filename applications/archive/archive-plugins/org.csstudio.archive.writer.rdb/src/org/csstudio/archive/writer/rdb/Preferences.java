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
@SuppressWarnings("nls")
public class Preferences
{
    public static final String WRITE_SAMPLE_TABLE = "write_sample_table";

    public static final String MAX_TEXT_SAMPLE_LENGTH = "max_text_sample_length";
    
    public static final String USE_POSTGRES_COPY = "use_postgres_copy";
    
    /** @return # Name of sample table for writing */
    public static String getWriteSampleTable()
    {
        String name = "sample";
        final IPreferencesService prefs = Platform.getPreferencesService();
        if (prefs != null)
            name = prefs.getString(Activator.ID, WRITE_SAMPLE_TABLE, name, null);
        return name;
    }
    
    /** @return Maximum length of text samples written to SAMPLE.STR_VAL */
    public static int getMaxStringSampleLength()
    {
        final IPreferencesService prefs = Platform.getPreferencesService();
        if (prefs == null)
            return 80;
        return prefs.getInt(Activator.ID, MAX_TEXT_SAMPLE_LENGTH, 80, null);
    }
    
    /** @return true to use postgres copy instead of insert */
    public static boolean isUsePostgresCopy()
    {
        final IPreferencesService prefs = Platform.getPreferencesService();
        if (prefs == null)
            return false;
        return prefs.getBoolean(Activator.ID, USE_POSTGRES_COPY, false, null);
    }
}
