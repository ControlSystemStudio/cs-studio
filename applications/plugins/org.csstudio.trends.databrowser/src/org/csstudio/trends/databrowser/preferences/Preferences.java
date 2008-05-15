package org.csstudio.trends.databrowser.preferences;

import java.util.ArrayList;
import java.util.StringTokenizer;

import org.csstudio.platform.model.CentralItemFactory;
import org.csstudio.platform.model.IArchiveDataSource;
import org.csstudio.trends.databrowser.Plugin;
import org.eclipse.jface.preference.IPreferenceStore;

/** Data Browser preferences.
 *  <p>
 *  The list of archive server URLs is stored as one string with
 *  separators for the individual URLs.
 *  <p>
 *  The default archive data sources are such a list,
 *  but each item in turn contains name/key/url.
 *  
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class Preferences
{
    /** Separator for the list of items.
     *  <p>
     *  Obviously, the separator must not be part of a valid URL.
     *  Might have to change this one...
     */
    private static final String LIST_SEPARATOR = "*";
    
    /** Separator for the pieces of an archive data source. */
    private static final char ARCHIVE_SEPARATOR = '|';

    /** Identifier for start time preference. */
    public static final String P_START_TIME_SPEC = "start_time";
    
    /** Identifier for end time preference. */
    public static final String P_END_TIME_SPEC = "end_time";

    /** Identifier for the auto-scale preference. */
    public static final String P_AUTOSCALE = "autoscale";

    /** Identifier for the plot bin preference. */
    public static final String P_PLOT_BINS = "plot_bins";
    
    /** Identifier for the show-request-types preference. */
    public static final String P_SHOW_REQUEST_TYPES = "show_request_types";

    /** Identifier for the Archive Server URLs preference. */
    public static final String P_URLS = "urls";

    /** Identifier for the Archives preference. */
    public static final String P_ARCHIVES = "archives";
    
    /** Identifier if the 'SampleFileImportAction' should be shown in the 
     * Config View.*/
    public static final String SHOW_SAMPLE_FILE_IMPORT_ACTION = "show_sample_file_import_action";
    
    /** @return Default start time specification. */
    static public String getStartSpecification()
    {
        IPreferenceStore store = Plugin.getDefault().getPreferenceStore();
        return store.getString(P_START_TIME_SPEC);
    }

    /** @return Default end time specification. */
    static public String getEndSpecification()
    {
        IPreferenceStore store = Plugin.getDefault().getPreferenceStore();
        return store.getString(P_END_TIME_SPEC);
    }
    
    /** @return Default auto-scale setting */
    static public boolean getAutoScale()
    {
        IPreferenceStore store = Plugin.getDefault().getPreferenceStore();
        return store.getBoolean(P_AUTOSCALE);
    }

    static public int getPlotBins()
    {
        IPreferenceStore store = Plugin.getDefault().getPreferenceStore();
        return store.getInt(P_PLOT_BINS);
    }
    
    /** @return Default auto-scale setting */
    static public boolean getShowRequestTypes()
    {
        IPreferenceStore store = Plugin.getDefault().getPreferenceStore();
        return store.getBoolean(P_SHOW_REQUEST_TYPES);
    }

    /** @return Default setting for 'show sample file import action'. */
    static public boolean getShowSampleFileImportAction()
    {
        IPreferenceStore store = Plugin.getDefault().getPreferenceStore();
        return store.getBoolean(SHOW_SAMPLE_FILE_IMPORT_ACTION);
    }

    
    /** @return Default archive server URLs. */
    static public String[] getArchiveServerURLs()
    {
        IPreferenceStore store = Plugin.getDefault().getPreferenceStore();
        String concat = store.getString(P_URLS);
        return splitListItems(concat);
    }

    /** @return Default archive data sources. */
    static public IArchiveDataSource[] getArchiveDataSources()
    {
        IPreferenceStore store = Plugin.getDefault().getPreferenceStore();
        String concat = store.getString(P_ARCHIVES);
        String encoded_sources[] = splitListItems(concat);
        IArchiveDataSource sources[] = new IArchiveDataSource[encoded_sources.length];
        for (int i = 0; i < sources.length; ++i)
            sources[i] = parseArchiveDataSource(encoded_sources[i]);
        return sources;
    }
    
    /** Concatenate list of items into one String.
     *  @see #splitListItems(String)
     */
    static String concatListItems(String[] urls)
    {
        StringBuffer concat = new StringBuffer();
        for (int i = 0; i < urls.length; i++)
        {
            concat.append(urls[i]);
            concat.append(LIST_SEPARATOR);
        }
        return concat.toString();
    }

    /** Split List items from string.
     *  @see #concatListItems(String[])
     */
    static String[] splitListItems(String concat_URLs)
    {
        StringTokenizer st = new StringTokenizer(concat_URLs, LIST_SEPARATOR);
        ArrayList<String> urls = new ArrayList<String>();
        while (st.hasMoreElements())
            urls.add((String)st.nextElement());
        return urls.toArray(new String[urls.size()]);
    }
    
    /** Concatenate the pieces of an archive data source into one String.
     *  @see #parseArchiveDataSource(String)
     */
    static String encodeArchiveDataSource(String url, String name, int key)
    {
        return name + ARCHIVE_SEPARATOR + key + ARCHIVE_SEPARATOR + url;
    }

    /** Parse an archive data source from a concatenated string.
     *  @see #encodeArchiveDataSource(String, String, int)
     */
    static IArchiveDataSource parseArchiveDataSource(String text)
    {
        text = text.trim();
        int i1 = text.indexOf(ARCHIVE_SEPARATOR);
        if (i1 < 0)
            return null;
        String name = text.substring(0, i1);
        int i2 = text.indexOf(ARCHIVE_SEPARATOR, i1+1);
        if (i2 < 1) // need some minumum length
            return null;
        int key;
        try
        {
            key = Integer.parseInt(text.substring(i1+1, i2));
        }
        catch (Exception e)
        {
            return null;
        }
        String url = text.substring(i2 + 1);
        if (url.length() < 1)
            return null;
        return CentralItemFactory.createArchiveDataSource(url, key, name);
    }
}   
