/*******************************************************************************
 * Copyright (c) 2010-2013 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.iter.css.product.preferences;

import java.util.logging.Level;

import org.csstudio.iter.css.product.Activator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;

/**
 * Helper to read preferences
 * 
 * @author Davy Dequidt
 */
@SuppressWarnings("nls")
public class Preferences {
	final public static String VERBOSE_LOG_LEVEL = "verbose_log.level";

	/**
	 * @return {@link Level} for verbose log
	 * @throws Exception
	 *             when value cannot be parsed
	 */
	public static Level getVerboseLogLevel() throws Exception {
		Level level = Level.INFO;
		final IPreferencesService prefs = Platform.getPreferencesService();
		if (prefs != null) {
			String txt = prefs.getString(Activator.PLUGIN_ID,
					VERBOSE_LOG_LEVEL, level.getName(), null);
			if (txt != null && !txt.isEmpty()) {
				try {
					level = Level.parse(txt);
				} catch (Throwable ex) {
					throw new Exception("Illegal console log level '" + txt
							+ "'");
				}
			}
		}
		return level;
	}
}
