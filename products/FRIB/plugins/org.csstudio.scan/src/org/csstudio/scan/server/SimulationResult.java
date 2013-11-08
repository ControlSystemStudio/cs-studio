/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.server;


/** Result of a scan simulation
 *  @author Kay Kasemir
 */
public class SimulationResult
{
    final private double simulation_seconds;

	final private String simulation_log;

	/** Initialize
	 *
	 *  @param simulation_seconds Duration of simulation in seconds
	 *  @param simulation_log Human-readable log of the simulation
	 */
	public SimulationResult(final double simulation_seconds, final String simulation_log)
    {
	    this.simulation_seconds = simulation_seconds;
	    this.simulation_log = simulation_log;
    }

	/** @return Duration of simulation in seconds */
	public double getSimulationSeconds()
	{
		return simulation_seconds;
	}

	/** @return Human-readable log of the simulation */
	public String getSimulationLog()
    {
    	return simulation_log;
    }
}
