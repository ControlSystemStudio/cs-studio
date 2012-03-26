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

import org.csstudio.scan.server.ScanServer;

/** Connect to a {@link ScanServer}
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ScanServerConnector
{
    /** Connect to {@link ScanServer} on host and port
     *  provided via system properties <code>ScanServerHost</code>
     *  and <code>ScanServerPort</code>
     *
     *  @return {@link ScanServer}
     *  @throws Exception on error
     *  @see #disconnect(ScanServer)
     */
    public static ScanServer connect() throws Exception
    {
        String host = System.getProperty(ScanServer.HOST_PROPERTY);
        if (host == null)
            host = ScanServer.DEFAULT_HOST;
        int port;
        try
        {
            port = Integer.parseInt(System.getProperty(ScanServer.PORT_PROPERTY));
        }
        catch (Throwable ex)
        {
            port = ScanServer.DEFAULT_PORT;
        }
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
        final Registry registry = LocateRegistry.getRegistry(hostname, port);
        final ScanServer server = (ScanServer) registry.lookup(ScanServer.RMI_SCAN_SERVER_NAME);

        Logger.getLogger(ScanServer.class.getName()).fine("Connected to " + server.getInfo());

        return server;
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
