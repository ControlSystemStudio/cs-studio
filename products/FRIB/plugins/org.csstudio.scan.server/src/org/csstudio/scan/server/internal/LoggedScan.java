/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.server.internal;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.scan.data.ScanData;
import org.csstudio.scan.log.DataLog;
import org.csstudio.scan.log.DataLogFactory;
import org.csstudio.scan.server.Scan;
import org.csstudio.scan.server.ScanContextListener;
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
    private List<ScanContextListener> listeners;

    /** Initialize
     *  @param scan {@link Scan}
     */
    public LoggedScan(final Scan scan)
    {
        super(scan);
        this.listeners = new ArrayList<ScanContextListener>();
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
	
    public synchronized void addScanContextListener(ScanContextListener scanContextListener) {
        listeners.add(scanContextListener);
    }
 
    public synchronized void removeScanContextListener(ScanContextListener scanContextListener) {
        listeners.remove(scanContextListener);
    }
    
    public synchronized void fireDataLogEvent(DataLog dataLog) {
 
        for (ScanContextListener listener : listeners)
            listener.logPerformed(dataLog);
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
