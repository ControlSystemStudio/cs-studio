/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan;

import org.csstudio.java.string.StringSplitter;
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
public class ScanSystemPreferences extends SystemSettings
{
    /** Default host for scan server */
    final public static String DEFAULT_HOST = "localhost";

    /** Default port used by scan server's REST interface */
    final public static int DEFAULT_PORT = 4810;

    /** System property for overriding the scan server host */
    final public static String HOST_PROPERTY = "ScanServerHost";

    /** System property for overriding the scan server port */
    final public static String PORT_PROPERTY = "ScanServerPort";
    
	/** @param path_spec Path elements joined by ","
     *  @return Separate path elements
     *  @throws Exception on parse error (missing end of quoted string)
     */
    public static String[] splitPath(final String path_spec) throws Exception
    {
        if (path_spec == null)
            return new String[0];
        return StringSplitter.splitIgnoreInQuotes(path_spec, ',', true);

    }

    /** @param paths Path elements
     *  @return Path elements joined by ","
     */
    public static String joinPaths(final String[] paths)
    {
        final StringBuilder buf = new StringBuilder();
        for (String path : paths)
        {
            if (buf.length() > 0)
                buf.append(", ");
            buf.append(path);
        }
        return buf.toString();
    }

    /** @return Path to the default beamline information file */
	public static String getBeamlineConfigPath()
	{
    	final IPreferencesService service = Platform.getPreferencesService();
    	return service.getString(Activator.ID, "beamline_config", "platform:/plugin/org.csstudio.scan/examples/beamline.xml", null);
	}

	/** @return Path to the default simulation information file */
	public static String getSimulationConfigPath()
	{
    	final IPreferencesService service = Platform.getPreferencesService();
    	return service.getString(Activator.ID, "simulation_config", "platform:/plugin/org.csstudio.scan/examples/simulation.xml", null);
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
