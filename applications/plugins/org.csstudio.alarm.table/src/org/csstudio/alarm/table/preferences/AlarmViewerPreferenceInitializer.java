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
				"ACK" + ";" +
				"TYPE" + ";" +
				"EVENTTIME" + ";" +
				"NAME" + ";" +
				"SEVERITY" + ";" +
				"STATUS" + ";" +
				"VALUE" + ";" +
				"TEXT" + ";" +
				"USER" + ";" +
				"HOST" + ";" +
				"APPLICATION-ID" + ";" +
				"PROCESS-ID" + ";" +
				"CLASS" + ";" +
				"DOMAIN" + ";" +
				"FACILITY" + ";" +
				"LOCATION" + ";" +
				"VALUE" + ";" +
				"DESTINATION"
		);

		store.setDefault(AlarmViewerPreferenceConstants.MAX, 200);
		store.setDefault(AlarmViewerPreferenceConstants.REMOVE, 10);
		store.setDefault(AlarmViewerPreferenceConstants.INITIAL_PRIMARY_CONTEXT_FACTORY, "org.apache.activemq.jndi.ActiveMQInitialContextFactory"); //$NON-NLS-1$
		store.setDefault(AlarmViewerPreferenceConstants.PRIMARY_URL, "tcp://elogbook.desy.de:61616"); //$NON-NLS-1$
		store.setDefault(AlarmViewerPreferenceConstants.INITIAL_SECONDARY_CONTEXT_FACTORY, "org.apache.activemq.jndi.ActiveMQInitialContextFactory"); //$NON-NLS-1$
		store.setDefault(AlarmViewerPreferenceConstants.SECONDARY_URL, "tcp://krynfs.desy.de:61616"); //$NON-NLS-1$
		store.setDefault(AlarmViewerPreferenceConstants.QUEUE, "ALARM");
        store.setDefault(AlarmViewerPreferenceConstants.SENDER_URL, "failover:(tcp://elogbook.desy.de:61616,tcp://krynfs.desy.de:61616)?maxReconnectDelay=2000"); //$NON-NLS-1$
	}

}
