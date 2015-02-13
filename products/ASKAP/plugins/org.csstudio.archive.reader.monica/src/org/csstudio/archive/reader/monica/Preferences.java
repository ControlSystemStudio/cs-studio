package org.csstudio.archive.reader.monica;

import java.util.Properties;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;

/**
 * Access to Monica Archive Data Browser preferences
 * 
 * @author Xinyu Wu
 */
@SuppressWarnings("nls")
public class Preferences {
	private static final Logger logger = Logger.getLogger(Preferences.class.getName());
	
	public static final String ICEMANAGER_ID = "org.csstudio.askap.utility.icemanager";
    public static final String ICE_PROPERTIES = "ice_properties";


	final public static String ADAPTORNAME = "adaptorName"; //$NON-NLS-1$
	
	public static String getAdaptorName() {
		return getString(ADAPTORNAME, "");
	}
	
	public static Properties getIceProperties() throws Exception {
		final IPreferencesService prefs = Platform.getPreferencesService();
        Properties prop = new Properties();
        
        if (prefs == null)
            return prop;

        String iceConfig = prefs.getString(ICEMANAGER_ID, ICE_PROPERTIES, "", null);
        
    	Pattern p = Pattern.compile("\"(.*)\"\\s*,\\s*\"(.*)\"");
    	String[] pairs = iceConfig.split("\\|");
    	for (int i=0; i < pairs.length; ++i) {
    		Matcher m = p.matcher(pairs[i].trim());
    		if (m.matches()) {
    			prop.put(m.group(1), m.group(2));
    		}
    	}
    	
    	return prop;
	}

    /** Get string preference
     *  @param key Preference key
     *  @return String or <code>null</code>
     */
    private static String getString(final String key, final String default_value)
    {
        final IPreferencesService prefs = Platform.getPreferencesService();
        if (prefs == null)
            return default_value;
        return prefs.getString(Activator.ID, key, default_value, null);
    }
}
