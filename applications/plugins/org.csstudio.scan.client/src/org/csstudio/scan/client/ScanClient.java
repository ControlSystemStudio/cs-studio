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

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.csstudio.scan.PathUtil;
import org.csstudio.scan.SystemSettings;
import org.csstudio.scan.command.DOMHelper;
import org.csstudio.scan.data.ScanData;
import org.csstudio.scan.device.DeviceInfo;
import org.csstudio.scan.server.Scan;
import org.csstudio.scan.server.ScanInfo;
import org.csstudio.scan.server.ScanServerInfo;
import org.csstudio.scan.server.ScanState;
import org.csstudio.scan.server.SimulationResult;
import org.csstudio.scan.util.IOUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/** Client for remotely accessing the scan server.
 * 
 *  <p>Uses the REST-based web interface of the
 *  scan server to submit new scans, monitor
 *  and control existing scans.
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
        this(SystemSettings.getServerHost(),
             SystemSettings.getServerPort());
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
        // URI will properly escape content of path
        final URI uri = new URI("http", null, host, port, path, null, null);
        final URL url = uri.toURL();
        final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Content-Type", "text/xml");
        connection.setReadTimeout((int) SECONDS.toMillis(timeout));
        return connection;
    }

    /** POST data to connection
     *  @param connection {@link HttpURLConnection}
     *  @param text Data to post
     *  @throws Exception on error
     */
    private void post(final HttpURLConnection connection, final String text) throws Exception
    {
        writeData(connection, "POST", text);
    }    

    /** Write data to connection
     *  @param connection {@link HttpURLConnection}
     *  @param text Data to post
     *  @throws Exception on error
     */
    private void writeData(final HttpURLConnection connection, final String method, final String text) throws Exception
    {
        connection.setDoOutput(true);
        connection.setRequestMethod(method);
        final OutputStream body = connection.getOutputStream();
        body.write(text.getBytes());
        body.flush();
        body.close();
    }

    /** Check HTTP response
     *  @param connection {@link HttpURLConnection}
     *  @throws Exception Anything but "2xx" results in exception
     */
    private void checkResponse(final HttpURLConnection connection) throws Exception
    {
        final int code = connection.getResponseCode();
        if (code < 200  ||  code > 299)
            throw new Exception("HTTP Response code " + code + " (" + connection.getResponseMessage() + ")");
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
        final String current_commmand = DOMHelper.getSubelementString(node, "command", "");
        
        final Scan scan = new Scan(id, name, created);
        return new ScanInfo(scan, state, error, runtime_ms, finishtime_ms,
                performed_work_units, total_work_units, current_address, current_commmand);
    }

    /** Obtain overall scan server information
     *  @return {@link ScanServerInfo}
     *  @throws Exception on error
     */
    public ScanServerInfo getServerInfo() throws Exception
    {
        final HttpURLConnection connection = connect("/server/info");
        try
        {
            checkResponse(connection);
            final Element root_node = parseXML(connection);
            if (! "server".equals(root_node.getNodeName()))
                throw new Exception("Expected <server/>");
            
            final String version = DOMHelper.getSubelementString(root_node, "version");
            final Date start_time = new Date(DOMHelper.getSubelementLong(root_node, "start_time", 0));
            String scan_config;
            try
            {
                scan_config = DOMHelper.getSubelementString(root_node, "scan_config");
            }
            catch (Exception ex)
            {   // Support deprecated server that still uses beamline_config
                scan_config = DOMHelper.getSubelementString(root_node, "beamline_config");
            }
            final String simulation_config = DOMHelper.getSubelementString(root_node, "simulation_config", "");
            final long used_mem = DOMHelper.getSubelementLong(root_node, "used_mem", 0);
            final long max_mem = DOMHelper.getSubelementLong(root_node, "max_mem", 0);
            final long non_heap = DOMHelper.getSubelementLong(root_node, "non_heap", 0);
            final String[] paths = PathUtil.splitPath(DOMHelper.getSubelementString(root_node, "script_paths", ""));
            final String macros = DOMHelper.getSubelementString(root_node, "macros", "");
            return new ScanServerInfo(version, start_time,
                    scan_config, simulation_config, paths, macros, used_mem, max_mem, non_heap);
        }
        finally
        {
            connection.disconnect();
        }
    }

    /** Obtain information for all scans
     *  @return {@link List} of {@link ScanInfo}s
     *  @throws Exception on error
     */
    public List<ScanInfo> getScanInfos() throws Exception
    {
        final HttpURLConnection connection = connect("/scans");
        try
        {
            checkResponse(connection);
            final Element root_node = parseXML(connection);
            if (! "scans".equals(root_node.getNodeName()))
                throw new Exception("Expected <scans/>");
            
            Element node = DOMHelper.findFirstElementNode(root_node.getFirstChild(), "scan");
            final List<ScanInfo> infos = new ArrayList<>();
            while (node != null)
            {
                final ScanInfo info = parseScanInfo(node);
                infos.add(info);
                node = DOMHelper.findNextElementNode(node, "scan");
            }
            return infos;
        }
        finally
        {
            connection.disconnect();
        }
    }

    /** Obtain information for a scan
     *  @param id ID that uniquely identifies a scan (within JVM of the scan engine)
     *  @return {@link ScanInfo}
     *  @throws Exception on error
     */
    public ScanInfo getScanInfo(final long id) throws Exception
    {
        final HttpURLConnection connection = connect("/scan/" + id);
        try
        {
            checkResponse(connection);
            final Element root_node = parseXML(connection);
            if (! "scan".equals(root_node.getNodeName()))
                throw new Exception("Expected <scan/>");
            return parseScanInfo(root_node);
        }
        finally
        {
            connection.disconnect();
        }
    }
    
    /** Obtain commands for a scan
     *  @param id ID that uniquely identifies a scan (within JVM of the scan engine)
     *  @return XML text for scan commands
     *  @throws Exception on error
     */
    public String getScanCommands(final long id) throws Exception
    {
        final HttpURLConnection connection = connect("/scan/" + id + "/commands");
        try
        {
            checkResponse(connection);
            return IOUtils.toString(connection.getInputStream());
        }
        finally
        {
            connection.disconnect();
        }
    }
    
    /** Obtain data logged by a scan
     *  @param id ID that uniquely identifies a scan (within JVM of the scan engine)
     *  @return {@link ScanData}
     *  @throws Exception on error
     */
    public ScanData getScanData(final long id) throws Exception
    {
        final HttpURLConnection connection = connect("/scan/" + id + "/data");
        try
        {
            checkResponse(connection);
            final InputStream stream = connection.getInputStream();
            final ScanDataSAXHandler handler = new ScanDataSAXHandler();
            final SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
            parser.parse(stream, handler);
            return handler.getScanData();
        }
        finally
        {
            connection.disconnect();
        }
    }

    /** Get serial of last logged sample.
     *
     *  <p>Can be used to determine if there are new samples
     *  that should be fetched via <code>getScanData()</code>
     *
     *  @param id ID that uniquely identifies a scan
     *  @return Serial of last sample in scan data or -1 if nothing has been logged
     *  @throws Exception on error
     *  @see #getScanData(long)
     */
    public long getLastScanDataSerial(final long id) throws Exception
    {
        final HttpURLConnection connection = connect("/scan/" + id + "/last_serial");
        try
        {
            checkResponse(connection);
            final Element root_node = parseXML(connection);
            if (! "serial".equals(root_node.getNodeName()))
                throw new Exception("Expected <serial/>");
            return Long.parseLong(root_node.getFirstChild().getNodeValue());
        }
        finally
        {
            connection.disconnect();
        }
    }

    /** Obtain devices used by a scan
     *  @param id ID that uniquely identifies a scan, or -1 to get default devices
     *  @return {@link ScanData}
     *  @throws Exception on error
     */
    public Collection<DeviceInfo> getScanDevices(final long id) throws Exception
    {
        final HttpURLConnection connection = connect("/scan/" + id + "/devices");
        try
        {
            checkResponse(connection);
            final Element root_node = parseXML(connection);
            if (! "devices".equals(root_node.getNodeName()))
                throw new Exception("Expected <devices/>");
            Element node = DOMHelper.findFirstElementNode(root_node.getFirstChild(), "device");
            final Collection<DeviceInfo> devices = new ArrayList<>();
            while (node != null)
            {
                devices.add(
                    new DeviceInfo(
                        DOMHelper.getSubelementString(node, "name"),
                        DOMHelper.getSubelementString(node, "alias"),
                        DOMHelper.getSubelementString(node, "status", "")));
                node = DOMHelper.findNextElementNode(node, "device");
            }
            return devices;
        }
        finally
        {
            connection.disconnect();
        }
    }
    
    /** Submit a scan for execution
     *  @param name Name of the new scan
     *  @param xml_commands XML commands of the scan to submit
     *  @return Scan ID
     *  @throws Exception on error
     */
    public long submitScan(final String name, final String xml_commands) throws Exception
    {
        final HttpURLConnection connection = connect("/scan/" + name);
        try
        {
            post(connection, xml_commands);
            checkResponse(connection);
            // Obtain returned scan ID
            final Element root_node = parseXML(connection);
            if (! "id".equals(root_node.getNodeName()))
                throw new Exception("Expected <id/>");
            final long id = Long.parseLong(root_node.getFirstChild().getNodeValue());
            return id;
        }
        finally
        {
            connection.disconnect();
        }
    }

    /** Submit a scan for simulation
     *  @param xml_commands XML commands of the scan to submit
     *  @return Scan ID
     *  @throws Exception on error
     */
    public SimulationResult simulateScan(final String xml_commands) throws Exception
    {
        final HttpURLConnection connection = connect("/simulate");
        try
        {
            post(connection, xml_commands);
            checkResponse(connection);
            // Decode simulation result
            final Element root_node = parseXML(connection);
            if (! "simulation".equals(root_node.getNodeName()))
                throw new Exception("Expected <simulation/>");
            return new SimulationResult(
                DOMHelper.getSubelementDouble(root_node, "seconds", 0.0),
                DOMHelper.getSubelementString(root_node, "log"));
        }
        finally
        {
            connection.disconnect();
        }
    }

    /** Put scan into different state via command
     *  @param id ID that uniquely identifies a scan (within JVM of the scan engine)
     *  @param command Command to send
     *  @throws Exception on error
     */
    private void sendScanCommand(final long id, final String command) throws Exception
    {
        final HttpURLConnection connection = connect("/scan/" + id + "/" + command);
        try
        {
            connection.setRequestMethod("PUT");
            checkResponse(connection);
        }
        finally
        {
            connection.disconnect();
        }
    }
    
    /** Put running scan into paused state
     *  @param id ID that uniquely identifies a scan (within JVM of the scan engine)
     *  @throws Exception on error
     */
    public void pauseScan(final long id) throws Exception
    {
        sendScanCommand(id, "pause");
    }

    /** Resume a paused scan
     *  @param id ID that uniquely identifies a scan (within JVM of the scan engine)
     *  @throws Exception on error
     */
    public void resumeScan(final long id) throws Exception
    {
        sendScanCommand(id, "resume");
    }

    /** Abort a running or paused scan
     *  @param id ID that uniquely identifies a scan (within JVM of the scan engine)
     *  @throws Exception on error
     */
    public void abortScan(final long id) throws Exception
    {
        sendScanCommand(id, "abort");
    }

    /** Submit patch to running scan
     *  @param id ID that uniquely identifies a scan
     *  @param address Address of command within scan
     *  @param property Property of command to patch
     *  @param value New value for that property
     *  @throws Exception on error
     */
    public void patchScan(final long id, final long address,
            final String property, final Object value) throws Exception
    {
        final HttpURLConnection connection = connect("/scan/" + id + "/patch");
        try
        {
            final String patch =
                "<patch>"
                + "<address>" + address + "</address>"
                + "<property>" + property + "</property>"
                + "<value>" + value + "</value>"
                + "</patch>";
            writeData(connection, "PUT", patch);
            checkResponse(connection);
        }
        finally
        {
            connection.disconnect();
        }
    }

    
    /** Remove a completed scan
     *  @param id ID that uniquely identifies a scan (within JVM of the scan engine)
     *  @throws Exception on error
     */
    public void removeScan(final long id) throws Exception
    {
        final HttpURLConnection connection = connect("/scan/" + id);
        try
        {
            connection.setRequestMethod("DELETE");
            checkResponse(connection);
        }
        finally
        {
            connection.disconnect();
        }
    }
    
    /** Remove all completed scans
     *  @throws Exception on error
     */
    public void removeCompletedScans() throws Exception
    {
        final HttpURLConnection connection = connect("/scans/completed");
        try
        {
            connection.setRequestMethod("DELETE");
            checkResponse(connection);
        }
        finally
        {
            connection.disconnect();
        }
    }
}
