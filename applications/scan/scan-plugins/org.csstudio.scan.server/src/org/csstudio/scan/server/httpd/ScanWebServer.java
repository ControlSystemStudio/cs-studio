/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.server.httpd;

import org.csstudio.platform.httpd.HttpServiceHelper;
import org.csstudio.scan.server.ScanServer;
import org.osgi.framework.BundleContext;
import org.osgi.service.http.HttpService;

/** Web server for {@link ScanServer} monitor/control
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ScanWebServer
{
    final private int port;
    
    public ScanWebServer(final BundleContext context,
            final ScanServer scan_server, final int port) throws Exception
    {
        this.port = port;
        HttpService httpd = HttpServiceHelper.createHttpService(context, port);
        httpd.registerResources("/", "/webroot", null);
        httpd.registerServlet("/server", new ServerServlet(scan_server), null, null);
        httpd.registerServlet("/simulate", new SimulateServlet(scan_server), null, null);
        httpd.registerServlet("/scans", new ScansServlet(scan_server), null, null);
        httpd.registerServlet("/scan", new ScanServlet(scan_server), null, null);
    }

    public void stop() throws Exception
    {
        HttpServiceHelper.stopHttpService(port);
    }
}
