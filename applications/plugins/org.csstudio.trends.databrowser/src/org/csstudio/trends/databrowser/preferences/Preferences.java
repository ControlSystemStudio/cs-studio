package org.csstudio.trends.databrowser.preferences;

import java.util.ArrayList;
import java.util.StringTokenizer;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import org.csstudio.trends.databrowser.Plugin;

/** Application-wide preferences.
 *  @author Kay Kasemir
 */
public class Preferences extends AbstractPreferenceInitializer
{
    private static final String SEPARATOR = "!"; //$NON-NLS-1$

    /** Identifier for one of the Archive Server URL preferences. */
    public static final String P_URLS = "urls"; //$NON-NLS-1$

    // Is there a way to get a list of URLs into the prefs?
    // All the IPreferenceStore methods seem to deal with individual
    // double/int/bool/string values, no way to store a String[].
    
    /** Get the default values for all preferences.
     *  @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
     */
    public void initializeDefaultPreferences()
    {
        IPreferenceStore store = Plugin.getDefault().getPreferenceStore();
        store.setDefault(P_URLS, Messages.Default_URLS);
    }
    
    /** @return Setting for archive server URL 0, 1, 2, ... or empty string.
     *  @see #getNumURLs()
     */
    static public String[] getURLs()
    {
        IPreferenceStore store = Plugin.getDefault().getPreferenceStore();
        String concat = store.getString(P_URLS);
        return splitURLs(concat);
    }
    
    /** Concatenate list of URLs into one String.
     *  @see #splitURLs(String)
     */
    public static String concatURLs(String[] urls)
    {
        StringBuffer concat = new StringBuffer();
        for (int i = 0; i < urls.length; i++)
        {
            concat.append(urls[i]);
            concat.append(SEPARATOR);
        }
        return concat.toString();
    }

    /** Split URLs from string.
     *  @see #concatURLs(String[])
     */
    public static String[] splitURLs(String concat_URLs)
    {
        StringTokenizer st = new StringTokenizer(concat_URLs, SEPARATOR);
        ArrayList<String> urls = new ArrayList<String>();
        while (st.hasMoreElements())
            urls.add((String)st.nextElement());
        return (String[]) urls.toArray(new String[urls.size()]);
    }
}   
