/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
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

import java.util.Date;

import org.csstudio.scan.data.ScanSampleFormatter;

/** Scan server info
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ScanServerInfo  extends MemoryInfo
{
    /** Serialization ID */
    final private static long serialVersionUID = ScanServer.SERIAL_VERSION;

    final private String version;
    final private Date start_time;
    final private String beamline_config;
    final private String simulation_config;

    /** Initialize
     *  @param version
     *  @param start_time
     *  @param beamline_config
     *  @param simulation_config
     */
    public ScanServerInfo(final String version, final Date start_time,
    		final String beamline_config,
    		final String simulation_config)
    {
	    this.version = version;
	    this.start_time = start_time;
	    this.beamline_config = beamline_config;
	    this.simulation_config = simulation_config;
    }

    /** @return Version number */
	public String getVersion()
    {
    	return version;
    }

	/** @return Start time */
	public Date getStartTime()
    {
    	return start_time;
    }

	/** @return Beam line configuration path */
	public String getBeamlineConfig()
    {
    	return beamline_config;
    }

	/** @return Simulation configuration path */
	public String getSimulationConfig()
	{
		return simulation_config;
	}

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        final StringBuilder buf = new StringBuilder();
        buf.append("Scan Server ").append(version).append("\n");
        buf.append("Started: ").append(ScanSampleFormatter.format(start_time)).append("\n");
        buf.append("Beamline Configuration: ").append(beamline_config).append("\n");
        buf.append("Simulation Configuration: ").append(simulation_config).append("\n");
        buf.append("Memory: ").append(getMemoryInfo()).append("\n");
        return buf.toString();
    }
}
