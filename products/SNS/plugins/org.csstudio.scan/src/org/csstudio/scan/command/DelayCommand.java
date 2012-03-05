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

/** Command that delays the scan for some time
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class DelayCommand extends ScanCommand
{
    /** Configurable properties of this command */
    final private static ScanCommandProperty[] properties = new ScanCommandProperty[]
    {
        new ScanCommandProperty("seconds", "Delay (seconds)", Double.class)
    };

    private volatile double seconds;

    /** Initialize delay with 1 second */
    public DelayCommand()
    {
        this(1.0);
    }

	/** Initialize
	 *  @param seconds Delay in seconds
	 */
	public DelayCommand(final double seconds)
    {
	    this.seconds = seconds;
    }

    /** {@inheritDoc} */
	@Override
    public ScanCommandProperty[] getProperties()
    {
        return properties;
    }

    /** @return Delay in seconds */
	public double getSeconds()
    {
        return seconds;
    }

	/**@param seconds Delay in seconds */
	public void setSeconds(final Double seconds)
	{
	    this.seconds = seconds;
	}

    /** {@inheritDoc} */
	@Override
    public void writeXML(final PrintStream out, final int level)
	{
	    writeIndent(out, level);
	    out.println("<delay><address>" + getAddress() + "</address>" +
	    		    "<seconds>" + seconds + "</seconds></delay>");
	}

    /** {@inheritDoc} */
	@Override
    public void readXML(final SimpleScanCommandFactory factory, final Element element) throws Exception
	{
        setAddress(DOMHelper.getSubelementInt(element, ScanCommandProperty.TAG_ADDRESS, -1));
        setSeconds(DOMHelper.getSubelementDouble(element, "seconds"));
	}

    /** {@inheritDoc} */
	@Override
	public String toString()
	{
	    return "Delay " + seconds + " sec";
	}
}
