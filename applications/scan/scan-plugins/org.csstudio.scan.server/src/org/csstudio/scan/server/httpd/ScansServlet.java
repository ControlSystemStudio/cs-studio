/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.server.httpd;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilderFactory;

import org.csstudio.scan.server.ScanInfo;
import org.csstudio.scan.server.ScanServer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/** Servlet for "/scans": listing scans, remove completed
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ScansServlet extends HttpServlet
{
    final private static long serialVersionUID = 1L;
    final private ScanServer scan_server;

    public ScansServlet(final ScanServer scan_server)
    {
        this.scan_server = scan_server;
    }

    /** Get scan information
     *  <p>GET scans - get all scan infos
     */
    @Override
    protected void doGet(final HttpServletRequest request,
            final HttpServletResponse response)
            throws ServletException, IOException
    {
        final Document doc;
        try
        {
            doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            final List<ScanInfo> scans = scan_server.getScanInfos();
            final Element root = doc.createElement("scans");
            doc.appendChild(root);
            for (ScanInfo info : scans)
            {
                final Element scan = ServletHelper.createXMLElement(doc, info);
                root.appendChild(scan);
            }
        }
        catch (Exception ex)
        {
            Logger.getLogger(getClass().getName()).log(Level.WARNING, "GET /scans error", ex);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex.getMessage());
            return;
        }
        try
        {
            ServletHelper.submitXML(doc, response);
        }
        catch (Exception ex)
        {
            Logger.getLogger(getClass().getName()).log(Level.WARNING, "GET /scans reply error", ex);
            // Can't send error to client because sending to client is the problem
        }
    }

    /** DELETE scans/completed: Remove completed scans
     *  Returns basic HTTP OK (200) on success, otherwise error
     */
    @Override
    protected void doDelete(final HttpServletRequest request,
            final HttpServletResponse response)
            throws ServletException, IOException
    {
        final String path = request.getPathInfo();
        try
        {
            if (! "/completed".equals(path))
                throw new Exception("Illegal path '/scans" + path + "'");
            scan_server.removeCompletedScans();
        }
        catch (Exception ex)
        {
            Logger.getLogger(getClass().getName()).log(Level.WARNING, "DELETE /scans/completed error", ex);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
            return;
        }
    }
}
