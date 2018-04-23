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

/**
 * The servlet that gets the http request from the user and interprets it.
 * @author Dominic Oram
 *
 */
public class Servlet extends HttpServlet {
    /** Required by Serializable */
    private static final long serialVersionUID = 1L;

    /** Factory from which to get the response */
    final protected ResponseFactory factory;

    /** The page that this servlet relates to */
    final protected Page page;

    /** Construct <code>HttpServlet</code>
     *  @param title Page title
     */
    protected Servlet(final Page page, final ResponseFactory factory)
    {
        this.page = page;
        this.factory = factory;

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

            factory.getResponse(page, format).fillResponse(req, resp);;
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
