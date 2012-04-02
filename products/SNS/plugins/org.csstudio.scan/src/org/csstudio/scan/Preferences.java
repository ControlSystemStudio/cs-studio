/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan;

import org.csstudio.scan.server.ScanServer;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;

/** Scan system preferences
 *
 *  <p>Note that some preferences (server host, port) are actually
 *  read from system properties to allow non-Eclipse tools that can
 *  only access system properties to also use the scan system.
 *
 *  <p>These system properties are populated from Eclipse
 *  preferences when running within Eclipse, but the actual
 *  values are always read from the system preferences
 *  to be compatible with non-Eclipse tools.
 *
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class Preferences extends SystemSettings
{
	/** @return Path to the default beamline information file */
	public static String getBeamlineConfigPath()
	{
    	final IPreferencesService service = Platform.getPreferencesService();
    	return service.getString(Activator.ID, "beamline_config", "platform:/plugin/org.csstudio.scan/examples/beamline.xml", null);
	}

	/** @return Path to the pre-scan commands */
    public static String getPreScanPath()
    {
        final IPreferencesService service = Platform.getPreferencesService();
        return service.getString(Activator.ID, "pre_scan", "platform:/plugin/org.csstudio.scan/examples/pre_scan.scn", null);
    }

    /** @return Path to the post-scan commands */
    public static String getPostScanPath()
    {
        final IPreferencesService service = Platform.getPreferencesService();
        return service.getString(Activator.ID, "post_scan", "platform:/plugin/org.csstudio.scan/examples/post_scan.scn", null);
    }

    /** @return Memory threshold for removing older scans */
    public static double getOldScanRemovalMemoryThreshold()
    {
        double threshold = 50.0;
        final IPreferencesService service = Platform.getPreferencesService();
        if (service == null) // Run without pref service
        	return threshold;
		return service.getDouble(Activator.ID, "old_scan_removal_memory_threshold", threshold, null);
    }

    /** @return Memory threshold for data logger */
    public static double getDataLoggerMemoryThreshold()
    {
        final IPreferencesService service = Platform.getPreferencesService();
        return service.getDouble(Activator.ID, "data_logger_memory_threshold", 80.0, null);
    }

    /** Set system properties (which are in the end what's actually used)
     *  from Eclipse preferences (which are more accessible for Eclipse tools
     *  with plugin_customization or preference GUI)
     */
    public static void setSystemPropertiesFromPreferences()
    {
        final IPreferencesService service = Platform.getPreferencesService();
        System.setProperty(ScanServer.HOST_PROPERTY,
                service.getString(Activator.ID, "server_host", ScanServer.DEFAULT_HOST, null));
        System.setProperty(ScanServer.PORT_PROPERTY,
                Integer.toString(
                        service.getInt(Activator.ID, "server_port", ScanServer.DEFAULT_PORT, null)));
    }
}
