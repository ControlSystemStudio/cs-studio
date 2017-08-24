/*******************************************************************************
 * Copyright (c) 2017 Science & Technology Facilities Council.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.engine.server;

import java.io.IOException;
import java.util.logging.Level;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.csstudio.archive.engine.Activator;
import org.csstudio.archive.engine.model.EngineModel;
import org.csstudio.archive.engine.server.html.ChannelListResponse;
import org.csstudio.archive.engine.server.html.DebugResponse;
import org.csstudio.archive.engine.server.html.DisconnectedResponse;
import org.csstudio.archive.engine.server.html.EnvironmentResponse;
import org.csstudio.archive.engine.server.html.GroupResponse;
import org.csstudio.archive.engine.server.html.GroupsResponse;
import org.csstudio.archive.engine.server.html.HTMLChannelResponse;
import org.csstudio.archive.engine.server.html.MainResponse;
import org.csstudio.archive.engine.server.html.ResetResponse;
import org.csstudio.archive.engine.server.html.RestartResponse;
import org.csstudio.archive.engine.server.html.StopResponse;
import org.csstudio.archive.engine.server.json.JSONChannelResponse;

public class ResponseFactory extends HttpServlet {
    /** Required by Serializable */
    private static final long serialVersionUID = 1L;
    /** Model from which to serve info */
    final protected EngineModel model;

    final protected Page page;

    /** Construct <code>HttpServlet</code>
     *  @param title Page title
     */
    protected ResponseFactory(final EngineModel model, Page page)
    {
        this.page = page;
        this.model = model;
    }

    /** {@inheritDoc} */
    @Override
    protected void doGet(final HttpServletRequest req,
                    final HttpServletResponse resp)
                    throws ServletException, IOException
    {
        try
        {
            Format format;
            try {
                format = Format.valueOf(req.getParameter("format"));
            } catch (Exception e) {
                format = Format.html;
            }

            AbstractResponse responseWriter = null;
            switch (page) {
                case MAIN:
                    responseWriter = new MainResponse(model);
                    break;
                case CHANNEL:
                    if (format.equals(Format.html)) {
                        responseWriter = new HTMLChannelResponse(model);
                    } else if (format.equals(Format.json)) {
                        responseWriter = new JSONChannelResponse(model);
                    }
                    break;
                case CHANNEL_LIST:
                    responseWriter = new ChannelListResponse(model);
                    break;
                case DISCONNECTED:
                    responseWriter = new DisconnectedResponse(model);
                    break;
                case ENVIRONMENT:
                    responseWriter = new EnvironmentResponse(model);
                    break;
                case GROUP:
                    responseWriter = new GroupResponse(model);
                    break;
                case GROUPS:
                    responseWriter = new GroupsResponse(model);
                    break;
                case DEBUG:
                    responseWriter = new DebugResponse(model);
                    break;
                case RESET:
                    responseWriter = new ResetResponse(model);
                    break;
                case RESTART:
                    responseWriter = new RestartResponse(model);
                    break;
                case STOP:
                    responseWriter = new StopResponse(model);
                    break;
            }

            if (responseWriter != null) {
                responseWriter.fillResponse(req, resp);
            }
        }

        catch (Exception ex)
        {
            Activator.getLogger().log(Level.WARNING, "HTTP Server exception", ex);
            if (resp.isCommitted())
                return;
            resp.sendError(400, "HTTP Server exception" + ex.getMessage());
        }
    }
}
