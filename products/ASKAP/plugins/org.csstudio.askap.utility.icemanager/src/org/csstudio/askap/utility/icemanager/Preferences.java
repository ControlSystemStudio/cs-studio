package org.csstudio.askap.utility.icemanager;


import java.util.Hashtable;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;

/**
 * 
 * @author Xinyu Wu
 */
public class Preferences {
	
    public static final String ICE_PROPERTIES = "ice_properties";
    public static final String ICESTORM_TOPICMANAGER_NAME = "icestorm_topicmanager_name";


    public static String getIceStormTopicManagerName(){
    	return getString(ICESTORM_TOPICMANAGER_NAME, "IceStorm/TopicManager@IceStorm.TopicManager");
    }

    /** Get Ice Properties
     * 
     * @return Map<name,value>
     */
    
    public static Map<String,String> getIceProperties(){
    	Hashtable<String,String> map = new Hashtable<String,String>();
    	String str = getString(ICE_PROPERTIES, "");
    	Pattern p = Pattern.compile("\"(.*)\"\\s*,\\s*\"(.*)\"");
    	String[] pairs = str.split("\\|");
    	for (int i=0; i < pairs.length; ++i) {
    		Matcher m = p.matcher(pairs[i].trim());
    		if (m.matches()) {
    			map.put(m.group(1), m.group(2));
    		}
    	}
    	return map;
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
