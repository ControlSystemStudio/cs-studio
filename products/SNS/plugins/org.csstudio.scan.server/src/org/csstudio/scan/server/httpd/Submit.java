/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.server.httpd;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.csstudio.scan.server.ScanServer;

/** Servlet for submitting a new scan
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class Submit extends HttpServlet
{
    final private static long serialVersionUID = 1L;
    final private ScanServer scan_server;

    public Submit(final ScanServer scan_server)
    {
        this.scan_server = scan_server;
    }

    @Override
    protected void doPost(final HttpServletRequest request,
            final HttpServletResponse response)
            throws ServletException, IOException
    {
        // Require XML
        final String format = request.getContentType();
        if (! format.endsWith("/xml"))
        {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Expecting XML content with scan");
            return;
        }

        // Determine name of scan
        String scan_name = request.getPathInfo();
        if (scan_name == null)
            scan_name = "Scan from " + request.getRemoteHost();
        else
        {
            if (scan_name.startsWith("/"))
                scan_name = scan_name.substring(1);
        }
        
        // Read scan commands
        final ByteArrayOutputStream buf = new ByteArrayOutputStream();
        StreamHelper.copy(request.getInputStream(), buf);
        final String scan_commands = buf.toString();
        
        // Submit scan
        final long scan_id = scan_server.submitScan(scan_name, scan_commands);
        
        // Return scan ID
        response.setContentType("text/xml");
        final PrintWriter out = response.getWriter();
        out.print("<id>");
        out.print(scan_id);
        out.println("</id>");
    }
}
