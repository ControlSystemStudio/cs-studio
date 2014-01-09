/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.server;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.csstudio.apputil.macros.MacroUtil;
import org.csstudio.scan.ScanSystemPreferences;
import org.csstudio.scan.device.ScanConfig;
import org.csstudio.scan.device.SimulatedDevice;
import org.csstudio.scan.server.internal.PathStreamTool;

/** Context used for the simulation of {@link ScanCommandImpl}
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class SimulationContext
{
	final private ScanConfig simulation_info;

    /** Macros for resolving device names */
    final private MacroContext macros;
	
	final private Map<String, SimulatedDevice> devices = new HashMap<String, SimulatedDevice>();

	final private PrintStream log_stream;

	private double simulation_seconds = 0.0;

	/** Initialize
	 *  @param log_stream Stream for simulation progress log
	 *  @throws Exception on error while initializing {@link SimulationInfo}
	 */
	public SimulationContext(final PrintStream log_stream) throws Exception
	{
        final InputStream config_stream = PathStreamTool.openStream(ScanSystemPreferences.getSimulationConfigPath());
	    this.simulation_info = new ScanConfig(config_stream);
        this.macros = new MacroContext(ScanSystemPreferences.getMacros());
		this.log_stream = log_stream;
	}

    /** @return Macro support */
    public MacroContext getMacros()
    {
        return macros;
    }

    /** @return Current time of simulation in seconds */
	public double getSimulationSeconds()
	{
		return simulation_seconds;
	}

	/** @return Current time of simulation, "HH:MM:SS" */
    public String getSimulationTime()
    {
		double time = simulation_seconds;
		final long hours = (long) (time / (60*60));
		time -= hours * (60 * 60);
		final long minutes = (long) (time / 60);
		time -= minutes * 60;
		final long secs = (long) (time);
		time -= secs;
		return String.format("%02d:%02d:%02d", hours, minutes, secs);
    }

	/** @param name Device name
	 *  @return {@link SimulatedDevice}
	 *  @throws Exception on error in macro handling
	 */
    public SimulatedDevice getDevice(final String name) throws Exception
    {
        final String expanded_name = MacroUtil.replaceMacros(name, macros);
    	SimulatedDevice device = devices.get(expanded_name);
		if (device == null)
		{
		    device = new SimulatedDevice(expanded_name, simulation_info);
			devices.put(expanded_name, device);
		}
	    return device;
    }

	/** Log information about the currently simulated command
	 *  @param info End-user readable description what the command would do when executed
	 *  @param seconds Estimated time in seconds that the command would take if executed
	 */
	public void logExecutionStep(final String info, final double seconds)
    {
		log_stream.print(getSimulationTime());
		log_stream.print(" - ");
		log_stream.println(info);
		simulation_seconds += seconds;
    }

	/** @param scan Scan implementations to simulate
	 *  @throws Exception
	 */
	public void simulate(final List<ScanCommandImpl<?>> scan) throws Exception
    {
		for (ScanCommandImpl<?> impl : scan)
		    impl.simulate(this);
    }
}
