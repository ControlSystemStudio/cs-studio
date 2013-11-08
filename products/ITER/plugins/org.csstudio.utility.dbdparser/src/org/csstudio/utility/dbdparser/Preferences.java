/*******************************************************************************
 * Copyright (c) 2010-2013 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.utility.dbdparser;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;

/**
 * Read preferences
 * <p>
 * See preferences.ini for explanation of supported preferences.
 * 
 * @author Fred Arnaud (Sopra Group)
 */
@SuppressWarnings("nls")
public class Preferences {

	final public static String EPICS_DBD_BASE_PATH = "epics_dbd_base_path";

	/**
	 * @param setting Preference identifier
	 * @return String from preference system, or <code>null</code>
	 */
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
		return service.getString(Activator.PLUGIN_ID, setting, default_value, null);
	}

	/** @return Absolute path of epics base.dbd file */
	public static String getEpicsDBDBasePath() {
		return getString(EPICS_DBD_BASE_PATH);
	}

}
