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

import org.csstudio.scan.server.ScanServer;
import org.w3c.dom.Element;

/** {@link ScanCommand} that sets a device to a value
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class SetCommand extends BaseCommand
{
	/** Serialization ID */
    final private static long serialVersionUID = ScanServer.SERIAL_VERSION;

    private String device_name;
	private Object value;

	/** Initialize
	 *  @param device_name Name of device
	 *  @param value Value to write to the device
	 */
	public SetCommand(final String device_name, Object value)
    {
		this.device_name = device_name;
		this.value = value;
    }

	/** @return Name of device to set */
	public String getDeviceName()
    {
        return device_name;
    }

	/** @param device_name Name of device */
    public void setDeviceName(final String device_name)
    {
        this.device_name = device_name;
    }

    /** @return Value to write to device */
    public Object getValue()
    {
        return value;
    }
    
    /** @param value Value to write to the device */
    public void setValue(final Object value)
    {
        this.value = value;
    }

    /** {@inheritDoc} */
    public void writeXML(final PrintStream out, final int level)
    {
        writeIndent(out, level);
        out.println("<set><device>" + device_name + "</device><value>" + value + "</value></set>");
    }
    
    /** {@inheritDoc} */
	@Override
	public String toString()
	{
	    return "Set '" + device_name + "' = " + value;
	}

    public static ScanCommand fromXML(final Element element)
    {
        final String device = DOMHelper.getSubelementString(element, "device", null);
        final double value = DOMHelper.getSubelementDouble(element, "value", 0.0);
        return new SetCommand(device, value);
    }
}
