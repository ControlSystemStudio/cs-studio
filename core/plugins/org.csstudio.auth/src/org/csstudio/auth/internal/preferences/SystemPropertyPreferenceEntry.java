/* 
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron, 
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. 
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED 
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND 
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE 
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, 
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE 
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR 
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. 
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, 
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION, 
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS 
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY 
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
 package org.csstudio.auth.internal.preferences;

import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.auth.internal.AuthActivator;
import org.csstudio.auth.security.SecurityFacade;
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
	
	private static final Logger log = Logger.getLogger(SystemPropertyPreferenceEntry.class.getName());
	
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
        	log.log(Level.SEVERE, "Error reading preferences", e);
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
        	log.log(Level.WARNING, "Error clearing preference node", e);
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
                AuthActivator.ID);
    }
    
    /**
     * Returns the preferences node that contains the defaults of the platform
     * preferences.
     * @return the platform preference defaults.
     */
    private static IEclipsePreferences getDefaultPlatformPreferences() {
    	return new DefaultScope().getNode(
    			AuthActivator.ID);
    }
}
