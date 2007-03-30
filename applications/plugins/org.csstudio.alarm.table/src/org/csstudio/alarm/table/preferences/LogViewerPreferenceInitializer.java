package org.csstudio.alarm.table.preferences;

import org.csstudio.alarm.table.JmsLogsPlugin;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;


/**
 * Class used to initialize default preference values.
 */
public class LogViewerPreferenceInitializer extends AbstractPreferenceInitializer {

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	public void initializeDefaultPreferences() {
		IPreferenceStore store = JmsLogsPlugin.getDefault().getPreferenceStore();
		store.setDefault(LogViewerPreferenceConstants.P_STRING,
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

		store.setDefault(LogViewerPreferenceConstants.MAX, 100);
		store.setDefault(LogViewerPreferenceConstants.REMOVE, 10);
		store.setDefault(LogViewerPreferenceConstants.INITIAL_PRIMARY_CONTEXT_FACTORY, "org.exolab.jms.jndi.InitialContextFactory"); //$NON-NLS-1$
		store.setDefault(LogViewerPreferenceConstants.PRIMARY_URL, "rmi://krykelog.desy.de:1099/"); //$NON-NLS-1$
		store.setDefault(LogViewerPreferenceConstants.INITIAL_SECONDARY_CONTEXT_FACTORY, "org.exolab.jms.jndi.InitialContextFactory"); //$NON-NLS-1$
		store.setDefault(LogViewerPreferenceConstants.SECONDARY_URL, "rmi://krynfs.desy.de:1099/"); //$NON-NLS-1$
		store.setDefault(LogViewerPreferenceConstants.QUEUE, "LOG");
}

}
