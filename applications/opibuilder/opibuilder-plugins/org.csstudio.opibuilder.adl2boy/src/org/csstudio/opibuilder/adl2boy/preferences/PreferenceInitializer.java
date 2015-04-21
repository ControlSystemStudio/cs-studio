package org.csstudio.opibuilder.adl2boy.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import org.csstudio.opibuilder.adl2boy.ADL2BOYPlugin;

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
		IPreferenceStore store = ADL2BOYPlugin.getDefault().getPreferenceStore();
		store.setDefault(PreferenceConstants.P_COLOR_PREFIX, "MEDM_COLOR_");
		store.setDefault(PreferenceConstants.P_FONT_PREFIX, "MEDM_FONT_");
	}

}
