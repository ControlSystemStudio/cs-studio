/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.log;

/** Interface to be implemented by log providers
 *
 *  @author Kay Kasemir
 */
public interface IDataLogFactory
{
	/** Create new log for a new scan
	 *  @param scan_name Name of the scan (doesn't need to be unique)
	 *  @return Unique scan ID that can now and later be used to access the data log
	 *  @throws Exception on error
	 */
    public long createDataLog(final String scan_name) throws Exception;

	/** Get log for a scan
	 *  @return Unique scan ID
	 *  @throws Exception on error
	 */
    public DataLog getDataLog(final long scan_id) throws Exception;
}
