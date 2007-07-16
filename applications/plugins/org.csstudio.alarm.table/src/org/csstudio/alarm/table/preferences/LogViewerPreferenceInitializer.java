package org.csstudio.alarm.table.preferences;

import org.csstudio.alarm.table.JmsLogsPlugin;
import org.csstudio.platform.libs.jms.preferences.PreferenceConstants;
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
				"ACK" + ";" + //$NON-NLS-1$ //$NON-NLS-2$
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

		store.setDefault(LogViewerPreferenceConstants.MAX, 200);
		store.setDefault(LogViewerPreferenceConstants.REMOVE, 10);
		store.setDefault(LogViewerPreferenceConstants.INITIAL_PRIMARY_CONTEXT_FACTORY, "org.apache.activemq.jndi.ActiveMQInitialContextFactory"); //$NON-NLS-1$
		store.setDefault(LogViewerPreferenceConstants.PRIMARY_URL, "failover:(tcp://elogbook.desy.de:64616)?maxReconnectAttempts=2"); //$NON-NLS-1$
		store.setDefault(LogViewerPreferenceConstants.INITIAL_SECONDARY_CONTEXT_FACTORY, "org.apache.activemq.jndi.ActiveMQInitialContextFactory"); //$NON-NLS-1$
		store.setDefault(LogViewerPreferenceConstants.SECONDARY_URL, "failover:(tcp://krynfs.desy.de:62616)?maxReconnectAttempts=2"); //$NON-NLS-1$
		store.setDefault(LogViewerPreferenceConstants.QUEUE, "LOG,ALARM,PUT_LOG"); //$NON-NLS-1$
}

}