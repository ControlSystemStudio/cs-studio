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

import org.csstudio.scan.data.ScanData;
import org.csstudio.scan.data.ScanSample;
import org.csstudio.scan.data.ScanSampleFormatter;
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
    
    /** Create XML content for scan server info
     *  @param doc XML {@link Document}
     *  @param info {@link ScanServerInfo}
     *  @return XML {@link Element} for the server info
     */
    public static Element createXMLElement(final Document doc, final ScanServerInfo info)
    {
        final Element server = doc.createElement("server");
        
        Element el = doc.createElement("version");
        el.appendChild(doc.createTextNode(info.getVersion()));
        server.appendChild(el);

        el = doc.createElement("start_time");
        el.appendChild(doc.createTextNode(Long.toString(info.getStartTime().getTime())));
        server.appendChild(el);

        el = doc.createElement("beamline_config");
        el.appendChild(doc.createTextNode(info.getBeamlineConfig()));
        server.appendChild(el);

        el = doc.createElement("simulation_config");
        el.appendChild(doc.createTextNode(info.getSimulationConfig()));
        server.appendChild(el);

        el = doc.createElement("used_mem");
        el.appendChild(doc.createTextNode(Long.toString(info.getUsedMem())));
        server.appendChild(el);

        el = doc.createElement("max_mem");
        el.appendChild(doc.createTextNode(Long.toString(info.getMaxMem())));
        server.appendChild(el);
        
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
        
        Element el = doc.createElement("id");
        el.appendChild(doc.createTextNode(Long.toString(info.getId())));
        scan.appendChild(el);
        
        el = doc.createElement("name");
        el.appendChild(doc.createTextNode(info.getName()));
        scan.appendChild(el);

        el = doc.createElement("created");
        el.appendChild(doc.createTextNode(Long.toString(info.getCreated().getTime())));
        scan.appendChild(el);

        el = doc.createElement("state");
        el.appendChild(doc.createTextNode(info.getState().name()));
        scan.appendChild(el);

        el = doc.createElement("runtime");
        el.appendChild(doc.createTextNode(Long.toString(info.getRuntimeMillisecs())));
        scan.appendChild(el);

        if (info.getTotalWorkUnits() > 0)
        {
            el = doc.createElement("total_work_units");
            el.appendChild(doc.createTextNode(Long.toString(info.getTotalWorkUnits())));
            scan.appendChild(el);
    
            el = doc.createElement("performed_work_units");
            el.appendChild(doc.createTextNode(Long.toString(info.getPerformedWorkUnits())));
            scan.appendChild(el);
        }
        
        final Date finish = info.getFinishTime();
        if (finish != null)
        {
            el = doc.createElement("finish");
            el.appendChild(doc.createTextNode(Long.toString(finish.getTime())));
            scan.appendChild(el);
        }

        el = doc.createElement("address");
        el.appendChild(doc.createTextNode(Long.toString(info.getCurrentAddress())));
        scan.appendChild(el);
        
        el = doc.createElement("command");
        el.appendChild(doc.createTextNode(info.getCurrentCommand()));
        scan.appendChild(el);
        
        final String error = info.getError();
        if (error != null)
        {
            el = doc.createElement("error");
            el.appendChild(doc.createTextNode(error));
            scan.appendChild(el);
        }

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
        
            final Element name = doc.createElement("name");
            name.setTextContent(device_name);
            device.appendChild(name);

            final Element samples = doc.createElement("samples");
            for (ScanSample data_sample : data.getSamples(device_name))
            {
                final Element sample = doc.createElement("sample");
                sample.setAttribute("id", Long.toString(data_sample.getSerial()));
                
                final Element time = doc.createElement("time");
                time.setTextContent(Long.toString(data_sample.getTimestamp().getTime()));
                sample.appendChild(time);
                
                final Element value = doc.createElement("value");
                value.setTextContent(ScanSampleFormatter.asString(data_sample));
                sample.appendChild(value);

                samples.appendChild(sample);
            }
            device.appendChild(samples);
            
            result.appendChild(device);
        }

        return result;
    }

    /** Submit XML HTTP client
     *  @param doc {@link Document} to submit
     *  @param response {@link HttpServletResponse}
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
