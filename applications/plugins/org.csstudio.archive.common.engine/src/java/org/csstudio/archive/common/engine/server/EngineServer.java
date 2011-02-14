/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.common.engine.server;

import javax.annotation.Nonnull;

import org.apache.log4j.Logger;
import org.csstudio.archive.common.engine.Activator;
import org.csstudio.archive.common.engine.model.EngineModel;
import org.csstudio.platform.httpd.HttpServiceHelper;
import org.csstudio.platform.logging.CentralLogger;
import org.osgi.framework.BundleContext;
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.HttpService;

/** Web server for the engine.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class EngineServer {
    private static final Logger LOG =
        CentralLogger.getInstance().getLogger(EngineServer.class);

    /** TCP port used by the web server */
    private final int _port;

    /** Construct and start the server
     *  @param model Model to serve
     *  @param port TCP port
     *  @throws Exception on error
     */
    public EngineServer(@Nonnull final EngineModel model,
                        final int port) throws Exception {
        this._port = port;
        final BundleContext context =
            Activator.getDefault().getBundle().getBundleContext();
        final HttpService http = HttpServiceHelper.createHttpService(context, port);

        final HttpContext httpContext = http.createDefaultHttpContext();
        http.registerResources("/", "/webroot", httpContext);

        http.registerServlet("/main", new MainResponse(model), null, httpContext);
        http.registerServlet("/groups", new GroupsResponse(model), null, httpContext);
        http.registerServlet("/disconnected", new DisconnectedResponse(model), null, httpContext);
        http.registerServlet("/group", new GroupResponse(model), null, httpContext);
        http.registerServlet("/channel", new ChannelResponse(model), null, httpContext);
        http.registerServlet("/channels", new ChannelListResponse(model), null, httpContext);
        http.registerServlet("/environment", new EnvironmentResponse(model), null, httpContext);
        http.registerServlet("/restart", new RestartResponse(model), null, httpContext);
        http.registerServlet("/reset", new ResetResponse(model), null, httpContext);
        http.registerServlet("/stop", new StopResponse(model), null, httpContext);
        http.registerServlet("/debug", new DebugResponse(model), null, httpContext);

        LOG.info("Engine HTTP Server port " + port);
    }

    /** Stop the server */
    public void stop() {
        try {
            HttpServiceHelper.stopHttpService(_port);
        } catch (final Exception ex) {
            LOG.warn(ex);
        }
    }
}
