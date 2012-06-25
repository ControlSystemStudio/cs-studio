/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.ui.plot;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.scan.client.ScanInfoModel;
import org.csstudio.scan.data.ScanData;
import org.csstudio.scan.server.ScanInfo;
import org.csstudio.scan.server.ScanServer;
import org.eclipse.swt.widgets.Display;

/** Model of the Plot's data
 *  <ul>
 *  <li>Maintains underlying {@link ScanInfoModel}
 *  <li>Currently selected scan and its scan data
 *  <li>Current X, Y axis assignments
 *  </ul>
 *
 *  <p>Periodically queries {@link ScanInfoModel} for
 *  changes in the data of selected scan,
 *  fetches that data, and updates {@link PlotDataProvider}.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class PlotDataModel implements Runnable
{
    final private Display display;

    /** Plot update period in ms */
    final private long update_period;

    /** Scan model */
    final private ScanInfoModel model;

    /** @see #run() */
    private volatile Thread update_thread;

    /** Currently selected scan */
    private volatile long selected_scan_id = -1;

    /** Devices in current scan */
    private volatile String[] devices = null;

    /** Last sample serial on which plot data was updated
     *  <p>Resetting it to -1 will trigger a new plot update
     *  after scan, x or y devices are modified
     */
    private volatile long last_serial = -1;

    /** Device used for the X axis
     *  SYNC on this
     */
    private String x_axis_device = null;

    /** Device used for the X axis
     *  SYNC on this
     */
    final private List<String> y_axis_devices = new ArrayList<String>();

    /** Data for plot: x device and first y device, x and second y device, ...
     *  SYNC on this
     */
    final private List<PlotDataProvider> plot_data = new ArrayList<PlotDataProvider>();

    /** Mostly to please FindBugs: Flag that update thread was woken early */
    private boolean wake_early = false;

    /** Initialize
     *  @throws Exception on error connecting to scan server
     *  @see #dispose()
     */
    public PlotDataModel(final Display display) throws Exception
    {
        this.display = display;
        update_period = Preferences.getUpdatePeriod();
        model = ScanInfoModel.getInstance();
    }

    /** Select scan for monitoring data
     *  @param id Scan ID
     */
    public void selectScan(final long id)
    {
        selected_scan_id = id;
        last_serial = -1;
        waveUpdateThread();
    }

    /** @param device_name Device to use for "X" axis */
    public synchronized void selectXDevice(final String device_name)
    {
        x_axis_device = device_name;
        updatePlotDataProviders();
    }

    /** @return Y device names */
    public synchronized String[] getYDevices()
    {
        return y_axis_devices.toArray(new String[y_axis_devices.size()]);
    }

    /** @param device_name Device to use for "Y" axis */
    public synchronized void selectYDevice(final int index, final String device_name)
    {
        y_axis_devices.set(index, device_name);
        updatePlotDataProviders();
    }

    /** @param devices Devices to use for "Y" axis */
    public synchronized void selectYDevices(final List<String> devices)
    {
        y_axis_devices.clear();
        y_axis_devices.addAll(devices);
        updatePlotDataProviders();
    }

    /** @param device_name Device to use for additional "Y" axis */
    public synchronized void addYDevice(final String device_name)
    {
        y_axis_devices.add(device_name);
        updatePlotDataProviders();
    }

    /** Remove device from "Y" axis */
    public synchronized void removeYDevice()
    {
        final int last = y_axis_devices.size() - 1;
        if (last < 0)
            return;
        y_axis_devices.remove(last);
        updatePlotDataProviders();
    }

    /** Create/update plot data providers */
    private synchronized void updatePlotDataProviders()
    {
        plot_data.clear();
        for (String y_axis_device : y_axis_devices)
            if (x_axis_device != null  &&  y_axis_device != null)
                plot_data.add(new PlotDataProvider(display, x_axis_device, y_axis_device));
        last_serial = -1;
        waveUpdateThread();
    }

    /** @return Data for traces */
    public PlotDataProvider[] getPlotDataProviders()
    {
        synchronized (this)
        {
            return plot_data.toArray(new PlotDataProvider[plot_data.size()]);
        }
    }

    /** Start the model */
    public void start()
    {
        update_thread = new Thread(this, "PlotDataModel");
        update_thread.setDaemon(true);
        update_thread.start();
    }

    /** Must be called to release resources */
    public void stop()
    {
        final Thread thread = update_thread;
        update_thread = null;
        try
        {
            thread.join();
        }
        catch (InterruptedException e)
        {
            // Ignore, shutting down anyway
        }
        model.release();
    }

    /** Runnable of the update thread.
     *
     *  Fetches scan data from selected scan,
     *  then picks the X and Y data
     *  and notifies listeners.
     */
    @Override
    public void run()
    {
        while (update_thread != null)
        {
            final ScanInfo scan = getScan(selected_scan_id);
            if (scan == null)
                // No scan selected
                devices = null;
            else
            {   // Get data for scan
                try
                {   // Check if there is new data
                    final ScanServer server = model.getServer();
                    final long current_serial = server.getLastScanDataSerial(scan.getId());
                    if (last_serial != current_serial)
                    {
                        last_serial = current_serial;
                        final ScanData scan_data = server.getScanData(scan.getId());
                        if (scan_data == null)
                            devices = null;
                        else
                        {
                            devices = scan_data.getDevices();
                            synchronized (this)
                            {
                                for (PlotDataProvider data : plot_data)
                                    data.update(scan_data);
                            }
                        }
                    }
                    // else: Skip fetching the same data. No plot_data.update, no events
                }
                catch (RemoteException ex)
                {
                    Logger.getLogger(getClass().getName()).log(Level.WARNING, "Plot data error", ex);
                    devices = null;
                }
            }

            // Was there any data?
            if (devices == null)
            {
                synchronized (this)
                {
                    for (PlotDataProvider data : plot_data)
                        data.clear();
                }
            }

            // Wait for next update period
            // or early wake from waveUpdateThread()
            synchronized (this)
            {
                try
                {
                    wait(update_period);
                }
                catch (InterruptedException e)
                {
                    // Ignore
                }
                // Mostly for FindBugs, or as debugger breakpoint to check wakeup
                if (wake_early)
                    wake_early = false;
            }
        }
    }

    /** Wake update thread ASAP because
     *  configuration has been changed
     */
    private void waveUpdateThread()
    {
        synchronized (this)
        {   // Findbugs gives 'naked notify' warning. Ignore.
            wake_early = true;
            notifyAll();
        }
    }

    /** @return Most recent infos obtained from server */
    public List<ScanInfo> getScanInfos()
    {
        return model.getInfos();
    }

    /** Get scan info by ID
     *  @param id Scan ID
     *  @return {@link ScanInfo} or <code>null</code>
     */
    public ScanInfo getScan(final long id)
    {
        if (id >= 0)
        {
            final List<ScanInfo> infos = model.getInfos();
            for (ScanInfo info : infos)
                if (info.getId() == id)
                    return info;
        }
        return null;
    }

    /** @return Devices used by currently selected Scan */
    public String[] getDevices()
    {
        return devices;
    }
}
