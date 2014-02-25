/*******************************************************************************
 * Copyright (c) 2010-2014 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.iter.authorizationprovider;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;

/**
 * Access to preferences
 * 
 * @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class Preferences {
	// Names of preferences, see preferences.ini
	public static final String DEFAULT_AUTHORIZATIONS = "default_authorizations";

	/**
	 * Get preference settings for column definitions
	 * 
	 * @return Array of raw strings for column preferences
	 * @throws Exception
	 *             on error
	 */
	public static String[] getDefaultAuthorization() throws Exception {
		String pref_text = "";
		// Read preferences
		final IPreferencesService service = Platform.getPreferencesService();
		if (service != null)
			pref_text = service.getString(Activator.ID,
					Preferences.DEFAULT_AUTHORIZATIONS, pref_text, null);
		// Split columns on '|'
		return pref_text.split("\\|");
	}
}
