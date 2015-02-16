package org.csstudio.platform.internal.jassauthentication.preference;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.java.string.StringSplitter;
import org.csstudio.platform.internal.jaasauthentication.Activator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.core.runtime.preferences.IPreferencesService;


/** Names of preferences
 *  @author Xihui Chen
 */
@SuppressWarnings("nls")
public class PreferencesHelper
{
	public static final String JAAS_CONFIG_SOURCE = "jaas_config_source"; //$NON-NLS-1$
	public static final String JAAS_CONFIG_FILE_ENTRY = "jaas_config_file_entry"; //$NON-NLS-1$
	public static final String JAAS_PREFS_CONFIG = "jaas_prefs_config"; //$NON-NLS-1$

	 /** @param setting Preference identifier
     *  @return String from preference system, or <code>null</code>
     */
    private static String getString(final String setting)
    {
        final IPreferencesService service = Platform.getPreferencesService();
        return service.getString(Activator.PLUGIN_ID, setting, null, null);
    }

    private static String getDefaultString(final String setting) {
    	return Activator.getDefault().getPluginPreferences().getDefaultString(setting);
    }

    public static String getConfigSource() {
    	return getString(JAAS_CONFIG_SOURCE);
	}

     public static String getDefaultConfigSource() {
    	return getDefaultString(JAAS_CONFIG_SOURCE);
	}

    public static String getConfigFileEntry() {
    	return getString(JAAS_CONFIG_FILE_ENTRY);
    }

    public static String getDefaultConfigFileEntry() {
    	return getDefaultString(JAAS_CONFIG_FILE_ENTRY);
    }
    private static String getPreferenceConfigurationString(boolean defaultValue) {
    	if(defaultValue)
    		return getDefaultString(JAAS_PREFS_CONFIG);
    	return getString(JAAS_PREFS_CONFIG);
    }

    /**
     * @param deaultValue load the default preference value if true;
     * @return an array of JAASConfigurationEntry
     */
    public static JAASConfigurationEntry[] getJAASConfigurationEntries(boolean defaultValue) {
    	List<JAASConfigurationEntry> configEntryList = new ArrayList<JAASConfigurationEntry>();
    	String prefString = getPreferenceConfigurationString(defaultValue);
    	String[] configEntryStringArray = null;
    	try {
			configEntryStringArray =
				StringSplitter.splitIgnoreInQuotes(prefString, ';', false);
		} catch (Exception e) {
			Logger.getLogger(PreferencesHelper.class.getName()).log(Level.WARNING, "Error in " + prefString, e);
			return null;
		}
		for(String entryString : configEntryStringArray) {
			try {
				String[] entryElements = StringSplitter.splitIgnoreInQuotes(
						entryString, '|', false);
				JAASConfigurationEntry configEntry = new JAASConfigurationEntry();
				configEntry.setLoginModuleName(entryElements[0]);
				configEntry.setModuleControlFlag(entryElements[1]);

				String[] subArray = new String[entryElements.length-2];
				for(int i = 0; i<subArray.length; i++)
					subArray[i] = entryElements[i+2];
				configEntry.setModuleOptionsList(parseOptions(subArray));

				configEntryList.add(configEntry);
			} catch (Exception e) {
	            Logger.getLogger(PreferencesHelper.class.getName()).log(Level.WARNING, "Error in " + entryString, e);
				return null;
			}
		}
		return configEntryList.toArray(new JAASConfigurationEntry[configEntryList.size()]);
    }

    private static List<String[]> parseOptions(String[] options) throws Exception {
    	List<String[]> result = new ArrayList<String[]>();
    	for(String option : options) {
			String[] optionTuple = StringSplitter.splitIgnoreInQuotes(option, '=', true);
			result.add(optionTuple);
    	}
    	return result;
    }

    /**
     * Stores the values back to the preference store.
     */
    public static void storeValues(final String configSource, final String configFileEntry) {
    	final Preferences preferences = Activator.getDefault().getPluginPreferences();
    	preferences.setValue(JAAS_CONFIG_SOURCE, configSource);
    	preferences.setValue(JAAS_CONFIG_FILE_ENTRY, configFileEntry);
    	//convert configurationEntryList into String and save it
    	StringBuilder result = new StringBuilder(""); //$NON-NLS-1$
    	for(JAASConfigurationEntry je : JAASPreferenceModel.configurationEntryList) {
    		if(je.getLoginModuleName() == null || je.getLoginModuleName().trim().equals("")) //$NON-NLS-1$
    			continue;
    		result.append(je.getLoginModuleName());
    		result.append('|');
    		result.append(je.getModuleControlFlag());
    		for(String[] optionTuple : je.getModuleOptionsList()) {
    			//don't save the value whose option name is invalid
    			if(optionTuple[0].trim().equals(""))
    				continue;
    			result.append('|');
    			result.append(optionTuple[0] + "=" + "\"" + optionTuple[1] + "\""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    		}
    		result.append(';');
    	}
    	preferences.setValue(JAAS_PREFS_CONFIG, result.toString());
    	Activator.getDefault().savePluginPreferences();

    }

}
