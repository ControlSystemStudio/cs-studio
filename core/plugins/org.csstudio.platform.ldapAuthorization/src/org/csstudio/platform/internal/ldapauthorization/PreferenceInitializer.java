package org.csstudio.platform.internal.ldapauthorization;

import org.eclipse.core.runtime.Preferences;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;

/**
 * Intializes the preferences for this plug-in.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

	@Override
	public void initializeDefaultPreferences() {
		Preferences prefs = Activator.getDefault().getPluginPreferences();
		prefs.setDefault(PreferenceConstants.LDAP_URL, "ldap://krynfs.desy.de:389/o=DESY,c=DE");
		prefs.setDefault(PreferenceConstants.LDAP_USER, "");
		prefs.setDefault(PreferenceConstants.LDAP_PASSWORD, "");
	}

}
