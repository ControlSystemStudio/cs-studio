/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.ui.areapanel;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;

/** Read preference settings.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class Preferences
{
    final public static String HIERARCHY_LEVEL = "hierarchy_level";
    final public static String COLUMNS = "columns";
    
    /** @return Level of alarm tree hierarchy to display by the panel. 1 for 'area' */
    public static int getHierarchyLevel()
    {
    	final int level = 1;
        final IPreferencesService service = Platform.getPreferencesService();
        if (service == null)
            return level;
        return service.getInt(Activator.ID, HIERARCHY_LEVEL, level, null);
    }

    /** @return Number of columns to use for display */
    public static int getColumns()
    {
    	final int columns = 2;
        final IPreferencesService service = Platform.getPreferencesService();
        if (service == null)
            return columns;
        return service.getInt(Activator.ID, COLUMNS, columns, null);
    }
}
