/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.engine.server;

import java.util.logging.Level;

import org.csstudio.archive.engine.Activator;
import org.csstudio.archive.engine.model.EngineModel;
import org.csstudio.platform.httpd.HttpServiceHelper;
import org.osgi.framework.BundleContext;
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.HttpService;

/** Web server for the engine.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class EngineServer
{
    /** TCP port used by the web server */
    final private int port;

    /** Construct and start the server
     *  @param model Model to serve
     *  @param port TCP port
     *  @throws Exception on error
     */
    public EngineServer(final EngineModel model,
                        final int port) throws Exception
    {
        this.port = port;
        final BundleContext context =
            Activator.getDefault().getBundle().getBundleContext();
        final HttpService http = HttpServiceHelper.createHttpService(context, port);

        final HttpContext http_context = http.createDefaultHttpContext();

        ResponseFactory respFactory = new ResponseFactory(model);
        http.registerResources("/", "/webroot", http_context);

        http.registerServlet("/main", new Servlet(Page.MAIN, respFactory), null, http_context);
        http.registerServlet("/groups", new Servlet(Page.GROUPS, respFactory), null, http_context);
        http.registerServlet("/disconnected", new Servlet(Page.DISCONNECTED, respFactory), null, http_context);
        http.registerServlet("/group", new Servlet(Page.GROUP, respFactory), null, http_context);
        http.registerServlet("/channel", new Servlet(Page.CHANNEL, respFactory), null, http_context);
        http.registerServlet("/channels", new Servlet(Page.CHANNEL_LIST, respFactory), null, http_context);
        http.registerServlet("/environment", new Servlet(Page.ENVIRONMENT, respFactory), null, http_context);
        http.registerServlet("/restart", new Servlet(Page.RESTART, respFactory), null, http_context);
        http.registerServlet("/reset", new Servlet(Page.RESET, respFactory), null, http_context);
        http.registerServlet("/stop", new Servlet(Page.STOP, respFactory), null, http_context);
        http.registerServlet("/debug", new Servlet(Page.DEBUG, respFactory), null, http_context);

        // When formatting the port via {0}, that could result in "4,812".
        // So format the URL outside of the logger.
        Activator.getLogger().log(Level.INFO, "Engine HTTP Server on {0}", "http://localhost:" + port + "/main");
    }

    /** Stop the server */
    public void stop()
    {
        try
        {
            HttpServiceHelper.stopHttpService(port);
        }
        catch (Exception ex)
        {
            Activator.getLogger().log(Level.WARNING, "Engine HTTP Server shutdown error", ex);
        }
    }
}
