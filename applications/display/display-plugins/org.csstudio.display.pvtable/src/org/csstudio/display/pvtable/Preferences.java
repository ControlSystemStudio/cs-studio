/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.display.pvtable;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;

/** Preference settings
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class Preferences
{
    public static double getTolerance()
    {
        double tolerance = 0.01;
        final IPreferencesService service = Platform.getPreferencesService();
        if (service != null)
            tolerance = service.getDouble(Plugin.ID, "tolerance", tolerance, null);
        return tolerance;
    }

    public static int getUpdateItemThreshold()
    {
        int threshold = 50;
        final IPreferencesService service = Platform.getPreferencesService();
        if (service != null)
            threshold = service.getInt(Plugin.ID, "update_item_threshold", threshold, null);
        return threshold;
    }

    public static boolean treatByteArrayAsString()
    {
        return getBoolOption("treat_byte_array_as_string");
    }

    public static boolean showDescription()
    {
        return getBoolOption("show_description");
    }
    
    public static boolean showSaveTimestamp(){
    	return getBoolOption("show_save_timestamp");
    }

    public static boolean showUnits()
    {
        return getBoolOption("show_units");
    }

    private static boolean getBoolOption(final String name)
    {
        boolean option = true;
        final IPreferencesService service = Platform.getPreferencesService();
        if (service != null)
            option = service.getBoolean(Plugin.ID, name, option, null);
        return option;
    }
}
