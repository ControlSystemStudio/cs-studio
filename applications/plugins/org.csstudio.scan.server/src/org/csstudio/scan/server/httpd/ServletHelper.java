/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.server.httpd;

import java.util.Date;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.csstudio.scan.PathUtil;
import org.csstudio.scan.data.ScanData;
import org.csstudio.scan.data.ScanSample;
import org.csstudio.scan.data.ScanSampleFormatter;
import org.csstudio.scan.device.DeviceInfo;
import org.csstudio.scan.server.ScanInfo;
import org.csstudio.scan.server.ScanServerInfo;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/** Servlet Helper
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ServletHelper
{
    /** Dump request headers 
     *  @param request
     */
    @SuppressWarnings("unchecked")
    public static void dumpHeaders(final HttpServletRequest request)
    {
        final Enumeration<String> headers = request.getHeaderNames();
        while (headers.hasMoreElements())
        {
            String header = headers.nextElement();
            System.out.println(header + " = " + request.getHeader(header));
        }
    }
    
    /** Create XML element for string
     *  @param doc Parent document
     *  @param name Name of XML element
     *  @param text Text content
     *  @return XML element
     */
    public static Element createXMLElement(final Document doc,
            final String name, final String text)
    {
        final Element el = doc.createElement(name);
        el.appendChild(doc.createTextNode(text));
        return el;
    }

    /** Create XML element for number
     *  @param doc Parent document
     *  @param name Name of XML element
     *  @param number Number content
     *  @return XML element
     */
    public static Element createXMLElement(final Document doc,
            final String name, final Long number)
    {
        return createXMLElement(doc, name, Long.toString(number));
    }

    /** Create XML element for date, encoded as milliseconds since epoch
     *  @param doc Parent document
     *  @param name Name of XML element
     *  @param date Date content
     *  @return XML element
     */
    public static Element createXMLElement(final Document doc,
            final String name, final Date date)
    {
        return createXMLElement(doc, name, date.getTime());
    }
    
    /** Create XML content for scan server info
     *  @param doc XML {@link Document}
     *  @param info {@link ScanServerInfo}
     *  @return XML {@link Element} for the server info
     */
    public static Element createXMLElement(final Document doc, final ScanServerInfo info)
    {
        final Element server = doc.createElement("server");
        server.appendChild(createXMLElement(doc, "version", info.getVersion()));
        server.appendChild(createXMLElement(doc, "start_time", info.getStartTime()));
        server.appendChild(createXMLElement(doc, "scan_config", info.getScanConfig()));
        // For older clients, also report as "beamline_config"
        server.appendChild(doc.createComment("beamline_config is deprecated, use scan_config"));
        server.appendChild(createXMLElement(doc, "beamline_config", info.getScanConfig()));
        
        server.appendChild(createXMLElement(doc, "simulation_config", info.getSimulationConfig()));
        
        server.appendChild(createXMLElement(doc, "script_paths", PathUtil.joinPaths(info.getScriptPaths())));
        server.appendChild(createXMLElement(doc, "macros", info.getMacros()));
        
        server.appendChild(createXMLElement(doc, "used_mem", info.getUsedMem()));
        server.appendChild(createXMLElement(doc, "max_mem", info.getMaxMem()));
        server.appendChild(createXMLElement(doc, "non_heap", info.getNonHeapUsedMem()));

        return server;
    }

    /** Create XML content for scan info
     *  @param doc XML {@link Document}
     *  @param info {@link ScanInfo}
     *  @return XML {@link Element} for the scan info
     */
    public static Element createXMLElement(final Document doc, final ScanInfo info)
    {
        final Element scan = doc.createElement("scan");
        
        scan.appendChild(createXMLElement(doc, "id", info.getId()));
        scan.appendChild(createXMLElement(doc, "name", info.getName()));
        scan.appendChild(createXMLElement(doc, "created", info.getCreated()));
        scan.appendChild(createXMLElement(doc, "state", info.getState().name()));
        scan.appendChild(createXMLElement(doc, "runtime", info.getRuntimeMillisecs()));

        if (info.getTotalWorkUnits() > 0)
        {
            scan.appendChild(createXMLElement(doc, "total_work_units", info.getTotalWorkUnits()));
            scan.appendChild(createXMLElement(doc, "performed_work_units", info.getPerformedWorkUnits()));
        }
        
        final Date finish = info.getFinishTime();
        if (finish != null)
            scan.appendChild(createXMLElement(doc, "finish", finish));

        scan.appendChild(createXMLElement(doc, "address", info.getCurrentAddress()));
        scan.appendChild(createXMLElement(doc, "command", info.getCurrentCommand()));
        
        final String error = info.getError();
        if (error != null)
            scan.appendChild(createXMLElement(doc, "error", error));

        return scan;
    }
    
    /** Create XML content for scan data
     *  @param doc XML {@link Document}
     *  @param data {@link ScanData}
     *  @return XML {@link Element} for the scan data
     */
    public static Node createXMLElement(final Document doc, final ScanData data)
    {
        final Element result = doc.createElement("data");
       
        for (String device_name : data.getDevices())
        {
            final Element device = doc.createElement("device");
            device.appendChild(createXMLElement(doc, "name", device_name));
        
            final Element samples = doc.createElement("samples");
            for (ScanSample data_sample : data.getSamples(device_name))
            {
                final Element sample = doc.createElement("sample");
                sample.setAttribute("id", Long.toString(data_sample.getSerial()));
                sample.appendChild(createXMLElement(doc, "time", data_sample.getTimestamp()));
                sample.appendChild(createXMLElement(doc, "value", ScanSampleFormatter.asString(data_sample)));
                samples.appendChild(sample);
            }
            
            device.appendChild(samples);
            result.appendChild(device);
        }
        return result;
    }

    /** Create XML content for device infos
     *  @param doc XML {@link Document}
     *  @param devices {@link DeviceInfo}s
     *  @return XML {@link Element} for the device infos
     */
    public static Node createXMLElement(final Document doc, final DeviceInfo... devices)
    {
        final Element result = doc.createElement("devices");
        
        for (DeviceInfo info : devices)
        {
            final Element device = doc.createElement("device");
            device.appendChild(createXMLElement(doc, "name", info.getName()));
            device.appendChild(createXMLElement(doc, "alias", info.getAlias()));
            if (! info.getStatus().isEmpty())
                device.appendChild(createXMLElement(doc, "status", info.getStatus()));
            result.appendChild(device);
        }

        return result;
    }

    /** Submit XML to HTTP client
     *  @param doc {@link Document} to submit
     *  @param response {@link HttpServletResponse} to which XML is submitted
     *  @throws Exception on error
     */
    public static void submitXML(final Document doc, final HttpServletResponse response) throws Exception
    {
        response.setContentType("text/xml");
        final Source xmlSource = new DOMSource(doc);
        final Result outputTarget = new StreamResult(response.getOutputStream());
        final Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        transformer.transform(xmlSource, outputTarget);
    }
}
