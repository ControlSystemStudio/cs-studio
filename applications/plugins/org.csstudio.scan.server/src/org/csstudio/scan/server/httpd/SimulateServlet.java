/*******************************************************************************
 * Copyright (c) 2013 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.server.httpd;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.csstudio.scan.server.ScanServer;
import org.csstudio.scan.server.SimulationResult;
import org.csstudio.scan.util.IOUtils;

/** Servlet for "/simulate": submitting a scan for simulation
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class SimulateServlet extends HttpServlet
{
    final private static long serialVersionUID = 1L;
    final private ScanServer scan_server;

    public SimulateServlet(final ScanServer scan_server)
    {
        this.scan_server = scan_server;
    }

    /** POST simulate: Submit a scan for simulation
     *  Returns of the simulation
     */
    @Override
    protected void doPost(final HttpServletRequest request,
            final HttpServletResponse response)
            throws ServletException, IOException
    {
        // Require XML: "text/xml", "text/xml; charset=UTF-8", ...
        final String format = request.getContentType();
        if (! format.contains("/xml"))
        {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Expecting XML content with scan, got format '" + format + "'");
            return;
        }

        // Read scan commands
        final String scan_commands = IOUtils.toString(request.getInputStream());
        
        // Simulate scan
        try
        {
            final SimulationResult simulation = scan_server.simulateScan(scan_commands);
            
            // Return scan ID
            response.setContentType("text/xml");
            final PrintWriter out = response.getWriter();
            out.println("<simulation>");
            out.print("  <log>");
            out.print("<![CDATA[");
            out.print(simulation.getSimulationLog());
            out.println("]]>");
            out.println("  </log>");
            out.println("  <seconds>" + simulation.getSimulationSeconds() + "</seconds>");
            out.println("</simulation>");
        }
        catch (Exception ex)
        {
            Logger.getLogger(getClass().getName()).log(Level.WARNING, "Error simulating scan", ex);
            throw new ServletException("Error simulating scan", ex);
        }
    }
}
