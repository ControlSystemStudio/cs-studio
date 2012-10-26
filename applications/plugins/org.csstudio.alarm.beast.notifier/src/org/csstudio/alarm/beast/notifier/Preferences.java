/*******************************************************************************
* Copyright (c) 2010-2012 ITER Organization.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
******************************************************************************/
package org.csstudio.alarm.beast.notifier;

import org.csstudio.auth.security.SecureStorage;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;

/** Read preferences
 *  <p>
 *  See preferences.ini for explanation of supported preferences.
 *  @author Fred Arnaud (Sopra Group)
 */
@SuppressWarnings("nls")
public class Preferences {
    final public static String TIMER_THRESHOLD = "timer_threshold";
    final public static String THREAD_THRESHOLD = "thread_threshold";

	/**
	 * @param setting Preference identifier
	 * @return String from preference system, or <code>null</code>
	 */
	@SuppressWarnings("unused")
	private static String getString(final String setting) {
		return getString(setting, null);
	}

	/**
	 * @param setting Preference identifier
	 * @param default_value Default value when preferences unavailable
	 * @return String from preference system, or <code>null</code>
	 */
	private static String getString(final String setting,
			final String default_value) {
		final IPreferencesService service = Platform.getPreferencesService();
		if (service == null)
			return default_value;
		return service.getString(Activator.ID, setting, default_value, null);
	}

	/** @return threshold for automated actions */
	public static int getTimerThreshold() {
		final IPreferencesService service = Platform.getPreferencesService();
		if (service == null)
			return 100; // default
		return service.getInt(Activator.ID, TIMER_THRESHOLD, 100, null);
	}
	
	/** @return threshold for automated actions */
	public static int getThreadThreshold() {
		final IPreferencesService service = Platform.getPreferencesService();
		if (service == null)
			return 100; // default
		return service.getInt(Activator.ID, THREAD_THRESHOLD, 100, null);
	}

	@SuppressWarnings("unused")
	private static String getSecureString(final String setting) {
		String value = SecureStorage.retrieveSecureStorage(Activator.ID, setting);
		return value;
	}
}
