package org.csstudio.opibuilder.widgets.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
/**
 * utility class to get url from databrowser2 preference
 * @author lamberm (Sopra)
 *
 */
public class DataSourceUrl {
	/**preference id of databrowser2*/
	private static final String QUALIFIER_DATABROWSER2_ID = "org.csstudio.trends.databrowser2";
	/**key of databrowser2*/
	private static final String QUALIFIER_DATABROWSER2_KEY_URL = "archives";
	/**default url*/
	private static final String DEFAULT_URL_DATASOURCE = "jdbc:postgresql://localhost/css_archive_3_0_0";
	/**
	 * get the default url from preferences of org.csstudio.trends.databrowser2's service
	 * @return a jdbc url
	 */
	public static String getDefaultURL() {
		List < String > list = getURLs();
		if (list != null && list.size() > 0) {
			return list.get(0);
		}
		return DEFAULT_URL_DATASOURCE;
	}
	/**
	 * return the list of url order by key
	 * @return
	 */
	public static List < String > getURLs() {
		final IPreferencesService prefs = Platform.getPreferencesService();
		List < String > list = null;
		String urlAll = "";
		if (prefs != null) {
        	urlAll = prefs.getString(QUALIFIER_DATABROWSER2_ID, QUALIFIER_DATABROWSER2_KEY_URL,
        			DEFAULT_URL_DATASOURCE, null);
        }
		if (urlAll != null) {
			Map < String, String > map = new TreeMap < String, String > (new Comparator < String >() {
				@Override
				public int compare(String arg0, String arg1) {
					return Integer.parseInt(arg0) - Integer.parseInt(arg1);
				}
			});
			String[] urlAllArray = urlAll.split("\\*");
			for (int i = 0; i < urlAllArray.length; i++) {
				String urlRow = urlAllArray[i];
				if (urlRow != null && urlRow.length() > 0 && urlRow.indexOf("|") > 0) {
					String[] col = urlRow.split("\\|");
					if (col != null && col.length >= 3) {
						String URL = col[2];
						String key = col[1];
						map.put(key, URL);
					}
				}
			}
			list = new ArrayList<String>(map.values());
		}
		return list;
	}
}
