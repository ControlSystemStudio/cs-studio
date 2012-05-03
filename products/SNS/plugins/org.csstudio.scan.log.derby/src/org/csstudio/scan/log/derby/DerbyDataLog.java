/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.log.derby;

import org.csstudio.scan.data.ScanData;
import org.csstudio.scan.data.ScanSample;
import org.csstudio.scan.log.DataLog;

/** Data log for Derby
 *
 *  <p>Uses the {@link DerbyDataLogger} for a specfic scan ID.
 *
 *  @author Kay Kasemir
 */
public class DerbyDataLog implements DataLog
{
	final private long scan_id;

	private RDBDataLogger logger = null;

	/** Serial of last logged sample */
    private long last_serial = -1;

	/** Initialize
	 *  @param scan_id ID of scan for which this logger should operate
	 */
	public DerbyDataLog(final long scan_id)
    {
		this.scan_id = scan_id;
    }

    /** {@inheritDoc} */
	@Override
	public synchronized void log(final ScanSample sample) throws Exception
	{
		if (logger == null)
			logger = new DerbyDataLogger();
		logger.log(scan_id, sample);
		last_serial = sample.getSerial();
	}

    /** {@inheritDoc} */
	@Override
	public synchronized long getLastScanDataSerial()
	{
		return last_serial;
	}

    /** {@inheritDoc} */
	@Override
	public ScanData getScanData() throws Exception
	{
		final RDBDataLogger logger = new DerbyDataLogger();
		try
		{
			return logger.getScanData(scan_id);
		}
		finally
		{
			logger.close();
		}
	}

    /** {@inheritDoc} */
	@Override
	public synchronized void close()
	{
		if (logger != null)
		{
			logger.close();
			logger = null;
			System.out.println("Closing derby log ID " + scan_id);
		}
	}
}
