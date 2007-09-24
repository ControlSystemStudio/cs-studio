package org.csstudio.diag.interconnectionServer.preferences;

import org.csstudio.platform.libs.xmpp.Activator;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

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
		IEclipsePreferences prefs = new DefaultScope().getNode(
				Activator.getDefault().getPluginId());

		prefs.put(PreferenceConstants.XMPP_USER_NAME, "icserver-alarm");
		prefs.put(PreferenceConstants.XMPP_PASSWORD, "icserver");
		prefs.put(PreferenceConstants.DATA_PORT_NUMBER, "18324");
		prefs.put(PreferenceConstants.COMMAND_PORT_NUMBER, "18325");
		prefs.put(PreferenceConstants.SENT_START_ID, "5000000");
		prefs.put(PreferenceConstants.JMS_CONTEXT_FACTORY, "ACTIVEMQ");
		prefs.put(PreferenceConstants.JMS_TIME_TO_LIVE_ALARMS, "3600000");
		prefs.put(PreferenceConstants.JMS_TIME_TO_LIVE_LOGS, "600000");
		prefs.put(PreferenceConstants.JMS_TIME_TO_LIVE_PUT_LOGS, "3600000");
		prefs.put(PreferenceConstants.PRIMARY_JMS_URL, "failover:(tcp://krynfs.desy.de:62616,tcp://krykjmsb.desy.de:64616)?maxReconnectDelay=500,maxReconnectAttempts=50");
		prefs.put(PreferenceConstants.SECONDARY_JMS_URL	, "failover:(tcp://krykjmsb.desy.de:64616,tcp://krynfs.desy.de:62616)?maxReconnectDelay=500,maxReconnectAttempts=50");
	}

}
