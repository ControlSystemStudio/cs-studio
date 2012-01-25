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

import org.w3c.dom.Element;

/** Command that delays the scan until a device reaches a certain value
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class WaitCommand extends ScanCommand
{
    /** Configurable properties of this command */
    final private static ScanCommandProperty[] properties = new ScanCommandProperty[]
    {
        new ScanCommandProperty("device_name", "Device Name", String.class),
        new ScanCommandProperty("desired_value", "Desired Value", Double.class),
        new ScanCommandProperty("comparison", "Comparison", Comparison.class),
        new ScanCommandProperty("tolerance", "Tolerance (for '=')", Double.class),
    };
    
    private String device_name;
    private double desired_value;
    private double tolerance;
    private Comparison comparison;

    /** Initialize empty wait command */
    public WaitCommand()
    {
        this("device", 0.0, Comparison.EQUALS, 0.1);
    }

    /** Initialize
     *  @param device_name Name of device to check
     *  @param desired_value Desired value of the device
     *  @param tolerance Numeric tolerance when checking value
     */
    public WaitCommand(final String device_name,
            final double desired_value, final double tolerance)
    {
        this(device_name, desired_value, Comparison.EQUALS, tolerance);
    }

    /** Initialize
     *  @param device_name Name of device to check
     *  @param desired_value Desired value of the device
     *  @param comparison Comparison to use
     *  @param tolerance Numeric tolerance when checking value
     */
	public WaitCommand(final String device_name,
	        final double desired_value, final Comparison comparison,
	        final double tolerance)
    {
        this.device_name = device_name;
        this.desired_value = desired_value;
	    this.comparison = comparison;
	    this.tolerance = tolerance;
    }

	/** {@inheritDoc} */
    @Override
    public ScanCommandProperty[] getProperties()
    {
        return properties;
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
    public void setDesiredValue(final Double desired_value)
    {
        this.desired_value = desired_value;
    }

    /** @return Desired comparison */
    public Comparison getComparison()
    {
        return comparison;
    }

    /** @param comparison Desired comparison */
    public void setComparison(final Comparison comparison)
    {
        this.comparison = comparison;
    }

    /** @return Tolerance */
    public double getTolerance()
    {
        return tolerance;
    }

    /** @param tolerance Tolerance */
    public void setTolerance(final Double tolerance)
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
                    "<comparison>" + comparison.name() + "</comparison>" +
                    "<tolerance>" + tolerance + "</tolerance>" +
        		    "</wait>");
    }
    
    /** {@inheritDoc} */
    @Override
    public void readXML(final SimpleScanCommandFactory factory, final Element element) throws Exception
    {
        setDeviceName(DOMHelper.getSubelementString(element, "device"));
        setDesiredValue(DOMHelper.getSubelementDouble(element, "value"));
        try
        {
            setComparison(Comparison.valueOf(DOMHelper.getSubelementString(element, "comparison")));
        }
        catch (Throwable ex)
        {
            setComparison(Comparison.EQUALS);
        }
        setTolerance(DOMHelper.getSubelementDouble(element, "tolerance"));
    }
    
    /** {@inheritDoc} */
	@Override
	public String toString()
	{
	    if (comparison == Comparison.EQUALS)
	        return "Wait for '" + device_name + "' " + comparison + " " + desired_value + " (+-" + tolerance + ")";
        return "Wait for '" + device_name + "' " + comparison + " " + desired_value;
	}
}
