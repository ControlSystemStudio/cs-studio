package org.csstudio.alarm.table.preferences;

import org.csstudio.alarm.table.JmsLogsPlugin;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;


/**
 * Class used to initialize default preference values.
 */
public class AlarmViewerPreferenceInitializer extends AbstractPreferenceInitializer {

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	public void initializeDefaultPreferences() {
		IPreferenceStore store = JmsLogsPlugin.getDefault().getPreferenceStore();
		store.setDefault(AlarmViewerPreferenceConstants.P_STRINGAlarm,
				"TYPE" + ";" + //$NON-NLS-1$ //$NON-NLS-2$
				"EVENTTIME" + ";" + //$NON-NLS-1$ //$NON-NLS-2$
				"TEXT" + ";" + //$NON-NLS-1$ //$NON-NLS-2$
				"USER" + ";" + //$NON-NLS-1$ //$NON-NLS-2$
				"HOST" + ";" + //$NON-NLS-1$ //$NON-NLS-2$
				"APPLICATION-ID" + ";" + //$NON-NLS-1$ //$NON-NLS-2$
				"PROCESS-ID" + ";" + //$NON-NLS-1$ //$NON-NLS-2$
				"NAME" + ";" + //$NON-NLS-1$ //$NON-NLS-2$
				"CLASS" + ";" + //$NON-NLS-1$ //$NON-NLS-2$
				"DOMAIN" + ";" + //$NON-NLS-1$ //$NON-NLS-2$
				"FACILITY" + ";" + //$NON-NLS-1$ //$NON-NLS-2$
				"LOCATION" + ";" + //$NON-NLS-1$ //$NON-NLS-2$
				"SEVERITY" + ";" + //$NON-NLS-1$ //$NON-NLS-2$
				"STATUS" + ";" + //$NON-NLS-1$ //$NON-NLS-2$
				"VALUE" + ";" + //$NON-NLS-1$ //$NON-NLS-2$
				"DESTINATION" //$NON-NLS-1$
		);

		store.setDefault(AlarmViewerPreferenceConstants.MAX, 100);
		store.setDefault(AlarmViewerPreferenceConstants.REMOVE, 10);
		store.setDefault(AlarmViewerPreferenceConstants.INITIAL_PRIMARY_CONTEXT_FACTORY, "org.exolab.jms.jndi.InitialContextFactory"); //$NON-NLS-1$
		store.setDefault(AlarmViewerPreferenceConstants.PRIMARY_URL, "rmi://krykelog.desy.de:1099/"); //$NON-NLS-1$
		store.setDefault(AlarmViewerPreferenceConstants.INITIAL_SECONDARY_CONTEXT_FACTORY, "org.exolab.jms.jndi.InitialContextFactory"); //$NON-NLS-1$
		store.setDefault(AlarmViewerPreferenceConstants.SECONDARY_URL, "rmi://krynfs.desy.de:1099/"); //$NON-NLS-1$
		store.setDefault(AlarmViewerPreferenceConstants.QUEUE, "LOG"); //$NON-NLS-1$
	}

}
