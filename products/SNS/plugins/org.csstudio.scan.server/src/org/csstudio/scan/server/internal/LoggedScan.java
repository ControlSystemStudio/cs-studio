/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.server.internal;

import org.csstudio.scan.data.ScanData;
import org.csstudio.scan.log.DataLog;
import org.csstudio.scan.log.DataLogFactory;
import org.csstudio.scan.server.Scan;
import org.csstudio.scan.server.ScanInfo;
import org.csstudio.scan.server.ScanServer;
import org.csstudio.scan.server.ScanState;

/** Scan with logged data
 *
 *  <p>Scan that was executed in the past and has logged data,
 *  or basis for {@link ExecutableScan}
 *  @author Kay Kasemir
 */
public class LoggedScan extends Scan
{
    /** Serialization ID */
    final private static long serialVersionUID = ScanServer.SERIAL_VERSION;

    /** Initialize
     *  @param scan {@link Scan}
     */
    public LoggedScan(final Scan scan)
    {
        super(scan);
    }

    /** @return {@link ScanState} */
    public synchronized ScanState getScanState()
    {
        return ScanState.Logged;
    }

    /** @return Info about this scan */
    public ScanInfo getScanInfo()
    {
        return new ScanInfo(this, getScanState());
    }

    /** {@inheritDoc} */
	public ScanData getScanData() throws Exception
	{
	    final DataLog logger = DataLogFactory.getDataLog(this);
	    try
	    {
	        return logger.getScanData();
	    }
	    finally
	    {
	        logger.close();
	    }
	}

    // Compare by ID
    @Override
    public boolean equals(final Object obj)
    {
        if (! (obj instanceof LoggedScan))
            return false;
        final LoggedScan other = (LoggedScan) obj;
        return getId() == other.getId();
    }
}
