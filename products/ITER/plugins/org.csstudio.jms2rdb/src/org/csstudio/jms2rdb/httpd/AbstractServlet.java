/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.jms2rdb.httpd;

import java.io.IOException;
import java.util.logging.Level;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.csstudio.jms2rdb.Activator;

/** Base class for servlets.
 *  @author Kay Kasemir
 *  reviewed by Katia Danilova 08/20/08
 */
@SuppressWarnings("nls")
abstract public class AbstractServlet extends HttpServlet
{
    /** Added default serial to avoid warning from java.io.Serializable */
    private static final long serialVersionUID = 1L;

    /** {@inheritDoc} */
    @Override
    protected void doGet(final HttpServletRequest req,
                    final HttpServletResponse resp)
                    throws ServletException, IOException
    {
        try
        {
            final HTMLWriter html = new HTMLWriter(resp, "JMS-to-RDB Tool");
            fillBody(html);
            html.close();
        }
        catch (Exception ex)
        {
            if (resp.isCommitted())
            {
                Activator.getLogger().log(Level.WARNING, "HTTP Server exception", ex);
                return;
            }
            resp.sendError(400, "HTTP Server exception" + ex.getMessage());
        }
    }

    /** Needs to be implemented by derived class to add HTML content
     *  @param html HTMLWriter to which to add HTML content
     */
    abstract protected void fillBody(HTMLWriter html);
}
