package org.csstudio.utility.ldapUpdater.preferences;

import org.csstudio.utility.ldapUpdater.Activator;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

public class LdapUpdaterPreferenceInitializer extends
		AbstractPreferenceInitializer {

	@Override
	public void initializeDefaultPreferences() {
		IEclipsePreferences prefs = new DefaultScope().getNode(
				Activator.getDefault().getPluginId());
//		prefs.put(LdapUpdaterPreferenceConstants.IOC_LIST_PATH, "P:\\scripts\\epxLDAPgen\\");		// unix : "/applic/directoryServer/";
		prefs.put(LdapUpdaterPreferenceConstants.IOC_DBL_DUMP_PATH, "Y:\\directoryServer\\");		// unix : "/applic/directoryServer/";
		prefs.put(LdapUpdaterPreferenceConstants.IOC_LIST_FILE, "P:\\scripts\\epxLDAPgen\\IOCpathes");		// unix : "/applic/directoryServer/";
		prefs.put(LdapUpdaterPreferenceConstants.LDAP_CONT_ROOT, "de.desy.epicsControls.");		// unix : "/applic/directoryServer/";
		prefs.put(LdapUpdaterPreferenceConstants.LDAP_HIST_PATH, "Y:\\scripts\\ldap-tests\\");		// unix : "/applic/directoryServer/";
		prefs.put(LdapUpdaterPreferenceConstants.XMPP_USER, "testc");		
		prefs.put(LdapUpdaterPreferenceConstants.XMPP_PASSWD, "testc");		
		prefs.put(LdapUpdaterPreferenceConstants.LDAP_AUTO_INTERVAL, "1000*3600*24");		

	}

}
