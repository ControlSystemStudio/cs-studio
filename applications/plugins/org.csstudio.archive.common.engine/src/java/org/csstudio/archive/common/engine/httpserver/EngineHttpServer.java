/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.common.engine.httpserver;

import javax.annotation.Nonnull;
import javax.servlet.ServletException;

import org.csstudio.archive.common.engine.ArchiveEngineActivator;
import org.csstudio.archive.common.engine.model.EngineModel;
import org.csstudio.platform.httpd.HttpServiceHelper;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Web server for the engine.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class EngineHttpServer {
    private static final String EX_MSG = "Engine HTTP server could not be instantiated.";

    private static final Logger LOG =
        LoggerFactory.getLogger(EngineHttpServer.class);

    /** TCP port used by the web server */
    private final int _port;

    /** Construct and start the server
     *  @param model Model to serve
     *  @param port TCP port
     * @throws EngineHttpServerException
     * @throws InvalidSyntaxException
     * @throws
     *  @throws Exception on error
     */
    public EngineHttpServer(@Nonnull final EngineModel model,
                            final int port) throws EngineHttpServerException {
        this._port = port;
        final BundleContext context =
            ArchiveEngineActivator.getDefault().getBundle().getBundleContext();
        HttpService http;
        try {
            http = HttpServiceHelper.createHttpService(context, port);

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

        } catch (final InvalidSyntaxException e) {
            throw new EngineHttpServerException(EX_MSG, e);
        } catch (final ServletException e) {
            throw new EngineHttpServerException(EX_MSG, e);
        } catch (final NamespaceException e) {
            throw new EngineHttpServerException(EX_MSG, e);
        } catch (final Exception e) {
            throw new EngineHttpServerException(EX_MSG, e);
        }
        LOG.info("Engine HTTP Server port " + port);
    }

    /** Stop the server */
    public void stop() {
        try {
            HttpServiceHelper.stopHttpService(_port);
        } catch (final Exception ex) {
            LOG.warn("Unknown exception", ex);
        }
    }
}
