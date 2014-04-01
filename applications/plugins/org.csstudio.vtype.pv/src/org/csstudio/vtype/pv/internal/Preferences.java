/*******************************************************************************
 * Copyright (c) 2014 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.vtype.pv.internal;

import org.csstudio.platform.libs.epics.EpicsPlugin;
import org.csstudio.platform.libs.epics.EpicsPlugin.MonitorMask;
import org.csstudio.platform.libs.epics.PreferenceConstants;
import org.csstudio.vtype.pv.jca.JCA_PVFactory;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;

/** Preference settings
 *  @author Kay Kasemir
 */
public class Preferences
{
    private static String getString(final String plugin, final String setting, final String default_value)
    {
        final IPreferencesService service = Platform.getPreferencesService();
        if (service == null)
            return default_value;
        return service.getString(plugin, setting, default_value, null);
    }

    public static String defaultType()
    {
        return getString(Activator.ID, "default_type", JCA_PVFactory.TYPE);
    }
        
    public static boolean usePureJava()
    {
        return Boolean.parseBoolean(getString(EpicsPlugin.ID, PreferenceConstants.PURE_JAVA, Boolean.TRUE.toString()));
    }
    
    public static MonitorMask monitorMask()
    {
        return MonitorMask.valueOf(getString(EpicsPlugin.ID, PreferenceConstants.MONITOR, "VALUE"));
    }
}
