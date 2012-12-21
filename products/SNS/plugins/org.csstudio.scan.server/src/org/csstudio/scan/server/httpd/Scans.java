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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.csstudio.scan.server.ScanInfo;
import org.csstudio.scan.server.ScanServer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/** Servlet for listing scans
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class Scans extends HttpServlet
{
    final private static long serialVersionUID = 1L;
    final private ScanServer scan_server;

    public Scans(final ScanServer scan_server)
    {
        this.scan_server = scan_server;
    }

    @Override
    protected void doGet(final HttpServletRequest request,
            final HttpServletResponse response)
            throws ServletException, IOException
    {
        final List<ScanInfo> scans = scan_server.getScanInfos();

        response.setContentType("text/xml");
        
        try
        {
            final Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            final Element root = doc.createElement("scans");
            doc.appendChild(root);
            for (ScanInfo info : scans)
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

                el = doc.createElement("percentage");
                el.appendChild(doc.createTextNode(Integer.toString(info.getPercentage())));
                scan.appendChild(el);

                el = doc.createElement("finish");
                el.appendChild(doc.createTextNode(Long.toString(info.getFinishTime().getTime())));
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
                
                root.appendChild(scan);
            }
            
            final Source xmlSource = new DOMSource(doc);
            final Result outputTarget = new StreamResult(response.getOutputStream());
            final Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            transformer.transform(xmlSource, outputTarget);
        }
        catch (Exception ex)
        {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex.getMessage());
            ex.printStackTrace();
        }
    }
}
