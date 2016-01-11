/*******************************************************************************
 * Copyright (c) 2010-2016 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.utility.batik;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;

/**
 * Read preferences
 * <p>
 * See preferences.ini for explanation of supported preferences.
 *
 * @author Fred Arnaud (Sopra Steria Group) - ITER
 */
@SuppressWarnings("nls")
public class Preferences {

    final public static String USE_CACHE = "use_cache";
    final public static String CACHE_MAX_SIZE = "cache_max_size";

    public static boolean getUseCache() {
        final IPreferencesService service = Platform.getPreferencesService();
        if (service == null) {
            return false;
        }
        return service.getBoolean(Activator.PLUGIN_ID, USE_CACHE, false, null);
    }

    public static int getCacheMaxSize() {
        final IPreferencesService service = Platform.getPreferencesService();
        if (service == null) {
            return 100;
        }
        return service.getInt(Activator.PLUGIN_ID, CACHE_MAX_SIZE, 100, null);
    }

}
