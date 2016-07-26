/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.ui.scandata;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.scan.client.ScanClient;
import org.csstudio.scan.client.ScanInfoModel;
import org.csstudio.scan.client.ScanInfoModelListener;
import org.csstudio.scan.data.ScanData;
import org.csstudio.scan.data.ScanSample;
import org.csstudio.scan.server.ScanInfo;
import org.csstudio.scan.server.ScanServerInfo;

/** Model that monitors the data of a scan
 *
 *  <p>Implementation note:
 *  Fetching the whole data for every scan update
 *  plus then converting that to a "spreadsheet"
 *  in the {@link ScanDataEditor}'s {@link ScanDataModelListener}
 *  seems expensive, but causes neglegible CPU load for
 *  scans with a few thousand rows of data and a handful
 *  of devices, so "good enough" for now.
 *
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ScanDataModel implements ScanInfoModelListener
{
    /** ID of scan that we monitor */
    final private long scan_id;

    /** Scan client */
    final private ScanInfoModel scan_info_model;

    /** Most recent scan data */
    private ScanData scan_data = null;

    /** Last sample serial of scan data */
    private long last_scan_data_serial = -1;

    /** Listener to notify about updates in the scan's data */
    final private ScanDataModelListener listener;

    /** Initialize
     *  @param scan_id ID of scan to monitor
     *  @param listener {@link ScanDataModelListener}
     *  @throws Exception on error
     */
    public ScanDataModel(final long scan_id, final ScanDataModelListener listener) throws Exception
    {
        this.scan_id = scan_id;
        scan_info_model = ScanInfoModel.getInstance();
        this.listener = listener;
        scan_info_model.addListener(this);
    }

    /** Must be called to release sources when done */
    public void release()
    {
        scan_info_model.removeListener(this);
        scan_info_model.release();
    }

    /** {@inheritDoc} */
    @Override
    public void scanServerUpdate(final ScanServerInfo server_info)
    {
        // Ignored
    }

    /** @return most recent scan data. May be <code>null</code> */
    public synchronized ScanData getScanData()
    {
        return scan_data;
    }

    /** {@inheritDoc} */
    @Override
    public void scanUpdate(final List<ScanInfo> infos)
    {
        // Skip querying a scan once server indicated that it's unknown
        if (last_scan_data_serial == ScanClient.UNKNOWN_SCAN_SERIAL)
            return;

        try
        {
            // Any change in the data?
            final ScanClient client = scan_info_model.getScanClient();
            final long serial = client.getLastScanDataSerial(scan_id);
            if (serial == last_scan_data_serial)
                return;

            // Get data
            final ScanData data;
            if (serial == ScanClient.UNKNOWN_SCAN_SERIAL)
            {
                Map<String, List<ScanSample>> unknown = new HashMap<>();
                unknown.put("Unknown Scan", Collections.emptyList());
                data = new ScanData(unknown);
            }
            else
                data = client.getScanData(scan_id);
            synchronized (this)
            {
                scan_data = data;
            }
            last_scan_data_serial = serial;
            // Update listener
            listener.updateScanData(data);
        }
        catch (Exception ex)
        {
            Logger.getLogger(getClass().getName()).log(Level.WARNING, "Cannot get scan data", ex);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void connectionError()
    {
        // Ignored
    }
}
