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
package org.csstudio.scan.server.internal;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.net.BindException;
import java.rmi.RemoteException;
import java.rmi.ServerException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.scan.ScanSystemPreferences;
import org.csstudio.scan.command.ScanCommand;
import org.csstudio.scan.command.ScanCommandFactory;
import org.csstudio.scan.command.XMLCommandReader;
import org.csstudio.scan.command.XMLCommandWriter;
import org.csstudio.scan.data.ScanData;
import org.csstudio.scan.device.Device;
import org.csstudio.scan.device.DeviceContext;
import org.csstudio.scan.device.DeviceInfo;
import org.csstudio.scan.log.DataLog;
import org.csstudio.scan.server.ScanCommandImpl;
import org.csstudio.scan.server.ScanCommandImplTool;
import org.csstudio.scan.server.ScanInfo;
import org.csstudio.scan.server.ScanServer;
import org.csstudio.scan.server.ScanServerInfo;
import org.csstudio.scan.server.SimulationContext;
import org.csstudio.scan.server.SimulationResult;
import org.csstudio.scan.server.UnknownScanException;

/** Server-side implementation of the {@link ScanServer} interface
 *  that the remote client invokes.
 *
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
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
        this(ScanServer.DEFAULT_PORT);
    }

    /** Start the scan server (allow clients to connect) */
    public void start() throws Exception
    {
        if (registry != null)
            throw new Exception("Already started");

        scan_engine.start();

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

        final ScanServer stub = (ScanServer) UnicastRemoteObject.exportObject(this, port+1);
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

        scan_engine.stop();
    }

    /** {@inheritDoc} */
    @Override
    public ScanServerInfo getInfo() throws RemoteException
    {
    	return new ScanServerInfo("V" + ScanServer.SERIAL_VERSION,
    			start_time,
    			ScanSystemPreferences.getBeamlineConfigPath(),
    			ScanSystemPreferences.getSimulationConfigPath());
    }

    /** {@inheritDoc} */
    @Override
    public DeviceInfo[] getDeviceInfos(final long id) throws RemoteException
    {
		// Get devices in context
    	Device[] devices;
    	if (id >= 0)
    	{
    	    final ExecutableScan scan = findScan(id);
    		devices = scan.getDevices();
    	}
    	else
        {
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
        }

    	// Turn into infos
    	final DeviceInfo[] infos = new DeviceInfo[devices.length];
    	for (int i = 0; i < infos.length; i++)
    	{
    		final Device device = devices[i];
			infos[i] = device.getInfo();
    	}
    	return infos;
    }

	/** {@inheritDoc} */
    @Override
    public SimulationResult simulateScan(final String commands_as_xml)
            throws RemoteException
    {
        try
        {   // Parse scan from XML
            final XMLCommandReader reader = new XMLCommandReader(new ScanCommandFactory());
            final List<ScanCommand> commands = reader.readXMLString(commands_as_xml);

            // Implement commands
            final ScanCommandImplTool tool = ScanCommandImplTool.getInstance();
            List<ScanCommandImpl<?>> scan = tool.implement(commands);

            // Setup simulation log
            ByteArrayOutputStream log_buf = new ByteArrayOutputStream();
            PrintStream log_out = new PrintStream(log_buf);
            log_out.println("Simulation:");
            log_out.println("--------");

            // Simulate
			final SimulationContext simulation = new SimulationContext(log_out);
            simulation.simulate(scan);

            // Close log
            log_out.println("--------");
            log_out.println(simulation.getSimulationTime() + "   Total estimated execution time");
            log_out.close();

            // Fetch simulation log, help GC to clear copies of log
            final String log_text = log_buf.toString();
            log_out = null;
            log_buf = null;

            return new SimulationResult(simulation.getSimulationSeconds(), log_text);
        }
        catch (Exception ex)
        {
            throw new ServerException("Scan Engine error while simulating scan", ex);
        }
    }

	/** {@inheritDoc} */
    @Override
    public long submitScan(final String scan_name, final String commands_as_xml)
            throws RemoteException
    {
    	cullScans();

        try
        {   // Parse received 'main' scan from XML
            final XMLCommandReader reader = new XMLCommandReader(new ScanCommandFactory());
            final List<ScanCommand> commands = reader.readXMLString(commands_as_xml);

            // Read pre- and post-scan commands
            String path = ScanSystemPreferences.getPreScanPath();
            final List<ScanCommand> pre_commands;
            if (path.isEmpty())
                pre_commands = Collections.<ScanCommand>emptyList();
            else
                pre_commands = reader.readXMLStream(PathStreamTool.openStream(path));

            path = ScanSystemPreferences.getPostScanPath();
            final List<ScanCommand> post_commands;
            if (path.isEmpty())
                post_commands = Collections.<ScanCommand>emptyList();
            else
                post_commands = reader.readXMLStream(PathStreamTool.openStream(path));

            // Obtain implementations for the requested commands as well as pre/post scan
            final ScanCommandImplTool implementor = ScanCommandImplTool.getInstance();
            final List<ScanCommandImpl<?>> pre_impl = implementor.implement(pre_commands);
            final List<ScanCommandImpl<?>> main_impl = implementor.implement(commands);
            final List<ScanCommandImpl<?>> post_impl = implementor.implement(post_commands);

            // Get default devices
    		final DeviceContext devices = DeviceContext.getDefault();

            // Submit scan to engine for execution
            final ExecutableScan scan = new ExecutableScan(scan_name, devices, pre_impl, main_impl, post_impl);
            scan_engine.submit(scan);
            return scan.getId();
        }
        catch (Exception ex)
        {
        	Logger.getLogger(getClass().getName()).log(Level.WARNING, "Scan submission failed", ex);
        	// Cannot wrap any Exception ex into RemoteExcetion because
        	// ex may not serialize. So include the name.
            throw new RemoteException("Scan Engine error while submitting scan: " +
            		ex.getClass().getName() + " " + ex.getMessage());
        }
    }

    /** If memory consumption is high, remove (one) older scan */
	private void cullScans() throws RemoteException
    {
	    final double threshold = ScanSystemPreferences.getOldScanRemovalMemoryThreshold();
		while (getInfo().getMemoryPercentage() > threshold)
	    {
	    	if (! scan_engine.removeOldestCompletedScan())
	    		return;
	    	System.gc();
	    }
    }

	/** {@inheritDoc} */
    @Override
    public List<ScanInfo> getScanInfos() throws RemoteException
    {
        final List<ExecutableScan> scans = scan_engine.getScans();
        final List<ScanInfo> infos = new ArrayList<ScanInfo>(scans.size());
        // Build result with most recent scan first
        for (int i=scans.size()-1; i>=0; --i)
            infos.add(scans.get(i).getScanInfo());
        return infos;
    }

    /** Find scan by ID
     *  @param id Scan ID
     *  @return {@link ExecutableScan}
     * @throws UnknownScanException if scan ID not valid
     */
    private ExecutableScan findScan(final long id) throws UnknownScanException
    {
        final List<ExecutableScan> scans = scan_engine.getScans();
        // Linear lookup. Good enough?
        for (ExecutableScan scan : scans)
            if (scan.getId() == id)
                return scan;
        throw new UnknownScanException(id);
    }

    /** {@inheritDoc} */
    @Override
    public ScanInfo getScanInfo(final long id) throws RemoteException
    {
        final ExecutableScan scan = findScan(id);
        return scan.getScanInfo();
    }

    /** {@inheritDoc} */
    @Override
    public String getScanCommands(long id) throws RemoteException
    {
        final ExecutableScan scan = findScan(id);
        try
        {
            return XMLCommandWriter.toXMLString(scan.getScanCommands());
        }
        catch (Exception ex)
        {
            throw new RemoteException(ex.getMessage(), ex);
        }
    }

    /** {@inheritDoc} */
    @Override
    public long getLastScanDataSerial(final long id) throws RemoteException
    {
        final ExecutableScan scan = findScan(id);
        if (scan == null)
            return 0;
        final DataLog log = scan.getDataLog();
        if (log == null)
            return 0;
        return log.getLastScanDataSerial();
    }

    /** {@inheritDoc} */
	@Override
    public ScanData getScanData(final long id) throws RemoteException
    {
        try
        {
            final ExecutableScan scan = findScan(id);
            return scan.getScanData();
        }
        catch (Exception ex)
        {
        	throw new RemoteException("Error logging data", ex);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void updateScanProperty(final long id, final long address,
            final String property_id, final Object value) throws RemoteException
    {
        final ExecutableScan scan = findScan(id);
        scan.updateScanProperty(address, property_id, value);
    }

    /** {@inheritDoc} */
    @Override
    public void pause(final long id) throws RemoteException
    {
        if (id >= 0)
        {
            final ExecutableScan scan = findScan(id);
            scan.pause();
        }
        else
        {
            final List<ExecutableScan> scans = scan_engine.getScans();
            for (ExecutableScan scan : scans)
                scan.pause();
        }
    }

    /** {@inheritDoc} */
    @Override
    public void resume(final long id) throws RemoteException
    {
        if (id >= 0)
        {
            final ExecutableScan scan = findScan(id);
            scan.resume();
        }
        else
        {
            final List<ExecutableScan> scans = scan_engine.getScans();
            for (ExecutableScan scan : scans)
                scan.resume();
        }
    }

    /** {@inheritDoc} */
    @Override
    public void abort(final long id) throws RemoteException
    {
    	if (id >= 0)
    	{
	        final ExecutableScan scan = findScan(id);
	        scan.abort();
    	}
        else
        {
            final List<ExecutableScan> scans = scan_engine.getScans();
            for (ExecutableScan scan : scans)
                scan.abort();
        }
    }

    /** {@inheritDoc} */
    @Override
    public void remove(final long id) throws RemoteException
    {
        final ExecutableScan scan = findScan(id);
        scan_engine.removeScan(scan);
    }

    /** {@inheritDoc} */
    @Override
    public void removeCompletedScans() throws RemoteException
    {
        scan_engine.removeCompletedScans();
    }
}
