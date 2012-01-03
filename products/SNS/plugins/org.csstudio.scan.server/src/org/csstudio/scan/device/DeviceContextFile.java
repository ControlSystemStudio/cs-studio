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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.csstudio.apputil.xml.DOMHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/** Helper for handling device context files
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class DeviceContextFile
{
	private static final String XML_DEVICES = "devices";
	private static final String XML_DEVICE = "device";
	private static final String XML_NAME = "name";
	private static final String XML_TYPE = "type";
	private static final String XML_PV = "pv";

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
        if (!root_name.equals(XML_DEVICES))
            throw new Exception("Got " + root_name + " instead of " + XML_DEVICES);
        
        final DeviceContext context = new DeviceContext();

        // Loop over devices
        Element node = DOMHelper.findFirstElementNode(root_node.getFirstChild(), XML_DEVICE);
        while (node != null)
        {
        	final String name = node.getAttribute(XML_NAME);
        	if (name == null  ||  name.isEmpty())
        		throw new Exception("Missing device '" + XML_NAME + "' attribute");

        	final String type = node.getAttribute(XML_TYPE);
        	if (type == null  ||  type.isEmpty())
        		throw new Exception("Missing device '" + XML_TYPE + "' attribute");
        	
        	if (XML_PV.equals(type))
        	{
        		final String pv = DOMHelper.getSubelementString(node, XML_PV);
        		context.addPVDevice(name, pv);
        	}
        	else
        		throw new Exception("Device '" + name + "' has unknown type '" + type + "'");
        	
        	node = DOMHelper.findNextElementNode(node, XML_DEVICE);
        }
        
		return context;
	}
}
