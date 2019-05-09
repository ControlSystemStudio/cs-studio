/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.logging.es;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;

/**
 * Access to preferences
 *
 * @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class Preferences
{
    // Names of preferences, see preferences.ini
    public static final String ES_URL = "es_url";
    public static final String ES_INDEX = "es_index";
    public static final String ES_MAPPING = "es_mapping";
    public static final String JMS_URL = "jms_url";
    public static final String JMS_USER = "jms_user";
    public static final String JMS_PASSWORD = "jms_password";
    public static final String JMS_TOPIC = "jms_topic";
    public static final String COLUMNS = "prop_cols";
    public static final String START = "start";

    /**
     * Decode the raw preference setting for column preferences into the
     * per-column settings
     *
     * @param pref_text
     *            Raw COLUMNS preference value, may be <code>null</code>
     * @return Array of per-column preference setting
     */
    public static String[] decodeRawColumnPreferences(String pref_text)
    {
        // Use default settings?
        if (pref_text == null)
        {
            pref_text = "CREATETIME,180,10|DELTA,60,1|SEVERITY,60,1|TEXT,50,400|NAME,50,100|USER,45,10|HOST,50,40|APPLICATION-ID,40,10|CLASS,50,10";
        }
        // Split columns on '|'
        return pref_text.split("\\|");
    }

    /**
     * Encode per-column pref. setting to one for all columns
     *
     * @param col_prefs
     *            Column settings for each column
     * @return One encoded string as stored in preferences
     */
    public static String encodeRawColumnPrefs(final String[] col_prefs)
    {
        StringBuilder buf = new StringBuilder();
        for (String col : col_prefs)
        {
            if (buf.length() > 0) buf.append("|");
            buf.append(col);
        }
        return buf.toString();
    }

    /**
     * Get preference settings for column definitions
     *
     * @return Array of raw strings for column preferences
     * @throws Exception
     *             on error
     */
    public static String[] getColumnPreferences() throws Exception
    {
        String pref_text = null;
        // Read preferences
        IPreferencesService service = Platform.getPreferencesService();
        if (service != null) pref_text = service.getString(Activator.ID,
                Preferences.COLUMNS, pref_text, null);
        return decodeRawColumnPreferences(pref_text);
    }

    /**
     * Gets the default start.
     *
     * @return the default start
     */
    public static String getDefaultStart()
    {
        // Read preferences
        IPreferencesService service = Platform.getPreferencesService();
        String start = "-8 hour"; // typical shift length
        if (service != null) start = service.getString(Activator.ID,
                Preferences.START, start, null);
        return start;
    }

    /**
     * Get column definitions from preferences
     *
     * @return Array of <code>PropertyColumnPreference</code>
     * @throws Exception
     *             on error
     */
    public static PropertyColumnPreference[] getPropertyColumns()
            throws Exception
    {
        String[] col = getColumnPreferences();
        PropertyColumnPreference prefs[] = new PropertyColumnPreference[col.length];
        for (int i = 0; i < col.length; ++i)
            prefs[i] = PropertyColumnPreference.fromString(col[i]);
        return prefs;
    }

}
