package org.csstudio.platform;

import java.util.ArrayList;
import java.util.Collection;

import org.csstudio.platform.logging.CentralLogger;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

/**
 * An entry of the system property preferences.
 * 
 * @author Joerg Rathlev
 */
public final class SystemPropertyPreferenceEntry {
	
	/**
	 * The node in the preferences below which the entries are stored.
	 */
	private static final String PREFERENCE_NODE = "systemProperties";
	
	/**
	 * The preference key.
	 */
	private String _key;
	
	/**
	 * The preference value.
	 */
	private String _value;
	
	/**
	 * Creates a new system property preference entry.
	 * @param key the property key.
	 * @param value the value.
	 */
	public SystemPropertyPreferenceEntry(final String key, final String value) {
		_key = key;
		_value = value;
	}
	
	/**
	 * Returns the preference key.
	 * @return the preference key.
	 */
	public String getKey() {
		return _key;
	}
	
	/**
	 * Returns the preference value.
	 * @return the preference value.
	 */
	public String getValue() {
		return _value;
	}

	/**
	 * Sets the key.
	 * @param key the key.
	 */
	public void setKey(final String key) {
		_key = key;
	}
	
	/**
	 * Sets the value.
	 * @param value the value.
	 */
	public void setValue(final String value) {
		_value = value;
	}
	
	/**
	 * Returns a string representation of this entry.
	 * @return a string representation of this entry.
	 */
	@Override
	public String toString() {
		return _key + "=" + _value;
	}
	
	/**
	 * Loads the system property defaults from the preferences. 
	 * @return the system property defaults.
	 */
	public static Collection<SystemPropertyPreferenceEntry> loadFromPreferences() {
		Collection<SystemPropertyPreferenceEntry> result =
			new ArrayList<SystemPropertyPreferenceEntry>();
        IEclipsePreferences platformPrefs = getPlatformPreferences();
        Preferences systemPropertyPrefs;
        try {
            if (platformPrefs.nodeExists(PREFERENCE_NODE)) {
            	systemPropertyPrefs =
            		platformPrefs.node(PREFERENCE_NODE);
            } else {
            	systemPropertyPrefs =
            		getDefaultPlatformPreferences().node(PREFERENCE_NODE);
            }
            String[] keys = systemPropertyPrefs.keys();
            for (String key : keys) {
                String value = systemPropertyPrefs.get(key, "");
                SystemPropertyPreferenceEntry entry =
                    new SystemPropertyPreferenceEntry(key, value);
                result.add(entry);
            }
        } catch (BackingStoreException e) {
            CentralLogger.getInstance().error(SystemPropertyPreferenceEntry.class,
            		"Error reading preferences", e);
        }
        return result;
	}
	
    /**
     * Stores the system property defaults in the preferences. Any existing
     * entries will be overwritten.
     * @param entries the entries to store.
     */
	public static void storeToPreferences(final Collection<SystemPropertyPreferenceEntry> entries) {
        IEclipsePreferences platformPrefs = getPlatformPreferences();
        Preferences systemPropertyPrefs =
            platformPrefs.node(PREFERENCE_NODE);
        // first, remove all of the existing entries
        try {
            systemPropertyPrefs.clear();
        } catch (BackingStoreException e) {
            CentralLogger.getInstance().warn(SystemPropertyPreferenceEntry.class,
            		"Error clearing preference node", e);
        }
        // now write the new values into the node
        for (SystemPropertyPreferenceEntry entry : entries) {
            systemPropertyPrefs.put(entry.getKey(), entry.getValue());
        }
    }

	/**
	 * Returns the preferences node that contains the platform preferences.
	 * @return the platform prefernces.
	 */
    private static IEclipsePreferences getPlatformPreferences() {
        return new InstanceScope().getNode(
                CSSPlatformPlugin.getDefault().getBundle().getSymbolicName());
    }
    
    /**
     * Returns the preferences node that contains the defaults of the platform
     * preferences.
     * @return the platform preference defaults.
     */
    private static IEclipsePreferences getDefaultPlatformPreferences() {
    	return new DefaultScope().getNode(
    			CSSPlatformPlugin.getDefault().getBundle().getSymbolicName());
    }
}
