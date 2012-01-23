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

/** {@link CommandImpl} that delays the scan until a device reaches a certain value
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class WaitCommand extends ScanCommand
{
    /** Serialization ID */
    final private static long serialVersionUID = ScanServer.SERIAL_VERSION;

    private String device_name;
    private double desired_value;
    private double tolerance;

    /** Initialize empty wait command */
    public WaitCommand()
    {
        this("device", 0.0, 0.1);
    }

    /** Initialize
     *  @param device_name Name of device to check
     *  @param desired_value Desired value of the device
     *  @param tolerance Numeric tolerance when checking value
     */
	public WaitCommand(final String device_name,
	        final double desired_value, final double tolerance)
    {
        this.device_name = device_name;
        this.desired_value = desired_value;
	    this.tolerance = tolerance;
    }

	/** @return Device name */
	public String getDeviceName()
    {
        return device_name;
    }

    /** @param device_name Name of device */
    public void setDeviceName(final String device_name)
    {
        this.device_name = device_name;
    }

	/** @return Desired value */
    public double getDesiredValue()
    {
        return desired_value;
    }
    
    /** @param desired_value Desired value */
    public void setDesiredValue(final double desired_value)
    {
        this.desired_value = desired_value;
    }

    /** @return Tolerance */
    public double getTolerance()
    {
        return tolerance;
    }

    /** @param tolerance Tolerance */
    public void setTolerance(final double tolerance)
    {
        this.tolerance = tolerance;
    }
    
    /** {@inheritDoc} */
    @Override
    public void writeXML(final PrintStream out, final int level)
    {
        writeIndent(out, level);
        out.println("<wait><device>" + device_name + "</device>" +
        		    "<value>" + desired_value + "</value>" +
                    "<tolerance>" + tolerance + "</tolerance>" +
        		    "</wait>");
    }
    
    /** {@inheritDoc} */
    @Override
    public void readXML(final SimpleScanCommandFactory factory, final Element element) throws Exception
    {
        setDeviceName(DOMHelper.getSubelementString(element, "device"));
        setDesiredValue(DOMHelper.getSubelementDouble(element, "value"));
        setTolerance(DOMHelper.getSubelementDouble(element, "tolerance"));
    }
    
    /** {@inheritDoc} */
	@Override
	public String toString()
	{
	    return "Wait for '" + device_name + "' to reach " + desired_value + " (+-" + tolerance + ")";
	}
}
