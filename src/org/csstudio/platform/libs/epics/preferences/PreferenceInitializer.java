package org.csstudio.platform.libs.epics.preferences;

import org.csstudio.platform.libs.epics.EpicsPlugin;
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
		IPreferenceStore store = EpicsPlugin.getDefault().getPreferenceStore();
		for(int i = 0; i < PreferenceConstants.constants.length; i++){
			store.setDefault(PreferenceConstants.constants[i], PreferenceConstants.defaults[i]);
		}
		
		
	}

}
