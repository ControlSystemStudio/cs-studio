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

/** Scan settings that use system properties instead of Eclipse Preferences
 *  allow non-Eclipse tools that can only access system properties to also use the scan system.
 *
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class SystemSettings
{
    /** Default host for scan server */
    final public static String DEFAULT_HOST = "localhost";

    /** Default port used by scan server's REST interface */
    final public static int DEFAULT_PORT = 4810;

    /** System property for overriding the scan server host */
    final public static String HOST_PROPERTY = "ScanServerHost";

    /** System property for overriding the scan server port */
    final public static String PORT_PROPERTY = "ScanServerPort";

	/** @return Scan server host name */
    public static String getServerHost()
    {
        String host = System.getProperty(HOST_PROPERTY);
        if (host != null)
            return host;
        return DEFAULT_HOST;
    }

    /** @return Scan server TCP port */
    public static int getServerPort()
    {
        String port = System.getProperty(PORT_PROPERTY);
        if (port != null)
        {
            try
            {
                return Integer.parseInt(port.trim());
            }
            catch (NumberFormatException ex)
            {
                Logger.getLogger(SystemSettings.class.getName()).log(Level.WARNING,
                        "Cannot parse scan server port", ex);
            }
        }
        return DEFAULT_PORT;
    }
}
