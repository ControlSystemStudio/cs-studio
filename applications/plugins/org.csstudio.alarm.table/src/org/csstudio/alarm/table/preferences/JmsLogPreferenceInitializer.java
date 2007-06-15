package org.csstudio.alarm.table.preferences;

import org.csstudio.alarm.table.JmsLogsPlugin;
import org.csstudio.platform.libs.jms.preferences.PreferenceConstants;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;


/**
 * Class used to initialize default preference values.
 */
public class JmsLogPreferenceInitializer extends AbstractPreferenceInitializer {

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	public void initializeDefaultPreferences() {
		IPreferenceStore store = JmsLogsPlugin.getDefault().getPreferenceStore();
		store.setDefault(JmsLogPreferenceConstants.KEY0,"NO_ALARM");
		store.setDefault(JmsLogPreferenceConstants.KEY1,"MINOR");
		store.setDefault(JmsLogPreferenceConstants.KEY2,"MAJOR");
		store.setDefault(JmsLogPreferenceConstants.KEY3,"INVALID");
		store.setDefault(JmsLogPreferenceConstants.KEY4,"4");
		store.setDefault(JmsLogPreferenceConstants.KEY5,"5");
		store.setDefault(JmsLogPreferenceConstants.KEY6,"6");
		store.setDefault(JmsLogPreferenceConstants.KEY7,"7");
		store.setDefault(JmsLogPreferenceConstants.KEY8,"8");
		store.setDefault(JmsLogPreferenceConstants.KEY9,"9");
		store.setDefault(JmsLogPreferenceConstants.COLOR0,"96,251,123");
		store.setDefault(JmsLogPreferenceConstants.COLOR1,"251,215,11");
		store.setDefault(JmsLogPreferenceConstants.COLOR2,"252,7,14");
		store.setDefault(JmsLogPreferenceConstants.COLOR3,"191,191,191");
		store.setDefault(JmsLogPreferenceConstants.COLOR4,"201,201,201");
		store.setDefault(JmsLogPreferenceConstants.COLOR5,"201,201,201");
		store.setDefault(JmsLogPreferenceConstants.COLOR6,"201,201,201");
		store.setDefault(JmsLogPreferenceConstants.COLOR7,"201,201,201");
		store.setDefault(JmsLogPreferenceConstants.COLOR8,"201,201,201");
		store.setDefault(JmsLogPreferenceConstants.COLOR9,"201,201,201");
		store.setDefault(JmsLogPreferenceConstants.VALUE0,"NO_ALARM");
		store.setDefault(JmsLogPreferenceConstants.VALUE1,"MINOR");
		store.setDefault(JmsLogPreferenceConstants.VALUE2,"MAJOR");
		store.setDefault(JmsLogPreferenceConstants.VALUE3,"INVALID");
		store.setDefault(JmsLogPreferenceConstants.VALUE4,"NOT DEFINED");
		store.setDefault(JmsLogPreferenceConstants.VALUE5,"NOT DEFINED");
		store.setDefault(JmsLogPreferenceConstants.VALUE6,"NOT DEFINED");
		store.setDefault(JmsLogPreferenceConstants.VALUE7,"NOT DEFINED");
		store.setDefault(JmsLogPreferenceConstants.VALUE8,"NOT DEFINED");
		store.setDefault(JmsLogPreferenceConstants.VALUE9,"NOT DEFINED");
	}
}
