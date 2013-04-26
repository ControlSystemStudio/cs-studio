/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * The scan engine idea is based on the "ScanEngine" developed
 * by the Software Services Group (SSG),  Advanced Photon Source,
 * Argonne National Laboratory,
 * Copyright (c) 2011 , UChicago Argonne, LLC.
 *
 * This implementation, however, contains no SSG "ScanEngine" source code
 * and is not endorsed by the SSG authors.
 ******************************************************************************/
package org.csstudio.scan.client;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.logging.Logger;

import org.csstudio.scan.SystemSettings;
import org.csstudio.scan.server.ScanServer;

/** Connect to a {@link ScanServer}
 *
 *  <p>Example for connecting to scan server:
 *  <pre>
 *  ScanServer server = ScanServerConnector.connect();
 *  ...
 *  ... server.getScanInfos();
 *  ...
 *  ScanServerConnector.disconnect(server);
 *  </pre>
 *
 *  <p>Default host and port to which to connect are defined in the {@link ScanServer} class.
 *  For generic Java tools, including Jython, Matlab, ...
 *  they can be overriddden via Java system preferences <code>ScanServerHost</code>
 *  and <code>ScanServerPort</code>.
 *
 *  <p>Eclipse-based tools set the system properties from Eclipse preferences,
 *  so what's used in the end are the system preference values,
 *  but they can be configured from Eclipse preferences
 *  and the GUI tools include preference pages.
 *
 *  @see ScanServer
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ScanServerConnector
{
    /** Connect to {@link ScanServer} on host and port
     *  provided via system properties <code>ScanServerHost</code>
     *  and <code>ScanServerPort</code>.
     *  If not set, it defaults to "localhost" and the default server port.
     *
     *  @return {@link ScanServer}
     *  @throws Exception on error
     *  @see #disconnect(ScanServer)
     */
    public static ScanServer connect() throws Exception
    {
        final String host = SystemSettings.getServerHost();
        final int port = SystemSettings.getServerPort();
        return connect(host, port);
    }

    /** Connect to {@link ScanServer}
     *  @param hostname Host name where server is running
     *  @return {@link ScanServer}
     *  @throws Exception on error
     *  @see #disconnect(ScanServer)
     */
    public static ScanServer connect(final String hostname, final int port) throws Exception
    {
    	try
    	{
	        final Registry registry = LocateRegistry.getRegistry(hostname, port);
	        final ScanServer server = (ScanServer) registry.lookup(ScanServer.RMI_SCAN_SERVER_NAME);

	        Logger.getLogger(ScanServer.class.getName()).fine("Connected to " + server.getInfo());
	        return server;
    	}
    	catch (Exception ex)
    	{
    		throw new Exception("Cannot connect to Scan Server " + hostname + ":" + port, ex);
    	}
    }

    /** Disconnect from a scan server
     *  @param server Server to disconnect
     */
    public static void disconnect(final ScanServer server)
    {
        // For now there's nothing to do,
        // but with a different implementation there could,
        // so make all client code call disconnect to be prepared.
    }
}
