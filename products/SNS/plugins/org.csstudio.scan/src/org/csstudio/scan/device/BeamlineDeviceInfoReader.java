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
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/** Helper for handling device context files
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class BeamlineDeviceInfoReader
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
	 *  @return {@link DeviceInfo}s read from file
	 *  @throws Exception on error
	 */
	public static DeviceInfo[] read(final String config_file_name) throws Exception
	{
		return read(new FileInputStream(config_file_name));
	}

	/** Read device configuration from XML stream
	 *  @param stream Stream for XML content
	 *  @return {@link DeviceInfo}s read from stream
	 *  @throws Exception on error
	 */
	public static DeviceInfo[] read(final InputStream stream) throws Exception
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

        final List<DeviceInfo> devices = new ArrayList<DeviceInfo>();

        // Locate <devices>
        Element node = findFirstElementNode(root_node.getFirstChild(), XML_DEVICES);
        if (node != null)
        {   // Loop over <pv>s
            final NodeList pvs = node.getElementsByTagName(XML_PV);
            for (int i=0; i<pvs.getLength(); ++i)
            {
                final Element pv = (Element) pvs.item(i);
            	final String name = getSubelementString(pv, XML_NAME, "");
            	if (name == null  ||  name.isEmpty())
            		throw new Exception("Missing PV name");

                final String alias = getSubelementString(pv, XML_ALIAS, name);

            	final boolean scan = findFirstElementNode(pv.getFirstChild(), XML_SCAN) != null;
                final boolean log = findFirstElementNode(pv.getFirstChild(), XML_LOG) != null;

            	if (scan || log)
            	    devices.add(new DeviceInfo(name, alias));
            }
        }

		return devices.toArray(new DeviceInfo[devices.size()]);
	}

    /** Look for Element node if given name.
     *  <p>
     *  Checks the node and its siblings.
     *  Does not descent down the 'child' links.
     *  @param node Node where to start.
     *  @param name Name of the nodes to look for.
     *  @return Returns node or the next matching sibling or null.
     */
    private static final Element findFirstElementNode(Node node, final String name)
    {
        while (node != null)
        {
            if (node.getNodeType() == Node.ELEMENT_NODE &&
                    node.getNodeName().equals(name))
                return (Element) node;
            node = node.getNextSibling();
        }
        return null;
    }

    /** Locate a sub-element tagged 'name', return its value.
     *
     *  Will only go one level down, not search the whole tree.
     *
     *  @param element Element where to start looking. May be null.
     *  @param name Name of sub-element to locate.
     *  @param default_value Default value if not found
     *
     *  @return Returns string that was found or default_value.
     */
    private static final String getSubelementString(
            final Element element, final String name, final String default_value)
    {
        if (element == null)
            return default_value;
        Node n = element.getFirstChild();
        n = findFirstElementNode(n, name);
        if (n != null)
        {
            Node text_node = n.getFirstChild();
            if (text_node == null)
                return default_value;
            return text_node.getNodeValue();
        }
        return default_value;
    }
}
