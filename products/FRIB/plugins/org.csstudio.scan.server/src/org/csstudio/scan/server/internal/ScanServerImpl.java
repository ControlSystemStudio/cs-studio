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
import org.csstudio.scan.server.JythonSupport;
import org.csstudio.scan.server.ScanCommandImpl;
import org.csstudio.scan.server.ScanCommandImplTool;
import org.csstudio.scan.server.ScanContext;
import org.csstudio.scan.server.ScanInfo;
import org.csstudio.scan.server.ScanServer;
import org.csstudio.scan.server.ScanServerInfo;
import org.csstudio.scan.server.SimulationContext;
import org.csstudio.scan.server.SimulationResult;
import org.csstudio.scan.server.app.Application;

/** Implementation of the {@link ScanServer}
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ScanServerImpl implements ScanServer
{
    /** {@link ScanEngine} used by this server */
    final private ScanEngine scan_engine = new ScanEngine();

    /** Time when this scan server was started */
    private Date start_time = null;

    /** Start the scan server */
    public void start() throws Exception
    {
        if (start_time != null)
            throw new Exception("Already started");

        scan_engine.start(true);
        start_time = new Date();
    }

    /** Stop the scan server */
    public void stop()
    {
        scan_engine.stop();
    }

    /** {@inheritDoc} */
    @Override
    public ScanServerInfo getInfo() throws Exception
    {
        return new ScanServerInfo("V" + ScanServer.VERSION + " (" + Application.getBundleVersion() + ")",
    			start_time,
    			ScanSystemPreferences.getBeamlineConfigPath(),
    			ScanSystemPreferences.getSimulationConfigPath(),
    			ScanSystemPreferences.getScriptPaths(),
    			ScanSystemPreferences.getMacros());
    }

    /** Query server for devices used by a scan
     * 
     *  <p>Meant to be called only inside the scan server.
     *  
     *  @param id ID that uniquely identifies a scan
     *            or -1 for default devices
     *  @return {@link Device}s
     *  @see #getDeviceInfos(long) for similar method that is exposed to clients
     *  @throws Exception on error
     */
     public Device[] getDevices(final long id) throws Exception
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
    public DeviceInfo[] getDeviceInfos(final long id) throws Exception
    {    	
    	final Device[] devices = getDevices(id);
    	// Turn Device[] into DeviceInfo[]
    	final DeviceInfo[] infos = new DeviceInfo[devices.length];
    	for (int i = 0; i < infos.length; i++)
    	    infos[i] = devices[i];
    	return infos;
    }

	/** {@inheritDoc} */
    @Override
    public SimulationResult simulateScan(final String commands_as_xml)
            throws Exception
    {
        try
        {   // Parse scan from XML
            final XMLCommandReader reader = new XMLCommandReader(new ScanCommandFactory());
            final List<ScanCommand> commands = reader.readXMLString(commands_as_xml);
            
            // Create Jython interpreter for this scan
            final JythonSupport jython = new JythonSupport();

            // Implement commands
            final ScanCommandImplTool tool = ScanCommandImplTool.getInstance();
            List<ScanCommandImpl<?>> scan = tool.implement(commands, jython);

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
            throw new Exception("Scan Engine error while simulating scan", ex);
        }
    }

	/** {@inheritDoc} */
    @Override
    public long submitScan(final String scan_name, final String commands_as_xml)
            throws Exception
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

            // Create Jython interpreter for this scan
            final JythonSupport jython = new JythonSupport();

            // Obtain implementations for the requested commands as well as pre/post scan
            final ScanCommandImplTool implementor = ScanCommandImplTool.getInstance();
            final List<ScanCommandImpl<?>> pre_impl = implementor.implement(pre_commands, jython);
            final List<ScanCommandImpl<?>> main_impl = implementor.implement(commands, jython);
            final List<ScanCommandImpl<?>> post_impl = implementor.implement(post_commands, jython);

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
            throw new Exception("Scan Engine error while submitting scan: " +
            		ex.getClass().getName(), ex);
        }
    }

    /** If memory consumption is high, remove (one) older scan */
	private void cullScans() throws Exception
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
    public List<ScanInfo> getScanInfos() throws Exception
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
    public ScanInfo getScanInfo(final long id) throws Exception
    {
        final LoggedScan scan = scan_engine.getScan(id);
        return scan.getScanInfo();
    }

    /** {@inheritDoc} */
    @Override
    public String getScanCommands(final long id) throws Exception
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
                throw new Exception(ex.getMessage(), ex);
            }
        }
        else
            throw new Exception("Commands not available for logged scan");
    }

    /** Obtain scan context.
     *  @param id ID that uniquely identifies a scan
     *  @return {@link ScanContext} or <code>null</code> if ID does not refer to an active scan
     *  @throws Exception
     */
    public ScanContext getScanContext(final long id) throws Exception
    {
        final ScanContext scan = scan_engine.getExecutableScan(id);
        if (scan != null)
            return scan;
        return null;
    }
    
    /** {@inheritDoc} */
    @Override
    public long getLastScanDataSerial(final long id) throws Exception
    {
        final LoggedScan scan = scan_engine.getScan(id);
        if (scan != null)
            return scan.getLastScanDataSerial();
        return -1;
    }

    /** {@inheritDoc} */
	@Override
    public ScanData getScanData(final long id) throws Exception
    {
        try
        {
            final LoggedScan scan = scan_engine.getScan(id);
            return scan.getScanData();
        }
        catch (Exception ex)
        {
        	throw new Exception("Error retrieving log data", ex);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void updateScanProperty(final long id, final long address,
            final String property_id, final Object value) throws Exception
    {
        final ExecutableScan scan = scan_engine.getExecutableScan(id);
        if (scan != null)
            scan.updateScanProperty(address, property_id, value);
    }

    /** {@inheritDoc} */
    @Override
    public void pause(final long id) throws Exception
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
    public void resume(final long id) throws Exception
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
    public void abort(final long id) throws Exception
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
    public void remove(final long id) throws Exception
    {
        final LoggedScan scan = scan_engine.getScan(id);
        try
        {
            scan_engine.removeScan(scan);
        }
        catch (Exception ex)
        {
            Logger.getLogger(getClass().getName()).log(Level.WARNING, "Error removing scan", ex);
            throw new Exception("Error removing scan", ex);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void removeCompletedScans() throws Exception
    {
        try
        {
            scan_engine.removeCompletedScans();
        }
        catch (Exception ex)
        {
            Logger.getLogger(getClass().getName()).log(Level.WARNING, "Error removing completed scans", ex);
            throw new Exception("Error removing completed scans", ex);
        }
    }
}
