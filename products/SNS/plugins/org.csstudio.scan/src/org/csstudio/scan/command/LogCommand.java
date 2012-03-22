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
package org.csstudio.scan.command;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.csstudio.scan.device.DeviceInfo;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/** Command that reads data from devices and logs it
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class LogCommand extends ScanCommand
{
    /** Configurable properties of this command */
    final private static ScanCommandProperty[] properties = new ScanCommandProperty[]
    {
        new ScanCommandProperty("device_names", "Device Names", DeviceInfo[].class)
    };

    private volatile String[] device_names;

    /** Initialize empty log command */
    public LogCommand()
    {
        this("device");
    }

	/** Initialize
	 *  @param device_names List of device names
	 */
	public LogCommand(final String... device_names)
    {
		this.device_names = device_names;
    }

	/** Initialize
     *  @param device_name Single device name
     */
    public LogCommand(final String device_name)
    {
        this(new String[] { device_name });
    }

    /** {@inheritDoc} */
    @Override
    public ScanCommandProperty[] getProperties()
    {
        return properties;
    }

	/** @return Names of devices to read and log */
    public String[] getDeviceNames()
    {
        return Arrays.copyOf(device_names, device_names.length);
    }

    /** @param device_names Names of devices to read and log */
    public void setDeviceNames(final String... device_names)
    {
        this.device_names = device_names;
    }

    /** {@inheritDoc} */
    @Override
    public void writeXML(final PrintStream out, final int level)
    {
        writeIndent(out, level);
        out.println("<log>");
        writeIndent(out, level+1);
        out.println("<address>" + getAddress() + "</address>");
        writeIndent(out, level+1);
        out.println("<devices>");
        for (String device : device_names)
        {
            writeIndent(out, level+2);
            out.println("<device>" + device + "</device>");
        }
        writeIndent(out, level+1);
        out.println("</devices>");
        writeIndent(out, level);
        out.println("</log>");
    }

    /** {@inheritDoc} */
    @Override
    public void readXML(final SimpleScanCommandFactory factory, final Element element) throws Exception
    {
        setAddress(DOMHelper.getSubelementInt(element, ScanCommandProperty.TAG_ADDRESS, -1));
        final List<String> devices = new ArrayList<String>();
        Element node = DOMHelper.findFirstElementNode(element.getFirstChild(), "devices");
        if (node == null)
            throw new Exception("Missing 'devices'");
        node = DOMHelper.findFirstElementNode(node.getFirstChild(), ScanCommandProperty.TAG_DEVICE);
        while (node != null)
        {
            Node text_node = node.getFirstChild();
            if (text_node == null)
                throw new Exception("Missing device name");
            devices.add(text_node.getNodeValue());
            node = DOMHelper.findNextElementNode(node, ScanCommandProperty.TAG_DEVICE);
        }
        setDeviceNames(devices.toArray(new String[devices.size()]));
    }

    /** {@inheritDoc} */
	@Override
	public String toString()
	{
		final StringBuilder buf = new StringBuilder();
		buf.append("Log ");
		for (int i=0; i<device_names.length; ++i)
		{
			final String device_name = device_names[i];
			if (i > 0)
				buf.append(", ");
			buf.append("'" + device_name + "'");
		}
	    return buf.toString();
	}
}
