package org.csstudio.platform.libs.jms.preferences;

import org.csstudio.platform.libs.jms.JmsPlugin;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;


public class PreferenceInitializer extends AbstractPreferenceInitializer {

	public void initializeDefaultPreferences() {
		IPreferenceStore store = JmsPlugin.getDefault().getPreferenceStore();
		store.setDefault(PreferenceConstants.INITIAL_CONTEXT_FACTORY, "org.exolab.jms.jndi.InitialContextFactory");
		store.setDefault(PreferenceConstants.URL, "rmi://krykelog.desy.de:1099/");
		store.setDefault(PreferenceConstants.QUEUE, "LOG");
		
	}

}
