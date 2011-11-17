/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.ui.plot;

import java.rmi.RemoteException;
import java.util.List;

import org.csstudio.scan.client.ScanInfoModel;
import org.csstudio.scan.data.ScanData;
import org.csstudio.scan.server.ScanInfo;

/** Model of the Plot's data
 *  <ul>
 *  <li>Maintains underlying {@link ScanInfoModel}
 *  <li>Currently selected scan and its scan data
 *  <li>Current X, Y axis assignments
 *  </ul>
 *  @author Kay Kasemir
 */
public class PlotDataModel
{
    /** Scan model */
    final private ScanInfoModel model;
    
    /** Currently selected scan */
    private ScanInfo selected_scan = null;

    private ScanData scan_data = null;
    
    /** Initialize
     *  @throws Exception on error connecting to scan server
     *  @see #dispose()
     */
    public PlotDataModel() throws Exception
    {
        model = ScanInfoModel.getInstance();
    }
    
    /** Must be called to release resources */
    public void dispose()
    {
        model.release();
    }

    public List<ScanInfo> getScanInfos()
    {
        return model.getInfos();
    }

    public void selectScan(final long id)
    {
        selected_scan  = getScan(id);
        if (selected_scan == null)
        {
            scan_data = null;
            return;
        }
        // TODO Perform in Job/Thread
        try
        {
            scan_data = model.getScanData(selected_scan);
        }
        catch (RemoteException ex)
        {
            scan_data = null;
        }
    }

    public ScanData getScanData()
    {
        return scan_data;
    }
    
    /** Get scan info by ID
     *  @param id Scan ID
     *  @return {@link ScanInfo} or <code>null</code>
     */
    private ScanInfo getScan(final long id)
    {
        final List<ScanInfo> infos = model.getInfos();
        for (ScanInfo info : infos)
            if (info.getId() == id)
                return info;
        return null;
    }

    public void selectXDevice(final String device_name)
    {
        // TODO Auto-generated method stub
        System.out.println("X axis should show " + device_name);
    }

    public void selectYDevice(final String device_name)
    {
        // TODO Auto-generated method stub
        System.out.println("Y axis should show " + device_name);
    }
}
