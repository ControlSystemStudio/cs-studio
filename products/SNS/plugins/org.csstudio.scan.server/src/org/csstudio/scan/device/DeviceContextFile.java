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
package org.csstudio.scan.device;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.csstudio.apputil.xml.DOMHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/** Helper for handling device context files
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class DeviceContextFile
{
	private static final String XML_BEAMLINE = "beamline";
    private static final String XML_DEVICES = "devices";
	private static final String XML_PV = "pv";
	private static final String XML_NAME = "name";
	private static final String XML_ALIAS = "alias";
    private static final String XML_SCAN = "scan";
    private static final String XML_LOG = "log";

	/** Read device configuration from XML file
	 *  @param config_file_name Name of XML file
	 *  @return {@link DeviceContext} initialized from file
	 *  @throws Exception on error
	 */
	public static DeviceContext read(final String config_file_name) throws Exception
	{
		return read(new FileInputStream(config_file_name));
	}

	/** Read device configuration from XML stream
	 *  @param stream Stream for XML content
	 *  @return {@link DeviceContext} initialized from stream
	 *  @throws Exception on error
	 */
	public static DeviceContext read(final InputStream stream) throws Exception
	{
        final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        final DocumentBuilder db = dbf.newDocumentBuilder();
        final Document doc = db.parse(stream);

        // Check root element
        final Element root_node = doc.getDocumentElement();
        root_node.normalize();
        final String root_name = root_node.getNodeName();
        if (!root_name.equals(XML_BEAMLINE))
            throw new Exception("Got " + root_name + " instead of " + XML_BEAMLINE);

        final DeviceContext context = new DeviceContext();

        // Locate <devices>
        // Loop over devices
        Element node = DOMHelper.findFirstElementNode(root_node.getFirstChild(), XML_DEVICES);
        if (node == null)
        {
            Logger.getLogger(DeviceContextFile.class.getName())
            .log(Level.WARNING, "No devices defined in beamline configuration");
            return context;
        }
        final NodeList pvs = node.getElementsByTagName(XML_PV);
        for (int i=0; i<pvs.getLength(); ++i)
        {
            final Element pv = (Element) pvs.item(i);
        	final String name = DOMHelper.getSubelementString(pv, XML_NAME);
        	if (name == null  ||  name.isEmpty())
        		throw new Exception("Missing PV name");

            final String alias = DOMHelper.getSubelementString(pv, XML_ALIAS, name);

        	final boolean scan = DOMHelper.findFirstElementNode(pv.getFirstChild(), XML_SCAN) != null;
            final boolean log = DOMHelper.findFirstElementNode(pv.getFirstChild(), XML_LOG) != null;

        	if (scan || log)
        	    context.addPVDevice(alias, name);
        }

		return context;
	}
}
