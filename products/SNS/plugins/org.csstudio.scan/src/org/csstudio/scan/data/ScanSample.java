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

import java.io.Serializable;
import java.util.Date;

import org.csstudio.scan.server.ScanServer;

/** A sample taken by a scan
 *
 *  <p>All samples have a time stamp
 *  and info about the device that produced the sample.
 *
 *  <p>A serial number is used to track
 *  the time line of samples that are acquired in a scan.
 *  A scan can run faster than the resolution of the time stamp.
 *  In other cases, the scan might actually be quite slow but we still
 *  want to identify samples as being taken at conceptually the same
 *  time.
 *  A serial number is used to identify which samples belong together
 *  because they were for example taken within the same iteration
 *  of a scan loop.
 *
 *  @author Kay Kasemir
 */
abstract public class ScanSample implements Serializable
{
    /** Serialization ID */
    final private static long serialVersionUID = ScanServer.SERIAL_VERSION;

    final private String device_name;
	final private Date timestamp;
	final private long serial;

	// TODO Does each Scan Sample need the device name?
	//      Should the device name only be kept in the logger?
    /** Initialize
     *  @param device_name Name of device that provided the sample
     *  @param timestamp Time stamp
     *  @param serial Serial to identify when the sample was taken
     */
    public ScanSample(final String device_name, final Date timestamp, final long serial)
    {
        this.device_name = device_name;
        this.timestamp = timestamp;
        this.serial = serial;
    }

 	/** @return Name of the device that provided this sample */
	public String getDeviceName()
    {
    	return device_name;
    }

	/** @return Time when this sample was obtained */
	public Date getTimestamp()
    {
    	return timestamp;
    }

	/** @return Serial number of this sample */
	public long getSerial()
	{
		return serial;
	}

	/** Get raw value of the sample
	 *  <p>Derived classes can implement access to
	 *  the value by other means
	 *  @return Value of the sample
	 */
	abstract public Object getValue();
}
