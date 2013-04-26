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
package org.csstudio.scan.log;

import org.csstudio.scan.data.ScanData;
import org.csstudio.scan.data.ScanSample;

/** Logger for {@link ScanSample}s
 *
 *  <p>Abstract base provides the sample serial handling.
 *  Derived classes implement the actual sample storage.
 *
 *  @author Kay Kasemir
 */
abstract public class DataLog
{
	/** Serial of last logged sample */
    private long last_serial = -1;

    /** @return Next unique scan sample serial */
    public synchronized long getNextScanDataSerial()
    {
    	return ++last_serial;
    }

    /** Add a sample to the data log
     *
     *  <p>Both the sample's name and its serial
     *  identify a unique sample.
     *  It is an error to log a sample with the same
     *  name and serial twice within a scan.
     *  The specific behavior will depend on the implementation.
     *
     *  @param device Device name
	 *  @param sample {@link ScanSample} to log
	 *  @throws Exception on error
	 *  @see #close()
	 */
	public void log(final String device, final ScanSample sample) throws Exception
	{
		doLog(device, sample);
		synchronized (this)
		{
			last_serial  = sample.getSerial();
		}
	}

    /** Perform actual logging of a sample.
     *  @param device Device name
     *  @param sample {@link ScanSample} to log
	 *  @throws Exception on error
	 */
	abstract protected void doLog(final String device, ScanSample sample) throws Exception;

	/** @return Serial of last sample in scan data or -1 if nothing has been logged */
    public synchronized long getLastScanDataSerial()
	{
	    return last_serial;
	}

    /** @return {@link ScanData} with copy of currently logged data or <code>null</code>
	 *  @throws Exception on error
     */
    abstract public ScanData getScanData() throws Exception;

    /** Should be called when done logging samples
     *  to allow logging mechanism to release resources.
     */
	public void close()
	{
		// NOP
	}
}
