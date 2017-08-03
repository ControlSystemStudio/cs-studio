/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.config.xml;

import java.util.logging.Logger;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;

/** Not really an Activator, just holds related info and could turn into activator
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class Activator
{
    public static final String CONFIG_PATH = "config_path";
    public static final String ENGINE_URL = "engine_url";

    /** Plugin ID defined in MANIFEST.MF */
    final public static String ID = "org.csstudio.archive.config.xml";

    /** @return Logger for plugin ID */
    public static Logger getLogger()
    {
        return Logger.getLogger(ID);
    }

    public static String getConfigPath() {
        return getString(CONFIG_PATH);
    }

    public static String getEngineURL() {
        return getString(ENGINE_URL);
    }

    /**
     * Get string preference
     *
     * @param key
     *            Preference key
     * @return String or <code>null</code>
     */
    private static String getString(final String key) {
        final IPreferencesService prefs = Platform.getPreferencesService();
        if (prefs == null)
            return null;
        return prefs.getString(Activator.ID, key, null, null);
    }
}
