package org.csstudio.alarm.treeView.preferences;

import org.csstudio.alarm.treeView.LdaptreePlugin;
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
		IPreferenceStore store = LdaptreePlugin.getDefault()
				.getPreferenceStore();
		store.setDefault(PreferenceConstants.PROTOCOL, "epics");
		store.setDefault(PreferenceConstants.URL, "ldap://krynfs.desy.de:389/o=DESY,c=DE");
		store.setDefault(PreferenceConstants.JMSURL, "rmi://krynfs.desy.de:1099/");
		store.setDefault(PreferenceConstants.JMSTOPIC, "ALARM");
		store.setDefault(PreferenceConstants.USER, "");
		store.setDefault(PreferenceConstants.PASSWORD, "");
		store.setDefault(PreferenceConstants.NODE, "Test");
	}

}
