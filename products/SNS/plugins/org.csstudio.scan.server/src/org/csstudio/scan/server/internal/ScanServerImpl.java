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
import java.net.InetAddress;
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
import org.csstudio.scan.server.app.Application;

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

        scan_engine.start(true);
        
        // RMI replies to clients with this address.
        // If /etc/hosts defines that as 127.0.0.1,
        // remote(!) clients will connect to 127.0.0.1,
        // and then people will be unhappy and waste hours
        // looking for the problem.
        final String localhost = InetAddress.getLocalHost().getHostAddress();
        if ("127.0.0.1".equals(localhost))
        {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE,
                "This host's local address is set to " + localhost + "\n" +
                "Remote clients will not be able to connect until it is configured to a real IP address");
        }

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
        return new ScanServerInfo("V" + ScanServer.SERIAL_VERSION + " (" + Application.getBundleVersion() + ")",
    			start_time,
    			ScanSystemPreferences.getBeamlineConfigPath(),
    			ScanSystemPreferences.getSimulationConfigPath());
    }

    /** Query server for devices used by a scan
     * 
     *  <p>Meant to be called only inside the scan server.
     *  
     *  @param id ID that uniquely identifies a scan (within JVM of the scan engine)
     *            or -1 for default devices
     *  @return {@link Device}s
     *  @see #getDeviceInfos(long) for similar method that is exposed to clients
     *  @throws RemoteException on error in remote access
     */
     public Device[] getDevices(final long id) throws RemoteException
    {
    	if (id >= 0)
    	{   // Get devices for specific scan
            final ExecutableScan scan = scan_engine.getExecutableScan(id);
            if (scan != null)
                return scan.getDevices();
            // else: It's a logged scan, no device info available any more
    	}
    	else
        {   // Get devices in context
            try
            {
                final DeviceContext context = DeviceContext.getDefault();
                return context.getDevices();
            }
            catch (Exception ex)
            {
                Logger.getLogger(getClass().getName()).log(Level.WARNING,
                        "Error reading device context", ex);
            }
        }
    	return new Device[0];
    }

    /** {@inheritDoc} */
    @Override
    public DeviceInfo[] getDeviceInfos(final long id) throws RemoteException
    {    	
    	final Device[] devices = getDevices(id);
    	// Turn Device[] into DeviceInfo[]
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
            final List<ScanCommand> pre_commands = new ArrayList<ScanCommand>();
            for (String path : ScanSystemPreferences.getPreScanPaths())
                pre_commands.addAll(reader.readXMLStream(PathStreamTool.openStream(path)));

            final List<ScanCommand> post_commands = new ArrayList<ScanCommand>();
            for (String path : ScanSystemPreferences.getPostScanPaths())
                post_commands.addAll(reader.readXMLStream(PathStreamTool.openStream(path)));

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
        final List<LoggedScan> scans = scan_engine.getScans();
        final List<ScanInfo> infos = new ArrayList<ScanInfo>(scans.size());
        // Build result with most recent scan first
        for (int i=scans.size()-1; i>=0; --i)
            infos.add(scans.get(i).getScanInfo());
        return infos;
    }

    /** {@inheritDoc} */
    @Override
    public ScanInfo getScanInfo(final long id) throws RemoteException
    {
        final LoggedScan scan = scan_engine.getScan(id);
        return scan.getScanInfo();
    }

    /** {@inheritDoc} */
    @Override
    public String getScanCommands(final long id) throws RemoteException
    {
        final ExecutableScan scan = scan_engine.getExecutableScan(id);
        if (scan != null)
        {
            try
            {
                return XMLCommandWriter.toXMLString(scan.getScanCommands());
            }
            catch (Exception ex)
            {
                throw new RemoteException(ex.getMessage(), ex);
            }
        }
        else
            throw new RemoteException("Commands not available for logged scan");
    }

    /** {@inheritDoc} */
    @Override
    public long getLastScanDataSerial(final long id) throws RemoteException
    {
        final ExecutableScan scan = scan_engine.getExecutableScan(id);
        if (scan != null)
        {
            final DataLog log = scan.getDataLog();
            if (log != null)
                return log.getLastScanDataSerial();
        }
        return 0;
    }

    /** {@inheritDoc} */
	@Override
    public ScanData getScanData(final long id) throws RemoteException
    {
        try
        {
            final LoggedScan scan = scan_engine.getScan(id);
            return scan.getScanData();
        }
        catch (Exception ex)
        {
        	throw new RemoteException("Error retrieving log data", ex);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void updateScanProperty(final long id, final long address,
            final String property_id, final Object value) throws RemoteException
    {
        final ExecutableScan scan = scan_engine.getExecutableScan(id);
        if (scan != null)
            scan.updateScanProperty(address, property_id, value);
    }

    /** {@inheritDoc} */
    @Override
    public void pause(final long id) throws RemoteException
    {
        if (id >= 0)
        {
            final ExecutableScan scan = scan_engine.getExecutableScan(id);
            if (scan != null)
                scan.pause();
        }
        else
        {
            final List<ExecutableScan> scans = scan_engine.getExecutableScans();
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
            final ExecutableScan scan = scan_engine.getExecutableScan(id);
            scan.resume();
        }
        else
        {
            final List<ExecutableScan> scans = scan_engine.getExecutableScans();
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
            final ExecutableScan scan = scan_engine.getExecutableScan(id);
	        scan.abort();
    	}
        else
        {
            final List<ExecutableScan> scans = scan_engine.getExecutableScans();
            for (ExecutableScan scan : scans)
                scan.abort();
        }
    }

    /** {@inheritDoc} */
    @Override
    public void remove(final long id) throws RemoteException
    {
        final LoggedScan scan = scan_engine.getScan(id);
        try
        {
            scan_engine.removeScan(scan);
        }
        catch (Exception ex)
        {
            Logger.getLogger(getClass().getName()).log(Level.WARNING, "Error removing scan", ex);
            throw new RemoteException("Error removing scan", ex);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void removeCompletedScans() throws RemoteException
    {
        try
        {
            scan_engine.removeCompletedScans();
        }
        catch (Exception ex)
        {
            Logger.getLogger(getClass().getName()).log(Level.WARNING, "Error removing completed scans", ex);
            throw new RemoteException("Error removing completed scans", ex);
        }
    }
}
