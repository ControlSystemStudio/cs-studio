/*******************************************************************************
 * Copyright (c) 2013 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.server.httpd;

import static org.csstudio.scan.server.app.Application.logger;

import java.io.IOException;
import java.util.logging.Level;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilderFactory;

import org.csstudio.scan.server.ScanServer;
import org.csstudio.scan.server.ScanServerInfo;
import org.w3c.dom.Document;

/** Servlet for "/server/*": General {@link ScanServer} info
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ServerServlet extends HttpServlet
{
    final private static long serialVersionUID = 1L;
    final private ScanServer scan_server;

    public ServerServlet(final ScanServer scan_server)
    {
        this.scan_server = scan_server;
    }

    @Override
    protected void doGet(final HttpServletRequest request,
            final HttpServletResponse response)
            throws ServletException, IOException
    {
        final RequestPath path = new RequestPath(request);
        final String detail;
        try
        {
            if (path.size() != 1)
                throw new Exception("Missing '/server/*' request detail");
            detail = path.getString(0);
        }
        catch (Exception ex)
        {
            logger.log(Level.WARNING, "GET /server error", ex);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
            return;
        }

        final Document doc;
        try
        {
            doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            switch (detail)
            {
            case "info":
                {
                    final ScanServerInfo info = scan_server.getInfo();
                    doc.appendChild(ServletHelper.createXMLElement(doc, info));
                }
                break;
            default:
                throw new Exception("Invalid request /server/" + detail);
            }
        }
        catch (Exception ex)
        {
            logger.log(Level.WARNING, "GET /server error", ex);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex.getMessage());
            return;
        }
        try
        {
            ServletHelper.submitXML(doc, response);
        }
        catch (Exception ex)
        {
            logger.log(Level.WARNING, "GET /server reply error", ex);
            // Can't send error to client because sending to client is the problem
        }
    }
}
