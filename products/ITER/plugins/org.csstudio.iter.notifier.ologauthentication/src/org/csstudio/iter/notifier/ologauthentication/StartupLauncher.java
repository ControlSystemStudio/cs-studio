/*******************************************************************************
 * Copyright (c) 2010-2014 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.iter.notifier.ologauthentication;

import java.util.logging.Level;

import org.csstudio.alarm.beast.notifier.model.IApplicationListener;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.equinox.app.IApplicationContext;

import edu.msu.nscl.olog.api.Olog;
import edu.msu.nscl.olog.api.OlogClient;
import edu.msu.nscl.olog.api.OlogClientImpl.OlogClientBuilder;

/**
 * Initialize {@link OlogClient}.
 * 
 * @author Fred Arnaud (Sopra Group) - ITER
 * 
 */
public class StartupLauncher implements IApplicationListener {

	@Override
	public void applicationStarted(IApplicationContext context) {
		final IPreferencesService prefs = Platform.getPreferencesService();

		// Set default olog user to anonymous
		String username = prefs.getString(Activator.PLUGIN_ID,
				PreferenceConstants.anonymous_olog_user, null, null);
		String password = prefs.getString(Activator.PLUGIN_ID,
				PreferenceConstants.anonymous_olog_password, null, null);

		if (username != null) {
			String url = prefs.getString(
							org.csstudio.alarm.beast.notifier.olog.Activator.PLUGIN_ID,
							org.csstudio.alarm.beast.notifier.olog.PreferenceConstants.Olog_URL,
							"http://localhost:8080/Olog/resources", null);
			OlogClientBuilder ologClientBuilder = OlogClientBuilder.serviceURL(url);
			ologClientBuilder.withHTTPAuthentication(true).username(username).password(password);
			Activator.getLogger().log(Level.INFO,
					"Creating Olog client : " + url + " with user '" + username + "'");
			try {
				Olog.setClient(ologClientBuilder.create());
			} catch (Exception e) {
				Activator.getLogger().log(Level.WARNING, e.getMessage(), e);
			}
		}
	}

}
