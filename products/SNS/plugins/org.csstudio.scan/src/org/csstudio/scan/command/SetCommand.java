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

/** Command that sets a device to a value
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class SetCommand extends ScanCommand
{
    /** Configurable properties of this command */
    final private static ScanCommandProperty[] properties = new ScanCommandProperty[]
    {
        ScanCommandProperty.DEVICE_NAME,
        new ScanCommandProperty("value", "Value", Object.class),
        ScanCommandProperty.WAIT,
        ScanCommandProperty.READBACK,
        ScanCommandProperty.TOLERANCE,
        ScanCommandProperty.TIMEOUT,
    };

    private volatile String device_name;
	private volatile Object value;
	private volatile String readback;
	private volatile boolean wait;
    private volatile double tolerance;
    private volatile double timeout;

    /** Initialize empty set command */
    public SetCommand()
    {
        this("device", 0.0, "", true, 0.1, 0.0);
    }

    /** Initialize for readback with default tolerance and timeout
     *  @param device_name Name of device and readback
     *  @param value Value to write to the device
     */
    public SetCommand(final String device_name, final Object value)
    {
        this(device_name, value, device_name, true, 0.1, 0.0);
    }

    /** Initialize with default tolerance and timeout
     *  @param device_name Name of device and readback (if used)
     *  @param value Value to write to the device
     *  @param wait Wait for readback to match?
     */
    public SetCommand(final String device_name, final Object value, final boolean wait)
    {
        this(device_name, value, device_name, wait, 0.1, 0.0);
    }

    /** Initialize
     *  @param device_name Name of device
     *  @param value Value to write to the device
     *  @param readback Readback device
     */
    public SetCommand(final String device_name, final Object value,
            final String readback)
    {
        this(device_name, value, readback, true, 0.1, 0.0);
    }

    /** Initialize
	 *  @param device_name Name of device
	 *  @param value Value to write to the device
	 *  @param readback Readback device
	 *  @param wait Wait for readback to match?
     *  @param tolerance Numeric tolerance when checking value
     *  @param timeout Timeout in seconds, 0 as "forever"
	 */
	public SetCommand(final String device_name, final Object value,
	        final String readback, final boolean wait,
            final double tolerance, final double timeout)
    {
	    if (device_name == null)
	        throw new NullPointerException();
		this.device_name = device_name;
		this.value = value;
		if (readback == null)
            throw new NullPointerException();
		this.readback = readback;
		this.wait = wait;
        this.tolerance = tolerance;
        this.timeout = timeout;
    }

	/** {@inheritDoc} */
    @Override
    public ScanCommandProperty[] getProperties()
    {
        return properties;
    }

	/** @return Name of device to set (may be "" but not <code>null</code>) */
	public String getDeviceName()
    {
        return device_name;
    }

	/** @param device_name Name of device */
    public void setDeviceName(final String device_name)
    {
        if (device_name == null)
            throw new NullPointerException();
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

    /** @return Wait for readback to match? */
    public boolean getWait()
    {
        return wait;
    }

    /** @param wait Wait for readback to match? */
    public void setWait(final Boolean wait)
    {
        this.wait = wait;
    }

    /** @return Name of readback device (may be "" but not <code>null</code>) */
    public String getReadback()
    {
        return readback;
    }

    /** @param readback Name of readback device */
    public void setReadback(final String readback)
    {
        if (device_name == null)
            throw new NullPointerException();
        this.readback = readback;
    }

    /** @return Tolerance */
    public double getTolerance()
    {
        return tolerance;
    }

    /** @param tolerance Tolerance */
    public void setTolerance(final Double tolerance)
    {
        this.tolerance = Math.max(0.0, tolerance);
    }

    /** @return Timeout in seconds */
    public double getTimeout()
    {
        return timeout;
    }

    /** @param timeout Time out in seconds */
    public void setTimeout(final Double timeout)
    {
        this.timeout = Math.max(0.0, timeout);
    }

    /** {@inheritDoc} */
    @Override
    public void writeXML(final PrintStream out, final int level)
    {
        writeIndent(out, level);
        out.println("<set>");
        writeIndent(out, level+1);
        out.println("<address>" + getAddress() + "</address>");
        writeIndent(out, level+1);
        out.println("<device>" + device_name + "</device>");
        writeIndent(out, level+1);
        out.println("<value>" + value + "</value>");
        if (! readback.isEmpty())
        {
            writeIndent(out, level+1);
            out.println("<readback>" + readback + "</readback>");
        }
        if (! wait)
        {
            writeIndent(out, level+1);
            out.println("<wait>" + wait + "</wait>");
        }
        if (tolerance > 0.0)
        {
            writeIndent(out, level+1);
            out.println("<tolerance>" + tolerance + "</tolerance>");
        }
        if (timeout > 0.0)
        {
            writeIndent(out, level+1);
            out.println("<timeout>" + timeout + "</timeout>");
        }
        writeIndent(out, level);
        out.println("</set>");
    }

    /** {@inheritDoc} */
    @Override
    public void readXML(final SimpleScanCommandFactory factory, final Element element) throws Exception
    {
        setAddress(DOMHelper.getSubelementInt(element, ScanCommandProperty.TAG_ADDRESS, -1));
        setDeviceName(DOMHelper.getSubelementString(element, ScanCommandProperty.TAG_DEVICE));
        setValue(DOMHelper.getSubelementDouble(element, ScanCommandProperty.TAG_VALUE));
        setReadback(DOMHelper.getSubelementString(element, ScanCommandProperty.TAG_READBACK, ""));
        setWait(Boolean.parseBoolean(DOMHelper.getSubelementString(element, ScanCommandProperty.TAG_WAIT, "true")));
        setTolerance(DOMHelper.getSubelementDouble(element, ScanCommandProperty.TAG_TOLERANCE, 0.1));
        setTimeout(DOMHelper.getSubelementDouble(element, ScanCommandProperty.TAG_TIMEOUT, 0.0));
    }

    /** @param buf If the set command uses a condition,
     *             information about it will be appended to string builder
     */
    public void appendConditionDetail(final StringBuilder buf)
    {
    	if (wait)
    	{
    		buf.append(" (wait for '");
    		if (readback.isEmpty())
    			buf.append(device_name);
    		else
    			buf.append(readback);
    		if (tolerance > 0)
    			buf.append("' +-").append(tolerance);
    		if (timeout > 0)
    			buf.append(", ").append(timeout).append(" sec timeout");
    		buf.append(")");
    	}
    }

    /** {@inheritDoc} */
	@Override
	public String toString()
	{
	    final StringBuilder buf = new StringBuilder();
	    buf.append("Set '").append(device_name).append("' = ").append(value);
	    appendConditionDetail(buf);
	    return buf.toString();
	}
}
