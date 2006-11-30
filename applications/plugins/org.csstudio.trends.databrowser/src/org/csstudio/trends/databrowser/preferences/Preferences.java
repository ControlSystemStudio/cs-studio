package org.csstudio.trends.databrowser.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import org.csstudio.trends.databrowser.Plugin;

/** Application-wide preferences.
 *  @author Kay Kasemir
 */
public class Preferences extends AbstractPreferenceInitializer
{
    /** Identifier for one of the Archive Server URL preferences. */
    public static final String P_URL1 = "url1"; //$NON-NLS-1$
    /** Identifier for one of the Archive Server URL preferences. */
    public static final String P_URL2 = "url2"; //$NON-NLS-1$
    /** Identifier for one of the Archive Server URL preferences. */
    public static final String P_URL3 = "url3"; //$NON-NLS-1$

    // Is there a way to get a list of URLs into the prefs?
    // All the IPreferenceStore methods seem to deal with individual
    // double/int/bool/string values, no way to store a String[].
    
    /** Get the default values for all preferences.
     *  @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
     */
    public void initializeDefaultPreferences()
    {
        IPreferenceStore store = Plugin.getDefault().getPreferenceStore();
        store.setDefault(P_URL1, Messages.Default_URL1);
        store.setDefault(P_URL2, Messages.Default_URL2);
        store.setDefault(P_URL3, Messages.Default_URL3);
    }
    
    /** @return The number of available URLs.
     *  @see #getURL(int)
     */
    static public int getNumURLs()
    {
        return 3;
    }

    /** @return Setting for archive server URL 0, 1, 2, ... or empty string.
     *  @see #getNumURLs()
     */
    static public String getURL(int i)
    {
        IPreferenceStore store = Plugin.getDefault().getPreferenceStore();
        switch (i)
        {
        case 0: return store.getString(P_URL1);
        case 1: return store.getString(P_URL2);
        case 2: return store.getString(P_URL3);
        }
        return ""; //$NON-NLS-1$
    }
}   
