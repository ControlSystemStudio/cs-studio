/*******************************************************************************
 * Copyright (c) 2010-2014 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.widgets.symbol;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.swt.graphics.RGB;

/**
 * Read preferences
 * <p>
 * See preferences.ini for explanation of supported preferences.
 * 
 * @author Fred Arnaud (Sopra Group)
 */
@SuppressWarnings("nls")
public class Preferences {
	final public static String COLOR_TO_CHANGE = "color_to_change";

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

	/** @return threshold for automated actions */
	public static RGB getColorToChange() {
		final IPreferencesService service = Platform.getPreferencesService();
		if (service == null)
			return new RGB(0, 0, 0); // default
		String colorStr = getString(COLOR_TO_CHANGE);
		if (colorStr == null)
			return new RGB(0, 0, 0); // default
		String[] RGB = colorStr.split(",");
		if (RGB == null | RGB.length < 3)
			return new RGB(0, 0, 0); // default
		return new RGB(Integer.valueOf(RGB[0]), Integer.valueOf(RGB[1]),
				Integer.valueOf(RGB[2]));
	}

}
