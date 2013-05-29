/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * The scan engine idea is based on the "ScanEngine" developed
 * by the Software Services Group (SSG),  Advanced Photon Source,
 * Argonne National Laboratory,
 * Copyright (c) 2011 , UChicago Argonne, LLC.
 *
 * This implementation, however, contains no SSG "ScanEngine" source code
 * and is not endorsed by the SSG authors.
 ******************************************************************************/
package org.csstudio.scan.client;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.csstudio.scan.command.DOMHelper;
import org.csstudio.scan.server.Scan;
import org.csstudio.scan.server.ScanInfo;
import org.csstudio.scan.server.ScanServer;
import org.csstudio.scan.server.ScanState;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/** Scan server client
 * 
 *  <p>REST-based implementation.
 *
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ScanClient
{
    final private String host;
    final private int port;
    
    /** Timeout in seconds */
    final private long timeout = 10;
    
    /** Initialize */
    public ScanClient()
    {
        this(ScanServer.DEFAULT_HOST, ScanServer.DEFAULT_PORT + 2);
    }
    
    /** Initialize
     *  @param host Scan server host
     *  @param port Scan server port
     */
    public ScanClient(final String host, final int port)
    {
        this.host = host;
        this.port = port;
    }
    
    /** Connect to "http://server:port/path"
     *  @param path Path to use in scan server REST interface
     *  @return {@link HttpURLConnection}
     *  @throws Exception on error
     */
    private HttpURLConnection connect(final String path) throws Exception
    {
        final URL url = new URL("http", host, port, path);
        final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Content-Type", "text/xml");
        connection.setReadTimeout((int) SECONDS.toMillis(timeout));
        return connection;
    }

    /** Check HTTP response
     *  @param connection {@link HttpURLConnection}
     *  @throws Exception Anything but "2xx" results in exception
     */
    private void checkResponse(final HttpURLConnection connection) throws Exception
    {
        final int code = connection.getResponseCode();
        if (code < 200  ||  code > 299)
            throw new Exception("HTTP Response code " + code);
    }

    /** Parse XML document from connection
     *  @param connection {@link HttpURLConnection}
     *  @return Root {@link Element} for parsed XML
     *  @throws Exception on error
     */
    private Element parseXML(final HttpURLConnection connection)
            throws Exception
    {
        final DocumentBuilder docBuilder =
                DocumentBuilderFactory.newInstance().newDocumentBuilder();
        final Document doc = docBuilder.parse(connection.getInputStream());
        doc.getDocumentElement().normalize();
        final Element root_node = doc.getDocumentElement();
        return root_node;
    }

    /** Parse ScanInfo from XML
     *  @param node Node that should contain {@link ScanInfo}
     *  @return {@link ScanInfo}
     *  @throws Exception on error
     */
    private ScanInfo parseScanInfo(final Element node) throws Exception
    {
        final int id = DOMHelper.getSubelementInt(node, "id", -1);
        final String name = DOMHelper.getSubelementString(node, "name");
        final Date created = new Date(DOMHelper.getSubelementLong(node, "created", 0));
        final ScanState state = ScanState.valueOf(DOMHelper.getSubelementString(node, "state"));
        final String error = DOMHelper.getSubelementString(node, "error", null);
        final long runtime_ms = DOMHelper.getSubelementLong(node, "runtime", 0);
        final long total_work_units = DOMHelper.getSubelementLong(node, "total_work_units", 0);
        final long performed_work_units = DOMHelper.getSubelementLong(node, "performed_work_units", 0);
        final long finishtime_ms = DOMHelper.getSubelementLong(node, "finish", 0);
        final long current_address = DOMHelper.getSubelementLong(node, "address", 0);
        final String current_commmand = DOMHelper.getSubelementString(node, "commmand", "");
        
        final Scan scan = new Scan(id, name, created);
        return new ScanInfo(scan, state, error, runtime_ms, finishtime_ms,
                performed_work_units, total_work_units, current_address, current_commmand);
    }

    /** Obtain information for all scans
     *  @return {@link List} of {@link ScanInfo}s
     *  @throws Exception on error
     */
    public List<ScanInfo> getScanInfos() throws Exception
    {
        final HttpURLConnection connection = connect("/scans"); 
        checkResponse(connection);
        final Element root_node = parseXML(connection);
        if (! "scans".equals(root_node.getNodeName()))
            throw new Exception("Expected <scans/>");
        
        final List<ScanInfo> infos = new ArrayList<>();
        Element node = DOMHelper.findFirstElementNode(root_node.getFirstChild(), "scan");
        while (node != null)
        {
            final ScanInfo info = parseScanInfo(node);
            infos.add(info);
            node = DOMHelper.findNextElementNode(node, "scan");
        }
        
        connection.disconnect();
        return infos;
    }

    /** Obtain information for a scan
     *  @param id ID that uniquely identifies a scan (within JVM of the scan engine)
     *  @return {@link ScanInfo}
     *  @throws Exception on error
     */
    public ScanInfo getScanInfo(final long id) throws Exception
    {
        final HttpURLConnection connection = connect("/scan/" + id);
        checkResponse(connection);
        final Element root_node = parseXML(connection);
        if (! "scan".equals(root_node.getNodeName()))
            throw new Exception("Expected <scan/>");
        
        final ScanInfo info = parseScanInfo(root_node);
        connection.disconnect();
        return info;
    }
    
    /** Obtain commands for a scan
     *  @param id ID that uniquely identifies a scan (within JVM of the scan engine)
     *  @return XML text for scan commands
     *  @throws Exception on error
     */
    public String getScanCommands(final long id) throws Exception
    {
        final HttpURLConnection connection = connect("/scan/" + id + "/commands"); 
        checkResponse(connection);
        final String xml = IOUtils.toString(connection.getInputStream());
        connection.disconnect();
        return xml;
    }
    
    // TODO getScanData(final long id) throws Exception
    
    public long submitScan(final String name, final String xml_commands) throws Exception
    {
        final HttpURLConnection connection = connect("/scan/" + name);

        // Send commands
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        final OutputStream body = connection.getOutputStream();
        body.write(xml_commands.getBytes());
        body.flush();
        body.close();
        
        // Obtain returned scan ID
        checkResponse(connection);
        final Element root_node = parseXML(connection);
        if (! "id".equals(root_node.getNodeName()))
            throw new Exception("Expected <id/>");
        final long id = Long.parseLong(root_node.getFirstChild().getNodeValue());
        connection.disconnect();
        return id;
    }
}
