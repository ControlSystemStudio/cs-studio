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
        http.registerResources("/", "/webroot", http_context);

        http.registerServlet("/main", new ResponseFactory(model, Page.MAIN), null, http_context);
        http.registerServlet("/groups", new ResponseFactory(model, Page.GROUPS), null, http_context);
        http.registerServlet("/disconnected", new ResponseFactory(model, Page.DISCONNECTED), null, http_context);
        http.registerServlet("/group", new ResponseFactory(model, Page.GROUP), null, http_context);
        http.registerServlet("/channel", new ResponseFactory(model, Page.CHANNEL), null, http_context);
        http.registerServlet("/channels", new ResponseFactory(model, Page.CHANNEL_LIST), null, http_context);
        http.registerServlet("/environment", new ResponseFactory(model, Page.ENVIRONMENT), null, http_context);
        http.registerServlet("/restart", new ResponseFactory(model, Page.RESTART), null, http_context);
        http.registerServlet("/reset", new ResponseFactory(model, Page.RESET), null, http_context);
        http.registerServlet("/stop", new ResponseFactory(model, Page.STOP), null, http_context);
        http.registerServlet("/debug", new ResponseFactory(model, Page.DEBUG), null, http_context);

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
