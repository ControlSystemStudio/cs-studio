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
import org.csstudio.vtype.pv.jca.JCA_PVFactory;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;

/** Preference settings
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
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
        return EpicsPlugin.getDefault().usePureJava();
    }

    public static MonitorMask monitorMask()
    {
        return EpicsPlugin.getDefault().getMonitorMask();
    }

    public static boolean monitorProperties()
    {
        return EpicsPlugin.getDefault().isDbePropertySupported();
    }

    /** @return Support var array, don't support, or use auto-detect (<code>null</code>) */
    public static Boolean isVarArraySupported()
    {
        return EpicsPlugin.getDefault().getVarArraySupported();
    }
}
