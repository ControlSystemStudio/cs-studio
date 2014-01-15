/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.databrowser2.preferences;

import java.util.ArrayList;
import java.util.logging.Level;

import org.csstudio.trends.databrowser2.Activator;
import org.csstudio.trends.databrowser2.model.ArchiveDataSource;
import org.csstudio.trends.databrowser2.model.ArchiveRescale;
import org.csstudio.trends.databrowser2.model.TraceType;
import org.csstudio.utility.singlesource.SingleSourcePlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;

/** Helper for reading preference settings
 *
 *  @author Kay Kasemir
 *  @author Naceur Benhadj (add property to hide "Property" view)
 */
@SuppressWarnings("nls")
public class Preferences
{
    /** Regular expression for separator between list items */
    static final String ITEM_SEPARATOR_RE = "\\*";

    /** Regular expression for separator between components within an item */
    static final String COMPONENT_SEPARATOR_RE = "\\|";

    /** Separator between list items */
    static final String ITEM_SEPARATOR = "*";

    /** Separator between components within an item */
    static final String COMPONENT_SEPARATOR = "|";
    
	public static final String PLT_REPOSITORY = "plt_repository"; //$NON-NLS-1$

    /** Preference tags.
     *  For explanation of the settings see preferences.ini
     */
	final public static String TIME_SPAN = "time_span",
			SCAN_PERIOD = "scan_period", BUFFER_SIZE = "live_buffer_size",
			UPDATE_PERIOD = "update_period", LINE_WIDTH = "line_width",
			TRACE_TYPE = "trace_type",
			ARCHIVE_FETCH_DELAY = "archive_fetch_delay",
			PLOT_BINS = "plot_bins", URLS = "urls", ARCHIVES = "archives",
			USE_DEFAULT_ARCHIVES = "use_default_archives",
			PROMPT_FOR_ERRORS = "prompt_for_errors",
			ARCHIVE_RESCALE = "archive_rescale",
			USE_AUTO_SCALE = "use_auto_scale",
			EMAIL_DEFAULT_SENDER = "email_default_sender",
			RAP_HIDE_SEARCH_VIEW = "rap.hide_search_view",
			RAP_HIDE_PROPERTIES_VIEW = "rap.hide_properties_view",		
			SECURE_DATA_BROWSER = "secure_data_browser";

    public static double getTimeSpan()
    {
        final IPreferencesService prefs = Platform.getPreferencesService();
        if (prefs == null) // Allow some JUnit tests without prefs
            return 60.0;
        return prefs.getDouble(Activator.PLUGIN_ID, TIME_SPAN, 60.0*60.0, null);
    }

    public static double getScanPeriod()
    {
        final IPreferencesService prefs = Platform.getPreferencesService();
        return prefs.getDouble(Activator.PLUGIN_ID, SCAN_PERIOD, 1.0, null);
    }

    public static int getLiveSampleBufferSize()
    {
        final IPreferencesService prefs = Platform.getPreferencesService();
        if (prefs == null) // Allow some JUnit tests without prefs
            return 5000;
        return prefs.getInt(Activator.PLUGIN_ID, BUFFER_SIZE, 5000, null);
    }

    public static double getUpdatePeriod()
    {
        final IPreferencesService prefs = Platform.getPreferencesService();
        if (prefs == null) // Allow some JUnit tests without prefs
            return 1.0;
        return prefs.getDouble(Activator.PLUGIN_ID, UPDATE_PERIOD, 1.0, null);
    }

    public static int getLineWidths()
    {
        final IPreferencesService prefs = Platform.getPreferencesService();
        if (prefs == null)
            return 2;
        return prefs.getInt(Activator.PLUGIN_ID, LINE_WIDTH, 2, null);
    }

    public static TraceType getTraceType()
    {
        final IPreferencesService prefs = Platform.getPreferencesService();
        if (prefs != null)
        {
            final String type_name = prefs.getString(Activator.PLUGIN_ID, TRACE_TYPE, TraceType.AREA.name(), null);
            try
            {
                return TraceType.valueOf(type_name);
            }
            catch (Exception ex)
            {
                Activator.getLogger().log(Level.WARNING, "Undefined trace type option '" + type_name + "'", ex);
            }
        }
        return TraceType.AREA;
    }

    public static long getArchiveFetchDelay()
    {
        final IPreferencesService prefs = Platform.getPreferencesService();
        return prefs.getLong(Activator.PLUGIN_ID, ARCHIVE_FETCH_DELAY, 1000, null);
    }

    public static int getPlotBins()
    {
        final IPreferencesService prefs = Platform.getPreferencesService();
        return prefs.getInt(Activator.PLUGIN_ID, PLOT_BINS, 800, null);
    }

