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
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.csstudio.scan.command.DOMHelper;
import org.csstudio.scan.data.ScanData;
import org.csstudio.scan.device.DeviceInfo;
import org.csstudio.scan.server.ScanInfo;
import org.csstudio.scan.server.ScanServer;
import org.csstudio.scan.util.IOUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/** Servlet for "/scan/*": submitting a new scan, deleting (aborting) a current one
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ScanServlet extends HttpServlet
{
    final private static long serialVersionUID = 1L;
    final private ScanServer scan_server;

    public ScanServlet(final ScanServer scan_server)
    {
        this.scan_server = scan_server;
    }

    /** POST scan/{name}: Submit a new, named, scan
     *  Returns ID of new scan
     */
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
        final String scan_commands = IOUtils.toString(request.getInputStream());
        
        // Submit scan
        try
        {
            final long scan_id = scan_server.submitScan(scan_name, scan_commands);
            
            // Return scan ID
            response.setContentType("text/xml");
            final PrintWriter out = response.getWriter();
            out.print("<id>");
            out.print(scan_id);
            out.println("</id>");
        }
        catch (Exception ex)
        {
            throw new ServletException("Error submitting scan", ex);
        }
    }
    
    /** 'Put' scan into new state
     *  <p>PUT scan/{id}/pause: Pause running scan
     *  <p>PUT scan/{id}/resume: Resume paused scan
     *  <p>PUT scan/{id}/abort: Abort running or paused scan
     *  <p>PUT scan/{id}/patch: Update property of a scan command<br>
     *     Requires description of what to update:
     *     <pre>
     *     &lt;patch>
     *        &lt;address>10&lt;/address>
     *        &lt;property>name_of_property&lt;/property>
     *        &lt;value>new_value&lt;/value>
     *     &lt;/patch>
     *     </pre>
     *  Returns basic HTTP OK (200) on success, otherwise error
     */
    @Override
    protected void doPut(final HttpServletRequest request,
            final HttpServletResponse response)
            throws ServletException, IOException
    {
        final RequestPath path = new RequestPath(request);
        try
        {
            if (path.size() != 2)
                throw new Exception("Missing scan ID and command");
            final long id = path.getLong(0);
            final String command = path.getString(1);
            switch (command)
            {
            case "pause":
                scan_server.pause(id);
                break;
            case "resume":
                scan_server.resume(id);
                break;
            case "abort":
                scan_server.abort(id);
                break;
            case "patch":
                final DocumentBuilder docBuilder =
                    DocumentBuilderFactory.newInstance().newDocumentBuilder();
                final Document doc = docBuilder.parse(request.getInputStream());
                final Element root_node = doc.getDocumentElement();
                if (! "patch".equals(root_node.getNodeName()))
                    throw new Exception("Expected <patch>");
                final long address = DOMHelper.getSubelementLong(root_node, "address", -1);
                final String property = DOMHelper.getSubelementString(root_node, "property");
                final String value = DOMHelper.getSubelementString(root_node, "value");
                scan_server.updateScanProperty(id, address, property, value);
                break;
            default:
                throw new Exception("Unknown command '" + command + "'");
            }
        }
        catch (Exception ex)
        {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
            return;
        }
    }

    /** DELETE scan/{id}: Remove a scan
     *  Returns basic HTTP OK (200) on success, otherwise error
     */
    @Override
    protected void doDelete(final HttpServletRequest request,
            final HttpServletResponse response)
            throws ServletException, IOException
    {
        // Determine scan ID and requested object
        final RequestPath path = new RequestPath(request);
        try
        {
            if (path.size() != 1)
                throw new Exception("Missing scan ID");
            final long id = path.getLong(0);
            scan_server.remove(id);
        }
        catch (Exception ex)
        {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
            return;
        }
    }    
    
    /** Get scan information
     *  <p>GET scan/{id} - get scan info
     *  <p>GET scan/{id}/commands - get scan commands
     *  <p>GET scan/{id}/data - get scan data
     *  <p>GET scan/{id}/last_serial - get scan data's last serial
     *  <p>GET scan/{id}/devices - get devices used by a scan
     */
    @Override
    protected void doGet(final HttpServletRequest request,
            final HttpServletResponse response)
            throws ServletException, IOException
    {
        // Determine scan ID and requested object
        final RequestPath path = new RequestPath(request);
        final long id;
        try
        {
            if (path.size() < 1)
                throw new Exception("Missing scan ID");
            id = path.getLong(0);
        }
        catch (Exception ex)
        {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
            return;
        }
        
        // Return requested object
        final String object = path.size() < 2 ? null : path.getString(1);
        try
        {
            if (object == null)
            {   // Get Scan info
                final ScanInfo info = scan_server.getScanInfo(id);
                if (info == null)
                    throw new Exception("Unknown scan ID " + id);
                final Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
                doc.appendChild(ServletHelper.createXMLElement(doc, info));
                ServletHelper.submitXML(doc, response);
            }
            else if ("commands".equalsIgnoreCase(object))
            {   // Get commands
                response.setContentType("text/xml");
                final ServletOutputStream out = response.getOutputStream();
                out.print(scan_server.getScanCommands(id));
                out.flush();
            }
            else if ("data".equalsIgnoreCase(object))
            {   // Get data
                final ScanData data = scan_server.getScanData(id);
                final Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
                doc.appendChild(ServletHelper.createXMLElement(doc, data));
                ServletHelper.submitXML(doc, response);
            }
            else if ("last_serial".equalsIgnoreCase(object))
            {   // Get last serial of data
                final long last_serial = scan_server.getLastScanDataSerial(id);
                final ServletOutputStream out = response.getOutputStream();
                out.print("<serial>" + last_serial + "</serial>");
                out.flush();
            }
            else if ("devices".equalsIgnoreCase(object))
            {   // Get devices
                final DeviceInfo[] devices = scan_server.getDeviceInfos(id);
                final Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
                doc.appendChild(ServletHelper.createXMLElement(doc, devices));
                ServletHelper.submitXML(doc, response);
            }
            else
                throw new Exception("Unknown request object " + object);
        }
        catch (Exception ex)
        {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex.getMessage());
            ex.printStackTrace();
        }
    }    
}
