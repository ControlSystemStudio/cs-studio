/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.server;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.csstudio.scan.device.SimulatedDevice;

/** Context used for the simulation of {@link ScanCommandImpl}
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class SimulationContext
{
	final private SimulationInfo simulation_info;

	final private Map<String, SimulatedDevice> devices = new HashMap<String, SimulatedDevice>();

	final private PrintStream log_stream;

	private double simulation_seconds = 0.0;

	/** Initialize
	 *  @param log_stream Stream for simulation progress log
	 *  @throws Exception on error while initializing {@link SimulationInfo}
	 */
	public SimulationContext(final PrintStream log_stream) throws Exception
	{
		this.simulation_info = SimulationInfo.getDefault();
		this.log_stream = log_stream;
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
	 */
    public SimulatedDevice getDevice(final String name)
    {
    	SimulatedDevice device = devices.get(name);
		if (device == null)
		{
			device = new SimulatedDevice(name, simulation_info);
			devices.put(name, device);
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
			simulate(impl);
    }

	/** @param impl Scan implementation to simulate
	 *  @throws Exception
	 */
	public void simulate(final ScanCommandImpl<?> impl) throws Exception
    {
		impl.simulate(this);
    }
}
