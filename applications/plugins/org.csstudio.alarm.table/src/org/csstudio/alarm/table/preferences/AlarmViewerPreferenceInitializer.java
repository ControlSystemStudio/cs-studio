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
				"TYPE" + ";" +
				"EVENTTIME" + ";" +
				"TEXT" + ";" +
				"USER" + ";" +
				"HOST" + ";" +
				"APPLICATION-ID" + ";" +
				"PROCESS-ID" + ";" +
				"NAME" + ";" +
				"CLASS" + ";" +
				"DOMAIN" + ";" +
				"FACILITY" + ";" +
				"LOCATION" + ";" +
				"SEVERITY" + ";" +
				"STATUS" + ";" +
				"VALUE" + ";" +
				"DESTINATION"
		);

		store.setDefault(AlarmViewerPreferenceConstants.MAX, 100);
		store.setDefault(AlarmViewerPreferenceConstants.INITIAL_PRIMARY_CONTEXT_FACTORY, "org.exolab.jms.jndi.InitialContextFactory"); //$NON-NLS-1$
		store.setDefault(AlarmViewerPreferenceConstants.PRIMARY_URL, "rmi://krykelog.desy.de:1099/"); //$NON-NLS-1$
		store.setDefault(AlarmViewerPreferenceConstants.INITIAL_SECONDARY_CONTEXT_FACTORY, "org.exolab.jms.jndi.InitialContextFactory"); //$NON-NLS-1$
		store.setDefault(AlarmViewerPreferenceConstants.SECONDARY_URL, "rmi://krykelog.desy.de:1099/"); //$NON-NLS-1$
		store.setDefault(AlarmViewerPreferenceConstants.QUEUE, "LOG");
	}

}
