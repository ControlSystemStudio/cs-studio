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
import java.util.List;

import org.w3c.dom.Element;

/** Command that delays the scan for some time
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class DelayCommand extends ScanCommand
{
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
    protected void configureProperties(final List<ScanCommandProperty> properties)
    {
        properties.add(new ScanCommandProperty("seconds", "Delay (seconds)", Double.class));
        super.configureProperties(properties);
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
	    out.println("<delay>");
        writeIndent(out, level+1);
        out.println("<seconds>" + seconds + "</seconds>");
        super.writeXML(out, level);
        writeIndent(out, level);
        out.println("</delay>");
	}

    /** {@inheritDoc} */
	@Override
    public void readXML(final SimpleScanCommandFactory factory, final Element element) throws Exception
	{
        setSeconds(DOMHelper.getSubelementDouble(element, "seconds"));
        super.readXML(factory, element);
	}

    /** {@inheritDoc} */
	@Override
	public String toString()
	{
	    return "Delay " + seconds + " sec";
	}
}
