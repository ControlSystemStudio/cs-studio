package org.csstudio.utility.ldap.preference;

import org.csstudio.utility.ldap.Activator;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * Class used to initialize default preference values.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	public void initializeDefaultPreferences() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		store.setDefault(PreferenceConstants.P_STRING_URL,
				"ldap://krykpcr.desy.de:389/o=DESY,c=DE");
		store.setDefault(PreferenceConstants.P_STRING_USER_DN,
		"cn=Manager,o=DESY,c=DE");
		store.setDefault(PreferenceConstants.P_STRING_USER_PASSWORD,"desy");
	}

}
