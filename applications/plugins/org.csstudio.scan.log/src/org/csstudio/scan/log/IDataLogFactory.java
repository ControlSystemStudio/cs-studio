/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.log;

import org.csstudio.scan.server.Scan;

/** Interface to be implemented by log providers
 *
 *  @author Kay Kasemir
 */
public interface IDataLogFactory
{
	/** Create new log for a new scan
	 *  @param scan_name Name of the scan (doesn't need to be unique)
	 *  @return Scan with ID that can now and later be used to access the data log
	 *  @throws Exception on error
	 */
    public Scan createDataLog(final String scan_name) throws Exception;

    /** Obtain all available scans
     *  @return Scans that have been logged
     *  @throws Exception on error
     */
    public Scan[] getScans() throws Exception;

	/** Get log for a scan
	 *
	 *  <p>Caller needs to <code>close()</code>
	 *  @param scan Scan
	 *  @return DataLog for Scan, or <code>null</code> if there is none
	 *  @throws Exception on error
	 *  @see DataLog#close()
	 */
    public DataLog getDataLog(final Scan scan) throws Exception;

    /** Delete logged data for a scan
     *  @param scan Scan
     *  @throws Exception on error
     */
    public void deleteDataLog(final Scan scan) throws Exception;
}
