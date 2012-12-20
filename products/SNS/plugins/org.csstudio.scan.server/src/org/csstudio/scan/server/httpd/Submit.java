/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.server.httpd;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet for submitting a new scan
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class Submit extends HttpServlet
{
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(final HttpServletRequest request,
            final HttpServletResponse response)
            throws ServletException, IOException
    {
        // TODO Get scan XML from request
        // TODO Submit scan
        // TODO return scan ID
        final PrintWriter out = response.getWriter();
        out.println("Welcome...");
    }
}
