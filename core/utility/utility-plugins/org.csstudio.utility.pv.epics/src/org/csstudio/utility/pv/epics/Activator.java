/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.utility.pv.epics;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.platform.libs.epics.EpicsPlugin;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.osgi.framework.BundleContext;

/** Plugin-activator for the EPICS PV.
 *  @author Kay Kasemir
 */
public class Activator extends Plugin
{
	/** Plug-in ID registered in MANIFEST.MF */
	public static final String ID = "org.csstudio.utility.pv.epics"; //$NON-NLS-1$

	/** Logger */
	private static Logger logger = Logger.getLogger(ID);

    /** The singleton instance */
	private static Activator plugin;

	/** Constructor */
	public Activator()
    {	plugin = this;	}

    /** @see AbstractCssPlugin */
    @SuppressWarnings("nls")
    @Override
    public void start(BundleContext context)
    {
        try
        {
            final EpicsPlugin epics = EpicsPlugin.getDefault();
            PVContext.use_pure_java = epics.usePureJava();
            PVContext.monitor_mask = epics.getMonitorMask();

            final IPreferencesService prefs = Platform.getPreferencesService();
            PVContext.support_dbe_property = prefs.getBoolean(EpicsPlugin.ID, "support_dbe_property", false, null);
        }
        catch (Throwable e)
        {
            getLogger().log(Level.SEVERE, "Cannot load EPICS_V3_PV", e);
        }
    }

	/** @return the shared instance */
	public static Activator getDefault()
    {
		return plugin;
	}

	/** Log levels:
	 *  CONFIG - Config info,
	 *  FINE   - JCA start/stop,
	 *  FINER  - PV create/dispose,
	 *  FINER  - Value traffic.
	 *  @return Logger associated with the plugin */
	public static Logger getLogger()
	{
	    return logger;
	}
}
