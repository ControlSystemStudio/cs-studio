package org.csstudio.utility.ldapUpdater;

import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.security.Credentials;
import org.csstudio.platform.security.ILoginCallbackHandler;
import org.csstudio.utility.ldapUpdater.preferences.LdapUpdaterPreferenceConstants;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;

/**
 * Provides username and password for the XMPP login.
 * 
 * @author Joerg Rathlev
 */
public class XmppLoginCallbackHandler implements ILoginCallbackHandler {

	/**
	 * {@inheritDoc}
	 */
	public final Credentials getCredentials() {
 		IPreferencesService prefs = Platform.getPreferencesService();
		String username = prefs.getString(Activator.PLUGIN_ID,
				LdapUpdaterPreferenceConstants.XMPP_USER, "", null);
		String password = prefs.getString(Activator.PLUGIN_ID,
				LdapUpdaterPreferenceConstants.XMPP_PASSWD, "", null);
		return new Credentials(username, password);
	}

	/**
	 * {@inheritDoc}
	 */
	public final void signalFailedLoginAttempt() {
		CentralLogger.getInstance().error(this, "XMPP login failed");
	}
}
