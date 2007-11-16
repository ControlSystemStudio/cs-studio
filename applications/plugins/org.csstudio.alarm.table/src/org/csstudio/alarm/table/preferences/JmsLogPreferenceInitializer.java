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
		store.setDefault(JmsLogPreferenceConstants.KEY0,"INVALID"); //$NON-NLS-1$
		store.setDefault(JmsLogPreferenceConstants.KEY1,"MAJOR"); //$NON-NLS-1$
		store.setDefault(JmsLogPreferenceConstants.KEY2,"MINOR"); //$NON-NLS-1$
		store.setDefault(JmsLogPreferenceConstants.KEY3,"NO_ALARM"); //$NON-NLS-1$
		store.setDefault(JmsLogPreferenceConstants.KEY4,"4"); //$NON-NLS-1$
		store.setDefault(JmsLogPreferenceConstants.KEY5,"5"); //$NON-NLS-1$
		store.setDefault(JmsLogPreferenceConstants.KEY6,"6"); //$NON-NLS-1$
		store.setDefault(JmsLogPreferenceConstants.KEY7,"7"); //$NON-NLS-1$
		store.setDefault(JmsLogPreferenceConstants.KEY8,"8"); //$NON-NLS-1$
		store.setDefault(JmsLogPreferenceConstants.KEY9,"9"); //$NON-NLS-1$
		store.setDefault(JmsLogPreferenceConstants.COLOR0,"191,191,191"); //$NON-NLS-1$
		store.setDefault(JmsLogPreferenceConstants.COLOR1,"252,7,14"); //$NON-NLS-1$
		store.setDefault(JmsLogPreferenceConstants.COLOR2,"251,215,11"); //$NON-NLS-1$
		store.setDefault(JmsLogPreferenceConstants.COLOR3,"96,251,123"); //$NON-NLS-1$
		store.setDefault(JmsLogPreferenceConstants.COLOR4,"201,201,201"); //$NON-NLS-1$
		store.setDefault(JmsLogPreferenceConstants.COLOR5,"201,201,201"); //$NON-NLS-1$
		store.setDefault(JmsLogPreferenceConstants.COLOR6,"201,201,201"); //$NON-NLS-1$
		store.setDefault(JmsLogPreferenceConstants.COLOR7,"201,201,201"); //$NON-NLS-1$
		store.setDefault(JmsLogPreferenceConstants.COLOR8,"201,201,201"); //$NON-NLS-1$
		store.setDefault(JmsLogPreferenceConstants.COLOR9,"201,201,201"); //$NON-NLS-1$
		store.setDefault(JmsLogPreferenceConstants.VALUE0,"INVALID"); //$NON-NLS-1$
		store.setDefault(JmsLogPreferenceConstants.VALUE1,"MAJOR"); //$NON-NLS-1$
		store.setDefault(JmsLogPreferenceConstants.VALUE2,"MINOR"); //$NON-NLS-1$
		store.setDefault(JmsLogPreferenceConstants.VALUE3,"NO_ALARM"); //$NON-NLS-1$
		store.setDefault(JmsLogPreferenceConstants.VALUE4,"NOT DEFINED"); //$NON-NLS-1$
		store.setDefault(JmsLogPreferenceConstants.VALUE5,"NOT DEFINED"); //$NON-NLS-1$
		store.setDefault(JmsLogPreferenceConstants.VALUE6,"NOT DEFINED"); //$NON-NLS-1$
		store.setDefault(JmsLogPreferenceConstants.VALUE7,"NOT DEFINED"); //$NON-NLS-1$
		store.setDefault(JmsLogPreferenceConstants.VALUE8,"NOT DEFINED"); //$NON-NLS-1$
		store.setDefault(JmsLogPreferenceConstants.VALUE9,"NOT DEFINED"); //$NON-NLS-1$
	}
}
