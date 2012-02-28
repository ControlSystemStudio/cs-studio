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
public class Preferences
{
	/** @return Path to the default beamline information file */
	public static String getBeamlineConfigPath()
	{
    	final IPreferencesService service = Platform.getPreferencesService();
    	return service.getString(Activator.ID, "beamline_config", "examples/beamline.xml", null);
	}

	/** @return Scan server host name */
    public static String getServerHost()
    {
        String host = System.getProperty(ScanServer.HOST_PROPERTY);
        if (host != null)
            return host;
        return ScanServer.DEFAULT_HOST;
    }

    /** @return Scan server TCP port */
    public static int getServerPort()
    {
        String port = System.getProperty(ScanServer.PORT_PROPERTY);
        if (port != null)
        {
            try
            {
                return Integer.parseInt(port.trim());
            }
            catch (NumberFormatException ex)
            {
                Logger.getLogger(Preferences.class.getName()).log(Level.WARNING,
                        "Cannot parse scan server port", ex);
            }
        }
        return ScanServer.DEFAULT_PORT;
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
