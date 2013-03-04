package org.csstudio.iter.utility.ologauth;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.auth.internal.usermanagement.IUserManagementListener;
import org.csstudio.auth.internal.usermanagement.UserManagementEvent;
import org.csstudio.auth.security.SecurityFacade;
import org.csstudio.auth.security.User;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

import edu.msu.nscl.olog.api.Olog;
import edu.msu.nscl.olog.api.OlogClientImpl.OlogClientBuilder;

public class OlogAuthAdapter implements IUserManagementListener {

	private static final Logger log = Logger.getLogger(OlogAuthAdapter.class
			.getName());

	public OlogAuthAdapter() {
		SecurityFacade.getInstance().addUserManagementListener(this);
		handleUserManagementEvent(null);
	}

	@Override
	public void handleUserManagementEvent(UserManagementEvent event) {
		final IPreferencesService prefs = Platform.getPreferencesService();

		final User user = SecurityFacade.getInstance().getCurrentUser();
		final ScopedPreferenceStore prefStore = new ScopedPreferenceStore(
				InstanceScope.INSTANCE,
				org.csstudio.utility.olog.Activator.PLUGIN_ID);
		prefStore
				.setValue(
						org.csstudio.utility.olog.PreferenceConstants.Use_authentication,
						true);
		String username;
		String password;
		if (user == null) {
			// Set default olog user to anonymous
			username = prefs.getString(Activator.PLUGIN_ID,
					PreferenceConstants.anonymous_olog_user, null, null);
			password = prefs.getString(Activator.PLUGIN_ID,
					PreferenceConstants.anonymous_olog_password, null, null);
			if (username != null) {
				prefStore.setValue(
						org.csstudio.utility.olog.PreferenceConstants.Username,
						username);
				prefStore.setValue(
						org.csstudio.utility.olog.PreferenceConstants.Password,
						password);
			}

		} else {
			// Set default olog user to tagmod
			username = prefs.getString(Activator.PLUGIN_ID,
					PreferenceConstants.connected_olog_user, null, null);
			password = prefs.getString(Activator.PLUGIN_ID,
					PreferenceConstants.connected_olog_password, null, null);
			if (username != null) {
				prefStore.setValue(
						org.csstudio.utility.olog.PreferenceConstants.Username,
						username);
				prefStore.setValue(
						org.csstudio.utility.olog.PreferenceConstants.Password,
						password);
			}
		}
		if (username != null) {
			OlogClientBuilder ologClientBuilder;
			String url = prefs.getString(
					org.csstudio.utility.olog.Activator.PLUGIN_ID,
					org.csstudio.utility.olog.PreferenceConstants.Olog_URL,
					"http://localhost:8080/Olog/resources", null);
			ologClientBuilder = OlogClientBuilder.serviceURL(url);
			ologClientBuilder.withHTTPAuthentication(true).username(username)
					.password(password);
			log.info("Creating Olog client : " + url);
			try {
				Olog.setClient(ologClientBuilder.create());
			} catch (Exception e) {
				log.log(Level.WARNING, e.getMessage(), e);
			}
		}
	}
}