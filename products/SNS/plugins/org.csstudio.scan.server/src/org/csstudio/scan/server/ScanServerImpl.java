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
package org.csstudio.scan.server;

import java.net.BindException;
import java.rmi.RemoteException;
import java.rmi.ServerException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.scan.command.CommandImpl;
import org.csstudio.scan.command.CommandImplFactory;
import org.csstudio.scan.command.ScanCommand;
import org.csstudio.scan.data.DataFormatter;
import org.csstudio.scan.data.ScanData;
import org.csstudio.scan.device.Device;
import org.csstudio.scan.device.DeviceContext;

/** Server-side implementation of the {@link ScanServer} interface
 *  that the remote client invokes.
 *
 *  @author Kay Kasemir
 */
public class ScanServerImpl implements ScanServer
{
    final private int port;

    /** {@link ScanEngine} used by this server */
    final private ScanEngine scan_engine = new ScanEngine();

    /** Time when this scan server was started */
    private Date start_time;

    /** RMI registry */
    private Registry registry;

    /** Initialize
     *  @param port TCP port for server
     */
    public ScanServerImpl(final int port)
    {
        this.port = port;
    }

    /** Initialize with default port */
    public ScanServerImpl()
    {
        this(ScanServer.RMI_PORT);
    }

    /** Start the scan server (allow clients to connect) */
    public void start() throws Exception
    {
        if (registry != null)
            throw new Exception("Already started");
        try
        {
            registry = LocateRegistry.createRegistry(port);
        }
        catch (RemoteException ex)
        {
            if (ex.getCause() instanceof BindException)
                throw new Exception("Cannot start Scan Server on port " + port, ex);
            else
                throw ex;
        }
        start_time = new Date();

        final ScanServer stub = (ScanServer) UnicastRemoteObject.exportObject(this, ScanServer.RMI_SCAN_SERVER_PORT);
        registry.rebind(ScanServer.RMI_SCAN_SERVER_NAME, stub);
    }

    /** Stop the scan server */
    public void stop()
    {
        if (registry != null)
        {
            try
            {
                registry.unbind(ScanServer.RMI_SCAN_SERVER_NAME);
            }
            catch (Throwable ex)
            {
                // Ignore, shutting down anyway
            }
            registry = null;
        }
    }

    /** {@inheritDoc} */
    @Override
    public String getInfo() throws RemoteException
    {
        return "Scan Server V" + ScanServer.SERIAL_VERSION + " (started " + DataFormatter.format(start_time) + ")";
    }

    /** {@inheritDoc} */
    @Override
    public DeviceInfo[] getDeviceInfos() throws RemoteException
    {
		// Get devices in context
    	Device[] devices;
    	try
    	{
    		final DeviceContext context = DeviceContext.getDefault();
    		devices = context.getDevices();
    	}
    	catch (Exception ex)
    	{
    		Logger.getLogger(getClass().getName()).log(Level.WARNING,
    				"Error reading device context", ex);
    		devices = new Device[0];
    	}
    	// Turn into infos
    	final DeviceInfo[] infos = new DeviceInfo[devices.length];
    	for (int i = 0; i < infos.length; i++)
    	{
    		final Device device = devices[i];
			infos[i] = new DeviceInfo(device.getName(), device.toString());
    	}
    	return infos;
    }
    
    /** {@inheritDoc} */
    @Override
    public long submitScan(final String scan_name, final List<ScanCommand> commands)
            throws RemoteException
    {
        try
        {
            // Obtain implementations for the requested commands
            final List<CommandImpl> implementations = CommandImplFactory.implement(commands);

            // Get devices
    		final DeviceContext devices = DeviceContext.getDefault();
            
            // Submit scan to engine for execution
            final Scan scan = new Scan(scan_name, implementations);
            scan_engine.submit(devices, scan);
            return scan.getId();
        }
        catch (Exception ex)
        {
            throw new ServerException("Scan Engine error while submitting scan", ex);
        }
    }

	/** {@inheritDoc} */
    @Override
    public List<ScanInfo> getScanInfos() throws RemoteException
    {
        final List<Scan> scans = scan_engine.getScans();
        final List<ScanInfo> infos = new ArrayList<ScanInfo>(scans.size());
        // Build result with most recent scan first
        for (int i=scans.size()-1; i>=0; --i)
            infos.add(scans.get(i).getScanInfo());
        return infos;
    }

    /** Find scan by ID
     *  @param id Scan ID
     *  @return {@link Scan} or <code>null</code> if not found
     */
    private Scan findScan(final long id)
    {
        final List<Scan> scans = scan_engine.getScans();
        // Linear lookup. Good enough?
        for (Scan scan : scans)
            if (scan.getId() == id)
                return scan;
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public ScanInfo getScanInfo(final long id) throws RemoteException
    {
        final Scan scan = findScan(id);
        if (scan != null)
            return scan.getScanInfo();
        return null;
    }

    /** {@inheritDoc} */
	@Override
    public ScanData getScanData(final long id) throws RemoteException
    {
        final Scan scan = findScan(id);
        if (scan != null)
            return scan.getScanData();
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public void pause(final long id) throws RemoteException
    {
        if (id >= 0)
        {
            final Scan scan = findScan(id);
            if (scan != null)
                scan.pause();
        }
        else
        {
            final List<Scan> scans = scan_engine.getScans();
            for (Scan scan : scans)
                scan.pause();
        }
    }

    /** {@inheritDoc} */
    @Override
    public void resume(final long id) throws RemoteException
    {
        if (id >= 0)
        {
            final Scan scan = findScan(id);
            if (scan != null)
                scan.resume();
        }
        else
        {
            final List<Scan> scans = scan_engine.getScans();
            for (Scan scan : scans)
                scan.resume();
        }
    }

    /** {@inheritDoc} */
    @Override
    public void abort(final long id) throws RemoteException
    {
        final Scan scan = findScan(id);
        if (scan != null)
            scan_engine.abortScan(scan);
    }

    /** {@inheritDoc} */
    @Override
    public void remove(final long id) throws RemoteException
    {
        final Scan scan = findScan(id);
        if (scan != null)
            scan_engine.removeScan(scan);
    }

    /** {@inheritDoc} */
    @Override
    public void removeCompletedScans() throws RemoteException
    {
        scan_engine.removeCompletedScans();
    }
}
