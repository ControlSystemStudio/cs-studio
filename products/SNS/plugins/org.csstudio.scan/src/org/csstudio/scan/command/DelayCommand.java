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

import org.csstudio.scan.server.ScanServer;

/** {@link ScanCommand} that delays the scan for some time
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class DelayCommand extends BaseCommand
{
    /** Serialization ID */
    private static final long serialVersionUID = ScanServer.SERIAL_VERSION;

    private double seconds;

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
	@Override
	public String toString()
	{
	    return "Delay " + seconds + " sec";
	}
}
