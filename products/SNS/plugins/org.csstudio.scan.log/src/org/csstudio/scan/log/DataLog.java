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
 *  @author Kay Kasemir
 */
public interface DataLog
{
	/** Add a sample to the data log
	 *  @param sample {@link ScanSample} to log
	 *  @throws Exception on error
	 *  @see #close()
	 */
	public void log(ScanSample sample) throws Exception;

    /** @return Serial of last sample in scan data or -1 */
    public long getLastScanDataSerial();

    /** @return {@link ScanData} with copy of currently logged data or <code>null</code>
	 *  @throws Exception on error
     */
    public ScanData getScanData() throws Exception;

    /** Should be called when done logging samples
     *  to allow logging mechanism to release resources.
     */
	public void close();
}
