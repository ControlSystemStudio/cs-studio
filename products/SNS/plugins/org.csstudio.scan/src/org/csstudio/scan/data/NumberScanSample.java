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
package org.csstudio.scan.data;

import java.util.Date;

import org.csstudio.scan.server.ScanServer;

/** Scan sample for numbers
 *  @author Kay Kasemir
 */
public class NumberScanSample extends ScanSample
{
    /** Serialization ID */
    final private static long serialVersionUID = ScanServer.SERIAL_VERSION;

    final private Number number;

    /** Initialize
     *  @param device_name Name of device that provided the sample
     *  @param timestamp Time stamp
     *  @param serial Serial to identify when the sample was taken
     *  @param number Number
     */
	public NumberScanSample(final String device_name, final Date timestamp,
	        final long serial, final Number number)
	{
		super(device_name, timestamp, serial);
		this.number = number;
	}

	/** @return Number held in this {@link ScanSample} */
	public Number getNumber()
    {
    	return number;
    }

	/** {@inheritDoc} */
	@Override
    public Object getValue()
	{
		return number;
	}
}
