package org.csstudio.alarm.treeView.preferences;

import org.csstudio.alarm.treeView.AlarmTreePlugin;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.osgi.service.prefs.Preferences;


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
		Preferences prefs = new DefaultScope().getNode(AlarmTreePlugin.PLUGIN_ID);
		prefs.put(PreferenceConstants.LDAP_URL, "ldap://krynfs.desy.de:389/o=DESY,c=DE");
		prefs.put(PreferenceConstants.LDAP_USER, "");
		prefs.put(PreferenceConstants.LDAP_PASSWORD, "");
		prefs.put(PreferenceConstants.FACILITIES, "");
		prefs.put(PreferenceConstants.JMS_URL_PRIMARY, "tcp://elogbook.desy.de:64616");
		prefs.put(PreferenceConstants.JMS_URL_SECONDARY, "tcp://krynfs.desy.de:62616");
		prefs.put(PreferenceConstants.JMS_CONTEXT_FACTORY_PRIMARY, "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
		prefs.put(PreferenceConstants.JMS_CONTEXT_FACTORY_SECONDARY, "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
		prefs.put(PreferenceConstants.JMS_QUEUE, "ALARM,ACK");
	}

}
