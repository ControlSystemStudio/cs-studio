/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;

import static org.csstudio.scan.PathUtil.splitPath;

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
public class ScanSystemPreferences extends SystemSettings
{
    private static volatile boolean warned_about_beamline_config = false;
    private static volatile boolean warned_about_simulation_config = false;
    
    /** @return Path to the scan configuration file */
	public static String getScanConfigPath()
	{
    	final IPreferencesService service = Platform.getPreferencesService();
        // Use legacy beamline_config, if provided, but prefer scan_config
    	final String config = service.getString(Activator.ID, "beamline_config", "", null);
    	if (! config.isEmpty())
    	{
    	    if (! warned_about_beamline_config)
    	    {
        	    Logger.getLogger(ScanSystemPreferences.class.getName())
                   .log(Level.WARNING, Activator.ID + "/beamline_config is deprecated, use ../scan_config");
        	    warned_about_beamline_config = true;
    	    }
    	    return config;
    	}
    	return service.getString(Activator.ID, "scan_config", "", null);
	}

	/** Simulation info should also be in same file as scan config,
	 *  but for backwards compatibility the legacy simulation file is still supported
	 *  @return Path to the simulation information file
	 *  @see #getScanConfigPath()
	 */
	public static String getSimulationConfigPath()
	{
    	final IPreferencesService service = Platform.getPreferencesService();
        // Use legacy simulation_config, if provided, but prefer scan_config
    	final String config = service.getString(Activator.ID, "simulation_config", "", null);
    	if (! config.isEmpty())
    	{
    	    if (! warned_about_simulation_config)
    	    {
        	    Logger.getLogger(ScanSystemPreferences.class.getName())
        	        .log(Level.WARNING, Activator.ID + "/simulation_config is deprecated, use ../scan_config");
        	    warned_about_simulation_config = true;
    	    }
    	    return config;
    	}
	    return getScanConfigPath();
	}

	/** @return Paths to pre-scan commands
     *  @throws Exception on error in path list
     */
    public static String[] getPreScanPaths() throws Exception
    {
        final IPreferencesService service = Platform.getPreferencesService();
        final String list = service.getString(Activator.ID, "pre_scan", "platform:/plugin/org.csstudio.scan/examples/pre_scan.scn", null);
        return splitPath(list);
    }

    /** @return Paths to post-scan commands
     *  @throws Exception on error in path list
     */
    public static String[] getPostScanPaths() throws Exception
    {
        final IPreferencesService service = Platform.getPreferencesService();
        final String list = service.getString(Activator.ID, "post_scan", "platform:/plugin/org.csstudio.scan/examples/post_scan.scn", null);
        return splitPath(list);
    }
    
    /** @return Search paths for scan scripts and 'included' scans
     *  @throws Exception on parse error (missing end of quoted string)
     */
    public static String[] getScriptPaths() throws Exception
    {
        final IPreferencesService service = Platform.getPreferencesService();
        if (service == null)
            return new String[0];
        final String pref = service.getString(Activator.ID, "script_paths", "platform:/plugin/org.csstudio.scan/examples", null);
        return splitPath(pref);
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

    /** @return Scan client poll period [millisecs] */
	public static long getScanClientPollPeriod()
	{
		long period = 1000;
    	final IPreferencesService service = Platform.getPreferencesService();
    	if (service != null)
    		period = service.getLong(Activator.ID, "scan_client_poll_period", period, null);
    	return period;
	}

    /** @return Prefix to scan server status PVs */
	public static String getStatusPvPrefix()
	{
		String prefix = "Demo:Scan:";
    	final IPreferencesService service = Platform.getPreferencesService();
    	if (service != null)
    		prefix = service.getString(Activator.ID, "status_pv_prefix", prefix, null);
    	return prefix;
	}

	/** @return Macros. Not <code>null</code> */
    public static String getMacros()
    {
        final IPreferencesService service = Platform.getPreferencesService();
        final String macros = service.getString(Activator.ID, "macros", "", null);
        if (macros == null)
            return "";
        return macros;
    }

    /** @return Minimum PV update period [seconds] */
    public static double getMinPVUpdatePeriod()
    {
        double period = 0.01;
        final IPreferencesService service = Platform.getPreferencesService();
        if (service != null)
            period = service.getDouble(Activator.ID, "pv_min_update_period", period, null);
        return period;
    }
    
	/** Set system properties (which are in the end what's actually used)
     *  from Eclipse preferences (which are more accessible for Eclipse tools
     *  with plugin_customization or preference GUI)
     */
    public static void setSystemPropertiesFromPreferences()
    {
        final IPreferencesService service = Platform.getPreferencesService();
        System.setProperty(HOST_PROPERTY,
                service.getString(Activator.ID, "server_host", DEFAULT_HOST, null));
        System.setProperty(PORT_PROPERTY,
                Integer.toString(
                        service.getInt(Activator.ID, "server_port", DEFAULT_PORT, null)));
    }
}
