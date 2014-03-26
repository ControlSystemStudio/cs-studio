/*******************************************************************************
 * Copyright (c) 2010-2014 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.iter.utility.ologauth;

import java.security.Principal;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.security.auth.Subject;

import org.csstudio.security.SecurityListener;
import org.csstudio.security.authorization.Authorizations;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

import edu.msu.nscl.olog.api.Olog;
import edu.msu.nscl.olog.api.OlogClientImpl.OlogClientBuilder;

public class OlogAuthAdapter implements SecurityListener {

	private static final Logger log = Logger.getLogger(OlogAuthAdapter.class
			.getName());

	public OlogAuthAdapter() {
		changedSecurity(null, false, null);
	}

	@Override
	public void changedSecurity(Subject subject, boolean is_current_user,
			Authorizations authorizations) {
		final IPreferencesService prefs = Platform.getPreferencesService();

		final ScopedPreferenceStore prefStore = new ScopedPreferenceStore(
				InstanceScope.INSTANCE,
				org.csstudio.utility.olog.Activator.PLUGIN_ID);
		prefStore
				.setValue(
						org.csstudio.utility.olog.PreferenceConstants.Use_authentication,
						true);
		String username;
		String password;
		String connectedOlogUsername = prefs.getString(Activator.PLUGIN_ID,
				PreferenceConstants.connected_olog_user, null, null);
		boolean anonymousLogin = true;
		if (subject != null) {
			for (Principal principal : subject.getPrincipals()) {
				if (principal != null && principal.getName() != null
						&& principal.getName().equals(connectedOlogUsername)) {
					anonymousLogin = false;
					break;
				}
			}
		}
		if(anonymousLogin) {
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
			username = connectedOlogUsername;
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
			log.log(Level.INFO, "Creating Olog client : " + url
					+ " with user '" + username + "'");
			try {
				Olog.setClient(ologClientBuilder.create());
			} catch (Exception e) {
				log.log(Level.WARNING, e.getMessage(), e);
			}
		}
	}
}