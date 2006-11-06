package org.csstudio.alarm.table.preferences;

import org.csstudio.alarm.table.JmsLogsPlugin;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;


/**
 * Class used to initialize default preference values.
 */
public class LogArchiveViewerPreferenceInitializer extends AbstractPreferenceInitializer {

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	public void initializeDefaultPreferences() {
		IPreferenceStore store = JmsLogsPlugin.getDefault().getPreferenceStore();
		store.setDefault(LogArchiveViewerPreferenceConstants.P_STRINGArch,
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
		store.setDefault(LogArchiveViewerPreferenceConstants.DATE_FORMAT,"E d-MMMM-yyyy H:mm:ss");
	}

}
