/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.msghist;

import java.util.concurrent.TimeUnit;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;

/** Access to preferences
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class Preferences
{
	// Names of preferences, see preferences.ini
	public static final String RDB_URL = "rdb_url";
	public static final String RDB_USER = "rdb_user";
	public static final String RDB_PASSWORD = "rdb_password";
	public static final String RDB_SCHEMA = "rdb_schema";
	public static final String COLUMNS = "prop_cols";
    public static final String START = "start";
    public static final String MAX_PROPERTIES = "max_properties"; 
    public static final String AUTO_REFRESH_PERIOD = "auto_refresh_period"; 
    
    
    /** Get preference settings for column definitions
     *  @return Array of raw strings for column preferences
     *  @throws Exception on error
     */
    public static String[] getColumnPreferences() throws Exception
    {
    	String pref_text = null;
    	// Read preferences
        final IPreferencesService service = Platform.getPreferencesService();
        if (service != null)
            pref_text = service.getString(Activator.ID, Preferences.COLUMNS,
                    pref_text, null);
        return decodeRawColumnPreferences(pref_text);
    }
    
    /** Decode the raw preference setting for column preferences
     *  into the per-column settings
     *  @param pref_text Raw COLUMNS preference value, may be <code>null</code>
     *  @return Array of per-column preference setting
     */
    public static String[] decodeRawColumnPreferences(String pref_text)
    {
    	// Use default settings?
    	if (pref_text == null)
            pref_text =
                "TYPE,45,5|TEXT,50,400|NAME,50,100|STATUS,45,80|SEVERITY,50,80";
        // Split columns on '|'
        return pref_text.split("\\|");
    }

    /** Encode per-column pref. setting to one for all columns
     *  @param col_prefs Column settings for each column
     *  @return One encoded string as stored in preferences
     */
    public static String encodeRawColumnPrefs(final String[] col_prefs)
    {
    	final StringBuilder buf = new StringBuilder();
    	for (String col : col_prefs)
    	{
    		if (buf.length() > 0)
    			buf.append("|");
			buf.append(col);
		}
    	return buf.toString();
	}

	/** Get column definitions from preferences
     *  @return Array of <code>PropertyColumnPreference</code>
     *  @throws Exception on error
     */
    public static PropertyColumnPreference[] getPropertyColumns() throws Exception
    {
        final String[] col = getColumnPreferences();
        final PropertyColumnPreference prefs[] =
            new PropertyColumnPreference[col.length];
        for (int i=0; i<col.length; ++i)
    	    prefs[i] = PropertyColumnPreference.fromString(col[i]);
        return prefs;
    }

    /**
     * Gets the default start.
     *
     * @return the default start
     */
    public static String getDefaultStart()
    {
        // Read preferences
        final IPreferencesService service = Platform.getPreferencesService();
        String start = "-1 hour";
        if (service != null)
            start = service.getString(Activator.ID, Preferences.START,
                    start, null);
        return start;
    }
      
    

	/**
	 * Gets the max properties.
	 *
	 * @return the max properties
	 */
	public static int getMaxProperties()
    {
        final IPreferencesService service = Platform.getPreferencesService();
        int max_properties = 100000;
        if (service != null)
        	max_properties = service.getInt(Activator.ID, Preferences.MAX_PROPERTIES,
        			max_properties, null);
        return max_properties;
    }
	
	
	/**
	 * Gets the default auto refresh timer.
	 *
	 * @return the default auto refresh timer (milliseconds)
	 */
	public static long getAutoRefreshPeriod()
    {
        // Read preferences
        final IPreferencesService service = Platform.getPreferencesService();
        long period = 0;
        if (service != null)
        	period = service.getLong(Activator.ID, Preferences.AUTO_REFRESH_PERIOD,
        			period, null);
        return TimeUnit.SECONDS.toMillis(period);
    }
	
}
