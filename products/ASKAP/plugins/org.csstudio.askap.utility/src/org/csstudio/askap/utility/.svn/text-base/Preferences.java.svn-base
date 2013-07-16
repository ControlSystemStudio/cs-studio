package org.csstudio.askap.utility;

import org.csstudio.askap.utility.AskapHelper.DateTimeFormat;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;


public class Preferences {

	public static String NAVIGATOR_CONFIGURATION_FILE = "navigator_config_file";
	public static String ASKAP_OPI_RELEASE_LOCATION = "opi_file_directory";
	public static final String DATE_TIME_FORMAT = "date_time_format";

	
    public static String getNavigatorConfigFile(){
    	return getString(NAVIGATOR_CONFIGURATION_FILE, "");
    }

    public static String getOPIDirectory(){
    	return getString(ASKAP_OPI_RELEASE_LOCATION, "");
    }
    
    
    
	public static DateTimeFormat getDateTimeFormat() {
		String format = getString(DATE_TIME_FORMAT, "LOCAL");
		if (format.equals("LOCAL"))
			return DateTimeFormat.LOCAL;
		
		return DateTimeFormat.UTC;
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
        return prefs.getString(Activator.PLUGIN_ID, key, default_value, null);
    }
    

}
