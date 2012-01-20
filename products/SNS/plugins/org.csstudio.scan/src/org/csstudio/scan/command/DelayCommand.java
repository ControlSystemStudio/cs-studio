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

/** {@link ScanCommand} that delays the scan for some time
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class DelayCommand extends ScanCommand
{
    /** Serialization ID */
    private static final long serialVersionUID = ScanServer.SERIAL_VERSION;

    private double seconds;

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

	/** @return Delay in seconds */
	public double getSeconds()
    {
        return seconds;
    }

	/**@param seconds Delay in seconds */
	public void setSeconds(final double seconds)
	{
	    this.seconds = seconds;
	}
	
    /** {@inheritDoc} */
	public void writeXML(final PrintStream out, final int level)
	{
	    writeIndent(out, level);
	    out.println("<delay><seconds>" + seconds + "</seconds></delay>");
	}
	
    /** Create from XML 
     *  @param element XML element for this command
     *  @return ScanCommand
     *  @throws Exception on error, for example missing configuration element
     */
    public static ScanCommand fromXML(final Element element) throws Exception
	{
	    final double seconds = DOMHelper.getSubelementDouble(element, "seconds");
	    return new DelayCommand(seconds);
	}
	
    /** {@inheritDoc} */
	@Override
	public String toString()
	{
	    return "Delay " + seconds + " sec";
	}
}