    public static ArchiveServerURL[] getArchiveServerURLs()
    {
        final IPreferencesService prefs = Platform.getPreferencesService();
        final String urls = prefs.getString(Activator.PLUGIN_ID, URLS, "", null).trim();
        if (urls.length() <= 0)
            return new ArchiveServerURL[0];
        
        ArrayList<ArchiveServerURL> list = new ArrayList<ArchiveServerURL>(); 
        for (String fragment : urls.split("\\*")) {
        	String[] strs = fragment.split("\\|");
        	if (strs.length == 1) {
        		list.add(new ArchiveServerURL(strs[0], null));
        	} else if (strs.length >= 2) {
        		list.add(new ArchiveServerURL(strs[0], strs[1]));
        	}
        }
        return list.toArray(new ArchiveServerURL[list.size()]);
    }

    public static ArchiveDataSource[] getArchives()
    {
        final ArrayList<ArchiveDataSource> archives = new ArrayList<ArchiveDataSource>();
        final IPreferencesService prefs = Platform.getPreferencesService();
        final String urls = prefs.getString(Activator.PLUGIN_ID, ARCHIVES, "", null);
        // data source specs are separated by '*'
        final String specs[] = urls.split(ITEM_SEPARATOR_RE);
        for (String spec : specs)
        {
            // Each spec is "<name>|<key>|<url>"
            if (spec.length() <= 0)
                continue;
            try
            {
                final String segs[] = spec.split(COMPONENT_SEPARATOR_RE);
                final String name = segs[0];
                final int key = Integer.parseInt(segs[1]);
                final String url = segs[2];
                archives.add(new ArchiveDataSource(url, key, name));
            }
            catch (Throwable ex)
            {
                throw new Error("Error in archive preference '" + spec + "'");
            }
        }
        return archives.toArray(new ArchiveDataSource[archives.size()]);
    }

    /** @return <code>true</code> to use default archives,
     *          ignoring data sources from config file
     */
    static public boolean useDefaultArchives()
    {
        final IPreferencesService prefs = Platform.getPreferencesService();
        if (prefs == null)
            return false;
        return prefs.getBoolean(Activator.PLUGIN_ID, USE_DEFAULT_ARCHIVES, false, null);
    }
    
    /** @return <code>true</code> to use auto scale by default.
     */
    static public boolean useAutoScale()
    {
		final IPreferencesService prefs = Platform.getPreferencesService();
		if (prefs == null)
			return false;
		return prefs.getBoolean(Activator.PLUGIN_ID, USE_AUTO_SCALE, false, null);
    }

    /** @return <code>true</code> to prompt for errors */
    static public boolean doPromptForErrors()
    {
        final IPreferencesService prefs = Platform.getPreferencesService();
        if (prefs == null)
            return false;
        return prefs.getBoolean(Activator.PLUGIN_ID, PROMPT_FOR_ERRORS, false, null);
    }

    /** @return Archive rescale setting */
    static public ArchiveRescale getArchiveRescale()
    {
        final IPreferencesService prefs = Platform.getPreferencesService();
        if (prefs == null)
            return ArchiveRescale.STAGGER;
        try
        {
            return ArchiveRescale.valueOf(
                    prefs.getString(Activator.PLUGIN_ID, ARCHIVE_RESCALE,
                                    ArchiveRescale.STAGGER.name(), null));
        }
        catch (Throwable ex)
        {
            Activator.getLogger().log(Level.WARNING, "Undefined rescale option", ex);
        }
        return ArchiveRescale.STAGGER;
	}

	public static IPath getPltRepository() {
		final IPreferencesService prefs = Platform.getPreferencesService();
		if (prefs == null)
			return null;
		String pltRepo = prefs.getString(Activator.PLUGIN_ID, PLT_REPOSITORY,
				null, null);
		if (pltRepo == null || pltRepo.trim().isEmpty())
			return null;
		return SingleSourcePlugin.getResourceHelper().newPath(pltRepo);
	}
	
	public static String getEmailDefaultSender() {
		final IPreferencesService prefs = Platform.getPreferencesService();
		if (prefs == null)
			return null;
		return prefs.getString(Activator.PLUGIN_ID, EMAIL_DEFAULT_SENDER, null, null);
	}
    
    /** @return <code>true</code> to hide search view on rap version.
     */
    public static boolean hideSearchView()
    {
		final IPreferencesService prefs = Platform.getPreferencesService();
		if (prefs == null)
			return false;
		return prefs.getBoolean(Activator.PLUGIN_ID, RAP_HIDE_SEARCH_VIEW, false, null);
    }
    
    

    /** @return <code>true</code> to hide properties view on rap version.
     */
    public static boolean hidePropertiesView()
    {
		final IPreferencesService prefs = Platform.getPreferencesService();
		if (prefs == null)
			return false;
		return prefs.getBoolean(Activator.PLUGIN_ID, RAP_HIDE_PROPERTIES_VIEW, false, null);
    }
    
    /** @return <code>true</code> to authentication is required to open data browser in rap.
     */
    public static boolean isDataBrowserSecured()
    {
		final IPreferencesService prefs = Platform.getPreferencesService();
		if (prefs == null)
			return false;
		return prefs.getBoolean(Activator.PLUGIN_ID, SECURE_DATA_BROWSER, false, null);
    }
    
    
}
