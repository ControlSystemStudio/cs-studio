/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.sscan.preferences;

import java.util.ArrayList;
import java.util.logging.Level;

import org.csstudio.trends.sscan.Activator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;

/** Helper for reading preference settings
 *
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class Preferences
{
    /** Regular expression for separator between list items */
    static final String ITEM_SEPARATOR_RE = "\\*";

    /** Regular expression for separator between components within an item */
    static final String COMPONENT_SEPARATOR_RE = "\\|";

    /** Separator between list items */
    static final String ITEM_SEPARATOR = "*";

    /** Separator between components within an item */
    static final String COMPONENT_SEPARATOR = "|";

    /** Preference tags.
     *  For explanation of the settings see preferences.ini
     */
    final public static String XMAX = "xMax",
                               YMAX = "yMax",
                               LINE_WIDTH = "line_width",
                               URLS = "urls";

    public static double getXMax()
    {
        final IPreferencesService prefs = Platform.getPreferencesService();
        if (prefs == null) // Allow some JUnit tests without prefs
            return 100.0;
        return prefs.getDouble(Activator.PLUGIN_ID, XMAX, 100, null);
    }

    public static double getYMax()
    {
        final IPreferencesService prefs = Platform.getPreferencesService();
        if (prefs == null) // Allow some JUnit tests without prefs
            return 100.0;
        return prefs.getDouble(Activator.PLUGIN_ID, YMAX, 100, null);
    }


    public static int getLineWidths()
    {
        final IPreferencesService prefs = Platform.getPreferencesService();
        if (prefs == null)
            return 2;
        return prefs.getInt(Activator.PLUGIN_ID, LINE_WIDTH, 2, null);
    }

    public static String getUrls()
    {
        final IPreferencesService prefs = Platform.getPreferencesService();
        return prefs.getString(Activator.PLUGIN_ID, URLS, "http://", null);
    }


}
